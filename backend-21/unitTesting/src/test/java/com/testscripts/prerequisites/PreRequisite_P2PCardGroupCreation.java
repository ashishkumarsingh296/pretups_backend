package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PCardGroup;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_P2P_CARDGROUP)
public class PreRequisite_P2PCardGroupCreation extends BaseTest {

    @Test(dataProvider = "serviceData")
    @TestManager(TestKey = "PRETUPS-326") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_P2PCardGroupCreation(int rowNum, String serviceName, String subService) throws InterruptedException {
        final String methodName = "Test_P2PCardGroupCreation";
        Log.startTestCase(methodName);

        // Test Case - To create P2P Card Group through Network Admin as per the DataProvider
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PP2PCARDGRP1").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
        HashMap<String, String> mapInfo = (HashMap<String, String>) p2pCardGroup.P2PCardGroupCreation(serviceName, subService);
        p2pCardGroup.writeCardGroupToExcel(mapInfo.get("CARDGROUPNAME"), mapInfo.get("CARDGROUP_SETID"), rowNum);

        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "serviceData")
    public Object[][] TestDataFeed() {
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        Object[][] categoryData = null;
        if (rowCount > 0) {
            categoryData = new Object[rowCount][3];
            for (int i = 1, j = 0; i <= rowCount; i++, j++) {
                categoryData[j][0] = i;
                categoryData[j][1] = ExcelUtility.getCellData(i, 1);
                categoryData[j][2] = ExcelUtility.getCellData(i, 2);
            }
        } else if (rowCount <= 0) {
            categoryData = new Object[][]{
                    {0, null, null}
            };
        }
        return categoryData;
    }

    /* ------------------------------------------------------------------------------------------------- */
}
