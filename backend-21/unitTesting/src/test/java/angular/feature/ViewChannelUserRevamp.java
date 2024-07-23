package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.ViewChannelUser.ViewChannelUser ;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import restassuredapi.test.FetchUserDetails;

import javax.swing.text.View;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewChannelUserRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    ViewChannelUser ViewChannelUser;
    Map<String, String> ResultMap;

    public ViewChannelUserRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        ViewChannelUser = new ViewChannelUser(driver);
        ResultMap = new HashMap();
    }

    RandomGeneration RandomGeneration = new RandomGeneration();


    public void PerformViewChannelUserByMSISDN(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodname = "PerformViewChannelUserByMSISDN";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID );
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        ViewChannelUser.spinnerWait();
        if(FromCategory.equals(ParentCategory)) {
            ViewChannelUser.clickChannelUserHeading();
            ViewChannelUser.spinnerWait();
            ViewChannelUser.enterSearchField(toMSISDN);
            ViewChannelUser.clickUsernameOfChildUser();
            ViewChannelUser.spinnerWait();
            String fetchedUsrNme = ViewChannelUser.getUserName();
            fetchedUsrNme = fetchedUsrNme.substring(fetchedUsrNme.indexOf(' ') + 1);
            Log.info("Fetched User Name :" + fetchedUsrNme);
            String CUmsisdn = ViewChannelUser.getMSISDN();
            String[] _arr = CUmsisdn.split("\\s");
            CUmsisdn = _arr[0];
            Log.info("Fetched MSISDN :" + CUmsisdn);
            String Geography = ViewChannelUser.getGeography();
            String externalCode = ViewChannelUser.getExternalCode();
            String CUDomain = ViewChannelUser.getCUDomain();
            String CUCategory = ViewChannelUser.getCUCategory();
            String CUPrntCategory = ViewChannelUser.getCUParentCategory();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String DPCUmsisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, RowNum);
            Log.info("MSISDN fetched from DataProvider :" + DPCUmsisdn);
            String DPGeography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, RowNum);
            Log.info("Geography fetched from DataProvider :" + DPGeography);
            String DPexternalCode = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, RowNum);
            Log.info("External Code fetched from DataProvider :" + DPexternalCode);
            String DPCUDomain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, RowNum);
            Log.info("Domain fetched from DataProvider :" + DPCUDomain);
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);
            String DPCUPrntCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, RowNum);
            Log.info("Parent Category fetched from DataProvider :" + DPCUPrntCategory);
            if (fetchedUsrNme.equals(DPfetchedUsrNme) && CUmsisdn.equals(DPCUmsisdn) && Geography.equals(DPGeography) && externalCode.equals(DPexternalCode) && CUDomain.equals(DPCUDomain) && CUCategory.equals(DPCUCategory) && CUPrntCategory.equals(DPCUPrntCategory)) {
                Log.info("Personal Details Tab fields and data validated.");
                ViewChannelUser.clickLoginDetailsTab();
                String CULoginID = ViewChannelUser.getCULoginID();
                CUmsisdn = ViewChannelUser.getLoginDetailsMSISDN();
                String[] _arr1 = CUmsisdn.split("\\s");
                CUmsisdn = _arr1[0];
                Log.info("Fetched MSISDN :" + CUmsisdn);
                String DPCULoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
                Log.info("Login ID fetched from Data Provider :" + DPCULoginID);
                if (CULoginID.equals(DPCULoginID) && CUmsisdn.equals(DPCUmsisdn)) {
                    ViewChannelUser.clickRoleDetailsTab();
                    Boolean sysRole = ViewChannelUser.ifSysRoleExists();
                    Boolean grpRole = ViewChannelUser.ifGrpRoleExists();
                    if (sysRole && grpRole) {
                        ViewChannelUser.clickPaymentServiceDetailsTab();
                        Boolean pmtInfo = ViewChannelUser.ifPaymentInformationExists();
                        Boolean spnRgts = ViewChannelUser.ifSuspensionRightsExists();
                        Boolean srvInfo = ViewChannelUser.ifServiceInformationExists();
                        Boolean vchrType = ViewChannelUser.ifVoucherTypeExists();
                        Boolean lwBlncAlrt = ViewChannelUser.ifLowBalanceAlertExists();
                        if (pmtInfo && spnRgts && srvInfo && vchrType && lwBlncAlrt) {
                            ViewChannelUser.clickProfileDetailsTab();
                            String CUCommProf = ViewChannelUser.getCUCommissionProfile();
                            String CUTrnfProf = ViewChannelUser.getCUTransferProfile();
                            String DPCUCommProf = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, RowNum);
                            Log.info("Fetched Commission Profile from DataProvider : " + DPCUCommProf);
                            String DPCUTrnfProf = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, RowNum);
                            Log.info("Fetched Transfer Profile from DataProvider : " + DPCUTrnfProf);
                            if (CUCommProf.equals(DPCUCommProf) && CUTrnfProf.equals(DPCUTrnfProf)) {
                                ViewChannelUser.clickThresholdDetailsTab();
                                Boolean blncPrfncs = ViewChannelUser.ifBalancePreferencesExists();
                                Boolean tnsfCntoPrfl = ViewChannelUser.ifTransferControlProfileExists();
                                if (blncPrfncs && tnsfCntoPrfl) {
                                    ExtentI.Markup(ExtentColor.GREEN, "View Channel User is successful with all the details in the tabs");
                                    ExtentI.attachCatalinaLogsForSuccess();
                                    ExtentI.attachScreenShot();
                                } else {
                                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    ExtentI.attachCatalinaLogs();
                                    ExtentI.attachScreenShot();
                                }
                            } else {
                                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Profiles Tab");
                                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Profiles Tab");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                        } else {
                            ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            ExtentI.attachCatalinaLogs();
                            ExtentI.attachScreenShot();
                        }
                    } else {
                        ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Role Details Tab");
                        currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Role Details Tab");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                } else {
                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Login Details Tab");
                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Login Details Tab");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Personal Details Tab");
                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Personal Details Details Tab");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("View Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


    public void PerformViewChannelUserByLoginID(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodname = "PerformViewChannelUserByLoginID";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        ViewChannelUser.spinnerWait();
        ViewChannelUser.clickChannelUserHeading();
        ViewChannelUser.spinnerWait();
        if(FromCategory.equals(ParentCategory)) {
            ViewChannelUser.enterSearchField(toLoginID);
            ViewChannelUser.clickUsernameOfChildUser();
            ViewChannelUser.spinnerWait();
            String fetchedUsrNme = ViewChannelUser.getUserName();
            fetchedUsrNme = fetchedUsrNme.substring(fetchedUsrNme.indexOf(' ') + 1);
            Log.info("Fetched User Name :" +fetchedUsrNme);
            String CUmsisdn = ViewChannelUser.getMSISDN();
            String[] _arr = CUmsisdn.split("\\s");
            CUmsisdn = _arr[0];
            Log.info("Fetched MSISDN :" +CUmsisdn);
            String Geography = ViewChannelUser.getGeography();
            String externalCode = ViewChannelUser.getExternalCode();
            String CUDomain = ViewChannelUser.getCUDomain();
            String CUCategory = ViewChannelUser.getCUCategory();
            String CUPrntCategory = ViewChannelUser.getCUParentCategory();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String DPCUmsisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, RowNum);
            Log.info("MSISDN fetched from DataProvider :" + DPCUmsisdn);
            String DPGeography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, RowNum);
            Log.info("Geography fetched from DataProvider :" + DPGeography);
            String DPexternalCode = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, RowNum);
            Log.info("External Code fetched from DataProvider :" + DPexternalCode);
            String DPCUDomain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, RowNum);
            Log.info("Domain fetched from DataProvider :" + DPCUDomain);
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);
            String DPCUPrntCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, RowNum);
            Log.info("Parent Category fetched from DataProvider :" + DPCUPrntCategory);
            if(fetchedUsrNme.equals(DPfetchedUsrNme) && CUmsisdn.equals(DPCUmsisdn) && Geography.equals(DPGeography) && externalCode.equals(DPexternalCode) && CUDomain.equals(DPCUDomain) && CUCategory.equals(DPCUCategory) && CUPrntCategory.equals(DPCUPrntCategory)) {
                Log.info("Personal Details Tab fields and data validated.");
                ViewChannelUser.clickLoginDetailsTab();
                String CULoginID = ViewChannelUser.getCULoginID();
                CUmsisdn = ViewChannelUser.getLoginDetailsMSISDN();
                String[] _arr1 = CUmsisdn.split("\\s");
                CUmsisdn = _arr1[0];
                Log.info("Fetched MSISDN :" +CUmsisdn);
                String DPCULoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
                Log.info("Login ID fetched from Data Provider :" +DPCULoginID);
                if(CULoginID.equals(DPCULoginID) && CUmsisdn.equals(DPCUmsisdn)) {
                    ViewChannelUser.clickRoleDetailsTab();
                    Boolean sysRole = ViewChannelUser.ifSysRoleExists();
                    Boolean grpRole = ViewChannelUser.ifGrpRoleExists();
                    if(sysRole && grpRole) {
                        ViewChannelUser.clickPaymentServiceDetailsTab();
                        Boolean pmtInfo = ViewChannelUser.ifPaymentInformationExists();
                        Boolean spnRgts = ViewChannelUser.ifSuspensionRightsExists();
                        Boolean srvInfo = ViewChannelUser.ifServiceInformationExists();
                        Boolean vchrType = ViewChannelUser.ifVoucherTypeExists();
                        Boolean lwBlncAlrt = ViewChannelUser.ifLowBalanceAlertExists();
                        if(pmtInfo && spnRgts && srvInfo && vchrType && lwBlncAlrt) {
                            ViewChannelUser.clickProfileDetailsTab();
                            String CUCommProf = ViewChannelUser.getCUCommissionProfile();
                            String CUTrnfProf = ViewChannelUser.getCUTransferProfile();
                            String DPCUCommProf = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, RowNum);
                            Log.info("Fetched Commission Profile from DataProvider : "+DPCUCommProf);
                            String DPCUTrnfProf = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, RowNum);
                            Log.info("Fetched Transfer Profile from DataProvider : "+DPCUTrnfProf);
                            if(CUCommProf.equals(DPCUCommProf) && CUTrnfProf.equals(DPCUTrnfProf)) {
                                ViewChannelUser.clickThresholdDetailsTab();
                                Boolean blncPrfncs = ViewChannelUser.ifBalancePreferencesExists();
                                Boolean tnsfCntoPrfl = ViewChannelUser.ifTransferControlProfileExists();
                                if(blncPrfncs && tnsfCntoPrfl)
                                {
                                    ExtentI.Markup(ExtentColor.GREEN, "View Channel User is successful with all the details in the tabs");
                                    ExtentI.attachCatalinaLogsForSuccess();
                                    ExtentI.attachScreenShot();
                                }
                                else{
                                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    ExtentI.attachCatalinaLogs();
                                    ExtentI.attachScreenShot();
                                }
                            }
                            else{
                                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Profiles Tab");
                                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Profiles Tab");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                        }
                        else
                        {
                            ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            ExtentI.attachCatalinaLogs();
                            ExtentI.attachScreenShot();
                        }
                    }
                    else{
                        ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Role Details Tab");
                        currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Role Details Tab");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
                else{
                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Login Details Tab");
                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Login Details Tab");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Personal Details Tab");
                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Personal Details Details Tab");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("View Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }



    public void PerformViewChannelUserByUserName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodname = "PerformViewChannelUserByUserName";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String CUUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
        ViewChannelUser.spinnerWait();
        Log.info("Fetched User Name: " + CUUserName);
        ViewChannelUser.clickChannelUserHeading();
        ViewChannelUser.spinnerWait();
        if(FromCategory.equals(ParentCategory)){
            ViewChannelUser.enterSearchField(CUUserName);
            ViewChannelUser.clickUsrNmWhenEntered(CUUserName);
            ViewChannelUser.spinnerWait();
            String fetchedUsrNme = ViewChannelUser.getUserName();
            fetchedUsrNme = fetchedUsrNme.substring(fetchedUsrNme.indexOf(' ') + 1);
            Log.info("Fetched User Name :" +fetchedUsrNme);
            String CUmsisdn = ViewChannelUser.getMSISDN();
            String[] _arr = CUmsisdn.split("\\s");
            CUmsisdn = _arr[0];
            Log.info("Fetched MSISDN :" +CUmsisdn);
            String Geography = ViewChannelUser.getGeography();
            String externalCode = ViewChannelUser.getExternalCode();
            String CUDomain = ViewChannelUser.getCUDomain();
            String CUCategory = ViewChannelUser.getCUCategory();
            String CUPrntCategory = ViewChannelUser.getCUParentCategory();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String DPCUmsisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, RowNum);
            Log.info("MSISDN fetched from DataProvider :" + DPCUmsisdn);
            String DPGeography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, RowNum);
            Log.info("Geography fetched from DataProvider :" + DPGeography);
            String DPexternalCode = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, RowNum);
            Log.info("External Code fetched from DataProvider :" + DPexternalCode);
            String DPCUDomain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, RowNum);
            Log.info("Domain fetched from DataProvider :" + DPCUDomain);
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);
            String DPCUPrntCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, RowNum);
            Log.info("Parent Category fetched from DataProvider :" + DPCUPrntCategory);
            if(fetchedUsrNme.equals(DPfetchedUsrNme) && CUmsisdn.equals(DPCUmsisdn) && Geography.equals(DPGeography) && externalCode.equals(DPexternalCode) && CUDomain.equals(DPCUDomain) && CUCategory.equals(DPCUCategory) && CUPrntCategory.equals(DPCUPrntCategory)) {
                Log.info("Personal Details Tab fields and data validated.");
                ViewChannelUser.clickLoginDetailsTab();
                String CLoginID = ViewChannelUser.getCULoginID();
                CUmsisdn = ViewChannelUser.getLoginDetailsMSISDN();
                String[] _arr1 = CUmsisdn.split("\\s");
                CUmsisdn = _arr1[0];
                Log.info("Fetched MSISDN :" +CUmsisdn);
                String DPCULoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
                Log.info("Login ID fetched from Data Provider :" +DPCULoginID);
                if(CLoginID.equals(DPCULoginID) && CUmsisdn.equals(DPCUmsisdn)) {
                    ViewChannelUser.clickRoleDetailsTab();
                    Boolean sysRole = ViewChannelUser.ifSysRoleExists();
                    Boolean grpRole = ViewChannelUser.ifGrpRoleExists();
                    if(sysRole && grpRole) {
                        ViewChannelUser.clickPaymentServiceDetailsTab();
                        Boolean pmtInfo = ViewChannelUser.ifPaymentInformationExists();
                        Boolean spnRgts = ViewChannelUser.ifSuspensionRightsExists();
                        Boolean srvInfo = ViewChannelUser.ifServiceInformationExists();
                        Boolean vchrType = ViewChannelUser.ifVoucherTypeExists();
                        Boolean lwBlncAlrt = ViewChannelUser.ifLowBalanceAlertExists();
                        if(pmtInfo && spnRgts && srvInfo && vchrType && lwBlncAlrt) {
                            ViewChannelUser.clickProfileDetailsTab();
                            String CUCommProf = ViewChannelUser.getCUCommissionProfile();
                            String CUTrnfProf = ViewChannelUser.getCUTransferProfile();
                            String DPCUCommProf = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, RowNum);
                            Log.info("Fetched Commission Profile from DataProvider : "+DPCUCommProf);
                            String DPCUTrnfProf = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, RowNum);
                            Log.info("Fetched Transfer Profile from DataProvider : "+DPCUTrnfProf);
                            if(CUCommProf.equals(DPCUCommProf) && CUTrnfProf.equals(DPCUTrnfProf)) {
                                ViewChannelUser.clickThresholdDetailsTab();
                                Boolean blncPrfncs = ViewChannelUser.ifBalancePreferencesExists();
                                Boolean tnsfCntoPrfl = ViewChannelUser.ifTransferControlProfileExists();
                                if(blncPrfncs && tnsfCntoPrfl)
                                {
                                    ExtentI.Markup(ExtentColor.GREEN, "View Channel User is successful with all the details in the tabs");
                                    ExtentI.attachCatalinaLogsForSuccess();
                                    ExtentI.attachScreenShot();
                                }
                                else{
                                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    ExtentI.attachCatalinaLogs();
                                    ExtentI.attachScreenShot();
                                }
                            }
                            else{
                                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Profiles Tab");
                                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Profiles Tab");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                        }
                        else
                        {
                            ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            ExtentI.attachCatalinaLogs();
                            ExtentI.attachScreenShot();
                        }
                    }
                    else{
                        ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Role Details Tab");
                        currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Role Details Tab");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
                else{
                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Login Details Tab");
                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Login Details Tab");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Personal Details Tab");
                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Personal Details Details Tab");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
        Assertion.assertSkip("View Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


    public void PerformViewChannelUserResetButton(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodname = "PerformViewChannelUserResetButton";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

        //Retailer cannot view users of upper hierarchy
        if (FromCategory == "Retailer"){
            Log.info(FromCategory + " cannot perform View channel user operation");
        }
        else {
            String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
            Log.info("LOGINID : " + loginID);
            login.UserLogin(driver, "ChannelUser", fromParent, FromCategory);
            ViewChannelUser.spinnerWait();
            ViewChannelUser.clickChannelUserHeading();
            ViewChannelUser.spinnerWait();
            String searchBy = RandomGeneration.randomAlphaNumeric(8);
            ViewChannelUser.enterSearchField(searchBy);
            ViewChannelUser.clickResetButton();
            ViewChannelUser.spinnerWait();
            String blankSearchField = ViewChannelUser.getBlankSearchField();
            Boolean checkSearchField = blankSearchField.equals("");
            if (checkSearchField) {
                ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
            } else {
                currentNode.log(Status.FAIL, "Fields are not blank hence Reset button failed.");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
    }

        public void PerformViewChannelUserInvalidSearchField(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
            final String methodname = "PerformViewChannelUserInvalidSearchField";
            Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum);
            String MasterSheetPath = _masterVO.getProperty("DataProvider");
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
            Log.info("LOGINID : " + loginID);
            login.UserLogin(driver, "ChannelUser", fromParent, FromCategory);
            ViewChannelUser.spinnerWait();
            ViewChannelUser.clickChannelUserHeading();
            ViewChannelUser.spinnerWait();
            String searchBy = RandomGeneration.randomAlphaNumeric(8);
            ViewChannelUser.enterSearchField(searchBy);
            String actualMessage = "No matching records found";
            String expectedMessage = ViewChannelUser.getSearchFieldError();
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            if (expectedMessage.equals(actualMessage)) {
                ExtentI.Markup(ExtentColor.GREEN, "Validation Error Message for Search Field Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
                ExtentI.attachScreenShot();
            } else {
                currentNode.log(Status.FAIL, "Error message for blank search field not displayed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }


    public void PerformViewChannelUserActiveUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodname = "PerformViewChannelUserActiveUser";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : " + loginID);
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        ViewChannelUser.spinnerWait();
        ViewChannelUser.clickChannelUserHeading();
            String CUStatus = "Active";
            ViewChannelUser.spinnerWait();
            ViewChannelUser.selectCUStatus(CUStatus);
            ViewChannelUser.clickUsernameOfChildUser();
            ViewChannelUser.spinnerWait();
            Boolean checkUsrNme = ViewChannelUser.userNameExists();
            Boolean checkMSISDN = ViewChannelUser.msisdnExists();
            Boolean Geography = ViewChannelUser.geographyExists();
            Boolean externalCode = ViewChannelUser.extCodeExists();
            Boolean CUDomain = ViewChannelUser.CUDomainExists();
            Boolean CUCategory = ViewChannelUser.CUCategoryExists();
            Boolean CUPrntCategory = ViewChannelUser.CUParentCategoryExists();
            if(checkUsrNme && checkMSISDN && Geography && externalCode && CUDomain && CUCategory && CUPrntCategory) {
                Log.info("Personal Details Tab fields and data validated.");
                ViewChannelUser.clickLoginDetailsTab();
                Boolean checkLoginID = ViewChannelUser.CULoginIDExists();
                checkMSISDN = ViewChannelUser.CULoginDetailsMSISDNExists();
                if(checkLoginID && checkMSISDN) {
                    ViewChannelUser.clickRoleDetailsTab();
                    Boolean sysRole = ViewChannelUser.ifSysRoleExists();
                    Boolean grpRole = ViewChannelUser.ifGrpRoleExists();
                    if(sysRole && grpRole) {
                        ViewChannelUser.clickPaymentServiceDetailsTab();
                        Boolean pmtInfo = ViewChannelUser.ifPaymentInformationExists();
                        Boolean spnRgts = ViewChannelUser.ifSuspensionRightsExists();
                        Boolean srvInfo = ViewChannelUser.ifServiceInformationExists();
                        Boolean vchrType = ViewChannelUser.ifVoucherTypeExists();
                        Boolean lwBlncAlrt = ViewChannelUser.ifLowBalanceAlertExists();
                        if(pmtInfo && spnRgts && srvInfo && vchrType && lwBlncAlrt) {
                            ViewChannelUser.clickProfileDetailsTab();
                            Boolean checkCommProf = ViewChannelUser.CUCommissionProfileExists();
                            Boolean checkTrsnfProf = ViewChannelUser.CUTransferProfile();
                            if(checkCommProf && checkTrsnfProf) {
                                ViewChannelUser.clickThresholdDetailsTab();
                                Boolean blncPrfncs = ViewChannelUser.ifBalancePreferencesExists();
                                Boolean tnsfCntoPrfl = ViewChannelUser.ifTransferControlProfileExists();
                                if(blncPrfncs && tnsfCntoPrfl)
                                {
                                    ExtentI.Markup(ExtentColor.GREEN, "View Channel User is successful with all the details in the tabs with Active Users");
                                    ExtentI.attachCatalinaLogsForSuccess();
                                    ExtentI.attachScreenShot();
                                }
                                else{
                                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                    ExtentI.attachCatalinaLogs();
                                    ExtentI.attachScreenShot();
                                }
                            }
                            else{
                                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Profiles Tab");
                                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Profiles Tab");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                        }
                        else
                        {
                            ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Payment & Services Tab");
                            ExtentI.attachCatalinaLogs();
                            ExtentI.attachScreenShot();
                        }
                    }
                    else{
                        ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Role Details Tab");
                        currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Role Details Tab");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
                else{
                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Login Details Tab");
                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Login Details Tab");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Personal Details Tab");
                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Personal Details Details Tab");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
    }


    public void PerformViewChannelUserHideFilters(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodname = "PerformViewChannelUserHideFilters";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID );
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        ViewChannelUser.spinnerWait();
        if(FromCategory.equals(ParentCategory)) {
            ViewChannelUser.clickChannelUserHeading();
            ViewChannelUser.spinnerWait();
            ViewChannelUser.clickHideFilter();
            Boolean checkDomain = ViewChannelUser.checkDomain();
            Boolean checkCategory = ViewChannelUser.checkCategory();
            Boolean checkGeography = ViewChannelUser.checkGeography();
            Boolean checkStatus = ViewChannelUser.checkStatus();
            if (!(checkDomain && checkCategory && checkGeography && checkStatus)) {
                ViewChannelUser.enterSearchField(toMSISDN);
                ViewChannelUser.clickUsernameOfChildUser();
                ViewChannelUser.spinnerWait();
                String fetchedUsrNme = ViewChannelUser.getUserName();
                fetchedUsrNme = fetchedUsrNme.substring(fetchedUsrNme.indexOf(' ') + 1);
                Log.info("Fetched User Name :" + fetchedUsrNme);
                String CUmsisdn = ViewChannelUser.getMSISDN();
                String[] _arr = CUmsisdn.split("\\s");
                CUmsisdn = _arr[0];
                Log.info("Fetched MSISDN :" + CUmsisdn);
                String Geography = ViewChannelUser.getGeography();
                String externalCode = ViewChannelUser.getExternalCode();
                String CUDomain = ViewChannelUser.getCUDomain();
                String CUCategory = ViewChannelUser.getCUCategory();
                String CUPrntCategory = ViewChannelUser.getCUParentCategory();
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
                String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
                Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
                String DPCUmsisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, RowNum);
                Log.info("MSISDN fetched from DataProvider :" + DPCUmsisdn);
                String DPGeography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, RowNum);
                Log.info("Geography fetched from DataProvider :" + DPGeography);
                String DPexternalCode = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, RowNum);
                Log.info("External Code fetched from DataProvider :" + DPexternalCode);
                String DPCUDomain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, RowNum);
                Log.info("Domain fetched from DataProvider :" + DPCUDomain);
                String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
                Log.info("Category fetched from DataProvider :" + DPCUCategory);
                String DPCUPrntCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, RowNum);
                Log.info("Parent Category fetched from DataProvider :" + DPCUPrntCategory);
                if (fetchedUsrNme.equals(DPfetchedUsrNme) && CUmsisdn.equals(DPCUmsisdn) && Geography.equals(DPGeography) && externalCode.equals(DPexternalCode) && CUDomain.equals(DPCUDomain) && CUCategory.equals(DPCUCategory) && CUPrntCategory.equals(DPCUPrntCategory)) {
                    Log.info("Personal Details Tab fields and data validated.");
                    ViewChannelUser.clickLoginDetailsTab();
                    String CULoginID = ViewChannelUser.getCULoginID();
                    CUmsisdn = ViewChannelUser.getLoginDetailsMSISDN();
                    String[] _arr1 = CUmsisdn.split("\\s");
                    CUmsisdn = _arr1[0];
                    Log.info("Fetched MSISDN :" + CUmsisdn);
                    String DPCULoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
                    Log.info("Login ID fetched from Data Provider :" + DPCULoginID);
                    if (CULoginID.equals(DPCULoginID) && CUmsisdn.equals(DPCUmsisdn)) {
                        ViewChannelUser.clickRoleDetailsTab();
                        Boolean sysRole = ViewChannelUser.ifSysRoleExists();
                        Boolean grpRole = ViewChannelUser.ifGrpRoleExists();
                        if (sysRole && grpRole) {
                            ViewChannelUser.clickPaymentServiceDetailsTab();
                            Boolean pmtInfo = ViewChannelUser.ifPaymentInformationExists();
                            Boolean spnRgts = ViewChannelUser.ifSuspensionRightsExists();
                            Boolean srvInfo = ViewChannelUser.ifServiceInformationExists();
                            Boolean vchrType = ViewChannelUser.ifVoucherTypeExists();
                            Boolean lwBlncAlrt = ViewChannelUser.ifLowBalanceAlertExists();
                            if (pmtInfo && spnRgts && srvInfo && vchrType && lwBlncAlrt) {
                                ViewChannelUser.clickProfileDetailsTab();
                                String CUCommProf = ViewChannelUser.getCUCommissionProfile();
                                String CUTrnfProf = ViewChannelUser.getCUTransferProfile();
                                String DPCUCommProf = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, RowNum);
                                Log.info("Fetched Commission Profile from DataProvider : " + DPCUCommProf);
                                String DPCUTrnfProf = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, RowNum);
                                Log.info("Fetched Transfer Profile from DataProvider : " + DPCUTrnfProf);
                                if (CUCommProf.equals(DPCUCommProf) && CUTrnfProf.equals(DPCUTrnfProf)) {
                                    ViewChannelUser.clickThresholdDetailsTab();
                                    Boolean blncPrfncs = ViewChannelUser.ifBalancePreferencesExists();
                                    Boolean tnsfCntoPrfl = ViewChannelUser.ifTransferControlProfileExists();
                                    if (blncPrfncs && tnsfCntoPrfl) {
                                        ExtentI.Markup(ExtentColor.GREEN, "View Channel User is successful with all the details in the tabs with Hiding filters");
                                        ExtentI.attachCatalinaLogsForSuccess();
                                        ExtentI.attachScreenShot();
                                    } else {
                                        ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                        currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                        ExtentI.attachCatalinaLogs();
                                        ExtentI.attachScreenShot();
                                    }
                                } else {
                                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Profiles Tab");
                                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Profiles Tab");
                                    ExtentI.attachCatalinaLogs();
                                    ExtentI.attachScreenShot();
                                }
                            } else {
                                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Payment & Services Tab");
                                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Payment & Services Tab");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                        } else {
                            ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Role Details Tab");
                            currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Role Details Tab");
                            ExtentI.attachCatalinaLogs();
                            ExtentI.attachScreenShot();
                        }
                    } else {
                        ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Login Details Tab");
                        currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Login Details Tab");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                } else {
                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Personal Details Tab");
                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Personal Details Details Tab");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }else{
            ExtentI.Markup(ExtentColor.RED, "Filters are not hidden after clicking on Hide Filter");
            currentNode.log(Status.FAIL, "Filters are not hidden after clicking on Hide Filter");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            }
        } else {
            Assertion.assertSkip("View Channel User cannot be performed by " + FromCategory + " for Agent [ For Parent " + ParentCategory + " ]");
        }
    }


    public void PerformViewChannelUserSuspendedUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum) {
        final String methodname = "PerformViewChannelUserSuspendedUser";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID );
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        ViewChannelUser.clickChannelUserHeading();
        String CUStatus = "Suspended";
        ViewChannelUser.selectCUStatus(CUStatus);
        ViewChannelUser.enterSearchField(toMSISDN);
        ViewChannelUser.clickUsernameOfChildUser();
        String fetchedUsrNme = ViewChannelUser.getUserName();
        fetchedUsrNme = fetchedUsrNme.substring(fetchedUsrNme.indexOf(' ') + 1);
        Log.info("Fetched User Name :" +fetchedUsrNme);
        String CUmsisdn = ViewChannelUser.getMSISDN();
        String[] _arr = CUmsisdn.split("\\s");
        CUmsisdn = _arr[0];
        Log.info("Fetched MSISDN :" +CUmsisdn);
        String Geography = ViewChannelUser.getGeography();
        String externalCode = ViewChannelUser.getExternalCode();
        String CUDomain = ViewChannelUser.getCUDomain();
        String CUCategory = ViewChannelUser.getCUCategory();
        String CUPrntCategory = ViewChannelUser.getCUParentCategory();
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
        Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
        String DPCUmsisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, RowNum);
        Log.info("MSISDN fetched from DataProvider :" + DPCUmsisdn);
        String DPGeography = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, RowNum);
        Log.info("Geography fetched from DataProvider :" + DPGeography);
        String DPexternalCode = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, RowNum);
        Log.info("External Code fetched from DataProvider :" + DPexternalCode);
        String DPCUDomain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, RowNum);
        Log.info("Domain fetched from DataProvider :" + DPCUDomain);
        String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
        Log.info("Category fetched from DataProvider :" + DPCUCategory);
        String DPCUPrntCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, RowNum);
        Log.info("Parent Category fetched from DataProvider :" + DPCUPrntCategory);
        if(fetchedUsrNme.equals(DPfetchedUsrNme) && CUmsisdn.equals(DPCUmsisdn) && Geography.equals(DPGeography) && externalCode.equals(DPexternalCode) && CUDomain.equals(DPCUDomain) && CUCategory.equals(DPCUCategory) && CUPrntCategory.equals(DPCUPrntCategory)) {
            Log.info("Personal Details Tab fields and data validated.");
            ViewChannelUser.clickLoginDetailsTab();
            String CULoginID = ViewChannelUser.getCULoginID();
            CUmsisdn = ViewChannelUser.getLoginDetailsMSISDN();
            String[] _arr1 = CUmsisdn.split("\\s");
            CUmsisdn = _arr1[0];
            Log.info("Fetched MSISDN :" +CUmsisdn);
            String DPCULoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login ID fetched from Data Provider :" +DPCULoginID);
            if(CULoginID.equals(DPCULoginID) && CUmsisdn.equals(DPCUmsisdn)) {
                ViewChannelUser.clickRoleDetailsTab();
                Boolean sysRole = ViewChannelUser.ifSysRoleExists();
                Boolean grpRole = ViewChannelUser.ifGrpRoleExists();
                if(sysRole && grpRole) {
                    ViewChannelUser.clickPaymentServiceDetailsTab();
                    Boolean pmtInfo = ViewChannelUser.ifPaymentInformationExists();
                    Boolean spnRgts = ViewChannelUser.ifSuspensionRightsExists();
                    Boolean srvInfo = ViewChannelUser.ifServiceInformationExists();
                    Boolean vchrType = ViewChannelUser.ifVoucherTypeExists();
                    Boolean lwBlncAlrt = ViewChannelUser.ifLowBalanceAlertExists();
                    if(pmtInfo && spnRgts && srvInfo && vchrType && lwBlncAlrt) {
                        ViewChannelUser.clickProfileDetailsTab();
                        String CUCommProf = ViewChannelUser.getCUCommissionProfile();
                        String CUTrnfProf = ViewChannelUser.getCUTransferProfile();
                        String DPCUCommProf = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, RowNum);
                        Log.info("Fetched Commission Profile from DataProvider : "+DPCUCommProf);
                        String DPCUTrnfProf = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, RowNum);
                        Log.info("Fetched Transfer Profile from DataProvider : "+DPCUTrnfProf);
                        if(CUCommProf.equals(DPCUCommProf) && CUTrnfProf.equals(DPCUTrnfProf)) {
                            ViewChannelUser.clickThresholdDetailsTab();
                            Boolean blncPrfncs = ViewChannelUser.ifBalancePreferencesExists();
                            Boolean tnsfCntoPrfl = ViewChannelUser.ifTransferControlProfileExists();
                            if(blncPrfncs && tnsfCntoPrfl)
                            {
                                ExtentI.Markup(ExtentColor.GREEN, "View Channel User is successful with all the details in the tabs");
                                ExtentI.attachCatalinaLogsForSuccess();
                                ExtentI.attachScreenShot();
                            }
                            else{
                                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Threshold & Usage Tab");
                                ExtentI.attachCatalinaLogs();
                                ExtentI.attachScreenShot();
                            }
                        }
                        else{
                            ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Profiles Tab");
                            currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Profiles Tab");
                            ExtentI.attachCatalinaLogs();
                            ExtentI.attachScreenShot();
                        }
                    }
                    else
                    {
                        ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Payment & Services Tab");
                        currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Payment & Services Tab");
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                }
                else{
                    ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Role Details Tab");
                    currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Role Details Tab");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else{
                ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Login Details Tab");
                currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Login Details Tab");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else
        {
            ExtentI.Markup(ExtentColor.RED, "View Channel User is not successful with expected details in the Personal Details Tab");
            currentNode.log(Status.FAIL, "View Channel User is not successful with expected details in the Personal Details Details Tab");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
    }

}
