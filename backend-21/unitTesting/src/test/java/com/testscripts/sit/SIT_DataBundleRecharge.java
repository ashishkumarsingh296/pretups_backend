package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.ChannelUserMap;
import com.Features.mapclasses.RechargeMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SIT_DataBundleRecharge)
public class SIT_DataBundleRecharge extends BaseTest {

	static boolean TestCaseCounter = false;
	C2STransfer c2STransfer;
	RandomGeneration randstr;
	GenerateMSISDN gnMsisdn;
	ChannelUserMap chnlUsrMap;
	ChannelUser chnlUsr;
	HashMap<String, String> paraMap;
	RechargeMap rechargeMap1;
	HashMap<String, String> c2strfMap;
	TransferControlProfile trfCntrlProf;
	AddChannelUserDetailsPage getMessage;
	static String networkCode;
	String type;
	_parser parser;
	String[] caseIDs = new String[]{"SITPPB1"};
	//String moduleCode;
	
	@BeforeMethod
	public void dataC2SPost(){
		c2STransfer = new C2STransfer(driver);
		randstr = new RandomGeneration();
		gnMsisdn = new GenerateMSISDN();
		chnlUsrMap = new ChannelUserMap();
		rechargeMap1 = new RechargeMap();
		chnlUsr = new ChannelUser(driver);
		parser = new _parser(); 
		getMessage = new AddChannelUserDetailsPage(driver);
		trfCntrlProf = new TransferControlProfile(driver);
		paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
		networkCode = _masterVO.getMasterValue(ExcelI.NETWORK_CODE);
		type = "CHANNEL";
		//moduleCode=_masterVO.getCaseMasterByID("SITPPB1").getModuleCode();
		c2strfMap = rechargeMap1.getC2SMap("DataBundleRecharge");
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-2012") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _01_DataBundleRecharge()	throws Exception {
		
	    Object[][] data = rechargeMap1.TestDataFeed1("DataBundleRecharge");
		
	    for(int i=0;i<data.length;i++){
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITDBRC01").getExtentCase());
		currentNode.assignCategory(TestCategory.SIT);
		final String methodName = "Data Bundle Recharge";
        Log.startTestCase(methodName);
		
		String amount = "41";

		String subsmsisdn =UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		c2STransfer.performC2STransfer(data[i][0].toString(), data[i][1].toString(), data[i][2].toString(),data[i][3].toString(),amount,subsmsisdn);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);}
		
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2013") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _02_DataBundleRecharge()	throws Exception {
		
	    currentNode = test.createNode(_masterVO.getCaseMasterByID("SITDBRC02").getExtentCase());
		currentNode.assignCategory(TestCategory.SIT);
		final String methodName = "Data Bundle Recharge invalid pin";
        Log.startTestCase(methodName);
		
		String amount = "41";
		String actual=null, expected = null;
		String subsmsisdn =UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		String pin = new RandomGeneration().randomNumeric(4);
		expected = MessagesDAO.prepareC2SMessageByKey("7015", "");
		while(pin.equals(c2strfMap.get("fromPIN"))){
				pin = new RandomGeneration().randomNumeric(4);}
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), pin,c2strfMap.get("service"),amount,subsmsisdn);
		}catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		
		Assertion.assertContainsEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);}
	
	@Test
	@TestManager(TestKey = "PRETUPS-2014") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _03_DataBundleRecharge()	throws Exception {
		
	    currentNode = test.createNode(_masterVO.getCaseMasterByID("SITDBRC03").getExtentCase());
		currentNode.assignCategory(TestCategory.SIT);
		final String methodName = "Data Bundle Recharge invalid pin";
        Log.startTestCase(methodName);
		
		String amount = "30";
		String actual=null, expected = null;
		String subsmsisdn =UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		expected = "Message is pending";
		
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),amount,subsmsisdn);
		}catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		
		Assertion.assertContainsEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);}
		
	
	}
