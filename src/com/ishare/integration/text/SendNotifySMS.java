package com.ishare.integration.text;

import java.util.HashMap;
import java.util.Set;

import com.cloopen.rest.sdk.CCPRestSDK;

public class SendNotifySMS {

	/**
	 * @param phonenum the object user's phone number
	 * @param auth the Push Message
	 */
	public SendNotifySMS(String phonenum, String text){
		HashMap<String, Object> result = null;

		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("sandboxapp.cloopen.com", "8883");// 初始化服务器地址和端口
		restAPI.setAccount("8a48b55146e1b04a0146f0de34b103d5", "eec123f0f961473da69f259c14feffea");// 初始化主帐号名称和主帐号令牌
		restAPI.setAppId("aaf98f8946eb7a5c0146f0ec134101a9");// 初始化应用ID
		result = restAPI.sendTemplateSMS(phonenum,"3362" ,new String[]{text});

		System.out.println("SDKTestGetSubAccounts result=" + result);
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			@SuppressWarnings("unchecked")
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
		}
	}
	
	public static void main(String[] args) {
		HashMap<String, Object> result = null;

		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("sandboxapp.cloopen.com", "8883");// 初始化服务器地址和端口
		restAPI.setAccount("8a48b55146e1b04a0146f0de34b103d5", "eec123f0f961473da69f259c14feffea");// 初始化主帐号名称和主帐号令牌
		restAPI.setAppId("aaf98f8946eb7a5c0146f0ec134101a9");// 初始化应用ID
		result = restAPI.sendTemplateSMS("18616703467","3362" ,new String[]{""+Math.random()});

		System.out.println("SDKTestGetSubAccounts result=" + result);
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			@SuppressWarnings("unchecked")
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
		}
	}

}
