package com.ishare.test.controller;

import com.ishare.bean.PoolRealTimeOrderBean;

public class PoolControllerSubmitRealTimePoolRequestTest {

	public static void main(String[] args) {
		//test create real time order
		PoolControllerTest test = new PoolControllerTest();
		PoolControllerTest.refreshData();
		PoolRealTimeOrderBean order = test.submitRealTimePoolRquest("13162577232");
		System.out.println(order.getId());
		
		//test add to a real time order
		PoolRealTimeOrderBean order_add = test.submitRealTimePoolRquest("15216708797");
		System.out.println(order_add.getId()+","+order.getId());
		
		//test give up joiner
//		String result = test.giveUpJoiner("15216708797");
//		System.out.println(result);
		
		//confirm order
		
	}

}
