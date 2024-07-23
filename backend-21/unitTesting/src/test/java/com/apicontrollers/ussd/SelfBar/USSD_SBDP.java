package com.apicontrollers.ussd.SelfBar;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class USSD_SBDP extends CaseMaster{

	
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSD_SBAPI selfBar = new USSD_SBAPI();
		int dataRowCounter = 0;
		
		 RandomGeneration randomGenerator = new RandomGeneration();
		 GenerateMSISDN gnMsisdn = new GenerateMSISDN();
			/*
			 * Variable initializations
			 */
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
			dataRowCounter = ExcelUtility.getRowCount();
			
			for (int i = 0; i <= dataRowCounter; i ++) {
				String USSDStatus = ExcelUtility.getCellData(0, "USSD", i);
				if (USSDStatus.equalsIgnoreCase("Y")) {
					apiData.put(selfBar.MSISDN1, _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());
					break;
				}
			}
			
			return apiData;
		
	}
	
}
