package com.testscripts.sit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.Map_TCPValues;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.Channel2ChannelMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.UserTransferCountsVO;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
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
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_User_Transfer_Count_Validation_C2C)
public class SIT_UserTransferCountValidationC2C extends BaseTest{
	static boolean testCaseCounter = false;
	static String c2cusertrfcount;
	String extentc2clog = "Performing C2C transfer";
	static String senderName, receiverName;
	UserTransferCountsVO preUserSenderTrfCountsVO = new UserTransferCountsVO();
	UserTransferCountsVO preUserReceiverTrfCountsVO = new UserTransferCountsVO();
	UserTransferCountsVO postUserSenderTrfCountsVO = new UserTransferCountsVO();
	UserTransferCountsVO postUserReceiverTrfCountsVO = new UserTransferCountsVO();
	HashMap<String, String> c2cMaped = new HashMap<String, String>();
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-897") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void aDailytransfercount() throws InterruptedException {
		final String methodName = "Test_UserTransferCountValidationC2C";
        Log.startTestCase(methodName);

		CaseMaster CaseMaster1=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD1");
		CaseMaster CaseMaster2=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD2");
		c2cusertrfcount="[SIT]"+CaseMaster1.getModuleCode();
		Channel2ChannelMap c2cMap=new Channel2ChannelMap();
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		
		String fromRowNum=c2cMap.getC2CMap("fromRowNum");
		String toRowNum=c2cMap.getC2CMap("toRowNum");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		 senderName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, Integer.parseInt(fromRowNum));
		 receiverName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, Integer.parseInt(toRowNum));
		
		 
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		preUserSenderTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(senderName);
		preUserReceiverTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(receiverName);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String value = DBHandler.AccessHandler.getPreference(c2cMap.getC2CMap("fromCategoryCode"),networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		 int maxApprovalLevel=0;
	        if(BTSLUtil.isNullString(value)) {
	        	maxApprovalLevel=0;
	        }
	        else
			maxApprovalLevel = Integer.parseInt(value);
		String currDate_sender = DBHandler.AccessHandler.checkDateIsCurrentdate(preUserSenderTrfCountsVO.getLastTransferDate());
		String currDate_receiver = DBHandler.AccessHandler.checkDateIsCurrentdate(preUserReceiverTrfCountsVO.getLastTransferDate());
		
		c2cMaped = c2cTransfer.channel2channelTransfer(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"));
		
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
         		c2cMaped=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMaped.get("TransactionID"),maxApprovalLevel);
     		}
         	else if(maxApprovalLevel == 2)
     		{
         		c2cMaped=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMaped.get("TransactionID"),maxApprovalLevel);
         		c2cMaped=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMaped.get("TransactionID"),maxApprovalLevel);
     		}
         	else if(maxApprovalLevel == 3)
     		{
         		c2cMaped=c2cTransfer.performingLevel1Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMaped.get("TransactionID"),maxApprovalLevel);
         		c2cMaped=c2cTransfer.performingLevel2Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMaped.get("TransactionID"),maxApprovalLevel);
         		c2cMaped=c2cTransfer.performingLevel3Approval(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"),c2cMaped.get("TransactionID"),maxApprovalLevel);
     		}
     } 
		
		
		long dailyoutcount;
		long dailyincount;
		if(currDate_sender.equalsIgnoreCase("true")){ dailyoutcount=preUserSenderTrfCountsVO.getDailyTransferOutCount()+1;}
		else{dailyoutcount=1;}
		
		if(currDate_receiver.equalsIgnoreCase("true")){dailyincount=preUserReceiverTrfCountsVO.getDailyTransferInCount()+1;}
		else{dailyincount=1;}
		
		postUserSenderTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(senderName);
		postUserReceiverTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(receiverName);
		
		Assertion.assertEquals(String.valueOf(postUserSenderTrfCountsVO.getDailyTransferOutCount()), String.valueOf(dailyoutcount));
		
		currentNode = test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("SIT");
		Assertion.assertEquals(String.valueOf(postUserReceiverTrfCountsVO.getDailyTransferInCount()), String.valueOf(dailyincount));
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-898") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void bWeeklytransfercount() throws InterruptedException {
		final String methodName = "Test_UserTransferCountValidationC2C";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster3=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD3");
		CaseMaster CaseMaster4=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD4");
		
		currentNode = test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory("SIT");
		String weekDate_sender = DBHandler.AccessHandler.checkDateExistinCurrentweek(preUserSenderTrfCountsVO.getLastTransferDate());
		String monthDate_sender = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preUserSenderTrfCountsVO.getLastTransferDate());

		String weekDate_receiver = DBHandler.AccessHandler.checkDateExistinCurrentweek(preUserReceiverTrfCountsVO.getLastTransferDate());
		String monthDate_receiver = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preUserReceiverTrfCountsVO.getLastTransferDate());

		
		long weeklyoutcount,weeklyincount;
		if(weekDate_sender.equalsIgnoreCase("true")&&monthDate_sender.equalsIgnoreCase("true")){
			weeklyoutcount=preUserSenderTrfCountsVO.getWeeklyTransferOutCount()+1;}
		else{ weeklyoutcount=1;}

		if(weekDate_receiver.equalsIgnoreCase("true")&&monthDate_receiver.equalsIgnoreCase("true")){
			weeklyincount=preUserReceiverTrfCountsVO.getWeeklyTransferInCount()+1;}
		else{weeklyincount=1;}
		
		Assertion.assertEquals(String.valueOf(postUserSenderTrfCountsVO.getWeeklyTransferOutCount()), String.valueOf(weeklyoutcount));
		
		currentNode = test.createNode(CaseMaster4.getExtentCase());
		currentNode.assignCategory("SIT");
		Assertion.assertEquals(String.valueOf(postUserReceiverTrfCountsVO.getWeeklyTransferInCount()), String.valueOf(weeklyincount));
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	
	@Test
	@TestManager(TestKey = "PRETUPS-899") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void cMonthlytransfercount() throws InterruptedException {
		final String methodName = "Test_UserTransferCountValidationC2C";
        Log.startTestCase(methodName);

		CaseMaster CaseMaster5=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD5");
		CaseMaster CaseMaster6=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD6");
		
		currentNode = test.createNode(CaseMaster5.getExtentCase());
		currentNode.assignCategory("SIT");
		String monthDate_sender = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preUserSenderTrfCountsVO.getLastTransferDate());
		String monthDate_receiver = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preUserReceiverTrfCountsVO.getLastTransferDate());

		long monthlyoutcount,monthlyincount;
		if(monthDate_sender.equalsIgnoreCase("true")){
			monthlyoutcount=preUserSenderTrfCountsVO.getMonthlyTransferOutCount()+1;}
		else{ monthlyoutcount=1;}
		
		if(monthDate_receiver.equalsIgnoreCase("true")){monthlyincount=preUserReceiverTrfCountsVO.getMonthlyTransferInCount()+1;}
		else{monthlyincount=1;}
		
		Assertion.assertEquals(String.valueOf(postUserSenderTrfCountsVO.getMonthlyTransferOutCount()), String.valueOf(monthlyoutcount));
		
		currentNode = test.createNode(CaseMaster6.getExtentCase());
		currentNode.assignCategory("SIT");
		Assertion.assertEquals(String.valueOf(postUserReceiverTrfCountsVO.getMonthlyTransferInCount()), String.valueOf(monthlyincount));
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}

	@Test
	@TestManager(TestKey = "PRETUPS-900") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void dDailyTrfOutcountreached() throws InterruptedException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String methodName = "Test_UserTransferCountValidationC2C";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster7=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD7");
		CaseMaster CaseMaster8=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD8");
		CaseMaster CaseMaster9=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD9");
		
		Channel2ChannelMap c2cMap=new Channel2ChannelMap();
		Map_TCPValues tcpMap = new Map_TCPValues();
		HashMap<String, String> userData = new HashMap<String, String>();
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		
	    String[][] valuesToModfiy = new String[][]{ {"enterDailyChannelTransferOutCount","1"}, 
	    										  {"enterDailyChannelTransferOutAlertingCount","1"}};
		
	    String[][] defaultToModify = new String[][]{ {"enterDailyChannelTransferOutCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyOutCount")}, 
				  								 {"enterDailyChannelTransferOutAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyOutAlertingCount")}};
	    
	    int fromRowNum = Integer.parseInt(c2cMap.getC2CMap("fromRowNum"));		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		  userData.put("tcpID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, fromRowNum));
		  userData.put("domainName", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, fromRowNum));
		  userData.put("categoryName", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, fromRowNum));
		  
	    currentNode = test.createNode(CaseMaster7.getExtentCase());
		currentNode.assignCategory("SIT");
	    
	    ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily out count to 1");
	    tcpchange.modifyAnyTCPValue(valuesToModfiy, userData,"channel");
	    
	    preUserSenderTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(senderName);
		preUserReceiverTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(receiverName);
		String curroutDate = DBHandler.AccessHandler.checkDateIsCurrentdate(preUserSenderTrfCountsVO.getLastOutTime());
		String currinDate = DBHandler.AccessHandler.checkDateIsCurrentdate(preUserReceiverTrfCountsVO.getLastInTime());
		
		performC2CTransaction("5207",senderName);
		
		currentNode = test.createNode(CaseMaster8.getExtentCase());
		currentNode.assignCategory("SIT");
		
	   long dailyoutcount, dailyincount;
		if(curroutDate.equalsIgnoreCase("true")) dailyoutcount=preUserSenderTrfCountsVO.getDailyTransferOutCount();
		else dailyoutcount=0;
		if(currinDate.equalsIgnoreCase("true")) dailyincount=preUserReceiverTrfCountsVO.getDailyTransferInCount();
		else dailyincount=0;
	   
		postUserSenderTrfCountsVO=DBHandler.AccessHandler.getUserTransferCounts(senderName);
		Assertion.assertEquals(String.valueOf(postUserSenderTrfCountsVO.getDailyTransferOutCount()), String.valueOf(dailyoutcount));
		
		currentNode = test.createNode(CaseMaster9.getExtentCase());
		currentNode.assignCategory("SIT");
		
		postUserReceiverTrfCountsVO  = DBHandler.AccessHandler.getUserTransferCounts(receiverName);

		Assertion.assertEquals(String.valueOf(postUserReceiverTrfCountsVO.getDailyTransferInCount()), String.valueOf(dailyincount));		
		
	    ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily out count to default");
		tcpchange.modifyAnyTCPValue(defaultToModify, userData,"channel");  
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	
	/*@Test
	public void eWeeklyTrfOutcountreached() throws InterruptedException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Log.startTestCase(this.getClass().getName());

		if (!testCaseCounter) {
			test = extent.createTest(c2cusertrfcount);
			testCaseCounter = true;
		}
	
		Channel2ChannelMap c2cMap=new Channel2ChannelMap();
		Map_TCPValues tcpMap = new Map_TCPValues();
		HashMap<String, String> userData = new HashMap<String, String>();
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		
	    String[][] valuesToModfiy = new String[][]{ {"enterDailyChannelTransferOutCount","1"}, 
	    										  {"enterDailyChannelTransferOutAlertingCount","1"},
	    										  {"enterWeeklyChannelTransferOutCount","1"}, 
	    										  {"enterWeeklyChannelTransferOutAlertingCount","1"}};
		
	    String[][] defaultToModify = new String[][]{ {"enterDailyChannelTransferOutCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyOutCount")}, 
				  								 {"enterDailyChannelTransferOutAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyOutAlertingCount")},
	    										{"enterWeeklyChannelTransferOutCount",tcpMap.DataMap_TCPCategoryLevel().get("WeeklyOutCount")}, 
	    										{"enterWeeklyChannelTransferOutAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("WeeklyOutAlertingCount")}};
	    
	    int fromRowNum = Integer.parseInt(c2cMap.getC2CMap("fromRowNum"));		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		  userData.put("tcpID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, fromRowNum));
		  userData.put("domainName", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, fromRowNum));
		  userData.put("categoryName", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, fromRowNum));
	    
	    currentNode = test.createNode("To verify that if weekly transfer out count for sender is reached then C2C transaction is not processed successfuly.");
		currentNode.assignCategory("SIT");
	    
	    ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily&weekly out count to 1");
	    tcpchange.modifyAnyTCPValue(valuesToModfiy, userData,"channel");
	    
	    pOutValues=DBHandler.AccessHandler.getusertransfercountvalues(senderName, "out");
		String pDailyout = pOutValues[1];
		Log.info("Daily out count for user "+senderName+" before C2C transfer : "+pDailyout);
		String curroutDate = DBHandler.AccessHandler.checkDateIsCurrentdate(pOutValues[0]);
		pInValues  = DBHandler.AccessHandler.getusertransfercountvalues(receiverName, "in");
		String pDailyin = pInValues[1];
		String currinDate = DBHandler.AccessHandler.checkDateIsCurrentdate(pInValues[0]);
		
		performC2CTransaction("5208",senderName);
		
		currentNode = test.createNode("To verify that if C2C transaction is not processed then weekly out count for sender remain same.");
		currentNode.assignCategory("SIT");
		
	   int weeklyoutcount, weeklyincount;
		if(curroutDate.equalsIgnoreCase("true")) weeklyoutcount=Integer.parseInt(pDailyout);
		else weeklyoutcount=0;
		if(currinDate.equalsIgnoreCase("true")) weeklyincount=Integer.parseInt(pDailyin);
		else weeklyincount=0;
	   
		aOutValues=DBHandler.AccessHandler.getusertransfercountvalues(senderName, "out");
		String aWeeklyout = aOutValues[2];
		Log.info("Daily out count for user "+senderName+" after C2C transfer : "+aWeeklyout);
		
		Validator.messageCompare(aWeeklyout, String.valueOf(weeklyoutcount));
		
		currentNode = test.createNode("To verify that if C2C transaction is not processed then weekly in count for receiver remain same.");
		currentNode.assignCategory("SIT");
		
		aInValues  = DBHandler.AccessHandler.getusertransfercountvalues(receiverName, "in");
		String aWeeklyin = aInValues[2];
		Validator.messageCompare(aWeeklyin, String.valueOf(weeklyincount));		
		
	    ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily&weekly out count to default");
		tcpchange.modifyAnyTCPValue(defaultToModify, userData,"channel");  
		
		}*/
	
	@Test
	@TestManager(TestKey = "PRETUPS-901") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void fDailyTrfIncountreached() throws InterruptedException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String methodName = "Test_UserTransferCountValidationC2C";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster10=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD10");
		CaseMaster CaseMaster11=_masterVO.getCaseMasterByID("SITC2CTRFCOUNTVALD11");
		
		Channel2ChannelMap c2cMap=new Channel2ChannelMap();
		Map_TCPValues tcpMap = new Map_TCPValues();
		HashMap<String, String> userData = new HashMap<String, String>();
		TransferControlProfile tcpchange = new TransferControlProfile(driver);

		String[][] valuesToModfiy = new String[][]{ {"enterDailyTransferInCount","1"}, 
				{"enterDailyTransferInAlertingCount","1"}};

		String[][] defaultToModify = new String[][]{ {"enterDailyTransferInCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInCount")}, 
				{"enterDailyTransferInAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInAlertingCount")}};

		int toRowNum = Integer.parseInt(c2cMap.getC2CMap("toRowNum"));		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		userData.put("tcpID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, toRowNum));
		userData.put("domainName", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, toRowNum));
		userData.put("categoryName", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, toRowNum));

		currentNode = test.createNode(CaseMaster10.getExtentCase());
		currentNode.assignCategory("SIT");

		ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily in count to 1");
		tcpchange.modifyAnyTCPValue(valuesToModfiy, userData,"channel");

		preUserReceiverTrfCountsVO  = DBHandler.AccessHandler.getUserTransferCounts(receiverName);
		String currinDate = DBHandler.AccessHandler.checkDateIsCurrentdate(preUserReceiverTrfCountsVO.getLastInTime());

		performC2CTransaction("5201",receiverName);

		currentNode = test.createNode(CaseMaster11.getExtentCase());
		currentNode.assignCategory("SIT");

		long dailyincount;
		if(currinDate.equalsIgnoreCase("true")) dailyincount=preUserReceiverTrfCountsVO.getDailyTransferInCount();
		else dailyincount=0;

		postUserReceiverTrfCountsVO=DBHandler.AccessHandler.getUserTransferCounts(receiverName);
		Assertion.assertEquals(String.valueOf(postUserReceiverTrfCountsVO.getDailyTransferInCount()), String.valueOf(dailyincount));		

		ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily out count to default");
		tcpchange.modifyAnyTCPValue(defaultToModify, userData,"channel");  
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	
	
	public void performC2CTransaction(String messageCode, String... parservalues){
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		Channel2ChannelMap c2cMap=new Channel2ChannelMap();
		try{
		    c2cTransfer.channel2channelTransfer(c2cMap.getC2CMap("fromCategory"), c2cMap.getC2CMap("toCategory"),c2cMap.getC2CMap("toMSISDN"), c2cMap.getC2CMap("fromPIN"));
		   }catch(Exception e){Log.info("Transaction is not successful");
		   String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		   String expected = MessagesDAO.prepareMessageByKey(messageCode, parservalues);
		   Assertion.assertEquals(actual, expected);
		   Assertion.completeAssertions();
		}
	}
}
