package com.apicontrollers.extgw.PrivateRecharge;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.apicontrollers.ussd.P2PRegistration.USSD_P2PREG_API;
import com.apicontrollers.ussd.PrivateRechargeRegistration.USSD_PRR_DP;
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

public class EXTGW_PrivateRecharge extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	String SID;
	
	
	
		@Test
		public void A1_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR01");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			SID = apiData.get("SID");
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void B2_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR02");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.LANGUAGE1, "");
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void C3_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR03");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.SID, "");
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void D4_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR04");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
	        RandomGeneration randomGeneration = new RandomGeneration();
	    	USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.SID, randomGeneration.randomNumeric(18));
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void E5_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR05");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(apiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);
			apiData.put(privateRecharge.NEWSID, randomGeneration.randomNumeric(18));
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void F6_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR06");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.NEWSID, "");
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void G7_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR07");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.SID, SID);
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void H8_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR08");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.SID, randomGeneration.randomNumeric(4)+randomGeneration.randomNumeric(11));
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void I9_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR09");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.TYPE, "");
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void J10_PrivateRechargeAPI() throws SQLException, ParseException {
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR10");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = apiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			apiData.put(privateRecharge.TYPE, randomGeneration.randomAlphaNumeric(6));
			String API = privateRecharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void K11_PrivateRechargeAPI() throws Exception {
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR11");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI recharge = new EXTGWC2SAPI();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
	    
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			apiData.put(recharge.MSISDN2,privateRechargeapiData.get("SID"));
			String API = recharge.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void L12_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR12");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			ChannelUser ChannelUser = new ChannelUser(driver);
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
		
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			
			
			
			
			
			HashMap<String, String> channelMap = new HashMap<String, String>();
			channelMap.put("searchMSISDN", dataMap.get(C2STransferAPI.MSISDN));
			channelMap.put("inSuspend_chk", "Y");
			ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
			
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			
			channelMap.put("inSuspend_chk", "N");
			ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
			
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void M13_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR13");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			ChannelUser ChannelUser = new ChannelUser(driver);
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			
			
			HashMap<String, String> channelMap = new HashMap<String, String>();
			channelMap.put("searchMSISDN", dataMap.get(C2STransferAPI.MSISDN));
			channelMap.put("outSuspend_chk", "Y");
			ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
			
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			
			channelMap.put("outSuspend_chk", "N");
			ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
			
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void N14_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR14");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			TransferControlProfile TCPObj = new TransferControlProfile(driver);
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			
			apiData.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			String API = C2STransferAPI.prepareAPI(apiData);

			TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPName, null);

			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

			TCPObj.channelLevelTransferControlProfileActive(0, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPName, null);

			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

		}
		
		@Test
		public void O15_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR15");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
			ResumeChannelUser CUResume = new ResumeChannelUser(driver);
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			
			
			
			
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			
			CUSuspend.suspendChannelUser_MSISDN(dataMap.get(C2STransferAPI.MSISDN), "Automated EXTGW O2C API Testing: USSDC2S06");
			CUSuspend.approveCSuspendRequest_MSISDN(dataMap.get(C2STransferAPI.MSISDN), "Automated EXTGW O2C API Testing: USSDC2S06");
			
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			
			CUResume.resumeChannelUser_MSISDN(dataMap.get(C2STransferAPI.MSISDN), "Automated EXTGW O2C API Testing: USSDC2S06");
			
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void P16_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR16");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
	
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			dataMap.put("PIN", "");
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void Q17_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR17");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			
	        RandomGeneration randomGeneration = new RandomGeneration();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			dataMap.put("PIN", randomGeneration.randomNumeric(4));
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void R18_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR18");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
		
	        RandomGeneration randomGeneration = new RandomGeneration();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put("MSISDN2", "");
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void S19_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR19");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
		
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			
			dataMap.put("AMOUNT", "");
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void T20_PrivateRechargeAPI() throws Exception {
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR20");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
	        RandomGeneration randomGeneration = new RandomGeneration();
	        USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
		
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			dataMap.put("AMOUNT", "-100");
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void U21_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR21");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			
	        RandomGeneration randomGeneration = new RandomGeneration();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			dataMap.put("SELECTOR", "");
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void V22_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR22");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			
	        RandomGeneration randomGeneration = new RandomGeneration();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			dataMap.put("SELECTOR", "-100");
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		@Test
		public void W23_PrivateRechargeAPI() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR23");
			EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
			EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
		
	        RandomGeneration randomGeneration = new RandomGeneration();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> privateRechargeapiData = USSD_PRR_DP.getAPIdata();
			String MSISDN = privateRechargeapiData.get("MSISDN");
			HashMap<String, String> preapiData2 =new HashMap<String, String>();
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
			String API1 = privateRecharge.prepareAPI(privateRechargeapiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
			dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
			dataMap.put("SELECTOR", randomGeneration.randomNumeric(3));
			String API = C2STransferAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*@Test
	public void A1_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR01");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		SID = apiData.get("SID");
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void B2_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR02");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.LANGUAGE1, "");
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void C3_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR03");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.SID, "");
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void D4_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR04");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.SID, randomGeneration.randomNumeric(18));
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void E5_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR05");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.NEWSID, randomGeneration.randomNumeric(18));
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void F6_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR06");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.NEWSID, "");
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void G7_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR07");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.SID, SID);
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void H8_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR08");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.SID, randomGeneration.randomNumeric(4)+randomGeneration.randomNumeric(11));
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void I9_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR09");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.TYPE, "");
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void J10_PrivateRechargeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR10");
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(privateRecharge.TYPE, randomGeneration.randomAlphaNumeric(6));
		String API = privateRecharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PrivateRecharge_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void K11_PrivateRechargeAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR11");
		EXTGWC2SAPI recharge = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> privateRechargeapiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(recharge.MSISDN2,privateRechargeapiData.get("SID"));
		String API = recharge.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void L12_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR12");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> privateRechargeapiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", dataMap.get(C2STransferAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
		
		dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void M13_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR13");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> privateRechargeapiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", dataMap.get(C2STransferAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
		
		dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void N14_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR14");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> privateRechargeapiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		apiData.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
		String API = C2STransferAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPName, null);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}
	
	@Test
	public void O15_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR15");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
		ResumeChannelUser CUResume = new ResumeChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> privateRechargeapiData = EXTGW_PrivateRecharge_DP.getAPIdata();
		dataMap.put(C2STransferAPI.MSISDN2, privateRechargeapiData.get("SID"));
		
		CUSuspend.suspendChannelUser_MSISDN(dataMap.get(C2STransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWC2S06");
		CUSuspend.approveCSuspendRequest_MSISDN(dataMap.get(C2STransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWC2S06");
		
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		CUResume.resumeChannelUser_MSISDN(dataMap.get(C2STransferAPI.MSISDN), "Automated EXTGW O2C API Testing: USSDC2S06");
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void P16_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR16");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("PIN", "");
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void Q17_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR17");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("PIN", randomGeneration.randomNumeric(4));
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void R18_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR18");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("MSISDN2", "");
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void S19_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR19");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("AMOUNT", "");
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void T20_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR20");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("AMOUNT", "-100");
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void U21_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR21");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("SELECTOR", "");
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void V22_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR22");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("SELECTOR", "-100");
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void W23_PrivateRechargeAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRR23");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
        RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put("SELECTOR", randomGeneration.randomNumeric(3));
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}*/
	
}

