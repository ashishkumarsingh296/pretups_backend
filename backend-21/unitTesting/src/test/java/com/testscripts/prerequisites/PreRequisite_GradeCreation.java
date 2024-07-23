package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.GradeManagement;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

/**
 * @author tinky.sharma
 * @rewritten krishan.chawla Pre-Requisite Class for Grade Creation
 */
@ModuleManager(name = Module.PREREQUISITE_GRADE_CREATION)
public class PreRequisite_GradeCreation extends BaseTest {

    // Test Case to Create Grade. The test as well writes the Default Grade to Channel Users Hierarchy
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-267") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_CreateGrades(int rowNum, String domainName, String categoryName) {
        final String methodName = "Test_CreateGrades";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGRADECREATION1").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);
        GradeManagement GradeManagement = new GradeManagement(driver);
        String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);

        if (!BTSLUtil.isNullString(DefaultGradeName)) {
            GradeManagement.writeGradeToSheet(rowNum, DefaultGradeName);
            Assertion.assertSkip("Grade for " + categoryName + " category already exists hence Test Case Skipped");
        } else {
            Map<String, String> dataMap = GradeManagement.addGrade(domainName, categoryName);
            String Created_GradeName = dataMap.get("GRADENAME");
            GradeManagement.writeGradeToSheet(rowNum, Created_GradeName);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        Object[][] categoryData = new Object[rowCount][3];
        for (int i = 1, j = 0; i <= rowCount; i++, j++) {
            categoryData[j][0] = i;
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
        }
        return categoryData;
    }

    /* ------------------------------------------------------------------------------------------------- */
}
