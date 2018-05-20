package com.svs.learn.bjms.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.bean.JobEntryForm;
import com.svs.learn.bjms.bean.JobInstance;
import com.svs.learn.bjms.service.JobManageService;

@RestController
@RequestMapping("/job")
public class JobsController {

	@Autowired
	JobManageService jmService;

	/**
	 * End point to save a new job. The job will be scheduled for execution if
	 * required.
	 * 
	 * @param form
	 * @return
	 */
	@PostMapping("/save")
	public JobDetails saveNewJob(@Validated @RequestBody JobEntryForm form) {

		return jmService.saveJobDetails(form);
	}

	/**
	 * End point to change job properties, deactivate a job. Scheduling changes may
	 * happen if required.
	 * 
	 * @param form
	 * @return
	 */
	@PostMapping("/update")
	public JobDetails updateJob(@Validated @RequestBody JobEntryForm form) {

		return jmService.updateJobDetails(form);
	}

	/**
	 * End point only for manually executable jobs. Start the job immediately.
	 * 
	 * @param form
	 * @return
	 */
	@PostMapping("/run")
	public JobDetails runManualJob(@RequestBody JobEntryForm form) {

		return jmService.runManualJob(form);
	}

	/**
	 * Get all saved job details along with recent instance.
	 * 
	 * @return
	 */
	@GetMapping("/")
	public List<JobDetails> getAllJobs() {

		return jmService.getJobDetailsWithRecentInstance();
	}

	/**
	 * Get all instances saved so far for a particular job.
	 * 
	 * @param jobId
	 * @return
	 */
	@GetMapping("/{jobId}")
	public List<JobInstance> getJobInstances(@PathVariable("jobId") Integer jobId) {

		return jmService.getAllJobInstance(jobId);
	}
}
