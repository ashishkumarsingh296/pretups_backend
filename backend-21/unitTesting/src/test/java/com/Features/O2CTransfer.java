package com.Features;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.VMS.InitiateVoucherO2CPage2;
import com.pageobjects.channeladminpages.VMS.InitiateVoucherO2CPage3;
import com.pageobjects.channeladminpages.VMS.InitiateVoucherO2CPage4;
import com.pageobjects.channeladminpages.channelreportO2C.VoucherTransactionReport;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeladminpages.homepage.OptToChanSubCatPage;
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
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage2;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage3;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage4;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage5;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage6;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.O2CTransferSubLink;
import com.pageobjects.channeluserspages.o2ctransfer.InitiateTransfer_Page_1;
import com.pageobjects.channeluserspages.o2ctransfer.InitiateTransfer_Page_2;
import com.pageobjects.channeluserspages.o2ctransfer.InitiateTransfer_Page_3;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.VMS.AddVoucherDenomination;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;

public class O2CTransfer {

	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	NetworkAdminHomePage naHomePage;
	InitiateO2CTransferPage initiateO2CPage;
	VoucherTransactionReport voucherTransactionReport;
	InitiateO2CTransferPage2 initiateO2CPage2;
	InitiateO2CTransferPage3 initiateO2CPage3;
	InitiateO2CTransferPage4 initiateO2CPage4;
	InitiateO2CTransferPage5 initiateO2CPage5;
	InitiateO2CTransferPage6 initiateO2CPage6;

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
	OptToChanSubCatPage optToChanSubPage;
	AddVoucherDenomination addVoucherDenomination;
	ChannelUserHomePage CUHomePage;
	O2CTransferSubLink CU_O2CTransfer;
	
	InitiateTransfer_Page_1 InitiateTransferByChannelUser1;
	InitiateTransfer_Page_2 InitiateTransferByChannelUser2;
	InitiateTransfer_Page_3 InitiateTransferByChannelUser3;
	
	InitiateVoucherO2CPage2 initiateVoucherO2CPage2;
	InitiateVoucherO2CPage3 initiateVoucherO2CPage3;
	InitiateVoucherO2CPage4 initiateVoucherO2CPage4;
	RandomGeneration randmGenrtr;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;
	int Nation_Voucher;

	static RandomGeneration randStr = new RandomGeneration();

	public O2CTransfer(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		naHomePage = new NetworkAdminHomePage(driver);
		initiateO2CPage = new InitiateO2CTransferPage(driver);
		initiateO2CPage2 = new InitiateO2CTransferPage2(driver);
		initiateO2CPage3 = new InitiateO2CTransferPage3(driver);
		initiateO2CPage4 = new InitiateO2CTransferPage4(driver);
		initiateO2CPage5 = new InitiateO2CTransferPage5(driver);
		initiateO2CPage6 = new InitiateO2CTransferPage6(driver);

		approveLevel1Page = new ApproveLevel1Page(driver);
		approveLevel1Page2 = new ApproveLevel1Page2(driver);
		approveLevel1Page3 = new ApproveLevel1Page3(driver);
		approveLevel1Page4 = new ApproveLevel1Page4(driver);
		voucherTransactionReport =new VoucherTransactionReport(driver);
		approveLevel2Page = new ApproveLevel2Page(driver);
		approveLevel2Page2 = new ApproveLevel2Page2(driver);
		approveLevel2Page3 = new ApproveLevel2Page3(driver);
		approveLevel2Page4 = new ApproveLevel2Page4(driver);

		approveLevel3Page = new ApproveLevel3Page(driver);
		approveLevel3Page2 = new ApproveLevel3Page2(driver);
		approveLevel3Page3 = new ApproveLevel3Page3(driver);
		approveLevel3Page4 = new ApproveLevel3Page4(driver);
		addVoucherDenomination = new AddVoucherDenomination(driver);
		
		InitiateTransferByChannelUser1 = new InitiateTransfer_Page_1(driver);
		InitiateTransferByChannelUser2 = new InitiateTransfer_Page_2(driver);
		InitiateTransferByChannelUser3 = new InitiateTransfer_Page_3(driver);
		
		initiateVoucherO2CPage2 = new InitiateVoucherO2CPage2(driver);
		initiateVoucherO2CPage3 = new InitiateVoucherO2CPage3(driver);
		initiateVoucherO2CPage4 = new InitiateVoucherO2CPage4(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
		CU_O2CTransfer = new O2CTransferSubLink(driver);
		
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		optToChanSubPage = new OptToChanSubCatPage(driver);
		randmGenrtr = new RandomGeneration();
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
		Nation_Voucher = Integer.parseInt(_masterVO.getClientDetail("Nation_Voucher"));

	}

	public Map<String, String> initiateTransfer(String userMSISDN, String productType, String Quantity, String productName, String Remarks) throws InterruptedException {
		final String methodname = "initiateTransfer";
		Log.methodEntry(methodname, userMSISDN, productType, Quantity, Remarks);
		
		String PositiveCommissioning = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY").toUpperCase();
		BigDecimal netPayableAmount = null;
		
		// Initiating O2C Transfer from Channel Admin
		userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		initiateO2CPage.selectDistributionType(PretupsI.O2C_STOCK_TYPE_LOOKUP);
		initiateO2CPage.enterMobileNumber(userMSISDN);
		
		boolean selectDropdownVisible = initiateO2CPage.isSelectProductTypeVisible() ;
		if(selectDropdownVisible==true) {
			initiateO2CPage.selectProductType1(productType);
		}
		
		initiateO2CPage.clickSubmitButton();

		//Entering transfer details
		initiateO2CPage2.enterQuantitywithname(Quantity,productName);
		initiateO2CPage2.enterRemarks(Remarks);
		
		/*
		 * Added a Control to enter Payment Instrument, Payment Instrument Num Payment Instrument Date & Transaction PIN
		 * Payment Details are only entered in case PAYMENTDETAILSMANDATE_O2C == 0;
		 */
		if (isPaymentMethodMandatory()) {
			initiateO2CPage2.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			initiateO2CPage2.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			initiateO2CPage2.enterPaymentInstDate(caHomepage.getDate());
		}
		
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCnt=ExcelUtility.getRowCount();
		String PIN=null;
		
		/*
		 * Preference to check if pin need to be entered for O2C Withdraw
		 */
		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(userInfo.get("CATEGORY_NAME"));
		
		if(pinAllowed.equals("Y")){
			for(int x=1; x<=rowCnt;x++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, x).equals(userInfo.get("CATEGORY_NAME")))
				{PIN= ExcelUtility.getCellData(0, ExcelI.PIN, x);
				break;
			}
		}
			initiateO2CPage2.enterPin(PIN);
		}
		
		initiateO2CPage2.clickSubmitButton();

		if(PositiveCommissioning.equals("FALSE")){
		netPayableAmount = _parser.currencyHandler(approveLevel1Page3.fetchNetPayableAmount(), new Locale(SystemPreferences.DISPLAY_LANGUAGE, SystemPreferences.DISPLAY_COUNTRY));	
		}
		else {
		netPayableAmount= _parser.currencyHandler(approveLevel1Page3.fetchreceiverCreditQtyPosCommission(), new Locale(SystemPreferences.DISPLAY_LANGUAGE, SystemPreferences.DISPLAY_COUNTRY));
		}
		
		ResultMap.put("NetPayableAmount",netPayableAmount.toString());
		
		initiateO2CPage3.clickConfirmButton();

		// Fetching O2C Transfer Success Message.
		String message= initiateO2CPage.getMessage();
		ResultMap.put("INITIATE_MESSAGE", message);	
		ResultMap.put("TRANSACTION_ID", _parser.getTransactionID(message, PretupsI.CHANNEL_TRANSFER_O2C_ID));
		
		Log.methodExit(methodname);
		return ResultMap;
	}
	
	public Map<String, String> initiateTransfer1(String userMSISDN, String productType, String Quantity, String Remarks) throws InterruptedException {
		final String methodname = "initiateTransfer";
		Log.methodEntry(methodname, userMSISDN, productType, Quantity, Remarks);
		
		String PositiveCommissioning = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY").toUpperCase();
		BigDecimal netPayableAmount = null;
		
		// Initiating O2C Transfer from Channel Admin
		userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		initiateO2CPage.selectDistributionType(PretupsI.O2C_STOCK_TYPE_LOOKUP);
		initiateO2CPage.enterMobileNumber(userMSISDN);
		
		boolean selectDropdownVisible = initiateO2CPage.isSelectProductTypeVisible() ;
		if(selectDropdownVisible==true) {
			initiateO2CPage.selectProductType1(productType);
		}
		
		initiateO2CPage.clickSubmitButton();

		//Entering transfer details
		initiateO2CPage2.enterQuantity(Quantity);
		initiateO2CPage2.enterRemarks(Remarks);
		
		/*
		 * Added a Control to enter Payment Instrument, Payment Instrument Num Payment Instrument Date & Transaction PIN
		 * Payment Details are only entered in case PAYMENTDETAILSMANDATE_O2C == 0;
		 */
		if (isPaymentMethodMandatory()) {
			initiateO2CPage2.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			initiateO2CPage2.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			initiateO2CPage2.enterPaymentInstDate(caHomepage.getDate());
		}
		
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCnt=ExcelUtility.getRowCount();
		String PIN=null;
		
		/*
		 * Preference to check if pin need to be entered for O2C Withdraw
		 */
		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(userInfo.get("CATEGORY_NAME"));
		
		if(pinAllowed.equals("Y")){
			for(int x=1; x<=rowCnt;x++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, x).equals(userInfo.get("CATEGORY_NAME")))
				{PIN= ExcelUtility.getCellData(0, ExcelI.PIN, x);
				break;
			}
		}
			initiateO2CPage2.enterPin(PIN);
		}
		
		initiateO2CPage2.clickSubmitButton();

		if(PositiveCommissioning.equals("FALSE")){
		netPayableAmount = _parser.currencyHandler(approveLevel1Page3.fetchNetPayableAmount(), new Locale(SystemPreferences.DISPLAY_LANGUAGE, SystemPreferences.DISPLAY_COUNTRY));	
		}
		else {
		netPayableAmount= _parser.currencyHandler(approveLevel1Page3.fetchreceiverCreditQtyPosCommission(), new Locale(SystemPreferences.DISPLAY_LANGUAGE, SystemPreferences.DISPLAY_COUNTRY));
		}
		
		ResultMap.put("NetPayableAmount",netPayableAmount.toString());
		
		initiateO2CPage3.clickConfirmButton();

		// Fetching O2C Transfer Success Message.
		String message= initiateO2CPage.getMessage();
		ResultMap.put("INITIATE_MESSAGE", message);	
		ResultMap.put("TRANSACTION_ID", _parser.getTransactionID(message, PretupsI.CHANNEL_TRANSFER_O2C_ID));
		
		Log.methodExit(methodname);
		return ResultMap;
	}

	
	public void voucherTrackingReport(String catCode) throws InterruptedException {
		final String methodname = "voucherTrackingReport";
		
		// Initiating O2C Transfer from Channel Admin
		userInfo= UserAccess.getUserWithAccesswithCategorywithDomain(RolesI.C2C_VOUCHERTRACKINGREPORT,catCode);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickChannelReportsC2C();
		try {
		caHomepage.clickVoucherTrackingReport();
		
		String date = caHomepage.getDate();
		String newDate =naHomePage.addDaysToCurrentDate(date, -1) ;
		voucherTransactionReport.enterDate(newDate);

		voucherTransactionReport.clickSubmitButton();
		}
		catch(Exception e) {
			Assertion.assertFail("Case is failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}
	
	
	public void voucherAvailbilityReport(String Category) throws InterruptedException {
		final String methodname = "voucherAvailbilityReport";
		
		// Initiating O2C Transfer from Channel Admin
		userInfo= UserAccess.getUserWithAccesswithCategorywithDomain(RolesI.C2C_VOUCHERAVAILBILITYREPORT,Category);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickChannelReportsC2C();
		try {
		caHomepage.clickVoucherAvailbilityReport();
		String domain = DBHandler.AccessHandler.getDomainCodeCatgories(Category);
		if(domain.equals("OPT")|| domain.equals("DIST")) {
			String date = caHomepage.getDate();
			String newDate =naHomePage.addDaysToCurrentDate(date, -1) ;
			voucherTransactionReport.enterDate(newDate);
		}
		else {
			voucherTransactionReport.clickreportLink();
		}
			}
			catch(Exception e) {
				Assertion.assertFail("Case is failed");
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		Log.methodExit(methodname);
	}
	
	
	public void voucherConsumptionReport(String Category) throws InterruptedException {
		final String methodname = "voucherConsumptionReport";
		
		// Initiating O2C Transfer from Channel Admin
		userInfo= UserAccess.getUserWithAccesswithCategorywithDomain(RolesI.C2C_VOUCHERCONSUMREPORT,Category);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickChannelReportsC2C();
		try {
		caHomepage.clickVoucherConsumptionReport();
		
		String date = caHomepage.getDate();
		String newDate =naHomePage.addDaysToCurrentDate(date, -1) ;
		voucherTransactionReport.enterDate(newDate);
		voucherTransactionReport.clickSubmitButton();
		}
		catch(Exception e) {
			Assertion.assertFail("Case is failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		
		Log.methodExit(methodname);
	}
	

	public HashMap<String, String> initiateO2CTransfer(HashMap<String, String> initiateMap) throws InterruptedException {
		String PositiveCommissioning = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY").toUpperCase();
		//String O2CApproval_Preference = DBHandler.AccessHandler.getSystemPreference("O2C_APPRV_QTY_LEVEL");
		BigDecimal netPayableAmount = null;
		//int approvalLength = O2CApproval_Preference.split(",").length;
		
/*		ResultMap.put("O2CAPPROVALCOUNT_PREFERENCE", O2CApproval_Preference.split(",")[approvalLength-1]);
		ResultMap.put("FIRSTAPPROVALLIMIT", _masterVO.getProperty("O2CFirstApprovalLimit"));
		ResultMap.put("SECONDAPPROVALLIMIT", _masterVO.getProperty("O2CSecondApprovalLimit"));*/

		login1.LoginAsUser(driver, initiateMap.get("LOGIN_ID"), initiateMap.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		if (initiateMap.get("TO_STOCK_TYPE") != null) 
			initiateMap.put("STOCK_TYPE_DROPDOWN_STATUS", "" + initiateO2CPage.selectDistributionType(initiateMap.get("TO_STOCK_TYPE")));
		else
		{
			initiateMap.put("STOCK_TYPE_DROPDOWN_STATUS", "" + initiateO2CPage.DistributionTypeIsDisplayed());
			if(initiateMap.get("STOCK_TYPE_DROPDOWN_STATUS").equals("false")) 
				return initiateMap;
		}
		initiateO2CPage.enterMobileNumber(initiateMap.get("TO_MSISDN"));
		
		if (initiateMap.get("PRODUCT_TYPE") != null)
			initiateO2CPage.selectProductType1(initiateMap.get("PRODUCT_TYPE"));
		initiateO2CPage.clickSubmitButton();

		
		//Entering transfer details
		
		initiateO2CPage2.enterQuantity(initiateMap.get("INITIATION_AMOUNT"));
		initiateO2CPage2.enterRemarks(initiateMap.get("REMARKS"));
		
		/*
		 * Added a Control to enter Payment Instrument, Payment Instrument Num Payment Instrument Date & Transaction PIN
		 * Payment Details are only entered in case PAYMENTDETAILSMANDATE_O2C == 0;
		 */
		if (isPaymentMethodMandatory()) {
			initiateO2CPage2.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			initiateO2CPage2.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			initiateO2CPage2.enterPaymentInstDate(caHomepage.getDate());
		}
			
		/*
		 * Preference to check if pin need to be entered for O2C Withdraw
		 */
		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(initiateMap.get("TO_CATEGORY"));
		
		if(pinAllowed.equals("Y"))
			initiateO2CPage2.enterPin(initiateMap.get("PIN"));
		
		initiateO2CPage2.clickSubmitButton();

		if(PositiveCommissioning.equals("FALSE")){
		netPayableAmount = new BigDecimal(approveLevel1Page3.fetchNetPayableAmount());	
		}
		else {
		netPayableAmount= new BigDecimal(approveLevel1Page3.fetchreceiverCreditQtyPosCommission());
		}
		
		initiateMap.put("NetPayableAmount",netPayableAmount.toString());
		
		initiateO2CPage3.clickConfirmButton();

		// Fetching O2C transfer Id from the message.
		String message= initiateO2CPage.getMessage();
		
		initiateMap.put("INITIATE_MESSAGE", message);
		
		int index =message.indexOf("OT");
		initiateMap.put("TRANSACTION_ID", message.substring(index).replaceAll("[.]$",""));
		
		return initiateMap;
	}
	
	public String getErrorMessage() {
		if (initiateO2CPage.getMessage() != null)
			return initiateO2CPage.getMessage();
		else
			return initiateO2CPage.getErrorMessage();
		}
	
	public long generateInitiationAmount(String TO_CATEGORY, boolean FirstApproval, boolean SecondApproval, int percentage) {
		final String methodname = "generateInitiationAmount";
		Log.debug("Entered "+ methodname + "("+TO_CATEGORY + ", " + FirstApproval + ", " + SecondApproval + ", " + percentage +")");
		long InitiateAmount = 0;
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(TO_CATEGORY, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		long FirstApprovalLimit = Long.parseLong(_parser.getDisplayAmount(Long.parseLong(approvalLevel[0])));
		long SecondApprovalLimit = Long.parseLong(_parser.getDisplayAmount(Long.parseLong(approvalLevel[1])));
		double percent = ( percentage / 100.0f );
		if (FirstApproval == false && SecondApproval == false) {
			InitiateAmount = (long) (FirstApprovalLimit - (FirstApprovalLimit * percent));
		} else if (FirstApproval == true && SecondApproval == false) {
			InitiateAmount = (long) (SecondApprovalLimit - ((SecondApprovalLimit - FirstApprovalLimit) * percent));
		} else if (FirstApproval == true && SecondApproval == true) {
			InitiateAmount = (long) (SecondApprovalLimit + (SecondApprovalLimit * percent));
		}
		
		Log.debug("Exiting " + methodname + " with InitiateAmount = " + InitiateAmount);
		return InitiateAmount;
	}
	
	public Map<String, String> performingLevel1Approval(String ChannelUserMSISDN, String TransactionID) throws InterruptedException {
		final String methodname = "performingLevel1Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		ResultMap.put("actualMessage", approveLevel1Page.getMessage());
		
		Log.methodExit(methodname);
		return ResultMap;
	}
	
	public Map<String, String> performingLevel1Approval(HashMap<String, String> approvalMap) throws InterruptedException {
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.enterMobileNumber(approvalMap.get("TO_MSISDN"));
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(approvalMap.get("TRANSACTION_ID"));
		approveLevel2Page2.clickSubmitBtn();
		
		if (approvalMap.get("EXTERNAL_TXN_NUM") == null)
			approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		else 
			approveLevel1Page3.enterExternalTxnNum(approvalMap.get("EXTERNAL_TXN_NUM"));
		
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		approvalMap.put("actualMessage", approveLevel1Page.getMessage());
		return approvalMap;
	}


public Map<String, String> performingLevel1ApprovalVoucher(HashMap<String, String> approvalMap, HashMap<String, String> initiateMap, String voucherType) throws InterruptedException {
	String PositiveCommissioning = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY").toUpperCase();	
	BigDecimal netPayableAmount = null;
	if(voucherType.equalsIgnoreCase("electronic")) {
			
			userInfo= UserAccess.getUserWithAccessVoucher(RolesI.O2C_APPROVAL_LEVEL1,"electronic");
		}
		else if(voucherType.equalsIgnoreCase("physical")) {
			userInfo= UserAccess.getUserWithAccessVoucher(RolesI.O2C_APPROVAL_LEVEL1,"physical");
		}
		else
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.enterMobileNumber(approvalMap.get("TO_MSISDN"));
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(approvalMap.get("TRANSACTION_ID"));
		approveLevel2Page2.clickSubmitBtn();
		
		if (approvalMap.get("EXTERNAL_TXN_NUM") == null)
			approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		else 
			approveLevel1Page3.enterExternalTxnNum(approvalMap.get("EXTERNAL_TXN_NUM"));
		
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		//approveLevel1Page3.enterVoucherQuanity(initiateMap.get("quantity"));
		approveLevel1Page3.enterfromSerialNumber(initiateMap.get("fromSerialNumber"));
		approveLevel1Page3.entertoSerialNumber(initiateMap.get("toSerialNumber"));
		approveLevel1Page3.SelectProduct(initiateMap.get("activeProfile"));
		approveLevel1Page3.clickSubmitBtn();
		
		if(PositiveCommissioning.equals("FALSE")){
			netPayableAmount = new BigDecimal(initiateVoucherO2CPage4.fetchNetPayableAmount());	
			}
			else {
			netPayableAmount= new BigDecimal(initiateVoucherO2CPage4.fetchreceiverCreditQtyPosCommission());
			}
			
		initiateMap.put("NetPayableAmount",netPayableAmount.toString());

		approveLevel1Page4.clickConfirmButtonVoucher();
		approvalMap.put("actualMessage", approveLevel1Page.getMessage());
		return approvalMap;
	}

	public String performingLevel2Approval(String ChannelUserMSISDN, String TransactionID, String quantity) throws InterruptedException {
		final String methodname = "performingLevel2Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID, quantity);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL2);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel2();
		approveLevel2Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel2Page.clickSubmitBtn();
		approveLevel2Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		approveLevel2Page3.clickApproveBtn();
		approveLevel2Page4.clickConfirmButton();
		String actualMessage= approveLevel2Page.getMessage();
		
		Log.methodExit(methodname);
		return actualMessage;
	}
	
	public String performingLevel2ApprovalVoucher(String ChannelUserMSISDN, String TransactionID, String quantity,HashMap<String, String> approvalMap,String voucherType) throws InterruptedException {
		final String methodname = "performingLevel2Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID, quantity);
		if(voucherType.equalsIgnoreCase("electronic")) {
			
			userInfo= UserAccess.getUserWithAccessVoucher(RolesI.O2C_APPROVAL_LEVEL1,"electronic");
		}
		else if(voucherType.equalsIgnoreCase("physical")) {
			userInfo= UserAccess.getUserWithAccessVoucher(RolesI.O2C_APPROVAL_LEVEL1,"physical");
		}
		else
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL2);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel2();
		approveLevel2Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel2Page.clickSubmitBtn();
		approveLevel2Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		if (approvalMap.get("EXTERNAL_TXN_NUM") == null)
			approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		else 
			approveLevel1Page3.enterExternalTxnNum(approvalMap.get("EXTERNAL_TXN_NUM"));
		
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel2Page3.clickSubmitBtn();
		approveLevel2Page4.clickConfirmButtonVoucher();
		String actualMessage= approveLevel2Page.getMessage();
		
		Log.methodExit(methodname);
		return actualMessage;
	}

	public String performingLevel3Approval(String ChannelUserMSISDN, String TransactionID, String quantity) throws InterruptedException {
		final String methodname = "performingLevel3Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID, quantity);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL3);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel3();
		approveLevel3Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel3Page.clickSubmitBtn();
		approveLevel3Page2.selectTransferNum(TransactionID);
		approveLevel3Page2.clickSubmitBtn();
		approveLevel3Page3.clickApproveBtn();
		approveLevel3Page4.clickConfirmButton();
		String actualMessage= approveLevel3Page.getMessage();
		
		Log.methodExit(methodname);
		return actualMessage;
	}
	
	public String performingLevel3ApprovalVoucher(String ChannelUserMSISDN, String TransactionID, String quantity,String voucherType) throws InterruptedException {
		final String methodname = "performingLevel3Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID, quantity);
		if(voucherType.equalsIgnoreCase("electronic")) {
			
			userInfo= UserAccess.getUserWithAccessVoucher(RolesI.O2C_APPROVAL_LEVEL1,"electronic");
		}
		else if(voucherType.equalsIgnoreCase("physical")) {
			userInfo= UserAccess.getUserWithAccessVoucher(RolesI.O2C_APPROVAL_LEVEL1,"physical");
		}
		else
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL3);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel3();
		approveLevel3Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel3Page.clickSubmitBtn();
		approveLevel3Page2.selectTransferNum(TransactionID);
		approveLevel3Page2.clickSubmitBtn();
		approveLevel3Page3.clickSubmitBtn();
		approveLevel3Page4.clickConfirmButtonVoucher();
		String actualMessage= approveLevel3Page.getMessage();
		
		Log.methodExit(methodname);
		return actualMessage;
	}
	
	public Map<String, String> initiateTransferByChannelUser(String parentCategory, String category, String productType, String Quantity, String Remarks) throws InterruptedException {
		
		String PositiveCommissioning = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY").toUpperCase();
		String O2CApproval_Preference = DBHandler.AccessHandler.getSystemPreference("O2C_APPRV_QTY_LEVEL");
		double netPayableAmount;
		ResultMap.put("FIRSTAPPROVALLIMIT", _masterVO.getProperty("O2CFirstApprovalLimit"));
		ResultMap.put("SECONDAPPROVALLIMIT", _masterVO.getProperty("O2CSecondApprovalLimit"));
		int productCount = ExcelUtility.getRowCount(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);

		// Initiating O2C Transfer from Channel Admin
		login1.UserLogin(driver, "ChannelUser", parentCategory, category);
		
		CUHomePage.clickO2CTransfer();
		CU_O2CTransfer.clickInitiateTransfer();
		
		boolean selectDropdownVisible = InitiateTransferByChannelUser1.isSelectProductTypeVisible() ;
		if(selectDropdownVisible==true) {
			if(productCount>1) {
				InitiateTransferByChannelUser1.selectProductType(productType);
			}
			else {
				Log.info("Only one product is visible");
			}
		}
		
		
		
		InitiateTransferByChannelUser1.clickSubmitButton();

		//Entering transfer details		
		InitiateTransferByChannelUser2.enterQuantity(Quantity);
		InitiateTransferByChannelUser2.enterRemarks(Remarks);
		
		/*
		 * Added a Control to enter Payment Instrument, Payment Instrument Num Payment Instrument Date & Transaction PIN
		 * Payment Details are only entered in case PAYMENTDETAILSMANDATE_O2C == 0;
		 */
		if (isPaymentMethodMandatory()) {
			InitiateTransferByChannelUser2.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			InitiateTransferByChannelUser2.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			InitiateTransferByChannelUser2.enterPaymentInstDate(caHomepage.getDate());
		}
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCnt=ExcelUtility.getRowCount();
		String PIN=null;
		
		/*
		 * Preference to check if pin need to be entered for O2C Withdraw
		 */
		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(category);
		
		if(pinAllowed.equals("Y")){
			for(int x=1; x<=rowCnt;x++){
				String ParentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, x);
				String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, x);
				if(ParentCategory.equals(parentCategory) && CategoryName.equals(category)) {
				PIN= ExcelUtility.getCellData(0, ExcelI.PIN, x);
				break;
			}
		}
			InitiateTransferByChannelUser2.enterPin(PIN);
		}
		InitiateTransferByChannelUser2.clickSubmitButton();

		if(PositiveCommissioning.equals("FALSE")){
		netPayableAmount = Double.parseDouble(approveLevel1Page3.fetchNetPayableAmount());	
		}
		else {
		netPayableAmount= Double.parseDouble(approveLevel1Page3.fetchreceiverCreditQtyPosCommission());
		}
		
		ResultMap.put("NetPayableAmount",""+netPayableAmount);
		
		InitiateTransferByChannelUser3.clickConfirmButton();

		// Fetching O2C transfer Id from the message.
		String message= InitiateTransferByChannelUser1.getMessage();
		
		ResultMap.put("INITIATE_MESSAGE", message);
		
		int index =message.indexOf("OT");
		ResultMap.put("TRANSACTION_ID", message.substring(index).replaceAll("[.]$",""));
		
		return ResultMap;
	}
	
	public HashMap<String, String> initiateVoucherO2CTransfer(HashMap<String, String> initiateMap, HashMap<String, String> voucherInitiateMap) throws InterruptedException {
		String PositiveCommissioning = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY").toUpperCase();
		//String O2CApproval_Preference = DBHandler.AccessHandler.getSystemPreference("O2C_APPRV_QTY_LEVEL");
		BigDecimal netPayableAmount = null;
		//int approvalLength = O2CApproval_Preference.split(",").length;
		
/*		ResultMap.put("O2CAPPROVALCOUNT_PREFERENCE", O2CApproval_Preference.split(",")[approvalLength-1]);
		ResultMap.put("FIRSTAPPROVALLIMIT", _masterVO.getProperty("O2CFirstApprovalLimit"));
		ResultMap.put("SECONDAPPROVALLIMIT", _masterVO.getProperty("O2CSecondApprovalLimit"));*/
		
		
		login1.LoginAsUser(driver, initiateMap.get("LOGIN_ID"), initiateMap.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		if (initiateMap.get("TO_STOCK_TYPE") != null) 
			initiateMap.put("STOCK_TYPE_DROPDOWN_STATUS", "" + initiateO2CPage.selectDistributionType(initiateMap.get("TO_STOCK_TYPE")));
		else
			initiateMap.put("STOCK_TYPE_DROPDOWN_STATUS", "" + initiateO2CPage.DistributionTypeIsDisplayed());

		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if (HCPT_VMS == 1) {
			initiateO2CPage.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP_NORMAL);
		}
		initiateO2CPage.enterMobileNumber(initiateMap.get("TO_MSISDN"));
		
		if (initiateMap.get("PRODUCT_TYPE") != null)
			initiateO2CPage.selectProductType1(initiateMap.get("PRODUCT_TYPE"));
		initiateO2CPage.clickSubmitButton();

		//Entering transfer details
		initiateVoucherO2CPage2.SelectVoucherType(voucherInitiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment("LC");
		}
		initiateVoucherO2CPage2.SelectDenomination(voucherInitiateMap.get("denomination"));
		initiateVoucherO2CPage2.EnterFromSerialNumber(voucherInitiateMap.get("fromSerialNumber"));
		initiateVoucherO2CPage2.EnterToSerialNumber(voucherInitiateMap.get("toSerialNumber"));
		initiateVoucherO2CPage2.EnterRemarks(voucherInitiateMap.get("remarks"));
		initiateVoucherO2CPage2.ClickonSubmit();
		
		initiateVoucherO2CPage3.SelectProductID(voucherInitiateMap.get("activeProfile"));
		initiateVoucherO2CPage3.SelectPaymentType(PretupsI.PMTYP_CASH_LOOKUP);
		initiateVoucherO2CPage3.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
		initiateVoucherO2CPage3.EnterPaymentDate(com.utils.BTSLDateUtil.getSystemLocaleDate(caHomepage.getDate()));
				
		/*
		 * Preference to check if pin need to be entered for O2C Withdraw
		 */
		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(initiateMap.get("CATEGORY_NAME"));
		
		if(pinAllowed.equals("Y"))
			initiateVoucherO2CPage3.EnterPin(initiateMap.get("PIN"));
		
		initiateVoucherO2CPage3.ClickonSubmit();

		if(PositiveCommissioning.equals("FALSE")){
		netPayableAmount = new BigDecimal(initiateVoucherO2CPage4.fetchNetPayableAmount());	
		}
		else {
		netPayableAmount= new BigDecimal(initiateVoucherO2CPage4.fetchreceiverCreditQtyPosCommission());
		}
		
		initiateMap.put("NetPayableAmount",netPayableAmount.toString());
		
		initiateVoucherO2CPage4.ClickonConfirm();

		// Fetching O2C transfer Id from the message.
		String message= initiateO2CPage.getMessage();
		
		initiateMap.put("INITIATE_MESSAGE", message);
		
		int index =message.indexOf("OT");
		initiateMap.put("TRANSACTION_ID", message.substring(index).replaceAll("[.]$",""));
		
		return initiateMap;
	}
	
	public boolean checkTransactionIdLevel1(String ChannelUserMSISDN, String TransactionID) throws InterruptedException {
		final String methodname = "performingLevel1Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel1Page.clickSubmitBtn();
		
		boolean isVisible = approveLevel1Page2.isVisibleTransferNumber(TransactionID);
		
		Log.methodExit(methodname);
		return isVisible;
	}
	
	public boolean checkCBCApplicableAtLevel1Closed(String ChannelUserMSISDN, String TransactionID) throws InterruptedException {
		final String methodname = "performingLevel1Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel1Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();
		
		boolean isVisible = approveLevel1Page4.isVisibleFieldInTable("CBC");//Replace hardcoded value from key from properties file
		
		Log.methodExit(methodname);
		return isVisible;
	}
	
	public boolean checkCBCApplicableAtLevel2Closed(String ChannelUserMSISDN, String TransactionID) throws InterruptedException {
		final String methodname = "performingLevel1Approval";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL2);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel2Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel2Page.clickSubmitBtn();
		approveLevel2Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel2Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		approveLevel2Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel2Page3.clickApproveBtn();
		
		boolean isVisible = approveLevel1Page4.isVisibleFieldInTable("CBC");//Replace hardcoded value from key from properties file
		
		Log.methodExit(methodname);
		return isVisible;
	}
	
	public Map<String, String> performingLevel1ApprovalAfterBackRemoveQuant(HashMap<String, String> approvalMap) throws InterruptedException {
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.enterMobileNumber(approvalMap.get("TO_MSISDN"));
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(approvalMap.get("TRANSACTION_ID"));
		approveLevel2Page2.clickSubmitBtn();
		
		if (approvalMap.get("EXTERNAL_TXN_NUM") == null)
			approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		else 
			approveLevel1Page3.enterExternalTxnNum(approvalMap.get("EXTERNAL_TXN_NUM"));
		
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickBackButton();
		
		approveLevel1Page3.clearApproval1Quantity();
		approveLevel1Page3.clickApproveBtn();
		
		approveLevel1Page4.clickConfirmButton();
		approvalMap.put("actualMessage", approveLevel1Page.getMessage());
		return approvalMap;
	}
	
	
	public String performingApproval1BackNetComm(HashMap<String, String> approvalMap) throws InterruptedException {
		final String methodname = "performingApproval1BackNetComm";
		String msisdn = approvalMap.get("TO_MSISDN");
		String transactionId = approvalMap.get("TRANSACTION_ID");
		Log.methodEntry(methodname, msisdn, transactionId);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.enterMobileNumber(msisdn);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(transactionId);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();
		
		approveLevel1Page4.clickBackButton();
		
		// TODO Need to validate net commission value
		
		Log.methodExit(methodname);
		return "";
	}
	
	public String performingApproval2BackNetComm(HashMap<String, String> approvalMap) throws InterruptedException {
		final String methodname = "performingApproval2BackNetComm";
		String msisdn = approvalMap.get("TO_MSISDN");
		String transactionId = approvalMap.get("TRANSACTION_ID");
		Log.methodEntry(methodname, msisdn, transactionId);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL2);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel2();
		approveLevel2Page.enterMobileNumber(msisdn);
		approveLevel2Page.clickSubmitBtn();
		approveLevel2Page2.selectTransferNum(transactionId);
		approveLevel2Page2.clickSubmitBtn();
		approveLevel2Page3.clickApproveBtn();
		
		approveLevel2Page4.clickBackButton();
		
		// TODO Need to validate net commission value
		
		Log.methodExit(methodname);
		return "";
	}
	
	public String performingApproval3BackNetComm(HashMap<String, String> approvalMap) throws InterruptedException {
		final String methodname = "performingApproval3BackNetComm";
		String msisdn = approvalMap.get("TO_MSISDN");
		String transactionId = approvalMap.get("TRANSACTION_ID");
		Log.methodEntry(methodname, msisdn, transactionId);
		
		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL3);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel3();
		approveLevel3Page.enterMobileNumber(msisdn);
		approveLevel3Page.clickSubmitBtn();
		approveLevel3Page2.selectTransferNum(transactionId);
		approveLevel3Page2.clickSubmitBtn();
		approveLevel3Page3.clickApproveBtn();
		
		approveLevel3Page4.clickBackButton();
		
		// TODO Need to validate net commission value
		
		Log.methodExit(methodname);
		return "";
	}

	public Map<String, String> initiateTransferForBundle(String userMSISDN, String productType, int quantity)
			throws InterruptedException {
		final String methodname = "initiateTransferForBundle";
		Log.methodEntry(methodname, userMSISDN, productType);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		int rowCount = ExcelUtility.getRowCount();
		Object[] bundles = new Object[rowCount];
		bundles = loadBundles(rowCount);
		for (int i = 0; i < rowCount; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) bundles[i]);
			Log.info(temp.get("voucherBundles"));
		}

		// Initiating O2C Transfer from Channel Admin
		userInfo = UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		initiateO2CPage.selectDistributionType(PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		initiateO2CPage.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		initiateO2CPage.enterMobileNumber(userMSISDN);
		initiateO2CPage.selectProductType1(productType);
		initiateO2CPage.clickSubmitButton();

		int price, totalExpectedMRP = 0;
		for (int i = 0; i < rowCount; i++) {
			if (bundles[i] != null) {
				initiateMap.putAll((Map<? extends String, ? extends String>) bundles[i]);
				try {
					initiateO2CPage4.selectVoucherBundleByIndex(i,
							initiateMap.get("voucherBundle"));
					initiateO2CPage4.enterQuantityByIndex(i, Integer.toString(quantity));
					price = quantity * Integer.parseInt(initiateMap.get("mrp"));
					totalExpectedMRP += price;
				} catch (NoSuchElementException e) {
				}
			}
		}
		initiateO2CPage4.enterRemarks("Test Automation");
		initiateO2CPage4.clickSubmitButton();
		initiateO2CPage5.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
		initiateO2CPage5.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
		initiateO2CPage5.enterPaymentInstDate(caHomepage.getDate());
		initiateO2CPage5.clickSubmitButton();
		initiateO2CPage6.clickConfirmButton();
		// Fetching O2C Transfer Success Message.
		String message = initiateO2CPage.getMessage();
		ResultMap.put("INITIATE_MESSAGE", message);
		ResultMap.put("TRANSACTION_ID", _parser.getTransactionID(message, PretupsI.CHANNEL_TRANSFER_O2C_ID));
		ResultMap.put("NetPayableAmount", totalExpectedMRP+"");
		Log.methodExit(methodname);
		return ResultMap;
	}
	
	public Map<String, String> performingLevel1ApprovalPackage(String ChannelUserMSISDN, String TransactionID)
			throws InterruptedException {
		final String methodname = "performingLevel1ApprovalPackage";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID);

		userInfo = UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		approveLevel1Page.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		approveLevel1Page.selectDomain(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel1Page.selectCategory(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel1Page2.clickSubmitBtn();

		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtnPackage();

		approveLevel1Page4.clickConfirmButtonVoucher();
		ResultMap.put("actualMessage", approveLevel1Page.getMessage());
		Log.methodExit(methodname);
		return ResultMap;
	}

	public String performingLevel2ApprovalPackage(String ChannelUserMSISDN, String TransactionID, int quantity)
			throws InterruptedException {
		final String methodname = "performingLevel2ApprovalPackage";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID, quantity);

		userInfo = UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL2);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel2();
		approveLevel2Page.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		approveLevel2Page.selectDomain(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel2Page.selectCategory(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel2Page.clickSubmitBtn();
		approveLevel2Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		approveLevel2Page3.clickSubmitBtnPackage();
		approveLevel2Page4.clickConfirmButtonVoucher();
		String actualMessage = approveLevel2Page.getMessage();

		Log.methodExit(methodname);
		return actualMessage;
	}

	public String performingLevel3ApprovalPackage(String ChannelUserMSISDN, String TransactionID, int quantity)
			throws InterruptedException {
		final String methodname = "performingLevel3ApprovalPackage";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID, quantity);

		userInfo = UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL3);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel3();
		approveLevel3Page.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		approveLevel3Page.selectDomain(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel3Page.selectCategory(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel3Page.clickSubmitBtn();
		approveLevel3Page2.selectTransferNum(TransactionID);
		approveLevel3Page2.clickSubmitBtn();
		approveLevel3Page3.clickSubmitBtnPackage();
		approveLevel3Page4.clickConfirmButtonVoucher();
		String actualMessage = approveLevel3Page.getMessage();

		Log.methodExit(methodname);
		return actualMessage;
	}

	public Map<String, String> initiateTransferForBundleNegative001(String userMSISDN, String productType,
			String Remarks) throws InterruptedException {
		final String methodname = "initiateTransferForBundle";
		Log.methodEntry(methodname, userMSISDN, productType, Remarks);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		int rowCount = ExcelUtility.getRowCount();
		Object[] bundles = new Object[rowCount];
		bundles = loadBundles(rowCount);
		for (int i = 0; i < rowCount; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) bundles[i]);
			Log.info(temp.get("voucherBundles"));
		}

		// Initiating O2C Transfer from Channel Admin
		userInfo = UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		initiateO2CPage.selectDistributionType(PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		initiateO2CPage.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		initiateO2CPage.enterMobileNumber(userMSISDN);
		initiateO2CPage.selectProductType1(productType);
		initiateO2CPage.clickSubmitButton();
		initiateO2CPage4.clickSubmitButton();

		// Fetching O2C Transfer Success Message.
		String message = initiateO2CPage.getErrorMessage();
		ResultMap.put("ERROR_MESSAGE", message);
		Log.methodExit(methodname);
		return ResultMap;
	}

	public Map<String, String> initiateTransferForBundleNegative002(String userMSISDN, String productType)
			throws InterruptedException {
		final String methodname = "initiateTransferForBundleNegative002";
		Log.methodEntry(methodname, userMSISDN, productType);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		int rowCount = ExcelUtility.getRowCount();
		Object[] bundles = new Object[rowCount];
		bundles = loadBundles(rowCount);
		for (int i = 0; i < rowCount; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) bundles[i]);
			Log.info(temp.get("voucherBundles"));
		}

		// Initiating O2C Transfer from Channel Admin
		userInfo = UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		initiateO2CPage.selectDistributionType(PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		initiateO2CPage.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		initiateO2CPage.enterMobileNumber(userMSISDN);
		initiateO2CPage.selectProductType1(productType);
		initiateO2CPage.clickSubmitButton();

		int quantity, price, totalExpectedMRP = 0;
		for (int i = 0; i < rowCount; i++) {
			if (bundles[i] != null) {
				initiateMap.putAll((Map<? extends String, ? extends String>) bundles[i]);
				try {
					initiateO2CPage4.selectVoucherBundleByIndex(i,
							initiateMap.get("voucherBundle"));
					quantity = Integer.parseInt(randStr.randomNumeric(1))+1;
					initiateO2CPage4.enterQuantityByIndex(i, Integer.toString(quantity));
					price = quantity * Integer.parseInt(initiateMap.get("mrp"));
					totalExpectedMRP += price;
				} catch (NoSuchElementException e) {
				}
			}
		}

		initiateO2CPage4.clickSubmitButton();

		// Fetching O2C Transfer Success Message.
		String message = initiateO2CPage.getErrorMessage();
		ResultMap.put("ERROR_MESSAGE", message);
		Log.methodExit(methodname);
		return ResultMap;
	}

	public Map<String, String> initiateTransferForBundleNegative003(String userMSISDN, String productType)
			throws InterruptedException {
		final String methodname = "initiateTransferForBundleNegative003";
		Log.methodEntry(methodname, userMSISDN, productType);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		int rowCount = ExcelUtility.getRowCount();
		Object[] bundles = new Object[rowCount];
		bundles = loadBundles(rowCount);
		for (int i = 0; i < rowCount; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) bundles[i]);
			Log.info(temp.get("voucherBundles"));
		}

		// Initiating O2C Transfer from Channel Admin
		userInfo = UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		initiateO2CPage.selectDistributionType(PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		initiateO2CPage.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		initiateO2CPage.enterMobileNumber(userMSISDN);
		initiateO2CPage.selectProductType1(productType);
		initiateO2CPage.clickSubmitButton();

		int quantity, price, totalExpectedMRP = 0;
		for (int i = 0; i < rowCount; i++) {
			if (bundles[i] != null) {
				initiateMap.putAll((Map<? extends String, ? extends String>) bundles[i]);
				try {
					initiateO2CPage4.selectVoucherBundleByIndex(i,
							initiateMap.get("voucherBundle"));
					quantity = Integer.parseInt(randStr.randomNumeric(1))+1;
					initiateO2CPage4.enterQuantityByIndex(i, Integer.toString(quantity));
					price = quantity * Integer.parseInt(initiateMap.get("mrp"));
					totalExpectedMRP += price;
				} catch (NoSuchElementException e) {
					
				}
			}
		}
		initiateO2CPage4.enterRemarks("Test Automation");
		initiateO2CPage4.clickSubmitButton();
		initiateO2CPage5.clickSubmitButton();

		// Fetching O2C Transfer Success Message.
		String message = initiateO2CPage.getErrorMessage();
		ResultMap.put("ERROR_MESSAGE", message);
		Log.methodExit(methodname);
		return ResultMap;
	}

	public Map<String, String> initiateTransferForBundleNegative004(String userMSISDN, String productType)
			throws InterruptedException {
		final String methodname = "initiateTransferForBundleNegative004";
		Log.methodEntry(methodname, userMSISDN, productType);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		int rowCount = ExcelUtility.getRowCount();
		Object[] bundles = new Object[rowCount];
		bundles = loadBundles(rowCount);
		for (int i = 0; i < rowCount; i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.putAll((Map<? extends String, ? extends String>) bundles[i]);
			Log.info(temp.get("voucherBundles"));
		}

		// Initiating O2C Transfer from Channel Admin
		userInfo = UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		caHomepage.clickInitiateTransfer();
		initiateO2CPage.selectDistributionType(PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		initiateO2CPage.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		initiateO2CPage.enterMobileNumber(userMSISDN);
		initiateO2CPage.selectProductType1(productType);
		initiateO2CPage.clickSubmitButton();

		int quantity, price, totalExpectedMRP = 0;
		for (int i = 0; i < rowCount; i++) {
			if (bundles[i] != null) {
				initiateMap.putAll((Map<? extends String, ? extends String>) bundles[i]);
				try {
					initiateO2CPage4.selectVoucherBundleByIndex(i,
							initiateMap.get("voucherBundle"));
					quantity = Integer.parseInt(randStr.randomNumeric(1))+1;
					initiateO2CPage4.enterQuantityByIndex(i, Integer.toString(quantity));
					price = quantity * Integer.parseInt(initiateMap.get("mrp"));
					totalExpectedMRP += price;
				} catch (NoSuchElementException e) {
				}
			}
		}
		initiateO2CPage4.enterRemarks("Test Automation");
		initiateO2CPage4.clickSubmitButton();
		initiateO2CPage5.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
		initiateO2CPage5.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
		initiateO2CPage5.clickSubmitButton();

		// Fetching O2C Transfer Success Message.
		String message = initiateO2CPage.getErrorMessage();
		ResultMap.put("ERROR_MESSAGE", message);
		Log.methodExit(methodname);
		return ResultMap;
	}

	public String performingLevel1ApprovalPackageNEG(String ChannelUserMSISDN, String TransactionID)
			throws InterruptedException {
		final String methodname = "performingLevel1ApprovalPackageNEG";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID);

		userInfo = UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickApproveLevel1();
		// approveLevel1Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel1Page.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		approveLevel1Page.selectDomain(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel1Page.selectCategory(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel1Page2.clickSubmitBtn();

		approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtnPackage();

		Log.methodExit(methodname);
		String actualMessage = approveLevel1Page.getErrorMessage();
		return actualMessage;

	}

	public String performingLevel3ApprovalPackageNEG(String ChannelUserMSISDN, String TransactionID, int quantity)
			throws InterruptedException {
		final String methodname = "performingLevel3ApprovalPackage";
		Log.methodEntry(methodname, ChannelUserMSISDN, TransactionID, quantity);

		userInfo = UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL3);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel3();
		// approveLevel3Page.enterMobileNumber(ChannelUserMSISDN);
		approveLevel3Page.selectDistributionMode(PretupsI.O2C_DIST_MODE_LOOKUP);
		approveLevel3Page.selectDomain(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel3Page.selectCategory(PretupsI.GATEWAY_TYPE_ALL);
		approveLevel3Page.clickSubmitBtn();
		approveLevel3Page2.selectTransferNum(TransactionID);
		approveLevel3Page2.clickSubmitBtn();
		approveLevel3Page3.clickSubmitBtnPackage();
		approveLevel3Page4.clickConfirmButtonVoucher();
		String actualMessage = approveLevel3Page.getErrorMessage();

		Log.methodExit(methodname);
		return actualMessage;
	}

	/**************************************************************************/
	/**					     	UTILITIES SECTION                             */
	/**************************************************************************/
	private boolean isPaymentMethodMandatory() {
		Log.info("Entered :: isPaymentMethodMandatory()");
		int isPaymentDetailsMandate = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATE_O2C"));
		if (isPaymentDetailsMandate != -1 && isPaymentDetailsMandate == 0) {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=true");
			return true;
		} else {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=false");
			return false;
		}
	}

	public Object[] loadBundles(int rowCount) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		Object[] dataObj = new Object[rowCount];
		int objCounter = 0;
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherBundle", ExcelUtility.getCellData(0, ExcelI.BUNDLE_NAME, i));
			VomsData.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
			dataObj[objCounter++] = VomsData.clone();
		}
		return dataObj;
	}

}
