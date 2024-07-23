package com.apicontrollers.ussd.suspend;

import java.util.HashMap;

import com.classes.BaseTest;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDSUSPENDDP extends BaseTest{
	
	public static int rowNum;
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSDSUSPENDAPI SuspendAPI = new USSDSUSPENDAPI();
		
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		
		/*ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String MSISDN1 = ExcelUtility.getCellData(0, ExcelI.MSISDN, 1);
		*/
		
		String values[] = new String[2];
		values[0] = "MSISDN";
		values[1] = "PIN";
		String p2p_subscriber[] = DBHandler.AccessHandler.getP2PSubscriberWithStatusY(values);
		
		
		apiData.put(SuspendAPI.TYPE,"SUSREQ");
		
		//apiData.put(SuspendAPI.MSISDN1,MSISDN1);
		//apiData.put(SuspendAPI.PIN,_APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, 1)));
		
		apiData.put(SuspendAPI.MSISDN1,p2p_subscriber[0]);
		String pin = Decrypt.decryption(p2p_subscriber[1]);
		apiData.put(SuspendAPI.PIN,_APIUtil.implementEncryption(pin));
		return apiData;
	}
}
