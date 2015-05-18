package com.ishare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ishare.bean.UserBean;
import com.ishare.dao.impl.UserDAO;

@Service
public class AdminService {

	@Autowired
	UserDAO userDAO;
	
	public boolean authenticateAdmin(String username, String password) {
		if ("a476678244@163.com".equals(username) && "007".equals(password)) {
			return true;
		}
		return false;
	}

	public List<UserBean> getAllUsers() {
		return userDAO.getAllUsers();
	}

}
