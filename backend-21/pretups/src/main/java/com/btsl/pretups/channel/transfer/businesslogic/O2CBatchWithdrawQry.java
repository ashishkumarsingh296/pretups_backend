package com.btsl.pretups.channel.transfer.businesslogic;

public interface O2CBatchWithdrawQry {

	/**
	 * @param currentLevel
	 * @param itemStatus
	 * @return
	 */
	String loadBatchO2CMasterDetailsQry(String currentLevel, String itemStatus);
	
	/**
	 * @param itemStatus
	 * @return
	 */
	String loadBatchItemsMapQry(String itemStatus);
	
	/**
	 * @return
	 */
	String processOrderByBatchQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchQry();
	
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
	String focBatchesSelectItemsDetailsQry();
	
	/**
	 * @param batchid
	 * @param msisdn
	 * @param goeDomain
	 * @param domain
	 * @param productCode
	 * @return
	 */
	String loadBatchO2CMasterDetailsQry(String batchid,String  msisdn,String  goeDomain,String  domain,String  productCode);
	
	/**
	 * @return
	 */
	String loadBatchDetailsListQry();
		
}
