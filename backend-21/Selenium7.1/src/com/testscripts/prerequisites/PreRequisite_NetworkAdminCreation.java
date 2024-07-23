package com.testscripts.prerequisites;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;
import com.utils.Validator;

/**
 * @author lokesh.kontey
 * This class creates Network Admin for PreRequisite
 */
public class PreRequisite_NetworkAdminCreation extends BaseTest{
	
	public static String NetworkADM_Name = null;
	public static String SuperADM_Name = null;
	
	@Test
	public void createNWADM() throws InterruptedException{
		
		// Pushing Test Case start to Logger
		Log.startTestCase(this.getClass().getName());
		
		// Initializing Operator User Feature class with current Driver
		OperatorUser optUsrCreation= new OperatorUser(driver);

		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	
		int rowCount= ExcelUtility.getRowCount();
	
		int j=0;
		
		while (j < rowCount) {
			String ParentCategoryCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, j);
			String CategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, j);
			if (ParentCategoryCode.equals(PretupsI.SUPERADMIN_CATCODE) && CategoryCode.equals(PretupsI.NETWORKADMIN_CATCODE)) {
				NetworkADM_Name = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				SuperADM_Name = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, j);
				break;
			}
			j++;
		}
		
		// Test Case creation on Extent Report
		test=extent.createTest("[Pre-Requisites]"+ NetworkADM_Name +" creation");
		
		/*
		 * Test Case Number 1: Network Admin Creation
		 */
		currentNode=test.createNode("To verify "+ SuperADM_Name +" is able to initiate "+ NetworkADM_Name);
		currentNode.assignCategory("Pre-Requisite");
		HashMap<String, String> optMap=optUsrCreation.operatorUserInitiate(SuperADM_Name, NetworkADM_Name);
		Validator.nullValidator(optMap.get("initiateMsg"));
		
		/*
		 * Test Case Number 2: Network Admin Approval
		 */
		currentNode=test.createNode("To verify Super Admin is able to approve Network Admin");
		currentNode.assignCategory("Pre-Requisite");
		optUsrCreation.approveUser(SuperADM_Name);
		
		/*
		 * Test Case Number 3: Network Admin Password Change
		 */
		currentNode=test.createNode("To verify that Network Admin is prompted for password change on first login.");
		currentNode.assignCategory("Pre-Requisite");
		optUsrCreation.changeUserFirstTimePassword();
		
		optUsrCreation.writeOperatorUserData(j);
		
		//Pushing End Test Case to Logger
		Log.endTestCase(this.getClass().getName());
	}
	
}
