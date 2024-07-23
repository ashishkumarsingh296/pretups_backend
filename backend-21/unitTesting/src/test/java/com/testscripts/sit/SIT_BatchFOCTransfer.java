package com.testscripts.sit;

import com.Features.BatchFOCTransfer;
import com.Features.BatchO2CTransfer;
import com.Features.ChannelUser;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.*;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.SIT_BATCHFOCTRANSFER)
public class SIT_BatchFOCTransfer extends BaseTest {

    public SIT_BatchFOCTransfer() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_BATCHFOCTRANSFER;
    }
    String assignCategory="SIT";

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_01_BatchFOCTransfer(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException, ParseException, SQLException {
        final String methodName = "TC_01_BatchFOCTransfer";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBFOCTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SITBFOCTRF21");
        CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("SITBFOCTRF22");
        CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("SITBFOCTRF23");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        businessController businessController = new businessController(_masterVO.getProperty("FOCCode"), null, userMSISDN);
        String expected1;
        String expected2;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");
        HashMap<String, String> initiatedQty = new HashMap<String, String>();
        initiatedQty.put(productCode, quantity);

        TransactionVO TransactionVO = businessController.preparePreTransactionVO();
        TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
        Map<String, String> map = BatchFOCTrf.BatchInitiateFOCTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch FOC has been initiated successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("batchfoc.processuploadedfile.msg.success",batchId, batchName, "1");
        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        map = BatchFOCTrf.batchLevel1ApprovalProcessFile(batchId);

        Assertion.assertPass("Batch FOC has been approved at level 1 successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected1 = MessagesDAO.prepareMessageByKey("batchfoc.batchfocprocessfile.msg.bachprocessfilesuccessfully.nextlevelrequired", "1", "0");
        Assertion.assertEquals(map.get("actualMessage"), expected1);

        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.PREREQUISITE);
        String actual2 = BatchFOCTrf.batchLevel2ApprovalProcessFile(batchId, quantity);

        Assertion.assertPass("Batch FOC has been approved at level 2 successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected2 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "1","0");
        Assertion.assertEquals(actual2, expected2);

        /*
         * Test Case to validate Network Stocks after successful FOC Transfer
         */
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBFOCTRF4").getExtentCase());
        currentNode.assignCategory(assignCategory);
        TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
        BusinessValidator.validateStocks(TransactionVO);

        /*
         * Test Case to validate Channel User balance after successful FOC Transfer
         */
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBFOCTRF5").getExtentCase());
        currentNode.assignCategory(assignCategory);
        BusinessValidator.validateUserBalances(TransactionVO);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_02_BatchFOCTransferBlankFile(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName){
        final String methodName = "TC_02_BatchFOCTransferBlankFile";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF6");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF24");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransferBlankFile(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch FOC with Blank file is processed successfully.");

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.norecordinfile",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_03_BatchFOCTransferBlankMSISDN(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_03_BatchFOCTransferBlankMSISDN";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF7");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF25");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransferBlankMSISDN(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch FOC with Blank MSISDN is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.invaliddata",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_05_BatchFOCTransferBatchApprove(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_05_BatchFOCTransferBatchApprove";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF9");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBFOCTRF26");


        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchFOCTrf.batchLevel1ApprovalBatchApprove(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2capprove.msg.bachprocessfailedsuccess", "1");

        Assertion.assertPass("Batch FOC with Batch Approve is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_06_BatchFOCTransferBatchReject(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_06_BatchFOCTransferBatchReject";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF10");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBFOCTRF27");


        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchFOCTrf.batchLevel1ApprovalBatchReject(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batchfoc.batchfocreject.msg.bachprocesssuccessfully", "1");

        Assertion.assertPass("Batch FOC with Batch Reject is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_07_BatchFOCTransferProcessFileReject(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_07_BatchFOCTransferProcessFileReject";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF11");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBFOCTRF28");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchFOCTrf.batchLevel1RejectProcessFile(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batchfoc.batchfocprocessfile.msg.bachprocessfilesuccessfully.nextlevelrequired", "1", "0");

        Assertion.assertPass("Batch FOC with Batch Reject is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_08_BatchFOCTransferProcessFileDiscard(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_08_BatchFOCTransferProcessFileDiscard";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF12");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBFOCTRF29");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchFOCTrf.batchLevel1DiscardProcessFile(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "0", "1");

        Assertion.assertPass("Batch FOC with Batch Discard is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_09_BatchFOCTransferInvalidFileFormat(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_09_BatchFOCTransferInvalidFileFormat";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF13");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF30");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransferInvalidFileFormat(userMSISDN, productCode, quantity, remarks, category, domain);
        String actual = map.get("INITIATE_MESSAGE");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("uploadfile.error.notrequiredcontent");

        Assertion.assertPass("Batch FOC with Invalid File is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_10_BatchFOCTransferInvalidBatchName(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_10_BatchFOCTransferInvalidBatchName";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF14");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF31");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransferInvalidBatchName(userMSISDN, productCode, quantity, remarks, category, domain);
        String actual = map.get("INITIATE_MESSAGE");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batchpointadjust.selectcategoryforbatchpointadjust.maskmsg.batchname");

        Assertion.assertPass("Batch FOC with Invalid Batch Name is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_11_BatchFOCTransferInSuspendedCU(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException, ParseException, SQLException {
        final String methodName = "TC_11_BatchFOCTransferInSuspendedCU";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF16");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF32");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        ChannelUser chnlUsr = new ChannelUser(driver);
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");
        HashMap<String, String> modificationMap = new HashMap<>();

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        modificationMap.put("inSuspend_chk", "true");
        modificationMap.put("searchMSISDN", userMSISDN);
        ExtentI.Markup(ExtentColor.TEAL, "In Suspending Channel User");
        chnlUsr.modifyChannelUserDetails(category, modificationMap);

        try{
            ExtentI.Markup(ExtentColor.TEAL, "Performing FOC Transfer");
            Map<String, String> map = BatchFOCTrf.BatchInitiateTransferInSuspendedCU(userMSISDN, productCode, quantity, remarks, category, domain);
            Assertion.assertPass("Batch FOC with In Suspended user is processed successfully.");
            currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
            String expectedMessage = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.userinsuspend");
            String actualMessage = map.get("INITIATE_MESSAGE");
            Log.info("Message fetched from WEB as : "+actualMessage);
            Assertion.assertEquals(actualMessage, expectedMessage);
        } catch(Exception e){
            Assertion.assertFail("Batch FOC Transfer initiation is successful, hence Test Case failed");
        }
        modificationMap.put("inSuspend_chk", "false");
        ExtentI.Markup(ExtentColor.TEAL, "Removing In Suspended Status from Channel User");
        chnlUsr.modifyChannelUserDetails(category, modificationMap);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_12_BatchFOCTransferNegativeAmount(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName)throws InterruptedException{
        final String methodName = "TC_12_BatchFOCTransferNegativeAmount";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF17");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF33");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransferNegativeAmount(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batchdirectpayout.processuploadedfile.error.qtynonnumeric",batchId, batchName, "1");

        Assertion.assertPass("Batch FOC with Negative Amount is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_13_BatchFOCTransferAlphanumericExtTxnNo(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName)throws InterruptedException{
        final String methodName = "TC_13_BatchFOCTransferAlphanumericExtTxnNo";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF18");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF34");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransferAlphanumericExtTxnNo(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.batchapprovereject.msg.error.externaltxnnotnumeric",batchId, batchName, "1");

        Assertion.assertPass("Batch FOC with Alphabetic External Transaction Number is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_14_BatchFOCTransferAlphanumericAmount(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName)throws InterruptedException{
        final String methodName = "TC_14_BatchFOCTransferAlphanumericAmount";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBFOCTRF19");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBFOCTRF35");

        BatchFOCTransfer BatchFOCTrf = new BatchFOCTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchFOCTrf.BatchInitiateTransferAlphanumericAmount(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.qtynumeric1",batchId, batchName, "1");

        Assertion.assertPass("Batch FOC with Alphanumeric Amount is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
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
        Object[][] Data = new Object[userCounter][4];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
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
            ProductObject[i][2] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, j);
        }

        /*
         * Creating combination of channel users for each product.
         */
        int countTotal = ProductObject.length * userCounter;
        Object[][] o2cData = new Object[countTotal][7];
        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
            o2cData[j][0] = Data[k][0];
            o2cData[j][1] = Data[k][1];
            o2cData[j][2] = Data[k][2];
            o2cData[j][3] = Data[k][3];
            o2cData[j][4] = ProductObject[i][0];
            o2cData[j][5] = ProductObject[i][1];
            o2cData[j][6] = ProductObject[i][2];
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

}
