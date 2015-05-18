package com.ishare.test.controller;

import com.ishare.bean.BaiduPush;
import com.ishare.bean.UserBean;

public class LoginControllerLoginTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PoolControllerTest.refreshData();
		LoginControllerTest controller = new LoginControllerTest();
		UserBean user = controller.login();
		System.out.println(user.getId());
		controller.updateBaiduPush();
		BaiduPush baiduPush = controller.integrationService
				.getBaiduPushByUser(user.getId());
		System.out.println(baiduPush.getBaiduUser());
	}

}
