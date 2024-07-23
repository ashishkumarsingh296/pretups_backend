package com.apicontrollers.extgw.lastxtransferenquiry;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

//import org.apache.poi.hssf.record.RecalcIdRecord;
import org.testng.annotations.Test;

import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CDP;
//import com.apicontrollers.extgw.AlternateNumber.EXTGWADDALTNUMERAPI;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.ExtentI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_LastXTransferEnquiry extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/*
	 * @throws Exception 
	 * @test Id EXTGWADDALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL WITH ALL CORRECT DATA
	 */
	
	@Test
	public void _01_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ01");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		ArrayList<String> serviceList=   ExtentI.fetchUniqueValuesFromColumn(ExcelI.C2S_SERVICES_SHEET, ExcelI.SERVICE_TYPE);
		
		for(String srcvType : serviceList)
		{
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),srcvType));
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWLASTXTRANSFERENQDP.getAPIdata(srcvType);
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
		
	}
	
	
	@Test
	public void _02_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ02");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		ArrayList<String> serviceList=   ExtentI.fetchUniqueValuesFromColumn(ExcelI.C2S_SERVICES_SHEET, ExcelI.SERVICE_TYPE);
		
		for(String srcvType : serviceList)
		{
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),srcvType));
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWLASTXTRANSFERENQDP.getAPIdata(srcvType);
			apiData.put(EXTGWLASTXTRANSFERENQAPI.EXTNWCODE,"AB");
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
		
	}
	
	@Test
	public void _03_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ03");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		ArrayList<String> serviceList=   ExtentI.fetchUniqueValuesFromColumn(ExcelI.C2S_SERVICES_SHEET, ExcelI.SERVICE_TYPE);
		
		for(String srcvType : serviceList)
		{
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),srcvType));
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWLASTXTRANSFERENQDP.getAPIdata(srcvType);
			apiData.put(EXTGWLASTXTRANSFERENQAPI.EXTNWCODE,"");
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
		
	}
	
	@Test
	public void _04_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ04");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
		ArrayList<String> serviceList=   ExtentI.fetchUniqueValuesFromColumn(ExcelI.C2S_SERVICES_SHEET, ExcelI.SERVICE_TYPE);
		
		for(String srcvType : serviceList)
		{
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),srcvType));
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWLASTXTRANSFERENQDP.getAPIdata(srcvType);
			apiData.put(EXTGWLASTXTRANSFERENQAPI.MSISDN,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.PIN,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.LOGINID,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.PASSWORD,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.EXTCODE,"");
			
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		}
		
	}
	
	
	@Test
	public void _05_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ05");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWC2CDP.getAPIdata();
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SERVICETYPE,"C2C");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SELECTOR,"1");
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}
	@Test
	public void _06_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ06");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWC2CDP.getAPIdata();
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SERVICETYPE,"C2C");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SELECTOR,"1");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.EXTNWCODE,"CD");
			
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}
	@Test
	public void _07_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ07");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWC2CDP.getAPIdata();
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SERVICETYPE,"C2C");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SELECTOR,"1");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.EXTNWCODE,"");
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}
	@Test
	public void _08_LastXTranssferEnquiry() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLASTXTRANSFERENQ08");
		EXTGWLASTXTRANSFERENQAPI VoucherPinResendAPI = new EXTGWLASTXTRANSFERENQAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			
			HashMap<String, String> apiData= EXTGWC2CDP.getAPIdata();
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SERVICETYPE,"C2C");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.SELECTOR,"1");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.MSISDN,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.PIN,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.LOGINID,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.PASSWORD,"");
			apiData.put(EXTGWLASTXTRANSFERENQAPI.EXTCODE,"");
			
			
			String API = VoucherPinResendAPI.prepareAPI(apiData);
			
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWLASTXTRANSFERENQAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}
	
	
 }
 