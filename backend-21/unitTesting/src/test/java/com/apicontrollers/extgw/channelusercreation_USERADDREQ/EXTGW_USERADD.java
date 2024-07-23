package com.apicontrollers.extgw.channelusercreation_USERADDREQ;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.CacheUpdate;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.TransferControlProfile;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGW_O2CDAO;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.CacheController;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_USERADD extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test(dataProvider="extgwDataProvider")
	public void _01_addUserAllDetails(int rowNum,String domainName, String parentCategoryName,String categoryCode,String geography) throws IOException{
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ1");
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("POPTCREATION2");
	CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("POPTCREATION3");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),domainName,categoryCode,parentCategoryName));
		currentNode.assignCategory(extentCategory);
		String webAccess = DBHandler.AccessHandler.webInterface(DBHandler.AccessHandler.getCategoryName(categoryCode)).toUpperCase();
		
		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		if(!DBHandler.AccessHandler.pinPreferenceForTXN("Operator").equals("Y"))
		{
			extgwUserAddMap.put(iAPI.MSISDN, "");
			extgwUserAddMap.put(iAPI.PIN, "");
		}
		if(webAccess.equals("N")){
			extgwUserAddMap.put(iAPI.WEBLOGINID, "");
			extgwUserAddMap.put(iAPI.WEBPASSWORD, "");
		}
		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		if(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString().equals(CaseMaster.getErrorCode())){
			extgwUserAddMap.put(iAPI.PIN, DBHandler.AccessHandler.fetchUserPIN(extgwUserAddMap.get(iAPI.WEBLOGINID), extgwUserAddMap.get(iAPI.MSISDN1)));

			String[] dataRequired= new String[]{"USER_GRADE","TRANSFER_PROFILE_ID","PROFILE_NAME","COMM_PROFILE_SET_NAME","GRPH_DOMAIN_NAME"};
			String[] dataFetched = DBHandler.AccessHandler.getUserDetails_combined(extgwUserAddMap.get(iAPI.WEBLOGINID), extgwUserAddMap.get(iAPI.MSISDN1), dataRequired);
			extgwUserAddMap.put(ExcelI.GRADE, dataFetched[0]);
			extgwUserAddMap.put(ExcelI.NA_TCP_PROFILE_ID, dataFetched[1]);
			extgwUserAddMap.put(ExcelI.NA_TCP_NAME, dataFetched[2]);
			extgwUserAddMap.put(ExcelI.COMMISSION_PROFILE, dataFetched[3]);
			extgwUserAddMap.put(ExcelI.GEOGRAPHY, dataFetched[4]);

			iMap.writeChannelUserData(rowNum, extgwUserAddMap);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),categoryCode));
		currentNode.assignCategory(extentCategory);
		
		if(webAccess.equals("Y")){
		new ChannelUser(driver).changeUserFirstTimePassword(extgwUserAddMap.get(iAPI.WEBLOGINID), extgwUserAddMap.get(iAPI.WEBPASSWORD), _masterVO.getProperty("NewPassword"));
		extgwUserAddMap.put(iAPI.WEBPASSWORD,_masterVO.getProperty("NewPassword"));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, rowNum, extgwUserAddMap.get(iAPI.WEBPASSWORD));
		}else{currentNode.log(Status.SKIP, "WEB ACCESS is not allowed to this category user.");}
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),categoryCode));
		currentNode.assignCategory(extentCategory);
		String PIN = DBHandler.AccessHandler.fetchUserPIN(extgwUserAddMap.get(iAPI.WEBLOGINID), extgwUserAddMap.get(iAPI.MSISDN1));
		new ChannelUser(driver).changeUserFirstTimePIN(extgwUserAddMap.get(iAPI.WEBLOGINID), extgwUserAddMap.get(iAPI.MSISDN1), PIN, _masterVO.getProperty("NewPIN"));
		ExtentI.insertValueInDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PIN, rowNum,_masterVO.getProperty("NewPIN"));

			EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
			HashMap<String, String> apiData = EXTGWDATAPROVIDER.O2C_getAPIdata();
		//	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryCode));
		//	currentNode.assignCategory(extentCategory);
			apiData.put(O2CTransferAPI.EXTCODE, "");
			apiData.put(O2CTransferAPI.MSISDN,extgwUserAddMap.get(iAPI.MSISDN1));
			apiData.put(O2CTransferAPI.PIN,_masterVO.getProperty("NewPIN"));

			 API = O2CTransferAPI.prepareAPI(apiData);
			 APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			// xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			//Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}}
	
	@Test
	public void _02_addUseronlyempcode() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ2");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),domainName,categoryCode,parentCategoryName));
		currentNode.assignCategory(extentCategory);

		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.LOGINID, "");
		extgwUserAddMap.put(iAPI.PASSWORD, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");

		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void _03_addUseronlyLoginPassword() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ3");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),domainName,categoryCode,parentCategoryName));
		currentNode.assignCategory(extentCategory);

		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.EMPCODE, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");

		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void _04_addUserNoDefaultTCP() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ4");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),domainName,categoryCode,parentCategoryName));
		currentNode.assignCategory(extentCategory);
		String[] tcpdata = DBHandler.AccessHandler.defaultTCP(categoryCode);
		DBHandler.AccessHandler.updateAnyColumnValue(CONSTANT.TRANSFER_PROFILE, CONSTANT.IS_DEFAULT, "N", CONSTANT.PROFILE_ID, tcpdata[0]);
		new UpdateCache().updateCache();
		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.EMPCODE, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");

		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		DBHandler.AccessHandler.updateAnyColumnValue(CONSTANT.TRANSFER_PROFILE, CONSTANT.IS_DEFAULT, "Y", CONSTANT.PROFILE_ID, tcpdata[0]);
		new UpdateCache().updateCache();
	}
	
	@Test
	public void _05_addUserNoDefaultCommission() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ5");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),domainName,categoryCode,parentCategoryName));
		currentNode.assignCategory(extentCategory);
		String[] commdata = DBHandler.AccessHandler.defaultCommission(categoryCode, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		DBHandler.AccessHandler.updateAnyColumnValue(CONSTANT.COMMISSION_PROFILE_SET, CONSTANT.IS_DEFAULT, "N", CONSTANT.COMM_PROFILE_SET_ID, commdata[0]);
		new UpdateCache().updateCache();
		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.EMPCODE, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");

		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		DBHandler.AccessHandler.updateAnyColumnValue(CONSTANT.COMMISSION_PROFILE_SET, CONSTANT.IS_DEFAULT, "Y", CONSTANT.COMM_PROFILE_SET_ID, commdata[0]);
		new UpdateCache().updateCache();
	}
	
	@Test
	public void _06_addUserNoDefaultGrade() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ6");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),domainName,categoryCode,parentCategoryName));
		currentNode.assignCategory(extentCategory);
		String[] commdata = DBHandler.AccessHandler.defaultGrade(categoryCode);
		DBHandler.AccessHandler.updateAnyColumnValue(CONSTANT.CHANNEL_GRADES, CONSTANT.IS_DEFAULT_GRADE, "N", CONSTANT.GRADE_CODE, commdata[0]);
		new UpdateCache().updateCache();
		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.EMPCODE, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");

		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		DBHandler.AccessHandler.updateAnyColumnValue(CONSTANT.CHANNEL_GRADES, CONSTANT.IS_DEFAULT_GRADE, "Y", CONSTANT.GRADE_CODE, commdata[0]);
		new UpdateCache().updateCache();
	}
	
	@Test
	public void _07_o2ctonewlyaddeduser() throws IOException{
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ7");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWDATAPROVIDER.O2C_getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
			EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
			currentNode.assignCategory(extentCategory);
			apiData.put(O2CTransferAPI.EXTCODE, "");
		
			String API = O2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
	}
	
	@Test
	public void _08_c2ctransferextgwusers(){
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ8");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWDATAPROVIDER.C2C_getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.PASSWORD, "");

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void _09_c2ctransferTCPsuspended(){
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ9");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWDATAPROVIDER.C2C_getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWDATAPROVIDER.FROM_Domain, EXTGWDATAPROVIDER.FROM_Category, EXTGWDATAPROVIDER.FROM_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWDATAPROVIDER.FROM_Domain, EXTGWDATAPROVIDER.FROM_Category, EXTGWDATAPROVIDER.FROM_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	@Test
	public void _10_c2ctransferTCPsuspended(){
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ10");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWDATAPROVIDER.C2C_getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWDATAPROVIDER.TO_Domain, EXTGWDATAPROVIDER.TO_Category, EXTGWDATAPROVIDER.TO_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWDATAPROVIDER.TO_Domain, EXTGWDATAPROVIDER.TO_Category, EXTGWDATAPROVIDER.TO_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	
	@Test
	public void _11_c2ctransfercommsuspended() throws InterruptedException{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ11");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		
		if (!TestCaseCounter) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		CommissionProfile commProfile = new CommissionProfile(driver);
		HashMap<String, String> apiData = EXTGWDATAPROVIDER.C2C_getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), EXTGWDATAPROVIDER.TO_CommissionProfileName,EXTGWDATAPROVIDER.TO_Category));
		currentNode.assignCategory(extentCategory);	
		
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Commission Profile:"+EXTGWDATAPROVIDER.TO_CommissionProfileName);
		Object[] idefault = commProfile.suspendcommissionProfileStatus(EXTGWDATAPROVIDER.TO_Domain, EXTGWDATAPROVIDER.TO_Category, EXTGWDATAPROVIDER.TO_Grade, EXTGWDATAPROVIDER.TO_CommissionProfileName);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Commission Profile:"+EXTGWDATAPROVIDER.TO_CommissionProfileName);
		commProfile.resumecommissionProfileStatus(EXTGWDATAPROVIDER.TO_Domain, EXTGWDATAPROVIDER.TO_Category, EXTGWDATAPROVIDER.TO_Grade, EXTGWDATAPROVIDER.TO_CommissionProfileName,(boolean)idefault[0]);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
	}
	
	@Test
	public void _12_c2ctransfercommsuspended() throws InterruptedException{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ12");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		
		if (!TestCaseCounter) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		CommissionProfile commProfile = new CommissionProfile(driver);
		HashMap<String, String> apiData = EXTGWDATAPROVIDER.C2C_getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), EXTGWDATAPROVIDER.FROM_CommissionProfileName,EXTGWDATAPROVIDER.FROM_Category));
		currentNode.assignCategory(extentCategory);	
		
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Commission Profile:"+EXTGWDATAPROVIDER.FROM_CommissionProfileName);
		Object[] idefault = commProfile.suspendcommissionProfileStatus(EXTGWDATAPROVIDER.FROM_Domain, EXTGWDATAPROVIDER.FROM_Category, EXTGWDATAPROVIDER.FROM_Grade, EXTGWDATAPROVIDER.FROM_CommissionProfileName);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Commission Profile:"+EXTGWDATAPROVIDER.FROM_CommissionProfileName);
		commProfile.resumecommissionProfileStatus(EXTGWDATAPROVIDER.FROM_Domain, EXTGWDATAPROVIDER.FROM_Category, EXTGWDATAPROVIDER.FROM_Grade, EXTGWDATAPROVIDER.FROM_CommissionProfileName,(boolean)idefault[0]);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
	}
	
	@Test
	public void _13_o2ctransferTCPsuspended(){
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ13");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWDATAPROVIDER.O2C_getAPIdata();
		String API = O2CTransferAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWDATAPROVIDER.Domain, EXTGWDATAPROVIDER.CUCategory, EXTGWDATAPROVIDER.TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWDATAPROVIDER.Domain, EXTGWDATAPROVIDER.CUCategory, EXTGWDATAPROVIDER.TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	@Test
	public void _14_O2ctransfercommsuspended() throws InterruptedException{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ14");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		
		if (!TestCaseCounter) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		CommissionProfile commProfile = new CommissionProfile(driver);
		HashMap<String, String> apiData = EXTGWDATAPROVIDER.O2C_getAPIdata();
		String API = O2CTransferAPI.prepareAPI(apiData);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), EXTGWDATAPROVIDER.CommProfile,EXTGWDATAPROVIDER.CUCategory));
		currentNode.assignCategory(extentCategory);	
		
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Commission Profile:"+EXTGWDATAPROVIDER.CommProfile);
		Object[] idefault = commProfile.suspendcommissionProfileStatus(EXTGWDATAPROVIDER.Domain, EXTGWDATAPROVIDER.CUCategory, EXTGWDATAPROVIDER.Grade, EXTGWDATAPROVIDER.CommProfile);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Commission Profile:"+EXTGWDATAPROVIDER.CommProfile);
		commProfile.resumecommissionProfileStatus(EXTGWDATAPROVIDER.Domain, EXTGWDATAPROVIDER.CUCategory, EXTGWDATAPROVIDER.Grade, EXTGWDATAPROVIDER.CommProfile,(boolean)idefault[0]);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
	}
	
	@Test
	public void _15_existingLoginID() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ15");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.LOGINID, "");
		extgwUserAddMap.put(iAPI.PASSWORD, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");
		extgwUserAddMap.put(iAPI.WEBLOGINID, DBHandler.AccessHandler.existingLoginID());
		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void _16_existingMSISDN() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ16");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.LOGINID, "");
		extgwUserAddMap.put(iAPI.PASSWORD, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");
		extgwUserAddMap.put(iAPI.MSISDN1, DBHandler.AccessHandler.existingMSISDN());
		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void _17_deletedMSISDN() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ17");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.LOGINID, "");
		extgwUserAddMap.put(iAPI.PASSWORD, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");
		extgwUserAddMap.put(iAPI.MSISDN1,DBHandler.AccessHandler.deletedMSISDN());
		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void _18_existingExternalCode() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ18");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.LOGINID, "");
		extgwUserAddMap.put(iAPI.PASSWORD, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");
		extgwUserAddMap.put(iAPI.EXTERNALCODE,DBHandler.AccessHandler.existingEXTCODE());
		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void _19_msisdnlessthanminlen() throws IOException{
		Object data[][] = DomainCategoryProvider();
		String domainName=data[0][1].toString(); String parentCategoryName=data[0][2].toString();
		String categoryCode=data[0][3].toString();String geography=data[0][4].toString();

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERADDREQ19");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERADDDP iMap= new EXTGWUSERADDDP();
		EXTGWUSERADDAPI iAPI = new EXTGWUSERADDAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String,String> extgwUserAddMap = new HashMap<String, String>();
		extgwUserAddMap=iMap.getAPIData();
		String geoCode = DBHandler.AccessHandler.getGeoCode(geography);
		extgwUserAddMap.put(iAPI.GEOGRAPHYCODE,geoCode);
		if(!parentCategoryName.equalsIgnoreCase("Root")){
			String pName = new Login().ParentName(driver, "ExtgwChannel", domainName, parentCategoryName);
			int row=ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"),ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, pName);
			extgwUserAddMap.put(iAPI.PARENTMSISDN,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row));
			extgwUserAddMap.put(iAPI.PARENTEXTERNALCODE,ExtentI.fetchValuefromDataProviderSheet(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.EXTERNAL_CODE, row));
		}
		extgwUserAddMap.put(iAPI.LOGINID, "");
		extgwUserAddMap.put(iAPI.PASSWORD, "");
		extgwUserAddMap.put(iAPI.MSISDN, "");
		extgwUserAddMap.put(iAPI.PIN, "");
		extgwUserAddMap.put(iAPI.USERCATCODE, categoryCode);
		
		int minMSISDNLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_MSISDN_LENGTH"));
		int remainingMSISDN = minMSISDNLength-SystemPreferences.MSISDN_PREFIX_LENGTH-1;
		String prefix = _masterVO.getMasterValue("Prepaid MSISDN Prefix");
		String minMSISDN = prefix + new RandomGeneration().randomNumberWithoutZero(remainingMSISDN);
		extgwUserAddMap.put(iAPI.MSISDN1,minMSISDN);
		
		String API = iAPI.prepareAPI(extgwUserAddMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERADDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@DataProvider(name="extgwDataProvider")
	public Object[][] DomainCategoryProvider() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][5];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			categoryData[j][4] = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i);

		}
		return categoryData;
	}
}
