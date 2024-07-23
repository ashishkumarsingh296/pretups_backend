package com.apicontrollers.extgw.SelfBar;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class EXTGW_SelfBar_DP extends CaseMaster {

	
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_SelfBar_API selfBar = new EXTGW_SelfBar_API();
		int dataRowCounter = 0;
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		
		 RandomGeneration randomGenerator = new RandomGeneration();
			/*
			 * Variable initializations
			 */
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
			dataRowCounter = ExcelUtility.getRowCount();
			
			for (int i = 0; i <= dataRowCounter; i ++) {
				String EXTGWStatus = ExcelUtility.getCellData(0, "EXTGW", i);
				if (EXTGWStatus.equalsIgnoreCase("Y")) {
					apiData.put(selfBar.MSISDN1, _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());
					break;
				}
			}
			
			return apiData;
		
	}
	

	
	

}
