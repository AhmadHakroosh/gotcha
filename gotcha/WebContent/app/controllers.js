// Main application controller
gotcha.controller('mainController', ['$scope', '$timeout', '$location', 'restService', 'dataSharingService', function($scope, $timeout, $location, restService, dataSharingService) {
	
	$scope.route = "app/views/login.html";

	// Check whether the HTTP Session is still alive
	restService.call('POST', 'welcome', {});
	
	$scope.$watch(function () {
		return dataSharingService.get("route");
	}, function (newValue, oldValue) {
		if (newValue !== oldValue) {
			$scope.route = "app/views/" + newValue + ".html";
			$location.path(newValue);
		}
	});
	
	$scope.templateUrl = function () {
		return $scope.route;
	};
}])
// Login controller that uses 'restService' for restful call
.controller('loginController', ['$scope', '$timeout', '$location', 'authService', 'notifyService', 'dataSharingService', 'animationService', function($scope, $timeout, $location, authService, notifyService, dataSharingService, animationService) {
	
	$scope.login = function () {
		var user = {
			'username': $scope.username,
			'password': $scope.password
		};

		authService.login(user);
	};

	notifyService.alert();
}])

.controller('registerController', ['$scope', '$timeout', 'registerationService', 'dataSharingService', 'notifyService', 'animationService', function($scope, $timeout, registerationService, dataSharingService, notifyService, animationService) {
	
	$scope.validUsername = "ok";
	$scope.validNickname = "ok";
	$scope.disabled = false;

	$scope.register = function () {
		var user = {
			"username": $scope.username,
			"password": $scope.password,
			"nickName": $scope.nickname,
			"description": $scope.description,
			"photoUrl": $scope.photoUrl
		};

		$(".modal-backdrop").css({
			display: 'none'
		});
		registerationService.register(user);
	};

	$scope.validateUsername = function (username) {
		var user = {
			"username": username
		};

		registerationService.checkExistance(user);
		$timeout(function () {
			$scope.validUsername = "glyphicon glyphicon-" + dataSharingService.get("valid") + "-circle";
		}, 1000);
	};

	$scope.validateNickname = function (nickname) {
		var user = {
			"nickName": nickname
		};

		registerationService.checkExistance(user);
		$timeout(function () {
			$scope.validNickname = "glyphicon glyphicon-" + dataSharingService.get("valid") + "-circle";
		}, 1000);
	};

	$scope.$watch(function () {
		return $scope.validUsername == "glyphicon glyphicon-ok-circle" && $scope.validNickname == "glyphicon glyphicon-ok-circle";
	}, function (newValue, oldValue) {
		$scope.disabled = newValue;
		console.log($scope.disabled);
	});

	notifyService.alert();
}])

.controller('messagesController', ['$scope', 'authService','dataSharingService', 'notifyService', 'animationService', function($scope, authService, dataSharingService, notifyService, animationService) {

	$scope.userProfile = dataSharingService.get("user").profile;

	$scope.logout = function () {
		authService.logout();
	}

	notifyService.alert();
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