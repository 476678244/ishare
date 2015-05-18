package com.ishare.test.integration;

import com.ishare.integration.qiniu.QiniuIntegrationManager;
import com.qiniu.api.io.PutRet;

public class QiniuIntegrationTest {

	public static void main(String[] args) throws Exception {
		
		String uptoken = QiniuIntegrationManager.generateUptoken();
		
		PutRet result = QiniuIntegrationManager.uploadFile("/Users/zonghan/male_student.jpg", uptoken, "male_student.jpg");

	}

}
