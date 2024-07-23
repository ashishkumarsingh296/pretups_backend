package com.apicontrollers.extgw.selfTPinReset_TPINRSETREQ;

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
import com.apicontrollers.extgw.changePIN_EXC2SCPNREQ.EXTGWCHANGEPINAPI;
import com.apicontrollers.extgw.changePIN_EXC2SCPNREQ.EXTGWCHANGEPINDP;
import com.apicontrollers.extgw.changePIN_EXC2SCPNREQ.EXTGW_CHANGEPIN;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.Decrypt;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_RESETPIN extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 *  TPINRSETREQ01
	 * Positive Test Case For Reset PIN
	 */
	@Test
	public void _001_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ01");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		String PIN = Decrypt.decryption(xmlPath.get(EXTGWRESETPINAPI.PIN).toString());
		EXTGWRESETPINDP.setPIN(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(),PIN);
	}

	
	@Test
	public void _002_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ02");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();
		RandomGeneration randstr = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		String prefix = new UniqueChecker().UC_PrefixData();
		String msisdn = prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.MSISDN, msisdn);
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void _003_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ03");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.TYPE, "TPINRSETREQ03" );
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void _004_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ04");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.OPERATION,"R");
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		String PIN = Decrypt.decryption(xmlPath.get(EXTGWRESETPINAPI.PIN).toString());
		EXTGWRESETPINDP.setPIN(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(),PIN);
	}
	
	@Test
	public void _005_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ05");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.OPERATION,"R0");
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void _006_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ06");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.OPERATION,"U1");
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void _007_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ07");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.EXTNWCODE,"");
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void _008_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ08");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.OPERATION, "U");
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void _009_ResetPinAPI() throws SQLException, ParseException, InterruptedException {
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ09");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		EXTGW_CHANGEPIN changePIN = new EXTGW_CHANGEPIN();
		HashMap<String, String> c2cdataMap=changePIN.c2ctranserData();
		C2CTransfer c2ctransfer = new C2CTransfer(driver);
		Channel2ChannelMap c2cMAP = new Channel2ChannelMap();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		String extgwdesc = "To verify that channel user["+c2cMAP.getC2CMap("fromCategory")+"] is not able to perform C2C transfer to ["+c2cMAP.getC2CMap("toCategory")+"] through EXTGW with the  changed PIN successfully.";
		String webdesc = "To verify that channel user["+c2cMAP.getC2CMap("fromCategory")+"] is not able to perform C2C transfer to ["+c2cMAP.getC2CMap("toCategory")+"] via WEB with the  changed PIN successfully.";
		
		
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		String actual=null; String expected=null;
		if(c2cdataMap.get("ALL").equals("Y")){
			currentNode = test.createNode(webdesc);
			currentNode.assignCategory(extentCategory);
			
			try{
			HashMap<String, String> c2ctransferReturn = c2ctransfer.channel2channelTransfer(c2cMAP.getC2CMap("fromCategory"), c2cMAP.getC2CMap("toCategory"), c2cMAP.getC2CMap("toMSISDN"), c2cMAP.getC2CMap("fromPIN"));
			Validator.messageCompare(c2ctransferReturn.get("actualMessage"), c2ctransferReturn.get("expectedMessage"));
			}catch(Exception e){
				actual=new AddChannelUserDetailsPage(driver).getActualMessage();
				expected = MessagesDAO.getLabelByKey("channeltransfer.chnltochnlviewproduct.msg.smspinreset");
				Validator.messageCompare(actual, expected);
			}
			currentNode = test.createNode(extgwdesc);
			currentNode.assignCategory(extentCategory);
			
			API = C2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		else if(c2cdataMap.get("WEBACCESS").equals("Y")&&c2cdataMap.get("EXTGWACCESS").equals("N")){
			currentNode = test.createNode(webdesc);
			currentNode.assignCategory(extentCategory);
			try{
				HashMap<String, String> c2ctransferReturn = c2ctransfer.channel2channelTransfer(c2cMAP.getC2CMap("fromCategory"), c2cMAP.getC2CMap("toCategory"), c2cMAP.getC2CMap("toMSISDN"), c2cMAP.getC2CMap("fromPIN"));
				Validator.messageCompare(c2ctransferReturn.get("actualMessage"), c2ctransferReturn.get("expectedMessage"));
				}catch(Exception e){
					actual=new AddChannelUserDetailsPage(driver).getActualMessage();
					expected = MessagesDAO.getLabelByKey("channeltransfer.chnltochnlviewproduct.msg.smspinreset");
					Validator.messageCompare(actual, expected);
				}
			}
		else if(c2cdataMap.get("WEBACCESS").equals("N")&&c2cdataMap.get("EXTGWACCESS").equals("Y")){
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
	public void _010_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ10");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.PASSWORD,"");
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		String PIN = Decrypt.decryption(xmlPath.get(EXTGWRESETPINAPI.PIN).toString());
		EXTGWRESETPINDP.setPIN(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(),PIN);
	}	
	
	//@Test
	public void _011_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ11");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
		suspendCHNLUser.suspendChannelUser_MSISDN(apiData.get(ResetPinAPI.MSISDN), "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(apiData.get(ResetPinAPI.MSISDN), "Automation remarks");

		ExtentI.Markup(ExtentColor.TEAL, "Execute API");
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		String PIN = Decrypt.decryption(xmlPath.get(EXTGWRESETPINAPI.PIN).toString());
		EXTGWRESETPINDP.setPIN(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(),PIN);
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(apiData.get(ResetPinAPI.MSISDN), "Auto Resume Remarks");
		
	}	
	
	@Test
	public void _012_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ12");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.MSISDN,DBHandler.AccessHandler.deletedMSISDN());
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	@Test
	public void _013_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ13");
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(ResetPinAPI.DATE,new RandomGeneration().randomAlphaNumeric(9));
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		String PIN = Decrypt.decryption(xmlPath.get(EXTGWRESETPINAPI.PIN).toString());
		EXTGWRESETPINDP.setPIN(xmlPath.get(EXTGWRESETPINAPI.TXNSTATUS).toString(),PIN);
	}	
	
	
	@Test
	public void _014_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ14");
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();
		EXTGWRESETPINAPI resetPINAPI = new EXTGWRESETPINAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWCHANGEPINDP.getAPIdata();
		HashMap<String, String> apiDatareset = EXTGWRESETPINDP.getAPIdata();
		if(apiData.get(ChangePinAPI.MSISDN).equals(apiDatareset.get(resetPINAPI.MSISDN))){
		String API = ChangePinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());}
		else {
			currentNode.log(Status.FAIL, "Data mismatch for reset pin and change pin:[CHANGEPIN: "+apiData.get(ChangePinAPI.MSISDN)+"] "
					+ "| [RESETPIN: "+apiDatareset.get(resetPINAPI.MSISDN)+"]");
		}
	}
	
	
	@Test
	public void _015_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ15");
		EXTGWRESETPINAPI resetPINAPI = new EXTGWRESETPINAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(resetPINAPI.LOGINID, new RandomGeneration().randomAlphaNumeric(6));
		String API = resetPINAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	@Test
	public void _016_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ16");
		EXTGWRESETPINAPI resetPINAPI = new EXTGWRESETPINAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		apiData.put(resetPINAPI.PASSWORD, new RandomGeneration().randomAlphaNumeric(8));
		String API = resetPINAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
	
	@Test
	public void _017_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TPINRSETREQ17");
		EXTGWRESETPINAPI resetPINAPI = new EXTGWRESETPINAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWRESETPINDP.getAPIdata();
		//apiData.put(resetPINAPI.PASSWORD, new RandomGeneration().randomAlphaNumeric(8));
		String API = resetPINAPI.prepareAPI(apiData);
		API = _APIUtil.removeTagsfromAPI(API, resetPINAPI.LOGINID,resetPINAPI.PASSWORD);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGWCHANGEPINDP.setPIN(xmlPath.get(EXTGWCHANGEPINAPI.TXNSTATUS).toString());
	}
}