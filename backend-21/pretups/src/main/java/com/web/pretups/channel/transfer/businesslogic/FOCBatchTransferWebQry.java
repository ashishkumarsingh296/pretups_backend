package com.web.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public interface FOCBatchTransferWebQry {
	String loadBatchFOCMasterDetailsQry(String p_itemStatus, String p_currentLevel);
	String processOrderByBatchQry();
	String closeOrderByBatchQry();
	String CloseOrderBatchSelectWalletQry();
	String UserBalancesQry();
	String SelectBalanceQry();
	String SelectItemsDetailsQry();
	String loadBatchDetailsListQry();
	PreparedStatement loadBatchFOCMasterDetailsQuery(Connection p_con, String p_goeDomain, String p_domain, String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID, String p_type) throws SQLException;
}
