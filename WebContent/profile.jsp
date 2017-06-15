<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="com.mashape.unirest.http.*, org.json.*, java.util.Arrays, com.aquariumpain.dialect.web.*, com.aquariumpain.dialect.web.daos.*"%>
<% 
		String accessToken = (String) session.getAttribute("access_token");
		if (accessToken != null) {
			HttpResponse<JsonNode> testTokenValidity = Unirest.get("https://api.github.com/user/emails")
					.header("accept", "application/json")
					.queryString("access_token", accessToken)
					.asJson();
			if (!testTokenValidity.getBody().isArray()) {
				response.sendRedirect("login.jsp");
				return;
			}
		}
		else {
			response.sendRedirect("login.jsp");
			return;
		}
		//get user info
		HttpResponse<JsonNode> userResponse = Unirest.get("https://api.github.com/user")
		  	.header("accept", "application/json")
		  	.queryString("access_token", accessToken)
		  	.asJson();
		JsonNode userBody = userResponse.getBody();
		JSONObject userObj = userBody.getObject();
		HttpResponse<JsonNode> emailResponse = Unirest.get("https://api.github.com/user/emails")
			.header("accept", "application/json")
			.queryString("access_token", accessToken)
			.asJson();
		JsonNode emailBody = emailResponse.getBody();
		JSONArray emailArray = emailBody.getArray();
		String email = "";
		for (int i = 0; i < emailArray.length(); i++) {
			JSONObject thisEmail = emailArray.getJSONObject(i);
			if ((Boolean) thisEmail.get("primary") == true) {
				email = thisEmail.get("email").toString();
				break;
			}
		}
		JSONObject userJson = new JSONObject()
				.put("github_id", userObj.get("id").toString())
				.put("username", userObj.get("login").toString())
				.put("email", email);
		User user = WebUtils.login(userJson);
		session.setAttribute("user", user);
%>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="profile"/>
</jsp:include>
<% if (user.isAdmin) {%>
			<div class="tabs is-fullwidth is-boxed">
  				<ul>
  					<li class="is-active"><a>Profile</a></li>
    				<li><a href="adminpanel.jsp">Admin Panel</a></li>
  				</ul>
			</div>
		<% } %>
<h1 class="title">Hello, <%= user.username %></h1><br><br>
Your Token:<pre><code id="token"><%= user.token %></code></pre><br><a class="button is-info" id="new-token">Regenerate Token</a><br><br>
<p>That is your token. Keep it secret so others can't use your account! Read the <a href="docs.jsp">documentation</a> to find out how to use it.</p><p id="github-id" hidden><%= user.githubId %></p>
<% if(user.isTrainer) { %><br><p>You are a trainer. This means you can send training API requests. Check the <a href="docs.jsp">documentation</a> to learn more</p><% } %>
<script>
    $.putJSON = function(url, data, callback) {
      return jQuery.ajax({
          'type': 'PUT',
          'url': url,
          'contentType': 'application/json',
          'data': JSON.stringify(data),
          'dataType': 'text',
          'success': callback,
          'error': function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
          }
      });
    };
    $(function () {
      var dialectRequestHandler = function () {
        $.putJSON("webapi/internal/regeneratetoken",
        { "github_id": $("#github-id").text() },
        function(data) {
          $("#token").html(data);
        });
      };
      $('#new-token').click(dialectRequestHandler);
    });
  </script>
</body>
</html>