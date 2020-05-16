package com.fingress.batch.quartz;

/*
Author @Akash
*/

import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuartzConfiguriation {

	@Autowired
	private QuatrzJobDetail quatrzJobDetail;
	
	@Autowired
	private Scheduler scheduler;
	
	@PostConstruct
	public void init() throws SchedulerException {
        scheduleJobs();
    }

	private JobDetail getJobDetailFor(Map<String, Object> x) {
		JobDataMap jobDataMap = new JobDataMap();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jobName", x.get("jobName").toString());
		map.put("cronExp", x.get("cronExp").toString());
		map.put("cronName", x.get("cronName").toString());
		map.put("cronGroup", x.get("cronGroup").toString());
		jobDataMap.putAll(map);
		JobDetail jobDetail = JobBuilder.newJob(QuartzJobLauncher.class).setJobData(jobDataMap)
				.withDescription("Job with data to write : " + x.get("jobName").toString() + " and CRON expression : "
						+ x.get("cronExp").toString())
				.withIdentity(x.get("jobName").toString() + "_" + x.get("name").toString()).build();
		return jobDetail;
	}

	private Trigger getTriggerFor(String cronExpression, JobDetail jobDetail, TriggerKey triggerKey) {
		Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail).withSchedule(cronSchedule(cronExpression))
				.withIdentity(triggerKey).build();
		return trigger;
	}

	public void scheduleJobs() throws SchedulerException {
		scheduler.clear();
		scheduler.start();
		List<Map<String, Object>> jobDetailList = quatrzJobDetail.getJobDetails();
		for (Map<String, Object> model : jobDetailList) {
			try {
				JobDetail jobfactory = getJobDetailFor(model);
				TriggerKey triggerKey = new TriggerKey(model.get("jobName").toString() + "_trigger");
				Trigger trigger = getTriggerFor(model.get("cronExp").toString(), jobfactory, triggerKey);
				scheduler.scheduleJob(jobfactory, trigger);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void scheduleNewJob(Map<String, Object> model) {
		try {
			JobDetail jobfactory = getJobDetailFor(model);
			TriggerKey triggerKey = new TriggerKey(model.get("jobName").toString() + "_trigger");
			Trigger trigger = getTriggerFor(model.get("cronExp").toString(), jobfactory, triggerKey);
			scheduler.scheduleJob(jobfactory, trigger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void rescheduleJob(String triggerKeyString, String cronExpression) {
		try {
			TriggerKey triggerKey = new TriggerKey(triggerKeyString);
			Trigger oldTrigger =scheduler.getTrigger(triggerKey);
			TriggerBuilder oldTriggerBuilder = oldTrigger.getTriggerBuilder();
			Trigger newTrigger = oldTriggerBuilder.withSchedule(cronSchedule(cronExpression)).build();
			scheduler.rescheduleJob(triggerKey, newTrigger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pauseJob(String triggerKeyString) {
		try {
			TriggerKey triggerKey = new TriggerKey(triggerKeyString);
		    scheduler.pauseTrigger(triggerKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void resumeJob(String triggerKeyString) {
		try {
			TriggerKey triggerKey = new TriggerKey(triggerKeyString);
		    scheduler.resumeTrigger(triggerKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
