package com.aquariumpain.dialect;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;
import com.aquariumpain.dialect.database.DatabaseOperations;

@Provider
public class AuthFilter implements Filter {
	public static final String AUTHENTICATION_HEADER = "X-Token";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String path = ((HttpServletRequest) request).getRequestURI();
		if (path.contains("/verification/")) {
		    chain.doFilter(request, response);
		} else {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse resp =(HttpServletResponse)response;
			DatabaseOperations databaseOps = (DatabaseOperations) DialectContextListener.servletContext.getAttribute("databaseOps");
			String token = req.getHeader(AUTHENTICATION_HEADER);
			if (token == null) {
			   resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		       resp.setContentType("text/plain");
		       PrintWriter writer = resp.getWriter();
		       writer.print("No Token. Token Must Be Sent In The X-Token Header.");
			}
			else {
				boolean isValidToken = databaseOps.checkToken(token);
				if (!isValidToken) {
					resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				    resp.setContentType("text/plain");
				    PrintWriter writer = resp.getWriter();
				    writer.print("Invalid Token. Make Sure You Sent The Correct Token.");
				}
				else {
					boolean isBannedToken = databaseOps.isTokenBanned(token);
					if (isBannedToken) {
						resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					    resp.setContentType("text/plain");
					    PrintWriter writer = resp.getWriter();
					    writer.print("Banned Token. Your account has been banned by an administrator.");
					}
					else {
						if (req.getRequestURI().contains("/api/chatbot/train")) {
							boolean isTokenTrainer = databaseOps.isTokenTrainable(token);
							if (!isTokenTrainer) {
								resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							    resp.setContentType("text/plain");
							    PrintWriter writer = resp.getWriter();
							    writer.print("Forbidden Request. You do not have permission to send training requests.");
							}
							else {
								chain.doFilter(request, response);
							}
						}
						else {
							chain.doFilter(request, response);
						}
					}
				}
			}
		}
	}

	@Override
	public void destroy() {}
}
