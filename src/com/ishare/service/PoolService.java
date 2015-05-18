package com.ishare.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolRealTimeOrderBean;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.bean.enums.PoolOrderTypeEnum;
import com.ishare.bean.param.GetOrderRequest;
import com.ishare.bean.param.RealTimePoolRequest;
import com.ishare.dao.impl.PoolJoinerDAO;
import com.ishare.dao.impl.PoolOrderDAO;
import com.ishare.dao.impl.PoolOrderJoinerMapDAO;
import com.ishare.dao.mongo.MongoOrderDAO;
import com.ishare.util.MessageUtil;
import com.ishare.util.concurrency.OrderConcurrencyUtil;

@Service
public class PoolService {

	private final double Earth_R = 6371004; // The Radius of the earth,the unit
											// is meter(m)
	private final double E_dist = 500; // acceptable deviation of same points
										// (m)
	@Autowired
	PoolOrderDAO poolOrderDAO;

	@Autowired
	@Qualifier("poolJoinerDAO")
	PoolJoinerDAO poolJoinerDAO;

	@Autowired
	@Qualifier("poolOrderJoinerMapDAO")
	PoolOrderJoinerMapDAO poolOrderJoinerMapDAO;

	@Autowired
	MongoOrderDAO mongoOrderDAO;

	@Autowired
	InformationPrepareService informationPrepareService;

	@Autowired
	RealTimePoolService realTimePoolService;

	public final static Logger logger = LoggerFactory
			.getLogger(PoolService.class);

	public List<PoolOrderBean> getMatchedOrders(long startLongtitude,
			long startLatitude, long endLongtitude, long endLatitude,
			Date startTime, int timePeriod, long userId, String startAddress,
			String endAddress, int joinerSeats, int genderCare) {
		List<PoolOrderBean> matchedOrders = this.poolOrderDAO
				.getMathchedOrdersOrderByStartTimeAsc(startLongtitude,
						startLatitude, endLongtitude, endLatitude, startTime,
						timePeriod, userId, joinerSeats, genderCare);
		logger.info(String.format("%s matched orders found for user[%s]",
				matchedOrders.size(), userId));
		return matchedOrders;
	}

	@Transactional
	public long createOrder(PoolOrderBean order, long userId) {
		// 1. create order with subject
		long orderId = this.poolOrderDAO.addOrder(order, userId);
		logger.info("order[" + orderId + "] created");
		// 2. create joiner with route
		long joinerId = this.poolJoinerDAO.createJoiner(order.getPoolJoiners()
				.get(0));
		logger.info("joiner[" + joinerId + "] created for order[" + orderId
				+ "]");
		// 3. create joiner order map
		this.poolOrderJoinerMapDAO.addJoinerOrderMap(orderId, joinerId, userId);
		return orderId;
	}

	public List<PoolOrderBean> getMysqlUserOrders(long userId) {
		return getMysqlUserOrders(userId, true);
	}
	
	// get user in progress orders just from MYSQL
	public List<PoolOrderBean> getMysqlUserOrders(long userId, boolean fullOrder) {
		List<Long> inProcessOrderIs = this.poolOrderJoinerMapDAO
				.getUserPrepareOrderIds(userId);
		List<PoolOrderBean> inProcessOrders = this.poolOrderDAO
				.getInProcessOrdersByIds(inProcessOrderIs, fullOrder);
		return inProcessOrders;
	}

	public List<PoolOrderBean> getUserAllOrders(long userId) {
		return this.getUserAllOrders(userId, true);
	}
	
	public List<PoolOrderBean> getUserAllOrders(long userId, boolean fullOrder) {
		List<PoolOrderBean> allOrders = new ArrayList<PoolOrderBean>();
		List<PoolOrderBean> mysqlOrders = this.getMysqlUserOrders(userId, fullOrder);
		List<PoolRealTimeOrderBean> realTimeOrders = this.realTimePoolService
				.getUserOrders(userId, fullOrder);
		List<PoolOrderBean> mongodbOrders = this.mongoOrderDAO
				.getUserOrders(userId, fullOrder);
		// user can just have one real time order, at first
		allOrders.addAll(realTimeOrders);
		// user may have several reserve orders, at second
		allOrders.addAll(mysqlOrders);
		// MongoDB orders includes both orders and real time orders
		// it means history orders, at last
		allOrders.addAll(mongodbOrders);
		return allOrders;
	}

	@Transactional
	public String joinOrder(PoolJoinerBean newJoiner, PoolOrderBean order)
			throws Exception {
		if (newJoiner.getUserBean() == null
				&& newJoiner.getUserBean().getId() == 0) {
			logger.error("please assign userId in newJoiner");
			throw new Exception("please assign userId in newJoiner");
		}
		// confirm whether user already joined to this order
		long userId = newJoiner.getUserBean().getId();
		if (this.poolJoinerDAO.userJoined(userId, order.getId())) {
			logger.warn(String.format("user[%s] already joined this order[%s]",
					userId, order.getId()));
			return MessageUtil.JOIN_ORDER_USER_ALREADY_JOINED_TO_ORDER;
		}
		// add joiner to order via the OrderConcurrencyUtil
		boolean successfullyJoined = OrderConcurrencyUtil.addJoinerToOrder(
				newJoiner, order, poolOrderJoinerMapDAO, poolJoinerDAO);
		if (!successfullyJoined) {
			return MessageUtil.JOIN_ORDER_GRAB_FAIL;
		}
		// change order status when joiner successfully joined
		if (this.poolOrderJoinerMapDAO.seatsFull(order.getId(),
				order.getTotalSeats())
				&& successfullyJoined) {
			// if seats full
			if (order.getDriverUserId() == 0) {
				order.setStatus(PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_UNJOINED
						.getValue());
			} else {
				order.setStatus(PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_JOINED
						.getValue());
			}
			this.poolOrderDAO.updateOrderStatus(order.getId(),
					order.getStatus());
		}
		return order.getStatus();
	}

	public PoolOrderBean getInProcessOrderById(long orderId) {
		PoolOrderBean order = this.poolOrderDAO.getInProcessOrderById(orderId);
		logger.info(String.format("order[%s] found refer to id[%s]",
				order.toString(), orderId));
		return order;
	}

	// 1.delete order_joiner map
	// 2.update order status if needed
	// 3.update captain user id if needed
	@Transactional
	public String giveUpOrder(long userId, PoolOrderBean order) {
		if (!this.isOrderStatusOKWhenGiveUpOrder(order.getStatus())) {
			logger.warn("user[" + userId + "] already confirmed this order["
					+ order.getId() + "]");
			return MessageUtil.GIVE_UP_ORDER_CANNOT_GIVE_UP;
		}
		// 1 delete joiner and joiner_order map
		long joinerId = this.findJoinerIdByUserId(order.getPoolJoiners(),
				userId);
		this.poolOrderJoinerMapDAO.deleteInProcessOrderJoinerMap(joinerId,
				order.getId());
		logger.info("order[" + order.getId() + "] joiner[" + joinerId
				+ "] map deleted");
		this.poolJoinerDAO.deleteJoiner(joinerId);
		logger.info("joiner[" + joinerId + "] deleted");
		// 2
		if (order.getStatus().equals(
				PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_JOINED
						.getValue())) {
			order.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_JOINED
					.getValue());
			this.poolOrderDAO.updateOrderStatus(order.getId(),
					order.getStatus());
		} else if (order.getStatus().equals(
				PoolOrderStatusEnum.PASSENGERS_COMPLETE_DRIVER_UNJOINED
						.getValue())) {
			order.setStatus(PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
					.getValue());
			this.poolOrderDAO.updateOrderStatus(order.getId(),
					order.getStatus());
		}
		// 3
		if (userId == order.getCaptainUserId()) {
			long newCaptainUserId = this
					.determineNewCaptainerUserId(this.poolJoinerDAO
							.findJoinersByOrder(order.getId()));
			if (newCaptainUserId > 0) {
				this.poolOrderDAO.updateUserId(newCaptainUserId, 0,
						order.getId());
				logger.info("order[" + order.getId()
						+ "] captainUserId update with value["
						+ newCaptainUserId + "]");
			} else {
				this.poolOrderDAO.updateOrderStatus(order.getId(),
						PoolOrderStatusEnum.POOL_ORDER_FAILED.getValue());
			}
		}
		return MessageUtil.SUCCESS_STRING;
	}

	// can not give up order under some situations
	private boolean isOrderStatusOKWhenGiveUpOrder(String status) {
		if (PoolOrderStatusEnum.JOINERS_CONFIRMED.getValue().equals(status)
				|| PoolOrderStatusEnum.POOL_ORDER_FAILED.getValue().equals(
						status)
				|| PoolOrderStatusEnum.POOL_ORDER_FINISHED.getValue().equals(
						status)) {
			return false;
		}
		return true;
	}

	private long findJoinerIdByUserId(List<PoolJoinerBean> joiners, long userId) {
		for (PoolJoinerBean joiner : joiners) {
			if (joiner.getUserBean().getId() == userId) {
				return joiner.getId();
			}
		}
		return 0;
	}

	private long determineNewCaptainerUserId(List<PoolJoinerBean> joiners) {
		if (!joiners.isEmpty()) {
			return joiners.get(0).getUserBean().getId();
		}
		return 0;
	}

	@Transactional
	public String confirmOrderGo(long userId, long orderId) {
		// set status for currentJoiner
		this.poolJoinerDAO.joinerConfirm(userId, orderId);
		// change order status if needed
		boolean allConfirmed = this.poolJoinerDAO.allConfirmed(orderId);
		if (allConfirmed) {
			this.poolOrderDAO.updateOrderStatus(orderId,
					PoolOrderStatusEnum.JOINERS_CONFIRMED.getValue());
			logger.info("order[" + orderId + "] all confirmed");
		} else {
			// passenger pending...
			// do noting...
		}
		return null;
	}

	public int getSeatCountsByOrderUser(PoolOrderBean order, long userId)
			throws Exception {
		for (PoolJoinerBean joiner : order.getPoolJoiners()) {
			if (joiner.getUserBean().getId() == userId) {
				return joiner.getSeatsCount();
			}
		}
		logger.error("no target joiner found refer to order[" + order.getId()
				+ "] user [" + userId + "]");
		throw new Exception("no target joiner found refer to user");
	}

	// when it`s time to determine, more than one joiner existing means need
	// confirm
	public boolean needConfirm(long orderId) {
		return this.poolJoinerDAO.twoMoreJoiners(orderId);
	}

	// change order status to failed, may also make change to related joiner,
	// route...in the future
	public void failOrder(long orderId) {
		this.poolOrderDAO.updateOrderStatus(orderId,
				PoolOrderStatusEnum.POOL_ORDER_FAILED.getValue());
	}

	public void allConfirm(PoolOrderBean order) {
		logger.info(String.format(
				"making order[%s] joiners status confirmed...",
				order.toString()));
		for (PoolJoinerBean joiner : order.getPoolJoiners()) {
			this.confirmOrderGo(joiner.getUserBean().getId(), order.getId());
		}
	}

	public List<PoolOrderBean> getAllOrdersReferStartTime(Date date) {
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		List<Long> orderIds = new ArrayList<Long>();
		orderIds = this.poolOrderDAO.findTargetDateOrder(date);
		orders = this.poolOrderDAO.getInProcessOrdersByIds(orderIds);
		return orders;
	}

	@SuppressWarnings("unused")
	private double getE_long(long startLatitude) {
		double e_long = Math.acos((Math.cos(E_dist / Earth_R) - Math
				.cos(Math.PI / 2 - startLatitude)
				* Math.cos(Math.PI / 2 - startLatitude))
				/ Math.sin(startLatitude) / Math.sin(startLatitude));
		return e_long * 180 / Math.PI;
	}

	public List<PoolOrderBean> FinishOrdersIfNeed(Date date) {
		List<PoolOrderBean> onTimeOrders = this.poolOrderDAO
				.getOrdersOnStartTime(date);
		for (PoolOrderBean order : onTimeOrders) {
			if (PoolOrderStatusEnum.POOL_ORDER_FAILED.getValue().equals(
					order.getStatus())) {
				// skip orders which has been failed
				continue;
			}
			order.setStatus(PoolOrderStatusEnum.POOL_ORDER_FINISHED.getValue());
			logger.info(String.format("changing order[%s] status to finished!",
					order.getId()));
			this.poolOrderDAO.updateOrderStatus(order.getId(),
					order.getStatus());
		}
		return onTimeOrders;
	}

	public void MoveOnTimeOrdersToMongo(Date date) {
		List<PoolOrderBean> onTimeOrders = this.poolOrderDAO
				.getOrdersOnStartTime(date);
		this.moveOrdersToMongoDB(onTimeOrders);
	}

	public void MoveOutOfTimeOrdersToMongo(Date date) {
		List<PoolOrderBean> outOfTimeOrders = this.poolOrderDAO
				.getOutOfTimeOrders(date);
		this.moveOrdersToMongoDB(outOfTimeOrders);
	}

	public void moveOrdersToMongoDB(List<PoolOrderBean> orders) {
		for (PoolOrderBean order : orders) {
			moveOrderToMongoDB(order);
		}
	}
	
	public String moveOrderToMongoDB(PoolOrderBean order) {
		String objectId = null;
		try {
			if (!PoolOrderStatusEnum.POOL_ORDER_FAILED.getValue().equals(
					order.getStatus())) {
				order.setStatus(PoolOrderStatusEnum.POOL_ORDER_FINISHED
						.getValue());
				logger.info(String.format(
						"changing order[%s] status to finished!",
						order.getId()));
			}
			objectId = this.mongoOrderDAO.saveOrder(order);
		} catch (Exception e) {
			return null;
		}
		try {
			this.deleteFullMysqlOrder(order);
		} catch (Exception e1) {
			// delete order in mongodb
			// ...
		}
		return objectId;
	}

	@Transactional
	public void deleteFullMysqlOrder(PoolOrderBean order) {
		// 1. delete order joiners
		for (PoolJoinerBean joiner : order.getPoolJoiners()) {
			this.poolOrderJoinerMapDAO.deleteInProcessOrderJoinerMap(
					joiner.getId(), order.getId());
			this.poolJoinerDAO.deleteFullJoiner(joiner);
		}
		this.poolOrderDAO.deleteFullOrder(order);
	}

	public List<PoolOrderBean> getAllOrders() {
		List<PoolOrderBean> mysqlOrders = this.poolOrderDAO.getOrders(null, null);
		List<PoolOrderBean> inMemoryOrders = this.realTimePoolService.getOrders(); 
		List<PoolOrderBean> mongodbOrders = this.mongoOrderDAO.getOrders();
		List<PoolOrderBean> allOrders = new ArrayList<PoolOrderBean>(
				mysqlOrders.size() + mongodbOrders.size() + inMemoryOrders.size());
		allOrders.addAll(inMemoryOrders);
		allOrders.addAll(mysqlOrders);
		allOrders.addAll(mongodbOrders);
		return allOrders;
	}

	public PoolRealTimeOrderBean matchRealTimePoolOrder(
			RealTimePoolRequest request) {
		PoolRealTimeOrderBean bestMatchedOrder = realTimePoolService
				.getBestMatchedOrder(request);
		if (bestMatchedOrder == null) {
			PoolRealTimeOrderBean newOrder = realTimePoolService
					.createOrderToPool(request, informationPrepareService);
			return newOrder;
		} else {
			PoolRealTimeOrderBean matchedOrder = realTimePoolService
					.addUserToOrder(request, bestMatchedOrder,
							informationPrepareService);
			return matchedOrder;
		}
	}

	public PoolOrderBean getOrderByRequest(GetOrderRequest request) {
		if (PoolOrderTypeEnum.REAL_TIME.getValue().equals(request.getType())) {
			// get real time order
			if (request.getOrderId() > 0) {
				// get from memory
				return realTimePoolService.getOrderById(request.getOrderId());
			} else {
				// get from mongodb
				return this.mongoOrderDAO.getOrderByObjectId(request.getObjectId());
			}
		}
		if (PoolOrderTypeEnum.RESERVE.getValue().equals(request.getType())) {
			// get mysql order
			if (request.getOrderId() > 0) {
				// get from mysql
				return this.poolOrderDAO.getInProcessOrderById(request.getOrderId());
			} else {
				// get from mongodb
				return this.mongoOrderDAO.getOrderByObjectId(request.getObjectId());
			}
		}
		return null;
	}

	/**
	 * this is to make order as joiners_confirmed and it means will not add any
	 * user any more
	 */
	public String confirmRealTimeOrderGo(long userId, long realTimeOrderId) {
		PoolRealTimeOrderBean order = realTimePoolService.getOrderById(realTimeOrderId);
		if (order.getCaptainUserId() != userId) {
			return "just captain user can make real time order as joiners_confirmed";
		}
		order.setStatus(PoolOrderStatusEnum.JOINERS_CONFIRMED.getValue());
		return "ok";
	}

	/**
	 * this is to remove user from real time order
	 */
	public String giveUpRealTimeOrder(long userId, long realTimeOrderId) {
		return realTimePoolService.removeUserFromRealTimeOrder(userId,
				realTimeOrderId, informationPrepareService);
	}
	
	public void deleteUserOrders(long userId) {
		this.realTimePoolService.deleteUserOrders(userId);
		List<PoolOrderBean> mysqlOrders = this.getMysqlUserOrders(userId);
		for (PoolOrderBean mysqlOrder : mysqlOrders) {
			this.deleteFullMysqlOrder(mysqlOrder);
		} 
		List<PoolOrderBean> mongoOrders = this.mongoOrderDAO.getUserOrders(userId);
		for (PoolOrderBean mongoOrder : mongoOrders) {
			this.mongoOrderDAO.deleteOrder(mongoOrder.getObjectId());
		}
	}
}
