package com.btsl.pretups.processes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DirectPayOutQry {
	
	String validateUsersQry();
	
	String selectNetworkDetailsFromNetworkStocksQry();
	
	String selectWalletDetailsFromNetworkStocksQry();
	
	String selectFromUserBalanceWheredailyBalanceUpdatedQry();
	
	String selectFromUserBalanceWhereNetworkCode();
	
	PreparedStatement loadGeographyListQry(Connection con, String[] zoneCodeArr, String zoneCode, String _networkCode) throws SQLException;

}
