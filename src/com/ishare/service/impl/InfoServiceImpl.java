package com.ishare.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ishare.bean.UserBean;
import com.ishare.dao.impl.BaiduPushDAO;
import com.ishare.dao.impl.CarDAO;
import com.ishare.dao.impl.UserDAO;
import com.ishare.service.InfoService;

@Service
public class InfoServiceImpl implements InfoService {

	@Autowired
	UserDAO userDAO;

	@Autowired
	@Qualifier("carDAO")
	CarDAO carDAO;

	@Autowired
	BaiduPushDAO baiduPushDAO;

	@Autowired
	PlatformTransactionManager txManager;

	@Override
	public long regiserUser(final UserBean userBean) {
		long userId = 0;

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = txManager.getTransaction(def);
		try {
			userId = this.userDAO.insertUser(userBean);
			if (userId == 1) {
				throw new RecoverableDataAccessException(null);
			}
		} catch (DataAccessException ex) {
			txManager.rollback(status); // 也可以執行status.setRollbackOnly();
			throw ex;
		}
		txManager.commit(status);

		return userId;
	}

	@Transactional
	@Override
	public long regiser(UserBean userBean) {
		long userId = 0;
		userId = this.userDAO.insertUser(userBean);
		userId = this.userDAO.insertUser(userBean);
		return userId;
	}
}
