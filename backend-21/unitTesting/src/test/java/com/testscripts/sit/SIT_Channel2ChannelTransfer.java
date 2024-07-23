package com.testscripts.sit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.ResultMap;

import com.Features.C2CTransfer;
import com.Features.CacheUpdate;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.Channel2ChannelMap;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.CacheController;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CDetailsPage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferDetailsPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.pageobjects.loginpages.LoginPage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
@ModuleManager(name = Module.SIT_Channel2ChannelTransfer)
public class SIT_Channel2ChannelTransfer extends BaseTest {

	static boolean testCaseCounter = false;
	static String masterSheetPath;

	static String c2ctransfermodule;
	String outsuspendmsg = "message.channeltransfer.return.errormsg.useroutsuspend";
	String tcpsuspendmsg = "channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive";
	String maxbalancereachmsg = "error.transfer.maxbalance.reached";
	String allowedmaxbalancemsg = "error.transfer.allowedmaxpct.isless";
	String multipleofmsg = "channeltransfer.chnltochnlviewproduct.error.multipleof";
	String commmSlabMsg = "channeltransfer.transferdetails.error.commissionprofile.product.notdefine";
	String minmaxQtyMsg = "channeltransfer.chnltochnlviewproduct.error.qtybetweenmaxmin";
	String extentc2clog = "Performing C2C transfer";
	String minresidualBalMsg = "channeltransfer.chnltochnlviewproduct.msg.min.residualbalance";
	String succesfullMsg = "channeltochannel.transfer.msg.success";
	String initiationMsg = "channeltochannel.initiate.transfer.msg.success";
	

	String productCode = null;
	String expMessage = null;
	String expMessage1 = null;
	String productType = null;
	String productName = null;
	String shortName = null;
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
	BusinessValidator BusinessValidator;
	HashMap<String, String> paraMap;
	HashMap<String,String> c2cResultMap;
	ChannelAdminHomePage caHomepage;
	
	String type;
	String[] caseIDs = new String[]{
			"SITC2CTRF1","SITC2CTRF2","SITC2CTRF3","SITC2CTRF4",
			"SITC2CTRF5","SITC2CTRF6","SITC2CTRF7","SITC2CTRF8",
			"SITC2CTRF9","SITC2CTRF10","SITC2CTRF11","SITC2CTRF12",
			"SITC2CTRF13","SITC2CTRF14","SITC2CTRF15","SITC2CTRF16",
			"SITC2CTRF17","SITC2CTRF18"};
	String assignCategory="SIT";
	
	@BeforeMethod
	public void dataV() {
		c2cTransfer = new C2CTransfer(driver);
		suspendCHNLUser = new SuspendChannelUser(driver);
		resumeCHNLUser = new ResumeChannelUser(driver);
		BusinessValidator = new BusinessValidator();
		caHomepage = new ChannelAdminHomePage(driver);
		chnlUsr = new ChannelUser(driver);
		parser = new _parser();
		commissionProfile = new CommissionProfile(driver);
		trfCntrlProf = new TransferControlProfile(driver);
		chnlUsrMap = new ChannelUserMap();
		c2cMap = new Channel2ChannelMap();
		paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);

		networkCode = _masterVO.getMasterValue("Network Code");
		type = "CHANNEL";
		data = DBHandler.AccessHandler.getProductDetails(networkCode, c2cMap.getC2CMap("domainCode"),
				c2cMap.getC2CMap("fromCategoryCode"), c2cMap.getC2CMap("toCategoryCode"), type);
		c2ctransfermodule = "["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2CTRF1").getModuleCode();
		
	}

	// 1. To verify that C2C transfer is not successful if sender is suspended.
	@TestManager(TestKey = "PRETUPS-944") @Test 
	public void aC2CTransfer() {
		final String methodName="aC2CTransfer";Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID(caseIDs[0]);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
		if(!ExcelUtility.isRoleExists(RolesI.SUSPEND_CHANNEL_USER_ROLECODE))
		{
			Assertion.assertSkip("RoleCode not found hence skipped");
		}
		else{
		suspendCHNLUser.suspendChannelUser_MSISDN(c2cMap.getC2CMap("fromMSISDN"), "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(c2cMap.getC2CMap("fromMSISDN"), "Automation remarks");
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		try {
			c2cTransfer.channel2channelTransfer(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
					c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"));
			Assertion.assertFail("C2C Transfer is successful.");
			
		} catch (Exception e) {
			String actualMessage = new LoginPage(driver).getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("login.index.error.userloginnotallowed");
			String expectedMessage2 = MessagesDAO.prepareMessageByKey("login.index.error.usersuspended");

			if(actualMessage.equalsIgnoreCase(expectedMessage) || actualMessage.equalsIgnoreCase(expectedMessage2))
				Assertion.assertPass("User can not login to system with suspended user");
			else
				Assertion.assertFail("User can login with a suspend user");
		}
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(c2cMap.getC2CMap("fromMSISDN"), "Auto Resume Remarks");}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	// 2. To verify that channel transfer is not initiated if Sender is OUT
	// suspended

	@TestManager(TestKey = "PRETUPS-947") @Test 
	public void bC2CTransfer() throws InterruptedException {

		 String value = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID(caseIDs[1]);
	
		final String methodName="bC2CTransfer";Log.startTestCase(methodName);
		currentNode = test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory(assignCategory);
		paraMap.put("outSuspend_chk", "Y");
		paraMap.put("searchMSISDN", c2cMap.getC2CMap("fromMSISDN"));
		paraMap.put("loginChange", "N");
		paraMap.put("assgnPhoneNumber", "N");
		ExtentI.Markup(ExtentColor.TEAL, "OutSuspend Channel User");
		chnlUsr.modifyChannelUserDetails(c2cMap.getC2CMap("fromCategory"), paraMap);

		msgParameter1 = new String[] {};

		performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
				c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), outsuspendmsg,maxApprovalLevel, false, msgParameter1);
		paraMap.put("outSuspend_chk", "N");
		ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
		chnlUsr.modifyChannelUserDetails(c2cMap.getC2CMap("fromCategory"), paraMap);
		Log.endTestCase(methodName);
	}

	// 3. To verify that C2C transfer is not successful if Transfer Control
	// Profile associated with sender is not active in the sytem.

	@TestManager(TestKey = "PRETUPS-949") @Test 
	public void cC2CTransfer() {
		 String value = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID(caseIDs[2]);	
		final String methodName="cC2CTransfer";Log.startTestCase(methodName);
		currentNode = test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0, c2cMap.getC2CMap("fromDomain"),
				c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("fromTCPName"), c2cMap.getC2CMap("fromTCPID"));

		msgParameter1 = new String[] { c2cMap.getC2CMap("fromMSISDN") };
		performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
				c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), tcpsuspendmsg,maxApprovalLevel, true, msgParameter1);
		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0, c2cMap.getC2CMap("fromDomain"),
				c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("fromTCPName"), c2cMap.getC2CMap("fromTCPID"));
		Log.endTestCase(methodName);
	}

	// 4. To verify that C2C transfer is not successful if Transfer Control
	// Profile associated with sender is not active in the sytem.

	@TestManager(TestKey = "PRETUPS-951") @Test 
	public void dC2CTransfer() {

		 String value = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID(caseIDs[3]);
		
			
			
		final String methodName="dC2CTransfer";Log.startTestCase(methodName);
		currentNode = test.createNode(CaseMaster4.getExtentCase());
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0, c2cMap.getC2CMap("toDomain"),
				c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toTCPName"), c2cMap.getC2CMap("toTCPID"));
		msgParameter1 = new String[] { c2cMap.getC2CMap("toMSISDN") };
		performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
				c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), tcpsuspendmsg,maxApprovalLevel, true, msgParameter1);

		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0, c2cMap.getC2CMap("toDomain"),
				c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toTCPName"), c2cMap.getC2CMap("toTCPID"));
		Log.endTestCase(methodName);
	}

	// 5. To verify that if sender make transaction of more than allowed maximum
	// percentage (form transfer profile) then transaction would be failed.

	@TestManager(TestKey = "PRETUPS-955") @Test 
	public void eC2CTransfer() throws InterruptedException {

		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel=0;
        if(BTSLUtil.isNullString(value)) {
        	maxApprovalLevel=0;
        }
        else
		maxApprovalLevel = Integer.parseInt(value);
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID(caseIDs[4]);
		CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID(caseIDs[5]);
		
			
		
		final String methodName="eC2CTransfer";Log.startTestCase(methodName);
		for (int productCount = 0; productCount <= data.length; productCount++) {
			if (productCount <= (data.length - 1)) {
				productCode = data[productCount][0].toString();
				productName = data[productCount][1].toString();
				shortName = data[productCount][2].toString();
				productType = data[productCount][3].toString();

				currentNode = test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), shortName));
				currentNode.assignCategory(assignCategory);

				String balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
				parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue() * 0.8 + 2);

				
			
				
				 if(BTSLUtil.isNullString(value)) {
					 String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
						c2cResultMap = performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),
								c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
								allowedmaxbalancemsg, msgParameter11);
		            	Log.info("C2C Approval level is not Applicable");
		        		}
		            else {
		            	if(maxApprovalLevel == 0)
		        		{
		            		String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
		            		c2cResultMap = performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),
		    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
		    						allowedmaxbalancemsg, msgParameter11);
		            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
		        		}
		            	if(maxApprovalLevel == 1)
		        		{
		            		
		            		String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
		            		c2cResultMap = performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),
		    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
		    						initiationMsg, msgParameter11);
		            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),allowedmaxbalancemsg,msgParameter11);
		        		}
		            	else if(maxApprovalLevel == 2)
		        		{
		            		String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
		            		
		            		c2cResultMap = performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),
		    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
		    						initiationMsg, msgParameter11);
		            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
		            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),allowedmaxbalancemsg,msgParameter11);
		        		}
		            	else if(maxApprovalLevel == 3)		
		        		{
		            		String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
		
		            		c2cResultMap = performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),
		    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
		    						initiationMsg, msgParameter11);
		            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
		            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
		            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),allowedmaxbalancemsg,msgParameter11);
		        		}
		        } 
			}

			else if (data.length > 1 && productCount > (data.length - 1)) {
				currentNode = test.createNode(CaseMaster6.getExtentCase());
				currentNode.assignCategory(assignCategory);
				String[] productBalance = new String[data.length];
				String[] productType1 = new String[data.length];
				String expectedMsg = null;
				StringBuilder bld = new StringBuilder();
				for (int productCount1 = 0, p = 0; productCount1 < data.length; productCount1++) {
					productCode = data[productCount1][0].toString();
					productName = data[productCount1][1].toString();
					shortName = data[productCount1][2].toString();
					productType = data[productCount1][3].toString();

					String balance = DBHandler.AccessHandler.getUserBalance(productCode,
							c2cMap.getC2CMap("fromLoginID"));
					parser.convertStringToLong(balance).changeDenomation();
					long usrBalance = (long) (parser.getValue() * 0.8 + 2);

					productType1[p] = shortName;
					productBalance[p] = String.valueOf(usrBalance);
					expectedMsg = MessagesDAO.prepareMessageByKey(allowedmaxbalancemsg, shortName,
							_masterVO.getProperty("AllowedMaxPercentage"));

					bld.append(expectedMsg);
					expMessage = bld.toString();
					p++;
				}

		
			if(BTSLUtil.isNullString(value)) {
				//String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
				c2cResultMap= performC2CTransaction1(productBalance, productType1, c2cMap.getC2CMap("fromCategory"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
						expMessage);

	            	Log.info("C2C Approval level is not Applicable");

	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		c2cResultMap= performC2CTransaction1(productBalance, productType1, c2cMap.getC2CMap("fromCategory"),
	    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
	    						expMessage);
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		
	            		c2cResultMap= performC2CTransaction1(productBalance, productType1, c2cMap.getC2CMap("fromCategory"),
	    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
	    						initiationMsg);
	            		
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),expMessage);   		
	 
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cResultMap= performC2CTransaction1(productBalance, productType1, c2cMap.getC2CMap("fromCategory"),
	    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
	    						initiationMsg);
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),expMessage);
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cResultMap= performC2CTransaction1(productBalance, productType1, c2cMap.getC2CMap("fromCategory"),
	    						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
	    						initiationMsg);
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
	            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),expMessage);
	        		}
	        } 
			}
			
		}
	Log.endTestCase(methodName);
	}

	// 6. To verify that if receiver�s balance crosses the maximum allowed
	// balance (from transfer profile) then C2C transaction would be failed.

	@TestManager(TestKey = "PRETUPS-956") @Test 
	public void fC2CTransfer() throws InterruptedException {
		 String value = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		 int maxApprovalLevel=0;
		 if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		CaseMaster CaseMaster7 = _masterVO.getCaseMasterByID(caseIDs[6]);
		final String methodName="fC2CTransfer";Log.startTestCase(methodName);
		for (int productCount = 0; productCount < data.length; productCount++) {
			productName = data[productCount][1].toString();
			shortName = data[productCount][2].toString();
			currentNode = test.createNode(MessageFormat.format(CaseMaster7.getExtentCase(),productName));
			currentNode.assignCategory(assignCategory);
			Log.info("No. of products available : " + data.length + " | " + productName);

			ExtentI.Markup(ExtentColor.TEAL, "Modify TCP");
			trfCntrlProf.modifyTCPmaximumBalance(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"),
					c2cMap.getC2CMap("channeltcpID"), "50", "49", productName);

			msgParameter1 = new String[] { shortName };
						
			if(BTSLUtil.isNullString(value)) {
				c2cResultMap = performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
						c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), maxbalancereachmsg,maxApprovalLevel, true, msgParameter1);
            	Log.info("C2C Approval level is not Applicable");
        		}
            else {
            	if(maxApprovalLevel == 0)
        		{     	
            		c2cResultMap = performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
    						c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), maxbalancereachmsg,maxApprovalLevel, true, msgParameter1);
            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		
            		//String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
            		c2cResultMap = performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
    						c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), initiationMsg,maxApprovalLevel, true, msgParameter1);
            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxbalancereachmsg,msgParameter1);
        		}
            	else if(maxApprovalLevel == 2)
        		{
            		//String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };
            		
            		c2cResultMap = performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
    						c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), initiationMsg,maxApprovalLevel, true, msgParameter1);
            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxbalancereachmsg,msgParameter1);
        		}
            	else if(maxApprovalLevel == 3)		
        		{
            		//String[] msgParameter11 = new String[] { shortName, _masterVO.getProperty("AllowedMaxPercentage") };

            		c2cResultMap = performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),
    						c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), initiationMsg,maxApprovalLevel, true, msgParameter1);
            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxApprovalLevel);
            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"),maxbalancereachmsg,msgParameter1);
        		}
     	
        } 

			ExtentI.Markup(ExtentColor.TEAL, "Revert the modified values of TCP");
			trfCntrlProf.modifyTCPmaximumBalance(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"),
					c2cMap.getC2CMap("channeltcpID"), _masterVO.getProperty("MaximumBalance"),
					_masterVO.getProperty("AlertingCount"), productName);
		}
		Log.endTestCase(methodName);
	}

	// 7. To verify that C2C transfer is not successful if the quantity to be
	// transferred is not a multiple of value defined in the receiver's
	// Commission profile.
	@TestManager(TestKey = "PRETUPS-960") @Test 
	public void gC2CTransfer() throws InterruptedException, ParseException {
		String value = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	    int maxApprovalLevel=0;
	    if(BTSLUtil.isNullString(value)) {
        	maxApprovalLevel=0;
        }
        else
		maxApprovalLevel = Integer.parseInt(value);
		CaseMaster CaseMaster8 = _masterVO.getCaseMasterByID(caseIDs[7]);
		CaseMaster CaseMaster9 = _masterVO.getCaseMasterByID(caseIDs[8]);

		final String methodName="gC2CTransfer";Log.startTestCase(methodName);
		for (int productCount = 0; productCount <= data.length; productCount++) {
			String quantity = null;
			String balance;
			String multiple = null;
			if (productCount <= (data.length - 1)) {
				productCode = data[productCount][0].toString();
				productName = data[productCount][1].toString();
				shortName = data[productCount][2].toString();
				productType = data[productCount][3].toString();
				multiple = "1";
				currentNode = test.createNode(MessageFormat.format(CaseMaster8.getExtentCase(), productName));
				currentNode.assignCategory(assignCategory);
				ExtentI.Markup(ExtentColor.TEAL, "Modify multiple of value in Commission profile");
				long time2 = commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"),
						productName, multiple);
				balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
				parser.convertStringToLong(balance).changeDenomation();
				double usrBalance = (long) (parser.getValue() * 0.2) + 0.25;
				quantity = String.valueOf(usrBalance);

				Thread.sleep(time2);
				String[] msgParameter11 = new String[] { shortName, multiple };
				c2cResultMap = performC2CTransaction(shortName, quantity, c2cMap.getC2CMap("fromCategory"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
						multipleofmsg, msgParameter11);
				
				/*if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	        } */
				
				ExtentI.Markup(ExtentColor.TEAL, "Reverting the multiple of value of commission profile");
				long time21 = commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"),
						productName, _masterVO.getProperty("MultipleOf"));
				Thread.sleep(time21);
			}

			else if (data.length > 1 && productCount > (data.length - 1)) {
				currentNode = test.createNode(CaseMaster9.getExtentCase());
				currentNode.assignCategory(assignCategory);

				String[] productType1 = new String[data.length];
				String[] quantity1 = new String[data.length];
				String expectedMsg = null;

				StringBuilder bld = new StringBuilder();
				for (int productCount1 = 0, p = 0; productCount1 < data.length; productCount1++) {
					productCode = data[productCount1][0].toString();
					productName = data[productCount1][1].toString();
					shortName = data[productCount1][2].toString();

					multiple = "1";
					balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
					parser.convertStringToLong(balance).changeDenomation();
					double usrBalance = (long) (parser.getValue() * 0.2) + 0.25;
					quantity1[p] = String.valueOf(usrBalance);
					productType1[p] = shortName;
					expectedMsg = MessagesDAO.prepareMessageByKey(multipleofmsg, shortName, multiple);
					bld.append(expectedMsg);
					expMessage = bld.toString();
					ExtentI.Markup(ExtentColor.TEAL,
							"Modifying the multiple of value in commission profile for : " + productName);
					long time2 = commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"),
							c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"),
							c2cMap.getC2CMap("toCommProfile"), productName, multiple);
					Thread.sleep(time2);
					p++;
				}

				c2cResultMap = performC2CTransaction1(quantity1, productType1, c2cMap.getC2CMap("fromCategory"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
						expMessage);
				
				/*if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	        }*/

				for (int productCount1 = 0; productCount1 < data.length; productCount1++) {
					productName = data[productCount1][1].toString();
					ExtentI.Markup(ExtentColor.TEAL,
							"Reverting the multiple of value in commission profile for : " + productName);
					long time2 = commissionProfile.modifyCommissionProfileMultipleOf(c2cMap.getC2CMap("toDomain"),
							c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"),
							c2cMap.getC2CMap("toCommProfile"), productName, _masterVO.getProperty("MultipleOf"));
					Thread.sleep(time2);
				}
			}
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	/*
	 * 8.To verify that C2C transfer will not be successful if requested amount
	 * is not in between minimum and maximum quantity allowed in the receiver's
	 * commission profile. 9.To verify that C2C transfer will not be successful
	 * if receiver's Commission profile slab is not defined for the requested
	 * quantity.
	 */
	@TestManager(TestKey = "PRETUPS-962") @Test 
	public void hC2CTransfer() throws InterruptedException, ParseException {
		String value2 = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	    int maxApprovalLevel=0;
	    if(BTSLUtil.isNullString(value2)) {
        	maxApprovalLevel=0;
        }
        else
		maxApprovalLevel = Integer.parseInt(value2);
		CaseMaster CaseMaster10 = _masterVO.getCaseMasterByID(caseIDs[9]);
		CaseMaster CaseMaster11 = _masterVO.getCaseMasterByID(caseIDs[10]);
		CaseMaster CaseMaster12 = _masterVO.getCaseMasterByID(caseIDs[11]);
		CaseMaster CaseMaster13 = _masterVO.getCaseMasterByID(caseIDs[12]);
		
		final String methodName="hC2CTransfer";Log.startTestCase(methodName);
		for (int productCount = 0; productCount <= data.length; productCount++) {
			String quantity = null;
			if (productCount <= (data.length - 1)) {
				productCode = data[productCount][0].toString();
				productName = data[productCount][1].toString();
				shortName = data[productCount][2].toString();
				productType = data[productCount][3].toString();

				currentNode = test.createNode(MessageFormat.format(CaseMaster10.getExtentCase(),productName));
				currentNode.assignCategory(assignCategory);

				String[] value = new String[] { "50", "100" };
				ExtentI.Markup(ExtentColor.TEAL, "Modifying commission profile slab for : " + productName);
				long time2 = commissionProfile.modifyMinTrfCommissionSlabfromRange(value, c2cMap.getC2CMap("toDomain"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"),
						productName);
				quantity = "49";

				Thread.sleep(time2);
				String[] msgParameter11 = new String[] { shortName };
				c2cResultMap = performC2CTransaction(shortName, quantity, c2cMap.getC2CMap("fromCategory"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
						minmaxQtyMsg, msgParameter11);

			/*	if(BTSLUtil.isNullString(value2)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	        } */
				
				currentNode = test.createNode(MessageFormat.format(CaseMaster11.getExtentCase(), productName));
				currentNode.assignCategory(assignCategory);
				quantity = "51";
				msgParameter11 = new String[] { productName, quantity };
				c2cResultMap = performC2CTransaction(shortName, quantity, c2cMap.getC2CMap("fromCategory"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
						commmSlabMsg, msgParameter11);

			/*	if(BTSLUtil.isNullString(value2)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	        } */
				
				value = new String[] { _masterVO.getProperty("MintransferValue"), "1" };
				ExtentI.Markup(ExtentColor.TEAL, "Revert the modified commission profile slab for : " + productName);
				long time21 = commissionProfile.modifyMinTrfCommissionSlabfromRange(value, c2cMap.getC2CMap("toDomain"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"), c2cMap.getC2CMap("toCommProfile"),
						productName);
				Thread.sleep(time21);
			}

			else if (data.length > 1 && productCount > (data.length - 1)) {
				currentNode = test.createNode(CaseMaster12.getExtentCase());
				currentNode.assignCategory(assignCategory);

				String[] productType1 = new String[data.length];
				String[] quantity1 = new String[data.length], quantity2 = new String[data.length];
				String expectedMsg, expectedMsg1 = null;
				String[] value;
				StringBuilder bld = new StringBuilder();
				StringBuilder bld1 = new StringBuilder();
				for (int productCount1 = 0, p = 0; productCount1 < data.length; productCount1++) {
					productCode = data[productCount1][0].toString();
					productName = data[productCount1][1].toString();
					shortName = data[productCount1][2].toString();

					quantity1[p] = "49";
					productType1[p] = shortName;
					expectedMsg = MessagesDAO.prepareMessageByKey(minmaxQtyMsg, shortName);
					bld.append(expectedMsg).toString();
					expMessage = bld.toString();

					quantity2[p] = "51";
					expectedMsg1 = MessagesDAO.prepareMessageByKey(commmSlabMsg, productName, quantity2[p]);
					bld1.append(expectedMsg1).toString();
					expMessage1 = bld1.toString();

					value = new String[] { "50", "100" };
					ExtentI.Markup(ExtentColor.TEAL, "Modifying commission profile slab for : " + productName);
					long time2 = commissionProfile.modifyMinTrfCommissionSlabfromRange(value,
							c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"),
							c2cMap.getC2CMap("toCommProfile"), productName);
					Thread.sleep(time2);
					p++;
				}

				c2cResultMap = performC2CTransaction1(quantity1, productType1, c2cMap.getC2CMap("fromCategory"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
						expMessage);

				/*if(BTSLUtil.isNullString(value2)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	        } */
				
				currentNode = test.createNode(CaseMaster13.getExtentCase());
				currentNode.assignCategory(assignCategory);

				c2cResultMap = performC2CTransaction1(quantity2, productType1, c2cMap.getC2CMap("fromCategory"),
						c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
						expMessage1);
				
			/*	if(BTSLUtil.isNullString(value2)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
	        		}
	        } */

				for (int productCount1 = 0; productCount1 < data.length; productCount1++) {
					productName = data[productCount1][1].toString();

					value = new String[] { _masterVO.getProperty("MintransferValue"), "1" };
					ExtentI.Markup(ExtentColor.TEAL,
							"Revert the modified commission profile slab for : " + productName);
					long time2 = commissionProfile.modifyMinTrfCommissionSlabfromRange(value,
							c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toGrade"),
							c2cMap.getC2CMap("toCommProfile"), productName);
					Thread.sleep(time2);
				}
			}
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	// 10. To verify that for C2C, if sender's balance crosses the minimum
	// allowed balance (from transfer control profile) then transaction will be
	// failed.
	@TestManager(TestKey = "PRETUPS-973") @Test 
	public void iC2CTransfer() throws InterruptedException {
		String value2 = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	    int maxApprovalLevel=0;
	    if(BTSLUtil.isNullString(value2)) {
        	maxApprovalLevel=0;
        }
        else
		maxApprovalLevel = Integer.parseInt(value2);
		CaseMaster CaseMaster14 = _masterVO.getCaseMasterByID(caseIDs[13]);
		


		final String methodName="iC2CTransfer";Log.startTestCase(methodName);
		for (int productCount1 = 0; productCount1 < data.length; productCount1++) {
			productCode = data[productCount1][0].toString();
			productName = data[productCount1][1].toString();
			shortName = data[productCount1][2].toString();
			currentNode = test.createNode(MessageFormat.format(CaseMaster14.getExtentCase(), productName));
			currentNode.assignCategory(assignCategory);
			Log.info("No. of products available : " + data.length + " | " + productName);

			trfCntrlProf.modifyTCPminimumBalance(c2cMap.getC2CMap("fromDomain"), c2cMap.getC2CMap("fromCategory"),
					c2cMap.getC2CMap("fromTCPID"), "100", "100", productName);

			String balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
			parser.convertStringToLong(balance).changeDenomation();
			long usrBalance = (long) (parser.getValue()) - 100 + 2;
			BigDecimal userbalance = new BigDecimal(usrBalance).setScale(2);
			String[] msgParameter11 = new String[] { new BigDecimal(100).setScale(2).toString(), productName,
					userbalance.toString(),productCode };
			c2cResultMap = performC2CTransaction(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),
					c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),maxApprovalLevel,
					minresidualBalMsg, msgParameter11);

			/*if(BTSLUtil.isNullString(value2)) {
            	Log.info("C2C Approval level is not Applicable");
        		}
            else {
            	if(maxApprovalLevel == 0)
        		{
            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
        		}
            	else if(maxApprovalLevel == 2)
        		{
            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
        		}
            	else if(maxApprovalLevel == 3)
        		{
            		c2cResultMap=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
            		c2cResultMap=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
            		c2cResultMap=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cResultMap.get("TransactionID"));
        		}
        } */
			
			trfCntrlProf.modifyTCPminimumBalance(c2cMap.getC2CMap("fromDomain"), c2cMap.getC2CMap("fromCategory"),
					c2cMap.getC2CMap("fromTCPID"), _masterVO.getProperty("MinimumBalance"),
					_masterVO.getProperty("AllowedMaxPercentage"), productName);
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	
	//  To verify that C2C transfer is successful with Commission Profile Calculations
	//@TestManager(TestKey = "PRETUPS-981") @Test 
	public void jC2CTransfer() throws InterruptedException, SQLException, ParseException {
		final String methodName="jC2CTransfer";Log.startTestCase(methodName);
		String value2 = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	    int maxApprovalLevel=0;
		CaseMaster CaseMaster15  = _masterVO.getCaseMasterByID(caseIDs[14]);
		
		Map<String, String> c2cMapCRDR = null;
		
		for (int productCount = 0; productCount <= data.length; productCount++) {
			if (productCount <= (data.length - 1)) {
				
				businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), c2cMap.getC2CMap("fromMSISDN"), c2cMap.getC2CMap("toMSISDN"));
				HashMap<String, String> initiatedQty = new HashMap<String, String>();
				
				TransactionVO TransactionVO = businessController.preparePreTransactionVO();
				TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
				productCode = data[productCount][0].toString();
				productName = data[productCount][1].toString();
				shortName = data[productCount][2].toString();
				productType = data[productCount][3].toString();
				currentNode = test.createNode(MessageFormat.format(CaseMaster15.getExtentCase(), shortName));
				currentNode.assignCategory(assignCategory);
				String balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap.getC2CMap("fromLoginID"));
				parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue() * 0.2);
				String quantity = String.valueOf(usrBalance);
				initiatedQty.put(productCode, quantity);
			
				c2cMapCRDR=new C2CTransfer(driver).channel2channelTransfer(shortName, String.valueOf(usrBalance), c2cMap.getC2CMap("fromCategory"),	c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),"false");
				
				/*if(BTSLUtil.isNullString(value2)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cMapCRDR=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMapCRDR.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cMapCRDR=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMapCRDR.get("TransactionID"));
	            		c2cMapCRDR=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMapCRDR.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cMapCRDR=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMapCRDR.get("TransactionID"));
	            		c2cMapCRDR=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMapCRDR.get("TransactionID"));
	            		c2cMapCRDR=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMapCRDR.get("TransactionID"));
	        		}
	        } */
				
				String actualMessage = c2cMapCRDR.get("actualMessage");
				String expectedMessage = c2cMapCRDR.get("expectedMessage");
				Assertion.assertEquals(actualMessage, expectedMessage);
		//new BusinessValidatorvalidateCRDR(c2cMap.getC2CMap("toMSISDN"), _masterVO.getProperty("C2CTransferCode"), productCode, String.valueOf(usrBalance), c2cMapCRDR.get("TransactionID"));;
		
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
			
			}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}	


	}	


	@TestManager(TestKey = "PRETUPS-986") 
	@Test 
	public void kC2CTransfer() throws InterruptedException{
		final String methodName="kC2CTransfer";Log.startTestCase(methodName);
		boolean bar=false;
		CaseMaster CaseMaster16 = _masterVO.getCaseMasterByID(caseIDs[15]);
		
			
	
		C2CTransferDetailsPage C2CTransferDetailsPage = new C2CTransferDetailsPage(driver);
		C2CDetailsPage C2CDetailsPage = new C2CDetailsPage(driver);
		ChannelUserHomePage CHhomePage = new ChannelUserHomePage(driver);
		Login login = new Login();
		ChannelUserSubLinkPages chnlSubLink= new ChannelUserSubLinkPages(driver);
		RandomGeneration randomNum = new RandomGeneration();
		
				currentNode = test.createNode(CaseMaster16.getExtentCase());
				currentNode.assignCategory(assignCategory);
				int pinblockcount = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_MAX_PIN_BLK_CONT));
				int pincount =1; String invalidPIN = randomNum.randomNumberWithoutZero(5);
				String expected = MessagesDAO.getLabelByKey("channeltransfer.userProductsConfirm.msg.smspininvalid");
				String actual;
				
				while(invalidPIN.equals(c2cMap.getC2CMap("fromPIN")))
				{invalidPIN=randomNum.randomNumberWithoutZero(4);}
				
				login.UserLogin(driver, "ChannelUser", c2cMap.getC2CMap("fromCategory"));
				CHhomePage.clickC2CTransfer();
				chnlSubLink.clickC2CTransferLink();
				C2CTransferDetailsPage.enterMobileNo(c2cMap.getC2CMap("toMSISDN"));
				C2CTransferDetailsPage.clickSubmit();
				C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
				C2CTransferDetailsPage.enterQuantityforC2C();
				C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+c2cMap.getC2CMap("toCategory"));
				if (isPaymentMethodMandatory()) {
					C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
					C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
					C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
				}
				while(pincount<=(pinblockcount-1)){
				Log.info("PIN count: "+pincount);
				C2CDetailsPage.enterSmsPin(invalidPIN);
				
				C2CDetailsPage.clickSubmit();pincount++;
				actual = new AddChannelUserDetailsPage(driver).getActualMessage();
				
				Assertion.assertEquals(actual, expected);
				}
				
				C2CDetailsPage.enterSmsPin(invalidPIN);
				C2CDetailsPage.clickSubmit();
				actual = new AddChannelUserDetailsPage(driver).getActualMessage();
				expected=MessagesDAO.getLabelByKey("channeluser.unblockpin.viewcommondetails.label.pinblocked");
				Validator.messageCompare(actual, expected);
				
				C2CDetailsPage.enterSmsPin(c2cMap.getC2CMap("fromPIN"));
				C2CDetailsPage.clickSubmit();
				actual = new AddChannelUserDetailsPage(driver).getActualMessage();
				expected=MessagesDAO.getLabelByKey("c2stranfer.repetitive.incorrect.pin.barred");
				Assertion.assertEquals(actual, expected);
	
				bar=true;
				
				if(bar){
					DBHandler.AccessHandler.deletionfrombarredMSISDN(c2cMap.getC2CMap("fromMSISDN"));
				}
				
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}		
	
	private boolean isPaymentMethodMandatory() {
		Log.info("Entered :: isPaymentMethodMandatory()");
		int isPaymentDetailsMandate =0;
		String value = DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATE_C2C");
	//	int isPaymentDetailsMandate = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATE_C2C"));
		if(BTSLUtil.isNullString(value)) {
			 isPaymentDetailsMandate=-1;
        }
        else
        	 isPaymentDetailsMandate = Integer.parseInt(value);
		if (isPaymentDetailsMandate != -1 && isPaymentDetailsMandate == 0) {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=true");
			return true;
		} else {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=false");
			return false;
		}
	}

	@TestManager(TestKey = "PRETUPS-989") @Test 
	public void lC2CTransfer() throws InterruptedException{
		final String methodName="lC2CTransfer";Log.startTestCase(methodName);
		
		String fromCategory = c2cMap.getC2CMap("fromCategory");
		String toCategory = c2cMap.getC2CMap("toCategory");
		String toMSISDN = c2cMap.getC2CMap("toMSISDN");
		String tocommProfileName = c2cMap.getC2CMap("toCommProfile");
		String fromPIN = c2cMap.getC2CMap("fromPIN");
		String toDomain = c2cMap.getC2CMap("toDomain");
		String grade = c2cMap.getC2CMap("toGrade");
		
		CommissionProfile commProfile = new CommissionProfile(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID(caseIDs[16]).getExtentCase(), tocommProfileName,toCategory));
		currentNode.assignCategory(assignCategory);	
		
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Commission Profile:"+tocommProfileName);
		Object[] idefault = commProfile.suspendcommissionProfileStatus(toDomain, toCategory, grade, tocommProfileName);

		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
		String actual = null;
		try{c2cTransfer.channel2channelTransfer(fromCategory, toCategory,toMSISDN, fromPIN);}
		catch(Exception e){Log.info("Transaction not successful.");
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();}
		
		String expected=MessagesDAO.prepareMessageByKey("commissionprofile.notactive.msg", toMSISDN,idefault[1].toString());
		Assertion.assertEquals(actual, expected);
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Commission Profile:"+tocommProfileName);
		
		commProfile.resumecommissionProfileStatus(toDomain, toCategory, grade, tocommProfileName,(boolean)idefault[0]);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
	}
	
	@TestManager(TestKey = "PRETUPS-990") 
	@Test 
	public void mC2CTransfer() throws InterruptedException{
		final String methodName="mC2CTransfer";Log.startTestCase(methodName);

		String fromCategory = c2cMap.getC2CMap("fromCategory");
		String toCategory = c2cMap.getC2CMap("toCategory");
		String toMSISDN = c2cMap.getC2CMap("toMSISDN");
		String fromcommProfileName = c2cMap.getC2CMap("fromCommProfile");
		String fromPIN = c2cMap.getC2CMap("fromPIN");
		String fromDomain = c2cMap.getC2CMap("fromDomain");
		String grade = c2cMap.getC2CMap("fromGrade");
		String fromMSISDN = c2cMap.getC2CMap("fromMSISDN");
		
		CommissionProfile commProfile = new CommissionProfile(driver);
		
	
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID(caseIDs[17]).getExtentCase(), fromcommProfileName,fromCategory));
		currentNode.assignCategory(assignCategory);
		
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Commission Profile:"+fromcommProfileName);
		Object[] idefault = commProfile.suspendcommissionProfileStatus(fromDomain, fromCategory, grade, fromcommProfileName);

		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
		String actual=null;
		try{c2cTransfer.channel2channelTransfer(fromCategory, toCategory,toMSISDN, fromPIN);}
		catch(Exception e){
			Log.info("Transaction not successful.");
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		
		String expected=MessagesDAO.prepareMessageByKey("commissionprofile.notactive.msg", fromMSISDN,idefault[1].toString());
		Assertion.assertEquals(actual, expected);
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Commission Profile:"+fromcommProfileName);
		commProfile.resumecommissionProfileStatus(fromDomain, fromCategory, grade, fromcommProfileName,(boolean)idefault[0]);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
	}
	

	// function just to optimize lines of code
	public HashMap<String,String> performC2CTransaction(String shortName, String quantity, String fromCategory, String toCategory,
			String toMSISDN, String fromPIN, int maxApprovalLevel, String msgCode, String... msgParameter) {
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		HashMap<String,String> resultMap = new HashMap<String,String>();
		String actualMessage = "";
		String expectedMessage = "";
		try {
			resultMap = new C2CTransfer(driver).channel2channelTransfer(shortName, quantity, fromCategory, toCategory, toMSISDN,
					fromPIN,"true");
			if(maxApprovalLevel!=0) {
				msgParameter = new String[] { resultMap.get("TransactionID") };
				actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
				 expectedMessage = MessagesDAO.prepareMessageByKey(msgCode, msgParameter);
				Log.info(" Message fetched from WEB as : " + actualMessage);
			}
			else {
				actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
				 expectedMessage = MessagesDAO.prepareMessageByKey(msgCode, msgParameter);
				Log.info(" Message fetched from WEB as : " + actualMessage);
			}
			
			Assertion.assertEquals(actualMessage, expectedMessage);
		} catch (Exception e) {
			actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
			expectedMessage = MessagesDAO.prepareMessageByKey(msgCode, msgParameter);
			if(expectedMessage==null){
				expectedMessage = MessagesDAO.prepareMessageByKey("error.transfer.minbalance.reached",msgParameter[3]);
			}
			Log.info(" Message fetched from WEB as : " + actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		Assertion.completeAssertions();
		return resultMap;
	}

	public HashMap<String,String> performC2CTransaction1(String[] quantity1, String[] productType1, String fromCategory,
			String toCategory, String toMSISDN, String fromPIN,int maxApprovalLevel, String expMessage) {
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		HashMap<String,String> resultMap = new HashMap<String,String>();
		String actualMessage = "";
		String expectedMessage = "";
		try {
			resultMap = new C2CTransfer(driver).channel2channelTransfer(quantity1, productType1, fromCategory, toCategory, toMSISDN,
					fromPIN);
			if(maxApprovalLevel!=0) {
				String[] msgParameter = new String[] { resultMap.get("TransactionID") };
				actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
				 expectedMessage = MessagesDAO.prepareMessageByKey(expMessage, msgParameter);
				Log.info(" Message fetched from WEB as : " + actualMessage);
			}
			else {
				expectedMessage = expMessage;
				Log.info(" Message fetched from WEB as : " + actualMessage);
			}
			Assertion.assertEquals(actualMessage, expectedMessage);
		} catch (Exception e) {
			 actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage().replaceAll("\\r|\\n", "");
			expectedMessage = expMessage;
			Log.info(" Message fetched from WEB as : " + actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		Assertion.completeAssertions();
		return resultMap;
	}

	public HashMap<String,String> performC2CTransaction2(String fromCategory, String toCategory, String toMSISDN, String fromPIN,
			String requiredmsg, int maxApprovalLevel,boolean prepareMsg, String... msgParameter) {
		ExtentI.Markup(ExtentColor.TEAL, extentc2clog);
		HashMap<String,String> resultMap = new HashMap<String,String>();
		try {
			resultMap = new C2CTransfer(driver).channel2channelTransfer(fromCategory, toCategory, toMSISDN, fromPIN);
			String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
			String expectedMessage = null;
			if (!prepareMsg)
				expectedMessage = MessagesDAO.getLabelByKey(requiredmsg);
			else {
				if(maxApprovalLevel!=0) {
					msgParameter = new String[] { resultMap.get("TransactionID") };
					actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
					 expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);
					Log.info(" Message fetched from WEB as : " + actualMessage);
				}
				else {
					
					 expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);
					Log.info(" Message fetched from WEB as : " + actualMessage);
				}
				
				
				
				
				expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);
			}
			Log.info(" Message fetched from WEB as : " + actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		} catch (Exception e) {
			String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
			String expectedMessage = null;
			if (!prepareMsg)
				expectedMessage = MessagesDAO.getLabelByKey(requiredmsg);
			else
				expectedMessage = MessagesDAO.prepareMessageByKey(requiredmsg, msgParameter);
			Log.info(" Message fetched from WEB as : " + actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		Assertion.completeAssertions();
		return resultMap;
	}
	

}
