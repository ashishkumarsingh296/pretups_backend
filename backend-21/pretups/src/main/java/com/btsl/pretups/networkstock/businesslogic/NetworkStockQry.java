package com.btsl.pretups.networkstock.businesslogic;

public interface NetworkStockQry {
	
	/**
	 * @return
	 */
	String debitNetworkStockSelectForQry();
	
	/**
	 * @return
	 */
	String creditNetworkStockSelectForQry();
	
	/**
	 * @return
	 */
	String loadCurrentStockListQry();

	/**
	 * @return
	 */
	String updateNetworkDailyStockQry();
}
