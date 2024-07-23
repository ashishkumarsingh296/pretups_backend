package com.testscripts.sit;

import com.Features.BatchO2CTransfer;
import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.*;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.pageobjects.channeladminpages.batchO2CTransfer.BatchInitiateO2CTransferPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.*;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.SIT_BATCHO2CTRANSFER)
public class SIT_BatchO2CTransfer extends BaseTest {

    public SIT_BatchO2CTransfer() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_BATCHO2CTRANSFER;
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_01_BatchO2CTransfer(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException, ParseException, SQLException {
        final String methodName = "TC_01_BatchO2CTransfer";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SITBO2CTRF23");
        CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("SITBO2CTRF24");
        CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("SITBO2CTRF25");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
        businessController businessController = new businessController(_masterVO.getProperty("O2CTransferCode"), null, userMSISDN);
        String expected1;
        String expected2;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");
        HashMap<String, String> initiatedQty = new HashMap();
        initiatedQty.put(productCode, quantity);

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        TransactionVO TransactionVO = businessController.preparePreTransactionVO();
        TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch O2C has been initiated successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchO2CTrf.batchLevel1ApprovalProcessFile(batchId);

        Assertion.assertPass("Batch O2C has been approved at level 1 successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "1", "0");

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.PREREQUISITE);
        String actual2 = BatchO2CTrf.batchLevel2ApprovalProcessFile(batchId, quantity);

        Assertion.assertPass("Batch O2C has been approved at level 2 successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected2 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "1","0");

        Assertion.assertEquals(actual2, expected2);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBO2CTRF15").getExtentCase()).assignCategory(TestCategory.SIT);
        TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
        BusinessValidator.validateStocks(TransactionVO);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITBO2CTRF16").getExtentCase()).assignCategory(TestCategory.SIT);
        BusinessValidator.validateUserBalances(TransactionVO);


        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_02_BatchO2CTransferBlankFile(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName){
        final String methodName = "TC_02_BatchO2CTransferBlankFile";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF4");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF26");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferBlankFile(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch O2C with Blank file is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);


        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.norecordinfile",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_03_BatchO2CTransferBlankMSISDN(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_03_BatchO2CTransferBlankMSISDN";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF5");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF27");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferBlankMSISDN(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch O2C with Blank MSISDN is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.invaliddata",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_04_BatchO2CTransferBlankPaymentType(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_04_BatchO2CTransferBlankPaymentType";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF6");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF28");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferBlankPaymentType(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch FOC with Blank Payment Type is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.missingpaymenttype",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_05_BatchO2CTransferBatchApprove(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_05_BatchO2CTransferBatchApprove";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF7");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBO2CTRF29");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchO2CTrf.batchLevel1ApprovalBatchApprove(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2capprove.msg.bachprocessfailedsuccess", "1");

        Assertion.assertPass("Batch O2C with Batch Approve is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_06_BatchO2CTransferBatchReject(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_06_BatchO2CTransferBatchReject";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF8");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBO2CTRF30");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchO2CTrf.batchLevel1ApprovalBatchReject(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2creject.msg.bachprocesssuccessfully", "1");

        Assertion.assertPass("Batch O2C with Batch Reject is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_07_BatchO2CTransferProcessFileReject(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_07_BatchO2CTransferProcessFileReject";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF9");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBO2CTRF31");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchO2CTrf.batchLevel1RejectProcessFile(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "1", "0");

        Assertion.assertPass("Batch FOC with Batch Reject is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_08_BatchO2CTransferProcessFileDiscard(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_08_BatchO2CTransferProcessFileDiscard";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF10");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBO2CTRF32");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
        String expected1;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransfer(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchO2CTrf.batchLevel1DiscardProcessFile(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "0", "1");

        Assertion.assertPass("Batch O2C with Batch Discard is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_09_BatchO2CTransferInvalidFileFormat(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_09_BatchO2CTransferInvalidFileFormat";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF11");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF33");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferInvalidFileFormat(userMSISDN, productCode, quantity, remarks, category, domain);
        String actual = map.get("INITIATE_MESSAGE");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("uploadfile.error.notrequiredcontent");

        Assertion.assertPass("Batch O2C with Invalid File is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_10_BatchO2CTransferInvalidBatchName(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_10_BatchO2CTransferInvalidBatchName";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF12");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF34");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferInvalidBatchName(userMSISDN, productCode, quantity, remarks, category, domain);
        String actual = map.get("INITIATE_MESSAGE");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batchpointadjust.selectcategoryforbatchpointadjust.maskmsg.batchname");

        Assertion.assertPass("Batch O2C with Invalid Batch Name is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_11_BatchO2CTransferInSuspendedCU(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException, ParseException, SQLException {
        final String methodName = "TC_11_BatchO2CTransferInSuspendedCU";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF17");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF35");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
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
            ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
            Map<String, String> map = BatchO2CTrf.BatchInitiateTransferInSuspendedCU(userMSISDN, productCode, quantity, remarks, category, domain);
            Assertion.assertPass("Batch O2C with In Suspended user is processed successfully.");
            currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
            String expectedMessage = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.userinsuspend");
            String actualMessage = map.get("INITIATE_MESSAGE");
            Log.info("Message fetched from WEB as : "+actualMessage);
            Assertion.assertEquals(actualMessage, expectedMessage);
        } catch(Exception e){
            Assertion.assertFail("Batch O2C Transfer initiation is successful, hence Test Case failed");
        }
        modificationMap.put("inSuspend_chk", "false");
        ExtentI.Markup(ExtentColor.TEAL, "Removing In Suspended Status from Channel User");
        chnlUsr.modifyChannelUserDetails(category, modificationMap);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_12_BatchO2CTransferNegativeAmount(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName)throws InterruptedException{
        final String methodName = "TC_12_BatchO2CTransferNegativeAmount";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF19");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF36");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferNegativeAmount(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batchdirectpayout.processuploadedfile.error.qtynonnumeric",batchId, batchName, "1");

        Assertion.assertPass("Batch O2C with Negative Amount is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_13_BatchO2CTransferAlphanumericExtTxnNo(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName)throws InterruptedException{
        final String methodName = "TC_13_BatchO2CTransferAlphanumericExtTxnNo";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF20");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF37");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferAlphanumericExtTxnNo(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.batchapprovereject.msg.error.externaltxnnotnumeric",batchId, batchName, "1");

        Assertion.assertPass("Batch O2C with Alphabetic External Transaction Number is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_14_BatchO2CTransferAlphanumericAmount(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName)throws InterruptedException{
        final String methodName = "TC_14_BatchO2CTransferAlphanumericAmount";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF21");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF38");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferAlphanumericAmount(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch O2C with Alphanumeric Amount is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.qtynumeric1",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_15_BatchO2CTransferInvalidPaymentType(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName)throws InterruptedException{
        final String methodName = "TC_15_BatchO2CTransferInvalidPaymentType";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF22");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF39");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferInvalidPaymentType(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        Assertion.assertPass("Batch O2C with Invalid Payment Type is processed successfully.");
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.paymenttype",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    /*  @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_12_BatchO2CTransferTCPSuspended(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException, ParseException, SQLException {
        final String methodName = "TC_12_BatchO2CTransferTCPSuspended";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF18");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
        TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);


        ChannelUser chnlUsr = new ChannelUser(driver);
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");
        HashMap<String, String> modificationMap = new HashMap<>();

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        String TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, 1);

        ExtentI.Markup(ExtentColor.TEAL, "Suspending Transfer Control Profile");
        trfCntrlProf.channelLevelTransferControlProfileSuspend(0, domain, category, TCPName, "NULL");

        try{
            ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
            Map<String, String> map = BatchO2CTrf.BatchInitiateTransferInSuspendedCU(userMSISDN, productCode, quantity, remarks, category, domain);
            String expectedMessage = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.userinsuspend");
            String actualMessage = map.get("INITIATE_MESSAGE");
            Log.info("Message fetched from WEB as : "+actualMessage);
            Assertion.assertEquals(actualMessage, expectedMessage);
        } catch(Exception e){
            Assertion.assertFail("Batch O2C Transfer initiation is successful, hence Test Case failed");
        }
        modificationMap.put("inSuspend_chk", "false");
        ExtentI.Markup(ExtentColor.TEAL, "Removing In Suspended Status from Channel User");
        chnlUsr.modifyChannelUserDetails(category, modificationMap);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    } */


/*
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_11_BatchO2CTransferViaExternalCode(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_11_BatchO2CTransferViaExternalCode";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF14");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITBO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITBO2CTRF3");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);
        String expected1;
        String expected2;
        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferViaExternalCode(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.msg.success",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);

        map = BatchO2CTrf.batchLevel1ApprovalProcessFile(batchId);
        expected1 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "1", "0");

        Assertion.assertEquals(map.get("actualMessage"), expected1);

        currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.PREREQUISITE);
        String actual2 = BatchO2CTrf.batchLevel2ApprovalProcessFile(batchId, quantity);
        expected2 = MessagesDAO.prepareMessageByKey("batcho2c.batcho2cprocessfile.msg.bachprocessfilesuccessfully", "1","0");

        Assertion.assertEquals(actual2, expected2);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }*/




    /*@Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void TC_11_BatchO2CTransferInvalidExtTxnDate(String parentCategory, String category, String userMSISDN,String domain, String productType,String productCode, String productName) throws InterruptedException {
        final String methodName = "TC_11_BatchO2CTransfernInvalidExtTxnDate";
        Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITBO2CTRF13");

        BatchO2CTransfer BatchO2CTrf = new BatchO2CTransfer(driver);

        String quantity = _masterVO.getProperty("Quantity");
        String remarks = _masterVO.getProperty("Remarks");

        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType)).assignCategory(TestCategory.SIT);
        Map<String, String> map = BatchO2CTrf.BatchInitiateTransferInvalidExtTxnDate(userMSISDN, productCode, quantity, remarks, category, domain);
        String batchId = map.get("BATCH_ID");
        String actual = map.get("INITIATE_MESSAGE");
        String batchName = map.get("BATCH_NAME");
        String expected;

        expected = MessagesDAO.prepareMessageByKey("batcho2c.processuploadedfile.error.invaliddata",batchId, batchName, "1");

        Assertion.assertContainsEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }*/





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
