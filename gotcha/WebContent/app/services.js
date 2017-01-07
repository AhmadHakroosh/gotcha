// Restful call service
gotcha.service('restService', ['$http', '$q', function($http, $q) {
	
	return {
		// Perform an Asynchronous callback with the specified URL
		call: function (method, url, data, model) {

			return $http({
				method: method,
				url: url,
				headers: {
					'Content-Type' : "application/json; charset=utf-8"
				},
				data: data
			}).then(
				function (response) {
					var gotchaData = localStorage.getItem("gotchaData");

					if (gotchaData == null) {
						gotchaData = {};
						gotchaData[model] = response.data;
						localStorage.setItem("gotchaData", JSON.stringify(gotchaData));
					} else {
						gotchaData = JSON.parse(gotchaData);
						gotchaData[model] = response.data;
						localStorage.setItem("gotchaData", JSON.stringify(gotchaData));
					}
				},
				function (response) {
					console.log("An unknown error has occured while trying to retrieve data from server.")
				}
			);
		},

		data: function () {
			var gotchaData = localStorage.getItem("gotchaData");
			gotchaData = JSON.parse(gotchaData);
			return gotchaData.user;
		}
	};
}])

.service('authService', ['restService', function(restService) {
	return {
		'login': function (user) {
			var data = {
				"user": user
			};

			restService.call('POST','login', user, "user");
			return restService.data();
		},
		
		'logout': function (user) {
			
		}
	}
}])

.service('register', ['restService', function(restService) {

}])

.service('subscribe', ['restService', function(restService) {
	
}])

.service('unsubscribe', ['restService', function(restService) {
	
}])
.service('sendMessage', ['restService', function(restService) {
	
}])

.service('createChannel', ['restService', function(restService) {
	
}]);