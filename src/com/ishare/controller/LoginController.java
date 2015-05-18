package com.ishare.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ishare.bean.UserBean;
import com.ishare.bean.enums.GenderEnum;
import com.ishare.bean.enums.JobEnum;
import com.ishare.bean.enums.RoleEnum;
import com.ishare.service.InformationPrepareService;
import com.ishare.service.IntegrationService;
import com.ishare.service.LoginService;
import com.ishare.util.AuthNumberUtil;
import com.ishare.util.HeadPicUtil;
import com.ishare.util.MessageUtil;

@Controller
@RequestMapping("/request/login")
public class LoginController {

	@Autowired
	InformationPrepareService informationPrepareService;

	@Autowired
	LoginService loginService;

	@Autowired
	IntegrationService integrationService;

	public final static Logger logger = LoggerFactory
			.getLogger(LoginController.class);

	@RequestMapping(value = "/login")
	@ResponseBody
	public UserBean login(@RequestParam("username") String username,
			@RequestParam("password") String password, HttpSession session,
			@RequestParam("baidu_user") String baiduUser,
			@RequestParam("baidu_channel") String baiduChannel) {
		String token = session.getId();
		UserBean user = this.loginService.login(username, password, token);
		if (user != null && user.getId() > 0) {
			// upsert baidu push info
			this.integrationService.upsertBaiduPush(user.getId(), baiduUser,
					baiduChannel);
		}
		return user;
	}

	@RequestMapping(value = "/authToken")
	@ResponseBody
	public UserBean authToken(@RequestParam("token") String token,
			@RequestParam("user_id") long userId) {
		return this.loginService.authenticateByTokenFetchUser(userId, token);
	}

	@RequestMapping(value = "/sendAuthNumber")
	@ResponseBody
	public String sendAuthNumber(@RequestParam("phoneNumber") String phoneNumber) {
		String authNumber = AuthNumberUtil
				.generateAuthNumber(AuthNumberUtil.authNumberLength);
		int validate = AuthNumberUtil.saveAuthNumber(phoneNumber, authNumber);
		if (validate == MessageUtil.SEND_AUTH_NUMBER_EXCEED_MAX_ALLOWED_TODAY) {
			logger.info(String.format(
					"EXCEED_MAX_ALLOWED_TODAY_STRING for phoneNumber[%s]",
					phoneNumber));
			return MessageUtil.SEND_AUTH_NUMBER_EXCEED_MAX_ALLOWED_TODAY_STRING;
		} else {
			UserBean user = this.informationPrepareService
					.getUserByUsername(phoneNumber);
			if (user != null) {
				logger.info(String.format(
						"PHONE_NUMBER_ALREADY_REGISTERED for phone[%s]",
						phoneNumber));
				return MessageUtil.SEND_AUTH_NUMBER_PHONE_NUMBER_ALREADY_REGISTERED;
			}
			AuthNumberUtil.sendAuthNumberToUser(phoneNumber, authNumber);
			return authNumber;
		}
	}

	/**
	 * @param authNum
	 *            yan zheng ma
	 * @return register result
	 */
	@RequestMapping(value = "/register")
	@ResponseBody
	public long register(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("nickname") String nickname,
			@RequestParam("role") String role,
			@RequestParam("authNum") String authNum,
			@RequestParam("job") String job,
			@RequestParam("gender") String gender) {
		// auth authNum...
		if (!AuthNumberUtil.auth(username, authNum, new Date())) {
			return MessageUtil.REGISTER_AUTH_NUMBER_WRONG;
		}
		if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()
				|| job.isEmpty() || gender.isEmpty()) {
			return 0;
		} else if (role.isEmpty()) {
			role = RoleEnum.PASSENGER.getValue();
		}
		// register
		UserBean userBean = new UserBean();
		userBean.setUsername(username);
		userBean.setPassword(password);
		userBean.setNickname(nickname);
		userBean.setRole(RoleEnum.checkValue(role));
		long userId = this.informationPrepareService.regiserUser(userBean);
		if (userId == MessageUtil.REGISTER_USER_NAME_EXISTS) {
			// username exists, return directly
			return userId;
		}
		// update head picture
		userBean.setHeadPic(HeadPicUtil.getHeadPicUrl(job, gender));
		// improve user info [job, gender]
		userBean.setGender(GenderEnum.checkValue(gender));
		userBean.setJob(JobEnum.checkValue(job));
		userBean.setId(userId);
		this.informationPrepareService.improveUserInfo(userBean);
		return userId;
	}
}
