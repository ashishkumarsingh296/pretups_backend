package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class AssociateProfileSpringMap {


	public Map<String, String> defaultMap() {

		Map<String, String> paraMeterMap = new HashMap<>();
		
		String masterSheetPath=_masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String msisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, 3);
		String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 2);
		String childCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 3);
		String loginId = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 2);
		String userName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, 2);
		String user = (userName +"("+ loginId +")");
		String parentCategory= ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		paraMeterMap.put("msisdn", msisdn);
		paraMeterMap.put("category", category);
		paraMeterMap.put("loginId", loginId);
		paraMeterMap.put("user", user);
		paraMeterMap.put("parentCategory", parentCategory);
		paraMeterMap.put("childCategory", childCategory);
		
		return paraMeterMap;
	}

	public Map<String, String> getAssociateProfileMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
	}

}
