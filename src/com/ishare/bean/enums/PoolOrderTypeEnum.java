package com.ishare.bean.enums;

public enum PoolOrderTypeEnum {

	DUAN_TU("duan_tu"), CHANG_TU("chang_tu"), REAL_TIME("real_time"), RESERVE("reserve");

	private String value;

	private PoolOrderTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static String checkValue(String value) {
		for (PoolOrderTypeEnum poolOrderTypeEnum : PoolOrderTypeEnum.values()) {
			if (poolOrderTypeEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
