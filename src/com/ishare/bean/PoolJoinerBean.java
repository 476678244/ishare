package com.ishare.bean;

public class PoolJoinerBean {
	
	public PoolJoinerBean() {}
	
	public PoolJoinerBean(UserBean userBean, int seatsCount) {
		this.userBean = userBean;
		this.seatsCount = seatsCount;
	}
	
	public PoolJoinerBean(long id, UserBean userBean, int seatsCount) {
		this.id = id;
		this.userBean = userBean;
		this.seatsCount = seatsCount;
	}

	// nullable, useful
	private long id;
	
	// need user id in userbean
	private UserBean userBean;
	
	// necessary
	private int seatsCount;
	
	// necessary
	private RouteBean routeBean;
	
	// nullable
	private String status;
	
	// nullable
	private int fee;
	
	// nullable
	private boolean paid;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public int getSeatsCount() {
		return seatsCount;
	}

	public void setSeatsCount(int seatsCount) {
		this.seatsCount = seatsCount;
	}

	public RouteBean getRouteBean() {
		return routeBean;
	}

	public void setRouteBean(RouteBean routeBean) {
		this.routeBean = routeBean;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	@Override
	public String toString() {
		return "PoolJoinerBean [id=" + id + ", userBean=" + userBean
				+ ", seatsCount=" + seatsCount + ", routeBean=" + routeBean
				+ ", status=" + status + ", fee=" + fee + ", paid=" + paid
				+ "]";
	}
}
