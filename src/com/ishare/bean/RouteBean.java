package com.ishare.bean;

public class RouteBean {

	// nullable, useful
	private long id;

	private SitePointBean startSitePoint;
	
	private SitePointBean endSitePoint;
	
	// nullable
	private String type;
	
	// nullable
	private String status;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SitePointBean getStartSitePoint() {
		return startSitePoint;
	}

	public SitePointBean getEndSitePoint() {
		return endSitePoint;
	}

	public void setStartSitePoint(SitePointBean startSitePoint) {
		this.startSitePoint = startSitePoint;
	}

	public void setEndSitePoint(SitePointBean endSitePoint) {
		this.endSitePoint = endSitePoint;
	}

	@Override
	public String toString() {
		return "RouteBean [id=" + id + ", startSitePoint=" + startSitePoint
				+ ", endSitePoint=" + endSitePoint + ", type=" + type
				+ ", status=" + status + "]";
	}
}
