package com.btsl.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Subesh KCV
 * This class provides application context to spring
 *
 */

@Component
public class PretupsServiceFactory  {

	@Autowired
	ApplicationContextProvider applicationContextProvider;
	
	public Object getPretupsServiceObject(Class className){
		return applicationContextProvider.getApplicationContext().getBean(className);	
	}
	

	

}
