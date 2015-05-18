package com.ishare.bean.param;

public class GetOrderRequest {

	private long orderId;
	
	private String objectId;
	
	private String type;

	public GetOrderRequest() {		
	}

	public GetOrderRequest(long orderId, String objectId, String type) {
		this.orderId = orderId;
		this.objectId = objectId;
		this.type = type;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
