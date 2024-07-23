package angular.feature;

import org.openqa.selenium.WebDriver;

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
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

import angular.classes.LoginRevamp;
import angular.pageobjects.O2CTransfer.O2CVoucherTransfer;

public class O2CVoucherInitiateRevamp extends BaseTest {
	public WebDriver driver;
    LoginRevamp login;
    O2CVoucherTransfer o2CVoucherTransfer;
    public O2CVoucherInitiateRevamp(WebDriver driver) {
    	this.driver = driver;
    	login = new LoginRevamp();
      	o2CVoucherTransfer = new O2CVoucherTransfer(driver);
    }
	public void performO2CVoucherInitiateandApproval(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		 final String methodname = "performO2CVoucherInitiateandApproval";
    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
         String MasterSheetPath = _masterVO.getProperty("DataProvider");
         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
 		 login.UserLogin(driver, "ChannelUser", chCategoryName);
 		 
 		o2CVoucherTransfer.clickOperatorHeading();
 		o2CVoucherTransfer.clickVoucherToggleIni();
 		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterQuantity(_masterVO.getProperty("voucherQty"));
		o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
		o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		boolean PINPopUP = o2CVoucherTransfer.O2CEnterPINPopupVisibility();  //enter User PIN for C2C
		  if (PINPopUP == true) {
			  o2CVoucherTransfer.enterO2CUserPIN(opPin);
			  o2CVoucherTransfer.clicksubmitButtonIni();
			  boolean O2CVoucherTransferInitiatedPopup = o2CVoucherTransfer.O2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
	            if (O2CVoucherTransferInitiatedPopup == true) {
	                String actualMessage = "Purchase successful";
	                String O2CvoucherTransferResultMessage = o2CVoucherTransfer.getC2CTransferTransferRequestInitiatedMessage();
	                if (actualMessage.equals(O2CvoucherTransferResultMessage)) {
		                  
	                    ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + O2CvoucherTransferResultMessage);
	                  
	                    ExtentI.attachScreenShot();
	                    String txnId =o2CVoucherTransfer.printO2CTransferTransactionID();
	                    o2CVoucherTransfer.clickO2CTransferRequestDoneButton();
	                   
	                 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
	       		     Long firstApprov = Long.parseLong(approvalLevel[0]);
	       		     Long secondApprov = Long.parseLong(approvalLevel[1]);
	       		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
	       		     performO2CVoucherApprovalInitiate1(txnId,opCategoryName,activeProfile,type);
	            	 Log.info("Level 1 Success !!");
	            	 
	            	 if (netPayableAmount > firstApprov)
	            	        		{
	            	            		
	            	            		performO2CVoucherApproval2(txnId);
	            	            		
	            	            		Log.info("Level 2 Success !!");
	            	            	}
	            	            	
	            	 if (netPayableAmount > secondApprov) 
	            	        		{
	            	            		
	            	            		performO2CVoucherApproval3(txnId);
	            	            	
	            	            		Log.info("Level 3 Success !!");
	            	            	
	            	        		}	     
	            	           
	            	
	                }
	                else {
	                    ExtentI.Markup(ExtentColor.RED, "Voucher Transfer Request Displays wrong message as " + O2CvoucherTransferResultMessage);
	                    ExtentI.attachCatalinaLogs();
	                    ExtentI.attachScreenShot();
	                }
	               }
	            else {
	            	o2CVoucherTransfer.clickTryAgain();
	                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Request Failed");
	                ExtentI.attachCatalinaLogs();
	                ExtentI.attachScreenShot();
	            }
		  }else {
				
	            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for O2C didnt display");
	            ExtentI.attachCatalinaLogs();
	            ExtentI.attachScreenShot();
	            Log.methodExit(methodname) ;
	        }
	}
	private void performO2CVoucherApprovalInitiate1(String txnId, String opCategoryName,String activeProfile,String type ) {
		
		 final String methodname = "performO2CVoucherApprovalInitiate1";
         String MasterSheetPath = _masterVO.getProperty("DataProvider");
         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
 		 login.UserLogin(driver, "Operator", opCategoryName);
 		 
		
		
		if(!o2CVoucherTransfer.isO2CApproval1Visible()) {
			  o2CVoucherTransfer.clickC2CApproval1Transaction();
	  			Log.info("O2C Approval 1 heading is clicked");
	  		}
	  		else {
	  			o2CVoucherTransfer.clickO2CApproval1Heading();
	  			o2CVoucherTransfer.clickC2CApproval1Transaction();
	  			Log.info("O2C Heading and Approval 1 is clicked");
	  		}
			 
			   o2CVoucherTransfer.clickApprovalVoucherToggleIni();
	         
	          String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
	          String status;
				if(type.equals("D") || type.equals("DT")) {
					status = "GE";
					}
					else {
					status = "WH";
					}
				
				String fromSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
	          o2CVoucherTransfer.enterTransactionId(txnId);
	          o2CVoucherTransfer.clickApprove();
	        
	          o2CVoucherTransfer.ClickonEdit();
	          o2CVoucherTransfer.enterFromSerialNoIni(fromSerialNumber);
	          o2CVoucherTransfer.enterToSerialNoIni(toSerialNumber);
	          o2CVoucherTransfer.clickCheck();
	          o2CVoucherTransfer.enterExternalRefNo(new RandomGeneration().randomNumberWithoutZero(5));
	          o2CVoucherTransfer.enterExternalDate(o2CVoucherTransfer.approvalExtDate());
	          o2CVoucherTransfer.clickonApproveButton();
		      o2CVoucherTransfer.clickonApproveButtonYes() ;
	         
	          boolean O2CTransferVoucherApproval = o2CVoucherTransfer.O2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
	          if(O2CTransferVoucherApproval==true) {
	        	  String actualMessage = "Transaction Approved";
	              String O2CvoucherTransferApprovalResultMessage = o2CVoucherTransfer.getO2CTransfervoucherApprovalMessage();
	              if (actualMessage.equals(O2CvoucherTransferApprovalResultMessage)) {
	                  
	                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Approval :" + O2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachScreenShot();
	                  o2CVoucherTransfer.clickApproveDone();
	                  
	              }
	              else {
	            	  ExtentI.Markup(ExtentColor.RED, "Voucher Approval Displays wrong message as " + O2CvoucherTransferApprovalResultMessage);
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
	private void performO2CVoucherApproval2(String txnId) {
		 if(!o2CVoucherTransfer.isO2CApproval2Visible()) {
			  o2CVoucherTransfer.clickC2CApproval2Transaction();
	  			Log.info("O2C Approval 2 heading is clicked");
	  		}
	  		else {
	  			o2CVoucherTransfer.clickO2CApproval2Heading();
	  			o2CVoucherTransfer.clickC2CApproval2Transaction();
	  			Log.info("O2C Heading and Approval 2 is clicked");
	  		}
			  
			   o2CVoucherTransfer.clickApprovalVoucherToggle();
	          
	          o2CVoucherTransfer.enterTransactionId(txnId);
	          o2CVoucherTransfer.clickApprove();
	          o2CVoucherTransfer.clickonApproveButton();
	         
	          boolean O2CTransferVoucherApproval = o2CVoucherTransfer.O2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
	          if(O2CTransferVoucherApproval==true) {
	        	  String actualMessage = "Transaction Approved";
	              String O2CvoucherTransferApprovalResultMessage = o2CVoucherTransfer.getO2CTransfervoucherApprovalMessage();
	              if (actualMessage.equals(O2CvoucherTransferApprovalResultMessage)) {
	                  
	                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Approval :" + O2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachScreenShot();
	                  o2CVoucherTransfer.clickApproveDone();
	                  
	              }
	              else {
	            	  ExtentI.Markup(ExtentColor.RED, "Voucher Approval Displays wrong message as " + O2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachCatalinaLogs();
	                  ExtentI.attachScreenShot(); 
	              }
	          }
	          else {
	        	  ExtentI.Markup(ExtentColor.RED, "O2C Voucher Transfer Approval Failed");
	              ExtentI.attachCatalinaLogs();
	              ExtentI.attachScreenShot();
	        	  
	          }
	
		
	}
	private void performO2CVoucherApproval3(String txnId) {
		 if(!o2CVoucherTransfer.isO2CApproval3Visible()) {
			  o2CVoucherTransfer.clickC2CApproval3Transaction();
	  			Log.info("O2C Approval 3 heading is clicked");
	  		}
	  		else {
	  			o2CVoucherTransfer.clickO2CApproval3Heading();
	  			o2CVoucherTransfer.clickC2CApproval3Transaction();
	  			Log.info("O2C Heading and Approval 3 is clicked");
	  		}
			  
			   o2CVoucherTransfer.clickApprovalVoucherToggle();
	          
	          o2CVoucherTransfer.enterTransactionId(txnId);
	          o2CVoucherTransfer.clickApprove();
	          o2CVoucherTransfer.clickonApproveButton();
	         
	          boolean O2CTransferVoucherApproval = o2CVoucherTransfer.O2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
	          if(O2CTransferVoucherApproval==true) {
	        	  String actualMessage = "Transaction Approved";
	              String O2CvoucherTransferApprovalResultMessage = o2CVoucherTransfer.getO2CTransfervoucherApprovalMessage();
	              if (actualMessage.equals(O2CvoucherTransferApprovalResultMessage)) {
	                  
	                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Approval :" + O2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachScreenShot();
	                  o2CVoucherTransfer.clickApproveDone();
	                  
	              }
	              else {
	            	  ExtentI.Markup(ExtentColor.RED, "Voucher Approval Displays wrong message as " + O2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachCatalinaLogs();
	                  ExtentI.attachScreenShot(); 
	              }
	          }
	          else {
	        	  ExtentI.Markup(ExtentColor.RED, "O2C Voucher Transfer Approval Failed");
	              ExtentI.attachCatalinaLogs();
	              ExtentI.attachScreenShot();
	        	  
	          }
	
		
	}
	public void performO2CVoucherInitiateBlankPaymentDate(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		 final String methodname = "performO2CVoucherInitiateBlankPaymentDate";
    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
         String MasterSheetPath = _masterVO.getProperty("DataProvider");
         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
 		 login.UserLogin(driver, "ChannelUser", chCategoryName);
 		 
 		o2CVoucherTransfer.clickOperatorHeading();
 		o2CVoucherTransfer.clickVoucherToggleIni();
 		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterQuantity(_masterVO.getProperty("voucherQty"));
		o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
		o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		String errorMessageCaptured = o2CVoucherTransfer.getPeymentDateError();
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
	public void performO2CVoucherInitiateBlankPaymentType(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		 final String methodname = "performO2CVoucherInitiateBlankPaymentType";
    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
         String MasterSheetPath = _masterVO.getProperty("DataProvider");
         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
 		 login.UserLogin(driver, "ChannelUser", chCategoryName);
 		 
 		o2CVoucherTransfer.clickOperatorHeading();
 		o2CVoucherTransfer.clickVoucherToggleIni();
 		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterQuantity(_masterVO.getProperty("voucherQty"));
		o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		String errorMessageCaptured = o2CVoucherTransfer.getPeymentTypeError();
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
	public void performO2CVoucherInitiateBlankInstrumentNumber(String opCategoryName, String opLoginId,
			String opPassword, String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type,
			String activeProfile, String mrp) {
		final String methodname = "performO2CVoucherInitiateBlankInstrumentNumber";
		Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		 login.UserLogin(driver, "ChannelUser", chCategoryName);
		 
		o2CVoucherTransfer.clickOperatorHeading();
		o2CVoucherTransfer.clickVoucherToggleIni();
		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterQuantity(_masterVO.getProperty("voucherQty"));
		o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		String errorMessageCaptured = o2CVoucherTransfer.getPeymentInstrumentNumberError();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Please Choose Payment Instrument Number." ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Payment Instrument Number Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}
	public void performO2CVoucherInitiateBlankRemarks(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		final String methodname = "performO2CVoucherInitiateBlankRemarks";
		Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		 login.UserLogin(driver, "ChannelUser", chCategoryName);
		 
		o2CVoucherTransfer.clickOperatorHeading();
		o2CVoucherTransfer.clickVoucherToggleIni();
		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterQuantity(_masterVO.getProperty("voucherQty"));
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		String errorMessageCaptured = o2CVoucherTransfer.getBlankRemarksError();
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
	public void performO2CVoucherInitiateBlankQuantity(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		final String methodname = "performO2CVoucherInitiateBlankQuantity";
		Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		 login.UserLogin(driver, "ChannelUser", chCategoryName);
		 
		o2CVoucherTransfer.clickOperatorHeading();
		o2CVoucherTransfer.clickVoucherToggleIni();
		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		String errorMessageCaptured = o2CVoucherTransfer.getBlankQuantity();
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
	public void performO2CVoucherInitiateBlankDenomination(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		final String methodname = "performO2CVoucherInitiateBlankDenomination";
		Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		 login.UserLogin(driver, "ChannelUser", chCategoryName);
		 
		o2CVoucherTransfer.clickOperatorHeading();
		o2CVoucherTransfer.clickVoucherToggleIni();
		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		String errorMessageCaptured = o2CVoucherTransfer.getDenominationError();
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
	public void performO2CVoucherInitiateBlankSegment(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		final String methodname = "performO2CVoucherInitiateBlankSegment";
		Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		 login.UserLogin(driver, "ChannelUser", chCategoryName);
		 
		o2CVoucherTransfer.clickOperatorHeading();
		o2CVoucherTransfer.clickVoucherToggleIni();
		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		String errorMessageCaptured = o2CVoucherTransfer.getSegmentError();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Voucher segment is required" ;
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Blank segment Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}
	public void performO2CVoucherInitiateReset(String opCategoryName, String opLoginId, String opPassword, String opPin,
			String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile, String mrp) {
		 final String methodname = "performO2CVoucherInitiateReset";
    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
         String MasterSheetPath = _masterVO.getProperty("DataProvider");
         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
 		 login.UserLogin(driver, "ChannelUser", chCategoryName);
 		 
 		o2CVoucherTransfer.clickOperatorHeading();
 		o2CVoucherTransfer.clickVoucherToggleIni();
 		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterQuantity(_masterVO.getProperty("voucherQty"));
		o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
		o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickResetButtonIni();		
		Boolean errorMessageCaptured = o2CVoucherTransfer.isresetINI();
		if (errorMessageCaptured) {
			currentNode.log(Status.PASS, "Fields are Reset");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else {
			currentNode.log(Status.FAIL, "Fields not Empty");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
		
	}
	public void performO2CVoucherInitiateandReject(String opCategoryName, String opLoginId, String opPassword,
			String opPin, String chCategoryName, String chMsisdn, String vouchertype, String type, String activeProfile,
			String mrp) {
		 final String methodname = "performO2CVoucherInitiateandApproval";
    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, vouchertype);
         String MasterSheetPath = _masterVO.getProperty("DataProvider");
         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
 		 login.UserLogin(driver, "ChannelUser", chCategoryName);
 		 
 		o2CVoucherTransfer.clickOperatorHeading();
 		o2CVoucherTransfer.clickVoucherToggleIni();
 		String vouchername = DBHandler.AccessHandler.getVoucherName(vouchertype);
		o2CVoucherTransfer.selectVoucherDenomIni(vouchername);
		String vseg =null;
		String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
		String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
		if(voucherSegment.equals("LC"))
				vseg="Local";
		o2CVoucherTransfer.selectSegment(vseg);
		o2CVoucherTransfer.selectDenomination(mrp);
		o2CVoucherTransfer.enterQuantity(_masterVO.getProperty("voucherQty"));
		o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
		o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
		o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
		o2CVoucherTransfer.clickPurchaseButtonIni();
		
		boolean PINPopUP = o2CVoucherTransfer.O2CEnterPINPopupVisibility();  //enter User PIN for C2C
		  if (PINPopUP == true) {
			  o2CVoucherTransfer.enterO2CUserPIN(opPin);
			  o2CVoucherTransfer.clicksubmitButtonIni();
			  boolean O2CVoucherTransferInitiatedPopup = o2CVoucherTransfer.O2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
	            if (O2CVoucherTransferInitiatedPopup == true) {
	                String actualMessage = "Purchase successful";
	                String O2CvoucherTransferResultMessage = o2CVoucherTransfer.getC2CTransferTransferRequestInitiatedMessage();
	                if (actualMessage.equals(O2CvoucherTransferResultMessage)) {
		                  
	                    ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + O2CvoucherTransferResultMessage);
	                  
	                    ExtentI.attachScreenShot();
	                    String txnId =o2CVoucherTransfer.printO2CTransferTransactionID();
	                    o2CVoucherTransfer.clickO2CTransferRequestDoneButton();
	                performO2CVoucherRejectInitiate1(txnId,opCategoryName,activeProfile,type);
	            	 Log.info("Level 1 Reject !!");
	            	 
	                }
	                else {
	                    ExtentI.Markup(ExtentColor.RED, "Voucher Transfer Request Displays wrong message as " + O2CvoucherTransferResultMessage);
	                    ExtentI.attachCatalinaLogs();
	                    ExtentI.attachScreenShot();
	                }
	               }
	            else {
	            	o2CVoucherTransfer.clickTryAgain();
	                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Request Failed");
	                ExtentI.attachCatalinaLogs();
	                ExtentI.attachScreenShot();
	            }
		  }else {
				
	            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for O2C didnt display");
	            ExtentI.attachCatalinaLogs();
	            ExtentI.attachScreenShot();
	            Log.methodExit(methodname) ;
	        }
		
	}
	private void performO2CVoucherRejectInitiate1(String txnId, String opCategoryName, String activeProfile,
			String type) {
		 final String methodname = "performO2CVoucherApprovalInitiate1";
         String MasterSheetPath = _masterVO.getProperty("DataProvider");
         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
 		 login.UserLogin(driver, "Operator", opCategoryName);
 		 
		
		
		if(!o2CVoucherTransfer.isO2CApproval1Visible()) {
			  o2CVoucherTransfer.clickC2CApproval1Transaction();
	  			Log.info("O2C Approval 1 heading is clicked");
	  		}
	  		else {
	  			o2CVoucherTransfer.clickO2CApproval1Heading();
	  			o2CVoucherTransfer.clickC2CApproval1Transaction();
	  			Log.info("O2C Heading and Approval 1 is clicked");
	  		}
			 
			   o2CVoucherTransfer.clickApprovalVoucherToggleIni();
	         
	          String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
	          String status;
				if(type.equals("D") || type.equals("DT")) {
					status = "GE";
					}
					else {
					status = "WH";
					}
				
				String fromSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID,status);
				
				if(fromSerialNumber==null)
					Assertion.assertSkip("Voucher Serial Number not Found");
				
				String toSerialNumber = fromSerialNumber;
	          o2CVoucherTransfer.enterTransactionId(txnId);
	          o2CVoucherTransfer.ClickReject();
	        
	          o2CVoucherTransfer.ClickonEdit();
	          o2CVoucherTransfer.enterFromSerialNoIni(fromSerialNumber);
	          o2CVoucherTransfer.enterToSerialNoIni(toSerialNumber);
	          o2CVoucherTransfer.clickCheck();
	          o2CVoucherTransfer.enterExternalRefNo(new RandomGeneration().randomNumberWithoutZero(5));
	          o2CVoucherTransfer.enterExternalDate(o2CVoucherTransfer.getDateMMDDYY());
	          o2CVoucherTransfer.clickonReject();
	         
	          boolean O2CTransferVoucherApproval = o2CVoucherTransfer.O2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
	          if(O2CTransferVoucherApproval==true) {
	        	  String actualMessage = "Transaction Rejected";
	              String O2CvoucherTransferApprovalResultMessage = o2CVoucherTransfer.getO2CTransfervoucherApprovalMessage();
	              if (actualMessage.equals(O2CvoucherTransferApprovalResultMessage)) {
	                  
	                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Rejected :" + O2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachScreenShot();
	                  o2CVoucherTransfer.clickApproveDone();
	                  
	              }
	              else {
	            	  ExtentI.Markup(ExtentColor.RED, "Voucher Rejected Displays wrong message as " + O2CvoucherTransferApprovalResultMessage);
	                  ExtentI.attachCatalinaLogs();
	                  ExtentI.attachScreenShot(); 
	              }
	          }
	          else {
	        	  ExtentI.Markup(ExtentColor.RED, "O2C Voucher Transfer Rejection Failed");
	              ExtentI.attachCatalinaLogs();
	              ExtentI.attachScreenShot();
	        	  
	          }
		
		
	}
}
