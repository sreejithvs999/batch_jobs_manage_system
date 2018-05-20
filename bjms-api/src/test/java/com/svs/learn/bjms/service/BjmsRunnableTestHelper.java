package com.svs.learn.bjms.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.bean.JobInstance;

public class BjmsRunnableTestHelper {

	public static JobInstance getEmailSendJobInstance() {

		JobDetails jobDetails = new JobDetails();
		jobDetails.setActiveFlag("Y");
		jobDetails.setJobId(10);
		jobDetails.setDescription("Email send Job");
		jobDetails.setExecPath("./src/test/resources/BatchJob.jar");

		List<String> params = new ArrayList<>();
		params.add("maxWait=10");
		params.add("emailIds=a@b.de,c@d.de,e@f.de,g@h.de,i@j.de,k@l.de");
		jobDetails.setConfigParams(params);

		jobDetails.setName("Email send job");
		jobDetails.setPriority(1);
		jobDetails.setScheduleOpt("P");
		jobDetails.setScheduleData("1:2"); // init delay and period as 1, 2 minutes
		JobInstance instance = new JobInstance();
		instance.setInstanceId(1001);
		instance.setJobDetails(jobDetails);
		return instance;
	}

	public static JobInstance getFileCopyJobInstance() {

		JobDetails jobDetails = new JobDetails();
		jobDetails.setActiveFlag("Y");
		jobDetails.setJobId(20);
		jobDetails.setDescription("File Copy job");
		jobDetails.setExecPath("./src/test/resources/BatchJob.jar");

		List<String> params = new ArrayList<>();
		params.add("maxWait=10");
		params.add("copyFiles=a.txt,b.txt,c.txt,d.txt,e.txt,f.txt,g.txt,h.txt,i.txt,j.txt");
		jobDetails.setConfigParams(params);

		jobDetails.setName("File copy job");
		jobDetails.setPriority(1);
		jobDetails.setScheduleOpt("C");
		jobDetails.setScheduleData("30 0/1 * ? * ?"); // s m H DoM M DoW
		JobInstance instance = new JobInstance();
		instance.setInstanceId(20001);
		instance.setJobDetails(jobDetails);
		return instance;
	}

	public static JobInstanceManager getJobInstanceMgr(Supplier<JobInstance> prepareJobInstance,
			BiConsumer<File, Integer> successHandler, Consumer<Throwable> errHandler, Runnable onComplete) {

		return new JobInstanceManager() {

			@Override
			public JobInstance prepareJobInstance() {

				return prepareJobInstance != null ? prepareJobInstance.get() : getEmailSendJobInstance();
			}

			@Override
			public void onJobRunSuccessfull(File outFile, int status) {
				if (successHandler != null) {
					successHandler.accept(outFile, status);
				}
			}

			@Override
			public void onJobRunFail(Throwable throwable) {
				if (errHandler != null) {
					errHandler.accept(throwable);
				}
			}

			@Override
			public void onJobRunComplete() {
				if (onComplete != null) {
					onComplete.run();
				}
			}

			@Override
			public Integer jobId() {
				return 1;
			}

			@Override
			public JobInstanceManager jobId(Integer jobId) {
				return this;
			}
		};

	}

}
