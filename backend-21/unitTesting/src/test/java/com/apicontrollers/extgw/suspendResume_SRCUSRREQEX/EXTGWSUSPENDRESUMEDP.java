package com.apicontrollers.extgw.suspendResume_SRCUSRREQEX;

import java.io.IOException;
import java.util.HashMap;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.ExcelUtility;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWSUSPENDRESUMEDP extends BaseTest{
	
	public static int rowNum;
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWSUSPENDRESUMEAPI SuspendResumeAPI = new EXTGWSUSPENDRESUMEAPI();
		
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String MSISDN2 = ExcelUtility.getCellData(0, ExcelI.MSISDN, 1);
		apiData.put(SuspendResumeAPI.PIN,_APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, 1)));
		apiData.put(SuspendResumeAPI.NETWORK, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(SuspendResumeAPI.MSISDN2, MSISDN2);	
		apiData.put(SuspendResumeAPI.TYPE,"SRCUSRREQEX");
		apiData.put(SuspendResumeAPI.LANGUAGE1,"0");
		apiData.put(SuspendResumeAPI.ACTION,"S/R");
		
		String categoryCode="BCU";
		int optrow=0;
		try {
			optrow = ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, categoryCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		apiData.put(SuspendResumeAPI.MSISDN1,ExcelUtility.getCellData(0, ExcelI.MSISDN, optrow));
		//apiData.put(SuspendResumeAPI.PIN,_APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, optrow)));
		return apiData;
	}
}
