package com.ishare.bean.enums;

public enum CarTypeEnum {

	PRIVATE("private"), TAXI("taxi");

	private String value;

	private CarTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static String checkValue(String value) {
		for (CarTypeEnum carTypeEnum : CarTypeEnum.values()) {
			if (carTypeEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
