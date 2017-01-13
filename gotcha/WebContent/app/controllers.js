// Main application controller
gotcha.controller('mainController', ['$scope', '$rootScope', '$location', '$http', function($scope, $rootScope, $location, $http) {
	
	$rootScope.route = "login";

	$http({
		method: 'POST',
		url: 'welcome',
		headers: {'Content-Type' : "application/json; charset=utf-8"},
		data: {}
	}).then(
		function (success) {
			$rootScope.route = success.data.route;
			$rootScope.user = success.data.user;
		},
		function (failure) {
			console.log(failure.data);
		}
	);

	$rootScope.$watch(function () {
		return $rootScope.route;
	}, function (newValue, oldValue) {
		$scope.templateUrl = "app/views/" + newValue + ".html";
		$location.path(newValue);
	});
}])
// Login controller that uses 'restService' for restful call
.controller('loginController', ['$scope', '$rootScope', '$timeout', '$http', 'notifyService', function($scope, $rootScope, $timeout, $http, notifyService) {
	
	$scope.login = function () {
		var user = {
			'username': $scope.username,
			'password': $scope.password
		};

		$http({
			method: 'POST',
			url: 'login/auth',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: user
		}).then(
			function (success) {
				var data = success.data;
				notifyService.alert({
					"status": data.status,
					"selector": data.notification.selector,
					"message": data.notification.message
				});
				$timeout(function () {
					$rootScope.route = data.route;
					$rootScope.user = data.user;
				}, 2500);
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	};
}])

.controller('registerController', ['$scope', '$rootScope', '$timeout', '$http', 'notifyService', function($scope, $rootScope, $timeout, $http, notifyService) {
	
	var checkAvailabality = function () {
		$scope.disabled = $scope.validUsername != "glyphicon glyphicon-ok-circle" || $scope.validNickname != "glyphicon glyphicon-ok-circle"
	};

	$scope.register = function () {
		var user = {
			"username": $scope.username,
			"password": $scope.password,
			"nickName": $scope.nickname,
			"description": $scope.description,
			"photoUrl": $scope.photoUrl
		};

		$http({
			method: 'POST',
			url: 'register',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: user
		}).then(
			function (success) {
				var data = success.data;
				notifyService.alert({
					"status": data.status,
					"selector": data.notification.selector,
					"message": data.notification.message
				});
				$timeout(function () {
					$(".modal-backdrop").css({display: 'none'});
					$rootScope.route = data.route;
					$rootScope.user = data.user;
				}, 2500);
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	};

	$scope.validateUsername = function () {
		$http({
			method: 'POST',
			url: 'validate',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: {"username": $scope.username}
		}).then(
			function (success) {
				$scope.validUsername = "glyphicon glyphicon-" + success.data.valid + "-circle";
				checkAvailabality();
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	}

	$scope.validateNickname = function () {
		$http({
			method: 'POST',
			url: 'validate',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: {"nickName": $scope.nickname}
		}).then(
			function (success) {
				$scope.validNickname = "glyphicon glyphicon-" + success.data.valid + "-circle";
				checkAvailabality();
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	}
}])

.controller('messagesController', ['$scope', '$http', '$timeout', '$rootScope', 'notifyService', function($scope, $http, $timeout, $rootScope, notifyService) {

	$scope.userProfile = $rootScope.user.profile;

	$scope.logout = function () {
		$http({
			method: 'POST',
			url: 'logout',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: {}
		}).then(
			function (success) {
				var data = success.data;
				notifyService.alert({
					"status": data.status,
					"selector": data.notification.selector,
					"message": data.notification.message
				});
				$timeout(function () {
					$rootScope.route = data.route;
				}, 2500);
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	}
}])

.controller('channelsListController', ['$scope', '$rootScope', function($scope, $rootScope) {
	$scope.section = "channels";
	$scope.channels = $rootScope.user.channels;
}])

.controller('directMessagesController', ['$scope', '$rootScope', function($scope, $rootScope) {
	$scope.section = "direct messages";
	$scope.directMessages = $rootScope.user.directMessages;
}])

.controller('chatController', ['$scope', function($scope) {
	
}]);