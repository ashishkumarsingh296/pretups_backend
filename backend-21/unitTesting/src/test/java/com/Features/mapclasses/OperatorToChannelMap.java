package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

/**
 * @author krishan.chawla
 * This class generates a Default Map for SIT Test Cases with details of user to which Operator to Channel Services are allowed.
 */
public class OperatorToChannelMap {

	String masterSheetPath;
	int sheetRowCounter;
	
	private HashMap<String, String> defaultMap(String transactionType) {
		
		masterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        sheetRowCounter = ExcelUtility.getRowCount();	
        int userCounter;
        
        for (userCounter=1; userCounter<=sheetRowCounter; userCounter++) {
        	String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, userCounter);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
        	if (aList.contains(transactionType)) {
        		break;
        	}
        }
        
        String toDomain = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, userCounter);
        String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
        
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		String productType = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, 1);
		String productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
        
        ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        sheetRowCounter = ExcelUtility.getRowCount();
        
        for (int userDetailsCounter = 1; userDetailsCounter<=sheetRowCounter; userDetailsCounter++) {
        	if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter).equals(toCategory)) {
        		break;
        	}
        }
                
		HashMap<String, String> paramMap = new HashMap<>();
		paramMap.put("TO_DOMAIN", toDomain);
		paramMap.put("TO_PARENT_CATEGORY", ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, userCounter));
		paramMap.put("TO_CATEGORY", toCategory);
		paramMap.put("TO_STOCK_TYPE", PretupsI.O2C_STOCK_TYPE_LOOKUP);
		paramMap.put("TO_CATEGORY_CODE", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, userCounter));
		paramMap.put("TO_USER_NAME", ExcelUtility.getCellData(0, ExcelI.USER_NAME, userCounter));
		paramMap.put("TO_LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userCounter));
		paramMap.put("TO_PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, userCounter));
		paramMap.put("TO_MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, userCounter));
		paramMap.put("TO_PIN", ExcelUtility.getCellData(0, ExcelI.PIN, userCounter));
		paramMap.put("TO_EXTERNAL_CODE", ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, userCounter));
		paramMap.put("TO_GEOGRAPHY", ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, userCounter));
		paramMap.put("TO_GRADE", ExcelUtility.getCellData(0, ExcelI.GRADE, userCounter));
		paramMap.put("TO_CARDGROUP_NAME", ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, userCounter));
		paramMap.put("TO_SA_TCP_NAME", ExcelUtility.getCellData(0, ExcelI.SA_TCP_NAME, userCounter));
		paramMap.put("TO_SA_TCP_ID", ExcelUtility.getCellData(0, ExcelI.SA_TCP_PROFILE_ID, userCounter));
		paramMap.put("TO_NA_TCP_NAME", ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, userCounter));
		paramMap.put("TO_NA_TCP_ID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, userCounter));
		paramMap.put("TO_COMMISSION_PROFILE", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, userCounter));
		paramMap.put("PRODUCT_TYPE", productType);
		paramMap.put("INITIATION_AMOUNT", "101");
		paramMap.put("REMARKS", "O2C Transfer SIT Testing through Automation");
		paramMap.put("COMMTYPE", PretupsI.COMM_TYPE_BASECOMM);
		paramMap.put("CBC_VALUE", "1");
		paramMap.put("CBC_VALUE1", "100");
		paramMap.put("PRODUCT_CODE",productCode);
		paramMap.put("APPLICABLE_FROM_DATE", "CURRENTDATE");
		paramMap.put("APPLICABLE_TO_DATE", "CURRENTDATE");
		paramMap.put("TIME_SLAB", "00:00-23:59");
		paramMap.put("CBC_TYPE", PretupsI.AMOUNT_TYPE_PERCENTAGE);
		paramMap.put("CBC_RATE", "2");

		return paramMap;
	}
	
	public HashMap<String, String> getOperatorToChannelMap(String transactionType) {	
			return defaultMap(transactionType);
	}
	
	public HashMap<String, String> getOperatorToChannelMapWithOperatorDetails(String transactionType) {	
		
		HashMap<String, String> returnMap = defaultMap(transactionType);
		Map<String, String> userInfo = UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		returnMap.putAll(userInfo);		
		return returnMap;
	}
	
public HashMap<String, String> getOperatorToChannelMapWithOperatorDetailsVoucher(String transactionType, String voucherType) {	
		
		HashMap<String, String> returnMap = defaultMap(transactionType);
		Map<String, String> userInfo = null;
		if(voucherType.equalsIgnoreCase("electronic")) {
		userInfo = UserAccess.getUserWithAccessForVoucherType(RolesI.INITIATE_O2C_TRANSFER_ROLECODE,"electronic");
		}
		else if(voucherType.equalsIgnoreCase("physical")) {
		userInfo = UserAccess.getUserWithAccessForVoucherType(RolesI.INITIATE_O2C_TRANSFER_ROLECODE,"physical");
		}
		returnMap.putAll(userInfo);		
		return returnMap;
	}
	
}
