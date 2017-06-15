<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="profile"/>
</jsp:include>
    <h1 class="title">Log In:</h1>
    <a class="button is-medium" 
    href="https://github.com/login/oauth/authorize?scope=user:email&client_id=<%= System.getenv("DIALECT_GITHUB_CLIENT_ID") %>">
    	<span class="icon is-medium">
      		<i class="fa fa-github"></i>
    	</span>
    	<span>GitHub</span>
    </a>
</body>
</html>