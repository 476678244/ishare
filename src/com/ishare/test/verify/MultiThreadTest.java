package com.ishare.test.verify;

public class MultiThreadTest {
	
	public static void main(String[] args) throws InterruptedException {
		new Thread(new Run()).start();
		//Thread.sleep(5);
		new Thread(new Run()).start();
	}

}
