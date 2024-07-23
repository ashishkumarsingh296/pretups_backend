package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class AutoO2CMap {

	String masterSheetPath;
	int sheetRowCounter;
	public static HashMap<String, String> c2cMap = new HashMap<>();
	public static HashMap<String, String> c2sMap = new HashMap<>();
	public static HashMap<String, String> userData = new HashMap<String, String>();

	private HashMap<String, String> defaultMap(String transactionType) {

		final String methodname = "defaultMap";
		Log.debug("Entered " + methodname + "(" + transactionType + ")");

		masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();
		int userCounter;
		String domainCode = null;
		String toUser = null;
		String c2cAllowed = null;
		String c2sAllowed = null;
		String userMSISDN = null;

		for (userCounter = 1; userCounter <= sheetRowCounter; userCounter++) {
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, userCounter);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(transactionType)) {
				break;
			}
		}

		String toDomain = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, userCounter);
		String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
		String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
		String fromCategoryCode = DBHandler.AccessHandler.getCategoryCode(fromCategory);
		String toCategoryCode = DBHandler.AccessHandler.getCategoryCode(toCategory);

		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USER_CATEGORY_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();
		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter).equals(toCategory)) {
				domainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, userDetailsCounter);
				break;
			}
		}

		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();

		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter).equals(toCategory)) {
				break;
			}
		}
		String channelUser = ExcelUtility.getCellData(0, ExcelI.USER_NAME, userCounter);
		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userCounter);
		String profileID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, userCounter);
		String msisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, userCounter);
		String controlCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, userCounter);
		String pin = ExcelUtility.getCellData(0, ExcelI.PIN, userCounter);
		String controlName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userCounter);
		String tcpID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, userCounter);
		String domainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, userCounter);
		String categoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userCounter);
		String maxTxnAmount = "1000";
		HashMap<String, String> paramMap = new HashMap<>();
		paramMap.put("EXTNWCODE", _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		c2cMap.put("EXTNWCODE", _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		c2sMap.put("EXTNWCODE", _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		paramMap.put("TO_DOMAIN", toDomain);
		paramMap.put("TO_CATEGORY", toCategory);
		paramMap.put("FROM_CATEGORY", fromCategory);
		paramMap.put("ChannelUser", channelUser);
		paramMap.put("Login_ID", loginID);
		paramMap.put("DOMAIN_CODE", domainCode);
		paramMap.put("FROM_CATEGORY_CODE", fromCategoryCode);
		paramMap.put("TO_CATEGORY_CODE", toCategoryCode);
		paramMap.put("PROFILE_ID", profileID);
		paramMap.put("MSISDN", msisdn);
		c2cMap.put("MSISDN1", msisdn);
		c2sMap.put("MSISDN", msisdn);
		paramMap.put("PIN", Decrypt.APIEncryption(pin));
		c2cMap.put("PIN", Decrypt.APIEncryption(pin));
		c2sMap.put("PIN", Decrypt.APIEncryption(pin));
		paramMap.put("CONTROL_CODE", controlCode);
		paramMap.put("maxTxnAmount", maxTxnAmount);
		paramMap.put("controlName", controlName);
		paramMap.put("dailyCount", "1");
		paramMap.put("weeklyCount", "5");
		paramMap.put("monthlyCount", "5");
		userData.put("tcpID", tcpID);
		userData.put("domainName", domainName);
		userData.put("categoryName", categoryName);
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		String geoDomainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		paramMap.put("GEO_DOMAIN", geoDomainName);
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.MASTER_SHEET_NAME);

		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();

		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			if (ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userDetailsCounter).equals(toCategory)
					&& !ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userDetailsCounter).equals(toCategory)
					&& !ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userDetailsCounter)
							.equalsIgnoreCase("Subscriber")) {
				toUser = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userDetailsCounter);
				c2cAllowed = "true";
				break;
			}
		}

		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			if (ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userDetailsCounter).equals(toCategory)
					&& ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userDetailsCounter).equals("Subscriber")) {
				c2sAllowed = "true";
				break;
			}
		}

		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();

		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter).equals(toUser)) {
				userMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter);
				break;
			}
		}

		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.PRODUCT_SHEET);
		c2cMap.put("PRODUCTCODE", ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		c2cMap.put("PRODUCT", ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1));
		c2sMap.put("PRODUCT", ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1));
		String subscriberType = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, 1);
		if (subscriberType.equalsIgnoreCase("POSTPROD"))
			subscriberType = "Postpaid";
		else
			subscriberType = "Prepaid";

		paramMap.put("c2cAllowed", c2cAllowed);
		paramMap.put("c2sAllowed", c2sAllowed);
		c2cMap.put("toUser", toUser);
		c2cMap.put("MSISDN2", userMSISDN);
		c2cMap.put("QTY", "200");

		c2sMap.put("MSISDN2", UniqueChecker.generate_subscriber_MSISDN(subscriberType));
		c2sMap.put("AMOUNT", "1000");

		Log.debug("Exiting " + methodname + "(" + Arrays.asList(paramMap) + ")");
		Log.debug("Exiting " + methodname + "(" + Arrays.asList(c2cMap) + ")");
		Log.debug("Exiting " + methodname + "(" + Arrays.asList(c2sMap) + ")");
		return paramMap;
	}

	public HashMap<String, String> getAutoOperatorToChannelMap(String transactionType) {
		return defaultMap(transactionType);
	}

	public String getAutoO2CMap(String Key, String transactionType) {
		Map<String, String> instanceMap = defaultMap(transactionType);
		return instanceMap.get(Key);
	}

	public HashMap<String, String> getAutoOperatorToChannelMapWithOperatorDetails(String transactionType) {

		HashMap<String, String> returnMap = defaultMap(transactionType);
		Map<String, String> userInfo = UserAccess.getUserWithAccess(RolesI.INITIATE_AUTO_O2C_TRANSFER_ROLECODE);
		returnMap.putAll(userInfo);
		return returnMap;
	}

}
