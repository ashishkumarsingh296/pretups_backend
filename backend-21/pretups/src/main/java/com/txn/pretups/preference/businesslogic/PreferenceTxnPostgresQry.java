package com.txn.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class PreferenceTxnPostgresQry implements PreferenceTxnQry {
	private Log log = LogFactory.getLog(PreferenceTxnOracleQry.class.getName());
	@Override
	public PreparedStatement loadPreferenceByServiceClassIdQry(Connection con, String networkCode, String serviceClassId) throws SQLException{
		
		String methodName ="loadPreferenceByServiceClassIdQry";
		 StringBuilder selectQuery = new StringBuilder();
		 selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,");
        selectQuery.append(" SP.default_value,SP.min_value,SP.max_value, SCP.value ");
        selectQuery.append(" FROM  service_class_preferences SCP right outer join system_preferences SP on (SCP.preference_code= SP.preference_code AND SCP.NETWORK_code =? AND SCP.service_class_id =?) ");
        selectQuery.append(" WHERE SP.display='Y'  AND SP.type=? ");
        selectQuery.append(" AND SP.module=?  ");
        selectQuery.append(" ORDER BY SP.name");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Query=" + selectQuery);
        }
        PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
        int i = 1;
        pstmtSelect.setString(i++, networkCode);
        pstmtSelect.setString(i++, serviceClassId);
        pstmtSelect.setString(i++, PreferenceI.SERVICE_CLASS_LEVEL);
        pstmtSelect.setString(i++, PretupsI.P2P_MODULE);
       
        
        return pstmtSelect;
		
	}
	
	
	@Override
	public PreparedStatement loadPreferenceByServiceTypeQry(Connection con, String networkCode, String serviceType) throws SQLException{
		String methodName ="loadPreferenceByServiceTypeQry";
		StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,");
        selectQuery.append("SP.default_value,SP.min_value,SP.max_value, CP.value ");
        selectQuery.append("FROM control_preferences CP  right outer join system_preferences SP on (CP.preference_code=SP.preference_code AND CP.NETWORK_code =? AND CP.control_code =? )");
        selectQuery.append("WHERE SP.display='Y' AND SP.type=? ");
        selectQuery.append("AND SP.module=?  ");
        selectQuery.append("ORDER BY SP.name");
        
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Query=" + selectQuery);
        }
        
        PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());
        int i = 1;
        pstmtSelect.setString(i++, networkCode);
        pstmtSelect.setString(i++, serviceType);
        pstmtSelect.setString(i++, PreferenceI.SERVICE_TYPE_LEVEL);
        pstmtSelect.setString(i++, PretupsI.P2P_MODULE);
        
        
        return pstmtSelect;
	}
}