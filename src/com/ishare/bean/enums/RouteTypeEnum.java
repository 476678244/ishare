package com.ishare.bean.enums;

public enum RouteTypeEnum {

	LIKED("liked"), INUSE("inuse");

	private String value;

	private RouteTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static String checkValue(String value) {
		for (RouteTypeEnum routeTypeEnum : RouteTypeEnum.values()) {
			if (routeTypeEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
