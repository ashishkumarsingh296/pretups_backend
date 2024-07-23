package com.apicontrollers.ussd.SetPin;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.mapclasses.Channel2ChannelMap;
import com.apicontrollers.ussd.Transfer.USSD_TRANSFER_API;
import com.apicontrollers.ussd.Transfer.USSD_TRANSFER_DP;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_SETPIN extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 *  USSDSETPIN01
	 * Positive Test Case For Set PIN
	 */
	@Test
	public void A1_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN01");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	@Test
	public void B2_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN02");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();
		RandomGeneration randstr = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String prefix = new UniqueChecker().UC_PrefixData();
		String msisdn = null;
		if(prefix == null)
		msisdn = randstr.randomNumeric(10);
		else
		msisdn = prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
		apiData.put(setPin.MSISDN1, msisdn);
		
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	@Test
	public void C3_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN03");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH)));
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.PIN, pin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	@Test
	public void D4_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN04");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.TYPE,new RandomGeneration().randomNumeric(4));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}

	@Test
	public void E5_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN05");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomAlphaNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH)));
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.NEWPIN,Decrypt.APIEncryption(pin));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	@Test
	public void F6_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN06");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.MAX_SMS_PIN_LENGTH))+1);
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.NEWPIN,pin);
		apiData.put(setPin.CONFIRMPIN,pin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	
	@Test
	public void G7_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN07");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH);
		
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		while(apiData.get(setPin.CONFIRMPIN).equals(apiData.get("NEWPIN"))){
			apiData.put(setPin.CONFIRMPIN, new RandomGeneration().randomNumeric(Integer.parseInt(pin)));
		}
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}	
	
	@Test
	public void H8_setPin() throws SQLException, ParseException, InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN08");
		USSD_TRANSFER_API C2CTransferAPI = new USSD_TRANSFER_API();
		HashMap<String, String> c2cdataMap=c2ctransferData();
		C2CTransfer c2ctransfer = new C2CTransfer(driver);
		Channel2ChannelMap c2cMAP = new Channel2ChannelMap();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		String ussddesc = "To verify that channel user["+c2cMAP.getC2CMap("fromCategory")+"] is able to perform C2C transfer to ["+c2cMAP.getC2CMap("toCategory")+"] through USSD with the  changed PIN successfully.";
		String webdesc = "To verify that channel user["+c2cMAP.getC2CMap("fromCategory")+"] is able to perform C2C transfer to ["+c2cMAP.getC2CMap("toCategory")+"] via WEB with the  changed PIN successfully.";
		
		
		HashMap<String, String> apiData = USSD_TRANSFER_DP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		
		if(c2cdataMap.get("ALL").equals("Y")){
			currentNode = test.createNode(webdesc);
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> c2ctransferReturn = c2ctransfer.channel2channelTransfer(c2cMAP.getC2CMap("fromCategory"), c2cMAP.getC2CMap("toCategory"), c2cMAP.getC2CMap("toMSISDN"), c2cMAP.getC2CMap("fromPIN"));
			Validator.partialmessageCompare(c2ctransferReturn.get("actualMessage"), c2ctransferReturn.get("expectedMessage"));
			
			currentNode = test.createNode(ussddesc);
			currentNode.assignCategory(extentCategory);
			
			API = C2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.partialmessageCompare(xmlPath.get(USSD_TRANSFER_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		else if(c2cdataMap.get("WEBACCESS").equals("Y")&&c2cdataMap.get("USSDACCESS").equals("N")){
			currentNode = test.createNode(webdesc);
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2ctransferReturn = c2ctransfer.channel2channelTransfer(c2cMAP.getC2CMap("fromCategory"), c2cMAP.getC2CMap("toCategory"), c2cMAP.getC2CMap("toMSISDN"), c2cMAP.getC2CMap("fromPIN"));
			Validator.partialmessageCompare(c2ctransferReturn.get("actualMessage"), c2ctransferReturn.get("expectedMessage"));
		}
		else if(c2cdataMap.get("WEBACCESS").equals("N")&&c2cdataMap.get("USSDACCESS").equals("Y")){
			currentNode = test.createNode(ussddesc);
			currentNode.assignCategory(extentCategory);
			API = C2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.partialmessageCompare(xmlPath.get(USSD_TRANSFER_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		else{
			currentNode.log(Status.SKIP, "WEB and USSD access is not allowed to the user");
		}
	}	
	
	
	@Test
	public void I9_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN09");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.PIN,"");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	
	@Test
	public void J10_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN10");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.NEWPIN,"");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	
	@Test
	public void K11_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN11");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN,"");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	
	@Test
	public void L12_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN12");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
		suspendCHNLUser.suspendChannelUser_MSISDN(apiData.get(setPin.MSISDN1), "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(apiData.get(setPin.MSISDN1), "Automation remarks");
		
		ExtentI.Markup(ExtentColor.TEAL, "Execute API");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(apiData.get(setPin.MSISDN1), "Auto Resume Remarks");
		
	}
	
//	@Test
	public void M13_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN13");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();
		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String uMSISDN = DBHandler.AccessHandler.deletedMSISDN();
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		apiData.put(setPin.MSISDN1,uMSISDN);
		String pin = DBHandler.AccessHandler.getUserDetails(uMSISDN,"SMS_PIN")[0].toString();
		apiData.put(setPin.PIN,Decrypt.APIEncryption(Decrypt.decryption(pin)));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void N14_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSETPIN14");
		USSD_SETPIN_API setPin = new USSD_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SETPIN_DP.getAPIdata();
		String oldPin = apiData.get("PIN");
		apiData.put(setPin.NEWPIN,oldPin);
		apiData.put(setPin.CONFIRMPIN,oldPin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SETPIN_DP.setPIN(xmlPath.get(USSD_SETPIN_API.TXNSTATUS).toString());
	}
	
	public HashMap<String, String> c2ctransferData(){
		
		USSD_SETPIN_API chngPIN = new USSD_SETPIN_API();
		
		HashMap<String, String> c2capiDATA = USSD_TRANSFER_DP.getAPIdata();
		HashMap<String, String> apiDATA = USSD_SETPIN_DP.getAPIdata();
		String MSISDN = apiDATA.get(chngPIN.MSISDN1);
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		int rowNum = 0; int rowNumBearer=0;
		try {
			rowNum = ExcelUtility.searchStringRowNum(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, MSISDN);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String categoryName= ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,rowNum);
		
		try {
			rowNumBearer = ExcelUtility.searchStringRowNum(masterSheetPath, ExcelI.ACCESS_BEARER_MATRIX_SHEET, categoryName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		
		String USSDAllowed = ExcelUtility.getCellData(0, ExcelI.USSD, rowNumBearer);
		String webAllowed = ExcelUtility.getCellData(0, ExcelI.WEB, rowNumBearer);
		
		
		
		if(webAllowed.equalsIgnoreCase("Y")&&webAllowed!=null){
			c2capiDATA.put("WEBACCESS", "Y");
		}else{c2capiDATA.put("WEBACCESS", "N");}
		
		if(USSDAllowed.equalsIgnoreCase("Y")&&USSDAllowed!=null){
			c2capiDATA.put("USSDACCESS", "Y");
		}else{c2capiDATA.put("USSDACCESS", "N");}
		
		if(webAllowed.equalsIgnoreCase("Y")&&USSDAllowed.equalsIgnoreCase("Y")){
			c2capiDATA.put("ALL", "Y");
		}else{c2capiDATA.put("ALL", "N");}
		
		return c2capiDATA;
	}

}
