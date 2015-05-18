package com.ishare.test.memory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.RoleEnum;
import com.ishare.controller.PoolController;
import com.ishare.test.Container;
import com.ishare.util.MemoryUtil;
import com.ishare.util.TransformerUtil;

public class CreateRobOrderTest {
	
	private static UserBean user = null;
	
	private static UserBean driver = null;

	public static void prepareUser(Container c) {
		user = new UserBean();
		user.setNickname("zwu");
		user.setUsername("zonghan");
		user.setPassword("pwd");
		
		driver = new UserBean();
		driver.setNickname("zym");
		driver.setUsername("zhaoyaming");
		driver.setPassword("pwd");
		driver.setRole(RoleEnum.DRIVER.getValue());
		
		c.informationPrepareService.regiserUser(user);
		c.informationPrepareService.regiserUser(driver);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		Container c = ctx.getBean("container", Container.class);
		CreateRobOrderTest.prepareUser(c);
		
		PoolController pool = ctx.getBean("poolController", PoolController.class);
		
		PoolOrderBean order = new PoolOrderBean();
		order.setTotalSeats(1);
		PoolJoinerBean joiner = new PoolJoinerBean();
		joiner.setUserBean(user);
		order.getPoolJoiners().add(joiner);
		
		String orderJson = TransformerUtil.PoolOrderBeanToJsonString(order);
		pool.createOrdersWithJson(orderJson, "token1", user.getId());
		
		if (MemoryUtil.poolInProgressOrders.size() == 1) {
			System.out.println("create order successfully!");
		} else {
			System.out.println("create order failed!");
		}
		
		pool.robOrder(driver.getId(), 1, MemoryUtil.poolInProgressOrders.get(0).getId());

		System.out.println(MemoryUtil.poolInProgressOrders.get(0).getStatus());
	}

}
