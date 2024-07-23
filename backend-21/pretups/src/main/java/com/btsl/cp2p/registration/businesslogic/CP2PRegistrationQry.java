package com.btsl.cp2p.registration.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface CP2PRegistrationQry {
	
	Log LOG = LogFactory.getLog(CP2PRegistrationQry.class.getName());
	String QUERY = "Query : ";
	
	public String loadCP2PSubscriberDetails(String servicetype);
	
	public String loadCP2PSubscriberDetails1(String servicetype);

}
