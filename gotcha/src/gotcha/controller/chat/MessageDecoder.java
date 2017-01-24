package gotcha.controller.chat;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.*;

import gotcha.model.Message;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public Message decode (String textMessage) throws DecodeException {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		JsonParser parser = new JsonParser();
		JsonElement jsonMessage = parser.parse(textMessage);
		JsonObject messageObject = new JsonObject();
		JsonObject sender = new JsonObject();
		// Modify message structure
		messageObject.add("message", jsonMessage);
		messageObject = messageObject.getAsJsonObject("message");
		sender = messageObject.getAsJsonObject("from");
		messageObject.remove("from");
		messageObject.add("from", sender.get("nickName"));
		Message message = gson.fromJson(jsonMessage, Message.class);
		return message;
	}
	
	@Override
	public boolean willDecode (String textMessage) {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		// Validate JSON
		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonMessage = parser.parse(textMessage);
			JsonObject messageObject = new JsonObject();
			JsonObject sender = new JsonObject();
			// Modify message structure
			messageObject.add("message", jsonMessage);
			messageObject = messageObject.getAsJsonObject("message");
			sender = messageObject.getAsJsonObject("from");
			messageObject.remove("from");
			messageObject.add("from", sender.get("nickName"));
			// Try to parse as a Message object
			gson.fromJson(jsonMessage, Message.class);
			return true;
		} catch (com.google.gson.JsonSyntaxException e) {
			return false;
		}
	}
	
	@Override
	public void destroy () {
		// Nothing to do..
	}

	@Override
	public void init (EndpointConfig config) {
		// Nothing to do..
	}
}
