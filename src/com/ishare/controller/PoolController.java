package com.ishare.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolRealTimeOrderBean;
import com.ishare.bean.RouteBean;
import com.ishare.bean.enums.PoolJoinerStatusEnum;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.bean.enums.RouteTypeEnum;
import com.ishare.bean.enums.StatusEnum;
import com.ishare.bean.param.GetOrderRequest;
import com.ishare.bean.param.RealTimePoolRequest;
import com.ishare.service.InformationPrepareService;
import com.ishare.service.LoginService;
import com.ishare.service.PoolService;
import com.ishare.util.MemoryUtil;
import com.ishare.util.MessageUtil;
import com.ishare.util.NotificationConfirmUtil;
import com.ishare.util.PayUtil;
import com.ishare.util.TransformerUtil;

@Controller
@Component("poolController")
@RequestMapping("/request/poolOrder")
public class PoolController {

	public static final boolean USE_MEMORY = false;

	@Autowired
	PoolService poolService;

	@Autowired
	InformationPrepareService informationPrepareService;

	@Autowired
	LoginService loginService;

	public final static Logger logger = LoggerFactory
			.getLogger(PoolController.class);

	/**
	 * return null means token wrong
	 */
	@RequestMapping(value = "/getOrders")
	@ResponseBody
	public List<PoolOrderBean> getMatchedOrders(
			@RequestParam("start_longtitude") long startLongtitude,
			@RequestParam("start_latitude") long startLatitude,
			@RequestParam("strat_address") String startAddress,
			@RequestParam("end_longtitude") long endLongtitude,
			@RequestParam("end_latitude") long endLatitude,
			@RequestParam("end_address") String endAddress,
			@RequestParam("startTimeLong") long startTime,
			@RequestParam("time_period") int timePeriod,
			@RequestParam("token") String token,
			@RequestParam("user_id") long userId,
			@RequestParam(value = "joiner_seats", required = false) int joinerSeats,
			@RequestParam(value = "gender_care", required = false) int genderCare) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return null;
		}
		Date startTimeDate = new Date(startTime);
		List<PoolOrderBean> poolOrders = new ArrayList<PoolOrderBean>();
		if (!ControllerUtil.validateCreateJoinSearchOrderDate(startTimeDate)) {
			return poolOrders;
		}
		if (USE_MEMORY) {
			poolOrders = MemoryUtil.getMatchedOrders(startLongtitude,
					startLatitude, endLongtitude, endLatitude, startTimeDate,
					timePeriod);
		} else {
			poolOrders = this.poolService.getMatchedOrders(startLongtitude,
					startLatitude, endLongtitude, endLatitude, startTimeDate,
					timePeriod, userId, startAddress, endAddress, joinerSeats,
					genderCare);
		}
		ControllerUtil.resolveStartTimeLong(poolOrders);
		return poolOrders;
	}

	// param as a json array containing the data of a order
	// return 0 if error occurs
	@RequestMapping(value = "/createOrdersWithJson")
	@ResponseBody
	public long createOrdersWithJson(
			@RequestParam("jsonOrder") String jsonOrder,
			@RequestParam("token") String token,
			@RequestParam("user_id") long user) {
		int tokenAuth = this.loginService.authenticateByToken(user, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return tokenAuth;
		}
		if (USE_MEMORY) {
			return MemoryUtil.createOrdersWithJson(jsonOrder);
		} else {
			PoolOrderBean order = TransformerUtil
					.JsonStringToPoolOrderBean(jsonOrder);
			if (!ControllerUtil.validateCreateJoinSearchOrderDate(order
					.getStartTime())) {
				return MessageUtil.CREATE_ORDER_NOT_VALID_START_DATE;
			}
			order.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
					.getValue());
			if (order.getPoolJoiners().isEmpty()) {
				logger.error("please prepare first joiner in order");
			}
			long userId = order.getPoolJoiners().get(0).getUserBean().getId();
			if (userId != user) {
				logger.error("input userid not equalsorder.joiner.user");
				return MessageUtil.CREATE_ORDER_USER_JOINER_NOT_MATCH;
			}
			RouteBean joinerRoute = new RouteBean();
			joinerRoute.setStartSitePoint(order.getStartSitePoint());
			joinerRoute.setEndSitePoint(order.getEndSitePoint());
			order.getPoolJoiners().get(0).setRouteBean(joinerRoute);
			long orderId = this.poolService.createOrder(order, userId);
			logger.info("order[" + orderId
					+ "] with joiner created succesfully");
			return orderId;
		}
	}

	@RequestMapping(value = "/giveUpOrder")
	@ResponseBody
	public String giveUpOrder(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return String.valueOf(tokenAuth);
		}
		if (USE_MEMORY) {
			MemoryUtil.giveUpOrder(userId, orderId);
			return "";
		} else {
			PoolOrderBean order = this.poolService
					.getInProcessOrderById(orderId);
			logger.info(String.format(
					"order[%s] found refer to orderId[%s] in giveupOrder(",
					order.toString(), orderId));
			String result = this.poolService.giveUpOrder(userId, order);
			logger.info(String.format("user[%s] give up order[%s] result[%s]",
					userId, orderId, result));
			return result;
		}
	}

	@RequestMapping(value = "/searchMyOrders")
	@ResponseBody
	public List<PoolOrderBean> searchMyOrders(
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return null;
		}
		List<PoolOrderBean> myOrders = new ArrayList<PoolOrderBean>();
		if (USE_MEMORY) {
			myOrders = MemoryUtil.searchMyOrders(userId);
		} else {
			myOrders = this.poolService.getMysqlUserOrders(userId);
		}
		ControllerUtil.resolveStartTimeLong(myOrders);
		return myOrders;
	}
	
	@RequestMapping(value = "/getUserOrders")
	@ResponseBody
	public List<PoolOrderBean> getUserOrders(
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return null;
		}
		List<PoolOrderBean> myOrders = new ArrayList<PoolOrderBean>();
		myOrders = this.poolService.getUserAllOrders(userId, false);
		ControllerUtil.resolveStartTimeLong(myOrders);
		return myOrders;
	}

	/**
	 * @param seatCounts
	 *            seatCounts contains driver_user_seats
	 * @param endJsonPoint
	 *            every joiner has his own senPoint
	 * @return order status[null means join failed or token failed]
	 */
	@RequestMapping(value = "/joinOrder")
	@ResponseBody
	public String joinOrder(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId,
			@RequestParam("seats_count") int seatsCount,
			@RequestParam("endJsonPoint") String endJsonPoint,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return String.valueOf(tokenAuth);
		}
		if (USE_MEMORY) {
			return MemoryUtil.joinOrder(userId, orderId, seatsCount,
					endJsonPoint, this.informationPrepareService);
		} else {
			// get order from db
			PoolOrderBean order = this.poolService
					.getInProcessOrderById(orderId);
			if (!ControllerUtil.validateCreateJoinSearchOrderDate(order
					.getStartTime())) {
				return MessageUtil.JOIN_ORDER_SOONER_THAN_30_MINUTES;
			}
			// create new joiner
			PoolJoinerBean newJoiner = new PoolJoinerBean();
			newJoiner.setPaid(false);
			// create route bean
			RouteBean route = new RouteBean();
			route.setStartSitePoint(order.getStartSitePoint());
			route.setEndSitePoint(TransformerUtil
					.JsonStringToSitePointBean(endJsonPoint));
			route.setStatus(StatusEnum.ACTIVE.getValue());
			route.setType(RouteTypeEnum.INUSE.getValue());
			newJoiner.setRouteBean(route);
			newJoiner.setSeatsCount(seatsCount);
			newJoiner.setStatus(PoolJoinerStatusEnum.UNCONFIRM.getValue());
			newJoiner.setUserBean(this.informationPrepareService
					.getUserByUserId(userId));
			String status = null;
			try {
				status = this.poolService.joinOrder(newJoiner, order);
				logger.info(String.format("joiner[%s] joined to order[%s] with result status[%s]",
								newJoiner.toString(), order.getId(), status));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return status;
		}
	}

	@RequestMapping(value = "/computMyFee")
	@ResponseBody
	public int computeMyFee(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId,
			@RequestParam("single_fee") int singleFee,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return tokenAuth;
		}
		PoolOrderBean order = this.poolService.getInProcessOrderById(orderId);
		int totalSeats = order.getTotalSeats();
		try {
			int seatCounts = this.poolService.getSeatCountsByOrderUser(order,
					userId);
			return PayUtil.computeMyFee(totalSeats, seatCounts, singleFee);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return 0;
	}

	/**
	 * driver joined in, will move this order to pool order list
	 * 
	 * @param driverSeatCounts
	 *            driver also has seatCounts , but we can set 1 as default for
	 *            driver now
	 */
	@RequestMapping(value = "/robOrder")
	@ResponseBody
	public String robOrder(@RequestParam("driver_id") long driverId,
			@RequestParam("driver_seat_counts") int driverSeatCounts,
			@RequestParam("order_id") long orderId) {
		if (USE_MEMORY) {
			return MemoryUtil.robOrder(driverId, driverSeatCounts, orderId,
					informationPrepareService);
		} else {
			// ...
			return "";
		}
	}

	@RequestMapping(value = "/confirmOrder")
	@ResponseBody
	public String confirmOrder(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId) {
		if (USE_MEMORY) {
			return MemoryUtil.confirmOrder(userId, orderId);
		} else {
			// ...
			return "";
		}
	}

	@RequestMapping(value = "/confirmGiveUpOrder")
	@ResponseBody
	public String confirmGiveUpOrder(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId) {
		if (USE_MEMORY) {
			return MemoryUtil.confirmGiveUpOrder(userId, orderId);
		} else {
			// ...
			return "";
		}
	}

	@RequestMapping(value = "/getNearbyOrders")
	@ResponseBody
	public List<PoolOrderBean> getNearbyOrders(
			@RequestParam("jsonPoint") String jsonPoint) {
		List<PoolOrderBean> poolOrders = new ArrayList<PoolOrderBean>();
		if (USE_MEMORY) {
			return MemoryUtil.getNearbyOrders(jsonPoint);
		} else {
			// ...
		}
		return poolOrders;
	}

	@RequestMapping(value = "/confirmNotification")
	@ResponseBody
	public void confirmNotification(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return;
		}
		NotificationConfirmUtil.confirm(userId, orderId);
	}

	@RequestMapping(value = "/getOrderById")
	@ResponseBody
	public PoolOrderBean getOrderById(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			PoolOrderBean errorOrder = new PoolOrderBean();
			errorOrder.setId(tokenAuth);
			return errorOrder;
		}
		PoolOrderBean order = this.poolService.getInProcessOrderById(orderId);
		ControllerUtil.resolveStartTimeLong(order);
		return order;
	}

	@RequestMapping(value = "/getOrderByRequest")
	@ResponseBody
	public PoolOrderBean getOrderByRequest(@RequestParam("user_id") long userId,
			@RequestParam("token") String token, 
			@RequestParam("requsetJson") String requsetJson) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			PoolOrderBean errorOrder = new PoolOrderBean();
			errorOrder.setId(tokenAuth);
			return errorOrder;
		}
		GetOrderRequest request = TransformerUtil.JsonStringToGetOrderRequest(requsetJson);
		if (request.getOrderId() > 0 || !StringUtils.isBlank(request.getObjectId())) {
		} else {
			return null;
		}
		PoolOrderBean order = this.poolService.getOrderByRequest(request);
		ControllerUtil.resolveStartTimeLong(order);
		return order;
	}

	@RequestMapping(value = "/submitRealTimePoolRequest")
	@ResponseBody
	public PoolRealTimeOrderBean submitRealTimePoolRequest(
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token,
			@RequestParam("realTimePoolRequestJson") String realTimePoolRequestJson) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			PoolRealTimeOrderBean errorOrder = new PoolRealTimeOrderBean();
			errorOrder.setId(tokenAuth);
			return errorOrder;
		}
		RealTimePoolRequest request = TransformerUtil
				.JsonStringToRealTimePoolRequest(realTimePoolRequestJson);
		PoolRealTimeOrderBean order = this.poolService.matchRealTimePoolOrder(request);
		return order;
	}
	
	@RequestMapping(value = "/confirmRealTimeOrderGo")
	@ResponseBody
	public String confirmRealTimeOrderGo(
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token,
			@RequestParam("realTimeOrderId") long realTimeOrderId) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return "error";
		}
		return this.poolService.confirmRealTimeOrderGo(userId, realTimeOrderId);
	}
	
	@RequestMapping(value = "/giveUpRealTimeOrder")
	@ResponseBody
	public String giveUpRealTimeOrder(
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token,
			@RequestParam("realTimeOrderId") long realTimeOrderId) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return "error";
		}
		return this.poolService.giveUpRealTimeOrder(userId, realTimeOrderId);
	}
}
