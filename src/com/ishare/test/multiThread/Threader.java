package com.ishare.test.multiThread;

public class Threader extends Thread{

	AddName addName = null;
	
	public Threader() {
		addName = new AddName();
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			addName.execute();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
