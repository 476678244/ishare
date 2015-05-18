package com.ishare.test.service.informationPrepare;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.UserBean;
import com.ishare.test.Container;
import com.ishare.test.Main;

public class UpdateHeadPicUrlTest {

	public static void main(String[] args) {
		Main.refreshData();
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		Container c = ctx.getBean("container", Container.class);
		UserBean user = c.informationPrepareService.getUserByUsername("zonghan");
		c.informationPrepareService.updateUserPic(user.getId(), "testpicurl.jpg");
		user = c.informationPrepareService.getUserByUsername("zonghan");
		System.out.println(user.getHeadPic());
	}

}
