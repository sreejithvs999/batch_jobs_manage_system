package com.svs.learn.bjms.service.helper;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.service.BjmsRunnable;
import com.svs.learn.bjms.service.JobInstanceManager;

/**
 * Component for delegating operations for configuration in job scheduling.
 * 
 * @author Sreejith VS
 *
 */
@Component
public class ScheduleConfigHelper {

	public static final String SCHEDULE_OPT_CRON_EXP = "C";
	public static final String SCHEDULE_OPT_PERIODIC = "P";
	public static final String SCHEDULE_OPT_MANUAL = "M";

	@Autowired
	ObjectProvider<JobInstanceManager> jobInstanceManagerProvider;

	@Autowired
	Environment environment;

	/**
	 * Find suitable trigger for the job.
	 * 
	 * @param jobDetails
	 * @return
	 */
	public Trigger getTriggerForJob(JobDetails jobDetails) {

		if (SCHEDULE_OPT_CRON_EXP.equals(jobDetails.getScheduleOpt())) {
			return new CronTrigger(jobDetails.getScheduleData());

		} else if (SCHEDULE_OPT_PERIODIC.equals(jobDetails.getScheduleOpt())) {

			Assert.isTrue(jobDetails.getScheduleData().matches("^\\d{1,6}\\:\\d{1,6}$"),
					"Schedule data is expected as a number:number format");
			String delays[] = jobDetails.getScheduleData().split(":");
			long initialDelay = Long.parseLong(delays[0]);
			long period = Long.parseLong(delays[1]);

			PeriodicTrigger pt = new PeriodicTrigger(period, TimeUnit.MINUTES);
			pt.setInitialDelay(initialDelay);
			return pt;
		}
		return new OnetimeTrigger();
	}

	public boolean isManualRunJob(JobDetails jobDetails) {
		return SCHEDULE_OPT_MANUAL.equals(jobDetails.getScheduleOpt());
	}

	/**
	 * Create Task to be scheduled for the job.
	 * 
	 * @param details
	 * @return
	 */
	public BjmsRunnable getTaskForJob(JobDetails details) {

		BjmsRunnable task = new BjmsRunnable();
		task.setInstanceMgr(jobInstanceManagerProvider.getObject().jobId(details.getJobId()));
		task.setWorkingDir(environment.getProperty("bjms.app.jobs.working.dir"));
		task.setLogDir(environment.getProperty("bjms.app.jobs.output.dir"));
		return task;
	}

	/**
	 * Trigger for run only once.
	 * 
	 * @author Sreejith VS
	 *
	 */
	static class OnetimeTrigger implements Trigger {

		boolean hasRun;

		@Override
		public Date nextExecutionTime(TriggerContext triggerContext) {

			if (!hasRun) {
				hasRun = true;
				return new Date(System.currentTimeMillis() + 1000); // run next second
			}
			return null;
		}
	}
}
