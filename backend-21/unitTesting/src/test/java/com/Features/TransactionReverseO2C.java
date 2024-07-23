package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.TransactionReverse.O2CTxnReverseChannelUserSelectPage;
import com.pageobjects.channeladminpages.TransactionReverse.O2CTxnReverseConfirmPage;
import com.pageobjects.channeladminpages.TransactionReverse.O2CTxnReverseDetailsPage;
import com.pageobjects.channeladminpages.TransactionReverse.O2CTxnReverseTransactionSelectPage;
import com.pageobjects.channeladminpages.TransactionReverse.TransactionReverseO2CPage;
import com.pageobjects.channeladminpages.TransactionReverse.TransactionReverseSubCategories;
import com.pageobjects.channeladminpages.TransactionReverse.TxnRvrseO2CPage2;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExtentI;
import com.utils.RandomGeneration;

public class TransactionReverseO2C {
	
	
	WebDriver driver;
	Login login1;
	ChannelAdminHomePage caHomepage;
	TransactionReverseSubCategories TransactionReverseSubCategories;
	TransactionReverseO2CPage TransactionReverseO2CPage1;
	TxnRvrseO2CPage2 TxnRvrseO2CPage2;
	O2CTxnReverseDetailsPage O2CTxnReverseDetailsPage;
	O2CTxnReverseConfirmPage O2CTxnReverseConfirmPage;
	O2CTxnReverseTransactionSelectPage O2CTxnReverseTransactionSelectPage;
	O2CTxnReverseChannelUserSelectPage O2CTxnReverseChannelUserSelectPage;
	RandomGeneration randmGenrtr;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;
	
	
	
	
	
	public TransactionReverseO2C(WebDriver driver){
	
	this.driver = driver;
	login1 = new Login();
	caHomepage = new ChannelAdminHomePage(driver);
	TransactionReverseSubCategories = new TransactionReverseSubCategories(driver);
	O2CTxnReverseDetailsPage = new O2CTxnReverseDetailsPage(driver);
	O2CTxnReverseConfirmPage = new O2CTxnReverseConfirmPage(driver);
	TransactionReverseO2CPage1 = new TransactionReverseO2CPage(driver);
	O2CTxnReverseTransactionSelectPage = new O2CTxnReverseTransactionSelectPage(driver);
	O2CTxnReverseChannelUserSelectPage = new O2CTxnReverseChannelUserSelectPage(driver);
	randmGenrtr = new RandomGeneration();
	ntwrkPage = new SelectNetworkPage(driver);
	userInfo= new HashMap<String, String>();
	ResultMap = new HashMap<String, String>();
	
}
	
	
	

	
	public String initiateO2CTxnReverse(String TransferID) throws InterruptedException {
		
		
		// Initiating O2C Transaction Reverse from Channel Admin
		userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickTransactionReverse();
		TransactionReverseSubCategories.click_O2C_ReverseTxnLink();
		TransactionReverseO2CPage1.EnterTransferNum(TransferID);
		TransactionReverseO2CPage1.clickSubmit();
		O2CTxnReverseDetailsPage.enterRemarks("txn reversed");
		O2CTxnReverseDetailsPage.clickReverseTransaction();
		O2CTxnReverseConfirmPage.clickConfirm();
		
		String actual = TransactionReverseO2CPage1.getMessage();
		
		//ResultMap.put("ACTUAL MESSAGE", actual);
		
		return actual;
	
	}
	
	
	
public String initiateO2CTxnReverseWithMSISDN(String MSISDN, String TransferNumber) throws InterruptedException {
		
		
		// Initiating O2C Transaction Reverse from Channel Admin
		userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickTransactionReverse();
		TransactionReverseSubCategories.click_O2C_ReverseTxnLink();
		TransactionReverseO2CPage1.EnterMobileNumber(MSISDN);
		TransactionReverseO2CPage1.selectTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
		TransactionReverseO2CPage1.EnterFromDateForMSISDN(caHomepage.getDate());
		TransactionReverseO2CPage1.EnterToDateForMSISDN(caHomepage.getDate());
		TransactionReverseO2CPage1.clickSubmit();
		
		String Heading = O2CTxnReverseTransactionSelectPage.getPageHeading();
		String ExpectedHeading = MessagesDAO.getLabelByKey("o2cchannelreversetrx.reverse.viewtransferlist.heading");
		if (Heading.equals(ExpectedHeading)){
		O2CTxnReverseTransactionSelectPage.selectTransferNum(TransferNumber);
		O2CTxnReverseTransactionSelectPage.clickreverseTxnButton();
		}
		
		O2CTxnReverseDetailsPage.enterRemarks("txn reversed");
		O2CTxnReverseDetailsPage.clickReverseTransaction();
		O2CTxnReverseConfirmPage.clickConfirm();
		String actual = TransactionReverseO2CPage1.getMessage();
		
		return actual;
	
	}



public String initiateO2CTxnReverseWithMSISDN_neg(String MSISDN, String TransferNumber) throws InterruptedException {
	
	
	// Initiating O2C Transaction Reverse from Channel Admin
	userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
	login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
	ntwrkPage.selectNetwork();
	caHomepage.clickTransactionReverse();
	TransactionReverseSubCategories.click_O2C_ReverseTxnLink();
	TransactionReverseO2CPage1.EnterMobileNumber(MSISDN);
	TransactionReverseO2CPage1.selectTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
	TransactionReverseO2CPage1.EnterFromDateForMSISDN(caHomepage.getDate());
	TransactionReverseO2CPage1.EnterToDateForMSISDN(caHomepage.getDate());
	TransactionReverseO2CPage1.clickSubmit();
	
	String Heading = O2CTxnReverseTransactionSelectPage.getPageHeading();
	String ExpectedHeading = MessagesDAO.getLabelByKey("o2cchannelreversetrx.reverse.viewtransferlist.heading");
	if (Heading.equals(ExpectedHeading)){
	O2CTxnReverseTransactionSelectPage.selectTransferNum(TransferNumber);
	O2CTxnReverseTransactionSelectPage.clickreverseTxnButton();
	}
	
	O2CTxnReverseDetailsPage.enterRemarks("txn reversed");
	O2CTxnReverseDetailsPage.clickReverseTransaction();
	O2CTxnReverseConfirmPage.clickConfirm();
	String actual = TransactionReverseO2CPage1.getErrorMessage();
	
	return actual;

}
	
public String initiateO2CTxnReverseWithDomain(String geoDomain, String domain,String product,String category,String userMSISDN, String TransferId) throws InterruptedException {
	
	//String MasterSheetPath = _masterVO.getProperty("DataProvider");
	// Initiating O2C Transaction Reverse from Channel Admin
	userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
	login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
	ntwrkPage.selectNetwork();
	caHomepage.clickTransactionReverse();
	TransactionReverseSubCategories.click_O2C_ReverseTxnLink();
	
	
	TransactionReverseO2CPage1.selectGeographyDomain(geoDomain);
	TransactionReverseO2CPage1.selectDomain(domain);
	TransactionReverseO2CPage1.selectProductType1(product);
	TransactionReverseO2CPage1.selectTransferCategory2(PretupsI.TRANSFER_CATEGORY_SALE);
	TransactionReverseO2CPage1.selectCategory(category);
	
	
	TransactionReverseO2CPage1.EnterFromDate(caHomepage.getDate());
	TransactionReverseO2CPage1.EnterToDate(caHomepage.getDate());
	TransactionReverseO2CPage1.clickSubmit();
	
	int rowNo = ExtentI.combinationExistAtRow(new String[]{ExcelI.MSISDN}, new String[]{userMSISDN}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	String userName = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, rowNo);
	String[] ownerUserName = DBHandler.AccessHandler.getOwnerUserDetails(ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, rowNo), ExcelI.USER_NAME);
	
	/*
	ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	int totalRow1 = ExcelUtility.getRowCount();

	int i=1;
	for( i=1; i<=totalRow1;i++)

	{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, i).matches(userMSISDN)))

		break;
	}

	System.out.println(i);
	
	
	String userName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
	System.out.println(userName);*/

	O2CTxnReverseChannelUserSelectPage.EnterOwneruserName(ownerUserName[0]);
	O2CTxnReverseChannelUserSelectPage.EnterChanneluserName(userName);
	O2CTxnReverseChannelUserSelectPage.clickSubmit();
	String Heading = O2CTxnReverseTransactionSelectPage.getPageHeading();
	String ExpectedHeading = MessagesDAO.getLabelByKey("o2cchannelreversetrx.reverse.viewtransferlist.heading");
	if (Heading.equals(ExpectedHeading)){
	O2CTxnReverseTransactionSelectPage.selectTransferNum(TransferId);
	O2CTxnReverseTransactionSelectPage.clickreverseTxnButton();
	}
	O2CTxnReverseDetailsPage.enterRemarks("txn reversed");
	O2CTxnReverseDetailsPage.clickReverseTransaction();
	O2CTxnReverseConfirmPage.clickConfirm();
	
	String actual = TransactionReverseO2CPage1.getMessage();
	
	return actual;

}

	
	
	

}
