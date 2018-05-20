package com.svs.learn.bjms.enums;

public enum JobStatus {

	QUEUED("Q"), RUNNING("R"), FAILED("F"), SUCCESS("S");

	private String charVal;

	JobStatus(String charVal) {
		this.charVal = charVal;
	}

	public String val() {
		return this.charVal;
	}

}
