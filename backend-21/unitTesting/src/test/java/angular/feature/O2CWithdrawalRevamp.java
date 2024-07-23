package angular.feature;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

import angular.classes.LoginRevamp;
import angular.pageobjects.O2CWithdrawal.O2CWithdrawal;

public class O2CWithdrawalRevamp extends BaseTest {

	public WebDriver driver;
	LoginRevamp login;
	
	O2CWithdrawal o2CWithdrawal;
	
	public O2CWithdrawalRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		o2CWithdrawal = new O2CWithdrawal(driver); 
	}
	
    RandomGeneration randomGeneration = new RandomGeneration();
	
	public void performO2CWithdrawalByMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
   	 final String methodname = "performO2CWithdrawalByMsisdn";
	 Log.methodEntry(methodname, opCategoryName, chMsisdn);
     String MasterSheetPath = _masterVO.getProperty("DataProvider");
     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
     login.UserLogin(driver, "Operator", opCategoryName);
          
     if(!o2CWithdrawal.isO2CVisible()) {
    	 o2CWithdrawal.clickO2CTransactionHeading();
			Log.info("O2C Transfer heading is clicked");
		}
		else {
			o2CWithdrawal.clickO2CHeading();
			o2CWithdrawal.clickO2CTransactionHeading();
			Log.info("O2C Heading and Transaction Heading is clicked");
		}
     	
     o2CWithdrawal.clickWithdrawHeading();
     o2CWithdrawal.selectOperatorWallet("Sale");
     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
     o2CWithdrawal.enterMsisdn(chMsisdn);
     o2CWithdrawal.clickProceedButton();
     o2CWithdrawal.enterAmount(productName, "simple");
     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
     o2CWithdrawal.clickWithdrawButton();
     o2CWithdrawal.enterPIN(opPin);
     o2CWithdrawal.clickWithdrawButtonPopup();
     boolean res = o2CWithdrawal.O2CWithdrawSuccessVisibility();
     
     if(!res) {
     	ExtentI.Markup(ExtentColor.RED, "O2C withdraw failed");
     	ExtentI.attachScreenShot();
     	ExtentI.attachCatalinaLogs();
     }
     else	{
    	String transactionId =  o2CWithdrawal.getTransactionId();
     	ExtentI.Markup(ExtentColor.GREEN, "O2C withdraw is successfully done with transaction Id: " + transactionId);
     	ExtentI.attachCatalinaLogsForSuccess();
     }

     Log.methodExit(methodname);

	}
	
	public void performO2CWithdrawalByUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName, String chUserName, String chDomainName, String chGeography) {
	   	 final String methodname = "performO2CWithdrawalByUserName";
		 Log.methodEntry(methodname, opCategoryName, chUserName , chDomainName , chCategoryName , chGeography);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("User Name");
	     Log.info("Geography of User: "+ chGeography);
	     String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(chGeography);
	     String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
	     Log.info("Geography of Parent: " + geoDomainName);
	     o2CWithdrawal.selectGeography(geoDomainName);
	     o2CWithdrawal.selectDomain(chDomainName);
	     o2CWithdrawal.selectCategory(chCategoryName);
	     o2CWithdrawal.enterChUserName(chUserName);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "simple");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawButton();
	     o2CWithdrawal.enterPIN(opPin);
	     o2CWithdrawal.clickWithdrawButtonPopup();
	     boolean res = o2CWithdrawal.O2CWithdrawSuccessVisibility();
	     
	     if(!res) {
	     	ExtentI.Markup(ExtentColor.RED, "O2C withdraw failed");
	     	ExtentI.attachScreenShot();
	     	ExtentI.attachCatalinaLogs();
	     }
	     else	{
	    	String transactionId =  o2CWithdrawal.getTransactionId();
	      	ExtentI.Markup(ExtentColor.GREEN, "O2C withdraw is successfully done with transaction Id: " + transactionId);
	     	ExtentI.attachCatalinaLogsForSuccess();
	     }

	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByLoginId(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName, String productCode , String productName , String chLoginId, String chDomainName) {
	   	 final String methodname = "performO2CWithdrawalByLoginId";
		 Log.methodEntry(methodname, opCategoryName, chLoginId , chCategoryName , chDomainName);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Login Id");
	     o2CWithdrawal.selectDomain(chDomainName);
	     o2CWithdrawal.selectCategory(chCategoryName);
	     o2CWithdrawal.enterLoginId(chLoginId);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "simple");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawButton();
	     o2CWithdrawal.enterPIN(opPin);
	     o2CWithdrawal.clickWithdrawButtonPopup();
	     boolean res = o2CWithdrawal.O2CWithdrawSuccessVisibility();
	     
	     if(!res) {
	     	ExtentI.Markup(ExtentColor.RED, "O2C withdraw failed");
	     	ExtentI.attachScreenShot();
	     	ExtentI.attachCatalinaLogs();
	     }
	     else	{
	    	String transactionId =  o2CWithdrawal.getTransactionId();
	      	ExtentI.Markup(ExtentColor.GREEN, "O2C withdraw is successfully done with transaction Id: " + transactionId);
	     	ExtentI.attachCatalinaLogsForSuccess();
	     }

	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalWithWrongPin(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithWrongPin";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "simple");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawButton();
	     int pinLength = opPin.length();
	     String randomPin = randomGeneration.randomNumeric(pinLength);
	     o2CWithdrawal.enterPIN(randomPin);
	     o2CWithdrawal.clickWithdrawButtonPopup();
	     String actualMsg = o2CWithdrawal.O2CWithdrawFailure();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "The PIN you have entered is incorrect.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);

	     Log.methodExit(methodname);

		}
	
	public void checkPinReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "checkPinReset";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "simple");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawButton();
	     o2CWithdrawal.enterPIN(opPin);
	     o2CWithdrawal.clickClosePopup();
	     o2CWithdrawal.clickWithdrawButton();
	     String res = o2CWithdrawal.checkPinFeild(); 
	     if(res.isEmpty()) {
	    	ExtentI.Markup(ExtentColor.GREEN, "Pin feild did reset successfully");
	     	ExtentI.attachCatalinaLogsForSuccess();
	     }
	     else	{
	     	ExtentI.Markup(ExtentColor.RED, "Pin Feild is did not reset.");
	     	ExtentI.attachScreenShot();
	     	ExtentI.attachCatalinaLogs();
	     }

	     Log.methodExit(methodname);

		}
	public void performO2CWithdrawalWithBlankRemarks(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithBlankRemarks";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "simple");
	     o2CWithdrawal.clickWithdrawButton();
	     String errorMsg = o2CWithdrawal.getRemarksError();
	     Log.info("Error message: " + errorMsg);
	     String expectedMsg = "Remarks Required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(errorMsg, expectedMsg);

	     Log.methodExit(methodname);

		}
		
	public void performO2CWithdrawalWithBlankAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithBlankAmount";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawButton();
	     String errorMsg = o2CWithdrawal.getAmountError(productName);
	     Log.info("Error message: " + errorMsg);
	     String expectedMsg = "Amount is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(errorMsg, expectedMsg);
	     
	     Log.methodExit(methodname);

		}

	public void checkWithdrawalDetailsReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "checkWithdrawalDetailsReset";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "simple");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawalDetailsReset();
	     boolean isRemarksEmpty = o2CWithdrawal.isRemarksEmpty();
	     boolean isAmountEmpty = o2CWithdrawal.isAmountEmpty();
	     if(isAmountEmpty && isRemarksEmpty) {
	    	 ExtentI.Markup(ExtentColor.GREEN, "Reset button worked successfully");
	    	 ExtentI.attachCatalinaLogsForSuccess();
	     }
	     else{
	     	ExtentI.Markup(ExtentColor.RED, "Reset button did not work successfully");
	     	ExtentI.attachScreenShot();
	     	ExtentI.attachCatalinaLogs();
	     }

	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalWithZeroAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithZeroAmount";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "zero");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawButton();
	     String errorMsg = o2CWithdrawal.O2CWithdrawFailure();
	     Log.info("Error message: " + errorMsg);
	     String expectedMsg = "Commission slab is not defined for product and requested quantity.";
	     Log.info("Expected message: " + expectedMsg);
	     Assertion.assertContainsEquals(errorMsg, expectedMsg);
	     
	     Log.methodExit(methodname);

		}

	public void performO2CWithdrawalWithAlphanumericAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithAlphanumericAmount";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "alphanumeric");
	     String errorMsg = o2CWithdrawal.getAmountError(productName);
	     Log.info("Error message: " + errorMsg);
	     String expectedMsg = "Invalid Amount.";
	     Log.info("Expected Message: " + expectedMsg);     
	     Assertion.assertContainsEquals(errorMsg, expectedMsg);
	     
	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalWithLargeAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithLargeAmount";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "largeAmount");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickWithdrawButton();
	     o2CWithdrawal.enterPIN(opPin);
	     o2CWithdrawal.clickWithdrawButtonPopup();
	     String actualMsg = o2CWithdrawal.O2CWithdrawFailure();
	     Log.info("Actual message: " + actualMsg);
	     String[] productNameSplit = productName.split(" ", -1);
	     String productNameJoined = String.join("",productNameSplit);
	     String expectedMsg = "Withdrawal quantity for product " + productNameJoined.toUpperCase() + " is more than user's balance.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);

	     Log.methodExit(methodname);

		}


	public void performO2CWithdrawalWithBlankOperatorWallet(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithBlankOperatorWallet";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     String actualMsg = o2CWithdrawal.getOperatorWalletError();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "Operator Wallet is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);

	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalWithBlankSearchByCriteria(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalWithBlankSearchByCriteria";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.enterMsisdn(chMsisdn);
	     o2CWithdrawal.clickProceedButton();
	     String actualMsg = o2CWithdrawal.getSearchByCriteriaError();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "Select Search Criteria First.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);

	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByLoginIdBlankDetails(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName, String productCode , String productName , String chLoginId, String chDomainName) {
	   	 final String methodname = "performO2CWithdrawalByLoginIdBlankDetails";
		 Log.methodEntry(methodname, opCategoryName, chLoginId , chCategoryName , chDomainName);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Login Id");
	     o2CWithdrawal.clickProceedButton();
	     
	     String actualMsg = o2CWithdrawal.getDomainError();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "Domain is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     actualMsg = o2CWithdrawal.getCategoryError();
	     Log.info("Actual message: " + actualMsg);
	     expectedMsg = "User Category is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     actualMsg = o2CWithdrawal.getLoginIdError();
	     Log.info("Actual message: " + actualMsg);
	     expectedMsg = "Login ID is required";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByInvalidLoginId(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName, String productCode , String productName , String chLoginId, String chDomainName) {
	   	 final String methodname = "performO2CWithdrawalByInvalidLoginId";
		 Log.methodEntry(methodname, opCategoryName, chLoginId , chCategoryName , chDomainName);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Login Id");
	     o2CWithdrawal.selectDomain(chDomainName);
	     o2CWithdrawal.selectCategory(chCategoryName);
	     String loginId = randomGeneration.randomAlphaNumeric(10);
	     o2CWithdrawal.enterLoginId(loginId);
	     o2CWithdrawal.clickProceedButton();
	     String actualMsg = o2CWithdrawal.getUserNotFoundMessage();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "User " + loginId + " not found .";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);

	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByUserNameBlankDetails(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName, String chUserName, String chDomainName, String chGeography) {
	   	 final String methodname = "performO2CWithdrawalByUserNameBlankDetails";
		 Log.methodEntry(methodname, opCategoryName, chUserName , chDomainName , chCategoryName , chGeography);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("User Name");
	     o2CWithdrawal.clickProceedButton();
	     
	     String actualMsg = o2CWithdrawal.getGeographyError();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "Geography is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     actualMsg = o2CWithdrawal.getDomainError();
	     Log.info("Actual message: " + actualMsg);
	     expectedMsg = "Domain is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     actualMsg = o2CWithdrawal.getOwnerCategoryError();
	     Log.info("Actual message: " + actualMsg);
	     expectedMsg = "Owner Category is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     actualMsg = o2CWithdrawal.getCategoryError();
	     Log.info("Actual message: " + actualMsg);
	     expectedMsg = "User Category is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     actualMsg = o2CWithdrawal.getChannelOwnerNameError();
	     Log.info("Actual message: " + actualMsg);
	     expectedMsg = "Channel owner name is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     actualMsg = o2CWithdrawal.getChUserNameError();
	     Log.info("Actual message: " + actualMsg);
	     expectedMsg = "User name is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);

	     Log.methodExit(methodname);

		}
	
	public void checkUserDetailsReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName, String productCode , String productName , String chLoginId, String chDomainName) {
	   	 final String methodname = "checkUserDetailsReset";
		 Log.methodEntry(methodname, opCategoryName, chLoginId , chCategoryName , chDomainName);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Login Id");
	     o2CWithdrawal.selectDomain(chDomainName);
	     o2CWithdrawal.selectCategory(chCategoryName);
	     o2CWithdrawal.enterLoginId(chLoginId);
	     o2CWithdrawal.clickProceedButton();
	     o2CWithdrawal.enterAmount(productName, "simple");
	     o2CWithdrawal.enterRemarks(_masterVO.getProperty("Remarks"));
	     o2CWithdrawal.clickUserDetailsReset();
	     
	     if(o2CWithdrawal.checkWithdrawDetailsAreaReset() &&
	        o2CWithdrawal.checkOperatorWalletReset() &&
	        o2CWithdrawal.checkSearchByReset() &&
	        o2CWithdrawal.checkSearchInputField().isEmpty()) {
	    	    ExtentI.Markup(ExtentColor.GREEN, "User details reset button worked successfully.");
		     	ExtentI.attachCatalinaLogsForSuccess();
		     }
		     else{
		    	 ExtentI.Markup(ExtentColor.RED, "User details reset button did not work successfully.");
			     ExtentI.attachScreenShot();
			     ExtentI.attachCatalinaLogs();
		     }
	     
	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByBlankMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalByMsisdn";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     o2CWithdrawal.clickProceedButton();
	     
	     String actualMsg = o2CWithdrawal.getMsisdnError();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "Mobile number is required.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByInvalidMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalByInvalidMsisdn";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     String msisdn = randomGeneration.randomNumberWithoutZero(10);
	     o2CWithdrawal.enterMsisdn(msisdn);
	     o2CWithdrawal.clickProceedButton();
	     String actualMsg = o2CWithdrawal.getUserNotFoundMessage();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "User " + msisdn + " details not found.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByAlphanumericMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName) {
	   	 final String methodname = "performO2CWithdrawalByInvalidMsisdn";
		 Log.methodEntry(methodname, opCategoryName, chMsisdn);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	          
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("Mobile Number");
	     String msisdn = randomGeneration.randomAlphaNumeric(10);
	     o2CWithdrawal.enterMsisdn(msisdn);
	     o2CWithdrawal.clickProceedButton();
	     String actualMsg = o2CWithdrawal.getMsisdnError();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "Please enter a valid mobile number.";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);
	     
	     Log.methodExit(methodname);

		}
	
	public void performO2CWithdrawalByInvalidUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode , String productName, String chUserName, String chDomainName, String chGeography) {
	   	 final String methodname = "performO2CWithdrawalByUserName";
		 Log.methodEntry(methodname, opCategoryName, chUserName , chDomainName , chCategoryName , chGeography);
	     String MasterSheetPath = _masterVO.getProperty("DataProvider");
	     ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	     login.UserLogin(driver, "Operator", opCategoryName);
	     
	     if(!o2CWithdrawal.isO2CVisible()) {
	    	 o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Transfer heading is clicked");
			}
			else {
				o2CWithdrawal.clickO2CHeading();
				o2CWithdrawal.clickO2CTransactionHeading();
				Log.info("O2C Heading and Transaction Heading is clicked");
			}
	     	
	     o2CWithdrawal.clickWithdrawHeading();
	     o2CWithdrawal.selectOperatorWallet("Sale");
	     o2CWithdrawal.selectSearchByCriteria("User Name");
	     Log.info("Geography of User: "+ chGeography);
	     String GeoCode = DBHandler.AccessHandler.getParentGeoDomCode(chGeography);
	     String geoDomainName = DBHandler.AccessHandler.getGrpDomainName(GeoCode);
	     Log.info("Geography of Parent: " + geoDomainName);
	     o2CWithdrawal.selectGeography(geoDomainName);
	     o2CWithdrawal.selectDomain(chDomainName);
	     o2CWithdrawal.selectCategory(chCategoryName);
	     String username = randomGeneration.randomAlphaNumeric(10);
	     o2CWithdrawal.enterChUserName(username);
	     o2CWithdrawal.clickProceedButton();
	     String actualMsg = o2CWithdrawal.getUserNotFoundMessage();
	     Log.info("Actual message: " + actualMsg);
	     String expectedMsg = "User " + username + " not found .";
	     Log.info("Expected Message: " + expectedMsg);
	     Assertion.assertContainsEquals(actualMsg, expectedMsg);

	     Log.methodExit(methodname);

		}







}
