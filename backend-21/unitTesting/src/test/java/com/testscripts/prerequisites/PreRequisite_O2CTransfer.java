package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_O2C_TRANSFER)
public class PreRequisite_O2CTransfer extends BaseTest {

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_O2CTransfer(String parentCategory, String category, String userMSISDN, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "Test_O2CTransfer";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
        Log.info("mobile number : "+userMSISDN);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PO2CTRF4");

        O2CTransfer o2cTrans = new O2CTransfer(driver);

        String expected1;
        String expected2 = null;
        Long netPayableAmount = null;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
        String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);

        // Test case to initiate O2C Transfer
        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.PREREQUISITE);
        Map<String, String> map = o2cTrans.initiateTransfer(userMSISDN, productType, quantity,productName, remarks);
        String txnId = map.get("TRANSACTION_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String expected = null;

        //Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
        if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
            expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
        else
            expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

        Assertion.assertEquals(actual, expected);

        // Test case to perform O2C approval level 1
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.PREREQUISITE);

        //Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
        if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
            map = o2cTrans.performingLevel1Approval(userMSISDN, txnId);
            netPayableAmount = _parser.getSystemAmount(map.get("NetPayableAmount"));

            if (netPayableAmount <= firstApprov)
                expected1 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
            else
                expected1 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);

            Assertion.assertEquals(map.get("actualMessage"), expected1);
        } else {
            Log.skip("Direct Operator to Channel is applicable in system");
        }

        /*
         * Test Case to perform approval level 2
         */
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > firstApprov) {
            currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.PREREQUISITE);

            //Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
            String actual2 = o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
            if (netPayableAmount <= secondApprov)
                expected2 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
            else
                expected2 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);

            Assertion.assertEquals(actual2, expected2);
        }

        /*
         * Test case to perform approval level 3
         */
        if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount > secondApprov) {
            currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.PREREQUISITE);

            //Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
            String actual3 = o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
            String expected3 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

            Assertion.assertEquals(actual3, expected3);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {
        String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        /*
         * Array list to store Categories for which O2C transfer is allowed
         */
        ArrayList<String> alist1 = new ArrayList<String>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(O2CTransferCode)) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
            }
        }

        /*
         * Counter to count number of users exists in channel users hierarchy sheet
         * of Categories for which O2C transfer is allowed
         */
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int chnlCount = ExcelUtility.getRowCount();
        int userCounter = 0;
        for (int i = 1; i <= chnlCount; i++) {
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                userCounter++;
            }
        }

        /*
         * Store required data of 'O2C transfer allowed category' users in Object
         */
        Object[][] Data = new Object[userCounter][3];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                j++;
            }
        }

        /*
         * Store products from Product Sheet to Object.
         */
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int prodRowCount = ExcelUtility.getRowCount();
        Object[][] ProductObject = new Object[prodRowCount][3];
        for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
            ProductObject[i][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
            ProductObject[i][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, j);
            ProductObject[i][2] = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, j);
        }

        /*
         * Creating combination of channel users for each product.
         */
        int countTotal = ProductObject.length * userCounter;
        Object[][] o2cData = new Object[countTotal][6];
        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
            o2cData[j][0] = Data[k][0];
            o2cData[j][1] = Data[k][1];
            o2cData[j][2] = Data[k][2];
            o2cData[j][3] = ProductObject[i][0];
            o2cData[j][4] = ProductObject[i][1];
            o2cData[j][5] = ProductObject[i][2];
            if (k < userCounter) {
                k++;
                if (k >= userCounter) {
                    k = 0;
                    i++;
                    if (i >= ProductObject.length)
                        i = 0;
                }
            } else {
                k = 0;
            }
        }
        return o2cData;
    }

    /* --------------------------------------------------------------------------------------------- */
}
