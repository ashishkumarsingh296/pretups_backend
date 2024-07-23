package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SlabGeneratorQry {
	
	public PreparedStatement loadSlabDatesQry(Connection conn, String networkCode, int previousMonths, int forwardMonths) throws SQLException;
}
