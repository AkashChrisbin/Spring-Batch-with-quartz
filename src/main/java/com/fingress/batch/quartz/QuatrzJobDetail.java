package com.fingress.batch.quartz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
@Service
public class QuatrzJobDetail {
	
	
	public List<Map<String,Object>> getJobDetails(){
		List<Map<String,Object>> jobDetailList = new ArrayList<>();
		try {
			Map<String,Object> jobDetail2 = new HashMap<>();	
			jobDetail2.put("jobName", "TestJob1");
			jobDetail2.put("jobType", "chunk");
			jobDetail2.put("name", "Test1");
			jobDetail2.put("group", "testGroup1");
			jobDetail2.put("cronExp", "0 0/1 * 1/1 * ? *");
			jobDetail2.put("cronName", "cron_trigger1");
			jobDetail2.put("cronGroup", "cron_group1");
			jobDetail2.put("stepName", "CronStep2");
			jobDetail2.put("reader", "com.fingress.batch.reader.FgTestReader");
			jobDetail2.put("processor", "com.fingress.batch.processor.FgTestProcessor");
			jobDetail2.put("writer", "com.fingress.batch.writer.FgTestWriter");
			jobDetailList.add(jobDetail2);
			
			
			Map<String,Object> jobDetail = new HashMap<>();	
			jobDetail.put("jobName", "TestJobTask1");
			jobDetail.put("jobType", "tasklet");
			jobDetail.put("name", "Test1");
			jobDetail.put("group", "testGroup1");
			jobDetail.put("cronExp", "*/5 * * * * ?");
			jobDetail.put("cronName", "cron_trigger3");
			jobDetail.put("cronGroup", "cron_group2");
			jobDetail.put("stepName", "CronStep4");
			jobDetail.put("tasklet", "com.fingress.batch.tasklet.FgTasklet");
			jobDetailList.add(jobDetail);
		}
		catch (Exception e) {
        e.printStackTrace();
		}
		return jobDetailList;
		
	}
	

}
