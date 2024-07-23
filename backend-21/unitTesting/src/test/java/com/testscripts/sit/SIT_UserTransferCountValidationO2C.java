package com.testscripts.sit;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.Map_TCPValues;
import com.Features.O2CTransfer;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.UserTransferCountsVO;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_User_Transfer_Count_Validation_O2C)
public class SIT_UserTransferCountValidationO2C extends BaseTest{
	static boolean testCaseCounter = false, directO2CPreference;
	
	long initiationAmount = 0;
	
	UserTransferCountsVO preUserTrfCountsVO = new UserTransferCountsVO();
	UserTransferCountsVO postUserTrfCountsVO = new UserTransferCountsVO();
	String assignCategory="SIT";
	
	@Test
	@TestManager(TestKey = "PRETUPS-902") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEA_DailyINTransferCount() throws InterruptedException {
		final String methodname = "CASEA_DailyINTransferCount";
		Log.startTestCase(methodname);
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		 
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		directO2CPreference = Boolean.parseBoolean(DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED"));
		preUserTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(transferMap.get("TO_USER_NAME"));
		
		long dailyInCount;
		String Counter = DBHandler.AccessHandler.checkDateIsCurrentdate(preUserTrfCountsVO.getLastInTime());
		if(Counter.equalsIgnoreCase("true")){ 
			dailyInCount = preUserTrfCountsVO.getDailyTransferInCount() + 1;
		} else {
			dailyInCount = 1;
		}
		
		initiationAmount = O2CTransfer.generateInitiationAmount(transferMap.get("TO_CATEGORY"), false, false, 5);
		transferMap.put("INITIATION_AMOUNT", String.valueOf(initiationAmount));
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		if (!directO2CPreference)
			O2CTransfer.performingLevel1Approval(transferMap);
		else
			Log.info("Direct Operator to Channel is applicable in system");		
				
		postUserTrfCountsVO  = DBHandler.AccessHandler.getUserTransferCounts(transferMap.get("TO_USER_NAME"));
		
		Assertion.assertEquals(String.valueOf(postUserTrfCountsVO.getDailyTransferInCount()), String.valueOf(dailyInCount));
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-905") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEB_DailyINTransferValue() throws InterruptedException {
		final String methodname = "CASEB_DailyINTransferValue";
		Log.startTestCase(methodname);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String weekDate = DBHandler.AccessHandler.checkDateExistinCurrentweek(preUserTrfCountsVO.getLastInTime());
		
		long dailyInValue;
		if(weekDate.equalsIgnoreCase("true"))
			dailyInValue = preUserTrfCountsVO.getDailyTransferInValue() + _parser.getSystemAmount(initiationAmount);
		else 
			dailyInValue = _parser.getSystemAmount(initiationAmount);

		Assertion.assertEquals(String.valueOf(postUserTrfCountsVO.getDailyTransferInValue()), String.valueOf(dailyInValue));
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-918") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEC_WeeklyINTransferCount() throws InterruptedException {
		final String methodname = "CASEC_WeeklyINTransferCount";
		Log.startTestCase(methodname);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String weekDate = DBHandler.AccessHandler.checkDateExistinCurrentweek(preUserTrfCountsVO.getLastInTime());
		
		long weeklyincount;
		if(weekDate.equalsIgnoreCase("true")) {
			weeklyincount = preUserTrfCountsVO.getWeeklyTransferInCount() + 1;
		} else { weeklyincount = 1; }

		Assertion.assertEquals(String.valueOf(postUserTrfCountsVO.getWeeklyTransferInCount()), String.valueOf(weeklyincount));
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-919") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASED_WeeklyINTransferValue() throws InterruptedException {
		final String methodname = "CASED_WeeklyINTransferValue";
		Log.startTestCase(methodname);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String weekDate = DBHandler.AccessHandler.checkDateExistinCurrentweek(preUserTrfCountsVO.getLastInTime());
		
		long weeklyInValue;
		if(weekDate.equalsIgnoreCase("true"))
			weeklyInValue = preUserTrfCountsVO.getWeeklyTransferInValue() + _parser.getSystemAmount(initiationAmount);
		else 
			weeklyInValue = _parser.getSystemAmount(initiationAmount);

		Assertion.assertEquals(String.valueOf(postUserTrfCountsVO.getWeeklyTransferInValue()), String.valueOf(weeklyInValue));
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-920") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEE_MonthlyINTransferCount() throws InterruptedException {
		final String methodname = "CASEE_MonthlyINTransferCount";
		Log.startTestCase(methodname);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String weekDate = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preUserTrfCountsVO.getLastInTime());
		
		long monthlyincount;
		if(weekDate.equalsIgnoreCase("true"))
			monthlyincount= preUserTrfCountsVO.getMonthlyTransferInCount() + 1;
		else
			monthlyincount = 1;
		
		Assertion.assertEquals(String.valueOf(postUserTrfCountsVO.getMonthlyTransferInCount()), String.valueOf(monthlyincount));
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-921") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEF_MonthlyINTransferValue() throws InterruptedException {
		final String methodname = "CASEF_MonthlyINTransferValue";
		Log.startTestCase(methodname);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String weekDate = DBHandler.AccessHandler.checkDateExistinCurrentmonth(preUserTrfCountsVO.getLastInTime());
		
		long monthlyInValue;
		if(weekDate.equalsIgnoreCase("true"))
			monthlyInValue= preUserTrfCountsVO.getMonthlyTransferInValue() + _parser.getSystemAmount(initiationAmount);
		else
			monthlyInValue = _parser.getSystemAmount(initiationAmount);
		
		Assertion.assertEquals(String.valueOf(postUserTrfCountsVO.getMonthlyTransferInValue()), String.valueOf(monthlyInValue));
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-927") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEG_DailyINTrfCount() throws InterruptedException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String methodname = "CASEG_DailyINTrfCount";
		Log.startTestCase(methodname);
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		Map_TCPValues tcpMap = new Map_TCPValues();
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		HashMap<String, String> userData = new HashMap<String, String>();
		
		userData.put("tcpID", transferMap.get("TO_NA_TCP_ID"));
		userData.put("domainName", transferMap.get("TO_DOMAIN"));
		userData.put("categoryName", transferMap.get("TO_CATEGORY"));
		
	    String[][] valuesToModfiy = new String[][]{ {"enterDailyTransferInCount","1"}, 
	    										  {"enterDailyTransferInAlertingCount","1"}};
		
	    String[][] defaultToModify = new String[][]{ {"enterDailyTransferInCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInCount")}, 
				  								 {"enterDailyTransferInAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInAlertingCount")}};
		  
	    currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C7").getExtentCase(),transferMap.get("TO_CATEGORY")));
		currentNode.assignCategory(assignCategory);
	    
	    tcpchange.modifyAnyTCPValue(valuesToModfiy, userData,"channel");
	    
	    directO2CPreference = Boolean.parseBoolean(DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED"));
	    preUserTrfCountsVO = DBHandler.AccessHandler.getUserTransferCounts(transferMap.get("TO_USER_NAME"));
		String currinDate = DBHandler.AccessHandler.checkDateIsCurrentdate(preUserTrfCountsVO.getLastInTime());
		
		transferMap.put("INITIATION_AMOUNT", "" + O2CTransfer.generateInitiationAmount(transferMap.get("TO_CATEGORY"), false, false, 5));
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);	
		if (!directO2CPreference) {
			performO2CApprovalLevel1(transferMap, "5201", transferMap.get("TO_USER_NAME"));
		} else
			Log.info("Direct Operator to Channel is applicable in system");	
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		long dailyincount;
		postUserTrfCountsVO  = DBHandler.AccessHandler.getUserTransferCounts(transferMap.get("TO_USER_NAME"));
		if(currinDate.equalsIgnoreCase("true")) 
			dailyincount = preUserTrfCountsVO.getDailyTransferInCount();
		else 
			dailyincount = 0;

		Assertion.assertEquals(String.valueOf(postUserTrfCountsVO.getDailyTransferInCount()),  String.valueOf(dailyincount));		
		
	    ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily out count to default");
		tcpchange.modifyAnyTCPValue(defaultToModify, userData,"channel");  
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
		}
	
/*	@Test
	public void CASEE_WeeklyINTrfCount() throws InterruptedException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String methodname = "CASEE_WeeklyINTrfCount";
		Log.startTestCase(methodname);

		if (!testCaseCounter) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C1").getModuleCode());
			testCaseCounter = true;
		}
	
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		Map_TCPValues tcpMap = new Map_TCPValues();
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		HashMap<String, String> userData = new HashMap<String, String>();
		
		userData.put("tcpID", transferMap.get("TO_NA_TCP_ID"));
		userData.put("domainName", transferMap.get("TO_DOMAIN"));
		userData.put("categoryName", transferMap.get("TO_CATEGORY"));
		
		String[][] valuesToModfiy = new String[][]{ {"enterDailyTransferInCount","1"}, 
			  										{"enterDailyTransferInAlertingCount","1"},
													{"enterWeeklyTransferInCount","1"}, 
													{"enterWeeklyTransferInAlertingCount","1"}};

		String[][] defaultToModify = new String[][]{ {"enterDailyTransferInCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInCount")}, 
			 										{"enterDailyTransferInAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInAlertingCount")},
													{"enterWeeklyTransferInCount",tcpMap.DataMap_TCPCategoryLevel().get("WeeklyInCount")},
													{"enterWeeklyTransferInAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("WeeklyInAlertingCount")}};
	    
	   
	    currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C9").getExtentCase(),transferMap.get("TO_CATEGORY")));
		currentNode.assignCategory(assignCategory);
	    
	    tcpchange.modifyAnyTCPValue(valuesToModfiy, userData,"channel");
	    
	    PREV_TRFINVAL = DBHandler.AccessHandler.getusertransfercountvalues(transferMap.get("TO_USER_NAME"), "in");
		String currinDate = DBHandler.AccessHandler.checkDateIsCurrentdate(PREV_TRFINVAL[0]);
		
		transferMap.put("INITIATION_AMOUNT", "" + O2CTransfer.generateInitiationAmount(transferMap.get("TO_CATEGORY"), false, false, 5));
		Map<String, String> InitiateMap_TC1= O2CTransfer.initiateO2CTransfer(transferMap);
		String TransactionID_TC1= InitiateMap_TC1.get("TRANSACTION_ID");		
		if (!directO2CPreference) {
			performO2CApprovalLevel1(transferMap, "5201", transferMap.get("TO_USER_NAME"));
		} else
			Log.info("Direct Operator to Channel is applicable in system");	
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITUSERTRFCOUNTO2C10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
	    int weeklyincount;
		if(currinDate.equalsIgnoreCase("true")) weeklyincount=Integer.parseInt(PREV_TRFINVAL[1]);
		else weeklyincount=0;
	   
		NEW_TRFINVAL = DBHandler.AccessHandler.getusertransfercountvalues(transferMap.get("TO_USER_NAME"), "in");
		Validator.messageCompare(NEW_TRFINVAL[2], String.valueOf(weeklyincount));
		
		tcpchange.modifyAnyTCPValue(defaultToModify, userData,"channel");  
		}*/
	
	public void performO2CApprovalLevel1(HashMap<String, String> transferMap, String messageCode, String... parservalues){
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		try{
			O2CTransfer.performingLevel1Approval(transferMap);	
		} catch(Exception e) { 
		   Log.info("Transaction is not successful");
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey(messageCode, parservalues);
			Log.info("Message fetched from WEB as : "+actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
			Assertion.completeAssertions();
		}
	}
}
