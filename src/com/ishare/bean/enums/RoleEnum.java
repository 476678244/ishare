package com.ishare.bean.enums;

public enum RoleEnum implements IshareEnum{

	PASSENGER("passenger"), DRIVER("dirver");

	private String value;

	private RoleEnum(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	public static String checkValue(String value) {
		for (RoleEnum roleEnum : RoleEnum.values()) {
			if (roleEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
