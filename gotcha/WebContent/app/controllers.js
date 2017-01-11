// Main application controller
gotcha.controller('mainController', ['$scope', '$timeout', '$location', 'restService', 'dataSharingService', function($scope, $timeout, $location, restService, dataSharingService) {
	
	$scope.route = "app/views/login.html";

	// Check whether the HTTP Session is still alive
	restService.call('POST', 'welcome', {});
	
	$scope.$watch(function () {
		return dataSharingService.get("route");
	}, function (newValue, oldValue) {
		$timeout(function () {
			$scope.route = "app/views/" + newValue + ".html";
			$location.path(newValue);
		}, 2000);
	});
	
	$scope.templateUrl = function () {
		return $scope.route;
	};
}])
// Login controller that uses 'restService' for restful call
.controller('loginController', ['$scope', '$timeout', '$location', 'authService', 'notifyService', 'dataSharingService', function($scope, $timeout, $location, authService, notifyService, dataSharingService) {
	
	$scope.login = function () {
		var user = {
			'username': $scope.username,
			'password': $scope.password
		};

		authService.login(user);
	};

	$scope.$watch(function () {
		return dataSharingService.get("notification");
	}, function (newValue, oldValue) {
		if (newValue !== oldValue) {
			var status = dataSharingService.get("status");
			notifyService.alert(newValue, status);
		}
	});
}])

.controller('registerController', ['$scope', 'registerationService', 'dataSharingService', 'notifyService', function($scope, registerationService, dataSharingService, notifyService) {
	
	$scope.register = function () {
		var user = {
			"username": $scope.username,
			"password": $scope.password,
			"nickName": $scope.nickname,
			"description": $scope.description,
			"photoUrl": $scope.photoUrl
		};

		registerationService.register(user);
	};

	$scope.$watch(function () {
		return dataSharingService.get("notification");
	}, function (newValue, oldValue) {
		if (newValue !== oldValue) {
			var status = dataSharingService.get("status");
			notifyService.alert(newValue, status);
		}
	});
}])

.controller('messagesController', ['$scope', 'dataSharingService', function($scope, dataSharingService) {
	$scope.userProfile = dataSharingService.get("user").profile;
}])

.controller('channelsListController', ['$scope', 'dataSharingService', function($scope, dataSharingService) {
	$scope.section = "channels";
	$scope.channels = dataSharingService.get("user").channels;
}])

.controller('directMessagesController', ['$scope', 'restService', 'dataSharingService', function($scope, restService, dataSharingService) {
	$scope.section = "direct messages";
	$scope.directMessages = dataSharingService.get("user").directMessages;
}])

.controller('chatController', ['$scope', function($scope) {
	
}]);