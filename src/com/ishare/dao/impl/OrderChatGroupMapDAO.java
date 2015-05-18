package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Repository
public class OrderChatGroupMapDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public final static Logger logger = LoggerFactory
			.getLogger(OrderChatGroupMapDAO.class);

	/**
	 * create order chatgroup map
	 */
	public void createOrderChatGroupMap(final long orderId,
			final String chatGroupId, final Date deleteTime) {
		String sql = "insert into order_chatgroup_map ("
				+ "order_id, chatgroup_id, delete_time) values (?, ?, ?)";
		logger.info("sql[" + sql + "] runnung with bind values[" + orderId
				+ "," + chatGroupId + "," + deleteTime.toString() + "]");
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, orderId);
				ps.setString(2, chatGroupId);
				ps.setTimestamp(3, new Timestamp(deleteTime.getTime()));
			}
		});
	}

	public void upsertOrderChatGroupMap(final long orderId,
			final String chatGroupId, final Date deleteTime) {
		String sql = "select order_id from order_chatgroup_map where order_id = ?";
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, orderId);
		if (rs.next()) {
			sql = "update order_chatgroup_map set chatgroup_id = ?, delete_time = ? where order_id = ?";
			logger.info(MessageFormat.format(
					"sql {0} is running with values [{1},{2},{3}]", sql,
					chatGroupId, deleteTime, orderId));
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(3, orderId);
					ps.setString(1, chatGroupId);
					ps.setTimestamp(2, new Timestamp(deleteTime.getTime()));
				}
			});
		} else {
			this.createOrderChatGroupMap(orderId, chatGroupId, deleteTime);
		}
	}

	public String getChatGroupIdByOrderId(final long orderId) {
		String sql = "select chatgroup_id from order_chatgroup_map where order_id = ?";
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, orderId);
		if (rs.next()) {
			String chatGroupId = rs.getString(1);
			logger.info(String.format("chatGroupId(%s) got refer to order(%s)",
					chatGroupId, orderId));
			return chatGroupId;
		}
		return null;
	}

	public List<Long> getUpToDeleteTimeOrder(final Date deleteTime) {
		List<Long> orderIds = new ArrayList<Long>();
		String sql = "select order_id from order_chatgroup_map where delete_time = ?";
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, deleteTime);
		if (rs.next()) {
			long orderId = rs.getLong(1);
			logger.info(String.format("orderId(%s) got refer to delete time(%s)", 
					orderId, deleteTime));
			orderIds.add(orderId);
		}
		return orderIds;
	}
}
