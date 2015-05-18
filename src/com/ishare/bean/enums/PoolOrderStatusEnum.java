package com.ishare.bean.enums;

public enum PoolOrderStatusEnum {

	PASSENGERS_INCOMPLETE_DRIVER_UNJOINED 
		("passengers_incomplete_driver_unjoined"),
	PASSENGERS_COMPLETE_DRIVER_UNJOINED
		("passengers_complete_driver_unjoined"),
	PASSENGERS_COMPLETE_DRIVER_JOINED
		("passengers_complete_driver_joined"),
	PASSENGERS_INCOMPLETE_DRIVER_JOINED
		("passengers_incomplete_driver_joined"),
	JOINERS_CONFIRMED
		("joiners_confirmed"),
	POOL_ORDER_GONE
		("pool_order_gone"),
	POOL_ORDER_FAILED
		("pool_order_failed"),
	POOL_ORDER_FINISHED
		("pool_order_finished");

	private String value;

	private PoolOrderStatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static String checkValue(String value) {
		for (PoolOrderStatusEnum statusEnum : PoolOrderStatusEnum.values()) {
			if (statusEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
