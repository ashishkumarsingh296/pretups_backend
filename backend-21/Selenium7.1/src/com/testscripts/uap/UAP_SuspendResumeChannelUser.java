/**
 * 
 */
package com.testscripts.uap;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class UAP_SuspendResumeChannelUser extends BaseTest {
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider="suspendChannelData")
	public void suspendedChannelUser(String loginid,String userName, String msisdn, String domain, String category, String geography, String Remarks) throws InterruptedException, IOException{

		SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);
		
		String SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();
		String expectedMessage = null;
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[UAP]Suspend/Resume Channel User");
			TestCaseCounter = true;
		}
		
		/*
		 * Test case 1-6
		 */
		currentNode=test.createNode("To verify that operator user is able to perform Suspend Channel User using LoginID of channel user.");
		currentNode.assignCategory("UAP");
		String actualMessage = suspendChnluser.suspendChannelUser_LoginID(loginid,Remarks);
		
		//Message validation
		currentNode=test.createNode("To verify that proper message appear on Web after performing Suspend Channel User using LoginID of channel user.");
		currentNode.assignCategory("UAP");
		if(SuspendApprovalReq.equals("TRUE"))
		{expectedMessage = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendrequestsuccessmessage",userName);}
		else
		{expectedMessage = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.suspendsuccessmessage",userName);}
		suspendChnluser.messageCompare(actualMessage, expectedMessage);
		
		
		if(SuspendApprovalReq.equals("TRUE"))
		{
		currentNode=test.createNode("To verify that operator user is able to approve suspend channel user request using LoginID.");
		currentNode.assignCategory("UAP");
		actualMessage = suspendChnluser.approveCSuspendRequest_LoginID(loginid, Remarks);
		
		//Message Validation
		currentNode=test.createNode("To verify that proper message appear on Web after approving suspend channel user request.");
		currentNode.assignCategory("UAP");
		expectedMessage = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage","");
		suspendChnluser.messageCompare(actualMessage, expectedMessage);
		}
		
		currentNode=test.createNode("To verify that operator user is able to perform resume channel user using loginID.");
		currentNode.assignCategory("UAP");
		actualMessage=resumeChnluser.resumeChannelUser_LoginID(loginid, msisdn, Remarks);
		
		//Message Validation
		currentNode=test.createNode("To verify that proper message appear on performing resume channel user.");
		currentNode.assignCategory("UAP");
		expectedMessage= MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumesuccessmessage", "");
		suspendChnluser.messageCompare(actualMessage, expectedMessage);
		
		/*
		 * Test case 7-9
		 */
		currentNode=test.createNode("To verify that operator user is able to perform Suspend Channel User using MSISDN of channel user.");
		currentNode.assignCategory("UAP");
		suspendChnluser.suspendChannelUser_MSISDN(msisdn, Remarks);
		
		if(SuspendApprovalReq.equals("TRUE")){
		currentNode=test.createNode("To verify that operator user is able to approve suspend channel user request using MSISDN.");
		currentNode.assignCategory("UAP");
		suspendChnluser.approveCSuspendRequest_MSISDN(msisdn, Remarks);}
		
		currentNode=test.createNode("To verify that operator user is able to perform resume channel user using MSISDN.");
		currentNode.assignCategory("UAP");
		resumeChnluser.resumeChannelUser_MSISDN(msisdn, Remarks);
		
		
		/*
		 * Test case 10-12
		 */
		currentNode=test.createNode("To verify that operator user is able to perform Suspend Channel User using geographical details of channel user.");
		currentNode.assignCategory("UAP");
		suspendChnluser.suspendChannelUser_GeoDetails(loginid, domain, category, geography, Remarks);
		
		if(SuspendApprovalReq.equals("TRUE")){
		currentNode=test.createNode("To verify that operator user is able to approve suspend channel user request using loginID.");
		currentNode.assignCategory("UAP");
		suspendChnluser.approveCSuspendRequest_LoginID(loginid, Remarks);}
		
		currentNode=test.createNode("To verify that operator user is able to perform resume channel user using geography details.");
		currentNode.assignCategory("UAP");
		resumeChnluser.resumeChannelUser_GeoDetails(loginid, domain, category, geography, Remarks, msisdn);
		
		/*
		 * Test case 13-15
		 */
		if(SuspendApprovalReq.equals("TRUE")){
		currentNode=test.createNode("To verify that operator user is able to discard suspend channel user request.");
		currentNode.assignCategory("UAP");
		suspendChnluser.suspendChannelUser_LoginID(loginid,Remarks);
		actualMessage = suspendChnluser.discardCSuspendRequest_LoginID(loginid, Remarks);
		
		//Message Validation
		currentNode=test.createNode("To verify that proper message appears on Web after discarding suspend channel user request.");
		currentNode.assignCategory("UAP");
		expectedMessage = MessagesDAO.prepareMessageByKey("user.resumechanneluserslistview.resumenotsuccessmessage", "");
		suspendChnluser.messageCompare(actualMessage, expectedMessage);
		
		currentNode=test.createNode("To verify that operator user is able to reject suspend channel user request.");
		currentNode.assignCategory("UAP");
		suspendChnluser.rejectCSuspendRequest_LoginID(loginid, Remarks);
		}
		Log.endTestCase(this.getClass().getName());
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
}
