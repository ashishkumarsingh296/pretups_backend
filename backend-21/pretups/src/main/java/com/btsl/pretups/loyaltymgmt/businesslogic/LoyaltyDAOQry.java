package com.btsl.pretups.loyaltymgmt.businesslogic;

/**
 * This interface provides method for getting sql queries
 * @author lalit.chattar
 *
 */
public interface LoyaltyDAOQry {

	public String loadLMSProfileAndVersion(String lmsProfileServiceType);
	public String checkUserAlreadyExist(String actionType);
	public String updateBonusOfUser();
}
