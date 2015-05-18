package com.ishare.controller.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.UserBean;
import com.ishare.controller.ControllerUtil;
import com.ishare.service.AdminService;
import com.ishare.service.InformationPrepareService;
import com.ishare.service.IntegrationService;
import com.ishare.service.LoginService;
import com.ishare.service.MessageService;
import com.ishare.service.PoolService;
import com.ishare.util.MessageUtil;

@Controller
@RequestMapping("/request/admin")
public class AdminController {

	public final static Logger logger = LoggerFactory
			.getLogger(AdminController.class);

	@Autowired
	InformationPrepareService informationPrepareService;

	@Autowired
	AdminService adminService;

	@Autowired
	PoolService poolService;
	
	@Autowired
	IntegrationService integrationService;
	
	@Autowired
	LoginService loginService;
	
	@Autowired
	MessageService messageService;

	@RequestMapping(value = "/login")
	public String login(@RequestParam("email") String username,
			@RequestParam("password") String password, HttpSession session) {
		if (this.adminService.authenticateAdmin(username, password)) {
			String token = session.getId();
			session.setAttribute("token", token);
			return "redirect:/request/admin/index.html";
		}
		return MessageUtil.FAIL_STRING;
	}

	private boolean authenticateToken(HttpSession session) {
		String token = (String) session.getAttribute("token");
		String sessionId = session.getId();
		if (sessionId.equals(token)) {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/getUsers")
	@ResponseBody
	public List<UserBean> getUsers(HttpSession session) {
		if (!this.authenticateToken(session)) {
			return null;
		}
		return this.adminService.getAllUsers();
	}

	@RequestMapping(value = "/getUser")
	@ResponseBody
	public UserBean getUser(HttpSession session,
			@RequestParam("user_id") long userId) {
		if (!this.authenticateToken(session)) {
			return null;
		}
		return this.informationPrepareService.getUserByUserId(userId);
	}

	@RequestMapping(value = "/getMyOrders")
	@ResponseBody
	public List<PoolOrderBean> getMyOrders(HttpSession session,
			@RequestParam("user_id") long userId) {
		if (!this.authenticateToken(session)) {
			return null;
		}
		List<PoolOrderBean> myOrders = new ArrayList<PoolOrderBean>();
		myOrders = this.poolService.getUserAllOrders(userId);
		ControllerUtil.resolveStartTimeLong(myOrders);
		return myOrders;
	}

	@RequestMapping(value = "/getOrders")
	@ResponseBody
	public List<PoolOrderBean> getOrders(HttpSession session) {
		if (!this.authenticateToken(session)) {
			return null;
		}
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		orders = this.poolService.getAllOrders();
		ControllerUtil.resolveStartTimeLong(orders);
		return orders;
	}
	
	@RequestMapping(value = "/deleteUserOrders")
	@ResponseBody
	public String deleteUserOrders(HttpSession session,
			@RequestParam("user_id") long userId) {
		if (!this.authenticateToken(session)) {
			return MessageUtil.TOKEN_EXPIRE_STRING;
		}
		this.poolService.deleteUserOrders(userId);
		return MessageUtil.SUCCESS_STRING;
	}
	
	@RequestMapping(value = "/deleteUser")
	@ResponseBody
	public String deleteUser(HttpSession session,
			@RequestParam("user_id") long userId) {
		if (!this.authenticateToken(session)) {
			return MessageUtil.TOKEN_EXPIRE_STRING;
		}
		// delete orders at first
		this.deleteUserOrders(session, userId);
		// then delete user
		UserBean user = this.informationPrepareService.getUserByUserId(userId);
		this.integrationService.deleteBaiduPush(userId);
		this.loginService.deleteUserToken(userId);
		this.messageService.deleteUserMessages(user.getUsername());
		this.informationPrepareService.deleteUser(userId);
		return MessageUtil.SUCCESS_STRING;
	}
}
