package com.apicontrollers.extgw.changePIN_EXC2SCPNREQ;

import java.util.HashMap;

import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWCHANGEPINAPI {
	
	//Request Parameters
	public final String COMMAND="COMMAND";
	public final String TYPE="TYPE";
	public final String DATE="DATE";
	public final String EXTNWCODE="EXTNWCODE";
	public final String MSISDN="MSISDN";
	public String PIN="OLDPIN";
	public final String NEWPIN="NEWPIN";
	public final String CONFIRMPIN="CONFIRMPIN";
	public final String LOGINID="LOGINID";
	public final String PASSWORD="PASSWORD";
	public final String LANGUAGE1="LANGUAGE1";
	public final String EXTREFNUM="EXTREFNUM";
	public final String REMARKS="REMARKS";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category C2S Transfer API
	 * 
	 */
	private final String API_ChangePIN_RM = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXC2SCPNREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<OLDPIN></OLDPIN>"
			+ "<NEWPIN></NEWPIN>"
			+ "<CONFIRMPIN></CONFIRMPIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<REMARKS></REMARKS>"
			+ "</COMMAND>";
	
	private final String API_ChangePIN_IDEA = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXC2SCPNREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<NEWPIN></NEWPIN>"
			+ "<CONFIRMPIN></CONFIRMPIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<REMARKS></REMARKS>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		if(_masterVO.getClientDetail("CLIENT_NAME").equalsIgnoreCase("IDEA")){
			PIN="PIN";
			return API_ChangePIN_IDEA;}
		else
			return API_ChangePIN_RM;

	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}

}
