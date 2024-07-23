package com.apicontrollers.extgw.channelusercreation_USERADDREQ;

import java.util.HashMap;
import java.util.Map;

import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.utils.ExtentI;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWUSERADDDP{
	
	public HashMap<String, String> getAPIData(){
		
		EXTGWUSERADDAPI userAddAPI = new EXTGWUSERADDAPI();
		HashMap<String, String> apiData = new HashMap<String, String>();
		RandomGeneration rndmGen = new RandomGeneration();
		
		Map<String, String>userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		apiData.put(userAddAPI.DATE,_APIUtil.getCurrentTimeStamp());
		apiData.put(userAddAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE) );
		apiData.put(userAddAPI.EMPCODE,DBHandler.AccessHandler.getEmpCode(userAccessMap.get("LOGIN_ID")));
		apiData.put(userAddAPI.LOGINID, userAccessMap.get("LOGIN_ID")); 
		apiData.put(userAddAPI.PASSWORD, userAccessMap.get("PASSWORD"));
		apiData.put(userAddAPI.MSISDN, userAccessMap.get("MSISDN"));
		apiData.put(userAddAPI.PIN, userAccessMap.get("PIN"));
		apiData.put(userAddAPI.EXTREFNUM, rndmGen.randomNumberWithoutZero(6));
		apiData.put(userAddAPI.GEOGRAPHYCODE, "");
		apiData.put(userAddAPI.PARENTMSISDN, "");
		apiData.put(userAddAPI.PARENTEXTERNALCODE, "");
		apiData.put(userAddAPI.USERCATCODE, "");
		apiData.put(userAddAPI.USERNAME, "AUTFN" + rndmGen.randomNumeric(4)+" "+"AUTLN" + rndmGen.randomNumeric(4));
		apiData.put(userAddAPI.SHORTNAME, "AUTSN" + rndmGen.randomNumeric(4));
		apiData.put(userAddAPI.USERNAMEPREFIX, "MR");
		apiData.put(userAddAPI.SUBSCRIBERCODE, rndmGen.randomNumeric(6));
		apiData.put(userAddAPI.EXTERNALCODE, UniqueChecker.UC_EXTCODE());
		apiData.put(userAddAPI.CONTACTPERSON, rndmGen.randomAlphabets(6));
		apiData.put(userAddAPI.CONTACTNUMBER, rndmGen.randomNumeric(6));
		apiData.put(userAddAPI.SSN, "");
		apiData.put(userAddAPI.ADDRESS1, "Add1" + rndmGen.randomNumeric(4));
		apiData.put(userAddAPI.ADDRESS2, "Add2" + rndmGen.randomNumeric(4));
		apiData.put(userAddAPI.CITY, "City" + rndmGen.randomNumeric(4));
		apiData.put(userAddAPI.STATE, "State" + rndmGen.randomNumeric(4));
		apiData.put(userAddAPI.COUNTRY, "Country" + rndmGen.randomNumeric(2));
		apiData.put(userAddAPI.EMAILID, rndmGen.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		apiData.put(userAddAPI.WEBLOGINID, UniqueChecker.UC_LOGINID());
		apiData.put(userAddAPI.WEBPASSWORD, _masterVO.getProperty("Password"));
		apiData.put(userAddAPI.MSISDN1, UniqueChecker.UC_MSISDN());
		apiData.put(userAddAPI.MSISDN2, "");
		apiData.put(userAddAPI.MSISDN3, "");
		
		return apiData;
		
	}
	
	public void writeChannelUserData(int rowNum,HashMap<String, String> chnlMap){
		EXTGWUSERADDAPI userAddAPI = new EXTGWUSERADDAPI();
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, rowNum, chnlMap.get(userAddAPI.USERNAME));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.LOGIN_ID, rowNum, chnlMap.get(userAddAPI.WEBLOGINID));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, rowNum, chnlMap.get(userAddAPI.WEBPASSWORD));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, rowNum, chnlMap.get(userAddAPI.MSISDN1));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, rowNum, chnlMap.get(userAddAPI.EXTERNALCODE));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PIN, rowNum, chnlMap.get(userAddAPI.PIN));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.GRADE, rowNum, chnlMap.get(ExcelI.GRADE));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.NA_TCP_PROFILE_ID, rowNum, chnlMap.get(ExcelI.NA_TCP_PROFILE_ID));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.NA_TCP_NAME, rowNum, chnlMap.get(ExcelI.NA_TCP_NAME));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.COMMISSION_PROFILE, rowNum, chnlMap.get(ExcelI.COMMISSION_PROFILE));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.GEOGRAPHY, rowNum, chnlMap.get(ExcelI.GEOGRAPHY));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.CARDGROUP_NAME, rowNum, ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.CARDGROUP_NAME, rowNum));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.SA_TCP_NAME, rowNum, ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.SA_TCP_NAME, rowNum));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.SA_TCP_PROFILE_ID, rowNum, ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.SA_TCP_PROFILE_ID, rowNum));	
	}
	
}
