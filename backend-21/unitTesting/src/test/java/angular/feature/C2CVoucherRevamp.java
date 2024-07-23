package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.c2cvouchertransfer.Vouchers ;
import restassuredapi.pojo.c2cvoucherapprovalresponsepojo.C2CVoucherApprovalResponsePojo;
import angular.pageobjects.c2ctransfer.C2CTransfers ;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;


public class C2CVoucherRevamp extends BaseTest {

	static String PathOfFile = _masterVO.getProperty("C2CBulkTransferPath") ;
	String arr[] ;

	public WebDriver driver;
	LoginRevamp login;
	C2CTransfers transfers;
	Vouchers vouchers ;


	public C2CVoucherRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		transfers = new C2CTransfers(driver);
		vouchers = new Vouchers(driver) ;
	}

	public void performC2CVoucherApproval(String txnid,String category) {
		  if(!vouchers.isC2CVisible()) {
  			vouchers.clickC2CApproval1Heading();
  			Log.info("C2C Approval 1 heading is clicked");
  		}
  		else {
  			vouchers.clickC2CHeading();
  			vouchers.clickC2CApproval1Heading();
  			Log.info("C2C Heading and Approval 1 is clicked");
  		}
		  
		  vouchers.clickVoucherToggle();
         
          vouchers.enterTransactionId(txnid);
          vouchers.clickApprove();
          try {
  			Thread.sleep(1000);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
          vouchers.clickonApproveButton();
          try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          vouchers.clickonApproveButton();
        

          vouchers.clickonYes();
       
          boolean C2CTransferVoucherApproval = vouchers.C2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
          if(C2CTransferVoucherApproval==true) {
        	  String actualMessage = "Transaction Approved";
              String C2CvoucherTransferApprovalResultMessage = vouchers.getC2CTransfervoucherApprovalMessage();
              if (actualMessage.equals(C2CvoucherTransferApprovalResultMessage)) {
                  
                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Approval :" + C2CvoucherTransferApprovalResultMessage);
                  ExtentI.attachScreenShot();
                  vouchers.clickApproveDone();
                  
              }
              else {
            	  ExtentI.Markup(ExtentColor.RED, "Voucher Approval Displays wrong message as " + C2CvoucherTransferApprovalResultMessage);
                  ExtentI.attachCatalinaLogs();
                  ExtentI.attachScreenShot(); 
              }
          }
          else {
        	  ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Approval Failed");
              ExtentI.attachCatalinaLogs();
              ExtentI.attachScreenShot();
        	  
          }
	}
	
	public void performC2CVoucherApproval2(String txnid,String category ) {
		  if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CApproval2Heading();
			Log.info("C2C Approval 2 heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CApproval2Heading();
			Log.info("C2C Heading and Approval 2 is clicked");
		}
        vouchers.clickVoucherToggle();
        vouchers.enterTransactionId(txnid);
        vouchers.clickApprove();
        try {
  			Thread.sleep(1000);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
          vouchers.clickonApproveButton();
          try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          vouchers.clickonApproveButton();
        vouchers.clickonYes();
     
        boolean C2CTransferVoucherApproval = vouchers.C2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
        if(C2CTransferVoucherApproval==true) {
      	  String actualMessage = "Transaction Approved";
            String C2CvoucherTransferApprovalResultMessage = vouchers.getC2CTransfervoucherApprovalMessage();
            if (actualMessage.equals(C2CvoucherTransferApprovalResultMessage)) {
                
                ExtentI.Markup(ExtentColor.GREEN, "Voucher Approval :" + C2CvoucherTransferApprovalResultMessage);
                ExtentI.attachScreenShot();
                vouchers.clickApproveDone();
                
            }
            else {
          	  ExtentI.Markup(ExtentColor.RED, "Voucher Approval Displays wrong message as " + C2CvoucherTransferApprovalResultMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot(); 
            }
        }
        else {
      	  ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Approval Failed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
      	  
        }
	}
	public void performC2CVoucherApproval3(String txnid,String category) {
		  if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CApproval3Heading();
			Log.info("C2C Approval 3 heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CApproval3Heading();
			Log.info("C2C Heading and Approval 3 is clicked");
		}
		 
        vouchers.clickVoucherToggle();
       
        vouchers.enterTransactionId(txnid);
        vouchers.clickApprove();
        try {
  			Thread.sleep(1000);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
          vouchers.clickonApproveButton();
          try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          vouchers.clickonApproveButton();

        vouchers.clickonYes();
     
        boolean C2CTransferVoucherApproval = vouchers.C2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
        if(C2CTransferVoucherApproval==true) {
      	  String actualMessage = "Transaction Approved";
            String C2CvoucherTransferApprovalResultMessage = vouchers.getC2CTransfervoucherApprovalMessage();
            if (actualMessage.equals(C2CvoucherTransferApprovalResultMessage)) {
                
                ExtentI.Markup(ExtentColor.GREEN, "Voucher Approval :" + C2CvoucherTransferApprovalResultMessage);
                ExtentI.attachScreenShot();
                vouchers.clickApproveDone();
                
            }
            else {
          	  	ExtentI.Markup(ExtentColor.RED, "Voucher Request Initiated Displays wrong message as " + C2CvoucherTransferApprovalResultMessage);
                ExtentI.attachCatalinaLogs();
                ExtentI.attachScreenShot(); 
            }
        }
        else {
      	  	ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Approval Failed");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
      	  
        }
	}
	
	public void performC2CVoucherTransfer(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CBulkTransfer";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				String username = DBHandler.AccessHandler.getUsernameFromMsisdn(toMSISDN);
				String userid= DBHandler.AccessHandler.getUserId(username);
				
				String fromSerialNumber = DBHandler.AccessHandler.getMaxSerialNumberWithuserid(productID, status, userid);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDate(vouchers.getDateMMDDYY());
				vouchers.clicktransferButton();
				
				

				boolean PINPopUP = vouchers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
			        if (PINPopUP == true) {
			            vouchers.enterC2CUserPIN(PIN);
			            vouchers.clicksubmitButton();
			            boolean C2CVoucherTransferInitiatedPopup = vouchers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
			            if (C2CVoucherTransferInitiatedPopup == true) {
			                String actualMessage = "Voucher Request Initiated";
			                String C2CvoucherTransferResultMessage = vouchers.getC2CTransferTransferRequestInitiatedMessage();
			                if (actualMessage.equals(C2CvoucherTransferResultMessage)) {
			                  
			                    ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + C2CvoucherTransferResultMessage);
			                  
			                    ExtentI.attachScreenShot();
			                    String txnId =vouchers.printC2CTransferTransactionID();
			                    vouchers.clickC2CTransferRequestDoneButton();
			                    
			                    

			                    String value = DBHandler.AccessHandler.getPreference(categorCode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
			            		int maxApprovalLevel = Integer.parseInt(value);
			            		
			            		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, ToCategory, EventsI.C2CTRANSFER_EVENT)) {

			            			 if(BTSLUtil.isNullString(value)) {
			            	            	Log.info("C2C Approval level is not Applicable");
			            	        		}
			            	            else {
			            	            	if(maxApprovalLevel == 0)
			            	        		{
			            	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
			            	            		
			            	        		}
			            	            	
			            	            	if(maxApprovalLevel >= 1)
			            	        		{
			            	            		performC2CVoucherApproval(txnId, ToCategory);
			            	        		
			            	            		Log.info("Level 1 Success !!");
			            	        		}
			            	            	if(maxApprovalLevel >= 2)
			            	        		{
			            	            		
			            	            		performC2CVoucherApproval2(txnId, ToCategory);
			            	            		
			            	            		Log.info("Level 2 Success !!");
			            	            	}
			            	            	
			            	            	if(maxApprovalLevel == 3)
			            	        		{
			            	            		
			            	            		performC2CVoucherApproval3(txnId, ToCategory);
			            	            	
			            	            		Log.info("Level 3 Success !!");
			            	            	
			            	        		}	     
			            	           }
			            		}
			                    else {
			                        Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + ToCategory + "]");
			                    }
			                  
			                    
			                } else {
			                    ExtentI.Markup(ExtentColor.RED, "Voucher Request Initiated Displays wrong message as " + C2CvoucherTransferResultMessage);
			                    ExtentI.attachCatalinaLogs();
			                    ExtentI.attachScreenShot();
			                }
			            } else {
			               
			                ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Failed");
			                ExtentI.attachCatalinaLogs();
			                ExtentI.attachScreenShot();
			            }
			        } else {
			        	vouchers.clickTryAgain();
			            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
			            ExtentI.attachCatalinaLogs();
			            ExtentI.attachScreenShot();
			            Log.methodExit(methodname) ;
			        }
				
			}
			else if (arr[i] == "User Name")
			{
				buyerData = userName ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDate(vouchers.getDateMMDDYY());
				vouchers.clicktransferButton();
				
				String expectedmessage = "Bulk Transfer Request Initiated";

				  boolean PINPopUP = vouchers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
			        if (PINPopUP == true) {
			            vouchers.enterC2CUserPIN(PIN);
			            vouchers.clicksubmitButton();
			            boolean C2CVoucherTransferInitiatedPopup = vouchers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
			            if (C2CVoucherTransferInitiatedPopup == true) {
			                String actualMessage = "Voucher Request Initiated";
			                String C2CvoucherTransferResultMessage = vouchers.getC2CTransferTransferRequestInitiatedMessage();
			                if (actualMessage.equals(C2CvoucherTransferResultMessage)) {
			                  
			                    ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + C2CvoucherTransferResultMessage);
			                  
			                    ExtentI.attachCatalinaLogsForSuccess() ;
			                    String txnId =vouchers.printC2CTransferTransactionID();
			                    vouchers.clickC2CTransferRequestDoneButton();
			                    String value = DBHandler.AccessHandler.getPreference(categorCode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
			            		int maxApprovalLevel = Integer.parseInt(value);
			            		
			            		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, ToCategory, EventsI.C2CTRANSFER_EVENT)) {

			            			 if(BTSLUtil.isNullString(value)) {
			            	            	Log.info("C2C Approval level is not Applicable");
			            	        		}
			            	            else {
			            	            	if(maxApprovalLevel == 0)
			            	        		{
			            	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
			            	            		
			            	        		}
			            	            	
			            	            	if(maxApprovalLevel >= 1)
			            	        		{
			            	            		performC2CVoucherApproval(txnId, ToCategory);
			            	        		
			            	            		Log.info("Level 1 Success !!");
			            	        		}
			            	            	if(maxApprovalLevel >= 2)
			            	        		{
			            	            		
			            	            		performC2CVoucherApproval2(txnId, ToCategory);
			            	            		
			            	            		Log.info("Level 2 Success !!");
			            	            	}
			            	            	
			            	            	if(maxApprovalLevel == 3)
			            	        		{
			            	            		
			            	            		performC2CVoucherApproval3(txnId, ToCategory);
			            	            	
			            	            		Log.info("Level 3 Success !!");
			            	            	
			            	        		}	     
			            	           }
			            		}
			                    else {
			                        Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + ToCategory + "]");
			                    }
			                  
			                    
			                } else {
			                    ExtentI.Markup(ExtentColor.RED, "Voucher Request Initiated Displays wrong message as " + C2CvoucherTransferResultMessage);
			                    ExtentI.attachCatalinaLogs();
			                    ExtentI.attachScreenShot();
			                }
			            } else {
			               
			                ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Failed");
			                ExtentI.attachCatalinaLogs();
			                ExtentI.attachScreenShot();
			            }
			        } else {
			        	vouchers.clickTryAgain();
			            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
			            ExtentI.attachCatalinaLogs();
			            ExtentI.attachScreenShot();
			            Log.methodExit(methodname) ;
			        }
			        
			}
			else if (arr[i] == "Login Id")
			{
				buyerData = loginID ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDate(vouchers.getDateMMDDYY());
				vouchers.clicktransferButton();
				
				String expectedmessage = "Bulk Transfer Request Initiated";

				  boolean PINPopUP = vouchers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
			        if (PINPopUP == true) {
			            vouchers.enterC2CUserPIN(PIN);
			            vouchers.clicksubmitButton();
			            boolean C2CVoucherTransferInitiatedPopup = vouchers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
			            if (C2CVoucherTransferInitiatedPopup == true) {
			                String actualMessage = "Voucher Request Initiated";
			                String C2CvoucherTransferResultMessage = vouchers.getC2CTransferTransferRequestInitiatedMessage();
			                if (actualMessage.equals(C2CvoucherTransferResultMessage)) {
			                  
			                    ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + C2CvoucherTransferResultMessage);
			                  
			                    ExtentI.attachCatalinaLogsForSuccess() ;
			                    String txnId =vouchers.printC2CTransferTransactionID();
			                    vouchers.clickC2CTransferRequestDoneButton();
			                    String value = DBHandler.AccessHandler.getPreference(categorCode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
			            		int maxApprovalLevel = Integer.parseInt(value);
			            		
			            		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, ToCategory, EventsI.C2CTRANSFER_EVENT)) {

			            			 if(BTSLUtil.isNullString(value)) {
			            	            	Log.info("C2C Approval level is not Applicable");
			            	        		}
			            	            else {
			            	            	if(maxApprovalLevel == 0)
			            	        		{
			            	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
			            	            		
			            	        		}
			            	            	
			            	            	if(maxApprovalLevel >= 1)
			            	        		{
			            	            		performC2CVoucherApproval(txnId, ToCategory);
			            	        		
			            	            		Log.info("Level 1 Success !!");
			            	        		}
			            	            	if(maxApprovalLevel >= 2)
			            	        		{
			            	            		
			            	            		performC2CVoucherApproval2(txnId, ToCategory);
			            	            		
			            	            		Log.info("Level 2 Success !!");
			            	            	}
			            	            	
			            	            	if(maxApprovalLevel == 3)
			            	        		{
			            	            		
			            	            		performC2CVoucherApproval3(txnId, ToCategory);
			            	            	
			            	            		Log.info("Level 3 Success !!");
			            	            	
			            	        		}	     
			            	           }
			            		}
			                    else {
			                        Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + ToCategory + "]");
			                    }
			                  
			                    
			                } else {
			                    ExtentI.Markup(ExtentColor.RED, "Voucher Request Initiated Displays wrong message as " + C2CvoucherTransferResultMessage);
			                    ExtentI.attachCatalinaLogs();
			                    ExtentI.attachScreenShot();
			                }
			            } else {
			               
			                ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Failed");
			                ExtentI.attachCatalinaLogs();
			                ExtentI.attachScreenShot();
			            }
			        } else {
			        	vouchers.clickTryAgain();
			            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
			            ExtentI.attachCatalinaLogs();
			            ExtentI.attachScreenShot();
			            Log.methodExit(methodname) ;
			        }
			}


		}
		
		
		Log.methodExit(methodname) ;

	}




	public void performC2CVoucherTransferBlankSearchBuyerAndMsisdn(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp){
		final String methodname = "performC2CVoucherTransferBlankSearchBuyerAndMsisdn";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
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
		vouchers.clickVoucherButton() ;
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankSearchBuyerMessage();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Select Search Creteria First." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
		currentNode.log(Status.FAIL, "Blank Search Buyer Error Message not displayed on GUI");
		ExtentI.attachCatalinaLogs();
		ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}


	public void performC2CVoucherTransferBlankMSISDNWithBuyerMobile(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferBlankMSISDNWithBuyerMobile";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
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
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Mobile Number") ;
		vouchers.enterbuyerDetails("") ;
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankMsisdnMessage();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Mobile number is required." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Mobile Number Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}


	public void performC2CVoucherTransferBlankCategoryWithUserName(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferBlankCategoryWithUserName";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
		
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("User Name") ;
		vouchers.enterbuyerDetailsuser(userName);
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankUserCategoryMessage();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "User Category is required." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Category Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}

	public void performC2CVoucherTransferBlankCategoryWithLoginID(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp){
		final String methodname = "performC2CVoucherTransferBlankCategoryWithLoginID";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
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
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Login Id") ;
		vouchers.enterbuyerDetailsuser(loginID);
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankUserCategoryMessage(); 
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "User Category is required." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Category Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}

	public void performC2CVoucherTransferResetFields(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferBlankMSISDN";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
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
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Login Id") ;
		vouchers.selectCategoryDrop(ToCategory);
		vouchers.enterbuyerDetailsuser(loginID);
		
		vouchers.clickResetButton();
		boolean isblank = vouchers.isreset();
		
		
		if (isblank) {
			currentNode.log(Status.PASS, "Fields are Reset");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Category Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}




























	public void performC2CBulkTransferBlankBatchName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferBlankBatchName";
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
		transfers.clickSubmitButtonC2CBulkTransfer();
		String errorMessageCaptured = transfers.batchNameErrorMessages();
		String expectedMessage = "Batch Name is required";
		boolean flag = false;
		if (expectedMessage.equals(errorMessageCaptured)) {
			flag = true;
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Blank Batch Name not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}


	public void performC2CBulkTransferWithoutUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferWithoutUpload";
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

		//transfers.clickDownloadUserListIcon();
		//transfers.clickDownloadBlankUserTemplateIcon();
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
		transfers.clickSubmitButtonC2CBulkTransfer() ;
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


	public void performC2CBulkTransferResetButton(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkTransferResetButton";
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
		transfers.clickSubmitButtonC2CBulkTransfer();

		Boolean blankCategory = transfers.getblankCategory();
		String blankBatchName = transfers.getblankBatchName();

		Boolean checkBatchName = blankBatchName.equals("");
		if(!blankCategory&&checkBatchName)
		{
			ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else{
			currentNode.log(Status.FAIL, "Fields are not blank hence Reset button failed.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}


	public void performC2CBulkTransferDownloadUserListWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferDownloadUserListWithoutCategory";
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
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.GREEN, "User List Downloaded Without Category");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}



	public void performC2CBulkTransferDownloadTemplateWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferDownloadTemplateWithoutCategory";
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
		String downloadDirPath = _masterVO.getProperty("C2CBulkTransferPath") ;
		int NoOfFilesBefore = transfers.noOfFilesInDownloadedDirectory(downloadDirPath) ;

		transfers.clickDownloadBlankUserTemplateIcon();

		int NoOfFilesAfter = transfers.noOfFilesInDownloadedDirectory(downloadDirPath) ;

		Log.info("No Of Files Before Download = "+NoOfFilesBefore) ;
		Log.info("No Of Files After Download = "+NoOfFilesAfter) ;

		if(NoOfFilesBefore < NoOfFilesAfter)
		{
			ExtentI.Markup(ExtentColor.GREEN,"Template File Downloaded Successfully") ;
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else
		{
			ExtentI.Markup(ExtentColor.RED,"Download Template File Failed") ;
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		/*Boolean categoryErrorDisplayed = transfers.checkCategoryErrorOnGUI() ;
		if(!categoryErrorDisplayed)
		{
			currentNode.log(Status.PASS,"Category required error not displayed to download template.");
			ExtentI.attachCatalinaLogsForSuccess();
		}
		else
		{
			currentNode.log(Status.FAIL, "Category is required to download Template");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}*/
		ExtentI.attachScreenShot();
		Log.methodExit(methodname) ;
	}


	public void performC2CBulkTransferFileUploadType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) throws InterruptedException {
		final String methodname = "C2CBulkTransferFileUploadType";
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
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "File Uploaded is not of type CSV,XLS or XLSX");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}


	public void performC2CBulkTransferBlankPIN(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkTransferBlankPIN";
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
		String uploadFileName = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkTransferUpload") + latestFileName ;
		transfers.uploadFile(uploadFileName);
		transfers.clickSubmitButtonC2CBulkTransfer();
		transfers.enterC2CPIN("") ;
		Boolean confirmButtonVisible = transfers.clickPINSubmitButton() ;
		if(confirmButtonVisible)
		{
			ExtentI.Markup(ExtentColor.GREEN, "Confirm PIN button is available with blank pin");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();

		}
		else{
			ExtentI.Markup(ExtentColor.RED, "C2C PIN Confirm Button is clicked successfully with blank pin");
			Log.info("C2C PIN Confirm Button is clicked successfully with blank pin") ;
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}

	public void performC2CBulkTransferBlankFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkTransferBlankPIN";
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
		ExcelUtility.createBlankExcelFile(PathOfFile+"C2CBulkTransfer.xls") ;
		Log.info("User template download Failed, Created an Empty Excel File") ;

		String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkTransferUpload") + latestFileName ;
		//System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

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
				ExtentI.attachScreenShot();
			} else {
				currentNode.log(Status.FAIL, "Invalid message given.");
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : " + reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
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


	public void performC2CBulkTransferInvalidMSISDN(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkTransferInvalidMSISDN";
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


		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkTransferUpload") + latestFileName ;
		transfers.uploadFile(uploadPath);
		transfers.clickSubmitButtonC2CBulkTransfer();
		transfers.enterPin(PIN);
		transfers.clickPINConfirmButton() ;

		String failReason = "No such user exists, MSISDN is invalid.";


		boolean successPopUP = transfers.successPopUPVisibility();
		if (successPopUP == true) {
			actualMessage = transfers.C2CFailReason();

			if (actualMessage.contains(failReason)) {
				ExtentI.Markup(ExtentColor.GREEN, "C2C Bulk Transfer Transaction fails due to invalid MSISDN, error message Found as: " + actualMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
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





	public void performC2CBulkTransferInvalidHeaderFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkTransferInvaliDataFileUpload";
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

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkTransferUpload") + latestFileName ;
//		System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

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
				ExtentI.attachScreenShot();
			}
			else
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
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




	public void performC2CBulkTransferInvalidDataFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkTransferInvaliDataFileUpload";
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
			ExcelUtility.createBlankExcelFile(PathOfFile+"C2CBulkTransfer.xls") ;
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

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkTransferUpload") + latestFileName ;
	//	System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

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
				ExtentI.attachScreenShot();
			}
			else
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
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

	public void performC2CBulkTransferBlankDataFileWithHeaderUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkTransferBlankPIN";
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

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkTransferUpload") + latestFileName ;
		//System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

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
				ExtentI.attachScreenShot();
			}
			else
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
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


	public void performC2CVoucherTransferBlankUserNameWithCategory(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp){
		final String methodname = "performC2CVoucherTransferBlankUserNameWithCategory";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
		
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("User Name") ;
		vouchers.selectCategoryDrop(ToCategory);
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankUserNameMessage();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Required" ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank User Name Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferInvalidMSISDNLength(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp){
		final String methodname = "performC2CVoucherTransferInvalidMSISDNLength";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
		
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Mobile Number") ;
		vouchers.enterbuyerDetails(new RandomGeneration().randomNumeric(18)) ;
		
		vouchers.clickProceedButton();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String errorMessageCaptured = vouchers.getInvalidMsisdnLengthMessage();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "MSISDN2 length should lie between 6 and 15." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Invalid Length Mobile Number Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferInvalidMSISDN(String FromCategory, String ToCategory, String toMSISDN,
			String PIN, String categorCode) {
		final String methodname = "performC2CVoucherTransferInvalidMSISDNLength";
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
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Mobile Number") ;
		vouchers.enterbuyerDetails(new RandomGeneration().randomAlphaNumeric(10)) ;
		
		vouchers.clickProceedButton();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String errorMessageCaptured = vouchers.getInvalidMsisdnLengthMessage();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "MSISDN2 is not numeric." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Invalid Mobile Number Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferWithBlankPaymentDate(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CBulkTransfer";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
			
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getPeymentDateError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Please Choose Date to proceed." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Payment Date Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;
				
			}

		}
		
		
		Log.methodExit(methodname) ;

		
	}


	public void performC2CVoucherTransferWithBlankPaymentType(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithBlankPaymentType";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getPeymentTypeError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Please Choose Payment mode." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Payment Type Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;

		
	}


	public void performC2CVoucherTransferWithBlankRemarks(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithBlankRemarks";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks("");
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getBlankRemarksError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Remarks Required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Remarks Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferWithBlankToSerial(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithBlankToSerial";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"User Name"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getBlankToSerialError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Voucher to SerialNo Required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank To Serial No. Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferWithInvalidToSerial(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		
		final String methodname = "performC2CVoucherTransferWithInvalidToSerial";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"User Name"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(new com.utils.RandomGeneration().randomAlphaNumeric(7));
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getBlankToSerialError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Please Enter Vaild to Serial Number." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Invalid To Serial No. Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
	}


	public void performC2CVoucherTransferWithInvalidFromSerial(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithInvalidFromSerial";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Login Id"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(new com.utils.RandomGeneration().randomAlphaNumeric(6));
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getBlankFromSerialError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Please Enter Vaild From Serial Number." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Invalid From Serial No. Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferWithBlankFromSerial(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithBlankFromSerial";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Login Id"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(new com.utils.RandomGeneration().randomAlphaNumeric(6));
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getBlankFromSerialError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Voucher from SerialNo Required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank From Serial No. Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferWithBlankDenomination(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithBlankDenomination";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Login Id"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getDenominationError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Voucher Denomination Required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Denomination Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferWithBlankSegment(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithBlankSegment";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"User Name"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				
				
				
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getSegmentError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Voucher segment is required" ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Segment Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
		
	}


	public void performC2CVoucherTransferWithBlankType(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferWithBlankType";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"User Name"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				vouchers.selectCategoryDrop(ToCategory);	
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetailsuser(buyerData) ;
				
				vouchers.clickproceedButton();
				
				
				String status="EN";
				
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.clicktransferButton();
				
				String errorMessageCaptured = vouchers.getTypeError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Voucher type is required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Segment Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;	
	}

	public void performC2CVoucherTransferandReject(String loginID, String password, String msisdn, String PIN, String ParenTName, String ToCategory, String categorCode,String toMSISDN,String Touser,String FromUser,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherTransferandReject";
		Log.methodEntry(methodname, FromUser, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromUser);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				String status="EN";
				String username = DBHandler.AccessHandler.getUsernameFromMsisdn(toMSISDN);
				String userid= DBHandler.AccessHandler.getUserId(username);
				
				
				String fromSerialNumber = DBHandler.AccessHandler.getMaxSerialNumberWithuserid(productID, status, userid);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
				vouchers.enterFromSerialNo(fromSerialNumber);
				vouchers.enterToSerialNo(toSerialNumber);
				vouchers.enterRemarks(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDate(vouchers.getDateMMDDYYYY());
				vouchers.clicktransferButton();
				
				

				boolean PINPopUP = vouchers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
			        if (PINPopUP == true) {
			            vouchers.enterC2CUserPIN(PIN);
			            vouchers.clicksubmitButton();
			            boolean C2CVoucherTransferInitiatedPopup = vouchers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
			            if (C2CVoucherTransferInitiatedPopup == true) {
			                String actualMessage = "Voucher Request Initiated";
			                String C2CvoucherTransferResultMessage = vouchers.getC2CTransferTransferRequestInitiatedMessage();
			                if (actualMessage.equals(C2CvoucherTransferResultMessage)) {
			                  
			                    ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + C2CvoucherTransferResultMessage);
			                  
			                    ExtentI.attachScreenShot();
			                    String txnId =vouchers.printC2CTransferTransactionID();
			                    vouchers.clickC2CTransferRequestDoneButton();
			                    
			                    

			                    String value = DBHandler.AccessHandler.getPreference(categorCode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
			            		int maxApprovalLevel = Integer.parseInt(value);
			            		
			            		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, ToCategory, EventsI.C2CTRANSFER_EVENT)) {

			            			 if(BTSLUtil.isNullString(value)) {
			            	            	Log.info("C2C Approval level is not Applicable");
			            	        		}
			            	            else {
			            	            	if(maxApprovalLevel == 0)
			            	        		{
			            	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
			            	            		
			            	        		}
			            	            	
			            	            	if(maxApprovalLevel >= 1)
			            	        		{
			            	            		performC2CVoucherReject(txnId, ToCategory,fromSerialNumber,toSerialNumber);
			            	        		
			            	            		Log.info("Level 1 Reject !!");
			            	        		}
			            	            	
			            	           }
			            		}
			                    else {
			                        Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + ToCategory + "]");
			                    }
			                  
			                    
			                } else {
			                    ExtentI.Markup(ExtentColor.RED, "Voucher Request Initiated Displays wrong message as " + C2CvoucherTransferResultMessage);
			                    ExtentI.attachCatalinaLogs();
			                    ExtentI.attachScreenShot();
			                }
			            } else {
			               
			                ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Failed");
			                ExtentI.attachCatalinaLogs();
			                ExtentI.attachScreenShot();
			            }
			        } else {
			        	vouchers.clickTryAgain();
			            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
			            ExtentI.attachCatalinaLogs();
			            ExtentI.attachScreenShot();
			            Log.methodExit(methodname) ;
			        }
				
			}
		

		}
		
		
		Log.methodExit(methodname) ;
		
	}

	private void performC2CVoucherReject(String txnId, String toCategory, String fromSerialNumber,
			String toSerialNumber) {
		  if(!vouchers.isC2CVisible()) {
	  			vouchers.clickC2CApproval1Heading();
	  			Log.info("C2C Approval 1 heading is clicked");
	  		}
	  		else {
	  			vouchers.clickC2CHeading();
	  			vouchers.clickC2CApproval1Heading();
	  			Log.info("C2C Heading and Approval 1 is clicked");
	  		}
	          vouchers.clickVoucherToggle();
	         
	          vouchers.enterTransactionId(txnId);
	          
	          vouchers.ClickReject();
	          vouchers.enterPaymentInstDateInitiate(vouchers.getDateMMDDYYYY());
	          vouchers.clickonReject();
	          vouchers.clickonYes();
	       
	          boolean C2CTransferVoucherApproval = vouchers.C2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
	          if(C2CTransferVoucherApproval==true) {
	        	  String actualMessage = "Transaction Rejected";
	              String C2CvoucherTransferApprovalResultMessage = vouchers.getC2CTransfervoucherApprovalMessage();
	              if (actualMessage.equals(C2CvoucherTransferApprovalResultMessage)) {
	                  
	                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Rejected :" + C2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachScreenShot();
	                  vouchers.clickApproveDone();
	                  
	              }
	              else {
	            	  ExtentI.Markup(ExtentColor.RED, "Voucher Reject Displays wrong message as " + C2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachCatalinaLogs();
	                  ExtentI.attachScreenShot(); 
	              }
	          }
	          else {
	        	  ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Reject Failed");
	              ExtentI.attachCatalinaLogs();
	              ExtentI.attachScreenShot();
	        	  
	          }
		
	}

	public void performC2CVoucherinitiate(String loginID, String categorCode, String msisdn, String PIN, String ToCategory, String FromCategory, String ParentName,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		
		
		final String methodname = "performC2CVoucherInitiate";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;
		
		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;

		String buyerData ;

		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = toMSISDN ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				vouchers.enterQuantity(_masterVO.getProperty("voucherQty"));
				vouchers.enterRemarksInit(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentModeInit(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDateInit(vouchers.getDateMMDDYY());
				vouchers.clickPurchaseButton();



 				boolean PINPopUP = vouchers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
				if (PINPopUP == true) {
					vouchers.enterC2CUserPIN(PIN);
					vouchers.clickpurchaseButton();
					boolean C2CVoucherTransferInitiatedPopup = vouchers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
					if (C2CVoucherTransferInitiatedPopup == true) {
						String actualMessage = "Voucher Request Initiated";
						String C2CvoucherTransferResultMessage = vouchers.getC2CTransferTransferRequestInitiatedMessage();
						if (actualMessage.equals(C2CvoucherTransferResultMessage)) {

							ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + C2CvoucherTransferResultMessage);

							ExtentI.attachScreenShot();
							String txnId =vouchers.printC2CPurchaseTransactionID();
							vouchers.clickC2CInitiateRequestDoneButton();



							String value = DBHandler.AccessHandler.getPreference(categorCode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
							int maxApprovalLevel = Integer.parseInt(value);

							if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, ToCategory, EventsI.C2CTRANSFER_EVENT)) {

								if(BTSLUtil.isNullString(value)) {
									Log.info("C2C Approval level is not Applicable");
								}
								else {
									if(maxApprovalLevel == 0)
									{
										Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");

									}

									if(maxApprovalLevel >= 1)
									{
										performC2CVoucherApprovalInitiate(txnId, ToCategory,activeProfile,toMSISDN);

										Log.info("Level 1 Success !!");
									}
									if(maxApprovalLevel >= 2)
									{

										performC2CVoucherApproval2(txnId, ToCategory);

										Log.info("Level 2 Success !!");
									}

									if(maxApprovalLevel == 3)
									{

										performC2CVoucherApproval3(txnId, ToCategory);

										Log.info("Level 3 Success !!");

									}
								}
							}
							else {
								Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + ToCategory + "]");
							}


						} else {
							ExtentI.Markup(ExtentColor.RED, "Voucher Request Initiated Displays wrong message as " + C2CvoucherTransferResultMessage);
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}
					} else {

						ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Failed");
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
				} else {
					vouchers.clickTryAgain();
					ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
					Log.methodExit(methodname) ;
				}

			}
			
		}


		Log.methodExit(methodname) ;
	}

	private void performC2CVoucherApprovalInitiate(String txnId, String toCategory,String activeProfile,String toMSISDN) {
		 
		final String methodname = "performC2CVoucherApprovalInitiate";
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", toCategory);
		
		
		
		if(!vouchers.isC2CVisible()) {
	  			vouchers.clickC2CApproval1Heading();
	  			Log.info("C2C Approval 1 heading is clicked");
	  		}
	  		else {
	  			vouchers.clickC2CHeading();
	  			vouchers.clickC2CApproval1Heading();
	  			Log.info("C2C Heading and Approval 1 is clicked");
	  		}
	          vouchers.clickVoucherToggle();
	          
	          String status="EN";
	          String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
	          String username = DBHandler.AccessHandler.getUsernameFromMsisdn(toMSISDN);
	          String userid= DBHandler.AccessHandler.getUserId(username);
			  String fromSerialNumber = DBHandler.AccessHandler.getMaxSerialNumberWithuserid(productID, status, userid);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
	          vouchers.enterTransactionId(txnId);
	          vouchers.clickApprove();
	          vouchers.ClickonEdit();
	          vouchers.enterFromSerialNumber(fromSerialNumber);
	          vouchers.enterToSerialNumber(toSerialNumber);
	          vouchers.clickCheck();
	        //  vouchers.enterPaymentInstDateInitiate(vouchers.getDateMMDDYYYY());
	          vouchers.clickonApproveButton();
	          vouchers.clickonApproveButton();
	          vouchers.clickonYes();
	       
	          boolean C2CTransferVoucherApproval = vouchers.C2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
	          if(C2CTransferVoucherApproval==true) {
	        	  String actualMessage = "Transaction Approved";
	              String C2CvoucherTransferApprovalResultMessage = vouchers.getC2CTransfervoucherApprovalMessage();
	              if (actualMessage.equals(C2CvoucherTransferApprovalResultMessage)) {
	                  
	                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Approval :" + C2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachScreenShot();
	                  vouchers.clickApproveDone();
	                  
	              }
	              else {
	            	  ExtentI.Markup(ExtentColor.RED, "Voucher Approval Displays wrong message as " + C2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachCatalinaLogs();
	                  ExtentI.attachScreenShot(); 
	              }
	          }
	          else {
	        	  ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Approval Failed");
	              ExtentI.attachCatalinaLogs();
	              ExtentI.attachScreenShot();
	        	  
	          }
		
	}

	public void performC2CVoucherInitiateBlankSearchBuyerAndMsisdn(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferBlankCategory";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
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
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankSearchBuyerMessageINITIATE();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Select Search Creteria First." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
		currentNode.log(Status.FAIL, "Blank Search Buyer Error Message not displayed on GUI");
		ExtentI.attachCatalinaLogs();
		ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateBlankMSISDNWithBuyerMobile(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CVoucherTransferBlankMSISDN";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
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
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Mobile Number") ;
		vouchers.enterbuyerDetails("") ;
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankMsisdnMessageINITIATE();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Mobile number is required." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Mobile Number Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateBlankCategoryWithUserName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode,String userName) {
		final String methodname = "performC2CVoucherInitiateBlankCategoryWithUserName";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
		
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("User Name") ;
		vouchers.enterbuyerDetailsuser(userName);
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankUserNameMessageINITIATE();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "User Category is required." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Category Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateBlankUserNameWithCategory(String FromCategory, String ToCategory,
			String toMSISDN, String PIN, String categorCode, String userName) {
		final String methodname = "performC2CVoucherInitiateBlankUserNameWithCategory";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
		
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("User Name") ;
		vouchers.selectCategoryDrop(FromCategory);
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankUserNameMessageInitiate();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Required" ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank User Name Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateBlankCategoryWithLoginID(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode,String loginid) {
		final String methodname = "performC2CVoucherInitiateBlankCategoryWithLoginID";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
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
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Login Id") ;
		vouchers.enterbuyerDetailsuser(loginid);
		vouchers.clickProceedButton();
		String errorMessageCaptured = vouchers.getBlankUserNameMessageINITIATE(); 
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "User Category is required." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Category Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateResetFields(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode,String loginid) {
		final String methodname = "performC2CVoucherInitiateResetFields";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
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
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Login Id") ;
		vouchers.selectCategoryDrop(FromCategory);
		vouchers.enterbuyerDetailsuser(loginid);
		
		vouchers.clickResetButton();
		boolean isblank = vouchers.isreset();
		
		
		if (isblank) {
			currentNode.log(Status.PASS, "Fields are Reset");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Fields not Reseted");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateInvalidMSISDNLength(String FromCategory, String ToCategory, String toMSISDN,
			String PIN, String categorCode) {
		final String methodname = "performC2CVoucherInitiateInvalidMSISDNLength";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
		
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Mobile Number") ;
		vouchers.enterbuyerDetails(new RandomGeneration().randomNumeric(3)) ;
		vouchers.clickProceedButton();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String errorMessageCaptured = vouchers.getInvalidMsisdnLengthMessage();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "MSISDN2 length should lie between 6 and 15." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Invalid Length Mobile Number Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateInvalidMSISDN(String FromCategory, String ToCategory, String toMSISDN,
			String PIN, String categorCode) {
		final String methodname = "performC2CVoucherInitiateInvalidMSISDN";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ToCategory);
		
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		vouchers.searchBuyerSelectDropdown("Mobile Number") ;
		vouchers.enterbuyerDetails(new RandomGeneration().randomAlphaNumeric(10)) ;
		vouchers.clickProceedButton();
		
		String errorMessageCaptured = vouchers.getInvalidMSISDNINITIATE();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Please enter a valid moblie number." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Invalid Mobile Number Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}

	public void performC2CVoucherInitiateWithBlankPaymentDate(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateWithBlankPaymentDate";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				vouchers.enterQuantity(_masterVO.getProperty("voucherQty"));
				vouchers.enterRemarksInit(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentModeInit(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDateInit("");
				vouchers.clickPurchaseButton();
				
				String errorMessageCaptured = vouchers.getPeymentDateErrorInitiate();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Please Choose Date to proceed." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Payment Date Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;
				
			}

		}
		
		
		Log.methodExit(methodname) ;

		
		
	}

	public void performC2CVoucherInitiateWithBlankPaymentType(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateWithBlankPaymentType";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				vouchers.enterQuantity(_masterVO.getProperty("voucherQty"));
				vouchers.enterRemarksInit(_masterVO.getProperty("Remarks"));
				//vouchers.selectPaymentModeInit(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDateInit("");
				vouchers.clickPurchaseButton();
				
				String errorMessageCaptured = vouchers.getPeymentDateTypeInitiate();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Please Choose Payment mode." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Payment Date Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;
				
			}

		}
		
		
		Log.methodExit(methodname) ;

		
		
	}

	public void performC2CVoucherInitiateWithBlankRemarks(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateWithBlankRemarks";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				vouchers.enterQuantity(_masterVO.getProperty("voucherQty"));
				vouchers.enterRemarksInit("");
				
				vouchers.clickPurchaseButton();
				
				String errorMessageCaptured = vouchers.getBlankRemarksErrorInitiate();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Remarks Required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Remarks Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;
				
			}

		}
		
		
		Log.methodExit(methodname) ;

	}

	public void performC2CVoucherInitiateWithBlankQuantity(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateWithBlankQuantity";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				vouchers.enterQuantity("");
				
				
				vouchers.clickPurchaseButton();
				
				String errorMessageCaptured = vouchers.getBlankQuantityErrorInitiate();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Quantity is required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Quantity Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;
				
			}

		}
		
		
		Log.methodExit(methodname) ;

		
	}

	public void performC2CVoucherInitiateWithInvalidQuantity(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateWithInvalidQuantity";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				vouchers.enterQuantity(new com.utils.RandomGeneration().randomAlphaNumeric(3));
				
				
				vouchers.clickPurchaseButton();
				
				String errorMessageCaptured = vouchers.getBlankQuantityErrorInitiate();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Quantity can only be numneric." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Invalid Quantity Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;
				
			}

		}
		
		
		Log.methodExit(methodname) ;
	}

	public void performC2CVoucherInitiateWithBlankDenomination(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateWithBlankDenomination";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				
				vouchers.clickPurchaseButton();
				String errorMessageCaptured = vouchers.getDenominationError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Voucher Denomination Required." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Denominatiob Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;

		
	}

	public void performC2CVoucherInitiateWithBlankSegment(String loginID, String password, String msisdn, String PIN, String FromCategory, String ToCategory, String categorCode,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateWithBlankSegment";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;

		String buyerData ;

		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = msisdn ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				
				
				
				vouchers.clickPurchaseButton();
				
				String errorMessageCaptured = vouchers.getSegmentError();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Voucher segment is required" ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
					currentNode.log(Status.FAIL, "Blank Segment Error Message not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;

			}
		
		}
		
		
		Log.methodExit(methodname) ;
		
		
	}

	public void performC2CVoucherInitiateandReject(String loginID, String categorCode, String msisdn, String PIN, String ToCategory, String FromCategory, String ParentName,String toMSISDN,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
		final String methodname = "performC2CVoucherInitiateandReject";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!vouchers.isC2CVisible()) {
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			vouchers.clickC2CHeading();
			vouchers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		vouchers.clickVoucherBuy();
		vouchers.clickVoucherButton() ;
		String arr[] = {"Mobile Number"} ;
		
		String buyerData ;
		
		for(int i=0; i<arr.length; i++)
		{
			vouchers.searchBuyerSelectDropdown(arr[i]) ;
			if(arr[i] == "Mobile Number" )
			{
				buyerData = toMSISDN ;
				String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
				vouchers.enterbuyerDetails(buyerData) ;
				vouchers.clickproceedButton();
				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
				vouchers.selectVoucherDenom(vouchername);
				String vseg =null;
				String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
				if(voucherSegment.equals("LC"))
					vseg="Local";
				vouchers.selectSegment(vseg);
				vouchers.selectDenomination(mrp);
				vouchers.enterQuantity(_masterVO.getProperty("voucherQty"));
				vouchers.enterRemarksInit(_masterVO.getProperty("Remarks"));
				vouchers.selectPaymentModeInit(_masterVO.getProperty("C2CPaymentModeType"));
				vouchers.enterPaymentInstDateInit(vouchers.getDateMMDDYY());
				vouchers.clickPurchaseButton();

				

				boolean PINPopUP = vouchers.C2CEnterPINPopupVisibility();  //enter User PIN for C2C
				if (PINPopUP == true) {
					vouchers.enterC2CUserPIN(PIN);
					vouchers.clickpurchaseButton();
					boolean C2CVoucherTransferInitiatedPopup = vouchers.C2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
					if (C2CVoucherTransferInitiatedPopup == true) {
						String actualMessage = "Voucher Request Initiated";
						String C2CvoucherTransferResultMessage = vouchers.getC2CTransferTransferRequestInitiatedMessage();
						if (actualMessage.equals(C2CvoucherTransferResultMessage)) {

							ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + C2CvoucherTransferResultMessage);

							ExtentI.attachScreenShot();
							String txnId =vouchers.printC2CPurchaseTransactionID();
							vouchers.clickC2CInitiateRequestDoneButton();



							String value = DBHandler.AccessHandler.getPreference(categorCode,_masterVO.getMasterValue(MasterI.NETWORK_CODE),PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
							int maxApprovalLevel = Integer.parseInt(value);

			            		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, ToCategory, EventsI.C2CTRANSFER_EVENT)) {

			            			 if(BTSLUtil.isNullString(value)) {
			            	            	Log.info("C2C Approval level is not Applicable");
			            	        		}
			            	            else {
			            	            	if(maxApprovalLevel == 0)
			            	        		{
			            	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
			            	            		
			            	        		}
			            	            	
			            	            	if(maxApprovalLevel >= 1)
			            	        		{
			            	            		performC2CVoucherRejectInitiate(txnId, ToCategory,activeProfile,toMSISDN);
			            	        		
			            	            		Log.info("Level 1 Reject !!");
			            	        		}
			            	            	
			            	           }
			            		}
			                    else {
			                        Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + ToCategory + "]");
			                    }
			                  
			                    
			                } else {
			                    ExtentI.Markup(ExtentColor.RED, "Voucher Request Initiated Displays wrong message as " + C2CvoucherTransferResultMessage);
			                    ExtentI.attachCatalinaLogs();
			                    ExtentI.attachScreenShot();
			                }
			            } else {
			               
			                ExtentI.Markup(ExtentColor.RED, "C2C Transfer Initiated Failed");
			                ExtentI.attachCatalinaLogs();
			                ExtentI.attachScreenShot();
			            }
			        } else {
			        	vouchers.clickTryAgain();
			            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for C2C didnt display");
			            ExtentI.attachCatalinaLogs();
			            ExtentI.attachScreenShot();
			            Log.methodExit(methodname) ;
			        }
				
			}
		

		}
		
		
		Log.methodExit(methodname) ;

	}

	private void performC2CVoucherRejectInitiate(String txnId, String toCategory, String activeProfile,String toMSISDN) {
		final String methodname = "performC2CVoucherRejectInitiate";
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", toCategory);
		
		if(!vouchers.isC2CVisible()) {
	  			vouchers.clickC2CApproval1Heading();
	  			Log.info("C2C Approval 1 heading is clicked");
	  		}
	  		else {
	  			vouchers.clickC2CHeading();
	  			vouchers.clickC2CApproval1Heading();
	  			Log.info("C2C Heading and Approval 1 is clicked");
	  		}
	          vouchers.clickVoucherToggle();
	         
	          String status="EN";
	          String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
	      	String username = DBHandler.AccessHandler.getUsernameFromMsisdn(toMSISDN);
			String userid= DBHandler.AccessHandler.getUserId(username);
			
			String fromSerialNumber = DBHandler.AccessHandler.getMaxSerialNumberWithuserid(productID, status, userid);
			
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
	          vouchers.enterTransactionId(txnId);
	          vouchers.ClickReject();
	          vouchers.ClickonEdit();
	          vouchers.enterFromSerialNumber(fromSerialNumber);
	          vouchers.enterToSerialNumber(toSerialNumber);
	          vouchers.clickCheck();
	        //vouchers.enterPaymentInstDateInitiate(vouchers.getDateMMDDYYYY());
	          vouchers.clickonReject();
	          
	          vouchers.clickonYes();
	       
	          boolean C2CTransferVoucherApproval = vouchers.C2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
	          if(C2CTransferVoucherApproval==true) {
	        	  String actualMessage = "Transaction Rejected";
	              String C2CvoucherTransferApprovalResultMessage = vouchers.getC2CTransfervoucherApprovalMessage();
	              if (actualMessage.equals(C2CvoucherTransferApprovalResultMessage)) {
	                  
	                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Rejected :" + C2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachScreenShot();
	                  vouchers.clickApproveDone();
	                  
	              }
	              else {
	            	  ExtentI.Markup(ExtentColor.RED, "Voucher Reject Displays wrong message as " + C2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachCatalinaLogs();
	                  ExtentI.attachScreenShot(); 
	              }
	          }
	          else {
	        	  ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Reject Failed");
	              ExtentI.attachCatalinaLogs();
	              ExtentI.attachScreenShot();
	        	  
	          }
			
	}


/*
	public void performC2CBulkTransferSymbolBatchName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferSymbolBatchName";
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
		transfers.clickSubmitButtonC2CBulkTransfer();
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
*/




/*

	public void performC2CBulkTransferBlankProduct(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferBlankProduct";
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



}
	

