package com.txn.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class PreferenceTxnOracleQry implements PreferenceTxnQry {
	private Log log = LogFactory.getLog(PreferenceTxnOracleQry.class.getName());
	@Override
	public PreparedStatement loadPreferenceByServiceClassIdQry(Connection con, String networkCode, String serviceClassId) throws SQLException{
		
		 String methodName ="loadPreferenceByServiceClassIdQry";
		 StringBuilder selectQuery = new StringBuilder();
		 selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,");
         selectQuery.append("SP.default_value,SP.min_value,SP.max_value, SCP.value ");
         selectQuery.append("FROM system_preferences SP,service_class_preferences SCP ");
         selectQuery.append("WHERE SP.display='Y' AND SP.preference_code=SCP.preference_code(+) AND SP.type=? ");
         selectQuery.append("AND SP.module=? AND SCP.NETWORK_code(+)=? ");
         selectQuery.append("AND SCP.service_class_id(+) =? ORDER BY SP.name");
         if (log.isDebugEnabled()) {
             log.debug(methodName, "Query=" + selectQuery);
         }
         PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
         int i = 1;
         pstmtSelect.setString(i++, PreferenceI.SERVICE_CLASS_LEVEL);
         pstmtSelect.setString(i++, PretupsI.P2P_MODULE);
         pstmtSelect.setString(i++, networkCode);
         pstmtSelect.setString(i++, serviceClassId);
         
         return pstmtSelect;
	}
	
	
	@Override
	public PreparedStatement loadPreferenceByServiceTypeQry(Connection con, String networkCode, String serviceType) throws SQLException{
		String methodName ="loadPreferenceByServiceTypeQry";
		StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,");
        selectQuery.append("SP.default_value,SP.min_value,SP.max_value, CP.value ");
        selectQuery.append("FROM system_preferences SP,control_preferences CP ");
        selectQuery.append("WHERE SP.display='Y' AND SP.preference_code=CP.preference_code(+) AND SP.type=? ");
        selectQuery.append("AND SP.module=? AND CP.NETWORK_code(+)=? ");
        selectQuery.append("AND CP.control_code(+) =? ORDER BY SP.name");
        
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Query=" + selectQuery);
        }
        
        PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
        int i = 1;
        pstmtSelect.setString(i++, PreferenceI.SERVICE_TYPE_LEVEL);
        pstmtSelect.setString(i++, PretupsI.P2P_MODULE);
        pstmtSelect.setString(i++, networkCode);
        pstmtSelect.setString(i++, serviceType);
       
        return pstmtSelect;
	}
}