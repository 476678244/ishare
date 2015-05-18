package com.ishare.integration.baidu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;

public class PushService_Notify {

	public final static Logger logger = LoggerFactory
			.getLogger(PushService_Notify.class);

	public PushService_Notify(String channelId, String userId, String title,
			String text, String userid, String orderid, String groupid) {

		//conceal our ids for ChatActivity,
//		{
//		"key1":"value1", 
//		"key2":"value2"
//		}
		String uid = "\"uid\":\""+userid+"\"";
		String oid = "\"oid\":\""+orderid+"\"";
		String gid = "\"gid\":\""+groupid+"\"";
		String ids = "{"+uid+","+oid+","+gid+"}";
		
		/*
		 * @brief 推送单播通知(Android Push SDK拦截并解析) message_type = 1 (默认为0)
		 */

		// 1. 设置developer平台的ApiKey/SecretKey
		String apiKey = "2oZH1ShZKlHN9oF1fbPXvnqK";
		String secretKey = "WDYZS4cpRDxK11uvuhGbBs6GFIGG2Kz5";
		ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);

		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				logger.info(event.getMessage());
			}
		});

		try {

			// 4. 创建请求类对象
			// 手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
										// 4:ios 5:wp
			request.setChannelId(Long.parseLong(channelId)); // test
																// 4509013450223549535L
			request.setUserId(userId); // test "775905651566141699"

			request.setMessageType(1);
//			request.setMessage("{\"title\":\"" + title
//					+ "\",\"description\":\"" + text + "\"}");
			request.setMessage("{\"title\":\"" + title
					+ "\",\"description\":\"" + text + "\""+",\"custom_content\":"+ids+"}");
//			System.out.println("{\"title\":\"" + title
//					+ "\",\"description\":\"" + text + "\""+",\"custom_content\":"+ids+"}");
			// 5. 调用pushMessage接口
			PushUnicastMessageResponse response = channelClient
					.pushUnicastMessage(request);

			// 6. 认证推送成功
			logger.info("push amount : " + response.getSuccessAmount());

		} catch (ChannelClientException e) {
			logger.info(e.getMessage());
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			logger.info(String.format(
					"request_id: %d, error_code: %d, error_message: %s",
					e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
		}

	}

	public static void main(String[] args) {

		/*
		 * @brief 推送单播通知(Android Push SDK拦截并解析) message_type = 1 (默认为0)
		 */

		// 1. 设置developer平台的ApiKey/SecretKey
		String apiKey = "2oZH1ShZKlHN9oF1fbPXvnqK";
		String secretKey = "WDYZS4cpRDxK11uvuhGbBs6GFIGG2Kz5";
		ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);

		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});

		try {

			// 4. 创建请求类对象
			// 手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
										// 4:ios 5:wp
			request.setChannelId(4509013450223549535L);
			request.setUserId("775905651566141699");

			request.setMessageType(1);
			request.setMessage("{\"title\":\"Notify_title_danbo\",\"description\":\"Notify_description_content\"}");

			// 5. 调用pushMessage接口
			PushUnicastMessageResponse response = channelClient
					.pushUnicastMessage(request);

			// 6. 认证推送成功
			System.out.println("push amount : " + response.getSuccessAmount());

		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(String.format(
					"request_id: %d, error_code: %d, error_message: %s",
					e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
		}

	}

}
