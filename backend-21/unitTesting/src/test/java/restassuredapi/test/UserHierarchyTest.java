package restassuredapi.test;

import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.userhierarchy.UserHierarchyAPI;
import restassuredapi.pojo.userhierarchyrequestpojo.UserHierarchyRequestMessage;
import restassuredapi.pojo.userhierarchyrequestpojo.UserHierarchyRequestParentVO;
import restassuredapi.pojo.userhierarchyresponsepojo.UserHierarchyResponse;

@ModuleManager(name = Module.REST_USER_HIERARCHY)
public class UserHierarchyTest extends BaseTest {

	static String moduleCode;
	UserHierarchyRequestParentVO userHierarchyRequestParentVO = new UserHierarchyRequestParentVO();
	HashMap<String, String> transfer_Details = new HashMap<String, String>();

	UserHierarchyRequestMessage userHierarchyRequestMessage = new UserHierarchyRequestMessage();

	UserHierarchyResponse userHierarchyResponse = new UserHierarchyResponse();

	HashMap<String, String> returnMap = new HashMap<String, String>();

	public void setupData() {

		userHierarchyRequestParentVO.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		userHierarchyRequestParentVO.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		userHierarchyRequestParentVO.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		userHierarchyRequestParentVO.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		userHierarchyRequestParentVO.setServicePort(_masterVO.getProperty("servicePort"));
		userHierarchyRequestParentVO.setSourceType(_masterVO.getProperty("sourceType"));

		userHierarchyRequestMessage.setExtcode("");
		userHierarchyRequestMessage.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		userHierarchyRequestMessage.setLanguage1(_masterVO.getProperty("languageCode0"));
		userHierarchyRequestMessage.setLanguage2(_masterVO.getProperty("languageCode0"));
		userHierarchyRequestMessage.setLoginid("");
		userHierarchyRequestMessage.setMsisdn("");
		userHierarchyRequestMessage.setPassword("");
		userHierarchyRequestMessage.setPin("");
		userHierarchyRequestMessage.setType("UPUSRHRCHY");

		userHierarchyRequestParentVO.setData(userHierarchyRequestMessage);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int OperatorRowCount = ExcelUtility.getRowCount();
		for (int i = 1; i < OperatorRowCount; i++) {
			String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			if (CategoryName.equals("SE") && (!LoginID.equals(null) || !LoginID.equals(""))) {
				returnMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
				returnMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
				returnMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				returnMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
				break;
			}
		}

	}

	public void setupDataLoginId() {

		setupData();

		userHierarchyRequestMessage.setLoginid(returnMap.get("LOGIN_ID"));
		userHierarchyRequestMessage.setMsisdn("");
		userHierarchyRequestMessage.setPassword(returnMap.get("PASSWORD"));
		userHierarchyRequestMessage.setPin(returnMap.get("PIN"));
		userHierarchyRequestMessage.setType("UPUSRHRCHY");
		userHierarchyRequestParentVO.setData(userHierarchyRequestMessage);

	}

	public void setupDataMsisdn() {

		setupData();

		userHierarchyRequestMessage.setLoginid("");
		userHierarchyRequestMessage.setMsisdn(returnMap.get("MSISDN"));
		userHierarchyRequestMessage.setPassword("");
		userHierarchyRequestMessage.setPin(returnMap.get("PIN"));
		userHierarchyRequestMessage.setType("UPUSRHRCHY");
		userHierarchyRequestParentVO.setData(userHierarchyRequestMessage);

	}
	
	// Successful data with valid data.
	@Test
	public void A_01_Test_success_loginId() throws Exception {
		final String methodName = "Test_UserHierarchyAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRUPWRDHIERARCHY1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();

		UserHierarchyAPI userHierarchyAPI = new UserHierarchyAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		userHierarchyAPI.setContentType(_masterVO.getProperty("contentType"));
		userHierarchyAPI.addBodyParam(userHierarchyRequestParentVO);
		userHierarchyAPI.setExpectedStatusCode(200);
		userHierarchyAPI.perform();

		userHierarchyResponse = userHierarchyAPI.getAPIResponseAsPOJO(UserHierarchyResponse.class);

		Boolean statusCode = false;


		if (userHierarchyResponse != null && userHierarchyResponse.getDataObject().getMessage() != null && userHierarchyResponse.getDataObject().getMessage().contains("Transaction has been completed")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Transaction has been completed", userHierarchyResponse.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	// Successful data with valid data.
	@Test
	public void A_02_Test_success_msisdn() throws Exception {
		final String methodName = "Test_UserHierarchyAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRUPWRDHIERARCHY2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataMsisdn();

		UserHierarchyAPI userHierarchyAPI = new UserHierarchyAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		userHierarchyAPI.setContentType(_masterVO.getProperty("contentType"));
		userHierarchyAPI.addBodyParam(userHierarchyRequestParentVO);
		userHierarchyAPI.setExpectedStatusCode(200);
		userHierarchyAPI.perform();

		userHierarchyResponse = userHierarchyAPI.getAPIResponseAsPOJO(UserHierarchyResponse.class);

		Boolean statusCode = false;

		if (userHierarchyResponse != null && userHierarchyResponse.getDataObject().getMessage() != null && userHierarchyResponse.getDataObject().getMessage().equalsIgnoreCase("Transaction has been completed")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Transaction has been completed", userHierarchyResponse.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	
	
	@Test
	public void A_03_Test_success_all_blank() throws Exception {
		final String methodName = "Test_UserHierarchyAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRUPWRDHIERARCHY3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataMsisdn();
		userHierarchyRequestMessage.setLoginid("");
		userHierarchyRequestMessage.setMsisdn("");
		userHierarchyRequestParentVO.setData(userHierarchyRequestMessage);
		
		
		
		
		UserHierarchyAPI userHierarchyAPI = new UserHierarchyAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		userHierarchyAPI.setContentType(_masterVO.getProperty("contentType"));
		userHierarchyAPI.addBodyParam(userHierarchyRequestParentVO);
		userHierarchyAPI.setExpectedStatusCode(200);
		userHierarchyAPI.perform();

		userHierarchyResponse = userHierarchyAPI.getAPIResponseAsPOJO(UserHierarchyResponse.class);

		Boolean statusCode = false;

		if (userHierarchyResponse != null && userHierarchyResponse.getDataObject().getMessage() != null && userHierarchyResponse.getDataObject().getMessage().equalsIgnoreCase("Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.", userHierarchyResponse.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}
	
	
	
	@Test
	public void A_04_Test_success_password_blank() throws Exception {
		final String methodName = "Test_UserHierarchyAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRUPWRDHIERARCHY4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		userHierarchyRequestMessage.setPassword("");
		
		
		
		
		UserHierarchyAPI userHierarchyAPI = new UserHierarchyAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		userHierarchyAPI.setContentType(_masterVO.getProperty("contentType"));
		userHierarchyAPI.addBodyParam(userHierarchyRequestParentVO);
		userHierarchyAPI.setExpectedStatusCode(200);
		userHierarchyAPI.perform();

		userHierarchyResponse = userHierarchyAPI.getAPIResponseAsPOJO(UserHierarchyResponse.class);

		Boolean statusCode = false;

		if (userHierarchyResponse != null && userHierarchyResponse.getDataObject().getMessage() != null && userHierarchyResponse.getDataObject().getMessage().contains("PASSWORD can not be blank")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("PASSWORD can not be blank.", userHierarchyResponse.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}
	
	
	
	@Test
	public void A_05_Test_success_password_notAlphaIncorrectLength() throws Exception {
		final String methodName = "Test_UserHierarchyAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRUPWRDHIERARCHY5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		userHierarchyRequestMessage.setPassword("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		
		
		
		
		UserHierarchyAPI userHierarchyAPI = new UserHierarchyAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		userHierarchyAPI.setContentType(_masterVO.getProperty("contentType"));
		userHierarchyAPI.addBodyParam(userHierarchyRequestParentVO);
		userHierarchyAPI.setExpectedStatusCode(200);
		userHierarchyAPI.perform();

		userHierarchyResponse = userHierarchyAPI.getAPIResponseAsPOJO(UserHierarchyResponse.class);

		Boolean statusCode = false;

		if (userHierarchyResponse != null && userHierarchyResponse.getDataObject().getMessage() != null && userHierarchyResponse.getDataObject().getMessage().contains("PASSWORD is not alpha numeric with special caharcter.")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("PASSWORD is not alpha numeric with special caharcter.", userHierarchyResponse.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	
	
	@Test
	public void A_06_Test_success_InvalidUser() throws Exception {
		final String methodName = "Test_UserHierarchyAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRUPWRDHIERARCHY6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		userHierarchyRequestMessage.setLoginid("DUMMY");
		
		
		
		
		UserHierarchyAPI userHierarchyAPI = new UserHierarchyAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		userHierarchyAPI.setContentType(_masterVO.getProperty("contentType"));
		userHierarchyAPI.addBodyParam(userHierarchyRequestParentVO);
		userHierarchyAPI.setExpectedStatusCode(200);
		userHierarchyAPI.perform();

		userHierarchyResponse = userHierarchyAPI.getAPIResponseAsPOJO(UserHierarchyResponse.class);

		Boolean statusCode = false;

		if (userHierarchyResponse != null && userHierarchyResponse.getDataObject().getMessage() != null && userHierarchyResponse.getDataObject().getMessage().contains("Invalid user")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Invalid user", userHierarchyResponse.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	
	@Test
	public void A_07_Test_success_InvalidNetworkCode() throws Exception {
		final String methodName = "Test_UserHierarchyAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRUPWRDHIERARCHY7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		
		String networkCode = "NG1";
		userHierarchyRequestMessage.setExtnwcode(networkCode);
		
		
		
		
		UserHierarchyAPI userHierarchyAPI = new UserHierarchyAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		userHierarchyAPI.setContentType(_masterVO.getProperty("contentType"));
		userHierarchyAPI.addBodyParam(userHierarchyRequestParentVO);
		userHierarchyAPI.setExpectedStatusCode(200);
		userHierarchyAPI.perform();

		userHierarchyResponse = userHierarchyAPI.getAPIResponseAsPOJO(UserHierarchyResponse.class);

		Boolean statusCode = false;

		if (userHierarchyResponse != null && userHierarchyResponse.getDataObject().getMessage() != null && userHierarchyResponse.getDataObject().getMessage().equalsIgnoreCase("External network code "+networkCode+" is invalid.")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		
		Assertion.assertEquals("External network code "+networkCode+" is invalid.", userHierarchyResponse.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

}
