package com.ishare.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component("mysqlDAO")
public class MysqlDAO {

	public String hello = "hello world";
	
	public String getHello() {
		
		jdbcTemplate.execute("SELECT * FROM ishare.role");
		SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM ishare.user where username = ?","zonghan");
		while(rs.next()){
			this.hello = rs.getString("password");
		}
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
