<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="com.mashape.unirest.http.*, org.json.*, java.util.Arrays"%>
<%
		//get temporary GitHub code...
		String sessionCode = request.getParameter("code");
		if (sessionCode == null) {
			response.sendRedirect("login.jsp");
			return;
		}
		String clientId = System.getenv("DIALECT_GITHUB_CLIENT_ID");
		String clientSecret = System.getenv("DIALECT_GITHUB_CLIENT_SECRET");
		
		// ... and POST it back to GitHub
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://github.com/login/oauth/access_token")
		  	.header("accept", "application/json")
		  	.field("client_id", clientId)
		  	.field("client_secret", clientSecret)
		  	.field("code", sessionCode)
		  	.asJson();
		
		// extract the token and granted scopes
		JsonNode body = jsonResponse.getBody();
		JSONObject obj = body.getObject();
		String accessToken = "";
		try {
			accessToken = obj.get("access_token").toString();
		}
		catch (Exception e) {
			response.sendRedirect("login.jsp");
			return;
		}
		String[] scopes = obj.get("scope").toString().split(",");
		boolean hasEmailScope = Arrays.asList(scopes).contains("user:email");
		if (hasEmailScope) {
			session.setAttribute("access_token", accessToken);
			response.sendRedirect("profile.jsp");
		}
%>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="profile"/>
</jsp:include>
	<%if (hasEmailScope) { %>
		Loading, please wait while you are logged in...
	<%} else {%>
		Login failed. You did not grant the read email permission. <a href="https://github.com/login/oauth/authorize?scope=user:email&client_id=<%= System.getenv("DIALECT_GITHUB_CLIENT_ID") %>">Click here to try again</a>
	<%} %>
</body>
</html>