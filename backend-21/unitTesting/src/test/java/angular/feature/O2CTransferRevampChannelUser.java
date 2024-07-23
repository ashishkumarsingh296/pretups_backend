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

public class O2CTransferRevampChannelUser extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    O2CTransferApproval O2CTransfers;
    Map<String, String> ResultMap;

    public O2CTransferRevampChannelUser(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        O2CTransfers = new O2CTransferApproval(driver);
        ResultMap = new HashMap<String, String>();
    }

    RandomGeneration RandomGeneration = new RandomGeneration();

    public Map<String, String> PerformO2CTransferCU(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCU";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        O2CTransfers.enterPin(chPin);
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
            if (actualMessage.contains(expectedmessage) && transferStatus.contains(O2CStatus)) {
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


    public void PerformO2CTransferCUResetButton(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUResetButton";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;

        O2CTransfers.enterCUPaymentDate(PaymentDate);
        O2CTransfers.clickResetButton();
        Boolean blankeTopAmount = O2CTransfers.getblankeTopUPAmount();
        Boolean blankReferenceNumber = O2CTransfers.getCUblankReferenceNumber();
        Boolean blankRemarks = O2CTransfers.getblankRemarks();
        Boolean blankPaymentInstrumentNumber = O2CTransfers.getblankPaymentInstrumentNumber();
        Boolean blankPaymentDate = O2CTransfers.getblankPaymentDate();
        Boolean allFields;
        if (blankeTopAmount && blankReferenceNumber && blankRemarks && blankPaymentInstrumentNumber && blankPaymentDate)
            allFields = true;
        else allFields = false;
        if(allFields)
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


    public void PerformO2CTransferCUAlphabeticReferenceNo(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUAlphabeticReferenceNo";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomAlphabets(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        String actualMessage = O2CTransfers.getCUReferenceError();
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


    public void PerformO2CTransferCUAlphabeticPaymentInstrumentNo(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUAlphabeticPaymentInstrumentNo";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = RandomGeneration.randomAlphabets(8);
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
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



    public void PerformO2CTransferCUBlankAmount(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUBlankAmount";
        String quantity = "";
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
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



    public void PerformO2CTransferCUBlankRemarks(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUBlankRemarks";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = "";
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
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



    public void PerformO2CTransferCUNegativeAmount(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCU";
        String quantity = "-" + _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
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


    public void PerformO2CTransferCUAlphanumericAmount(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUAlphanumericAmount";
        String quantity = RandomGeneration.randomAlphaNumeric(8);
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
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


    public void PerformO2CTransferCUBlankPIN(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUBlankPIN";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        chPin = "";
        O2CTransfers.enterPin(chPin);
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


    public void PerformO2CTransferCUInvalidPIN(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodname = "PerformO2CTransferCUInvalidPIN";
        String quantity = _masterVO.getProperty("Quantity");
        String Remarks = _masterVO.getProperty("Remarks");
        String paymentInstType = _masterVO.getProperty("PaymentInstrumntType");
        String PaymentInstNum = _masterVO.getProperty("PaymentInstNum");
        Log.methodEntry(methodname, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String transferStatus = null;
        O2CTransfers.clickCUO2CHeading();
        O2CTransfers.clickCUO2CPurchaseHeading();
        Log.info(productName);
        if (productName.equals("eTopUP")) {
            O2CTransfers.entereTopUPAmount(quantity);
        } else if (productName.equals("Post eTopUP")) {
            O2CTransfers.enterPosteTopUPAmount(quantity);
        }
        String Reference = RandomGeneration.randomNumeric(8);
        O2CTransfers.enterCUReferenceNumber(Reference);
        O2CTransfers.enterRemarks(Remarks);
        O2CTransfers.selectPaymentMode(paymentInstType);
        O2CTransfers.enterPaymentInstrumentNumber(PaymentInstNum);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dateobj = new Date();
        //String PaymentDate = df.format(dateobj);
        String PaymentDate = O2CTransfers.getDateMMDDYY() ;
        O2CTransfers.enterCUPaymentDate(PaymentDate);
        O2CTransfers.clickSubmitButton();
        chPin = RandomGeneration.randomNumeric(4);
        O2CTransfers.enterPin(chPin);
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



}
