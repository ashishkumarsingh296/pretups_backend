package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class ZeroBalCounterSummRptMap {
	Map<String, String> map = new HashMap<>();

	public ZeroBalCounterSummRptMap() {
		map = defaultMap();
	}


	public Map<String,String> defaultMap(){
		Map<String, String> zerobalMap = new HashMap<>();
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();

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

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				userCounter++;
			}
		}

		Object[][] Data = new Object[1][10];
		for (int i = 1; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[0][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i+1);
				Data[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i+1);
				Data[0][2] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
				Data[0][3] = CustomerRechargeCode;
				Data[0][4] = i;
				Data[0][5] = ExcelUtility.getCellData(0, ExcelI.MSISDN,i+1);
				Data[0][6] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i+1);
				Data[0][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i+4);
				Data[0][8] = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i+4);
				Data[0][9] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i+2);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String zone = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, 1);
		


		zerobalMap.put("parentCategory", Data[0][0].toString());
		zerobalMap.put("domainName", Data[0][1].toString());
		zerobalMap.put("loggedInUserName", Data[0][2].toString());
		zerobalMap.put("service", Data[0][3].toString());
		zerobalMap.put("msisdn", Data[0][5].toString());
		zerobalMap.put("userName",Data[0][6].toString());
		zerobalMap.put("zone",zone);
		zerobalMap.put("categoryName",Data[0][7].toString());
		zerobalMap.put("parentcategorycode",Data[0][8].toString());
		zerobalMap.put("geozone",Data[0][8].toString());
		return zerobalMap;
	}

	public Map<String, String> setzerobalMap(String Key, String Value) {	
		Map<String, String> m = new HashMap<>(this.map);
		m.put(Key, Value);
		return m;
	}

	public String getzerobalMap(String Key) {	
		Map<String, String> instanceMap= new HashMap<>(this.map);
		return instanceMap.get(Key);
	}
	
	public Map<String, String> getzerobalMap(){
		Map<String, String> m = new HashMap<>(this.map);
		return m;
	}

}
