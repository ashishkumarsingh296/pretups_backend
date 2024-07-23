package com.apicontrollers.extgw.VMS.voucherpinresend;


import java.util.HashMap;

//import org.apache.poi.hssf.record.RecalcIdRecord;
import org.testng.annotations.Test;

import com.Features.mapclasses.RechargeMap;
//import com.apicontrollers.extgw.AlternateNumber.EXTGWADDALTNUMERAPI;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_VoucherPinResend extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/*
	 * @throws Exception 
	 * @test Id EXTGWADDALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL WITH ALL CORRECT DATA
	 */
	
	@Test
	public void _01_VoucherPinResndAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERPINRESEND01");
		EXTGWVOUCHERPINRESENDAPI VoucherPinResendAPI = new EXTGWVOUCHERPINRESENDAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		RechargeMap rechargeMap = new RechargeMap();
		
		//EXTGWVOUCHERPINRESENDDP extgwvoucherpinresenddp = new EXTGWVOUCHERPINRESENDDP();
		HashMap<String, String> apiData= EXTGWVOUCHERPINRESENDDP.getAPIdata();
		
		
		/*
		 * apiData.put(EXTGWVOUCHERPINRESENDAPI.MSISDN,datamap.get("LOGINID"));
		 * apiData.put(EXTGWVOUCHERPINRESENDAPI.PIN,datamap.get("PASSWORD"));
		 * 
		 * apiData.put(EXTGWVOUCHERPINRESENDAPI.LOGINID,datamap.get("MSISDN"));
		 * apiData.put(EXTGWVOUCHERPINRESENDAPI.PASSWORD,datamap.get("PIN"));
		 * 
		 * apiData.put(EXTGWVOUCHERPINRESENDAPI.EXTCODE,datamap.get("EXTCODE"));
		 * apiData.put(EXTGWVOUCHERPINRESENDAPI.SERVICETYPE,datamap.get("SERVICETYPE"));
		 * apiData.put(EXTGWVOUCHERPINRESENDAPI.SELECTOR,datamap.get("SELECTOR"));
		 */
		
		
		
		
		String API = VoucherPinResendAPI.prepareAPI(apiData);
		
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWVOUCHERPINRESENDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	
	@Test
	public void _02_VoucherPinResndAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERPINRESEND02");
		EXTGWVOUCHERPINRESENDAPI VoucherPinResendAPI = new EXTGWVOUCHERPINRESENDAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWVOUCHERPINRESENDDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		apiData.put(EXTGWVOUCHERPINRESENDAPI.EXTNWCODE,"");
		
		
		String API = VoucherPinResendAPI.prepareAPI(apiData);
		
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWVOUCHERPINRESENDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void _03_VoucherPinResndAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERPINRESEND03");
		EXTGWVOUCHERPINRESENDAPI VoucherPinResendAPI = new EXTGWVOUCHERPINRESENDAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWVOUCHERPINRESENDDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		apiData.put(EXTGWVOUCHERPINRESENDAPI.EXTNWCODE,"AB");
		
		
		String API = VoucherPinResendAPI.prepareAPI(apiData);
		
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWVOUCHERPINRESENDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void _04_VoucherPinResndAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERPINRESEND04");
		EXTGWVOUCHERPINRESENDAPI VoucherPinResendAPI = new EXTGWVOUCHERPINRESENDAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWVOUCHERPINRESENDDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		apiData.put(EXTGWVOUCHERPINRESENDAPI.LOGINID,"");
		apiData.put(EXTGWVOUCHERPINRESENDAPI.PASSWORD,"");
		apiData.put(EXTGWVOUCHERPINRESENDAPI.MSISDN,"");
		apiData.put(EXTGWVOUCHERPINRESENDAPI.PIN,"");
		apiData.put(EXTGWVOUCHERPINRESENDAPI.EXTCODE,"");
		
		
		String API = VoucherPinResendAPI.prepareAPI(apiData);
		
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWVOUCHERPINRESENDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}

 }
 