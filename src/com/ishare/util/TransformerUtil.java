package com.ishare.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.param.GetOrderRequest;
import com.ishare.bean.param.RealTimePoolRequest;
import com.ishare.controller.PoolController;

public class TransformerUtil {

	public final static Logger logger = LoggerFactory
			.getLogger(PoolController.class);

	public static String UserBeanToJsonString(UserBean userBean) {
		return JSONObject.fromObject(userBean).toString();
	}

	public static UserBean JsonStringToUserBean(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		UserBean userBean = (UserBean) JSONObject.toBean(jsonObject,
				UserBean.class);
		logger.info("user bean[" + userBean.toString()
				+ "] created refer to json[" + jsonString + "]");
		return userBean;
	}

	public static String PoolOrderBeanToJsonString(PoolOrderBean poolOrderBean) {
		poolOrderBean.setStartTimeLong(poolOrderBean.getStartTime().getTime());
		return JSONObject.fromObject(poolOrderBean).toString();
	}

	public static PoolOrderBean JsonStringToPoolOrderBean(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		@SuppressWarnings("rawtypes")
		Map<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("poolJoiners", PoolJoinerBean.class);
		PoolOrderBean poolOrderBean = (PoolOrderBean) JSONObject.toBean(
				jsonObject, PoolOrderBean.class, classMap);
		poolOrderBean.setStartTime(new Date(poolOrderBean.getStartTimeLong()));
		logger.info("order bean[" + poolOrderBean.toString()
				+ "] created refer to json[" + jsonString + "]");
		return poolOrderBean;
	}

	public static String SitePointBeanToJsonString(SitePointBean sitePointBean) {
		return JSONObject.fromObject(sitePointBean).toString();
	}

	public static SitePointBean JsonStringToSitePointBean(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		SitePointBean sitePointBean = (SitePointBean) JSONObject.toBean(
				jsonObject, SitePointBean.class);
		logger.info("sitePoint bean[" + sitePointBean.getAddress()
				+ "] created refer to json[" + jsonString + "]");
		return sitePointBean;
	}

	public static String ObjectToJson(Object obj) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setAllowNonStringKeys(true);
		return JSONObject.fromObject(obj, jsonConfig).toString();
	}

	public static RealTimePoolRequest JsonStringToRealTimePoolRequest(
			String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		RealTimePoolRequest request = (RealTimePoolRequest) JSONObject.toBean(
				jsonObject, RealTimePoolRequest.class);
		logger.info("RealTimePoolRequest[" + request.toString()
				+ "] created refer to json[" + jsonString + "]");
		return request;
	}
	
	public static GetOrderRequest JsonStringToGetOrderRequest(
			String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		GetOrderRequest request = (GetOrderRequest) JSONObject.toBean(
				jsonObject, GetOrderRequest.class);
		logger.info("GetOrderRequest[" + request.toString()
				+ "] created refer to json[" + jsonString + "]");
		return request;
	}
}
