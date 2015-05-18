package com.ishare.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.bind.annotation.RequestParam;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.RouteBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.enums.PoolJoinerStatusEnum;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.bean.enums.RoleEnum;
import com.ishare.bean.enums.RouteTypeEnum;
import com.ishare.service.InformationPrepareService;

public class MemoryUtil {

	// for new order , add to progress orders
	// for finished/failed order, move to historyOrders

	public static List<PoolOrderBean> poolHistoryOrders = new ArrayList<PoolOrderBean>();

	public static List<PoolOrderBean> poolInProgressOrders = new ArrayList<PoolOrderBean>();

	// public static List<UserBean> users = new ArrayList<UserBean>();
	// the longitude and latitude deviation to define as the same point in Shanghai
	public static long deviation = 3500;

	private static long orderSequence = 0;

	public static long getOrderSeq() {
		return ++orderSequence;
	}

	private static final long offset_geo_passenger = 4000;
	// LQL code, offset to judge if they are same point, 2000 is 220 m
	private static final long offset_geo_driver = 8000;
	// LQL code, offset for driver to get orders

	@SuppressWarnings("unused")
	private static final double offset_distance = 0.2;

	// LQL code, offset to judge if the way-point is acceptable

	public static List<PoolOrderBean> getMatchedOrders(long startLongtitude,
			long startLatitude, long endLongtitude, long endLatitude,
			Date startTime, int timePeriod) {
		List<PoolOrderBean> poolOrders = new ArrayList<PoolOrderBean>();
		PoolOrderBean temp; // LQL code
		// LQL code, to return right orders
		for (int i = 0; i < MemoryUtil.poolInProgressOrders.size(); i++) {
			temp = MemoryUtil.poolInProgressOrders.get(i);
			if (temp.getStatus().equals(
					PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_JOINED)
					|| temp.getStatus()
							.equals(PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_UNJOINED)
					|| temp.getStatus().equals(
							PoolOrderStatusEnum.JOINERS_CONFIRMED)) {
				continue;
			}
			// same start_point
			if (Math.abs(startLongtitude
					- temp.getStartSitePoint().getLongtitude()) < offset_geo_passenger
					&& Math.abs(startLatitude
							- temp.getStartSitePoint().getLaitude()) < offset_geo_passenger) {
				// same end_point
				if (Math.abs(endLongtitude
						- temp.getEndSitePoint().getLongtitude()) < offset_geo_passenger
						&& Math.abs(endLatitude
								- temp.getEndSitePoint().getLaitude()) < offset_geo_passenger) {
					poolOrders.add(temp);
				}
				// way-point
				// else{
				// SitePointBean waypoint = new
				// SitePointBean(endLongtitude,endLatitude,null);
				// //fit our demanding
				// if(route_distance(temp.getStartSitePoint(),waypoint)
				// +route_distance(temp.getEndSitePoint(),waypoint)
				// -route_distance(temp.getStartSitePoint(),temp.getEndSitePoint())<offset_distance){
				// poolOrders.add(temp);
				// }
				// }
			}
		}

		// // sample:
		// poolOrders.addAll(MemoryUtil.poolPrepareOrders);
		// poolOrders.sort()
		return poolOrders;
	}

	// param as a json array containing the data of a order
	public static long createOrdersWithJson(String jsonOrder) {
		// !!! this method need to prepare
		// PoolJoinerBean,PoolSubjectBean,PoolOrderBean from android side
		PoolOrderBean poolOrderBean = TransformerUtil
				.JsonStringToPoolOrderBean(jsonOrder);
		poolOrderBean
				.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
						.getValue());
		poolOrderBean.setId(MemoryUtil.getOrderSeq());
		MemoryUtil.poolInProgressOrders.add(poolOrderBean);
		return poolOrderBean.getId();
	}

	public static void giveUpOrder(long userId, long orderId) {
		// delete joiner from pool orders list
		for (PoolOrderBean poolOrderBean : MemoryUtil.poolInProgressOrders) {
			if (poolOrderBean.getId() == orderId) {
				for (PoolJoinerBean poolJoinerBean : poolOrderBean
						.getPoolJoiners()) {
					if (poolJoinerBean.getUserBean().getId() == userId) {
						poolOrderBean.getPoolJoiners().remove(poolJoinerBean);
						// update order status
						if (PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_UNJOINED
								.getValue().equals(poolOrderBean.getStatus())) {
							poolOrderBean
									.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
											.getValue());
						} else if (PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_JOINED
								.getValue().equals(poolOrderBean.getStatus())) {
							poolOrderBean
									.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_JOINED
											.getValue());
						}
						// need to update captain user id
						if (userId == poolOrderBean.getCaptainUserId()) {
							poolOrderBean.setCaptainUserId(poolOrderBean
									.getPoolJoiners().get(0).getUserBean()
									.getId());
						}
						if (poolOrderBean.getPoolJoiners().isEmpty()) {
							MemoryUtil.poolInProgressOrders
									.remove(poolOrderBean);
						}
						return;
					}
				}
			}
		}
	}

	public static List<PoolOrderBean> searchMyOrders(long userId) {
		// first search in preparing orders
		List<PoolOrderBean> myOrders = new ArrayList<PoolOrderBean>();
		for (PoolOrderBean poolOrderBean : MemoryUtil.poolInProgressOrders) {
			for (PoolJoinerBean poolJoinerBean : poolOrderBean.getPoolJoiners()) {
				if (poolJoinerBean.getUserBean().getId() == userId) {
					myOrders.add(poolOrderBean);
				}
			}
		}
		// then search pooled orders and finished/failed orders
		for (PoolOrderBean poolOrderBean : MemoryUtil.poolHistoryOrders) {
			for (PoolJoinerBean poolJoinerBean : poolOrderBean.getPoolJoiners()) {
				if (poolJoinerBean.getUserBean().getId() == userId) {
					myOrders.add(poolOrderBean);
				}
			}
		}
		return myOrders;
	}

	private static PoolOrderBean findPoolInProgressOrderBeanByOrderId(
			long orderId) {
		for (PoolOrderBean poolOrderBean : MemoryUtil.poolInProgressOrders) {
			if (poolOrderBean.getId() == orderId) {
				return poolOrderBean;
			}
		}
		return null;
	}

	private static PoolOrderBean findPoolHistoryOrderBeanByOrderId(long orderId) {
		for (PoolOrderBean poolOrderBean : MemoryUtil.poolHistoryOrders) {
			if (poolOrderBean.getId() == orderId) {
				return poolOrderBean;
			}
		}
		return null;
	}

	private static PoolJoinerBean findFromPoolJoiners(long userId,
			List<PoolJoinerBean> joiners) {
		for (PoolJoinerBean poolJoinerBean : joiners) {
			if (userId == poolJoinerBean.getUserBean().getId()) {
				return poolJoinerBean;
			}
		}
		return null;
	}

	// for driver, will set the default seatCount as 0
	// order will become seats full when passengers are complete
	private static boolean seatsFull(PoolOrderBean order) {
		int totalSeats = order.getTotalSeats();
		int seats = 0;
		for (PoolJoinerBean joiner : order.getPoolJoiners()) {
			if (RoleEnum.DRIVER.getValue().equals(
					joiner.getUserBean().getRole())) {
				// do not include driver seats count now
				continue;
			}
			seats += joiner.getSeatsCount();
		}
		if (totalSeats <= seats) {
			return true;
		}
		return false;
	}

	private static boolean seatsAvailable(PoolOrderBean order) {
		int totalSeats = order.getTotalSeats();
		int seats = 0;
		for (PoolJoinerBean joiner : order.getPoolJoiners()) {
			if (RoleEnum.DRIVER.getValue().equals(
					joiner.getUserBean().getRole())) {
				// do not include driver seats count now
				continue;
			}
			seats += joiner.getSeatsCount();
		}
		if (totalSeats > seats) {
			return true;
		}
		return false;
	}

	public static String joinOrder(long userId, long orderId, int seatCounts,
			String endJsonPoint, InformationPrepareService infoService) {
		// get order
		PoolOrderBean order = findPoolInProgressOrderBeanByOrderId(orderId);
		// add joiner to order
		PoolJoinerBean poolJoiner = new PoolJoinerBean();
		poolJoiner.setSeatsCount(seatCounts);
		poolJoiner.setStatus(PoolJoinerStatusEnum.UNCONFIRM.getValue());
		poolJoiner.setUserBean(infoService.getUserByUserId(userId));
		RouteBean route = new RouteBean();
		route.setEndSitePoint(TransformerUtil
				.JsonStringToSitePointBean(endJsonPoint));
		route.setType(RouteTypeEnum.INUSE.getValue());
		poolJoiner.setRouteBean(route);
		// check first
		if (!seatsAvailable(order)) {
			return "join failed!";
		}
		order.getPoolJoiners().add(poolJoiner);
		// change status
		if (seatsFull(order)) {
			if (order.getDriverUserId() == 0) {
				order.setStatus(PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_UNJOINED
						.getValue());
			} else {
				order.setStatus(PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_JOINED
						.getValue());
			}
		}
		return order.getStatus();
	}

	public static String robOrder(long driverId, int driverSeatCounts,
			long orderId, InformationPrepareService infoService) {
		// get order
		PoolOrderBean order = findPoolInProgressOrderBeanByOrderId(orderId);
		// add joiner to order
		PoolJoinerBean driverJoiner = new PoolJoinerBean();
		driverJoiner.setSeatsCount(driverSeatCounts);
		// for driver, by default join means confirm to go
		driverJoiner.setStatus(PoolJoinerStatusEnum.CONFIRM_GO.getValue());
		driverJoiner.setUserBean(infoService.getUserByUserId(driverId));
		order.getPoolJoiners().add(driverJoiner);
		order.setDriverUserId(driverId);
		// change status
		order.setStatus(PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_JOINED
				.getValue());
		return order.getStatus();
	}

	public static String confirmOrder(@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId) {
		// get order
		PoolOrderBean order = findPoolHistoryOrderBeanByOrderId(orderId);
		List<PoolJoinerBean> joiners = order.getPoolJoiners();
		PoolJoinerBean currentJoiner = findFromPoolJoiners(userId, joiners);
		// set status for currentJoiner
		currentJoiner.setStatus(PoolJoinerStatusEnum.CONFIRM_GO.getValue());
		// change order status if needed
		boolean allConfirmed = true;
		for (PoolJoinerBean joiner : joiners) {
			if (!joiner.getStatus().equals(PoolJoinerStatusEnum.CONFIRM_GO)) {
				allConfirmed = false;
			}
		}
		if (allConfirmed) {
			order.setStatus(PoolOrderStatusEnum.JOINERS_CONFIRMED.getValue());
		} else {
			// passenger pending...
			// do noting...
		}
		return order.getStatus();
	}

	public static String confirmGiveUpOrder(
			@RequestParam("user_id") long userId,
			@RequestParam("order_id") long orderId) {
		PoolOrderBean order = findPoolHistoryOrderBeanByOrderId(orderId);
		PoolJoinerBean joiner = findFromPoolJoiners(userId,
				order.getPoolJoiners());
		// remove joiner
		order.getPoolJoiners().remove(joiner);
		// change others` status to unconfirmed
		for (PoolJoinerBean otherJoiner : order.getPoolJoiners()) {
			otherJoiner.setStatus(PoolJoinerStatusEnum.UNCONFIRM.getValue());
		}
		// need to push others to confirm again
		return "ok!";
	}

	public static List<PoolOrderBean> getNearbyOrders(
			@RequestParam("jsonPoint") String jsonPoint) {
		List<PoolOrderBean> poolOrders = new ArrayList<PoolOrderBean>();
		// LQL code
		PoolOrderBean temp;
		SitePointBean spb = TransformerUtil
				.JsonStringToSitePointBean(jsonPoint);
		for (int i = 0; i < MemoryUtil.poolInProgressOrders.size(); i++) {
			temp = MemoryUtil.poolInProgressOrders.get(i);
			if (Math.abs(spb.getLongtitude()
					- temp.getStartSitePoint().getLongtitude()) < offset_geo_driver
					&& Math.abs(spb.getLaitude()
							- temp.getStartSitePoint().getLaitude()) < offset_geo_driver) {
				poolOrders.add(temp);
			}
		}
		// poolOrders.addAll(MemoryUtil.poolPrepareOrders);
		// sort orders..
		return poolOrders;
	}

	// LQL code, get the route distance
	@SuppressWarnings("unused")
	private static long route_distance(SitePointBean s1, SitePointBean s2) {
		String result = "";
		BufferedReader in = null;
		String p1 = (double) s1.getLaitude() / 10e6 + ","
				+ (double) s1.getLongtitude() / 10e6;
		String p2 = (double) s2.getLaitude() / 10e6 + ","
				+ (double) s2.getLongtitude() / 10e6;
		String urlstr = "http://api.map.baidu.com/direction/v1/routematrix?"
				+ "output=json&ak=hhAt8u6GxqKaB24SyvEVgxI3&" + "origins=" + p1
				+ "&" + "destinations=" + p2;
		try {
			URL realUrl = new URL(urlstr);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			System.out.println(result);
			JSONObject jb = JSONObject.fromObject(result);
			JSONObject js1 = jb.getJSONObject("result");
			JSONArray jo = js1.getJSONArray("elements");
			jb = jo.getJSONObject(0);
			js1 = jb.getJSONObject("distance");
			long distance = js1.getLong("value");
			return distance;
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
			return -1;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}
}
