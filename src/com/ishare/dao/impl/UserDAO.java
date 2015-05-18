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
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ishare.bean.IdentityBean;
import com.ishare.bean.PaymentBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.IdentityStatusEnum;
import com.mysql.jdbc.Statement;

@Repository
public class UserDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("routeDAO")
	RouteDAO routeDAO;

	@Autowired
	@Qualifier("carDAO")
	CarDAO carDAO;

	public final static Logger logger = LoggerFactory.getLogger(UserDAO.class);

	public long insertUser(final UserBean userBean) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into user (username, password, nickname, role) values (?, ?, ?, ?)";
				PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, userBean.getUsername());
				ps.setString(2, userBean.getPassword());
				ps.setString(3, userBean.getNickname());
				ps.setString(4, userBean.getRole());
				return ps;
			}
		}, keyHolder);
		long userId = keyHolder.getKey().longValue();
		return userId;
	}

	// this assumes that the userBean is full of db info
	public String insertIdentityWithUserUpdated(
			final IdentityBean identityBean, UserBean userBean) {
		final String status = identityBean.isIdentityInfoFull() ? IdentityStatusEnum.UPLOAD_FINISH
				.getValue() : IdentityStatusEnum.IN_PROGRESS.getValue();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into identity(identification_num, real_name, driver_license_front,"
						+ " driver_licese_back, status) values (?,?,?,?,?)";
				PreparedStatement pstat = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				pstat.setString(1, identityBean.getIdentification_num());
				pstat.setString(2, identityBean.getReal_name());
				pstat.setString(3, identityBean.getDriver_license_front());
				pstat.setString(4, identityBean.getDriver_license_back());
				pstat.setString(5, status);
				return pstat;
			}
		}, keyHolder);
		long identityId = keyHolder.getKey().longValue();
		System.out.println("identity with id :" + identityId + " inserted");
		identityBean.setId(identityId);
		userBean.setIdentityBean(identityBean);
		this.updateIdentityId(userBean);
		return status;
	}

	// this assumes that the userBean is full of db info
	public String insertPaymentWithUserUpdated(final PaymentBean paymentBean,
			UserBean userBean) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into payment(type, account)"
						+ " values (?,?)";
				PreparedStatement pstat = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				pstat.setString(1, paymentBean.getType());
				pstat.setString(2, paymentBean.getAccount());
				return pstat;
			}
		}, keyHolder);
		long paymentId = keyHolder.getKey().longValue();
		System.out.println("identity with id :" + paymentId + " inserted");
		paymentBean.setId(paymentId);
		userBean.setPaymentBean(paymentBean);
		this.updatePaymentId(userBean);
		return null;
	}

	public void updateUser(final UserBean userBean) {
		String sql = "update user set age = ?, nickname = ?,"
				+ " gender = ?, job = ?, charactor = ?, head_pic = ?  where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, userBean.getAge());
				ps.setString(2, userBean.getNickname());
				ps.setString(3, userBean.getGender());
				ps.setString(4, userBean.getJob());
				ps.setString(5, userBean.getCharactor());
				ps.setString(6, userBean.getHeadPic());
				// if (userBean.getPaymentBean() != null) {
				// ps.setLong(6, userBean.getPaymentBean().getId());
				// } else {
				// ps.setNull(6, Types.BIGINT);
				// }
				ps.setLong(7, userBean.getId());
			}
		});
	}

	public void updateCarId(final UserBean userBean) {
		String sql = "update user set car_id = ? where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userBean.getCarBean().getId());
				ps.setLong(2, userBean.getId());
			}
		});
	}

	public void updatePaymentId(final UserBean userBean) {
		String sql = "update user set payment_id = ? where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userBean.getPaymentBean().getId());
				ps.setLong(2, userBean.getId());
			}
		});
	}

	public void updateIdentityId(final UserBean userBean) {
		String sql = "update user set identity_id = ? where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userBean.getIdentityBean().getId());
				ps.setLong(2, userBean.getId());
			}
		});
	}

	public String updateIdentity(final IdentityBean identityBean) {
		String sql = "update identity set identification_num = ? real_name = ? driver_license_front = ? driver_licese_back = ?"
				+ "status = ? where id = ?";
		final String status = identityBean.isIdentityInfoFull() ? IdentityStatusEnum.UPLOAD_FINISH
				.getValue() : IdentityStatusEnum.IN_PROGRESS.getValue();
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, identityBean.getIdentification_num());
				ps.setString(2, identityBean.getReal_name());
				ps.setString(3, identityBean.getDriver_license_front());
				ps.setString(4, identityBean.getDriver_license_back());
				ps.setString(5, status);
				ps.setLong(6, identityBean.getId());
			}
		});
		return status;
	}

	public UserBean getUserByUsername(String username) {
		String sql = "select * from user where username = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				username));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, username);
		return this.getUserBeanByRs(rs);
	}

	public UserBean getUserByUserId(long id) {
		String sql = "select * from user where id = ?";
		logger.info(String.format("sql[%s] running with bind values[%s]", sql,
				id));
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, id);
		return this.getUserBeanByRs(rs);
	}

	private UserBean getUserBeanByRs(SqlRowSet rs) {
		UserBean userBean = new UserBean();
		if (rs.next()) {
			userBean.setId(rs.getInt(1));
			userBean.setUsername(rs.getString(2));
			// userBean.setPassword(rs.getString(3));
			userBean.setAge(rs.getInt(4));
			userBean.setGender(rs.getString(5));
			userBean.setNickname(rs.getString(6));
			userBean.setRole(rs.getString(7));
			userBean.setJob(rs.getString(8));
			userBean.setCharactor(rs.getString(9));
			int paymentId = rs.getInt(10);
			int carId = rs.getInt(11);
			int identityId = rs.getInt(12);
			if (paymentId != 0) {
				userBean.setPaymentBean(this.getPaymentBeanById(paymentId));
			}
			if (carId != 0) {
				userBean.setCarBean(this.carDAO.getCarBeanById(carId));
			}
			if (identityId != 0) {
				userBean.setIdentityBean(this.getIdentityBeanById(identityId));
			}
			userBean.setHeadPic(rs.getString(13));
			logger.info(String.format("userBean[%s] found ",
					userBean.toString()));
			return userBean;
		}
		return null;
	}

	private PaymentBean getPaymentBeanById(int id) {
		String queryPayment = "select * from payment where id = ?";
		SqlRowSet queryPaymentRs = this.jdbcTemplate.queryForRowSet(
				queryPayment, id);
		PaymentBean paymentBean = new PaymentBean();
		if (queryPaymentRs.next()) {
			paymentBean.setType(queryPaymentRs.getString(2));
			paymentBean.setAccount(queryPaymentRs.getString(3));
			return paymentBean;
		} else {
			return null;
		}
	}

	private IdentityBean getIdentityBeanById(int id) {
		String queryIdentity = "select * from identity where id = ?";
		SqlRowSet queryIdentityRs = this.jdbcTemplate.queryForRowSet(
				queryIdentity, id);
		IdentityBean identityBean = new IdentityBean();
		if (queryIdentityRs.next()) {
			identityBean.setId(queryIdentityRs.getInt(1));
			identityBean.setIdentification_num(queryIdentityRs.getString(2));
			identityBean.setReal_name(queryIdentityRs.getString(3));
			identityBean.setDriver_license_front(queryIdentityRs.getString(4));
			identityBean.setDriver_license_back(queryIdentityRs.getString(5));
			identityBean.setStatus(queryIdentityRs.getString(6));
			return identityBean;
		} else {
			return null;
		}
	}

	public String updatePayment(final PaymentBean paymentBean) {
		String sql = "update payment set type = ? account = ? where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, paymentBean.getType());
				ps.setString(2, paymentBean.getAccount());
			}
		});
		return null;
	}

	public boolean authByPassword(String username, String password) {
		String query = "select id from user where username = ? and password = ?";
		logger.info("[" + query + "] bindValues[" + username + "," + password
				+ "]");
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(query, username,
				password);
		if (rs.next()) {
			logger.info("username[" + username + "]password[" + password
					+ "] auth ok!");
			return true;
		}
		logger.info("username[" + username + "]password[" + password
				+ "] auth fail!");
		return false;
	}

	public void updateHeadPic(final String headPicUrl, final long userId) {
		String sql = "update user set head_pic = ?  where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, headPicUrl);
				ps.setLong(2, userId);
			}
		});
		logger.info("user[" + userId + "] head picture updated with url["
				+ headPicUrl + "]");
	}
	
	public List<UserBean> getAllUsers() {
		String sql = "select username, age, gender, nickname, role, job, charactor, id from user ";
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(
				sql);
		List<UserBean> users = new ArrayList<UserBean>();
		while (rs.next()) {
			UserBean user = new UserBean();
			user.setUsername(rs.getString(1));
			user.setAge(rs.getInt(2));
			user.setGender(rs.getString(3));
			user.setNickname(rs.getString(4));
			user.setRole(rs.getString(5));
			user.setJob(rs.getString(6));
			user.setCharactor(rs.getString(7));
			user.setId(rs.getLong(8));
			users.add(user);
		}
		return users;
	}
	
	public void deleteUser(final long userId) {
		String sql = "delete from user where id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, userId);
			}
		});
	}
}
