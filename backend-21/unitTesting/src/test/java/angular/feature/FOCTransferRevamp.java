package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.FOCTransfers.FOCTransfers ;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.*;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FOCTransferRevamp extends BaseTest {


    public WebDriver driver;
    LoginRevamp login;
    FOCTransfers FOCTransfers;
    Map<String, String> ResultMap;

    public FOCTransferRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        FOCTransfers = new FOCTransfers(driver);
        ResultMap = new HashMap<String, String>();
    }


    RandomGeneration RandomGeneration = new RandomGeneration();

    public Map<String, String> PerformFOCTransferByMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferByMSISDN";
        String quantity = _masterVO.getProperty("Quantity");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
//        String searchBy = "Mobile Number " ;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait();
        FOCTransfers.spinnerWait();
        FOCTransfers.clickOPTO2CHeading();
        if(FOCTransfers.isFOCCommissionVisible()) {
           FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        Log.info(productName);

        //FOCTransfers.enterQuantityforFOCRevamp() ;
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);
        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC to: " + chCategoryName + " User : " + chMsisdn) ;

        FOCTransfers.clickTransferButton();
        FOCTransfers.enterPin(opPin);
        FOCTransfers.clickRechargeIcon();
        String expectedmessage = "Transfer Request Initiated";
        String FOCStatus = "NEW";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCTransactionID();
            Log.info("Transaction ID : "+transactionID) ;
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Transfer Transaction message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                FOCTransfers.clickDoMoreTransfers();
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ResultMap.put("INITIATE_MESSAGE", transferStatus);
        return ResultMap;
    }

    public Map<String, String> PerformFOCTransferByLoginID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferByLoginID";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
//        String searchBy = "Mobile Number " ;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null,  Domain = null;
        FOCTransfers.spinnerWait() ;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
       // FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait() ;
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCLoginIDBuyer"));
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String geographicalDomainName = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        String parentGeographicDomainCode = DBHandler.AccessHandler.getParentGeographicDomainCode(geographicalDomainName);
        String getGeographicDomainName = DBHandler.AccessHandler.getGeographicDomainName(parentGeographicDomainCode);
        FOCTransfers.selectFOCGeography(getGeographicDomainName);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        FOCTransfers.selectDomain(Domain);
        FOCTransfers.selectCategory(chCategoryName);
        String LoginID = DBHandler.AccessHandler.getLoginidFromMsisdn(chMsisdn);
        FOCTransfers.enterLoginID(LoginID);
        FOCTransfers.clickProceedButton();

        FOCTransfers.clickProceedButton();
        Log.info(productName);

       // FOCTransfers.enterQuantityforFOCRevamp() ;
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);
        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC to: " + chCategoryName + " User : " + chMsisdn) ;

        FOCTransfers.clickTransferButton();
        FOCTransfers.enterPin(opPin);
        FOCTransfers.clickRechargeIcon();
        String expectedmessage = "Transfer Request Initiated";
        String FOCStatus = "NEW";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCTransactionID();
            Log.info("Transaction ID : "+transactionID) ;
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Transfer Transaction message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                FOCTransfers.clickDoMoreTransfers();
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ResultMap.put("INITIATE_MESSAGE", transferStatus);
        return ResultMap;
    }

    public Map<String, String> PerformFOCTransferByUserName(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferByMSISDN";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
//        String searchBy = "Mobile Number " ;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null,  Domain = null;
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        // FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCUserNameBuyer"));
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String geographicalDomainName = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        String parentGeographicDomainCode = DBHandler.AccessHandler.getParentGeographicDomainCode(geographicalDomainName);
        String getGeographicDomainName = DBHandler.AccessHandler.getGeographicDomainName(parentGeographicDomainCode);
        FOCTransfers.selectFOCGeography(getGeographicDomainName);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        FOCTransfers.selectDomain(Domain);
        FOCTransfers.selectCategory(chCategoryName);
        String UserName = DBHandler.AccessHandler.getUsernameFromMsisdn(chMsisdn);
        FOCTransfers.enterUserName(UserName);
        FOCTransfers.clickProceedButton();

        FOCTransfers.clickProceedButton();
        Log.info(productName);

      //  FOCTransfers.enterQuantityforFOCRevamp() ;
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);
        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC to: " + chCategoryName + " User : " + chMsisdn) ;

        FOCTransfers.clickTransferButton();
        FOCTransfers.enterPin(opPin);
        FOCTransfers.clickRechargeIcon();
        String expectedmessage = "Transfer Request Initiated";
        String FOCStatus = "NEW";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCTransactionID();
            Log.info("Transaction ID : "+transactionID) ;
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Transfer Transaction message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                FOCTransfers.clickDoMoreTransfers();
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ResultMap.put("INITIATE_MESSAGE", transferStatus);
        return ResultMap;
    }




    public Boolean PerformFOCReject1(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCReject1";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickApproval1Transaction();
        }
//        FOCTransfers.clickFOCApprovalSingleOperationHeading();

//        Log.info("Geography of User: "+geography);
//        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
//        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
//        geoDomainName = " " + geoDomainName;
//        Log.info("Geography of Parent: " + geoDomainName);
//        FOCTransfers.selectApprovalGeography(geoDomainName);
//        Domain = " " + Domain;
//        FOCTransfers.selectApprovalDomain(Domain);
//        FOCTransfers.selectApprovalCategory(chCategoryName);
//        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickRejectTxnButton();
//        String extTxnNo = RandomGeneration.randomNumeric(6);
//        FOCTransfers.enterExtTxnNo(extTxnNo);
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//        Date dateobj = new Date();
//        String PaymentDate = df.format(dateobj);
//        FOCTransfers.enterApprovalDate(PaymentDate);
//        String Reference = RandomGeneration.randomNumeric(8);
//        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.clickRejectButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Rejected";
        String FOCStatus = "CNCL";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.approvalSuccessfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickApprovalDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformFOCTransferByMSISDNApproval1(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCTransferByMSISDN";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        FOCTransfers.clickApproval1FOCHeading();
        FOCTransfers.clickApproval1Transaction();
        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.selectFOCApprovalTransactions() ;
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Purchase successful";
        String FOCStatus = "NEW";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }

    public Boolean PerformFOCApproval1(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval1";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        FOCTransfers.spinnerWait() ;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.spinnerWait() ;
            FOCTransfers.clickApproval1Transaction();
        }
        FOCTransfers.spinnerWait() ;
        //FOCTransfers.clickFOCApprovalSingleOperationHeading();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC Approval1 to: " + chCategoryName + " User : " + chMsisdn) ;
      /*  String paymentInstrumentNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterPaymentInstrumentNo(paymentInstrumentNo) ;
      */
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String FOCStatusClose = "CLOSE";
        String FOCStatusApprove1 = "APPRV1";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.approvalSuccessfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& (transferStatus.contains(FOCStatusClose)||transferStatus.contains(FOCStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickApprovalDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }

/*


    public Boolean PerformFOCReject1(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCReject1";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }
        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickRejectTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.clickRejectButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Purchase successful";
        String FOCStatus = "NEW";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }

*/

    /*public Boolean PerformFOCApproval2(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval2";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
//        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
//        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval2Visible()) {
            FOCTransfers.clickApproval2FOCHeading();
        }
        else {
            FOCTransfers.clickApproval2FOCHeading();
            FOCTransfers.clickApproval2Transaction();
        }
        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(chMsisdn);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String FOCStatusClose = "CLOSE";
        String FOCStatusApprove = "APPRV2";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& (transferStatus.contains(FOCStatusClose)||transferStatus.contains(FOCStatusApprove))) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }*/
    
    public Boolean PerformFOCApproval2(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
    Boolean finalFlag = false;
    final String methodname = "PerformFOCApproval2";
    Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
    String transferStatus = null, geography = null, Domain = null;
    String Remarks = _masterVO.getProperty("Remarks");
    String MasterSheetPath = _masterVO.getProperty("DataProvider");
//    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
//    login.UserLogin(driver, "Operator", opCategoryName);
    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
    geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
    Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//    Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
    FOCTransfers.spinnerWait() ;
    FOCTransfers.spinnerWait();
    if(FOCTransfers.isFOCApproval2Visible()) {
        FOCTransfers.clickApproval2FOCHeading();
    }
    else {
        FOCTransfers.clickApproval2FOCHeading();
        FOCTransfers.spinnerWait();
        FOCTransfers.clickApproval2Transaction();
    }
    
    FOCTransfers.spinnerWait();
//    FOCTransfers.clickFOCApprovalSingleOperationHeading();

//    Log.info("Geography of User: "+geography);
//    String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
//    String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
//    geoDomainName = " " + geoDomainName;
//    Log.info("Geography of Parent: " + geoDomainName);
//    FOCTransfers.selectApprovalGeography(geoDomainName);
//    Domain = " " + Domain;
//    FOCTransfers.selectApprovalDomain(Domain);
//    FOCTransfers.selectApprovalCategory(chCategoryName);
//    FOCTransfers.clickApprovalProceedButton();
//    FOCTransfers.enterSearch(chMsisdn);
    FOCTransfers.enterSearch(txnId);
//    FOCTransfers.clickApproveTxnButton();
    FOCTransfers.clickApproveTxnButton();
//    String extTxnNo = RandomGeneration.randomNumeric(6);
//    FOCTransfers.enterExtTxnNo(extTxnNo);
//    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//    Date dateobj = new Date();
//    String PaymentDate = df.format(dateobj);
//    FOCTransfers.enterApprovalDate(PaymentDate);
//    String Reference = RandomGeneration.randomNumeric(8);
//    FOCTransfers.enterApprovalReferenceNo(Reference);
//    FOCTransfers.enterRemarks(Remarks);
    FOCTransfers.clickApproveButton();
    FOCTransfers.clickApproveYesButton();
    String expectedmessage = "Transaction Approved";
    String FOCStatusClose = "CLOSE";
    String FOCStatusApprove = "APPRV2";
    String successfulMessage = null;
    boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
    if (successPopUP == true) {
        String transactionID = FOCTransfers.FOCApproveTransactionID();
        successfulMessage = FOCTransfers.approvalSuccessfulMessage();
        transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
        ResultMap.put("TRANSACTION_ID", transactionID);
        if (successfulMessage.contains(expectedmessage)&& (transferStatus.contains(FOCStatusClose)||transferStatus.contains(FOCStatusApprove))) {
            ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 2 message Found as: " + successfulMessage);
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
            finalFlag = true;
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 2 Failed Reason: " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Transfer Approval 2 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        FOCTransfers.clickApprovalDoneButton();
    } else {
        String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
        ExtentI.Markup(ExtentColor.RED, "FOC Approval 2 Failed : " + errorMEssageForFailure);
        currentNode.log(Status.FAIL, "FOC Approval 2 is not successful. Transfer message on WEB: " + successfulMessage);
        ExtentI.attachCatalinaLogs();
        ExtentI.attachScreenShot();
    }

    return finalFlag;
}

    public void performApprovalWithAlphanumericReferenceNumber(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        final String methodname = "performApprovalWithAlphanumericReferenceNumber";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;
        FOCTransfers.spinnerWait();
        if(FOCTransfers.isFOCApproval2Visible()) {
            FOCTransfers.clickApproval2FOCHeading();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickApproval1Transaction();
        }
        
        FOCTransfers.spinnerWait();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickApproveTxnButton();
        FOCTransfers.enterApprovalReferenceNo(RandomGeneration.randomAlphaNumeric(5));
        String actualMsg = FOCTransfers.getApprvalRefNoError();
        String expectedMsg = "Please Enter Digits only.";
        Assertion.assertContainsEquals(actualMsg, expectedMsg);
	    Log.methodExit(methodname);
    }
    
    public void performApprovalWithAlphanumericExternalTxnNumber(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        final String methodname = "performApprovalWithAlphanumericExternalTxnNumber";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;
        FOCTransfers.spinnerWait();
        if(FOCTransfers.isFOCApproval2Visible()) {
            FOCTransfers.clickApproval2FOCHeading();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickApproval1Transaction();
        }
        FOCTransfers.spinnerWait();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickApproveTxnButton();
        FOCTransfers.enterExtTxnNo(RandomGeneration.randomAlphaNumeric(5));
        String actualMsg = FOCTransfers.getExtTxnNoError();
        String expectedMsg = "Please Enter Digits only.";
        Assertion.assertContainsEquals(actualMsg, expectedMsg);
	    Log.methodExit(methodname);
    }
    
    public void performApprovalWithoutExternalTxnNumber(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        final String methodname = "performApprovalWithoutExternalTxnNumber";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;
        FOCTransfers.spinnerWait();
        if(FOCTransfers.isFOCApproval2Visible()) {
            FOCTransfers.clickApproval2FOCHeading();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickApproval1Transaction();
        }
        FOCTransfers.spinnerWait();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickApproveTxnButton();
        FOCTransfers.clickApproveButton();
        String actualMsg = FOCTransfers.getExtTxnNoError();
        String expectedMsg = "External TXN number required.";
        Assertion.assertContainsEquals(actualMsg, expectedMsg);
	    Log.methodExit(methodname);
    }
    
    public void performApprovalWithInvalidSearchByTransactionID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        final String methodname = "performApprovalWithInvalidSearchByTransactionID";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;
        FOCTransfers.spinnerWait();
        if(FOCTransfers.isFOCApproval2Visible()) {
            FOCTransfers.clickApproval2FOCHeading();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickApproval1Transaction();
        }
        FOCTransfers.spinnerWait();
        FOCTransfers.enterSearch(RandomGeneration.randomAlphaNumeric(6));
        String actualMsg = FOCTransfers.getNoRecordsApprvalMsg();
        String expectedMsg = "No matching records found";
        Assertion.assertContainsEquals(actualMsg, expectedMsg);
	    Log.methodExit(methodname);
    }

    
    public Boolean PerformFOCReject2(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCReject2";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval2Visible()) {
            FOCTransfers.clickApproval2FOCHeading();
        }
        else {
            FOCTransfers.clickApproval2FOCHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickApproval2Transaction();
        }
//        FOCTransfers.clickFOCApprovalSingleOperationHeading();
//
//        Log.info("Geography of User: "+geography);
//        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
//        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
//        geoDomainName = " " + geoDomainName;
//        Log.info("Geography of Parent: " + geoDomainName);
//        FOCTransfers.selectApprovalGeography(geoDomainName);
//        Domain = " " + Domain;
//        FOCTransfers.selectApprovalDomain(Domain);
//        FOCTransfers.selectApprovalCategory(chCategoryName);
//        FOCTransfers.clickApprovalProceedButton();
//        FOCTransfers.enterSearch(chMsisdn);
//        FOCTransfers.clickApproveTxnButton();
//        String extTxnNo = RandomGeneration.randomNumeric(6);
//        FOCTransfers.enterExtTxnNo(extTxnNo);
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//        Date dateobj = new Date();
//        String PaymentDate = df.format(dateobj);
//        FOCTransfers.enterApprovalDate(PaymentDate);
//        String Reference = RandomGeneration.randomNumeric(8);
//        FOCTransfers.enterApprovalReferenceNo(Reference);
//        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickRejectTxnButton();
        FOCTransfers.clickRejectButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Rejected";
        String FOCStatus = "CNCL";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.approvalSuccessfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Reject 2 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Reject 2 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Reject 2 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickApprovalDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Reject 2 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Reject 2 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformFOCApproval3(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval3";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait();

        if(FOCTransfers.isFOCApproval3Visible()) {
            FOCTransfers.clickApproval3FOCHeading();
        }
        else {
            FOCTransfers.clickApproval3FOCHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickApproval3Transaction();
        }
//        FOCTransfers.clickFOCApprovalSingleOperationHeading();
//
//        Log.info("Geography of User: "+geography);
//        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
//        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
//        geoDomainName = " " + geoDomainName;
//        Log.info("Geography of Parent: " + geoDomainName);
//        FOCTransfers.selectApprovalGeography(geoDomainName);
//        Domain = " " + Domain;
//        FOCTransfers.selectApprovalDomain(Domain);
//        FOCTransfers.selectApprovalCategory(chCategoryName);
//        FOCTransfers.clickApprovalProceedButton();
//        FOCTransfers.enterSearch(chMsisdn);
//        FOCTransfers.clickApproveTxnButton();
//        String extTxnNo = RandomGeneration.randomNumeric(6);
//        FOCTransfers.enterExtTxnNo(extTxnNo);
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//        Date dateobj = new Date();
//        String PaymentDate = df.format(dateobj);
//        FOCTransfers.enterApprovalDate(PaymentDate);
//        String Reference = RandomGeneration.randomNumeric(8);
//        FOCTransfers.enterApprovalReferenceNo(Reference);
//        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickApproveTxnButton();
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Purchase successful";
        String FOCStatus = "CLOSE";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.approvalSuccessfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 3 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 3 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 3 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickApprovalDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 3 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 3 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformFOCReject3(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCByMSISDN";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval3Visible()) {
            FOCTransfers.clickApproval3FOCHeading();
        }
        else {
            FOCTransfers.clickApproval3FOCHeading();
            FOCTransfers.clickApproval3Transaction();
        }
        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(chMsisdn);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Purchase successful";
        String FOCStatus = "NEW";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



/*
    public Map<String, String> PerformFOCTransferByLoginID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferByLoginID";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Login Id";
        String transferStatus = null, Domain = null;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        Domain = " " + ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCTransactionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCTransactionHeading();
        }
         
        FOCTransfers.clickFOCeTopUPHeading();
        FOCTransfers.selectSearchBy(searchBy);
        FOCTransfers.selectDomain(Domain);
        FOCTransfers.selectCategory(chCategoryName);
        String LoginID = DBHandler.AccessHandler.getLoginidFromMsisdn(chMsisdn);
        FOCTransfers.enterLoginID(LoginID);
        FOCTransfers.clickProceedButton();
        if(productName=="eTopUP")
        {
            FOCTransfers.entereTopUPAmount(quantity);

        }else if(productName=="Post eTopUP"){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.enterAmount(quantity);

        FOCTransfers.selectPaymentMode(paymentInstType);
        FOCTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterPaymentDate(PaymentDate);
        FOCTransfers.clickTransferButton();
        FOCTransfers.enterPin(opPin);
        FOCTransfers.clickRechargeIcon();
        String expectedmessage = "Purchase successful";
        String FOCStatus = "NEW";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& transferStatus.contains(FOCStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Transfer Transaction message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();

                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Transfer Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Transfer Transaction is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ResultMap.put("INITIATE_MESSAGE", transferStatus);
        return ResultMap;
    }

*/



    public void PerformFOCTransferAlphanumericMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productCode, String opPin) {
        final String methodname = "PerformFOCTransferAlphanumericMSISDN";
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productCode);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);

        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        chMsisdn = RandomGeneration.randomAlphaNumeric(5);
        FOCTransfers.enterMSISDN(chMsisdn);
        //FOCTransfers.clickProceedButton();
        String successfulMessage = FOCTransfers.getMsisdnError();
        String expectedMessage = "Please enter a valid mobile number.";
        if (expectedMessage.equals(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Alphanumeric MSISDN error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformFOCTransferBlankMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferBlankMSISDN";
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        chMsisdn = "";
        FOCTransfers.spinnerWait();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        String successfulMessage = FOCTransfers.getMsisdnError();
        String expectedMessage = "Mobile number is required.";
        if (expectedMessage.equals(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank MSISDN error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformFOCTransferInvalidMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferInvalidMSISDN";
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        RandomGeneration RandomGeneration = new RandomGeneration();
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        chMsisdn = RandomGeneration.randomNumeric(5);
        FOCTransfers.spinnerWait();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        String successfulMessage = FOCTransfers.getMsisdnError();
        String expectedMessage = "Please enter a valid mobile number.";
        if (expectedMessage.equals(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformFOCTransferResetButton(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferResetButton";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        Log.info(productName);
       // FOCTransfers.enterQuantityforFOCRevamp() ;
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);
        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC to: " + chCategoryName + " User : " + chMsisdn) ;
        FOCTransfers.clickResetButton();
        FOCTransfers.clickTransferButton();

        Boolean blankeTopAmount = FOCTransfers.getblankeTopUPAmount();
        Boolean blankRemarks = FOCTransfers.getblankRemarks();
        Boolean allFields;
        if (blankeTopAmount && blankRemarks)
            allFields = true;
        else allFields = false;
        //Boolean blankSearchBy = FOCTransfers.getBlankSearchInput();
        if(allFields )
        {
            ExtentI.Markup(ExtentColor.GREEN, "All fields are blank, hence Reset button click successful");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        }
        else{
            currentNode.log(Status.FAIL, "Fields are not blank, hence Reset button failed.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformFOCTransferResetButtonSearchBy(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferResetButtonSearchBy";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        FOCTransfers.spinnerWait();
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        FOCTransfers.clickSearchByResetButton();
        Boolean blanSearchByDropdown = FOCTransfers.getBlankSearchByDropdown() ;
        Boolean blankSearchByInput = FOCTransfers.getBlankSearchInput();
        if(blanSearchByDropdown && blankSearchByInput)
        {
            ExtentI.Markup(ExtentColor.GREEN, "Search fields are blank, hence Reset button click successful");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        }
        else{
            currentNode.log(Status.FAIL, "Fields are not blank, hence Reset button failed.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformFOCTransferAlphabeticReferenceNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferAlphabeticReferenceNo";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.spinnerWait() ;
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        FOCTransfers.spinnerWait() ;
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        Log.info(productName);
        FOCTransfers.enterReferenceNumber("abc");
        String successfulMessage = FOCTransfers.getReferenceError();
        String expectedMessage = " Please Enter Digits only.";
        if (expectedMessage.contains(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Alphabetic Reference Number error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformFOCTransferAlphabeticPaymentInstrumentNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferAlphabeticPaymentInstrumentNo";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = RandomGeneration.randomAlphabets(8);
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCTransactionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCTransactionHeading();
        }
         
        FOCTransfers.clickFOCeTopUPHeading();
        FOCTransfers.selectSearchBy(searchBy);
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.enterAmount(quantity);
        FOCTransfers.selectPaymentMode(paymentInstType);
        FOCTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterPaymentDate(PaymentDate);
        FOCTransfers.clickTransferButton();
        String successfulMessage = FOCTransfers.getPmtInstNoError();
        String expectedMessage = " Invalid Payment Instrument Number.";
        if (expectedMessage.contains(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Alphabetic Payment Instrument Number error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformFOCTransferBlankAmount(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferBlankAmount";
        String quantity = "";
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.spinnerWait() ;
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait() ;
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        FOCTransfers.clickTransferButton();
        String successfulMessage = FOCTransfers.getAmountError();
        String expectedMessage = " Amount is required.";
        if (expectedMessage.contains(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Amount error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformFOCTransferBlankRemarks(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferBlankRemarks";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = "";
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.spinnerWait() ;
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        FOCTransfers.spinnerWait() ;
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        Log.info(productName);

        FOCTransfers.enterQuantityforFOCRevamp() ;
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.clickTransferButton();
        String successfulMessage = FOCTransfers.getRemarksError();
        String expectedMessage = "Remarks Required.";
        if (expectedMessage.contains(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Remarks error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformFOCTransferNegativeAmount(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferNegativeAmount";
        String quantity = "-" + _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        FOCTransfers.spinnerWait() ;
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);
        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC to: " + chCategoryName + " User : " + chMsisdn) ;

        FOCTransfers.clickTransferButton();
        String successfulMessage = null;
        if(productName.equals("eTopUP"))
        {
            successfulMessage = FOCTransfers.getNegativeETopupAmountError();

        }else if(productName.equals("Post eTopUP")){
            successfulMessage = FOCTransfers.getNegativePosteTopUpAmountError();
        }
        String expectedMessage = "Invalid Amount.";
        if (expectedMessage.contains(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Amount error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformFOCTransferBlankPIN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferBlankPIN";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        FOCTransfers.spinnerWait() ;
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        Log.info(productName);

       // FOCTransfers.enterQuantityforFOCRevamp() ;
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);
        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC to: " + chCategoryName + " User : " + chMsisdn) ;

        FOCTransfers.clickTransferButton();
        Boolean confirmButtonDisabled = FOCTransfers.checkDisabledRechargeButton();
        if(confirmButtonDisabled)
        {
            ExtentI.Markup(ExtentColor.GREEN, "Confirm PIN button is disabled for blank PIN in FOC Transfer");
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else{
            ExtentI.Markup(ExtentColor.RED, "FOC Transfer PIN Confirm Button is not disabled successfully with blank PIN");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformFOCTransferInvalidPIN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferInvalidPIN";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        //FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait() ;
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCMobileBuyer"));
        FOCTransfers.enterMSISDN(chMsisdn);
        FOCTransfers.clickProceedButton();
        Log.info(productName);

        //FOCTransfers.enterQuantityforFOCRevamp() ;
        if(productName.equals("eTopUP"))
        {
            FOCTransfers.entereTopUPAmount(quantity);
        }else if(productName.equals("Post eTopUP")){
            FOCTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterReferenceNumber(Reference);
        FOCTransfers.enterRemarks("Remarks entered for FOC to: " + chCategoryName + " User : " + chMsisdn) ;

        FOCTransfers.clickTransferButton();
        opPin = RandomGeneration.randomNumeric(4);
        FOCTransfers.enterPin(opPin);
        FOCTransfers.clickRechargeIcon();
        String expectedMessage = "The PIN you have entered is incorrect.";
        String successfulMessage = FOCTransfers.transferStatusFailed();
        if(expectedMessage.contains(successfulMessage))
        {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Invalid PIN");
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else{
            ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Invalid PIN");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformFOCTransferBlankLoginID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferBlankLoginID";
//        String searchBy = "Login Id";
//        String Domain;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.spinnerWait();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        // FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCLoginIDBuyer"));
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
//        String geographicalDomainName = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
//        String parentGeographicDomainCode = DBHandler.AccessHandler.getParentGeographicDomainCode(geographicalDomainName);
//        String getGeographicDomainName = DBHandler.AccessHandler.getGeographicDomainName(parentGeographicDomainCode);
//        FOCTransfers.selectFOCGeography(getGeographicDomainName);
//        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        FOCTransfers.selectDomain(Domain);
//        FOCTransfers.selectCategory(chCategoryName);
        FOCTransfers.clickProceedButton();
        String actualMessage = FOCTransfers.getFOCGeographyError();
        String expectedMessage = "Geography is required.";
        Assertion.assertContainsEquals(expectedMessage, actualMessage);
        actualMessage = FOCTransfers.getDomainError();
        expectedMessage = "Domain is required.";
        Assertion.assertContainsEquals(expectedMessage, actualMessage);
        actualMessage = FOCTransfers.getCategoryError();
        expectedMessage = "User Category is required.";
        Assertion.assertContainsEquals(expectedMessage, actualMessage);
        actualMessage = FOCTransfers.getBlankLoginIDErrorMessage();
        expectedMessage = "Login ID is required";
        Assertion.assertContainsEquals(expectedMessage, actualMessage);

//        String successfulMessage = FOCTransfers.getBlankLoginIDErrorMessage();
//        String expectedMessage = "Login ID is required";
//        if (expectedMessage.contains(successfulMessage)) {
//            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
//            ExtentI.attachCatalinaLogsForSuccess();
//        } else {
//            currentNode.log(Status.FAIL, "Blank Login ID error not shown");
//            ExtentI.attachCatalinaLogs();
//            ExtentI.attachScreenShot();
//        }
        Log.methodExit(methodname);
    }


    public void PerformFOCTransferBlankUsername(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferBlankUsername";
//        String searchBy = "Login Id";
//        String Domain;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.spinnerWait() ;
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        // FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait() ;
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCUserNameBuyer"));
//        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
//        String geographicalDomainName = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
//        String parentGeographicDomainCode = DBHandler.AccessHandler.getParentGeographicDomainCode(geographicalDomainName);
//        String getGeographicDomainName = DBHandler.AccessHandler.getGeographicDomainName(parentGeographicDomainCode);
//        FOCTransfers.selectFOCGeography(getGeographicDomainName);
//        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        FOCTransfers.selectDomain(Domain);
//        FOCTransfers.selectCategory(chCategoryName);
//        String UserName = DBHandler.AccessHandler.getUsernameFromMsisdn(chMsisdn);
        
        FOCTransfers.clickProceedButton();
        String actualMessage = FOCTransfers.getFOCGeographyError();
        String expectedMessage = "Geography is required.";
        Assertion.assertContainsEquals(expectedMessage, actualMessage);
        actualMessage = FOCTransfers.getDomainError();
        expectedMessage = "Domain is required.";
        Assertion.assertContainsEquals(expectedMessage, actualMessage);
        actualMessage = FOCTransfers.getCategoryError();
        expectedMessage = "User Category is required.";
        Assertion.assertContainsEquals(expectedMessage, actualMessage);
        actualMessage = FOCTransfers.getUsernameErrorMessage();
        expectedMessage = "User name is required.";
        
//        Assertion.assertContainsEquals(expectedMessage, actualMessage);
//        String successfulMessage = FOCTransfers.getLoginIDErrorMessage();
//        String expectedMessage = "User name is required.";
//        if (expectedMessage.contains(successfulMessage)) {
//            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
//            ExtentI.attachCatalinaLogsForSuccess();
//        } else {
//            currentNode.log(Status.FAIL, "Blank Username error not shown");
//            ExtentI.attachCatalinaLogs();
//            ExtentI.attachScreenShot();
//        }
        Log.methodExit(methodname);
    }

    public void PerformFOCTransferInvalidUsername(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferInvalidLoginID";
        String searchBy = "Login Id";
        String Domain;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;
        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        // FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCUserNameBuyer"));
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String geographicalDomainName = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        String parentGeographicDomainCode = DBHandler.AccessHandler.getParentGeographicDomainCode(geographicalDomainName);
        String getGeographicDomainName = DBHandler.AccessHandler.getGeographicDomainName(parentGeographicDomainCode);
        FOCTransfers.selectFOCGeography(getGeographicDomainName);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        FOCTransfers.selectDomain(Domain);
        FOCTransfers.selectCategory(chCategoryName);
        String UserName = RandomGeneration.randomAlphabets(8);
        FOCTransfers.enterUserName(UserName);
        FOCTransfers.clickProceedButton();
        String successfulMessage = FOCTransfers.getUsernameNotFoundErrorMessage();
        String expectedMessage = "User "+ UserName +" not found .";
        if (expectedMessage.contains(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Username error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

    }

    public void PerformFOCTransferInvalidLoginID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformFOCTransferInvalidLoginID";
        String searchBy = "Login Id";
        String Domain;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        String transferStatus = null;
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCCommissionVisible()) {
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        else {
            FOCTransfers.clickOPTO2CHeading();
            FOCTransfers.clickOPTFOCCommissionHeading();
        }
        // FOCTransfers.clickOPTFOCSingleOperationHeading();
        FOCTransfers.spinnerWait() ;
        FOCTransfers.selectSearchBy(_masterVO.getProperty("FOCLoginIDBuyer"));
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String geographicalDomainName = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        String parentGeographicDomainCode = DBHandler.AccessHandler.getParentGeographicDomainCode(geographicalDomainName);
        String getGeographicDomainName = DBHandler.AccessHandler.getGeographicDomainName(parentGeographicDomainCode);
        FOCTransfers.selectFOCGeography(getGeographicDomainName);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        FOCTransfers.selectDomain(Domain);
        FOCTransfers.selectCategory(chCategoryName);
        String LoginID = RandomGeneration.randomAlphabets(8);
        FOCTransfers.enterLoginID(LoginID);
        FOCTransfers.clickProceedButton();
        String successfulMessage = FOCTransfers.getLoginIDNotFoundErrorMessage();
        String expectedMessage = "User not found with LoginID : " + LoginID + ".";
        if (expectedMessage.contains(successfulMessage)) {
            Assertion.assertContainsEquals(successfulMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Login ID error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

    }


    public Boolean PerformFOCApproval1DuplicateExtTxnNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval1DuplicateExtTxnNo";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }

        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String FOCStatusClose = "CLOSE";
        String FOCStatusApprove1 = "APPRV1";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& (transferStatus.contains(FOCStatusClose)||transferStatus.contains(FOCStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();

            Log.info("Geography of User: "+geography);
            geoDomainName = " " + geoDomainName;
            Log.info("Geography of Parent: " + geoDomainName);
            FOCTransfers.selectApprovalGeography(geoDomainName);
            Domain = " " + Domain;
            FOCTransfers.selectApprovalDomain(Domain);
            FOCTransfers.selectApprovalCategory(chCategoryName);
            FOCTransfers.clickApprovalProceedButton();
            FOCTransfers.enterSearch(txnId);
            FOCTransfers.clickApproveTxnButton();
            FOCTransfers.enterExtTxnNo(extTxnNo);
            FOCTransfers.enterApprovalDate(PaymentDate);
            FOCTransfers.enterApprovalReferenceNo(Reference);
            FOCTransfers.enterRemarks(Remarks);
            FOCTransfers.clickApproveButton();
            FOCTransfers.clickApproveYesButton();
            String expectedMessage = "External Transaction Number is not unique.";
            String successfulMessage1 = FOCTransfers.transferStatusFailed();
            if(expectedMessage.contains(successfulMessage1))
            {
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Duplicate External Transaction Number");
                ExtentI.attachCatalinaLogsForSuccess();
            }
            else{
                ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Duplicate External Transaction Number");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }

        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        return finalFlag;
    }



    public Boolean PerformFOCApprovalInvalidSearchBy(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApprovalInvalidSearchBy";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }

        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        String SearchBy = RandomGeneration.randomAlphaNumeric(8);
        FOCTransfers.enterSearch(SearchBy);
        FOCTransfers.clickApproveTxnButton();
        String expectedMessage = "No matching records found";
        String successfulMessage = FOCTransfers.invalidSearchBy();
        if(expectedMessage.contains(successfulMessage))
        {
            ExtentI.Markup(ExtentColor.GREEN, "No Search Results came after invalid Search By");
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else{
            ExtentI.Markup(ExtentColor.RED, "Search results are shown after invalid Search By");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }

    public Boolean PerformFOCApproval1SearchMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval1SearchMSISDN";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }

        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(chMsisdn);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String FOCStatusClose = "CLOSE";
        String FOCStatusApprove1 = "APPRV1";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& (transferStatus.contains(FOCStatusClose)||transferStatus.contains(FOCStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformFOCApproval1SearchUserName(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval1SearchUserName";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }

        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        String UserName = DBHandler.AccessHandler.getUsernameFromMsisdn(chMsisdn);
        FOCTransfers.enterSearch(UserName);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String FOCStatusClose = "CLOSE";
        String FOCStatusApprove1 = "APPRV1";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& (transferStatus.contains(FOCStatusClose)||transferStatus.contains(FOCStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformFOCApproval1SearchDate(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval1SearchDate";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }

        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterSearch(PaymentDate);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);

        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        FOCTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String FOCStatusClose = "CLOSE";
        String FOCStatusApprove1 = "APPRV1";
        String successfulMessage = null;
        boolean successPopUP = FOCTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = FOCTransfers.FOCApproveTransactionID();
            successfulMessage = FOCTransfers.successfulMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusFOC(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (successfulMessage.contains(expectedmessage)&& (transferStatus.contains(FOCStatusClose)||transferStatus.contains(FOCStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "FOC Approval 1 message Found as: " + successfulMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "FOC Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "FOC Transfer Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            FOCTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = FOCTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "FOC Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "FOC Approval 1 is not successful. Transfer message on WEB: " + successfulMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformFOCApproval1BlankExternalTxnNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval1BlankExternalTxnNo";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if (FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        } else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }

        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: " + geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = "";
        FOCTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        String expectedMessage = "External TXN number required.";
        String successfulMessage1 = FOCTransfers.transferStatusFailed();
        if (expectedMessage.contains(successfulMessage1)) {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Blank External Transaction Number");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Blank External Transaction Number");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformFOCApproval1BlankExternalTxnDate(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformFOCApproval1BlankExternalTxnDate";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
//        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        FOCTransfers.spinnerWait() ;

        if(FOCTransfers.isFOCApproval1Visible()) {
            FOCTransfers.clickApproval1Transaction();
        }
        else {
            FOCTransfers.clickApproval1FOCHeading();
            FOCTransfers.clickApproval1Transaction();
        }

        FOCTransfers.clickFOCApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        geoDomainName = " " + geoDomainName;
        Log.info("Geography of Parent: " + geoDomainName);
        FOCTransfers.selectApprovalGeography(geoDomainName);
        Domain = " " + Domain;
        FOCTransfers.selectApprovalDomain(Domain);
        FOCTransfers.selectApprovalCategory(chCategoryName);
        FOCTransfers.clickApprovalProceedButton();
        FOCTransfers.enterSearch(txnId);
        FOCTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        FOCTransfers.enterExtTxnNo(extTxnNo);
        String PaymentDate = "";
        FOCTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        FOCTransfers.enterApprovalReferenceNo(Reference);
        FOCTransfers.enterRemarks(Remarks);
        FOCTransfers.clickApproveButton();
        String expectedMessage = "Please Choose Date to proceed .";
        String successfulMessage1 = FOCTransfers.transferStatusFailed();
        if (expectedMessage.contains(successfulMessage1)) {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Blank External Transaction Date");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Blank External Transaction Date");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



}
