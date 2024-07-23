package com.Features.mapclasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

          

public class C2CEnquirySpringMap {
	
	Map<String, String> map = new HashMap<>();

	public C2CEnquirySpringMap() {
		map = defaultMap();
	}


	public Map<String,String> defaultMap(){

		Map<String, String> c2CEnquiryMap = new HashMap<>();
		String masterSheetPath=_masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String currentDate = sdf.format(cal.getTime());
		currentDate = currentDate.replaceFirst("^0+(?!$)", "");
		c2CEnquiryMap.put("transferNumber", "CT171121.1010.780001");
		c2CEnquiryMap.put("fromDate", currentDate);
		c2CEnquiryMap.put("toDate", currentDate);
		c2CEnquiryMap.put("transferType", "ALL");
		c2CEnquiryMap.put("fromUserMSISDN", "1234");
		c2CEnquiryMap.put("toUserMSISDN", "1234");
		c2CEnquiryMap.put("category",category);
		return c2CEnquiryMap;
	}


	public Map<String, String> setC2CEnquiryMap(String Key, String Value) {
		Map<String, String> m= new HashMap<>(this.map);
		m.put(Key, Value);
		return m;
	}

	
}
