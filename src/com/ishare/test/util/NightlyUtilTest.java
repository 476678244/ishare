package com.ishare.test.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.ishare.util.NightlyUtil;
import com.ishare.util.NotificationConfirmUtil;

public class NightlyUtilTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NotificationConfirmUtil.confirm(1, 1);
		NotificationConfirmUtil.confirm(2, 1);
		NotificationConfirmUtil.confirm(1, 2);
		NotificationConfirmUtil.confirm(2, 1);
		Calendar calendar = new GregorianCalendar(2014, 7, 25, 0, 0);
		Date startTime = calendar.getTime();
		NightlyUtil.nightlyDo(startTime);

	}

}
