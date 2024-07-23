package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.recharge.Recharges;
import angular.pageobjects.voucher.Vouchers;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DVDRechargeRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    Vouchers vouchers;

    public DVDRechargeRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();

        vouchers = new Vouchers(driver);
    }

    RandomGeneration RandomGeneration = new RandomGeneration();

    public void performDVDRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    //vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherDenomAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherDenomAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            vouchers.enterPin(PIN);
                            vouchers.clickRechargeButton();
                            String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
                            boolean successPopUP = vouchers.successPopUPVisibility();
                            if (successPopUP == true) {
                                transferID = vouchers.transferID();
                                trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
                                transferStatus = vouchers.transferStatus();
                                soldStatus = DBHandler.AccessHandler.fetchSoldStatus(transferID);
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
                                vouchers.clickDoneButton();
                            } else {
                                currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
                                ExtentI.getChannelRequestDailyLogs(transferID);
                                ExtentI.getOneLineTXNLogsC2S(transferID);
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        Log.methodExit(methodname);
    }


    public void performDVDRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRechargeCopyButton";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;
        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        if (vouchers.isRechargeVisible()) {
            vouchers.clickRecharge();
            vouchers.clickDVDHeading();
        } else {
            vouchers.clickRechargeHeading();
            vouchers.clickRecharge();
            vouchers.clickDVDHeading();
        }

        HashMap<String, String> vomsList = new HashMap<>();
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));


                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherDenomAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherDenomAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            vouchers.enterPin(PIN);
                            vouchers.clickRechargeButton();
                            vouchers.clickCopyButton();

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
                            transferID = vouchers.transferID();
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
                            vouchers.clickDoneButton();
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        Log.methodExit(methodname);
    }


    public void performDVDRechargeInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherdenomAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherdenomAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            PIN = RandomGeneration.randomNumeric(4);
                            vouchers.enterPin(PIN);
                            vouchers.clickRechargeButton();
                            boolean successPopUP = vouchers.successPopUPVisibility();
                            String expectedmessage = "Invalid PIN";
                            String actualMessage = vouchers.transferStatus();
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
                                break outerloop;
                            }
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        Log.methodExit(methodname);
    }


    public void performDVDRechargeBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherTypeAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherTypeAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            PIN = "";
                            vouchers.enterPin(PIN);
                            Boolean confirmButtonDisabled = vouchers.checkDisabledRechargeButton();
                            if (confirmButtonDisabled) {
                                ExtentI.Markup(ExtentColor.GREEN, "Confirm PIN button is disabled for blank PIN in C2S Bulk");
                                ExtentI.attachCatalinaLogsForSuccess();
                            } else {
                                ExtentI.Markup(ExtentColor.RED, "C2S Bulk PIN Confirm Button is not disabled successfully with blank PIN");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }


    public void performDVDRechargeBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = "";
                vouchers.enterSubMSISDN(SubMSISDN);

                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherTypeAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherTypeAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                            String actualMessage = null;
                            String expectedMessage = "Mobile nunber is required.";
                            boolean flag1 = false;
                            for (WebElement ele : errorMessageCaptured) {
                                actualMessage = ele.getText();
                                if (expectedMessage.equals(actualMessage)) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            if (flag1) {
                                Assertion.assertContainsEquals(actualMessage, expectedMessage);
                                ExtentI.attachCatalinaLogsForSuccess();
                            } else {
                                currentNode.log(Status.FAIL, "Blank MSISDN error not shown");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }


    public void performDVDRechargeBlankQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = "";
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherTypeAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherTypeAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                            String actualMessage = null;
                            String expectedMessage = "Quantity is required.";
                            boolean flag1 = false;
                            for (WebElement ele : errorMessageCaptured) {
                                actualMessage = ele.getText();
                                if (expectedMessage.equals(actualMessage)) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            if (flag1) {
                                Assertion.assertContainsEquals(actualMessage, expectedMessage);
                                ExtentI.attachCatalinaLogsForSuccess();
                            } else {
                                currentNode.log(Status.FAIL, "Blank Quantity error not shown");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }


    public void performDVDRechargeBlankVoucherType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;


        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);

                String Quantity = "1";
                vouchers.enterQuantity(Quantity);
                vouchers.clickRechargeIcon();
                List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                String actualMessage = null;
                String expectedMessage = "Voucher type is required";
                boolean flag1 = false;
                for (WebElement ele : errorMessageCaptured) {
                    actualMessage = ele.getText();
                    if (expectedMessage.equals(actualMessage)) {
                        flag1 = true;
                        break;
                    }
                }
                if (flag1) {
                    Assertion.assertContainsEquals(actualMessage, expectedMessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Blank Voucher Type error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
                break outerloop;
            }

        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        Log.methodExit(methodname);

    }


    public void performDVDRechargeInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = RandomGeneration.randomNumeric(5);
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherTypeAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherTypeAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                            String actualMessage = null;
                            String expectedMessage = "Please, Enter valid Mobile Number.";
                            boolean flag1 = false;
                            for (WebElement ele : errorMessageCaptured) {
                                actualMessage = ele.getText();
                                if (expectedMessage.equals(actualMessage)) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            if (flag1) {
                                Assertion.assertContainsEquals(actualMessage, expectedMessage);
                                ExtentI.attachCatalinaLogsForSuccess();
                            } else {
                                currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }


    public void performDVDRechargeNegativeQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = "-1";
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherTypeAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherTypeAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                            String actualMessage = null;
                            String expectedMessage = "Invalid Value.";
                            boolean flag1 = false;
                            for (WebElement ele : errorMessageCaptured) {
                                actualMessage = ele.getText();
                                if (expectedMessage.equals(actualMessage)) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            if (flag1) {
                                Assertion.assertContainsEquals(actualMessage, expectedMessage);
                                ExtentI.attachCatalinaLogsForSuccess();
                            } else {
                                currentNode.log(Status.FAIL, "Negative Quantity error not shown");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }


    public void performDVDRechargeAlphanumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = RandomGeneration.randomAlphaNumeric(5);
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherdenomAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherdenomAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                            String actualMessage = null;
                            String expectedMessage = "Please, Enter valid Mobile Number.";
                            for (WebElement ele : errorMessageCaptured) {
                                actualMessage = ele.getText();
                                Assertion.assertContainsEquals(actualMessage, expectedMessage);

                                if (expectedMessage.equals(actualMessage)) {
                                    ExtentI.Markup(ExtentColor.GREEN, "Validation Error Status Found as: " + actualMessage);
                                    ExtentI.attachCatalinaLogsForSuccess();
                                    ExtentI.attachScreenShot();
                                    break;
                                } else {
                                    currentNode.log(Status.FAIL, "Alphanumeric MSISDN error not shown");
                                    ExtentI.attachCatalinaLogs();
                                    ExtentI.attachScreenShot();
                                }
                                break outerloop;
                            }
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }

    public void performDVDRechargeAddSlots(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherdenomAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherdenomAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickAddButton();
                            if (voucherType.contains("digital")) {
                                String type = "Digital";
                                availabilityVoucherType = vouchers.selectAddVoucherType(type);
                            } else if (voucherType.contains("test_digit")) {
                                String type = "Digital test";
                                availabilityVoucherType = vouchers.selectAddVoucherType(type);
                            }
                            if (availabilityVoucherType) {
                                Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                                vouchers.selectAddSegment(Segment);
                                voucherdenomAvailable = vouchers.selectAddDenominationDVD(vomsList.get("vomsMRP"));
                                if (voucherdenomAvailable) {
                                    voucherProfileAvailable = vouchers.selectAddProfile(vomsList.get("vomsProfileName"));
                                    if (voucherProfileAvailable) {
                                        vouchers.enterAddQuantity(Quantity);
                                        vouchers.clickRechargeIcon();
                                        vouchers.enterPin(PIN);
                                        vouchers.clickRechargeButton();
                                        String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
                                        boolean successPopUP = vouchers.successPopUPVisibility();
                                        if (successPopUP == true) {
                                            transferID = vouchers.transferID();
                                            trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
                                            transferStatus = vouchers.transferStatus();
                                            soldStatus = DBHandler.AccessHandler.fetchSoldStatus(transferID);
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
                                            vouchers.clickDoneButton();
                                        } else {
                                            currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
                                            ExtentI.getChannelRequestDailyLogs(transferID);
                                            ExtentI.getOneLineTXNLogsC2S(transferID);
                                            ExtentI.attachCatalinaLogs();
                                            ExtentI.attachScreenShot();
                                        }
                                        break outerloop;
                                    } else
                                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                                } else
                                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                            } else
                                Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }


    public void performDVDRechargeBlankDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                String Quantity = _masterVO.getProperty("voucherQty");

                vouchers.enterQuantity(Quantity);
                vouchers.clickRechargeIcon();
                List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                String actualMessage = null;
                String expectedMessage = "Voucher Denomination Required.";
                boolean flag1 = false;
                for (WebElement ele : errorMessageCaptured) {
                    actualMessage = ele.getText();
                    if (expectedMessage.equals(actualMessage)) {
                        flag1 = true;
                        break;
                    }
                }
                if (flag1) {
                    Assertion.assertContainsEquals(actualMessage, expectedMessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Blank Voucher Type error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
                break outerloop;

            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        Log.methodExit(methodname);
    }


    public void performDVDRechargeBlankVoucherProfile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                String Quantity = _masterVO.getProperty("voucherQty");

                vouchers.enterQuantity(Quantity);
                vouchers.clickRechargeIcon();
                List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                String actualMessage = null;
                String expectedMessage = "Profile name is required";
                boolean flag1 = false;
                for (WebElement ele : errorMessageCaptured) {
                    actualMessage = ele.getText();
                    if (expectedMessage.equals(actualMessage)) {
                        flag1 = true;
                        break;
                    }
                }
                if (flag1) {
                    Assertion.assertContainsEquals(actualMessage, expectedMessage);
                    ExtentI.attachCatalinaLogsForSuccess();
                } else {
                    currentNode.log(Status.FAIL, "Blank Voucher Profile error not shown");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
                break outerloop;
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
        Log.methodExit(methodname);
    }


    public void performDVDRechargeZeroQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = "0";
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherdenomAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherdenomAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickRechargeIcon();
                            List<WebElement> errorMessageCaptured = vouchers.blankErrorMessages();
                            String actualMessage = null;
                            String expectedMessage = "Invalid Value.";
                            boolean flag1 = false;
                            for (WebElement ele : errorMessageCaptured) {
                                actualMessage = ele.getText();
                                if (expectedMessage.equals(actualMessage)) {
                                    flag1 = true;
                                    break;
                                }
                            }
                            if (flag1) {
                                Assertion.assertContainsEquals(actualMessage, expectedMessage);
                                ExtentI.attachCatalinaLogsForSuccess();
                            } else {
                                currentNode.log(Status.FAIL, "Zero Quantity error not shown");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                            break outerloop;
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }

    public void performDVDRechargeDeleteSlot(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodname = "performDVDRecharge";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String DigitalTest = PretupsI.VOUCHER_TYPE_TEST_DIGITAL;
        String Digital = PretupsI.VOUCHER_TYPE_DIGITAL;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        Boolean flag = false;

        String transferID = null, transferStatus = null, trf_status = null, soldStatus = null;

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
        int vomsCount = ExcelUtility.getRowCount();
        HashMap<String, String> vomsList = new HashMap<>();
        outerloop:
        for (int i = 1, j = 0; i <= vomsCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(Digital) || ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).contains(DigitalTest)) {
                flag = true;
                vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
                vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
                vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
                vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
                j++;
                Log.info("Fetched Details for Vouchers: Voucher Type: " + vomsList.get("vomsProfileName") + ", Sub Service: " + vomsList.get("vomsProfileName") + " MRP: " + vomsList.get("vomsMRP") + " Voucher Profile: " + vomsList.get("vomsProfileName"));

                if (vouchers.isRechargeVisible()) {
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                } else {
                    vouchers.clickRechargeHeading();
                    vouchers.clickRecharge();
                    vouchers.clickDVDHeading();
                }

                String productID = DBHandler.AccessHandler.fetchProductID(vomsList.get("vomsProfileName"));
                String voucherSegment = DBHandler.AccessHandler.getVoucherSegment(productID);
                String voucherType = vomsList.get("voucherType");
                Log.info("Voucher Segment:" + voucherSegment);
                Log.info("Voucher Type:" + voucherType);
                String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                vouchers.enterSubMSISDN(SubMSISDN);
                Boolean availabilityVoucherType = false;
                String Quantity = _masterVO.getProperty("voucherQty");
                if (voucherType.contains("digital")) {
                    String type = "Digital";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                } else if (voucherType.contains("test_digit")) {
                    String type = "Digital test";
                    availabilityVoucherType = vouchers.selectVoucherType(type);
                }
                if (availabilityVoucherType) {
                    String Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                    vouchers.selectSegment(Segment);

                    Boolean voucherdenomAvailable = vouchers.selectDenominationDVD(vomsList.get("vomsMRP"));
                    if (voucherdenomAvailable) {
                        Boolean voucherProfileAvailable = vouchers.selectProfile(vomsList.get("vomsProfileName"));
                        if (voucherProfileAvailable) {
                            vouchers.enterQuantity(Quantity);
                            vouchers.clickAddButton();
                            if (voucherType.contains("digital")) {
                                String type = "Digital";
                                availabilityVoucherType = vouchers.selectAddVoucherType(type);
                            } else if (voucherType.contains("test_digit")) {
                                String type = "Digital test";
                                availabilityVoucherType = vouchers.selectAddVoucherType(type);
                            }
                            if (availabilityVoucherType) {
                                Segment = DBHandler.AccessHandler.getLookUpNameByCode(voucherSegment);
                                vouchers.selectAddSegment(Segment);
                                voucherdenomAvailable = vouchers.selectAddDenominationDVD(vomsList.get("vomsMRP"));
                                if (voucherdenomAvailable) {
                                    voucherProfileAvailable = vouchers.selectAddProfile(vomsList.get("vomsProfileName"));
                                    if (voucherProfileAvailable) {
                                        vouchers.enterAddQuantity(Quantity);
                                        vouchers.clickDeleteButton();
                                        Boolean checkQuantity;
                                        checkQuantity = vouchers.checkAddQuantity();

                                        if (!checkQuantity) {
                                            ExtentI.Markup(ExtentColor.GREEN, "Delete button worked sucessfully");
                                            ExtentI.attachCatalinaLogsForSuccess();
                                        } else {
                                            ExtentI.Markup(ExtentColor.RED, "Additional Slots are still available, delete button still not available");
                                            ExtentI.attachCatalinaLogs();
                                            ExtentI.attachScreenShot();
                                        }
                                        break outerloop;
                                    } else
                                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                                } else
                                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                            } else
                                Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                        } else
                            Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                    } else
                        Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
                } else
                    Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");
            }
        }
        if (!flag) Assertion.assertSkip("As vouchers are not available for " + FromCategory + ", case is skipped.");

        Log.methodExit(methodname);
    }
}