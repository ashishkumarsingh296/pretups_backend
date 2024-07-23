package com.testscripts.smoke;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;


/**
 * @author lokesh.kontey
 * This class is created to add Operator Users
 */
public class Smoke_OperatorUserCreation extends BaseTest {
	String LoginID;
	String MSISDN;
	String PassWord;
	static String homepage1;
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> optresultMap;
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider = "Domain&CategoryProvider")
	public void operatorUserCreation(String ParentUser, String LoginUser) throws InterruptedException {
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
			test = extent.createTest("[Smoke]Operator User Creation");
			TestCaseCounter = true;
		}
		
		OperatorUser OperatorUserLogic = new OperatorUser(driver);
		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");
		String intOptInitiateMsg;
		/*
		 * Test Case Number 1: Operator User Initiate.
		 */
		currentNode=test.createNode("To verify that " + ParentUser + " is able to initiate " + LoginUser+".");
		currentNode.assignCategory("Smoke");
		optresultMap = OperatorUserLogic.operatorUserInitiate(ParentUser, LoginUser);
		
		/*
		 * Test Case Number 2: Message Validation
		 */
		currentNode=test.createNode("To verify that valid message is displayed after initiating "+LoginUser+" .");
		currentNode.assignCategory("Smoke");
		
		if(APPLEVEL.equals("0")){
		 intOptInitiateMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessage", optresultMap.get("UserName"));
		}else{
		 intOptInitiateMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessageforrequest", optresultMap.get("UserName"));
		}
		if (optresultMap.get("initiateMsg").equals(intOptInitiateMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + intOptInitiateMsg + "] but found [" + optresultMap.get("initiateMsg") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		
		/*
		 * Test Case Number 3: Operator User Approval
		 */
		

		if(APPLEVEL.equals("1")){
			currentNode=test.createNode("To verify that " + ParentUser + " is able to approve " + LoginUser+".");
			currentNode.assignCategory("Smoke");
			optresultMap=OperatorUserLogic.approveUser(ParentUser);
			/*
			 * Test Case Number 4: Operator User Approval message validation
			 */	
			currentNode=test.createNode("To verify that valid message is displayed after approval of "+LoginUser+" .");
			currentNode.assignCategory("Smoke");
			String intOptApproveMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.approveuccessmessage", optresultMap.get("UserName"));
			
			
		if (optresultMap.get("approveMsg").equals(intOptApproveMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + intOptApproveMsg + "] but found [" + optresultMap.get("approveMsg") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}}
		else{
			Log.info("Approval is not required.");
		}
		
		/*
		 * Test Case Number 5: Operator user Password Change
		 */
		currentNode=test.createNode("To verify that newly created " + LoginUser + " is prompted to change password after successful login for the first attempt and also able to change password.");
		currentNode.assignCategory("Smoke");
		OperatorUserLogic.changeUserFirstTimePassword();
		
		/*
		 * Test Case Number 6: Operator user PIN Change
		 */
		
		if(OperatorUserLogic.pinChangeRequired.equals("true")){
		currentNode=test.createNode("To verify that newly created " + LoginUser + " is able to change pin.");
		currentNode.assignCategory("Smoke");
		OperatorUserLogic.changeUserFirstTimePIN();
		}
		else{
		Log.info("PIN required : "+OperatorUserLogic.pinChangeRequired);	
		}
		
		
		Log.endTestCase(this.getClass().getName());
	}
	
	/**
	 * 
	 * @return categoryData
	 * @throws IOException
	 */

	@DataProvider(name = "Domain&CategoryProvider")
	public Object[][] DomainCategoryProvider() throws IOException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		
		Object[][] categoryData = new Object[rowCount-1][2];
		int j=0;
		for (int i = 2; i <=rowCount; i++)
		{
				categoryData[j][0] = ExcelUtility.getCellData(0,ExcelI.PARENT_NAME, i);
				categoryData[j][1] = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, i);
				j++;		
		}
		
		return categoryData;
	}
	
}