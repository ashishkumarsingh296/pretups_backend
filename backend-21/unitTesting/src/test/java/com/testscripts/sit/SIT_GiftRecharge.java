package com.testscripts.sit;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.ChannelUserMap;
import com.Features.mapclasses.GiftRechargeTransferMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeConfirmNotificationPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_Gift_Recharge)
public class SIT_GiftRecharge extends BaseTest {

	static boolean TestCaseCounter = false;
	C2STransfer c2STransfer;
	RandomGeneration randstr;
	GenerateMSISDN gnMsisdn;
	ChannelUserMap chnlUsrMap;
	ChannelUser chnlUsr;
	HashMap<String, String> paraMap;
	GiftRechargeTransferMap c2strfMap1;
	HashMap<String, String> c2strfMap;
	TransferControlProfile trfCntrlProf;
	AddChannelUserDetailsPage getMessage;
	static String networkCode;
	String type;
	_parser parser;
	String moduleCode;
	String[] caseIDs=new String[]{
			"SITGIFTRECHARGE1","SITGIFTRECHARGE2","SITGIFTRECHARGE3","SITGIFTRECHARGE4","SITGIFTRECHARGE5",
			"SITGIFTRECHARGE6","SITGIFTRECHARGE7","SITGIFTRECHARGE8","SITGIFTRECHARGE9","SITGIFTRECHARGE10",
			"SITGIFTRECHARGE11"};

	@BeforeMethod
	public void dataC2SGift(){
		c2STransfer = new C2STransfer(driver);
		randstr = new RandomGeneration();
		gnMsisdn = new GenerateMSISDN();
		chnlUsrMap = new ChannelUserMap();
		c2strfMap1 = new GiftRechargeTransferMap();
		c2strfMap = c2strfMap1.getC2SGiftMap();
		chnlUsr = new ChannelUser(driver);
		parser = new _parser(); 
		getMessage = new AddChannelUserDetailsPage(driver);
		trfCntrlProf = new TransferControlProfile(driver);
		paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
		networkCode = _masterVO.getMasterValue("Network Code");
		type = "CHANNEL";
		moduleCode = _masterVO.getCaseMasterByID("SITGIFTRECHARGE1").getModuleCode();
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-506") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_C2Srecharge()	throws Exception {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[0]).getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "121";

		String subsmsisdn =UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),amount,subsmsisdn);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-510") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_C2Srecharge()	throws Exception {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[1]).getExtentCase());
		currentNode.assignCategory("SIT");
		String amount = "20";
		String prefix = new UniqueChecker().UC_PrefixData();
		if( prefix != null ){
			String subsmsisdn = prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
			String expected = MessagesDAO.getLabelByKey("c2stranfer.c2srecharge.error.nonetworkprefix");
			String actual;
			try {
				c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),amount,subsmsisdn);
			} catch (Exception ex) {
				Log.info("prepared: " + expected);
				actual = getMessage.getActualMessage();
				 Assertion.assertEquals(actual, expected);
			}
		} else {
	
			Assertion.assertSkip("All Network Prefixes are consumed.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-513") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_C2Srecharge()	throws IOException, InterruptedException {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[2]).getExtentCase());
		currentNode.assignCategory("SIT");
		paraMap.put("outSuspend_chk", "Y");	paraMap.put("searchMSISDN", c2strfMap.get("fromMSISDN")); paraMap.put("loginChange", "N");
		paraMap.put("assgnPhoneNumber", "N");
		ExtentI.Markup(ExtentColor.TEAL, "OutSuspend Channel User");
		chnlUsr.modifyChannelUserDetails(c2strfMap.get("fromCategory"), paraMap);
		
		String actual;
		String expected = MessagesDAO.getC2SLabelByKey("14006");
		try{
		c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
		actual = getMessage.getActualMessage();
		Assertion.assertEquals(actual, expected);
		}
		catch(Exception e){
			actual = getMessage.getActualMessage();
			Assertion.assertEquals(actual, expected);
		}
		
		paraMap.put("outSuspend_chk", "N");
		ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
		chnlUsr.modifyChannelUserDetails(c2strfMap.get("fromCategory"), paraMap);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-517") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void d_C2Srecharge(){
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[3]).getExtentCase());
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0,c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"),c2strfMap.get("fromTCPName"), c2strfMap.get("fromTCPID"));
	
		String actual; 
		String expected = MessagesDAO.getC2SLabelByKey("140004");
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			actual = getMessage.getActualMessage();
			Assertion.assertEquals(actual, expected);	
		}
			catch(Exception e){
				actual = getMessage.getActualMessage();
				Assertion.assertEquals(actual, expected);
			}
		
		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0,c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"),c2strfMap.get("fromTCPName"), c2strfMap.get("fromTCPID"));
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-519") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void e_C2CTransfer(){
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		
		Object[][] data1= DBHandler.AccessHandler.getProductDetailsForC2S(c2strfMap.get("fromLoginID"), c2strfMap.get("service"));
		String maxTrnsfrDB = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.MAXTRNSFR);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID(caseIDs[4]).getExtentCase());
		currentNode.assignCategory("SIT");
		
				long balance= Long.parseLong(data1[0][1].toString());	
				/*parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue()*0.8 + 2);
				String amount = String.valueOf(usrBalance);*/
				long amountSys = (long)(balance*0.8)+_parser.getSystemAmount("2");
				String amount = _parser.getDisplayAmount(amountSys);
				String maxTrnsfr = _parser.getDisplayAmount(Long.parseLong(maxTrnsfrDB));
				String subsmsisdn = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				
				//BigDecimal userbalance =new BigDecimal(Long.parseLong(balance)).divide(new BigDecimal(100)).setScale(2);
		
		String actual;
		String expected;
		if(amountSys>Long.parseLong(maxTrnsfrDB))
			expected = MessagesDAO.prepareC2SMessageByKey("8512", amount,maxTrnsfr);
		else
			expected = MessagesDAO.prepareC2SMessageByKey("6602", data1[0][6].toString(),amount,_parser.getDisplayAmount(balance),"80");
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),amount,subsmsisdn);
			actual = getMessage.getActualMessage();
			Assertion.assertEquals(actual, expected);	
		}
			catch(Exception e){
				actual = getMessage.getActualMessage();
				Assertion.assertEquals(actual, expected);
			}		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	

	@Test
	@TestManager(TestKey = "PRETUPS-520") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void f_C2Srecharge()	throws IOException, InterruptedException {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[5]).getExtentCase());
		currentNode.assignCategory("SIT");
		Object[][] data1= DBHandler.AccessHandler.getProductDetailsForC2S(c2strfMap.get("fromLoginID"), c2strfMap.get("service"));
		String productName = data1[0][5].toString();
		
		trfCntrlProf.modifyTCPPerC2SminimumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), "100", "100", productName);
		
		String actual;
		String expected = MessagesDAO.prepareC2SMessageByKey("6019", "60","100",_masterVO.getProperty("MaximumBalance"));
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"60",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			actual = getMessage.getActualMessage();
			Assertion.assertEquals(actual, expected);	
		}
			catch(Exception e){
				actual = getMessage.getActualMessage();
				Assertion.assertEquals(actual, expected);
			}
		
		trfCntrlProf.modifyTCPPerC2SminimumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), _masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-522") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void g_C2Srecharge()	throws IOException, InterruptedException {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[6]).getExtentCase());
		currentNode.assignCategory("SIT");
		Object[][] data1= DBHandler.AccessHandler.getProductDetailsForC2S(c2strfMap.get("fromLoginID"), c2strfMap.get("service"));
		String productName = data1[0][5].toString();
		
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), "100", "100", productName);
		
		String actual;
		String expected = MessagesDAO.prepareC2SMessageByKey("6019", "101",_masterVO.getProperty("MinimumBalance"),"100");

		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"101",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			actual = getMessage.getActualMessage();
			Assertion.assertEquals(actual, expected);	
		}
			catch(Exception e){
				actual = getMessage.getActualMessage();
				Assertion.assertEquals(actual, expected);
			}
		
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), _masterVO.getProperty("MaximumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName);		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-524") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void h_C2Srecharge()	throws IOException, InterruptedException {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);
		
		String subsmsisdn = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[7]).getExtentCase());
		currentNode.assignCategory("SIT");
	
		c2STransfer.modifyMRPPreference("true",true,"120");
		BigDecimal minutes = new BigDecimal(120).divide(new BigDecimal(60),2, RoundingMode.HALF_UP);
		//Perform 2 different transactions
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"60",subsmsisdn);
			}
			catch(Exception e){
				Assertion.assertFail(new AddChannelUserDetailsPage(driver).getActualMessage());
			}
		
		
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsmsisdn);
		}
			catch(Exception e){
				Assertion.assertFail(new AddChannelUserDetailsPage(driver).getActualMessage());
			}
		
		//Perform transaction with same amount to same subscriber
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[8]).getExtentCase());
		currentNode.assignCategory("SIT");
		
		String expected = MessagesDAO.prepareC2SMessageByKey("3006201", subsmsisdn,String.valueOf(minutes.setScale(2)));
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsmsisdn);
			String msg = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(msg.equals("SUCCESS")){
				Assertion.assertFail("Transaction goes successful.");
			}
		}
			catch(Exception e){
				String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
				Assertion.assertEquals(actual, expected);
			}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-525") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void i_C2Srecharge()	throws IOException, InterruptedException {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[9]).getExtentCase());
		currentNode.assignCategory("SIT");	
		
		c2STransfer.modifyMRPPreference("false",false,"120");
		BigDecimal minutes = new BigDecimal(120).divide(new BigDecimal(60),2, RoundingMode.HALF_UP);
		String subsMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		String actual;
		String expected=MessagesDAO.prepareC2SMessageByKey("2050",subsMSISDN, String.valueOf(minutes.setScale(2)));
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsMSISDN);
			String trfStatus = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(!trfStatus.equals("SUCCESS")){
				Assertion.assertFail("Transaction is not successful.");
			}
		}catch(Exception e){
				Assertion.assertFail(getMessage.getActualMessage());
			}
		
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsMSISDN);
			String trfStatus = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(trfStatus.equals("SUCCESS")){
				Assertion.assertFail("Transaction goes successful.");
			}	
		}catch(Exception e){
				actual=getMessage.getActualMessage();
				Assertion.assertEquals(actual, expected);
			
			}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-526") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void j_C2Srecharge()	throws IOException, InterruptedException {
		final String methodName = "Test_GiftRecharge";
        Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID(caseIDs[10]).getExtentCase());
		currentNode.assignCategory("SIT");	
		
		c2STransfer.modifyMRPPreference("false",true,"0");
		String subsMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsMSISDN);
			String trfStatus = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(!trfStatus.equals("SUCCESS")){
				Assertion.assertFail("Transaction is not successful.");
			}
		}catch(Exception e){
			Assertion.assertFail(getMessage.getActualMessage());	
			}
		
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsMSISDN);
			String trfStatus = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(!trfStatus.equals("SUCCESS")){
				Assertion.assertFail("Transaction is not successful.");
			}	
		}catch(Exception e){
			Assertion.assertFail(getMessage.getActualMessage());
			}
		Assertion.completeAssertions();
		Log.endTestCase(methodName); 
	}
	
	
	}
