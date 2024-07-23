package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.vouchersBulk.vouchersBulk;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


public class DVDBulkRechargeRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    vouchersBulk VouchersBulk;

    public DVDBulkRechargeRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        ;
        VouchersBulk = new vouchersBulk(driver);
    }

    RandomGeneration RandomGeneration = new RandomGeneration();

    public void performDVDBulkRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;
        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null) {
            VouchersBulk.clickDownloadUserTemplateIcon();
            latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
            String noofVouchers = _masterVO.getProperty("voucherQty");
            String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            Log.info("Writing to excel ....");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
            ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
            ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
            ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
            ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
            ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
            Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
            String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
            String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
            VouchersBulk.uploadFile(uploadPath);
            VouchersBulk.clickRechargeIcon();
            VouchersBulk.enterPin(PIN);
            VouchersBulk.clickRechargeButton();
            String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
            boolean successPopUP = VouchersBulk.successPopUPVisibility();
            if (successPopUP == true) {
                transferID = VouchersBulk.transferID();
                transferStatus = VouchersBulk.transferStatus();
                soldStatus = DBHandler.AccessHandler.fetchBulkSoldStatus(transferID);
                if (soldStatus.equals("Y") && transferStatus.toUpperCase().contains(successStatus)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + " and Sold Status: " + soldStatus + ", hence Transaction Successful");
                    ExtentI.attachScreenShot();
                    ExtentI.attachCatalinaLogsForSuccess();

                } else {
                    currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
                    ExtentI.getChannelRequestDailyLogs(transferID);
                    ExtentI.getOneLineTXNLogsC2S(transferID);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
                VouchersBulk.clickDoneButton();
            } else {
                currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
                ExtentI.getChannelRequestDailyLogs(transferID);
                ExtentI.getOneLineTXNLogsC2S(transferID);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else
        {
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);

    }


    public void performDVDBulkRechargeResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeResetButton";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null) {
            VouchersBulk.clickDownloadUserTemplateIcon();
            latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
            String noofVouchers = _masterVO.getProperty("voucherQty");
            String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            Log.info("Writing to excel ....");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
            ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
            ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
            ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
            ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
            ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
            Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
            String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
            String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
            VouchersBulk.uploadFile(uploadPath);
            VouchersBulk.clickResetButton();
            VouchersBulk.clickRechargeIcon();
            Boolean checkUploadbutton;
            checkUploadbutton = VouchersBulk.checkUpload();
            if (checkUploadbutton) {
                ExtentI.Markup(ExtentColor.GREEN, "Reset button worked sucessfully");
                ExtentI.attachCatalinaLogsForSuccess();
            } else {
                ExtentI.Markup(ExtentColor.RED, "Upload Button are still available, reset button not working");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else
        {
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);
    }


    public void performDVDBulkRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;
        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null) {
            VouchersBulk.clickDownloadUserTemplateIcon();
            latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
            String noofVouchers = _masterVO.getProperty("voucherQty");
            String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            Log.info("Writing to excel ....");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
            ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
            ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
            ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
            ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
            ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
            Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
            String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
            String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
            VouchersBulk.uploadFile(uploadPath);
            VouchersBulk.clickRechargeIcon();
            VouchersBulk.enterPin(PIN);
            VouchersBulk.clickRechargeButton();
            VouchersBulk.clickCopyButton();

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
            transferID = VouchersBulk.transferID();
            Log.info("Copied Transfer ID fetched as : " + clipboard);
            if (clipboard.contains(transferID)) {
                ExtentI.Markup(ExtentColor.GREEN, "Transaction ID Copied");
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
            } else {
                currentNode.log(Status.FAIL, "Transaction ID Copy failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
            VouchersBulk.clickDoneButton();
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);
    }


    public void performDVDBulkRechargeInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRechargeInvalidMSISDN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);

        if(voucherType!=null) {
            VouchersBulk.clickDownloadUserTemplateIcon();
            latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
            String noofVouchers = _masterVO.getProperty("voucherQty");
            String SubMSISDN = RandomGeneration.randomNumeric(5);
            ;
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            Log.info("Writing to excel ....");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
            ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
            ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
            ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
            ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
            ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
            Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
            String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
            String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
            VouchersBulk.uploadFile(uploadPath);
            VouchersBulk.clickRechargeIcon();
            VouchersBulk.enterPin(PIN);
            VouchersBulk.clickRechargeButton();
            String expectedMessage = "Invalid subscriber's msisdn.";
            String actualMessage = VouchersBulk.fetchFailedReason();
            if (expectedMessage.equals(actualMessage)) {
                Assertion.assertContainsEquals(actualMessage, expectedMessage);
                ExtentI.attachCatalinaLogsForSuccess();
            } else {
                currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);
    }


    public void performDVDBulkRechargeNegativeQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeNegativeQuantity";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;
        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);

        if(voucherType!=null) {
            VouchersBulk.clickDownloadUserTemplateIcon();
            latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
            String noofVouchers = "-1";
            String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            Log.info("Writing to excel ....");
            ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
            ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
            ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
            ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
            ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
            ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
            ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
            Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
            String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
            String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
            VouchersBulk.uploadFile(uploadPath);
            VouchersBulk.clickRechargeIcon();
            VouchersBulk.enterPin(PIN);
            VouchersBulk.clickRechargeButton();
            String expectedMessage = "Invalid or empty wallet type.";
            String actualMessage = VouchersBulk.fetchFailedReason();
            if (expectedMessage.equals(actualMessage)) {
                Assertion.assertContainsEquals(actualMessage, expectedMessage);
                ExtentI.attachCatalinaLogsForSuccess();
            } else {
                currentNode.log(Status.FAIL, "Negative Quantity error not shown");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);

    }


    public void performDVDBulkRechargeBlankQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeBlankQuantity";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;
        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null) {
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = "";
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
        ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
        ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedMessage = "Number of Vouchers can not be blank.";
        String actualMessage = VouchersBulk.fetchFailedReason();
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Quantity error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);

    }


    public void performDVDBulkRechargeBlankVoucherType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeBlankVoucherType";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = "";
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(segmentCode!=null){
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = _masterVO.getProperty("voucherQty");
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
        ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
        ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedMessage = "Voucher Type Code can not be blank.";
        String actualMessage = VouchersBulk.fetchFailedReason();
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Voucher Type Code error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);

    }


    public void performDVDBulkRechargeBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeBlankPIN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null){
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = _masterVO.getProperty("voucherQty");
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
        ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
        ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        PIN = "";
        VouchersBulk.enterPin(PIN);
        Boolean confirmButtonDisabled = VouchersBulk.checkDisabledRechargeButton();
        if (confirmButtonDisabled) {
            ExtentI.Markup(ExtentColor.GREEN, "Confirm PIN button is disabled for blank PIN in C2S Bulk");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.RED, "C2S Bulk PIN Confirm Button is not disabled successfully with blank PIN");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);
    }


    public void performDVDBulkRechargeInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeInvalidPIN";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null){
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = _masterVO.getProperty("voucherQty");
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
        ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
        ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        PIN = RandomGeneration.randomNumeric(4);
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedmessage = "Invalid PIN";
        String actualMessage = VouchersBulk.transferStatus();
        if (actualMessage.contains(expectedmessage)) {
            ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Invalid PIN with message: " + actualMessage);
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();

        } else {
            currentNode.log(Status.FAIL, "Transaction went ahead with Invalid PIN with message: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);
    }


    public void performDVDBulkRechargeBlankSegment(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeBlankSegment";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null){
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = _masterVO.getProperty("voucherQty");
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
        ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedMessage = "Voucher Segment Code can not be blank.";
        String actualMessage = VouchersBulk.fetchFailedReason();
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Segment code error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);

    }


    public void performDVDBulkRechargeBlankDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeBlankDenomination";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null){
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = _masterVO.getProperty("voucherQty");
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
        ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedMessage = "Voucher Denomination can not be blank.";
        String actualMessage = VouchersBulk.fetchFailedReason();
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Voucher Denomination error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);

    }


    public void performDVDBulkRechargeBlankProfileID(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeBlankProfileID";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null){
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = _masterVO.getProperty("voucherQty");
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
        ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedMessage = "Voucher Profile ID can not be blank.";
        String actualMessage = VouchersBulk.fetchFailedReason();
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Voucher Profile ID error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);

    }

    public void performDVDBulkRechargeZeroQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeZeroQuantity";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();
        String latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.DVD_TEMPLATE);
        Log.info("Reading from excel ....");
        voucherType = ExcelUtility.getCellDataHSSF(2, 1);
        segmentCode = ExcelUtility.getCellDataHSSF(2, 3);
        denomination = ExcelUtility.getCellDataHSSF(2, 4);
        profileID = ExcelUtility.getCellDataHSSF(2, 6);
        Log.info("Read to Excel : Voucher Type: " + voucherType + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID);
        if(voucherType!=null){
        VouchersBulk.clickDownloadUserTemplateIcon();
        latestFileName = VouchersBulk.getLatestFilefromDir(PathOfFile);
        String noofVouchers = "0";
        String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(latestFileName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(SubMSISDN, 3, 0);   //MSISDN
        ExcelUtility.setCellDataXLS(voucherType, 3, 1);   //voucherType
        ExcelUtility.setCellDataXLS(segmentCode, 3, 2);       //segmentCode
        ExcelUtility.setCellDataXLS(denomination, 3, 3);   //denomination
        ExcelUtility.setCellDataXLS(profileID, 3, 4);   //Profile ID
        ExcelUtility.setCellDataXLS(noofVouchers, 3, 5);   //Number of Vouchers
        Log.info("Written to Excel : MSISDN: " + SubMSISDN + ", Segment Code: " + segmentCode + ", Denomination: " + denomination + ", Profile ID: " + profileID + ", No of Vouchers: " + noofVouchers);
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedMessage = "Number of Vouchers can not be zero.";
        String actualMessage = VouchersBulk.fetchFailedReason();
        if (expectedMessage.equals(actualMessage)) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Voucher Profile ID error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else{
            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        }
        Log.methodExit(methodname);


    }


    public void performDVDBulkRechargeInvalidFileType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeInvalidFileType";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PNGPath = System.getProperty("user.dir")+_masterVO.getProperty("PNGFile");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);


        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadMasterSheetIcon();

        VouchersBulk.uploadFile(PNGPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String errorMessageCaptured = VouchersBulk.fileUploadTypeErrorMessage();
        String expectedmessage = "Only CSV,XLS & XLSX Files are allowed";
        if (errorMessageCaptured.equals(expectedmessage)) {
            Assertion.assertContainsEquals(errorMessageCaptured, expectedmessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Invalid File Upload error not shown");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname);

    }



    public void performDVDBulkRechargeTemplatewithoutData(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeTemplatewithoutData";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;
        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        VouchersBulk.clickDownloadUserTemplateIcon();
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();


        String expectedmessage = "No valid record found in file.";
        String actualMessage = VouchersBulk.UploadStatus();
            if (actualMessage.contains(expectedmessage)) {
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();

            } else {
                currentNode.log(Status.FAIL, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        Log.methodExit(methodname);

    }


    public void performDVDBulkRechargeBlankTemplateFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDBulkRechargeBlankTemplateFile";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;
        String voucherType, segmentCode, denomination, profileID;

        if (VouchersBulk.isRechargeVisible()) {
            VouchersBulk.clickRecharge();
        } else {
            VouchersBulk.clickRechargeHeading();
            //VouchersBulk.clickRecharge();
        }
        VouchersBulk.clickC2SBulkOperationHeading();
        VouchersBulk.clickBulkDVDRecharge();
        ExcelUtility.createBlankExcelFile(PathOfFile+"C2SBulkTransfer.xls");
        Log.info("Created an Empty Excel File");
        String filename = VouchersBulk.getLatestFileNamefromDir(PathOfFile);
        String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("DVDBulkRechargeUpload") + filename;
        VouchersBulk.uploadFile(uploadPath);
        VouchersBulk.clickRechargeIcon();
        VouchersBulk.enterPin(PIN);
        VouchersBulk.clickRechargeButton();
        String expectedmessage = "Invalid file format. Not a valid XLS format.";
        String actualMessage = VouchersBulk.transferStatus();
            if (actualMessage.contains(expectedmessage)) {
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();

            } else {
                currentNode.log(Status.FAIL, "Transaction Not Successful with Template File of Blank Data: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
    }
}



