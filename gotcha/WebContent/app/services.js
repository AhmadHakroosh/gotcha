// Restful call service
gotcha.service('restService', ['$http', '$q', 'dataSharingService', function($http, $q, dataSharingService) {
	
	return {
		// Perform an Asynchronous callback with the specified URL
		call: function (method, url, input) {
			$http({
				method: method,
				url: url,
				headers: {
					'Content-Type' : "application/json; charset=utf-8"
				},
				data: input
			}).then(
				function (success) {
					dataSharingService.set("status", success.data.status !== undefined ? success.data.status : dataSharingService.get("status"));
					dataSharingService.set("route", success.data.route !== undefined ? success.data.route : dataSharingService.get("route"));
					dataSharingService.set("notification", success.data.notification !== undefined ? success.data.notification : dataSharingService.get("notification"));
					dataSharingService.set("user", success.data.user !== undefined ? success.data.user : dataSharingService.get("user"));
					dataSharingService.set("users", success.data.users !== undefined ? success.data.users : dataSharingService.get("users"));
			}, function (failure) {
				console.log("An unknown error has occured while trying to retrieve data from server...");
			});
		}
	};
}])
// Authentication system service
.service('authService', ['restService', function(restService) {
	
	var data = {};

	return {
		// Log the passed user in
		login: function (user) {
			restService.call('POST', 'login/auth', user);
		},
		// Log the passed user out
		logout: function (user) {
			restService.call('POST', 'logout', user);
		}
	}
}])
// Registeration system service
.service('registerService', ['restService', function(restService) {
	return {
		// Register the passed user to the system
		register: function (user) {
			restService.call('POST', 'register', user);
		},
		// Get users with the provided username (or) nickname
		getUsers: function () {
			restService.call('POST', 'getUsers', {});
		}
	}
}])
// Subscription system service
.service('subscriptionService', ['restService', function(restService) {
	return {
		// Subscribe the passed user to the passed channel
		subscribe: function (user, channel) {
			var data = {
				"user": user,
				"channel": channel
			};

			restService.call('POST', 'subscribe', data);
		},
		// Unsubscribe the passed user from the passed channel
		unsubscribe: function (user, channel) {
			var data = {
				"user": user,
				"channel": channel
			};

			restService.call('POST', 'unsubscribe', data);
		}
	}
}])
// Messaging system service
.service('messagingService', ['$location', '$rootScope', function($location, $rootScope) {
	
	return {
		// Open websocket
		create: function () {
			var user = $rootScope.user;
			var sessionUri = "ws://" + $location.host() + ":" + $location.port() + "/gotcha/" + user.profile.nickName;
			$rootScope.session = new WebSocket(sessionUri);
			// Define websocket methods
			$rootScope.session.onopen = function (event) {

			}
		}
	}
}])
// Channel management system service
.service('channelManagementService', ['restService', 'subscriptionService', function(restService, subscriptionService) {
	return {
		// Create a new channel
		createChannel: function (channel) {
			restService.call('POST', 'createChannel');
		},
		// Delete an existing channel
		deleteChannel: function (channel) {
			restService.call('POST', 'deleteChannel');
		},
		// Add a new user to an existing channel
		addUser: function (user, channel) {
			subscriptionService.subscribe(user, channel);
		},
		// Remove a user from an existing channel
		removeUser: function (user, channel) {
			subscriptionService.unsubscribe(user, channel);
		}
	}
}])
// Notification system service
.service('notifyService', ['$rootScope', '$timeout', 'dataSharingService', function ($rootScope, $timeout, dataSharingService) {
	return {
		alert: function (notification) {
			$(notification.selector).addClass("alert alert-" + notification.status).html(notification.message);
			$timeout(function () {
				$(notification.selector).removeClass("alert alert-" + notification.status).html("");
			}, 2500);
		}
	}
}])
// Service for data sharing over all scopes 
.service('dataSharingService', [function () {

	var data = {
			"route": "login"
	};

	return {
		get: function (key) {
			return data[key];
		},

		set: function (key, value) {
			data[key] = value;
		},
		
		log: function () {
			console.log(data);
		}
	}
}])
// System animations service
.service('animationService', ['$timeout', function ($timeout) {
	return {
		animate: function () {
			$("body").addClass('loading');
			$timeout(function () {
				$("body").removeClass('loading');
			});
		}
	}
}]);