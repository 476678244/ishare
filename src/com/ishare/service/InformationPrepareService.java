package com.ishare.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ishare.bean.CarBean;
import com.ishare.bean.IdentityBean;
import com.ishare.bean.PaymentBean;
import com.ishare.bean.UserBean;
import com.ishare.dao.impl.CarDAO;
import com.ishare.dao.impl.UserDAO;
import com.ishare.util.MessageUtil;

@Service
public class InformationPrepareService {

	@Autowired
	UserDAO userDAO;

	@Autowired
	@Qualifier("carDAO")
	CarDAO carDAO;

	public final static Logger logger = LoggerFactory
			.getLogger(InformationPrepareService.class);

	public long regiserUser(UserBean userBean) {
		if (userDAO.getUserByUsername(userBean.getUsername()) != null) {
			return MessageUtil.REGISTER_USER_NAME_EXISTS;
		}
		long userId = this.userDAO.insertUser(userBean);
		return userId;
	}

	public UserBean getUserByUsername(String username) {
		return userDAO.getUserByUsername(username);
	}

	public UserBean getUserByUserId(long userId) {
		return userDAO.getUserByUserId(userId);
	}

	public String improveIdentity(String username, IdentityBean identityBean) {
		UserBean userBean = this.userDAO.getUserByUsername(username);
		String status = null;
		if (userBean.getIdentityBean() == null) {
			status = this.userDAO.insertIdentityWithUserUpdated(identityBean,
					userBean);
		} else {
			identityBean.setId(userBean.getIdentityBean().getId());
			status = this.userDAO.updateIdentity(identityBean);
		}
		return status;
	}

	public String improveUserInfo(UserBean userBean) {
		this.userDAO.updateUser(userBean);
		return null;
	}

	public String improveCarInfo(CarBean carBean, String username) {
		UserBean userBean = this.userDAO.getUserByUsername(username);
		if (userBean.getCarBean() == null) {
			this.carDAO.insertCarWithUserUpdated(carBean, userBean);
		} else {
			this.carDAO.updateCar(carBean, userBean);
		}
		return null;
	}

	public String improvePaymentInfo(PaymentBean paymentBean, String username) {
		UserBean userBean = this.userDAO.getUserByUsername(username);
		if (userBean.getPaymentBean() == null) {
			this.userDAO.insertPaymentWithUserUpdated(paymentBean, userBean);
		} else {
			this.userDAO.updatePayment(paymentBean);
		}
		return null;
	}

	public void updateUserPic(long userId, String picUrl) {
		logger.info(String.format("updating user[%s]`s head pic url to [%s]",
				userId, picUrl));
		this.userDAO.updateHeadPic(picUrl, userId);
	}
	
	public void deleteUser(long userId) {
		this.userDAO.deleteUser(userId);
	}
}
