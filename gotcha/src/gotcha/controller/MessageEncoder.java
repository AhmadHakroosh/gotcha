package gotcha.controller;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.*;

import gotcha.model.Message;

public class MessageEncoder implements Encoder.Text<Message> {

	@Override
	public String encode (Message message) throws EncodeException {
		Gson gson = new GsonBuilder().create();
		String jsonMessage = gson.toJson(message, Message.class);
		return jsonMessage;
	}
	
	@Override
	public void destroy() {
		// Nothing to do..
	}

	@Override
	public void init(EndpointConfig arg0) {
		// Nothing to do..
	}
}
