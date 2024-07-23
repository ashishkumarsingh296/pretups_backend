package com.apicontrollers.extgw.channelusermodify;

import java.util.HashMap;
import java.util.Map;

import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.utils.ExtentI;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;



public class EXTGWUSERMODDP{
	
	public HashMap<String, String> getAPIData(){
		
		EXTGWUSERMODAPI userAddAPI = new EXTGWUSERMODAPI();
		HashMap<String, String> apiData = new HashMap<String, String>();
		RandomGeneration rndmGen = new RandomGeneration();
		
		String loginID=ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.LOGIN_ID, 1);
		String[] columnnames = new String[]{"user_name","short_name","user_name_prefix",
											"user_code","external_code","contact_person","contact_no",
											"ssn","address1","address2","city","state","country","email","msisdn"};
		String[] userData = DBHandler.AccessHandler.getUserDetails(loginID, columnnames);

		Map<String, String>userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		apiData.put(userAddAPI.DATE,_APIUtil.getCurrentTimeStamp());
		apiData.put(userAddAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE) );
		apiData.put(userAddAPI.EMPCODE,DBHandler.AccessHandler.getEmpCode(userAccessMap.get("LOGIN_ID")));
		apiData.put(userAddAPI.LOGINID, userAccessMap.get("LOGIN_ID")); 
		apiData.put(userAddAPI.PASSWORD, userAccessMap.get("PASSWORD"));
		apiData.put(userAddAPI.MSISDN, userAccessMap.get("MSISDN"));
		apiData.put(userAddAPI.PIN, userAccessMap.get("PIN"));
		apiData.put(userAddAPI.EXTREFNUM, rndmGen.randomNumberWithoutZero(6));
		apiData.put(userAddAPI.USERMSISDN,userData[14]);
		apiData.put(userAddAPI.USERNAME, userData[0]);
		apiData.put(userAddAPI.SHORTNAME, userData[1]);
		apiData.put(userAddAPI.USERNAMEPREFIX, userData[2]);
		apiData.put(userAddAPI.SUBSCRIBERCODE, userData[3]);
		apiData.put(userAddAPI.EXTERNALCODE, userData[4]);
		apiData.put(userAddAPI.CONTACTPERSON, userData[5]);
		apiData.put(userAddAPI.CONTACTNUMBER, userData[6]);
		apiData.put(userAddAPI.SSN, userData[7]);
		apiData.put(userAddAPI.ADDRESS1, userData[8]);
		apiData.put(userAddAPI.ADDRESS2, userData[9]);
		apiData.put(userAddAPI.CITY, userData[10]);
		apiData.put(userAddAPI.STATE, userData[11]);
		apiData.put(userAddAPI.COUNTRY, userData[12]);
		apiData.put(userAddAPI.EMAILID, userData[13]);
		apiData.put(userAddAPI.WEBLOGINID, "");
		apiData.put(userAddAPI.WEBPASSWORD, "");
		apiData.put(userAddAPI.MSISDN1, userData[14]);
		apiData.put(userAddAPI.MSISDN2, "");
		apiData.put(userAddAPI.MSISDN3, "");
		
		return apiData;
		
	}
	
	public void writeChannelUserData(int rowNum,HashMap<String, String> chnlMap){
		EXTGWUSERMODAPI userAddAPI = new EXTGWUSERMODAPI();
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
