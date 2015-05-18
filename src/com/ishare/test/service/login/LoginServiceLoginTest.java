package com.ishare.test.service.login;

import com.ishare.bean.UserBean;
import com.ishare.test.Main;
import com.ishare.test.service.informationPrepare.InformationPrepareServiceTest;

public class LoginServiceLoginTest {

	public static void main(String[] args) {
		Main.deleteData();
		InformationPrepareServiceTest.createUsers();
		InformationPrepareServiceTest.createTokens();
		LoginServiceTest test = new LoginServiceTest();
		System.out.println(test.authByPassword());
		UserBean user = test.login();
		System.out.println(user.getToken());
	}

}
