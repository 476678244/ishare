package com.ishare.test.util;

import com.ishare.util.PropertyUtil;

public class PropertyUtilTest {

	public static void main(String[] args) {
		String text = PropertyUtil.readProperty("reserve_inform_go_huanxin");
		System.out.println(text);
	}

}
