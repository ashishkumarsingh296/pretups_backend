package com.btsl.pretups.routing.subscribermgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class RoutingPostgresQry implements RoutingQry {

	@Override
	public PreparedStatement loadInterfaceIDForMNPQry(Connection pCon,String pMsisdn,
			String pSubscriberType) throws SQLException {
			PreparedStatement pstmt=null;
		
		StringBuilder strBuff=new StringBuilder("SELECT SR.interface_id,SR.network_code  ,I.external_id, I.status,I.message_language1,");
		strBuff.append(" I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,");
		strBuff.append(" I.status_type statustype, I.single_state_transaction "); 
		strBuff.append(" FROM subscriber_routing SR,interfaces I left outer join service_classes SC on");
		strBuff.append(" (I.interface_id=SC.interface_id AND SC.service_class_code=? AND SC.STATUS<>'N'),");
		strBuff.append(" interface_types IT WHERE SR.msisdn = ? AND SR.subscriber_type=? AND SR.status='Y' AND I.status<>'N' ");
		strBuff.append(" AND I.interface_id=SR.interface_id AND I.interface_type_id=IT.interface_type_id ");
		
		LogFactory.printLog("loadInterfaceIDForMNPQry", strBuff.toString(), LOG);
		
	     pstmt= pCon.prepareStatement(strBuff.toString());
	     pstmt.setString(1,PretupsI.ALL);
         pstmt.setString(2,pMsisdn );
         pstmt.setString(3,pSubscriberType);
		
		 
		return pstmt;
		 
	}

}
