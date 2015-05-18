package com.ishare.test.controller;

import com.ishare.test.Main;

public class PoolControllerCreateJoinGiveUpOrderTest {

	public static void main(String[] args) {
		PoolControllerTest test = new PoolControllerTest();
		Main.refreshData();
		long orderId = test.createOrder();
		System.out.println(orderId);
		String status = test.joinOrder();
		System.out.println(status);
		String result = test.giveUpJoiner("zonghan", orderId);
		System.out.println(result);
	}

}
