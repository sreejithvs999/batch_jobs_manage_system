package com.svs.learn.bjms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class BjmsConfig {

	@Bean(name = "jobScheduler")
	public ThreadPoolTaskScheduler jobScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(50);
		taskScheduler.setThreadNamePrefix("bjms_job_");
		taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		taskScheduler.setAwaitTerminationSeconds(Integer.MAX_VALUE);
		return taskScheduler;
	}

	@Bean(name = "appScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		taskScheduler.setThreadPriority(Thread.MAX_PRIORITY);
		taskScheduler.setThreadNamePrefix("bjms_app_");
		taskScheduler.setDaemon(true);
		return taskScheduler;
	}
}
