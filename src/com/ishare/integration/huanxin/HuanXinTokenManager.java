package com.ishare.integration.huanxin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HuanXinTokenManager {
	private static String token = null;
	private static Date applydate = null;
	private static final int duration = 7;
	
	public static String getToken(){
		Date now = new Date();
		long nowtime = now.getTime();
		if(HuanXinTokenManager.token == null || HuanXinTokenManager.applydate == null){
			HuanXinTokenManager.token = applyToken();
			HuanXinTokenManager.applydate = now;
		}
		// token out of date, apply again
		else if(HuanXinTokenManager.applydate.getTime()+(long)(HuanXinTokenManager.duration-1)*24*3600*1000 < nowtime){
			HuanXinTokenManager.token = applyToken();
			HuanXinTokenManager.applydate = now;
		}
		return HuanXinTokenManager.token;
	}
	
	// apply token from HuanXin
	private static String applyToken(){
		Map<String, Object> getAccessTokenPostBody = new HashMap<String, Object>();
		getAccessTokenPostBody.put("grant_type", "password");
		getAccessTokenPostBody.put("username", "xiuemp");
		getAccessTokenPostBody.put("password", "xiuemp12");
		String adminToken = getAccessToken("a1.easemob.com", "ishare#ishare", true,
		 getAccessTokenPostBody);
		return adminToken;
	}
	
	private static String getAccessToken(String host, String appKey, Boolean isAdmin,
			Map<String, Object> postBody) {
		String orgName = appKey.substring(0, appKey.lastIndexOf("#"));
		String appName = appKey.substring(appKey.lastIndexOf("#") + 1);
		String accessToken = "";
		String rest = "management/token";
		if (!isAdmin) {
			rest = orgName + "/" + appName + "/token";
		}

		String reqURL = "https://" + host + "/" + rest;
		String result = HttpsUtils.sendSSLRequest(reqURL, null, HttpsUtils.Map2Json(postBody),
				HttpsUtils.Method_POST);
		Map<String, String> resultMap = HttpsUtils.Json2Map(result);

		accessToken = resultMap.get("access_token");

		return accessToken;
	} 
}
