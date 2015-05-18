package com.ishare.test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.ishare.bean.CarBean;
import com.ishare.bean.IdentityBean;
import com.ishare.bean.PaymentBean;
import com.ishare.bean.UserBean;
import com.ishare.controller.HelloController;
import com.ishare.test.dao.mongo.MongoOrderDAOTest;
import com.ishare.test.service.informationPrepare.InformationPrepareServiceTest;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main.deleteData();
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		Container c = ctx.getBean("container", Container.class);
		HelloController hello = ctx.getBean("helloController", HelloController.class); 
		//hello.getHelloValue(new StandardSessionFacade(null));
		UserBean userBean = new UserBean();
		userBean.setNickname("zwu");
		userBean.setUsername("zonghan");
		userBean.setPassword("pwd");
		
		Main.insertUser(userBean, c);
		
		UserBean userInDb = c.informationPrepareService.getUserByUsername("zonghan");
		String username = userInDb.getUsername();
		
		userInDb.setAge(3);
		userInDb.setGender("male");
		userInDb.setJob("job1");
		userInDb.setCharactor("outgoning");
		c.informationPrepareService.improveUserInfo(userInDb);
		
//		Main.insertCar(username, c);
//		Main.insertIdentity(username, c);
//		Main.insertPayment(username, c);

		
		UserBean newUser = c.informationPrepareService.getUserByUsername(username);
		System.out.println(c.a);
	}
	
	public static long insertUser(UserBean userBean, Container c) {
		return c.informationPrepareService.regiserUser(userBean);
	}
	
	public static UserBean getUserById(long id, Container c) {
		return c.informationPrepareService.getUserByUserId(id);
	}
	
	public static void insertIdentity(String username, Container c) {
		IdentityBean identityBean = new IdentityBean();
		identityBean.setIdentification_num("123");
		identityBean.setReal_name("zwu");
		identityBean.setDriver_license_front("url1");
		identityBean.setDriver_license_back("url2");
		c.informationPrepareService.improveIdentity(username, identityBean);
	}

	public static void insertCar(String username, Container c) {
		CarBean carBean = new CarBean();
		carBean.setDriving_license_back("url1");
		carBean.setDriving_license_front("url2");
		carBean.setEmployee_identification_pic("url3");
		carBean.setEmployee_num("12345");
		carBean.setPaizhao("L3122");
		carBean.setTaxi_company("company1");
		carBean.setType("taxi");
		c.informationPrepareService.improveCarInfo(carBean, username);
	}
	
	public static void insertPayment(String username, Container c) {
		PaymentBean paymentBean = new PaymentBean();
		paymentBean.setType("zhifubao");
		paymentBean.setAccount("aa@163.com");
		c.informationPrepareService.improvePaymentInfo(paymentBean, username);
	}
	
	public static void deleteData() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		JdbcTemplate jdbcTemplate = ctx.getBean("jdbcTemplate", JdbcTemplate.class);
		List<String> sqls = new ArrayList<String>(10);
		sqls.add("delete from message");
		sqls.add("delete from pool_history_order_joiner_map");
		sqls.add("delete from pool_history_order");
		sqls.add("delete from pool_in_process_order_joiner_map");
		sqls.add("delete from pool_in_process_order");
		sqls.add("delete from pool_subject");
		sqls.add("delete from pool_joiner");
		sqls.add("delete from user_route_map");
		sqls.add("delete from route");
		sqls.add("delete from user_baidu_push");
		sqls.add("delete from user_token");
		sqls.add("delete from user");
		sqls.add("delete from payment");
		sqls.add("delete from car");
		sqls.add("delete from identity");
		for (String sql : sqls) {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
				}
			});
		}
	}
	
	public static void refreshData() {
		Main.deleteData();
		MongoOrderDAOTest.deleteAllOrders();
		InformationPrepareServiceTest.createUsers();
		InformationPrepareServiceTest.createTokens();
	}
}
