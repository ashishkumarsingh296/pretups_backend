package com.apicontrollers.extgw.PrivateRecharge;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.ussd.P2PRegistration.USSD_P2PREG_API;
import com.apicontrollers.ussd.PrivateRechargeRegistration.USSD_PRR_API;
import com.apicontrollers.ussd.PrivateRechargeRegistration.USSD_PRR_DP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_SID extends BaseTest {
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TC1_PositiveAPI() throws SQLException, ParseException {
	
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSID01");
		EXTGW_SID_API sid1 = new EXTGW_SID_API();
		USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
		USSD_PRR_API privateRecharge = new USSD_PRR_API();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SID_DP.getAPIdata();
		HashMap<String, String> preapiData = USSD_PRR_DP.getAPIdata();
		String MSISDN = preapiData.get("MSISDN");
		String sid = preapiData.get("SID");
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
		preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
		preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
		_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);

		String preAPI = privateRecharge.prepareAPI(preapiData);
		_APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, preAPI);
		
		
		apiData.put(sid1.MSISDN, MSISDN);
		String API = sid1.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(sid1.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC2_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSID02");
	
		EXTGW_SID_API sid1 = new EXTGW_SID_API();
		USSD_P2PREG_API privateRechargeReg = new USSD_P2PREG_API();
		USSD_PRR_API privateRecharge = new USSD_PRR_API();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SID_DP.getAPIdata();
		HashMap<String, String> preapiData = USSD_PRR_DP.getAPIdata();
		String MSISDN = preapiData.get("MSISDN");
		String sid = preapiData.get("SID");
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		preapiData2.put(privateRechargeReg.MSISDN, MSISDN);	
		preapiData2.put(privateRechargeReg.TYPE, "REGREQ");	
		preapiData2.put(privateRechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = privateRechargeReg.prepareAPI(preapiData2);
		_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);

		String preAPI = privateRecharge.prepareAPI(preapiData);
		_APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, preAPI);
		apiData.put(sid1.MSISDN, "");
		String API = sid1.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(sid1.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
	}


}
