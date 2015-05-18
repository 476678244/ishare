package com.ishare.bean;

public class SitePointBean {

	private long longtitude;

	private long laitude;

	private String address;

	public SitePointBean(long longtitude, long latitude, String address) {
		this.longtitude = longtitude;
		this.laitude = latitude;
		this.address = address;
	}

	public SitePointBean() {

	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getLongtitude() {
		return longtitude;
	}

	public long getLaitude() {
		return laitude;
	}

	public void setLongtitude(long longtitude) {
		this.longtitude = longtitude;
	}

	public void setLaitude(long laitude) {
		this.laitude = laitude;
	}

	@Override
	public String toString() {
		return "SitePointBean [longtitude=" + longtitude + ", laitude="
				+ laitude + ", address=" + address + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + (int) (laitude ^ (laitude >>> 32));
		result = prime * result + (int) (longtitude ^ (longtitude >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SitePointBean other = (SitePointBean) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (laitude != other.laitude)
			return false;
		if (longtitude != other.longtitude)
			return false;
		return true;
	}
}
