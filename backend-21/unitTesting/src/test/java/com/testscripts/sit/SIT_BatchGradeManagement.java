package com.testscripts.sit;

import com.Features.BatchGradeManagement;
import com.classes.BaseTest;
import com.classes.CONSTANT;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.Map;


@ModuleManager(name = Module.SIT_BATCH_GRADE_MANAGEMENT)
public class SIT_BatchGradeManagement extends BaseTest {

    public SIT_BatchGradeManagement() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_BATCHGRADEMANAGEMENT;
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-267") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_01_BatchGradeManagement(int rowNum, String domainName, String categoryName) throws InterruptedException {
        final String methodName = "TC_01_BatchGradeManagement";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);
        String expected;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT1").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        BatchGradeManagement BatchGradeManagement = new BatchGradeManagement(driver);

        String DefaultGradeName = BatchGradeManagement.getDefaultGrade(categoryName);
        Log.info("The default grade name found: " +DefaultGradeName);

        String actualMessage = BatchGradeManagement.BatchGradeManagement(domainName, categoryName, DefaultGradeName);

        Assertion.assertPass("Batch Grade Management has been associated successfully.");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT2").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("userbulkgradeassociation.upload.associate.file.msg.success");

        Assertion.assertEquals(actualMessage, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-267") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_02_BatchGradeManagementInvalidGrade(int rowNum, String domainName, String categoryName) throws InterruptedException {
        final String methodName = "TC_02_BatchGradeManagementInvalidGrade";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);
        String expected;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT3").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        BatchGradeManagement BatchGradeManagement = new BatchGradeManagement(driver);

        String actualMessage = BatchGradeManagement.BatchGradeManagementInvalidGrade(domainName, categoryName);

        Assertion.assertPass("Batch Grade Management is not successful with invalid grade.");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT4").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("userbulkgradeassociation.upload.associate.file.msg.success");

        Assertion.assertEquals(actualMessage, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-267") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_03_BatchGradeManagementBlankDomain(int rowNum, String domainName, String categoryName) throws InterruptedException {
        final String methodName = "TC_03_BatchGradeManagementBlankDomain";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);
        String expected;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT5").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        BatchGradeManagement BatchGradeManagement = new BatchGradeManagement(driver);

        String actualMessage = BatchGradeManagement.BatchGradeManagementBlankDomain(domainName, categoryName);

        Assertion.assertPass("Batch Grade Management is not successful with Blank Domain.");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT6").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("userbulkgradeassociation.upload.error.domain.required");

        Assertion.assertEquals(actualMessage, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-267") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_04_BatchGradeManagementBlankCategory(int rowNum, String domainName, String categoryName) throws InterruptedException {
        final String methodName = "TC_04_BatchGradeManagementBlankCategory";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);
        String expected;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT7").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        BatchGradeManagement BatchGradeManagement = new BatchGradeManagement(driver);

        String actualMessage = BatchGradeManagement.BatchGradeManagementBlankCategory(domainName, categoryName);

        Assertion.assertPass("Batch Grade Management is not successful with Blank Category.");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT8").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("userbulkgradeassociation.upload.error.domain.required");

        Assertion.assertEquals(actualMessage, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-267") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_05_BatchGradeManagementBlankFile(int rowNum, String domainName, String categoryName) throws InterruptedException {
        final String methodName = "TC_05_BatchGradeManagementBlankFile";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);
        String expected;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT9").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        BatchGradeManagement BatchGradeManagement = new BatchGradeManagement(driver);

        String actualMessage = BatchGradeManagement.BatchGradeManagementBlankFile(domainName, categoryName);

        Assertion.assertPass("Batch Grade Management is not successful with Blank File.");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT10").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("userbulkgradeassociation.upload.associate.file.msg.success");

        Assertion.assertEquals(actualMessage, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-267") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_06_BatchGradeManagementInvalidFile(int rowNum, String domainName, String categoryName) throws InterruptedException {
        final String methodName = "TC_06_BatchGradeManagementInvalidFile";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);
        String expected;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT11").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        BatchGradeManagement BatchGradeManagement = new BatchGradeManagement(driver);

        String actualMessage = BatchGradeManagement.BatchGradeManagementInvalidFile(domainName, categoryName);

        Assertion.assertPass("Batch Grade Management is not successful with Invalid File.");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITBTCHGRDMGMT12").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("uploadfile.error.notrequiredcontent");

        Assertion.assertEquals(actualMessage, expected);

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
}
