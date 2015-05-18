package com.ishare.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolRealTimeOrderBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.GenderEnum;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.bean.enums.PoolOrderTypeEnum;
import com.ishare.bean.param.RealTimePoolRequest;
import com.ishare.integration.huanxin.ChatTools;
import com.ishare.integration.huanxin.HuanXinTokenManager;
import com.ishare.integration.huanxin.SendGroupMessage;
import com.ishare.util.MemoryUtil;
import com.ishare.util.PropertyUtil;
import com.ishare.util.concurrency.PoolRealTimeOrderIdUtil;
import com.ishare.util.realtime.PoolRealTimeOrderPool;
import com.ishare.util.realtime.RealTimeOrderMonitor;

@Service
public class RealTimePoolService {

	public final static Logger logger = LoggerFactory
			.getLogger(RealTimePoolService.class);

	private static long deviation = MemoryUtil.deviation;

	private static long MEANFUL_TIME_TO_WAIT_JOINER = 1000 * 60 * 10;

	/**
	 * this is to get the best memory-cached real time order from pool refer to
	 * real time pool request if no matched order, return null
	 */
	public PoolRealTimeOrderBean getBestMatchedOrder(RealTimePoolRequest request) {
		PoolRealTimeOrderBean order = null;
		// ...
		if (PoolRealTimeOrderPool.getPoolSize() == 0)
			return null;
		Collection<PoolRealTimeOrderBean> orders = PoolRealTimeOrderPool
				.getAllOrders();
		for (PoolRealTimeOrderBean temp : orders) {
			// must exactly same start point
			if (temp.getStartSitePoint().getLaitude() != request
					.getStartLatitude()
					|| temp.getStartSitePoint().getLongtitude() != request
							.getStartLongtitude())
				continue;
			// the destination can be in range of 500m
			if (temp.getEndSitePoint().getLaitude() > request.getEndLatitude() + deviation
					|| temp.getEndSitePoint().getLaitude() < request
							.getEndLatitude() - deviation
					|| temp.getEndSitePoint().getLongtitude() > request
							.getEndLongtitude() + deviation
					|| temp.getEndSitePoint().getLongtitude() < request
							.getEndLongtitude() - deviation)
				continue;
			// guarantee the seats
			if (temp.getTotalSeats() - CountSeats(temp.getPoolJoiners()) < request
					.getSeatsCount())
				continue;
			// can't join an order you already in? change to restrict only one
			// chance to real time tackle if it's a female only order
			if (request.getSubject() != null
					&& request.getSubject().getGender()
							.equals(GenderEnum.FEMALE.getValue())) {
				if (!temp.getPoolSubject().getGender()
						.equals(GenderEnum.FEMALE.getValue()))
					continue;
			}
			// all examination passed,
			// then it's the very matched order, just return
			order = temp;
			return order;
		}
		return null;
	}

	/**
	 * 1. create a new order, generate id for this order 2. create a new HuanXin
	 * chat group, set chatGroupId to order 3. add this created order to pool
	 */
	public PoolRealTimeOrderBean createOrderToPool(RealTimePoolRequest request,
			InformationPrepareService informationPrepareService) {
		PoolRealTimeOrderBean order = new PoolRealTimeOrderBean();
		order.setId(PoolRealTimeOrderIdUtil.generateId());
		// ...
		order.setCaptainUserId(request.getUserId());
		order.setPoolOrderType(PoolOrderTypeEnum.REAL_TIME.getValue());
		// order will start after 10 mins
		Date startTime = new Date(new Date().getTime() + MEANFUL_TIME_TO_WAIT_JOINER);
		order.setStartTime(startTime);
		order.setTotalSeats(Integer.valueOf(PropertyUtil.DEFAULT_REAL_TIME_ORDER_TOTAL_SEATS));
		order.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
				.getValue());
		order.setPoolSubject(request.getSubject());
		List<PoolJoinerBean> joinerList = new ArrayList<PoolJoinerBean>();
		UserBean captainUser = informationPrepareService
				.getUserByUserId(request.getUserId());
		PoolJoinerBean captain = new PoolJoinerBean(captainUser, request.getSeatsCount());
		joinerList.add(captain);
		order.setPoolJoiners(joinerList);
		order.setStartSitePoint(new SitePointBean(request.getStartLongtitude(),
				request.getStartLatitude(), request.getStartAddress()));
		order.setEndSitePoint(new SitePointBean(request.getEndLongtitude(),
				request.getEndLatitude(), request.getEndAddress()));
		// create HuanXin ChatGroup for the real_time order
		String groupid = ChatTools.createChatGroupForRealTime(startTime, captainUser,
				request);
		ChatTools.sendNewNotify(groupid, captainUser.getNickname());
		order.setChatGroupId(groupid);
		// to memory real_time order pool
		PoolRealTimeOrderPool.addOrder(order);
		PoolRealTimeOrderPool.startMonitoring(order);
		return order;
	}

	/**
	 * this is to add a user to target order 1. construct joiner refer to userId
	 * 2. add joiner to target order 3. add user to HuanXin chat group of target
	 * order ! notice : we should take care of whether it need concurrency
	 * concern
	 */
	public PoolRealTimeOrderBean addUserToOrder(RealTimePoolRequest request,
			PoolRealTimeOrderBean order,
			InformationPrepareService informationPrepareService) {
		UserBean user = informationPrepareService.getUserByUserId(request
				.getUserId());
		PoolJoinerBean joiner = new PoolJoinerBean(user, request.getSeatsCount());
		// add to order
		List<PoolJoinerBean> newJoinerList = order.getPoolJoiners();
		newJoinerList.add(joiner);
		order.setPoolJoiners(newJoinerList);
		Date freshStartTime = new Date(new Date().getTime()
				+ RealTimeOrderMonitor.MORE_TIME_FOR_NEW_JOINER);
		order.setStartTime(freshStartTime);
		order.setStartTimeLong(freshStartTime.getTime());
		// add to HuanXin ChatGroup
		ChatTools.addUsertoChatGroup(order.getChatGroupId(), user.getUsername());
		ChatTools.sendAddNotify(order.getChatGroupId(), user.getNickname());
		// check if the order is full
		if (order.getTotalSeats() - CountSeats(order.getPoolJoiners()) == 0) {
			order.setStatus(PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_UNJOINED
					.getValue());
		}
		return order;
	}

	public String removeUserFromRealTimeOrder(long userId, long orderId,
			InformationPrepareService informationPrepareService) {
		PoolRealTimeOrderBean order = PoolRealTimeOrderPool
				.getOrderById(orderId);
		// only one joiner, delete order &chatGroup directly
		if (order.getPoolJoiners().size() == 1) {
			PoolRealTimeOrderPool.removeOrder(orderId);
			ChatTools.deleteChatGroup(order.getChatGroupId());
		}
		// there are more than 1 joiner, and the requester is not captain user
		else if (order.getCaptainUserId() != userId) {
			List<PoolJoinerBean> joinerList = order.getPoolJoiners();
			for (int i = 0; i < joinerList.size(); i++) {
				if (joinerList.get(i).getId() == userId) {
					joinerList.remove(i);
					break;
				}
			}
			order.setPoolJoiners(joinerList);
			UserBean user = informationPrepareService.getUserByUserId(userId);
			ChatTools.deleteUserFromChatGroup(order.getChatGroupId(),
					user.getUsername());
		}
		// there are more than 1 joiner, and the requester is the very captain
		// user
		else {
			List<PoolJoinerBean> joinerList = order.getPoolJoiners();
			for (int i = 0; i < joinerList.size(); i++) {
				if (joinerList.get(i).getId() == userId) {
					joinerList.remove(i);
					break;
				}
			}
			order.setPoolJoiners(joinerList);
			UserBean user = informationPrepareService.getUserByUserId(userId);
			ChatTools.deleteUserFromChatGroup(order.getChatGroupId(),
					user.getUsername());
			// select a new captain user
			PoolJoinerBean newCaptain = joinerList.get(0);
			order.setCaptainUserId(newCaptain.getId());
			order.setStartSitePoint(newCaptain.getRouteBean()
					.getStartSitePoint());
			order.setEndSitePoint(newCaptain.getRouteBean().getEndSitePoint());
			// 这里应该传递给客户端一则信息，以便让这个新captainUser的聊天室界面发生变化（多出“出发”按钮）！
			// 实现方法可以是给改新captainUser发送一个百度推送，让他点击跳转新的界面
			// 或者是系统在聊天室发送一条特殊的信息，说明指定谁来当队长，然后修改客户端接受聊天信息时候的处理，
			// 使之能截获这条信息，新队长截获信息便自动发生变化。
			// 目前看第二种方法客户体验好点。还可以再讨论讨论
			String changeCaptain = newCaptain.getUserBean().getNickname()
					+ "，成为了新创建者，拥有了发布出发的权限";
			new SendGroupMessage(order.getChatGroupId(), changeCaptain,
					HuanXinTokenManager.getToken());
		}
		return "ok";
	}

	/**
	 * Count the total seats joiners used
	 */
	private static int CountSeats(List<PoolJoinerBean> lst) {
		int count = 0;
		for (PoolJoinerBean temp : lst) {
			count += temp.getSeatsCount();
		}
		return count;
	}

	public List<PoolRealTimeOrderBean> getUserOrders(long userId, boolean fullOrder) {
		boolean refreshOrderRelatedInfo = fullOrder;
		return PoolRealTimeOrderPool.getUserOrders(userId, refreshOrderRelatedInfo);
	}
	
	public List<PoolOrderBean> getOrders() {
		Collection<PoolRealTimeOrderBean> ordersCollection = PoolRealTimeOrderPool.getAllOrders();
		List<PoolOrderBean> inMemoryOrders = new ArrayList<PoolOrderBean>(
			ordersCollection.size());
		inMemoryOrders.addAll(ordersCollection);
		return inMemoryOrders;
	}
	
	/**
	 * @param orderId real time order id
	 */
	public PoolRealTimeOrderBean getOrderById(long orderId) {
		return PoolRealTimeOrderPool.getOrderById(orderId);
	}
	
	public void deleteUserOrders(long userId) {
		List<PoolRealTimeOrderBean> userOrders = this.getUserOrders(userId, false);
		for (PoolRealTimeOrderBean order : userOrders) {
			PoolRealTimeOrderPool.removeOrder(order.getId());
		}
	}
}
