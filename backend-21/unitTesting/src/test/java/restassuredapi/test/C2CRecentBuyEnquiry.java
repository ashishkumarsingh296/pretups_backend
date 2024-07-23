package restassuredapi.test;

import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2crecentbuyenquiry.C2CRecentBuyEnquiryAPI;
import restassuredapi.pojo.c2crecentbuyenquiryrequestpojo.C2CRecentBuyEnquiryRequestPojo;
import restassuredapi.pojo.c2crecentbuyenquiryrequestpojo.Data;
import restassuredapi.pojo.c2crecentbuyenquiryresponsepojo.C2CRecentBuyEnquiryResponsePojo;

@ModuleManager(name = Module.REST_C2C_ENQUIRY)
public class C2CRecentBuyEnquiry extends BaseTest {

	static String moduleCode;
	C2CRecentBuyEnquiryRequestPojo c2CRecentBuyEnquiryRequestPojo = new C2CRecentBuyEnquiryRequestPojo();
	HashMap<String, String> transfer_Details = new HashMap<String, String>();

	Data data = new Data();

	C2CRecentBuyEnquiryResponsePojo c2CRecentBuyEnquiryResponsePojo = new C2CRecentBuyEnquiryResponsePojo();

	HashMap<String, String> returnMap = new HashMap<String, String>();

	public void setupData() {

		c2CRecentBuyEnquiryRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		c2CRecentBuyEnquiryRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		c2CRecentBuyEnquiryRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		c2CRecentBuyEnquiryRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		c2CRecentBuyEnquiryRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		c2CRecentBuyEnquiryRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));

		data.setExtcode("");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
		data.setLanguage2(_masterVO.getProperty("languageCode0"));
		data.setLoginid("");
		data.setMsisdn("");
		data.setPassword("");
		data.setPin("");

		c2CRecentBuyEnquiryRequestPojo.setData(data);

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

		data.setLoginid(returnMap.get("LOGIN_ID"));
		data.setMsisdn("");
		data.setPassword(returnMap.get("PASSWORD"));
		data.setPin(returnMap.get("PIN"));
		c2CRecentBuyEnquiryRequestPojo.setData(data);

	}

	public void setupDataMsisdn() {

		setupData();

		data.setLoginid("");
		data.setMsisdn(returnMap.get("MSISDN"));
		data.setPassword("");
		data.setPin(returnMap.get("PIN"));
		c2CRecentBuyEnquiryRequestPojo.setData(data);

	}
	
	// Successful data with valid data.
	@Test
	public void A_01_Test_success_loginId() throws Exception {
		final String methodName = "Test_c2CRecentBuyEnquiryAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CRECENTBUY1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();

		C2CRecentBuyEnquiryAPI c2CRecentBuyEnquiryAPI = new C2CRecentBuyEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		c2CRecentBuyEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CRecentBuyEnquiryAPI.addBodyParam(c2CRecentBuyEnquiryRequestPojo);
		c2CRecentBuyEnquiryAPI.setExpectedStatusCode(200);
		c2CRecentBuyEnquiryAPI.perform();

		c2CRecentBuyEnquiryResponsePojo = c2CRecentBuyEnquiryAPI.getAPIResponseAsPOJO(C2CRecentBuyEnquiryResponsePojo.class);

		Boolean statusCode = false;


		if (c2CRecentBuyEnquiryResponsePojo != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage() != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage().contains("Transaction has been completed")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Transaction has been completed", c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	// Successful data with valid data.
	@Test
	public void A_02_Test_success_msisdn() throws Exception {
		final String methodName = "Test_c2CRecentBuyEnquiryAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CRECENTBUY2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataMsisdn();

		C2CRecentBuyEnquiryAPI c2CRecentBuyEnquiryAPI = new C2CRecentBuyEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		c2CRecentBuyEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CRecentBuyEnquiryAPI.addBodyParam(c2CRecentBuyEnquiryRequestPojo);
		c2CRecentBuyEnquiryAPI.setExpectedStatusCode(200);
		c2CRecentBuyEnquiryAPI.perform();

		c2CRecentBuyEnquiryResponsePojo = c2CRecentBuyEnquiryAPI.getAPIResponseAsPOJO(C2CRecentBuyEnquiryResponsePojo.class);

		Boolean statusCode = false;

		if (c2CRecentBuyEnquiryResponsePojo != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage() != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage().equalsIgnoreCase("Transaction has been completed")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Transaction has been completed", c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	
	
	@Test
	public void A_03_Test_success_all_blank() throws Exception {
		final String methodName = "Test_c2CRecentBuyEnquiryAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CRECENTBUY3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataMsisdn();
		data.setLoginid("");
		data.setMsisdn("");
		c2CRecentBuyEnquiryRequestPojo.setData(data);
		
		
		
		
		C2CRecentBuyEnquiryAPI c2CRecentBuyEnquiryAPI = new C2CRecentBuyEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		c2CRecentBuyEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CRecentBuyEnquiryAPI.addBodyParam(c2CRecentBuyEnquiryRequestPojo);
		c2CRecentBuyEnquiryAPI.setExpectedStatusCode(200);
		c2CRecentBuyEnquiryAPI.perform();

		c2CRecentBuyEnquiryResponsePojo = c2CRecentBuyEnquiryAPI.getAPIResponseAsPOJO(C2CRecentBuyEnquiryResponsePojo.class);

		Boolean statusCode = false;

		if (c2CRecentBuyEnquiryResponsePojo != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage() != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage().equalsIgnoreCase("Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.", c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}
	
	
	
	@Test
	public void A_04_Test_success_password_blank() throws Exception {
		final String methodName = "Test_c2CRecentBuyEnquiryAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CRECENTBUY4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		data.setPassword("");
		
		
		
		
		C2CRecentBuyEnquiryAPI c2CRecentBuyEnquiryAPI = new C2CRecentBuyEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		c2CRecentBuyEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CRecentBuyEnquiryAPI.addBodyParam(c2CRecentBuyEnquiryRequestPojo);
		c2CRecentBuyEnquiryAPI.setExpectedStatusCode(200);
		c2CRecentBuyEnquiryAPI.perform();

		c2CRecentBuyEnquiryResponsePojo = c2CRecentBuyEnquiryAPI.getAPIResponseAsPOJO(C2CRecentBuyEnquiryResponsePojo.class);

		Boolean statusCode = false;

		if (c2CRecentBuyEnquiryResponsePojo != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage() != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage().contains("PASSWORD can not be blank")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("PASSWORD can not be blank.", c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}
	
	
	
	@Test
	public void A_05_Test_success_password_notAlphaIncorrectLength() throws Exception {
		final String methodName = "Test_c2CRecentBuyEnquiryAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CRECENTBUY5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		data.setPassword("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		
		
		
		
		C2CRecentBuyEnquiryAPI c2CRecentBuyEnquiryAPI = new C2CRecentBuyEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		c2CRecentBuyEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CRecentBuyEnquiryAPI.addBodyParam(c2CRecentBuyEnquiryRequestPojo);
		c2CRecentBuyEnquiryAPI.setExpectedStatusCode(200);
		c2CRecentBuyEnquiryAPI.perform();

		c2CRecentBuyEnquiryResponsePojo = c2CRecentBuyEnquiryAPI.getAPIResponseAsPOJO(C2CRecentBuyEnquiryResponsePojo.class);

		Boolean statusCode = false;

		if (c2CRecentBuyEnquiryResponsePojo != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage() != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage().contains("PASSWORD is not alpha numeric with special caharcter.")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("PASSWORD is not alpha numeric with special caharcter.", c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	
	
	@Test
	public void A_06_Test_success_InvalidUser() throws Exception {
		final String methodName = "Test_c2CRecentBuyEnquiryAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CRECENTBUY6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		data.setLoginid("DUMMY");
		
		
		
		
		C2CRecentBuyEnquiryAPI c2CRecentBuyEnquiryAPI = new C2CRecentBuyEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		c2CRecentBuyEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CRecentBuyEnquiryAPI.addBodyParam(c2CRecentBuyEnquiryRequestPojo);
		c2CRecentBuyEnquiryAPI.setExpectedStatusCode(200);
		c2CRecentBuyEnquiryAPI.perform();

		c2CRecentBuyEnquiryResponsePojo = c2CRecentBuyEnquiryAPI.getAPIResponseAsPOJO(C2CRecentBuyEnquiryResponsePojo.class);

		Boolean statusCode = false;

		if (c2CRecentBuyEnquiryResponsePojo != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage() != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage().contains("Invalid user")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Invalid user", c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

	
	@Test
	public void A_07_Test_success_InvalidNetworkCode() throws Exception {
		final String methodName = "Test_c2CRecentBuyEnquiryAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CRECENTBUY7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupDataLoginId();
		
		String networkCode = "NG1";
		data.setExtnwcode(networkCode);
		
		
		
		
		C2CRecentBuyEnquiryAPI c2CRecentBuyEnquiryAPI = new C2CRecentBuyEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

		c2CRecentBuyEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CRecentBuyEnquiryAPI.addBodyParam(c2CRecentBuyEnquiryRequestPojo);
		c2CRecentBuyEnquiryAPI.setExpectedStatusCode(200);
		c2CRecentBuyEnquiryAPI.perform();

		c2CRecentBuyEnquiryResponsePojo = c2CRecentBuyEnquiryAPI.getAPIResponseAsPOJO(C2CRecentBuyEnquiryResponsePojo.class);

		Boolean statusCode = false;

		if (c2CRecentBuyEnquiryResponsePojo != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage() != null && c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage().equalsIgnoreCase("External network code "+networkCode+" is invalid.")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		
		Assertion.assertEquals("External network code "+networkCode+" is invalid.", c2CRecentBuyEnquiryResponsePojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	}

}
