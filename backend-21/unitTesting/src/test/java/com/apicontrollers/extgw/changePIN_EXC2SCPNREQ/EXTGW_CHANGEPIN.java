package com.apicontrollers.extgw.changePIN_EXC2SCPNREQ;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.mapclasses.Channel2ChannelMap;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CDP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGW_C2SDAO;
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
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_CHANGEPIN extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 *  EXTGWCHGPIN01
	 * Positive Test Case For Change PIN
	 */
	@Test
	public void _001_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN01");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}

	@Test
	public void _002_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN02");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();
		RandomGeneration randstr = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		//String prefix = new UniqueChecker().UC_PrefixData();
		String msisdn = new UniqueChecker().UC_MSISDN();//prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
		apiData.put(ChangePinAPI.MSISDN, msisdn);
		
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	@Test
	public void _003_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN03");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH)));
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.PIN,Decrypt.APIEncryption(pin));
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}

	@Test
	public void _004_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN04");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.TYPE,new RandomGeneration().randomNumeric(4));
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}

	@Test
	public void _005_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN05");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomAlphaNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH)));
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.NEWPIN,Decrypt.APIEncryption(pin));
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	@Test
	public void _006_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN06");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.MAX_SMS_PIN_LENGTH))+1);
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.NEWPIN,_APIUtil.implementEncryption(pin));
		apiData.put(ChangePinAPI.CONFIRMPIN,_APIUtil.implementEncryption(pin));
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	@Test
	public void _007_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN07");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String remarksPref = DBHandler.AccessHandler.getSystemPreference(CONSTANT.USER_EVENT_REMARKS);
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		if(remarksPref.equalsIgnoreCase("TRUE")&&remarksPref!=null){
			apiData.put(ChangePinAPI.REMARKS,"");
		}
		apiData.put(ChangePinAPI.EXTREFNUM, "");
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}	
	
	//@Test
	public void _008_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN08");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		while(apiData.get(ChangePinAPI.CONFIRMPIN).equals(apiData.get("NEWPIN"))){
			apiData.put(ChangePinAPI.CONFIRMPIN, new RandomGeneration().randomNumeric(Integer.parseInt(pin)));
		}
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}	
	
	@Test
	public void _009_ChangePinAPI() throws SQLException, ParseException, InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN09");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		HashMap<String, String> c2cdataMap=c2ctranserData();
		C2CTransfer c2ctransfer = new C2CTransfer(driver);
		Channel2ChannelMap c2cMAP = new Channel2ChannelMap();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		String extgwdesc = "To verify that channel user["+c2cMAP.getC2CMap("fromCategory")+"] is able to perform C2C transfer to ["+c2cMAP.getC2CMap("toCategory")+"] through EXTGW with the  changed PIN successfully.";
		String webdesc = "To verify that channel user["+c2cMAP.getC2CMap("fromCategory")+"] is able to perform C2C transfer to ["+c2cMAP.getC2CMap("toCategory")+"] via WEB with the  changed PIN successfully.";
		
		
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		if(c2cdataMap.get("ALL").equals("Y")){
			currentNode = test.createNode(webdesc);
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> c2ctransferReturn = c2ctransfer.channel2channelTransfer(c2cMAP.getC2CMap("fromCategory"), c2cMAP.getC2CMap("toCategory"), c2cMAP.getC2CMap("toMSISDN"), c2cMAP.getC2CMap("fromPIN"));
			Validator.messageCompare(c2ctransferReturn.get("actualMessage"), c2ctransferReturn.get("expectedMessage"));
			
			currentNode = test.createNode(extgwdesc);
			currentNode.assignCategory(extentCategory);
			//if (GatewayI.isServiceExist(GatewayI.EXTGW, ServicesControllerI.C2CTRANSFER_REQ)) {
			API = C2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			/*} else {
				Log.skip("C2C Transfer is not available from EXTGW.");
			}*/
		}
		else if(c2cdataMap.get("WEBACCESS").equals("Y")&&c2cdataMap.get("EXTGWACCESS").equals("N")){
			currentNode = test.createNode(webdesc);
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> c2ctransferReturn = c2ctransfer.channel2channelTransfer(c2cMAP.getC2CMap("fromCategory"), c2cMAP.getC2CMap("toCategory"), c2cMAP.getC2CMap("toMSISDN"), c2cMAP.getC2CMap("fromPIN"));
			Validator.messageCompare(c2ctransferReturn.get("actualMessage"), c2ctransferReturn.get("expectedMessage"));
		}
		else if((c2cdataMap.get("WEBACCESS").equals("N")&&c2cdataMap.get("EXTGWACCESS").equals("Y")) /*&& GatewayI.isServiceExist(GatewayI.EXTGW, ServicesControllerI.C2CTRANSFER_REQ)*/){
			currentNode = test.createNode(extgwdesc);
			currentNode.assignCategory(extentCategory);
			API = C2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		else{
			currentNode.log(Status.SKIP, "WEB and EXTGW access is not allowed to the user");
		}
	}	
	
	
	@Test
	public void _010_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN10");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.PIN,"");
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	
	@Test
	public void _011_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN11");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.NEWPIN,"");
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	
	@Test
	public void _012_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN12");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.CONFIRMPIN,"");
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	
	@Test
	public void _013_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN13");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.DATE,new RandomGeneration().randomAlphaNumeric(9));
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	
	//@Test
	public void _014_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN14");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
		suspendCHNLUser.suspendChannelUser_MSISDN(apiData.get(ChangePinAPI.MSISDN), "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(apiData.get(ChangePinAPI.MSISDN), "Automation remarks");
		
		ExtentI.Markup(ExtentColor.TEAL, "Execute API");
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(apiData.get(ChangePinAPI.MSISDN), "Auto Resume Remarks");
		
	}
	
//	@Test
	public void _015_ChangePinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN15");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();
		if (TestCaseCounter == false) {	
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String uMSISDN = DBHandler.AccessHandler.deletedMSISDN();
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.MSISDN,uMSISDN);
		String pin = DBHandler.AccessHandler.getUserDetails(uMSISDN,"SMS_PIN")[0].toString();
		apiData.put(ChangePinAPI.PIN,Decrypt.APIEncryption(Decrypt.decryption(pin)));
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void _016_ChangePinAPI(){
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN16");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.MIN_SMS_PIN_LENGTH))-1);
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.NEWPIN,_APIUtil.implementEncryption(pin));
		apiData.put(ChangePinAPI.CONFIRMPIN,_APIUtil.implementEncryption(pin));
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	
	}
	
	@Test
	public void _017_ChangePinAPI() throws SQLException, ParseException, InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN17");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		
		Object[] dataObject = EXTGWC2SDP.getAPIdataWithAllUsers();
		EXTGW_C2SDAO APIDAO = (EXTGW_C2SDAO) dataObject[1];
		HashMap<String, String> prepareData = APIDAO.getApiData();
		
		if(!apiData.get(ChangePinAPI.MSISDN).equals(prepareData.get("MSISDN")))
		{Log.info("Data not found, hence preparing data to perform C2S recharge.");
		prepareData.put("MSISDN", apiData.get(ChangePinAPI.MSISDN));
		prepareData.put("PIN", apiData.get(ChangePinAPI.PIN));
		prepareData.put("LOGINID", "");
		prepareData.put("PASSWORD", "");
		prepareData.put("EXTCODE", "");
		}
		
		String API = C2STransferAPI.prepareAPI(prepareData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
	//To verify that channel user is not  able to change through EXTGW if pin contains consecutive number
	@Test
	public void _018_ChangePinAPINegative() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWCHGPIN18");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		apiData.put(ChangePinAPI.NEWPIN,"1234");
		apiData.put(ChangePinAPI.CONFIRMPIN, "1234");
		EXTGWCHANGEPINDP.nPIN="1234";
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	public HashMap<String, String> c2ctranserData(){
	
		EXTGWCHANGEPINAPI chngPIN = new EXTGWCHANGEPINAPI();
		
		HashMap<String, String> c2capiDATA = EXTGWC2CDP.getAPIdata();
		HashMap<String, String> apiDATA = EXTGWCHANGEPINDP.getAPIdata();
		String MSISDN = apiDATA.get(chngPIN.MSISDN);
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
		
		String extgwAllowed = ExcelUtility.getCellData(0, ExcelI.EXTGW, rowNumBearer);
		String webAllowed = ExcelUtility.getCellData(0, ExcelI.WEB, rowNumBearer);
		
		
		
		if(webAllowed.equalsIgnoreCase("Y")&&webAllowed!=null){
			c2capiDATA.put("WEBACCESS", "Y");
		}else{c2capiDATA.put("WEBACCESS", "N");}
		
		if(extgwAllowed.equalsIgnoreCase("Y")&&extgwAllowed!=null){
			c2capiDATA.put("EXTGWACCESS", "Y");
		}else{c2capiDATA.put("EXTGWACCESS", "N");}
		
		if(webAllowed.equalsIgnoreCase("Y")&&extgwAllowed.equalsIgnoreCase("Y")){
			c2capiDATA.put("ALL", "Y");
		}else{c2capiDATA.put("ALL", "N");}
		
		return c2capiDATA;
	}
	
	
	
	
	
}
