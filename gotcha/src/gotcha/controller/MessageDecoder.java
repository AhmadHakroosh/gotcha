package gotcha.controller;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.*;

import gotcha.model.Message;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public Message decode (String textMessage) throws DecodeException {
		Gson gson = new GsonBuilder().create();
		Message message = gson.fromJson(textMessage, Message.class);
		return message;
	}
	
	@Override
	public boolean willDecode (String jsonMessage) {
		
		String regex = "(?:,|\\{)?([^:]*):(\"[^\"]*\"|\\{[^}]*\\}|[^},]*)";
		// Validate JSON
		if(jsonMessage.matches(regex)) {
			return true;
		}
		
		return false;
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
