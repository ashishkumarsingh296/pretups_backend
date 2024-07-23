package com.testscripts.prerequisites;


import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;

import com.Features.OperatorUser;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

/*
 * @author PVG
 * This class is created to add Operator Users
 */

public class PreRequisite_OperatorUserCreation extends BaseTest {
	String LoginID;
	String MSISDN;
	String PassWord;
	static String homepage1;
	static HashMap<String, String> map, map1 = null;
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "Domain&CategoryProvider")
	public void operatorUserCreation(int RowNum, String ParentUser, String LoginUser) throws InterruptedException {
		
		// Pushing Test Case start to Logger
		Log.startTestCase(this.getClass().getName());
		
		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test=extent.createTest("[Pre-Requisite]Operator User Creation");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case - To Create Operator Users as per the Operator Users Hierarchy Sheet
		 */
		currentNode=test.createNode("To verify that " + ParentUser + " is able to create " + LoginUser);
		currentNode.assignCategory("Pre-Requisite");

		OperatorUser OperatorUserLogic = new OperatorUser(driver);
		
		OperatorUserLogic.operatorUserInitiate(ParentUser, LoginUser);
		OperatorUserLogic.approveUser(ParentUser);
		OperatorUserLogic.writeOperatorUserData(RowNum);
		
		currentNode=test.createNode("To verify that newly created " + LoginUser + " is prompted to change password after successful login for the first attempt and also able to change password.");
		currentNode.assignCategory("Pre-Requisite");
		OperatorUserLogic.changeUserFirstTimePassword();
		OperatorUserLogic.writeOperatorUserData(RowNum);
		
		if(OperatorUserLogic.pinChangeRequired.equals("true")){
			currentNode=test.createNode("To verify that newly created " + LoginUser + " is able to change pin.");
			currentNode.assignCategory("Pre-Requisite");
			OperatorUserLogic.changeUserFirstTimePIN();
			}
		else{
			Log.info("Pin Change is not required.");
		}

		OperatorUserLogic.writeOperatorUserData(RowNum);
		
		Log.endTestCase(this.getClass().getName());
	}

	@DataProvider(name = "Domain&CategoryProvider")
	public Object[][] DomainCategoryProvider() throws IOException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int counter=0;
		
		for (int i = 2; i <=rowCount; i++) {
			String ParentCode=ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, i);
			String CategoryCode=ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			if (!(ParentCode+CategoryCode).equals(PretupsI.SUPERADMIN_CATCODE+PretupsI.NETWORKADMIN_CATCODE)) {
					counter++;
			}
			}
		Object[][] categoryData = new Object[counter][3];
		int j=0;
		for (int i = 2; i <=rowCount; i++)
		{
				String ParentCode=ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, i);
				String CategoryCode=ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
				if (!(ParentCode+CategoryCode).equals(PretupsI.SUPERADMIN_CATCODE+PretupsI.NETWORKADMIN_CATCODE)) {
				categoryData[j][0] = i;
				categoryData[j][1] = ExcelUtility.getCellData(0,ExcelI.PARENT_NAME, i);
				categoryData[j][2] = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, i);
				j++;
				}
		}
		return categoryData;
	}
}