package com.ishare.test.util;

import com.ishare.util.NotificationConfirmUtil;

public class NotificationConfirmUtilTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NotificationConfirmUtil.confirm(1, 1);
		NotificationConfirmUtil.confirm(2, 1);
		NotificationConfirmUtil.confirm(1, 2);
		NotificationConfirmUtil.confirm(2, 1);
		
		System.out.println(NotificationConfirmUtil.isUserConfirmed(1, 1));
		
		System.out.println(NotificationConfirmUtil.isUserConfirmed(1, 2));
		
		System.out.println(NotificationConfirmUtil.isUserConfirmed(3, 1));
	}

}
