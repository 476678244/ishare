package com.ishare.test.verify;

import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.PoolRealTimeOrderBean;
import com.ishare.dao.mongo.MongoOrderDAO;
import com.ishare.util.concurrency.PoolRealTimeOrderIdUtil;
import com.ishare.util.realtime.RealTimeOrderMonitor;

public class RealTimeOrderMonitorsPerformanceTest {

	public static void main(String[] args) {
		final ApplicationContext context = new ClassPathXmlApplicationContext(
				"beans.xml");
		MongoOrderDAO dao = context.getBean(MongoOrderDAO.class);
		for (int i = 0; i < 1000; i++) {
//			PoolRealTimeOrderBean order = new PoolRealTimeOrderBean();
//			order.setId(PoolRealTimeOrderIdUtil.generateId());
//			order.setStartTime(new Date(new Date().getTime() + 1000 * 20));
//			PoolRealTimeOrderPool.pool.put(order.getId(), order);
//			RealTimeOrderMonitor monitor = new RealTimeOrderMonitor(order, dao);
//			PoolRealTimeOrderPool.monitors.put(order.getId(), monitor);
//			new Thread(monitor).start();
		}
	}
}
