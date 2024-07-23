package com.btsl.pretups.user.businesslogic;

public interface UserBalancesQry {
	
	/**
	 * @return
	 */
	String selectForUserbalancesQry();
	
	/**
	 * @return
	 */
	String updateUserDailyBalancesQry();
	
	/**
	 * @return
	 */
	String creditUserBalanceForBonusAccQry();
	
	/**
	 * @param walletTypeCondition
	 * @return
	 */
	String loadUserBalanceForProductAndWalletsRSQry(String walletTypeCondition);
	
	/**
	 * @return
	 */
	String updateUserDailyBalancesForWalletsSelectForUpdateQry();
	
	/**
	 * @return
	 */
	String updateUserDailyBalancesForMultipleProductAndWalletQry();
	
	/**
	 * @return
	 */
	String diffCreditAndDebitUserBalancesQry();
	
	/**
	 * This method is used to get query string for user balance current date report 
     * @return String
     */
	String userBalanceCSVReportCurrentDate();
	
	
	/**
	 * This method is used to get query string for user balance previous date report 
     * @return String
     */
	String userBalanceCSVReportPreviousDate();
	
}
