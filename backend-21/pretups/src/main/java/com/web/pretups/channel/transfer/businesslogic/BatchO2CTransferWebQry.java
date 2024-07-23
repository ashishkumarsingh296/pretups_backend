package com.web.pretups.channel.transfer.businesslogic;

public interface BatchO2CTransferWebQry {
	
	/**
	 * @param categoryCode
	 * @param receiverStatusAllowed
	 * @param geographicalDomainCode
	 * @return
	 */
	String loadUsersForBatchO2CQry(String[] categoryCode,String[] receiverStatusAllowed, String[] geographicalDomainCode);
	
	/**
	 * @param receiverStatusAllowed
	 * @param categoryCode
	 * @param geographicalDomainCode
	 * @return
	 */
	String validateUsersForBatchO2CQry(String receiverStatusAllowed,String categoryCode,String geographicalDomainCode);
	
	/**
	 * @param itemStatus
	 * @return
	 */
	String loadBatchO2CItemsMapQry(String itemStatus);
	
	/**
	 * @param currentLevel
	 * @param itemStatus
	 * @return
	 */
	String loadO2CBatchMasterDetailsQry(String currentLevel, String itemStatus);
	
	/**
	 * @return
	 */
	String closeOrderByBatchLoadNetworkStockQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchSelectNetworkStockQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchSelectUserBalancesQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchSelectBalanceQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchItemsDetailsQry();
	
	/**
	 * @return
	 */
	String processOrderByBatchItemsDetailsQry();


}
