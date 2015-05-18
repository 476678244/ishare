package com.ishare.bean;

import org.apache.commons.lang.StringUtils;

import com.ishare.bean.enums.IdentityStatusEnum;

// nullable
public class IdentityBean {

	private long id;

	private String identification_num;

	private String real_name;

	private String driver_license_front;

	private String driver_license_back;

	private String status;

	public String getIdentification_num() {
		return identification_num;
	}

	public void setIdentification_num(String identification_num) {
		this.identification_num = identification_num;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getDriver_license_front() {
		return driver_license_front;
	}

	public void setDriver_license_front(String driver_license_front) {
		this.driver_license_front = driver_license_front;
	}

	public String getDriver_license_back() {
		return driver_license_back;
	}

	public void setDriver_license_back(String driver_license_back) {
		this.driver_license_back = driver_license_back;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isIdentityInfoFull() {
		if (!StringUtils.isBlank(this.identification_num)
				&& !StringUtils.isBlank(this.real_name)
				&& !StringUtils.isBlank(this.driver_license_front)
				&& !StringUtils.isBlank(this.driver_license_back)) {
			return true;
		}
		return false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isUploadFinish() {
		return IdentityStatusEnum.UPLOAD_FINISH.getValue().equals(
				this.getStatus()) ? true : false;
	}

	public boolean isVerified() {
		return IdentityStatusEnum.VERIFIED.getValue().equals(this.getStatus()) ? true
				: false;
	}
}
