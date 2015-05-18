package com.ishare.bean.enums;

public enum AgeEnum {

	ZEROS("00s"), NINTYS("90s"), EIGHTYS("80s"), SEVENTYS("70s"), BLANK("blank");

	private String value;

	private AgeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static String checkValue(String value) {
		for (AgeEnum ageEnum : AgeEnum.values()) {
			if (ageEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
