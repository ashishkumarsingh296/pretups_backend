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

public class RoutingTxnPostgresQry implements RoutingTxnQry{
	 private Log log = LogFactory.getLog(this.getClass().getName());

	@Override	
	public PreparedStatement loadInterfaceIDForMNP(Connection pcon, String pMsisdn, String psubscriberType)throws SQLException
	{
		final String methodName = "loadInterfaceID";
		StringBuilder strBuff=new StringBuilder("SELECT SR.interface_id,SR.network_code  ,I.external_id, I.status,I.message_language1,");
		strBuff.append(" I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,");
		strBuff.append(" I.status_type statustype, I.single_state_transaction "); 
		strBuff.append(" FROM subscriber_routing SR,interfaces I left outer join service_classes SC on(I.interface_id=SC.interface_id and SC.service_class_code=? and SC.STATUS<>'N'),interface_types IT  ");
		strBuff.append(" WHERE SR.msisdn = ? AND SR.subscriber_type=? AND SR.status='Y' AND I.status<>'N' ");
		strBuff.append(" AND I.interface_id=SR.interface_id AND I.interface_type_id=IT.interface_type_id ");
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + strBuff);
        }
        PreparedStatement pstmt;
		pstmt = pcon.prepareStatement(strBuff.toString());
        pstmt.setString(1, PretupsI.ALL);
        pstmt.setString(2, pMsisdn);
        pstmt.setString(3, psubscriberType);
		return pstmt;
		
	}
	
@Override
	public PreparedStatement loadInterfaceID(Connection pcon, String pMsisdn, String psubscriberType)throws SQLException
	{
		StringBuilder strBuff = new StringBuilder("SELECT SR.interface_id  ,I.external_id, I.status,I.message_language1,");
        strBuff.append(" I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,");
        strBuff.append(" I.status_type statustype, I.single_state_transaction ");
        strBuff.append(" FROM subscriber_routing SR,interfaces I left outer join service_classes SC on(I.interface_id=SC.interface_id and SC.service_class_code=? and SC.STATUS<>'N'),interface_types IT  ");
		strBuff.append(" WHERE SR.msisdn = ? AND SR.subscriber_type=? AND SR.status='Y' AND I.status<>'N' ");
        strBuff.append(" AND I.interface_id=SR.interface_id AND I.interface_type_id=IT.interface_type_id ");
        PreparedStatement pstmt;
		pstmt = pcon.prepareStatement(strBuff.toString());
        pstmt.setString(1, PretupsI.ALL);
        pstmt.setString(2, pMsisdn);
        pstmt.setString(3, psubscriberType);
		return pstmt;
		
	}


}
