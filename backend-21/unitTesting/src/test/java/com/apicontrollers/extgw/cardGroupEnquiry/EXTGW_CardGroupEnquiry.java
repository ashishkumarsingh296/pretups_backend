package com.apicontrollers.extgw.cardGroupEnquiry;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.SuspendChannelUser;
import com.apicontrollers.extgw.suspendResume_SRCUSRREQEX.EXTGWSUSPENDRESUMEAPI;
import com.apicontrollers.extgw.suspendResume_SRCUSRREQEX.EXTGWSUSPENDRESUMEDP;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_CardGroupEnquiry extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws Exception 
	 * 
	 * @testid EXTGWCrdGrpENQ01
	 * Positive Test Case For TRFCATEGORY: Card Group Enquiry
	 */
	
	
	@Test
	public void TC_01_PositiveCardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ01");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGW_CardGroupENQ_DP.getAPIdataWithAllUsers();

		
		for (int i = 0; i < dataObject.length; i++) {
			
			EXTGW_CrdGrpENQ_DAO APIDAO = (EXTGW_CrdGrpENQ_DAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	}


	

	
	
	@Test
	public void TC_02_PositiveCardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ02");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			apiData.put(CardGroupAPI.LOGINID, "");
			apiData.put(CardGroupAPI.PASSWORD, "");
				
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	
	@Test
	public void TC_03_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ03");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			apiData.put(CardGroupAPI.MSISDN1, "");
			apiData.put(CardGroupAPI.PIN, "");
			apiData.put(CardGroupAPI.LOGINID,"");
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	

	
	
	@Test
	public void TC_04_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ04");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			String PinRequired = DBHandler.AccessHandler.getSystemPreference("PIN_REQUIRED");
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			//if(PinRequired.equalsIgnoreCase("false")){
				apiData.put(CardGroupAPI.PIN, "");
				String API = CardGroupAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
				/*}
			else {
				currentNode.log(Status.SKIP, "Pin Validation is not required");
			}*/
	}
	
	
	@Test
	public void TC_05_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ05");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			apiData.put(CardGroupAPI.PASSWORD, RandomGeneration.randomNumeric(9));
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_06_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ06");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			apiData.put(CardGroupAPI.PASSWORD, "");
			apiData.put(CardGroupAPI.MSISDN1,"");
			apiData.put(CardGroupAPI.PIN,"");				
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_07_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ07");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			
			String CorrectPin = apiData.get(CardGroupAPI.PIN);

			String InValPin;

			do
			{
				InValPin = RandomGeneration.randomNumeric(4);

			}while(CorrectPin==InValPin);

			apiData.put(CardGroupAPI.PIN,InValPin);	
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	
	@Test
	public void TC_08_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ08");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			
			String CorrectMSISDN = apiData.get(CardGroupAPI.MSISDN1);

			String InValMSISDN;

			do
			{
				InValMSISDN = RandomGeneration.randomNumeric(9);

			} while(CorrectMSISDN==InValMSISDN);

			apiData.put(CardGroupAPI.MSISDN1,InValMSISDN);	
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	

	
	
	@Test
	public void TC_09_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ09");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			
			String MSISDN2 = RandomGeneration.randomAlphaNumeric(7);
			apiData.put(CardGroupAPI.MSISDN2, MSISDN2);

							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_10_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ10");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.MSISDN2, "");
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_11__PPBServiceType_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ11");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		String PostpaidBillPaymentCode = _masterVO.getProperty("PostpaidBillPaymentCode");
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.SERVICETYPE, PostpaidBillPaymentCode);
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	@Test
	public void TC_12__InvalidServiceType_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ12");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.SERVICETYPE, "xxx");
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	

	
	@Test
	public void TC_13__blankServiceType_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ13");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.SERVICETYPE, "");
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	
	@Test
	public void TC_14_Positive_blankSubService_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ14");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.SUBSERVICE, "");
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_15__InvalidSubService_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ15");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.SUBSERVICE, "abc");
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	

	
	@Test
	public void TC_16__blankAmount_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ16");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.AMOUNT, "");
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	
	@Test
	public void TC_17__negAmount_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ17");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			apiData.put(CardGroupAPI.AMOUNT, "-1");
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	


	
	
	@Test
	public void TC_18__OutOFRangeAmount_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ18");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		_parser parser = new _parser();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			System.out.println("CardGroup Set Id is " +EXTGW_CardGroupENQ_DP.CrdGrp);
			String minRange = DBHandler.AccessHandler.getCardGroupStartRange(EXTGW_CardGroupENQ_DP.CrdGrp);
			
			parser.convertStringToLong(minRange).changeDenomation();
		
			long amount = (long) (parser.getValue()) - 1;
			
			apiData.put(CardGroupAPI.AMOUNT, String.valueOf(amount));
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	

	
	
	
	
	@Test
	public void TC_19__OutSuspendedChannelUser_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ19");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		ChannelUser ChannelUser = new ChannelUser(driver);
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			HashMap<String, String> channelMap = new HashMap<String, String>();
			channelMap.put("searchMSISDN", apiData.get(CardGroupAPI.MSISDN1));
			channelMap.put("outSuspend_chk", "Y");
			ChannelUser.modifyChannelUserDetails(EXTGW_CardGroupENQ_DP.CUCategory, channelMap);
							
			
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGW_CardGroupENQ_DP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_20__DeleteInitiatedChannelUser_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ20");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		ChannelUser ChannelUser = new ChannelUser(driver);
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			String uMSISDN = DBHandler.AccessHandler.deletedMSISDN();
			
			apiData.put(CardGroupAPI.MSISDN1, uMSISDN);
							
			
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	
	
	@Test
	public void TC_21_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ21");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			apiData.put(CardGroupAPI.MSISDN1, "");
			apiData.put(CardGroupAPI.PIN, "");				
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	@Test
	public void TC_22__SuspendChannelUser_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ22");
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ23");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		ChannelUser ChannelUser = new ChannelUser(driver);
		EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
		apiData.put(suspendResumeAPI.ACTION,"S");
		String API = suspendResumeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		//_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " +xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString());
	
		String SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		if(SuspendApprovalReq.equals("TRUE"))
		{
			ExtentI.Markup(ExtentColor.TEAL,"Verifying that operator user is able to approve suspend channel user request using LoginID.");
		
		int rownum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, apiData.get(suspendResumeAPI.MSISDN2));
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String actualMessage = suspendChnluser.approveCSuspendRequest_LoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rownum), "Suspend Request Approved");
		}
		String uMSISDN = apiData.get(suspendResumeAPI.MSISDN2);
		
		
			
			HashMap<String, String> apiData1 = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			
			apiData1.put(CardGroupAPI.MSISDN1, uMSISDN);
							
			
			String API1 = CardGroupAPI.prepareAPI(apiData1);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse1);
		
		XmlPath xmlPath1 = new XmlPath(CompatibilityMode.HTML, APIResponse1[1]);
		Validator.messageCompare(xmlPath1.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiDataR = EXTGWSUSPENDRESUMEDP.getAPIdata();
		
		apiDataR.put(suspendResumeAPI.ACTION,"R");
		String API2 = suspendResumeAPI.prepareAPI(apiDataR);
		String[] APIResponse2 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API2);
		//_APIUtil.addExecutionRecord(CaseMaster, APIResponse2);
		XmlPath xmlPath2 = new XmlPath(CompatibilityMode.HTML, APIResponse2[1]);
		Log.info("The Txn status is " +xmlPath2.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString());
		
		
		
		String API3 = CardGroupAPI.prepareAPI(apiData1);
	String[] APIResponse3 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API3);
	_APIUtil.addExecutionRecord(CaseMaster1, APIResponse3);
	
	XmlPath xmlPath3 = new XmlPath(CompatibilityMode.HTML, APIResponse3[1]);
	Validator.messageCompare(xmlPath3.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster1.getErrorCode());
		
		
	}
	
	
	
	@Test
	public void TC_24__OutOFRangeAmount_CardGrpEnqAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCrdGrpENQ24");
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();
		_parser parser = new _parser();
		
	
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
			
			HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
			
			System.out.println("CardGroup Set Id is " +EXTGW_CardGroupENQ_DP.CrdGrp);
			String maxRange = DBHandler.AccessHandler.getCardGroupEndRange(EXTGW_CardGroupENQ_DP.CrdGrp);
			
			parser.convertStringToLong(maxRange).changeDenomation();
		
			long amount = (long) (parser.getValue()) + 1;
			
			apiData.put(CardGroupAPI.AMOUNT, String.valueOf(amount));
							
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CardGroupAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	

		
	
}
