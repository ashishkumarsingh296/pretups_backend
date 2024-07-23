package com.btsl.pretups.channel.reports.businesslogic;

public interface BatchC2SC2CTransSummaryQry {

	/**
	 * @param accessType
	 * @param geographicalCodes
	 * @return
	 */
	String loadUserC2CDataByMsisdnOrLoginIdQry(String accessType,String[] geographicalCodes );
	
	/**
	 * @param accessType
	 * @param geographicalCodes
	 * @return
	 */
	String loadUserC2SDataByMsisdnOrLoginIdQry(String accessType,String geographicalCodes );
	
	/**
	 * @param accessType
	 * @param geographicalCodes
	 * @return
	 */
	String loadUserC2CDataByMsisdnOrLoginIdForOperatorQry(String accessType,String geographicalCodes );
	
	/**
	 * @param accessType
	 * @param geographicalCodes
	 * @return
	 */
	String loadUserC2SDataByMsisdnOrLoginIdForOperatorQry(String accessType,String geographicalCodes );
}
