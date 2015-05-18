package com.ishare.util.realtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolRealTimeOrderBean;
import com.ishare.service.InformationPrepareService;
import com.ishare.util.ContextUtil;

public class PoolRealTimeOrderPool {

	public final static Logger logger = LoggerFactory
			.getLogger(PoolRealTimeOrderPool.class);

	private static Map<Long, PoolRealTimeOrderBean> pool = new Hashtable<Long, PoolRealTimeOrderBean>();

	private static Map<Long, RealTimeOrderMonitor> monitors = new Hashtable<Long, RealTimeOrderMonitor>();

	public static Collection<PoolRealTimeOrderBean> getAllOrders() {
		Collection<PoolRealTimeOrderBean> allOrders = pool.values();
		for (PoolRealTimeOrderBean order : allOrders) {
			refreshOrderRelatedInformation(order);
		}
		return allOrders;		
	}

	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * to get real time order from pool refer to orderId
	 */
	public static PoolRealTimeOrderBean getOrderById(long orderId) {
		PoolRealTimeOrderBean order = pool.get(orderId);
		refreshOrderRelatedInformation(order);
		return order;
	}

	public static void removeOrder(long orderId) {
		PoolRealTimeOrderPool.pool.remove(orderId);
	}

	public static void addOrder(PoolRealTimeOrderBean order) {
		pool.put(order.getId(), order);
	}

	public static void startMonitoring(PoolRealTimeOrderBean order) {
		RealTimeOrderMonitor monitor = new RealTimeOrderMonitor(order);
		new Thread(monitor).start();
		PoolRealTimeOrderPool.monitors.put(order.getId(), monitor);
	}
	
	public static void stopMonitoring(long orderId) {
		RealTimeOrderMonitor monitor = monitors.get(orderId);
		monitor.stopMonitoring();
	}
	
	/**
	 * this is to get user`s order, currently user can just have one order
	 */
	public static List<PoolRealTimeOrderBean> getUserOrders(long userId, boolean refreshOrderRelatedInfo) {
		List<PoolRealTimeOrderBean> orders = new ArrayList<PoolRealTimeOrderBean>();
		for (PoolRealTimeOrderBean order : pool.values()) {
			for (PoolJoinerBean joiner : order.getPoolJoiners()) {
				if (joiner.getUserBean().getId() == userId) {
					if (refreshOrderRelatedInfo) {
						refreshOrderRelatedInformation(order);
					}
					orders.add(order);
					return orders;
				}
			}
		}
		return orders;
	}
	
	public static void deleteOrder(Long orderId) {
		monitors.get(orderId).stopMonitoring();
		monitors.remove(orderId);
		pool.remove(orderId);
	}
	
	public static void refreshOrderRelatedInformation(PoolRealTimeOrderBean order) {
		InformationPrepareService informationPrepareService = 
			ContextUtil.getContext().getBean(InformationPrepareService.class);
		for (PoolJoinerBean joiner : order.getPoolJoiners()) {
			joiner.setUserBean(informationPrepareService.getUserByUserId(
				joiner.getUserBean().getId()));
		}
	}
}
