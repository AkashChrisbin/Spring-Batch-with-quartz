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
			jobDetail2.put("cronExp", "0 0/2 * 1/1 * ? *");
			jobDetail2.put("cronName", "cron_trigger1");
			jobDetail2.put("cronGroup", "cron_group1");
			jobDetail2.put("stepName", "CronStep2");
			jobDetail2.put("reader", "com.fingress.batch.reader.FgTestReader");
			jobDetail2.put("processor", "com.fingress.batch.processor.FgTestProcessor");
			jobDetail2.put("writer", "com.fingress.batch.writer.FgTestWriter");
			jobDetailList.add(jobDetail2);
		}
		catch (Exception e) {
        e.printStackTrace();
		}
		return jobDetailList;
		
	}
	

}
