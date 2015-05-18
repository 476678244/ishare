package com.ishare.bean.enums;

public enum GenderEnum {

	MALE("male"), FEMALE("female"), BLANK("blank");

	private String value;

	private GenderEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static String checkValue(String value) {
		for (GenderEnum thisEnum : GenderEnum.values()) {
			if (thisEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
