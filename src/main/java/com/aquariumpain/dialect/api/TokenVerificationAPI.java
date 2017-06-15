package com.aquariumpain.dialect.api;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;
import com.aquariumpain.dialect.database.DatabaseOperations;

@Path("/verification")
public class TokenVerificationAPI {
	
	@Path("/verifytoken")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String verifyToken(String JSONRequest, @Context ServletContext context) {
		JSONObject tokenObject = new JSONObject(JSONRequest);
		DatabaseOperations databaseOps = (DatabaseOperations) context.getAttribute("databaseOps");
		boolean verified = databaseOps.checkToken(tokenObject.getString("token"));
		boolean banned = databaseOps.isTokenBanned(tokenObject.getString("token"));
		String out = verified && !banned ? "1" : "0";
		return new JSONObject().put("response", out).toString();
	}
	
	@Path("/checktrainer")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String checkTrainer(String JSONRequest, @Context ServletContext context) {
		JSONObject tokenObject = new JSONObject(JSONRequest);
		DatabaseOperations databaseOps = (DatabaseOperations) context.getAttribute("databaseOps");
		boolean trainer = databaseOps.isTokenTrainable(tokenObject.getString("token"));
		String out = trainer ? "1" : "0";
		return new JSONObject().put("response", out).toString();
	}
}
