/**
 * 
 */
package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils._masterVO;

public class RechargeMap {

	/**
	 * DataProvider for C2S transfer
	 * @return Object
	 */
	
	HashMap<String, String> c2Sparametermap = new HashMap<>();
	
	public Object[][] TestDataFeed1(String RechargeCode) {
		String CustomerRechargeCode = _masterVO.getProperty(RechargeCode);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
/*
 * Array list to store Categories for which Recharge is allowed
 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(CustomerRechargeCode)&&DBHandler.AccessHandler.webInterface(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i)).equals("Y")) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}

/*
 * Counter to count number of users exists in channel users hierarchy sheet 
 * of Categories for Recharge is allowed
 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		
/*
 * Store required data of 'Data bundle allowed category' users in Object
 */
		Object[][] Data = new Object[1][6];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][3] = CustomerRechargeCode;
				Data[j][4] = i;
				Data[j][5] = ExcelUtility.getCellData(0, ExcelI.MSISDN,i);
				
				break;
			}
		}
		
				
	return Data;
	}

	
	public HashMap<String,String> defaultMap(String RechargeCode){
		Object[][] datac2s = TestDataFeed1(RechargeCode);
		
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		c2Sparametermap.put("parentCategory", datac2s[0][0].toString());
		c2Sparametermap.put("fromCategory", datac2s[0][1].toString());
		c2Sparametermap.put("fromPIN", datac2s[0][2].toString());
		c2Sparametermap.put("service", datac2s[0][3].toString());
		c2Sparametermap.put("fromRowNum", datac2s[0][4].toString());
		c2Sparametermap.put("fromMSISDN", datac2s[0][5].toString());
		
		int fromrownum = Integer.parseInt(c2Sparametermap.get("fromRowNum"));
		
		c2Sparametermap.put("fromTCPName", ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, fromrownum));
		c2Sparametermap.put("fromTCPID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, fromrownum));
		c2Sparametermap.put("fromDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, fromrownum));
		c2Sparametermap.put("fromLoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, fromrownum));
		c2Sparametermap.put("fromCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, fromrownum));
		c2Sparametermap.put("fromGrade", ExcelUtility.getCellData(0, ExcelI.GRADE,fromrownum));
		c2Sparametermap.put("fromCommProfile", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, fromrownum));
		c2Sparametermap.put("domainCode",ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USER_CATEGORY_SHEET, ExcelI.DOMAIN_CODE, new String[]{ExcelI.DOMAIN_NAME}, new String[]{c2Sparametermap.get("fromDomain")}));
		
		
		
		return c2Sparametermap;
		
	}
	
	public HashMap<String, String> getC2SMap(String RechargeCode) {	
		return defaultMap(RechargeCode);
}
	
	public Map<String, String> setC2SMap(String RechargeCode,String[] Key, String[] Value) {	
		Map<String, String> instanceMap = defaultMap(RechargeCode);
		for(int i=0;i<Key.length;i++){
		instanceMap.put(Key[i], Value[i]);}
		return instanceMap;
}
	
}
