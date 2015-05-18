package com.ishare.bean.enums;

public enum JobEnum implements IshareEnum{

	STUDENT("student"), EMPLOYEE("employee"), BLANK("blank");

	private String value;

	private JobEnum(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	public static String checkValue(String value) {
		for (JobEnum jobEnum : JobEnum.values()) {
			if (jobEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
