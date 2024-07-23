package com.apicontrollers.extgw.c2cwithdraw;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.commons.PretupsI;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_C2CWithdraw extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException
	 * @throws SQLException
	 * @testid EXC2CWDREQ01 Positive Test Case For TRFCATEGORY: C2Cwithdraw
	 */
	@Test
	public void _001_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ01");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<apiData.length;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase()+" [PRODUCT: "+c2cDAO.getApiData().get(C2CWithdrawAPI.PRODUCTCODE)+"]");
			currentNode.assignCategory(extentCategory);


			businessController businessController = new businessController(_masterVO.getProperty("C2CWithdrawCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN2), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN1));
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.LOGINID, "");
			c2cwmap.put(C2CWithdrawAPI.PASSWORD, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE,"");
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();

			initiatedQty.put(c2cDAO.getApiData().get("productCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);

			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);}
	}
	
	@Test
	public void _002_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ02");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<apiData.length;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase()+" [PRODUCT: "+c2cDAO.getApiData().get(C2CWithdrawAPI.PRODUCTCODE)+"]");
			currentNode.assignCategory(extentCategory);


			businessController businessController = new businessController(_masterVO.getProperty("C2CWithdrawCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN2), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN1));
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.MSISDN1, "");
			c2cwmap.put(C2CWithdrawAPI.PIN, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE2,"");
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();

			initiatedQty.put(c2cDAO.getApiData().get("productCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);

			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);}
	}

	@Test
	public void _003_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ03");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<apiData.length;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase()+" [PRODUCT: "+c2cDAO.getApiData().get(C2CWithdrawAPI.PRODUCTCODE)+"]");
			currentNode.assignCategory(extentCategory);


			businessController businessController = new businessController(_masterVO.getProperty("C2CWithdrawCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN2), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN1));
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.MSISDN1, "");
			c2cwmap.put(C2CWithdrawAPI.PIN, "");
			c2cwmap.put(C2CWithdrawAPI.LOGINID, "");
			c2cwmap.put(C2CWithdrawAPI.PASSWORD, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE2,"");
			c2cwmap.put(C2CWithdrawAPI.MSISDN2, "");
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();

			initiatedQty.put(c2cDAO.getApiData().get("productCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);

			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);}
	}

	@Test
	public void _004_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ04");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<apiData.length;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase()+" [PRODUCT: "+c2cDAO.getApiData().get(C2CWithdrawAPI.PRODUCTCODE)+"]");
			currentNode.assignCategory(extentCategory);


			businessController businessController = new businessController(_masterVO.getProperty("C2CWithdrawCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN2), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN1));
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.MSISDN1, "");
			c2cwmap.put(C2CWithdrawAPI.PIN, "");
			c2cwmap.put(C2CWithdrawAPI.LOGINID, "");
			c2cwmap.put(C2CWithdrawAPI.PASSWORD, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE2,"");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();

			initiatedQty.put(c2cDAO.getApiData().get("productCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);

			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);}
	}
	
	@Test
	public void _005_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ05");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<apiData.length;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase()+" [PRODUCT: "+c2cDAO.getApiData().get(C2CWithdrawAPI.PRODUCTCODE)+"]");
			currentNode.assignCategory(extentCategory);


			businessController businessController = new businessController(_masterVO.getProperty("C2CWithdrawCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN2), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN1));
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.MSISDN1, "");
			c2cwmap.put(C2CWithdrawAPI.PIN, "");
			c2cwmap.put(C2CWithdrawAPI.LOGINID, "");
			c2cwmap.put(C2CWithdrawAPI.PASSWORD, "");
			c2cwmap.put(C2CWithdrawAPI.MSISDN2,"");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();

			initiatedQty.put(c2cDAO.getApiData().get("productCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);

			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);}
	}
	
	@Test
	public void _006_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ06");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<apiData.length;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase()+" [PRODUCT: "+c2cDAO.getApiData().get(C2CWithdrawAPI.PRODUCTCODE)+"]");
			currentNode.assignCategory(extentCategory);


			businessController businessController = new businessController(_masterVO.getProperty("C2CWithdrawCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN2), c2cDAO.getApiData().get(C2CWithdrawAPI.MSISDN1));
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.MSISDN2,"");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();

			initiatedQty.put(c2cDAO.getApiData().get("productCode"), c2cDAO.getApiData().get(C2CWithdrawAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);

			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Channel to Channel Withdraw.");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);}
	}
	
	@Test
	public void _007_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ07");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.PRODUCTCODE,"521");
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			c2cwmap.put(C2CWithdrawAPI.MSISDN2,"");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void _008_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ08");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		RandomGeneration rndmGen = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.MSISDN2,"");
			c2cwmap.put(C2CWithdrawAPI.LOGINID,"");
			c2cwmap.put(C2CWithdrawAPI.PASSWORD,"");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			String pin = c2cwmap.get(C2CWithdrawAPI.PIN);
			String rndmPIN = rndmGen.randomNumberWithoutZero(pin.length());
			while(rndmPIN.equals(pin)){
				rndmPIN = rndmGen.randomAlphaNumeric(pin.length());
			}
			c2cwmap.put(C2CWithdrawAPI.PIN,rndmPIN);
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void _009_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ09");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.MSISDN2,"");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE2,"");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void _010_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ10");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.PASSWORD, "");
			c2cwmap.put(C2CWithdrawAPI.MSISDN1, "");
			c2cwmap.put(C2CWithdrawAPI.PIN, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE, "");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE2,"");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void _011_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ11");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.PASSWORD, "");
			c2cwmap.put(C2CWithdrawAPI.LOGINID, "");
			c2cwmap.put(C2CWithdrawAPI.PIN, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE, "");
			c2cwmap.put(C2CWithdrawAPI.LOGINID2, "");
			c2cwmap.put(C2CWithdrawAPI.EXTCODE2,"");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void _012_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ12");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.EXTNWCODE, "");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}

	
	@Test
	public void _015_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ15");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();

			ExtentI.Markup(ExtentColor.TEAL, "Suspending TCP of sender Channel User");

			int rownum = 0;
			try {
				rownum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, c2cwmap.get(C2CWithdrawAPI.MSISDN2));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String catName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, rownum);
			String domName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, rownum);
			String tcpName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, rownum);
			String tcpID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, rownum);
			
			trfCntrlProf.channelLevelTransferControlProfileSuspend(0, domName, catName, tcpName, tcpID);

			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Resuming TCP of sender Channel User");

			trfCntrlProf.channelLevelTransferControlProfileActive(0, domName, catName, tcpName, tcpID);
		}
	}
	
	@Test
	public void _016_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ16");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();

			ExtentI.Markup(ExtentColor.TEAL, "Suspending tcp of receiver Channel User");
	
			int rownum = 0;
			try {
				rownum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, c2cwmap.get(C2CWithdrawAPI.MSISDN1));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String catName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, rownum);
			String domName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, rownum);
			String tcpName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, rownum);
			String tcpID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, rownum);
			trfCntrlProf.channelLevelTransferControlProfileSuspend(0, domName, catName, tcpName, tcpID);
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			ExtentI.Markup(ExtentColor.TEAL, "Resuming tcp of receiver Channel User");
			trfCntrlProf.channelLevelTransferControlProfileActive(0, domName, catName, tcpName, tcpID);

		}
	}
	
	@Test
	public void _018_C2CWithdraw() throws SQLException, ParseException, InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ18");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		ChannelUser chnlUsr = new ChannelUser(driver);
		HashMap<String, String> paraMap = new HashMap<String, String>();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();

			ExtentI.Markup(ExtentColor.TEAL, "Insuspend receiver Channel User");
			paraMap.put("inSuspend_chk", "true");
			paraMap.put("searchMSISDN", c2cwmap.get(C2CWithdrawAPI.MSISDN1));

			int rownum = 0;
			try {
				rownum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, c2cwmap.get(C2CWithdrawAPI.MSISDN1));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String catName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, rownum);
			chnlUsr.modifyChannelUserDetails(catName, paraMap);

			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			ExtentI.Markup(ExtentColor.TEAL, "Removing insuspend for receiver Channel User");
			paraMap.put("inSuspend_chk", "false");
			chnlUsr.modifyChannelUserDetails(catName, paraMap);

		}
	}
	
	@Test
	public void _019_C2CWithdraw() throws SQLException, ParseException, InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ19");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();
		ChannelUser chnlUsr = new ChannelUser(driver);
		HashMap<String, String> paraMap = new HashMap<String, String>();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();

			ExtentI.Markup(ExtentColor.TEAL, "Outsuspend receiver Channel User");
			paraMap.put("outSuspend_chk", "Y");
			paraMap.put("searchMSISDN", c2cwmap.get(C2CWithdrawAPI.MSISDN1));

			int rownum = 0;
			try {
				rownum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, c2cwmap.get(C2CWithdrawAPI.MSISDN1));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String catName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, rownum);
			chnlUsr.modifyChannelUserDetails(catName, paraMap);

			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			ExtentI.Markup(ExtentColor.TEAL, "Removing outsuspend for receiver Channel User");
			paraMap.put("outSuspend_chk", "N");
			chnlUsr.modifyChannelUserDetails(catName, paraMap);

		}
	}	
	
	@Test
	public void _020_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ20");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.PRODUCTCODE, "");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}

	@Test
	public void _021_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ21");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.QTY, "");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void _022_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ22");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.LANGUAGE1, "43");
			
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
	
	@Test
	public void _023_C2CWithdraw() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXC2CWDREQ23");
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] apiData = EXTGWC2CWDP.getAPIdataWithAllUsers();

		for(int i=0;i<1;i++){

			EXTGW_C2CWDAO c2cDAO= (EXTGW_C2CWDAO) apiData[i];
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2cwmap = c2cDAO.getApiData();
			c2cwmap.put(C2CWithdrawAPI.EXTREFNUM, new RandomGeneration().randomNumberWithoutZero(21));
			String API = C2CWithdrawAPI.prepareAPI(c2cwmap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}
}
