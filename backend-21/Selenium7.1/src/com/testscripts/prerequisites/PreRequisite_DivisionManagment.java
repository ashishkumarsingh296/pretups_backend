package com.testscripts.prerequisites;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashMap;

import com.Features.DivisionDeptManagment;
import com.classes.BaseTest;
import com.utils.Log;

public class PreRequisite_DivisionManagment extends BaseTest {

	String division;
	String ModuleName;
	String Status;
	HashMap<String, String> divdeptMap;

	@Test
	public void divisionManagement() throws IOException {

		// Pushing Test Case start to Logger
		Log.startTestCase(this.getClass().getName());

		// Initializing the Division Department Feature Class with current Driver
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		
		// Variables for Extent reporting
		ModuleName= "DivisionManagement";
		String caseType="Pre-Requisite";
		String testcase1="To verify that superadmin is able to add division in the system.";
		String testcase2="To verify that superadmin is able to add depratment in the system.";
		
		// Test Case creation on Extent Report
		test = extent.createTest("[Pre-Requisite]Division Management");
		
		/*
		 *  Test Case Number 1: Division Creation
		 */
		currentNode=test.createNode(testcase1);
		currentNode.assignCategory(caseType);
		Status= currentNode.getStatus().toString().toUpperCase();
		divdeptMap=divisiondeptMgmt.divisionManagement();
		//WriteTestCaseSheet.writeTestCaseSheet(caseType,new String[]{caseType,ModuleName,testcase1,Status});
		
		/*
		 * Test Case Number 2: Department Creation
		 */
		currentNode=test.createNode(testcase2);
		currentNode.assignCategory(caseType);
		Status= currentNode.getStatus().toString().toUpperCase();
		divdeptMap=divisiondeptMgmt.departmentManagement();
		//WriteTestCaseSheet.writeTestCaseSheet(caseType,new String[]{caseType,ModuleName,testcase2,Status});
		
		divisiondeptMgmt.writedivisiondepartment();
		
		//Pushing Test Case end to Logger
		Log.endTestCase(this.getClass().getName());
	}
}
