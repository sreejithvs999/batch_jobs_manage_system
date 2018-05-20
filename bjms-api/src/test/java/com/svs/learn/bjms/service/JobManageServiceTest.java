package com.svs.learn.bjms.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.bean.JobEntryForm;
import com.svs.learn.bjms.bean.JobInstance;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobManageServiceTest {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	JobManageService jobManageService;

	static Integer savedJobId; // share between 2 tests

	@Test
	public void test1SaveJob() {

		log.info("Test : save a job ");
		JobEntryForm jobForm = new JobEntryForm();

		jobForm.setDescription("Linear Regression Job");
		jobForm.setExecPath("./src/test/resources/BatchJob.jar");

		List<String> params = new ArrayList<>();
		params.add("data=4:65,2.5:49,1:20,7:85,10:91,7.5:96,5.5:68,3:58");
		params.add("x=6");
		params.add("maxWait=20");
		jobForm.setConfigParams(params);

		jobForm.setName("Linear Regression Job");
		jobForm.setPriority(1);
		jobForm.setScheduleOpt("C");
		jobForm.setScheduleData("10 0/1 * ? * ?");// initial delay, period (minutes)

		JobDetails jd = jobManageService.saveJobDetails(jobForm);
		savedJobId = jd.getJobId();
		log.info("Saved job id :{} ", savedJobId);
	}

	@Test
	public void test2Wait() throws Exception {

		log.info("*** Wait for scheduler to work ***");
		Thread.sleep(1000 * 60 * 1);
	}

	@Test
	public void test3UpdateJob() {

		log.info("Test: update the saved job. jobId:{}", savedJobId);
		JobEntryForm jobForm = new JobEntryForm();

		jobForm.setActiveFlag(JobDetails.ACTIVE_FLAG);

		jobForm.setJobId(savedJobId);
		jobForm.setDescription("Linear Regression Job - Updated");
		jobForm.setExecPath("./src/test/resources/BatchJob.jar");

		List<String> params = new ArrayList<>();
		params.add("data=4:65,2.5:49,1:20,7:85,10:91,7.5:96,5.5:68,3:58");
		params.add("x=6");
		params.add("maxWait=20");
		jobForm.setConfigParams(params);

		jobForm.setName("Linear Regression Job- Updated");
		jobForm.setPriority(1);
		jobForm.setScheduleOpt("C");
		jobForm.setScheduleData("35 0/1 * ? * ?");

		jobManageService.updateJobDetails(jobForm);
	}

	@Test
	public void test4Wait() throws Exception {

		log.info("*** Keep test alive for schedulers do multiple runs ***");
		Thread.sleep(1000 * 60 * 1);
	}

	@Test
	public void test5GetJobsAndInstance() {

		log.info("Test: Get Jobs with recent instance");
		List<JobDetails> jobs = jobManageService.getJobDetailsWithRecentInstance();
		jobs.stream().forEach(System.out::println);
	}

	@Test
	public void test6GetAllInstances() {
		log.info("Test: Get all instances of a job");
		List<JobInstance> instances = jobManageService.getAllJobInstance(1);
		instances.stream().forEach(System.out::println);
	}
}
