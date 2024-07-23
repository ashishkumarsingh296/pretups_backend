
package com.testscripts.sit;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.ChannelUser;
import com.Features.TransactionReverseC2C;
import com.Features.mapclasses.Channel2ChannelMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_TRANSACTION_REVERSE_C2C)
public class SIT_TransactionReverseC2C extends BaseTest { 

	HashMap<String, String> c2cMap=new HashMap<String, String>();
	HashMap<String, String> channelMap=new HashMap<>();

	String txnID;
	String newToMSISDN = null;
	String RecieverUserName=null;
	String productCode = null;
	String expMessage = null;
	String expMessage1 = null;
	String productType = null;
	String productName = null;
	String shortName = null;
	static String networkCode;
	static String type;
	static Object[][] data;
	String[] msgParameter1;
	ChannelUser chnlUsr;
	_parser parser;
	Channel2ChannelMap c2cMap1;
	BusinessValidator BusinessValidator;
	C2CTransfer c2cTransfer;
	
	@BeforeMethod
	public void dataV() {
		c2cTransfer = new C2CTransfer(driver);

		BusinessValidator = new BusinessValidator();
		chnlUsr = new ChannelUser(driver);
		parser = new _parser();
		c2cMap1 = new Channel2ChannelMap();
		

		networkCode = _masterVO.getMasterValue("Network Code");
		type = "CHANNEL";
		data = DBHandler.AccessHandler.getProductDetails(networkCode, c2cMap1.getC2CMap("domainCode"),
				c2cMap1.getC2CMap("fromCategoryCode"), c2cMap1.getC2CMap("toCategoryCode"), type);
		
		
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1167") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_C2CTransactionReverseWithRecieverMSISDN(String FromCategory, String ToCategory, String toMSISDN, String FromPIN,String Domain, String ParentCategory, String geoType,String catCode, int RowNum) throws InterruptedException, IOException {
		final String methodName = "Test_C2C_Transaction_Reverse";
		Log.startTestCase(methodName);
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		ChannelUser channelUser= new ChannelUser(driver);
		Log.startTestCase(this.getClass().getName());
		String SenderUserName= null;
		String actual = null;
		String productCode = null;

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITC2CTXNREV1");

		//currentNode=test.createNode("To verify that C2C Transaction Reverse is successful from "+FromCategory+" to "+ToCategory+" .");
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),FromCategory, ToCategory)) ;
		currentNode.assignCategory("SIT");

		/*
		 * To initiate C2C Transfer
		 */
		 String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);

		if(FromCategory.equals(ToCategory))
		{   channelMap=channelUser.channelUserInitiate(RowNum, Domain, ParentCategory, ToCategory, geoType);
		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
		if(APPLEVEL.equals("2"))
		{channelUser.approveLevel1_ChannelUser();
		channelUser.approveLevel2_ChannelUser();
		}
		else if(APPLEVEL.equals("1")){
			channelUser.approveLevel1_ChannelUser();	
		}else{
			Log.info("Approval not required.");	
		}

		newToMSISDN =channelMap.get("MSISDN");

		Log.info("Newly created User Name:" +channelMap.get("UserName") );

		RecieverUserName = channelMap.get("uName");
		
				}
		if(FromCategory.equals(ToCategory)){
			Log.info("To MSISDN from Sheet:" +toMSISDN);
			Log.info("New created MSISDN = "+newToMSISDN);
			toMSISDN= newToMSISDN;
			Log.info("Now toMSISDN is: " +toMSISDN);
		}
			c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);
			Log.info("The message on screen is : " +c2cMap.get("actualMessage"));
			txnID = c2cMap.get("TransactionID");	
			 if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	        } 
		
		//C2C Transaction Reverse
		ExtentI.Markup(ExtentColor.TEAL, "Initiate C2C Txn Reversal ");
		
		 actual = TransactionReverseC2C.initiateC2CTxnReverse(txnID,toMSISDN);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
				break;
		}
		System.out.println(i);
		SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		Log.info("From User as in sheet : " +SenderUserName);
		System.out.println(SenderUserName);
		
		if(!FromCategory.equals(ToCategory)){
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))
			break;
		}
			RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
			Log.info("To User Name from sheet:" +RecieverUserName);
		}
		
		
		
		
		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1168") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_C2CTransactionReverseWithSenderMSISDN(String FromCategory, String ToCategory, String toMSISDN, String FromPIN,String Domain, String ParentCategory, String geoType,String catCode, int RowNum) throws InterruptedException, IOException {
		final String methodName = "Test_C2C_Transaction_Reverse";
		Log.startTestCase(methodName);
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		Log.startTestCase(this.getClass().getName());
		String SenderUserName= null;
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITC2CTXNREV2");

		//currentNode=test.createNode("To verify that C2C Transaction Reverse is successful with Sender MSISDN of " +FromCategory);
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),FromCategory));
		currentNode.assignCategory("SIT");

		/*
		 * To initiate C2C Transfer
		 */
		 String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		Log.info("C2C transfer from "+FromCategory+" to "+ToCategory+" .");

		if(FromCategory.equals(ToCategory)){
			Log.info("To MSISDN from Sheet:" +toMSISDN);
			Log.info("New created MSISDN = "+newToMSISDN);
			toMSISDN= newToMSISDN;
			Log.info("Now toMSISDN is: " +toMSISDN);
		}
			c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);
			Log.info("The message on screen is : " +c2cMap.get("actualMessage"));
			txnID = c2cMap.get("TransactionID");	
			 if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	        } 
				//Transaction Reverse C2C 
		ExtentI.Markup(ExtentColor.TEAL, "Initiate C2C Txn Reversal ");
		String actual = TransactionReverseC2C.initiateC2CTxnReverseWithSenderMSISDN(FromCategory, txnID);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			System.out.println(FromCategory);
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
				break;
		}
		System.out.println(i);
		SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		System.out.println(SenderUserName);
		if(!FromCategory.equals(ToCategory)){
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))
			break;
		}

			RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
			System.out.println("Reciever User Name is:" +RecieverUserName);
		}
		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1169") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_C2CTransactionReverseWithSenderLoginID(String FromCategory, String ToCategory,String toMSISDN, String FromPIN,String Domain, String ParentCategory, String geoType,String catCode,int RowNum) throws InterruptedException, IOException {
		final String methodName = "Test_C2C_Transaction_Reverse";
		Log.startTestCase(methodName);
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		Log.startTestCase(this.getClass().getName());
		String SenderUserName= null;

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITC2CTXNREV3");

		//currentNode=test.createNode("To verify that C2C Transaction Reverse is successful with Sender LoginID from "+FromCategory+" to "+ToCategory+" .");
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),FromCategory,ToCategory));
		currentNode.assignCategory("SIT");
		/*
		 * To initiate C2C Transfer
		 */
		Log.info("C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		 String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		if(FromCategory.equals(ToCategory)){
			Log.info("To MSISDN from Sheet:" +toMSISDN);
			Log.info("New created MSISDN = "+newToMSISDN);
			toMSISDN= newToMSISDN;
			Log.info("Now toMSISDN is: " +toMSISDN);
		}
			c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);
			Log.info("The message on screen is : " +c2cMap.get("actualMessage"));
			txnID = c2cMap.get("TransactionID");
			 if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	        } 
		
		//C2C Transaction Reverse
		ExtentI.Markup(ExtentColor.TEAL, "Initiate C2C Txn Reversal ");
		String actual = TransactionReverseC2C.initiateC2CTxnReverseWithSenderLoginID(FromCategory, txnID);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
				break;
		}
		SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		System.out.println("Sender user name as found in sheet in row number" +i+ "is:" +SenderUserName);
		
		if(!FromCategory.equals(ToCategory)){
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))
			break;
		}
			RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
			System.out.println(RecieverUserName);
		}
		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);
	
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1170") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_C2CTransactionReverseWithDomainCode(String FromCategory, String ToCategory, String toMSISDN,String FromPIN,String Domain, String ParentCategory, String geoType,String catCode, int RowNum) throws InterruptedException, IOException {
		final String methodName = "Test_C2C_Transaction_Reverse";
		Log.startTestCase(methodName);
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		Log.startTestCase(this.getClass().getName());
		String SenderUserName= null;
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITC2CTXNREV4");

		//currentNode=test.createNode("To verify that C2C Transaction Reverse is successful with Domain Code: " +Domain);
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),Domain));
		currentNode.assignCategory("SIT");
		
		Log.info("C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		 String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		
		if(FromCategory.equals(ToCategory)){
			Log.info("To MSISDN from Sheet:" +toMSISDN);
			Log.info("New created MSISDN = "+newToMSISDN);
			toMSISDN= newToMSISDN;
			Log.info("Now toMSISDN is: " +toMSISDN);
		}
			c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);
			Log.info("The message on screen is : " +c2cMap.get("actualMessage"));
			txnID = c2cMap.get("TransactionID");
			 if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	            		c2cMap=c2cTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"),maxApprovalLevel);
	        		}
	        } 

		//C2C Transaction Reversal
		ExtentI.Markup(ExtentColor.TEAL, "Initiate C2C Txn Reversal ");

		String actual = TransactionReverseC2C.initiateC2CTxnReverseWithDomainCode(FromCategory,Domain,txnID);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
				break;
		}
		System.out.println(i);
		SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		System.out.println(SenderUserName);
		
		if(!FromCategory.equals(ToCategory)){
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))
			break;
		}
			RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
			Log.info("The Reciever UserName in sheet:" +RecieverUserName);
		}

		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1171") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_C2CTransactionReverseWithBalanceCalculation(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType,String catCode, int RowNum) throws InterruptedException, IOException, ParseException, SQLException {
		final String methodName = "Test_C2C_Transaction_Reverse";
		Log.startTestCase(methodName);
		C2CTransfer c2cTransfer = new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		c2cMap1 = new Channel2ChannelMap();
		Log.startTestCase(this.getClass().getName());
		String SenderUserName = null;

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITC2CTXNREV5");

		 String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
	        int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);

		Map<String, String> c2cMapCRDR = null;

		//currentNode=test.createNode("To verify that C2C Transaction Reverse is successful with Balance Calculation: " +Domain);
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), Domain));

		currentNode.assignCategory("SIT");

		Log.info("C2C Transfer from " + FromCategory + " to " + ToCategory + " .");

		if (FromCategory.equals(ToCategory)) {
			Log.info("To MSISDN from Sheet:" + toMSISDN);
			Log.info("New created MSISDN = " + newToMSISDN);
			toMSISDN = newToMSISDN;
			Log.info("Now toMSISDN is: " + toMSISDN);
		}


		networkCode = _masterVO.getMasterValue("Network Code");
		String type = "CHANNEL";
		data = DBHandler.AccessHandler.getProductDetails(networkCode, c2cMap1.getC2CMap("domainCode"),
				c2cMap1.getC2CMap("fromCategoryCode"), c2cMap1.getC2CMap("toCategoryCode"), type);
		for (int productCount = 0; productCount <= data.length; productCount++) {
			if (productCount <= (data.length - 1)) {

				businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), c2cMap1.getC2CMap("toMSISDN"), c2cMap1.getC2CMap("fromMSISDN"));
				HashMap<String, String> initiatedQty = new HashMap<String, String>();

				TransactionVO TransactionVO = businessController.preparePreTransactionVO();
				TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);

				productCode = data[productCount][0].toString();
				productName = data[productCount][1].toString();
				shortName = data[productCount][2].toString();
				productType = data[productCount][3].toString();

				currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITC2CTXNREV6").getExtentCase(), Domain));
				currentNode.assignCategory("SIT");

				String balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap1.getC2CMap("fromLoginID"));
				parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue() * 0.2);
				String quantity = String.valueOf(usrBalance);
				initiatedQty.put(productCode, quantity);

				c2cMapCRDR = new C2CTransfer(driver).channel2channelTransfer(shortName, String.valueOf(usrBalance), c2cMap1.getC2CMap("fromCategory"), c2cMap1.getC2CMap("toCategory"), c2cMap1.getC2CMap("toMSISDN"), c2cMap1.getC2CMap("fromPIN"), "false");
				Log.info("The message on screen is : " + c2cMapCRDR.get("actualMessage"));
				txnID = c2cMapCRDR.get("TransactionID");
				 if(BTSLUtil.isNullString(value)) {
		            	Log.info("C2C Approval level is not Applicable");
		        		}
		            else {
		            	if(maxApprovalLevel == 0)
		        		{
		            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
		        		}
		            	if(maxApprovalLevel == 1)
		        		{
		            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		        		}
		            	else if(maxApprovalLevel == 2)
		        		{
		            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		        		}
		            	else if(maxApprovalLevel == 3)
		        		{
		            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		            		c2cMap=c2cTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		        		}
		            }
				//C2C Transaction Reversal
				ExtentI.Markup(ExtentColor.TEAL, "Initiate C2C Txn Reversal ");

				String actual = TransactionReverseC2C.initiateC2CTxnReverseWithDomainCode(FromCategory, Domain, txnID);

				String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success", null, txnID, c2cMap1.getC2CMap("fromUserName"), c2cMap1.getC2CMap("toUserName"));
				Assertion.assertEquals(actual, expected);

				TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
				BusinessValidator.validateStocks(TransactionVO);

			}

		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1172") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void f_C2CTransactionReverseWithBalanceCalculation(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) throws InterruptedException, IOException, ParseException, SQLException {
		final String methodName = "Test_C2C_Transaction_Reverse";
		Log.startTestCase(methodName);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		c2cMap1 = new Channel2ChannelMap();
		Log.startTestCase(this.getClass().getName());
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITC2CTXNREV7");

		Map<String, String> c2cMapCRDR = null;

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), Domain));
		currentNode.assignCategory("SIT");

		Log.info("C2C Transfer from " + FromCategory + " to " + ToCategory + " .");

		if (FromCategory.equals(ToCategory)) {
			Log.info("To MSISDN from Sheet:" + toMSISDN);
			Log.info("New created MSISDN = " + newToMSISDN);
			toMSISDN = newToMSISDN;
			Log.info("Now toMSISDN is: " + toMSISDN);
		}


		networkCode = _masterVO.getMasterValue(ExcelI.NETWORK_CODE);
		String type = "CHANNEL";
		data = DBHandler.AccessHandler.getProductDetails(networkCode, c2cMap1.getC2CMap("domainCode"),
				c2cMap1.getC2CMap("fromCategoryCode"), c2cMap1.getC2CMap("toCategoryCode"), type);
		for (int productCount = 0; productCount <= data.length; productCount++) {
			if (productCount <= (data.length - 1)) {
				HashMap<String, String> initiatedQty = new HashMap<String, String>();

				productCode = data[productCount][0].toString();
				productName = data[productCount][1].toString();
				shortName = data[productCount][2].toString();
				productType = data[productCount][3].toString();

				String balance = DBHandler.AccessHandler.getUserBalance(productCode, c2cMap1.getC2CMap("fromLoginID"));
				parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue() * 0.2);
				String quantity = String.valueOf(usrBalance);
				initiatedQty.put(productCode, quantity);

				 String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
			        int maxApprovalLevel=0;
			        if(BTSLUtil.isNullString(value)) {
			        	maxApprovalLevel=0;
			        }
			        else
					maxApprovalLevel = Integer.parseInt(value);

				c2cMapCRDR = new C2CTransfer(driver).channel2channelTransfer(shortName, String.valueOf(usrBalance), c2cMap1.getC2CMap("fromCategory"), c2cMap1.getC2CMap("toCategory"), c2cMap1.getC2CMap("toMSISDN"), c2cMap1.getC2CMap("fromPIN"), "false");
				Log.info("The message on screen is : " + c2cMapCRDR.get("actualMessage"));
				txnID = c2cMapCRDR.get("TransactionID");
				 if(BTSLUtil.isNullString(value)) {
		            	Log.info("C2C Approval level is not Applicable");
		        		}
		            else {
		            	if(maxApprovalLevel == 0)
		        		{
		            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
		        		}
		            	if(maxApprovalLevel == 1)
		        		{
		            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		        		}
		            	else if(maxApprovalLevel == 2)
		        		{
		            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		        		}
		            	else if(maxApprovalLevel == 3)
		        		{
		            		c2cMap=c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		            		c2cMap=c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		            		c2cMap=c2cTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMapCRDR.get("TransactionID"),maxApprovalLevel);
		        		}
		            }
				
				//C2C Transaction Reversal
				ExtentI.Markup(ExtentColor.TEAL, "Initiate C2C Txn Reversal ");

				String actual = TransactionReverseC2C.initiateC2CTxnReverseWithDomainCode(FromCategory, Domain, txnID);
				String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success", null, txnID, c2cMap1.getC2CMap("fromUserName"), c2cMap1.getC2CMap("toUserName"));
				Assertion.assertEquals(actual, expected);

				ExtentI.Markup(ExtentColor.TEAL, "Attempting to Initiate C2C Txn Reversal again for same TxnID");
				try{
				actual = TransactionReverseC2C.initiateC2CTxnReverseWithDomainCode(FromCategory, Domain, txnID);}
				catch(Exception e){actual = new AddChannelUserDetailsPage(driver).getActualMessage();}
				expected = MessagesDAO.prepareMessageByKey("c2sreversetrx.c2ctransferlist.error.nodata.alreadyreversed", txnID);
				Assertion.assertEquals(actual, expected);
				
				}

		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed1() {
		String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, "Transfer Rule Sheet");
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which C2C withdraw is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, "Transfer Rule Sheet");
			String services = ExcelUtility.getCellData(0, "SERVICES", i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,"Transfer Rule Sheet");
				alist1.add(ExcelUtility.getCellData(0, "TO_CATEGORY", i));
				alist2.add(ExcelUtility.getCellData(0, "FROM_CATEGORY", i));
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

		/*
		 * Calculate the Count of Users for each category
		 */
		int totalObjectCounter = 0;
		for (int i=0; i<alist1.size(); i++) {
			int categorySizeCounter = 0;
			for (int excelCounter=0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
				if(ExcelUtility.getCellData(0, "CATEGORY_NAME",excelCounter).equals(alist1.get(i))){
					categorySizeCounter++;
				}
			}
			categorySize.add(""+categorySizeCounter);
			totalObjectCounter = totalObjectCounter + categorySizeCounter;
		}

		/*
		 * Counter to count number of users exists in channel users hierarchy sheet 
		 * of Categories for which C2C Withdraw is allowed
		 */

		Object[][] Data = new Object[totalObjectCounter][9];

		for(int j=0, k=0;j<alist1.size();j++){

			ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
			int excelRowSize = ExcelUtility.getRowCount();
			String ChannelUserPIN = null;
			for(int i=1;i<=excelRowSize;i++){
				if(ExcelUtility.getCellData(0, "CATEGORY_NAME",i).equals(alist2.get(j))){
					ChannelUserPIN = ExcelUtility.getCellData(0, "PIN", i);
					break;
				}
			}

			for(int excelCounter=1; excelCounter <=excelRowSize; excelCounter++){
				if(ExcelUtility.getCellData(0, "CATEGORY_NAME",excelCounter).equals(alist1.get(j))){
					Data[k][0] = alist2.get(j);
					Data[k][1] = alist1.get(j);
					Data[k][2] = ExcelUtility.getCellData(0, "MSISDN", excelCounter);
					Data[k][3] = ChannelUserPIN;
					Data[k][4]= ExcelUtility.getCellData(0, "DOMAIN_NAME", excelCounter);
					Data[k][5]= ExcelUtility.getCellData(0,"PARENT_CATEGORY_NAME",excelCounter);
					Data[k][6]= ExcelUtility.getCellData(0,"GRPH_DOMAIN_TYPE",excelCounter);
					Data[k][7]= ExcelUtility.getCellData(0,"CATEGORY_CODE",excelCounter);
					Data[k][8]= excelCounter;
					k++;
				}
			}

		}                       

		Object[][] Data1 = new Object[1][9];
		Data1[0][0]=Data[0][0];
		Data1[0][1]=Data[0][1];
		Data1[0][2]=Data[0][2];
		Data1[0][3]=Data[0][3];
		Data1[0][4]=Data[0][4];
		Data1[0][5]=Data[0][5];
		Data1[0][6]=Data[0][6];
		Data1[0][7]=Data[0][7];
		Data1[0][8]=Data[0][8];
		return Data1;
	}



}
