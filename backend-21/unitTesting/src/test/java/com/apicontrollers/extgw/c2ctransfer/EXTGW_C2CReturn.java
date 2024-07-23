package com.apicontrollers.extgw.c2ctransfer;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.*;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_C2CReturn extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TCA_Negative_SenderOutSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR01");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(C2CReturnAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CRDP.FROM_Category, channelMap);
		
		String API = C2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CRDP.FROM_Category, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCB_NegativeC2CAPIwithInvalidLanguageCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR02");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.LANGUAGE1,"a");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCC_PositiveC2CAPIUsingMSISDNAndPIN() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR03");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CReturnCode"), apiData.get(C2CReturnAPI.MSISDN1), apiData.get(C2CReturnAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CRDP.ProductCode, apiData.get(C2CReturnAPI.QTY));
		
		String API = C2CReturnAPI.prepareAPI(apiData);
		apiData.put(C2CReturnAPI.LOGINID, "");
		apiData.put(C2CReturnAPI.PASSWORD, "");

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
        // Added PostBalanceTransactionVO by Vimal Kumar
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer

		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);
		*/
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
*/
		currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Return");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel=0;
		if(!BTSLUtil.isNullString(value)) {
			maxApprovalLevel = Integer.parseInt(value);
		}

		if(maxApprovalLevel==0) {
			BusinessValidator.validateUserBalances(TransactionVO);
		}else{
			Assertion.assertSkip("Approval Required for Balance Transaction");
		}


	}
	
	
	@Test
	public void TCD_NegativeC2CAPIwithBlankEXTNWCODE() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR04");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.EXTNWCODE, "");
		String API = C2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCE_NegativeC2CAPIwithMSISDNPinMismatch() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR05");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.PIN, RandomGeneration.randomNumeric(4));
		String API = C2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCF_NegativeC2CAPIwithBlankProductCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR06");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.PRODUCTCODE, "");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCG_NegativeC2CAPIwithBlankReceiver() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR07");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.MSISDN2, "");
		apiData.put(C2CReturnAPI.LOGINID2, "");
		apiData.put(C2CReturnAPI.EXTCODE2, "");
		String API = C2CReturnAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCH_PositiveC2CAPIUsingLoginIDAndPassword() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR08");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CReturnCode"), apiData.get(C2CReturnAPI.MSISDN1), apiData.get(C2CReturnAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CRDP.ProductCode, apiData.get(C2CReturnAPI.QTY));
		
		String API = C2CReturnAPI.prepareAPI(apiData);
		apiData.put(C2CReturnAPI.MSISDN1, "");
		apiData.put(C2CReturnAPI.PIN, "");
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		//Added PostBalanceTransactionVO by Vimal Kumar
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer

		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);
		*/
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Return");
		//currentNode.assignCategory("Smoke");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel=0;
		if(BTSLUtil.isNullString(value)) {
			maxApprovalLevel=0;
		}
		else
			maxApprovalLevel = Integer.parseInt(value);
		if(maxApprovalLevel==0) {
			BusinessValidator.validateUserBalances(TransactionVO);
		}else{
			Assertion.assertSkip("Approval Required for Balance Transaction");
		}
	}

	@Test
	public void TCI_NegativeC2CAPIwithInvalidLoginID() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR09");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.LOGINID, "abc");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	  public void TCJ_PositiveC2CAPIwithSenderEXTCODEAndReceiverLoginID() throws
	  SQLException, ParseException {
	  
	  CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR10");
	  EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
	  RandomGeneration RandomGeneration = new RandomGeneration();
	  
	  if (TestCaseCounter == false) { test =
	  extent.createTest(CaseMaster.getModuleCode()); TestCaseCounter = true; }
	  
	  currentNode = test.createNode(CaseMaster.getExtentCase());
	  currentNode.assignCategory(extentCategory);
	  HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
	  apiData.put(C2CReturnAPI.LOGINID, "");
	  apiData.put(C2CReturnAPI.MSISDN1, "");
	  apiData.put(C2CReturnAPI.MSISDN2, "");
	  apiData.put(C2CReturnAPI.EXTCODE2, "");
	  String API = C2CReturnAPI.prepareAPI(apiData);
	  String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
	  _APIUtil.addExecutionRecord(CaseMaster, APIResponse); XmlPath xmlPath = new
	   XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	  Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(),
	  CaseMaster.getErrorCode());
	  }
	
	@Test
	  public void TCK_PositiveC2CAPIwithSenderEXTCODEAndReceiverMSISDN() throws
	  SQLException, ParseException {
	  
	  CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR11");
	  EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI(); 
	  RandomGeneration RandomGeneration = new RandomGeneration();
	  
	  if (TestCaseCounter == false) { test =
	  extent.createTest(CaseMaster.getModuleCode()); TestCaseCounter = true; }
	  
	  currentNode = test.createNode(CaseMaster.getExtentCase());
	  currentNode.assignCategory(extentCategory);
	  HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
	  apiData.put(C2CReturnAPI.LOGINID, "");
	  apiData.put(C2CReturnAPI.MSISDN1, "");
	  apiData.put(C2CReturnAPI.LOGINID2, "");
	  apiData.put(C2CReturnAPI.EXTCODE2, "");
	  String API = C2CReturnAPI.prepareAPI(apiData);
	  String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
	  _APIUtil.addExecutionRecord(CaseMaster, APIResponse); 
	  XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	  Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(),
	  CaseMaster.getErrorCode());
	  }
	
	@Test
	  public void TCL_PositiveC2CAPIwithSecderAndReceiverExternalCode() throws
	  SQLException, ParseException {
	  
	  CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR12");
	  EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI(); 
	  RandomGeneration RandomGeneration = new RandomGeneration();
	  
	  if (TestCaseCounter == false) { test =
	  extent.createTest(CaseMaster.getModuleCode()); TestCaseCounter = true; }
	  
	  currentNode = test.createNode(CaseMaster.getExtentCase());
	  currentNode.assignCategory(extentCategory);
	  HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
	  apiData.put(C2CReturnAPI.LOGINID, "");
	  apiData.put(C2CReturnAPI.MSISDN1, "");
	  apiData.put(C2CReturnAPI.LOGINID2, "");
	  apiData.put(C2CReturnAPI.MSISDN2, "");
	  String API = C2CReturnAPI.prepareAPI(apiData);
	  String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
	  _APIUtil.addExecutionRecord(CaseMaster, APIResponse); XmlPath xmlPath = new
	   XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	  Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(),
	  CaseMaster.getErrorCode());
	  }
	
	@Test
	public void TCM_NegativeC2CAPIwithoutPIN() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR13");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI(); 
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.PIN, "");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCN_NegativeC2CAPIwithBlankPassword() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR14");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI(); 
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.PASSWORD, "");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCO_NegativeC2CAPIwithReferenceNumberLimitExceeded() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR15");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI(); 
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.EXTREFNUM, RandomGeneration.randomNumeric(21));
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCP_NegativeC2CAPIwithNegativeAmount() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR16");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI(); 
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.QTY, "-1");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCQ_NegativeC2CAPIwithBlankAmount() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR17");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI(); 
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.QTY, "");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCR_NegativeC2CAPIwithInvalidEXTCODE2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR18");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.EXTCODE2, RandomGeneration.randomNumeric(4));
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCS_NegativeC2CAPIwithInvalidMSISDN2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR19");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.MSISDN2, RandomGeneration.randomNumeric(10));
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCT_NegativeC2CAPIwithInvalidLoginID2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR20");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.LOGINID2, RandomGeneration.randomAlphaNumeric(5));
		String API = C2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCU_NegativeC2CAPIwithBlankFields() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR21");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		
		apiData.put(C2CReturnAPI.MSISDN1, "");
		apiData.put(C2CReturnAPI.PIN, "");
		apiData.put(C2CReturnAPI.LOGINID, "");
		apiData.put(C2CReturnAPI.PASSWORD, "");
		apiData.put(C2CReturnAPI.EXTCODE, "");
		String API = C2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCV_NegativeC2CAPIwithInvalidExternalCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR22");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.EXTCODE, RandomGeneration.randomNumeric(9));
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCW_NegativeC2CAPIwithalphanumericLanguageCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR23");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.LANGUAGE1, "a");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCX_NegativeC2CAPIwithalphanumericEXTCODE() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR24");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.EXTCODE, "a");
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCY_NegativeC2CAPIwithInvalidMSISDN2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR25");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		apiData.put(C2CReturnAPI.MSISDN2, RandomGeneration.randomNumeric(21));
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCZ_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR26");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWC2CRDP.FROM_Domain, EXTGWC2CRDP.FROM_Category, EXTGWC2CRDP.FROM_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWC2CRDP.FROM_Domain, EXTGWC2CRDP.FROM_Category, EXTGWC2CRDP.FROM_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}

	@Test
	public void TCZ_A_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CR27");
		EXTGWC2CRAPI C2CReturnAPI = new EXTGWC2CRAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CRDP.getAPIdata();
		String API = C2CReturnAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWC2CRDP.TO_Domain, EXTGWC2CRDP.TO_Category, EXTGWC2CRDP.TO_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWC2CRDP.TO_Domain, EXTGWC2CRDP.TO_Category, EXTGWC2CRDP.TO_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}

}
