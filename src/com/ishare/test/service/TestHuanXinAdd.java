package com.ishare.test.service;

import com.ishare.integration.huanxin.EasemobGroupMessage;
import com.ishare.integration.huanxin.HuanXinTokenManager;

public class TestHuanXinAdd {

	public static void main(String[] args){
		String appkey = "ishare#ishare";
		String adminToken = HuanXinTokenManager.getToken();
		EasemobGroupMessage.addUserToGroup(appkey, adminToken, "141259817504393", "15216708797");
	}
}
