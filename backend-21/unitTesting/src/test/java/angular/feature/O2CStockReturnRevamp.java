package angular.feature;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

import angular.classes.LoginRevamp;
import angular.pageobjects.O2CReturn.O2CStockReturn;

public class O2CStockReturnRevamp extends BaseTest{
	
	public WebDriver driver;
    LoginRevamp login;
    O2CStockReturn o2CStockReturn;
    Map<String, String> ResultMap;

    public O2CStockReturnRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        o2CStockReturn = new O2CStockReturn(driver);
        ResultMap = new HashMap<String, String>();
    }
    
    public void performO2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
            o2CStockReturn.clickReturnHeading();
            o2CStockReturn.enterAmount(productName, "simple");
            o2CStockReturn.enterRemark();
            o2CStockReturn.clickReturnButtton();
            o2CStockReturn.enterPin(chPin, "next");
            boolean res = o2CStockReturn.isSuccess();
            
            if(!res) {
            	ExtentI.Markup(ExtentColor.RED, "O2c return failed");
            	ExtentI.attachScreenShot();
            	ExtentI.attachCatalinaLogs();
            }
            else	{
            	ExtentI.Markup(ExtentColor.GREEN, "O2c return is successfully done");
            	ExtentI.attachCatalinaLogsForSuccess();
            }

            Log.methodExit(methodname);
        
    }
	
    public void performO2CStockReturnWithoutAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
            o2CStockReturn.clickReturnHeading();
            o2CStockReturn.enterRemark();
            o2CStockReturn.clickReturnButtton();
            String text = o2CStockReturn.isEmptyAmountValidated();
            if(text.equals("Amount is required.")) {
            	ExtentI.Markup(ExtentColor.GREEN, "O2c return displayed this when amount was empty: " + text);
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "O2c return displayed this when reamark feild was empty: " + text);
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);
    }
    
    public void performO2CStockReturnWithoutRemark(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
            o2CStockReturn.clickReturnHeading();
            o2CStockReturn.enterAmount(productName, "simple");
            o2CStockReturn.clickReturnButtton();
            String text = o2CStockReturn.isEmptyRemarkValidated();
            if(text.equals("Remarks Required.")) {
            	
            	ExtentI.Markup(ExtentColor.GREEN, "O2c return displayed this when reamark feild was empty: " + text);
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "O2c return displayed this when reamark feild was empty: " + text);
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);
          
    }
    
    public void performO2CStockReturnWithWrongPin(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
        	o2CStockReturn.clickReturnHeading();
        	o2CStockReturn.enterAmount(productName, "simple");
        	o2CStockReturn.enterRemark();
        	o2CStockReturn.clickReturnButtton();
        	
        	RandomGeneration randomGeneration = new RandomGeneration();
        	int pinLen = opPin.length();
        	
        	o2CStockReturn.enterPin(randomGeneration.randomNumeric(pinLen), "next");
        	String text = o2CStockReturn.isUnSuccessful();
            if(text.equals("The PIN you have entered is incorrect.")) {
            	ExtentI.Markup(ExtentColor.GREEN, "O2c return displayed this when wrong pin was entered: " + text);
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "O2c return displayed this when wrong pin was entered: " + text);
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);
    }
    
    public void performO2CStockReturnLargeAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
        	o2CStockReturn.clickReturnHeading();
        	o2CStockReturn.enterAmount(productName,"largeAmount");
        	o2CStockReturn.enterRemark();
        	o2CStockReturn.clickReturnButtton();
        	o2CStockReturn.enterPin(chPin, "next");
        	String text = o2CStockReturn.isUnSuccessful();
            if(!text.equals(" Your request cannot be processed at this time, please try again later. ")) {
            	ExtentI.Markup(ExtentColor.GREEN, "O2c return displayed this when amount larger then available balance was entered: " + text);
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "O2c return displayed this when amount larger then available balance was entered: " + text);
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);     
    }
    
    public void performO2CStockReturnWithAlphaNumericAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
        	o2CStockReturn.clickReturnHeading();
        	o2CStockReturn.enterAmount(productName,"alphaNumeric");
        	String text = o2CStockReturn.isEmptyAmountValidated();
        	
            if(text.equals("Invalid Amount.")) {
            	ExtentI.Markup(ExtentColor.GREEN, "O2c return displayed this when amount was filled by alphaNumeric value: " + text);
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "O2c return displayed this when amount was filled by alphaNumeric value: " + text);
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);     
    }
    
    public void performO2CStockReturnWithZeroAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
        	o2CStockReturn.clickReturnHeading();
        	o2CStockReturn.enterAmount(productName,"zero");
        	o2CStockReturn.enterRemark();
            o2CStockReturn.clickReturnButtton();
            String text = o2CStockReturn.isUnSuccessful();
            
            if(text.equals("Commission slab is not defined for product and requested quantity.")) {
            	ExtentI.Markup(ExtentColor.GREEN, "O2c return displayed this when amount was filled by zero: " + text);
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "O2c return displayed this when amount was filled by zero: " + text);
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);     
    }
    
    public void checkResetButton(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
        	o2CStockReturn.clickO2CHeading();
        	o2CStockReturn.clickReturnHeading();
        	o2CStockReturn.enterAmount(productName,"simple");
        	o2CStockReturn.enterRemark();
            o2CStockReturn.clickResetButtton();
           
            Log.info("checking if feilds are empty");
            boolean flag = o2CStockReturn.checkIfFeildsEmpty(productName);
            
            if(flag) {
            	ExtentI.Markup(ExtentColor.GREEN, "All feilds are empty after clicking on reset button");
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "All feilds are not empty after clicking on reset button");
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);     
    }
    
    public void checkPinReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin) {
    	final String methodname = "performO2CStockReturn";
   	 	Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn);
   	 	
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", chCategoryName);
        String loginID = login.getUserLoginID(driver, "ChannelUser", chCategoryName);
        Log.info("LOGINID : "+loginID ) ;
        
	        o2CStockReturn.clickO2CHeading();
	        o2CStockReturn.clickReturnHeading();
	        o2CStockReturn.enterAmount(productName, "simple");
	        o2CStockReturn.enterRemark();
	        o2CStockReturn.clickReturnButtton();
	        o2CStockReturn.enterPin(chPin, "close");
	        
	        Log.info("checking if feilds are empty");
	        boolean flag = o2CStockReturn.checkIfPinEmpty();
           
            
            if(flag) {
            	ExtentI.Markup(ExtentColor.GREEN, "Pin is successfully reset after closing popup");
            	ExtentI.attachCatalinaLogsForSuccess();
            }
            else {
            	ExtentI.Markup(ExtentColor.RED, "Pin didn't reset successfully after closing popup");
            	ExtentI.attachCatalinaLogs();
            	ExtentI.attachScreenShot();
            }
            Log.methodExit(methodname);     
    }
    
}
