package com.testscripts.prerequisites;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransferRule;
import com.classes.BaseTest;
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
 * @author krishan.chawla
 * This class is created for O2C Transfer Rule Creation
 */
@ModuleManager(name = Module.PREREQUISITE_O2C_TRANSFER_RULE)
public class PreRequisite_O2CTransferRuleCreation extends BaseTest {

    String MasterSheetPath;
    private Object[][] TransferRuleCategories;
    private String FirstApprovalLimit;
    private String SecondApprovalLimit;

    @Test(dataProvider = "RequiredTransferRuleCategories")
    @TestManager(TestKey = "PRETUPS-420") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void Test_O2CTransferRuleCreation(String ToDomain, String ToCategory, String Services) {
        final String methodName = "Test_O2CTransferRuleCreation";
        Log.startTestCase(methodName, ToDomain, ToCategory, Services);

        Object Params[];
        O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);

        // Test Case - O2C Transfer Role Creation
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE1").getExtentCase(), ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        Params = O2CTransferRule.createTransferRule(ToDomain, ToCategory, Services, FirstApprovalLimit, SecondApprovalLimit);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE2").getExtentCase(), ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (Params[0].equals(false) && Params[1].equals(false))
            O2CTransferRule.approveTransferRule(ToCategory);
        else
            Assertion.assertSkip("O2C Transfer rule for " + ToCategory + " category already exists, hence Skipped");

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "RequiredTransferRuleCategories")
    public Object[][] RequiredTransferRules() {

        FirstApprovalLimit = _masterVO.getProperty("O2CFirstApprovalLimit");
        SecondApprovalLimit = _masterVO.getProperty("O2CSecondApprovalLimit");
        int MatrixRow = 0;
        MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        int O2CTransferRuleCount = 0;
        for (int i = 1; i < rowCount; i++) {
            String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
            if (FromCategory.equals("Operator")) {
                O2CTransferRuleCount++;
            }
        }

        TransferRuleCategories = new Object[O2CTransferRuleCount][3];

        for (int i = 1; i < rowCount; i++) {
            String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
            if (FromCategory.equals("Operator")) {
                TransferRuleCategories[MatrixRow][0] = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, i);
                TransferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
                TransferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
                MatrixRow++;
            }
        }
        return TransferRuleCategories;
    }

    /* ----------------------------------------------------------------------------------------------- */
}
