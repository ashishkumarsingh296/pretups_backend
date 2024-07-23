package com.btsl.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btsl.pretups.common.PretupsI;
import com.restapi.c2s.services.PretupsBusinessServiceI;

@Component
public class EventProcessorFactory {
	
@Autowired
private ApplicationContextProvider applicationContextProvider; 
	
	public  PretupsBusinessServiceI  getEventProcessorService(PretupsEventTypes pretupsEventyType) {
		//ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
		  switch (pretupsEventyType) {
		  case OFFLINE_REPORT_EVENT :
			               return (PretupsBusinessServiceI) applicationContextProvider.getApplicationContext().getBean(PretupsI.OFFLINEREPORTSERVICE_BEAN_NAME);
		  default:
              throw new UnsupportedOperationException("" +
                      "enum " + pretupsEventyType + "not supported.");	               

		  }
		
	}

	  

}
