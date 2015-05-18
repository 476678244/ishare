package com.ishare.test.controller;

import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.enums.PoolOrderTypeEnum;
import com.ishare.bean.param.GetOrderRequest;
import com.ishare.test.Main;

public class PoolControllerGetOrderByRequestTest {

	public static void main(String[] args) {
		Main.refreshData();
		PoolControllerTest test = new PoolControllerTest();
		long orderId = test.createOrder();
		GetOrderRequest request = new GetOrderRequest(
				orderId, null, PoolOrderTypeEnum.RESERVE.getValue());
		PoolOrderBean order = test.getOrderByRequest("zonghan", request);
		System.out.println("reserve order id:" + order.getId());
		String objectId = test.moveMysqlOrderToMongoDB(order);
		request = new GetOrderRequest(0, objectId, PoolOrderTypeEnum.REAL_TIME.getValue());
		order = test.getOrderByRequest("zonghan", request);
		System.out.println("real time order object id:" + order.getObjectId());
	}

}
