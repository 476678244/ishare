package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.ishare.bean.RouteBean;
import com.ishare.bean.SitePointBean;
import com.mysql.jdbc.Statement;

@Component("routeDAO")
public class RouteDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public long insertRoute(final RouteBean routeBean) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into route(start_longtitude, start_latitude, "
						+ "start_address, end_longtitude, end_latitude, end_address, type, "
						+ "status) values (?,?,?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, routeBean.getStartSitePoint().getLongtitude());
				ps.setLong(2, routeBean.getStartSitePoint().getLaitude());
				ps.setString(3, routeBean.getStartSitePoint().getAddress());
				ps.setLong(4, routeBean.getEndSitePoint().getLongtitude());
				ps.setLong(5, routeBean.getEndSitePoint().getLaitude());
				ps.setString(6, routeBean.getEndSitePoint().getAddress());
				ps.setString(7, routeBean.getType());
				ps.setString(8, routeBean.getStatus());
				return ps;
			}
		}, keyHolder);
		long routeId = keyHolder.getKey().longValue();
		return routeId;
	}

	public String updateRoute(final RouteBean routeBean) {
		String sql = "update route set start_longtitude = ?, start_latitude = ?, "
				+ "start_address = ?, end_longtitude = ?,"
				+ " end_latitude = ?, end_address = ?, type = ?, "
				+ "statis = ?) where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, routeBean.getStartSitePoint().getLongtitude());
				ps.setLong(2, routeBean.getStartSitePoint().getLaitude());
				ps.setString(3, routeBean.getStartSitePoint().getAddress());
				ps.setLong(4, routeBean.getEndSitePoint().getLongtitude());
				ps.setLong(5, routeBean.getEndSitePoint().getLaitude());
				ps.setString(6, routeBean.getEndSitePoint().getAddress());
				ps.setString(7, routeBean.getType());
				ps.setString(8, routeBean.getStatus());
				ps.setLong(9, routeBean.getId());
			}
		});
		return null;
	}

	public RouteBean getRouteById(long routeId) {
		String queryRoute = "select * from route where id = ?";
		SqlRowSet queryRouteRs = this.jdbcTemplate.queryForRowSet(queryRoute,
				routeId);
		RouteBean routeBean = new RouteBean();
		if (queryRouteRs.next()) {
			routeBean.setId(routeId);
			SitePointBean startSitePointBean = new SitePointBean();
			startSitePointBean.setLongtitude(queryRouteRs.getLong(2));
			startSitePointBean.setLaitude(queryRouteRs.getLong(3));
			startSitePointBean.setAddress(queryRouteRs.getString(4));
			SitePointBean endSitePointBean = new SitePointBean();
			endSitePointBean.setLongtitude(queryRouteRs.getLong(5));
			endSitePointBean.setLaitude(queryRouteRs.getLong(6));
			endSitePointBean.setAddress(queryRouteRs.getString(7));
			routeBean.setStartSitePoint(startSitePointBean);
			routeBean.setEndSitePoint(endSitePointBean);
			routeBean.setType(queryRouteRs.getString(8));
			routeBean.setStatus(queryRouteRs.getString(9));
			return routeBean;
		} else {
			return null;
		}
	}
	
	public void deleteRoute(long id) {
		String sql = "delete from route where id = ?";
		this.jdbcTemplate.update(sql, id);
	}
}
