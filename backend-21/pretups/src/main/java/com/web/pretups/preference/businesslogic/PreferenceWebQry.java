package com.web.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreferenceWebQry {
	String loadNetworkPreferenceDataQry();
	PreparedStatement loadServiceClassPreferenceDataQry(Connection p_con, String p_networkCode, String p_serviceClass) throws SQLException;
	PreparedStatement loadControlUnitPreferenceDataQry(Connection p_con, String p_zoneCode) throws SQLException;
	String loadNetworkPreferenceDataQuery();

}
