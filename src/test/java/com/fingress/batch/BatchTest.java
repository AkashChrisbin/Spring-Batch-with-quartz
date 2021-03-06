package com.fingress.batch;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fingress.batch.quartz.CustomBacthJobInitializer;
import com.fingress.batch.quartz.QuartzConfiguriation;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchTest {
	
	@Autowired
	private QuartzConfiguriation quartz;
	
	
	@Autowired
	private CustomBacthJobInitializer jobLauncher;

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testTrigger() {
		try {
		quartz.rescheduleJob("TestJob1_trigger","*/5 * * * * ?");
		Thread.sleep(200000000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.print("Completed");
	}
	
	
	@Test
	public void getJobInfo() {
		try {
		Thread.sleep(500);	
		Job job =jobLauncher.getJobInfo("TestJobTask1");
		System.out.println("The job is ->"+job);
		Thread.sleep(50000);	
	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.print("Completed");
	}
	
	
	@Test
	public void pauseTrigger() {
		try {
		quartz.pauseJob("TestJobTask1_trigger");
		Thread.sleep(200000000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.print("Completed");
	}
	

	@Test
	public void resumeTrigger() {
		try {
		quartz.pauseJob("TestJobTask1_trigger");	
		Thread.sleep(2000);
		quartz.resumeJob("TestJobTask1_trigger");
		Thread.sleep(200000000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.print("Completed");
	}
	
	@Test
	public void testJobLauncher() {
		try {
			Map<String,Object> jobDetail = new HashMap<>();	
			jobDetail.put("jobName", "TestJob");
			jobDetail.put("jobType", "chunk");
			jobDetail.put("name", "Test");
			jobDetail.put("group", "testGroup");
			jobDetail.put("cronExp", "0 0/1 * 1/1 * ? *");
			jobDetail.put("cronName", "cron_trigger");
			jobDetail.put("cronGroup", "cron_group");
			jobDetail.put("stepName", "CronStep1");
			jobDetail.put("reader", "com.fingress.batch.reader.FgReader");
			jobDetail.put("processor", "com.fingress.batch.processor.FgProcessor");
			jobDetail.put("writer", "com.fingress.batch.writer.FgWriter");
		
			jobLauncher.launchJob(jobDetail);
			Thread.sleep(200000000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
