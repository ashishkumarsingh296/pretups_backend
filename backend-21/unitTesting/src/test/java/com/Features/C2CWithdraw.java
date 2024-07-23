package com.Features;

import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWDetailsPage;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawConfirmPage;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawDetailsPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class C2CWithdraw {

	WebDriver driver=null;
	
	
	C2CWithdrawDetailsPage c2cwithdrawDetailsPage;
	C2CWDetailsPage c2cwDetailsPage;
	C2CWithdrawConfirmPage c2cwithdrawConfirmPage;
	ChannelUserHomePage CHhomePage;
	Login login;
	RandomGeneration randomNum;
	HashMap<String, String> c2cWithdrawMap;
	ChannelUserSubLinkPages chnlSubLink;
	
	public C2CWithdraw(WebDriver driver){
	this.driver=driver;	
	c2cwithdrawDetailsPage = new C2CWithdrawDetailsPage(driver);
	c2cwDetailsPage = new C2CWDetailsPage(driver);
	c2cwithdrawConfirmPage = new C2CWithdrawConfirmPage(driver);
	CHhomePage = new ChannelUserHomePage(driver);
	login = new Login();
	randomNum = new RandomGeneration();
	c2cWithdrawMap=new HashMap<String, String>();
	chnlSubLink= new ChannelUserSubLinkPages(driver);
	}
	
	public HashMap<String, String> channel2channelWithdraw(String ToCategory,String FromCategory,String MSISDN, String PIN) throws InterruptedException {	
		//System.out.println("FromDomain:: "+FromDomain+" From cat:: " +FromCategory+" To cat:: "+ToCategory);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", ToCategory);
		CHhomePage.clickWithdrawalLink();
		chnlSubLink.clickWithdrawLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		c2cwithdrawDetailsPage.enterMobileNo(MSISDN);
		c2cwithdrawDetailsPage.clickSubmit();
		//C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		c2cWithdrawMap.put("InitiatedQuantities", c2cwithdrawDetailsPage.enterQuantityforC2C());
		c2cwDetailsPage.enterRemarks("Remarks entered for C2C withdraw from: "+FromCategory);
		c2cwDetailsPage.enterSmsPin(PIN);
		c2cwDetailsPage.clickSubmit();
		c2cwithdrawConfirmPage.clickConfirm();
	
		c2cWithdrawMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("userreturn.withdraw.msg.success",c2cwDetailsPage.getTransactionID()));
		c2cWithdrawMap.put("actualMessage",c2cwDetailsPage.getMessage());
		

		return c2cWithdrawMap;

	}
	

	public HashMap<String, String> channel2channelWithdrawNeg(String ToCategory,String FromCategory,String MSISDN, String PIN) throws InterruptedException {	
		System.out.println(" From cat:: " +FromCategory+" To cat:: "+ToCategory);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", ToCategory);
		CHhomePage.clickWithdrawalLink();
		chnlSubLink.clickWithdrawLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		c2cwithdrawDetailsPage.enterMobileNo(MSISDN);
		c2cwithdrawDetailsPage.clickSubmit();
       String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
		
		c2cWithdrawMap.put("actualMessage",actualMessage);
		

		return c2cWithdrawMap;

	}
	
	public HashMap<String, String> channel2channelWithdraw(String ToCategory,String FromCategory,String MSISDN, String PIN, String productType,String amount) throws InterruptedException {	
		//System.out.println("FromDomain:: "+FromDomain+" From cat:: " +FromCategory+" To cat:: "+ToCategory);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", ToCategory);
		CHhomePage.clickWithdrawalLink();
		chnlSubLink.clickWithdrawLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		c2cwithdrawDetailsPage.enterMobileNo(MSISDN);
		c2cwithdrawDetailsPage.clickSubmit();
		//C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		c2cwithdrawDetailsPage.enterQuantityforC2CW(productType, amount);;
		c2cwDetailsPage.enterRemarks("Remarks entered for C2C withdraw from: "+FromCategory);
		c2cwDetailsPage.enterSmsPin(PIN);
		c2cwDetailsPage.clickSubmit();
		c2cwithdrawConfirmPage.clickConfirm();
	
		c2cWithdrawMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("userreturn.withdraw.msg.success",c2cwDetailsPage.getTransactionID()));
		c2cWithdrawMap.put("actualMessage",c2cwDetailsPage.getMessage());
		

		return c2cWithdrawMap;

	}

	
}
