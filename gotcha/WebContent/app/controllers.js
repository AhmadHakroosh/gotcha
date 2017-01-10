// Login controller that uses 'restService' for restful call
gotcha.controller('loginController', ['$location', '$scope', 'authService', function($location, $scope, authService) {
	
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

.controller('channelsListController', ['$scope', 'restService', function($scope, restService) {
	
}])

.controller('directMessagesController', ['$scope', 'restService', function($scope, restService) {
	
}])

.controller('chatController', ['$location', '$scope', 'restService', function($location, $scope, restService) {
	
}]);