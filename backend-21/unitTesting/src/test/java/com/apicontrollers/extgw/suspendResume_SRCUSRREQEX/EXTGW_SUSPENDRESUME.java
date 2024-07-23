package com.apicontrollers.extgw.suspendResume_SRCUSRREQEX;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.SuspendChannelUser;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CDP;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CDP;
import com.apicontrollers.extgw.o2ctransfer.EXTGW_O2CDAO;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_SUSPENDRESUME extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 *  SRCUSRREQEX01
	 * Positive Test Case For Suspend Channel user
	 * @throws IOException 
	 */
	@Test
	public void _001_suspendResumeAPI() throws SQLException, ParseException, IOException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX01");
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
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		if(SuspendApprovalReq.equals("TRUE"))
		{
		currentNode=test.createNode("To verify that operator user is able to approve suspend channel user request using LoginID.");
		currentNode.assignCategory(extentCategory);
		int rownum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, apiData.get(suspendResumeAPI.MSISDN2));
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String actualMessage = suspendChnluser.approveCSuspendRequest_LoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rownum), "Suspend Request Approved");
		
		//Message Validation
		currentNode=test.createNode("To verify that proper message appear on Web after approving suspend channel user request.");
		currentNode.assignCategory(extentCategory);
		String expectedMessage = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		suspendChnluser.messageCompare(actualMessage, expectedMessage);
		}
	
	
	}

	@Test
	public void _002_suspendResumeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX02");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.PASSWORD, "");
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		}

	@Test
	public void _003_suspendResumeAPI() throws SQLException, ParseException {
		//String Exisiting_TXN_No = null;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX03");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		Object[] dataObject = EXTGWO2CDP.getAPIdataWithAllUsers();
		Log.info("User Details: "+dataObject[1]);
		EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) dataObject[1];
		HashMap<String, String> apiData = APIDAO.getApiData();
		apiData.put(O2CTransferAPI.EXTCODE, "");
		//Exisiting_TXN_No = apiData.get(O2CTransferAPI.EXTTXNNUMBER);
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		
		}
	
	@Test
	public void _004_suspendResumeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX04");
			EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
			apiData.put(suspendResumeAPI.MSISDN2,"");
			apiData.put(suspendResumeAPI.ACTION,"S");
			String API = suspendResumeAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}

	@Test
	public void _005_suspendResumeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX05");
			EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
			
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
			apiData.put(suspendResumeAPI.ACTION,"R");
			apiData.put(suspendResumeAPI.MSISDN1,"");
			String API = suspendResumeAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
			}
	
	@Test
	public void _006_suspendResumeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX06");
			EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
			apiData.put(suspendResumeAPI.PIN,"");
			apiData.put(suspendResumeAPI.ACTION,"S");
			String API = suspendResumeAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	@Test
	public void _007_suspendResumeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX07");
			EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
			apiData.put(suspendResumeAPI.NETWORK,"");
			apiData.put(suspendResumeAPI.ACTION,"S");
			String API = suspendResumeAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	@Test
	public void _008_suspendResumeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX08");
			EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
			apiData.put(suspendResumeAPI.ACTION,"");
			String API = suspendResumeAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	@Test
	public void _009_suspendResumeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX09");
			EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH)));
			HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
			apiData.put(suspendResumeAPI.PIN,pin);
			apiData.put(suspendResumeAPI.ACTION,"S");
			String API = suspendResumeAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	@Test
	public void _010_suspendResumeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX10");
			EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
			apiData.put(suspendResumeAPI.ACTION,"R");
			String API = suspendResumeAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}	
	
	@Test
	public void _011_suspendResumeAPI() throws SQLException, ParseException {
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX11");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.PASSWORD, "");
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
	}	

		@Test
		public void _012_suspendResumeAPI() throws SQLException, ParseException {
				
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX12");
			EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			Object[] dataObject = EXTGWO2CDP.getAPIdataWithAllUsers();
			Log.info("User Details: "+dataObject[1]);
			EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) dataObject[1];
			HashMap<String, String> apiData = APIDAO.getApiData();
			apiData.put(O2CTransferAPI.EXTCODE, "");
			//Exisiting_TXN_No = apiData.get(O2CTransferAPI.EXTTXNNUMBER);
			
			String API = O2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
		}
		
		@Test
		public void _013_suspendResumeAPI() throws SQLException, ParseException {
				
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX13");
				EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
		
				if (TestCaseCounter == false) {
					test = extent.createTest(CaseMaster.getModuleCode());
					TestCaseCounter = true;
				}
				
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory(extentCategory);
				HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
				apiData.put(suspendResumeAPI.ACTION,"R");
				apiData.put(suspendResumeAPI.MSISDN2,"");
				String API = suspendResumeAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
		
		@Test
		public void _014_suspendResumeAPI() throws SQLException, ParseException {
				
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX14");
				EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
		
				if (TestCaseCounter == false) {
					test = extent.createTest(CaseMaster.getExtentCase());
					TestCaseCounter = true;
				}
				
				currentNode = test.createNode(CaseMaster.getDescription());
				currentNode.assignCategory(extentCategory);
				
				HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
				apiData.put(suspendResumeAPI.MSISDN1,"");
				apiData.put(suspendResumeAPI.ACTION,"S");
				String API = suspendResumeAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}

		@Test
		public void _015_suspendResumeAPI() throws SQLException, ParseException {
				
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX15");
				EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
		
				if (TestCaseCounter == false) {
					test = extent.createTest(CaseMaster.getModuleCode());
					TestCaseCounter = true;
				}
				
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory(extentCategory);
				HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
				apiData.put(suspendResumeAPI.ACTION,"R");
				apiData.put(suspendResumeAPI.PIN,"");
				String API = suspendResumeAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
		
		@Test
		public void _016_suspendResumeAPI() throws SQLException, ParseException {
				
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX16");
				EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
		
				if (TestCaseCounter == false) {
					test = extent.createTest(CaseMaster.getModuleCode());
					TestCaseCounter = true;
				}
				
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory(extentCategory);
				HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
				apiData.put(suspendResumeAPI.ACTION,"R");
				apiData.put(suspendResumeAPI.NETWORK,"");
				String API = suspendResumeAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
		
		@Test
		public void _017_suspendResumeAPI() throws SQLException, ParseException {
				
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX17");
				EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
		
				if (TestCaseCounter == false) {
					test = extent.createTest(CaseMaster.getModuleCode());
					TestCaseCounter = true;
				}
				
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory(extentCategory);
				HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
				apiData.put(suspendResumeAPI.ACTION,"P");
				String API = suspendResumeAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
		
		@Test
		public void _018_suspendResumeAPI() throws SQLException, ParseException {
				
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SRCUSRREQEX18");
				EXTGWSUSPENDRESUMEAPI suspendResumeAPI= new EXTGWSUSPENDRESUMEAPI();
		
				if (TestCaseCounter == false) {
					test = extent.createTest(CaseMaster.getModuleCode());
					TestCaseCounter = true;
				}
				
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory(extentCategory);
				String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH)));
				HashMap<String, String> apiData = EXTGWSUSPENDRESUMEDP.getAPIdata();
				apiData.put(suspendResumeAPI.ACTION,"R");
				apiData.put(suspendResumeAPI.PIN,pin);
				String API = suspendResumeAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get(EXTGWSUSPENDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
}