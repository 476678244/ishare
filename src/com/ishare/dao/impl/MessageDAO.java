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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ishare.bean.MessageBean;
import com.mysql.jdbc.Statement;

@Repository
public class MessageDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public final static Logger logger = LoggerFactory
			.getLogger(MessageDAO.class);

	public long addMessage(final MessageBean message) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into message(type, content, from_user_name, to_user_name, related_order) "
						+ " values (?,?,?,?,?)";
				logger.info(String.format(
						"sql[%s] running with bind values[%s, %s, %s ,%s, %s]",
						sql, message.getType(), message.getContent(),
						message.getFromUser(), message.getToUser(),
						message.getRelatedOrder()));
				PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, message.getType());
				ps.setString(2, message.getContent());
				ps.setString(3, message.getFromUser());
				ps.setString(4, message.getToUser());
				ps.setLong(5, message.getRelatedOrder());
				return ps;
			}
		}, keyHolder);
		long messageId = keyHolder.getKey().longValue();
		return messageId;
	}

	public List<MessageBean> getUserMessages(String toUser) {
		String sql = "select type, content, from_user_name, related_order from message where to_user_name = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				toUser));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, toUser);
		List<MessageBean> messages = new ArrayList<MessageBean>();
		while (rs.next()) {
			MessageBean message = new MessageBean();
			message.setType(rs.getString(1));
			message.setContent(rs.getString(2));
			message.setFromUser(rs.getString(3));
			message.setToUser(toUser);
			message.setRelatedOrder(rs.getLong(4));
			messages.add(message);
		}
		return messages;
	}
	
	public void deleteUserMessages(final String toUser) {
		String sql = "delete from message where to_user_name = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, toUser);
			}
		});
	}
}
