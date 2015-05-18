package com.ishare.bean;

public class MessageBean {

	private long id;

	private String type;

	private String content;

	private String fromUser = "system";

	private String toUser;

	private long relatedOrder;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public long getRelatedOrder() {
		return relatedOrder;
	}

	public void setRelatedOrder(long relatedOrder) {
		this.relatedOrder = relatedOrder;
	}

	@Override
	public String toString() {
		return "MessageBean [id=" + id + ", type=" + type + ", content="
				+ content + ", fromUser=" + fromUser + ", toUser=" + toUser
				+ ", relatedOrder=" + relatedOrder + "]";
	}
}
