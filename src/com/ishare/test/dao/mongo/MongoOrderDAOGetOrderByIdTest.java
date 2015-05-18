package com.ishare.test.dao.mongo;

import com.ishare.bean.PoolOrderBean;

public class MongoOrderDAOGetOrderByIdTest {

	public static void main(String[] args) {
		MongoOrderDAOTest test = new MongoOrderDAOTest();
		//test.saveOrder();
		PoolOrderBean order = test.getOrderByObjectId();
		System.out.println(order.getPoolJoiners().get(0).getUserBean().getUsername());
	}

}
