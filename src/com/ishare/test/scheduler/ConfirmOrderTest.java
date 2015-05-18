package com.ishare.test.scheduler;

import java.util.Date;

import com.ishare.util.SchedulerUtil;

public class ConfirmOrderTest {

	public static void main(String[] args) {
		SchedulerUtil.allConfirmIfNeed(new Date());
	}

}
