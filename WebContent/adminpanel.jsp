<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="com.mashape.unirest.http.*, org.json.*, java.util.Arrays, com.aquariumpain.dialect.web.*, com.aquariumpain.dialect.web.daos.*"%>
<%
	User user = (User) session.getAttribute("user");
	boolean isAdminUser = false;
	if (user != null) {
		if (user.isAdmin) {
			isAdminUser = true;
		}
	}
	else {
		response.sendRedirect("profile.jsp");
		return;
	}
%>
		<jsp:include page="header.jsp">
			<jsp:param name="page" value="profile"/>
		</jsp:include>
		<% if (isAdminUser) {%>
			<div class="tabs is-fullwidth is-boxed">
  				<ul>
  					<li><a href="profile.jsp">Profile</a></li>
    				<li class="is-active"><a>Admin Panel</a></li>
  				</ul>
			</div>
			<div class="container">
				<div class="field has-addons">
  					<p class="control is-expanded">
    					<input id="search-text" class="input is-medium is-primary" type="text" placeholder="Search By Username Or Email">
  					</p>
  					<p class="control">
    					<a class="button is-medium is-primary" id="submit">Search</a>
  					</p>
				</div>
			</div>
			<br>
			<div>
				<table class="table is-bordered" id="user-table"></table>
			</div>
			<br>
			<a class="button is-medium is-info" id="save">Save Changes</a>
		<% } else { response.sendRedirect("profile.jsp"); }%>
		<script>
			var editedUsers = {};
			$.postJSON = function(url, data, callback) {
		      return jQuery.ajax({
		          'type': 'POST',
		          'url': url,
		          'contentType': 'application/json',
		          'data': JSON.stringify(data),
		          'dataType': 'json',
		          'success': callback,
		          'error': function (jqXHR, textStatus, errorThrown) {
		            console.log(jqXHR, textStatus, errorThrown);
		          }
		      });
		    };
		    $.putJSON = function(url, data, callback) {
			      return jQuery.ajax({
			          'type': 'PUT',
			          'url': url,
			          'contentType': 'application/json',
			          'data': JSON.stringify(data),
			          'success': callback,
			          'error': function (jqXHR, textStatus, errorThrown) {
			            console.log(jqXHR, textStatus, errorThrown);
			          }
			      });
			    };
		    $(function () {
		        var dialectRequestHandler = function () {
		          editedUsers = {};
		          var searchText = $("#search-text").val().trim();
		          if (searchText == "") return;
		          $.postJSON("webapi/internal/search",
		          { "search": searchText, "token": "${user.token}" },
		          function(data) {
		        	  var html = "No Users Were Found With That Name";
		        	  if (data.length > 0) {
		        		  html = "<thead><tr><th>ID</th><th>Username</th><th>Email</th><th>Trainer</th><th>Admin</th><th>Banned</th></tr></thead><tbody>";
			        	  data.forEach(function (el) {
			        		  html += "<tr><th>" + el.id + "</th><td>" + el.username + "</td><td>" + el.email + "</td><td><input type='checkbox' onchange='handleChange(this);' " + (el.isTrainer ? "checked" : "") + "></td><td><input type='checkbox' onchange='handleChange(this);' " + (el.isAdmin ? "checked" : "") + "></td><td><input type='checkbox' onchange='handleChange(this);' " + (el.isBanned ? "checked" : "") + "></td></tr>";
			        	  });
			        	  html += "</tbody>";
		        	  }
		              $("#user-table").html(html);
		          });
		        };
		        $('#submit').click(dialectRequestHandler);
		        $("#search-text").keypress(function(e) {
		          if(e.which == 13) {
		            dialectRequestHandler();
		          }
		        });
		    });
		    $(function () {
		        var dialectRequestHandler = function () {
		          if (Object.keys(editedUsers).length == 0) return;
		          var editedUserArray = Object.values(editedUsers);
		          editedUserArray.forEach(function (el) {
		        	  $.putJSON("webapi/internal/edituserperms", el, function(data){});
		          });
		          editedUsers = {};
		    	};
		        $('#save').click(dialectRequestHandler);
		    });
		    function handleChange(checkbox) {
		    	var row = checkbox.parentNode.parentNode;
		        var trainer = row.lastChild.previousSibling.previousSibling.firstChild.checked;
		        var admin = row.lastChild.previousSibling.firstChild.checked;
		        var banned = row.lastChild.firstChild.checked;
		        var id = parseInt(row.firstChild.textContent);
		        editedUsers[id] = { "id": id, "trainer": trainer, "admin": admin, "banned": banned, "token": "${user.token}" };
		    }
		</script>
	</body>
</html>