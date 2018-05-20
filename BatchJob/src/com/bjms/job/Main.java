package com.bjms.job;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Test batch job for BJMS.
 * 
 * @author Sreejith VS
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {

		System.out.println("****** Test Batch Job *****");

		Map<String, String> params = new HashMap<>();
		for (String arg : args) {

			int keyEnds = arg.indexOf("=");
			if (keyEnds > 0) {
				params.put(arg.substring(0, keyEnds), arg.substring(keyEnds + 1));
			} else {
				params.put(arg, "");
			}
		}

		int maxWait = 10 + new Random().nextInt(20); // by default wait for 10-30 seconds
		System.out.println("parameters of batch = " + params);

		if (params.containsKey("maxWait")) {
			maxWait = Integer.parseInt(params.get("maxWait"));
		}

		System.out.println(String.format("Batch need %s seconds time to finish", maxWait));

		// Sample tasks of batch.
		Runnable batchTask = () -> System.out.println("no op: " + new Date());

		if (params.containsKey("emailIds")) {
			Iterator<String> emailIdItr = Arrays.asList(params.get("emailIds").split(",")).iterator();
			batchTask = () -> {
				if (emailIdItr.hasNext()) {
					System.out.println("Sending email to : " + emailIdItr.next());
				} else {
					Thread.currentThread().interrupt();
				}
			};
		}

		if (params.containsKey("copyFiles")) {

			Iterator<String> filesItr = Arrays.asList(params.get("copyFiles").split(",")).iterator();
			batchTask = () -> {
				if (filesItr.hasNext()) {
					System.out.println("Copying file : " + filesItr.next());
				} else {
					Thread.currentThread().interrupt();
				}
			};

		}

		if (params.containsKey("data")) {

			Iterator<String> datasItr = Arrays.asList(params.get("data").split(",")).iterator();
			batchTask = () -> {
				if (datasItr.hasNext()) {
					String[] pair = datasItr.next().split("\\:");
					System.out.println(String.format("Input data to Algorithm (x=%s,y=%s)", pair[0], pair[1]));
				} else {
					System.out.println(String.format("Find value of y = f(%s)", params.get("x")));
					Thread.currentThread().interrupt();
				}
			};
		}

		try {
			while (maxWait-- > 0 && !Thread.interrupted()) {
				Thread.sleep(1000);
				batchTask.run();
			}
			System.out.println("Batch got finished " + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
