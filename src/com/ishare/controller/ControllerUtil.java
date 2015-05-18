package com.ishare.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ishare.bean.PoolOrderBean;

public class ControllerUtil {

	public final static Logger logger = LoggerFactory
			.getLogger(ControllerUtil.class);

	public static long UNKNOWN_USER = -9999;

	// 30 minutes later
	public static boolean validateCreateJoinSearchOrderDate(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MINUTE, 31);
		Date halfHourLater = calendar.getTime();
		if (date.after(halfHourLater)) {
			logger.info(String.format(
					"date validate(CreateJoinSearch) OK for [%s]", date));
			return true;
		}
		logger.info(String.format(
				"date validate(CreateJoinSearch) failed for [%s]", date));
		return false;
	}

	public static void resolveStartTimeLong(List<PoolOrderBean> orders) {
		for (PoolOrderBean order : orders) {
			if (order.getStartTime() == null) continue;
			order.setStartTimeLong(order.getStartTime().getTime());
		}
	}

	public static void resolveStartTimeLong(PoolOrderBean order) {
		order.setStartTimeLong(order.getStartTime().getTime());
	}
}
