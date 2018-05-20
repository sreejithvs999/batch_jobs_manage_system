package com.svs.learn.bjms.bean;

import java.util.List;

public class JobDetails {

	private Integer jobId;
	private String name;
	private String description;
	private String execPath;
	private String scheduleOpt;
	private String scheduleData;
	private Integer priority;
	private String activeFlag;

	public static final String ACTIVE_FLAG = "Y";
	
	private List<String> configParams;

	private JobInstance recentInstance;

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExecPath() {
		return execPath;
	}

	public void setExecPath(String execPath) {
		this.execPath = execPath;
	}

	public String getScheduleOpt() {
		return scheduleOpt;
	}

	public void setScheduleOpt(String scheduleOpt) {
		this.scheduleOpt = scheduleOpt;
	}

	public String getScheduleData() {
		return scheduleData;
	}

	public void setScheduleData(String scheduleData) {
		this.scheduleData = scheduleData;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

	public List<String> getConfigParams() {
		return configParams;
	}

	public void setConfigParams(List<String> configParams) {
		this.configParams = configParams;
	}

	public JobInstance getRecentInstance() {
		return recentInstance;
	}

	public void setRecentInstance(JobInstance recentInstance) {
		this.recentInstance = recentInstance;
	}

	@Override
	public String toString() {

		return "JobDetails(jobId=" + jobId + ", name=" + name + ")";
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof JobDetails)) {
			return false;
		}

		JobDetails other = (JobDetails) obj;
		if (this.jobId == null || other.getJobId() == null || !this.jobId.equals(other.getJobId())) {
			return false;
		}
		return true;
	}

}
