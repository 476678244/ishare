package com.ishare.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("poolDAO")
public class PoolDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("routeDAO")
	RouteDAO routeDAO;
	
	@Autowired
	@Qualifier("poolOrderJoinerMapDAO")
	PoolOrderJoinerMapDAO poolOrderJoinerMapDAO;
	
	@Autowired
	PoolJoinerDAO poolJoinerDAO;
	
	@Autowired
	PoolOrderDAO poolOrderDAO;
}
