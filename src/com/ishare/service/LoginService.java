package com.ishare.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ishare.bean.UserBean;
import com.ishare.dao.impl.TokenDAO;
import com.ishare.dao.impl.UserDAO;
import com.ishare.util.MessageUtil;

@Service
public class LoginService {

	public final static Logger logger = LoggerFactory
			.getLogger(LoginService.class);

	@Autowired
	@Qualifier("tokenDAO")
	TokenDAO tokenDAO;

	@Autowired
	UserDAO userDAO;

	public int authenticateByToken(long userId, String token) {
		int result = this.tokenDAO.authToken(userId, token);
		if (result == MessageUtil.TOKEN_OK) {
			logger.info("user[" + userId + "] token[" + token + "] auth ok!");
		} else {
			logger.info("user[" + userId + "] token[" + token + "] auth fail!");
		}
		return result;
	}

	public UserBean authenticateByTokenFetchUser(long userId, String token) {
		int result = this.tokenDAO.authToken(userId, token);
		UserBean user = new UserBean();
		if (result == MessageUtil.TOKEN_OK) {
			logger.info("user[" + userId + "] token[" + token + "] auth ok!");
			user = this.userDAO.getUserByUserId(userId);
		} else {
			logger.info("user[" + userId + "] token[" + token + "] auth fail!");
			user.setId(result);
		}
		return user;
	}

	public String createToken(String token, long userId) {
		logger.info("token[" + token + "] generated for user(" + userId + ")");
		this.tokenDAO.upsertToken(userId, token);
		logger.info("token[" + token + "] user[" + userId + "] inserted to DB");
		return token;
	}

	public boolean authenticateByPassword(String username, String password) {
		return this.userDAO.authByPassword(username, password);
	}

	public void deleteToken(long userId, String token) {
		this.tokenDAO.deleteUserToken(token, userId);
	}

	public UserBean login(String username, String password, String token) {
		boolean authPass = this.authenticateByPassword(username, password);
		if (authPass) {
			logger.info("login success![" + username + "," + password + "]");
			UserBean user = this.userDAO.getUserByUsername(username);
			this.tokenDAO.upsertToken(user.getId(), token);
			logger.info("token[" + token + "] created for user[" + username
					+ "]");
			user.setToken(token);
			return user;
		}
		logger.info("user" + username + "] password[" + password
				+ "] login failed!");
		UserBean errorUser = new UserBean();
		errorUser.setId(MessageUtil.LOGIN_USERNAME_PASSWORD_WRONG);
		return errorUser;
	}
	
	public String getUserToken(long userId) {
		return this.tokenDAO.getUserToken(userId);
	}
	
	public void deleteUserToken(long userId) {
		this.tokenDAO.deleteUserAllTokens(userId);
	}
}
