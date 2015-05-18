function getUserOrders(userId, divName, document ) {
	$.get("/ishare/request/admin/getMyOrders?user_id="+userId, function(orders) {
		renderOrders(orders, divName, document);
	});	
}

function getOrders(divName, document) {
	$.get("/ishare/request/admin/getOrders", function(orders) {
		renderOrders(orders, divName, document);
	});
}

function renderOrders(orders, divName, document) {
	var orderItem = constructOrderItem(constructOrderContents(orders), orders.length);
	var div = document.getElementById(divName);	
	div.innerHTML = orderItem;
	for (var i = 0; i< orders.length; i++) {
		var order = orders[i];
		var key = getOrderKey(order);
		var idItem = document.getElementById("orderId" + key);
		idItem.onclick = new function() {
          var tempOrder = order;
          return function(){
        	  renderOrderDetail(divName, document, tempOrder);
          }
        }
	}
}

function getUser(userId, divName, document) {
	$.get("/ishare/request/admin/getUser?user_id=" + userId, function(user) {
		var users = new Array();
		users[0] = user;
		var contents = constructUserContents(users);
		addUserContentsToDiv(contents, divName, document);
		return user;
	});	
}

function getUsers(divName, document) {
	$.get("/ishare/request/admin/getUsers", function(users) {
		var contents = constructUserContents(users);
		addUserContentsToDiv(contents, divName, document);
		applyDeleteActionToDeleteButtons(users);
		return users;
	});	
}

function renderOrderDetail(divName, document, order) {
	var tempOrders = new Array();
	tempOrders[0] = order;
	var orderDetailHtml = constructOrderItem(constructOrderContents(tempOrders), 1);
	orderDetailHtml += constructJoinersItem(constructJoinersContent(order.poolJoiners));
	var subjects = new Array();
	subjects[0] = order.poolSubject;
	orderDetailHtml += constructSubjectItem(constructSubjectsContent(subjects));
	var divOrder = document.getElementById(divName);
	divOrder.innerHTML = orderDetailHtml;
}

function deleteUserOrders(userId) {
	$.get("/ishare/request/admin/deleteUserOrders?user_id=" + userId, function(result) {
		alertNumberResult(result);
	});	
}

function deleteUser(userId) {
	$.get("/ishare/request/admin/deleteUser?user_id=" + userId, function(result) {
		alertNumberResult(result);
		getUsers("div_users", document);
	});	
}

function alertNumberResult(result) {
	if (result == 1) {
		alert("Success!");
	} else {
		alert("fail!");
	}
}