package com.ishare.dao.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolSubjectBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.enums.PoolOrderTypeEnum;
import com.ishare.dao.MongoClientSingleton;
import com.ishare.dao.impl.UserDAO;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Repository
public class MongoOrderDAO {

	@Autowired
	UserDAO userDAO;

	private static final String COLLECTION_ORDER = "order";

	private static final String MONGO_DATABASE_NAME = "ishare";

	private static final int GET_ALL_USER_ORDERS = -1;

	private DBCollection getOrderCollection() throws UnknownHostException {
		MongoClient mongoClient = MongoClientSingleton.getMongoClient();
		DB db = mongoClient.getDB(MONGO_DATABASE_NAME);
		DBCollection coll = db.getCollection(COLLECTION_ORDER);
		return coll;
	}

	public String saveOrder(PoolOrderBean order) {
		DBCollection coll;
		try {
			coll = getOrderCollection();
		} catch (UnknownHostException e) {
			return "";
		}
		BasicDBObject poolSubject = new BasicDBObject("gender", order
				.getPoolSubject().getGender());
		BasicDBObject startSitePoint = new BasicDBObject("longtitude", order
				.getStartSitePoint().getLongtitude()).append("latitude",
				order.getStartSitePoint().getLaitude()).append("address",
				order.getStartSitePoint().getAddress());
		BasicDBObject endSitePoint = new BasicDBObject("longtitude", order
				.getEndSitePoint().getLongtitude()).append("latitude",
				order.getEndSitePoint().getLaitude()).append("address",
				order.getEndSitePoint().getAddress());
		BasicDBList joinersDoc = new BasicDBList();
		List<PoolJoinerBean> joiners = order.getPoolJoiners();
		for (PoolJoinerBean joiner : joiners) {
			BasicDBObject joinerDoc = new BasicDBObject("seatsCount",
					joiner.getSeatsCount())
					.append("status", joiner.getStatus()).append("userId",
							joiner.getUserBean().getId());
			joinersDoc.add(joinerDoc);
		}
		BasicDBObject orderDoc = new BasicDBObject("orderId", order.getId())
				.append("startTime", order.getStartTime())
				.append("totalSeats", order.getTotalSeats())
				.append("captainUserId", order.getCaptainUserId())
				.append("type", order.getPoolOrderType())
				.append("status", order.getStatus())
				.append("note", order.getNote())
				.append("poolSubject", poolSubject)
				.append("startSitePoint", startSitePoint)
				.append("endSitePoint", endSitePoint)
				.append("poolJoiners", joinersDoc);
		coll.insert(orderDoc);
		return String.valueOf(orderDoc.get("_id"));
	}

	public PoolOrderBean getOrderByObjectId(String objectIdString) {
		DBCollection coll;
		try {
			coll = getOrderCollection();
		} catch (UnknownHostException e) {
			return null;
		}
		BasicDBObject query = new BasicDBObject("_id", new ObjectId(
				objectIdString));
		DBCursor cursor = coll.find(query);
		try {
			if (cursor.hasNext()) {
				DBObject dbObject = cursor.next();
				PoolOrderBean order = this.getOrderByDBObject(dbObject);
				return order;
			}
			return null;
		} finally {
			cursor.close();
		}
	}

	private PoolOrderBean getOrderByDBObject(DBObject dbObject) {
		return this.getOrderByDBObject(dbObject, true);
	}

	private PoolOrderBean getOrderByDBObject(DBObject dbObject, boolean fetchJoiners) {
		PoolOrderBean order = null;
		try {
			order = new PoolOrderBean();
			order.setObjectId(String.valueOf(dbObject.get("_id")));
			System.out.println(order.getObjectId());
			order.setId(Long.valueOf(String.valueOf(dbObject.get("orderId"))));
			order.setCaptainUserId(Long.valueOf(String.valueOf(dbObject
					.get("captainUserId"))));
			Object startTime = dbObject.get("startTime");
			order.setStartTime((java.util.Date) startTime);
			order.setTotalSeats(Integer.valueOf(String.valueOf(dbObject
					.get("totalSeats"))));
			String type = String.valueOf(dbObject.get("type"));
			if (StringUtils.isBlank(type)) {
				type = PoolOrderTypeEnum.RESERVE.getValue();
			}
			order.setPoolOrderType(type);
			order.setStatus(String.valueOf(dbObject.get("status")));
			order.setNote(String.valueOf(dbObject.get("note")));
			DBObject subject = (DBObject) dbObject.get("poolSubject");
			order.setPoolSubject(getSubjectByDBObject(subject));
			DBObject startSitePoint = (DBObject) dbObject.get("startSitePoint");
			order.setStartSitePoint(this
					.getSitePointBeanByDBObject(startSitePoint));
			DBObject endSitePoint = (DBObject) dbObject.get("endSitePoint");
			order.setEndSitePoint(this.getSitePointBeanByDBObject(endSitePoint));
			if (fetchJoiners) {
				DBObject joiners = (DBObject) dbObject.get("poolJoiners");
				order.setPoolJoiners(this.getPoolJoinersByDBObject(joiners));
			}
			return order;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return order;
		}
	}

	public List<PoolOrderBean> getUserOrders(long userId, int number, boolean fullOrder) {
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		DBCollection coll = null;
		try {
			coll = getOrderCollection();
		} catch (UnknownHostException e) {
			return orders;
		}
		BasicDBObject query = new BasicDBObject("poolJoiners.userId", userId);
		DBCursor cursor = coll.find(query).sort(
				new BasicDBObject("startTime", -1));
		try {
			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();
				boolean fetchJoiners = fullOrder;
				PoolOrderBean order = this.getOrderByDBObject(dbObject, fetchJoiners);
				orders.add(order);
				if (orders.size() >= number && GET_ALL_USER_ORDERS != number) {
					return orders;
				}
			}
			return orders;
		} finally {
			cursor.close();
		}
	}

	public List<PoolOrderBean> getUserOrders(long userId) {
		return this.getUserOrders(userId, GET_ALL_USER_ORDERS, true);
	}
	
	public List<PoolOrderBean> getUserOrders(long userId, boolean fullOrder) {
		return this.getUserOrders(userId, GET_ALL_USER_ORDERS, fullOrder);
	}

	private PoolSubjectBean getSubjectByDBObject(DBObject dbObject) {
		PoolSubjectBean subject = new PoolSubjectBean();
		subject.setGender(String.valueOf(dbObject.get("gender")));
		return subject;
	}

	private SitePointBean getSitePointBeanByDBObject(DBObject dbObject) {
		SitePointBean site = new SitePointBean();
		site.setLongtitude(Long.valueOf(String.valueOf(dbObject
				.get("longtitude"))));
		site.setLaitude(Long.valueOf(String.valueOf(dbObject.get("latitude"))));
		site.setAddress(String.valueOf(dbObject.get("address")));
		return site;
	}

	private List<PoolJoinerBean> getPoolJoinersByDBObject(DBObject obj) {
		List<PoolJoinerBean> joiners = new ArrayList<PoolJoinerBean>();
		Set<String> keys = obj.keySet();
		for (String key : keys) {
			DBObject joinerObj = (DBObject) obj.get(key);
			PoolJoinerBean joiner = this.getPoolJoinerByDBObject(joinerObj);
			joiners.add(joiner);
		}
		return joiners;
	}

	private PoolJoinerBean getPoolJoinerByDBObject(DBObject obj) {
		PoolJoinerBean joiner = new PoolJoinerBean();
		joiner.setSeatsCount(Integer.valueOf(String.valueOf(obj
				.get("seatsCount"))));
		long userId = Long.valueOf(String.valueOf(obj.get("userId")));
		joiner.setUserBean(this.userDAO.getUserByUserId(userId));
		return joiner;
	}
	
	public List<PoolOrderBean> getOrders() {
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		DBCollection coll = null;
		try {
			coll = getOrderCollection();
		} catch (UnknownHostException e) {
			return orders;
		}
		BasicDBObject query = new BasicDBObject();
		DBCursor cursor = coll.find(query).sort(
				new BasicDBObject("startTime", -1));
		try {
			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();
				PoolOrderBean order = this.getOrderByDBObject(dbObject);
				orders.add(order);
			}
			return orders;
		} finally {
			cursor.close();
		}
	}
	
	public void deleteAllOrders() {
		DBCollection coll = null;
		try {
			coll = getOrderCollection();
		} catch (UnknownHostException e) {
			return ;
		}
		coll.remove(new BasicDBObject());
	}
	
	public void deleteOrder(String objectId) {
		DBCollection coll = null;
		try {
			coll = getOrderCollection();
		} catch (UnknownHostException e) {
			return ;
		}
		coll.remove(new BasicDBObject("_id", new ObjectId(objectId)));
	}
}
