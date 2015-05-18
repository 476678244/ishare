package com.ishare.test.service;

import java.util.ArrayList;
import java.util.List;

import com.ishare.integration.huanxin.EasemobGroupMessage;
import com.ishare.integration.huanxin.HuanXinTokenManager;
import com.ishare.integration.huanxin.SendGroupMessage;

public class TestHuanXinSend {
	//HuanXin key and Token
	static String appkey = "ishare#ishare";

	public static void main(String[] args){
		String adminToken = HuanXinTokenManager.getToken();
		
		// Create new group chat
		List<String> memList = new ArrayList<String>();
//		memList.add("2");
		memList.add("13162577232");
//		memList.add("15216708797");
		try {
			 String groupid = EasemobGroupMessage.creatChatGroups(appkey,adminToken, "09月30日03:25@上海交通大学思源门（东川路800号）@male,student","集合吧", false, "Vshare",
			 memList);
		// send the system notification to group chat
//			 String sysnotify = "大家快来讨论去哪集合吧";
//			 new SendGroupMessage("1410856420253564", sysnotify, adminToken);
//			 System.out.println("GoupID: "+groupid);
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
		
	}
}
