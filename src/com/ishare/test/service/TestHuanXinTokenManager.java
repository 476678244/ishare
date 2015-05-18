package com.ishare.test.service;

import com.ishare.integration.huanxin.HuanXinTokenManager;

public class TestHuanXinTokenManager {

	public static void main(String[] args){
		while(true){
			System.out.println(HuanXinTokenManager.getToken());
		}
	}
}
