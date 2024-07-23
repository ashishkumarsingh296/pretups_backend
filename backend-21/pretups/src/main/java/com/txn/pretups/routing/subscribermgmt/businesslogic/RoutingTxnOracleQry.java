package com.txn.pretups.routing.subscribermgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

/**
 * 
 * @author gaurav.pandey
 *
 */

public class RoutingTxnOracleQry implements RoutingTxnQry{
	 private Log log = LogFactory.getLog(this.getClass().getName());
	
	/**
	 * Load interface IN query for oracle
	 */
@Override	
	public PreparedStatement loadInterfaceIDForMNP(Connection pcon, String pMsisdn, String psubscriberType) throws SQLException
	{
	    final String methodName = "loadInterfaceID";
	    StringBuilder strBuff=new StringBuilder("SELECT SR.interface_id,SR.network_code  ,I.external_id, I.status,I.message_language1,");
		strBuff.append(" I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,");
		strBuff.append(" I.status_type statustype, I.single_state_transaction "); 
		strBuff.append(" FROM subscriber_routing SR,interfaces I,interface_types IT ,service_classes SC ");
		strBuff.append(" WHERE SR.msisdn = ? AND SR.subscriber_type=? AND SR.status='Y' AND I.status<>'N' ");
		strBuff.append(" AND I.interface_id=SR.interface_id AND I.interface_type_id=IT.interface_type_id ");
		strBuff.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + strBuff);
        }
		 PreparedStatement pstmt;
		 pstmt = pcon.prepareStatement(strBuff.toString());
         pstmt.setString(1, pMsisdn);
         pstmt.setString(2, psubscriberType);
         pstmt.setString(3, PretupsI.ALL);
		 return pstmt;
	}
	/**
	 * 
	 */
@Override
	public PreparedStatement loadInterfaceID(Connection pcon, String pMsisdn, String psubscriberType)throws SQLException
	{
	    final String methodName = "loadInterfaceID";
		StringBuilder strBuff = new StringBuilder("SELECT SR.interface_id  ,I.external_id, I.status,I.message_language1,");
        strBuff.append(" I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,");
        strBuff.append(" I.status_type statustype, I.single_state_transaction ");
        strBuff.append(" FROM subscriber_routing SR,interfaces I,interface_types IT ,service_classes SC ");
        strBuff.append(" WHERE SR.msisdn = ? AND SR.subscriber_type=? AND SR.status='Y' AND I.status<>'N' ");
        strBuff.append(" AND I.interface_id=SR.interface_id AND I.interface_type_id=IT.interface_type_id ");
        strBuff.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + strBuff);
        }
        PreparedStatement pstmt;
		pstmt = pcon.prepareStatement(strBuff.toString());
        pstmt.setString(1, pMsisdn);
        pstmt.setString(2, psubscriberType);
        pstmt.setString(3, PretupsI.ALL);
		 return pstmt;
		
	}

}
