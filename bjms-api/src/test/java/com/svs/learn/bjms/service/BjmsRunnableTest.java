package com.svs.learn.bjms.service;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import com.svs.learn.bjms.bean.JobInstance;
import com.svs.learn.bjms.service.BjmsRunnable;
import com.svs.learn.bjms.service.helper.ScheduleConfigHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BjmsRunnableTest {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("jobScheduler")
	ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	ScheduleConfigHelper scheduleConfigHelper;

	@Test
	public void test1ManualRun() {

		BjmsRunnable bjmsRunnable = new BjmsRunnable();
		JobInstance instance = BjmsRunnableTestHelper.getEmailSendJobInstance();
		instance.getJobDetails().setScheduleOpt("M");
		instance.getJobDetails().setScheduleData("");
		bjmsRunnable.setInstanceMgr(BjmsRunnableTestHelper.getJobInstanceMgr(() -> instance, null, null, null));
		log.info("Test: Schedule Single Email job, manual running immediately.");
		taskScheduler.schedule(bjmsRunnable, scheduleConfigHelper.getTriggerForJob(instance.getJobDetails()));
	}

	@Test
	public void test2PeriodicRun() {

		BjmsRunnable bjmsRunnable = new BjmsRunnable();

		JobInstance instance = BjmsRunnableTestHelper.getEmailSendJobInstance();
		instance.getJobDetails().setName("Periodic Email Send Job");
		instance.getJobDetails().setDescription("Periodic Email send Job (every 1 minutes)");
		instance.getJobDetails().setJobId(30);
		instance.setInstanceId(30000);

		bjmsRunnable.setInstanceMgr(BjmsRunnableTestHelper.getJobInstanceMgr(() -> instance, null, null,
				() -> instance.setInstanceId(instance.getInstanceId() + 1)));

		log.info("Test: Schedule email send job, initial delay 1 min, period 2 minutes. ");
		taskScheduler.schedule(bjmsRunnable, scheduleConfigHelper.getTriggerForJob(instance.getJobDetails()));
	}

	@Test
	public void test3CronExprRun() {

		BjmsRunnable bjmRunnable = new BjmsRunnable();

		JobInstance instance = BjmsRunnableTestHelper.getFileCopyJobInstance();
		instance.getJobDetails().setName("Cron Expr File copy job");
		instance.getJobDetails().setDescription("Cron Expr file copy job( run 30th second of every minute)");
		instance.setInstanceId(20000);
		bjmRunnable.setInstanceMgr(BjmsRunnableTestHelper.getJobInstanceMgr(() -> instance, null, null,
				() -> instance.setInstanceId(instance.getInstanceId() + 1)));

		log.info("Test: Schedule file copy job run every minute at 30th second.");
		taskScheduler.schedule(bjmRunnable, scheduleConfigHelper.getTriggerForJob(instance.getJobDetails()));

	}

	@Test
	public void test4Wait() throws Exception {

		log.info("*** Keep test alive for schedulers to runs ***");
		Thread.sleep(1000 * 60 * 2);
	}
}
