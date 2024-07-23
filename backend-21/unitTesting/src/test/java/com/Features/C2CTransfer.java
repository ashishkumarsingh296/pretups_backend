package com.Features;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.VMS.InitiateVoucherO2CPage2;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel1Page;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel1Page2;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel1Page3;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel1Page4;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel2Page;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel2Page2;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel2Page3;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel2Page4;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel3Page;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel3Page2;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel3Page3;
import com.pageobjects.channeladminpages.o2ctransfer.ApproveLevel3Page4;
import com.pageobjects.channeluserspages.c2ctransfer.C2CDetailsPage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferConfirmPage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferDetailsPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.pretupsControllers.BTSLUtil;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;

public class C2CTransfer {

	WebDriver driver=null;
	
	
	C2CTransferDetailsPage C2CTransferDetailsPage;
	C2CDetailsPage C2CDetailsPage;
	C2CTransferConfirmPage C2CTransferConfirmPage;
	ChannelUserHomePage CHhomePage;
	ChannelAdminHomePage caHomepage;
	Login login;
	String voucherSegment =_masterVO.getProperty("segmentType");
	RandomGeneration randomNum;
	HashMap<String, String> c2cTransferMap;
	ChannelUserSubLinkPages chnlSubLink;
	ApproveLevel1Page approveLevel1Page;
	ApproveLevel1Page2 approveLevel1Page2;
	ApproveLevel1Page3 approveLevel1Page3;
	ApproveLevel1Page4 approveLevel1Page4;

	ApproveLevel2Page approveLevel2Page;
	ApproveLevel2Page2 approveLevel2Page2;
	ApproveLevel2Page3 approveLevel2Page3;
	ApproveLevel2Page4 approveLevel2Page4;

	ApproveLevel3Page approveLevel3Page;
	ApproveLevel3Page2 approveLevel3Page2;
	ApproveLevel3Page3 approveLevel3Page3;
	ApproveLevel3Page4 approveLevel3Page4;
	
	InitiateVoucherO2CPage2 initiateVoucherO2CPage2;

	
	public C2CTransfer(WebDriver driver){
	this.driver=driver;	
	caHomepage = new ChannelAdminHomePage(driver);
	C2CTransferDetailsPage = new C2CTransferDetailsPage(driver);
	C2CDetailsPage = new C2CDetailsPage(driver);
	C2CTransferConfirmPage = new C2CTransferConfirmPage(driver);
	CHhomePage = new ChannelUserHomePage(driver);
	login = new Login();
	randomNum = new RandomGeneration();
	c2cTransferMap=new HashMap<String, String>();
	chnlSubLink= new ChannelUserSubLinkPages(driver);
	approveLevel1Page = new ApproveLevel1Page(driver);
	approveLevel1Page2 = new ApproveLevel1Page2(driver);
	approveLevel1Page3 = new ApproveLevel1Page3(driver);
	approveLevel1Page4 = new ApproveLevel1Page4(driver);

	approveLevel2Page = new ApproveLevel2Page(driver);
	approveLevel2Page2 = new ApproveLevel2Page2(driver);
	approveLevel2Page3 = new ApproveLevel2Page3(driver);
	approveLevel2Page4 = new ApproveLevel2Page4(driver);

	approveLevel3Page = new ApproveLevel3Page(driver);
	approveLevel3Page2 = new ApproveLevel3Page2(driver);
	approveLevel3Page3 = new ApproveLevel3Page3(driver);
	approveLevel3Page4 = new ApproveLevel3Page4(driver);
	initiateVoucherO2CPage2 = new InitiateVoucherO2CPage2(driver);
	}
	
	public HashMap<String, String> channel2channelTransfer(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransfer";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		c2cTransferMap.put("InitiatedQuantities", C2CTransferDetailsPage.enterQuantityforC2C());
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatory()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		C2CDetailsPage.clickSubmit();
	//	if(!C2CDetailsPage.checkSMSPINEmpty()) {
		//C2CDetailsPage.enterSmsPin(PIN);
			C2CTransferConfirmPage.clickConfirm();
	//	}
		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);

		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> channel2channelVocuherTransfer(String FromCategory,String ToCategory, String MSISDN, String PIN,Object[][] dataObj, String loginID) throws InterruptedException {
		final String methodname = "channel2channelTransfer";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		HashMap<String, String> initiateMap = (HashMap<String, String>) dataObj[0][0];
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CVoucherTransferLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		//c2cTransferMap.put("InitiatedQuantities", C2CTransferDetailsPage.enterQuantityforC2C());
		initiateVoucherO2CPage2.SelectVoucherType(initiateMap.get("voucherType"));
		initiateVoucherO2CPage2.SelectVoucherSegment(voucherSegment);
		initiateVoucherO2CPage2.SelectDenomination(initiateMap.get("denomination"));
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status ="EN";
		String userID =DBHandler.AccessHandler.getUserIdLoginID(loginID);
		String SerialNumber = DBHandler.AccessHandler.getMinSerialNumberuserID(productID,status,userID);
		initiateMap.put("fromSerialNumber", SerialNumber);
		initiateMap.put("toSerialNumber", SerialNumber);
		initiateVoucherO2CPage2.EnterFromSerialNumber(initiateMap.get("fromSerialNumber"));
		initiateVoucherO2CPage2.EnterToSerialNumber(initiateMap.get("toSerialNumber"));
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatoryVoucher()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		
		C2CDetailsPage.clickVocuherSubmit();
		C2CTransferConfirmPage.clickConfirm();
		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);

		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	private boolean isPaymentMethodMandatory() {
		Log.info("Entered :: isPaymentMethodMandatory()");
		int isPaymentDetailsMandate =0;
		String value = DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATE_C2C");
	//	int isPaymentDetailsMandate = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATE_C2C"));
		if(BTSLUtil.isNullString(value)) {
			 isPaymentDetailsMandate=-1;
        }
        else
        	 isPaymentDetailsMandate = Integer.parseInt(value);
		if (isPaymentDetailsMandate != -1 && isPaymentDetailsMandate == 0) {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=true");
			return true;
		} else {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=false");
			return false;
		}
	}
	
	private boolean isPaymentMethodMandatoryVoucher() {
		Log.info("Entered :: isPaymentMethodMandatory()");
		int isPaymentDetailsMandate = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATEVOUCHER_C2C"));
		if (isPaymentDetailsMandate != -1 && isPaymentDetailsMandate == 0) {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=true");
			return true;
		} else {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=false");
			return false;
		}
	}
	
	//Log.methodEntry(productType, quantity, FromCategory, ToCategory, MSISDN,PIN,multiproduct);
	public HashMap<String,String> channel2channelTransfer(String... var) throws InterruptedException {	
		final String methodname = "channel2channelTransfer";
		
		String quantity=var[1], productType=var[0], FromCategory=var[2], ToCategory=var[3], MSISDN=var[4], PIN=var[5],multiproduct=var[6];
		Log.methodEntry(productType, quantity, FromCategory, ToCategory, MSISDN,PIN,multiproduct);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		C2CTransferDetailsPage.enterQuantityforC2C(productType, quantity,multiproduct);
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatory()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		C2CDetailsPage.clickSubmit();
		C2CTransferConfirmPage.clickConfirm();

		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);

		Log.methodExit(methodname);
		return c2cTransferMap;
		
	}
	
	public HashMap<String, String> performingLevel1Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,int level) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr1();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		if(level==1) {
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", TransactionID));
		}
		else {
			c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", TransactionID));
		}
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel2Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,int level) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		if(level==2) {
			c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", TransactionID));
			}
			else {
				c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", TransactionID));
			}Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,int level) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", TransactionID));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel1Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode, String... msgParameter) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr1();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey(msgCode, msgParameter));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel2Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode, String... msgParameter) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey(msgCode, msgParameter));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode, String... msgParameter) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey(msgCode, msgParameter));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel1Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr1();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage().replaceAll("\\r|\\n", ""));
		c2cTransferMap.put("expectedMessage", msgCode);
		Assertion.assertContainsEqualsSet(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel2Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage().replaceAll("\\r|\\n", ""));
		c2cTransferMap.put("expectedMessage",msgCode);
		Assertion.assertContainsEqualsSet(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage().replaceAll("\\r|\\n", ""));
		c2cTransferMap.put("expectedMessage",msgCode);
		Assertion.assertContainsEqualsSet(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel1ApprovalVoucher(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApprovalVocuher1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferVoucApr1();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		approveLevel2Page2.enterRemarks1("Automation Test");
			//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtn();

		approveLevel1Page4.clickConfirmButtonVoucher();
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	
	
	public HashMap<String, String> performingLevel2ApprovalVoucher(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApprovalVocuher2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferVoucApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel2Page2.enterRemarks2("Automation Test");
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtn();

		approveLevel1Page4.clickConfirmButtonVoucher();
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3ApprovalVoucher(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApprovalVocuher3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferVoucApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		approveLevel2Page2.enterRemarks3("Automation Test");
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtn();
		approveLevel1Page4.clickConfirmButtonVoucher();
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
public HashMap<String, String> channel2channelTransfer(String[] quantity,String[] productType,String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {	
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		C2CTransferDetailsPage.enterQuantityforC2C(quantity,productType);
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatory()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		C2CDetailsPage.clickSubmit();
		C2CTransferConfirmPage.clickConfirm();
		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);
		return c2cTransferMap;

	}

	
	
}
