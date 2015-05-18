package com.ishare.test.dao.mongo;

import java.util.List;

import com.ishare.bean.PoolOrderBean;

public class MongoDAOGetUserOrdersTest {

	public static void main(String[] args) {
		MongoOrderDAOTest test = new MongoOrderDAOTest();
		List<PoolOrderBean> orders = test.getUserOrders();
		System.out.println(orders.size());
	}

}
