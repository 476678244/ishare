package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

import com.ishare.bean.PoolSubjectBean;
import com.mysql.jdbc.Statement;

@Repository
public class PoolSubjectDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public final static Logger logger = LoggerFactory
			.getLogger(PoolSubjectDAO.class);

	public int insertPoolSubject(final PoolSubjectBean pooSubjectBean) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into pool_subject(gender, atmosphere, "
						+ "job, age) values (?,?,?,?)";
				logger.info("sql[" + sql + "] runnung with bind values["
						+ pooSubjectBean.getGender() + ","
						+ pooSubjectBean.getAtmosphere() + ","
						+ pooSubjectBean.getJob() + ","
						+ pooSubjectBean.getAge() + "]");
				PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, pooSubjectBean.getGender());
				ps.setString(2, pooSubjectBean.getAtmosphere());
				ps.setString(3, pooSubjectBean.getJob());
				ps.setString(4, pooSubjectBean.getAge());
				return ps;
			}
		}, keyHolder);
		int poolSubjectId = keyHolder.getKey().intValue();
		return poolSubjectId;
	}

	public String updatePoolSubject(final PoolSubjectBean poolSubjectBean) {
		String sql = "update pool_subject set gender = ? atmosphere = ? job = ? age = ?"
				+ " where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, poolSubjectBean.getGender());
				ps.setString(2, poolSubjectBean.getAtmosphere());
				ps.setString(3, poolSubjectBean.getJob());
				ps.setString(4, poolSubjectBean.getAge());
			}
		});
		return null;
	}

	public PoolSubjectBean getPoolSubjectById(long poolSubjectId) {
		String queryPoolSubject = "select * from pool_subject where id = ?";
		SqlRowSet queryPoolSubjectRs = this.jdbcTemplate.queryForRowSet(
				queryPoolSubject, poolSubjectId);
		PoolSubjectBean poolSubjectBean = new PoolSubjectBean();
		if (queryPoolSubjectRs.next()) {
			poolSubjectBean.setId(poolSubjectId);
			poolSubjectBean.setGender(queryPoolSubjectRs.getString(2));
			poolSubjectBean.setAtmosphere(queryPoolSubjectRs.getString(3));
			poolSubjectBean.setJob(queryPoolSubjectRs.getString(4));
			poolSubjectBean.setAge(queryPoolSubjectRs.getString(5));
			return poolSubjectBean;
		} else {
			return null;
		}
	}
	
	public void deletePoolSubject(long id) {
		String sql = "delete from pool_subject where id = ?";
		this.jdbcTemplate.update(sql, id);
	}
}
