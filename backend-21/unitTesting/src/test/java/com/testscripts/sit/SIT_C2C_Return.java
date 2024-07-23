package com.testscripts.sit;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CReturn;
import com.Features.CacheUpdate;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.Channel2ChannelMap;
import com.Features.mapclasses.Channel2ChannelReturnMap;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.UserTransferCountsVO;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.CacheController;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_C2C_Return)
public class SIT_C2C_Return  extends BaseTest {

	Channel2ChannelMap c2cMap;
	HashMap<String, String> paraMap;
	ChannelUserMap chnlUsrMap;
	Object[][] data;
	String productCode = null;
	String expMessage = null; 
	String expMessage1 =null;
	String productType = null;
	String productName = null;
	String shortName =null;
	static String networkCode;
	_parser parser;
	String[] msgParameter1;
	String allowedmaxbalancemsg = "error.transfer.allowedmaxpct.isless";
	String type="CHANNEL";
	HashMap<String, String> c2cMap1=new HashMap<String, String>();
	static boolean TestCaseCounter = false;
	String assignCategory="SIT";
	
	@BeforeMethod
	public void dataV() {

		parser = new _parser();

		c2cMap = new Channel2ChannelMap();
		chnlUsrMap = new ChannelUserMap();
		networkCode = _masterVO.getMasterValue("Network Code");
		paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);

	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-890") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _01_C2CReturn(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String FromDomain) throws InterruptedException, IOException {

		C2CReturn c2cReturn= new C2CReturn(driver);
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);

		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
		c2cMap = new Channel2ChannelMap();

		final String methodName = "Test_C2C_Return";
        Log.startTestCase(methodName);
		/*
		 * Test Case Number 1: To initiate C2C Transfer
		 */

		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UCHNL2CHNLRETURN1").getExtentCase(),FromCategory,ToCategory));
		currentNode.assignCategory(assignCategory);

		c2cMap1=c2cReturn.channel2channelReturn(FromCategory, ToCategory, toMSISDN, FromPIN);

		/*
		 * Test Case Number 2: Message Validation
		 */
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UCHNL2CHNLRETURN2").getExtentCase(),FromCategory,ToCategory));
		currentNode.assignCategory(assignCategory);
		Assertion.assertEquals(c2cMap1.get("actualMessage"), c2cMap1.get("expectedMessage"));

		/*
		 * Test Case: If receiver user is suspended
		 */

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getExtentCase());
		currentNode.assignCategory(assignCategory);

		suspendCHNLUser.suspendChannelUser_MSISDN(toMSISDN, "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(toMSISDN, "Automation remarks");
		try{
			c2cReturn.channel2channelReturn(FromCategory, ToCategory, toMSISDN, FromPIN);
			Assertion.assertFail("C2C Transfer is successful.");}
		catch(Exception e){
			String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("message.channeltransfer.usersuspended.msg", toMSISDN);
			Log.info(" Message fetched from WEB as : "+actualMessage);
			if(actualMessage==null){actualMessage="";}
			
			int row = ExtentI.combinationExistAtRow(new String[]{ExcelI.MSISDN}, new String[]{toMSISDN}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String name = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, row);
			String expectedMessage1 = MessagesDAO.prepareMessageByKey("message.channeltransfer.usersuspended.msg", name);
		
			if(!actualMessage.equals(expectedMessage)){
				Assertion.assertEquals(actualMessage, expectedMessage1);}
			else {Assertion.assertEquals(actualMessage, expectedMessage);}
		}

		resumeCHNLUser.resumeChannelUser_MSISDN(toMSISDN, "Auto Resume Remarks");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}






	/////////////////////////////////////////////////////////////////////////////////////

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-891") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _02_C2CReturnWithSuspendedTCP(String FromCategory, String ToCategory, String toMSISDN, String FromPIN , String FromDomain) throws InterruptedException, IOException {
		C2CReturn c2cReturn= new C2CReturn(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
		final String methodName = "Test_C2C_Return";
        Log.startTestCase(methodName);

    	
        
		/*
		 * Test Case: If Sender's user TCP is not active
		 */

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN2").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))

			break;
		}

		System.out.println(i);


		String TCPNAME = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
		String TCPId = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
		String FromMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);

		//trfCntrlProf.channelLevelTransferControlProfileActive(0,FromDomain, FromCategory,TCPNAME, TCPId);

		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");

		trfCntrlProf.channelLevelTransferControlProfileSuspend(0,FromDomain, FromCategory,TCPNAME, TCPId);
		String actualMessage;
		try{
			c2cMap1 = c2cReturn.channel2channelReturnNeg(FromCategory, ToCategory, toMSISDN, FromPIN);
			actualMessage = c2cMap1.get("actualMessage");
		}	
		catch(Exception e){
			actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive",FromMSISDN );
			Log.info(" Message fetched from WEB as : "+actualMessage);
			if(actualMessage==null){actualMessage="";}
			int row = ExtentI.combinationExistAtRow(new String[]{ExcelI.MSISDN}, new String[]{FromMSISDN}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String name = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, row);
			String expectedMessage1 = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive", name);
		
			if(!actualMessage.equals(expectedMessage)){
				Assertion.assertEquals(actualMessage, expectedMessage1);}
			else {Assertion.assertEquals(actualMessage, expectedMessage);}
		}


		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0,FromDomain, FromCategory,TCPNAME, TCPId);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-892") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _03_C2CReturnWithRecieverTCPInactive(String FromCategory, String ToCategory, String toMSISDN, String FromPIN , String FromDomain) throws InterruptedException, IOException {
		C2CReturn c2cReturn= new C2CReturn(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
		final String methodName = "Test_C2C_Return";
        Log.startTestCase(methodName);
    	
		/*
		 * Test Case: If Reciever's TCP is suspended
		 */

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN3").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(ToCategory)))

			break;
		}

		System.out.println(i);


		String TCPNAME = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
		String TCPId = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
		String domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);

		//trfCntrlProf.channelLevelTransferControlProfileActive(0,domain, ToCategory,TCPNAME, TCPId);

		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");

		trfCntrlProf.channelLevelTransferControlProfileSuspend(0,domain, ToCategory,TCPNAME, TCPId);

		String actualMessage;
		try{
			c2cMap1 = c2cReturn.channel2channelReturnNeg(FromCategory, ToCategory, toMSISDN, FromPIN);
			actualMessage = c2cMap1.get("actualMessage");
		}
		catch(Exception e){
			actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive", toMSISDN);
			Log.info(" Message fetched from WEB as : "+actualMessage);
			if(actualMessage==null){actualMessage="";}
			int row = ExtentI.combinationExistAtRow(new String[]{ExcelI.MSISDN}, new String[]{toMSISDN}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String name = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, row);
			String expectedMessage1 = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive", name);
		
			if(!actualMessage.equals(expectedMessage)){
				Assertion.assertEquals(actualMessage, expectedMessage1);}
			else {Assertion.assertEquals(actualMessage, expectedMessage);}
		}

		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0,domain, ToCategory,TCPNAME, TCPId);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}




	// 4. To verify that if sender make transaction of more than allowed maximum percentage (form transfer profile) then transaction would be failed.

	/*@Test(dataProvider="categoryData")
	public void d_C2CReturnMaxPercentageAllowed(String FromCategory, String ToCategory, String toMSISDN, String FromPIN , String FromDomain) throws InterruptedException{

		C2CReturn c2cReturn= new C2CReturn(driver);
		String expectedMsg = null;
		StringBuilder bld = new StringBuilder();

		if (!TestCaseCounter) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}


		Log.startTestCase(this.getClass().getName());

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
			break;
		}
		String FromDomainCode = DBHandler.AccessHandler.getDomainCode(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
		String FromCategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
		String fromLoginId = ExcelUtility.getCellData(0,ExcelI.LOGIN_ID, i);

		int k=1;
		for( k=1; k<=totalRow1;k++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, k).matches(ToCategory)))

			break;
		}
		String ToCategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);


		networkCode = _masterVO.getMasterValue("Network Code");		
		data = DBHandler.AccessHandler.getProductDetails(networkCode, FromDomainCode, ToCategoryCode,FromCategoryCode,type);
		String expectedMsg1; boolean zeroBalance = false;
		for(int productCount=0;productCount<=data.length;productCount++){

			if(productCount<=(data.length-1)){
				productCode = data[productCount][0].toString();
				productName = data[productCount][1].toString();
				shortName = data[productCount][2].toString();
				productType = data[productCount][3].toString();

				currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN4").getExtentCase(),shortName));
				currentNode.assignCategory(assignCategory);
				Log.info(""+productCode+" : "+fromLoginId+" : "+i+" : "+FromCategoryCode);
				String balance= DBHandler.AccessHandler.getUserBalance(productCode, fromLoginId);

				parser.convertStringToLong(balance).changeDenomation();
				long usrBalance = (long) (parser.getValue()*0.8 + 2);


				String[] msgParameter11 = new String[]{shortName,_masterVO.getProperty("AllowedMaxPercentage")};

				String actualMessage=null;
				
				// If product balance is Zero then product is not visible on WEB
				if(balance.equals("0")){
					actualMessage = "The balance for product "+ shortName +" : "+balance;
					Validator.messageCompare(actualMessage, actualMessage);
					zeroBalance=true;
				}
				else{
				try{c2cMap1 = c2cReturn.channel2channelReturnProductType(FromCategory, ToCategory,toMSISDN,shortName,String.valueOf(usrBalance), FromPIN);
				actualMessage= c2cMap1.get("actualMessage");}
				catch(Exception e){
					actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
				}
				expectedMsg1 = MessagesDAO.prepareMessageByKey("userreturn.returnproductlist.error.qtyless",shortName);

				//String actualMessage = c2cMap1.get("actualMessage");
				Validator.messageCompare(actualMessage, expectedMsg1);}


			}

			else if(data.length>1 && productCount>(data.length-1)&&!zeroBalance){
				currentNode=test.createNode(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN5").getExtentCase());
				currentNode.assignCategory(assignCategory);
				String[] productBalance = new String[data.length];
				String[] productType1 = new String[data.length];


				for(int productCount1=0, p=0;productCount1 < data.length;productCount1++){
					productCode = data[productCount1][0].toString();
					productName = data[productCount1][1].toString();
					shortName = data[productCount1][2].toString();
					productType = data[productCount1][3].toString();

					String balance= DBHandler.AccessHandler.getUserBalance(productCode, fromLoginId);	
					parser.convertStringToLong(balance).changeDenomation();
					long usrBalance = (long) (parser.getValue()*0.8 + 2);

					productType1[p] = shortName;
					productBalance[p] = String.valueOf(usrBalance);
					expectedMsg = MessagesDAO.prepareMessageByKey("userreturn.returnproductlist.error.qtyless",shortName);

					bld.append(expectedMsg);
					expMessage = bld.toString();
					p++;
				}

				String actualMessage;
				try{
					c2cMap1 = c2cReturn.channel2channelReturnAllProducts(FromCategory, ToCategory,   toMSISDN,productType1,productBalance, FromPIN);
					actualMessage= c2cMap1.get("actualMessage");}
				catch(Exception e){
					actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
				}
				//String actualMessage = c2cMap1.get("actualMessage");
				Validator.messageCompare(actualMessage, expMessage);

			}
		}Log.endTestCase(this.getClass().getName());}*/




	//5. To verify that if receiver’s balance crosses the maximum allowed balance (from transfer profile) then C2C Return would be failed.

	/*	
	@Test(dataProvider="categoryData")
	public void fC2CTransfer(String FromCategory, String ToCategory, String toMSISDN, String FromPIN , String FromDomain){

		C2CReturn c2cReturn= new C2CReturn(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);

		if (!TestCaseCounter) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}
		Log.startTestCase(this.getClass().getName());
		for(int productCount=0;productCount<data.length;productCount++){
			productName = data[productCount][1].toString();
			shortName = data[productCount][2].toString();
			currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN6").getExtentCase(),productName));
			currentNode.assignCategory(assignCategory);
			Log.info("No. of products available : "+data.length +" | "+ productName);

			ExtentI.Markup(ExtentColor.TEAL, "Modify TCP");
		trfCntrlProf.modifyTCPmaximumBalance(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("channeltcpID"), "50","49", productName);

		msgParameter1 = new String[]{shortName};
		String qty = 
		//performC2CTransaction2(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"), maxbalancereachmsg, true, msgParameter1);
		c2cMap1 = c2cReturn.channel2channelReturn(FromCategory, ToCategory,toMSISDN,shortName,FromPIN);
		String expectedMsg = MessagesDAO.prepareMessageByKey("userreturn.returnproductlist.error.qtyless",shortName);

		bld.append(expectedMsg);
		expMessage = bld.toString();

		String actualMessage = c2cMap1.get("actualMessage");
		Validator.messageCompare(actualMessage, expMessage);

		ExtentI.Markup(ExtentColor.TEAL, "Revert the modified values of TCP");
		trfCntrlProf.modifyTCPmaximumBalance(c2cMap.getC2CMap("toDomain"), c2cMap.getC2CMap("toCategory"), c2cMap.getC2CMap("channeltcpID"), _masterVO.getProperty("MaximumBalance"),_masterVO.getProperty("AlertingCount"), productName);
		}
		Log.endTestCase(this.getClass().getName());}



	 */

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1300") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _04_c2cReturnIncorrectPIN(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String FromDomain) throws InterruptedException, IOException {

		C2CReturn c2cReturn= new C2CReturn(driver);
		final String methodName = "C2C_Return incorrect pin";
		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
        
        Log.startTestCase(methodName);
	
    	
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN7").getExtentCase(),FromCategory,ToCategory));
		currentNode.assignCategory(assignCategory);
		String expected = MessagesDAO.getLabelByKey("userreturn.withdrawreturn.msg.smspininvalid");
		String actual = null;
		try{
		c2cReturn.channel2channelReturn(FromCategory, ToCategory, toMSISDN, "4357");
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		Assertion.assertEquals(actual, expected);}
		catch(Exception e){
			Log.writeStackTrace(e);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			Assertion.assertEquals(actual, expected);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-1301") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _05_c2cReturnSenderOutsuspended() throws InterruptedException, IOException {

		//String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String FromDomain
		C2CReturn c2cReturn= new C2CReturn(driver);
		Channel2ChannelReturnMap c2cReturnMap = new Channel2ChannelReturnMap();
		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
        
		final String methodName = "C2C_Return sender outsuspended";
        Log.startTestCase(methodName);
        
    	
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN8").getExtentCase(),c2cReturnMap.getC2CReturnMap(c2cReturnMap.fromCategory),c2cReturnMap.getC2CReturnMap(c2cReturnMap.toCategory)));
		currentNode.assignCategory(assignCategory);
		
		paraMap.put("outSuspend_chk", "Y");
		paraMap.put("searchMSISDN", c2cReturnMap.getC2CReturnMap(c2cReturnMap.fromMSISDN));
		paraMap.put("loginChange", "N");
		paraMap.put("assgnPhoneNumber", "N");
		ExtentI.Markup(ExtentColor.TEAL, "OutSuspend Channel User");
		new ChannelUser(driver).modifyChannelUserDetails(c2cReturnMap.getC2CReturnMap(c2cReturnMap.fromCategory), paraMap);
		
		String expected = MessagesDAO.getLabelByKey("message.channeltransfer.transfer.errormsg.useroutsuspend");
		String actual = null;
		try{
		c2cReturn.channel2channelReturn(c2cReturnMap.getC2CReturnMap(c2cReturnMap.fromCategory), c2cReturnMap.getC2CReturnMap(c2cReturnMap.toCategory), 
				c2cReturnMap.getC2CReturnMap(c2cReturnMap.toMSISDN), c2cReturnMap.getC2CReturnMap(c2cReturnMap.fromPIN));
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		Assertion.assertEquals(actual, expected);}
		catch(Exception e){
			Log.writeStackTrace(e);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			Assertion.assertEquals(actual, expected);
		}
		
		paraMap.put("outSuspend_chk", "N");
		ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
		new ChannelUser(driver).modifyChannelUserDetails(c2cReturnMap.getC2CReturnMap(c2cReturnMap.fromCategory), paraMap);
				
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test 
	@TestManager(TestKey = "PRETUPS-1302") 
	public void _06_c2creturnCommissionsuspended() throws InterruptedException{
		
		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
		final String methodName="_06_c2creturnCommissionsuspended";Log.startTestCase(methodName);
		Channel2ChannelReturnMap c2cRMap = new Channel2ChannelReturnMap();
		
		String fromCategory = c2cRMap.getC2CReturnMap(c2cRMap.fromCategory);
		String toCategory = c2cRMap.getC2CReturnMap(c2cRMap.toCategory);
		String toMSISDN = c2cRMap.getC2CReturnMap(c2cRMap.toMSISDN);
		String fromcommProfileName = c2cRMap.getC2CReturnMap(c2cRMap.fromCommProfile);
		String fromPIN = c2cRMap.getC2CReturnMap(c2cRMap.fromPIN);
		String fromDomain = c2cRMap.getC2CReturnMap(c2cRMap.fromDomain);
		String grade = c2cRMap.getC2CReturnMap(c2cRMap.fromGrade);
		String fromMSISDN = c2cRMap.getC2CReturnMap(c2cRMap.fromMSISDN);
		
		CommissionProfile commProfile = new CommissionProfile(driver);
		C2CReturn c2cReturn= new C2CReturn(driver);
	
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN9").getExtentCase(), fromcommProfileName,fromCategory));
		currentNode.assignCategory(assignCategory);
		
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Commission Profile:"+fromcommProfileName);
		Object[] idefault = commProfile.suspendcommissionProfileStatus(fromDomain, fromCategory, grade, fromcommProfileName);

		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		
		String actual=null;
		try{c2cReturn.channel2channelReturn(fromCategory, toCategory, toMSISDN, fromPIN);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();}
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
	
	@Test 
	@TestManager(TestKey = "PRETUPS-1303") 
	public void _07_c2cReturnQtyGreaterThanBalance(){
		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
		final String methodname = "_07_c2cReturnQtyGreaterThanBalance";
		Log.startTestCase(methodname);
				
		//String parentCategory, String Category, String MSISDN, String ProductType, String ProductCode
		C2CReturn c2cReturn = new C2CReturn(driver);
		Channel2ChannelReturnMap c2cRMap = new Channel2ChannelReturnMap();
		String FromCategory = c2cRMap.getC2CReturnMap(c2cRMap.fromCategory);
		String ToCategory = c2cRMap.getC2CReturnMap(c2cRMap.toCategory);
		String MSISDN = c2cRMap.getC2CReturnMap(c2cRMap.toMSISDN);
		String PIN = c2cRMap.getC2CReturnMap(c2cRMap.fromPIN);

		/*if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}*/
		
		String ProductType = ExtentI.fetchValuefromDataProviderSheet(ExcelI.PRODUCT_SHEET, ExcelI.PRODUCT_TYPE, 1);
		String ProductCode = ExtentI.fetchValuefromDataProviderSheet(ExcelI.PRODUCT_SHEET, ExcelI.PRODUCT_CODE, 1);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN10").getExtentCase(),ProductType));
		currentNode.assignCategory(assignCategory);
		
		String shortName = ExtentI.getValueofCorrespondingColumns(ExcelI.PRODUCT_SHEET, ExcelI.SHORT_NAME, new String[]{ExcelI.PRODUCT_CODE}, new String[]{ProductCode});
		String usrBalance = DBHandler.AccessHandler.getUserBalance(ProductCode,c2cRMap.getC2CReturnMap(c2cRMap.fromMSISDN));
		long reqAmount = _parser.getSystemAmount(_parser.getDisplayAmount(Long.parseLong(usrBalance)))+_parser.getSystemAmount(1);
		String amount = _parser.getDisplayAmount(reqAmount);
		
		Log.info("Amount to be entered: "+amount);
		String actual=null;
		try{
			c2cReturn.channel2channelReturnProductType(FromCategory, ToCategory, MSISDN, shortName, amount, PIN);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();}
		catch(Exception e){actual = new AddChannelUserDetailsPage(driver).getActualMessage();}
		String expected = MessagesDAO.prepareMessageByKey("userreturn.returnproductlist.error.qtyless", shortName);
		Assertion.assertEquals(actual, expected);
	}
	
	/*@Test 
	@TestManager(TestKey = "PRETUPS-1304") 
	public void _08_c2cReturnUserThreshold() throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN1").getModuleCode());
			TestCaseCounter = true;
		}
		final String methodname = "_08_c2cReturnUserThreshold";
		Log.startTestCase(methodname);
		
		C2CReturn c2cReturn = new C2CReturn(driver);
		Channel2ChannelReturnMap c2cRMap = new Channel2ChannelReturnMap();
		UserTransferCountsVO preReturnData = new UserTransferCountsVO();
		UserTransferCountsVO postReturnData = new UserTransferCountsVO();
		String FromCategory = c2cRMap.getC2CReturnMap(c2cRMap.fromCategory);
		String ToCategory = c2cRMap.getC2CReturnMap(c2cRMap.toCategory);
		String MSISDN = c2cRMap.getC2CReturnMap(c2cRMap.toMSISDN);
		String PIN = c2cRMap.getC2CReturnMap(c2cRMap.fromPIN);
		String fromUserName = c2cRMap.getC2CReturnMap(c2cRMap.fromUserName);
		
		
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCHNL2CHNLRETURN11").getExtentCase(), FromCategory,ToCategory));
		currentNode.assignCategory(assignCategory);
		
		preReturnData = DBHandler.AccessHandler.getUserTransferCounts(fromUserName);
		String currDate = DBHandler.AccessHandler.checkDateIsCurrentdate(preReturnData.getLastTransferDate());
		String currWeek = DBHandler.AccessHandler.checkDateExistinCurrentweek(preReturnData.getLastTransferDate());
		String currMonth = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preReturnData.getLastTransferDate());
		
		if(CommonUtils.roleCodeExistInLinkSheet(RolesI.C2CRETURN, FromCategory)) {
				HashMap<String, String> returnMap = c2cReturn.channel2channelReturn(FromCategory, ToCategory, MSISDN, PIN);
				Assertion.assertEquals(returnMap.get("actualMessage"), returnMap.get("expectedMessage"));	
		}
		postReturnData  = DBHandler.AccessHandler.getUserTransferCounts(fromUserName);
				
		long dailyoutcount, weeklyoutcount, monthlyoutcount;
	
		if(currDate.equalsIgnoreCase("true")){ dailyoutcount=preReturnData.getDailyTransferOutCount()+1;}
		else{dailyoutcount=1;}
		if(currWeek.equalsIgnoreCase("true")){ weeklyoutcount=preReturnData.getWeeklyTransferOutCount()+1;}
		else{weeklyoutcount=1;}
		if(currMonth.equalsIgnoreCase("true")){ monthlyoutcount=preReturnData.getMonthlyTransferOutCount()+1;}
		else{monthlyoutcount=1;}
		
		Log.info("Validating daily transfer out count of Sender:");
		Assertion.assertEquals(String.valueOf(postReturnData.getDailyTransferOutCount()), String.valueOf(dailyoutcount));
		Log.info("Validating weekly transfer out count of Sender:");
		Assertion.assertEquals(String.valueOf(postReturnData.getWeeklyTransferOutCount()), String.valueOf(weeklyoutcount));
		Log.info("Validating monthly transfer out count of Sender:");
		Assertion.assertEquals(String.valueOf(postReturnData.getMonthlyTransferOutCount()), String.valueOf(monthlyoutcount));
		
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
		
	}*/


	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed1() {
		String C2CReturnCode = _masterVO.getProperty("C2CReturnCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();


		/*
		 * Array list to store Categories for which C2C withdraw is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> alist3 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CReturnCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
				alist3.add(ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i));
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

		/*
		 * Calculate the Count of Users for each category
		 */
		int totalObjectCounter = 0;
		for (int i=0; i<alist1.size(); i++) {
			int categorySizeCounter = 0;
			for (int excelCounter=0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,excelCounter).equals(alist1.get(i))){
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

		Object[][] Data = new Object[totalObjectCounter][5];
		Object[][] Data1 = new Object[1][5];

		for(int j=0, k=0;j<alist1.size();j++){

			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int excelRowSize = ExcelUtility.getRowCount();
			String ChannelUserPIN = null;
			for(int i=1;i<=excelRowSize;i++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(alist2.get(j))){
					ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
					break;
				}
			}

			for(int excelCounter=1; excelCounter <=excelRowSize; excelCounter++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,excelCounter).equals(alist1.get(j))){
					Data[k][0] = alist2.get(j);
					Data[k][1] = alist1.get(j);
					Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
					Data[k][3] = ChannelUserPIN;
					Data[k][4] = alist3.get(j);
					k++;
				}
			}



			Data1 = new Object[1][5];

			Data1[0][0] = Data[0][0];
			Data1[0][1] = Data[0][1];
			Data1[0][2] = Data[0][2];
			Data1[0][3] = Data[0][3];
			Data1[0][4] = Data[0][4];
		}
		return Data1;
	}

}



