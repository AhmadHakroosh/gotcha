gotcha.filter("orderMessagesBy", function () {
	return function (object, time) {
		if (!angular.isObject(object)) {
			return object;
		}

		var messages = [];

		for (var id in object) {
			messages.push(object[id]);
		}

		messages.sort(function (message_a, message_b) {
			var date_a = Date.parse(message_a.time);
			var date_b = Date.parse(message_b.time);
			return date_a - date_b;
		});

		return messages;
	}
});