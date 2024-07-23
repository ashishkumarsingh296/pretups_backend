package com.Features;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.batchFOCTransfer.BatchInitiateFOCTransferPage;
import com.pageobjects.channeladminpages.batchFOCTransfer.FOCBatchApprovalLevel1Page;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel2Page;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage3;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.O2CTransferSubLink;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.*;
import org.openqa.selenium.WebDriver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BatchFOCTransfer {
    WebDriver driver;

    Login login1;
    ChannelAdminHomePage caHomepage;
    BatchInitiateFOCTransferPage initiateBatchFOCPage;
    InitiateO2CTransferPage3 initiateO2CPage3;

    FOCBatchApprovalLevel1Page FOCBatchApprovalLevel1Page;

    ApproveLevel2Page approveLevel2Page;

    ChannelUserHomePage CUHomePage;
    O2CTransferSubLink CU_O2CTransfer;

    RandomGeneration randmGenrtr;
    SelectNetworkPage ntwrkPage;
    Map<String, String> userInfo;
    Map<String, String> ResultMap;

    String filepath= _masterVO.getProperty("BatchFOCTransferPath");
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Date dateObj = new Date();
    String currentDate = df.format(dateObj);
    RandomGeneration rand = new RandomGeneration();

    public BatchFOCTransfer(WebDriver driver) {
        this.driver = driver;
        login1 = new Login();
        caHomepage = new ChannelAdminHomePage(driver);
        initiateBatchFOCPage = new BatchInitiateFOCTransferPage(driver);
        initiateO2CPage3 = new InitiateO2CTransferPage3(driver);
        FOCBatchApprovalLevel1Page = new FOCBatchApprovalLevel1Page(driver);
        CUHomePage = new ChannelUserHomePage(driver);
        CU_O2CTransfer = new O2CTransferSubLink(driver);
        approveLevel2Page = new ApproveLevel2Page(driver);
        randmGenrtr = new RandomGeneration();
        ntwrkPage = new SelectNetworkPage(driver);
        userInfo= new HashMap();
        ResultMap = new HashMap();
    }

    public Map<String, String> BatchInitiateFOCTransfer(String userMSISDN, String productName, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateFOCTransfer";
        Log.methodEntry(methodName, userMSISDN, productName, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible();
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productName);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(externalCode, 1, 3);
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 4); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 5); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchFOCPage.getMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> batchLevel1ApprovalProcessFile(String batchID){
        final String methodName = "batchLevel1ApprovalProcessFile";
        Log.methodEntry(methodName, batchID);

        userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE1_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clicBatchFOCApprovalLevel1();

        FOCBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        FOCBatchApprovalLevel1Page.clickSubmitBtn();

        FOCBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("Y", 1, 15);   //Approval Remarks
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        FOCBatchApprovalLevel1Page.uploadFile(uploadPath);
        FOCBatchApprovalLevel1Page.clicktoprocessfile();

        FOCBatchApprovalLevel1Page.enterLanguage1();
        FOCBatchApprovalLevel1Page.enterLanguage2();
        FOCBatchApprovalLevel1Page.clicktoprocessfile();

        ResultMap.put("actualMessage", FOCBatchApprovalLevel1Page.getMessage());

        Log.methodExit(methodName);
        return ResultMap;
    }

    public String batchLevel2ApprovalProcessFile(String batchID, String quantity){
        final String methodName = "batchLevel2ApprovalProcessFile";
        Log.methodEntry(methodName, batchID, quantity);

        userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE2_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickBatchFOCApprovalLevel2();

        FOCBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        FOCBatchApprovalLevel1Page.clickSubmitBtn();

        FOCBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("Y", 1, 17);   //Approval Remarks
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        FOCBatchApprovalLevel1Page.uploadFile(uploadPath);
        FOCBatchApprovalLevel1Page.clicktoprocessfile();

        FOCBatchApprovalLevel1Page.enterLanguage1();
        FOCBatchApprovalLevel1Page.enterLanguage2();

        FOCBatchApprovalLevel1Page.clicktoprocessfile();
        String actualMessage= approveLevel2Page.getMessage();

        Log.methodExit(methodName);
        return actualMessage;
    }


    public Map<String, String> BatchInitiateTransferBlankFile(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain){
        final String methodName = "BatchInitiateTransferBlankFile";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTB" + rand.randomNumeric(7);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productType);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchFOCPage.getErrorMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferBlankMSISDN(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferBlankMSISDN";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productType);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 4); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 5); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchFOCPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchFOCPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }


    public Map<String, String> BatchInitiateTransfer(String userMSISDN, String productName, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransfer";
        Log.methodEntry(methodName, userMSISDN, productName, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible();
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productName);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(externalCode, 1, 3);
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 4); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 5); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchFOCPage.getMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }


    public Map<String, String> batchLevel1ApprovalBatchApprove(String batchID){
        final String methodName = "batchLevel1ApprovalBatchApprove";
        Log.methodEntry(methodName, batchID);

        userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE1_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clicBatchFOCApprovalLevel1();

        FOCBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        FOCBatchApprovalLevel1Page.clickSubmitBtn();
        FOCBatchApprovalLevel1Page.clicktobatchapprove();

        FOCBatchApprovalLevel1Page.enterLanguage1();
        FOCBatchApprovalLevel1Page.enterLanguage2();
        FOCBatchApprovalLevel1Page.clickApproveButton();
        driver.switchTo().alert().accept();

        ResultMap.put("actualMessage", FOCBatchApprovalLevel1Page.getMessage());

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> batchLevel1ApprovalBatchReject(String batchID){
        final String methodName = "batchLevel1ApprovalBatchReject";
        Log.methodEntry(methodName, batchID);

        userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE1_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clicBatchFOCApprovalLevel1();

        FOCBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        FOCBatchApprovalLevel1Page.clickSubmitBtn();
        FOCBatchApprovalLevel1Page.clicktobatchreject();

        FOCBatchApprovalLevel1Page.enterLanguage1();
        FOCBatchApprovalLevel1Page.enterLanguage2();
        FOCBatchApprovalLevel1Page.clickRejectButton();
        driver.switchTo().alert().accept();

        ResultMap.put("actualMessage", FOCBatchApprovalLevel1Page.getMessage());

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> batchLevel1RejectProcessFile(String batchID){
        final String methodName = "batchLevel1RejectProcessFile";
        Log.methodEntry(methodName, batchID);

        userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE1_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clicBatchFOCApprovalLevel1();

        FOCBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        FOCBatchApprovalLevel1Page.clickSubmitBtn();

        FOCBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("N", 1, 15);   //Approval Remarks
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        FOCBatchApprovalLevel1Page.uploadFile(uploadPath);
        FOCBatchApprovalLevel1Page.clicktoprocessfile();

        FOCBatchApprovalLevel1Page.enterLanguage1();
        FOCBatchApprovalLevel1Page.enterLanguage2();
        FOCBatchApprovalLevel1Page.clicktoprocessfile();

        ResultMap.put("actualMessage", FOCBatchApprovalLevel1Page.getMessage());

        Log.methodExit(methodName);
        return ResultMap;
    }


    public Map<String, String> batchLevel1DiscardProcessFile(String batchID){
        final String methodName = "batchLevel1DiscardProcessFile";
        Log.methodEntry(methodName, batchID);

        userInfo= UserAccess.getUserWithAccess(RolesI.APPROVE1_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clicBatchFOCApprovalLevel1();

        FOCBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        FOCBatchApprovalLevel1Page.clickSubmitBtn();

        FOCBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("D", 1, 35);   //Approval Remarks
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        FOCBatchApprovalLevel1Page.uploadFile(uploadPath);
        FOCBatchApprovalLevel1Page.clicktoprocessfile();

        FOCBatchApprovalLevel1Page.enterLanguage1();
        FOCBatchApprovalLevel1Page.enterLanguage2();
        FOCBatchApprovalLevel1Page.clicktoprocessfile();

        ResultMap.put("actualMessage", FOCBatchApprovalLevel1Page.getMessage());

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferInvalidFileFormat(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain){
        final String methodName = "BatchInitiateTransferInvalidFileFormat";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);
        String PNGFile = System.getProperty("user.dir")+_masterVO.getProperty("PNGFile");
        String batchName = "AUTBatch" + rand.randomNumeric(7);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productType);
        }
        initiateBatchFOCPage.enterBatchName(batchName);
        initiateBatchFOCPage.uploadFile(PNGFile);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchFOCPage.getErrorMessage();
        ResultMap.put("INITIATE_MESSAGE", message);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferInvalidBatchName(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferInvalidBatchName";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + ";;*&^%$&" ;

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productType);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);

        initiateBatchFOCPage.enterBatchName(batchName);
        initiateBatchFOCPage.clickSubmitButton();

        String message= initiateBatchFOCPage.getErrorMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }


    public Map<String, String> BatchInitiateTransferInSuspendedCU(String userMSISDN, String productName, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferInSuspendedCU";
        Log.methodEntry(methodName, userMSISDN, productName, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();
        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible();
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productName);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(externalCode, 1, 3);
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 4); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 5); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchFOCPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchFOCPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferNegativeAmount(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferNegativeAmount";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();
        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productType);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(externalCode, 1, 3);
        ExcelUtility.setCellDataXLS("-" + _masterVO.getProperty("Quantity"), 1, 4); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 5); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchFOCPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchFOCPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferAlphanumericExtTxnNo(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferAlphanumericExtTxnNo";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = rand.randomAlphaNumeric(10);
        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productType);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(externalCode, 1, 3);
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 4); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 5); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchFOCPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchFOCPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }


    public Map<String, String> BatchInitiateTransferAlphanumericAmount(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferAlphanumericAmount";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();
        String invalidQuantity = rand.randomAlphaNumeric(5);
        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);


        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchFOCTransferLink();
        initiateBatchFOCPage.selectDomain(domain);
        initiateBatchFOCPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchFOCPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchFOCPage.selectProductType1(productType);
        }
        initiateBatchFOCPage.clickDownloadFileTemplate();
        initiateBatchFOCPage.enterBatchName(batchName);
        String latestFileName = initiateBatchFOCPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.FOC_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(externalCode, 1, 3);
        ExcelUtility.setCellDataXLS(invalidQuantity, 1, 4); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 5); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchFOCPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchFOCTransferUpload") + filename;
        initiateBatchFOCPage.uploadFile(uploadPath);
        initiateBatchFOCPage.clickSubmitButton();
        initiateBatchFOCPage.enterLanguage1();
        initiateBatchFOCPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchFOCPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchFOCPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_FOC_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }


}
