package com.ishare.test.controller;

import java.util.Date;

import com.ishare.controller.ControllerUtil;

public class ControllerUtilTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date now = new Date();
		ControllerUtil.validateCreateJoinSearchOrderDate(now);

	}

}
