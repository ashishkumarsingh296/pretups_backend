package com.apicontrollers.extgw.CreditTransfer;

import java.util.HashMap;

import org.testng.annotations.Test;

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

public class EXTGW_CreditTransfer extends BaseTest{
		
		public static boolean TestCaseCounter = false;
		private final String extentCategory = "API";

		/**
		 * @throws Exception 
		 * @testid EXTGWC2S01
		 * Positive Test Case For TRFCATEGORY: PRC
		 */
		@Test
		public void TC_A_PositiveEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC01");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		@Test
		public void TC_B_PositiveEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC02");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.AMOUNT, "");
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		@Test
		public void TC_C_PositiveEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC03");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.PIN, "");
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		@Test
		public void TC_D_PositiveEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC04");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.MSISDN1, "");
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		
		
		
		@Test
		public void TC_E_PositiveEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC05");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.MSISDN2, "");
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		
		@Test
		public void TC_F_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC06");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.AMOUNT, "s1");
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}

		
		
		
		
		@Test
		public void TC_G_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC07");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.AMOUNT, "-1");
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}	
		

		
		@Test
		public void TC_H_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC08");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
			RandomGeneration RandomGeneration = new RandomGeneration();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.MSISDN1, RandomGeneration.randomAlphaNumeric(9));
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}	
		
		
		
		
		
		@Test
		public void TC_I_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC09");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
			RandomGeneration RandomGeneration = new RandomGeneration();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.MSISDN2, RandomGeneration.randomAlphaNumeric(9));
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}

		
		
		@Test
		public void TC_J_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC10");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
			RandomGeneration RandomGeneration = new RandomGeneration();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);

			String CorrectPin = dataMap.get(CreditTransAPI.PIN);

			String InValPin;

			do
			{
				InValPin = RandomGeneration.randomNumeric(4);

			}while(CorrectPin==InValPin);

			dataMap.put(CreditTransAPI.PIN,InValPin);	

			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}		
		
		

		
		
		@Test
		public void TC_K_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC11");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
			RandomGeneration RandomGeneration = new RandomGeneration();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.LANGUAGE1, RandomGeneration.randomAlphaNumeric(5));
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}

		
		
		@Test
		public void TC_L_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC12");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
			RandomGeneration RandomGeneration = new RandomGeneration();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.LANGUAGE2, RandomGeneration.randomAlphaNumeric(4));
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
	
		
		
		
		@Test
		public void TC_M_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC13");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
			RandomGeneration RandomGeneration = new RandomGeneration();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.SELECTOR, RandomGeneration.randomAlphaNumeric(4));
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}	
		
		

		
		
		
		@Test
		public void TC_N_NegativeEXTGW_PRC_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPRC14");
			EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
			RandomGeneration RandomGeneration = new RandomGeneration();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = PRC_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(CreditTransAPI.INFO1, RandomGeneration.randomAlphaNumeric(4));
			String API = CreditTransAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}		
		
}
