package com.ishare.bean.enums;

public enum AtmosphereEnum {
	
	OUTGOING("outgoing"), QUEIT("quiet"), BLANK("blank");

	private String value;

	private AtmosphereEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static String checkValue(String value) {
		for (AtmosphereEnum atmosphereEnum : AtmosphereEnum.values()) {
			if (atmosphereEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
