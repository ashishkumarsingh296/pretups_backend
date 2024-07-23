package com.testscripts.prerequisites;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CTransferRule;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

/**
 * @author lokesh.kontey This class is created for C2C Transfer Rule Creation
 */
@ModuleManager(name = Module.PREREQUISITE_C2C_TRANSFER_RULE)
public class PreRequisite_C2CTransferRuleCreation extends BaseTest {

    @Test(dataProvider = "RequiredTransferRuleCategories")
    @TestManager(TestKey = "PRETUPS-425") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_C2CTransferRuleCreation(String ToDomain, String ToCategory, String Services, String FromCategory, String FromDomain) {
        final String methodName = "Test_C2CTransferRuleCreation";
        Log.startTestCase(methodName, ToDomain, ToCategory, Services, FromCategory, FromDomain);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PC2CTRFRULECREATE1");
        C2CTransferRule c2ctrfrule = new C2CTransferRule(driver);
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        c2ctrfrule.channeltochannelTrfRule(ToDomain, ToCategory, Services, FromCategory, FromDomain);

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
        int C2CTransferRuleCount = 0;
        for (int i = 1; i <= rowCount; i++) {
            String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
            String ToCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
            if (!FromCategory.equals("Operator") && !ToCategory.equals("Subscriber")) {
                C2CTransferRuleCount++;
            }

        }
        Object[][] transferRuleCategories = new Object[C2CTransferRuleCount][5];

        for (int i = 1; i <= rowCount; i++) {
            String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
            String ToCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
            if (!FromCategory.equals("Operator") && !ToCategory.equals("Subscriber")) {
                transferRuleCategories[MatrixRow][0] = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, i);
                transferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
                transferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
                transferRuleCategories[MatrixRow][3] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
                transferRuleCategories[MatrixRow][4] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
                MatrixRow++;
            }
        }
        return transferRuleCategories;
    }

    /* ------------------------------------------------------------------------------------------------- */
}
