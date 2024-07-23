package com.txn.pretups.loyaltymgmt.businesslogic;
/**
 * Added for Loyalty TXN dao migration to postgres
 * @author gaurav.pandey
 *
 */

public interface LoyaltyTxnQry {
	/**
	 * 
	 * @param plmsProfileServiceType
	 * @return
	 */

 String loadLMSProfileAndVersionQry(String plmsProfileServiceType);
 
 /**
	 * interface method 
	 * @return
	 */
 String checkUserAlreadyExistQry();
 
	
}
