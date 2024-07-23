package com.web.pretups.channel.transfer.businesslogic;

public interface C2CBatchTransferWebQry {
	
	public String loadBatchC2CMasterDetailsForTxrQry(String p_itemStatus, String p_currentLevel);
	public String loadBatchC2CMasterDetailsForWdrQry(String p_itemStatus, String p_currentLevel);
	public String processOrderByBatchQry();
	public String loadBatchDetailsListQry();
	public String loadBatchC2CMasterDetailsQry(String p_batchid,String pLOGinID, String p_categoryCode, String pLOGinCatCode, String p_userName);
	public String loadBatchC2CMasterDetailsForTxrAndWdrQry(String p_itemStatus, String p_currentLevel, String category);
	String loadBatchDetailsListDownloadQry();
	
	String loadBatchDetailsByBatchIdQry();
	String loadBatchDetailsByAdvancedQry();
	

}
