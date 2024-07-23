package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.RechargeBulk.RechargesBulk;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.asserts.SoftAssert;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C2SBulkTransferRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    RechargesBulk RechargesBulk;

    public C2SBulkTransferRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        RechargesBulk = new RechargesBulk(driver);
    }

    RandomGeneration RandomGeneration = new RandomGeneration();

    public void performC2SBulkTransferDaily(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2STransferDaily";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedmessage = "Bulk Recharge Successful";
                String actualMessage = RechargesBulk.transferStatus();
                if (successPopUP == true) {
                    if (actualMessage.contains(expectedmessage)) {
                        batchID = RechargesBulk.transferID();
                        String scheduleStatus = DBHandler.AccessHandler.fetchScheduleStatus(batchID);
                        Log.info("Schedule Status:" + scheduleStatus);
                        if(batchID.contains("S"))
                        {
                            ExtentI.Markup(ExtentColor.GREEN, "Scheduled Batch ID found in DataBase and Schedule is on with status: " + scheduleStatus);
                        }
                        else if(batchID.contains("E"))
                        {
                            ExtentI.Markup(ExtentColor.YELLOW, "Scheduled Batch ID found in DataBase and Schedule is off with status: " + scheduleStatus);
                        }
                        else
                        {
                            ExtentI.Markup(ExtentColor.RED, "Schedule status not found with BatchID in DataBase");
                        }

                        ExtentI.Markup(ExtentColor.GREEN, "C2C Bulk Transfer Transaction message Found as: " + actualMessage + "with BatchID: " + batchID);
                        RechargesBulk.transferID();
                        ExtentI.attachCatalinaLogsForSuccess();
                        ExtentI.attachScreenShot();
                        RechargesBulk.clickDoneButton();
                    } else {
                        String errorMessageForFailure = RechargesBulk.rechargeFailedReason() ;
                        ExtentI.Markup(ExtentColor.RED, "C2C Bulk Transfer Failed Reason: " + errorMessageForFailure);
                        currentNode.log(Status.FAIL, "C2C Bulk Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                } else {
                    currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + actualMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferWeekly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2STransferWeekly";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Weekly");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedmessage = "Bulk Recharge Successful";
                String actualMessage = RechargesBulk.transferStatus();
                if (successPopUP == true) {
                    if (actualMessage.contains(expectedmessage)) {
                        batchID = RechargesBulk.transferID();
                        String scheduleStatus = DBHandler.AccessHandler.fetchScheduleStatus(batchID);
                        Log.info("Schedule Status:" + scheduleStatus);
                        if(batchID.contains("S"))
                        {
                            ExtentI.Markup(ExtentColor.GREEN, "Scheduled Batch ID found in DataBase and Schedule is on with status: " + scheduleStatus);
                        }
                        else if(batchID.contains("E"))
                        {
                            ExtentI.Markup(ExtentColor.YELLOW, "Scheduled Batch ID found in DataBase and Schedule is off with status: " + scheduleStatus);
                        }
                        else
                        {
                            ExtentI.Markup(ExtentColor.RED, "Schedule status not found with BatchID in DataBase");
                        }

                        ExtentI.Markup(ExtentColor.GREEN, "C2C Bulk Transfer Transaction message Found as: " + actualMessage + "with BatchID: " + batchID);
                        RechargesBulk.transferID();
                        ExtentI.attachCatalinaLogsForSuccess();
                        ExtentI.attachScreenShot();
                        RechargesBulk.clickDoneButton();
                    } else {
                        String errorMessageForFailure = RechargesBulk.rechargeFailedReason() ;
                        ExtentI.Markup(ExtentColor.RED, "C2C Bulk Transfer Failed Reason: " + errorMessageForFailure);
                        currentNode.log(Status.FAIL, "C2C Bulk Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                } else {
                    currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + actualMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
        }
        Log.methodExit(methodname);
    }




    public void performC2SBulkTransferMonthly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2STransferMonthly";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Monthly");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedmessage = "Bulk Recharge Successful";
                String actualMessage = RechargesBulk.transferStatus();
                if (successPopUP == true) {
                    if (actualMessage.contains(expectedmessage)) {
                        batchID = RechargesBulk.transferID();
                        String scheduleStatus = DBHandler.AccessHandler.fetchScheduleStatus(batchID);
                        Log.info("Schedule Status:" + scheduleStatus);
                        if(batchID.contains("S"))
                        {
                            ExtentI.Markup(ExtentColor.GREEN, "Scheduled Batch ID found in DataBase and Schedule is on with status: " + scheduleStatus);
                        }
                        else if(batchID.contains("E"))
                        {
                            ExtentI.Markup(ExtentColor.YELLOW, "Scheduled Batch ID found in DataBase and Schedule is off with status: " + scheduleStatus);
                        }
                        else
                        {
                            ExtentI.Markup(ExtentColor.RED, "Schedule status not found with BatchID in DataBase");
                        }

                        ExtentI.Markup(ExtentColor.GREEN, "C2C Bulk Transfer Transaction message Found as: " + actualMessage + "with BatchID: " + batchID);
                        RechargesBulk.transferID();
                        ExtentI.attachCatalinaLogsForSuccess();
                        ExtentI.attachScreenShot();
                        RechargesBulk.clickDoneButton();
                    } else {
                        String errorMessageForFailure = RechargesBulk.rechargeFailedReason() ;
                        ExtentI.Markup(ExtentColor.RED, "C2C Bulk Transfer Failed Reason: " + errorMessageForFailure);
                        currentNode.log(Status.FAIL, "C2C Bulk Transfer Transaction is not successful. Transfer message on WEB: " + actualMessage);
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                } else {
                    currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + actualMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferInvalidMSISDN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        RandomGeneration RandomGeneration = new RandomGeneration();
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if (RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                } else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = RandomGeneration.randomNumeric(5);
                double b = RechargesBulk.getCurrentBalance();
                int a = (int) (b * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN, 2, 0);    //MSISDN
                ExcelUtility.setCellDataXLS(SubService, 2, 1);    //SubService
                ExcelUtility.setCellDataXLS(amount, 2, 2);        //Amount
                ExcelUtility.setCellDataXLS(LanguageCode, 2, 3);    //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Invalid subscriber's msisdn.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferNegativeAmount";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double b = RechargesBulk.getCurrentBalance();
                int a = (int) (b * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab);
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = "-" + Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);    //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);    //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);        //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);    //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Invalid or empty wallet type.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Negative Amount error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferZeroAmount";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String amount = "0";
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);  //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);  //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);      //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);  //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Requested amount can not be zero.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Zero Amount error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankAmount";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String amount = "";
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Requested amount can not be blank.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Blank Amount error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankMSISDN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = "";
                double b = RechargesBulk.getCurrentBalance();
                int a = (int) (b * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Mobile number can not be blank.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Blank MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }




    public void performC2SBulkTransferBlankSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankSubService";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double b = RechargesBulk.getCurrentBalance();
                int a = (int) (b * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: Blank" + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Sub-service can not be blank.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Blank Sub Service error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }




    public void performC2SBulkTransferBlankLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferInvalidLanguage";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double b = RechargesBulk.getCurrentBalance();
                int a = (int) (b * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: Blank " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Language code can not be blank.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Blank Language Code error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferInvalidLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferInvalidLanguage";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double b = RechargesBulk.getCurrentBalance();
                int a = (int) (b * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "4";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Invalid language code";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Invalid Language Code error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferInvalidSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferInvalidSubService";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double b = RechargesBulk.getCurrentBalance();
                int a = (int) (b * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "4";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = SubService + " is not a valid sub-service value.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Invalid Sub-Service error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferAlphanumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferAlphanumericAmount";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String amount = RandomGeneration.randomAlphaNumeric(5);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode ,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Invalid or empty wallet type.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Alphanumeric MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }




    public void performC2SBulkTransferAlphanumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferAlphanumericMSISDN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }

                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = RandomGeneration.randomAlphaNumeric(8);
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedMessage = "Invalid subscriber's msisdn.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }

    public void performC2SBulkTransferResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferResetButton";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickResetButton();
                RechargesBulk.clickRechargeButtonForBulk();
                Boolean blankScheduleDate = RechargesBulk.getblankScheduleDate();
                Boolean blankOccurrence = RechargesBulk.getblankOccurrence();
                Boolean blankNoofdays = RechargesBulk.getblankNoofDays();
                if(blankScheduleDate&&blankOccurrence&&blankNoofdays)
                {
                    ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
                    ExtentI.attachCatalinaLogsForSuccess();
                }
                else{
                    currentNode.log(Status.FAIL, "Fields are not blank hence Reset button failed.");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferInvalidPIN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                PIN = RandomGeneration.randomNumeric(4);
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedmessage = "The PIN you have entered is incorrect.";
                String actualMessage = RechargesBulk.transferStatus();
                if (successPopUP == true) {
                    if (actualMessage.contains(expectedmessage)) {
                        ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Invalid PIN with message: " + actualMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                        ExtentI.attachScreenShot();

                    } else {
                        currentNode.log(Status.FAIL, "Transaction went ahead with Invalid PIN with message: " + actualMessage);
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                    RechargesBulk.clickRetryButton();
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankPIN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                PIN = "";
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                Boolean confirmButtonDisabled = RechargesBulk.checkDisabledRechargeButton();
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
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferBlankScheduleDate(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankScheduleDate";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if (RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                } else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN, 2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService, 2, 1);   //SubService
                ExcelUtility.setCellDataXLS(amount, 2, 2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode, 2, 3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                List<WebElement> errorMessageCaptured = RechargesBulk.blankErrorMessages();
                String actualMessage = null;
                String expectedmessage = "Please Choose Date to proceed.";
                boolean flag = false;
                for (WebElement ele : errorMessageCaptured) {
                    actualMessage = ele.getText();
                    if (expectedmessage.equals(actualMessage)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    Assertion.assertContainsEquals(actualMessage, expectedmessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Blank Schedule Date error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }

        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferBlankOccurrence(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankOccurrence";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if (RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                } else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN, 2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService, 2, 1);   //SubService
                ExcelUtility.setCellDataXLS(amount, 2, 2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode, 2, 3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                List<WebElement> errorMessageCaptured = RechargesBulk.blankErrorMessages();
                String actualMessage = null;
                String expectedmessage = "Occurrences Required.";
                boolean flag = false;
                for (WebElement ele : errorMessageCaptured) {
                    actualMessage = ele.getText();
                    if (expectedmessage.equals(actualMessage)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    Assertion.assertContainsEquals(actualMessage, expectedmessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Blank Occurrence error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }

        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferBlankNoofIterations(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankNoofIterations";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if (RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                } else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN, 2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService, 2, 1);   //SubService
                ExcelUtility.setCellDataXLS(amount, 2, 2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode, 2, 3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = "";
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                List<WebElement> errorMessageCaptured = RechargesBulk.blankErrorMessages();
                String actualMessage = null;
                String expectedmessage = "Value is Required.";
                boolean flag = false;
                for (WebElement ele : errorMessageCaptured) {
                    actualMessage = ele.getText();
                    if (expectedmessage.equals(actualMessage)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    Assertion.assertContainsEquals(actualMessage, expectedmessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Blank No of Iterations error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }

        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferWithoutUploadingFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferWithoutUploadingFile";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                List<WebElement> errorMessageCaptured = RechargesBulk.blankErrorMessages();
                String actualMessage = null;
                String expectedmessage = "File is required.";
                boolean flag = false;
                for (WebElement ele : errorMessageCaptured) {
                    actualMessage = ele.getText();
                    Log.info("Error message found:" +actualMessage);
                    if (expectedmessage.equals(actualMessage)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    Assertion.assertContainsEquals(actualMessage, expectedmessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Blank No of Iterations error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferTemplatewithoutData(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferTemplatewithoutData";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedmessage = "Uploaded file does not have any valid records.";
                String actualMessage = RechargesBulk.UploadStatus();
                if (successPopUP == true) {
                    if (actualMessage.contains(expectedmessage)) {
                        ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                        ExtentI.attachScreenShot();

                    } else {
                        currentNode.log(Status.FAIL, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                    RechargesBulk.clickRetryButton();
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performC2SBulkTransferBlankTemplateFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferBlankTemplateFile";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                int noOfFiles = RechargesBulk.noOfFilesInDownloadedDirectory(PathOfFile);

                if(noOfFiles > 0)
                {
                    ExcelUtility.deleteFiles(PathOfFile);
                }
                ExcelUtility.createBlankExcelFile(PathOfFile+"C2SBulkTransfer.xls");
                Log.info("Created an Empty Excel File");

                String latestFileName = RechargesBulk.getLatestFileNamefromDir(PathOfFile);

                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + latestFileName;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();

                String expectedmessage = "Invalid file format. Not a valid XLS format.";
                String actualMessage = RechargesBulk.transferStatus();
                if (successPopUP == true) {
                    if (actualMessage.contains(expectedmessage)) {
                        ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                        ExtentI.attachScreenShot();

                    } else {
                        currentNode.log(Status.FAIL, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                    RechargesBulk.clickRetryButton();
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferInvalidFileType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferInvalidFileType";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PNGPath = System.getProperty("user.dir")+_masterVO.getProperty("PNGFile");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if (RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                } else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                RechargesBulk.uploadFile(PNGPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = "3";
                RechargesBulk.enterNoofDays(NoofDays);
                String errorMessageCaptured = RechargesBulk.fileUploadTypeErrorMessage();
                String expectedmessage = "Only CSV,XLS & XLSX Files are allowed";
                if (errorMessageCaptured.equals(expectedmessage)) {
                    Assertion.assertContainsEquals(errorMessageCaptured, expectedmessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Invalid File Upload error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }

        }
        Log.methodExit(methodname);
    }



    public void performC2SBulkTransferCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performC2SBulkTransferCopyButton";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow = ExcelUtility.getRowCount();
        //HashMap to fetch servicename from Excel
        Map<String, String[]> serviceMap = new HashMap<String, String[]>();
        String batchID = null, transferStatus = null, trf_status = null;
        for (int i = 1; i <= totalRow; i++) {
            String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
            if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
                serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
                if(RechargesBulk.isRechargeVisible()) {
                    RechargesBulk.clickRecharge();
                }
                else {
                    RechargesBulk.clickRechargeHeading();
                    RechargesBulk.clickRecharge();
                }
                RechargesBulk.clickC2SBulkOperationHeading();
                RechargesBulk.clickBulkPrepaidRecharge();
                //ExcelUtility.deleteFiles(PathOfFile);
                RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                double current_balance = RechargesBulk.getCurrentBalance();
                int a = (int) (current_balance * 0.05);
                int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
                int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
                if (minCardSlab < a && a < midCardSlab) ;
                else if (a > midCardSlab) {
                    a = midCardSlab;
                } else if (a < minCardSlab) {
                    a = minCardSlab + 1;
                }
                String amount = Integer.toString(a);
                String SubService = "1";
                String LanguageCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
                RechargesBulk.uploadFile(uploadPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
                RechargesBulk.enterNoofDays(NoofDays);
                RechargesBulk.clickRechargeButtonForBulk();
                RechargesBulk.enterPin(PIN);
                RechargesBulk.clickRechargeButton();
                boolean successPopUP = RechargesBulk.successPopUPVisibility();
                RechargesBulk.clickCopyButton();

                Actions actions = new Actions(driver);
                actions.sendKeys(Keys.chord(Keys.CONTROL, "v")).build().perform();
                String clipboard = null;
                try {
                    clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                batchID = RechargesBulk.fetchbatchID();
                Log.info("Copied Batch ID fetched as : "+clipboard);
                if(clipboard.equals(batchID))
                {
                    ExtentI.Markup(ExtentColor.GREEN, "Batch ID Copied Successfully");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Batch ID Copy failed");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
                RechargesBulk.clickDoneButton();
            }
        }
        Log.methodExit(methodname);
    }




}
