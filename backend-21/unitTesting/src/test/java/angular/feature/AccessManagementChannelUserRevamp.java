package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.AccessManagementChannelUser.AccessManagementChannelUser;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.sshmanager.SSHService;
import com.utils.*;
import org.openqa.selenium.WebDriver;
import java.util.HashMap;
import java.util.Map;

public class AccessManagementChannelUserRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    AccessManagementChannelUser AccessManagementChannelUser;
    Map<String, String> ResultMap;

    public AccessManagementChannelUserRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        AccessManagementChannelUser = new AccessManagementChannelUser(driver);
        ResultMap = new HashMap();
    }

    RandomGeneration RandomGeneration = new RandomGeneration();


    public void PerformSendPasswordChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformSendPasswordChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : " + loginID);
        String remarks = "Automation Remarks";
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        AccessManagementChannelUser.spinnerWait();
        if(FromCategory.equals(ParentCategory)) {
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            /*String Name = AccessManagementChannelUser.getName();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPFetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String Category = AccessManagementChannelUser.getCategory();
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);*/
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkfields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            /*String userGrade = AccessManagementChannelUser.getUserGrade();
            String fetchedUserGrade = ExcelUtility.getCellData(0, ExcelI.GRADE, RowNum);
            Log.info("User Grade fetched from DataProvider :" + fetchedUserGrade); */
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkfields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(/*Category.equals(DPCUCategory) && */CUloginID.equals(fetchedLoginID) /*&& userGrade.equals(fetchedUserGrade)*/) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickSendPassword();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                Boolean chnlReqDailyLog  = SSHService.startMessageSentLogMonitor();
                Log.info("Logs: " +chnlReqDailyLog);
                String msgSentLogs = SSHService.stopMessageSentLogMonitor();
                Log.info("Logs: " +msgSentLogs);
                String actualSuccessMsg = AccessManagementChannelUser.getSuccessMessage();
                String expectedMessage = "Send Password Successful";
                if (expectedMessage.contains(actualSuccessMsg)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Send Password Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Send Password is not Successful for the Child Channel Use");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Access Management of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Access Management of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


    public void PerformUnBlockPasswordChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformSendPasswordChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String errorMessage = null;
        int count= DBHandler.AccessHandler.maxPasswordBlockCount(ToCategory);
        if(FromCategory.equals(ParentCategory)) {
            for(int i=0; i<count; i++) {
            Log.info("Invalid attempt: "+ (i+1));
            errorMessage = login.LoginAsUser(driver,toLoginID, "*invalidpwd*");
        }
        Log.info("Error message for Login with Child User: " +errorMessage);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID );
        String remarks = "Automation Remarks";
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkfields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkfields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(CUloginID.equals(fetchedLoginID)) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickUnblockPassword();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                String actualSuccessMsg = AccessManagementChannelUser.getSuccessMessage();
                String expectedMessage = "Unblock Password Successful";
                if (expectedMessage.contains(actualSuccessMsg)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Unblock Password Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Unblock Password is not Successful for the Child Channel Use");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Unblock Password of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Unblock Password of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }



    public void PerformUnBlockSendPasswordChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformUnBlockSendPasswordChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String errorMessage = null;
        int count= DBHandler.AccessHandler.maxPasswordBlockCount(ToCategory);
        if(FromCategory.equals(ParentCategory)) {
            for(int i=0; i<count; i++) {
                Log.info("Invalid attempt: "+ (i+1));
                errorMessage = login.LoginAsUser(driver,toLoginID, "*invalidpwd*");
            }
            Log.info("Error message for Login with Child User: " +errorMessage);
            String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
            Log.info("LOGINID : "+loginID );
            String remarks = "Automation Remarks";
            login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            /*String Name = AccessManagementChannelUser.getName();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String Category = AccessManagementChannelUser.getCategory();
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);*/
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkfields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            /*String userGrade = AccessManagementChannelUser.getUserGrade();
            String fetchedUserGrade = ExcelUtility.getCellData(0, ExcelI.GRADE, RowNum);
            Log.info("User Grade fetched from DataProvider :" + fetchedUserGrade); */
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkfields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(/*Category.equals(DPCUCategory) && */CUloginID.equals(fetchedLoginID) /*&& userGrade.equals(fetchedUserGrade)*/) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickUnblockSendPassword();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                /*Boolean chnlReqDailyLog  = SSHService.startMessageSentLogMonitor();
                Log.info("Logs: " +chnlReqDailyLog);
                String msgSentLogs = SSHService.stopMessageSentLogMonitor();
                Log.info("Logs: " +msgSentLogs);*/
                String actualSuccessMsg = AccessManagementChannelUser.getSuccessMessage();
                String expectedMessage = "Unblock & Send Password Successful";
                if (expectedMessage.contains(actualSuccessMsg)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Unblock & Send Password Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Unblock & Send Password is not Successful for the Child Channel Use");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Unblock & Send Password of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Unblock & Send Password of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


    public void PerformSendPasswordWithoutRemarksChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformSendPasswordWithoutRemarksChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        if(FromCategory.equals(ParentCategory)) {
            String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
            Log.info("LOGINID : "+loginID );
            String remarks = "";
            login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            /*String Name = AccessManagementChannelUser.getName();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String Category = AccessManagementChannelUser.getCategory();
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);*/
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkfields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            /*String userGrade = AccessManagementChannelUser.getUserGrade();
            String fetchedUserGrade = ExcelUtility.getCellData(0, ExcelI.GRADE, RowNum);
            Log.info("User Grade fetched from DataProvider :" + fetchedUserGrade); */
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkfields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(/*Category.equals(DPCUCategory) && */CUloginID.equals(fetchedLoginID) /*&& userGrade.equals(fetchedUserGrade)*/) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickSendPassword();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                /*Boolean chnlReqDailyLog  = SSHService.startMessageSentLogMonitor();
                Log.info("Logs: " +chnlReqDailyLog);
                String msgSentLogs = SSHService.stopMessageSentLogMonitor();
                Log.info("Logs: " +msgSentLogs);*/
                String actualFailedMsg = AccessManagementChannelUser.getFailedMessage();
                String actualSubFailedMsg = AccessManagementChannelUser.getSubFailedMessage();
                String expectedMessage = "Send Password Failed";
                String expectedSubMessage = "Remarks Required";
                if (expectedMessage.contains(actualFailedMsg) && actualSubFailedMsg.contains(expectedSubMessage)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Send Password without remarks is not Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Send Password without remarks is Successful for the Child Channel Use");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Send Password without remarks of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Send Password without remarks of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }



    public void PerformUnBlockPasswordWithoutRemarksChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformUnBlockPasswordWithoutRemarksChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        if(FromCategory.equals(ParentCategory)) {
            String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
            Log.info("LOGINID : "+loginID );
            String remarks = "";
            login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            /*String Name = AccessManagementChannelUser.getName();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String Category = AccessManagementChannelUser.getCategory();
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);*/
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkFields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkFields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            /*String userGrade = AccessManagementChannelUser.getUserGrade();
            String fetchedUserGrade = ExcelUtility.getCellData(0, ExcelI.GRADE, RowNum);
            Log.info("User Grade fetched from DataProvider :" + fetchedUserGrade); */
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkFields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkFields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(/*Category.equals(DPCUCategory) && */CUloginID.equals(fetchedLoginID) /*&& userGrade.equals(fetchedUserGrade)*/) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickUnblockPassword();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                /*Boolean chnlReqDailyLog  = SSHService.startMessageSentLogMonitor();
                Log.info("Logs: " +chnlReqDailyLog);
                String msgSentLogs = SSHService.stopMessageSentLogMonitor();
                Log.info("Logs: " +msgSentLogs);*/
                String actualFailedMsg = AccessManagementChannelUser.getFailedMessage();
                String actualSubFailedMsg = AccessManagementChannelUser.getSubFailedMessage();
                String expectedMessage = "Unblock Password Failed";
                String expectedSubMessage = "Remarks Required";
                if (expectedMessage.contains(actualFailedMsg) && actualSubFailedMsg.contains(expectedSubMessage)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Unblock Password without remarks is not Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Unblock Password without remarks is Successful for the Child Channel Use");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Unblock Password without remarks of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Unblock Password without remarks of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


    public void PerformUnblockSendPasswordWithoutRemarksChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformUnblockSendPasswordWithoutRemarksChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        if(FromCategory.equals(ParentCategory)) {
            String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
            Log.info("LOGINID : "+loginID );
            String remarks = "";
            login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            /*String Name = AccessManagementChannelUser.getName();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String Category = AccessManagementChannelUser.getCategory();
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);*/
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkfields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            /*String userGrade = AccessManagementChannelUser.getUserGrade();
            String fetchedUserGrade = ExcelUtility.getCellData(0, ExcelI.GRADE, RowNum);
            Log.info("User Grade fetched from DataProvider :" + fetchedUserGrade); */
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkfields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(/*Category.equals(DPCUCategory) && */CUloginID.equals(fetchedLoginID) /*&& userGrade.equals(fetchedUserGrade)*/) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickUnblockSendPassword();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                /*Boolean chnlReqDailyLog  = SSHService.startMessageSentLogMonitor();
                Log.info("Logs: " +chnlReqDailyLog);
                String msgSentLogs = SSHService.stopMessageSentLogMonitor();
                Log.info("Logs: " +msgSentLogs);*/
                String actualFailedMsg = AccessManagementChannelUser.getFailedMessage();
                String actualSubFailedMsg = AccessManagementChannelUser.getSubFailedMessage();
                String expectedMessage = "Unblock & Send Password Failed";
                String expectedSubMessage = "Remarks Required";
                if (expectedMessage.contains(actualFailedMsg) && actualSubFailedMsg.contains(expectedSubMessage)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Unblock & Send Password of Channel User without remarks is not Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Unblock & Send Password of Channel User without remarks is Successful for the Child Channel Use");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Unblock & Send Password of Channel User without remarks of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Unblock & Send Password of Channel User without remarks of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


    public void PerformResetPasswordWithoutRemarksChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformResetPasswordWithoutRemarksChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        if(FromCategory.equals(ParentCategory)) {
            String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
            Log.info("LOGINID : "+loginID );
            String remarks = "";
            login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            /*String Name = AccessManagementChannelUser.getName();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPfetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPfetchedUsrNme);
            String Category = AccessManagementChannelUser.getCategory();
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);*/
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkfields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            /*String userGrade = AccessManagementChannelUser.getUserGrade();
            String fetchedUserGrade = ExcelUtility.getCellData(0, ExcelI.GRADE, RowNum);
            Log.info("User Grade fetched from DataProvider :" + fetchedUserGrade); */
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkfields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(/*Category.equals(DPCUCategory) && */CUloginID.equals(fetchedLoginID) /*&& userGrade.equals(fetchedUserGrade)*/) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickResetPassword();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                /*Boolean chnlReqDailyLog  = SSHService.startMessageSentLogMonitor();
                Log.info("Logs: " +chnlReqDailyLog);
                String msgSentLogs = SSHService.stopMessageSentLogMonitor();
                Log.info("Logs: " +msgSentLogs);*/
                String actualFailedMsg = AccessManagementChannelUser.getFailedMessage();
                String actualSubFailedMsg = AccessManagementChannelUser.getSubFailedMessage();
                String expectedMessage = "Reset Password Failed";
                String expectedSubMessage = "Remarks Required";
                if (expectedMessage.contains(actualFailedMsg) && actualSubFailedMsg.contains(expectedSubMessage)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Reset Password of Channel User without remarks is not Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Reset Password of Channel User without remarks is Successful for the Child Channel User");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Reset Password of Channel User without remarks of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Reset Password of Channel User without remarks of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }

    public void PerformViewMoreAccessManagement(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformViewMoreAccessManagement";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : " + loginID);
        String remarks = "Automation Remarks";
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        AccessManagementChannelUser.spinnerWait();
        if(FromCategory.equals(ParentCategory)) {
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            String Name = AccessManagementChannelUser.getName();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String DPFetchedUsrNme = ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum);
            Log.info("User Name fetched from DataProvider :" + DPFetchedUsrNme);
            String Category = AccessManagementChannelUser.getCategory();
            String DPCUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, RowNum);
            Log.info("Category fetched from DataProvider :" + DPCUCategory);
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = networkName.isEmpty();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty() && checkfields;
            if(Category.equals(DPCUCategory) && CUloginID.equals(fetchedLoginID) && checkfields1) {
                    ExtentI.Markup(ExtentColor.GREEN, "Access Management of Channel User is successful as Fields are validated and correct");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    ExtentI.Markup(ExtentColor.RED, "Access Management of Channel User is not successful as Fields are not validated and correct");
                    currentNode.log(Status.FAIL, "Access Management of Channel User is not successful as Fields are not validated and correct");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


    public void PerformResetPasswordChannelUser(String FromCategory, String ToCategory, String toMSISDN, String PIN, String fromParent, Integer RowNum, String ParentCategory, String toLoginID) {
        final String methodName = "PerformResetPasswordChannelUser";
        Log.methodEntry(methodName, FromCategory, ToCategory, toMSISDN, PIN, fromParent, RowNum, ParentCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : " + loginID);
        String remarks = "Automation Remarks";
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);
        AccessManagementChannelUser.spinnerWait();
        if(FromCategory.equals(ParentCategory)) {
            AccessManagementChannelUser.clickChannelUserHeading();
            AccessManagementChannelUser.spinnerWait();
            AccessManagementChannelUser.enterSearchField(toMSISDN);
            AccessManagementChannelUser.clickAccessManagement();
            AccessManagementChannelUser.clickViewMore();
            String contactPerson = AccessManagementChannelUser.getContactPerson();
            String ssn = AccessManagementChannelUser.getSSN();
            String networkName = AccessManagementChannelUser.getNetworkName();
            Boolean checkfields = contactPerson.isEmpty() && ssn.isEmpty() && networkName.isEmpty();
            if(!checkfields)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String CUloginID = AccessManagementChannelUser.getLoginID();
            String fetchedLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, RowNum);
            Log.info("Login fetched from DataProvider :" + fetchedLoginID);
            String address = AccessManagementChannelUser.getAddress();
            String city = AccessManagementChannelUser.getCity();
            String state = AccessManagementChannelUser.getState();
            String country = AccessManagementChannelUser.getCountry();
            Boolean checkfields1 = address.isEmpty() && city.isEmpty() && state.isEmpty() && country.isEmpty();
            if(!checkfields1)
            {
                Log.info("Fields are not empty");
            }
            else{
                Log.info("Fields are empty");
            }
            if(CUloginID.equals(fetchedLoginID)) {
                Log.info("Fields are validated and are correct");
                AccessManagementChannelUser.clickResetButton();
                AccessManagementChannelUser.enterRemarks(remarks);
                AccessManagementChannelUser.clickYesButton();
                Boolean chnlReqDailyLog  = SSHService.startMessageSentLogMonitor();
                Log.info("Logs: " +chnlReqDailyLog);
                String msgSentLogs = SSHService.stopMessageSentLogMonitor();
                Log.info("Logs: " +msgSentLogs);
                String actualSuccessMsg = AccessManagementChannelUser.getSuccessMessage();
                String expectedMessage = "Send Password Successful";
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

                if (expectedMessage.contains(actualSuccessMsg)) {
                    ExtentI.Markup(ExtentColor.GREEN, "Reset Password Successful for the Child Channel User");
                    ExtentI.insertValueInDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, RowNum, "0000");
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                } else {
                    currentNode.log(Status.FAIL, "Reset Password is not Successful for the Child Channel Use");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            }
            else
            {
                ExtentI.Markup(ExtentColor.RED, "Access Management of Channel User is not successful as Fields are not validated and correct");
                currentNode.log(Status.FAIL, "Access Management of Channel User is not successful as Fields are not validated and correct");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else{
            Assertion.assertSkip("Access Management of Channel User cannot be performed by " +FromCategory+ " for Agent [ For Parent "+ParentCategory +" ]");
        }
    }


}
