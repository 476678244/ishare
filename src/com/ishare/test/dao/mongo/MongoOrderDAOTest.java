package com.ishare.test.dao.mongo;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolSubjectBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.UserBean;
import com.ishare.controller.MessageController;
import com.ishare.dao.mongo.MongoOrderDAO;
import com.ishare.service.InformationPrepareService;

public class MongoOrderDAOTest {

	ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
	InformationPrepareService info = ctx.getBean(InformationPrepareService.class);
	MessageController controller = ctx.getBean(MessageController.class);
	MongoOrderDAO dao = ctx.getBean(MongoOrderDAO.class);
	
	
	public void saveOrder() {
		PoolOrderBean order = new PoolOrderBean();
		order.setId(2);
		order.setCaptainUserId(3);
		SitePointBean startSite = new SitePointBean();
		startSite.setAddress("startSite");
		SitePointBean endSite = new SitePointBean();
		endSite.setAddress("endSite");
		order.setStartSitePoint(startSite);
		order.setEndSitePoint(endSite);
		PoolJoinerBean joiner = new PoolJoinerBean();
		UserBean zonghan = info.getUserByUsername("zonghan");
		joiner.setUserBean(zonghan);
		joiner.setSeatsCount(2);
		order.getPoolJoiners().add(joiner);
		PoolJoinerBean joiner2 = new PoolJoinerBean();
		UserBean yaming = info.getUserByUsername("yaming");
		joiner2.setUserBean(yaming);
		joiner2.setSeatsCount(3);
		order.getPoolJoiners().add(joiner2);
		PoolSubjectBean subject = new PoolSubjectBean();
		order.setPoolSubject(subject);
		order.setStartTime(new Date());
		dao.saveOrder(order);
		System.out.println("ok!");
	}

	public PoolOrderBean getOrderByObjectId() {
		return dao.getOrderByObjectId("5422dc58e4b0ccee355a999f");
	}
	
	public List<PoolOrderBean> getUserOrders() {
		return dao.getUserOrders(3);
	}
	
	public static void deleteAllOrders() {
		new MongoOrderDAOTest().dao.deleteAllOrders();
	}
}
