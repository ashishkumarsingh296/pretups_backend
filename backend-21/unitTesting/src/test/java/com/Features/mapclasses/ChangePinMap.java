package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class ChangePinMap {

	static String newPin;

	public Map<String, String> defaultMap() {

		Map<String, String> paraMeterMap = new HashMap<>();
		newPin = isSMSPinValid();
		String confirmPin = newPin;
		String masterSheetPath=_masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String msisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, 3);
		String oldPin = ExcelUtility.getCellData(0, ExcelI.PIN, 3);
		String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 2);
		String childCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 3);
		String loginId = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 2);
		String userName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, 2);
		String user = (userName +"("+ loginId +")");
		String parentCategory= ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		paraMeterMap.put("newPin", newPin);
		paraMeterMap.put("confirmPin", confirmPin);
		paraMeterMap.put("oldPin", oldPin);
		paraMeterMap.put("msisdn", msisdn);
		paraMeterMap.put("category", category);
		paraMeterMap.put("loginId", loginId);
		paraMeterMap.put("user", user);
		paraMeterMap.put("parentCategory", parentCategory);
		paraMeterMap.put("childCategory", childCategory);
		
		return paraMeterMap;
	}

	public Map<String, String> getChangePinMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
	}

	public static String isSMSPinValid() {
		RandomGeneration randStr = new RandomGeneration();
		int j;
		char pos1 = 0;
		char pos ;
		int result =1;
		String p_smsPin = null;
		while(result!=0){
			int count=0, ctr = 0;
			p_smsPin=randStr.randomNumeric(4);
			for (int i = 0;i < p_smsPin.length(); i++) {
				pos = p_smsPin.charAt(i);

				if (i < p_smsPin.length() - 1) {
					pos1 = p_smsPin.charAt(i + 1);
				}

				j = pos1;
				if (pos == pos1) {
					count++;
				} else if (j == pos + 1 || j == pos - 1) {
					ctr++;
				}
			}

			if (count == p_smsPin.length()) {
				result = -1;Log.info("PIN is same digit: "+p_smsPin);
			} else if (ctr == (p_smsPin.length() - 1)) {
				result = 1;Log.info("PIN is consecutive: "+p_smsPin);
			} else {
				result =0;Log.info("PIN is Valid: " +p_smsPin);
			}}
		return p_smsPin;
	}
}
