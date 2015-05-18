package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ishare.bean.PoolJoinerBean;
import com.ishare.bean.enums.PoolJoinerStatusEnum;
import com.mysql.jdbc.Statement;

@Repository
public class PoolJoinerDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("routeDAO")
	RouteDAO routeDAO;

	@Autowired
	UserDAO userDAO;

	public final static Logger logger = LoggerFactory
			.getLogger(PoolJoinerDAO.class);

	public long createJoiner(final PoolJoinerBean joiner) {
		final long routeId = this.routeDAO.insertRoute(joiner.getRouteBean());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into pool_joiner (user_id, seats_count, route_id, "
						+ " status, paid, fee) values (?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, joiner.getUserBean().getId());
				ps.setInt(2, joiner.getSeatsCount());
				ps.setLong(3, routeId);
				ps.setString(4, joiner.getStatus());
				ps.setBoolean(5, false);
				ps.setInt(6, joiner.getFee());
				logger.info("sql[" + sql + "] running with bind values["
						+ joiner.toString() + "]");
				return ps;
			}
		}, keyHolder);
		long joinerId = 0;
		joinerId = keyHolder.getKey().longValue();
		return joinerId;
	}

	public void deleteJoiner(final long joinerId) {
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "delete from pool_joiner where id = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setLong(1, joinerId);
				logger.info("sql[" + sql + "] running with bind values["
						+ joinerId + "]");
				return ps;
			}
		});
	}
	
	public void deleteFullJoiner(final PoolJoinerBean joiner) {
		this.routeDAO.deleteRoute(joiner.getRouteBean().getId());
		this.deleteJoiner(joiner.getId());
	} 

	public void joinerConfirm(long userId, long orderId) {
		this.updateJoinerStatus(userId, orderId,
				PoolJoinerStatusEnum.CONFIRM_GO.getValue());
		logger.info(String
				.format("user[%s] confirm order[%s]", userId, orderId));
	}

	private void updateJoinerStatus(final long userId, final long orderId,
			final String status) {
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "update pool_joiner joiner set joiner.status = ? "
						+ " where joiner.id = (select map.pool_joiner_id "
						+ " from pool_in_process_order_joiner_map map "
						+ " where user_id = ? and pool_in_process_order_id = ?) ";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, status);
				ps.setLong(2, userId);
				ps.setLong(3, orderId);
				logger.info(String.format(
						"sql[%s] running with bind values[%s,%s,%s]", sql,
						status, userId, orderId));
				return ps;
			}
		});
	}

	public boolean allConfirmed(long orderId) {
		boolean allConfirmed = true;
		String sql = "select joiner.status from pool_in_process_order_joiner_map map, "
				+ "pool_joiner joiner where map.pool_in_process_order_id = ? "
				+ "and map.pool_joiner_id = joiner.id";
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				orderId));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, orderId);
		while (rs.next()) {
			if (!rs.getString(1).equals(
					PoolJoinerStatusEnum.CONFIRM_GO.getValue())) {
				allConfirmed = false;
			}
		}
		return allConfirmed;
	}

	// decide whether having two or more joiners for the order
	public boolean twoMoreJoiners(long orderId) {
		String sql = "select pool_joiner_id from pool_in_process_order_joiner_map"
				+ " map where map.pool_in_process_order_id = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				orderId));
		int joinersCount = 0;
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, orderId);
		while (rs.next()) {
			joinersCount++;
		}
		return joinersCount > 1 ? true : false;
	}

	public List<PoolJoinerBean> findJoinersByOrder(long orderId) {
		List<PoolJoinerBean> joiners = new ArrayList<PoolJoinerBean>();
		String sql = "select * from pool_joiner where id in "
				+ "(select pool_joiner_id from pool_in_process_order_joiner_map"
				+ " map where map.pool_in_process_order_id = ?)";
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, orderId);
		while (rs.next()) {
			PoolJoinerBean joiner = new PoolJoinerBean();
			joiner.setId(rs.getLong(1));
			long userId = rs.getLong(2);
			joiner.setUserBean(this.userDAO.getUserByUserId(userId));
			joiner.setSeatsCount(rs.getInt(3));
			long routeId = rs.getLong(4);
			joiner.setRouteBean(this.routeDAO.getRouteById(routeId));
			joiner.setStatus(rs.getString(5));
			joiner.setPaid(rs.getBoolean(6));
			joiner.setFee(rs.getInt(7));
			joiners.add(joiner);
		}
		return joiners;
	}

	public boolean userJoined(long userId, long orderId) {
		boolean userJoined = false;
		String sql = "select id from pool_in_process_order_joiner_map map "
				+ " where map.pool_in_process_order_id = ? "
				+ "and map.user_id = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				orderId));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, orderId, userId);
		if (rs.next()) {
			userJoined = true;
		}
		return userJoined;
	}
	
}
