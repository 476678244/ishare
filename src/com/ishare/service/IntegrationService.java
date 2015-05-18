package com.ishare.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ishare.bean.BaiduPush;
import com.ishare.dao.impl.BaiduPushDAO;

@Service
public class IntegrationService {

	@Autowired
	BaiduPushDAO baiduPushDAO;

	public final static Logger logger = LoggerFactory
			.getLogger(IntegrationService.class);

	public void upsertBaiduPush(long userId, String baiduUser,
			String baiduChannel) {
		logger.info(String.format("upserting baidu push for "
				+ "user[%s], baiduUser[%s], baiduChannel[%s]...", userId,
				baiduUser, baiduChannel));
		this.baiduPushDAO.upsertBaiduPush(userId, baiduUser, baiduChannel);

	}

	public BaiduPush getBaiduPushByUser(long userId) {
		return this.baiduPushDAO.getBaiduPushByUser(userId);
	}
	
	public void deleteBaiduPush(long userId) {
		this.baiduPushDAO.deleteBaiduPushByUser(userId);
	}
}
