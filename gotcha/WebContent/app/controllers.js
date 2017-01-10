gotcha.controller('mainController', ['$scope', '$timeout', '$location', 'restService', 'dataSharingService', function($scope, $timeout, $location, restService, dataSharingService) {
	// Check whether the HTTP Session is still alive
	restService.call('POST', 'welcome', {});
	// Get the location we're supposed to be in
	$timeout(function () {
		$scope.templateUrl = function () {
			var route = dataSharingService.get("route");
			return route !== undefined ? "app/views/" + route + ".html" : "app/views/login.html";
	}}, 1000);
	// watcher for templateUrl in order to update the url in address bar
	$scope.$watch(function () {
		return dataSharingService.get("route");
	}, function (newValue, oldValue) {
		$location.path(newValue);
	});
}])

// Login controller that uses 'restService' for restful call
.controller('loginController', ['$scope', '$timeout', '$location', 'authService', 'notifyService', function($scope, $timeout, $location, authService, notifyService) {
	
	$scope.login = function () {
		var user = {
			'username': $scope.username,
			'password': $scope.password
		};

		authService.login(user);
	}
}])

.controller('registerController', ['$scope', 'restService', function($scope, restService) {
	
}])

.controller('messagesController', ['$scope', 'dataSharingService', function($scope, dataSharingService) {
	$scope.userProfile = dataSharingService.get("user").profile;
}])

.controller('channelsListController', ['$scope', 'restService', 'dataSharingService', function($scope, restService, dataSharingService) {
	$scope.channels = dataSharingService.get("user").channels;
}])

.controller('directMessagesController', ['$scope', 'restService', 'dataSharingService', function($scope, restService, dataSharingService) {
	$scope.directMessages = dataSharingService.get("user").directMessages;
}])

.controller('chatController', ['$location', '$scope', 'restService', function($location, $scope, restService) {
	
}]);