package com.ishare.test.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.ishare.bean.PoolOrderBean;
import com.ishare.test.Main;

public class PoolServiceGetMatchedOrdersTest {

	public static void main(String[] args) throws Exception {
		Main.refreshData();
		PoolServiceTest service = new PoolServiceTest();
		long orderId = service.createOrder();
		service.joinOrder();
		Calendar calendar = new GregorianCalendar(2014, 7, 25, 12, 45);
		Date date = calendar.getTime();
		service.createOrder(date, 3);
		calendar = new GregorianCalendar(2014, 7, 25, 12, 15);
		date = calendar.getTime();
		service.createOrder(date, 3);
		List<PoolOrderBean> orders = service.getMatchedOrders(orderId);
		System.out.println(orders.size() + " expected 2");
	}

}
