package com.aquariumpain.dialect.web;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import com.aquariumpain.dialect.chatbot.Chatbot;
import com.aquariumpain.dialect.database.DatabaseOperations;
import com.google.gson.Gson;

@Path("/internal")
public class WebAPI {
	
	private static Gson gson = new Gson();
	
	@Path("/ask")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String authlessAsk(String JSONRequest, @Context ServletContext context) {
		JSONObject askObject = new JSONObject(JSONRequest);
		String chatbotOut = Chatbot.ask(askObject.getString("input"), context);
		return new JSONObject().put("output", chatbotOut).toString();
	}
	
	@Path("/search")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(String JSONRequest, @Context ServletContext context) {
		JSONObject searchObject = new JSONObject(JSONRequest);
		String token = null;
		try {
			token = searchObject.getString("token");
		}
		catch (Exception e) {}
		DatabaseOperations databaseOps = (DatabaseOperations) context.getAttribute("databaseOps");
		if (token != null) {
			boolean validToken = databaseOps.checkToken(token);
			boolean adminToken = databaseOps.isTokenAdmin(token);
			if (validToken && adminToken) {
				String out = WebUtils.search(searchObject.getString("search"));
				return Response.ok(out, MediaType.APPLICATION_JSON).build();
			}
			else {
				return Response.status(Response.Status.UNAUTHORIZED).entity("Token does not have the admin permission.").build();
			}
		}
		else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("No Token Provided").build();
		}
	}
	
	@Path("/edituserperms")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editUserPerms(String JSONRequest, @Context ServletContext context) {
		JSONObject json = new JSONObject(JSONRequest);
		String token = null;
		try {
			token = json.getString("token");
		}
		catch (Exception e) {}
		DatabaseOperations databaseOps = (DatabaseOperations) context.getAttribute("databaseOps");
		if (token != null) {
			boolean validToken = databaseOps.checkToken(token);
			boolean adminToken = databaseOps.isTokenAdmin(token);
			if (validToken && adminToken) {
				String out = gson.toJson(databaseOps.updateUserPerms(json));
				return Response.ok(out, MediaType.APPLICATION_JSON).build();
			}
			else {
				return Response.status(Response.Status.UNAUTHORIZED).entity("Token does not have the admin permission.").build();
			}
		}
		else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("No Token Provided").build();
		}
	}
	
	@Path("/regeneratetoken")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String regenerateToken(String JSONRequest, @Context ServletContext context) {
		JSONObject json = new JSONObject(JSONRequest);
		DatabaseOperations databaseOps = (DatabaseOperations) context.getAttribute("databaseOps");
		String out = databaseOps.resetToken(json.getString("github_id"));
		return out;
	}
}
