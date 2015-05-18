package com.ishare.integration.qiniu;

import org.json.JSONException;

import com.ishare.util.PropertyUtil;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;

public class QiniuIntegrationManager {

	public static String accessKey = PropertyUtil
			.readProperty("qiniu_access_key");

	public static String secretKey = PropertyUtil
			.readProperty("qiniu_secret_key");

	public static final String ISHARE_FILES = "ishareheadpictures";

	public static String generateUptoken() throws AuthException, JSONException {
		Config.ACCESS_KEY = accessKey;
		Config.SECRET_KEY = secretKey;
		Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
		// make sure bucket exists
		String bucketName = ISHARE_FILES;
		PutPolicy putPolicy = new PutPolicy(bucketName);
		String uptoken = putPolicy.token(mac);
		return uptoken;
	}

	public static PutRet uploadFile(String localFile, String uptoken, String key) {
		PutExtra extra = new PutExtra();
		PutRet ret = IoApi.putFile(uptoken, key, localFile, extra);
		return ret;
	}
}
