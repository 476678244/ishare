package com.ishare.integration.huanxin;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendGroupMessage {
	/**
	 * 
	 * @param groupId	The chat group ID to send message
	 * @param message	The message to send
	 * @param adminToken	The token get by HuanXinTokenManager
	 */
	public SendGroupMessage(String groupId, String message, String adminToken){
		String appkey = "ishare#ishare";
		String token = adminToken;
		// 发送Text消息
		 String toGroupId = groupId;
		 String fromUser="咔噗";
		 String txtContent=message;
		
		 try {
			sendTextMessage(appkey, token, txtContent, fromUser, toGroupId);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		String appkey = "ishare#ishare";
		String token = "YWMtsS4cthvxEeSZAiVLlqhIxwAAAUfF1MAhZT2v6MHsie8KDJNgOMXeNRQZtjo";
		// 发送Text消息
		 String toGroupId = "140764986167516";
		 String fromUser="咔噗";
		 String txtContent="刚才系统后台的建立群聊没有成功，正在分析，这个测试发送数据";
		
		 try {
			sendTextMessage(appkey, token, txtContent, fromUser, toGroupId);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendTextMessage(String appkey,String admin_token, String textContent,
			String fromUser, String toGroupId) throws JsonProcessingException,
			KeyManagementException, NoSuchAlgorithmException {

		String httpUrl = "https://a1.easemob.com/"
				+ appkey.replaceFirst("#", "/") + "/messages";

		List<String> group = new ArrayList<String>();
		group.add(toGroupId);
		Map<String, Object> paramsBody = new HashMap<String, Object>();
		paramsBody.put("target_type", "chatgroups");
		paramsBody.put("target", group);
		Map<String, String> msgBody = new HashMap<String, String>();
		msgBody.put("type", "txt");
		msgBody.put("msg", textContent);
		paramsBody.put("msg", msgBody);
		paramsBody.put("from", fromUser);
		Map<String, String> extBody = new HashMap<String, String>();
		extBody.put("attr1", "v1");
		extBody.put("attr2", "v2");
		paramsBody.put("ext", extBody);
		String jsonBody = new ObjectMapper().writeValueAsString(paramsBody);
		System.out.println("jsonBody:" + jsonBody);
		String result = HttpsUtils.sendSSLRequest(httpUrl, admin_token, jsonBody,
				HttpsUtils.Method_POST);
		System.out.println("group receiver message :" + result);
	}
	
}
