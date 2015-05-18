package com.ishare.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ishare.util.concurrency.OrderConcurrencyUtil;

public class NightlyUtil {

	private static List<Nightly> nightlyList = new ArrayList<Nightly>();

	static {
		nightlyList.add(new AuthNumberUtil());
		nightlyList.add(new OrderConcurrencyUtil());
		nightlyList.add(new NotificationConfirmUtil());
	}

	public static boolean atNight(Date now) {
		if (now.getHours() == 0 && now.getMinutes() == 0) {
			return true;
		}
		return false;
	}

	public static void nightlyDo(Date now) {
		if (atNight(now)) {
			for (Nightly nightlyRunner : nightlyList) {
				nightlyRunner.nigthlyDo();
			}
		}
	}
}
