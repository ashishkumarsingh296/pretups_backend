/**
 * 
 */
package com.testscripts.uap;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author lokesh.kontey
 *
 */
@ModuleManager(name =Module.UAP_VIEW_SELF_DETAILS)
public class UAP_ViewSelfDetailsOperator extends BaseTest {

	static boolean TestCaseCounter = false;
	
	@Test(dataProvider="ViewSelfDetails")
	@TestManager(TestKey = "PRETUPS-382") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void viewSelfDetails(String ParentName, String Category, String LoginID, String Password){
		final String methodName = "Test_viewSelfDetails";
	    Log.startTestCase(methodName);
		
		Log.startTestCase(this.getClass().getName());
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("UVIEWSELFDETAILS1");
		
		OperatorUser optUserLogic = new OperatorUser(driver);
		
		currentNode=test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),Category,ParentName));
		currentNode.assignCategory("UAP");
		optUserLogic.viewSelfDetails(LoginID, Password);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@DataProvider(name="ViewSelfDetails")
	public Object[][] OperatorUserData(){
		
		String RoleCode = RolesI.VIEW_SELF_DETAILS_OPERATOR;
		
	    	Log.info("Trying to get User with Access: " + RoleCode);
	    	ArrayList<String> Array = new ArrayList<String>();
	    
	    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
	    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
	    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
	    		}
	    	}
	    	
	    	int j=0;
	    	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	    	int excelLimit = ExcelUtility.getRowCount();
	    	for (int i = 0; i <= excelLimit; i++) {
	    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
	    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	    		if (Array.contains(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
	    			j++;
	    		}
	    	}
	    	
	    	Object[][] data = new Object[j][4];
	    	for (int i = 0,k=0; i <= excelLimit; i++) {
	    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
	    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	    		if (Array.contains(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
	    			data[k][0]= ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
	    			data[k][1]= ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
	    			data[k][2]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	    			data[k][3]= ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
	    			k++;
	    		}
	    	}
	    
		return data;
		
		
	}
	
	
}
