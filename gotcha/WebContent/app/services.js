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
					dataSharingService.set("status", success.data.status);
					dataSharingService.set("route", success.data.route);
					dataSharingService.set("notification", success.data.notification);
					dataSharingService.set("user", success.data.user);
					dataSharingService.set("valid", success.data.valid);
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
.service('registerationService', ['restService', function(restService) {
	return {
		// Register the passed user to the system
		register: function (user) {
			restService.call('POST', 'register', user);
		},
		// Get users with the provided username (or) nickname
		checkExistance: function (user) {
			restService.call('POST', 'validate', user);
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
.service('messagingService', ['$location', 'dataSharingService', function($location, dataSharingService) {
	
	$scope.chats = {};
	$scope.currentChat = {};

	return {
		// Add a new chat connection (websocket)
		openChat: function (user) {
			if ($scope.chats[user] === undefined) {
				$scope.chats[user] = new websocket("ws://" + $location.absUrl() + "/@" + user);
			}
			// Return the current open chat
			$scope.currentChat = $scope.chats[user];
			return $scope.currentChat;
		},
		// Send message to the current chat
		sendMessage: function (user, message) {

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
		alert: function () {
			$rootScope.$watch(function () {
				return dataSharingService.get("notification") !== undefined && dataSharingService.get("status") !== undefined;
			}, function (newValue, oldValue) {
				if (newValue) {
					var notification = dataSharingService.get("notification");
					var status = dataSharingService.get("status");
					$(notification.selector).addClass('alert alert-' + status).html(notification.message);
					$timeout(function () {
						$(notification.selector).removeClass('alert alert-' + status).html("");
					}, 3000);
				}
			});
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
		animate: function (id, className) {
			$("#" + id).addClass(className).css({
				transform: 'scale(1)'
			});

			$timeout(function () {
				$("#" + id).removeClass(className).css({
					transform: ''
				});
			}, 3000);
		}
	}
}]);