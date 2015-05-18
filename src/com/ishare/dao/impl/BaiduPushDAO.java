package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ishare.bean.BaiduPush;

@Repository
public class BaiduPushDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public final static Logger logger = LoggerFactory
			.getLogger(BaiduPushDAO.class);

	public void upsertBaiduPush(final long userId, final String baiduUser,
			final String baiduChannel) {
		String sql = "select user_id from user_baidu_push where user_id = ?";
		long foundUser = 0;
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, userId);
		if (rs.next()) {
			foundUser = rs.getLong(1);
		}
		if (foundUser > 0) {
			sql = "update user_baidu_push set baidu_user = ?, baidu_channel = ? where user_id = ?";
		} else {
			this.insert(userId, baiduUser, baiduChannel);
			return;
		}
		logger.info(String.format(
				"sql[%s] running with bind values[%s, %s, %s]", sql, baiduUser,
				baiduChannel, userId));
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, baiduUser);
				ps.setString(2, baiduChannel);
				ps.setLong(3, userId);
			}
		});
	}

	private void insert(final long userId, final String baiduUser,
			final String baiduChannel) {
		String sql = "insert into user_baidu_push(user_id,baidu_user,baidu_channel) values(?,?,?)";
		logger.info(String.format(
				"sql[%s] running with bind values[%s, %s, %s]", sql, baiduUser,
				baiduChannel, userId));
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userId);
				ps.setString(2, baiduUser);
				ps.setString(3, baiduChannel);
			}
		});
	}

	public BaiduPush getBaiduPushByUser(final long userId) {
		String sql = "select baidu_user, baidu_channel from user_baidu_push where user_id = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				userId));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, userId);
		if (rs.next()) {
			BaiduPush baiduPush = new BaiduPush();
			baiduPush.setUserId(userId);
			baiduPush.setBaiduUser(rs.getString(1));
			baiduPush.setBaiduChannel(rs.getString(2));
			return baiduPush;
		}
		return null;
	}
	
	public void deleteBaiduPushByUser(final long userId) {
		String sql = "delete from user_baidu_push where user_id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userId);
			}
		});
	}
}
