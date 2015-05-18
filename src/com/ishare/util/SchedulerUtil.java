package com.ishare.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.ishare.bean.BaiduPush;
import com.ishare.bean.MessageBean;
import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.MessageTypeEnum;
import com.ishare.integration.baidu.PushService_Notify;
import com.ishare.integration.huanxin.ChatTools;
import com.ishare.integration.huanxin.EasemobGroupMessage;
import com.ishare.integration.huanxin.HuanXinTokenManager;
import com.ishare.integration.text.SendNotifySMS;
import com.ishare.service.IntegrationService;
import com.ishare.service.MessageService;
import com.ishare.service.PoolService;

public class SchedulerUtil extends QuartzJobBean {

	public static ApplicationContext context = ContextUtil.getContext();

	public static PoolService poolService = context.getBean(PoolService.class);

	public static IntegrationService integrationService = context
			.getBean(IntegrationService.class);

	public static MessageService messageService = context
			.getBean(MessageService.class);

	public final static Logger logger = LoggerFactory
			.getLogger(SchedulerUtil.class);

	// HuanXin key
	String appkey = "ishare#ishare";

	public static List<PoolOrderBean> allConfirmIfNeed(Date current) {
		// get the time 30 minutes later
		Date target = computeDate(current);
		// make all the order to joiners confirmed for the order to start in 30
		// minutes
		List<PoolOrderBean> orders = poolService
				.getAllOrdersReferStartTime(target);
		List<PoolOrderBean> ordersToInform = new ArrayList<PoolOrderBean>();
		for (PoolOrderBean order : orders) {
			if (poolService.needConfirm(order.getId())) {
				poolService.allConfirm(order);
				ordersToInform.add(order);
			} else {
				// ... make order failed
				poolService.failOrder(order.getId());
			}
		}
		return ordersToInform;
	}

	public static void allFinishIfNeed(Date currentDate) {
		poolService.FinishOrdersIfNeed(currentDate);
	}

	/**
	 * this is to move all the orders to MongoDb whose startTime is currentDate
	 * including failed orders and orders to be set as finished
	 * 
	 * @param currentDate
	 *            scheduled date
	 */
	public static void moveOnTimeOrdersToMongoIfNeed(Date currentDate) {
		poolService.MoveOnTimeOrdersToMongo(currentDate);
	}

	/**
	 * this is to move out of time orders to MongoDb because in early versions,
	 * there are many orders finished or failed which are still in MYSQL DB, need
	 * to move these orders to MongoDb
	 */
	public static void moveOutOfTimeOrdersToMongoIfNeed(Date currentDate) {
		poolService.MoveOutOfTimeOrdersToMongo(currentDate);
	}

	private static Date computeDate(Date current) {
		Date target = new Date(current.getTime() + 60 * 30 * 1000);
		logger.info(String.format("target Date[%s] refer to Date[%s]", target,
				current));
		return target;
	}

	private int timeout;

	/**
	 * Setter called after the ExampleJob is instantiated with the value from
	 * the JobDetailBean (5)
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("time out setted as :" + this.timeout);
		// get current date
		Date currentDate = context.getScheduledFireTime();
		Date takeOff = computeDate(currentDate);
		// all confirm
		List<PoolOrderBean> orders = SchedulerUtil
				.allConfirmIfNeed(currentDate);
		// inform all users by network
		System.out.println("Test_Scheduler");
		// HuanXin group member list
		List<String> memList = new ArrayList<String>();
		String groupid = null;
		for (PoolOrderBean order : orders) {
			memList.clear();
			// add chat members
			for (PoolJoinerBean joiner : order.getPoolJoiners()) {
				String userName = joiner.getUserBean().getUsername();
				memList.add(userName);
			}
			String startAddress = order.getStartSitePoint().getAddress();
			String endAddress = order.getEndSitePoint().getAddress();
			// create the HuanXin Chat Group, get token first
			String adminToken = HuanXinTokenManager.getToken();
			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");
			try {
				// the caption user's portrait will be the portrait of group
				// chat
				UserBean captain_user = order.getPoolJoiners().get(0)
						.getUserBean();
				String pic = captain_user.getGender() + ","
						+ captain_user.getJob();
				String groupName = sdf.format(takeOff) + "@" + startAddress
						+ "@" + pic;
				groupid = EasemobGroupMessage.creatChatGroups(appkey,
						adminToken, groupName, "讨论集合地点", false, "Vshare",
						memList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (PoolJoinerBean joiner : order.getPoolJoiners()) {
				long userId = joiner.getUserBean().getId();
				String nickName = joiner.getUserBean().getNickname();
				long orderId = order.getId();
				// inform this user by network;
				BaiduPush bp = integrationService.getBaiduPushByUser(userId);
				String BaiduText = String
						.format(PropertyUtil.RESERVE_INFORM_GO_BODY_BAIDU_PUSH,
								nickName, sdf.format(takeOff), startAddress,
								endAddress);
				logger.info(String.format(
						"network pushing to user[%s] with title[%s], text[%s]",
						userId,
						PropertyUtil.RESERVE_INFORM_GO_TITLE_BAIDU_PUSH,
						BaiduText));
				new PushService_Notify(bp.getBaiduChannel(), bp.getBaiduUser(),
						PropertyUtil.RESERVE_INFORM_GO_TITLE_BAIDU_PUSH,
						BaiduText, "" + userId, "" + orderId, groupid);
				// add inform message to message box
				saveMessage(joiner, orderId, BaiduText);
			}
			new Thread(new DelGroupThread(groupid)).start();
		}
		// nightly run...
		NightlyUtil.nightlyDo(currentDate);
		// move failed and to be finished orders to MongoDB
		moveOnTimeOrdersToMongoIfNeed(currentDate);
		// move out of time orders to MongoDb
		moveOutOfTimeOrdersToMongoIfNeed(currentDate);
		// if inform by network failed, then to inform by message
		try {
			logger.info("sleeping 10 minutes...");
			Thread.sleep(10 * 60 * 1000); // sleep for 10mins
			logger.info("wake up!");
			textInform(orders, takeOff);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void saveMessage(PoolJoinerBean joiner, long orderId,
			String BaiduText) {
		MessageBean message = new MessageBean();
		message.setType(MessageTypeEnum.ORDER_CONFIRM_NOTIFICATION.getValue());
		message.setContent(BaiduText);
		message.setToUser(joiner.getUserBean().getUsername());
		message.setRelatedOrder(orderId);
		messageService.addMessage(message);
	}

	private void textInform(List<PoolOrderBean> orders, Date takeOff) {
		for (PoolOrderBean order : orders) {
			String startAddress = order.getStartSitePoint().getAddress();
			String endAddress = order.getEndSitePoint().getAddress();
			for (PoolJoinerBean joiner : order.getPoolJoiners()) {
				long userId = joiner.getUserBean().getId();
				long orderId = order.getId();
				String username = joiner.getUserBean().getUsername();
				String nickName = joiner.getUserBean().getNickname();
				if (!NotificationConfirmUtil.isUserConfirmed(userId, orderId)) {
					logger.info(String.format(
							"text message notifying user[%s], order[%s]",
							userId, orderId));
					SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");
					String text = String.format(
							PropertyUtil.RESERVE_INFORM_GO_BODY_TEXT, nickName,
							sdf.format(takeOff), startAddress, endAddress);
					new SendNotifySMS(username, text);
				}
			}
		}
	}

	class DelGroupThread implements Runnable {

		private String groupId;

		public DelGroupThread(String id) {
			this.groupId = id;
		}

		@Override
		public void run() {
			// delete the group after 1 hour
			try {
				Thread.sleep(1 * 60 * 60 * 1000);
				ChatTools.deleteChatGroup(groupId);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}

		}
	}
}
