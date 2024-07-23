package com.apicontrollers.extgw.SetPin;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_SETPIN extends BaseTest {
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 *  EXTNGWSETPIN01
	 * Positive Test Case For Set PIN
	 */
	@Test
	public void A1_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSETPIN01");
		EXTGW_SETPIN_API setPin = new EXTGW_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
 		HashMap<String, String> apiData = EXTGW_SETPIN_DP.getAPIdata();
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGW_SETPIN_DP.setPIN(xmlPath.get(EXTGW_SETPIN_API.TXNSTATUS).toString());
	}
	
	@Test
	public void B2_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSETPIN02");
		EXTGW_SETPIN_API setPin = new EXTGW_SETPIN_API();
		RandomGeneration randstr = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		HashMap<String, String> apiData = EXTGW_SETPIN_DP.getAPIdata();
		
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String prefix = new UniqueChecker().UC_PrefixData();
		String msisdn = prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
		apiData.put(setPin.MSISDN1, msisdn);
		
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGW_SETPIN_DP.setPIN(xmlPath.get(EXTGW_SETPIN_API.TXNSTATUS).toString());
	}
	
	
	@Test
	public void C3_setPin() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSETPIN03");
		EXTGW_SETPIN_API setPin = new EXTGW_SETPIN_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		String pin = new RandomGeneration().randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.PIN_LENGTH)));
		HashMap<String, String> apiData = EXTGW_SETPIN_DP.getAPIdata();
		apiData.put(setPin.PIN, pin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_SETPIN_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		EXTGW_SETPIN_DP.setPIN(xmlPath.get(EXTGW_SETPIN_API.TXNSTATUS).toString());
	}

}
