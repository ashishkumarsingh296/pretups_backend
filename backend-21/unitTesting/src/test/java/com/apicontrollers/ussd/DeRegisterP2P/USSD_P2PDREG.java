package com.apicontrollers.ussd.DeRegisterP2P;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.ussd.P2PRegistration.USSD_P2PREG_API;
import com.apicontrollers.ussd.PrivateRechargeDeActivation.USSD_PRD_API;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_P2PDREG extends BaseTest {
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	@Test
	public void A1_P2PDEREGISTER_API() throws SQLException, ParseException {
		
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDEREG01");
		
		USSD_P2PREG_API RechargeReg = new USSD_P2PREG_API();
		USSD_DeRegisterP2P_API RechargedeReg = new USSD_DeRegisterP2P_API();
		RandomGeneration randomGenerator = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		
		if (TestCaseCounter == false) {
			com.classes.BaseTest.test = com.classes.BaseTest.extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		com.classes.BaseTest.currentNode = com.classes.BaseTest.test.createNode(CaseMaster.getDescription());
		com.classes.BaseTest.currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		preapiData2.put(RechargeReg.MSISDN,  _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());	
		preapiData2.put(RechargeReg.TYPE, "REGREQ");	
		preapiData2.put(RechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = RechargeReg.prepareAPI(preapiData2);
		_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
		HashMap<String, String> preapiData1 =new HashMap<String, String>();
		preapiData2.put(RechargedeReg.MSISDN,preapiData2.get("MSISDN1") );	
		preapiData2.put(RechargedeReg.PIN,Decrypt.decryption(DBHandler.AccessHandler.getSubscriberP2PPin(preapiData2.get("MSISDN1"))) );
		preapiData2.put(RechargeReg.TYPE, "DREGREQ");
		String preAPI = RechargedeReg.prepareAPI(preapiData2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preAPI);
	_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(USSD_PRD_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
}
