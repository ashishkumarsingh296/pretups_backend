package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.LowThresholdTransactionReport.LowThresholdTransactionReportPage;

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

public class LowThresholdTransactionReportPageRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    LowThresholdTransactionReportPage lowThresholdTransactionReportPage;

    public LowThresholdTransactionReportPageRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        lowThresholdTransactionReportPage = new LowThresholdTransactionReportPage(driver);
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
        
        lowThresholdTransactionReportPage.clickLowThresholdTransactionReportlink();
        lowThresholdTransactionReportPage.isLowThresholdTransactionReportTextVisible();
        lowThresholdTransactionReportPage.clickProceedButton();
        lowThresholdTransactionReportPage.clickUserProfile();
        lowThresholdTransactionReportPage.clickOnLogout();

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
        
        lowThresholdTransactionReportPage.clickLowThresholdTransactionReportlink();
        lowThresholdTransactionReportPage.clickProceedButton();
        lowThresholdTransactionReportPage.clickHidelink();
        lowThresholdTransactionReportPage.clickShowlink();
        lowThresholdTransactionReportPage.isLowThresholdTransactionReportTextVisible();        
        lowThresholdTransactionReportPage.clickUserProfile();
        lowThresholdTransactionReportPage.clickOnLogout();

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
        
        lowThresholdTransactionReportPage.clickLowThresholdTransactionReportlink();
        lowThresholdTransactionReportPage.clickProceedButton();
        lowThresholdTransactionReportPage.isdownloadButtonVisible();
        lowThresholdTransactionReportPage.isLowThresholdTransactionReportTextVisible();        
        lowThresholdTransactionReportPage.clickUserProfile();
        lowThresholdTransactionReportPage.clickOnLogout();

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
        
        lowThresholdTransactionReportPage.clickLowThresholdTransactionReportlink();
        lowThresholdTransactionReportPage.isLowThresholdTransactionReportTextVisible();        

// need to write a code for selecting from date and to date values
        lowThresholdTransactionReportPage.clickDateRangeField();
        lowThresholdTransactionReportPage.selectYear("2021");
        lowThresholdTransactionReportPage.selectMonth("5");
        
        lowThresholdTransactionReportPage.selectFromDate();
        lowThresholdTransactionReportPage.selectToDate();
        
        lowThresholdTransactionReportPage.clickProceedButton();
//      lowThresholdTransactionReportPage.isdownloadButtonVisible();
//      lowThresholdTransactionReportPage.isEditColumnButtonVisible();
        lowThresholdTransactionReportPage.clickUserProfile();
        lowThresholdTransactionReportPage.clickOnLogout();

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
        
        lowThresholdTransactionReportPage.clickLowThresholdTransactionReportlink();
        lowThresholdTransactionReportPage.isLowThresholdTransactionReportTextVisible();        
        lowThresholdTransactionReportPage.clickProceedButton();
        lowThresholdTransactionReportPage.clickUserProfile();
        lowThresholdTransactionReportPage.clickOnLogout();

        Log.methodExit(methodname);
    }
    
}
