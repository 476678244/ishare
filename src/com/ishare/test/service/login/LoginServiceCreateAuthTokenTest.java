package com.ishare.test.service.login;

import com.ishare.test.Main;
import com.ishare.test.service.informationPrepare.InformationPrepareServiceTest;

public class LoginServiceCreateAuthTokenTest {

	public static void main(String[] args) {
		Main.deleteData();
		InformationPrepareServiceTest.createUsers();
		LoginServiceTest test = new LoginServiceTest();
		System.out.println(test.authToken());
		test.createToken();
		System.out.println(test.authToken());
	}
}
