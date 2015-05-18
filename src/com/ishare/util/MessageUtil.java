package com.ishare.util;

public class MessageUtil {

	// success
	public static final int SUCCESS = 1;
	public static final String SUCCESS_STRING = "1";

	// fail
	public static final int FAIL = -3;
	public static final String FAIL_STRING = "-3";

	// token
	public static final int TOKEN_WRONG = -1;
	public static final int TOKEN_EXPIRE = -2;
	public static final int TOKEN_OK = 1;
	public static final String TOKEN_WRONG_STRING = "-1";
	public static final String TOKEN_EXPIRE_STRING = "-2";

	// register
	public static final int REGISTER_USER_NAME_EXISTS = -11;
	public static final int REGISTER_AUTH_NUMBER_WRONG = -12;

	// sendAuthNumber
	public static final int SEND_AUTH_NUMBER_EXCEED_MAX_ALLOWED_TODAY = -11;
	public static final String SEND_AUTH_NUMBER_EXCEED_MAX_ALLOWED_TODAY_STRING = "-11";
	public static final String SEND_AUTH_NUMBER_PHONE_NUMBER_ALREADY_REGISTERED = "-12";

	// login
	public static final int LOGIN_USERNAME_PASSWORD_WRONG = -11;

	// createOrder
	public static final int CREATE_ORDER_USER_JOINER_NOT_MATCH = -11;
	public static final int CREATE_ORDER_NOT_VALID_START_DATE = -12;

	// give up order
	public static final String GIVE_UP_ORDER_CANNOT_GIVE_UP = "order can not be given up!";

	// join order
	public static final String JOIN_ORDER_USER_ALREADY_JOINED_TO_ORDER = "this user has already joined to the order";
	public static final String JOIN_ORDER_GRAB_FAIL = "fail to grab joinning in order";
	public static final String JOIN_ORDER_SOONER_THAN_30_MINUTES = "join time sooner than 30 minutes";

	// generate uptoken
	public static final String ERROR_GENERATE_UPTOKEN = "error generate uptoken";
}
