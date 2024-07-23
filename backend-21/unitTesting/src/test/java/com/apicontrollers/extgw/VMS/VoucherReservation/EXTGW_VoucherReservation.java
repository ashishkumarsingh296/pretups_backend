package com.apicontrollers.extgw.VMS.VoucherReservation;

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
import com.utils.Decrypt;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_VoucherReservation extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	
	@Test
	public void _01_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES01");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String serialno = apiData.get(reservationAPI.SNO);
		apiData.put(reservationAPI.SNO, "");
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), serialno);
	}
	
	@Test
	public void _02_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES02");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String serialno = apiData.get(reservationAPI.SNO);
		apiData.put(reservationAPI.PIN, "");
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), serialno);
	}
	
	@Test
	public void _03_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES03");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.GENERATED);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		apiData.put(reservationAPI.SNO, sno);
		apiData.put(reservationAPI.PIN, pin);
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), sno);
	}
	
	
	@Test
	public void _04_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES04");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberForExpiredDate(PretupsI.EXPIRED);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		apiData.put(reservationAPI.SNO, sno);
		apiData.put(reservationAPI.PIN, pin);
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _05_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES05");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.SUSPENDED);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		apiData.put(reservationAPI.SNO, sno);
		apiData.put(reservationAPI.PIN, pin);
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _06_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES06");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.STOLEN);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		apiData.put(reservationAPI.SNO, sno);
		apiData.put(reservationAPI.PIN, pin);
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _07_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES07");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.CONSUMED);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		apiData.put(reservationAPI.SNO, sno);
		apiData.put(reservationAPI.PIN, pin);
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _08_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES08");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ONHOLD);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		apiData.put(reservationAPI.SNO, sno);
		apiData.put(reservationAPI.PIN, pin);
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), sno);
	}
	
	@Test
	public void _09_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES09");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		apiData.put(reservationAPI.SNO, "");
		apiData.put(reservationAPI.PIN, new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PIN_MAX_LENGTH))));
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), "");
	}

	@Test
	public void _10_voucherReservation() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVOUCHERRES10");
		EXTGW_VoucherReservation_API reservationAPI = new EXTGW_VoucherReservation_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_VoucherReservation_DP.getAPIdata();
		apiData.put(reservationAPI.SNO, UniqueChecker.UC_VOMSSerialNumber());
		apiData.put(reservationAPI.PIN, "");
		String API = reservationAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(reservationAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		Validator.messageCompare(xmlPath.get(reservationAPI.SUBSCRIBERMSISDN).toString(), apiData.get(reservationAPI.SUBID));
		Validator.messageCompare(xmlPath.get(reservationAPI.SERIALNO).toString(), apiData.get(reservationAPI.SNO));
	}
}