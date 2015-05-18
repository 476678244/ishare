package com.ishare.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PoolOrderBean {

	// nullable, useful, used for mysql as type of long
	private long id;
	
	// used for mongodb _id as type of objectId`s toString()
	private String objectId;
	
	// can't null
	private String poolOrderType;
	
	private Date startTime;
	
	private long startTimeLong;
	
	// this number only represent for the passengers
	private int totalSeats;
	
	// nullable
	private long driverUserId;
	
	// nullable, useful
	private long captainUserId;
	
	// nullable
	private String status;
	
	// nullable
	private boolean likeTaxiOnly;
	
	private PoolSubjectBean poolSubject; 
	
	// necessary
	private List<PoolJoinerBean> poolJoiners = new ArrayList<PoolJoinerBean>(4);
	
	// necessary
	private SitePointBean startSitePoint;
	
	// necessary
	private SitePointBean endSitePoint;
	
	// nullable
	private SitePointBean lastMiddleSitePoint;
	
	// nullable
	private long distance;

	/** Bei Zhu*/
	private String note;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPoolOrderType() {
		return poolOrderType;
	}

	public void setPoolOrderType(String poolOrderType) {
		this.poolOrderType = poolOrderType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public long getDriverUserId() {
		return driverUserId;
	}

	public void setDriverUserId(long driverUserId) {
		this.driverUserId = driverUserId;
	}

	public long getCaptainUserId() {
		return captainUserId;
	}

	public void setCaptainUserId(long captainUserId) {
		this.captainUserId = captainUserId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public PoolSubjectBean getPoolSubject() {
		return poolSubject;
	}

	public void setPoolSubject(PoolSubjectBean poolSubject) {
		this.poolSubject = poolSubject;
	}

	public List<PoolJoinerBean> getPoolJoiners() {
		return poolJoiners;
	}

	public void setPoolJoiners(List<PoolJoinerBean> poolJoiners) {
		this.poolJoiners = poolJoiners;
	}

	public SitePointBean getStartSitePoint() {
		return startSitePoint;
	}

	public SitePointBean getEndSitePoint() {
		return endSitePoint;
	}

	public SitePointBean getLastMiddleSitePoint() {
		return lastMiddleSitePoint;
	}

	public void setStartSitePoint(SitePointBean startSitePoint) {
		this.startSitePoint = startSitePoint;
	}

	public void setEndSitePoint(SitePointBean endSitePoint) {
		this.endSitePoint = endSitePoint;
	}

	public void setLastMiddleSitePoint(SitePointBean lastMiddleSitePoint) {
		this.lastMiddleSitePoint = lastMiddleSitePoint;
	}

	public boolean isLikeTaxiOnly() {
		return likeTaxiOnly;
	}

	public void setLikeTaxiOnly(boolean likeTaxiOnly) {
		this.likeTaxiOnly = likeTaxiOnly;
	}

	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public long getStartTimeLong() {
		return startTimeLong;
	}

	public void setStartTimeLong(long startTimeLong) {
		this.startTimeLong = startTimeLong;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Override
	public String toString() {
		return "PoolOrderBean [id=" + id + ", objectId=" + objectId
				+ ", poolOrderType=" + poolOrderType + ", startTime="
				+ startTime + ", startTimeLong=" + startTimeLong
				+ ", totalSeats=" + totalSeats + ", driverUserId="
				+ driverUserId + ", captainUserId=" + captainUserId
				+ ", status=" + status + ", likeTaxiOnly=" + likeTaxiOnly
				+ ", poolSubject=" + poolSubject + ", poolJoiners="
				+ poolJoiners + ", startSitePoint=" + startSitePoint
				+ ", endSitePoint=" + endSitePoint + ", lastMiddleSitePoint="
				+ lastMiddleSitePoint + ", distance=" + distance + ", note="
				+ note + "]";
	}
	
}
