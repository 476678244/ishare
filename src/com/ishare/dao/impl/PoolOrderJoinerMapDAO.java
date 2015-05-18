package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component("poolOrderJoinerMapDAO")
public class PoolOrderJoinerMapDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public final static Logger logger = LoggerFactory
			.getLogger(PoolOrderJoinerMapDAO.class);

	public void addJoinerOrderMap(final long orderId, final long joinerId,
			final long userId) {
		String sql = "insert into pool_in_process_order_joiner_map ("
				+ "pool_joiner_id, pool_in_process_order_id, user_id) values (?, ?, ?)";
		logger.info("sql[" + sql + "] running with bind values[" + joinerId
				+ "," + orderId + "," + userId + "]");
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, joinerId);
				ps.setLong(2, orderId);
				ps.setLong(3, userId);
			}
		});
	}

	public List<Long> getUserPrepareOrderIds(long userId) {
		String query = "select * from pool_in_process_order_joiner_map where user_id = ?";
		SqlRowSet queryRs = this.jdbcTemplate.queryForRowSet(query, userId);
		logger.info(String.format("sql[%s] running with bind values[%s]",
				query, userId));
		List<Long> orderIds = new ArrayList<Long>();
		while (queryRs.next()) {
			orderIds.add(queryRs.getLong(3));
			logger.info(String.format(
					"in process orderId[%s] found refer to userId[%s]",
					queryRs.getLong(3), userId));
		}
		return orderIds;
	}

	public List<Long> getUserOrderIds(long userId) {
		String query = "select * from pool_history_order_joiner_map where user_id = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]",
				query, userId));
		SqlRowSet queryRs = this.jdbcTemplate.queryForRowSet(query, userId);
		List<Long> orderIds = new ArrayList<Long>();
		while (queryRs.next()) {
			logger.info(String.format(
					"history orderId[%s] found refer to userId[%s]",
					queryRs.getLong(3), userId));
			orderIds.add(queryRs.getLong(3));
		}
		return orderIds;
	}

	public boolean seatsFull(long orderId, int fullSeats) {
		String query = "select joiner.id as joinerId , joiner.seats_count "
				+ "from ishare.pool_joiner joiner, ishare.pool_in_process_order_joiner_map map "
				+ "where joiner.id = map.pool_joiner_id and map.pool_in_process_order_id = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]",
				query, orderId));
		SqlRowSet queryRs = this.jdbcTemplate.queryForRowSet(query, orderId);
		int totalSeats = 0;
		while (queryRs.next()) {
			int seat = queryRs.getInt(2);
			totalSeats += seat;
		}
		if (totalSeats >= fullSeats) {
			logger.info("seats full detected for order[" + orderId + "]");
			return true;
		} else {
			return false;
		}
	}

	public void deleteInProcessOrderJoinerMap(final long joinerId,
			final long orderId) {
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "delete from pool_in_process_order_joiner_map "
						+ "where pool_joiner_id = ? and pool_in_process_order_id = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setLong(1, joinerId);
				ps.setLong(2, orderId);
				logger.info("sql[" + sql + "] running with bind values["
						+ joinerId + "," + orderId + "]");
				return ps;
			}
		});
		return;
	}
}
