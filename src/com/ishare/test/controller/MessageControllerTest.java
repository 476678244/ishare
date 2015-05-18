package com.ishare.test.controller;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.MessageBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.MessageTypeEnum;
import com.ishare.controller.MessageController;
import com.ishare.service.InformationPrepareService;
import com.ishare.service.MessageService;

public class MessageControllerTest {

	ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
	MessageController controller = ctx.getBean(MessageController.class);
	MessageService service = ctx.getBean(MessageService.class);
	InformationPrepareService infoService = ctx
			.getBean(InformationPrepareService.class);

	public void insertMessage() {
		MessageBean message = new MessageBean();
		message.setType(MessageTypeEnum.ORDER_CONFIRM_NOTIFICATION.getValue());
		message.setContent("content1");
		message.setFromUser("system");
		message.setToUser("zonghan");
		message.setRelatedOrder(1);
		service.addMessage(message);
	}

	public List<MessageBean> getMessages() {
		UserBean user = infoService.getUserByUsername("zonghan");
		return controller.getUserMessages(user.getUsername(), user.getId(),
				"token1");
	}
}
