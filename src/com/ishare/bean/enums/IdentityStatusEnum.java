package com.ishare.bean.enums;

public enum IdentityStatusEnum {

	IN_PROGRESS("in_progress"), UPLOAD_FINISH("upload_finish"), VERIFIED("verified");

	private String value;

	private IdentityStatusEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static String checkValue(String value) {
		for (IdentityStatusEnum iarTypeEnum : IdentityStatusEnum.values()) {
			if (iarTypeEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
