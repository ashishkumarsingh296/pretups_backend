package restassuredapi.test;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.selfregistrationapi.SelfRegistrationAPI;
import restassuredapi.pojo.selfregistrationresponsepojo.SelfRegistrationResponse;

@ModuleManager(name = Module.SELF_REGISTRATION_API)
public class SelfRegistration extends BaseTest {
	
	static String moduleCode;
    
    GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transfer_Details=new HashMap<String,String>();

	HashMap<String, String> returnMap = new HashMap<String, String>();
	HashMap<String,String> transferDetails=new HashMap<String,String>(); 
	public void getExcelData(){
		

	       String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int OperatorRowCount = ExcelUtility.getRowCount();
        for (int i = 1; i < OperatorRowCount; i++) {
               String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
               String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
               if (CategoryName.equals("DIST") && (!LoginID.equals(null) || !LoginID.equals(""))) {
                   transferDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
               }
        }
        
     		}

	RandomGeneration randStr = new RandomGeneration();

	@Test
	@TestManager(TestKey="PRETUPS-6309")
	public void A_01_Test_Success() throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		returnMap.put("PIN", new RandomGeneration().randomNumeric(4));
		returnMap.put("USERNAME", "rahul "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(200);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6302")
	public void A_02_Test_invalid_msisdn() throws Exception {

		final String methodName = "A_02_Test_invalid_msisdn";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", "gfghwd");
		returnMap.put("PIN", new RandomGeneration().randomNumeric(4));
		returnMap.put("USERNAME", "rahul "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(1021015);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 1021015);
		Assertion.assertEquals(Long.toString(statusCode), "1021015");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6300")
	public void A_03_Test_Success_MSISDN_BLANK() throws Exception {

		final String methodName = "A_03_Test_Success_MSISDN_BLANK";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", "");
		returnMap.put("PIN", new RandomGeneration().randomNumeric(4));
		returnMap.put("USERNAME", "rahul "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(1002203);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 1002203);
		Assertion.assertEquals(Long.toString(statusCode), "1002203");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6304")
	public void A_04_Test_PIN_BLANK() throws Exception {

		final String methodName = "A_04_Test_PIN_BLANK";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		returnMap.put("PIN", "");
		returnMap.put("USERNAME", "rahul "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(4325);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 4325);
		Assertion.assertEquals(Long.toString(statusCode), "4325");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	@Test
	@TestManager(TestKey="PRETUPS-6305")
	public void A_05_Test_PIN_INVALID() throws Exception {

		final String methodName = "A_05_Test_PIN_INVALID";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		returnMap.put("PIN", "hghhg");
		returnMap.put("USERNAME", "rahul "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(4070);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 4070);
		Assertion.assertEquals(Long.toString(statusCode), "4070");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	@Test
	@TestManager(TestKey="PRETUPS-6303")
	public void A_06_Test_PIN_Length() throws Exception {

		final String methodName = "A_06_Test_PIN_Length";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		returnMap.put("PIN", new RandomGeneration().randomNumeric(8));
		returnMap.put("USERNAME", "rahul "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(4075);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 4075);
		Assertion.assertEquals(Long.toString(statusCode), "4075");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	@Test
	@TestManager(TestKey="PRETUPS-6306")
	public void A_07_Test_NAME_BLANK() throws Exception {

		final String methodName = "A_07_Test_NAME_BLANK";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		returnMap.put("PIN", new RandomGeneration().randomNumeric(4));
		returnMap.put("USERNAME", "");
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(5000003);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 5000003);
		Assertion.assertEquals(Long.toString(statusCode), "5000003");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	
	@Test
	@TestManager(TestKey="PRETUPS-6307")
	public void A_08_Test_EMAILID_BLANK() throws Exception {

		final String methodName = "A_08_Test_EMAILID_BLANK";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		returnMap.put("PIN", new RandomGeneration().randomNumeric(4));
		returnMap.put("USERNAME", "deepa "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", "");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(1004069);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 1004069);
		Assertion.assertEquals(Long.toString(statusCode), "1004069");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	
	@Test
	@TestManager(TestKey="PRETUPS-6308")
	public void A_09_Test_EMAILID_INVALID() throws Exception {

		final String methodName = "A_09_Test_EMAILID_INVALID";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		returnMap.put("PIN", new RandomGeneration().randomNumeric(4));
		returnMap.put("USERNAME", "deepa "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase());
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(10007);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 10007);
		Assertion.assertEquals(Long.toString(statusCode), "10007");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	@Test
	@TestManager(TestKey="PRETUPS-6301")
	public void A_09_Test_Mobile_no_already_exist() throws Exception {

		final String methodName = "A_09_Test_Mobile_no_already_exist";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFREG10");
		getExcelData();
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		SelfRegistrationResponse selfRegistrationResponse = new SelfRegistrationResponse();
		SelfRegistrationAPI selfRegistrationAPI = new SelfRegistrationAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		selfRegistrationAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		selfRegistrationAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		selfRegistrationAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		selfRegistrationAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		selfRegistrationAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		selfRegistrationAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "SELFREG");
		returnMap.put("MSISDN", transferDetails.get("MSISDN"));
		returnMap.put("PIN", new RandomGeneration().randomNumeric(4));
		returnMap.put("USERNAME", "deepa "+new RandomGeneration().randomAlphabets(4));
		returnMap.put("EMAILID", randStr.randomAlphaNumeric(5).toLowerCase()+ "@mail.com");
		selfRegistrationAPI.setBodyParam(returnMap);
		selfRegistrationAPI.setExpectedStatusCode(4550);
		selfRegistrationAPI.perform();

		selfRegistrationResponse = selfRegistrationAPI.getAPIResponseAsPOJO(SelfRegistrationResponse.class);

		long statusCode = Long.valueOf(selfRegistrationResponse.getTxnStatus());

		Assert.assertEquals(statusCode, 4550);
		Assertion.assertEquals(Long.toString(statusCode), "4550");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	
}
