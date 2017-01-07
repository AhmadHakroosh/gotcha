// Login controller that uses 'restService' for restful call
gotcha.controller('loginController', ['$scope', 'authService', function($scope, authService) {
	
	$scope.login = function () {
		var user = {
			'username': $scope.username,
			'password': $scope.password
		};

		user = authService.login(user);
		var userProfile = user.profile;
	}
}])

.controller('registerController', ['$scope', 'restService', function($scope, restService) {
	
}])

.controller('channelsController', ['$scope', 'restService', function($scope, restService) {
	
}])

.controller('directMessagesController', ['$scope', 'restService', function($scope, restService) {
	
}])

.controller('chatController', ['$scope', 'restService', function($scope, restService) {
	
}]);