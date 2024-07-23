package com.web.pretups.networkstock.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface NetworkStockWebQry {
	public PreparedStatement loadProductsForStockQry(Connection pcon, String pnetworkCode, String pnetworkFor, String pmodule)throws SQLException;
	public String loadStockTransactionListQry(String pstatus);
	public PreparedStatement loadStockItemListQry(Connection p_con, String p_txnNo, String p_networkCode, String p_networkFor)throws SQLException;
	public String loadViewStockListQry(String p_stockNo, String p_status, String p_networkCode,String p_networkFor, String p_stockEntryType);
	public String loadStockDeductionListQry( String p_status);
	public String updateNetworkStockQry();
}
