package com.ishare.test.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolSubjectBean;
import com.ishare.bean.RouteBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.AgeEnum;
import com.ishare.bean.enums.AtmosphereEnum;
import com.ishare.bean.enums.JobEnum;
import com.ishare.bean.enums.PoolJoinerStatusEnum;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.bean.enums.RouteTypeEnum;
import com.ishare.bean.enums.StatusEnum;
import com.ishare.test.Container;

public class PoolServiceTest {

	public static long currentUser = 0;

	ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
	Container container = ctx.getBean("container", Container.class);

	public long createOrder() {
		// construct date
		Calendar calendar = new GregorianCalendar(2014, 7, 25, 12, 15);
		Date date = calendar.getTime();
		return this.createOrder(date, 3);
	}

	public long createOrder(Date date) {
		return this.createOrder(date, 4);
	}

	@Test
	public long createOrder(Date date, int totalSeats) {
		PoolOrderBean order = new PoolOrderBean();
		order.setCaptainUserId(1);
		SitePointBean endSitePoint = new SitePointBean(11, 12, "address1");
		order.setEndSitePoint(endSitePoint);
		order.setStartSitePoint(new SitePointBean(12, 13, "address12"));
		PoolSubjectBean subject = new PoolSubjectBean();
		subject.setAge(AgeEnum.EIGHTYS.getValue());
		subject.setAtmosphere(AtmosphereEnum.OUTGOING.getValue());
		//subject.setGender(GenderEnum.FEMALE.getValue());
		subject.setJob(JobEnum.STUDENT.getValue());
		order.setPoolSubject(subject);
		UserBean user = container.informationPrepareService
				.getUserByUsername("zonghan");
		currentUser = user.getId();
		PoolJoinerBean joiner = new PoolJoinerBean();
		joiner.setSeatsCount(1);
		joiner.setStatus(PoolJoinerStatusEnum.UNCONFIRM.getValue());
		RouteBean route = new RouteBean();
		route.setStartSitePoint(order.getStartSitePoint());
		route.setEndSitePoint(endSitePoint);
		route.setStatus(StatusEnum.ACTIVE.getValue());
		route.setType(RouteTypeEnum.INUSE.getValue());
		joiner.setRouteBean(route);
		joiner.setUserBean(user);
		order.getPoolJoiners().add(joiner);
		order.setDistance(200);
		order.setNote("note1");
		order.setTotalSeats(totalSeats);
		order.setStartTime(date);
		order.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
				.getValue());
		return container.poolService.createOrder(order, user.getId());
	}

	@Test
	public List<PoolOrderBean> getUserOrders(long userId) {
		List<PoolOrderBean> orders = container.poolService.getUserAllOrders(currentUser);
		return orders;
	}

	@Test
	public void joinOrder() throws Exception {
		// input
		// userId , orderId, seatsCount, endSitePoint
		UserBean user = container.informationPrepareService
				.getUserByUsername("yaming");
		PoolOrderBean order = container.poolService.getMysqlUserOrders(currentUser)
				.get(0);
		int seatsCount = 1;
		SitePointBean endSite = new SitePointBean();
		endSite.setLongtitude(1);
		endSite.setLaitude(2);
		endSite.setAddress("Address1");
		// prepare data on behalf of controller
		PoolJoinerBean newJoiner = new PoolJoinerBean();
		newJoiner.setSeatsCount(seatsCount);
		RouteBean route = new RouteBean();
		route.setStartSitePoint(order.getStartSitePoint());
		route.setEndSitePoint(endSite);
		route.setStatus(StatusEnum.ACTIVE.getValue());
		route.setType(RouteTypeEnum.INUSE.getValue());
		newJoiner.setRouteBean(route);
		newJoiner.setStatus(PoolJoinerStatusEnum.UNCONFIRM.getValue());
		newJoiner.setUserBean(user);
		// run service
		container.poolService.joinOrder(newJoiner, order);
	}

	public void allConfirm() {
		UserBean user = container.informationPrepareService
				.getUserByUsername("zonghan");
		long userId = user.getId();
		PoolOrderBean order = container.poolService.getMysqlUserOrders(userId)
				.get(0);
		container.poolService.allConfirm(order);
	}

	public boolean needConfirm() {
		UserBean user = container.informationPrepareService
				.getUserByUsername("zonghan");
		long userId = user.getId();
		PoolOrderBean order = container.poolService.getMysqlUserOrders(userId)
				.get(0);
		return container.poolService.needConfirm(order.getId());
	}

	public List<PoolOrderBean> getMatchedOrders(long orderId) {
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		Calendar calendar = new GregorianCalendar(2014, 7, 25, 12, 30);
		Date startTime = calendar.getTime();
		UserBean user = container.informationPrepareService
				.getUserByUsername("186");
		PoolOrderBean order = container.poolService
				.getInProcessOrderById(orderId);
		orders = container.poolService.getMatchedOrders(0, 0, 0, 0, startTime,
				30, user.getId(), order.getStartSitePoint().getAddress(), order
						.getEndSitePoint().getAddress(), 1, 0);
		return orders;
	}
}
