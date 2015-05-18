package com.ishare.util.concurrency;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.dao.impl.PoolJoinerDAO;
import com.ishare.dao.impl.PoolOrderJoinerMapDAO;
import com.ishare.util.Nightly;
import com.ishare.util.TransformerUtil;

public class OrderConcurrencyUtil implements Nightly {

	public final static Logger logger = LoggerFactory
			.getLogger(OrderConcurrencyUtil.class);

	/** Long refer to order id */
	public static Map<Long, OrderConcurrencyUtil> concurrencyObjects = new HashMap<Long, OrderConcurrencyUtil>();

	public static synchronized OrderConcurrencyUtil getOrderConcurrencyUtil(
			long orderId) {
		OrderConcurrencyUtil object = OrderConcurrencyUtil.concurrencyObjects
				.get(orderId);
		if (object == null) {
			object = new OrderConcurrencyUtil();
			OrderConcurrencyUtil.concurrencyObjects.put(orderId, object);
		}
		return object;
	}

	public static boolean addJoinerToOrder(PoolJoinerBean newJoiner,
			PoolOrderBean order, PoolOrderJoinerMapDAO poolOrderJoinerMapDAO,
			PoolJoinerDAO poolJoinerDAO) {
		OrderConcurrencyUtil object = OrderConcurrencyUtil
				.getOrderConcurrencyUtil(order.getId());
		if (object != null) {
			logger.info("******successfully get concurrency object!");
		} else {
			logger.error("error!");
		}
		return object.syncAddJoinerToOrder(newJoiner, order,
				poolOrderJoinerMapDAO, poolJoinerDAO);
	}

	/**
	 * add joiner to order one by one for same order
	 * 
	 * @return joiner result
	 */
	private synchronized boolean syncAddJoinerToOrder(PoolJoinerBean newJoiner,
			PoolOrderBean order, PoolOrderJoinerMapDAO poolOrderJoinerMapDAO,
			PoolJoinerDAO poolJoinerDAO) {
		long userId = newJoiner.getUserBean().getId();
		// confirm whether seatsFull
		if (poolOrderJoinerMapDAO.seatsFull(order.getId(),
				order.getTotalSeats())) {
			return false;
		}
		// create joiner in DB
		long joinerId = poolJoinerDAO.createJoiner(newJoiner);
		logger.info(String.format("joiner[%s] created", joinerId));
		// add joiner order map
		poolOrderJoinerMapDAO
				.addJoinerOrderMap(order.getId(), joinerId, userId);
		logger.info(String.format("order[%s] joiner[%s] user[%s] map added",
				order.getId(), joinerId, userId));
		return true;
	}

	@Override
	public void nigthlyDo() {
		logger.info(String
				.format("clearing OrderConcurrencyUtil.concurrencyObjects with size[%s]",
						OrderConcurrencyUtil.concurrencyObjects.size()));
		logger.info(String.format("concurrencyObjects as json:",
				TransformerUtil.ObjectToJson(concurrencyObjects)));
		OrderConcurrencyUtil.concurrencyObjects.clear();
		logger.info(String
				.format("OrderConcurrencyUtil.concurrencyObjects cleared with size[%s]",
						OrderConcurrencyUtil.concurrencyObjects.size()));
	}
}
