package com.ishare.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.ishare.bean.CarBean;
import com.ishare.bean.UserBean;
import com.ishare.bean.enums.CarTypeEnum;
import com.mysql.jdbc.Statement;

@Component("carDAO")
public class CarDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("userDAO")
	UserDAO userDAO;

	public CarBean getCarBeanById(int id) {
		String queryCar = "select * from car where id = ?";
		SqlRowSet queryCarRs = this.jdbcTemplate.queryForRowSet(queryCar, id);
		CarBean carBean = new CarBean();
		if (queryCarRs.next()) {
			carBean.setPaizhao(queryCarRs.getString(2));
			String carType = queryCarRs.getString(3);
			carBean.setType(carType);
			carBean.setDriving_license_front(queryCarRs.getString(8));
			carBean.setDriving_license_back(queryCarRs.getString(9));
			if (CarTypeEnum.PRIVATE.equals(carType)) {
				return carBean;
			}
			carBean.setTaxi_company(queryCarRs.getString(4));
			carBean.setEmployee_num(queryCarRs.getString(5));
			carBean.setEmployee_identification_pic(queryCarRs.getString(6));
			carBean.setStatus(queryCarRs.getString(7));
			return carBean;
		} else {
			return null;
		}
	}

	// this assumes that the userBean is full of db info
	public String insertCarWithUserUpdated(final CarBean carBean,
			UserBean userBean) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					java.sql.Connection con) throws SQLException {
				String sql = "insert into car(paizhao, type, taxi_company, employee_num,"
						+ " employee_identification_pic, status, driving_license_front, "
						+ "driving_license_back) values (?,?,?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, carBean.getPaizhao());
				ps.setString(2, carBean.getType());
				ps.setString(3, carBean.getTaxi_company());
				ps.setString(4, carBean.getEmployee_num());
				ps.setString(5, carBean.getEmployee_identification_pic());
				ps.setString(6, carBean.getStatus());
				ps.setString(7, carBean.getDriving_license_front());
				ps.setString(8, carBean.getDriving_license_back());
				return ps;
			}
		}, keyHolder);
		int carId = keyHolder.getKey().intValue();
		System.out.println("car with id :" + carId + " inserted");
		carBean.setId(carId);
		userBean.setCarBean(carBean);
		this.userDAO.updateCarId(userBean);
		return null;
	}

	public String updateCar(final CarBean carBean, UserBean userBean) {
		String sql = "update car set paizhao = ?, type = ?, taxi_company = ?, employee_num = ?,"
				+ " employee_identification_pic = ?, status = ?, driving_license_front = ?, "
				+ "driving_license_back = ?) where id = ?";
		this.jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, carBean.getPaizhao());
				ps.setString(2, carBean.getType());
				ps.setString(3, carBean.getTaxi_company());
				ps.setString(4, carBean.getEmployee_num());
				ps.setString(5, carBean.getEmployee_identification_pic());
				ps.setString(6, carBean.getStatus());
				ps.setString(7, carBean.getDriving_license_front());
				ps.setString(8, carBean.getDriving_license_back());
				ps.setLong(9, carBean.getId());
			}
		});
		return null;
	}

}
