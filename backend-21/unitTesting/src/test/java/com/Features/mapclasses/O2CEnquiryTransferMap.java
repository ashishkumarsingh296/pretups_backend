package com.Features.mapclasses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class O2CEnquiryTransferMap{
	
	Map<String, String> map = new HashMap<>();

	public O2CEnquiryTransferMap() throws SQLException {
		map = defaultMap();
	}


	public Map<String,String> defaultMap() throws SQLException{

		Map<String, String> O2CEnquiryMap = new HashMap<>();
		String masterSheetPath =_masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.PRODUCT_SHEET);
		String productType = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, 1);
		String product;
		if(productType.equals("PREPROD"))
			product = "Prepaid Product";
		else
			product = "Postpaid Product";
		ResultSet rs = DBHandler.AccessHandler.getProductNameByType("TRFTY");
		rs.next();
		String transferCategory = rs.getString("lookup_name");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 2);
		String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, 2);
		String userName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, 2);
		String userMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, 2);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String currentDate = sdf.format(cal.getTime());
		currentDate = currentDate.replaceFirst("^0+(?!$)", "");
		O2CEnquiryMap.put("transferNum", "OT171117.1921.920001");
		O2CEnquiryMap.put("msisdn","1234");
		O2CEnquiryMap.put("transferCategory",transferCategory);
		O2CEnquiryMap.put("category",category);
		O2CEnquiryMap.put("fromDate", currentDate);
		O2CEnquiryMap.put("toDate", currentDate);
		O2CEnquiryMap.put("productType",product);
		O2CEnquiryMap.put("productTypeCode",productType);
		O2CEnquiryMap.put("transferType", "ALL");
		O2CEnquiryMap.put("domainCategory", category);
		O2CEnquiryMap.put("orderstatus", "ALL");
		O2CEnquiryMap.put("userName", userName);
		O2CEnquiryMap.put("userMSISDN", userMSISDN);
		O2CEnquiryMap.put("parentCategory", parentCategory);
		return O2CEnquiryMap;
	}


	public Map<String, String> setO2CEnquiryMap(String Key, String Value) {
		Map<String, String> m= new HashMap<>(this.map);
		m.put(Key, Value);
		return m;
	}
	
	public Map<String, String> getO2CEnquiryMap() {	
		Map<String, String> m= new HashMap<>(this.map);
		return m;
}


}
