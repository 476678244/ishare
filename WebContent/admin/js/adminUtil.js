function getOrderKey(order) {
	if (order.objectId == null) {
		return order.id + order.poolOrderType;
	} else {
		return order.objectId;
	}
}