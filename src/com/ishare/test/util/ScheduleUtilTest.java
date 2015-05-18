package com.ishare.test.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.service.IntegrationService;
import com.ishare.service.PoolService;

public class ScheduleUtilTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		PoolService poolService = context.getBean(PoolService.class);

		IntegrationService integrationService = context
				.getBean(IntegrationService.class);

		Calendar calendar = new GregorianCalendar(2014, 7, 25, 12, 15);
		Date date = calendar.getTime();
		poolService.FinishOrdersIfNeed(date);
		System.out.println(integrationService);
	}

}
