package com.apicontrollers.extgw.P2PAccountInfo;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.ussd.P2PRegistration.USSD_P2PREG_API;
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

public class EXTGW_AccInfo extends BaseTest{
		
		public static boolean TestCaseCounter = false;
		private final String extentCategory = "API";

		/**
		 * @throws Exception 
		 * @testid EXTGWC2S01
		 * Positive Test Case For TRFCATEGORY: PRC
		 */
		@Test
		public void TC_A_PositiveEXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWACC01");
			EXTGW_AccInfoAPI AccInfoAPI = new EXTGW_AccInfoAPI();
			USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
			RandomGeneration randomGenerator = new RandomGeneration();
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> dataMap = EXTGW_AccInfo_DP.getAPIdata();
			/*HashMap<String, String> preapiData2 =new HashMap<String, String>();
			String MSISDN =_masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN(); 
					dataMap.put("MSISDN1",MSISDN);
			preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
			preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
			preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
			String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
			_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);*/
			
			String API = AccInfoAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
	
		
		
	/*	@Test
		public void TC_B_PositiveEXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWACC02");
			EXTGW_AccInfoAPI AccInfoAPI = new EXTGW_AccInfoAPI();
			String MSISDN = null;

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGW_AccInfo_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			MSISDN = DBHandler.AccessHandler.getP2PSubscriberMSISDN("POST","Y" );
			

			dataMap.put(AccInfoAPI.MSISDN1,MSISDN);
			String API = AccInfoAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			
		}*/
		
		
		
		@Test
		public void TC_A_NegativeEXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWACC03");
			EXTGW_AccInfoAPI AccInfoAPI = new EXTGW_AccInfoAPI();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGW_AccInfo_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(AccInfoAPI.MSISDN1,"");
			String API = AccInfoAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		@Test
		public void TC_D_Negative_EXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWACC04");
			EXTGW_AccInfoAPI AccInfoAPI = new EXTGW_AccInfoAPI();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGW_AccInfo_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(AccInfoAPI.SELECTOR,"");
			String API = AccInfoAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(AccInfoAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		
		

}
