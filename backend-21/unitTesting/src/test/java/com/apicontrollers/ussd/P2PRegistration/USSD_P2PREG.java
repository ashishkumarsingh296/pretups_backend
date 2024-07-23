package com.apicontrollers.ussd.P2PRegistration;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

import com.apicontrollers.ussd.DeRegisterP2P.USSD_DeRegisterP2P_API;
import com.apicontrollers.ussd.PrivateRechargeDeActivation.USSD_PRD_API;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_P2PREG extends BaseTest
{
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	//To Verify that subscriber is able to register for P2P service through USSD.
	@Test
	public void A1_P2PREGISTER_API() throws SQLException, ParseException {
		
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDREG01");
		
		USSD_P2PREG_API RechargeReg = new USSD_P2PREG_API();
		USSD_DeRegisterP2P_API RechargedeReg = new USSD_DeRegisterP2P_API();
		RandomGeneration randomGenerator = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		com.classes.BaseTest.currentNode = com.classes.BaseTest.test.createNode(CaseMaster.getDescription());
		com.classes.BaseTest.currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		preapiData2.put(RechargeReg.MSISDN,  _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());	
		preapiData2.put(RechargeReg.TYPE, "REGREQ");	
		preapiData2.put(RechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = RechargeReg.prepareAPI(preapiData2);
		String[] APIResponse=_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
	_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(USSD_PRD_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	//To Verify the Validity of self generated Pin
	@Test
	public void A1_P2PREGISTER_PINVALID_API() throws SQLException, ParseException {
		
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDREG02");
		
		USSD_P2PREG_API RechargeReg = new USSD_P2PREG_API();
		USSD_DeRegisterP2P_API RechargedeReg = new USSD_DeRegisterP2P_API();
		RandomGeneration randomGenerator = new RandomGeneration();
		if (TestCaseCounter == false) {
			com.classes.BaseTest.test = com.classes.BaseTest.extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		com.classes.BaseTest.currentNode = com.classes.BaseTest.test.createNode(CaseMaster.getExtentCase());
		com.classes.BaseTest.currentNode = com.classes.BaseTest.test.createNode(CaseMaster.getDescription());
		com.classes.BaseTest.currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		preapiData2.put(RechargeReg.MSISDN,  _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());	
		preapiData2.put(RechargeReg.TYPE, "REGREQ");	
		preapiData2.put(RechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = RechargeReg.prepareAPI(preapiData2);
		String[] APIResponse=_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
	_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		String pin=DBHandler.AccessHandler.getSubscriberP2PPin(preapiData2.get("MSISDN1"));
		if(vailidatePIN(Decrypt.decryption(pin)))
		{
			ExtentI.Markup(ExtentColor.GREEN, "TestCase is successful Pin generated is vaild");
			com.classes.BaseTest.currentNode.log(Status.PASS, "TestCase is successful Pin generated is vaild");
		}
		else
		{
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Pin generated is not vaild ");
			com.classes.BaseTest.currentNode.log(Status.FAIL, "TestCase is not successful as Pin generated is not vaild ");
		}
		//Validator.messageCompare(xmlPath.get(USSD_PRD_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	public boolean vailidatePIN(String Pin)
	{
		
		if(Pin.length()!=4)
			return false ;
		
		
		   String regex1 = "[0-9]{4}"; 
		// String regex2 = "[0-9]{4}"; 
	        // compiling regex 
	        Pattern p = Pattern.compile(regex1); 
	          
	        // Creates a matcher that will match input1 against regex 
	        Matcher m = p.matcher(Pin); 
	        if(m.find() && m.group().equals(Pin)) 
	        {
	            System.out.println(Pin + " is a valid System Pin");
	            return true;    
	        } 
	        else{
	            System.out.println(Pin + " is not a valid PIN"); 
	            return false;
	        }
		
	}
	
	
	
}


