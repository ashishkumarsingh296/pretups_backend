package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.DivisionDeptManagment;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.utils.Log;
import com.utils.Validator;

public class SIT_DivisionDeptManagement extends BaseTest{
	
	static boolean TestCaseCounter = false;
    static String division;
	String ModuleName;
	String Status;
	HashMap<String, String> divdeptMap;
	

	
	@Test
	public void b_divisionManagement_BlankDivisionName() throws IOException {
		
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Division and Dept Management");
			TestCaseCounter = true;
		}
		
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		
		currentNode=test.createNode("To verify that Super Admin is not able to add a division with blank division name");
		currentNode.assignCategory("SIT");
		Map<String, String> dataMap = divisiondeptMgmt.divisionManagement_blankDivisionName(division);
		
		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.divisiondetails.label.divisionname")); 
		
		Validator.messageCompare(actual, ExpectedMessage);

		Log.endTestCase(this.getClass().getName());
	}
	
	
	
	@Test
	public void c_divisionManagement_BlankDivisionCode() throws IOException {
		
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Division and Dept Management");
			TestCaseCounter = true;
		}
		
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		
		currentNode=test.createNode("To verify that Super Admin is not able to add a division with blank division code");
		currentNode.assignCategory("SIT");
		Map<String, String> dataMap = divisiondeptMgmt.divisionManagement_blankDivisionCode(division);
		
		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.divisiondetails.label.divisionshortcode")); 
		
		Validator.messageCompare(actual, ExpectedMessage);

		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test
	public void d_divisionManagement_StatusNotSelected() throws IOException {
		
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Division and Dept Management");
			TestCaseCounter = true;
		}
		
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		
		currentNode=test.createNode("To verify that Super Admin is not able to add a division with blank Status");
		currentNode.assignCategory("SIT");
		Map<String, String> dataMap = divisiondeptMgmt.divisionManagement_StatusNotSelected(division);
		Log.info("The static division name is:" +division);
		
		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.divisiondetails.label.status"));
		
		Validator.messageCompare(actual, ExpectedMessage);

		Log.endTestCase(this.getClass().getName());
	}
	
	
	
	
	@Test
	public void e_dept_BlankDeptName() throws IOException {
		
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Division and Dept Management");
			TestCaseCounter = true;
		}
		
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		
		currentNode=test.createNode("To verify that Super Admin is not able to add a Department with blank Name");
		currentNode.assignCategory("SIT");
		
		
		Map<String, String> dataMap = divisiondeptMgmt.departmentManagement_blankdeptName(division);
		
		String actual= dataMap.get("departmentaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.viewdepartmentdetails.label.departmentname"));
		
		Validator.messageCompare(actual, ExpectedMessage);

		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test
	public void f_dept_BlankDeptCode() throws IOException {
		
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Division and Dept Management");
			TestCaseCounter = true;
		}
		
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		
		currentNode=test.createNode("To verify that Super Admin is not able to add a Department with blank Code");
		currentNode.assignCategory("SIT");
		
		Map<String, String> dataMap = divisiondeptMgmt.departmentManagement_blankDeptCode(division);
		
		String actual= dataMap.get("departmentaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.viewdepartmentdetails.label.departmentshortcode"));
		
		Validator.messageCompare(actual, ExpectedMessage);

		Log.endTestCase(this.getClass().getName());
	}
	
	
	
	
}
