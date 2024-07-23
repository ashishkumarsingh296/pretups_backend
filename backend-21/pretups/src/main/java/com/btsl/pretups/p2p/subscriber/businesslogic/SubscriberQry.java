package com.btsl.pretups.p2p.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public interface SubscriberQry {
	
	PreparedStatement loadSubscriberDetailsByMsisdnQry(Connection con, String msisdn,
			String serviceType) throws SQLException;
	
	PreparedStatement loadSubscriberDetailsQry(Connection con, String msisdn,
			String serviceType,Date fromDate, Date toDate, String status) throws SQLException;
	
	PreparedStatement loadSubscriberDetailsByIDForUpdateQry(Connection con, String userID, String serviceType)throws SQLException;

}
