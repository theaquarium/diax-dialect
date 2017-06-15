package com.aquariumpain.dialect;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/")
public class RESTfulHelloWorld 
{
	@GET
	@Produces("text/html")
	public Response getStartingPage(@Context ServletContext context)
	{
		String output = "<h1>Hello World!<h1>" +
				"<p>RESTful Service is running ... <br>Ping @ " + new Date().toString() + "</p><br>";
		output += context.getAttribute("contextTest");
		return Response.status(200).entity(output).build();
	}
}