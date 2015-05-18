package com.ishare.test.service;

import com.ishare.bean.PoolOrderBean;
import com.ishare.test.Main;

public class PoolServiceJoinOrderTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Main.refreshData();
		PoolServiceTest service = new PoolServiceTest();
		long orderId = service.createOrder();
		service.joinOrder();
		PoolOrderBean order = service.container.poolService
				.getInProcessOrderById(orderId);
		int formerSize = order.getPoolJoiners().size();
		System.out.println(formerSize);
		service.container.poolService.giveUpOrder(PoolServiceTest.currentUser, order);
		order = service.container.poolService.getInProcessOrderById(orderId);
		int currentSize = order.getPoolJoiners().size();
		System.out.println(currentSize);
		if (formerSize <= currentSize) {
			throw new Exception();
		}
	}

}
