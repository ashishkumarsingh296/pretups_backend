package com.testscripts.prerequisites;

import org.testng.annotations.Test;

import com.Features.DivisionDeptManagment;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_DIVISION_MANAGEMENT)
public class PreRequisite_DivisionManagment extends BaseTest {

    @Test
    @TestManager(TestKey = "PRETUPS-263")
    public void Test_AddDivisionDepartment() {
        final String methodName = "Test_AddDivisionDepartment";
        Log.startTestCase(methodName);

        DivisionDeptManagment deptManagement = new DivisionDeptManagment(driver);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PDIVMGMT1");
        currentNode = test.createNode(CaseMaster1.getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
        deptManagement.divisionManagement();

        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PDIVMGMT2");
        currentNode = test.createNode(CaseMaster2.getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
        deptManagement.departmentManagement();

        deptManagement.writedivisiondepartment();

        Log.endTestCase(methodName);
    }
}
