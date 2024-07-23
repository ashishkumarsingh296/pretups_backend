package com.btsl.pretups.channel.transfer.businesslogic;

public interface OperatorC2CBatchTransferQry {
	String loadBatchC2CMasterDetailsForTxrQry(String p_itemStatus, String p_currentLevel);
	String loadBatchC2CMasterDetailsQry(String p_batchid);
	String loadBatchC2CMasterDetailsForWdrQry(String p_itemStatus, String p_currentLevel);
	String loadBatchItemsMapQry(String p_itemStatus);
	String processOrderByBatchQry();
	String closeOrderByBatchQry();
	String loadBatchC2CMasterDetailsQry(String p_batchid, String p_categoryCode, String p_loginCatCode, String p_userName);
}
