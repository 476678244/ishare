package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.ishare.util.MessageUtil;

@Component("tokenDAO")
public class TokenDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public final static Logger logger = LoggerFactory.getLogger(TokenDAO.class);

	/**
	 * just unique for user&token , token can be duplicate
	 */
	public void createUserToken(final long userId, final String token) {
		String sql = "insert into user_token ("
				+ "user_id, token, start_date) values (?, ?, ?)";
		final Date now = new Date();
		logger.info("sql[" + sql + "] runnung with bind values[" + userId + ","
				+ token + "," + now.toString() + "]");
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userId);
				ps.setString(2, token);
				ps.setTimestamp(3, new Timestamp(now.getTime()));
			}
		});
	}

	public void upsertToken(final long userId, final String token) {
		String sql = "select user_id from user_token where user_id = ?";
		logger.info("sql[" + sql + "]running with bind values[" + userId + "]");
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, userId);
		if (rs.next()) {
			sql = "update user_token set token = ?, start_date = ? where user_id = ?";
			final Date now = new Date();
			logger.info(MessageFormat.format(
					"sql {0} is running with values [{1},{2},{3}]", sql, token,
					now, userId));
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(3, userId);
					ps.setString(1, token);
					ps.setTimestamp(2, new Timestamp(now.getTime()));
				}
			});
		} else {
			this.createUserToken(userId, token);
		}
	}

	public void deleteUserToken(final String token, final long userId) {
		String sql = "delete from user_token where token = ? and user_id = ?";
		logger.info("[" + sql + "],bindValues[" + token + "," + userId + "]");
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, token);
				ps.setLong(2, userId);
			}
		});
	}

	public int authToken(final long userId, final String token) {
		String query = "select start_date from user_token where user_id = ? and token = ?";
		logger.info("sql[" + query + "]running with bind values[" + userId
				+ "," + token + "]");
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(query, userId, token);
		if (rs.next()) {
			// ... will add valid time in the future
			return MessageUtil.TOKEN_OK;
		}
		return MessageUtil.TOKEN_WRONG;
	}
	
	public String getUserToken(final long userId) {
		String query = "select token from user_token where user_id = ?";
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(query, userId);
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}
	
	public void deleteUserAllTokens(final long userId) {
		String sql = "delete from user_token where user_id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userId);
			}
		});
	}
}
