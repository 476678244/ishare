package com.ishare.test.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.ishare.bean.PoolOrderBean;
import com.ishare.test.Main;

public class PoolServiceGetUserOrdersTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main.refreshData();
		PoolServiceTest service = new PoolServiceTest();
		service.createOrder();
		Calendar calendar = new GregorianCalendar(2014, 7, 25, 12, 30);
		Date date = calendar.getTime();
		service.createOrder(date);
		calendar = new GregorianCalendar(2014, 7, 25, 12, 45);
		date = calendar.getTime();
		service.createOrder(date);
		List<PoolOrderBean> orders = service.getUserOrders(PoolServiceTest.currentUser);
		System.out.println(orders.size());
		System.out.println(orders.get(0).getDistance());
		System.out.println(orders.get(0).getNote());
	}

}
