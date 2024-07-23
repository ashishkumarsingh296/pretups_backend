package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.Passbook.PassbookPage;

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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PassbookPageRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    PassbookPage passbookPage;

    public PassbookPageRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        passbookPage = new PassbookPage(driver);
    }

    RandomGeneration RandomGeneration = new RandomGeneration();
    
    public void performLoginLogout(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
        
    	final String methodname = "performLoginLogout";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",ParentCategory, FromCategory);
        
        passbookPage.clickPassbooklinkOnWidget();
        passbookPage.ispassbookTextVisible();
        passbookPage.clickProceedButton();
        passbookPage.clickUserProfile();
        passbookPage.clickOnLogout();

        Log.methodExit(methodname);
    }
    
    public void performHidelink(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
        
    	final String methodname = "performHidelink";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",ParentCategory, FromCategory);
        
        passbookPage.clickPassbooklinkOnWidget();
        passbookPage.clickProceedButton();
        passbookPage.clickHidelink();
        passbookPage.clickShowlink();
        passbookPage.ispassbookTextVisible();
        passbookPage.clickUserProfile();
        passbookPage.clickOnLogout();

        Log.methodExit(methodname);
    }
    
    public void performProceedButton(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
        
    	final String methodname = "performProceedButton";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",ParentCategory, FromCategory);
        
        passbookPage.clickPassbooklinkOnWidget();
        passbookPage.clickProceedButton();
        passbookPage.isdownloadButtonVisible();
        passbookPage.ispassbookTextVisible();
        passbookPage.clickUserProfile();
        passbookPage.clickOnLogout();

        Log.methodExit(methodname);
    }
    
    public void performSelectFromdateTodate(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
        
    	final String methodname = "performSelectFromdateTodate";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",ParentCategory, FromCategory);
        
        passbookPage.clickPassbooklinkOnWidget();
        passbookPage.ispassbookTextVisible();

// need to write a code for selecting from date and to date values
        passbookPage.clickDateRangeField();
        passbookPage.selectYear("2021");
        passbookPage.selectMonth("5");
        
        passbookPage.selectFromDate();
        passbookPage.selectToDate();
        
        passbookPage.clickProceedButton();
//      passbookPage.isdownloadButtonVisible();
//      passbookPage.isEditColumnButtonVisible();
        passbookPage.clickUserProfile();
        passbookPage.clickOnLogout();

        Log.methodExit(methodname);
    }
    
    public void performVerifyProductcode(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
        
    	final String methodname = "performVerifyProductcode";
        Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",ParentCategory, FromCategory);
        
        passbookPage.clickPassbooklinkOnWidget();
        passbookPage.ispassbookTextVisible();
        passbookPage.productCodeDropdown();
        passbookPage.productCodeeTopUP();
    //    passbookPage.productCodePosteTopUP();
    //    passbookPage.productCodeAll();
        passbookPage.clickProceedButton();
        passbookPage.clickUserProfile();
        passbookPage.clickOnLogout();

        Log.methodExit(methodname);
    }
}





















//passbookPage.clickDateRangeField();
//passbookPage.clickFromDateValue();
//passbookPage.clickToDateValue();
//passbookPage.clickProceedButton();
//passbookPage.isdownloadButtonVisible();
//passbookPage.isEditColumnButtonVisible();
//passbookPage.clickHidelink();
//passbookPage.clickShowlink();
//passbookPage.ispassbookTextVisible();
//passbookPage.cick_userprofile();
//passbookPage.clickLogout_btn();