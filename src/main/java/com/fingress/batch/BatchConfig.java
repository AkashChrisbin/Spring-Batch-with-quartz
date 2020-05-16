package com.fingress.batch;

/*
Author @Akash
*/


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.fingress.batch.quartz.AutowiringSpringBeanJobFactory;
import com.fingress.batch.quartz.QuartzConfiguriation;
import com.fingress.batch.quartz.QuatrzJobDetail;

@Configuration
@EnableBatchProcessing
@Import({ QuartzConfiguriation.class })
public class BatchConfig extends DefaultBatchConfigurer implements InitializingBean {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobRegistry jobRegistry;

	@Autowired
	private QuatrzJobDetail quatrzJobDetail;

	@Autowired
	private GenericWebApplicationContext ctx;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private DataSource dataSource;

	public static final String QUARTZ_PROPERTIES_PATH = "/quartz.properties";

	@Bean
	public SchedulerFactoryBean schedulerFactory(ApplicationContext applicationContext) throws IOException {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean
				.setJobFactory(new AutowiringSpringBeanJobFactory(applicationContext.getAutowireCapableBeanFactory()));
		schedulerFactoryBean.setQuartzProperties(quartzProperties());
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		return schedulerFactoryBean;
	}

	@Bean
	public Scheduler scheduler(ApplicationContext applicationContext) throws SchedulerException, IOException {
		Scheduler scheduler = schedulerFactory(applicationContext).getScheduler();
		return scheduler;
	}

	@Bean
	protected JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTransactionManager(transactionManager);
		factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
		return factory.getObject();
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTIES_PATH));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	public void initializeJob() {
		List<Map<String, Object>> jobDetailList = quatrzJobDetail.getJobDetails();
		jobDetailList.forEach(x -> {

			Job job = null;

			if (x.get("jobType").toString().equalsIgnoreCase("chunk")) {
				job = jobBuilderFactory.get(x.get("jobName").toString()).incrementer(new RunIdIncrementer())
						.flow(initializeStep(x)).end().build();
			} else {
				job = jobBuilderFactory.get(x.get("jobName").toString()).start(initializeStep(x)).build();
			}
			if (job != null) {
				try {

					jobRegistry.register(new ReferenceJobFactory(job));

				} catch (DuplicateJobException e) {
					e.printStackTrace();
				}
			}

		});

	}

	@SuppressWarnings("unchecked")
	public Step initializeStep(Map<String, Object> classMap) {

		try {
			if (classMap.get("jobType").toString().equalsIgnoreCase("chunk")) {
				String reader = classMap.get("reader").toString();
				String processor = classMap.get("processor").toString();
				String writer = classMap.get("writer").toString();

				Class<?> readerClass = Class.forName(reader);
				Class<?> processorClass = Class.forName(processor);
				Class<?> writerClass = Class.forName(writer);

				Object readerObj = readerClass.newInstance();
				Object processorObj = processorClass.newInstance();
				Object writerObj = writerClass.newInstance();

				return stepBuilderFactory.get(classMap.get("stepName").toString()).allowStartIfComplete(false)
						.<Object, Object>chunk(1000).reader((ItemReader<? extends Object>) readerObj)
						.processor((ItemProcessor<? super Object, ? extends Object>) processorObj)
						.writer((ItemWriter<? super Object>) writerObj).build();
			}
			else {
				String tasklet = classMap.get("tasklet").toString();
				Class<?> taskletClass = Class.forName(tasklet);
				Object taskletObj = taskletClass.newInstance();
				return stepBuilderFactory.get(classMap.get("stepName").toString()).tasklet((Tasklet) taskletObj).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void launchJob(Map<String, Object> jobMap) {
		try {
			Job job = null;
			if (jobMap.get("jobType").toString().equalsIgnoreCase("chunk")) {
				job = jobBuilderFactory.get(jobMap.get("jobName").toString()).incrementer(new RunIdIncrementer())
						.flow(initializeStep(jobMap)).end().build();
			} else {
				job = jobBuilderFactory.get(jobMap.get("jobName").toString()).start(initializeStep(jobMap)).build();
			}
			if (job != null) {
				jobRegistry.register(new ReferenceJobFactory(job));
				QuartzConfiguriation scheduler = ctx.getBean(QuartzConfiguriation.class);
				scheduler.scheduleNewJob(jobMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initializeJob();
	}

}
