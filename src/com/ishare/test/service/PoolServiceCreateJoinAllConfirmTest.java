package com.ishare.test.service;

import com.ishare.bean.PoolOrderBean;
import com.ishare.test.Main;

public class PoolServiceCreateJoinAllConfirmTest {

	/**
	 * @param args
	 * @throws Exception
	 */
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
	}

}
