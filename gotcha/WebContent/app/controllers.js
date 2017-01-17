// Main application controller
gotcha.controller('mainController', ['$scope', '$rootScope', '$location', '$http', 'animationService', function($scope, $rootScope, $location, $http, animationService) {
	
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
.controller('loginController', ['$scope', '$rootScope', '$timeout', '$http', 'messagingService', 'notifyService', 'animationService', function($scope, $rootScope, $timeout, $http, messagingService, notifyService, animationService) {
	
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
				if (data.status == "success") {
					$timeout(function () {
						$rootScope.route = data.route;
						$rootScope.user = data.user;
						messagingService.create();
					}, 2500);
				}
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	};
}])

.controller('registerController', ['$scope', '$rootScope', '$timeout', '$http', '$filter', 'notifyService', 'animationService', function($scope, $rootScope, $timeout, $http, $filter, notifyService, animationService) {
	
	$scope.checkButton = function () {
		$scope.disabled = 
			$scope.validUsername != "glyphicon glyphicon-ok-circle" || $scope.validNickname != "glyphicon glyphicon-ok-circle"
			||
			$scope.username == "" || $scope.nickname == "" ||	$scope.password == ""
			||
			$scope.username == undefined || $scope.nickname == undefined ||	$scope.password == undefined;
	};

	$scope.register = function () {
		var user = {
			"username": $scope.username,
			"password": $scope.password,
			"nickName": $scope.nickname,
			"description": $scope.description,
			"photoUrl": $scope.photoUrl,
			"status" : "active",
			"lastSeen": $filter('date')(Date.now(), "dd/MM/yyyy HH:mm")
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
				if (data.status == "success") {
					$timeout(function () {
						$(".modal-backdrop").css({display: 'none'});
						$rootScope.route = data.route;
						$rootScope.user = data.user;
						messagingService.create();						
					}, 2500);
				}
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	};

	$scope.validateUsername = function () {
		if ($scope.username != "" && $scope.username !== undefined) {
			$http({
				method: 'POST',
				url: 'validate',
				headers: {'Content-Type' : "application/json; charset=utf-8"},
				data: {"username": $scope.username}
			}).then(
				function (success) {
					$scope.validUsername = "glyphicon glyphicon-" + success.data.valid + "-circle";
					$scope.checkButton();
				},
				function (failure) {
					console.log(failure.data);
				}
			);
		} else {
			$scope.validUsername = "";
		}
	};

	$scope.validateNickname = function () {
		if ($scope.nickname != "" && $scope.nickname !== undefined) {
			$http({
				method: 'POST',
				url: 'validate',
				headers: {'Content-Type' : "application/json; charset=utf-8"},
				data: {"nickName": $scope.nickname}
			}).then(
				function (success) {
					$scope.validNickname = "glyphicon glyphicon-" + success.data.valid + "-circle";
					$scope.checkButton();
				},
				function (failure) {
					console.log(failure.data);
				}
			);
		} else {
			$scope.validNickname = "";
		}
	};

	$scope.disabled = true;
}])

.controller('messagesController', ['$scope', '$http', '$timeout', '$rootScope', 'messagingService', 'notifyService', function($scope, $http, $timeout, $rootScope, messagingService, notifyService) {

	$scope.user = $rootScope.user;
	// Send button is disabled by default
	$scope.$watch(function () {
		return $scope.inputMessage;
	}, function (newValue, oldValue) {
		if (newValue === undefined || newValue == "") {
			$(".glyphicon-send").css("color", "lightgrey");
		} else {
			$(".glyphicon-send").css("color", "#007AB8");
		}
	});
	$scope.showDropdown = false;
	$scope.oppositeStatus = $scope.user.status == "active" ? "away" : "active";
	
	(function () {
		$("#main-activity-window").height($(window).height() - $("#top-header").height());
		$("#activity-window").height(0.9 * $("#main-activity-window").height());
		$("#typing-area").height(0.1 * $("#main-activity-window").height());
		$("#main-activity-window .sidebar").height($("#main-activity-window").height());
	})();
	
	// Toggle dropdown menu
	$scope.toggleShow = function () {
		$scope.showDropdown = !$scope.showDropdown;
	};
	// Logout from the system
	$scope.logout = function () {
		$http({
			method: 'POST',
			url: 'logout',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: {}
		}).then(
			function (success) {
				var data = success.data;
				$rootScope.route = data.route;
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	};
	// Update typing area send button
	$scope.checkButton = function () {
		if ($scope.chatInput !== undefined || $scope.chatInput != "") {
			$scope.disabled = false;
		} else {
			$scope.disabled = true;
		}
	};
	// Send message
	$scope.send = function () {
		messagingService.send($scope.chatInput);
		$scope.chatInput = "";
	}
}])

.controller('chatController', ['$scope', function($scope) {
	
}]);