package com.svs.learn.bjms.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.svs.learn.bjms.service.JobManageService;

/**
 * ApplicationListener for adding saved jobs to scheduling.
 * 
 * @author Sreejith VS
 *
 */
@Component
public class BjmsAppListener implements ApplicationListener<ContextRefreshedEvent> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	JobManageService jobManageService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("Startup : submitting jobs to queue for scheduling ...");
		jobManageService.triggerAppInitScheduling();
	}
}
