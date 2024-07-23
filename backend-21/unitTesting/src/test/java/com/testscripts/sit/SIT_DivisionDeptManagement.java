package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.DivisionDeptManagment;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_DivisionDeptManagement)
public class SIT_DivisionDeptManagement extends BaseTest{

	static boolean TestCaseCounter = false;
	static String division;
	static String department;
	static String deptShortCode;
	String ModuleName;
	String Status;
	HashMap<String, String> divdeptMap;
	String assignCategory="SIT";


	@TestManager(TestKey = "PRETUPS-923") 
	@Test
	public void b_divisionManagement_BlankDivisionName() throws IOException {


		final String methodName="b_divisionManagement_BlankDivisionName";Log.startTestCase(methodName);

		

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map<String, String> dataMap = divisiondeptMgmt.divisionManagement_blankDivisionName(division);

		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.divisiondetails.label.divisionname")); 

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}



	@TestManager(TestKey = "PRETUPS-924") 
	@Test
	public void c_divisionManagement_BlankDivisionCode() throws IOException {


		final String methodName="c_divisionManagement_BlankDivisionCode";Log.startTestCase(methodName);

	

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map<String, String> dataMap = divisiondeptMgmt.divisionManagement_blankDivisionCode(division);

		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.divisiondetails.label.divisionshortcode")); 

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-925") 
	@Test
	public void d_divisionManagement_StatusNotSelected() throws IOException {


		final String methodName="d_divisionManagement_StatusNotSelected";Log.startTestCase(methodName);

	
		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map<String, String> dataMap = divisiondeptMgmt.divisionManagement_StatusNotSelected(division);
		Log.info("The static division name is:" +division);

		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.divisiondetails.label.status"));

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}




	@TestManager(TestKey = "PRETUPS-926") 
	@Test
	public void e_dept_BlankDeptName() throws IOException {


		final String methodName="e_dept_BlankDeptName";Log.startTestCase(methodName);

		

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT4").getExtentCase());
		currentNode.assignCategory(assignCategory);


		Map<String, String> dataMap = divisiondeptMgmt.departmentManagement_blankdeptName(division);

		String actual= dataMap.get("departmentaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.viewdepartmentdetails.label.departmentname"));

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-928") 
	@Test
	public void f_dept_BlankDeptCode() throws IOException {


		final String methodName="f_dept_BlankDeptCode";Log.startTestCase(methodName);

	

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT5").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.departmentManagement_blankDeptCode(division);

		String actual= dataMap.get("departmentaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("master.viewdepartmentdetails.label.departmentshortcode"));

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}





	@TestManager(TestKey = "PRETUPS-929") 
	@Test
	public void g_div_Multiple() throws IOException {


		final String methodName="g_div_Multiple";Log.startTestCase(methodName);

	

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT6").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.divisionCreationMultiple();

		division  = dataMap.get("division");
		String actual0= dataMap.get("divisionaddMsg1");
		String ExpectedMessage0 = MessagesDAO.prepareMessageByKey("master.adddivision.success");

		if(actual0.equalsIgnoreCase(ExpectedMessage0)){
			Log.info("Division " +dataMap.get("division")+ "added successfully");
		}
		else
		{
			Log.info("issue while adding division");
		}


		String actual= dataMap.get("divisionaddMsg2");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.adddivision.success");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}	




	@TestManager(TestKey = "PRETUPS-930") 
	@Test
	public void h_div_Modify() throws IOException {


		final String methodName="h_div_Modify";Log.startTestCase(methodName);


		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT7").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.divisionManagementModify(division);

		//division = dataMap.get("division");

		String actual= dataMap.get("divisionModifyMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.modifydivision.success");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-931") 
	@Test
	public void i_div_Delete() throws IOException {


		final String methodName="i_div_Delete";Log.startTestCase(methodName);

		

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT8").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.divisionManagementDelete(division);

		String actual= dataMap.get("divisionDelMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.deletedivision.success");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}





	@TestManager(TestKey = "PRETUPS-932") 
	@Test
	public void j_div_DeleteNeg() throws IOException {


		final String methodName="j_div_DeleteNeg";Log.startTestCase(methodName);


		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT9").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap1 = divisiondeptMgmt.divisionManagement();

		division = dataMap1.get("division");
		String actual0= dataMap1.get("divisionaddMsg");
		String ExpectedMessage0 = MessagesDAO.prepareMessageByKey("master.adddivision.success");

		if(actual0.equalsIgnoreCase(ExpectedMessage0)){
			Log.info("Division " +division+ "added successfully");
		}
		else
		{
			Log.info("issue while adding division");
		}


		Map<String, String> dataMap2 = divisiondeptMgmt.departmentManagement();

		String actual1= dataMap2.get("divisionaddMsg");
		String ExpectedMessage1 = MessagesDAO.prepareMessageByKey("master.adddepartment.success");

		if(actual1.equalsIgnoreCase(ExpectedMessage1)){
			Log.info("Department " +dataMap2.get("department")+ "added successfully");
		}
		else
		{
			Log.info("issue while adding department");
		}

		Map<String, String> dataMap = divisiondeptMgmt.divisionManagementDeleteNeg(division);

		String actual= dataMap.get("divisionDelMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.deletedivision.departmentexists");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}







	@TestManager(TestKey = "PRETUPS-933") 
	@Test
	public void k_div_UniqueName() throws IOException {


		final String methodName="k_div_UniqueName";Log.startTestCase(methodName);

	

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT10").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.divisionManagementUnique_neg(division, division, true);

		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.adddivision.divisionname.alreadyexists");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}	




	@TestManager(TestKey = "PRETUPS-934") 
	@Test
	public void l_div_UniqueShortCode() throws IOException {


		final String methodName="l_div_UniqueShortCode";Log.startTestCase(methodName);

		

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT11").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.divisionManagementUnique_neg(division, division, false);

		String actual= dataMap.get("divisionaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.adddivision.divisionshortcode.alreadyexists");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}














	@TestManager(TestKey = "PRETUPS-935") 
	@Test
	public void m_dept_Modify() throws IOException {


		final String methodName="m_dept_Modify";Log.startTestCase(methodName);

	

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT12").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> DivMap = divisiondeptMgmt.divisionManagement();

		division  = DivMap.get("division");
		String actual0= DivMap.get("divisionaddMsg");
		String ExpectedMessage0 = MessagesDAO.prepareMessageByKey("master.adddivision.success");

		if(actual0.equalsIgnoreCase(ExpectedMessage0)){
			Log.info("Division " +DivMap.get("division")+ "added successfully");
		}
		else
		{
			Log.info("issue while adding division");
		}

		Map<String, String> Map = divisiondeptMgmt.departmentManagement();

		department = Map.get("department");

		Map<String, String> dataMap = divisiondeptMgmt.deptManagementModify(department);

		String actual= dataMap.get("deptModifyMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.modifydepartment.success");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-936") 
	@Test
	public void n_dept_Delete() throws IOException {


		final String methodName="n_dept_Delete";Log.startTestCase(methodName);

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT13").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.deptManagementDelete(department,false);

		String actual= dataMap.get("deptDeleteMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.deletedepartment.success");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}












	@TestManager(TestKey = "PRETUPS-937") 
	@Test
	public void o_department_UniqueName() throws IOException {


		final String methodName="o_department_UniqueName";Log.startTestCase(methodName);


		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map<String, String> Map = divisiondeptMgmt.departmentManagement();

		department = Map.get("department");
		deptShortCode = Map.get("departmentShortCode");


		Map<String, String> dataMap = divisiondeptMgmt.departmentManagementUnique_neg(department, deptShortCode, true);

		String actual= dataMap.get("departmentaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.adddivision.deptname.alreadyexists");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}	




	@TestManager(TestKey = "PRETUPS-939") 
	@Test
	public void p_dept_UniqueShortCode() throws IOException {


		final String methodName="p_dept_UniqueShortCode";Log.startTestCase(methodName);

	

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT16").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> dataMap = divisiondeptMgmt.departmentManagementUnique_neg(department, deptShortCode, false);

		String actual= dataMap.get("departmentaddMsg");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.adddivision.deptshortcode.alreadyexists");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}



	@TestManager(TestKey = "PRETUPS-940") 
	@Test
	public void q_department_DeleteNeg() throws IOException {


		final String methodName="q_department_DeleteNeg";Log.startTestCase(methodName);

	

		DivisionDeptManagment divisiondeptMgmt= new DivisionDeptManagment(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITDIVMGMT14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		//division = DBHandler.AccessHandler.get_division_name("superadmin");
		//String dept = DBHandler.AccessHandler.get_department_name("superadmin");
		
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.DIVISION_DEPT_SHEET);
		String DivisionName = ExcelUtility.getCellData(0, ExcelI.DIVISION, 1);
		division = DivisionName;
		String DepartmentName = ExcelUtility.getCellData(0, ExcelI.DEPARTMENT, 1);
		Map<String, String> dataMap1 = divisiondeptMgmt.deptManagementDelete(DepartmentName, true);


		String actual= dataMap1.get("deptDeleteMsg");



		String ExpectedMessage = MessagesDAO.prepareMessageByKey("master.deletedepartment.userexists");

		Assertion.assertEquals(actual, ExpectedMessage);

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}





}
