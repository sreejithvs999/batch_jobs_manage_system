package com.svs.learn.bjms.bean;

import java.util.Date;

public class JobInstance {

	private Integer instanceId;
	private Date startedOn;
	private Date endedOn;
	private String status;
	private JobDetails jobDetails;

	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}

	public Date getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(Date startedOn) {
		this.startedOn = startedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEndedOn() {
		return endedOn;
	}

	public void setEndedOn(Date endedOn) {
		this.endedOn = endedOn;
	}

	public JobDetails getJobDetails() {
		return jobDetails;
	}

	public void setJobDetails(JobDetails jobDetails) {
		this.jobDetails = jobDetails;
	}

	@Override
	public String toString() {
		return "jobInstance(instanceId=" + instanceId + ", jobDetails=" + jobDetails + ")";
	}
}
