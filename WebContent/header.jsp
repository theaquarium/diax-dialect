<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dialect</title>

<link rel="icon" href="favicon.ico?" type="image/x-icon"/>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.4.2/css/bulma.min.css">
<script
  src="https://code.jquery.com/jquery-3.2.1.min.js"
  integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
  crossorigin="anonymous"></script>
<style>
body {
	text-align: center;
}

.is-active {
	border
}
</style>
</head>
<body>
<c:out value="${param.page}"/>
<nav class="nav has-shadow">
	<div class="container">
	  	<div class="nav-left">
	    	<a class="nav-item" href="index.jsp">
	     		<h1 class="title">&nbsp;Diax Dialect</h1>
	    	</a>
	  	</div>
	  	<div class="nav-right nav-menu">
      		<a class="nav-item is-tab ${param.page == 'home' ? 'is-active' : ''}" href="index.jsp">Home</a>
      		<a class="nav-item is-tab ${param.page == 'docs' ? 'is-active' : ''}" href="docs.jsp">Documentation</a>
      		<a class="nav-item is-tab ${param.page == 'profile' ? 'is-active' : ''}" href="profile.jsp">Profile</a>
    	</div>
	</div>
</nav>
<br>