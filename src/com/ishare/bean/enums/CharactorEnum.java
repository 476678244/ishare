package com.ishare.bean.enums;

public enum CharactorEnum implements IshareEnum {

	OUTGOING("outgoing"), INNERGOING("innergoing"), BLANK("blank");

	private String value;

	private CharactorEnum(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	public static String checkValue(String value) {
		for (CharactorEnum charactorEnum : CharactorEnum.values()) {
			if (charactorEnum.getValue().equals(value)) {
				return value;
			}
		}
		return null;
	}
}
