package com.svs.learn.bjms.service;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.bean.JobInstance;
import com.svs.learn.bjms.dao.JobManageDao;
import com.svs.learn.bjms.enums.JobStatus;

/**
 * DefaultJobInstanceManager is sufficient for handling life cycle of all types
 * of jobs.
 * 
 * @author Sreejith VS
 *
 */
@Component
@Scope(value = "prototype")
@Transactional
public class DefaultJobInstanceManager implements JobInstanceManager {

	private static final Logger log = LoggerFactory.getLogger(DefaultJobInstanceManager.class);

	private Integer jobId;

	private JobInstance jobInstance;

	@Autowired
	private JobManageDao jmDao;

	@Override
	public JobInstance prepareJobInstance() {
		jobInstance = jmDao.findQueuedJobInstance(jobId);

		if (jobInstance != null && JobDetails.ACTIVE_FLAG.equals(jobInstance.getJobDetails().getActiveFlag())) {

			log.info("Prepare to run : {}", jobInstance);
			jobInstance.setStartedOn(new Date());
			jobInstance.setStatus(JobStatus.RUNNING.val());
			jmDao.updateJobInstance(jobInstance);
			return jobInstance;
		}
		return null; // so the job wont run.
	}

	@Override
	public void onJobRunSuccessfull(File outFile, int status) {
		log.info("Update as success: {}, output={}, exitCode={}", jobInstance, outFile, status);
		jobInstance.setEndedOn(new Date());
		jobInstance.setStatus(JobStatus.SUCCESS.val());
		jmDao.updateJobInstance(jobInstance);
	}

	@Override
	public void onJobRunFail(Throwable throwable) {
		log.info("Update as failure: {}", jobInstance);
		jobInstance.setEndedOn(new Date());
		jobInstance.setStatus(JobStatus.FAILED.val());
		jmDao.updateJobInstance(jobInstance);
	}

	@Override
	public void onJobRunComplete() {
		log.info("Job run completed: {}", jobInstance);

		JobDetails jobDetails = jmDao.getJobDetails(jobId);

		if (jobDetails != null && JobDetails.ACTIVE_FLAG.equals(jobDetails.getActiveFlag())) {// Queue another instance
			jmDao.insertJobInstance(jobDetails);
		}
	}

	@Override
	public Integer jobId() {
		return jobId;
	}

	@Override
	public JobInstanceManager jobId(Integer jobId) {
		Assert.isNull(this.jobId, "jobId is alredy set.");
		this.jobId = jobId;
		return this;
	}
}
