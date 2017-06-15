<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
		<jsp:include page="header.jsp">
			<jsp:param name="page" value="home"/>
		</jsp:include>
		
		<style>
		#response {
 			position: relative;
 			width: 50%;
 			left: 25%;
 			height: 250px;
 			overflow-y: scroll;
 			border: 1px solid #ccc;
 		}
 		.field {
 			position: relative;
 			width: 50%;
 			left: 25%;
 		}
		</style>
		
		<h2 class="title">Diax Dialect, the Free, Open-Source, Cleverbot Alternative</h2>
		<p>Diax Dialect is easy to use with a <a href="https://www.npmjs.com/package/diax-dialect">module for Node.JS</a> and simple API</p><br>
		<p class="subtitle">Try It:</p>
		<div id="response" class=""></div><br>
		<div class="field has-addons">
			<p class="control" style="width:100%">
					<input id="input" class="input is-info" type="text" placeholder="Text">
			</p>
			<p class="control">
					<a class="button is-info" id="submit">Submit</a>
			</p>
		</div>
    	<script>
		    $.postJSON = function(url, data, callback) {
		      return jQuery.ajax({
		          'type': 'POST',
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
		        var inputText = $("#input").val().trim();
		        if (inputText == "") return;
		        $.postJSON("webapi/internal/ask",
		        { "input": inputText },
		        function(data) {
		          var html = $("#response").html();
		          $("#response").html(html + '<br><span style="color: #6A5ACD">Me: ' + inputText + '</span><br><span style="color: #1E90FF">Dialect: ' + JSON.parse(data).output + '</span><br>');
		          $("#response").scrollTop($("#response").prop('scrollHeight'));
		          $("#input").val("");
		        });
		      };
		      $('#submit').click(dialectRequestHandler);
		      $("#input").keypress(function(e) {
		        if(e.which == 13) {
		          dialectRequestHandler();
		        }
		      });
		    });
  		</script>
	</body>
</html>
