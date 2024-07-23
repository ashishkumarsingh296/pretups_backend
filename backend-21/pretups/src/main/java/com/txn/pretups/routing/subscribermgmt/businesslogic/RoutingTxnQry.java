package com.txn.pretups.routing.subscribermgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author gaurav.pandey
 *
 */
public interface  RoutingTxnQry {
	/**
	 * 
	 * @param pcon
	 * @param pMsisdn
	 * @param psubscriberType
	 * @return
	 * @throws SQLException 
	 */
	PreparedStatement loadInterfaceIDForMNP(Connection pcon,String pMsisdn,String psubscriberType) throws SQLException;
	/**
	 * 
	 * @param pcon
	 * @param pMsisdn
	 * @param psubscriberType
	 * @return
	 * @throws SQLException 
	 */
	PreparedStatement loadInterfaceID(Connection pcon, String pMsisdn, String psubscriberType) throws SQLException;

}
