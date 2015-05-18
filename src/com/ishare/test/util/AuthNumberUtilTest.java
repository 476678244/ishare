package com.ishare.test.util;

import java.util.Date;

import com.ishare.util.AuthNumberUtil;

public class AuthNumberUtilTest {

	public static void main(String[] args) {
		String number = AuthNumberUtil.generateAuthNumber(6);
		AuthNumberUtil.saveAuthNumber("zonghan", number);
		number = AuthNumberUtil.generateAuthNumber(6);
		AuthNumberUtil.saveAuthNumber("zonghan", number);
		number = AuthNumberUtil.generateAuthNumber(6);
		AuthNumberUtil.saveAuthNumber("zonghan", number);
		System.out.println(number);
		AuthNumberUtil.auth("zonghan", number, new Date());

	}

}
