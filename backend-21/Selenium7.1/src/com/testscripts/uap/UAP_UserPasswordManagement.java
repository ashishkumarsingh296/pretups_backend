/**
 * 
 */
package com.testscripts.uap;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.AccessControlMgmt;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */


public class UAP_UserPasswordManagement extends BaseTest {

	HashMap<String, String> optresultMap;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	String MasterSheetPath;
	
	@Test(dataProvider="UserPasswordManagmentData")
	public void userPasswordMgmt(String parameter, String loginidOrmsisdn, String CategoryCode,String Remarks, String LoginID) throws InterruptedException, IOException{
		
		Log.startTestCase(this.getClass().getName());
		
		AccessControlMgmt accControlMgmt = new AccessControlMgmt(driver);
		if (TestCaseCounter == false) { 
			test = extent.createTest("[UAP]User Password Management");
			TestCaseCounter = true;
		}
		
		/*
		 * Test case 1
		 */
		currentNode=test.createNode("To verify that operator user is able to perform Send Password using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		optresultMap=accControlMgmt.userPwdMgmt_sendPassword(parameter, loginidOrmsisdn, Remarks);
		
		/*
		 * Test case 2
		 */
		currentNode=test.createNode("To verify that valid message appears once Send Password is performed using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		String sendPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.sendmsg.success", "");
		if(sendPwdMsg.equals(optresultMap.get("sendPasswordMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + sendPwdMsg + "] but found [" + optresultMap.get("sendPasswordMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

		/*
		 * Test case 3
		 */
		currentNode=test.createNode("To verify that operator user is able to perform Unblock Password using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		accControlMgmt.blockPassword(LoginID, CategoryCode);
		optresultMap=accControlMgmt.userPwdMgmt_unblockPassword(parameter, loginidOrmsisdn, Remarks);
		
		/*
		 * Test case 4
		 */
		currentNode=test.createNode("To verify that valid message appears once Unblock Password is performed using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		String unblockPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.unblocksuccess", "");
		if(unblockPwdMsg.equals(optresultMap.get("unblockPasswordMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + unblockPwdMsg + "] but found [" + optresultMap.get("unblockPasswordMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		
		/*
		 * Test case 5
		 */
		accControlMgmt.blockPassword(LoginID, CategoryCode);
		currentNode=test.createNode("To verify that operator user is able to perform 'Unblock and Send Password' using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		optresultMap=accControlMgmt.userPwdMgmt_unblockandSendPassword(parameter, loginidOrmsisdn, Remarks);
		
		/*
		 * Test case 6
		 */
		currentNode=test.createNode("To verify that valid message appears once 'Unblock and Send Password' is performed using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		String unblockSendPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.unblocksendsuccess", "");
		if(unblockSendPwdMsg.equals(optresultMap.get("unblocksendPasswordMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + unblockSendPwdMsg + "] but found [" + optresultMap.get("unblocksendPasswordMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		
		/*
		 * Test case 7
		 */
		/*currentNode=test.createNode("To verify that operator user is able to perform 'Reset Password' using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		optresultMap=accControlMgmt.userPwdMgmt_resetPassword(parameter, loginidOrmsisdn, Remarks);*/
		
		/*
		 * Test case 8 
		 */
		/*currentNode=test.createNode("To verify that valid message appears once 'Reset Password' is performed using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		String resetPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.resetsuccess", "");
		if(resetPwdMsg.equals(optresultMap.get("resetPasswordMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + resetPwdMsg + "] but found [" + optresultMap.get("resetPasswordMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}*/
		
		/*
		 * Test case to set password after reset password
		 */
	/*	String password=DBHandler.AccessHandler.fetchUserPassword(LoginID);
		String NEWPASSWORD = randGenerate.randomAlphabets(3)+"@"+randGenerate.randomNumeric(4);
		currentNode=test.createNode("To verify that user is prompted to change password once reset password is performed using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		login.LoginAsUser(driver, LoginID, password);
		chngpwd.changePassword(password, NEWPASSWORD, NEWPASSWORD);
		if (!DBHandler.AccessHandler.fetchUserPassword(LoginID).equals(password)) {
			currentNode.log(Status.PASS, "Password changed successfully");
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			ExcelUtility.setCellData(0, ExcelI.PASSWORD, 1, NEWPASSWORD);
		}
		else{
			currentNode.log(Status.FAIL, "Error occurs while changing password");
		}*/
		
		/*
		 * Test case 9
		 */
		currentNode=test.createNode("To verify that operator user is able to perform 'Cancel' operation using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		accControlMgmt.userPwdMgmt_cancel(parameter, loginidOrmsisdn, Remarks);
		
		/*
		 * Test case 10
		 */
		currentNode=test.createNode("To verify that operator user is able to perform 'Back' operation using "+parameter.toUpperCase()+".");
		currentNode.assignCategory("UAP");
		accControlMgmt.userPwdMgmt_back(parameter, loginidOrmsisdn, Remarks);
		
	}
	
	
	@DataProvider(name="UserPasswordManagmentData")
	public Object[][] userPwdmgmtdata(){
		
		MasterSheetPath=_masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		
		String LoginID=null, MSISDN=null, CategoryCode = null;
		
		for (int i = 1; i <= rowCount; i++) {
		 LoginID=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
		 MSISDN=ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
		 CategoryCode=ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
		if(LoginID!=null && !LoginID.equals(""))
			break;
		}
		
		Object[][] userdata= new Object[][]{
				{"loginid",LoginID,CategoryCode,"Automation Remarks for loginid",LoginID},
				{"msisdn",MSISDN,CategoryCode,"Automation Remarks for msisdn",LoginID}
		};
		
		return userdata;
	}
	
}
