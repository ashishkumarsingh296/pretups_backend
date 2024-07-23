package com.btsl.pretups.channel.transfer.businesslogic;

public interface FOCBatchTransferQry {
	
	/**
	 * @param itemStatus
	 * @return
	 */
	String loadBatchItemsMapQry (String itemStatus );
	
	/**
	 * @param itemStatus
	 * @param currentLevel
	 * @return
	 */
	String loadBatchDPMasterDetailsQry(String itemStatus , String currentLevel);
	
	/**
	 * @return
	 */
	String closeOrderByBatchForDirectPayoutLoadNetworkStockQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchForDirectPayoutSelectWalletQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchForDirectPayoutSelectBalanceQry();
	
	/**
	 * @return
	 */
	String closeOrderByBatchForDirectPayoutUserBalancesQry();
	
	/**
	 * @return
	 */
	String focBatcheSelectItemDetailsQry();
	
	/**
	 * @return
	 */
	String getMultipleOffQry();
	
	
}
