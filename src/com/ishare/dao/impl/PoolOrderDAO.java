package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.PoolSubjectBean;
import com.ishare.bean.SitePointBean;
import com.ishare.bean.enums.PoolOrderStatusEnum;
import com.ishare.bean.enums.PoolOrderTypeEnum;
import com.ishare.util.MemoryUtil;
import com.ishare.util.SearchOrderUtil;
import com.mysql.jdbc.Statement;

@Repository
public class PoolOrderDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	PoolSubjectDAO poolSubjectDAO;

	@Autowired
	PoolJoinerDAO poolJoinerDAO;

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	OrderChatGroupMapDAO orderChatGroupMapDAO;

	public final static Logger logger = LoggerFactory
			.getLogger(PoolOrderDAO.class);

	public static final long deviation = MemoryUtil.deviation;

	/**
	 * 1. add subject 2. add order
	 * 
	 * @param captainUserId
	 *            the user who created this order is the captain user
	 * @return created order id
	 */
	public long addOrder(PoolOrderBean order, long captainUserId) {
		PoolSubjectBean subject = order.getPoolSubject();
		long subjectId = this.poolSubjectDAO.insertPoolSubject(subject);
		logger.info("pool subject[" + subjectId + "] created");
		subject.setId(subjectId);
		long createdOrderId = this.insertOrder(order, captainUserId);
		return createdOrderId;
	}

	private long insertOrder(final PoolOrderBean order, final long captainUserId) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into pool_in_process_order (order_type, start_time, total_seats, "
						+ " captain_user_id, status, pool_subject_id, start_longtitude,"
						+ " start_latitude, end_longtitude, end_latitude,"
						+ " start_address, end_address, likeTaxiOnly, note, distance"
						+ ") values (?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, order.getPoolOrderType());
				Timestamp time = new Timestamp(order.getStartTime().getTime());
				// ps.setDate(2, sqlDate);
				ps.setTimestamp(2, time);
				ps.setInt(3, order.getTotalSeats());
				ps.setLong(4, captainUserId);
				ps.setString(5, order.getStatus());
				ps.setLong(6, order.getPoolSubject().getId());
				ps.setLong(7, order.getStartSitePoint().getLongtitude());
				ps.setLong(8, order.getStartSitePoint().getLaitude());
				ps.setLong(9, order.getEndSitePoint().getLongtitude());
				ps.setLong(10, order.getEndSitePoint().getLaitude());
				ps.setString(11, order.getStartSitePoint().getAddress());
				ps.setString(12, order.getEndSitePoint().getAddress());
				ps.setBoolean(13, order.isLikeTaxiOnly());
				ps.setString(14, order.getNote());
				ps.setLong(15, order.getDistance());
				logger.info("sql[" + sql + "] running with bind values["
						+ order.toString() + "]");
				return ps;
			}
		}, keyHolder);
		long orderId = keyHolder.getKey().longValue();
		return orderId;
	}

	public PoolOrderBean getInProcessOrderById(long orderId) {
		List<Long> orderIds = new ArrayList<Long>();
		orderIds.add(orderId);
		// single search follow multiSearch process
		List<PoolOrderBean> orders = this.getInProcessOrdersByIds(orderIds);
		if (orders.isEmpty()) {
			return null;
		} else {
			return orders.get(0);
		}
	}

	public List<PoolOrderBean> getInProcessOrdersByIds(List<Long> orderIds) {
		return getInProcessOrdersByIds(orderIds, true);
	}

	public List<PoolOrderBean> getInProcessOrdersByIds(List<Long> orderIds, boolean fullOrder) {
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		if (orderIds.isEmpty()) {
			logger.warn("orderIds list is empty!");
			return orders;
		}
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				jdbcTemplate);
		String sql = "select * from pool_in_process_order where id in (:ids)"
				+ "order by start_time DESC";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids", orderIds);
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				orderIds.toString()));
		SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql,
				parameters);
		boolean fetchJoiners = fullOrder ? true : false;
		orders = this.getOrdersBySqlRowSet(rs, fetchJoiners);
		return orders;
	}

	public void updateOrderStatus(final long orderId, final String newStatus) {
		final String sql = "update pool_in_process_order set status = ? where id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, newStatus);
				ps.setLong(2, orderId);
				logger.info("sql[" + sql + "] running with bind values["
						+ newStatus + "," + orderId + "]");
			}
		});
		logger.info(String.format("order[%s] status updated with value[%s]",
				orderId, newStatus));
	}

	// can just update either captainUserId or driverUserId
	public void updateUserId(final long captainUserId, final long driverUserId,
			final long orderId) {
		String sql = "update pool_in_process_order set captain_user_id = ? where id = ?";
		if (captainUserId == 0) {
			sql = "update pool_in_process_order set driver_user_id = ? where id = ?";
			logger.info("sql[" + sql + "] running with bind values["
					+ driverUserId + "," + orderId + "]");
		} else {
			logger.info("sql[" + sql + "] running with bind values["
					+ captainUserId + "," + orderId + "]");
		}
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				if (captainUserId == 0) {
					ps.setLong(1, driverUserId);
				} else {
					ps.setLong(1, captainUserId);
				}
				ps.setLong(2, orderId);
			}
		});
	}

	public List<Long> findTargetDateOrder(Date date) {
		List<Long> orderIds = new ArrayList<Long>();
		String sql = "select id from pool_in_process_order where start_time = ?";
		Timestamp time = new Timestamp(date.getTime());
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				time));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, time);
		while (rs.next()) {
			orderIds.add(rs.getLong(1));
		}
		return orderIds;
	}

	public List<PoolOrderBean> getMathchedOrdersOrderByStartTimeAsc(
			long startLongtitude, long startLatitude, long endLongtitude,
			long endLatitude, Date startTime, int timePeriod, long userId,
			int joinerSeats, int genderCare) {
		String orderBySql = " order by abs(UNIX_TIMESTAMP(start_time) - UNIX_TIMESTAMP(?))";
		return this.getMathchedOrders(startLongtitude, startLatitude,
				endLongtitude, endLatitude, startTime, timePeriod, userId,
				orderBySql, startTime, joinerSeats, genderCare);
	}

	private List<PoolOrderBean> getMathchedOrders(long startLongtitude,
			long startLatitude, long endLongtitude, long endLatitude,
			Date startTime, int timePeriod, long userId, String orderBySql,
			Object orderByParam, int joinerSeats, int genderCare) {
		List<PoolOrderBean> matchedOrders = new ArrayList<PoolOrderBean>();
		String genderClause = "";
		if (genderCare == SearchOrderUtil.GENDER_CARE_ONLY_FEMALE) {
			genderClause = SearchOrderUtil.GENDER_CLAUSE_ONLY_FEMALE;
		} else if (genderCare == SearchOrderUtil.GENDER_CARE_NONE_ONLY_FEMALE) {
			genderClause = SearchOrderUtil.GENDER_CLAUSE_NONE_ONLY_FEMALE;
		}
		String sql = "select * from ("
				+ " select o.id, o.order_type, o.start_time, o.total_seats, o.diver_user_id, "
				+ " o.captain_user_id, o.status, o.pool_subject_id, o.start_longtitude, "
				+ " o.start_latitude, o.last_middle_longtitude, o.last_middle_latitude, "
				+ " o.end_longtitude, o.end_latitude, o.start_address, o.middle_address, o.end_address, "
				+ " o.likeTaxiOnly, o.note, o.distance , sum(seats_count) as seats"
				+ " from pool_in_process_order o , pool_in_process_order_joiner_map map , "
				+ " pool_joiner joiner , pool_subject subject "
				+ " where o.id = map.pool_in_process_order_id and map.pool_joiner_id = joiner.id "
				+ " and o.pool_subject_id = subject.id "
				+ genderClause
				+ " and o.id not in "
				+ "     (select temp_o.id from pool_in_process_order temp_o, pool_in_process_order_joiner_map temp_map "
				+ "			where temp_map.user_id = ? and temp_o.id = temp_map.pool_in_process_order_id )"
				+ " and start_longtitude >= ? and start_longtitude <= ? "
				+ " and start_latitude >= ? and start_latitude <= ? "
				+ " and end_longtitude >= ? and end_longtitude <= ? "
				+ " and end_latitude >= ? and end_latitude <= ? "
				+ " and start_time >= ? and start_time <= ? "
				+ " and (o.status = ? or o.status = ? ) group by o.id ) as orders "
				+ " where orders.seats <= orders.total_seats - ? " + orderBySql;
		Date lTime, rTime;
		long d1_tmp = startTime.getTime() - timePeriod * 1000 * 60;
		long d2_tmp = startTime.getTime() + timePeriod * 1000 * 60;
		lTime = new Date(d1_tmp);
		rTime = new Date(d2_tmp);
		// valid status
		String status1 = PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_JOINED
				.getValue();
		String status2 = PoolOrderStatusEnum.PASSENGERS_INCOMPLETE_DRIVER_UNJOINED
				.getValue();
		logger.info(String
				.format("sql[%s] running with bind values[%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s]",
						sql, userId, startLongtitude - deviation,
						startLongtitude + deviation, startLatitude - deviation,
						startLatitude + deviation, endLongtitude - deviation,
						endLongtitude + deviation, endLatitude - deviation,
						endLatitude + deviation, lTime, rTime, status1,
						status2, joinerSeats, orderByParam));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, userId,
				startLongtitude - deviation, startLongtitude + deviation,
				startLatitude - deviation, startLatitude + deviation,
				endLongtitude - deviation, endLongtitude + deviation,
				endLatitude - deviation, endLatitude + deviation, lTime, rTime,
				status1, status2, joinerSeats, orderByParam);
		matchedOrders = this.getOrdersBySqlRowSet(rs);
		return matchedOrders;
	}
	
	private List<PoolOrderBean> getOrdersBySqlRowSet(SqlRowSet rs) {
		return getOrdersBySqlRowSet(rs, true);
	}

	private List<PoolOrderBean> getOrdersBySqlRowSet(SqlRowSet rs, boolean fetchJoiners) {
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		while (rs.next()) {
			PoolOrderBean order = new PoolOrderBean();
			order.setId(rs.getLong(1));
			order.setPoolOrderType(rs.getString(2));
			if (StringUtils.isBlank(order.getPoolOrderType())) {
				// for legacy orders, empty type means reserve 
				order.setPoolOrderType(PoolOrderTypeEnum.RESERVE.getValue());
			}
			order.setStartTime(rs.getTimestamp(3));
			order.setTotalSeats(rs.getInt(4));
			order.setDriverUserId(rs.getLong(5));
			order.setCaptainUserId(rs.getLong(6));
			order.setStatus(rs.getString(7));
			long subjectId = rs.getLong(8);
			if (subjectId != 0) {
				PoolSubjectBean subject = this.poolSubjectDAO
						.getPoolSubjectById(subjectId);
				order.setPoolSubject(subject);
			}
			order.setStartSitePoint(new SitePointBean(rs.getLong(9), rs
					.getLong(10), rs.getString(15)));
			order.setLastMiddleSitePoint(new SitePointBean(rs.getLong(11), rs
					.getLong(12), rs.getString(16)));
			order.setEndSitePoint(new SitePointBean(rs.getLong(13), rs
					.getLong(14), rs.getString(17)));
			order.setLikeTaxiOnly(rs.getBoolean(18));
			order.setNote(rs.getString(19));
			order.setDistance(rs.getLong(20));
			if (fetchJoiners) {
				// fetch joiners
				order.setPoolJoiners(this.poolJoinerDAO.findJoinersByOrder(order
						.getId()));
			}
			logger.info(String.format("order[%s] found refer to id[%s]",
					order.toString(), order.getId()));
			orders.add(order);
		}
		return orders;
	}

	public List<PoolOrderBean> getOrders(String whereClause, Object whereParam) {
		List<PoolOrderBean> orders = new ArrayList<PoolOrderBean>();
		String sql = "select o.id, o.order_type, o.start_time, o.total_seats, o.diver_user_id, "
				+ " o.captain_user_id, o.status, o.pool_subject_id, o.start_longtitude, "
				+ " o.start_latitude, o.last_middle_longtitude, o.last_middle_latitude, "
				+ " o.end_longtitude, o.end_latitude, o.start_address, o.middle_address, o.end_address, "
				+ " o.likeTaxiOnly, o.note, o.distance "
				+ " from pool_in_process_order o ";
		if (!StringUtils.isBlank(whereClause)) {
			sql += " where " + whereClause;
			logger.info(String.format("sql[%s] running with bind values[%s]",
					sql, whereParam));
			SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, whereParam);
			orders = this.getOrdersBySqlRowSet(rs);
			return orders;
		}
		logger.info(String.format("sql[%s] running with bind values:", sql));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);
		orders = this.getOrdersBySqlRowSet(rs);
		return orders;
	}

	public List<PoolOrderBean> getOrdersOnStartTime(Date startTime) {
		String whereClause = " start_time = ? ";
		return this.getOrders(whereClause, startTime);
	}
	
	public List<PoolOrderBean> getOutOfTimeOrders(Date now) {
		String whereClause = " start_time < ? ";
		return this.getOrders(whereClause, now);
	}
	
	public List<PoolOrderBean> getOrdersOnFinishTime(Date finishTime) {
		List<Long> orderIds = this.orderChatGroupMapDAO.getUpToDeleteTimeOrder(finishTime);
		return this.getInProcessOrdersByIds(orderIds);
	}
	
	public void deleteFullOrder(PoolOrderBean order) {
		String sql = "delete from pool_in_process_order where id = ?";
		this.jdbcTemplate.update(sql, order.getId());
		this.poolSubjectDAO.deletePoolSubject(order.getPoolSubject().getId());
	}
}
