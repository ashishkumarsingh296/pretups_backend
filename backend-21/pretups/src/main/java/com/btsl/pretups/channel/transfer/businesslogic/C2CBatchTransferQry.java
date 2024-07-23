package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface C2CBatchTransferQry {
	public String closeOrderByBatchselectUserBalancesQry();
	public String closeOrderByBatchselectBalanceQry();
	public PreparedStatement loadUsersForHierarchyFixedCatQry(Connection p_con,String statusAllowed, String p_networkCode, String p_toCategoryCode, String p_parentUserID, String p_userName, String p_userID, String p_fixedCat, int p_ctrlLvl, String p_txnType) throws SQLException;
	public PreparedStatement loadUsersForHierarchyFixedCatTcpQry(Connection p_con,String statusAllowed, String p_networkCode, String p_toCategoryCode, String p_parentUserID, String p_userName, String p_userID, String p_fixedCat, int p_ctrlLvl, String p_txnType) throws SQLException;
	
	
	public PreparedStatement loadUsersByParentIDRecursiveQry(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_txnType,String statusAllowed)throws SQLException;
	public PreparedStatement loadUsersByParentIDRecursiveTcpQry(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_txnType,String statusAllowed)throws SQLException;
	
	
	
	public PreparedStatement loadUserForChannelByPassQry(String statusAllowed,Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_txnType)throws SQLException;
	public PreparedStatement loadUserForChannelByPassTcpQry(String statusAllowed,Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_txnType)throws SQLException;
	
	public String loadBatchDetailsListQry();
	public String loadBatchC2CMasterDetailsQry(String p_batchid, String pLOGinCatCode, String p_categoryCode,String p_userName, String p_domain);
	public String loadBatchC2CMasterDetailsForTxrQry(String p_currentLevel, String p_itemStatus);
	public String closeBatchC2CTransferBalanceQry();
	public String closeBatchC2CTransferUserBalanceQry();
	
}
