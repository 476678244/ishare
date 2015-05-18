package com.ishare.test.controller;

import java.util.List;

import com.ishare.bean.MessageBean;
import com.ishare.test.Main;

public class MessageControllerGetMessagesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main.refreshData();
		MessageControllerTest test = new MessageControllerTest();
		test.insertMessage();
		List<MessageBean> messages = test.getMessages();
		System.out.println(messages);
	}

}
