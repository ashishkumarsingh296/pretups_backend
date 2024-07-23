/**
 * 
 */
package com.testscripts.uap;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.AccessControlMgmt;
import com.Features.ChangeChannelUserPIN;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.loginpages.ChangePasswordForNewUser;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author lokesh.kontey
 *
 */

@ModuleManager(name = Module.UAP_USER_PASSWORD_MANAGEMENT)
public class UAP_UserPasswordManagement extends BaseTest {

	HashMap<String, String> optresultMap;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	String MasterSheetPath;
	
	@Test(dataProvider="UserPasswordManagmentData")
	@TestManager(TestKey = "PRETUPS-407")
	public void userPasswordMgmt(String parameter, String loginidOrmsisdn, String CategoryCode,String Remarks, String LoginID) throws InterruptedException, IOException{
		
		final String methodName = "Test_userPasswordMgmt";
        Log.startTestCase(methodName);
				
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT3");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT4");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT5");
		CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT6");
		CaseMaster CaseMaster7 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT7");
		CaseMaster CaseMaster8 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT8");
		CaseMaster CaseMaster9 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT9");
		CaseMaster CaseMaster10 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT10");
		CaseMaster CaseMaster11 = _masterVO.getCaseMasterByID("UUSRPSWDMGMT11");		
		
		AccessControlMgmt accControlMgmt = new AccessControlMgmt(driver);
				
		String value = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue("Network Code"), CONSTANT.DISSABLE_BUTTON_LIST);
		List<String> disableBtn = Arrays.asList(value.split(",[ ]*"));
		/*
		 * Test case 1
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		if(!DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("SHA")){
		if(disableBtn.contains("SEND")){
			Assertion.assertSkip("'Send Password' button is disabled in the system, hence the case is skipped.");
			}
		else{Log.info("sendPassword exist");
			optresultMap=accControlMgmt.userPwdMgmt_sendPassword(parameter, loginidOrmsisdn, Remarks);
		
		
		/*
		 * Test case 2
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		String sendPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.sendmsg.success", "");
		Assertion.assertEquals(optresultMap.get("sendPasswordMsg"), sendPwdMsg);
		}}else{Log.skip("Case Skipped as in SHA type encryption Send Password is not allowed.");}
		/*
		 * Test case 3
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		if(disableBtn.contains("UNBLOCK")){
			Assertion.assertSkip("'Unblock Password' button is disabled in the system, hence the case is skipped.");
			}
		else{Log.info("Unblock Password button exist");
		accControlMgmt.blockPassword(LoginID, CategoryCode);
		optresultMap=accControlMgmt.userPwdMgmt_unblockPassword(parameter, loginidOrmsisdn, Remarks);

		/*
		 * Test case 4
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		String unblockPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.unblocksuccess", "");
		Assertion.assertEquals(optresultMap.get("unblockPasswordMsg"), unblockPwdMsg);
		}
		/*
		 * Test case 5
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		if(!DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("SHA")){
		if(disableBtn.contains("UNBLOCK_SEND")){
			Assertion.assertSkip("'Unblock and Send Password' button is disabled in the system, hence the case is skipped.");
			}
		else{Log.info("Unblock Password button exist");
		accControlMgmt.blockPassword(LoginID, CategoryCode);
		optresultMap=accControlMgmt.userPwdMgmt_unblockandSendPassword(parameter, loginidOrmsisdn, Remarks);
		
		/*
		 * Test case 6
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		String unblockSendPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.unblocksendsuccess", "");
		Assertion.assertEquals(optresultMap.get("unblocksendPasswordMsg"), unblockSendPwdMsg);
		}}
		else{Log.skip("Case Skipped as in SHA type encryption Send Password is not allowed.");}
		/*
		 * Test case 7
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster7.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		optresultMap=accControlMgmt.userPwdMgmt_resetPassword(parameter, loginidOrmsisdn, Remarks);
		
		/*
		 * Test case 8 
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster8.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		String resetPwdMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpassword.msg.resetsuccess", "");
		Assertion.assertEquals(optresultMap.get("resetPasswordMsg"), resetPwdMsg);
		
		/*
		 * Test case to set password after reset password
		 */
		Login login = new Login();
		ChangePasswordForNewUser chngpwd = new ChangePasswordForNewUser(driver);
		String password=DBHandler.AccessHandler.fetchUserPassword(LoginID);
		//String NEWPASSWORD = randGenerate.randomAlphabets(3).toUpperCase()+"@"+ ChangeChannelUserPIN.isSMSPinValid();
		CONSTANT.CHANGING_PASSWORD = _masterVO.getProperty("ResetPassword").substring(0, 4) + ChangeChannelUserPIN.isSMSPinValid();
		String RESETPASSWORD = CONSTANT.CHANGING_PASSWORD;
		currentNode=test.createNode(MessageFormat.format(CaseMaster9.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		login.LoginAsUser(driver, LoginID, password);
		chngpwd.changePassword(password, RESETPASSWORD, RESETPASSWORD);
		
		if (!DBHandler.AccessHandler.fetchUserPassword(LoginID).equals(password)) {
			currentNode.log(Status.PASS, "Password changed successfully");
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			ExcelUtility.setCellData(0, ExcelI.PASSWORD, 1, RESETPASSWORD);
		}
		else{
			Assertion.assertFail("Error occurs while changing password");			
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();
		}
		
		/*
		 * Test case 9
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster10.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		accControlMgmt.userPwdMgmt_cancel(parameter, loginidOrmsisdn, Remarks);
		
		/*
		 * Test case 10
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster11.getExtentCase(), parameter.toUpperCase()));
		currentNode.assignCategory("UAP");
		accControlMgmt.userPwdMgmt_back(parameter, loginidOrmsisdn, Remarks);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
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
