package com.ishare.util.realtime;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ishare.bean.PoolRealTimeOrderBean;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.dao.mongo.MongoOrderDAO;
import com.ishare.integration.huanxin.ChatTools;
import com.ishare.util.ContextUtil;

public class RealTimeOrderMonitor implements Runnable {

	public final static Logger logger = LoggerFactory
			.getLogger(RealTimeOrderMonitor.class);

	public static final int MORE_TIME_FOR_NEW_JOINER = 10 * 60 * 1000;
	
	private static final int MORE_TIME_LEFT_FOR_CHAT_GROUP_AFTER_CONFIRMED = 10 * 60 * 1000;

	public RealTimeOrderMonitor(PoolRealTimeOrderBean order) {
		this.order = order;
		this.mongoOrderDAO = ContextUtil.getContext().getBean(
				MongoOrderDAO.class);
	}

	private PoolRealTimeOrderBean order;

	private MongoOrderDAO mongoOrderDAO;

	public PoolRealTimeOrderBean getOrder() {
		return order;
	}

	public void setOrder(PoolRealTimeOrderBean order) {
		this.order = order;
	}

	public MongoOrderDAO getMongoOrderDAO() {
		return mongoOrderDAO;
	}

	public void setMongoOrderDAO(MongoOrderDAO mongoOrderDAO) {
		this.mongoOrderDAO = mongoOrderDAO;
	}

	@Override
	public void run() {
		this.monitor();
	}

	private void monitor() {
		long sleepTime = order.getStartTime().getTime() - new Date().getTime();
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		// have a look when sleepTime passed
		if (new Date().after(order.getStartTime())) {
			// this means need to take action on order
			if (order.getPoolJoiners().size() <= 1) { // if not two more joiners
				// fail order
				order.setStatus(PoolOrderStatusEnum.POOL_ORDER_FAILED
						.getValue());
				this.mongoOrderDAO.saveOrder(order);
				PoolRealTimeOrderPool.removeOrder(order.getId());
				ChatTools.deleteChatGroup(order.getChatGroupId());
			} else if (PoolOrderStatusEnum.JOINERS_CONFIRMED.getValue().equals(
					order.getStatus())) {
				order.setStatus(PoolOrderStatusEnum.POOL_ORDER_FINISHED
						.getValue());
				this.mongoOrderDAO.saveOrder(order);
				PoolRealTimeOrderPool.removeOrder(order.getId());
				try {
					Thread.sleep(MORE_TIME_LEFT_FOR_CHAT_GROUP_AFTER_CONFIRMED);
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.info("thread 10 minutes to delete chat group is interrupted");
				}
				ChatTools.deleteChatGroup(order.getChatGroupId());
			} else {
				order.setStatus(PoolOrderStatusEnum.JOINERS_CONFIRMED
						.getValue());
				Date now = new Date();
				Date newStartTime = new Date(now.getTime() + MORE_TIME_FOR_NEW_JOINER);
				// 10 minutes later
				order.setStartTime(newStartTime);
				this.monitor();
			}
		} else {
			// this means startTime is updated(moved later),
			// need to listen sleep and monitor again
			this.monitor();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stopMonitoring() {
		Thread.currentThread().stop();
	}
}
