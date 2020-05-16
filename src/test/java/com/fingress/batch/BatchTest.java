package com.fingress.batch;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fingress.batch.quartz.QuartzConfiguriation;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchTest {
	
	@Autowired
	private QuartzConfiguriation quartz;
	
	
	@Autowired
	private BatchConfig jobLauncher;

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
	public void testJobLauncher() {
		try {
			Map<String,Object> jobDetail = new HashMap<>();	
			jobDetail.put("jobName", "TestJob");
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
