package com.ishare.bean.enums;

public enum MessageTypeEnum {

	ORDER_CONFIRM_NOTIFICATION("order_confirm_notification");

	private String value;

	private MessageTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static String checkValue(String value) {
		for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
			if (messageTypeEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
