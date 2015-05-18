package com.ishare.bean.param;

import com.ishare.bean.PoolSubjectBean;

public class RealTimePoolRequest {

	private long startLongtitude;
	
	private long startLatitude;
	
	private String startAddress;
	
	private long endLongtitude;
	
	private long endLatitude;
	
	private String endAddress;
	
	private int seatsCount;
	
	private long userId;
	
	private PoolSubjectBean subject;

	public long getStartLongtitude() {
		return startLongtitude;
	}

	public void setStartLongtitude(long startLongtitude) {
		this.startLongtitude = startLongtitude;
	}

	public long getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(long startLatitude) {
		this.startLatitude = startLatitude;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public long getEndLongtitude() {
		return endLongtitude;
	}

	public void setEndLongtitude(long endLongtitude) {
		this.endLongtitude = endLongtitude;
	}

	public long getEndLatitude() {
		return endLatitude;
	}

	public void setEndLatitude(long endLatitude) {
		this.endLatitude = endLatitude;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public int getSeatsCount() {
		return seatsCount;
	}

	public void setSeatsCount(int seatsCount) {
		this.seatsCount = seatsCount;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public PoolSubjectBean getSubject() {
		return subject;
	}

	public void setSubject(PoolSubjectBean subject) {
		this.subject = subject;
	}
	
	
}
