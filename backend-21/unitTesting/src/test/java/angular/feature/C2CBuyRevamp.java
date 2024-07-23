package angular.feature;

       import angular.classes.LoginRevamp;
import angular.pageobjects.c2capproval.C2CApproval;
import angular.pageobjects.c2ctransfer.C2CTransfers;
        import com.aventstack.extentreports.Status;
        import com.aventstack.extentreports.markuputils.ExtentColor;
        import com.classes.BaseTest;
        import com.commons.ExcelI;
        import com.dbrepository.DBHandler;
        import com.utils.*;
        import org.openqa.selenium.WebDriver;
        import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class C2CBuyRevamp extends BaseTest {



    public WebDriver driver;
    LoginRevamp login;
    C2CTransfers transfers;
    C2CApproval approval;
    HashMap<String, String> c2cTransferMap;


    public C2CBuyRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        transfers = new C2CTransfers(driver);
        approval = new C2CApproval(driver);
        c2cTransferMap = new HashMap<String, String>();
    }


    public HashMap<String,String> performC2CBuyMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode, String fromParent) {
        final String methodname = "performC2CBuyMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent ,FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        HashMap<String,String> productQty=transfers.enterQuantityforC2CBuyRevamp() ;
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
        transfers.enterC2CBuyPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));

//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getTodayDate());
        transfers.clickC2CBuySubmitButton();

        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(PIN);
            transfers.clickC2CBuySubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "Stock Purchase Request Initiated";
                String C2CTransferResultMessage = transfers.getC2CBuyPurchaseRequestInitiatedMessage();
                if (actualMessage.equals(C2CTransferResultMessage)) {
                    transfers.printC2CTopupsInitiatedAmounts();
                    ExtentI.Markup(ExtentColor.GREEN, "C2C Purchase Initiated : " + C2CTransferResultMessage);
                    String TxnId=transfers.printC2CBuyTransactionID();
                    ExtentI.attachCatalinaLogsForSuccess() ;
                    ExtentI.attachScreenShot();
                    transfers.clickC2CBuyRequestDoneButton();
                    
                    c2cTransferMap.put("TxnId",TxnId);
                    for (Map.Entry<String,String> entry : productQty.entrySet())  
                    	c2cTransferMap.put(entry.getKey(),entry.getValue());
                } else {
                    ExtentI.Markup(ExtentColor.RED, "C2C Purchase Initiated Displays wrong message as " + C2CTransferResultMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Purchase Initiated Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname);
        return c2cTransferMap; 
    }

    public HashMap<String,String> performC2CBuyLoginBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode, String fromParent) {
        final String methodname = "performC2CTransfer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser", fromParent,FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CBuyerLoginID")) ;

        transfers.c2cSelectCategoryforLoginIDBuyerType(ToCategory) ;


        String loginidOfToChannelUser = DBHandler.AccessHandler.getLoginidFromMsisdn(toMSISDN) ;
       // String LoginIDOfToChannelUser = transfers.getLoginIDOfToChannelUserToTransferC2C(toMSISDN) ;

        transfers.enterLoginidOfLoginIDBuyerType(loginidOfToChannelUser) ;
        transfers.clickC2CProceed();
        HashMap<String,String> productQty=transfers.enterQuantityforC2CBuyRevamp() ;

        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
        transfers.enterC2CBuyPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getDateOnGUI());
        transfers.clickC2CBuySubmitButton();

        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(PIN);
            transfers.clickC2CBuySubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "Stock Purchase Request Initiated";
                String C2CTransferResultMessage = transfers.getC2CBuyPurchaseRequestInitiatedMessage();
                if (actualMessage.equals(C2CTransferResultMessage)) {
                    transfers.printC2CTopupsInitiatedAmounts();
                    ExtentI.Markup(ExtentColor.GREEN, "C2C Purchase Initiated : " + C2CTransferResultMessage);
                    String TxnId=transfers.printC2CBuyTransactionID();
                    ExtentI.attachCatalinaLogsForSuccess();
                    ExtentI.attachScreenShot();
                    transfers.clickC2CBuyRequestDoneButton();
                    c2cTransferMap.put("TxnId",TxnId);
                    for (Map.Entry<String,String> entry : productQty.entrySet())  
                    	c2cTransferMap.put(entry.getKey(),entry.getValue());
                } else {
                    ExtentI.Markup(ExtentColor.RED, "C2C Purchase Initiated Displays wrong message as " + C2CTransferResultMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Purchase Initiated Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname);
        return c2cTransferMap; 
    }

    public HashMap<String,String> performC2CBuyUsernameBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode, String fromParent) {
        final String methodname = "performC2CBuyUsernameBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        String UserName = login.getUsernameofUser("ChannelUser", loginID);
        Log.info("USERNAME  : "+UserName ) ;
        login.UserLogin(driver, "ChannelUser",fromParent ,FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CUserNameBuyerType")) ;

        transfers.c2cSelectCategoryforUsernameBuyerType(ToCategory) ;
        //transfers.enterUsernameOfUserNameBuyerType() ;
        String usernameOfToChannelUser = DBHandler.AccessHandler.getUsernameFromMsisdn(toMSISDN) ;
        Log.info("USERNAME : "+usernameOfToChannelUser+ " fetched from DB for msisdn "+toMSISDN) ;
        transfers.enterUsernameOfUserNameBuyerType(usernameOfToChannelUser) ;
        transfers.clickC2CProceed();
        HashMap<String,String> productQty=transfers.enterQuantityforC2CBuyRevamp() ;

        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
        transfers.enterC2CBuyPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getDateOnGUI());
        transfers.clickC2CBuySubmitButton();

        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(PIN);
            transfers.clickC2CBuySubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "Stock Purchase Request Initiated";
                String C2CTransferResultMessage = transfers.getC2CBuyPurchaseRequestInitiatedMessage();
                if (actualMessage.equals(C2CTransferResultMessage)) {
                    transfers.printC2CTopupsInitiatedAmounts();
                    ExtentI.Markup(ExtentColor.GREEN, "C2C Purchase Initiated : " + C2CTransferResultMessage);
                    String TxnId=transfers.printC2CBuyTransactionID();
                    ExtentI.attachCatalinaLogsForSuccess() ;
                    ExtentI.attachScreenShot();
                    transfers.clickC2CBuyRequestDoneButton();
                    c2cTransferMap.put("TxnId",TxnId);
                    for (Map.Entry<String,String> entry : productQty.entrySet())  
                    	c2cTransferMap.put(entry.getKey(),entry.getValue());
                } else {
                    ExtentI.Markup(ExtentColor.RED, "C2C Purchase Initiated Displays wrong message as " + C2CTransferResultMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Purchase Initiated Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname);
        return c2cTransferMap; 
    }


    public void performC2CBuyBlankBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
 
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        transfers.selectC2CBuyerType("");
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        
        String blankBuyerErrorMessageCaptured = transfers.getC2CBlankBuyerErrorMessageonGUI();
        Log.info("Blank Buyer Error message fetched from GUI : " + blankBuyerErrorMessageCaptured);
        String expectedMessage = "Select Search Criteria First." ;
        if (expectedMessage.equals(blankBuyerErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankBuyerErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Buyer Error message not displayed on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }

    public void performC2CBuyBlankMSISDNofMobileBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankMSISDNofMobileBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
     
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType"));
        transfers.clickC2CProceed();

        String blankMSISDNErrorMessageCaptured = transfers.getC2CBlankMsisdnErrorMessageOnGUI();
        Log.info("Blank MSISDN Error message fetched from GUI : " + blankMSISDNErrorMessageCaptured);
        String expectedMessage = "Mobile number is required." ;
        if (expectedMessage.equals(blankMSISDNErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankMSISDNErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank MSISDN Error message not displayed on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }

    public void performC2CBuyBlankTOPUPSofMobileBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankTOPUPSofMobileBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN) ;
        transfers.clickC2CProceed() ;
        String expectedMessage = "Amount is required." ;
        String actualMessage ="";
        transfers.clickC2CBuySubmitButton() ;
        List<WebElement> blankTopupErrorMessage = transfers.getC2CBuyBlankTOPUPErrorMessageOnGUI();
        boolean flag = false ;
		for (WebElement ele : blankTopupErrorMessage) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true ;
				break;
			}
		}
		if(flag)
		{
			Assertion.assertContainsEquals(actualMessage, expectedMessage) ;
            ExtentI.attachCatalinaLogsForSuccess();
		}
		else
		{
			currentNode.log(Status.FAIL, "Blank Amount Error not displayed on GUI");
	        ExtentI.attachCatalinaLogs();
	        ExtentI.attachScreenShot();
		}

        Log.methodExit(methodname);
    }

    public void performC2CBuyBlankCategoryOfUsernameBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankCategoryOfUsernameBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CUserNameBuyerType"));
        transfers.clickC2CProceed();
        String blankCategoryErrorMessageCaptured = transfers.getC2CBlankCategoryErrorMessageOnGUI() ;
        Log.info("Function returned - Blank Category Error message fetched from GUI : " + blankCategoryErrorMessageCaptured);
        String expectedMessage = "User Category is required." ;
        if (expectedMessage.equals(blankCategoryErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankCategoryErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Category of username Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }

    public void performC2CBuyBlankUsernameOfUsernameBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankUsernameOfUsernameBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CUserNameBuyerType"));
        transfers.clickC2CProceed();
        String blankUsernameErrorMessageCaptured = transfers.getC2CBlankUsernameErrorMessageOnGUI() ;
        Log.info("Function returned - Blank Username Error message fetched from GUI : " + blankUsernameErrorMessageCaptured);
        String expectedMessage = "Required" ;
        if (expectedMessage.equals(blankUsernameErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankUsernameErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Username of username Buyer type Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }


    public void performC2CBuyBlankCategoryOfLOGINIDBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankCategoryOfLOGINIDBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CBuyerLoginID"));
        transfers.clickC2CProceed();
        String blankCategoryErrorMessageCaptured = transfers.getC2CBlankCategoryErrorMessageOnGUI() ;
        Log.info("Function returned - Blank Category Error message fetched from GUI : " + blankCategoryErrorMessageCaptured);
        String expectedMessage = "User Category is required." ;
        if (expectedMessage.equals(blankCategoryErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankCategoryErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Category of Loginid Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }


    public void performC2CBuyBlankLoginidOfLOGINIDBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankLoginidOfLOGINIDBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
      
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
     
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CBuyerLoginID"));
        transfers.clickC2CProceed();
        String blankLoginidErrorMessageCaptured = transfers.getC2CBlankLoginidErrorMessageOnGUI() ;
        Log.info("Function returned - Blank Loginid Error message fetched from GUI : " + blankLoginidErrorMessageCaptured);
        String expectedMessage = "Required" ;
        if (expectedMessage.equals(blankLoginidErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankLoginidErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Loginid of Loginid Buyer type Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
    
    public void performC2CBuyNegativeAmountOfMobileBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyNegativeAmountOfMobileBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
    
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
      
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType"));
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        transfers.enterNegativeQuantityforC2CBuyRevamp() ;
        
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
        transfers.enterC2CBuyPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));

//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getTodayDate());
        transfers.clickC2CBuySubmitButton();
        
        String negativeAmountErrorMessageCaptured = transfers.getC2CBuyNegativeAmountErrorMessageOnGUI() ;
        Log.info("Function returned - Negative Amount Error message fetched from GUI : " + negativeAmountErrorMessageCaptured);
        String expectedMessage = "Invalid Amount." ;
        if (expectedMessage.equals(negativeAmountErrorMessageCaptured)) {
            Assertion.assertContainsEquals(negativeAmountErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Negative Amount of Mobile Buyer type Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
    
    public void performC2CBuyBlankRemarksOfMobileBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankRemarksOfMobileBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
      
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
 
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType"));
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        transfers.enterQuantityforC2CBuyRevamp() ;
        
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("") ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
        transfers.enterC2CBuyPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));

//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getTodayDate());
        transfers.clickC2CBuySubmitButton();
        
        String blankRemarksErrorMessageCaptured = transfers.getC2CBuyBlankRemarksErrorMessageOnGUI() ;
        Log.info("Function returned - Blank Remarks Error message fetched from GUI : " + blankRemarksErrorMessageCaptured);
        String expectedMessage = "Remarks Required." ;
        if (expectedMessage.equals(blankRemarksErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankRemarksErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
        	Assertion.assertContainsEquals(blankRemarksErrorMessageCaptured, expectedMessage);
            currentNode.log(Status.FAIL, "Blank Remarks of Mobile Buyer type Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
    
    public void performC2CBuyBlankPaymentModeOfMobileBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankPaymentModeOfMobileBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType"));
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        transfers.enterQuantityforC2CBuyRevamp() ;
        
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));

//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getTodayDate());
        transfers.clickC2CBuySubmitButton();
        
        String blankPaymentModesErrorMessageCaptured = transfers.getC2CBuyBlankPaymentModesErrorMessageOnGUI() ;
        Log.info("Function returned - Blank PaymentModes Error message fetched from GUI : " + blankPaymentModesErrorMessageCaptured);
        String expectedMessage = "Please Choose Payment mode." ;
        if (expectedMessage.equals(blankPaymentModesErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankPaymentModesErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
        	Assertion.assertContainsEquals(blankPaymentModesErrorMessageCaptured, expectedMessage);
            currentNode.log(Status.FAIL, "Blank PaymentModes of Mobile Buyer type Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
    
    public void performC2CBuyBlankAmountOfMobileBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyBlankAmountOfMobileBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType"));
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getTodayDate());
        transfers.clickC2CBuySubmitButton();
        
        String blankAmountErrorMessageCaptured = transfers.getC2CBuyBlankAmountErrorMessageOnGUI() ;
        Log.info("Function returned - Blank Amount Error message fetched from GUI : " + blankAmountErrorMessageCaptured);
        String expectedMessage = "Amount is required." ;
        if (expectedMessage.equals(blankAmountErrorMessageCaptured)) {
            Assertion.assertContainsEquals(blankAmountErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "blank Amount of Mobile Buyer type Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
    
    public void performC2CBuyInvalidRefNoOfMobileBuyer(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyInvalidRefNoOfMobileBuyer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType"));
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterQuantityforC2CBuyRevamp() ;
        transfers.enterC2CBuyRefNum(randomGeneration.randomAlphabets(5));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getTodayDate());
        transfers.clickC2CBuySubmitButton();
        
        String InvalidRefNoErrorMessageCaptured = transfers.getC2CBuyInvalidRefNoErrorMessageOnGUI() ;
        Log.info("Function returned - Invalid RefNo Error message fetched from GUI : " + InvalidRefNoErrorMessageCaptured);
        String expectedMessage = "Please Enter Digits only." ;
        if (expectedMessage.equals(InvalidRefNoErrorMessageCaptured)) {
            Assertion.assertContainsEquals(InvalidRefNoErrorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Invalid RefNo of Mobile Buyer type Error did not display on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
    
    public void performC2CBuyInvalidPinMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CBuyInvalidPinMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser", FromCategory);
        
        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleBuyHeading();
        transfers.clickEtopup();

        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        transfers.enterQuantityforC2CBuyRevamp() ;
        
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterC2CBuyRefNum(randomGeneration.randomNumeric(6));
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectC2CBuyPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
        transfers.enterC2CBuyPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));

//        transfers.enterC2CBuyPaymentInstDate(transfers.getDateMMDDYYYY());
        transfers.enterC2CBuyPaymentInstDate(transfers.getTodayDate());
        transfers.clickC2CBuySubmitButton();

        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(new RandomGeneration().randomNumeric(4));
            transfers.clickC2CBuySubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CTransferFailedVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "Buy Fail";
                String C2CTransferResultMessage = transfers.getC2CTransferTransferFailedMessage();
                if (actualMessage.equals(C2CTransferResultMessage)) {
                	Assertion.assertContainsEquals(C2CTransferResultMessage, actualMessage);
                    ExtentI.Markup(ExtentColor.GREEN, "C2C Transfer Failure : " + C2CTransferResultMessage);
                    ExtentI.attachCatalinaLogsForSuccess() ;
                } else {
                	Assertion.assertContainsEquals(C2CTransferResultMessage, actualMessage);
                    ExtentI.Markup(ExtentColor.RED, "C2C Transfer Failed Displays wrong message as " + C2CTransferResultMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Transfer Failure Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname);
    }
    
    public HashMap<String, String> performingLevel1Approval(String FromCategory, String ToCategory,
			String PIN, String txndId, int maxApprovalLevel,String fromParent) {
		final String methodname = "performC2CTransferMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser", fromParent,FromCategory);

        if (!approval.isC2CVisible()) {
            approval.clickC2CApprovalLevel1Heading();
            Log.info("C2C Transfer heading is clicked");
        } else {
        	approval.clickC2CHeading();
            approval.clickC2CApprovalLevel1Heading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        approval.spinnerWait();

        approval.clickC2CSingleOperationHeading();
        approval.clickeTopupHeading();
        
        approval.enterTxnId(txndId);
        approval.approveMainScreen();
        
        approval.enterRemarks("Remarks entered for C2C Approval Level 1 to: " + ToCategory);
        
        approval.approveSecondScreen();
        approval.clickYes();
        
        boolean C2CTransferInitiatedPopup = approval.C2CTransferInitiatedVisibility();
        if (C2CTransferInitiatedPopup == true) {
            String expectedMessage = "Transaction Approved";
            String actualMsg=approval.getSuccessMsg();
            if (expectedMessage.equals(actualMsg)) {
            	Assertion.assertContainsEqualsSet(actualMsg, expectedMessage);
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Approved : " + actualMsg);
                String txnId=approval.getTxnId();
                ExtentI.attachCatalinaLogsForSuccess() ;
                approval.clickDoneButton();
                
                c2cTransferMap.put("TxnId",txnId);
            } else {
                ExtentI.Markup(ExtentColor.RED, "C2C Approval Displays wrong message as " + actualMsg);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "C2C Approval PopUP Initiate failed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel2Approval(String FromCategory, String ToCategory, 
			String PIN, String txndId, int maxApprovalLevel,String fromParent) {
		final String methodname = "performC2CTransferMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);

        if (!approval.isC2CVisible()) {
            approval.clickC2CApprovalLevel2Heading();
            Log.info("C2C Transfer heading is clicked");
        } else {
        	approval.clickC2CHeading();
            approval.clickC2CApprovalLevel2Heading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        approval.spinnerWait();
//        try {
//			Thread.sleep(5500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        approval.clickC2CSingleOperationHeading();
        approval.clickeTopupHeading();
        
        approval.enterTxnId(txndId);
        approval.approveMainScreen();
        
        approval.enterRemarks("Remarks entered for C2C Approval Level 2 to: " + ToCategory );
       
        approval.approveSecondScreen();
        approval.clickYes();
        
        boolean C2CTransferInitiatedPopup = approval.C2CTransferInitiatedVisibility();
        if (C2CTransferInitiatedPopup == true) {
            String expectedMessage = "Transaction Approved";
            String actualMsg=approval.getSuccessMsg();
            if (expectedMessage.equals(actualMsg)) {
            	Assertion.assertContainsEqualsSet(actualMsg, expectedMessage);
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Approved : " + actualMsg);
                String txnId=approval.getTxnId();
                ExtentI.attachCatalinaLogsForSuccess() ;
                approval.clickDoneButton();
                
                c2cTransferMap.put("TxnId",txnId);
            } else {
                ExtentI.Markup(ExtentColor.RED, "C2C Approval Displays wrong message as " + actualMsg);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "C2C Approval PopUP Initiate failed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3Approval(String FromCategory, String ToCategory,
			String PIN, String txndId, int maxApprovalLevel, String fromParent) {
		final String methodname = "performC2CTransferMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);

        if (!approval.isC2CVisible()) {
            approval.clickC2CApprovalLevel3Heading();
            Log.info("C2C Transfer heading is clicked");
        } else {
        	approval.clickC2CHeading();
            approval.clickC2CApprovalLevel3Heading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        approval.spinnerWait();
//        try {
//			Thread.sleep(5500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        approval.clickC2CSingleOperationHeading();
        approval.clickeTopupHeading();
        
        approval.enterTxnId(txndId);
        approval.approveMainScreen();
        
        approval.enterRemarks("Remarks entered for C2C Approval Level 3 to: " + ToCategory);
        
        approval.approveSecondScreen();
        approval.clickYes();
        
        boolean C2CTransferInitiatedPopup = approval.C2CTransferInitiatedVisibility();
        if (C2CTransferInitiatedPopup == true) {
            String expectedMessage = "Transaction Approved";
            String actualMsg=approval.getSuccessMsg();
            if (expectedMessage.equals(actualMsg)) {
                Assertion.assertContainsEqualsSet(actualMsg, expectedMessage);
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Approved : " + actualMsg);
                String txnId=approval.getTxnId();
                ExtentI.attachCatalinaLogsForSuccess() ;
                approval.clickDoneButton();
                
                c2cTransferMap.put("TxnId",txnId);
            } else {
                ExtentI.Markup(ExtentColor.RED, "C2C Approval Displays wrong message as " + actualMsg);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "C2C Approval PopUP Initiate failed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        
		return c2cTransferMap;
	}
	
	
	public void invalidRefNoApprovalLevel(String FromCategory, String ToCategory,
			String PIN, String txndId, int maxApprovalLevel, String fromParent) {
		final String methodname = "performC2CTransferMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);

        if (!approval.isC2CVisible()) {
            approval.clickC2CApprovalLevel1Heading();
            Log.info("C2C Transfer heading is clicked");
        } else {
        	approval.clickC2CHeading();
            approval.clickC2CApprovalLevel1Heading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        approval.spinnerWait();

        approval.clickC2CSingleOperationHeading();
        approval.clickeTopupHeading();
        
        approval.enterTxnId(txndId);
        approval.approveMainScreen();
        
        approval.enterRefNo(new RandomGeneration().randomAlphabets(5));
        approval.enterRemarks("Remarks entered for C2C Approval Level 3 to: " + ToCategory);
        approval.approveSecondScreen();
    
            String expectedMessage = "Please Enter Digits only.";
            String actualMsg=approval.getInvalidRefNoMsg();
            if (expectedMessage.equals(actualMsg)) {
                Assertion.assertContainsEqualsSet(actualMsg, expectedMessage);
                ExtentI.attachCatalinaLogsForSuccess() ;
            } else {
                ExtentI.Markup(ExtentColor.RED, "C2C Approval Displays wrong message as " + actualMsg);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
       
	}
	
	
	public void rejectInApprovalLevel(String FromCategory, String ToCategory,
			String PIN, String txndId, int maxApprovalLevel,String fromParent) {
		final String methodname = "rejectInApprovalLevel";
        Log.methodEntry(methodname, FromCategory, ToCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser", fromParent,FromCategory);

        if (!approval.isC2CVisible()) {
            approval.clickC2CApprovalLevel1Heading();
            Log.info("C2C Transfer heading is clicked");
        } else {
        	approval.clickC2CHeading();
            approval.clickC2CApprovalLevel1Heading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        approval.spinnerWait();

        approval.clickC2CSingleOperationHeading();
        approval.clickeTopupHeading();
        
        approval.enterTxnId(txndId);
        approval.rejectMainScreen();
        
        approval.rejectSecondScreen();
        approval.rejectYes();
        
        boolean C2CTransferInitiatedPopup = approval.C2CTransferInitiatedVisibility();
        if (C2CTransferInitiatedPopup == true) {
            String expectedMessage = "Transaction Rejected";
            String actualMsg=approval.getSuccessMsg();
            if (expectedMessage.equals(actualMsg)) {
            	Assertion.assertContainsEqualsSet(actualMsg, expectedMessage);
                ExtentI.Markup(ExtentColor.GREEN, "Transaction Rejected : " + actualMsg);
                ExtentI.attachCatalinaLogsForSuccess() ;
                approval.clickDoneButton();
                
                
            } else {
                ExtentI.Markup(ExtentColor.RED, "C2C Approval Displays wrong message as " + actualMsg);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "C2C Approval PopUP Initiate failed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        
		
	}
	
	
	public HashMap<String,String> performC2CReturnMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode, String fromParent,HashMap<String,String> qty) {
        final String methodname = "performC2CReturnMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent ,FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickSingleReturnHeading();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        
        
        transfers.enterQuantityforC2CWithdrawlRevamp(qty);
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.clickC2CReturnButton();


        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(PIN);
            transfers.clickC2CReturnSubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CWithdrawlVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "Return Successful";
                String C2CReturnResultMessage = transfers.getC2CWithdrawlSuccessfulMessage();
                if (actualMessage.equals(C2CReturnResultMessage)) {
                	Assertion.assertEquals(actualMessage, C2CReturnResultMessage);
                    ExtentI.Markup(ExtentColor.GREEN, "C2C Return : " + C2CReturnResultMessage);
                    ExtentI.attachCatalinaLogsForSuccess() ;
                    transfers.clickC2CBuyRequestDoneButton();
                } else {
                    ExtentI.Markup(ExtentColor.RED, "C2C Return Initiated Displays wrong message as " + C2CReturnResultMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                String errorMEssageForFailure = transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Return Initiated Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        
        Log.methodExit(methodname);
        return c2cTransferMap; 
    }
	
	public HashMap<String,String> performC2CReturnLoginBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode, String fromParent,HashMap<String,String> qty) {
        final String methodname = "performC2CReturnMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent ,FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickSingleReturnHeading();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CBuyerLoginID")) ;

        transfers.c2cSelectCategoryforLoginIDBuyerType(ToCategory) ;


        String loginidOfToChannelUser = DBHandler.AccessHandler.getLoginidFromMsisdn(toMSISDN) ;
       // String LoginIDOfToChannelUser = transfers.getLoginIDOfToChannelUserToTransferC2C(toMSISDN) ;

        transfers.enterLoginidOfLoginIDBuyerType(loginidOfToChannelUser) ;
        transfers.clickC2CProceed();
        
        
        transfers.enterQuantityforC2CWithdrawlRevamp(qty);
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.clickC2CReturnButton();


        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(PIN);
            transfers.clickC2CReturnSubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CWithdrawlVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "Return Successful";
                String C2CReturnResultMessage = transfers.getC2CWithdrawlSuccessfulMessage();
                if (actualMessage.equals(C2CReturnResultMessage)) {
                	Assertion.assertEquals(actualMessage, C2CReturnResultMessage);
                    ExtentI.Markup(ExtentColor.GREEN, "C2C Return : " + C2CReturnResultMessage);
                    ExtentI.attachCatalinaLogsForSuccess() ;
                    transfers.clickC2CBuyRequestDoneButton();
                } else {
                    ExtentI.Markup(ExtentColor.RED, "C2C Return Initiated Displays wrong message as " + C2CReturnResultMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                String errorMEssageForFailure = transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Return Initiated Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        
        Log.methodExit(methodname);
        return c2cTransferMap; 
    }
	
	public HashMap<String,String> performC2CReturnUsernameBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode, String fromParent,HashMap<String,String> qty) {
        final String methodname = "performC2CReturnMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent ,FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickSingleReturnHeading();
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CUserNameBuyerType")) ;

        transfers.c2cSelectCategoryforUsernameBuyerType(ToCategory) ;
        //transfers.enterUsernameOfUserNameBuyerType() ;
        String usernameOfToChannelUser = DBHandler.AccessHandler.getUsernameFromMsisdn(toMSISDN) ;
        Log.info("USERNAME : "+usernameOfToChannelUser+ " fetched from DB for msisdn "+toMSISDN) ;
        transfers.enterUsernameOfUserNameBuyerType(usernameOfToChannelUser) ;
        transfers.clickC2CProceed();
        
        
        transfers.enterQuantityforC2CWithdrawlRevamp(qty);
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.clickC2CReturnButton();


        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(PIN);
            transfers.clickC2CReturnSubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CWithdrawlVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "Return Successful";
                String C2CReturnResultMessage = transfers.getC2CWithdrawlSuccessfulMessage();
                if (actualMessage.equals(C2CReturnResultMessage)) {
                	Assertion.assertEquals(actualMessage, C2CReturnResultMessage);
                    ExtentI.Markup(ExtentColor.GREEN, "C2C Return : " + C2CReturnResultMessage);
                    ExtentI.attachCatalinaLogsForSuccess() ;
                    transfers.clickC2CBuyRequestDoneButton();
                } else {
                    ExtentI.Markup(ExtentColor.RED, "C2C Return Initiated Displays wrong message as " + C2CReturnResultMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                String errorMEssageForFailure = transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Return Initiated Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        
        Log.methodExit(methodname);
        return c2cTransferMap; 
    }
	
	public void performC2CReturnMobileBuyerType_InvalidAmount(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode,String fromParent) {
        final String methodname = "performC2CWithdrawalMobileBuyerType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        
        transfers.clickSingleReturnHeading();  
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        int value= -100;
        transfers.enterNegativeQuantityforC2CWithdrawlRevamp(value);
     
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        
        transfers.clickC2CReturnButton();
        
        String InvalidAmtMessageCaptured = transfers.getC2CWithdrawlNegativeAmtErrorMessageonGUI();
        Log.info("Blank Buyer Error message fetched from GUI : " + InvalidAmtMessageCaptured);
        String expectedMessage = "Invalid Amount." ;
        if (expectedMessage.equals(InvalidAmtMessageCaptured)) {
            Assertion.assertContainsEquals(InvalidAmtMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Invalid Amount message not displayed on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
	
	public void performC2CReturnMobileBuyerType_BlankAmount(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode,String fromParent) {
        final String methodname = "performC2CReturnMobileBuyerType_BlankAmount";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        
        transfers.clickSingleReturnHeading();  
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
     
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        
        transfers.clickC2CReturnButton();
        
        String InvalidAmtMessageCaptured = transfers.getC2CWithdrawlBlankAmtErrorMessageonGUI();
        Log.info("Blank Buyer Error message fetched from GUI : " + InvalidAmtMessageCaptured);
        String expectedMessage = "Amount is required." ;
        if (expectedMessage.equals(InvalidAmtMessageCaptured)) {
            Assertion.assertContainsEquals(InvalidAmtMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Amount message not displayed on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
	
	public void performC2CReturnMobileBuyerType_BlankRemarks(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode,String fromParent) {
        final String methodname = "performC2CReturnMobileBuyerType_BlankRemarks";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        
        transfers.clickSingleReturnHeading(); 
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        int value = 50;
        transfers.enterNegativeQuantityforC2CWithdrawlRevamp(value);        
        transfers.clickC2CReturnButton();
        
        String blankRemarksMessageCaptured = transfers.getC2CWithdrawlBlankRemarksErrorMessageonGUI();
        Log.info("Blank Remarks Error message fetched from GUI : " + blankRemarksMessageCaptured);
        String expectedMessage = "Remarks Required.." ;
        if (expectedMessage.equals(blankRemarksMessageCaptured)) {
            Assertion.assertContainsEquals(blankRemarksMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Remarks message not displayed on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }
	
	
	public void performC2CReturnMobileBuyerType_InvalidPin(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode,String fromParent) {
        final String methodname = "performC2CReturnMobileBuyerType_InvalidPin";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser",fromParent, FromCategory);

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        
        transfers.clickSingleReturnHeading();   
        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        int value = 50;
        transfers.enterNegativeQuantityforC2CWithdrawlRevamp(value);   
        transfers.enterC2CBuyRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.clickC2CReturnButton();
        
        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
        if (PINPopUP == true) {
            transfers.enterC2CUserPIN(new RandomGeneration().randomNumeric(4));
            transfers.clickC2CReturnSubmitButtonAfterEnteringPIN();
            boolean C2CTransferInitiatedPopup = transfers.C2CWithdrawlVisibility();     //Transfer initiated displays etopup ptopup
            if (C2CTransferInitiatedPopup == true) {
                String actualMessage = "The PIN you have entered is incorrect.";
                String InvalidPinMessage = transfers.getC2WithdrawlInvalidPinMessage();
                if (actualMessage.equals(InvalidPinMessage)) {
                	Assertion.assertContainsEquals(InvalidPinMessage, actualMessage);
                    ExtentI.Markup(ExtentColor.GREEN, "C2C WithdrawlTransfer Failure : " + InvalidPinMessage);
                    ExtentI.attachCatalinaLogsForSuccess() ;
                } else {
                	Assertion.assertContainsEquals(InvalidPinMessage, actualMessage);
                    ExtentI.Markup(ExtentColor.RED, "C2C Withdrawl Failed Displays wrong message as " + InvalidPinMessage);
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                String errorMEssageForFailure = transfers.getErrorMessageForFailure();
                ExtentI.Markup(ExtentColor.RED, "C2C Withdrawl Failure Failed");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        } else {
            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname);
    }
	
	
	
    
//    public void performC2CTransferCheckAmountIsEqualGrandTotal(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
//        final String methodname = "performC2CTransfer";
//        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
//        String MasterSheetPath = _masterVO.getProperty("DataProvider");
//        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
//        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
//        Log.info("LOGINID : "+loginID ) ;
//        login.UserLogin(driver, "ChannelUser", FromCategory);
//        RandomGeneration RandomGeneration = new RandomGeneration();
//        String transferID = null, transferStatus = null, trf_status = null;
//
//        if (!transfers.isC2CVisible()) {
//            transfers.clickC2CTransactionHeading();
//            Log.info("C2C Transfer heading is clicked");
//        } else {
//            transfers.clickC2CHeading();
//            transfers.clickC2CTransactionHeading();
//            Log.info("C2C Heading and Transaction Heading is clicked");
//        }
//        transfers.clickC2CSingleOperationHeading();
//        transfers.clickSingleTransferHeading();
//        transfers.clickEtopup();
//        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
//        transfers.enterC2CMsisdn(toMSISDN);
//        transfers.clickC2CProceed();
//        String totalC2CTransferAmount = transfers.enterQuantityforC2CRevamp() ;
//        RandomGeneration randomGeneration = new RandomGeneration();
//        transfers.enterRefNum(randomGeneration.randomNumeric(6));
//        transfers.enterRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
//        transfers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
//        transfers.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
//
//        transfers.enterPaymentInstDate(transfers.getDateMMDDYY());
//        transfers.clickC2CTransferSubmitButton();
//
//        boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
//        if (PINPopUP == true) {
//            String grandTotalAmount = transfers.checkAmountIsEqualsGrandTotal() ;
//            if (totalC2CTransferAmount.equals(grandTotalAmount)) {
//                    ExtentI.Markup(ExtentColor.GREEN, "C2C Transfer Initiated : " );
//                    transfers.printC2CTransferTransactionID();
//                    ExtentI.attachCatalinaLogsForSuccess() ;
//                    transfers.clickC2CTransferRequestDoneButton();
//                }
//             else {
//                String errorMEssageForFailure = transfers.getErrorMessageForFailure();
//                ExtentI.Markup(ExtentColor.RED, "Amounts do no match");
//                ExtentI.attachCatalinaLogs();
//                ExtentI.attachScreenShot();
//            }
//        } else {
//            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
//            ExtentI.attachCatalinaLogs();
//            ExtentI.attachScreenShot();
//        }
//        Log.methodExit(methodname);
//    }



/*

    public void performC2CTransferBlankBatchName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "C2CTransferBlankBatchName";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);
        transfers.clickSubmitButtonC2CTransfer();
        String errorMessageCaptured = transfers.batchNameErrorMessages();
        String expectedMessage = "Batch Name is required";
        boolean flag = false;
        if (expectedMessage.equals(errorMessageCaptured)) {
            flag = true;
            Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Blank Batch Name not displayed on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname);
    }


    public void performC2CTransferWithoutUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "C2CTransferWithoutUpload";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);

        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
        transfers.clickSubmitButtonC2CTransfer() ;
        List<WebElement> errorMessageCaptured = transfers.validationErrorsOnGUI() ;
        String actualMessage = null;
        String expectedMessage = "File is required" ;
        boolean flag = false;
        for (WebElement ele : errorMessageCaptured) {
            actualMessage = ele.getText();
            if (expectedMessage.equals(actualMessage)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "C2C is successful without uploading file");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }


    public void performC2CTransferResetButton(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransferResetButton";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);

        transfers.clickDownloadUserListIcon();
        transfers.clickDownloadBlankUserTemplateIcon();
        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
        transfers.clickResetButton();
        transfers.clickSubmitButtonC2CTransfer();

        Boolean blankCategory = transfers.getblankCategory();
        String blankBatchName = transfers.getblankBatchName();

        Boolean checkBatchName = blankBatchName.equals("");
        if(!blankCategory&&checkBatchName)
        {
            ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else{
            currentNode.log(Status.FAIL, "Fields are not blank hence Reset button failed.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }


    public void performC2CTransferDownloadUserListWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "C2CTransferDownloadUserListWithoutCategory";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.clickDownloadUserListIcon() ;
        List<WebElement> errorMessageCaptured = transfers.validationErrorsOnGUI() ;
        String actualMessage = null;
        String expectedMessage =  "User Category is required." ;
        boolean flag = false;
        for (WebElement ele : errorMessageCaptured) {
            actualMessage = ele.getText();
            if (expectedMessage.equals(actualMessage)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            Assertion.assertContainsEquals(actualMessage, expectedMessage);
            ExtentI.Markup(ExtentColor.GREEN, "User List Downloaded Failed Without Category");
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            ExtentI.Markup(ExtentColor.GREEN, "User List Downloaded Without Category");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }



    public void performC2CTransferDownloadTemplateWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "C2CTransferDownloadTemplateWithoutCategory";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        String downloadDirPath = _masterVO.getProperty("C2CTransferPath") ;
        int NoOfFilesBefore = transfers.noOfFilesInDownloadedDirectory(downloadDirPath) ;

        transfers.clickDownloadBlankUserTemplateIcon();

        int NoOfFilesAfter = transfers.noOfFilesInDownloadedDirectory(downloadDirPath) ;

        Log.info("No Of Files Before Download = "+NoOfFilesBefore) ;
        Log.info("No Of Files After Download = "+NoOfFilesAfter) ;

        if(NoOfFilesBefore < NoOfFilesAfter)
        {
            ExtentI.Markup(ExtentColor.GREEN,"Template File Downloaded Successfully") ;
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else
        {
            ExtentI.Markup(ExtentColor.RED,"Download Template File Failed") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        ExtentI.attachScreenShot();
        Log.methodExit(methodname) ;
    }


    public void performC2CTransferFileUploadType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) throws InterruptedException {
        final String methodname = "C2CTransferFileUploadType";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String PNGPath = System.getProperty("user.dir")+_masterVO.getProperty("PNGFile");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);
        transfers.enterBatchName(RandomGeneration.randomNumeric(10));
        transfers.uploadFile(PNGPath);

        String errorMessageCaptured = transfers.fileUploadTypeErrorMessage();
        String expectedMessage = "Only CSV,XLS & XLSX Files are allowed";
        if (expectedMessage.equals(errorMessageCaptured)) {
            Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "File Uploaded is not of type CSV,XLS or XLSX");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }


    public void performC2CTransferBlankPIN(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransferBlankPIN";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();

        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);
        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


        ExcelUtility.createBlankExcelFile(PathOfFile);

        String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;
        String uploadFileName = System.getProperty("user.dir")+_masterVO.getProperty("C2CTransferUpload") + latestFileName ;
        transfers.uploadFile(uploadFileName);
        transfers.clickSubmitButtonC2CTransfer();
        transfers.enterC2CPIN("") ;
        //Boolean confirmButtonVisible = transfers.clickPINSubmitButton() ;
        Boolean confirmButtonDisabled = transfers.clickPINSubmitButton() ;
        if(confirmButtonDisabled)
        {
            ExtentI.Markup(ExtentColor.GREEN, "Confirm PIN button is disabled for blank pin");
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else{
            ExtentI.Markup(ExtentColor.RED, "C2C PIN Confirm Button is not disabled successfully with blank pin");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }

    public void performC2CTransferBlankFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransferBlankPIN";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();

        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);
        transfers.clickDownloadUserListIcon();
        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


        int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

        if(noOfFiles > 0)
        {
            //	Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
            ExcelUtility.deleteFiles(PathOfFile) ;
        }
        ExcelUtility.createBlankExcelFile(PathOfFile+"C2CTransfer.xls") ;
        Log.info("User template download Failed, Created an Empty Excel File") ;

        String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

        String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CTransferUpload") + latestFileName ;
        //System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
        transfers.uploadFile(uploadPath);

        transfers.clickSubmitButtonC2CTransfer();

        transfers.enterC2CPIN(PIN) ;
        transfers.clickPINSubmitButton() ;
        String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
        Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);

        if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
            String reasonForFail = transfers.getErrorMessageForFailure();
            String actualMessage = "The number of errors in the file exceed the maximum number allowed";
            if (reasonForFail.equals(actualMessage)) {
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
                ExtentI.Markup(ExtentColor.GREEN, "C2C with Blank Excel Failed for reason : " + reasonForFail);
                ExtentI.attachCatalinaLogsForSuccess();
            } else {
                currentNode.log(Status.FAIL, "Invalid message given.");
                ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : " + reasonForFail);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else {
            currentNode.log(Status.FAIL, "C2C IS Successful with Blank Excel.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        Log.methodExit(methodname) ;
    }


    public void performC2CTransferInvalidMSISDN(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransferInvalidMSISDN";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        String transferID = "CT", actualMessage = null, trf_status = null;
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transaction heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);

        ExcelUtility.deleteFiles(PathOfFile) ;
        transfers.clickDownloadBlankUserTemplateIcon();


        String filePath = transfers.getLatestFilefromDir(PathOfFile);
        Log.info("File Path:" + filePath);

        String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;
        String FilePathAndName = PathOfFile + latestFileName ;

        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
        String MSISDN2 = RandomGeneration.randomNumeric(12);
        Log.info("Writing to excel ....");
        ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1);
        ExcelUtility.setCellDataXLS(MSISDN2,1, 0);
        ExcelUtility.setCellDataXLS("03",1,3);
        ExcelUtility.setCellDataXLS("ETOPUP",1,4);
        Log.info("Written to Excel : " + MSISDN2 + ", " + "03, ETOPUP");


        String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CTransferUpload") + latestFileName ;
        transfers.uploadFile(uploadPath);
        transfers.clickSubmitButtonC2CTransfer();
        transfers.enterPin(PIN);
        transfers.clickPINConfirmButton() ;

        String failReason = "No such user exists, MSISDN is invalid.";


        boolean successPopUP = transfers.successPopUPVisibility();
        if (successPopUP == true) {
            actualMessage = transfers.C2CFailReason();

            if (actualMessage.contains(failReason)) {
                ExtentI.Markup(ExtentColor.GREEN, "C2C Bulk Transfer Transaction fails due to invalid MSISDN, error message Found as: " + actualMessage);
                ExtentI.attachCatalinaLogsForSuccess();
            } else {
                currentNode.log(Status.FAIL, "Error message due to invalid MSISDN not shown. Transfer message on WEB: " + actualMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }

        } else {
            currentNode.log(Status.FAIL, "Error message due to invalid MSISDN not shown. Transfer message on WEB: " + actualMessage);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        Log.methodExit(methodname) ;
    }





    public void performC2CTransferInvalidHeaderFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransferInvaliDataFileUpload";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();

        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);
        transfers.clickDownloadUserListIcon();
        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


        int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

        if(noOfFiles > 0)
        {
            //	Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
            ExcelUtility.deleteFiles(PathOfFile) ;
        }

        transfers.clickDownloadBlankUserTemplateIcon() ;

        String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

        String FilePathAndName = PathOfFile + latestFileName ;
        ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
        for(int j = 0; j<ExcelUtility.getColumnCount() ; j++)
        {
            ExcelUtility.setCellDataXLS("Mobile", 0, j) ;
        }

        String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CTransferUpload") + latestFileName ;
//		System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
        transfers.uploadFile(uploadPath);

        transfers.clickSubmitButtonC2CTransfer();

        transfers.enterC2CPIN(PIN) ;
        transfers.clickPINSubmitButton() ;
        String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
        Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);

        String actualMessage = "Invalid column header given." ;
        if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
            String reasonForFail = transfers.getErrorMessageForFailure() ;
            if(reasonForFail.equals(actualMessage))
            {
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
                ExtentI.Markup(ExtentColor.GREEN, "C2C Failed with Invalid header, error on GUI : "+reasonForFail);
                ExtentI.attachCatalinaLogsForSuccess();
            }
            else
            {
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
                ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else {
            currentNode.log(Status.FAIL, "C2C IS Successful with Invalid Header.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        Log.methodExit(methodname) ;
    }




    public void performC2CTransferInvalidDataFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransferInvaliDataFileUpload";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();

        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);
        transfers.clickDownloadUserListIcon();
        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


        int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

        if(noOfFiles > 0)
        {
            //	Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
            ExcelUtility.deleteFiles(PathOfFile) ;
        }
        else
        {
            ExcelUtility.createBlankExcelFile(PathOfFile+"C2CTransfer.xls") ;
            Log.info("User template download Failed, Created an Empty Excel File") ;
        }
        transfers.clickDownloadBlankUserTemplateIcon() ;

        String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

        String FilePathAndName = PathOfFile + latestFileName ;
        ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
        for(int i = 1; i<=2; i++)
        {
            for(int j=0; j < ExcelUtility.getColumnCount() ; j++ )
            {
                ExcelUtility.setCellDataXLS("aaa", i, j) ;
            }
        }

        String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CTransferUpload") + latestFileName ;
        transfers.uploadFile(uploadPath);

        transfers.clickSubmitButtonC2CTransfer();

        transfers.enterC2CPIN(PIN) ;
        transfers.clickPINSubmitButton() ;
        String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
        Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);
        if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
            String reasonForFail = transfers.getErrorMessageForFailure()  ;
            String actualMessage = "ALL records contain error.Kindly check logs." ;
            if(reasonForFail.equals(actualMessage))
            {
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
                ExtentI.Markup(ExtentColor.GREEN, "C2C with Blank Excel Failed for reason : "+reasonForFail);
                ExtentI.attachCatalinaLogsForSuccess();
            }
            else
            {
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
                ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }

        }
        else {
            currentNode.log(Status.FAIL, "C2C IS Successful with Blank Excel.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        Log.methodExit(methodname) ;
    }

    public void performC2CTransferBlankDataFileWithHeaderUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransferBlankPIN";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();

        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(ToCategory);
        transfers.clickDownloadUserListIcon();
        transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


        int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

        if(noOfFiles > 0)
        {
            ExcelUtility.deleteFiles(PathOfFile) ;
        }
        transfers.clickDownloadBlankUserTemplateIcon() ;

        String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

        String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CTransferUpload") + latestFileName ;
        transfers.uploadFile(uploadPath);

        transfers.clickSubmitButtonC2CTransfer();

        transfers.enterC2CPIN(PIN) ;
        transfers.clickPINSubmitButton() ;
        String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
        Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);
        String actualMessage = "No record available." ;
        if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
            String reasonForFail = transfers.getErrorMessageForFailure() ;
            if(reasonForFail.equals(actualMessage))
            {
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
                ExtentI.Markup(ExtentColor.GREEN, "C2C Failed with Blank data, error on GUI : "+reasonForFail);
                ExtentI.attachCatalinaLogsForSuccess();
            }
            else
            {
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
                ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }
        else {
            currentNode.log(Status.FAIL, "C2C IS Successful with Blank Data.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        Log.methodExit(methodname) ;
    }


    public void performC2CTransferSymbolBatchName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "C2CTransferSymbolBatchName";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        if(!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();

        }
        else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();

        }
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        transfers.selectCategory(FromCategory);
        transfers.clickDownloadUserListIcon();
        transfers.isFileDownloaded();
        transfers.clickDownloadBlankUserTemplateIcon();
        transfers.enterBatchName("%$#@^&");
        transfers.clickSubmitButtonC2CTransfer();
        String errorMessageCaptured = transfers.batchNameErrorMessages();
        String expectedMessage = "Batch Name is invalid";
        if (expectedMessage.equals(errorMessageCaptured)) {
            Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        } else {
            currentNode.log(Status.FAIL, "Batch name entered is Invalid or Symbolic");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }







    public void performC2CTransferBlankProduct(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "C2CTransferBlankProduct";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", FromCategory);
        transfers.clickC2CHeading();
        transfers.clickC2CTransactionHeading();
        transfers.clickC2CBulkOperationHeading();
        transfers.clickBulkTransferHeading();
        String errorMessageCaptured = transfers.blankCategoryMessages();
        Log.info("errorMessageCaptured : " + errorMessageCaptured);
        String expectedMessage = "Product is required.";
        if (expectedMessage.equals(errorMessageCaptured)) {
            Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
            ExtentI.attachCatalinaLogsForSuccess();
        }
        else {
            currentNode.log(Status.FAIL, "Blank Category Error not displayed on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodname) ;
    }




*/















/*
    CHECK DATE FORMAT
     public void performC2CTransferMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
        final String methodname = "performC2CTransfer";
        Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
        Log.info("LOGINID : "+loginID ) ;
        login.UserLogin(driver, "ChannelUser", FromCategory);
        RandomGeneration RandomGeneration = new RandomGeneration();
        String transferID = null, transferStatus = null, trf_status = null;

        if (!transfers.isC2CVisible()) {
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Transfer heading is clicked");
        } else {
            transfers.clickC2CHeading();
            transfers.clickC2CTransactionHeading();
            Log.info("C2C Heading and Transaction Heading is clicked");
        }
        transfers.clickC2CSingleOperationHeading();
        transfers.clickSingleTransferHeading();
        transfers.clickEtopup();

        transfers.selectC2CBuyerType(_masterVO.getProperty("C2CMobileBuyerType")) ;
        transfers.enterC2CMsisdn(toMSISDN);
        transfers.clickC2CProceed();
        transfers.enterQuantityforC2CRevamp() ;
        RandomGeneration randomGeneration = new RandomGeneration();
        transfers.enterRefNum(randomGeneration.randomNumeric(6));
        transfers.enterRemarks("Remarks entered for C2C to: " + ToCategory + " User : " + toMSISDN) ;
        transfers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
        transfers.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
        String dateFormatInDB = DBHandler.AccessHandler.getSystemPreference(ExcelI.DATE_FORMAT_CAL_JAVA) ;

        String dateOnGUI = transfers.getDateOnGUI() ;
        Boolean dateFormatsEqual = transfers.checkDateFormats(dateFormatInDB, dateOnGUI) ;
        if(dateFormatsEqual)
        {
            Log.info("Date Formats in DB are same") ;
            transfers.enterPaymentInstDate(transfers.getDateMMDDYY());
            transfers.clickC2CTransferSubmitButton();

            boolean PINPopUP = transfers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
            if (PINPopUP == true) {
                transfers.enterC2CUserPIN(PIN);
                transfers.clickC2CTransferSubmitButtonAfterEnteringPIN();
                boolean C2CTransferInitiatedPopup = transfers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
                if (C2CTransferInitiatedPopup == true) {
                    String actualMessage = "Transfer Request Initiated";
                    String C2CTransferResultMessage = transfers.getC2CTransferTransferRequestInitiatedMessage();
                    if (actualMessage.equals(C2CTransferResultMessage)) {
                        transfers.printC2CTopupsInitiatedAmounts();
                        ExtentI.Markup(ExtentColor.GREEN, "C2C Transfer Initiated : " + C2CTransferResultMessage);
                        transfers.printC2CTransferTransactionID();
                        ExtentI.attachCatalinaLogsForSuccess() ;
                        transfers.clickC2CTransferRequestDoneButton();
                    } else {
                        ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Displays wrong message as " + C2CTransferResultMessage);
                        ExtentI.attachCatalinaLogs();
                        ExtentI.attachScreenShot();
                    }
                } else {
                    String errorMEssageForFailure = transfers.getErrorMessageForFailure();
                    ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Failed");
                    ExtentI.attachCatalinaLogs();
                    ExtentI.attachScreenShot();
                }
            } else {
                ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot();
            }
        }else
        {
            ExtentI.Markup(ExtentColor.RED, "DATE FORMAT INCORRECT on GUI");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }

        Log.methodExit(methodname);
    }


 */

}


