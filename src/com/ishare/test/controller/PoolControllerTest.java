package com.ishare.test.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolRealTimeOrderBean;
import com.ishare.bean.PoolSubjectBean;
import com.ishare.bean.RouteBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.AgeEnum;
import com.ishare.bean.enums.AtmosphereEnum;
import com.ishare.bean.enums.GenderEnum;
import com.ishare.bean.enums.JobEnum;
import com.ishare.bean.enums.PoolJoinerStatusEnum;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.bean.enums.PoolOrderTypeEnum;
import com.ishare.bean.enums.RouteTypeEnum;
import com.ishare.bean.enums.StatusEnum;
import com.ishare.bean.param.GetOrderRequest;
import com.ishare.bean.param.RealTimePoolRequest;
import com.ishare.controller.PoolController;
import com.ishare.service.LoginService;
import com.ishare.service.PoolService;
import com.ishare.test.Container;
import com.ishare.test.Main;
import com.ishare.test.service.informationPrepare.InformationPrepareServiceTest;
import com.ishare.util.TransformerUtil;

public class PoolControllerTest {

	public static long currentUser = 0;
	public static long currentOrderId = 1;
	public static long currentJoinerId = 0;

	ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
	PoolController poolController = ctx.getBean("poolController",
			PoolController.class);
	LoginService loginService = ctx.getBean(LoginService.class);
	PoolService poolService = ctx.getBean(PoolService.class);

	Container container = ctx.getBean("container", Container.class);

	public static void refreshData() {
		Main.deleteData();
		InformationPrepareServiceTest.createUsers();
		InformationPrepareServiceTest.createTokens();
	}

	public long createOrder() {
		PoolOrderBean order = new PoolOrderBean();
		order.setCaptainUserId(1);
		SitePointBean endSitePoint = new SitePointBean(11, 12, "address1");
		order.setEndSitePoint(endSitePoint);
		order.setStartSitePoint(new SitePointBean(12, 13, "address12"));
		PoolSubjectBean subject = new PoolSubjectBean();
		subject.setAge(AgeEnum.EIGHTYS.getValue());
		subject.setAtmosphere(AtmosphereEnum.OUTGOING.getValue());
		subject.setGender(GenderEnum.FEMALE.getValue());
		subject.setJob(JobEnum.STUDENT.getValue());
		order.setPoolSubject(subject);
		UserBean user = container.informationPrepareService
				.getUserByUsername("zonghan");
		currentUser = user.getId();
		PoolJoinerBean joiner = new PoolJoinerBean();
		joiner.setSeatsCount(2);
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
		order.setTotalSeats(4);
		order.setStartTime(new Date(new Date().getTime() + 1000 * 60 * 60));
		order.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
				.getValue());
		long orderId = this.poolController.createOrdersWithJson(
				TransformerUtil.PoolOrderBeanToJsonString(order), "token1",
				user.getId());
		currentOrderId = orderId;
		return orderId;
	}

	public String joinOrder() {
		SitePointBean endSite = new SitePointBean();
		endSite.setAddress("end address2");
		long userId = this.container.informationPrepareService
				.getUserByUsername("yaming").getId();
		String status = this.poolController.joinOrder(userId, currentOrderId, 1,
			TransformerUtil.SitePointBeanToJsonString(endSite),
				this.loginService.getUserToken(userId));
		return status;
	}

	public String giveUpJoiner(String username, long orderId) {
		long userId = this.container.informationPrepareService
				.getUserByUsername(username).getId();
		return this.poolController.giveUpOrder(
				userId, orderId, this.loginService.getUserToken(userId));
	}

	public PoolRealTimeOrderBean submitRealTimePoolRquest(String username) {
		UserBean user = container.informationPrepareService
				.getUserByUsername(username);
		RealTimePoolRequest request = new RealTimePoolRequest();
		request.setUserId(user.getId());
		request.setStartLongtitude(1);
		request.setStartLatitude(2);
		request.setStartAddress("address1");
		request.setEndLongtitude(1);
		request.setEndLatitude(1);
		request.setEndAddress("address1");
		request.setSeatsCount(1);
		return this.poolController.submitRealTimePoolRequest(user.getId(),
				"token1", TransformerUtil.ObjectToJson(request));
	}
	
	public PoolOrderBean getOrderByRequest(String username, GetOrderRequest request) {
		UserBean user = container.informationPrepareService.getUserByUsername(username);
		String requsetJson = TransformerUtil.ObjectToJson(request);
		return this.poolController.getOrderByRequest(
				user.getId(), loginService.getUserToken(user.getId()), requsetJson);
	}
	
	public String moveMysqlOrderToMongoDB(PoolOrderBean order) {
		return poolService.moveOrderToMongoDB(order);
	}
}
