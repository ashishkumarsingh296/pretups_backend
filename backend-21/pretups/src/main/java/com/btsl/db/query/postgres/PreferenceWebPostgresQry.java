package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.web.pretups.preference.businesslogic.PreferenceWebQry;



public class PreferenceWebPostgresQry implements PreferenceWebQry{
	private static Log _log = LogFactory.getLog(PreferenceWebPostgresQry.class.getName());
	@Override
	public String loadNetworkPreferenceDataQry(){
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,SP.default_value,SP.min_value, ");
        selectQuery.append(" SP.max_value,SP.max_size,SP.modified_allowed,SP.description,SP.modified_on s_modified_on, ");
        selectQuery.append(" NP.value,NP.modified_on n_modified_on ,SP.allowed_values, SP.fixed_value ");
        selectQuery.append(" FROM network_preferences NP right outer join system_preferences SP on NP.preference_code=SP.preference_code and NP.NETWORK_code = ? ");
        selectQuery.append(" WHERE SP.display='Y'  AND SP.type=?  ");
        selectQuery.append(" ORDER BY SP.name ");
        return selectQuery.toString();	
	}
	@Override
	public PreparedStatement loadServiceClassPreferenceDataQry(Connection p_con, String p_networkCode, String p_serviceClass) throws SQLException{
		final String methodName = "loadServiceClassPreferenceDataQry";
		 PreparedStatement pstmtSelect = null;
		 StringBuilder selectQuery = new StringBuilder();	 
         selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,SP.default_value,SP.min_value,SP.max_value,");
         selectQuery.append(" SP.max_size,SP.modified_allowed,SP.description,SP.modified_on sp_modified_on, ");
         selectQuery.append(" SCP.value,SCP.modified_on scp_modified_on,SP.allowed_values, SP.fixed_value ");
         selectQuery.append(" FROM system_preferences SP left join service_class_preferences SCP on SCP.preference_code=SP.preference_code");
         selectQuery.append(" AND SCP.NETWORK_code=? AND SCP.service_class_id = ? ");
         selectQuery.append(" WHERE SP.display='Y' AND SP.type=?  ORDER BY SP.name");
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Query=" + selectQuery);
         }
         pstmtSelect = p_con.prepareStatement(selectQuery.toString());
         int i = 1;       
         pstmtSelect.setString(i++, p_networkCode);
         pstmtSelect.setString(i++, p_serviceClass);
         pstmtSelect.setString(i++, PreferenceI.SERVICE_CLASS_LEVEL);
		 return pstmtSelect;	
	}
	
	@Override
	public PreparedStatement loadControlUnitPreferenceDataQry(Connection p_con, String p_zoneCode) throws SQLException{
		final String methodName = "loadControlUnitPreferenceDataQry";
		PreparedStatement pstmtSelect = null;
		final StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,SP.default_value,SP.min_value, ");
        selectQuery.append(" SP.max_value,SP.max_size,SP.modified_allowed,SP.description,SP.modified_on sp_modified_on, ");
        selectQuery.append(" CP.value,CP.modified_on CP_modified_on,CP.network_code,SP.allowed_values, SP.fixed_value ");
        selectQuery.append(" FROM system_preferences SP left join control_preferences CP on CP.preference_code=SP.preference_code AND CP.control_code = ? ");
        selectQuery.append(" WHERE SP.type=? ");
        selectQuery.append(" ORDER BY SP.name ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery.toString());
        pstmtSelect.setString(1, p_zoneCode);
        pstmtSelect.setString(2, PreferenceI.ZONE_LEVEL);
		 return pstmtSelect;
	}
	
	@Override
	public String loadNetworkPreferenceDataQuery(){
	    final StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT SP.module, SP.preference_code,SP.name,SP.value_type,SP.default_value,SP.min_value, ");
        selectQuery.append(" SP.max_value,SP.max_size,SP.modified_allowed,SP.description,SP.modified_on s_modified_on, ");
        selectQuery.append(" NP.value,NP.modified_on n_modified_on ,SP.allowed_values, SP.fixed_value ");
        selectQuery.append(" FROM network_preferences NP right outer join system_preferences SP on  NP.preference_code=SP.preference_code AND NP.NETWORK_code = ? ");
        selectQuery.append(" WHERE SP.display='Y' ");
        selectQuery.append(" AND SP.preference_code=? ");
        return selectQuery.toString();
	}
}
