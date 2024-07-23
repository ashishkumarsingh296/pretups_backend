package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.O2CPages.O2CTransferApproval;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.aventstack.extentreports.Status;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.*;
import org.openqa.selenium.WebDriver;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class O2CTransferRevampChannelAdmin extends BaseTest {


    public WebDriver driver;
    LoginRevamp login;
    O2CTransferApproval O2CTransfers;
    Map<String, String> ResultMap;

    public O2CTransferRevampChannelAdmin(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        O2CTransfers = new O2CTransferApproval(driver);
        ResultMap = new HashMap<String, String>();
    }

    RandomGeneration RandomGeneration = new RandomGeneration();

    public Map<String, String> PerformO2CTransferByMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferByMSISDN";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
           O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        O2CTransfers.enterPin(opPin);
        O2CTransfers.clickRechargeIcon();
        String expectedmessage = "Purchase successful";
        String O2CStatus = "NEW";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CTransactionID();
            actualMessage = O2CTransfers.actualMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& transferStatus.contains(O2CStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Transfer Transaction message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();

                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Transfer Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ResultMap.put("INITIATE_MESSAGE", transferStatus);
        return ResultMap;
    }



    public Boolean PerformO2CApproval1(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus, geography, Domain;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        login.UserLogin(driver, "Operator", opCategoryName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();
        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(O2CTransfers.approvalExtDateCU());
        /*String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);*/
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String O2CStatusClose = "CLOSE";
        String O2CStatusApprove1 = "APPRV1";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& (transferStatus.contains(O2CStatusClose)||transferStatus.contains(O2CStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformO2CReject1(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CReject1";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus, geography, Domain;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        login.UserLogin(driver, "Operator", opCategoryName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickRejectTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(O2CTransfers.approvalExtDateCU());
        /*String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);*/
        O2CTransfers.clickRejectButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Rejected";
        String O2CStatus = "CNCL";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& transferStatus.contains(O2CStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformO2CApproval2(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval2";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        login.UserLogin(driver, "Operator", opCategoryName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval2Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval2O2CHeading();
        }
        else {
            O2CTransfers.clickApproval2O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval2Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        //O2CTransfers.selectTransactionType();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String O2CStatusClose = "CLOSE";
        String O2CStatusApprove = "APPRV2";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& (transferStatus.contains(O2CStatusClose)||transferStatus.contains(O2CStatusApprove))) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }

    public Boolean PerformO2CReject2(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CReject2";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        login.UserLogin(driver, "Operator", opCategoryName);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval2Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval2O2CHeading();
        }
        else {
            O2CTransfers.clickApproval2O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval2Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();
        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        /*O2CTransfers.selectTransactionType();*/
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(O2CTransfers.approvalExtDateCU());
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickRejectButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Rejected";
        String O2CStatus = "CNCL";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& transferStatus.contains(O2CStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformO2CApproval3(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval3";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        login.UserLogin(driver, "Operator", opCategoryName);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval3Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval3O2CHeading();
        }
        else {
            O2CTransfers.clickApproval3O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval3Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        /*O2CTransfers.selectTransactionType();*/
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String O2CStatus = "CLOSE";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& transferStatus.contains(O2CStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformO2CReject3(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CByMSISDN";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        login.UserLogin(driver, "Operator", opCategoryName);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval3Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval3O2CHeading();
        }
        else {
            O2CTransfers.clickApproval3O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval3Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        /*DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);*/
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickRejectButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Rejected";
        String O2CStatus = "CNCL";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& transferStatus.contains(O2CStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Map<String, String> PerformO2CTransferByLoginID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferByLoginID";
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
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.selectDomain(Domain);
        O2CTransfers.selectCategory(chCategoryName);
        String LoginID = DBHandler.AccessHandler.getLoginidFromMsisdn(chMsisdn);
        O2CTransfers.enterLoginID(LoginID);
        O2CTransfers.clickProceedButton();
        if(productName=="eTopUP")
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName=="Post eTopUP"){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);

        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        O2CTransfers.enterPin(opPin);
        O2CTransfers.clickRechargeIcon();
        String expectedmessage = "Purchase successful";
        String O2CStatus = "NEW";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CTransactionID();
            actualMessage = O2CTransfers.actualMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& transferStatus.contains(O2CStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Transfer Transaction message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();

                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Transfer Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ResultMap.put("INITIATE_MESSAGE", transferStatus);
        return ResultMap;
    }


    public Map<String, String> PerformO2CTransferByUserName(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferByUserName";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "User Name";
        String transferStatus = null, geography = null, Domain = null;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectGeography(geoDomainName);
        O2CTransfers.selectDomain(Domain);
        O2CTransfers.selectCategory(chCategoryName);
        String UserName = DBHandler.AccessHandler.getUsernameFromMsisdn(chMsisdn);
        /*String UserID = DBHandler.AccessHandler.getUserId(UserName);
        String user = UserName + "(" + UserID + ")";*/
        O2CTransfers.enterUserName(UserName);
        O2CTransfers.clickProceedButton();
        if(productName=="eTopUP")
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName=="Post eTopUP"){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);

        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        O2CTransfers.enterPin(opPin);
        O2CTransfers.clickRechargeIcon();
        String expectedmessage = "Purchase successful";
        String O2CStatus = "NEW";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CTransactionID();
            actualMessage = O2CTransfers.actualMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& transferStatus.contains(O2CStatus)) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Transfer Transaction message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Transfer Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ResultMap.put("INITIATE_MESSAGE", transferStatus);
        return ResultMap;
    }


    public void PerformO2CTransferAlphanumericMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productCode, String opPin) {
        final String methodname = "PerformO2CTransferAlphanumericMSISDN";
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productCode);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);

        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        chMsisdn = RandomGeneration.randomAlphaNumeric(5);
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        String actualMessage = O2CTransfers.getMsisdnError();
        String expectedMessage = "Please enter a valid moblie number.";
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
            } else {
            currentNode.log(Status.FAIL, "Alphanumeric MSISDN error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformO2CTransferBlankMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferBlankMSISDN";
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        chMsisdn = "";
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        String actualMessage = O2CTransfers.getMsisdnError();
        String expectedMessage = "Mobile number is required.";
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank MSISDN error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformO2CTransferInvalidMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferInvalidMSISDN";
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        chMsisdn = RandomGeneration.randomNumeric(5);
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        String actualMessage = O2CTransfers.getMsisdnError();
        String expectedMessage = "Please enter a valid moblie number.";
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformO2CTransferBlankLoginID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferInvalidLoginID";
        String searchBy = "Login Id";
        String transferStatus, Domain;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.selectDomain(Domain);
        O2CTransfers.selectCategory(chCategoryName);
        String LoginID = "";
        O2CTransfers.enterLoginID(LoginID);
        O2CTransfers.clickProceedButton();
        String actualMessage = O2CTransfers.getMsisdnError();
        String expectedMessage = "Login ID is required";
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Login ID error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

    }



    public void PerformO2CTransferResetButton(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferResetButton";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickResetButton();
        Boolean blankeTopAmount = O2CTransfers.getblankeTopUPAmount();
        Boolean blankReferenceNumber = O2CTransfers.getblankReferenceNumber();
        Boolean blankRemarks = O2CTransfers.getblankRemarks();
        Boolean blankPaymentInstrumentNumber = O2CTransfers.getblankPaymentInstrumentNumber();
        Boolean blankPaymentDate = O2CTransfers.getblankPaymentDate();
        Boolean allFields;
        if (blankeTopAmount && blankReferenceNumber && blankRemarks && blankPaymentInstrumentNumber && blankPaymentDate)
            allFields = true;
        else allFields = false;
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSearchByResetButton();
        Boolean blankSearchBy = O2CTransfers.getBlankSearchInput();
        if(allFields && blankSearchBy)
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


    public void PerformO2CTransferResetButtonSearchBy(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferResetButtonSearchBy";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSearchByResetButton();
        Boolean blankSearchBy = O2CTransfers.getBlankSearchInput();
        if(blankSearchBy)
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



    public void PerformO2CTransferAlphabeticReferenceNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferAlphabeticReferenceNo";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomAlphabets(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        String actualMessage = O2CTransfers.getReferenceError();
        String expectedMessage = " Please Enter Digits only.";
        if (expectedMessage.contains(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Alphabetic Reference Number error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformO2CTransferAlphabeticPaymentInstrumentNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferAlphabeticPaymentInstrumentNo";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        String actualMessage = O2CTransfers.getPmtInstNoError();
        String expectedMessage = " Invalid Payment Instrument Number.";
        if (expectedMessage.contains(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Alphabetic Payment Instrument Number error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformO2CTransferBlankAmount(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferBlankAmount";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        String actualMessage = O2CTransfers.getAmountError();
        String expectedMessage = " Amount is required.";
        if (expectedMessage.contains(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Amount error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformO2CTransferBlankRemarks(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferBlankRemarks";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        String actualMessage = O2CTransfers.getRemarksError();
        String expectedMessage = "Remarks Required.";
        if (expectedMessage.contains(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Remarks error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }



    public void PerformO2CTransferNegativeAmount(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferNegativeAmount";
        String quantity = "-" + _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        String searchBy = "Mobile Number";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        String actualMessage = null;
        if(productName.equals("eTopUP"))
        {
            actualMessage = O2CTransfers.geteTopupAmountError();

        }else if(productName.equals("Post eTopUP")){
            actualMessage = O2CTransfers.getPosteTopUpAmountError();
        }
        String expectedMessage = "Invalid Amount.";
        if (expectedMessage.contains(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Amount error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformO2CTransferBlankPIN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferBlankPIN";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        opPin = "";
        O2CTransfers.enterPin(opPin);
        Boolean confirmButtonDisabled = O2CTransfers.checkDisabledRechargeButton();
        if(confirmButtonDisabled)
        {
            ExtentI.Markup(ExtentColor.GREEN, "Confirm PIN button is disabled for blank PIN in C2S Bulk");
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else{
            ExtentI.Markup(ExtentColor.RED, "C2S Bulk PIN Confirm Button is not disabled successfully with blank PIN");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }


    public void PerformO2CTransferInvalidPIN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferInvalidPIN";
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
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.enterMSISDN(chMsisdn);
        O2CTransfers.clickProceedButton();
        Log.info(productName);
        if(productName.equals("eTopUP"))
        {
            O2CTransfers.entereTopUPAmount(quantity);

        }else if(productName.equals("Post eTopUP")){
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.enterAmount(quantity);
        O2CTransfers.selectOPTPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        opPin = RandomGeneration.randomNumeric(4);
        O2CTransfers.enterPin(opPin);
        O2CTransfers.clickRechargeIcon();
        String expectedMessage = "The PIN you have entered is incorrect.";
        String actualMessage = O2CTransfers.transferStatusFailed();
        if(expectedMessage.contains(actualMessage))
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


      public void PerformO2CTransferInvalidLoginID(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin) {
        final String methodname = "PerformO2CTransferInvalidLoginID";
        String searchBy = "Login Id";
        String Domain;
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "Operator", opCategoryName);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(O2CTransfers.isO2CTransactionVisible()) {
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        else {
            O2CTransfers.clickOPTO2CHeading();
            O2CTransfers.clickOPTO2CTransactionHeading();
        }
        O2CTransfers.clickOPTO2CSingleOperationHeading();
        O2CTransfers.clickO2CPurchaseHeading();
        O2CTransfers.clickO2CeTopUPHeading();
        O2CTransfers.selectSearchBy(searchBy);
        O2CTransfers.selectDomain(Domain);
        O2CTransfers.selectCategory(chCategoryName);
        String LoginID = RandomGeneration.randomAlphabets(8);
        O2CTransfers.enterLoginID(LoginID);
        O2CTransfers.clickProceedButton();
        O2CTransfers.clickProceedButton();
        String actualMessage = O2CTransfers.getLoginIDError();
        String expectedMessage = "User "+ LoginID +" not found .";
        if (expectedMessage.contains(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Login ID error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

    }


    public Boolean PerformO2CApproval1DuplicateExtTxnNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1DuplicateExtTxnNo";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.clickApproval1Transaction();
        }

        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String O2CStatusClose = "CLOSE";
        String O2CStatusApprove1 = "APPRV1";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& (transferStatus.contains(O2CStatusClose)||transferStatus.contains(O2CStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();

            Log.info("Geography of User: "+geography);
            Log.info("Geography of Parent: " + geoDomainName);
            O2CTransfers.selectApprovalGeography(geoDomainName);
            O2CTransfers.selectApprovalDomain(Domain);
            O2CTransfers.selectApprovalCategory(chCategoryName);
            O2CTransfers.clickApprovalProceedButton();
            O2CTransfers.enterSearch(txnId);
            O2CTransfers.clickApproveTxnButton();
            O2CTransfers.enterExtTxnNo(extTxnNo);
            O2CTransfers.enterApprovalDate(PaymentDate);
            O2CTransfers.enterApprovalReferenceNo(Reference);
            O2CTransfers.enterRemarks(Remarks);
            O2CTransfers.clickApproveButton();
            O2CTransfers.clickApproveYesButton();
            String expectedMessage = "External Transaction Number is not unique.";
            String actualMessage1 = O2CTransfers.actualApproveMessage();
            if(expectedMessage.contains(actualMessage1))
            {
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Duplicate External Transaction Number");
                ExtentI.attachCatalinaLogsForSuccess();
            }
            else{
                ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Duplicate External Transaction Number");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        return finalFlag;
    }



    public Boolean PerformO2CApprovalInvalidSearchBy(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApprovalInvalidSearchBy";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        String SearchBy = RandomGeneration.randomAlphaNumeric(8);
        O2CTransfers.enterSearch(SearchBy);
        String expectedMessage = "No matching records found";
        String actualMessage = O2CTransfers.invalidSearchBy();
        if(expectedMessage.contains(actualMessage))
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

    public Boolean PerformO2CApproval1SearchMSISDN(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1SearchMSISDN";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(chMsisdn);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String O2CStatusClose = "CLOSE";
        String O2CStatusApprove1 = "APPRV1";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& (transferStatus.contains(O2CStatusClose)||transferStatus.contains(O2CStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformO2CApproval1SearchUserName(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1SearchUserName";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        String UserName = DBHandler.AccessHandler.getUsernameFromMsisdn(chMsisdn);
        O2CTransfers.enterSearch(UserName);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String O2CStatusClose = "CLOSE";
        String O2CStatusApprove1 = "APPRV1";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& (transferStatus.contains(O2CStatusClose)||transferStatus.contains(O2CStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformO2CApproval1SearchDate(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1SearchDate";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String transferStatus = null, geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterSearch(PaymentDate);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        O2CTransfers.clickApproveYesButton();
        String expectedmessage = "Transaction Approved";
        String O2CStatusClose = "CLOSE";
        String O2CStatusApprove1 = "APPRV1";
        String actualMessage = null;
        boolean successPopUP = O2CTransfers.successPopUPApproveVisibility();
        if (successPopUP == true) {
            String transactionID = O2CTransfers.O2CApproveTransactionID();
            actualMessage = O2CTransfers.actualApproveMessage();
            transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
            ResultMap.put("TRANSACTION_ID", transactionID);
            if (actualMessage.contains(expectedmessage)&& (transferStatus.contains(O2CStatusClose)||transferStatus.contains(O2CStatusApprove1))) {
                ExtentI.Markup(ExtentColor.GREEN, "O2C Approval 1 message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
                finalFlag = true;
            } else {
                String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Approval 1 Failed Reason: " + errorMEssageForFailure);
                currentNode.log(Status.FAIL, "O2C Transfer Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            O2CTransfers.clickApproveDoneButton();
        } else {
            String errorMEssageForFailure = O2CTransfers.getErrorMessageForFailure();
            ExtentI.Markup(ExtentColor.RED, "O2C Approval 1 Failed : " + errorMEssageForFailure);
            currentNode.log(Status.FAIL, "O2C Approval 1 is not successful. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformO2CApproval1BlankExternalTxnNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1BlankExternalTxnNo";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: " + geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = "";
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        String expectedMessage = "External TXN number required.";
        String actualMessage1 = O2CTransfers.InvalidExtTxnNo();
        if (expectedMessage.contains(actualMessage1)) {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Blank External Transaction Number");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Blank External Transaction Number");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformO2CApproval1BlankExternalTxnDate(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1BlankExternalTxnDate";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        String PaymentDate = "";
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        String expectedMessage = "Please Choose Date to proceed .";
        String actualMessage1 = O2CTransfers.InvalidExtTxnDate();
        if (expectedMessage.contains(actualMessage1)) {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Blank External Transaction Date");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Blank External Transaction Date");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }


    public Boolean PerformO2CApproval1AlphanumericExternalTxnNo(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1AlphanumericExternalTxnNo";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: " + geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomAlphaNumeric(8);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        String expectedMessage = "Please Enter Digits only.";
        String actualMessage1 = O2CTransfers.InvalidExtTxnNo();
        if (expectedMessage.contains(actualMessage1)) {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Alphanumeric External Transaction Number");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Alphanumeric External Transaction Number, no error displayed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



    public Boolean PerformO2CApproval1AlphanumericReferenceNumber(String opCategoryName, String chCategoryName, String chMsisdn, String productName, String opPin, String txnId) {
        Boolean finalFlag = false;
        final String methodname = "PerformO2CApproval1AlphanumericReferenceNumber";
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        String geography = null, Domain = null;
        String Remarks = _masterVO.getProperty("Remarks");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        geography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1);
        Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        Log.methodEntry(methodname, opCategoryName, chCategoryName, chMsisdn, productName);
        O2CTransfers.spinnerWait();
        if(O2CTransfers.isO2CApproval1Visible()) {
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        else {
            O2CTransfers.clickApproval1O2CHeading();
            O2CTransfers.spinnerWait();
            O2CTransfers.clickApproval1Transaction();
        }
        O2CTransfers.clickO2CApprovalSingleOperationHeading();

        Log.info("Geography of User: "+geography);
        String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(geography);
        String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
        Log.info("Geography of Parent: " + geoDomainName);
        O2CTransfers.selectApprovalGeography(geoDomainName);
        O2CTransfers.selectApprovalDomain(Domain);
        O2CTransfers.selectApprovalCategory(chCategoryName);
        O2CTransfers.clickApprovalProceedButton();
        O2CTransfers.enterSearch(txnId);
        O2CTransfers.clickApproveTxnButton();
        String extTxnNo = RandomGeneration.randomNumeric(6);
        O2CTransfers.enterExtTxnNo(extTxnNo);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        String PaymentDate = df.format(dateobj);
        O2CTransfers.enterApprovalDate(PaymentDate);
        String Reference = RandomGeneration.randomAlphaNumeric(8);
        O2CTransfers.enterApprovalReferenceNo(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.clickApproveButton();
        String expectedMessage = "Please Enter Digits only.";
        String actualMessage1 = O2CTransfers.InvalidExtTxnDate();
        if (expectedMessage.contains(actualMessage1)) {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Alphanumeric External Transaction Number");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Alphanumeric External Transaction Number, no error displayed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        return finalFlag;
    }



}
