package com.svs.learn.bjms.bean;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class JobEntryForm {

	private Integer jobId;

	@NotNull(message = "Name is null")
	private String name;
	private String description;

	@NotNull(message = "excecPath is null")
	private String execPath;

	@NotNull(message = "scheduleOpt is null")
	@Pattern(regexp = "^[P|C|M]$", message = "scheduleOpt is invalid")
	private String scheduleOpt;

	@NotNull(message = "scheduleData is null")
	private String scheduleData;

	private Integer priority;

	private String activeFlag;

	private List<String> configParams;

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

	public List<String> getConfigParams() {
		return configParams;
	}

	public void setConfigParams(List<String> configParams) {
		this.configParams = configParams;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

}
