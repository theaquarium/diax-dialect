package com.aquariumpain.dialect.web;

import java.util.LinkedList;
import java.util.UUID;
import org.json.JSONObject;
import com.aquariumpain.dialect.DialectContextListener;
import com.aquariumpain.dialect.database.DatabaseOperations;
import com.aquariumpain.dialect.web.daos.User;
import com.google.gson.Gson;

public class WebUtils {
	
	Gson gson = new Gson();
	
	private static DatabaseOperations databaseOps = (DatabaseOperations) DialectContextListener.servletContext.getAttribute("databaseOps");

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
	
	public static User login(JSONObject userInput) {
		User user = databaseOps.getUserByGithubId(userInput.get("github_id").toString());
		if (user == null) {
			user = databaseOps.createUser(userInput);
		}
		else if (user.email != userInput.getString("email") || user.username != userInput.getString("username")) {
			user = databaseOps.updateUserDetails(userInput);
		}
		return user;
	}
	
	public static String search(String search) {
		LinkedList<User> userList = databaseOps.getUsersByUsernameOrEmail(search);
		String json = new Gson().toJson(userList);
		return json;
	}
}
