package com.testscripts.sit;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name=Module.SIT_Add_Channel_User)
public class SIT_AddChannelUser  extends BaseTest{
	
	
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-1848") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void Test_01_suspendedChannelUserMsisdn(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks) throws InterruptedException, IOException{

		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		String expectedMessage = null;
		
		final String methodName = "Test_suspendedChannelUser";
        Log.startTestCase(methodName);
				
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SITSUSRESCHNLUSR1");
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		String actualMessage = suspendChnluser.suspendChannelUser_MSISDN(msisdn, Remarks);
		expectedMessage = MessagesDAO.prepareMessageByKey("pretups.user.deletesuspendchanneluser.suspendrequestsuccessmessage",userName);
		assertEquals(actualMessage, expectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-1849") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void Test_02_approveSuspendedChannelUser(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks) throws InterruptedException, IOException{

		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		String expectedMessage = null;
		
		final String methodName = "Test_suspendedChannelUser";
        Log.startTestCase(methodName);
				
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SITSUSRESCHNLUSR2");
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		String actualMessage = suspendChnluser.approveCSuspendRequest_MSISDN(msisdn, Remarks);
		expectedMessage = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		
		assertEquals(actualMessage, expectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-1850") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void Test_03_resumeSuspendedChannelUser(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks) throws InterruptedException, IOException{
		
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChannelUser = new ResumeChannelUser(driver);
		String expectedMessage = null;
		
		final String methodName = "Test_suspendedChannelUser";
        Log.startTestCase(methodName);
				
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SITSUSRESCHNLUSR3");
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		String actualMessage = resumeChannelUser.resumeChannelUser_MSISDN(msisdn, Remarks);
		expectedMessage = MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage","");
		
		assertEquals(actualMessage, expectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-1851") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void Test_04_RejectedSuspendedChannelUser(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks) throws InterruptedException, IOException{

		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		String expectedMessage = null;
		
		final String methodName = "Test_suspendedChannelUser";
        Log.startTestCase(methodName);
				
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SITSUSRESCHNLUSR4");
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		suspendChnluser.suspendChannelUser_MSISDN(msisdn, Remarks);
//		suspendChnluser.approveCSuspendRequest_MSISDN(msisdn, Remarks);
		String actualMessage = suspendChnluser.rejectCSuspendRequest_MSISDN(msisdn, Remarks);
		expectedMessage = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.rejectedsuccessmessage","");
		
		assertEquals(actualMessage, expectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@DataProvider(name="suspendChannelData")
	public Object[][] suspendchannelData(){
		
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 2);
		String UserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, 2);
		String MSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, 2);
		String DOMAIN = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 2);
		String CATEGORY = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME,2);
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		String GEOGRAPHY = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		String Remarks = "Automation Remarks";
		
		Object[][] paramData= new Object[][]{
				{LoginID,UserName,MSISDN,DOMAIN,CATEGORY,GEOGRAPHY,Remarks}
		};
		return paramData;
	}
}
