package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.Map;

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

@ModuleManager(name = Module.PREREQUISITE_TRANSFER_CONTROL_PROFILE)
public class PreRequisite_TCPCreation extends BaseTest {

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-272") // TO BE UNCOMMENTED WITH JIRA TEST CASE MAPPING
    public void Test_CategoryLevelTransferControlProfileCreation(int rowNum, String domainName, String categoryName) {
        final String methodName = "Test_CategoryLevelTransferControlProfileCreation";
        Log.startTestCase(methodName, rowNum, domainName, categoryName);

        TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

        // Test Case Number 1 - Category Level TCP Creation
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PTRFCNTRLPRF1").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);
        Map<String, String> dataMap1 = TransferControlProfile.createCategoryLevelTransferControlProfile(rowNum, domainName, categoryName);
        TransferControlProfile.addProfileToDataProvider("SATCP", rowNum, dataMap1.get("CatTCP"), dataMap1.get("profile_ID"));

        // Test Case Number 2 - Channel Level TCP Creation
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PTRFCNTRLPRF2").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);
        Map<String, String> dataMap = TransferControlProfile.createChannelLevelTransferControlProfile(rowNum, domainName, categoryName);
        TransferControlProfile.modifyChannelLevelTransferProfileDefault(rowNum, domainName, categoryName, dataMap.get("TCP_Name"), dataMap.get("profile_ID"));
        TransferControlProfile.addProfileToDataProvider("NATCP", rowNum, dataMap.get("TCP_Name"), dataMap.get("profile_ID"));

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
