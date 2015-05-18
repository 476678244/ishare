package com.ishare.bean.enums;

public enum PoolJoinerStatusEnum {

	UNCONFIRM("unconfirm"), CONFIRM_GO("confirm_go"), CONFIRM_NOT_GO(
			"confirm_not_go"), PAID("paid");

	private String value;

	private PoolJoinerStatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static String checkValue(String value) {
		for (PoolJoinerStatusEnum poolJoinerStatusEnum : PoolJoinerStatusEnum
				.values()) {
			if (poolJoinerStatusEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
