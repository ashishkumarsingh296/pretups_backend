package com.testscripts.smoke;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransferRule;
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

@ModuleManager(name = Module.SMOKE_O2C_TRANSFER_RULE)
public class Smoke_O2CTransferRuleCreation extends BaseTest {

    private String MasterSheetPath;
    private String FirstApprovalLimit;
    private String SecondApprovalLimit;

    @Test(dataProvider = "RequiredTransferRuleCategories")
     @TestManager(TestKey = "PRETUPS-393") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void a_InitiateTransferRules(String ToDomain, String ToCategory, String Services) {
        final String methodName = "Test_InitiateTransferRules";
        Log.startTestCase(methodName, ToDomain, ToCategory, Services);

        Object Params[];
        O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);

        /*
         * Case Number 1: To verify Network Admin is able to create O2C Transfer Rule
         * 				  Code Handles Associate / Initiate both links.
         */
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE1").getExtentCase(), ToCategory)).assignCategory(TestCategory.SMOKE);
        Params = O2CTransferRule.createTransferRule(ToDomain, ToCategory, Services, FirstApprovalLimit, SecondApprovalLimit);

        if (Params[1].equals(false)) {
            if (Params[0].equals(false)) {

                // Case Number 2: Message Validation
                currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CTRFRULE1").getExtentCase()).assignCategory(TestCategory.SMOKE);
                String Message = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccessapprequired", "");
                Assertion.assertEquals(Params[2].toString(), Message);

                // Case Number 3: O2C Transfer Rule Approval
                currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE2").getExtentCase(), ToCategory)).assignCategory(TestCategory.SMOKE);
                O2CTransferRule.approveTransferRule(ToCategory);
            } else {

                currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CTRFRULE2").getExtentCase()).assignCategory(TestCategory.SMOKE);
                String Message = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccess", "");
                Assertion.assertEquals(Params[2].toString(), Message);

                currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE2").getExtentCase(), ToCategory)).assignCategory(TestCategory.SMOKE);
                Assertion.assertSkip("O2C Transfer rule for " + ToCategory + " category already exists, hence Skipped");
            }
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test
    @TestManager(TestKey = "PRETUPS-394") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void b_ModifyTransferRule() {
        final String methodName = "Test_ModifyTransferRule";

        MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        String ToDomain = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, 1);
        String ToCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, 1);
        // Data Objects ends

        Object Params[];
        O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);

        /*
         * Case Number 1: To verify Network Admin is able to modify O2C Transfer Rule
         * 				  Code Handles Associate / Initiate both links.
         */
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SO2CTRFRULE3").getExtentCase(), ToCategory)).assignCategory(TestCategory.SMOKE);
        Params = O2CTransferRule.modifyTransferRule(ToDomain, ToCategory, FirstApprovalLimit, SecondApprovalLimit);

        if (Params[0].equals(false)) {
            // Case Number 3: O2C Transfer Rule Approval
            currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SO2CTRFRULE4").getExtentCase(), ToCategory)).assignCategory(TestCategory.SMOKE);
            O2CTransferRule.approveTransferRule(ToCategory);
        }

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

        Object[][] transferRuleCategories = new Object[O2CTransferRuleCount][3];

        for (int i = 1; i < rowCount; i++) {
            String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
            if (FromCategory.equals("Operator")) {
                transferRuleCategories[MatrixRow][0] = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, i);
                transferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
                transferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
                MatrixRow++;
            }
        }
        return transferRuleCategories;
    }

    /* ------------------------------------------------------------------------------------------------ */
}
