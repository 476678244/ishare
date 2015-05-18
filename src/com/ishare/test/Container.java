package com.ishare.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ishare.service.InformationPrepareService;
import com.ishare.service.LoginService;
import com.ishare.service.PoolService;


@Component("container")
public class Container {

	@Autowired
	public InformationPrepareService informationPrepareService;
	
	@Autowired
	public PoolService poolService;
	
	@Autowired
	public LoginService loginService;
	
	public int a = 10;
}
