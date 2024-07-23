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

public class O2CVoucherTransferRevamp extends BaseTest {
		public WebDriver driver;
	    LoginRevamp login;
	    
	    O2CVoucherTransfer o2CVoucherTransfer;
	    public O2CVoucherTransferRevamp(WebDriver driver) {
	    	this.driver = driver;
	    	login = new LoginRevamp();
	      	o2CVoucherTransfer = new O2CVoucherTransfer(driver);
	    }

	    public void performO2CVoucherTransferandApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp) {
	    	 final String methodname = "performO2CVoucherTransferandApproval";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
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
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber);
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
					boolean PINPopUP = o2CVoucherTransfer.O2CEnterPINPopupVisibility();  //enter User PIN for C2C
					  if (PINPopUP == true) {
						  o2CVoucherTransfer.enterO2CUserPIN(opPin);
						  o2CVoucherTransfer.clicksubmitButton();
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
					       		   performO2CVoucherApproval1(txnId, chCategoryName);
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
				            } else {
				            	o2CVoucherTransfer.clickTryAgain();
				                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Request Failed");
				                ExtentI.attachCatalinaLogs();
				                ExtentI.attachScreenShot();
				            }
					
					  }
					else {
						
			            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for O2C didnt display");
			            ExtentI.attachCatalinaLogs();
			            ExtentI.attachScreenShot();
			            Log.methodExit(methodname) ;
			        }
	 			}
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

		private void performO2CVoucherApproval1(String txnId, String chCategoryName) {
			  if(!o2CVoucherTransfer.isO2CApproval1Visible()) {
				  o2CVoucherTransfer.clickC2CApproval1Transaction();
		  			Log.info("O2C Approval 1 heading is clicked");
		  		}
		  		else {
		  			o2CVoucherTransfer.clickO2CApproval1Heading();
		  			o2CVoucherTransfer.clickC2CApproval1Transaction();
		  			Log.info("O2C Heading and Approval 1 is clicked");
		  		}
				  
				   o2CVoucherTransfer.clickApprovalVoucherToggle();
		         
		          o2CVoucherTransfer.enterTransactionId(txnId);
		          o2CVoucherTransfer.clickApprove();
		          try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		          o2CVoucherTransfer.enterExternalRefNo(new RandomGeneration().randomNumberWithoutZero(5));
		          o2CVoucherTransfer.enterExternalDate(o2CVoucherTransfer.getDateMMDDYY());
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
		        	  ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Approval Failed");
		              ExtentI.attachCatalinaLogs();
		              ExtentI.attachScreenShot();
		        	  
		          }
			
		}

		public void performO2CVoucherTransferBlankSearchBy(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankSearchBy";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("C2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("C2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankSearchBuyerMessage();
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

		public void performO2CVoucherTransferBlankMSISDNWithBuyerMobile(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankMSISDNWithBuyerMobile";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("Mobile Number") ;
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankMsisdnMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "Mobile number is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank Msisdn Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferBlankGeoGraphyWithUser(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankGeoGraphyWithUser";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("User Name") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankGeograpyMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage ="Geography is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank Geography Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferBlankDomainWithUser(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankGeoGraphyWithUser";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("User Name") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankDomainMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "Domain is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank Domain Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
				
		}

		public void performO2CVoucherTransferBlankOwnerCategoryWithUser(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankOwnerCategoryWithUser";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("User Name") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankOwnerCategoryMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "Owner Category is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank Owner Category Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferBlankCategoryWithUser(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankOwnerCategoryWithUser";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("User Name") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankCategoryMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "User Category is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank User Category Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferBlankChannelOwnerNameWithUser(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankChannelOwnerNameWithUser";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("User Name") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankChannelOwnerNameMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "Channel owner name is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank Channel Owner Name Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
		}

		public void performO2CVoucherTransferBlankUserNameWithUser(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankChannelOwnerNameWithUser";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("User Name") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankUserNameMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "User name is required." ;
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

		public void performO2CVoucherTransferBlankDomainWithLoginId(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			final String methodname = "performO2CVoucherTransferBlankDomainWithLoginId";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("Login Id") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankDomainMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "Domain is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank Domain Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
			
			
		}

		public void performO2CVoucherTransferBlankCategoryWithLoginId(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			final String methodname = "performO2CVoucherTransferBlankCategoryWithLoginId";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	       
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("Login Id") ;
	         
		 	 o2CVoucherTransfer.clickproceedButton();
		 	String errorMessageCaptured = o2CVoucherTransfer.getBlankDomainMessage();
			Log.info("errorMessageCaptured : " + errorMessageCaptured);
			String expectedMessage = "Domain is required." ;
			if (expectedMessage.equals(errorMessageCaptured)) {
				Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
			currentNode.log(Status.FAIL, "Blank Domain Error Message not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
		}

		public void performO2CVoucherTransferResetFields(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			final String methodname = "performO2CVoucherTransferResetFields";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	       
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("Mobile Number") ;
	         o2CVoucherTransfer.enterbuyerDetails(chMsisdn);
		 	 o2CVoucherTransfer.clickResetButton();
		 	 boolean isblank = o2CVoucherTransfer.isreset();
		 	if (isblank) {
				currentNode.log(Status.PASS, "Fields are Reset");
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			}
			else {
				currentNode.log(Status.FAIL, "Reset Field Error displayed on GUI");
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferInvalidMSISDNLength(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			final String methodname = "performO2CVoucherTransferInvalidMSISDNLength";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	       
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         o2CVoucherTransfer.searchBuyerSelectDropdown("Mobile Number") ;
	         o2CVoucherTransfer.enterbuyerDetails(new RandomGeneration().randomNumberWithoutZero(4));
			
	         o2CVoucherTransfer.clickproceedButton();
			 	String errorMessageCaptured = o2CVoucherTransfer.getInvalidMsisdnLengthMessage();
				Log.info("errorMessageCaptured : " + errorMessageCaptured);
				String expectedMessage = "Please enter a valid moblie number." ;
				if (expectedMessage.equals(errorMessageCaptured)) {
					Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else {
				currentNode.log(Status.FAIL, "Invalid Msisdn Length Error Message not displayed on GUI");
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname) ;
		}

		public void performO2CVoucherTransferBlankPaymentDate(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankPaymentDate";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					String toSerialNumber = fromSerialNumber;
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber);
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.clickPurchaseButton();
					
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
		}

		public void performO2CVoucherTransferBlankPaymentType(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankPaymentType";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					String toSerialNumber = fromSerialNumber;
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber);
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.clickPurchaseButton();
					
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferBlankInstrumentNumber(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankInstrumentNumber";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					String toSerialNumber = fromSerialNumber;
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber);
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.clickPurchaseButton();
					
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferBlankRemarks(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankRemarks";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					String toSerialNumber = fromSerialNumber;
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber);
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferBlankToSerial(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankToSerial";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
					String errorMessageCaptured = o2CVoucherTransfer.getBlankToSerialError();
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
			
		}

		public void performO2CVoucherTransferInvalidToSerial(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferInvalidToSerial";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterToSerialNo(new RandomGeneration().randomAlphaNumeric(7));
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
					String errorMessageCaptured = o2CVoucherTransfer.getBlankToSerialError();
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
			
		}

		public void performO2CVoucherTransferBlankFromSerial(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankFromSerial";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					
					String toSerialNumber = fromSerialNumber;
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber)
					;o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
					String errorMessageCaptured = o2CVoucherTransfer.getBlankFromSerialError();
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
			
		}

		public void performO2CVoucherTransferInvalidFromSerial(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferInvalidFromSerial";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
					String status="GE";
					
					String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
					
					if(fromSerialNumber==null)
						Assertion.assertSkip("Voucher Serial Number not Found");
					
					
					String toSerialNumber = fromSerialNumber;
					o2CVoucherTransfer.enterFromSerialNo(new com.utils.RandomGeneration().randomAlphaNumeric(6));
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber);o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
					String errorMessageCaptured = o2CVoucherTransfer.getBlankFromSerialError();
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
			
		}

		public void performO2CVoucherTransferBlankDenomination(String opCategoryName, String opLoginId,
				String opPassword, String opPin, String chCategoryName, String chMsisdn, String voucherType,
				String type, String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankDenomination";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
				
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
			
		}

		public void performO2CVoucherTransferBlankSegment(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankSegment";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				
					
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
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
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
		}

		public void performO2CVoucherTransferBlankType(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferBlankType";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
					String errorMessageCaptured = o2CVoucherTransfer.getTypeError();
					Log.info("errorMessageCaptured : " + errorMessageCaptured);
					String expectedMessage = "Voucher type is required" ;
					if (expectedMessage.equals(errorMessageCaptured)) {
						Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
						ExtentI.attachCatalinaLogsForSuccess();
						ExtentI.attachScreenShot();
					}
					else {
						currentNode.log(Status.FAIL, "Blank type Error Message not displayed on GUI");
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
	 			}
	 		}
	 			
	 		Log.methodExit(methodname) ;
			
		}

		public void performO2CVoucherTransferandReject(String opCategoryName, String opLoginId, String opPassword,
				String opPin, String chCategoryName, String chMsisdn, String voucherType, String type,
				String activeProfile, String mrp) {
			 final String methodname = "performO2CVoucherTransferandReject";
	    	 Log.methodEntry(methodname, opCategoryName, opCategoryName, chMsisdn, voucherType);
	         String MasterSheetPath = _masterVO.getProperty("DataProvider");
	         ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	         login.UserLogin(driver, "Operator", opCategoryName);
	        
	         
	         if(!o2CVoucherTransfer.isO2CVisible()) {
	        	 o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Transfer heading is clicked");
	 		}
	 		else {
	 			o2CVoucherTransfer.clickO2CHeading();
	 			o2CVoucherTransfer.clickO2CTransactionHeading();
	 			Log.info("O2C Heading and Transaction Heading is clicked");
	 		}
	         
	         o2CVoucherTransfer.clickVoucherToggle();
	         String arr[] = {"Mobile Number"} ;
	 		
	 		String buyerData ;
	 		
	 		for(int i=0; i<arr.length; i++)
	 		{
	 			o2CVoucherTransfer.searchBuyerSelectDropdown(arr[i]) ;
	 			if(arr[i] == "Mobile Number" )
	 			{
	 				buyerData = chMsisdn;
	 				o2CVoucherTransfer.enterbuyerDetails(buyerData) ;
	 				o2CVoucherTransfer.clickproceedButton();
	 				String vouchername = DBHandler.AccessHandler.getVoucherName(voucherType);
					o2CVoucherTransfer.selectVoucherDenom(vouchername);
					String vseg =null;
					String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
					String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
					if(voucherSegment.equals("LC"))
							vseg="Local";
					o2CVoucherTransfer.selectSegment(vseg);
					o2CVoucherTransfer.selectDenomination(mrp);
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
					o2CVoucherTransfer.enterFromSerialNo(fromSerialNumber);
					o2CVoucherTransfer.enterToSerialNo(toSerialNumber);
					o2CVoucherTransfer.enterRemarks(_masterVO.getProperty("Remarks"));
					o2CVoucherTransfer.selectPaymentMode(_masterVO.getProperty("C2CPaymentModeType"));
					o2CVoucherTransfer.enterPaymentInstDate(o2CVoucherTransfer.getDateMMDDYY());
					o2CVoucherTransfer.clickPurchaseButton();
					
					boolean PINPopUP = o2CVoucherTransfer.O2CEnterPINPopupVisibility();  //enter User PIN for C2C
					  if (PINPopUP == true) {
						  o2CVoucherTransfer.enterO2CUserPIN(opPin);
						  o2CVoucherTransfer.clicksubmitButton();
				            boolean O2CVoucherTransferInitiatedPopup = o2CVoucherTransfer.O2CTransferInitiatedVisibility();     //Transfer initiated displays etopup ptopup
				            if (O2CVoucherTransferInitiatedPopup == true) {
				                String actualMessage = "Purchase successful";
				                String O2CvoucherTransferResultMessage = o2CVoucherTransfer.getC2CTransferTransferRequestInitiatedMessage();
				                if (actualMessage.equals(O2CvoucherTransferResultMessage)) {
					                  
				                    ExtentI.Markup(ExtentColor.GREEN, "Voucher Request Initiated :" + O2CvoucherTransferResultMessage);
				                  
				                    ExtentI.attachScreenShot();
				                    String txnId =o2CVoucherTransfer.printO2CTransferTransactionID();
				                    o2CVoucherTransfer.clickO2CTransferRequestDoneButton();
				                   performO2CVoucherReject(txnId);
				            	    Log.info("Level 1 Reject !!");
				            	    
	            	               
	            	           
				                 
				                }
				                else {
				                    ExtentI.Markup(ExtentColor.RED, "Voucher Transfer Request Displays wrong message as " + O2CvoucherTransferResultMessage);
				                    ExtentI.attachCatalinaLogs();
				                    ExtentI.attachScreenShot();
				                }
				            } else {
				            	o2CVoucherTransfer.clickTryAgain();
				                ExtentI.Markup(ExtentColor.RED, "O2C Transfer Request Failed");
				                ExtentI.attachCatalinaLogs();
				                ExtentI.attachScreenShot();
				            }
					
					  }
					else {
						
			            ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup for O2C didnt display");
			            ExtentI.attachCatalinaLogs();
			            ExtentI.attachScreenShot();
			            Log.methodExit(methodname) ;
			        }
	 			}
	 		}}

		private void performO2CVoucherReject(String txnId) {
			if(!o2CVoucherTransfer.isO2CApproval1Visible()) {
				  o2CVoucherTransfer.clickC2CApproval1Transaction();
		  			Log.info("O2C Approval 1 heading is clicked");
		  		}
		  		else {
		  			o2CVoucherTransfer.clickO2CApproval1Heading();
		  			o2CVoucherTransfer.clickC2CApproval1Transaction();
		  			Log.info("O2C Heading and Approval 1 is clicked");
		  		}
				  
				   o2CVoucherTransfer.clickApprovalVoucherToggle();
		         
		          o2CVoucherTransfer.enterTransactionId(txnId);
		          o2CVoucherTransfer.ClickReject();
		         
		          o2CVoucherTransfer.clickonReject();
		         
		          boolean O2CTransferVoucherReject = o2CVoucherTransfer.O2CTransferApprovalVisibility();     //Transfer initiated displays etopup ptopup
		          if(O2CTransferVoucherReject==true) {
		        	  String actualMessage = "Transaction Rejected";
		              String O2CvoucherTransferRejectResultMessage = o2CVoucherTransfer.getO2CTransfervoucherApprovalMessage();
		              if (actualMessage.equals(O2CvoucherTransferRejectResultMessage)) {
		                  
		                  ExtentI.Markup(ExtentColor.GREEN, "Voucher Rejected :" + O2CvoucherTransferRejectResultMessage);
		                  ExtentI.attachScreenShot();
		                  o2CVoucherTransfer.clickApproveDone();
		                  
		              }
		              else {
		            	  ExtentI.Markup(ExtentColor.RED, "Voucher Rejected Displays wrong message as " + O2CvoucherTransferRejectResultMessage);
		                  ExtentI.attachCatalinaLogs();
		                  ExtentI.attachScreenShot(); 
		              }
		          }
		          else {
		        	  ExtentI.Markup(ExtentColor.RED, "C2C Voucher Transfer Rejected Failed");
		              ExtentI.attachCatalinaLogs();
		              ExtentI.attachScreenShot();
		        	  
		          }
			
		}
			
		
}
