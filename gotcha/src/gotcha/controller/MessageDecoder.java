package gotcha.controller;

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
