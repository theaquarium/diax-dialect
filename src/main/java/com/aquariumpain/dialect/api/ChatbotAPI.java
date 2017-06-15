package com.aquariumpain.dialect.api;


import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;
import com.aquariumpain.dialect.chatbot.Chatbot;

@Path("/chatbot")
public class ChatbotAPI {
	
	@Path("/train")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public void train(String JSONRequest, @Context ServletContext context) {
		JSONObject trainObject = new JSONObject(JSONRequest);
		Chatbot.createDependenciesAndIncrement(trainObject.getString("input"), trainObject.getString("output"), context);
	}
	
	@Path("/ask")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String postAsk(String JSONRequest, @Context ServletContext context) {
		JSONObject askObject = new JSONObject(JSONRequest);
		String chatbotOut = Chatbot.ask(askObject.getString("input"), context);
		return new JSONObject().put("output", chatbotOut).toString();
	}
}