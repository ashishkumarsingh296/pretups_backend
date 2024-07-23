package com.testscripts.uap;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;


/**
 * @author lokesh.kontey
 * This class is created to add Operator Users
 */
@ModuleManager(name =Module.UAP_OPERATOR_USER_CREATION)
public class UAP_OperatorUserCreation extends BaseTest {
	String LoginID;
	String MSISDN;
	String PassWord;
	static String homepage1;
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> optresultMap;
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider = "Domain&CategoryProvider")
	@TestManager(TestKey = "PRETUPS-381") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void operatorUserCreation(String ParentUser, String LoginUser) throws InterruptedException {
		final String methodName = "Test_operatorUserCreation";
	    Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("POPTCREATION1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SOPTCREATION1");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SOPTCREATION2");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SOPTCREATION3");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("UOPTCREATION1");
		CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("UOPTCREATION2");
		
			
		OperatorUser OperatorUserLogic = new OperatorUser(driver);
		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");
		String intOptInitiateMsg;
		/*
		 * Test Case Number 1: Operator User Initiate.
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),ParentUser,LoginUser));
		currentNode.assignCategory("UAP");
		optresultMap = OperatorUserLogic.operatorUserInitiate(ParentUser, LoginUser);
		
		/*
		 * Test Case Number 2: Message Validation
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),LoginUser));
		currentNode.assignCategory("UAP");
		if(APPLEVEL.equals("0")){
		 intOptInitiateMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessage", optresultMap.get("UserName"));
		}else{
		 intOptInitiateMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessageforrequest", optresultMap.get("UserName"));
		}
		Assertion.assertEquals(optresultMap.get("initiateMsg"),intOptInitiateMsg);
		
		/*
		 * Test Case Number 3: Operator User Approval
		 */
		if(APPLEVEL.equals("1")){
			currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(),ParentUser,LoginUser));
			currentNode.assignCategory("UAP");
			optresultMap=OperatorUserLogic.approveUser(ParentUser);
		/*
		 * Test Case Number 4: Operator User Approval message validation
		 */	
			currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(),LoginUser));
			currentNode.assignCategory("UAP");
			String intOptApproveMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.approveuccessmessage", optresultMap.get("UserName"));
			Assertion.assertEquals(optresultMap.get("approveMsg"),intOptApproveMsg);
		}
		else{
			Log.info("Approval is not required.");
		}
		
		/*
		 * Test Case: Modify Operator user details
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(),ParentUser,LoginUser));
		currentNode.assignCategory("UAP");
		String actualMessage = OperatorUserLogic.modifyOperatorDetails(ParentUser, LoginUser);
		String expectedMessage = MessagesDAO.prepareMessageByKey("user.addoperatoruser.updatesuccessmessage", optresultMap.get("UserName"));
		Assertion.assertEquals(actualMessage, expectedMessage);
		/*
		 * Test Case: View Operator Details
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(),ParentUser,LoginUser));
		currentNode.assignCategory("UAP");
		OperatorUserLogic.viewOperatorUser(ParentUser, LoginUser);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
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