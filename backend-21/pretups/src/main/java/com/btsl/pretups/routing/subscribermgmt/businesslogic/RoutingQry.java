package com.btsl.pretups.routing.subscribermgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface RoutingQry {
	
	Log LOG = LogFactory.getLog(RoutingQry.class.getName());
	String QUERY = "Query : "; 
	
	public PreparedStatement loadInterfaceIDForMNPQry(Connection pCon,String pMsisdn,String pSubscriberType) throws SQLException;

}
