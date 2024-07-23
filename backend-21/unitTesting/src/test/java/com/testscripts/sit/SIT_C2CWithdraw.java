package com.testscripts.sit;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CWithdraw;
import com.Features.CacheUpdate;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
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

@ModuleManager(name = Module.SIT_C2C_Withdraw)
public class SIT_C2CWithdraw extends BaseTest{

	HashMap<String, String> c2cWithdrawMap=new HashMap<String, String>();
	static boolean TestCaseCounter = false;	
	String assignCategory="SIT";
	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-868") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _01_C2CWithdraw1(String ToCategory, String FromCategory, String FromDomain, String fromMSISDN, String toPIN) throws InterruptedException {
		C2CWithdraw c2cWithdraw= new C2CWithdraw(driver);
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);

		final String methodName = "Test_C2CWithdraw";
        Log.startTestCase(methodName);

		//Test Case : C2C Withdraw
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SC2CWITHDRAW1").getExtentCase(), FromCategory,ToCategory));
		currentNode.assignCategory(assignCategory);
		c2cWithdrawMap=c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN);

		//Test Case : Message validation
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SC2CWITHDRAW2").getExtentCase(), FromCategory,ToCategory));
		currentNode.assignCategory(assignCategory);
		Assertion.assertEquals(c2cWithdrawMap.get("actualMessage"), c2cWithdrawMap.get("expectedMessage"));

		//Test Case: If Sender is suspended
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITC2CWITHDRAW1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		suspendCHNLUser.suspendChannelUser_MSISDN(fromMSISDN, "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(fromMSISDN, "Automation remarks");
		try{
			c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN);
			Assertion.assertFail("C2C Withdraw is successful.");}
		catch(Exception e){
			String actualMessage = driver.findElement(By.xpath("//ul/li")).getText();
			String expectedMessage = MessagesDAO.prepareMessageByKey("message.channeltransfer.usersuspended.msg", fromMSISDN);
			Log.info(" Message fetched from WEB as : "+actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		resumeCHNLUser.resumeChannelUser_MSISDN(fromMSISDN, "Auto Resume Remarks");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-872") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _02_C2CWithdrawWithSuspendedTCP(String ToCategory, String FromCategory , String FromDomain, String fromMSISDN, String toPIN ) throws InterruptedException, IOException {
		C2CWithdraw C2CWithdraw= new C2CWithdraw(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);

		final String methodName = "Test_C2CWithdraw";
        Log.startTestCase(methodName);


		/*
		 * Test Case: If Sender's user TCP is not active
		 */

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITC2CWITHDRAW2").getExtentCase());
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

		int j=1;
		for( j=1; j<=totalRow1;j++)

		{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j).matches(ToCategory)))

			break;
		}

		System.out.println(j);

		String toMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, j);


		//trfCntrlProf.channelLevelTransferControlProfileActive(0,FromDomain, FromCategory,TCPNAME, TCPId);


		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");

		trfCntrlProf.channelLevelTransferControlProfileSuspend(0,FromDomain, FromCategory,TCPNAME, TCPId);

		String actualMessage;
		try{
			c2cWithdrawMap = C2CWithdraw.channel2channelWithdrawNeg(ToCategory, FromCategory, fromMSISDN, toPIN);
			actualMessage = c2cWithdrawMap.get("actualMessage");
		}
		catch(Exception e){
			actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
		}

		String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive",fromMSISDN );
		Log.info(" Message fetched from WEB as : "+actualMessage);
		Assertion.assertEquals(actualMessage, expectedMessage);



		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0,FromDomain, FromCategory,TCPNAME, TCPId);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-880") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _03_C2CWithdrawWithRecieverSuspendedTCP(String ToCategory, String FromCategory , String FromDomain, String fromMSISDN, String toPIN ) throws InterruptedException, IOException {
		C2CWithdraw C2CWithdraw= new C2CWithdraw(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);

		final String methodName = "Test_C2CWithdraw";
        Log.startTestCase(methodName);
		
		/*
		 * Test Case: If Reciever's user TCP is not active
		 */

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITC2CWITHDRAW3").getExtentCase());
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
		String toDomain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
		String toMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);


		//trfCntrlProf.channelLevelTransferControlProfileActive(0,toDomain, ToCategory,TCPNAME, TCPId);


		ExtentI.Markup(ExtentColor.TEAL, "Suspend TCP ");

		trfCntrlProf.channelLevelTransferControlProfileSuspend(0,toDomain, ToCategory,TCPNAME, TCPId);

		String actualMessage;
		try{
			c2cWithdrawMap = C2CWithdraw.channel2channelWithdrawNeg(ToCategory, FromCategory, fromMSISDN, toPIN);
			actualMessage = c2cWithdrawMap.get("actualMessage");
		}	
		catch(Exception e){
			actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
		}


		String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive",toMSISDN );
		Log.info(" Message fetched from WEB as : "+actualMessage);
		Assertion.assertEquals(actualMessage, expectedMessage);



		ExtentI.Markup(ExtentColor.TEAL, "Resume TCP");
		trfCntrlProf.channelLevelTransferControlProfileActive(0,toDomain, ToCategory,TCPNAME, TCPId);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1310") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _04_C2CWithdrawSenderOutsuspended(String ToCategory, String FromCategory, String FromDomain, String fromMSISDN, String toPIN) throws InterruptedException {
		C2CWithdraw c2cWithdraw= new C2CWithdraw(driver);
		
		//Test Case: If Sender is out-suspended
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITC2CWITHDRAW4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		final String methodName = "_04_C2CWithdrawSenderOutsuspended";
        Log.startTestCase(methodName);
		
		ChannelUserMap chnlUsrMap = new ChannelUserMap();
		HashMap<String, String> paraMap = (HashMap<String, String>) chnlUsrMap.getChannelUserMap(null, null);

		paraMap.put("outSuspend_chk", "Y");
		paraMap.put("searchMSISDN", fromMSISDN);
		paraMap.put("loginChange", "N");
		paraMap.put("assgnPhoneNumber", "N");
		ExtentI.Markup(ExtentColor.TEAL, "OutSuspend Channel User");
		new ChannelUser(driver).modifyChannelUserDetails(FromCategory, paraMap);
		
		HashMap<String,String> msg = c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN);

		Assertion.assertEquals(msg.get("actualMessage"), msg.get("expectedMessage"));
		
		paraMap.put("outSuspend_chk", "N");
		ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
		new ChannelUser(driver).modifyChannelUserDetails(FromCategory, paraMap);
					
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1311") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _05_C2CWithdrawCommissionsuspended(String ToCategory, String FromCategory, String FromDomain, String fromMSISDN, String toPIN) throws InterruptedException {
		C2CWithdraw c2cWithdraw= new C2CWithdraw(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITC2CWITHDRAW5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		final String methodName = "_05_C2CWithdrawCommissionsuspended";
        Log.startTestCase(methodName);
		
        String grade = ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.GRADE, new String[]{ExcelI.MSISDN}, new String[]{fromMSISDN});
        String fromcommProfileName = ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.COMMISSION_PROFILE, new String[]{ExcelI.MSISDN}, new String[]{fromMSISDN});
        
		CommissionProfile commProfile = new CommissionProfile(driver);
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Commission Profile:"+fromcommProfileName);
		Object[] idefault = commProfile.suspendcommissionProfileStatus(FromDomain, FromCategory, grade, fromcommProfileName);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		String actual = null;
		
		String expected = MessagesDAO.prepareMessageByKey("commissionprofile.notactive.msg", fromMSISDN,idefault[1].toString());
		try{
		c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN);}
		catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		
		Assertion.assertEquals(actual, expected);
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Commission Profile:"+fromcommProfileName);
		commProfile.resumecommissionProfileStatus(FromDomain, FromCategory, grade, fromcommProfileName,(boolean)idefault[0]);
		new CacheUpdate(driver).updateCache(CacheController.CacheI.COMMISSION_PROFILE());
			
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1312") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void _06_C2CWithdrawMorethanUsrBalance(String ToCategory, String FromCategory, String FromDomain, String fromMSISDN, String toPIN) throws InterruptedException {
		C2CWithdraw c2cWithdraw= new C2CWithdraw(driver);
		
		String ProductType = ExtentI.fetchValuefromDataProviderSheet(ExcelI.PRODUCT_SHEET, ExcelI.PRODUCT_TYPE, 1);
		String ProductCode = ExtentI.fetchValuefromDataProviderSheet(ExcelI.PRODUCT_SHEET, ExcelI.PRODUCT_CODE, 1);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITC2CWITHDRAW6").getExtentCase(), ProductType);
		currentNode.assignCategory(assignCategory);
		final String methodName = "_06_C2CWithdrawMorethanUsrBalance";
        Log.startTestCase(methodName);
		        
		String shortName = ExtentI.getValueofCorrespondingColumns(ExcelI.PRODUCT_SHEET, ExcelI.SHORT_NAME, new String[]{ExcelI.PRODUCT_CODE}, new String[]{ProductCode});
		String usrBalance = DBHandler.AccessHandler.getUserBalance(ProductCode,fromMSISDN);
		long reqAmount = _parser.getSystemAmount(_parser.getDisplayAmount(Long.parseLong(usrBalance)))+_parser.getSystemAmount(1);
		String amount = _parser.getDisplayAmount(reqAmount);
		
		Log.info("Amount to be entered: "+amount);
		String actual=null;
		try{
			c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN,ProductType,amount);
			}
		catch(Exception e){actual = new AddChannelUserDetailsPage(driver).getActualMessage();}
		String expected = MessagesDAO.prepareMessageByKey("userreturn.returnproductlist.error.qtyless", shortName);
		Assertion.assertEquals(actual, expected);
        
        Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/*@Test(dataProvider = "categoryData") 
	@TestManager(TestKey = "PRETUPS-1313") 
	public void _07_c2cWithdrawUserThreshold(String ToCategory, String FromCategory, String FromDomain, String fromMSISDN, String toPIN) throws InterruptedException{
		
		final String methodname = "_07_c2cWithdrawUserThreshold";
		Log.startTestCase(methodname);
		
		C2CWithdraw c2cWithdraw= new C2CWithdraw(driver);
		
		UserTransferCountsVO preReturnData = new UserTransferCountsVO();
		UserTransferCountsVO postReturnData = new UserTransferCountsVO();

		String fromUserName = ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, new String[]{ExcelI.MSISDN}, new String[]{fromMSISDN});
		
		
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITC2CWITHDRAW7").getExtentCase(), FromCategory));
		currentNode.assignCategory(assignCategory);
		
		preReturnData = DBHandler.AccessHandler.getUserTransferCounts(fromUserName);
		String currDate = DBHandler.AccessHandler.checkDateIsCurrentdate(preReturnData.getLastTransferDate());
		String currWeek = DBHandler.AccessHandler.checkDateExistinCurrentweek(preReturnData.getLastTransferDate());
		String currMonth = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preReturnData.getLastTransferDate());
		
		if(CommonUtils.roleCodeExistInLinkSheet(RolesI.C2CWDL_ROLECODE, FromCategory)) {
				HashMap<String, String> withdrawMap = c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN);
				Assertion.assertEquals(withdrawMap.get("actualMessage"), withdrawMap.get("expectedMessage"));	
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
		String C2CWithdrawCode = _masterVO.getProperty("C2CWithdrawCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which C2C withdraw is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> alist3 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CWithdrawCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
				alist3.add(ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i));

			}
		}

		/*
		 * Counter to count number of users exists in channel users hierarchy sheet 
		 * of Categories for which C2C Withdraw is allowed
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[alist1.size()][5];
		Object[][] Data1 = new Object[1][5];
		for(int j=0;j<alist1.size();j++){
			Data[j][0] = alist2.get(j);
			Data[j][1] = alist1.get(j);
			Data[j][2] = alist3.get(j);

			for(int i=1;i<=chnlCount;i++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][1])){
					Data[j][3] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i); 
					break;}
			}

			for(int i=1;i<=chnlCount;i++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][0])){
					Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
					break;}
			}

			Data1[0][0] = Data[0][0];
			Data1[0][1] = Data[0][1];
			Data1[0][2] = Data[0][2];
			Data1[0][3] = Data[0][3];
			Data1[0][4] = Data[0][4];
		} 
		return Data1;
	}

}


