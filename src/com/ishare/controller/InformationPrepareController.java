package com.ishare.controller;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ishare.bean.CarBean;
import com.ishare.bean.IdentityBean;
import com.ishare.bean.PaymentBean;
import com.ishare.bean.UserBean;
import com.ishare.integration.qiniu.QiniuIntegrationManager;
import com.ishare.service.InformationPrepareService;
import com.ishare.service.LoginService;
import com.ishare.util.MessageUtil;
import com.ishare.util.TransformerUtil;
import com.qiniu.api.auth.AuthException;

@Controller
@RequestMapping("/request/infoPrepare")
public class InformationPrepareController {

	@Autowired
	InformationPrepareService informationPrepareService;

	@Autowired
	LoginService loginService;

	public final static Logger logger = LoggerFactory
			.getLogger(InformationPrepareController.class);

	@RequestMapping(value = "/improveUserInfoWithJson")
	@ResponseBody
	public UserBean improveUserInfo(@RequestParam("jsonUser") String jsonUser,
			@RequestParam("token") String token) {
		UserBean user = TransformerUtil.JsonStringToUserBean(jsonUser);
		int tokenAuth = this.loginService.authenticateByToken(user.getId(), token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			user.setId(tokenAuth);
			return user;
		}
		if (user.getId() > 0) {
			UserBean userInDb = this.informationPrepareService
					.getUserByUserId(user.getId());
			userInDb.setGender(user.getGender());
			userInDb.setJob(user.getJob());
			userInDb.setNickname(user.getNickname());
			this.informationPrepareService.improveUserInfo(userInDb);
			UserBean updatedUser = this.informationPrepareService
					.getUserByUserId(user.getId());
			return updatedUser;
		}
		return user;
	}

	@RequestMapping(value = "/uploadHeadPic")
	@ResponseBody
	public String uploadHeadPic(@RequestParam("url") String picUrl,
			@RequestParam("user_id") long userId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			String.valueOf(tokenAuth);
		}
		this.informationPrepareService.updateUserPic(userId, picUrl);
		return picUrl;
	}

	@RequestMapping(value = "/generateUptoken")
	@ResponseBody
	public String generateUptoken(@RequestParam("user_id") long userId,
			@RequestParam("token") String token) {
		int tokenAuth = this.loginService.authenticateByToken(userId, token);
		if (tokenAuth != MessageUtil.TOKEN_OK) {
			String.valueOf(tokenAuth);
		}
		String uptoken = null;
		try {
			uptoken = QiniuIntegrationManager.generateUptoken();
		} catch (AuthException e) {
			logger.error(e.getMessage());
			uptoken = MessageUtil.ERROR_GENERATE_UPTOKEN;
		} catch (JSONException e) {
			logger.error(e.getMessage());
			uptoken = MessageUtil.ERROR_GENERATE_UPTOKEN;
		}
		return uptoken;
	}

	// not usable now
	@RequestMapping(value = "/uploadIdentityWithPayInfo")
	@ResponseBody
	public String uploadIdentityWithPayInfo(
			@RequestParam("username") String username,
			@RequestParam("identification_num") String identification_num,
			@RequestParam("real_name") String real_name,
			@RequestParam("driver_license_front") String driver_license_front,
			@RequestParam("driver_license_back") String driver_license_back,
			@RequestParam("pay_type") String payment_type,
			@RequestParam("pay_account") String payment_account) {
		IdentityBean identityBean = new IdentityBean();
		identityBean.setIdentification_num(identification_num);
		identityBean.setReal_name(real_name);
		identityBean.setDriver_license_front(driver_license_front);
		identityBean.setDriver_license_back(driver_license_back);
		String identityStatus = informationPrepareService.improveIdentity(
				username, identityBean);
		PaymentBean paymentBean = new PaymentBean();
		paymentBean.setType(payment_type);
		paymentBean.setAccount(payment_account);
		informationPrepareService.improvePaymentInfo(paymentBean, username);
		return identityStatus;
	}

	// not usable now
	@RequestMapping(value = "/uploadCarInfo")
	@ResponseBody
	public String uploadCarInfo(
			@RequestParam("username") String username,
			@RequestParam("paizhao") String paizhao,
			@RequestParam("type") String type,
			@RequestParam("taxi_company") String taxi_company,
			@RequestParam("employee_num") String employee_num,
			@RequestParam("employee_identification_pic") String employee_identification_pic,
			@RequestParam("driving_license_front") String driving_license_front,
			@RequestParam("driving_license_back") String driving_license_back) {
		CarBean carBean = new CarBean();
		carBean.setDriving_license_back(driving_license_back);
		carBean.setDriving_license_front(driving_license_front);
		carBean.setEmployee_identification_pic(employee_identification_pic);
		carBean.setEmployee_num(employee_num);
		carBean.setPaizhao(paizhao);
		carBean.setTaxi_company(taxi_company);
		carBean.setType(type);
		String result = this.informationPrepareService.improveCarInfo(carBean,
				username);
		return result;
	}
}
