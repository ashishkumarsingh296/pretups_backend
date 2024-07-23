package com.apicontrollers.extgw.VMS.VoucherValidation;

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

public class EXTGW_VoucherValidation extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	
	@Test
	public void _01_voucherValidation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERVAL01");
		EXTGW_VoucherValidation_API validationAPI = new EXTGW_VoucherValidation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherValidation_DP.getAPIdata();
		String API = validationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(validationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(validationAPI.ISVALID).toString(), "Y");
		Validator.messageCompare(xmlPath.get(validationAPI.SERIALNO).toString(), apiData.get(validationAPI.SNO));
		
	}
	
	@Test
	public void _02_voucherValidation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERVAL02");
		EXTGW_VoucherValidation_API validationAPI = new EXTGW_VoucherValidation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherValidation_DP.getAPIdata();
		String API = validationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(validationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(validationAPI.ISVALID).toString(), "Y");
		Validator.messageCompare(xmlPath.get(validationAPI.SERIALNO).toString(), apiData.get(validationAPI.SNO));
		
	}
	
	@Test
	public void _03_voucherValidation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERVAL03");
		EXTGW_VoucherValidation_API validationAPI = new EXTGW_VoucherValidation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherValidation_DP.getAPIdata();
		String API = validationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(validationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(validationAPI.ISVALID).toString(), "Y");
		Validator.messageCompare(xmlPath.get(validationAPI.SERIALNO).toString(), apiData.get(validationAPI.SNO));
		
	}
	
	@Test
	public void _04_voucherValidation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERVAL04");
		EXTGW_VoucherValidation_API validationAPI = new EXTGW_VoucherValidation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherValidation_DP.getAPIdata();
		apiData.put(validationAPI.PIN, new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PIN_MAX_LENGTH))));
		String API = validationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(validationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(validationAPI.ISVALID).toString(), "N");
		Validator.messageCompare(xmlPath.get(validationAPI.SERIALNO).toString(), apiData.get(validationAPI.SNO));
		
	}
	
	@Test
	public void _05_voucherValidation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERVAL05");
		EXTGW_VoucherValidation_API validationAPI = new EXTGW_VoucherValidation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherValidation_DP.getAPIdata();
		apiData.put(validationAPI.SNO, UniqueChecker.UC_VOMSSerialNumber());
		String API = validationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(validationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(validationAPI.ISVALID).toString(), "N");
		Validator.messageCompare(xmlPath.get(validationAPI.SERIALNO).toString(), apiData.get(validationAPI.SNO));
		
	}
	
	@Test
	public void _06_voucherValidation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERVAL06");
		EXTGW_VoucherValidation_API validationAPI = new EXTGW_VoucherValidation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherValidation_DP.getAPIdata();
		apiData.put(validationAPI.SNO, DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.GENERATED));
		String API = validationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(validationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(validationAPI.ISVALID).toString(), "N");
		Validator.messageCompare(xmlPath.get(validationAPI.SERIALNO).toString(), apiData.get(validationAPI.SNO));
		
	}
}