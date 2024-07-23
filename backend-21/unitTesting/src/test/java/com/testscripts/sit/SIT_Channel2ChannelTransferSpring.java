package com.testscripts.sit;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CTransferSpring;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.Channel2ChannelMapSpring;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.dbrepository.DBHandler;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferInitiatePageSpring;
import com.pageobjects.loginpages.LoginPage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;

public class SIT_Channel2ChannelTransferSpring extends BaseTest {
	
	static boolean testCaseCounter = false;
	static String masterSheetPath;
	
	String c2ctransfermodule= "[SIT]C2C Transfer"; 
	
	String tcpsuspendmsg  = "channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive";
	String maxbalancereachmsg = "error.transfer.maxbalance.reached";
	String allowedmaxbalancemsg = "error.transfer.allowedmaxpct.isless";
	String extentc2clog = "Performing C2C transfer";
	String commmSlabMsg = "channeltransfer.transferdetails.error.commissionprofile.product.notdefine";
	String multipleofmsg = "pretups.channeltransfer.chnltochnlviewproduct.error.multipleof";
	String minmaxQtyMsg = "pretups.channeltransfer.chnltochnlviewproduct.error.qtybetweenmaxmin";
	String outsuspendmsg = "pretups.channeltransfer.chnltochnlsearchuser.usernotfound.msg.transferoutsuspend";
	
	
	String productCode = null;
	String expMessage = null; 
	String expMessage1 =null;
	String productType = null;
	String productName = null;
	String shortName =null;
	static String networkCode;
	static Object[][] data;
	String[] msgParameter1;
	
	C2CTransferSpring c2cTransferSpring;
	SuspendChannelUser suspendCHNLUser;
	ResumeChannelUser resumeCHNLUser;	
	ChannelUser chnlUsr;
	_parser parser;
	CommissionProfile commissionProfile;
	TransferControlProfile trfCntrlProf;
	ChannelUserMap chnlUsrMap;
	Channel2ChannelMapSpring c2cMap;
	HashMap<String, String> paraMap;
	String type;
	
	@BeforeMethod
	public void dataV() {
	
	c2cTransferSpring= new C2CTransferSpring(driver);
	suspendCHNLUser = new SuspendChannelUser(driver);
	resumeCHNLUser = new ResumeChannelUser(driver);	
	chnlUsr = new ChannelUser(driver);
	parser = new _parser();
	commissionProfile  = new CommissionProfile(driver);
	trfCntrlProf = new TransferControlProfile(driver);
	chnlUsrMap = new ChannelUserMap();
	c2cMap = new Channel2ChannelMapSpring();
	paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
	
	networkCode = _masterVO.getMasterValue("Network Code");
	type = "CHANNEL";
	data = DBHandler.AccessHandler.getProductDetails(networkCode, c2cMap.getC2CMap("domainCode"), c2cMap.getC2CMap("fromCategoryCode"), c2cMap.getC2CMap("toCategoryCode"), type);
}
// 1. To verify that C2C transfer is not successful if sender is suspended.
	@Test
	public void aC2CTransfer() {
		Log.startTestCase(this.getClass().getName());
			
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		
		currentNode=test.createNode("To verify that C2C Transfer is not successful if Sender channel user is suspended.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
        suspendCHNLUser.suspendChannelUser_MSISDN(c2cMap.getC2CMap("fromMSISDN"), "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(c2cMap.getC2CMap("fromMSISDN"), "Automation remarks");
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try{c2cTransferSpring.channel2channelTransfer(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"));
			currentNode.log(Status.FAIL, "C2C Transfer is successful.");}
		catch(Exception e){String actualMessage = new LoginPage(driver).getErrorMessage();
							String expectedMessage = MessagesDAO.prepareMessageByKey("login.index.error.userloginnotallowed");
							Validator.messageCompare(actualMessage, expectedMessage);}
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(c2cMap.getC2CMap("fromMSISDN"), "Auto Resume Remarks");
		Log.endTestCase(this.getClass().getName());
	}
	
	// 2. To verify that channel transfer is not initiated if  Sender is OUT suspended 
	
	@Test
	public void bC2CTransfer() throws InterruptedException{
		
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		Log.startTestCase(this.getClass().getName());
		currentNode=test.createNode("To verify that channel transfer is not initiated if  Sender is OUT suspended.");
		currentNode.assignCategory("SIT");
		paraMap.put("outSuspend_chk", "Y");	paraMap.put("searchMSISDN", c2cMap.getC2CMap("fromMSISDN")); paraMap.put("loginChange", "N");
		paraMap.put("assgnPhoneNumber", "N");
		ExtentI.Markup(ExtentColor.TEAL, "OutSuspend Channel User");
		chnlUsr.modifyChannelUserDetails(c2cMap.getC2CMap("fromCategory"), paraMap);
		
		msgParameter1 = new String[]{}; 
		
		performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), outsuspendmsg, false, msgParameter1);
		paraMap.put("outSuspend_chk", "N");
		ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
		chnlUsr.modifyChannelUserDetails(c2cMap.getC2CMap("fromCategory"), paraMap);
		Log.endTestCase(this.getClass().getName());
	}
	
	// 3. To verify that C2C transfer is not successful if Transfer Control Profile associated with sender is not active in the sytem.
	
	@Test
	public void cC2CTransfer(){
		
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		Log.startTestCase(this.getClass().getName());
		currentNode=test.createNode("To verify that C2C transfer is not successful if Transfer Control Profile associated with sender is not active in the sytem.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0,c2cMap.getC2CMap("fromDomain"), c2cMap.getC2CMap("fromCategory"),c2cMap.getC2CMap("fromTCPName"), c2cMap.getC2CMap("fromTCPID"));
		
		msgParameter1= new String[]{c2cMap.getC2CMap("fromMSISDN")};
		performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), tcpsuspendmsg, true, msgParameter1);
		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0,c2cMap.getC2CMap("fromDomain"), c2cMap.getC2CMap("fromCategory"),c2cMap.getC2CMap("fromTCPName"), c2cMap.getC2CMap("fromTCPID"));
		Log.endTestCase(this.getClass().getName());
	}
	
	// 4. To verify that C2C transfer is not successful if Transfer Control Profile associated with receiver is not active in the sytem.
	
	@Test
	public void dC2CTransfer(){
		
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		Log.startTestCase(this.getClass().getName());
		currentNode=test.createNode("To verify that C2C transfer is not successful if Transfer Control Profile associated with receiver is not active in the sytem.");
		currentNode.assignCategory("SIT");	
			ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP");
			trfCntrlProf.channelLevelTransferControlProfileSuspend(0,c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toTCPName"), c2cMap.getC2CMap("toTCPID"));
			msgParameter1  = new String[]{c2cMap.getC2CMap("toMSISDN")};
			performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), tcpsuspendmsg, true, msgParameter1);
			
			ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
			trfCntrlProf.channelLevelTransferControlProfileActive(0,c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toTCPName"), c2cMap.getC2CMap("toTCPID"));
			Log.endTestCase(this.getClass().getName());
	}
	
	
	// 5. To verify that if sender make transaction of more than allowed maximum percentage (form transfer profile) then transaction would be failed.

	@Test
	public void eC2CTransfer(){
		
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		Log.startTestCase(this.getClass().getName());
		for(int productCount=0;productCount<=data.length;productCount++){
		if(productCount<=(data.length-1)){
					productCode = data[productCount][0].toString();
					productName = data[productCount][1].toString();
					  shortName = data[productCount][2].toString();
					productType = data[productCount][3].toString();

				currentNode=test.createNode("To verify that if sender user perform c2c transaction of more than allowed maximum percentage via '"+shortName+"' then transaction get failed.");
				currentNode.assignCategory("SIT");
		
				String balance= DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));	
				parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue()*0.8 + 2);
				
				String[] msgParameter11 = new String[]{shortName,_masterVO.getProperty("AllowedMaxPercentage")};
				performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), allowedmaxbalancemsg, msgParameter11);
				}

		else if(data.length>1 && productCount>(data.length-1)){
				currentNode=test.createNode("To verify that if sender user perform c2c transaction of more than allowed maximum percentage from all the available products  then transaction get failed.");
				currentNode.assignCategory("SIT");
				String[] productBalance = new String[data.length];
				String[] productType1 = new String[data.length];
				String expectedMsg = null;
				StringBuilder bld = new StringBuilder();
				for(int productCount1=0, p=0;productCount1 < data.length;productCount1++){
					productCode = data[productCount1][0].toString();
					productName = data[productCount1][1].toString();
					  shortName = data[productCount1][2].toString();
					productType = data[productCount1][3].toString();
	
					String balance= DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));	
					parser.convertStringToLong(balance).changeDenomation();
					long usrBalance = (long) (parser.getValue()*0.8 + 2);

					productType1[p] = shortName;
					productBalance[p] = String.valueOf(usrBalance);
					if(parser.getValue()<usrBalance){
						expectedMsg = MessagesDAO.prepareMessageByKey("pretups.channeltransfer.chnltochnlviewproduct.error.qtymorenetworkstock",_masterVO.getProperty("AllowedMaxPercentage"));
					}
					expectedMsg = MessagesDAO.prepareMessageByKey(allowedmaxbalancemsg,shortName,_masterVO.getProperty("AllowedMaxPercentage"));
					
					bld.append(expectedMsg);
					expMessage = bld.toString();
					p++;
				}
				
				performC2CTransaction1(productBalance, productType1, c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), expMessage);
		}
		}Log.endTestCase(this.getClass().getName());}
	
	//6. To verify that if receiver’s balance crosses the maximum allowed balance (from transfer profile) then C2C transaction would be failed.	
	
	@Test
	public void fC2CTransfer(){
		
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		Log.startTestCase(this.getClass().getName());
		for(int productCount=0;productCount<data.length;productCount++){
			productName = data[productCount][1].toString();
			shortName = data[productCount][2].toString();
			currentNode=test.createNode("To verify that if sender user perform c2c transaction of more than maximum balance of product "+productName+"  then transaction get failed.");
			currentNode.assignCategory("SIT");
			Log.info("No. of products available : "+data.length +" | "+ productName);
		 	
			ExtentI.Markup(ExtentColor.TEAL, "Modify TCP");
		trfCntrlProf.modifyTCPmaximumBalance(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("channeltcpID"), "50","49", productName);
		
		msgParameter1 = new String[]{shortName};
		performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), maxbalancereachmsg, true, msgParameter1);
		
		ExtentI.Markup(ExtentColor.TEAL, "Revert the modified values of TCP");
		trfCntrlProf.modifyTCPmaximumBalance(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("channeltcpID"), _masterVO.getProperty("MaximumBalance"),_masterVO.getProperty("AlertingCount"), productName);
		}
		Log.endTestCase(this.getClass().getName());}
	
	
		
	 //10. To verify that for C2C, if sender's balance crosses the minimum allowed balance (from transfer control profile) then transaction will be failed.
		@Test
		public void iC2CTransfer(){
			
			if (!testCaseCounter) {
				test=extent.createTest(c2ctransfermodule);
				testCaseCounter = true;
			}
			
			Log.startTestCase(this.getClass().getName());
			for(int productCount1=0;productCount1<data.length;productCount1++){
				productCode = data[productCount1][0].toString();
				productName = data[productCount1][1].toString();
				shortName = data[productCount1][2].toString();
				currentNode=test.createNode("To verify that for C2C, if sender's balance for product "+productName+" crosses the minimum allowed balance (from transfer control profile) then transaction will be failed.");
				currentNode.assignCategory("SIT");
				Log.info("No. of products available : "+data.length +" | "+ productName);
			 	
			trfCntrlProf.modifyTCPminimumBalance(c2cMap.getC2CMap("fromDomain"), c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("fromTCPID"), "100","100", productName);
			
			String balance= DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));	
			parser.convertStringToLong(balance).changeDenomation();
			long usrBalance = (long) (parser.getValue()) - 100 + 2;
			BigDecimal userbalance = new BigDecimal(usrBalance).setScale(2);
			String[] msgParameter11 = new String[]{new BigDecimal(100).setScale(2).toString(),productName,userbalance.toString()};
			performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), "pretups.channeltransfer.chnltochnlviewproduct.msg.min.residualbalance", msgParameter11);

			trfCntrlProf.modifyTCPminimumBalance(c2cMap.getC2CMap("fromDomain"), c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("fromTCPID"), _masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName);
			}	
			Log.endTestCase(this.getClass().getName());
		}
		
	
//function just to optimize lines of code	
	public void performC2CTransaction(String shortName,String quantity,String fromCategory, String toCategory, String toMSISDN, String fromPIN, String msgCode, String...msgParameter ){
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try{
			new C2CTransferSpring(driver).channel2channelTransfer(shortName,quantity,fromCategory, toCategory, toMSISDN, fromPIN);
		String actualMessage = new C2CTransferInitiatePageSpring(driver).getServerSideErrorMsg();
		String expectedMessage = MessagesDAO.prepareMessageByKey(msgCode,msgParameter);
		Log.info(" actualMessage of successblog : "+actualMessage);
		Log.info("expectedMessage of successblog : "+expectedMessage);
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	    catch(Exception e){String actualMessage = new C2CTransferInitiatePageSpring(driver).getServerSideErrorMsg();
	    				String expectedMessage = MessagesDAO.prepareMessageByKey(msgCode,msgParameter);
	    				Log.info(" actualMessage of exceptionBlog : "+actualMessage);
	    				Log.info("expectedMessage of exceptionBlog : "+expectedMessage);
						Validator.messageCompare(actualMessage, expectedMessage);}
	}	
	
	public void performC2CTransaction1(String[] quantity1,String[] productType1,String fromCategory, String toCategory, String toMSISDN, String fromPIN, String expMessage){
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try{new C2CTransferSpring(driver).channel2channelTransfer(quantity1,productType1,fromCategory, toCategory, toMSISDN, fromPIN);
		String actualMessage = new C2CTransferInitiatePageSpring(driver).getServerSideMultipleErrorMsg();
		String expectedMessage = expMessage;
		Log.info(" actualMessage of successblog : "+actualMessage);
		Log.info("expectedMessage of successblog : "+expectedMessage);
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	    catch(Exception e){String actualMessage = new C2CTransferInitiatePageSpring(driver).getServerSideErrorMsg();
						String expectedMessage = expMessage;
						Log.info(" actualMessage of exceptionBlog : "+actualMessage);
	    				Log.info("expectedMessage of exceptionBlog : "+expectedMessage);
						Validator.messageCompare(actualMessage, expectedMessage);}
	}
	
	public void performC2CTransaction2(String fromCategory,String toCategory,String toMSISDN,String fromPIN, String requiredmsg, boolean prepareMsg, String...msgParameter){
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try{new C2CTransferSpring(driver).channel2channelTransfer(fromCategory, toCategory, toMSISDN, fromPIN);
		String actualMessage = new C2CTransferInitiatePageSpring(driver).getServerSideErrorMsg();
		String expectedMessage = null;
		if(!prepareMsg)
		expectedMessage = MessagesDAO.getLabelByKey(requiredmsg);
		else
		{expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);}
		Log.info(" actualMessage of successblog : "+actualMessage);
		Log.info("expectedMessage of successblog : "+expectedMessage);
		Validator.messageCompare(actualMessage, expectedMessage);}
	    catch(Exception e){String actualMessage = new C2CTransferInitiatePageSpring(driver).getServerSideErrorMsg();
	    				String expectedMessage = null;
	    				if(!prepareMsg)
						expectedMessage = MessagesDAO.getLabelByKey(requiredmsg);
	    				else
	    				expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);
	    				Log.info(" actualMessage of exceptionBlog : "+actualMessage);
	    				Log.info("expectedMessage of exceptionBlog : "+expectedMessage);
						Validator.messageCompare(actualMessage, expectedMessage);}
	}

	
	@DataProvider(name = "InputFieldsForC2C_validations")
	public Object[][] InputFieldsForC2C_validations() {

		Channel2ChannelMapSpring c2cMap = new Channel2ChannelMapSpring();

		String[] description = new String[8];
		description[0] = "To verify channel user is able To perform transaction";
		description[1] = "To verify channel user is unable To perform transaction when MSISDN field is blank";
		description[2] = "To verify channel user is unable To perform transaction when MSISDN field is alphabates";
		description[3] = "To verify channel user is unable To perform transaction when Category field is blank";
		description[4] = "To verify channel user is unable To perform transaction when To User field is blank";
		description[5] = "To verify channel user is unable To perform transaction when Quantity field is blank";
		description[6] = "To verify channel user is unable To perform transaction when PIN field is blank";
				
		Object[][] DataForC2C = {
				{ 0, description[0], c2cMap.getC2CMap() },
				{ 1, description[1], c2cMap.getC2CMap("toMSISDN", "") },
				{ 2, description[2], c2cMap.getC2CMap("toMSISDN", "ABCDEF") },
				{ 3, description[3], c2cMap.getC2CMap("toCategory", "") },
				{ 4, description[4], c2cMap.getC2CMap("toUser", "") },
				{ 5, description[5], c2cMap.getC2CMap("quantity", "") },
				{ 6, description[6], c2cMap.getC2CMap("fromPIN", "") },
				
				

		};
		return DataForC2C;

	}

	@Test(dataProvider = "InputFieldsForC2C_validations")
	public void testCycleSIT(int CaseNum, String Description,
			HashMap<String, String> mapParam) throws IOException, InterruptedException {
		Log.startTestCase(this.getClass().getName());
		C2CTransferSpring c2cTransferSpring = new C2CTransferSpring(driver);
		if (testCaseCounter == false) {
			test = extent.createTest("c2ctransfermodule");
			testCaseCounter = true;
		}
		HashMap<String, String> ChannelTransferMap = null;
		switch (CaseNum) {
		case 0: /*To verify channel user is able To perform transaction*/ {
			currentNode = test
					.createNode("To verify channel user is able To perform transaction");
			currentNode.assignCategory("SIT");
			ChannelTransferMap = c2cTransferSpring
					.channel2channelTransferByMapValue(mapParam, "msisdn");
			Validator.messageCompare(ChannelTransferMap.get("actualMessage"),
					ChannelTransferMap.get("expectedMessage"));
			break;
		}
		case 1:
				 /* To verify channel user is unable To perform transaction when
				 * MSISDN field is blank*/
				 
		{
			currentNode = test
					.createNode("To verify channel user is unable To perform transaction when MSISDN field is blank");
			currentNode.assignCategory("SIT");
			ChannelTransferMap = c2cTransferSpring
					.channel2channelTransferByMapValue(mapParam, "msisdn");
			String actualMsg = ChannelTransferMap.get("fieldError");
			String expectedMsg = MessagesDAO
					.getLabelByKey("pretups.channeltransfer.msisdn.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);

			break;
		}
		case 2:
				 /* To verify channel user is unable To perform transaction when
				 * MSISDN field is alphabates*/
				 
		{
			currentNode = test
					.createNode("To verify channel user is unable To perform transaction when MSISDN field is alphabates");
			currentNode.assignCategory("SIT");
			ChannelTransferMap = c2cTransferSpring
					.channel2channelTransferByMapValue(mapParam, "msisdn");
			String actualMsg = ChannelTransferMap.get("fieldError");
			String expectedMsg = MessagesDAO
					.getLabelByKey("pretups.channeltransfer.msisdn.is.not.valid");
			Validator.messageCompare(actualMsg, expectedMsg);

			break;
		}
		case 3:
				 /* To verify channel user is unable To perform transaction when
				  Category field is blank*/
				 
		{
			currentNode = test
					.createNode("To verify channel user is unable To perform transaction when Category field is blank");
			currentNode.assignCategory("SIT");
			ChannelTransferMap = c2cTransferSpring
					.channel2channelTransferByMapValue(mapParam, "loginId");
			String actualMsg = ChannelTransferMap.get("fieldError");
			String expectedMsg = MessagesDAO
					.getLabelByKey("pretups.channeltransfer.category.code.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
		}
		case 4: {
				 /* To verify channel user is unable To perform transaction when
				 * To User field is blank*/
				 
			currentNode = test
					.createNode("To verify channel user is unable To perform transaction when To User field is blank");
			currentNode.assignCategory("SIT");
			ChannelTransferMap = c2cTransferSpring
					.channel2channelTransferByMapValue(mapParam, "loginId");
			String actualMsg = ChannelTransferMap.get("fieldError");
			String expectedMsg = MessagesDAO
					.getLabelByKey("pretups.channeltransfer.touser.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
		}
		case 5: {
				 /*To verify channel user is unable To perform transaction when
				 * Quantity field is blank*/
				 
			currentNode = test
					.createNode("To verify channel user is unable To perform transaction when Quantity field is blank");
			currentNode.assignCategory("SIT");
			ChannelTransferMap = c2cTransferSpring
					.channel2channelTransferByMapValue(mapParam, "msisdn");
			String actualMsg = ChannelTransferMap.get("fieldError");
			Validator.messageCompare(actualMsg, "true");
			break;
		}
		case 6:
				/* * To verify channel user is unable To perform transaction when
				 * PIN field is blank*/
				 
		{
			currentNode = test
					.createNode("To verify channel user is unable To perform transaction when PIN field is blank");
			currentNode.assignCategory("SIT");
			ChannelTransferMap = c2cTransferSpring
					.channel2channelTransferByMapValue(mapParam, "msisdn");
			String actualMsg = ChannelTransferMap.get("fieldError");
			String expectedMsg = MessagesDAO
					.getLabelByKey("pretups.channeltransfer.pin.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);

			break;
		}
		
		default:
			Log.info("No valid data found.");
		}
	}
}
