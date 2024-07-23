package com.btsl.pretups.loyalitystock.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface LoyalityStockQry {
	Log LOG = LogFactory.getLog(LoyalityStockQry.class.getName());
	String QUERY = "Query : ";
	PreparedStatement loadProductsForStockQry(Connection pCon, String pNetworkCode, String pNetworkFor, String pModule) throws SQLException;
	String loadStockTransactionListQry(String pStatus);
}
