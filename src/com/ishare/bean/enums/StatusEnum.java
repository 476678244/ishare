package com.ishare.bean.enums;

public enum StatusEnum {

	ACTIVE("active"), INACTIVE("inactive");

	private String value;

	private StatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static String checkValue(String value) {
		for (StatusEnum statusEnum : StatusEnum.values()) {
			if (statusEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
