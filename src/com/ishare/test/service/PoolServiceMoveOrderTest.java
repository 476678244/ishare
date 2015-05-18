package com.ishare.test.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.ishare.bean.PoolOrderBean;
import com.ishare.test.Main;

public class PoolServiceMoveOrderTest {

	public static void main(String[] args) throws Exception {
		Main.refreshData();
		PoolServiceTest service = new PoolServiceTest();
		long orderId = service.createOrder();
		boolean needConfirm = service.needConfirm();
		System.out.println(needConfirm);
		service.joinOrder();
		needConfirm = service.needConfirm();
		System.out.println(needConfirm);
		service.allConfirm();
		PoolOrderBean order = service.container.poolService
				.getInProcessOrderById(orderId);
		System.out.println(order.getStatus());
		Calendar calendar = new GregorianCalendar(2014, 7, 25, 12, 15);
		Date date = calendar.getTime();
		service.container.poolService.MoveOnTimeOrdersToMongo(date);
	}

}
