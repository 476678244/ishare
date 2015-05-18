package com.ishare.test.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RunOnTime {

	static int count = 0;

	/**
	 * create date:2009- 6- 10 author:Administrator
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		operateOnTime();
	}

	/**
	 * 
	 * java 定时执行 create date:2009- 6- 10 author:Administrator
	 * 
	 */
	public static void operateOnTime() {

		// 定时执行的任务
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				// 在此代码内调用要执行任务的代码 ...
				System.out.println(" 第 " + ++count + " 次执行 ");
				System.out.println("start doing @:" + new Date());
				try {
					Thread.sleep(800000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("end doing @:" + new Date());
			}

		};

		// 创建一个定时器
		Timer timer = new Timer();
		// 设置在多长时间以后执行，然后每个多长时间重复执行
		// 设值 5 秒钟后开始执行第一次，以后每隔 2 秒中执行一次
		// timer.schedule(task, 5 * 1000, 2 * 1000);

		// 设置从某一时刻开始执行，然后每隔多长时间重复执行
		// 设置从当前时间开始执行，然后每间隔2秒执行一次
		timer.schedule(task, Calendar.getInstance().getTime(), 60 * 1000 * 5);
	}

}