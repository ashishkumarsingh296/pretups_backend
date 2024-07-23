/**
 * 
 */
package com.testscripts.uap;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author lokesh.kontey
 *
 */
@ModuleManager(name=Module.UAP_SUSPEND_RESUME_CHANNEL_USER)
public class UAP_SuspendResumeChannelUser extends BaseTest {
	static boolean TestCaseCounter = false;
	static String SuspendApprovalReq;
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-403") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _01_suspendedChannelUser(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks) throws InterruptedException, IOException{

		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);
		
		SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();
		String expectedMessage = null;
		
		final String methodName = "Test_suspendedChannelUser";
        Log.startTestCase(methodName);
				
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR3");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR4");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR5");
		CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR6");
		CaseMaster CaseMaster7 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR7");
		CaseMaster CaseMaster8 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR8");
		CaseMaster CaseMaster9 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR9");
		CaseMaster CaseMaster10 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR10");
		CaseMaster CaseMaster11 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR11");
		CaseMaster CaseMaster12 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR12");
		CaseMaster CaseMaster13 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR13");
		CaseMaster CaseMaster14 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR14");
		CaseMaster CaseMaster15 = _masterVO.getCaseMasterByID("USUSRESCHNLUSR15");
		
		/*
		 * Test case 1-6
		 */
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("UAP");
		String actualMessage = suspendChnluser.suspendChannelUser_LoginID(loginid,Remarks);
		
		//Message validation
		currentNode=test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("UAP");
		if(SuspendApprovalReq.equals("TRUE"))
		{expectedMessage = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendrequestsuccessmessage",userName);}
		else
		{expectedMessage = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendsuccessmessage",userName);
		}
		Assertion.assertEquals(actualMessage, expectedMessage);
		//suspendChnluser.messageCompare(actualMessage, expectedMessage);
		
		
		if(SuspendApprovalReq.equals("TRUE"))
		{
		currentNode=test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory("UAP");
		actualMessage = suspendChnluser.approveCSuspendRequest_LoginID(loginid, Remarks);
		
		//Message Validation
		currentNode=test.createNode(CaseMaster4.getExtentCase());
		currentNode.assignCategory("UAP");
		expectedMessage = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		Assertion.assertEquals(actualMessage, expectedMessage);
		//suspendChnluser.messageCompare(actualMessage, expectedMessage);
		}
		
		currentNode=test.createNode(CaseMaster5.getExtentCase());
		currentNode.assignCategory("UAP");
		actualMessage=resumeChnluser.resumeChannelUser_LoginID(loginid, msisdn, Remarks);
		
		//Message Validation
		currentNode=test.createNode(CaseMaster6.getExtentCase());
		currentNode.assignCategory("UAP");
		expectedMessage= MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		Assertion.assertEquals(actualMessage, expectedMessage);
		//suspendChnluser.messageCompare(actualMessage, expectedMessage);
		
		/*
		 * Test case 7-9
		 */
		currentNode=test.createNode(CaseMaster7.getExtentCase());
		currentNode.assignCategory("UAP");
		suspendChnluser.suspendChannelUser_MSISDN(msisdn, Remarks);
		
		if(SuspendApprovalReq.equals("TRUE")){
		currentNode=test.createNode(CaseMaster8.getExtentCase());
		currentNode.assignCategory("UAP");
		suspendChnluser.approveCSuspendRequest_MSISDN(msisdn, Remarks);}
		
		currentNode=test.createNode(CaseMaster9.getExtentCase());
		currentNode.assignCategory("UAP");
		resumeChnluser.resumeChannelUser_MSISDN(msisdn, Remarks);
		
		
		/*
		 * Test case 10-12
		 */
		currentNode=test.createNode(CaseMaster10.getExtentCase());
		currentNode.assignCategory("UAP");
		suspendChnluser.suspendChannelUser_GeoDetails(loginid, domain, category, geography, Remarks);
		
		if(SuspendApprovalReq.equals("TRUE")){
		currentNode=test.createNode(CaseMaster11.getExtentCase());
		currentNode.assignCategory("UAP");
		suspendChnluser.approveCSuspendRequest_LoginID(loginid, Remarks);}
		
		currentNode=test.createNode(CaseMaster12.getExtentCase());
		currentNode.assignCategory("UAP");
		resumeChnluser.resumeChannelUser_GeoDetails(loginid, domain, category, geography, Remarks, msisdn);
		
		/*
		 * Test case 13-15
		 */
		if(SuspendApprovalReq.equals("TRUE")){
		currentNode=test.createNode(CaseMaster13.getExtentCase());
		currentNode.assignCategory("UAP");
		suspendChnluser.suspendChannelUser_LoginID(loginid,Remarks);
		actualMessage = suspendChnluser.discardCSuspendRequest_LoginID(loginid, Remarks);
		
		//Message Validation
		currentNode=test.createNode(CaseMaster14.getExtentCase());
		currentNode.assignCategory("UAP");
		expectedMessage = MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumenotsuccessmessage", "");
		Assertion.assertEquals(actualMessage, expectedMessage);
		//suspendChnluser.messageCompare(actualMessage, expectedMessage);
		
		currentNode=test.createNode(CaseMaster15.getExtentCase());
		currentNode.assignCategory("UAP");
		suspendChnluser.rejectCSuspendRequest_LoginID(loginid, Remarks);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-404") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _02_suspendedChannelUserNoRemarks(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks){
	
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		Remarks="";
		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR16").getExtentCase());
		currentNode.assignCategory("UAP");
		String expected = MessagesDAO.getLabelByKey("pretups.user.deletesuspend.error.remarkrequired");
		String actual = null;
		
		String preference = DBHandler.AccessHandler.getSystemPreference(CONSTANT.USER_EVENT_REMARKS);
		if(preference.equalsIgnoreCase("TRUE")) {
		
		try{
		suspendChnluser.suspendChannelUser_MSISDN(msisdn, Remarks);}
		catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		Assertion.assertEquals(actual, expected);}
		else {
			Assertion.assertSkip("Remarks field is not mandatory so this case is skipped");
		}
	}
	
	@Test(dataProvider="suspendViaChannelUser")
	@TestManager(TestKey = "PRETUPS-405") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _03_suspendUserviaChannelUSer(String LoginID, String Password, String childMSISDN, String childUserName){
	
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR17").getExtentCase());
		currentNode.assignCategory("UAP");
		ExtentI.Markup(ExtentColor.GREEN, "Suspending user via Channel User");
		if(BTSLUtil.isNullString(SuspendApprovalReq))
		{SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();}
			
		String actual = null, expected=null,remarks = "Automation remarks";
		if(SuspendApprovalReq.equals("TRUE"))
		{expected= MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendrequestsuccessmessage",childUserName);}
		else
		{expected = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendsuccessmessage",childUserName);
		}

		actual = suspendChnluser.suspendChannelUser_MSISDN(LoginID,Password, childMSISDN,remarks);
		Assertion.assertEquals(actual, expected);
		
		if(SuspendApprovalReq.equals("TRUE"))
		{ExtentI.Markup(ExtentColor.GREEN, "Suspend user approval");
		actual = suspendChnluser.approveCSuspendRequest_MSISDN(childMSISDN, remarks);
   		expected = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		Assertion.assertEquals(actual, expected);}
		
		ExtentI.Markup(ExtentColor.GREEN, "Resume the suspended user");
		actual=resumeChnluser.resumeChannelUser_MSISDN(childMSISDN, remarks);
		expected= MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		Assertion.assertEquals(actual, expected);	
		
	}
	
	@Test(dataProvider="suspendViaChannelUser")
	@TestManager(TestKey = "PRETUPS-406") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _04_suspendUserNoRemarksviaChannelUSer(String LoginID, String Password, String childMSISDN, String childUserName){
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR18").getExtentCase());
		currentNode.assignCategory("UAP");
		
		String actual = null, expected=null,remarks = "";
		String preference = DBHandler.AccessHandler.getSystemPreference(CONSTANT.USER_EVENT_REMARKS);
		if(preference.equalsIgnoreCase("TRUE")) {
		try{suspendChnluser.suspendChannelUser_MSISDN(LoginID,Password,childMSISDN,remarks);}
		catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
	    expected = MessagesDAO.getLabelByKey("pretups.user.deletesuspend.error.remarkrequired");
		
		Assertion.assertEquals(actual, expected);
		}
		else {
			Assertion.assertSkip("Remarks field is not mandatory so this case is skipped");
		}
	}
	
	@Test(dataProvider="suspendViaChannelUser")
	@TestManager(TestKey = "PRETUPS-407") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _05_resumeUserNoRemarks(String LoginID, String Password, String childMSISDN, String childUserName){
	
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR19").getExtentCase());
		currentNode.assignCategory("UAP");
		ExtentI.Markup(ExtentColor.GREEN, "Suspending user via Channel User");
		if(BTSLUtil.isNullString(SuspendApprovalReq))
		{SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();}
			
		String actual = null, expected=null,remarks = "Automation remarks";
		if(SuspendApprovalReq.equals("TRUE"))
		{expected= MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendrequestsuccessmessage",childUserName);}
		else
		{expected = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendsuccessmessage",childUserName);
		}

		actual = suspendChnluser.suspendChannelUser_MSISDN(LoginID,Password, childMSISDN,remarks);
		Assertion.assertEquals(actual, expected);
		
		if(SuspendApprovalReq.equals("TRUE"))
		{ExtentI.Markup(ExtentColor.GREEN, "Suspend user approval");
		actual = suspendChnluser.approveCSuspendRequest_MSISDN(childMSISDN, remarks);
   		expected = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		Assertion.assertEquals(actual, expected);}
		
		ExtentI.Markup(ExtentColor.GREEN, "Trying to Resume the suspended user without remarks");
		String preference = DBHandler.AccessHandler.getSystemPreference(CONSTANT.USER_EVENT_REMARKS);
		if(preference.equalsIgnoreCase("TRUE")) {
		try{resumeChnluser.resumeChannelUser_MSISDN(childMSISDN, "");}
		catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		expected= MessagesDAO.prepareMessageByKey("pretups.user.deletesuspend.error.remarkrequired", "");
		Assertion.assertEquals(actual, expected);
		ExtentI.Markup(ExtentColor.GREEN, "Resume the suspended user with remarks");
		actual=resumeChnluser.resumeChannelUser_MSISDN(childMSISDN, remarks);
		expected= MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		Assertion.assertEquals(actual, expected);
		}
		else {
		
		ExtentI.Markup(ExtentColor.GREEN, "Resume the suspended user with remarks");
		actual=resumeChnluser.resumeChannelUser_MSISDN(childMSISDN, remarks);
		expected= MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		Assertion.assertEquals(actual, expected);
		}
	}
	
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-408") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _06_userRemarksSetFalseOperatorUSer(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks){
	
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);
		
		Remarks=""; String expected = null, actual = null;
		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR20").getExtentCase());
		currentNode.assignCategory("UAP");
		ExtentI.Markup(ExtentColor.GREEN, "Set User Event Remarks to False");
		suspendChnluser.modifyPreference(CONSTANT.USER_EVENT_REMARKS,"FALSE");
		ExtentI.Markup(ExtentColor.GREEN, "Suspending user via Operator User");
		actual = suspendChnluser.suspendChannelUser_MSISDN(msisdn, Remarks);
		
		if(SuspendApprovalReq.equals("TRUE"))
		{expected= MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendrequestsuccessmessage",userName);}
		else
		{expected = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendsuccessmessage",userName);
		}
		Assertion.assertEquals(actual, expected);
		
		if(SuspendApprovalReq.equals("TRUE")){
		actual = suspendChnluser.approveCSuspendRequest_MSISDN(msisdn, Remarks);
		expected = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		Assertion.assertEquals(actual, expected);
		}
		
		actual = resumeChnluser.resumeChannelUser_MSISDN(msisdn, Remarks);
		expected = MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		Assertion.assertEquals(actual, expected);
				
		ExtentI.Markup(ExtentColor.GREEN, "Set User Event Remarks to True");
		suspendChnluser.modifyPreference(CONSTANT.USER_EVENT_REMARKS,"TRUE");
		
	}
	
	
	@Test(dataProvider="suspendViaChannelUser")
	@TestManager(TestKey = "PRETUPS-409") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _07_userRemarksSetFalseChannelUser(String LoginID, String Password, String childMSISDN, String childUserName){
	
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR21").getExtentCase());
		currentNode.assignCategory("UAP");
		String preference = DBHandler.AccessHandler.getSystemPreference(CONSTANT.USER_EVENT_REMARKS);
		boolean flag=false;
		if(preference.equalsIgnoreCase("TRUE")) {
		ExtentI.Markup(ExtentColor.GREEN, "Set User Event Remarks to False");
		suspendChnluser.modifyPreference(CONSTANT.USER_EVENT_REMARKS,"FALSE");
		flag=true;
		}
		ExtentI.Markup(ExtentColor.GREEN, "Suspending user via Channel User");
		if(BTSLUtil.isNullString(SuspendApprovalReq))
		{SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();}
			
		String actual = null, expected=null,remarks = "";
		if(SuspendApprovalReq.equals("TRUE"))
		{expected= MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendrequestsuccessmessage",childUserName);}
		else
		{expected = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendsuccessmessage",childUserName);
		}

		actual = suspendChnluser.suspendChannelUser_MSISDN(LoginID,Password, childMSISDN,remarks);
		Assertion.assertEquals(actual, expected);
		
		if(SuspendApprovalReq.equals("TRUE"))
		{ExtentI.Markup(ExtentColor.GREEN, "Suspend user approval");
		actual = suspendChnluser.approveCSuspendRequest_MSISDN(childMSISDN, remarks);
   		expected = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		Assertion.assertEquals(actual, expected);}
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR22").getExtentCase());
		currentNode.assignCategory("UAP");
		ExtentI.Markup(ExtentColor.GREEN, "Resume the suspended user without remarks");
		actual=resumeChnluser.resumeChannelUser_MSISDN(childMSISDN, remarks);
		expected= MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		Assertion.assertEquals(actual, expected);
		if(flag) {
		ExtentI.Markup(ExtentColor.GREEN, "Set User Event Remarks to True");
		suspendChnluser.modifyPreference(CONSTANT.USER_EVENT_REMARKS,"TRUE");
		}
	}
	
	@Test(dataProvider="suspendChannelData")
	@TestManager(TestKey = "PRETUPS-410") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void _08_suspendedChannelUserNoRemarksApproval(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks){
		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("USUSRESCHNLUSR23").getExtentCase());
		currentNode.assignCategory("UAP");
		String actual = null, expected = null, remarks = "";
		if(SuspendApprovalReq.equals("TRUE")){
		expected= MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendrequestsuccessmessage",userName);
		actual = suspendChnluser.suspendChannelUser_MSISDN(msisdn,Remarks);
		Assertion.assertEquals(actual, expected);
		
		remarks="";
		ExtentI.Markup(ExtentColor.GREEN, "Suspend user approval without remarks.");
		try{
		actual = suspendChnluser.approveCSuspendRequest_MSISDN(msisdn, remarks);}
		catch(Exception e){actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		expected = MessagesDAO.getLabelByKey("pretups.user.deletesuspend.error.remarkrequired");
		Assertion.assertEquals(actual, expected);
		
		ExtentI.Markup(ExtentColor.GREEN, "Suspend user approval");
		actual = suspendChnluser.approveCSuspendRequest_MSISDN(msisdn, Remarks);
		expected = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		Assertion.assertEquals(actual, expected);
		
		ExtentI.Markup(ExtentColor.GREEN, "Resume the suspended user");
		actual=resumeChnluser.resumeChannelUser_MSISDN(msisdn, Remarks);
		expected= MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		Assertion.assertEquals(actual, expected);}
		else {Assertion.assertSkip("Case skipped as approval is not required for Suspending user.");}
		
	}
	
	
	@DataProvider(name="suspendChannelData")
	public Object[][] suspendchannelData(){
		
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1);
		String UserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, 1);
		String MSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, 1);
		String DOMAIN = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		String CATEGORY = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME,1);
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		String GEOGRAPHY = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		String Remarks = "Automation Remarks";
		
		Object[][] paramData= new Object[][]{
				{LoginID,UserName,MSISDN,DOMAIN,CATEGORY,GEOGRAPHY,Remarks}
		};
		return paramData;
	}
	
	@DataProvider(name="suspendViaChannelUser")
	public Object[][] suspendViaChannelUser(){
		int rowCount = ExcelUtility.getRowCount(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String LoginID =null, Password=null, Category=null, childMSISDN=null, childUserName=null;
		for(int i=1;i<=rowCount;i++){
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			Category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if(CommonUtils.roleCodeExistInLinkSheet(RolesI.SUSPEND_CHANNEL_USER_ROLECODE,Category))
			{LoginID = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.LOGIN_ID, i);
			Password  = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, i);

			childMSISDN = ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, new String[]{ExcelI.PARENT_CATEGORY_NAME}, new String[]{Category});
			childUserName = ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, new String[]{ExcelI.MSISDN}, new String[]{childMSISDN});
			break;}
		}
		Object[][] paramData= new Object[][]{
				{LoginID,Password,childMSISDN, childUserName}
		};
		
		return paramData;
	}
}
