package com.apicontrollers.ussd.SelfBar;

import java.sql.SQLException;
import java.text.ParseException;
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

public class USSD_SelfBar extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TC1_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSB01");
		USSD_SBAPI selfBar = new USSD_SBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SBDP.getAPIdata();
		
		String API = selfBar.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(selfBar.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC2_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSB02");
		USSD_SBAPI selfBar = new USSD_SBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SBDP.getAPIdata();
		apiData.put(selfBar.MSISDN1, "");
		String API = selfBar.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(selfBar.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
}
