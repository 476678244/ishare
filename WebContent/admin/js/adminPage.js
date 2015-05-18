var tabNavigatorHtml = 
	"<div class='navbar-inner'>" +
		"<div class='container'>" +
			"<a class='btn btn-navbar' data-toggle='collapse' data-target='.nav-collapse'>" +
				"<span class='icon-bar'></span> " +
				"<span class='icon-bar'></span> " +
				"<span class='icon-bar'></span> " +
			"</a>"  +
			"<a class='brand' href='#'>Akira</a>" +
			"<div class='nav-collapse'>" +
				"<ul class='nav'> " +
					"<li id='usersInTab'><a href='index.html'>Manage User</a></li> " +
					"<li id='ordersInTab'><a href='orders.html'>Manage Order</a></li> " +
					"<li><a href='settings.htm'>Account Settings</a></li> " +
					"<li><a href='help.htm'>Help</a></li> " +
					"<li class='dropdown'>" +
						"<a href='help.htm' class='dropdown-toggle' data-toggle='dropdown'>Tours <b class='caret'></b></a>" +
						"<ul class='dropdown-menu'>" +
							"<li><a href='help.htm'>Introduction Tour</a></li>" +
							"<li><a href='help.htm'>Project Organisation</a></li>" +
							"<li><a href='help.htm'>Task Assignment</a></li>" +
							"<li><a href='help.htm'>Access Permissions</a></li>" +
							"<li class='divider'></li>" +
							"<li class='nav-header'>Files</li>" +
							"<li><a href='help.htm'>How to upload multiple files</a></li>" +
							"<li><a href='help.htm'>Using file version</a></li>" +
						"</ul>" +
					"</li>" +
				"</ul>" +
				"<form class='navbar-search pull-left' action=''>" +
					"<input type='text' class='search-query span2' placeholder='Search' />" +
				"</form>" +
				"<ul class='nav pull-right'>" +
					"<li><a href='profile.htm'>@username</a></li>" +
					"<li><a href='../page/adminLogin.html'>Logout</a></li>" +
				"</ul>" +
			"</div>" +
		"</div>" +
	"</div>";

var navigatorHtml = " <div class='well' style='padding: 8px 0;''>" 
	+ "<ul class='nav nav-list'>" 
	+ "<li class='nav-header'>Akira</li>"
	+ "<li id='usersInList'><a href='index.html'><i class='icon-white icon-home'></i> User Management</a></li>"
	+ "<li id='ordersInList'><a href='orders.html'><i class='icon-white icon-home'></i> Order Management</a></li>"
	+ "<li><a href='projects.htm'><i class='icon-folder-open'></i>Projects</a></li>"
	+ "<li><a href='tasks.htm'><i class='icon-check'></i> Tasks</a></li>"
	+ "<li><a href='messages.htm'><i class='icon-envelope'></i>Messages</a></li>"
	+ "<li><a href='files.htm'><i class='icon-file'></i> Files</a></li>"
	+ "<li><a href='activity.htm'><i class='icon-list-alt'></i> Activity</a></li>"
	+ "<li class='nav-header'>Your Account</li>"
	+ "<li><a href='profile.htm'><i class='icon-user'></i> Profile</a></li>"
	+ "<li><a href='settings.htm'><i class='icon-cog'></i> Settings</a></li>"
	+ "<li class='divider'></li>"
	+ "<li><a href='help.htm'><i class='icon-info-sign'></i> Help</a></li>"
	+ "<li class='nav-header'>Bonus Templates</li>"
	+ "<li><a href='gallery.htm'><i class='icon-picture'></i> Gallery</a></li>"
	+ "<li><a href='blank.htm'><i class='icon-stop'></i> Blank Slate</a></li>"
    + "</ul> </div> ";
var constructOrderItem = function (contents, size) {
	var allOrdersHtml = "<h2>Orders(" + size + ")</h2>" 
		+ "<table class='table table-bordered table-striped'>" 
		+ "<thead> <tr>"
		+ "<th>Id</th> <th>Object Id</th> <th>Type</th> " 
		+ "<th>Start Time</th> <th>Seats</th> <th>Captain User</th> "
		+ "<th>Status</th> <th>Start</th> <th>End</th> <th>Distance</th>"
		+ "</tr> </thead>"
		+ contents + "</table>";
	return allOrdersHtml;
};

function constructOrderContents (orders) {
	var orderContents = "<tbody>";
	for( var i = 0 ; i < orders.length ; i++ ) { 
		var order = orders[i];
		var time = new Date(parseInt(order.startTime));
		var key = getOrderKey(order);
		var orderContent = "<tr> <th>" 
			+ "<a id=orderId" + key + ">" 
			+ order.id + "</a> </th>"
			+ "<th>" + order.objectId + "</th>"
			+ "<th>" + order.poolOrderType + "</th>"
			+ "<th>" + time + "</th>"
			+ "<th>" + order.totalSeats + "</th>"
			+ "<th><a href='index.html?user_id=" 
			+ order.captainUserId + "'>" + order.captainUserId + "</th>"
			+ "<th>" + order.status + "</th>"
			+ "<th>" + order.startSitePoint['address'] + "</th>"
			+ "<th>" + order.endSitePoint['address'] + "</th>"
			+ "<th>" + order.distance + "</th>"
			+ "</tr>" ;
		orderContents += orderContent;
	}
	orderContents += "</tbody>";
	return orderContents;
}

function constructUserContents (users) {
	var userContents = "<tbody>";
	for( var i = 0 ; i < users.length ; i++ ) { 
		var user = users[i];
		var userContent = "<tr> <th>" + user.id + "</th>"
						+ "<th><a href='orders.html?user_id=" 
							+ user.id + "'>" + user.username + "</th>"
						+ "<th>" + user.age + "</th>"
						+ "<th>" + user.gender + "</th>"
						+ "<th>" + user.nickname + "</th>"
						+ "<th>" + user.role + "</th>"
						+ "<th>" + user.job + "</th>"
						+ "<th>" + user.charactor + "</th>"
						+ "<th><button type='button' class='btn btn-warning' id='deleteOrderButton" 
							+ user.id + "'>Delete All</button></th>"
						+ "<th><button type='button' class='btn btn-danger' id='deleteUserButton" 
							+ user.id + "'>Delete</button></th>"
						+ "</tr>" ;
		userContents += userContent;
	}
	userContents += "</tbody>";
	return userContents;
}

var addUserContentsToDiv = function (contents, divName, document) {			
	var allUsersHtml =  "<table class='table table-bordered table-striped'>" 
						+ "<thead> <tr>"
						+ "<th>ID</th> <th>User Name</th> <th>Age</th> <th>Gender</th> "
						+ "<th>Nick Name</th> <th>Role</th> <th>Job</th> <th>Character</th>"
						+ "<th>Action On Order</th><th>Action On User</th></tr></thead>"
						+ contents + "</table>";
	var divUsers = document.getElementById(divName);	
	divUsers.innerHTML = allUsersHtml;
};

var constructJoinersItem = function (joinerContents) {
	var joinerItem = "<h2>Joiners</h2>" 
		+ "<table class='table table-bordered table-striped'>" 
		+ "<thead> <tr>"
		+ "<th>ID</th> <th>User Id</th> <th>Seat Count</th> <th>Status</th> "
		+ "<th>Fee</th> <th>Paid</th>"
		+ "</tr> </thead>"
		+ joinerContents + "</table>";
	return joinerItem;
};

function constructJoinersContent(joiners) {
	var joinerContents = "<tbody>";
	for( var i = 0 ; i < joiners.length ; i++ ) { 
		var joiner = joiners[i];
		var joinerUser = 0;
		if (joiner.userBean != null) {
			joinerUser = joiner.userBean.id;
		}
		var joinerContent = "<tr> <th>" + joiner.id + "</th>"
						+ "<th><a href='index.html?user_id=" 
						+ joinerUser + "'>" + joinerUser + "</th>"
						+ "<th>" + joiner.seatsCount + "</th>"
						+ "<th>" + joiner.status + "</th>"
						+ "<th>" + joiner.fee + "</th>"
						+ "<th>" + joiner.paid + "</th>"
						+ "</tr>" ;
		joinerContents += joinerContent;
	}
	joinerContents += "</tbody>";
	return joinerContents;
}

var constructSubjectItem = function (subjectContents) {
	var subjectItem = "<h2>Subjects</h2>" 
		+ "<table class='table table-bordered table-striped'>" 
		+ "<thead> <tr>"
		+ "<th>ID</th> <th>Gender</th> <th>Atmosphere</th> <th>Charactor</th> "
		+ "<th>job</th> </tr> </thead>"
		+ subjectContents + "</table>";
	return subjectItem;
};

function constructSubjectsContent(subjects) {
	var subjectContents = "<tbody>";
	for( var i = 0 ; i < subjects.length ; i++ ) { 
		var subject = subjects[i];
		var subjectContent = "<tr> <th>" + subject.id + "</th>"
						+ "<th>" + subject.gender + "</th>"
						+ "<th>" + subject.atmosphere + "</th>"
						+ "<th>" + subject.charactor + "</th>"
						+ "<th>" + subject.job + "</th>"
						+ "</tr>" ;
		subjectContents += subjectContent;
	}
	subjectContents += "</tbody>";
	return subjectContents;
}