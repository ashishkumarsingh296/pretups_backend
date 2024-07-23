/**
 * 
 */
package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.commons.RolesI;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils._masterVO;

public class PostpaidBillPaymentMap {

	/**
	 * DataProvider for C2S transfer
	 * @return Object
	 */
	//@DataProvider(name = "categoryData")
	
	HashMap<String, String> c2Sparametermap = new HashMap<>();
	
	public Object[][] TestDataFeed1() {
		String CustomerRechargeCode = _masterVO.getProperty("PostpaidBillPaymentCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
/*
 * Array list to store Categories for which Customer Recharge is allowed
 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(CustomerRechargeCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}

/*
 * Counter to count number of users exists in channel users hierarchy sheet 
 * of Categories for which C2S transfer is allowed
 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		/*int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String category_name = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if (alist1.contains(category_name)&&CommonUtils.roleCodeExistInLinkSheet(RolesI.C2SRECHARGE,category_name)) {
				userCounter++;
			}
		}*/

/*
 * Store required data of 'C2S transfer allowed category' users in Object
 */
		Object[][] Data = new Object[1][6];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String category_name = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if (alist1.contains(category_name) && CommonUtils.roleCodeExistInLinkSheet(RolesI.C2SRECHARGE, category_name)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][3] = CustomerRechargeCode;
				Data[j][4] = i;
				Data[j][5] = ExcelUtility.getCellData(0, ExcelI.MSISDN,i);	
				break;
			}
		}
		
		/*Object[][] Data1 = new Object[1][6];
				Data1[0][0] = Data[0][0];
				Data1[0][1] = Data[0][1];
				Data1[0][2] = Data[0][2];
				Data1[0][3] = Data[0][3];
				Data1[0][4] = Data[0][4];	
				Data1[0][5] = Data[0][5];*/		
		
		
	return Data;
	}

	
	public HashMap<String,String> defaultMap(){
		Object[][] datac2s = TestDataFeed1();
		
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
	
	public HashMap<String, String> getC2SPostpaidMap() {	
		/*Map<String, String> instanceMap = defaultMap();
		return instanceMap.get(Key);*/
		return defaultMap();
}
	
	public Map<String, String> setC2SPostpaidMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
}
	
}
