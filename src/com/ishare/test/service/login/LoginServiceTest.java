package com.ishare.test.service.login;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.UserBean;
import com.ishare.service.LoginService;
import com.ishare.test.Container;

public class LoginServiceTest {

	ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
	Container container = ctx.getBean("container", Container.class);
	
	LoginService service = container.loginService;

	public void createToken() {
		service.createToken("token1",
				container.informationPrepareService
						.getUserByUsername("zonghan").getId());
	}

	public int authToken() {
		return service.authenticateByToken(
				container.informationPrepareService
						.getUserByUsername("zonghan").getId(), "token1");
	}

	public boolean authByPassword() {
		return service.authenticateByPassword("zonghan", "zzz");
	}

	public void deleteToken() {
		this.service.deleteToken(
				container.informationPrepareService
						.getUserByUsername("zonghan").getId(), "token1");
	}
	
	public UserBean login() {
		return service.login("zonghan", "pwd", "token2");
	}
}
