package com.ishare.test.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.ishare.integration.baidu.PushService_Notify;


public class TestPushService {

	public static void main(String[] args){
		String nickName = "伊丹";
		Random r = new Random();
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");
		Date takeOff = new Date();
		String startAddress = "唐丰苑";
		String endAddress = "张江高科";
		String userId = ""+r.nextInt();
		String orderId = "118";
		String groupid = "1412840701890573";
		
		
		String BaiduText = "您好，"+nickName+"，"+sdf.format(takeOff)+"您有微笑出行订单，从["+startAddress+"]到["+endAddress+"]，快来聊天室讨论去哪集合吧";
		new PushService_Notify("3543357139556608534","603772183128113986"
				,"微笑出行",BaiduText, userId, orderId, groupid);
	}
}
