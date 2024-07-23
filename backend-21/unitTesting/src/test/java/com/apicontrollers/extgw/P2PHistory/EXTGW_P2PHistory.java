package com.apicontrollers.extgw.P2PHistory;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_P2PHistory extends BaseTest{
		
		public static boolean TestCaseCounter = false;
		private final String extentCategory = "API";

		/**
		 * @throws Exception 
		 * @testid EXTGWC2S01
		 * Positive Test Case For TRFCATEGORY: PRC
		 */
		@Test
		public void TC_A_PositiveEXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWHIST01");
			EXTGW_P2PHistoryAPI P2PHistoryAPI = new EXTGW_P2PHistoryAPI();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGW_P2PHistory_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = P2PHistoryAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(P2PHistoryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
	
		
		@Test
		public void TC_A_NegativeEXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWHIST02");
			EXTGW_P2PHistoryAPI P2PHistoryAPI = new EXTGW_P2PHistoryAPI();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGW_P2PHistory_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(P2PHistoryAPI.MSISDN1,"");
			String API = P2PHistoryAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(P2PHistoryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}

		
		
		/* The case is ambiguous
		@Test
		public void TC_C_PositiveEXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWHIST03");
			EXTGW_P2PHistoryAPI P2PHistoryAPI = new EXTGW_P2PHistoryAPI();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGW_P2PHistory_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(P2PHistoryAPI.PIN,"");
			String API = P2PHistoryAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(P2PHistoryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
		
		*/
		
		@Test
		public void TC_D_PositiveEXTGW_ACCINFO_API() throws Exception {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWHIST04");
			EXTGW_P2PHistoryAPI P2PHistoryAPI = new EXTGW_P2PHistoryAPI();

			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}

			HashMap<String, String> dataMap = EXTGW_P2PHistory_DP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			dataMap.put(P2PHistoryAPI.LANGUAGE1,"");
			String API = P2PHistoryAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(P2PHistoryAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}


	
}
