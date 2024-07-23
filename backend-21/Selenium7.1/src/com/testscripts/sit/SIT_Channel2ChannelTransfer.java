package com.testscripts.sit;

import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.Channel2ChannelMap;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.loginpages.LoginPage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;

public class SIT_Channel2ChannelTransfer extends BaseTest {
	
	static boolean testCaseCounter = false;
	static String masterSheetPath;
	
	String c2ctransfermodule= "[SIT]C2C Transfer"; 
	String outsuspendmsg = "message.channeltransfer.return.errormsg.useroutsuspend";
	String tcpsuspendmsg  = "channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive";
	String maxbalancereachmsg = "error.transfer.maxbalance.reached";
	String allowedmaxbalancemsg = "error.transfer.allowedmaxpct.isless";
	String multipleofmsg = "channeltransfer.chnltochnlviewproduct.error.multipleof";
	String commmSlabMsg = "channeltransfer.transferdetails.error.commissionprofile.product.notdefine";
	String minmaxQtyMsg = "channeltransfer.chnltochnlviewproduct.error.qtybetweenmaxmin";
	String extentc2clog = "Performing C2C transfer";
	
	String productCode = null;
	String expMessage = null; 
	String expMessage1 =null;
	String productType = null;
	String productName = null;
	String shortName =null;
	static String networkCode;
	static Object[][] data;
	String[] msgParameter1;
	
	C2CTransfer c2cTransfer;
	SuspendChannelUser suspendCHNLUser;
	ResumeChannelUser resumeCHNLUser;	
	ChannelUser chnlUsr;
	_parser parser;
	CommissionProfile commissionProfile;
	TransferControlProfile trfCntrlProf;
	ChannelUserMap chnlUsrMap;
	Channel2ChannelMap c2cMap;
	HashMap<String, String> paraMap;
	
	@BeforeMethod
	public void dataV() {
	c2cTransfer= new C2CTransfer(driver);
	suspendCHNLUser = new SuspendChannelUser(driver);
	resumeCHNLUser = new ResumeChannelUser(driver);	
	chnlUsr = new ChannelUser(driver);
	parser = new _parser();
	commissionProfile  = new CommissionProfile(driver);
	trfCntrlProf = new TransferControlProfile(driver);
	chnlUsrMap = new ChannelUserMap();
	c2cMap = new Channel2ChannelMap();
	paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);
	
	networkCode = _masterVO.getMasterValue("Network Code");
	data = DBHandler.AccessHandler.getProductDetailsForC2C(networkCode, c2cMap.getC2CMap("domainCode"), c2cMap.getC2CMap("fromCategoryCode"), c2cMap.getC2CMap("toCategoryCode"));
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
		try{c2cTransfer.channel2channelTransfer(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"));
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
	
	// 4. To verify that C2C transfer is not successful if Transfer Control Profile associated with sender is not active in the sytem.
	
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
	
	

	//7. To verify that C2C transfer is not successful if the quantity to be transferred is not a multiple of value defined in the receiver's Commission profile.
	@Test
	public void gC2CTransfer() throws InterruptedException, ParseException{
		
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		Log.startTestCase(this.getClass().getName());
		for(int productCount=0;productCount<=data.length;productCount++){
			String quantity = null;
			String balance;
			String multiple = null;
			if(productCount<=(data.length-1)){
						productCode = data[productCount][0].toString();
						productName = data[productCount][1].toString();
						  shortName = data[productCount][2].toString();
						productType = data[productCount][3].toString();
						multiple = "1";
					currentNode=test.createNode("To verify that C2C transfer is not successful if the quantity to be transferred for "+productName+" is not a multiple of value defined in the receiver's Commission profile");
					currentNode.assignCategory("SIT");
					ExtentI.Markup(ExtentColor.TEAL, "Modify multiple of value in Commission profile");
					long time2 = commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName, multiple);
					balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
					parser.convertStringToLong(balance).changeDenomation();
					double usrBalance = (long) (parser.getValue()*0.2) + 0.25;
					quantity = String.valueOf(usrBalance);
			
					Thread.sleep(time2);
					String[] msgParameter11 = new String[]{shortName,multiple};
					performC2CTransaction(shortName, quantity, c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), multipleofmsg, msgParameter11);
					ExtentI.Markup(ExtentColor.TEAL, "Reverting the multiple of value of commission profile");
					long time21 = commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName, _masterVO.getProperty("MultipleOf"));
					Thread.sleep(time21);		
			}

			else if(data.length>1 && productCount>(data.length-1)){
					currentNode=test.createNode("To verify that C2C transfer is not successful if the quantity to be transferred for all products is not a multiple of value defined in the receiver's Commission profile");
					currentNode.assignCategory("SIT");
					
					String[] productType1 = new String[data.length];
					String[] quantity1 = new String[data.length];
					String expectedMsg = null;
					
					StringBuilder bld = new StringBuilder();
					for(int productCount1=0, p=0;productCount1 < data.length;productCount1++){
						productCode = data[productCount1][0].toString();
						productName = data[productCount1][1].toString();
						  shortName = data[productCount1][2].toString();
	
						multiple = "1";
						balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
						parser.convertStringToLong(balance).changeDenomation();
						double usrBalance = (long) (parser.getValue()*0.2) + 0.25;
						quantity1[p] = String.valueOf(usrBalance);
						productType1[p] = shortName;
						expectedMsg = MessagesDAO.prepareMessageByKey(multipleofmsg,shortName,multiple);
						bld.append(expectedMsg);
						expMessage = bld.toString();
						ExtentI.Markup(ExtentColor.TEAL, "Modifying the multiple of value in commission profile for : "+productName);
						long time2=commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName, multiple);
						Thread.sleep(time2);
						p++;
					}
					
					performC2CTransaction1(quantity1, productType1, c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), expMessage);
					
					for(int productCount1=0;productCount1 < data.length;productCount1++){
						productName = data[productCount1][1].toString();
						ExtentI.Markup(ExtentColor.TEAL, "Reverting the multiple of value in commission profile for : "+productName);
						long time2 = commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName, _masterVO.getProperty("MultipleOf"));
						Thread.sleep(time2);
					}
			}
			}
		Log.endTestCase(this.getClass().getName());}
	
	
		
	/*8.To verify that C2C transfer will not be successful if requested amount is not in between minimum and maximum quantity allowed in the receiver's commission profile.	
	  9.To verify that C2C transfer will not be successful if receiver's Commission profile slab is not defined for the requested quantity.*/	
	@Test
	public void hC2CTransfer() throws InterruptedException, ParseException{
		
		if (!testCaseCounter) {
			test=extent.createTest(c2ctransfermodule);
			testCaseCounter = true;
		}
		
		Log.startTestCase(this.getClass().getName());
		for(int productCount=0;productCount<=data.length;productCount++){
			String quantity = null;
			if(productCount<=(data.length-1)){
						productCode = data[productCount][0].toString();
						productName = data[productCount][1].toString();
						  shortName = data[productCount][2].toString();
						productType = data[productCount][3].toString();
						
					currentNode=test.createNode("To verify that C2C transfer will not be successful if requested amount is not in between minimum and maximum quantity allowed for product "+productName+" in the receiver's commission profile.");
					currentNode.assignCategory("SIT");
			
					String[] value = new String[] {"50","100"};
					ExtentI.Markup(ExtentColor.TEAL, "Modifying commission profile slab for : "+productName);
					long time2 = commissionProfile.modifyMinTrfCommissionSlabfromRange(value,c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName);
					quantity = "49";
			
					Thread.sleep(time2);
					String[] msgParameter11 = new String[]{shortName}; 
					performC2CTransaction(shortName, quantity, c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), minmaxQtyMsg, msgParameter11);
					
					currentNode=test.createNode("To verify that C2C transfer will not be successful if receiver's Commission profile slab for "+productName+" is not defined for the requested quantity.");
					currentNode.assignCategory("SIT");
					quantity = "51";
					msgParameter11 = new String[]{productName,quantity};
					performC2CTransaction(shortName, quantity, c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), commmSlabMsg, msgParameter11);
		
					value = new String[]{_masterVO.getProperty("MintransferValue"), "1"};
					ExtentI.Markup(ExtentColor.TEAL, "Revert the modified commission profile slab for : "+productName);	
					long time21 = commissionProfile.modifyMinTrfCommissionSlabfromRange(value,c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName);
					Thread.sleep(time21);		
			}

			else if(data.length>1 && productCount>(data.length-1)){
					currentNode=test.createNode("To verify that C2C transfer will not be successful if requested amount is not in between minimum and maximum quantity allowed for all products in the receiver's commission profile.");
					currentNode.assignCategory("SIT");
					
					String[] productType1 = new String[data.length];
					String[] quantity1 = new String[data.length], quantity2 = new String[data.length];
					String expectedMsg, expectedMsg1 = null;
					String[] value;
					StringBuilder bld = new StringBuilder();
					StringBuilder bld1 = new StringBuilder();
					for(int productCount1=0, p=0;productCount1 < data.length;productCount1++){
						productCode = data[productCount1][0].toString();
						productName = data[productCount1][1].toString();
						  shortName = data[productCount1][2].toString();
	
						quantity1[p] = "49";
						productType1[p] = shortName;
						expectedMsg = MessagesDAO.prepareMessageByKey(minmaxQtyMsg,shortName);
						bld.append(expectedMsg).toString();
						expMessage = bld.toString();
						
						quantity2[p] = "51";
						expectedMsg1 = MessagesDAO.prepareMessageByKey(commmSlabMsg,productName,quantity2[p]);
						bld1.append(expectedMsg1).toString();
						expMessage1 = bld1.toString();
						
						value = new String[] {"50","100"};
						ExtentI.Markup(ExtentColor.TEAL, "Modifying commission profile slab for : "+productName);
						long time2=commissionProfile.modifyMinTrfCommissionSlabfromRange(value,c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName);
						Thread.sleep(time2);
						p++;
					}

					performC2CTransaction1(quantity1, productType1, c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), expMessage);
					
					currentNode=test.createNode("To verify that C2C transfer will not be successful if receiver's Commission profile slab for all products is not defined for the requested quantity.");
					currentNode.assignCategory("SIT");	
					
					performC2CTransaction1(quantity2, productType1, c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), expMessage1);
					
					for(int productCount1=0;productCount1 < data.length;productCount1++){
						productName = data[productCount1][1].toString();
						
						value = new String[]{_masterVO.getProperty("MintransferValue"), "1"};
						ExtentI.Markup(ExtentColor.TEAL, "Revert the modified commission profile slab for : "+productName);	
						long time2 = commissionProfile.modifyMinTrfCommissionSlabfromRange(value,c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"), productName);
						Thread.sleep(time2);
					}
			}}
		Log.endTestCase(this.getClass().getName());	
	}
		
		
			
	 //10. To verify that for C2C, if sender's balance crosses the minimum allowed balance (from transfer control profile) then transaction will be failed.
		//@Test
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

			String[] msgParameter11 = new String[]{shortName};
			performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), commmSlabMsg, msgParameter11);

			trfCntrlProf.modifyTCPminimumBalance(c2cMap.getC2CMap("fromDomain"), c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("fromTCPID"), _masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), productName);
			}	
			Log.endTestCase(this.getClass().getName());
		}
		
	
//function just to optimize lines of code	
	public void performC2CTransaction(String shortName,String quantity,String fromCategory, String toCategory, String toMSISDN, String fromPIN, String msgCode, String...msgParameter ){
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try{new C2CTransfer(driver).channel2channelTransfer(shortName,quantity,fromCategory, toCategory, toMSISDN, fromPIN);
		String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey(msgCode,msgParameter);
		Log.info(" Message fetched from WEB as : "+actualMessage);
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	    catch(Exception e){String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
	    				String expectedMessage = MessagesDAO.prepareMessageByKey(msgCode,msgParameter);
	    				Log.info(" Message fetched from WEB as : "+actualMessage);
						Validator.messageCompare(actualMessage, expectedMessage);}
	}	
	
	public void performC2CTransaction1(String[] quantity1,String[] productType1,String fromCategory, String toCategory, String toMSISDN, String fromPIN, String expMessage){
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try{new C2CTransfer(driver).channel2channelTransfer(quantity1,productType1,fromCategory, toCategory, toMSISDN, fromPIN);
		String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage().replaceAll("\\r|\\n", "");
		String expectedMessage = expMessage;
		Log.info(" Message fetched from WEB as : "+actualMessage);
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	    catch(Exception e){String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage().replaceAll("\\r|\\n", "");
						String expectedMessage = expMessage;
						Log.info(" Message fetched from WEB as : "+actualMessage);
						Validator.messageCompare(actualMessage, expectedMessage);}
	}
	
	public void performC2CTransaction2(String fromCategory,String toCategory,String toMSISDN,String fromPIN, String requiredmsg, boolean prepareMsg, String...msgParameter){
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try{new C2CTransfer(driver).channel2channelTransfer(fromCategory, toCategory, toMSISDN, fromPIN);
		String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expectedMessage = null;
		if(!prepareMsg)
		expectedMessage = MessagesDAO.getLabelByKey(requiredmsg);
		else
		{expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);}
		Log.info(" Message fetched from WEB as : "+actualMessage);
		Validator.messageCompare(actualMessage, expectedMessage);}
	    catch(Exception e){String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
	    				String expectedMessage = null;
	    				if(!prepareMsg)
						expectedMessage = MessagesDAO.getLabelByKey(requiredmsg);
	    				else
	    				expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);
						Log.info(" Message fetched from WEB as : "+actualMessage);
						Validator.messageCompare(actualMessage, expectedMessage);}
	}

}
