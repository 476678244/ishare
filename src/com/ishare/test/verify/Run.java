package com.ishare.test.verify;

public class Run implements Runnable {

	@Override
	public void run() {
		try {
			Point.print();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
