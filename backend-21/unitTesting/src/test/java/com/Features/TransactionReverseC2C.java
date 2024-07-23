package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.TransactionReverse.C2CTxnReverseChannelUserSelectPage;
import com.pageobjects.channeladminpages.TransactionReverse.C2CTxnReverseConfirmPage;
import com.pageobjects.channeladminpages.TransactionReverse.C2CTxnReverseDetailsPage;
import com.pageobjects.channeladminpages.TransactionReverse.C2CTxnReverseTransactionSelectPage;
import com.pageobjects.channeladminpages.TransactionReverse.TransactionReverseC2CPage;
import com.pageobjects.channeladminpages.TransactionReverse.TransactionReverseSubCategories;
import com.pageobjects.channeladminpages.TransactionReverse.TxnRvrseC2CPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class TransactionReverseC2C {
	
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	TransactionReverseSubCategories TransactionReverseSubCategories;
	TransactionReverseC2CPage TransactionReverseC2CPage;
	TxnRvrseC2CPage TxnRvrseC2CPage;
	C2CTxnReverseDetailsPage C2CTxnReverseDetailsPage;
	C2CTxnReverseConfirmPage C2CTxnReverseConfirmPage;
	C2CTxnReverseTransactionSelectPage C2CTxnReverseTransactionSelectPage;
	C2CTxnReverseChannelUserSelectPage C2CTxnReverseChannelUserSelectPage;
	RandomGeneration randmGenrtr;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;
	
	
	
	
	
	public TransactionReverseC2C(WebDriver driver){
	
	this.driver = driver;
	login1 = new Login();
	caHomepage = new ChannelAdminHomePage(driver);
	TransactionReverseSubCategories = new TransactionReverseSubCategories(driver);
	C2CTxnReverseDetailsPage = new C2CTxnReverseDetailsPage(driver);
	C2CTxnReverseConfirmPage = new C2CTxnReverseConfirmPage(driver);
	TransactionReverseC2CPage = new TransactionReverseC2CPage(driver);
	C2CTxnReverseTransactionSelectPage = new C2CTxnReverseTransactionSelectPage(driver);
	C2CTxnReverseChannelUserSelectPage = new C2CTxnReverseChannelUserSelectPage(driver);
	randmGenrtr = new RandomGeneration();
	ntwrkPage = new SelectNetworkPage(driver);
	userInfo= new HashMap<String, String>();
	ResultMap = new HashMap<String, String>();
	
}
	
	
	
	
public String initiateC2CTxnReverse(String TransferID, String recMobileNum) throws InterruptedException {
		
		
		// Initiating O2C Transaction Reverse from Channel Admin
		userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
		login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickTransactionReverse();
		TransactionReverseSubCategories.click_C2C_ReverseTxnLink();
		TransactionReverseC2CPage.EnterMobileNumber(recMobileNum);
		TransactionReverseC2CPage.EnterTransferNum(TransferID);
		TransactionReverseC2CPage.clickSubmit();
		C2CTxnReverseDetailsPage.enterRemarks("txn reversed");
		C2CTxnReverseDetailsPage.clickReverseTransaction();
		C2CTxnReverseConfirmPage.clickConfirm();
		
		String actual = TransactionReverseC2CPage.getMessage();
		
		//ResultMap.put("ACTUAL MESSAGE", actual);
		
		return actual;
	
	}


public String initiateC2CTxnReverseWithSenderMSISDN(String FromCategory, String TransferID ) throws InterruptedException {
	
	
	// Initiating O2C Transaction Reverse from Channel Admin
	userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
	login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
	ntwrkPage.selectNetwork();
	caHomepage.clickTransactionReverse();
	TransactionReverseSubCategories.click_C2C_ReverseTxnLink();
	
	String MasterSheetPath = _masterVO.getProperty("DataProvider");
	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	
	int totalRow1 = ExcelUtility.getRowCount();

	int i=1;
	for( i=1; i<=totalRow1;i++)

	{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))

		break;
	}

	System.out.println(i);
	
	
	String SenderMobileNum = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
	System.out.println(SenderMobileNum);
	
	TransactionReverseC2CPage.EnterSenderMobileNumber(SenderMobileNum);
	TransactionReverseC2CPage.clickSubmit();
	C2CTxnReverseTransactionSelectPage.selectTransferNum(TransferID);
	C2CTxnReverseTransactionSelectPage.clickSubmitBtn();
	C2CTxnReverseDetailsPage.enterRemarks("txn reversed");
	C2CTxnReverseDetailsPage.clickReverseTransaction();
	C2CTxnReverseConfirmPage.clickConfirm();
	
	String actual = TransactionReverseC2CPage.getMessage();
	
	//ResultMap.put("ACTUAL MESSAGE", actual);
	
	return actual;

}



public String initiateC2CTxnReverseWithSenderLoginID(String FromCategory, String TransferID ) throws InterruptedException {
	
	
	// Initiating O2C Transaction Reverse from Channel Admin
	userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
	login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
	ntwrkPage.selectNetwork();
	caHomepage.clickTransactionReverse();
	TransactionReverseSubCategories.click_C2C_ReverseTxnLink();
	
	String MasterSheetPath = _masterVO.getProperty("DataProvider");
	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	
	int totalRow1 = ExcelUtility.getRowCount();

	int i=1;
	for( i=1; i<=totalRow1;i++)

	{			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))

		break;
	}

	System.out.println(i);
	
	
	String SenderLoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	System.out.println(SenderLoginID);
	
	
	TransactionReverseC2CPage.EnterSenderLoginID(SenderLoginID);
	TransactionReverseC2CPage.clickSubmit();
	C2CTxnReverseTransactionSelectPage.selectTransferNum(TransferID);
	C2CTxnReverseTransactionSelectPage.clickSubmitBtn();
	C2CTxnReverseDetailsPage.enterRemarks("txn reversed");
	C2CTxnReverseDetailsPage.clickReverseTransaction();
	C2CTxnReverseConfirmPage.clickConfirm();
	
	String actual = TransactionReverseC2CPage.getMessage();
	
	//ResultMap.put("ACTUAL MESSAGE", actual);
	
	return actual;

}


public String initiateC2CTxnReverseWithDomainCode(String FromCategory, String Domain, String TransferID ) throws InterruptedException {
	
	
	// Initiating O2C Transaction Reverse from Channel Admin
	userInfo= UserAccess.getUserWithAccess(RolesI.TRANSACTION_REVERSE);
	login1.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
	ntwrkPage.selectNetwork();
	caHomepage.clickTransactionReverse();
	TransactionReverseSubCategories.click_C2C_ReverseTxnLink();
	
	TransactionReverseC2CPage.selectDomain(Domain);
	TransactionReverseC2CPage.selectCategory(FromCategory);
	
	String MasterSheetPath = _masterVO.getProperty("DataProvider");
	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

	int totalRow1 = ExcelUtility.getRowCount();
	int i=1;
	for( i=1; i<=totalRow1;i++)
	{
		if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
		break;
	}
	System.out.println(i);
	String SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
	System.out.println(SenderUserName);
	
	
	TransactionReverseC2CPage.EnterSenderUserName(SenderUserName);
	TransactionReverseC2CPage.clickSubmit();
	C2CTxnReverseTransactionSelectPage.selectTransferNum(TransferID);
	C2CTxnReverseTransactionSelectPage.clickSubmitBtn();
	C2CTxnReverseDetailsPage.enterRemarks("txn reversed");
	C2CTxnReverseDetailsPage.clickReverseTransaction();
	C2CTxnReverseConfirmPage.clickConfirm();
	
	String actual = TransactionReverseC2CPage.getMessage();
	
	//ResultMap.put("ACTUAL MESSAGE", actual);
	
	return actual;

}






}
