package com.ishare.test.service.informationPrepare;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.UserBean;
import com.ishare.bean.enums.GenderEnum;
import com.ishare.test.Container;
import com.ishare.test.Main;

public class InformationPrepareServiceTest {

	public static void createUsers() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		Container c = ctx.getBean("container", Container.class);
		UserBean userInDb = c.informationPrepareService
				.getUserByUsername("186");
		if (userInDb == null) {
			UserBean userBean = new UserBean();
			userBean.setNickname("zwu");
			userBean.setUsername("186");
			userBean.setPassword("pwd");

			Main.insertUser(userBean, c);
		}
		userInDb = c.informationPrepareService.getUserByUsername("186");
		userInDb.setGender(GenderEnum.MALE.getValue());
		c.informationPrepareService.improveUserInfo(userInDb);

		userInDb = c.informationPrepareService.getUserByUsername("zonghan");
		if (userInDb == null) {
			UserBean userBean = new UserBean();
			userBean.setNickname("zonghan");
			userBean.setUsername("zonghan");
			userBean.setPassword("pwd");

			Main.insertUser(userBean, c);
		}
		userInDb = c.informationPrepareService.getUserByUsername("zonghan");
		userInDb.setGender(GenderEnum.FEMALE.getValue());
		c.informationPrepareService.improveUserInfo(userInDb);

		userInDb = c.informationPrepareService.getUserByUsername("yaming");
		if (userInDb == null) {
			UserBean userBean = new UserBean();
			userBean.setNickname("yaming");
			userBean.setUsername("yaming");
			userBean.setPassword("pwd");
			userBean.setGender(GenderEnum.FEMALE.getValue());

			Main.insertUser(userBean, c);
		}
		userInDb = c.informationPrepareService.getUserByUsername("yaming");
		userInDb.setGender(GenderEnum.FEMALE.getValue());
		c.informationPrepareService.improveUserInfo(userInDb);
	}

	public static void createTokens() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		Container c = ctx.getBean("container", Container.class);
		c.loginService.createToken("token1", c.informationPrepareService
				.getUserByUsername("zonghan").getId());
		c.loginService.createToken("token0", c.informationPrepareService
				.getUserByUsername("186").getId());
		c.loginService.createToken("token2", c.informationPrepareService
				.getUserByUsername("yaming").getId());
	}
}
