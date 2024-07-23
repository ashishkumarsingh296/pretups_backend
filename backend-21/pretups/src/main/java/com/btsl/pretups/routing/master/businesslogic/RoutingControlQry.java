package com.btsl.pretups.routing.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public interface RoutingControlQry {
	
	 Log log = LogFactory.getLog(RoutingControlQry.class.getName());
	PreparedStatement loadInterfaceRoutingControlDetailsQry(Connection con , String alt1) throws SQLException;
}
