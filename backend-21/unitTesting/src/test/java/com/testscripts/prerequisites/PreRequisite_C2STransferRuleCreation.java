package com.testscripts.prerequisites;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_C2S_TRANSFER_RULE)
public class PreRequisite_C2STransferRuleCreation extends BaseTest {

    @Test(dataProvider = "RequiredTransferRuleCategories")
    @TestManager(TestKey = "PRETUPS-428") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_AddC2STransferRule(String fromDomain, String fromCategory, String services, String requestBearer, int rownum) {
        final String methodName = "Test_AddC2STransferRule";
        Log.startTestCase(methodName, fromDomain, fromCategory, services, requestBearer, rownum);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PC2STRFRULE1").getExtentCase(), fromCategory)).assignCategory(TestCategory.PREREQUISITE);
        Object[][] cardGroupDataObj;
        boolean preRequisite = true;
        C2STransferRules c2STransferRules = new C2STransferRules(driver);
        cardGroupDataObj = c2STransferRules.addC2STransferRule(fromDomain, fromCategory, services, requestBearer, rownum, preRequisite);
        c2STransferRules.writeC2SData(cardGroupDataObj);

        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "RequiredTransferRuleCategories")
    public Object[][] RequiredTransferRules() {

        int MatrixRow = 0;
        String masterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        int C2STransferRuleCount = 0;
        for (int i = 1; i <= rowCount; i++) {
            String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
            String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
            if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
                C2STransferRuleCount++;
            }
        }

        int totalCells = 5;
        Object[][] transferRuleCategories = new Object[C2STransferRuleCount][totalCells];

        for (int i = 1; i <= rowCount; i++) {
            String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
            String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
            if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
                transferRuleCategories[MatrixRow][0] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
                transferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
                transferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
                transferRuleCategories[MatrixRow][3] = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
                transferRuleCategories[MatrixRow][4] = i;
                MatrixRow++;
            }
        }

        return transferRuleCategories;
    }

    /* ------------------------------------------------------------------------------------------------ */
}
