package com.testscripts.smoke;

import java.io.IOException;
import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.TransferControlProfile;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SMOKE_TRANSFER_CONTROL_PROFILE)
public class Smoke_TransferControlProfile extends BaseTest {

    @Test(dataProvider = "dataProvider")
    @TestManager(TestKey = "PRETUPS-307") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_TransferControlProfileCreation(int rowNum, String domainName, String categoryName) {
        final String methodName = "Test_TransferControlProfileCreation";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);

        TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

        // Test Case Number 1 - Category Level TCP Creation
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PTRFCNTRLPRF1").getExtentCase(), categoryName)).assignCategory(TestCategory.SMOKE);
        TransferControlProfile.createCategoryLevelTransferControlProfile(rowNum, domainName, categoryName);

        // Test Case Number 2 - Channel Level TCP Creation
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PTRFCNTRLPRF2").getExtentCase(), categoryName)).assignCategory(TestCategory.SMOKE);
        TransferControlProfile.createChannelLevelTransferControlProfile(rowNum, domainName, categoryName);

        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "dataProvider")
    public Object[][] TestDataFeed() throws IOException {
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        Object[][] categoryData = new Object[1][3];
        categoryData[0][0] = 1;
        categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
        return categoryData;
    }

    /* ------------------------------------------------------------------------------------------------- */
}
