package com.aquariumpain.dialect;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.aquariumpain.dialect.database.DatabaseOperations;

@WebListener
public class DialectContextListener implements ServletContextListener {

	public static ServletContext servletContext;
	
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Dialect WS is starting up");
        //add to ServletContext
        ServletContext context = servletContextEvent.getServletContext();
        DatabaseOperations databaseOps = new DatabaseOperations(context);
        context.setAttribute("databaseOps", databaseOps);
        context.setAttribute("contextTest", "Context Listener Success");
        servletContext = context;
        System.out.println("Dialect WS is ready");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Dialect WS is shutting down");
    }

}
