package com.ishare.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ishare.bean.MessageBean;
import com.ishare.dao.impl.MessageDAO;

@Service
public class MessageService {

	@Autowired
	MessageDAO messageDAO;

	public final static Logger logger = LoggerFactory
			.getLogger(MessageService.class);

	public long addMessage(MessageBean message) {
		long id = this.messageDAO.addMessage(message);
		logger.info("message[" + message.toString() + "] with id :" + id
				+ " inserted");
		return id;
	}

	public List<MessageBean> getUserMessages(String toUser) {
		List<MessageBean> userMessages = this.messageDAO
				.getUserMessages(toUser);
		logger.info(String.format("messages[%s] got related to user[%s]",
				userMessages.toString(), toUser));
		return userMessages;
	}
	
	public void deleteUserMessages(String toUser) {
		this.messageDAO.deleteUserMessages(toUser);
	}
}
