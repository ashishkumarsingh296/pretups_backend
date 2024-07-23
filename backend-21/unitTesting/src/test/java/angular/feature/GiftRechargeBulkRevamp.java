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

public class GiftRechargeBulkRevamp extends BaseTest {


    public WebDriver driver;
    LoginRevamp login;
    RechargesBulk RechargesBulk;

    public GiftRechargeBulkRevamp(WebDriver driver) {
		this.driver = driver;
        login = new LoginRevamp();
        RechargesBulk = new RechargesBulk(driver);
    }

    RandomGeneration RandomGeneration = new RandomGeneration();



    public void performGiftRechargeBulkRevampDaily(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampDaily";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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



    public void performGiftRechargeBulkRevampWeekly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampWeekly";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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



    public void performGiftRechargeBulkRevampMonthly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampMonthly";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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




    public void performGiftRechargeBulkRevampInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampInvalidMSISDN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        RandomGeneration RandomGeneration = new RandomGeneration();
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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




    public void performGiftRechargeBulkRevampNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampNegativeAmount";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String amount = "-" + Integer.toString(a);
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Invalid Requested amount.";
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






    public void performGiftRechargeBulkRevampZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampZeroAmount";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String amount = "0";
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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
                        currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }





    public void performGiftRechargeBulkRevampBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankAmount";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                String latestFileName = RechargesBulk.getLatestFilefromDir(PathOfFile);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String amount = "";
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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



    public void performGiftRechargeBulkRevampBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankMSISDN";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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




    public void performGiftRechargeBulkRevampBlankSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankSubService";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Sub-service can not be blank.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Blank Sub-Service error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performGiftRechargeBulkRevampBlankLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankLanguageCode";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Receiver language code can not be blank.";
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




    public void performGiftRechargeBulkRevampInvalidLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampInvalidLanguageCode";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "5";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS("4",2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Invalid language code for receiver.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Invalid Language-Code error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }




    public void performGiftRechargeBulkRevampInvalidSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampInvalidSubService";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "4";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS("4",2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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




    public void performGiftRechargeBulkRevampBlankGifterMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankGifterMSISDN";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN = "";
                String gifterName = UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Gifter mobile number can not be blank.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Blank Gifter MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }




    public void performGiftRechargeBulkRevampBlankGifterName(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankGifterName";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName="";
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Gifter name can not be blank.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Blank Gifter Name error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }





    public void performGiftRechargeBulkRevampNumericGifterName(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampNumericGifterName";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        RandomGeneration randomGeneration = new RandomGeneration();
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=randomGeneration.randomNumeric(5);
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Gifter name "+ gifterName + " is invalid, blankspace/special characters/numeric values are not allowed in name.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Invalid Gifter Name error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }



    public void performGiftRechargeBulkRevampAlphabeticGifterMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampAlphabeticGifterMSISDN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        RandomGeneration randomGeneration = new RandomGeneration();
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN = randomGeneration.randomAlphabets(7);
                String gifterName = UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN, 2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS("1", 2, 1);   //SubService
                ExcelUtility.setCellDataXLS(amount, 2, 2);       //Amount
                ExcelUtility.setCellDataXLS("0", 2, 3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN, 2, 4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName, 2, 5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode, 2, 6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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
                        currentNode.log(Status.FAIL, "Invalid Gifter MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }






    public void performGiftRechargeBulkRevampAlphanumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampAlphanumericAmount";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String amount = "-" + Integer.toString(a);
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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

                String expectedMessage = "Invalid Requested amount.";
                String actualMessage = RechargesBulk.fetchFailedReason();

                if (successPopUP == true) {
                    if (expectedMessage.equals(actualMessage)) {
                        Assertion.assertContainsEquals(actualMessage, expectedMessage);
                        ExtentI.attachCatalinaLogsForSuccess();
                    } else {
                        currentNode.log(Status.FAIL, "Alphanumeric Amount error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performGiftRechargeBulkRevampAlphanumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampAlphanumericMSISDN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("C2SBulkTransferPath");
        RandomGeneration RandomGeneration = new RandomGeneration();
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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
                        currentNode.log(Status.FAIL, "Alphanumeric MSISDN error not shown");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
            }
        }
        Log.methodExit(methodname);
    }


    public void performGiftRechargeBulkRevampResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampResetButton";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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


    public void performGiftRechargeBulkRevampInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampInvalidPIN";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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


    public void performGiftRechargeBulkRevampBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankPIN";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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
                Boolean confirmButtonDisabled = RechargesBulk.checkDisabledRechargeButton() ;
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



    public void performGiftRechargeBulkRevampBlankScheduleDate(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankScheduleDate";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
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



    public void performGiftRechargeBulkRevampBlankOccurrence(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankOccurrence";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
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


    public void performGiftRechargeBulkRevampBlankNoofIterations(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankNoofIterations";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
                String filename = RechargesBulk.getLatestFileNamefromDir(PathOfFile);
                String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2SBulkTransferUpload") + filename;
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



    public void performGiftRechargeBulkRevampWithoutUploadingFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampWithoutUploadingFile";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
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





    public void performGiftRechargeBulkRevampTemplatewithoutData(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampTemplatewithoutData";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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






    public void performGiftRechargeBulkRevampBlankTemplateFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampBlankTemplateFile";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                int noOfFiles = RechargesBulk.noOfFilesInDownloadedDirectory(PathOfFile);

                if(noOfFiles > 0)
                {
                    ExcelUtility.deleteFiles(PathOfFile);
                }
                ExcelUtility.createBlankExcelFile(PathOfFile+"C2SBulkTransfer.xls");
                Log.info("Created an Empty Excel File");

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






    public void performGiftRechargeBulkRevampInvalidFileType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampInvalidFileType";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PNGPath = System.getProperty("user.dir")+_masterVO.getProperty("PNGFile");
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //RechargesBulk.clickDownloadUserListIcon();
                RechargesBulk.clickDownloadUserTemplateIcon();
                RechargesBulk.uploadFile(PNGPath);
                RechargesBulk.clickScheduleNowCheckbox();
                String occurrenceType = _masterVO.getProperty("Daily");
                RechargesBulk.selectOccurence(occurrenceType);
                String NoofDays = _masterVO.getProperty("Noofiterations");
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




    public void performGiftRechargeBulkRevampCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performGiftRechargeBulkRevampCopyButton";
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
                RechargesBulk.clickMakeAsGiftCheckbox();
                //ExcelUtility.deleteFiles(PathOfFile);
                //RechargesBulk.clickDownloadUserListIcon();
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
                String giftMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                String gifterName=UniqueChecker.UC_GifterName();
                String SubService = "1";
                String LanguageCode = "0";
                String GifterLangCode = "0";
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                Log.info("Writing to excel ....");
                ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
                ExcelUtility.setCellDataXLS(SubMSISDN,2, 0);   //MSISDN
                ExcelUtility.setCellDataXLS(SubService,2,1);   //SubService
                ExcelUtility.setCellDataXLS(amount,2,2);       //Amount
                ExcelUtility.setCellDataXLS(LanguageCode,2,3);   //LanguageCode
                ExcelUtility.setCellDataXLS(giftMSISDN,2,4);   //Gifter Mobile Number
                ExcelUtility.setCellDataXLS(gifterName,2,5);   //Gifter Name
                ExcelUtility.setCellDataXLS(GifterLangCode,2,6);   //Gifter Language Code
                Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Sub Service: " + SubService + ", Amount: " + amount + ", Language Code: " + LanguageCode + ", Gifter MSISDN: " + giftMSISDN + ", Gifter Name: " + gifterName + ", Gifter Language Code" + GifterLangCode);
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
