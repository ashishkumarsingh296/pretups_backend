package com.Features;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.batchO2CTransfer.BatchInitiateO2CTransferPage;
import com.pageobjects.channeladminpages.batchO2CTransfer.O2CBatchApprovalLevel1Page;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.o2ctransfer.*;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.O2CTransferSubLink;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.*;
import org.openqa.selenium.WebDriver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BatchO2CTransfer {

    WebDriver driver;

    Login login1;
    ChannelAdminHomePage caHomepage;
    BatchInitiateO2CTransferPage initiateBatchO2CPage;
    InitiateO2CTransferPage3 initiateO2CPage3;

    O2CBatchApprovalLevel1Page O2CBatchApprovalLevel1Page;

    ApproveLevel2Page approveLevel2Page;

    ChannelUserHomePage CUHomePage;
    O2CTransferSubLink CU_O2CTransfer;

    RandomGeneration randmGenrtr;
    SelectNetworkPage ntwrkPage;
    Map<String, String> userInfo;
    Map<String, String> ResultMap;

    String filepath=_masterVO.getProperty("BatchO2CTransferPath");
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Date dateObj = new Date();
    String currentDate = df.format(dateObj);
    RandomGeneration rand = new RandomGeneration();


    public BatchO2CTransfer(WebDriver driver) {
        this.driver = driver;
        login1 = new Login();
        caHomepage = new ChannelAdminHomePage(driver);
        initiateBatchO2CPage = new BatchInitiateO2CTransferPage(driver);
        initiateO2CPage3 = new InitiateO2CTransferPage3(driver);
        O2CBatchApprovalLevel1Page = new O2CBatchApprovalLevel1Page(driver);
        CUHomePage = new ChannelUserHomePage(driver);
        CU_O2CTransfer = new O2CTransferSubLink(driver);
        approveLevel2Page = new ApproveLevel2Page(driver);
        randmGenrtr = new RandomGeneration();
        ntwrkPage = new SelectNetworkPage(driver);
        userInfo= new HashMap();
        ResultMap = new HashMap();
    }

    public Map<String, String> BatchInitiateTransfer(String userMSISDN, String productName, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransfer";
        Log.methodEntry(methodName, userMSISDN, productName, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible();
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productName);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("paymentInstrumentTypeCash"), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchO2CPage.getMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
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
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("", 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("paymentInstrumentTypeCash"), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferBlankPaymentType(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferBlankPaymentType";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferBlankFile(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain){
        final String methodName = "BatchInitiateTransferBlankFile";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTB" + rand.randomNumeric(7);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchO2CPage.getErrorMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
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
        caHomepage.clicBatchO2CApprovalLevel1();

        O2CBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        O2CBatchApprovalLevel1Page.clickSubmitBtn();

        O2CBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("Y", 1, 35);   //Approval Remarks
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        O2CBatchApprovalLevel1Page.uploadFile(uploadPath);
        O2CBatchApprovalLevel1Page.clicktoprocessfile();

        O2CBatchApprovalLevel1Page.enterLanguage1();
        O2CBatchApprovalLevel1Page.enterLanguage2();
        O2CBatchApprovalLevel1Page.clicktoprocessfile();

        ResultMap.put("actualMessage", O2CBatchApprovalLevel1Page.getMessage());

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
        caHomepage.clicBatchO2CApprovalLevel1();

        O2CBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        O2CBatchApprovalLevel1Page.clickSubmitBtn();

        O2CBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("N", 1, 35);   //Approval Remarks
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        O2CBatchApprovalLevel1Page.uploadFile(uploadPath);
        O2CBatchApprovalLevel1Page.clicktoprocessfile();

        O2CBatchApprovalLevel1Page.enterLanguage1();
        O2CBatchApprovalLevel1Page.enterLanguage2();
        O2CBatchApprovalLevel1Page.clicktoprocessfile();

        ResultMap.put("actualMessage", O2CBatchApprovalLevel1Page.getMessage());

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
        caHomepage.clicBatchO2CApprovalLevel1();

        O2CBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        O2CBatchApprovalLevel1Page.clickSubmitBtn();

        O2CBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("D", 1, 35);   //Approval Remarks
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        O2CBatchApprovalLevel1Page.uploadFile(uploadPath);
        O2CBatchApprovalLevel1Page.clicktoprocessfile();

        O2CBatchApprovalLevel1Page.enterLanguage1();
        O2CBatchApprovalLevel1Page.enterLanguage2();
        O2CBatchApprovalLevel1Page.clicktoprocessfile();

        ResultMap.put("actualMessage", O2CBatchApprovalLevel1Page.getMessage());

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
        caHomepage.clicBatchO2CApprovalLevel1();

        O2CBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        O2CBatchApprovalLevel1Page.clickSubmitBtn();
        O2CBatchApprovalLevel1Page.clicktobatchapprove();

        O2CBatchApprovalLevel1Page.enterLanguage1();
        O2CBatchApprovalLevel1Page.enterLanguage2();
        O2CBatchApprovalLevel1Page.clickApproveButton();
        driver.switchTo().alert().accept();

        ResultMap.put("actualMessage", O2CBatchApprovalLevel1Page.getMessage());

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
        caHomepage.clicBatchO2CApprovalLevel1();

        O2CBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        O2CBatchApprovalLevel1Page.clickSubmitBtn();
        O2CBatchApprovalLevel1Page.clicktobatchreject();

        O2CBatchApprovalLevel1Page.enterLanguage1();
        O2CBatchApprovalLevel1Page.enterLanguage2();
        O2CBatchApprovalLevel1Page.clickRejectButton();
        driver.switchTo().alert().accept();

        ResultMap.put("actualMessage", O2CBatchApprovalLevel1Page.getMessage());

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
        caHomepage.clickBatchO2CApprovalLevel2();

        O2CBatchApprovalLevel1Page.selectBatchtoApprovelevel1(batchID);
        O2CBatchApprovalLevel1Page.clickSubmitBtn();

        O2CBatchApprovalLevel1Page.clickdownloadfileforapproval();
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS("Y", 1, 35);   //Approval Remarks
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        O2CBatchApprovalLevel1Page.uploadFile(uploadPath);
        O2CBatchApprovalLevel1Page.clicktoprocessfile();

        O2CBatchApprovalLevel1Page.enterLanguage1();
        O2CBatchApprovalLevel1Page.enterLanguage2();

        O2CBatchApprovalLevel1Page.clicktoprocessfile();
        String actualMessage= approveLevel2Page.getMessage();

        Log.methodExit(methodName);
        return actualMessage;
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
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.enterBatchName(batchName);
        initiateBatchO2CPage.uploadFile(PNGFile);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchO2CPage.getErrorMessage();
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
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);

        initiateBatchO2CPage.enterBatchName(batchName);
        initiateBatchO2CPage.clickSubmitButton();

        String message= initiateBatchO2CPage.getErrorMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferViaExternalCode(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain){
        final String methodName = "BatchInitiateTransferViaExternalCode";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        String externalCode = DBHandler.AccessHandler.getExternalCodeFromMsisdn(userMSISDN);
        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("paymentInstrumentTypeCash"), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(externalCode,1,4); //externalCode
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + "External Code: " +externalCode+ ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();

        String message= initiateBatchO2CPage.getMessage();
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }


    public Map<String, String> BatchInitiateTransferInvalidExtTxnDate(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferInvalidExtTxnDate";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);
        String invalidDate = rand.randomNumeric(6);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = rand.randomNumberWithoutZero(10);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(invalidDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS("DD", 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + invalidDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }


    public Map<String, String> BatchInitiateTransferInSuspendedCU(String userMSISDN, String productName, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferInSuspendedCU";
        Log.methodEntry(methodName, userMSISDN, productName, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible();
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productName);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("paymentInstrumentTypeCash"), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferNegativeAmount(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferNegativeAmount";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = UniqueChecker.UC_EXT_TXN_NO();

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("paymentInstrumentTypeCash"), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS("-" + _masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }

    public Map<String, String> BatchInitiateTransferAlphanumericExtTxnNo(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferAlphanumericExtTxnNo";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = rand.randomAlphaNumeric(10);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("paymentInstrumentTypeCash"), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
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

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("paymentInstrumentTypeCash"), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(invalidQuantity, 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }



    public Map<String, String> BatchInitiateTransferInvalidPaymentType(String userMSISDN, String productType, String Quantity, String Remarks, String category, String domain) throws InterruptedException {
        final String methodName = "BatchInitiateTransferInvalidPaymentType";
        Log.methodEntry(methodName, userMSISDN, productType, Quantity, Remarks);

        String batchName = "AUTBatch" + rand.randomNumeric(7);
        String extTrfNo = rand.randomNumeric(10);

        userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_BATCH_O2CTRANSFER);
        login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
        ntwrkPage.selectNetwork();
        caHomepage.clickOperatorToChannel();
        caHomepage.clickInitiateBatchO2CTransferLink();
        initiateBatchO2CPage.selectDomain(domain);
        initiateBatchO2CPage.selectCategory(category);
        boolean selectDropdownVisible = initiateBatchO2CPage.isSelectProductTypeVisible() ;
        if(selectDropdownVisible) {
            initiateBatchO2CPage.selectProductType1(productType);
        }
        initiateBatchO2CPage.clickDownloadFileTemplate();
        initiateBatchO2CPage.enterBatchName(batchName);
        String latestFileName = initiateBatchO2CPage.getLatestFilePathfromDir(filepath);
        ExcelUtility.setExcelFileXLS(latestFileName,ExcelI.O2C_TEMPLATE);
        Log.info("Writing to excel.. ");
        ExcelUtility.setCellDataXLS(userMSISDN, 1, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(extTrfNo, 1, 1);   //External Transfer Number
        ExcelUtility.setCellDataXLS(currentDate, 1, 2);   //External Transfer Date
        ExcelUtility.setCellDataXLS(rand.randomAlphabets(6), 1, 3); // Payment Type
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Quantity"), 1, 5); //Amount
        ExcelUtility.setCellDataXLS(_masterVO.getProperty("Remarks"), 1, 6); //Remarks
        Log.info("Written to Excel : MSISDN: " + userMSISDN + ", External Transfer Number: " + extTrfNo + ", External Transfer Date: " + currentDate + ", Payment Type: " + _masterVO.getProperty("paymentInstrumentTypeCash") + ", Amount: " + _masterVO.getProperty("Quantity") + ", Remarks: " +_masterVO.getProperty("Remarks"));
        String filename = initiateBatchO2CPage.getLatestFileNamefromDir(filepath);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("BatchO2CTransferUpload") + filename;
        initiateBatchO2CPage.uploadFile(uploadPath);
        initiateBatchO2CPage.clickSubmitButton();
        initiateBatchO2CPage.enterLanguage1();
        initiateBatchO2CPage.enterLanguage2();

        initiateO2CPage3.clickConfirmButton();
        initiateBatchO2CPage.clickViewErrors();
        SwitchWindow.switchwindow(driver);

        String message= initiateBatchO2CPage.getViewErrorMessage();
        SwitchWindow.backwindow(driver);
        ResultMap.put("INITIATE_MESSAGE", message);
        ResultMap.put("BATCH_ID", _parser.getBatchID(message, _masterVO.getClientDetail("BATCH_O2C_BATCH_ID")));
        ResultMap.put("BATCH_NAME", batchName);

        Log.methodExit(methodName);
        return ResultMap;
    }



}
