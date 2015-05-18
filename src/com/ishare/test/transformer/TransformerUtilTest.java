package com.ishare.test.transformer;

import java.util.Date;

import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.UserBean;
import com.ishare.util.TransformerUtil;

public class TransformerUtilTest {

	public static void main(String[] args) {

		PoolOrderBean order = new PoolOrderBean();
		Date date = new Date();
		long s = date.getTime();
		Date newDate = new Date(s);
		order.setStartTime(date);
		
		String json = TransformerUtil.PoolOrderBeanToJsonString(order);
		System.out.println(json);
		TransformerUtil.JsonStringToPoolOrderBean(json);
		
		UserBean user = null;
		String jsonUser = TransformerUtil.UserBeanToJsonString(user);
		System.out.println(jsonUser);
	}
}
