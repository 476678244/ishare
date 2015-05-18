package com.ishare.test.multiThread;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadTest {

	public static void main(String[] args) throws InterruptedException {
		List<Threader> threads = new ArrayList<Threader>();
		for (int i = 0; i < 100; i++) {
			threads.add(new Threader());
		}

		for (Threader threader : threads) {
			threader.start();
		}

		Thread.sleep(1000);
		System.out.println(AddName.names.size());
	}

}
