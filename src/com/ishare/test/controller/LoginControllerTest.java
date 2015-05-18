package com.ishare.test.controller;

import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpSession;

import com.ishare.bean.UserBean;
import com.ishare.controller.LoginController;
import com.ishare.service.IntegrationService;

public class LoginControllerTest {

	ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
	LoginController loginController = ctx.getBean(LoginController.class);
	IntegrationService integrationService = ctx
			.getBean(IntegrationService.class);

	public UserBean login() {
		HttpSession session = new MockHttpSession();
		;
		UserBean user = loginController.login("zonghan", "pwd", session,
				"baiduUser", "baiduChannel");
		return user;
	}

	public UserBean updateBaiduPush() {
		HttpSession session = new MockHttpSession();
		UserBean user = loginController.login("zonghan", "pwd", session,
				"baiduUser2", "baiduChannel2");
		return user;
	}
}
