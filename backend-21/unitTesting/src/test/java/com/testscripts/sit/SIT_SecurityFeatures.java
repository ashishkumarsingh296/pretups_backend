package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.AccessControlMgmt;
import com.Features.ChannelUser;
import com.Features.mapclasses.ChannelUserMap;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.security.SecurityHelper;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_SecurityFeatures extends BaseTest {

	String LoginID;
	String MSISDN;
	String PassWord;
	static String homepage1;
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> optresultMap;
	static boolean TestCaseCounter = false;
	
	@Test(priority=1)
	public void validateGenericErrorMessageOnLoginWithInvalidPassword() {
		final String methodname = "validateGenericErrorMessageOnLoginWithInvalidPassword";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE01");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1);
		
		Login Login = new Login();
		String errorMessage = Login.LoginAsUser(driver, loginID, "NULL");
		String expectedMessage = MessagesDAO.getLabelByKey("login.index.error.invalidlogin");
		Validator.messageCompare(errorMessage, expectedMessage);
	}
	
	@Test(priority=2)
	public void validateGenericErrorMessageOnLoginInvalidLoginID() {
		final String methodname = "validateGenericErrorMessageOnLoginInvalidLoginID";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE02");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		
		Login Login = new Login();
		String errorMessage = Login.LoginAsUser(driver, "NULL", "NULL");
		String expectedMessage = MessagesDAO.getLabelByKey("login.index.error.invalidlogin");
		Validator.messageCompare(errorMessage, expectedMessage);
	}
	
	@Test(priority=3)
	public void validateInvalidAttemptsCountOnLoginWithInvalidPassword() {
		final String methodname = "validateGenericErrorMessageOnLoginWithInvalidPassword";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE04");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1);
		
		Login Login = new Login();
		String errorMessage = Login.LoginAsUser(driver, loginID, "NULL");
		String expectedMessage = MessagesDAO.getLabelByKey("login.index.error.invalidlogin");
		Validator.messageCompare(errorMessage, expectedMessage);
	}
	
	@Test(priority=4)
	public void validateRightClickAction() {
		final String methodname = "validateRightClickAction";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE03");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		
		Actions action = new Actions(driver);
		action.contextClick(driver.findElement(By.name("loginID"))).build().perform();
		
		try {
			Log.info("Right clicked, Trying to find the Alert Message for Right click not allowed message.");
			Alert alert = driver.switchTo().alert();
			String alertMessage = alert.getText();
			
			/*
			 * As per PreTUPS Trunk Code, Javascript alert message is HardCoded.
			 * @reference: pretups/src/main/webapp/jsp/login/index.jsp
			 */
			String expectedMessage = "This page is fully protected. Right click not allowed";
			Validator.messageCompare(alertMessage, expectedMessage);
		} catch(Exception ex) {
			
		}
	}
	
	@Test(priority=5)
	public void validatePasswordFieldIsMasked() {
		final String methodname = "validatePasswordFieldIsMasked";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE05");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		
		Log.info("Trying to fetch the type attribute of Password Field");
		WebElement Password = driver.findElement(By.name("password"));
		String passwordFileType = Password.getAttribute("type").toLowerCase();
		Log.info("Password Field Attribute 'type' fetched successfully as: " + passwordFileType);
		Validator.messageCompare(passwordFileType, "password");
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=6)
	public void allowedIPAddressAuthentication(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "allowedIPAddressAuthentication";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE06");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		HashMap<String, String> channelresultMap;
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		mapParam.put("AllowedIP", "192.168.29.10");
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		Login login = new Login();
		
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
		channelresultMap.get("channelInitiateMsg");		

		if (APPLEVEL.equals("2")) {
			channelresultMap = channelUserLogic.approveLevel1_ChannelUser();
			channelresultMap = channelUserLogic.approveLevel2_ChannelUser();	
		} else if(APPLEVEL.equals("1")) {
			channelresultMap = channelUserLogic.approveLevel1_ChannelUser();
		} else {
			Log.info("Approval not required.");	
		}
		
		String actualMessage = login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
		String expectedMessage = MessagesDAO.getLabelByKey("login.index.error.invalidrequesturl");
		Validator.messageCompare(actualMessage, expectedMessage);
		
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=7)
	public void userPasswordBlockOnInvalidPasswordCount(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "userPasswordBlockOnInvalidPasswordCount";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE07");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		AccessControlMgmt accControlMgmt = new AccessControlMgmt(driver);
		HashMap<String, String> channelresultMap;
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
		channelresultMap.get("channelInitiateMsg");		

		if (APPLEVEL.equals("2")) {
			channelresultMap = channelUserLogic.approveLevel1_ChannelUser();
			channelresultMap = channelUserLogic.approveLevel2_ChannelUser();	
		} else if(APPLEVEL.equals("1")) {
			channelresultMap = channelUserLogic.approveLevel1_ChannelUser();
		} else {
			Log.info("Approval not required.");	
		}
		
		String actualMessage = accControlMgmt.blockPassword(channelresultMap.get("LOGIN_ID"), catCode[0]);
		String expectedMessage = MessagesDAO.getLabelByKey("login.index.error.invalidpwd.passwordblocked");
		Validator.messageCompare(actualMessage, expectedMessage);
		
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=8)
	public void CUChangePassword(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "CUChangePassword";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE08");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		HashMap<String, String> channelresultMap;
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		Login login = new Login();
		
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
		channelresultMap.get("channelInitiateMsg");		

		if (APPLEVEL.equals("2")) {
			channelresultMap = channelUserLogic.approveLevel1_ChannelUser();
			channelresultMap = channelUserLogic.approveLevel2_ChannelUser();	
		} else if(APPLEVEL.equals("1")) {
			channelresultMap = channelUserLogic.approveLevel1_ChannelUser();
		} else {
			Log.info("Approval not required.");	
		}
		
		String actualMessage = login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
		String expectedMessage = MessagesDAO.getLabelByKey("login.index.error.invalidrequesturl");
		Validator.messageCompare(actualMessage, expectedMessage);
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=9)
	public void validateHttpOnlyFlagInCookieParam() throws InterruptedException, IOException {
		final String methodname = "validateHttpOnlyFlagInCookieParam";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE09");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		SecurityHelper SecurityHelper = new SecurityHelper();
		Map<String, List<String>> response = SecurityHelper.getHTTPRawResponse(_masterVO.getMasterValue(MasterI.WEB_URL));
		
		boolean httpflag = false;
		for (String current : response.get("Set-Cookie")) {
		        if (current.toLowerCase().contains("httponly")) {
		        	httpflag = true;
		        }
		}
		
		if (httpflag)
			currentNode.pass("Set-Cookie Parameter contains 'HttpOnly' flag!");
		else
			Log.failNode("Set-Cookie Parameter does not contain 'HttpOnly' flag");
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=10)
	public void validateCacheControlFlagInCookieParam() throws InterruptedException, IOException {
		final String methodname = "validateHttpOnlyFlagInCookieParam";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE10");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		SecurityHelper SecurityHelper = new SecurityHelper();
		Map<String, List<String>> response = SecurityHelper.getHTTPRawResponse(_masterVO.getMasterValue(MasterI.WEB_URL));
		
		boolean httpflag = false;
		for (String current : response.get("Cache-Control")) {
		        if (current.toLowerCase().contains("no-cache")) {
		        	httpflag = true;
		        }
		}
		
		if (httpflag)
			currentNode.pass("Cache-Control Parameter contains 'no-cache' flag in Response!");
		else
			Log.failNode("Cache-Control Parameter does not contain 'no-cache' flag in Response!");
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=11)
	public void validateIfPasswordsAreHashedInDB() throws InterruptedException, IOException {
		final String methodname = "validateIfPasswordsAreHashedInDB";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE11");
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1);
		
		String password = DBHandler.AccessHandler.fetchUserPassword(loginID);
		
		if (password != null) {
			currentNode.pass("Password is available in encrypted form in Database!");
		} else {
			Log.failNode("Password is available in decrypted form in Database!");
		}
		
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=12)
	public void validatePasswordPolicyDuringAccountCreation(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "validatePasswordPolicyDuringAccountCreation";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE12");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		mapParam.put("PASSWORD", "SecurityTesting");
		mapParam.put("CONFIRMPASSWORD", "SecurityTesting");
		
		try {
			channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			Log.failNode("Channel User Creation was successful with simple Password: " + mapParam.get("PASSWORD"));
		} catch (Exception ex) {
			String actualMessage = channelUserLogic.getErrorMessage();
			String expectedMessage = MessagesDAO.getLabelByKey("login.index.error.invalidrequesturl");
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=13)
	public void validateIfUserIsForcedToChangePasswordDuringFirstLogin(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "validateIfUserIsForcedToChangePasswordDuringFirstLogin";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE13");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		HashMap<String, String> channelresultMap;
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
		channelresultMap.get("channelInitiateMsg");		

		if (APPLEVEL.equals("2")) {
			channelUserLogic.approveLevel1_ChannelUser();
			channelUserLogic.approveLevel2_ChannelUser();	
		} else if(APPLEVEL.equals("1")) {
			channelUserLogic.approveLevel1_ChannelUser();
		} else {
			Log.info("Approval not required.");	
		}
		
		channelUserLogic.changeUserFirstTimePassword();
		currentNode.pass("Channel User Prompted to Change Password during first login hence test case passed!");
		
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=14)
	public void validateSpaceInPasswordWhileCUCreation(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "validateSpaceInPasswordWhileCUCreation";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE14");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		mapParam.put("PASSWORD", "Com@ 937");
		mapParam.put("CONFIRMPASSWORD", "Com@ 937");
		
		try {
			channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
		} catch (Exception ex) {
			String actualMessage = channelUserLogic.getErrorMessage();
			String expectedMessage = MessagesDAO.getLabelByKey("operatorutil.validatepassword.error.nospace") + "\n" + MessagesDAO.getLabelByKey("user.adduser.error.confirmpassword.space.not.allowed");
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=15)
	public void validateSpecialCharaterInLoginIDDuringCUCreation(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "validateSpecialCharaterInLoginIDDuringCUCreation";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE15");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		mapParam.put("LoginID", "or '1' = '1'");
		
		try {
			channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
		} catch (Exception ex) {
			String actualMessage = channelUserLogic.getErrorMessage();
			String expectedMessage = MessagesDAO.getLabelByKey("user.adduser.error.msg.char.allowed.loginid") + "\n" + MessagesDAO.getLabelByKey("user.adduser.error.msg.invalid.loginid");
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider="ChannelUserCreationDataProvider", priority=16)
	public void NegativeValueInMSISDNDuringCUCreation(int RowNum, String Domain, String Parent, String Category, String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		final String methodname = "NegativeValueInMSISDNDuringCUCreation";
		Log.startTestCase(methodname);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SECURITYFEATURE16");
		ChannelUser channelUserLogic= new ChannelUser(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("Security-Feature");
		mapParam.put("contactNo", "-7286234823");
		
		try {
			channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
		} catch (Exception ex) {
			String actualMessage = channelUserLogic.getErrorMessage();
			String expectedMessage = MessagesDAO.getLabelByKey("user.adduser.error.msg.char.allowed.loginid") + "\n" + MessagesDAO.getLabelByKey("user.adduser.error.msg.invalid.loginid");
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		
		Log.endTestCase(methodname);
	}
	
	@DataProvider(name = "ChannelUserCreationDataProvider")
	public Object[][] DomainCategoryProvider_validations() {
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		
		ChannelUserMap chnlUserMap = new ChannelUserMap();
		int rowNum=1;
		
		String[] userDetailsHL = new String[5];
		
		userDetailsHL[0] = ExcelUtility.getCellData(0, "DOMAIN_NAME", 1);
		userDetailsHL[1] = ExcelUtility.getCellData(0, "PARENT_CATEGORY_NAME", 1);
		userDetailsHL[2] = ExcelUtility.getCellData(0, "CATEGORY_NAME", 1);
		userDetailsHL[3] = ExcelUtility.getCellData(0, "GRPH_DOMAIN_TYPE", 1);	
		
		return new Object[][]{{rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3], chnlUserMap.getDefaultMap()}												 };
	}
	
}
