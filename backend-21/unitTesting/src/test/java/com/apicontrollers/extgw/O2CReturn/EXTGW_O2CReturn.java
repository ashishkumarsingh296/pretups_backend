package com.apicontrollers.extgw.O2CReturn;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
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
import com.commons.PretupsI;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_O2CReturn extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	private String Exisiting_TXN_No = null;
	private String txnNo = null;
	
	@Test
	public void TC1A_PositiveO2CAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR01");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
			Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
			EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
			HashMap<String, String> apiData = APIDAO.getApiData();
			businessController businessController = new businessController(_masterVO.getProperty("O2CReturnCode"), apiData.get(O2CReturnAPI.MSISDN), null);
			
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
			currentNode.assignCategory(extentCategory);
			Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			String API = O2CReturnAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Return");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();
			initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CReturnAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);
			
			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Return");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	@Test
	public void TC2_PositiveO2CAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR02");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		businessController businessController = new businessController(_masterVO.getProperty("O2CReturnCode"), apiData.get(O2CReturnAPI.MSISDN), null);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.EXTCODE, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		//Test Case to validate Network Stocks after successful O2C Transfer
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Return");
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CReturnAPI.QTY));
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		// Test Case to validate Channel User balance after successful O2C Transfer
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Return");
		currentNode.assignCategory(extentCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	@Test
	public void TC3_PositiveO2CAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR03");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		businessController businessController = new businessController(_masterVO.getProperty("O2CReturnCode"), apiData.get(O2CReturnAPI.MSISDN), null);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.MSISDN, "");
		apiData.put(O2CReturnAPI.PIN, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		txnNo = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		//Test Case to validate Network Stocks after successful O2C Transfer
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Return");
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CReturnAPI.QTY));
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		// Test Case to validate Channel User balance after successful O2C Transfer
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Return");
		currentNode.assignCategory(extentCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	@Test
	public void TC4_NegativeO2CAPI_BlankMSISDNAndEXTCODE() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR04");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.MSISDN, "");
		apiData.put(O2CReturnAPI.PIN, "");
		apiData.put(O2CReturnAPI.EXTCODE, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
	}
	
	@Test
	public void TC5_NegativeO2CAPI_BlankProductCode() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR05");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.PRODUCTCODE, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC6_NegativeO2CAPI_BlankAmount() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR06");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.QTY, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC7_NegativeO2CAPI_BlankTxnDate() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR07");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.EXTTXNDATE, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC8_NegativeO2CAPI_BlankTxnNumber() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR08");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.EXTTXNNUMBER, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC9_NegativeO2CAPI_BlankPIN() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR09");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.PIN, "");
		apiData.put(O2CReturnAPI.EXTCODE, "");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC10_NegativeO2CAPI_WrongEXTCODE() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR10");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.EXTCODE, randomGeneration.randomNumeric(4));
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC11_NegativeO2CAPI_NegativeAmount() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR11");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.QTY, "-1");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
	}
	
	@Test
	public void TC12_NegativeO2CAPI_AmountInDecimal() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR12");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.QTY, "50.50");
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC13_NegativeO2CAPI_RemarksMoreThan100() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR13");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.REMARKS, randomGeneration.randomAlphabets(101));
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void TC14_NegativeO2CAPI_WrongPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR14");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CRDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		Object[] exeObj = EXTGWO2CRDP.getAPIdataWithAllUsers();
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) exeObj[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CReturnAPI.PIN, randomGeneration.randomNumeric(4));
		Exisiting_TXN_No = apiData.get(O2CReturnAPI.EXTTXNNUMBER);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		}
	}
	
	@Test
	public void TC15_NegativeO2CAPIWhileCUInSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR15");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CRDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CReturnAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CRDP.CUCategory, channelMap);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CRDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TC16_PositiveO2CAPIWhileCUOutSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR16");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false){
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CRDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CReturnAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CRDP.CUCategory, channelMap);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CRDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TC17_NegativeO2CAPIWithExisting_EXTTXNNUMBER() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR17");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CRDP.getAPIdata();
		apiData.put(O2CReturnAPI.EXTTXNNUMBER, txnNo);
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TC18_NegativeO2CAPIWhileTCPSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR18");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CRDP.getAPIdata();
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWO2CRDP.Domain, EXTGWO2CRDP.CUCategory, EXTGWO2CRDP.TCPName, null);
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWO2CRDP.Domain, EXTGWO2CRDP.CUCategory, EXTGWO2CRDP.TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TC19_NegativeO2CAPIWhileCUSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CR19");
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
		SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
		ResumeChannelUser CUResume = new ResumeChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CRDP.getAPIdata();
		CUSuspend.suspendChannelUser_MSISDN(apiData.get(O2CReturnAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2CR19");
		CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(O2CReturnAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2CR19");
		
		String API = O2CReturnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		CUResume.resumeChannelUser_MSISDN(apiData.get(O2CReturnAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2CR19");
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
}
