package com.apicontrollers.extgw.VMS.VoucherRecharge;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_VoucherRecharge extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	
	@Test
	public void _01_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH01");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		String serialno = apiData.get(rechargeAPI.SNO);
		apiData.put(rechargeAPI.SNO, "");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), serialno);
	}
	
	@Test
	public void _02_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH02");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		apiData.put(rechargeAPI.PIN, new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PIN_MAX_LENGTH))));
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), apiData.get(rechargeAPI.SNO));
	}
	
	@Test
	public void _03_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH03");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		apiData.put(rechargeAPI.SNO, UniqueChecker.UC_VOMSSerialNumber());
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), apiData.get(rechargeAPI.SNO));
	}
	
	@Test
	public void _04_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH04");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		apiData.put(rechargeAPI.PIN,"");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), apiData.get(rechargeAPI.SNO));
	}
	
	@Test
	public void _05_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH05");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		String serialno = apiData.get(rechargeAPI.SNO);
		apiData.put(rechargeAPI.SNO,"");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), serialno);
	}
	
	@Test
	public void _06_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH06");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.CONSUMED);
		apiData.put(rechargeAPI.SNO,sno);
		apiData.put(rechargeAPI.PIN, "");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _07_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH07");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.GENERATED);
		apiData.put(rechargeAPI.SNO,sno);
		apiData.put(rechargeAPI.PIN, "");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _08_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH08");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ENABLE);
		apiData.put(rechargeAPI.SNO,sno);
		apiData.put(rechargeAPI.PIN, "");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _09_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH09");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ONHOLD);
		apiData.put(rechargeAPI.SNO,sno);
		apiData.put(rechargeAPI.PIN, "");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _10_voucherRecharge() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRECH10");
		EXTGW_VoucherRecharge_API rechargeAPI = new EXTGW_VoucherRecharge_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherRecharge_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberForExpiredDate(PretupsI.EXPIRED);
		apiData.put(rechargeAPI.SNO,sno);
		apiData.put(rechargeAPI.PIN, "");
		String API = rechargeAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(rechargeAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(rechargeAPI.SUBSCRIBERMSISDN).toString(), apiData.get(rechargeAPI.SUBID));
		Validator.messageCompare(xmlPath.get(rechargeAPI.SERIALNO).toString(), sno);
	}
}