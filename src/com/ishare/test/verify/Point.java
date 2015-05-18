package com.ishare.test.verify;

public class Point {

	public static int x = 0;
	
	public static void print() throws InterruptedException {
		for (int i = 0 ; i < 10 ; i++) {
			System.out.println(Thread.currentThread().getId() + "/" + i);
			Thread.sleep(10);
		}
	}
}
