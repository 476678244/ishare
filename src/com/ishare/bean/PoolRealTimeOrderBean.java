package com.ishare.bean;

public class PoolRealTimeOrderBean extends PoolOrderBean {

	// meaningless for mobile side
	private String chatGroupId;

	public String getChatGroupId() {
		return chatGroupId;
	}

	public void setChatGroupId(String chatGroupId) {
		this.chatGroupId = chatGroupId;
	}

	@Override
	public String toString() {
		return "PoolRealTimeOrderBean [chatGroupId=" + chatGroupId + "]";
	}
}
