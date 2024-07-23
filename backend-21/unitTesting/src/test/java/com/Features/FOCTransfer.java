package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeladminpages.homepage.OptToChanSubCatPage;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_1_Page_1;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_1_Page_2;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_1_Page_3;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_1_Page_4;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_2_Page_1;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_2_Page_2;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_2_Page_3;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_2_Page_4;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_3_Page_1;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_3_Page_2;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_3_Page_3;
import com.pageobjects.channeladminpages.o2ctransfer.FOC_Approval_3_Page_4;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateFOCTransferPage;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateFOCTransferPage_2;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateFOCTransferPage_3;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;

public class FOCTransfer {

	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	InitiateFOCTransferPage initiateFOCPage;
	InitiateFOCTransferPage_2 initiateFOCPage2;
	InitiateFOCTransferPage_3 initiateFOCPage3;
	
	//FOC Approval Level 1
	FOC_Approval_1_Page_1 FirstApprovalLevel_1;
	FOC_Approval_1_Page_2 FirstApprovalLevel_2;
	FOC_Approval_1_Page_3 FirstApprovalLevel_3;
	FOC_Approval_1_Page_4 FirstApprovalLevel_4;
	
	//FOC Approval Level 2
	FOC_Approval_2_Page_1 SecondApprovalLevel_1;
	FOC_Approval_2_Page_2 SecondApprovalLevel_2;
	FOC_Approval_2_Page_3 SecondApprovalLevel_3;
	FOC_Approval_2_Page_4 SecondApprovalLevel_4;
	
	//FOC Approval Level 3
	FOC_Approval_3_Page_1 ThirdApprovalLevel_1;
	FOC_Approval_3_Page_2 ThirdApprovalLevel_2;
	FOC_Approval_3_Page_3 ThirdApprovalLevel_3;
	FOC_Approval_3_Page_4 ThirdApprovalLevel_4;

	OptToChanSubCatPage optToChanSubPage;
	RandomGeneration randmGenrtr;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;

	ChannelEnquirySubCategories channelEnqSub;

	public FOCTransfer(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		initiateFOCPage = new InitiateFOCTransferPage(driver);
		initiateFOCPage2 = new InitiateFOCTransferPage_2(driver);
		initiateFOCPage3 = new InitiateFOCTransferPage_3(driver);
		FirstApprovalLevel_1 = new FOC_Approval_1_Page_1(driver);
		FirstApprovalLevel_2 = new FOC_Approval_1_Page_2(driver);
		FirstApprovalLevel_3 = new FOC_Approval_1_Page_3(driver);
		FirstApprovalLevel_4 = new FOC_Approval_1_Page_4(driver);
		
		SecondApprovalLevel_1 = new FOC_Approval_2_Page_1(driver);
		SecondApprovalLevel_2 = new FOC_Approval_2_Page_2(driver);
		SecondApprovalLevel_3 = new FOC_Approval_2_Page_3(driver);
		SecondApprovalLevel_4 = new FOC_Approval_2_Page_4(driver);
		
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		optToChanSubPage = new OptToChanSubCatPage(driver);
		randmGenrtr = new RandomGeneration();
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
	}

	public Map<String, String> initiateFOCTransfer(String userMSISDN, String productType, String Quantity,String productName, String Remarks) {
		
		Map<String, String> ResultMap = new HashMap<String, String>();
		
		String FOC_Approval_Levels = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
		ResultMap.put("PREF_FOC_APPROVAL_LEVEL", FOC_Approval_Levels);
		
		// Initiating FOC Transfer from Channel Admin
		userInfo= UserAccess.getUserWithAccess(RolesI.INITIATE_FOC_TRANSFER_ROLECODE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		
		//Selecting Network If Required
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickInitiateFOCTransfer();
		
		initiateFOCPage.enterMobileNumber(userMSISDN);
		boolean selectDropdownVisible = initiateFOCPage.isSelectProductTypeVisible() ;
		if(selectDropdownVisible==true) {
			initiateFOCPage.selectProductType1(productType);
		}
		initiateFOCPage.clickSubmitButton();

		//Entering transfer details
		initiateFOCPage2.enterQuantitywithname(Quantity,productName);
		initiateFOCPage2.enterRemarks(Remarks);
		
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
				{
					PIN= ExcelUtility.getCellData(0, ExcelI.PIN, x);
					break;
				}
			}
			initiateFOCPage2.enterPin(PIN);
		}
		
		initiateFOCPage2.clickSubmitButton();
		initiateFOCPage3.clickConfirmButton();

		// Fetching O2C transfer Id from the message.
		String message= initiateFOCPage.getMessage();
		ResultMap.put("INITIATE_MESSAGE", message);
		ResultMap.put("TRANSACTION_ID", _parser.getTransactionID(message, PretupsI.CHANNEL_TRANSFER_O2C_ID));
		return ResultMap;
	}

	public String performFOCApprovalLevel1(String ChannelUserMSISDN, String TransactionID) throws InterruptedException {

		userInfo= UserAccess.getUserWithAccess(RolesI.FOC_APPROVAL_LEVEL1);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickFOCApprovalLevel1();
		
		FirstApprovalLevel_1.enterMobileNumber(ChannelUserMSISDN);
		FirstApprovalLevel_1.clickSubmitBtn();

		FirstApprovalLevel_2.selectTransactionID(TransactionID);
		FirstApprovalLevel_2.clickSubmitBtn();

		if (PretupsI.TRANSFER_EXTERNAL_TXN_FIRST_LEVEL.equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreferenceDefaultValue("EXTTXNLEVEL"))) {
			FirstApprovalLevel_3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
			FirstApprovalLevel_3.enterExternalTxnDate(caHomepage.getDate());
		}
		
		FirstApprovalLevel_3.clickApproveBtn();

		FirstApprovalLevel_4.clickConfirmButton();
		String actualMessage= FirstApprovalLevel_1.getMessage();
		return actualMessage;
	}

	public String performFOCApprovalLevel2(String ChannelUserMSISDN, String TransactionID, String quantity) {

		userInfo= UserAccess.getUserWithAccess(RolesI.FOC_APPROVAL_LEVEL2);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickFOCApprovalLevel2();
		SecondApprovalLevel_1.enterMobileNumber(ChannelUserMSISDN);
		SecondApprovalLevel_1.clickSubmitBtn();
		SecondApprovalLevel_2.selectTransactionID(TransactionID);
		SecondApprovalLevel_2.clickSubmitButton();
		SecondApprovalLevel_3.clickApproveBtn();
		SecondApprovalLevel_4.clickConfirmButton();
		String actualMessage= SecondApprovalLevel_1.getMessage();
		return actualMessage;

	}

	public String performFOCApprovalLevel3(String ChannelUserMSISDN, String TransactionID, String quantity) {

		userInfo= UserAccess.getUserWithAccess(RolesI.O2C_APPROVAL_LEVEL3);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();

		optToChanSubPage.clickApproveLevel3();
		ThirdApprovalLevel_1.enterMobileNumber(ChannelUserMSISDN);
		ThirdApprovalLevel_1.clickSubmitBtn();
		ThirdApprovalLevel_2.selectTransactionID(TransactionID);
		ThirdApprovalLevel_2.clickSubmitBtn();
		ThirdApprovalLevel_3.clickApproveBtn();
		ThirdApprovalLevel_4.clickConfirmButton();
		String actualMessage= ThirdApprovalLevel_1.getMessage();
		return actualMessage;

	}
	
	
	/**
	 * FOC Transfer Reject at Level 1 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String rejectFOCApprovalLevel1(String userMSISDN, String TransactionID) {
		//Operator User Access Implementation by Krishan.
		userInfo = UserAccess.getUserWithAccess(RolesI.FOC_APPROVAL_LEVEL1); //Getting User with Access to Approve Network Stock
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		//User Access module ends.

		//Selecting Network If Required		
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		
		optToChanSubPage.clickFOCApprovalLevel1();
		
		FirstApprovalLevel_1.enterMobileNumber(userMSISDN);
		FirstApprovalLevel_1.clickSubmitBtn();

		FirstApprovalLevel_2.selectTransactionID(TransactionID);
		FirstApprovalLevel_2.clickSubmitBtn();
		
		FirstApprovalLevel_3.clickRejectBtn();
		FirstApprovalLevel_4.clickConfirmButton();
		
		String RejectionMessage = FirstApprovalLevel_1.getMessage();
		return RejectionMessage;
		}

	/**
	 * FOC Transfer Reject at Level 2 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String rejectFOCApprovalLevel2(String userMSISDN, String TransactionID) {
		//Operator User Access Implementation by Krishan.
		userInfo = UserAccess.getUserWithAccess(RolesI.FOC_APPROVAL_LEVEL2); //Getting User with Access to Approve Network Stock
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		//User Access module ends.

		//Selecting Network If Required		
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		
		optToChanSubPage.clickFOCApprovalLevel2();
		
		SecondApprovalLevel_1.enterMobileNumber(userMSISDN);
		SecondApprovalLevel_1.clickSubmitBtn();

		SecondApprovalLevel_2.selectTransactionID(TransactionID);
		SecondApprovalLevel_2.clickSubmitButton();
		
		SecondApprovalLevel_3.clickRejectBtn();
		SecondApprovalLevel_4.clickConfirmButton();
		
		String RejectionMessage = SecondApprovalLevel_1.getMessage();
		return RejectionMessage;
		}
	
	/**
	 * FOC Transfer Reject at Level 3 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String rejectFOCApprovalLevel3(String userMSISDN, String TransactionID) {
		//Operator User Access Implementation by Krishan.
		userInfo = UserAccess.getUserWithAccess(RolesI.FOC_APPROVAL_LEVEL3); //Getting User with Access to Approve Network Stock
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		//User Access module ends.

		//Selecting Network If Required		
		ntwrkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		
		optToChanSubPage.clickFOCApprovalLevel2();
		
		ThirdApprovalLevel_1.enterMobileNumber(userMSISDN);
		ThirdApprovalLevel_1.clickSubmitBtn();

		ThirdApprovalLevel_2.selectTransactionID(TransactionID);
		ThirdApprovalLevel_2.clickSubmitBtn();
		
		ThirdApprovalLevel_3.clickRejectBtn();
		ThirdApprovalLevel_4.clickConfirmButton();
		
		String RejectionMessage = ThirdApprovalLevel_1.getMessage();
		return RejectionMessage;
		}
	
	public String getErrorMessage() {
		if (initiateFOCPage.getMessage() != null)
			return initiateFOCPage.getMessage();
		else
			return initiateFOCPage.getErrorMessage();
		}
}
