<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
		<jsp:include page="header.jsp">
			<jsp:param name="page" value="docs"/>
		</jsp:include>
		<div style="margin-left: 25px; margin-right: 25px; text-align: left;">
			<div class="content">
				Diax Dialect, is a free, open-source, Cleverbot alternative. 
				This is the official documentation for the API used to access Diax Dialect. 
				You can use the API in two ways, by using the <a href="https://www.npmjs.com/package/diax-dialect">node module</a> or via the HTTP protocol. 
				The API is what is used to talk to the chatbot. It is intended to be simple and easy to use. 
				After you get your token, you can use it to make requests to the Dialect API.
				<ul style="font-size:17px;">
					<li><a id="api-button">API</a></li>
					<li><a id="node-button">Node.JS Module</a></li>
					<li><a id="example-button">Examples</a></li>
				</ul>
			</div>
			<h1 class="title" style="text-align:center;">HTTP API</h1>
			<p id="api">
				<br>All requests are made to the base URL of <code>http://www.diax.me/dialect/</code>. 
				There are two kinds of requests to the API for now which are ask and train requests. 
				Ask requests are used to talk to the chatbot while train requests are used to train the chatbot. 
				Ask requests are made to the url <code>http://www.diax.me/dialect/api/chatbot/ask</code> and training requests to <code>http://www.diax.me/dialect/api/chatbot/train</code>. 
				<br><br>I recommend for all Node.JS users to use the npm module for accessing the API instead of making direct requests.<br><br>
				Ask requests should be sent through a POST request in a JSON object that looks like this: <code>{ input: "my really cool dialect call goes here" }</code>. The response will be returned as a JSON object that looks like this: <code>{ output: "what dialect answers to my cool api call goes here" }</code>.<br><br>
				Train requests should be sent through a JSON object that looks like this: <code>{ input: "my really cool dialect call goes here", output: "what dialect should answer to my cool api call goes here" }</code>. 
				Training requests can only made if you have the training permission. You can check if you are a trainer by going to your profile page and if you are, the page should say so. Training requests do not return anything.<br><br>
				All requests should be authenticated. That is what your token is used for. Because of the unusual nature of using one authorization credential, the default HTTP Authorization header can not be used. 
				The dialect server will look for tokens in the <code>X-Token</code> header.<br><br>
				The server will return <code>401 Unauthorized</code> if a non-trainer makes a training request, if the token was invalid or wasn't provided, or if the account has been banned by an administrator. 
				If you are writing a module or wrapper for the API, it is recommended for you to use built in token validation requests. 
				A POST request to <code>http://www.diax.me/dialect/api/verification/verifytoken</code> with the body <code>{ token: "my token will go here" }</code> will return a JSON object that looks like this <code>{ response: "0" }</code> or <code>{ response: "1" }</code>. If the server returns <code>1</code>, the token is valid, if the server returns <code>0</code> the token is either invalid or banned. 
				In turn, there is also a endpoint for checking whether a user is a trainer located at <code>http://www.diax.me/dialect/api/verification/checktrainer</code>. Again, a POST request should be sent the same way as the verification endpoint and it will return the same value of <code>1</code> or <code>0</code>.<br>
				See the examples for some examples of API requests.
			</p>
			<br><h1 class="title" style="text-align:center;">Node.JS Module</h1>
			<p id="node">
				The node module can be installed by typing <code>npm install diax-dialect --save</code> in your project directory. Then you can include the module with <code>const Dialect = require('diax-dialect');</code>. 
				Then to create your Dialect instance, declare a global variable without initializing it (<code>var myDialect;</code>) and then call: <pre>Dialect.create("my-token", function(thisDialect) { myDialect = thisDialect; });</pre>This sets that variable you declared earlier to your dialect instance.<br> 
				After you do that, you can simply use the dialect api by doing: <pre>myDialect.ask("something to ask the dialect", function(response) { console.log(response); });</pre>This calls your dialect instance's ask method and runs the callback.<br>
				You can also train the api with the node module by running <code>myDialect.train("something to ask the dialect", "what the dialect should respond");</code>.
			</p>
			<br><h1 class="title" style="text-align:center;">Examples</h1>
			<p id="example">
				<h2 class="subtitle">Verify Token:</h2>
				<pre style="text-align:left; padding-left: 20px;">POST /dialect/api/verification/verifytoken HTTP/1.1
Host: www.diax.me
Accept: application/json
Content-Type: application/json

{token:"my-token"}</pre><br>
				<h2 class="subtitle">Check if user is trainer:</h2>
				<pre style="text-align:left; padding-left: 20px;">POST /dialect/api/verification/checktrainer HTTP/1.1
Host: www.diax.me
Accept: application/json
Content-Type: application/json

{token:"my-token"}</pre><br>
				<h2 class="subtitle">Ask Request:</h2>
				<pre style="text-align:left; padding-left: 20px;">POST /dialect/api/chatbot/ask HTTP/1.1
Host: www.diax.me
Accept: application/json
Content-Type: application/json
X-Token: my-token

{input:"stuff to say to chatbot"}</pre><br>
				<h2 class="subtitle">Train Request:</h2>
				<pre style="text-align:left; padding-left: 20px;">POST /dialect/api/chatbot/traink HTTP/1.1
Host: www.diax.me
Accept: application/json
Content-Type: application/json
X-Token: my-token

{input:"stuff to say to chatbot", output:"stuff chatbot should return"}</pre><br>
				<h2 class="subtitle">Node Example:</h2>
				<pre style="text-align:left; padding-left: 20px;">const Dialect = require('diax-dialect');
var myDialect;
Dialect.create("token", function(dialect) {
	myDialect = dialect;
});
// ask
myDialect.ask("Hello", function(response) {
	console.log(response);
});
// train
myDialect.train("Hello", "Hi there!");</pre>
			</p>
		</div><br><br>
		<script>
			$("#example-button").click(function() {
			    $('html, body').animate({
			        scrollTop: $("#example").offset().top
			    }, 800);
			});
			$("#api-button").click(function() {
			    $('html, body').animate({
			        scrollTop: $("#api").offset().top
			    }, 800);
			});
			$("#node-button").click(function() {
			    $('html, body').animate({
			        scrollTop: $("#node").offset().top
			    }, 800);
			});
		</script>
</body>
</html>