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
import com.Features.mapclasses.CustomerRcTransferMap;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeConfirmNotificationPage;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;

public class SIT_C2STransfer extends BaseTest {

	static boolean TestCaseCounter = false;
	C2STransfer c2STransfer;
	RandomGeneration randstr;
	GenerateMSISDN gnMsisdn;
	ChannelUserMap chnlUsrMap;
	ChannelUser chnlUsr;
	HashMap<String, String> paraMap;
	CustomerRcTransferMap c2strfMap1;
	HashMap<String, String> c2strfMap;
	TransferControlProfile trfCntrlProf;
	AddChannelUserDetailsPage getMessage;
	static String networkCode;
	static String moduleCode;
	static Object[][] data;
	String caseIDs[]= new String[]{"SITC2STRF1","SITC2STRF2","SITC2STRF3","SITC2STRF4","SITC2STRF5",
			"SITC2STRF6","SITC2STRF7","SITC2STRF8","SITC2STRF9","SITC2STRF10"};
	String type;
	_parser parser;
	@BeforeMethod
	public void dataC2S(){
		c2STransfer = new C2STransfer(driver);
		randstr = new RandomGeneration();
		gnMsisdn = new GenerateMSISDN();
		chnlUsrMap = new ChannelUserMap();
		c2strfMap1 = new CustomerRcTransferMap();
		c2strfMap = c2strfMap1.getC2SMap();
		chnlUsr = new ChannelUser(driver);
		parser = new _parser(); 
		getMessage = new AddChannelUserDetailsPage(driver);
		trfCntrlProf = new TransferControlProfile(driver);
		paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
		networkCode = _masterVO.getMasterValue("Network Code");
		type = "CHANNEL";
		data = DBHandler.AccessHandler.getProductDetails(networkCode, c2strfMap.get("domainCode"), c2strfMap.get("fromCategoryCode"), c2strfMap.get("toCategoryCode"),type);
	}
	
	
	@Test
	public void a_C2Srecharge()	throws Exception {
		Log.startTestCase(this.getClass().getName());
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID(caseIDs[0]);
		moduleCode = CaseMaster1.getModuleCode();
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());//"To verify that channel user is not able to perform Customer Recharge through WEB if subscriber MSISDN is not correct.");
		currentNode.assignCategory("SIT");
		String amount = "20";
		String prefix = new UniqueChecker().UC_PrefixData();
		if( prefix != null ){
		String subsmsisdn = prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
		String expected = MessagesDAO.getLabelByKey("c2stranfer.c2srecharge.error.nonetworkprefix");
		String actual;
		c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),amount,subsmsisdn);
		Log.info("prepared: "+expected);
		actual = getMessage.getActualMessage();
		Validator.messageCompare(actual, expected);
		}
		else {
			Log.skip("All Network Prefixes are consumed.");
		}
	}
	
	@Test
	public void b_C2Srecharge()	throws IOException, InterruptedException {
		Log.startTestCase(this.getClass().getName());
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID(caseIDs[1]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("SIT");
		paraMap.put("outSuspend_chk", "Y");	paraMap.put("searchMSISDN", c2strfMap.get("fromMSISDN")); paraMap.put("loginChange", "N");
		paraMap.put("assgnPhoneNumber", "N");
		ExtentI.Markup(ExtentColor.TEAL, "OutSuspend Channel User");
		chnlUsr.modifyChannelUserDetails(c2strfMap.get("fromCategory"), paraMap);
		
		String actual;
		String expected = MessagesDAO.getC2SLabelByKey("7033");
		try{
		c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"));
		actual = getMessage.getActualMessage();
		Validator.messageCompare(actual, expected);
		}
		catch(Exception e){
			actual = getMessage.getActualMessage();
			Validator.messageCompare(actual, expected);
		}
		
		paraMap.put("outSuspend_chk", "N");
		ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
		chnlUsr.modifyChannelUserDetails(c2strfMap.get("fromCategory"), paraMap);
		Log.endTestCase(this.getClass().getName());
	}
	
	//Automating below test case is not done as the sender balance goes beyond the cardgroup limit 
	/*currentNode = test.createNode("To verify that channel user is not able to perform customer recharge  if requested recharge amount is breaching the 'Minimum residual' balance value in associated TCP");
	currentNode.assignCategory("SIT");*/
	
	
	/*Below test case cannot be automated as the preference for "C2S Minimum Transfer Value" is not available in 6.10.
	 * currentNode = test.createNode("To verify that C2S recharge is fail if recharge amount is less than the 'C2S Minimum Transfer Value' defined in the preference for Customer recharge");
		currentNode.assignCategory("SIT");
	 */
	
	@Test
	public void c_C2Srecharge(){
		Log.startTestCase(this.getClass().getName());
		
		CaseMaster CaseMaster3=_masterVO.getCaseMasterByID(caseIDs[2]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0,c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"),c2strfMap.get("fromTCPName"), c2strfMap.get("fromTCPID"));
	
		String actual; 
		String expected = MessagesDAO.getC2SLabelByKey("7041");
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"));
			actual = getMessage.getActualMessage();
			Validator.messageCompare(actual, expected);	
		}
			catch(Exception e){
				actual = getMessage.getActualMessage();
				Validator.messageCompare(actual, expected);
			}
		
		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0,c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"),c2strfMap.get("fromTCPName"), c2strfMap.get("fromTCPID"));
		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test
	public void d_C2CTransfer(){
		
		Log.startTestCase(this.getClass().getName());
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID(caseIDs[3]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}
		
		Object[][] data1= DBHandler.AccessHandler.getProductDetailsForC2S(c2strfMap.get("fromLoginID"), c2strfMap.get("service"));
		String maxTrnsfrDB = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.MAXTRNSFR);
		currentNode=test.createNode(CaseMaster4.getExtentCase());
		currentNode.assignCategory("SIT");

		long balance= Long.parseLong(data1[0][1].toString());	
		/*parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue()*0.8 + 2);
				String amount = String.valueOf(usrBalance);*/
		long amountSys = (long)(balance*0.8)+_parser.getSystemAmount("2");
		String amount = _parser.getDisplayAmount(amountSys);
		String subsmsisdn = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		String maxTrnsfr = _parser.getDisplayAmount(Long.parseLong(maxTrnsfrDB));
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
			Validator.messageCompare(actual, expected);	
		}
		catch(Exception e){
			actual = getMessage.getActualMessage();
			Validator.messageCompare(actual, expected);
		}		
		Log.endTestCase(this.getClass().getName());}
	

	@Test
	public void e_C2Srecharge()	throws IOException, InterruptedException {
		Log.startTestCase(this.getClass().getName());
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID(caseIDs[4]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}
		currentNode = test.createNode(CaseMaster5.getExtentCase());
		currentNode.assignCategory("SIT");
		Object[][] data1= DBHandler.AccessHandler.getProductDetailsForC2S(c2strfMap.get("fromLoginID"), c2strfMap.get("service"));
		String productName = data1[0][5].toString();
		
		trfCntrlProf.modifyTCPPerC2SminimumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), "100", "100", productName);
		
		String actual;
		String expected = MessagesDAO.prepareC2SMessageByKey("6019", "60","100",_masterVO.getProperty("MaximumBalance"));
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"60",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			actual = getMessage.getActualMessage();
			Validator.messageCompare(actual, expected);	
		}
			catch(Exception e){
				actual = getMessage.getActualMessage();
				Validator.messageCompare(actual, expected);
			}
		
		trfCntrlProf.modifyTCPPerC2SminimumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), _masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName);
		
	}
	
	
	@Test
	public void f_C2Srecharge()	throws IOException, InterruptedException {
		Log.startTestCase(this.getClass().getName());
		CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID(caseIDs[5]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}
		currentNode = test.createNode(CaseMaster6.getExtentCase());
		currentNode.assignCategory("SIT");
		Object[][] data1= DBHandler.AccessHandler.getProductDetailsForC2S(c2strfMap.get("fromLoginID"), c2strfMap.get("service"));
		String productName = data1[0][5].toString();
		
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), "100", "100", productName);
		
		String actual;
		String expected = MessagesDAO.prepareC2SMessageByKey("6019", "101",_masterVO.getProperty("MinimumBalance"),"100");

		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"101",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
			actual = getMessage.getActualMessage();
			Validator.messageCompare(actual, expected);	
		}
			catch(Exception e){
				actual = getMessage.getActualMessage();
				Validator.messageCompare(actual, expected);
			}
		
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(c2strfMap.get("fromDomain"), c2strfMap.get("fromCategory"), c2strfMap.get("fromTCPID"), _masterVO.getProperty("MaximumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName);		
	}
	
	//@Test
	public void g_C2Srecharge()	throws IOException, InterruptedException {
		Log.startTestCase(this.getClass().getName());
		CaseMaster CaseMaster7 = _masterVO.getCaseMasterByID(caseIDs[6]);
		CaseMaster CaseMaster8 = _masterVO.getCaseMasterByID(caseIDs[7]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}
		
		String subsmsisdn = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		currentNode = test.createNode(CaseMaster7.getExtentCase());
		currentNode.assignCategory("SIT");
	
		c2STransfer.modifyMRPPreference("true",true,"120");
		BigDecimal minutes = new BigDecimal(120).divide(new BigDecimal(60),2, RoundingMode.HALF_UP);
		//Perform 2 different transactions
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"60",subsmsisdn);
			}
			catch(Exception e){ExtentI.Markup(ExtentColor.RED, new AddChannelUserDetailsPage(driver).getActualMessage());
				currentNode.log(Status.FAIL,"Transaction is not successful.");
			}
		
		
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsmsisdn);
		}
			catch(Exception e){
				ExtentI.Markup(ExtentColor.RED, new AddChannelUserDetailsPage(driver).getActualMessage());
				currentNode.log(Status.FAIL,"Transaction is not successful.");
			}
		
		//Perform transaction with same amount to same subscriber
		
		currentNode = test.createNode(CaseMaster8.getExtentCase());
		currentNode.assignCategory("SIT");
		
		String expected = MessagesDAO.prepareC2SMessageByKey("3006201", subsmsisdn,String.valueOf(minutes.setScale(2)));
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsmsisdn);
			String msg = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(msg.equals("SUCCESS")){
				currentNode.log(Status.FAIL, "Transaction goes successful.");
			}
		}
			catch(Exception e){
				String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
				Validator.messageCompare(actual, expected);
			}
	}
	
	//@Test
	public void h_C2Srecharge()	throws IOException, InterruptedException {
		Log.startTestCase(this.getClass().getName());
		
		CaseMaster CaseMaster9 = _masterVO.getCaseMasterByID(caseIDs[8]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster9.getExtentCase());
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
				currentNode.log(Status.FAIL, "Transaction is not successful.");
			}
		}catch(Exception e){
				currentNode.log(Status.FAIL, getMessage.getActualMessage());
			}
		
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsMSISDN);
			String trfStatus = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(trfStatus.equals("SUCCESS")){
				currentNode.log(Status.FAIL, "Transaction goes successful.");
			}	
		}catch(Exception e){
				actual=getMessage.getActualMessage();
				Validator.messageCompare(actual, expected);
			
			}
	}
	
	//@Test
	public void i_C2Srecharge()	throws IOException, InterruptedException {
		Log.startTestCase(this.getClass().getName());
		CaseMaster CaseMaster10 = _masterVO.getCaseMasterByID(caseIDs[9]);
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]"+moduleCode);
		TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster10.getExtentCase());
		currentNode.assignCategory("SIT");	
		
		c2STransfer.modifyMRPPreference("false",true,"0");
		String subsMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsMSISDN);
			String trfStatus = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(!trfStatus.equals("SUCCESS")){
				currentNode.log(Status.FAIL, "Transaction is not successful.");
			}
		}catch(Exception e){
			    currentNode.log(Status.FAIL, getMessage.getActualMessage());	
			}
		
		try{
			c2STransfer.performC2STransfer(c2strfMap.get("parentCategory"), c2strfMap.get("fromCategory"), c2strfMap.get("fromPIN"),c2strfMap.get("service"),"50",subsMSISDN);
			String trfStatus = new C2SRechargeConfirmNotificationPage(driver).transferStatus();
			if(!trfStatus.equals("SUCCESS")){
				currentNode.log(Status.FAIL, "Transaction is not successful.");
			}	
		}catch(Exception e){
				currentNode.log(Status.FAIL, getMessage.getActualMessage());
			}
	}
}

	
