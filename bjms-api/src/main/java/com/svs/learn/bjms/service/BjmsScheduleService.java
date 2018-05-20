package com.svs.learn.bjms.service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.service.helper.ScheduleConfigHelper;

/**
 * The service handle job scheduling operations. It maintain two scheduler
 * objects. The <b>appScheduler</b> which runs every 5 second and check whether
 * any job is there in the <b>jobQueue</b> and schedule them to run with
 * <b>jobScheduler</b> object. <br/>
 * 
 * References to the scheduled user jobs are kept in <b>jobSchedules</b> object
 * for revoking or monitoring them later. <br/>
 * 
 * @author Sreejith VS
 *
 */
@Service
public class BjmsScheduleService {

	private static Logger log = LoggerFactory.getLogger(BjmsScheduleService.class);

	@Autowired
	@Qualifier("appScheduler")
	private ThreadPoolTaskScheduler appScheduler;

	@Autowired
	@Qualifier("jobScheduler")
	ThreadPoolTaskScheduler jobScheduler;

	@Autowired
	ScheduleConfigHelper scheduleConfigHelper;

	private Queue<JobDetails> jobQueue = new ConcurrentLinkedQueue<>();

	private Map<Integer, ScheduledFuture<?>> jobSchedules = new ConcurrentHashMap<>();

	private static final Integer JOBS_SCHEDULE_INTERVAL = 5000;// 5s

	public void submitForScheduling(JobDetails jobDetails) {
		jobQueue.add(jobDetails);
		log.info("Job is taken for scheduling : {}", jobDetails);
	}

	public void revokeScheduling(JobDetails jobDetails) {

		jobQueue.remove(jobDetails);
		if (jobSchedules.containsKey(jobDetails.getJobId())) {
			ScheduledFuture<?> future = jobSchedules.remove(jobDetails.getJobId());
			if (future != null) {
				boolean cancelStatus = future.cancel(true);
				log.info("Revocation of task :{}, status : {}", jobDetails, cancelStatus);
			}
		}
	}

	public void scheduleManualRun(JobDetails jobToSchedule) {
		scheduleBatchJobs(jobToSchedule);
	}

	/**
	 * The method add the job to jobScheduler. <br/>
	 * 
	 * When job priority is considered, job would be added according to the queue
	 * capacity. Low priority jobs has to be moved out or different schedulers has
	 * to be used.
	 * 
	 * @param jobToSchedule
	 */
	private void scheduleBatchJobs(JobDetails jobToSchedule) {

		ScheduledFuture<?> scheduleRef = jobScheduler.schedule(scheduleConfigHelper.getTaskForJob(jobToSchedule),
				scheduleConfigHelper.getTriggerForJob(jobToSchedule));

		jobSchedules.put(jobToSchedule.getJobId(), scheduleRef);
		log.info("Job added to scheduler : {}", jobToSchedule);
	}

	public void invokeAppScheduler() {

		appScheduler.scheduleAtFixedRate(() -> {

			while (!jobQueue.isEmpty()) {
				scheduleBatchJobs(jobQueue.poll());
			}
		}, JOBS_SCHEDULE_INTERVAL);
	}

}
