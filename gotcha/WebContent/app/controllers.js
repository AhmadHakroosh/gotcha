gotcha.controller('mainController', ['$scope', '$rootScope', '$timeout', '$location', 'restService', function($scope, $rootScope, $timeout, $location, restService) {
	
	restService.call('POST', 'welcome', {});

	$timeout(function () {
		if ($rootScope.route == "login") {
			$scope.templateUrl = "app/views/login.html";
			$location.path("login");
		} else {
			$scope.templateUrl = "app/views/main.html";
			$location.path("messages");
		}
	}, 1000);
}])

// Login controller that uses 'restService' for restful call
.controller('loginController', ['$rootScope', '$scope', '$timeout', '$location', 'authService', 'notifyService', 'routingService', function($rootScope, $scope, $timeout, $location, authService, notifyService, routingService) {
	
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

.controller('channelsListController', ['$rootScope', '$scope', 'restService', function($rootScope, $scope, restService) {
	$scope.channels = $rootScope.data.user.channels;
}])

.controller('directMessagesController', ['$scope', 'restService', function($scope, restService) {
	
}])

.controller('chatController', ['$location', '$scope', 'restService', function($location, $scope, restService) {
	
}]);