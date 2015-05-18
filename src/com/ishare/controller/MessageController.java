package com.ishare.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ishare.bean.MessageBean;
import com.ishare.service.LoginService;
import com.ishare.service.MessageService;
import com.ishare.util.MessageUtil;
import com.ishare.util.WriteLogFileUtil;

@Controller
@RequestMapping("/request/message")
public class MessageController {

	public final static Logger logger = LoggerFactory
			.getLogger(MessageController.class);

	@Autowired
	MessageService messageService;

	@Autowired
	LoginService loginService;

	@RequestMapping(value = "/getUserMessages")
	@ResponseBody
	public List<MessageBean> getUserMessages(
			@RequestParam("username") String username,
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			return null;
		}
		return this.messageService.getUserMessages(username);
	}

	@RequestMapping(value = "/uploadAndroidErrorMessage")
	@ResponseBody
	public int uploadAndroidErrorMessage(@RequestParam("info") String info,
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token) {
		if (userId != ControllerUtil.UNKNOWN_USER) {
			int tokenAuth = this.loginService.authenticateByToken(userId, token);
			if (tokenAuth != MessageUtil.TOKEN_OK) {
				return tokenAuth;
			}
		}
		WriteLogFileUtil.android_write(info);
		logger.error(String.format("android error[%s]", info));
		return MessageUtil.SUCCESS;
	}

}
