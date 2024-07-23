package com.btsl.pretups.p2p.query.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public interface P2PQueryHistoryQry {
	
	Log LOG = LogFactory.getLog(P2PQueryHistoryQry.class.getName());
	String QUERY = "Query : "; 

	public PreparedStatement loadSubscriberDetails(Connection p_con, P2pQueryHistoryVO p_queryHistoryVO) throws SQLException, ParseException;
}
