package com.testscripts.smoke;

import java.text.MessageFormat;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.GradeManagement;
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
import com.utils.constants.TestCategory;

/**
 * @author tinky.sharma
 * @rewritten krishan.chawla
 * Smoke Class for Grade Creation
 */
@ModuleManager(name = Module.SMOKE_GRADE_CREATION)
public class Smoke_GradeCreation extends BaseTest {

    // Test Case to Create Grade.
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-285") //TO BE UNCOMMENTED BY JIRA TEST CASE ID
    public void Test_GradeCreation(int rowNum, String domainName, String categoryName) {
        final String methodName = "Test_GradeCreation";
        Log.startTestCase(methodName);

        GradeManagement GradeManagement = new GradeManagement(driver);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGRADECREATION1").getExtentCase(), categoryName)).assignCategory(TestCategory.SMOKE);
        Map<String, String> dataMap = GradeManagement.addGrade(domainName, categoryName);
        String actual = dataMap.get("ACTUALMESSAGE");

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SGRADECREATION1").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String Message = MessagesDAO.prepareMessageByKey("domain.addgrade.message.success");
        Assertion.assertEquals(actual, Message);

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

        Object[][] categoryData = new Object[1][3];

        categoryData[0][0] = 1;
        categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);

        return categoryData;
    }

    /* -------------------------------------------------------------------------------------------------- */
}
