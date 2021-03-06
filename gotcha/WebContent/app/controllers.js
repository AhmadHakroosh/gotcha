// Main application controller
gotcha.controller('mainController', ['$scope', '$rootScope', '$location', '$http', '$filter', 'animationService', function($scope, $rootScope, $location, $http, $filter, animationService) {
	
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
			if (success.data.user !== undefined) {
				$rootScope.user.lastSeen = $filter('date')(Date.now(), "MMM dd,yyyy HH:mm:ss");
			}
			$rootScope.channels = success.data.channels;
			$rootScope.directMessages = success.data.directMessages;
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
	
	$("body").css({
		"background": "url(assets/images/login.png)",
		"background-size": "cover"
	});

	$scope.login = function () {
		var user = {
			"username": $scope.username,
			"password": $scope.password
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
						$rootScope.channels = data.channels;
						$rootScope.directMessages = data.directMessages;
					}, 2500);
				}
			},
			function (failure) {
				console.log(failure.data);
			}
		);
	};
}])

.controller('registerController', ['$scope', '$rootScope', '$timeout', '$http', '$filter', 'messagingService', 'notifyService', 'animationService', function($scope, $rootScope, $timeout, $http, $filter, messagingService, notifyService, animationService) {
	// Scope variables
	$scope.disabled = true;

	// Scope methods
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
			"photoUrl": $scope.photoUrl !== undefined ? $scope.photoUrl : "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSMyKQ_ZaKgbgQ6PE--NyftpawhbFDuv0lIZAslbH_o5QVS3KY9wHo87AqxyQ",
			"status" : "active",
			"lastSeen": $filter('date')(Date.now(), "MMM dd,yyyy HH:mm:ss")
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
						$rootScope.channels = data.channels;
						$rootScope.directMessages = data.directMessages;		
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
}])

.controller('messagesController', ['$document', '$scope', '$http', '$timeout', '$rootScope', '$location', '$filter', '$interval', 'messagingService', 'notifyService', function($document, $scope, $http, $timeout, $rootScope, $location, $filter, $interval, messagingService, notifyService) {
	
	$("body").css({
		"background": "none",
	});

	$scope.mentions = 0;
	$scope.length = function (object) {
		return Object.keys(object).length;
	}
	
	// Scope variables
	$scope.user = $rootScope.user;
	$scope.channels = {};
	$scope.directMessages = {};
	$scope.threads = {};
	$scope.activeChat;
	$scope.activeThread;
	$scope.showDropdown = false;
	$scope.showSubscribersList = false;
	$scope.oppositeStatus;
	$scope.disableChannelCreation = true;
	$scope.found = {
		"status": false,
		"channels": {},
		"users": {}
	};
	
	var pack = function () {
		var to;
		if ($scope.isChannel) to = $scope.activeChat.name;
		if ($scope.isDirectMessage) to = $scope.activeChat.user.nickName;
		var message = {
			"parentId": 0,
			"from": {
				"nickName": $scope.user.nickName,
				"description": $scope.user.description,
				"status": $scope.user.status,
				"lastSeen": $scope.user.lastSeen,
				"photoUrl": $scope.user.photoUrl
			},
			"to": to,
			"text": $scope.inputMessage,
			"lastUpdate": $filter('date')(Date.now(), "MMM dd,yyyy HH:mm:ss"),
			"time": $filter('date')(Date.now(), "MMM dd,yyyy HH:mm:ss")
		};

		return JSON.stringify(message);
	};

	var packReply = function () {
		var to;
		if ($scope.isChannel) to = $scope.activeChat.name;
		if ($scope.isDirectMessage) to = $scope.activeChat.user.nickName;
		var reply = {
			"parentId": $scope.activeThread.id,
			"from": {
				"nickName": $scope.user.nickName,
				"description": $scope.user.description,
				"status": $scope.user.status,
				"lastSeen": $scope.user.lastSeen,
				"photoUrl": $scope.user.photoUrl
			},
			"to": to,
			"text": $scope.inputReply,
			"lastUpdate": $filter('date')(Date.now(), "MMM dd,yyyy HH:mm:ss"),
			"time": $filter('date')(Date.now(), "MMM dd,yyyy HH:mm:ss")
		};

		return JSON.stringify(reply);
	};

	var unpack = function (json) {
		var message = JSON.parse(json);
		// Check for mention
		if (message.text.indexOf("@" + $scope.user.nickName) != -1) {
			$scope.mentions += 1;
			message.mention = true;
		} else {
			message.mention = false;
		}

		var scrollPos = $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("scrollTop") - $("#chat-console").prop("clientHeight");
		var threadScrollPos = $("#thread-console").prop("scrollHeight") - $("#thread-console").prop("scrollTop") - $("#thread-console").prop("clientHeight");
		
		$scope.threads[message.id] = message;

		if ($scope.channels[message.to] != undefined) {
			if (message.parentId == 0) {
				$scope.channels[message.to].messages[message.id] = message;
				message.isMain = true;
			} else {
				$scope.threads[message.parentId].replies[message.id] = message;
				$scope.threads[message.parentId].lastUpdate = message.time;
				$scope.threads[message.parentId].lastReply = message;
				$scope.threads[message.parentId].repliesCount += 1;
			}
			message.replies = {};
			message.repliable = true;
			message.repliesCount = 0;
			$scope.channels[message.to].newMessages += 1;
			$scope.channels[message.to].mentions += message.mention ? 1 : 0;
		} else {
			if (message.to == $scope.user.nickName && $scope.directMessages[message.from.nickName] !== undefined) {
				if (message.parentId == 0) {
					$scope.directMessages[message.from.nickName].messages[message.id] = message;
					message.isMain = true;
				} else {
					$scope.threads[message.parentId].replies[message.id] = message;
					$scope.threads[message.parentId].lastUpdate = message.time;
					$scope.threads[message.parentId].lastReply = message;
					$scope.threads[message.parentId].repliesCount += 1;
				}
				message.replies = {};
				message.repliable = true;
				message.repliesCount = 0;
				$scope.directMessages[message.from.nickName].newMessages += 1;
				$scope.directMessages[message.from.nickName].mentions += message.mention ? 1 : 0;
			} else if (message.to == $scope.user.nickName && $scope.directMessages[message.from.nickName] == undefined) {
				if (message.parentId == 0) {
					getDirectMessageData(message.from.nickName);
					message.isMain = true;
				} else {
					$scope.threads[message.parentId].replies[message.id] = message;
					$scope.threads[message.parentId].lastUpdate = message.time;
					$scope.threads[message.parentId].lastReply = message;
					$scope.threads[message.parentId].repliesCount += 1;
				}
				message.replies = {};
				message.repliesCount = 0;
				message.repliable = true;
			} else {
				if (message.parentId == 0) {
					$scope.directMessages[message.to].messages[message.id] = message;
					message.isMain = true;
				} else {
					$scope.threads[message.parentId].replies[message.id] = message;
					$scope.threads[message.parentId].lastUpdate = message.time;
					$scope.threads[message.parentId].lastReply = message;
					$scope.threads[message.parentId].repliesCount += 1;
				}
				message.replies = {};
				message.repliesCount = 0;
				message.repliable = true;
				$scope.directMessages[message.to].mentions += message.mention ? 1 : 0;
			}
		}

		if (scrollPos == 0) {
			$scope.activeChat.newMessages = 0;
			$scope.mentions -= $scope.activeChat.mentions;
			$scope.activeChat.mentions = 0;
			$timeout(function () {
				$("#chat-console").animate({scrollTop: $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight")}, 500);
			}, 1);
		}

		if ($scope.activeThread && threadScrollPos == 0) {
			$scope.activeChat.newMessages = 0;
			$scope.mentions -= $scope.activeChat.mentions;
			$scope.activeChat.mentions = 0;
			$timeout(function () {
				$("#thread-console").animate({scrollTop: $("#thread-console").prop("scrollHeight") - $("#thread-console").prop("clientHeight")}, 500);
			}, 1);
		}

		$scope.$apply();
	};

	// Ifi functions
	(function () {
		$("#main-activity-window").height($(window).height() - $("#top-header").height());
		$("#activity-window").height(0.9 * $("#main-activity-window").height());
		$("#typing-area").height(0.1 * $("#main-activity-window").height());
		$("#active-thread").height($("#main-activity-window").height());
		$("#main-activity-window .sidebar").height($("#main-activity-window").height());
	})();
	
	$("#chat-console").on('scroll', function () {
		if (this.scrollTop <= 0) {
			if ($scope.isChannel) {
				getTenChannelMessages($scope.activeChat.name);
			} else {
				getTenDirectChatMessages($scope.activeChat.user.nickName);
			}
		}

		if (this.scrollTop == $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight")) {
			$scope.activeChat.newMessages = 0;
			$scope.activeChat.mentions = 0;
			$scope.viewLastMessages = false;
		}

		if (this.scrollTop < $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight") - 50) {
			$scope.viewLastMessages = true;
		}
	});
	
	$("#thread-console").on('scroll', function () {
		if (this.scrollTop <= 0) {
			$scope.getTenThreadMessages($scope.activeThread);
		}

		if (this.scrollTop == $("#thread-console").prop("scrollHeight") - $("#thread-console").prop("clientHeight")) {
			$scope.activeChat.newMessages = 0;
			$scope.activeChat.mentions = 0;
		}
	});

	$("#mobile-menu-icon").on("click", function () {
		$(".sidebar").toggleClass('hidden-xs').toggleClass('col-xs-10');
		$("#top-header #main-header").css({'position':'absolute', 'z-index':'1000'});
		$("#activity-window").css({'position':'absolute', 'z-index':'0'});
	});

	var user = $scope.user;
	var sessionUri = "ws://" + $location.host() + ":" + $location.port() + "/gotcha/" + user.nickName;
	$scope.session = new WebSocket(sessionUri);
	// Define websocket methods
	// On connection open
	$scope.session.onopen = function (event) {
		console.log("Connected to server...");
	};
	// On received message
	$scope.session.onmessage = function (event) {
		unpack(event.data);
	};
	// On error
	$scope.session.onerror = function (event) {
		notify("Error: " + event.data);
	};
	// On connection close
	$scope.session.onclose = function (event) {
		$rootScope.session = null;
		console.log("disconnected from server...");
	};

	// Send message
	$scope.send = function () {
		var message = pack();
		$scope.session.send(message);
		$timeout(function () {
			$("#chat-console").animate({scrollTop: $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight")}, 500);
			$scope.activeChat.newMessages = 0;
		}, 2);
		$scope.inputMessage = undefined;
	};

	$scope.reply = function () {
		var reply = packReply();
		$scope.session.send(reply);
		$timeout(function () {
			$("#thread-console").animate({scrollTop: $("#thread-console").prop("scrollHeight") - $("#thread-console").prop("clientHeight")}, 500);
			$scope.activeChat.newMessages = 0;
		}, 2);
		$scope.inputReply = "@" + $scope.activeThread.from.nickName + ": ";
	};

	$scope.close = function () {
		$scope.session.close();
	};

	// Scope watchers
	$scope.$watch(function () {
		return $scope.user.status;
	}, function (newValue, oldValue) {
		$scope.oppositeStatus = newValue == "active" ? "away" : "active";
	});
	
	$scope.$watch(function () {
		if ($scope.channelName !== undefined && $scope.channelName !== "" && /^[a-zA-Z\d\-_.,]+$/.test($scope.channelName) && $scope.valid == "glyphicon glyphicon-ok-circle") {
			$scope.disableChannelCreation = false;
		} else {
			$scope.disableChannelCreation = true;
		}
	});
	
	// Scope event binding	
	$("#profile-dropdown-menu-toggle").bind('click', function(event) {
		event.stopPropagation();
	});

	$document.bind('click', function () {
		$scope.showDropdown = false;
		$scope.showSubscribersList = false;
		$scope.$apply();
	});

	// Toggle dropdown menu
	$scope.toggleShow = function () {
		$scope.showDropdown = !$scope.showDropdown;
	};

	$scope.showSubscribers = function () {
		$scope.showSubscribersList = true;
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
				$scope.close();
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

	// User status update
	$scope.changeStatus = function () {
		var user = {
			"status": $scope.oppositeStatus
		};

		$http({
			method: 'POST',
			url: 'setStatus',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: user
		}).then(
			function (success) {
				$scope.user.status = $scope.oppositeStatus;
			},
			function (failure) {
				console.log("cannot change status!");
			}
		);
	};
	
	// Channel name validation
	$scope.validate = function () {
		if ($scope.channelName !== undefined && $scope.channelName != "") {
			$http({
				method: 'POST',
				url: 'validate',
				headers: {'Content-Type' : "application/json; charset=utf-8"},
				data: {"name": $scope.channelName}
			}).then(
				function (success) {
					if (/^[a-zA-Z\d\-_,]+$/.test($scope.channelName)) {
						$scope.valid = "glyphicon glyphicon-" + success.data.valid + "-circle";
					} else {
						$scope.valid = "glyphicon glyphicon-ban-circle";
					}
				},
				function (failure) {
					console.log(failure.data);
				}
			);
		} else {
			$scope.valid = "";
		}
	}

	// Channel creation method
	$scope.createChannel = function () {
		var channel = {
			"name": $scope.channelName,
			"description": $scope.channelDescription,
			"createdBy": $scope.user.username,
			"createdTime": $filter('date')(Date.now(), "MMM dd,yyyy HH:mm:ss")
		};

		$http({
			method: 'POST',
			url: 'createChannel',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: channel
		}).then(
			function (success) {
				var data = success.data;
				notifyService.alert({
					"status": data.status,
					"selector": data.notification.selector,
					"message": data.notification.message
				});
				if (data.status == "success") {
					$scope.subscribe(data.channel.name);
					$timeout(function () {
						$("#create-channel-form").css({display: 'none'});
						$(".modal-backdrop").css({display: 'none'});
						$rootScope.route = data.route;
						$scope.channelName = "";
						$scope.channelDescription = "";
						$scope.valid = "";
					}, 1500);
				}
			},
			function (failure) {
				console.log("Cannot create channel!");
			}
		);
	};

	// Subscribe to the given channel
	$scope.subscribe = function (channel) {
		
		if ($scope.channels[channel] !== undefined) return;
		
		var subscription = {
			"nickname": $scope.user.nickName,
			"channel": channel
		};

		$http({
			method: 'POST',
			url: 'subscribe',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: subscription
		}).then(
			function (success) {
				var data = success.data;
				notifyService.alert({
					"status": data.status,
					"selector": data.notification.selector,
					"message": data.notification.message
				});
				if (data.status == "success") {
					$rootScope.route = data.route;
					getChannelData(data.subscription.channel);
					$timeout(function () {
						$scope.openChannel(data.subscription.channel);
					}, 1000);
				}
			},
			function (failure) {
				console.log("Cannot subscribe to channel!");
			}
		);
	};
	
	// Ubsubscribe from the given channel
	$scope.unsubscribe = function (channel) {
		if (channel === undefined || channel == "") return;
		
		var subscription = {
				"nickname": $scope.user.nickName,
				"channel": channel
		};
		
		$http({
			method: 'POST',
			url: 'unsubscribe',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: subscription
		}).then(
			function (success) {
				var data = success.data;
				notifyService.alert({
					"status": data.status,
					"selector": data.notification.selector,
					"message": data.notification.message
				});
				if (data.status == "success") {
					delete $scope.channels[channel];
					// Open another channel/direct-message
					if ($scope.length($scope.channels) > 0) $scope.openChannel(Object.keys($scope.channels)[0]);
					else if ($scope.length($scope.directMessages) > 0) $scope.openDirectMessage(Object.keys($scope.directMessages)[0]);
					
					$timeout(function () {
						$rootScope.route = data.route;					
					}, 1000);
				}
			},
			function (failure) {
				console.log("Cannot unsubscribe from the channel!");
			}
		);

				
	};
	
	// Open channel chat method
	$scope.openChannel = function (channel) {
		$scope.closeThread();
		$("#channels-list li").removeClass("active-chat");
		$("#direct-messages-list li").removeClass("active-chat");
		$("#channel-" + channel).addClass("active-chat");
		$(".sidebar").addClass('hidden-xs').removeClass('col-xs-10');
		$("#top-header #main-header").css({'position':'', 'z-index':'1001'});
		$("#activity-window").css({'position':'relative', 'z-index':'0'});
		$scope.isChannel = true;
		$scope.isDirectMessage = false;
		$scope.activeChat = $scope.channels[channel];
		$scope.channels[channel].newMessages = 0;
		$scope.mentions -= $scope.channels[channel].mentions;
		$scope.channels[channel].mentions = 0;
		$scope.channels[channel].lastRead = Date.now();
		$scope.query = undefined;
		$timeout(function () {
			$("#chat-console").animate({scrollTop: $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight")}, 100);
		}, 2);
	};
	
	// Open direct chat method
	$scope.openDirectMessage = function (nickname) {
		$scope.closeThread();
		$(".sidebar").addClass('hidden-xs').removeClass('col-xs-10');
		$("#top-header #main-header").css({'position':'', 'z-index':'1001'});
		$("#activity-window").css({'position':'relative', 'z-index':'0'});
		if ($scope.directMessages[nickname] == undefined) {
			getDirectMessageData(nickname);
			$timeout(function () {
				$("#channels-list li").removeClass("active-chat");
				$("#direct-messages-list li").removeClass("active-chat");
				$("#message-" + nickname).addClass("active-chat");
				$scope.isChannel = false;
				$scope.isDirectMessage = true;
				$scope.activeChat = $scope.directMessages[nickname];
				$scope.directMessages[nickname].newMessages = 0;
				$scope.mentions -= $scope.directMessages[nickname].mentions;
				$scope.directMessages[nickname].mentions = 0;
				$scope.directMessages[nickname].lastRead = Date.now();
				$timeout(function () {
					$("#chat-console").animate({scrollTop: $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight")}, 100);
				}, 2);
			}, 1000);
		} else {
			$("#channels-list li").removeClass("active-chat");
			$("#direct-messages-list li").removeClass("active-chat");
			$("#message-" + nickname).addClass("active-chat");
			$scope.isChannel = false;
			$scope.isDirectMessage = true;
			$scope.activeChat = $scope.directMessages[nickname];
			$scope.directMessages[nickname].newMessages = 0;
			$scope.mentions -= $scope.directMessages[nickname].mentions;
			$scope.directMessages[nickname].mentions = 0;
			$scope.directMessages[nickname].lastRead = Date.now();
			$timeout(function () {
				$("#chat-console").animate({scrollTop: $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight")}, 100);
			}, 2);
		}
		
		$scope.query = undefined;
	};
	
	// Retrieve given channel data
	var getChannelData = function (name) {
		if (name === undefined || name == "") return;
		
		var channel = {
				"name": name
		};
		
		$http({
			method: 'POST',
			url: 'channelData',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: channel
		}).then(
			function (success) {
				$scope.channels[name] = success.data;
				$scope.channels[name].messages = {};
				$scope.channels[name].newMessages = 0;
				$scope.channels[name].mentions = 0;
				$scope.channels[name].lastRead = $scope.user.lastSeen;
				getTenChannelMessages(name);
			},
			function (failure) {
				console.log("Error while trying to retrive channel data.");
			}
		);
	};
	
	// Retrieve given channel messages
	var getTenChannelMessages = function (channel) {
		if (channel === undefined || channel == "") return;
		
		var message = {
			"id": $scope.length($scope.channels[channel].messages),
			"to": channel,
			"time": $scope.user.signedUp
		};

		$http({
			method: 'POST',
			url: 'getTenChannelMessages',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: message
		}).then(
			function (success) {
				var scrollPos = $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("scrollTop") - $("#chat-console").prop("clientHeight");
				success.data.forEach(function (message) {
					$scope.getLastThreadMessage(message);
					$scope.getMessageRepliesNumber(message);
					$scope.threads[message.id] = message;
					message.from = JSON.parse(message.from);
					message.repliable = $scope.channels[channel].subscribers[message.from.nickName] !== undefined ? true : false;
					message.replies = {};
					message.isMain = true;
					$scope.channels[channel].messages[message.id] = message;
					var messageTime = Date.parse(message.lastUpdate);
					var lastRead = Date.parse($scope.channels[channel].lastRead);
					if (messageTime >= lastRead && message.from != $scope.user.nickName) {
						$scope.channels[channel].newMessages += 1;
						if (message.text.indexOf("@" + $scope.user.nickName) != -1) {
							$scope.mentions += 1;
							$scope.channels[channel].mentions += 1;
						}
					}
					if (message.text.indexOf("@" + $scope.user.nickName) != -1) {
						message.mention = true;
					}
				});

				if ($scope.activeChat == $scope.channels[channel] && success.data.length > 0) {
					$timeout(function () {
						$("#chat-console").scrollTop($("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight") - scrollPos);
					}, 1);
				}
			},
			function (failure) {
				console.log("Error while retrieving channel messages.");
			}
		);
	};
	
	// Retrieve user data
	var getDirectMessageData = function (nickname) {
		if (nickname === undefined || nickname == "") return;
		
		var user = {
				"nickName": nickname
		};
		
		$http({
			method: 'POST',
			url: 'getDirectMessageData',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: user
		}).then(
			function (success) {
				$scope.directMessages[nickname] = success.data;
				$scope.directMessages[nickname].messages = {};
				$scope.directMessages[nickname].newMessages = 0;
				$scope.directMessages[nickname].mentions = 0;
				$scope.directMessages[nickname].lastRead = $scope.user.lastSeen;
				getTenDirectChatMessages(nickname);
			},
			function (failure) {
				console.log("Error while trying to retrive channel data.");
			}
		);
	};
	
	// Retrieve given channel messages
	var getTenDirectChatMessages = function (nickname) {
		if (nickname === undefined || nickname == "") return;
		
		var message = {
			"id": $scope.length($scope.directMessages[nickname].messages),
			"from": nickname,
			"to": $scope.user.nickName
		};

		$http({
			method: 'POST',
			url: 'getTenDirectChatMessages',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: message
		}).then(
			function (success) {
				var scrollPos = $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("scrollTop") - $("#chat-console").prop("clientHeight");
				success.data.forEach(function (message) {
					$scope.getLastThreadMessage(message);
					$scope.getMessageRepliesNumber(message);
					$scope.threads[message.id] = message;
					message.from = JSON.parse(message.from);
					message.repliable = true;
					message.replies = {};
					message.isMain = true;
					$scope.directMessages[nickname].messages[message.id] = message;
					var messageTime = Date.parse(message.lastUpdate);
					var lastRead = Date.parse($scope.directMessages[nickname].lastRead);
					if (messageTime >= lastRead && message.from != $scope.user.nickName) {
						$scope.directMessages[nickname].newMessages += 1;
						if (message.text.indexOf("@" + $scope.user.nickName) != -1) {
							$scope.mentions += 1;
							$scope.directMessages[nickname].mentions += 1;
						}
					}
					if (message.text.indexOf("@" + $scope.user.nickName) != -1) {
						message.mention = true;
					}
				});

				if ($scope.activeChat == $scope.directMessages[nickname] && success.data.length > 0) {
					$timeout(function () {
						$("#chat-console").scrollTop($("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight") - scrollPos);
					}, 1);
				}
			},
			function (failure) {
				console.log("Error while retrieving direct messages.");
			}
		);
	};

	$scope.openThread = function (message) {
		$scope.activeThread = message;
		$("#activity-window").fadeIn('slow', function() {
			$("#activity-window").removeClass("col-lg-10 col-md-10 col-sm-9");
			$("#activity-window").addClass("col-lg-6 col-md-6 col-sm-5");
			$("#typing-area").removeClass("col-lg-10 col-md-10 col-sm-9");
			$("#typing-area").addClass("col-lg-6 col-md-6 col-sm-5");
			$("#active-thread").addClass("col-lg-4 col-md-4 col-sm-4 col-xs-10");
			if ($(window).width() < 768) {
				$("#activity-window").css({'right': '83.3333333%'});
				$("#typing-area").css({'right': '83.3333333%'});
				$("#active-thread").css({'right': '0', 'position': 'absolute'});
			}
		});
		if ($scope.length(message.replies) < 10) {
			$scope.getTenThreadMessages(message);
		}
		$scope.inputReply = "@" + $scope.activeThread.from.nickName + ": ";
		$timeout(function () {
			$("#thread-console").height($("#active-thread").height() - $("#thread-parent").height() - $("#thread-header").height() - (0.1625 * $("#main-activity-window").height()));
		}, 3);
	};

	$scope.closeThread = function () {
		$scope.activeThread = undefined;
		$("#activity-window").fadeIn('slow', function() {
			$("#activity-window").removeClass("col-lg-6 col-md-6 col-sm-5");
			$("#activity-window").addClass("col-lg-10 col-md-10 col-sm-9");
			$("#typing-area").removeClass("col-lg-6 col-md-6 col-sm-5");
			$("#typing-area").addClass("col-lg-10 col-md-10 col-sm-9");
			$("#active-thread").removeClass("col-lg-4 col-md-4 col-sm-4");
			if ($(window).width() < 768) {
				$("#activity-window").css({'right': ''});
				$("#typing-area").css({'right': ''});
				$("#active-thread").css({'right': '', 'position': ''});
			}
		});
	};

	$scope.getLastThreadMessage = function (thread) {
		if (thread === undefined) return;
		
		var message = {
			"id": thread.id
		};

		$http({
			method: 'POST',
			url: 'getLastThreadMessage',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: message
		}).then(
			function (success) {
				var reply = success.data;
				if (reply.id != 0 && reply.parentId != 0) {
					reply.from = JSON.parse(reply.from);
					thread.lastReply = reply;
				}
			},
			function (failure) {
				console.log("An error has occurred while trying to get last reply from server!");
			}
		);
	};

	$scope.getMessageRepliesNumber = function (thread) {
		if (thread === undefined) return;
		
		var message = {
			"id": thread.id
		};

		$http({
			method: 'POST',
			url: 'getMessageRepliesNumber',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: message
		}).then(
			function (success) {
				var replies = success.data.replies;
				thread.repliesCount = replies;
			},
			function (failure) {
				console.log("An error has occurred while trying to get last reply from server!");
			}
		);
	};

	$scope.getTenThreadMessages = function (thread) {
		if (thread === undefined) return;

		var message = {
			"id": thread.id,
			"parentId": $scope.length(thread.replies)
		};

		$http({
			method: 'POST',
			url: 'getTenThreadMessages',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: message
		}).then(
			function (success) {
				var scrollPos = $("#thread-console").prop("scrollHeight") - $("#thread-console").prop("scrollTop") - $("#thread-console").prop("clientHeight");
				success.data.forEach(function (reply) {
					$scope.getLastThreadMessage(reply);
					$scope.getMessageRepliesNumber(reply);
					reply.from = JSON.parse(reply.from);
					$scope.threads[reply.id] = reply;
					thread.replies[reply.id] = reply;
					reply.replies = {};
					if ($scope.isChannel) {
						reply.repliable = $scope.activeChat.subscribers[reply.from.nickName] !== undefined ? true : false;
					} else {
						reply.repliable = true;
					}
					var replyTime = Date.parse(reply.lastUpdate);
					var lastRead;
					if ($scope.isChannel) {
						lastRead = Date.parse($scope.channels[reply.to].lastRead);
					} else {
						lastRead = Date.parse($scope.activeChat.user.nickName.lastRead);
					}
					if (replyTime >= lastRead && reply.from != $scope.user.nickName) {
						$scope.directMessages[nickname].newMessages += 1;
						if (reply.text.indexOf("@" + $scope.user.nickName) != -1) {
							$scope.mentions += 1;
							$scope.directMessages[nickname].mentions += 1;
						}
					}
					if (reply.text.indexOf("@" + $scope.user.nickName) != -1) {
						reply.mention = true;
					}
				});

				if (success.data.length > 0) {
					$timeout(function () {
						$("#thread-console").scrollTop($("#thread-console").prop("scrollHeight") - $("#thread-console").prop("clientHeight") - scrollPos);
					}, 1);
				}
			},
			function (failure) {
				console.log("An error has occurred while trying to get replies from server!");
			}
		);
	};

	var getMainParentThread = function (thread) {
		while (threads[thread.parentId].id != 0) {

		}
	};

	$scope.showMentions = function () {
		$scope.mentions = 0;
		// TODO
	};

	$rootScope.channels.forEach(function (channel) {
		getChannelData(channel);
		$timeout(function () {
			if ($scope.length($scope.channels) > 0) {
				$scope.openChannel(Object.keys($scope.channels)[0]);
			} else if ($scope.length($scope.directMessages > 0)) {
				$scope.openDirectMessage(Object.keys($scope.directMessages)[0]);
			}
		}, 100);
	});
	
	$rootScope.directMessages.forEach(function (directMessage) {
		getDirectMessageData(directMessage);
	});

	$interval(function () {
		$("#direct-messages-list ul li .other-user-nickName").each(function () {
			var user = {
				"nickName": this.innerText
			};

			$http({
				method: 'POST',
				url: 'getStatus',
				headers: {'Content-Type' : "application/json; charset=utf-8"},
				data: user
			}).then(
				function (success) {
					var nickname = success.data.user.nickName;
					var status = success.data.user.status;
					var lastSeen = success.data.user.lastSeen;

					$scope.directMessages[nickname].user.status = status;
					$scope.directMessages[nickname].user.lastSeen = lastSeen;
				}
			);
		});

		$("#channels-list ul li").each(function () {
			var channel = {
				"name": this.innerText.split("# ")[1]
			};

			$http({
				method: 'POST',
				url: 'channelData',
				headers: {'Content-Type' : "application/json; charset=utf-8"},
				data: channel
			}).then(
				function (success) {
					$scope.channels[channel.name].subscribers = success.data.subscribers;
					$scope.channels[channel.name].description = success.data.description;
				},
				function (failure) {
					console.log("Error while trying to retrive channel data.");
				}
			);
		});

		if ($scope.found !== undefined) {
			for (var foundChannel in $scope.found.channels) {
				var channel = {
					"name": foundChannel
				};

				$http({
					method: 'POST',
					url: 'channelData',
					headers: {'Content-Type' : "application/json; charset=utf-8"},
					data: channel
				}).then(
					function (success) {
						$scope.found.channels[success.data.name].subscriptions = success.data.subscribers;
						$scope.found.channels[success.data.name].description = success.data.description;
					},
					function (failure) {
						console.log("Error while trying to retrive channel data.");
					}
				);
			}
		}

	}, 1000);
	
	$scope.search = function () {
		var query = {
			"in": "Channel OR User",
			"what": $scope.query
		};

		$http({
			method: 'POST',
			url: 'search',
			headers: {'Content-Type' : "application/json; charset=utf-8"},
			data: query
		}).then(
			function (success) {
				$scope.found = success.data;
				$scope.found.status = true;
				for (var channel in $scope.found.channels) {
					if ($scope.channels[channel] !== undefined) {
						$("#" + channel + "-subscribe-switch").attr('checked', true);
					} else {
						$("#" + channel + "-subscribe-switch").attr('checked', false);
					}
				}

				$timeout(function () { 
					$(".subscribe-switch").change(function (event) {
						if (!event.handled && $(this).is(':checked')) {
							$scope.subscribe(this.id.split("-subscribe-switch")[0]);
							event.handled = true;
						} else if (!event.handled && !$(this).is(':checked')) {
							$scope.unsubscribe(this.id.split("-subscribe-switch")[0]);
							event.handled = true;
						}
					});
				}, 1000);
			},
			function (failure) {
				$scope.found.status = false;
				$scope.found.message = "We did not find any matches for your search!";
			}
		);
	};

	$scope.showLastMessages = function () {
		$("#chat-console").animate({scrollTop: $("#chat-console").prop("scrollHeight") - $("#chat-console").prop("clientHeight")}, 1000);
		$scope.viewLastMessages = false;
	};

	$scope.clearSearch = function () {
		$scope.query = undefined;
		$scope.found = {
			"status": false,
			"channels": {},
			"users": {}
		};
	};

	$scope.shorten = function (length, string) {
		if (string !== undefined) {
			return string.length > length ? string.substr(0, length) + "..." : string;
		}
	};
}]);