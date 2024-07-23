package com.restapi.user.service;

import com.btsl.common.BTSLBaseException;

public interface TransactionPinManagementService {
	
	/**
	 * @author vamshikrishna.v
	 * @param msisdn
	 * @return TransactionPinManagementResponseVO
	 * @throws BTSLBaseException
	 *
	 */
	TransactionPinManagementResponseVO validatePinModifyRequired(String msisdn) throws BTSLBaseException;

}
