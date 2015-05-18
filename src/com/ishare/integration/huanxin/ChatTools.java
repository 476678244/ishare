package com.ishare.integration.huanxin;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ishare.bean.UserBean;
import com.ishare.bean.param.RealTimePoolRequest;

public class ChatTools {
	// HuanXin key
	private static String appkey = "ishare#ishare";

	public static String createChatGroupForRealTime(Date time,
			UserBean captainUser, RealTimePoolRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		List<String> memList = new ArrayList<String>();
		memList.add(captainUser.getUsername());
		String groupid = null;
		try {
			// the caption user's portrait will be the portrait of group
			String token = HuanXinTokenManager.getToken();
			String pic = captainUser.getGender() + "," + captainUser.getJob();
			String groupName = sdf.format(time) + "@"
					+ request.getStartAddress() + "@" + pic;
			groupid = EasemobGroupMessage.creatChatGroups(appkey, token,
					groupName, "讨论集合地点", false, "Vshare", memList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupid;
	}

	public static boolean addUsertoChatGroup(String groupId, String userName) {
		String token = HuanXinTokenManager.getToken();
		EasemobGroupMessage.addUserToGroup(appkey, token, groupId, userName);
		return true;
	}

	public static boolean deleteChatGroup(String groupId) {
		try {
			String token = HuanXinTokenManager.getToken();
			EasemobGroupMessage.deleteChatGroups(appkey, token, groupId);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean deleteUserFromChatGroup(String groupId,
			String userName) {
		String token = HuanXinTokenManager.getToken();
		EasemobGroupMessage.deleteUserFromGroup(appkey, token, groupId,
				userName);
		return true;
	}
	
	public static void sendAddNotify(String groupId, String userNick){
		String text = userNick+"，加入了拼车，创建者可点击左上角“出发”按钮决定出发，或继续等待其他用户加入，订单继续保留10分钟";
		new SendGroupMessage(groupId, text, HuanXinTokenManager.getToken());
	}
	
	public static void sendNewNotify(String groupId, String userNick){
		String text = userNick+"，您是第一个拼车的用户，请稍待其他用户加入，订单为您保留10分钟";
		new SendGroupMessage(groupId, text, HuanXinTokenManager.getToken());
	}
}
