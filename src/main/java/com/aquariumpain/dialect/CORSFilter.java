package com.aquariumpain.dialect;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.filters.CorsFilter;

public class CORSFilter extends CorsFilter {
	
	private static final long serialVersionUID = 1L;
	
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		String path = ((HttpServletRequest) servletRequest).getRequestURI();
		if (path.contains("/webapi/")) {
		    filterChain.doFilter(servletRequest, servletResponse);
		} else {
			super.doFilter(servletRequest, servletResponse, filterChain);
		}
	}
}
