package com.fingress.batch.quartz;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Service
@Configurable
public class CustomBacthJobInitializer {
	
	@Autowired
	private JobRegistry jobRegistry;
	
	
	@Autowired
	private GenericWebApplicationContext ctx;
	
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	
	public void initializeJob(Map<String,Object> x) {
		Job job =null;
		try {
		
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
		}catch (Exception e) {
		e.printStackTrace();
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
	
	public Job  getJobInfo(String jobname) {
		Job job = null;
		try {
			job = jobRegistry.getJob(jobname);
		}catch (Exception e) {
	     e.printStackTrace();
		}
		return job;
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

}
