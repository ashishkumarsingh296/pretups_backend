package com.btsl.pretups.routing.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;

public class RoutingControlPostgresQry implements RoutingControlQry{
	private String className = "RoutingControlPostgresQry";
	@Override
	public PreparedStatement loadInterfaceRoutingControlDetailsQry(Connection con , String alt1) throws SQLException {
		String methodName = className+"#loadInterfaceRoutingControlDetailsQry";
		StringBuilder strBuffAlt = new StringBuilder("SELECT  I.interface_id,I.status,I.message_language1, I.message_language2, I.external_id,IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,I.status_type statustype ");
        strBuffAlt.append(" FROM interfaces I left outer join service_classes SC on ( I.interface_id=SC.interface_id AND SC.service_class_code=? AND SC.STATUS <> 'N'  ) ,interface_types IT  ");
        strBuffAlt.append(" WHERE I.interface_id=? AND I.interface_type_id=IT.interface_type_id AND I.status='Y'  "); 
        log.debug(methodName, strBuffAlt.toString());
        PreparedStatement pstmtSelectAlt = con.prepareStatement(strBuffAlt.toString());
        pstmtSelectAlt.setString(1, PretupsI.ALL);
        pstmtSelectAlt.setString(2, alt1);
        return pstmtSelectAlt;
	}

}
