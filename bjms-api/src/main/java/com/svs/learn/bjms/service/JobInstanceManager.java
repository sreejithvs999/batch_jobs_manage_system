package com.svs.learn.bjms.service;

import java.io.File;

import com.svs.learn.bjms.bean.JobInstance;

/**
 * JobInstanceManager instance is used by Scheduler Run task.
 * 
 * @author Sreejith VS
 *
 */
public interface JobInstanceManager {

	/**
	 * Obtain job instance information for spawning batch process.
	 * 
	 * @return
	 */
	public JobInstance prepareJobInstance();

	/**
	 * call back when process spawning is successfully done from scheduler point of
	 * view. It doesn't mean the batch operations are successful or not, which might
	 * be available from output file content depending on how batch program works.
	 * 
	 * @param outFile
	 *            output or error file of batch process
	 * @param status
	 *            exit status of batch process
	 */
	public void onJobRunSuccessfull(File outFile, int status);

	/**
	 * This method is called when any exception occured while spawning process for
	 * batch job.
	 * 
	 * @param throwable
	 *            the exception caught
	 */
	public void onJobRunFail(Throwable throwable);

	/**
	 * This method will be called just before run() completion irrespective of
	 * success or failure of process spawning.
	 */
	public void onJobRunComplete();

	/**
	 * set job id,
	 * 
	 * @param jobId
	 * @return
	 */
	public JobInstanceManager jobId(Integer jobId);

	/**
	 * get job Id
	 * 
	 * @return jobId
	 */
	public Integer jobId();
}
