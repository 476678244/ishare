package com.ishare.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextUtil {

	private static ApplicationContext context = new ClassPathXmlApplicationContext(
			"beans.xml");

	public static ApplicationContext getContext() {
		return context;
	}
}
