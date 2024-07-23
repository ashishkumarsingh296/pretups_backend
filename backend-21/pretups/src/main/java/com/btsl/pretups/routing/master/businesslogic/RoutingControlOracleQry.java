package com.btsl.pretups.routing.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;


public class RoutingControlOracleQry implements RoutingControlQry{

	private String className = "RoutingControlOracleQry";
	@Override
	public PreparedStatement loadInterfaceRoutingControlDetailsQry(Connection con , String alt1) throws SQLException
			 {
		String methodName = className+"#loadInterfaceRoutingControlDetailsQry";
		 StringBuilder strBuffAlt = new StringBuilder("SELECT  I.interface_id,I.status,I.message_language1, I.message_language2, I.external_id,IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,I.status_type statustype ");
	        strBuffAlt.append(" FROM interfaces I,interface_types IT ,service_classes SC ");
	        strBuffAlt.append(" WHERE I.interface_id=? AND I.interface_type_id=IT.interface_type_id AND I.status='Y'  "); 
	        strBuffAlt.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");
	        log.debug(methodName, strBuffAlt.toString());
	        PreparedStatement pstmtSelectAlt = con.prepareStatement(strBuffAlt.toString());
            pstmtSelectAlt.setString(1, alt1);
            pstmtSelectAlt.setString(2, PretupsI.ALL);
	        return pstmtSelectAlt;
	}

}
