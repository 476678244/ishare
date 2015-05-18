package com.ishare.test.util;

import java.util.Date;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.RouteBean;
import com.ishare.bean.SitePointBean;
import com.ishare.util.TransformerUtil;

public class TransformerUtilTest {

	public static void main(String[] args) {
		
		PoolOrderBean order = new PoolOrderBean();
		PoolJoinerBean joiner = new PoolJoinerBean();
		RouteBean route = new RouteBean();
		
		SitePointBean site = new SitePointBean();
		order.getPoolJoiners().add(joiner);
		order.setStartSitePoint(site);
		order.setLastMiddleSitePoint(site);
		order.setEndSitePoint(site);
		
		route.setStartSitePoint(site);
		route.setEndSitePoint(site);
		joiner.setRouteBean(route);
		order.setStartTime(new Date());
		
		//order.setEndSitePoint(site);
		
		
		
		String json = TransformerUtil.PoolOrderBeanToJsonString(order);
		
		System.out.println(json);
		
		PoolOrderBean orderBean = TransformerUtil.JsonStringToPoolOrderBean(json);
		System.out.println(orderBean.getStartTime());
	}

}
