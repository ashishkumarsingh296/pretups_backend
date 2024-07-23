package com.txn.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreferenceTxnQry {
	public PreparedStatement loadPreferenceByServiceClassIdQry(Connection con, String networkCode, String serviceClassId) throws SQLException;
	public PreparedStatement loadPreferenceByServiceTypeQry(Connection con, String networkCode, String serviceType) throws SQLException;
	
}