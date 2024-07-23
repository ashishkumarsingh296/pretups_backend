package com.testscripts.smoke;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.HashMap;

import com.Features.DivisionDeptManagment;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.utils.Log;


public class Smoke_DivisionManagment extends BaseTest {

	static String division;
	HashMap<String, String> divdeptMap;	
	String nStatus;
	
	@Test//(priority=0)
	public void a_divisionManagement() throws IOException {

		String testcase1="To verify that superadmin is able to add division in the system";
		String testcase2="To verify that valid message is displayed on creating division in the system";
		
		String caseType="Smoke";
		
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		Log.startTestCase(this.getClass().getName());
		test = extent.createTest("[Smoke]Division Management");
		
		currentNode=test.createNode(testcase1);
		currentNode.assignCategory(caseType);
		nStatus= currentNode.getStatus().toString().toUpperCase();
		divdeptMap=divisiondeptMgmt.divisionManagement();
		//WriteTestCaseSheet.writeTestCaseSheet(caseType,new String[]{caseType,ModuleName,testcase1,nStatus});
		
		currentNode=test.createNode(testcase2);
		currentNode.assignCategory(caseType);
		nStatus= currentNode.getStatus().toString().toUpperCase();
		String divisionMsg = MessagesDAO.prepareMessageByKey("master.adddivision.success");
		//assertEquals(divdeptMap.get("divisionaddMsg"), divisionMsg);
		if (divdeptMap.get("divisionaddMsg").equals(divisionMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + divisionMsg + "] but found [" + divdeptMap.get("divisionaddMsg") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		Log.endTestCase(this.getClass().getName());
		//WriteTestCaseSheet.writeTestCaseSheet(caseType,new String[]{caseType,ModuleName,testcase2,nStatus});
	}
	
	@Test//(priority=1)
	public void b_departmentManagement() throws IOException{
		Log.startTestCase(this.getClass().getName());
		String testcase3="To verify that superadmin is able to add multiple departments in the system";
		String testcase4="To verify that valid message is displayed on creating department in the system";
		
		String caseType="Smoke";
		
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);
		
		currentNode=test.createNode(testcase3);
		currentNode.assignCategory(caseType);
		nStatus= currentNode.getStatus().toString().toUpperCase();
		divdeptMap=divisiondeptMgmt.departmentManagement();
		divisiondeptMgmt.departmentManagement();
		//WriteTestCaseSheet.writeTestCaseSheet(caseType,new String[]{caseType,ModuleName,testcase3,nStatus});
		
		currentNode=test.createNode(testcase4);
		currentNode.assignCategory(caseType);
		nStatus= currentNode.getStatus().toString().toUpperCase();
		String departmentMsg = MessagesDAO.prepareMessageByKey("master.adddepartment.success");
		//assertEquals(divdeptMap.get("departmentaddMsg"), departmentMsg);
		if (divdeptMap.get("departmentaddMsg").equals(departmentMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + departmentMsg + "] but found [" + divdeptMap.get("departmentaddMsg") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		Log.endTestCase(this.getClass().getName());
		//WriteTestCaseSheet.writeTestCaseSheet(caseType,new String[]{caseType,ModuleName,testcase4,nStatus});
	}
}
