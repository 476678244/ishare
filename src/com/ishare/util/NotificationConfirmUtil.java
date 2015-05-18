package com.ishare.util;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationConfirmUtil implements Nightly {

	private static Map<Long, Set<Long>> orderUserMap = new Hashtable<Long, Set<Long>>();

	public final static Logger logger = LoggerFactory
			.getLogger(NotificationConfirmUtil.class);

	public static synchronized void confirm(long userId, long orderId) {
		Set<Long> users = orderUserMap.get(orderId);
		if (null == users) {
			users = new HashSet<Long>();
			users.add(userId);
			orderUserMap.put(orderId, users);
		} else {
			users.add(userId);
		}
	}

	public static boolean isUserConfirmed(long userId, long orderId) {
		Set<Long> users = orderUserMap.get(orderId);
		if (null == users) {
			return false;
		}
		if (users.contains(userId)) {
			return true;
		}
		return false;
	}

	@Override
	public void nigthlyDo() {
		logger.info(String
				.format("clearing orderUserMap of NotificationConfirmUtil with size[%s]",
						orderUserMap.size()));
		logger.info(String.format(
				"orderUserMap of NotificationConfirmUtil in json format:%s",
				TransformerUtil.ObjectToJson(orderUserMap)));
		orderUserMap.clear();
		logger.info(String
				.format("cleared orderUserMap of NotificationConfirmUtil with size[%s]",
						orderUserMap.size()));
	}

	public static Map<Long, Set<Long>> getOrderUserMap() {
		return orderUserMap;
	}
	
}
