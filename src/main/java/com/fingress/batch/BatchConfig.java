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
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
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
import com.fingress.batch.quartz.AutowiringSpringBeanJobFactory;
import com.fingress.batch.quartz.CustomBacthJobInitializer;
import com.fingress.batch.quartz.QuartzConfiguriation;
import com.fingress.batch.quartz.QuatrzJobDetail;

@Configuration
@EnableBatchProcessing
@Import({ QuartzConfiguriation.class })
public class BatchConfig extends DefaultBatchConfigurer implements InitializingBean {


	@Autowired
	private QuatrzJobDetail quatrzJobDetail;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private CustomBacthJobInitializer batchInitializer;
	

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
			batchInitializer.initializeJob(x);
		});
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		initializeJob();
	}

}
