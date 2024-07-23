package com.btsl.pretups.rest.client;


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 
 * @author akhilesh.mittal1 Web Application initializer explicitly to support
 *         Spring
 */

public class AnnotationsBasedApplicationInitializer /*implements WebApplicationInitializer */ {

	
	//@Override
	public void onStartup(ServletContext container) throws ServletException {

		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		//	ctx.register(RootApplicationConfig.class);
	//		ContextLoaderListener contextLoaderListener = new ContextLoaderListener(ctx);
	//		  container.addListener(contextLoaderListener);
			
			//ctx.register(RootApplicationConfig.class);
	        //ctx.setConfigLocations("com.btsl.common","com.restapi", "com.restapi.user.service", "com.btsl.pretups.channel.transfer.requesthandler","com.btsl.pretups.channel.transfer","com.restapi.cardgroup.service","com.restapi.networkstock.service" );
	        ContextLoaderListener contextLoaderListener = new ContextLoaderListener(ctx);
	  	     container.addListener(contextLoaderListener);
			ctx.setServletContext(container);
			ServletRegistration.Dynamic servlet = container.addServlet("dispatcher2", new DispatcherServlet(ctx));
			servlet.setLoadOnStartup(1);

			servlet.addMapping("/");
			}
	
	
	
}