package com.fingress.batch.tasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fingress.batch.context.AppContext;
import com.fingress.batch.quartz.CustomBacthJobInitializer;


@SuppressWarnings("unused")
@Component
public class FgTasklet implements Tasklet {
	
	

	@SuppressWarnings("unused")
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		CustomBacthJobInitializer launcher  = AppContext.getBean(CustomBacthJobInitializer.class);
		Job job =launcher.getJobInfo("TestJobTask1");
		System.out.print("Tasklet Invoked");
		return RepeatStatus.FINISHED;
	}

}
