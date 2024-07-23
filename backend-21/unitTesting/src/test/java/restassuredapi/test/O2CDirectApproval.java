package restassuredapi.test;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import restassuredapi.api.O2CDirectApproval.O2CDirectApprovalAPI;
import restassuredapi.pojo.o2cdirectapprovalresponsepojo.O2CDirectApprovalResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.BTSLDateUtil;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.O2C_APPROVAL_API)
public class O2CDirectApproval extends BaseTest{
	
static String moduleCode;
	
O2CDirectApprovalResponsePojo o2CDirectApprovalResponsePojo = new O2CDirectApprovalResponsePojo();
	HashMap<String, String> returnMap = new HashMap<String, String>();
	String[] details;
	
	public void setupData() {


		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int OperatorRowCount = ExcelUtility.getRowCount();
		for (int i = 1; i < OperatorRowCount; i++) {
			String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			if (CategoryName.equals("DIST") && (!LoginID.equals(null) || !LoginID.equals(""))) {
				returnMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				returnMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
				break;
			}
		}
		
		}
	public void setupDataMAPP() throws ParseException
	{	
		setupData();
	    //list of coloumns whose vaues we want to get from db
		String[] colNames= {"IMEI","MHASH","TOKEN","SMS_PIN"};
		
		Date currentDate = new Date();
		
		String date1= BTSLDateUtil.getDateStringFromDate(currentDate, "yyyy-MM-dd HH:mm:ss");
		Date date=BTSLDateUtil.getDateFromDateString(date1, "yyyy-MM-dd HH:mm:ss");
		//inserting token,imei,mhash,token_last_used
		DBHandler.AccessHandler.updateAnyColumnValue("USER_PHONES", "IMEI",_masterVO.getProperty("imei"), "MSISDN", returnMap.get("MSISDN"));
		DBHandler.AccessHandler.updateAnyColumnValue("USER_PHONES", "MHASH",_masterVO.getProperty("mhash"), "MSISDN", returnMap.get("MSISDN"));
		DBHandler.AccessHandler.updateAnyColumnValue("USER_PHONES", "TOKEN",_masterVO.getProperty("token"), "MSISDN", returnMap.get("MSISDN"));
		DBHandler.AccessHandler.updateAnyColumnDateValue("USER_PHONES", "TOKEN_LASTUSED_DATE",date, "MSISDN", returnMap.get("MSISDN"));

		details=DBHandler.AccessHandler.getUserDetailsFromUserPhones(returnMap.get("MSISDN"),colNames);
		
	}
	
	
	

	@Test
	@TestManager(TestKey="PRETUPS-6280")
	public void A_01_Test_Success() throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPR1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		O2CDirectApprovalAPI o2CDirectApprovalAPI = new O2CDirectApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CDirectApprovalAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CDirectApprovalAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CDirectApprovalAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CDirectApprovalAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CDirectApprovalAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CDirectApprovalAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CDirectApprovalAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		setupDataMAPP();
		returnMap.put("TYPE", "O2CAGAPRL");
		String txnId = DBHandler.AccessHandler.getTransactionIDO2C("PENDING");
		String refno = txnId+"R";
		String imei = details[0];
		String mhash = details[1];
		String token = details[2];
		returnMap.put("IMEI", imei);
		returnMap.put("MHASH", mhash);
		returnMap.put("TOKEN", token);
		returnMap.put("STATUS", "SUCCESS");
		returnMap.put("LANGUAGE1", "0");
		returnMap.put("TXNID",txnId);
		returnMap.put("REFNO",refno);
		returnMap.put("EXTNWCODE", _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		o2CDirectApprovalAPI.setBodyParam(returnMap);
		o2CDirectApprovalAPI.setExpectedStatusCode(200);
		o2CDirectApprovalAPI.perform();

		o2CDirectApprovalResponsePojo = o2CDirectApprovalAPI.getAPIResponseAsPOJO(O2CDirectApprovalResponsePojo.class);

		long statusCode = Long.valueOf(o2CDirectApprovalResponsePojo.getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6281")
	public void A_02_Test_Success_Reject() throws Exception {

		final String methodName = "A_02_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPR2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		O2CDirectApprovalAPI o2CDirectApprovalAPI = new O2CDirectApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CDirectApprovalAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CDirectApprovalAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CDirectApprovalAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CDirectApprovalAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CDirectApprovalAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CDirectApprovalAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CDirectApprovalAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		String txnId = DBHandler.AccessHandler.getTransactionIDO2C("PENDING");
		returnMap.put("STATUS", "FAIL");
		returnMap.put("TXNID",txnId);
		o2CDirectApprovalAPI.setBodyParam(returnMap);
		o2CDirectApprovalAPI.setExpectedStatusCode(200);
		o2CDirectApprovalAPI.perform();

		o2CDirectApprovalResponsePojo = o2CDirectApprovalAPI.getAPIResponseAsPOJO(O2CDirectApprovalResponsePojo.class);

		long statusCode = Long.valueOf(o2CDirectApprovalResponsePojo.getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6284")
	public void A_03_Test_Blank_Mhash() throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPR3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		O2CDirectApprovalAPI o2CDirectApprovalAPI = new O2CDirectApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CDirectApprovalAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CDirectApprovalAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CDirectApprovalAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CDirectApprovalAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CDirectApprovalAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CDirectApprovalAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CDirectApprovalAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("MHASH", "");
		o2CDirectApprovalAPI.setBodyParam(returnMap);
		o2CDirectApprovalAPI.setExpectedStatusCode(200);
		o2CDirectApprovalAPI.perform();

		o2CDirectApprovalResponsePojo = o2CDirectApprovalAPI.getAPIResponseAsPOJO(O2CDirectApprovalResponsePojo.class);

		String message=o2CDirectApprovalResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid MHASH.");
			
		Assertion.assertEquals(message, "Invalid MHASH.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	@Test
	@TestManager(TestKey="PRETUPS-6285")
	public void A_04_Test_Blank_Token() throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPR4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		O2CDirectApprovalAPI o2CDirectApprovalAPI = new O2CDirectApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CDirectApprovalAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CDirectApprovalAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CDirectApprovalAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CDirectApprovalAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CDirectApprovalAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CDirectApprovalAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CDirectApprovalAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("MHASH", details[0]);
		returnMap.put("TOKEN", "");
		o2CDirectApprovalAPI.setBodyParam(returnMap);
		o2CDirectApprovalAPI.setExpectedStatusCode(200);
		o2CDirectApprovalAPI.perform();

		o2CDirectApprovalResponsePojo = o2CDirectApprovalAPI.getAPIResponseAsPOJO(O2CDirectApprovalResponsePojo.class);
		
		String message=o2CDirectApprovalResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid Token.");
			
		Assertion.assertEquals(message, "Invalid Token.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6282")
	public void A_05_Test_Invalid_Txnid() throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPR5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		O2CDirectApprovalAPI o2CDirectApprovalAPI = new O2CDirectApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CDirectApprovalAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CDirectApprovalAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CDirectApprovalAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CDirectApprovalAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CDirectApprovalAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CDirectApprovalAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CDirectApprovalAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		setupData();
		returnMap.put("TYPE", "O2CAGAPRL");
		String txnId = DBHandler.AccessHandler.getTransactionIDO2C("CLOSE");
		returnMap.put("TOKEN", details[2]);
		returnMap.put("TXNID",txnId);
		o2CDirectApprovalAPI.setBodyParam(returnMap);
		o2CDirectApprovalAPI.setExpectedStatusCode(200);
		o2CDirectApprovalAPI.perform();

		o2CDirectApprovalResponsePojo = o2CDirectApprovalAPI.getAPIResponseAsPOJO(O2CDirectApprovalResponsePojo.class);

		String message=o2CDirectApprovalResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid transaction id.");
			
		Assertion.assertEquals(message, "Invalid transaction id.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6283")
	public void A_06_Test_PENDING_Blank_Imei() throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPR6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		O2CDirectApprovalAPI o2CDirectApprovalAPI = new O2CDirectApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CDirectApprovalAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CDirectApprovalAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CDirectApprovalAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CDirectApprovalAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CDirectApprovalAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CDirectApprovalAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CDirectApprovalAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		setupData();
		returnMap.put("IMEI", "");
		o2CDirectApprovalAPI.setBodyParam(returnMap);
		o2CDirectApprovalAPI.setExpectedStatusCode(200);
		o2CDirectApprovalAPI.perform();

		o2CDirectApprovalResponsePojo = o2CDirectApprovalAPI.getAPIResponseAsPOJO(O2CDirectApprovalResponsePojo.class);

		String message=o2CDirectApprovalResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid IMEI.");
			
		Assertion.assertEquals(message, "Invalid IMEI.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test
	@TestManager(TestKey="PRETUPS-6286")
	public void A_07_Test_Invalid_Status() throws Exception {

		final String methodName = "A_07_Test_Invalid_Status";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPR7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		O2CDirectApprovalAPI o2CDirectApprovalAPI = new O2CDirectApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CDirectApprovalAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CDirectApprovalAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CDirectApprovalAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CDirectApprovalAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CDirectApprovalAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CDirectApprovalAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CDirectApprovalAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		String txnId = DBHandler.AccessHandler.getTransactionIDO2C("PENDING");
		returnMap.put("IMEI", details[0]);
		returnMap.put("STATUS", "CLOSE");
		returnMap.put("TXNID", txnId);
		o2CDirectApprovalAPI.setBodyParam(returnMap);
		o2CDirectApprovalAPI.setExpectedStatusCode(200);
		o2CDirectApprovalAPI.perform();

		o2CDirectApprovalResponsePojo = o2CDirectApprovalAPI.getAPIResponseAsPOJO(O2CDirectApprovalResponsePojo.class);

		String message=o2CDirectApprovalResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid status.");
			
		Assertion.assertEquals(message, "Invalid status.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	


}
