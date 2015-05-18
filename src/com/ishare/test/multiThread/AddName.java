package com.ishare.test.multiThread;

import java.util.ArrayList;
import java.util.List;

public class AddName {

	public static List<String> names = new ArrayList<String>();

	// if remove synchronized, result will be 100
	public static synchronized void execute() throws InterruptedException {
		if (names.size() >= 5) {
			return;
		}
		Thread.sleep(100);
		names.add("name");

	}
}
