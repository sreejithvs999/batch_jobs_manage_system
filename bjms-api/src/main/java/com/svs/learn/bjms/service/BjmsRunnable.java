package com.svs.learn.bjms.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.bean.JobInstance;

public class BjmsRunnable implements Runnable {

	Logger log = LoggerFactory.getLogger(BjmsRunnable.class);

	private JobInstanceManager instanceMgr;

	private String workingDir;
	private String logDir;

	private void init() {

		Assert.isTrue(instanceMgr != null, "instanceMgr is not set.");

		if (StringUtils.isEmpty(logDir)) {
			logDir = ".";
		}
		if (StringUtils.isEmpty(workingDir)) {
			workingDir = logDir;
		}
	}

	@Override
	public void run() {

		init();

		JobInstance jobInstance = instanceMgr.prepareJobInstance();

		if (jobInstance == null) {
			log.info("No instance in queue available for job:{}, running nothing!", instanceMgr.jobId());
			return;
		}

		log.info("Beginning run: {}", jobInstance);
		JobDetails jobDetail = jobInstance.getJobDetails();
		Process jobProcess = null;

		try {

			ProcessBuilder pb = new ProcessBuilder();

			pb.command(createCommandString(jobDetail));

			pb.directory(new File(workingDir));

			File fileOut = new File(logDir, createOutFileName(jobInstance));
			pb.redirectOutput(fileOut);
			pb.redirectError(fileOut);

			jobProcess = pb.start();
			jobProcess.waitFor();

			log.info("Exit run : {} ,exitCode : {}", jobInstance, jobProcess.exitValue());

			instanceMgr.onJobRunSuccessfull(fileOut, jobProcess.exitValue());

		} catch (Exception e) {

			log.error("Error while run: {}", jobInstance);
			log.error("", e);
			instanceMgr.onJobRunFail(e);

		} finally {

			if (jobProcess != null) {
				jobProcess.destroy();
			}
		}

		log.info("Complete run: {}", jobInstance);
		instanceMgr.onJobRunComplete();
	}

	private List<String> createCommandString(JobDetails details) {
		List<String> commands = new ArrayList<>();
		commands.add("java");
		commands.add("-jar");
		commands.add(details.getExecPath());
		for (String param : details.getConfigParams()) {
			commands.add(param);
		}
		return commands;
	}

	private String createOutFileName(JobInstance instance) {
		return String.format("%6d_%6d_%d.log", instance.getJobDetails().getJobId(), instance.getInstanceId(),
				System.currentTimeMillis());
	}

	public void setInstanceMgr(JobInstanceManager instanceMgr) {
		this.instanceMgr = instanceMgr;
	}

	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}
}
