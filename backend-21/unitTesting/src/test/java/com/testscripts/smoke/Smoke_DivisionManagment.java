package com.testscripts.smoke;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.DivisionDeptManagment;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SMOKE_DIVISION_MANAGEMENT)
public class Smoke_DivisionManagment extends BaseTest {

    private HashMap<String, String> divdeptMap;

    @Test 
    @TestManager(TestKey = "PRETUPS-264") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void a_DivisionManagement() {
        final String methodName = "Test_DivisionManagement";
        Log.startTestCase(methodName);

        String testcase1 = _masterVO.getCaseMasterByID("PDIVMGMT1").getExtentCase();
        DivisionDeptManagment divisionDeptManagment = new DivisionDeptManagment(driver);

        currentNode = test.createNode(testcase1).assignCategory(TestCategory.SMOKE);
        divdeptMap = divisionDeptManagment.divisionManagement();

        String testcase2 = _masterVO.getCaseMasterByID("SDIVMGMT1").getExtentCase();
        currentNode = test.createNode(testcase2).assignCategory(TestCategory.SMOKE);
        String divisionMsg = MessagesDAO.prepareMessageByKey("master.adddivision.success");
        Assertion.assertEquals(divdeptMap.get("divisionaddMsg"), divisionMsg);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test 
    @TestManager(TestKey = "PRETUPS-265") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void b_DepartmentManagement() {
        final String methodName = "Test_DepartmentManagement";
        Log.startTestCase(methodName);

        String testcase3 = _masterVO.getCaseMasterByID("SDIVMGMT2").getExtentCase();
        String testcase4 = _masterVO.getCaseMasterByID("SDIVMGMT3").getExtentCase();
        DivisionDeptManagment divisiondeptMgmt = new DivisionDeptManagment(driver);

        currentNode = test.createNode(testcase3).assignCategory(TestCategory.SMOKE);
        divdeptMap = divisiondeptMgmt.departmentManagement();
        divisiondeptMgmt.departmentManagement();

        currentNode = test.createNode(testcase4).assignCategory(TestCategory.SMOKE);
        String departmentMsg = MessagesDAO.prepareMessageByKey("master.adddepartment.success");
        Assertion.assertEquals(divdeptMap.get("departmentaddMsg"), departmentMsg);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
}
