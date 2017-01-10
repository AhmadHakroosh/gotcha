// Restful call service
gotcha.service('restService', ['$http', '$q', 'notifyService', function($http, $q, notifyService) {
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
				function (success) {
					console.log(success.data);
				},
				function (error) {
					console.log("An unknown error has occured while trying to retrieve data from server.")
				}
			);
		}
	};
}])
// Authentication system service
.service('authService', ['restService', function(restService) {
	return {
		// Log the passed user in
		login: function (user) {
			restService.call('POST', 'login/auth', user, "user");
		},
		// Log the passed user out
		logout: function (user) {
			restService.call('POST', 'logout', user, "user");
		}
	}
}])

.service('registerationService', ['restService', function(restService) {
	return {
		// Register the passed user to the system
		register: function (user) {
			restService.call('POST', 'register', user, "user");
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

			restService.call('POST', 'subscribe', data, "subscriptions");
		},
		// Unsubscribe the passed user from the passed channel
		unsubscribe: function (user, channel) {
			var data = {
				"user": user,
				"channel": channel
			};

			restService.call('POST', 'unsubscribe', data, "subscriptions");
		}
	}
}])
// Messaging system service
/*.service('messagingService', ['$rootScope', '$location', function($rootScope, $location) {
	$rootScope.chats = {};
	return {
		// Open chat window
		openChat: function (user) {
			if ($rootScope.chats[user] !== undefined && $rootScope.chats[user].readyState !== WebSocket.CLOSED) {
				return;
			}
			var chatUrl = "ws" + $location.$$absUrl.split("http")[1] + "@" + user;
			$rootScope.chats[user] = new WebSocket(chatUrl);
			
			/**
			 * Binding functions to the listeners of the websocket
			 */
			/*$rootScope.chats[user].onopen = function (event) {
				if (event.data === undefined) {
					return;
				}
				
				sendMessage(event.data);
			};
			
			$rootScope.chats[user].onmessage = function (event) {
				sendMessage(event.data);
			};
			
			$rootScope.chats[user].onclose = function (event) {
				sendMessage("Connection closed.");
			};
		},
		// Messages sending service
		sendMessage: function (message) {
			
		}
	}
}])*/
// Channel management system service
.service('channelManagementService', ['restService', 'subscriptionService', function(restService, subscriptionService) {
	return {
		// Create a new channel
		createChannel: function (channel) {
			restService.call('POST', 'createChannel', channel);
		},
		// Delete an existing channel
		deleteChannel: function (channel) {
			restService.call('POST', 'deleteChannel', channel);
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
.service('notifyService', function() {
	return {
		alert: function (selector, notification) {
			$(selector).html(notification);
		}
	}
})

.service('routingService', ['$location', '$timeout', function($location, $timeout) {
	return {
		route: function (location) {
			$timeout(function () {
				$location.$$absUrl = $location.$$absUrl.split("http://localhost:8080/gotcha/")[0] + location + "/";
			}, 1000);
		}
	}	
}])

.service('loadDataService', ['$rootScope', function($rootScope) {
	return {
		load: function (controllerScope, element, data) {
			$rootScope.controllerScope[element] = data;
		}
	}
}])