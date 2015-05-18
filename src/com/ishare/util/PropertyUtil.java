package com.ishare.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtil {

	public final static Logger logger = LoggerFactory
			.getLogger(PropertyUtil.class);

	public static String readProperty(String key) {
		String value = "";
		Properties prop = new Properties();
		InputStream in = PropertyUtil.class.getClassLoader()
				.getResourceAsStream("cnf.properties");
		try {
			prop.load(in);
			value = prop.getProperty(key).trim();
			value = new String(value.getBytes("ISO8859-1"), "UTF-8");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return value;
	}

	public static final String RESERVE_INFORM_GO_TITLE_BAIDU_PUSH = PropertyUtil
			.readProperty("reserve_inform_go_title_baidu_push");

	public static final String RESERVE_INFORM_GO_BODY_BAIDU_PUSH = PropertyUtil
			.readProperty("reserve_inform_go_body_baidu_push");

	public static final String RESERVE_INFORM_GO_BODY_TEXT = PropertyUtil
			.readProperty("reserve_inform_go_body_text");

	public static final String RESERVE_INFORM_GO_HUANXIN = PropertyUtil
			.readProperty("reserve_inform_go_huanxin");

	public static final String ANDROID_ERROR_LOG_PATH = PropertyUtil
			.readProperty("android_error_log_path");
	
	public static final String DEFAULT_REAL_TIME_ORDER_TOTAL_SEATS = PropertyUtil
			.readProperty("default_real_time_order_total_seats");
}
