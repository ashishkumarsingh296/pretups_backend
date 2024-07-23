package com.Features.mapclasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class OperatorUserSpringMap {
	Map<String, String> map = new HashMap<>();

	public OperatorUserSpringMap() {
		map = defaultMap();
	}
	
	public Map<String,String> defaultMap(){

		Map<String, String> c2SEnquiryMap = new HashMap<>();
		String service = null;
		String masterSheetPath=_masterVO.getProperty("DataProvider");
		String customerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for(int i = 1; i<=rowCount; i++){
			if(customerRechargeCode.equalsIgnoreCase(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i))){
				service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
				break;
			}
		}
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 2);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String currentDate = sdf.format(cal.getTime());
		currentDate = currentDate.replaceFirst("0", "");
		c2SEnquiryMap.put("transferID", "R171118.1041.160001");
		c2SEnquiryMap.put("fromDate", currentDate);
		c2SEnquiryMap.put("toDate", currentDate);
		c2SEnquiryMap.put("service", service);
		c2SEnquiryMap.put("senderMSISDN", "1234");
		c2SEnquiryMap.put("receiverMSISDN", "1234");
		c2SEnquiryMap.put("category",category);
		return c2SEnquiryMap;
	}


	public Map<String, String> setOperatorUserMap(String Key, String Value) {
		Map<String, String> m= new HashMap<>(this.map);
		m.put(Key, Value);
		return m;
	}

}
