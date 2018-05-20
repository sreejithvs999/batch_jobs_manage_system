package com.svs.learn.bjms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.bean.JobEntryForm;
import com.svs.learn.bjms.bean.JobInstance;
import com.svs.learn.bjms.dao.JobManageDao;
import com.svs.learn.bjms.exception.BjmsException;
import com.svs.learn.bjms.service.helper.ScheduleConfigHelper;

@Service
public class JobManageService {

	private static final Logger log = LoggerFactory.getLogger(JobManageService.class);

	@Autowired
	JobManageDao jmDao;

	@Autowired
	BjmsScheduleService scheduleService;

	@Autowired
	ScheduleConfigHelper scheduleConfigHelper;

	@Transactional
	public JobDetails saveJobDetails(JobEntryForm form) {

		JobDetails jobDetails = new JobDetails();
		BeanUtils.copyProperties(form, jobDetails);

		JobDetails jd = jmDao.insertJmsJob(jobDetails);
		log.info("inserted Job details = {} ", jd);

		JobInstance instance = jmDao.insertJobInstance(jd);
		log.info("inserted Job instance = {}", instance);

		if (!scheduleConfigHelper.isManualRunJob(jobDetails)) {
			scheduleService.submitForScheduling(jobDetails);
		}

		return jd;
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public JobDetails updateJobDetails(JobEntryForm form) {
		JobDetails updatedDetails = new JobDetails();
		BeanUtils.copyProperties(form, updatedDetails);

		JobDetails savedDetails = jmDao.getJobDetails(form.getJobId());
		if (savedDetails == null) {
			throw new BjmsException("No job found for the given id.");
		}

		if (jmDao.anyRunningInstanceForJob(form.getJobId())) {
			throw new BjmsException("Job is running, can't make changes now.");
		}

		jmDao.deleteAnyQueuedInstanceForJob(form.getJobId());

		jmDao.deleteJobParameters(form.getJobId());

		if (!scheduleConfigHelper.isManualRunJob(savedDetails)) {
			scheduleService.revokeScheduling(savedDetails);
		}

		if (!JobDetails.ACTIVE_FLAG.equals(updatedDetails.getActiveFlag())) {
			updatedDetails.setActiveFlag("N");
		}

		jmDao.updateJmsJob(updatedDetails);

		if (JobDetails.ACTIVE_FLAG.equals(updatedDetails.getActiveFlag())) {
			jmDao.insertJobInstance(updatedDetails);

			if (!scheduleConfigHelper.isManualRunJob(updatedDetails)) {
				scheduleService.submitForScheduling(updatedDetails);
			}
		}

		return updatedDetails;
	}

	@Transactional
	public JobDetails runManualJob(JobEntryForm form) {

		JobDetails jobDetails = jmDao.getJobDetails(form.getJobId());
		if (jobDetails == null) {
			throw new BjmsException("No job found for the given id.");
		}

		if (!JobDetails.ACTIVE_FLAG.equals(jobDetails.getActiveFlag())) {
			throw new BjmsException("The job is not active.");
		}

		if (!scheduleConfigHelper.isManualRunJob(jobDetails)) {
			throw new BjmsException("The specified job is not manully runnable type.");
		}

		scheduleService.scheduleManualRun(jobDetails);
		return jobDetails;
	}

	public List<JobDetails> getJobDetailsWithRecentInstance() {
		return jmDao.getJobDetailsAndRecentInstance();
	}

	public List<JobInstance> getAllJobInstance(Integer jobId) {
		return jmDao.getJobInstances(jobId);
	}

	public void triggerAppInitScheduling() {
		jmDao.getJobDetailsAndRecentInstance().stream().filter(
				job -> !scheduleConfigHelper.isManualRunJob(job) && JobDetails.ACTIVE_FLAG.equals(job.getActiveFlag()))
				.forEach(scheduleService::submitForScheduling);
		scheduleService.invokeAppScheduler();
	}
}
