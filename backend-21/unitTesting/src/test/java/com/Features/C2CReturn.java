package com.Features;

import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.pageobjects.channeluserspages.c2creturn.C2CReturnPage1;
import com.pageobjects.channeluserspages.c2creturn.C2CReturnPage2;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.utils.ExtentI;
import com.utils.RandomGeneration;

public class C2CReturn {

	WebDriver driver=null;
	
	
	C2CReturnPage1 c2cRetPage1;
	C2CReturnPage2 c2cRetPage2;
	ChannelUserHomePage CHhomePage;
	Login login;
	RandomGeneration randomNum;
	HashMap<String, String> c2cReturnMap;
	ChannelUserSubLinkPages chnlSubLink;
	
	public C2CReturn(WebDriver driver){
	this.driver=driver;	
	c2cRetPage1 = new C2CReturnPage1(driver);
	c2cRetPage2 = new C2CReturnPage2(driver);
	CHhomePage = new ChannelUserHomePage(driver);
	login = new Login();
	randomNum = new RandomGeneration();
	c2cReturnMap=new HashMap<String, String>();
	chnlSubLink= new ChannelUserSubLinkPages(driver);
	}
	
	public HashMap<String, String> channel2channelReturn(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {	
		
		//login.LoginAsUser(driver, LoginId, Password);
		String errorMsg=login.UserLogin(driver, "ChannelUser", ToCategory,FromCategory);
		if(errorMsg!=null){ExtentI.Markup(ExtentColor.RED,"Control transfer level is not PARENT for 'Return' transfer rule from '"+FromCategory+"' to '"+ToCategory+"' .");
			login.UserLogin(driver, "ChannelUser",FromCategory);}
		
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CReturnLink();

		c2cRetPage1.enterMSISDN(MSISDN);
		c2cRetPage1.clickSubmitBtn();
		c2cReturnMap.put("InitiatedQuantities", c2cRetPage2.enterQuantityforC2C());
		c2cRetPage2.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		c2cRetPage2.enterSMSPin(PIN);
		c2cRetPage2.clickSubmitBtn();
		c2cRetPage2.clickConfirmBtn();
	
		c2cReturnMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("userreturn.msg.success",c2cRetPage2.getTransactionID()));
		c2cReturnMap.put("actualMessage",c2cRetPage2.getMessage());

		return c2cReturnMap;

	}
	
	
public HashMap<String, String> channel2channelReturnNeg(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {	
		
		//login.LoginAsUser(driver, LoginId, Password);
		String errorMsg=login.UserLogin(driver, "ChannelUser", ToCategory, FromCategory);
		if(errorMsg!=null){ExtentI.Markup(ExtentColor.RED,"Control transfer level is not PARENT for 'Return' transfer rule from '"+FromCategory+"' to '"+ToCategory+"' .");
		login.UserLogin(driver, "ChannelUser",FromCategory);}
		
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CReturnLink();

		c2cRetPage1.enterMSISDN(MSISDN);
		c2cRetPage1.clickSubmitBtn();
		
		String actualMessage = driver.findElement(By.xpath("//ol/li")).getText();
		
		c2cReturnMap.put("actualMessage",actualMessage);
		

		return c2cReturnMap;

	}

public HashMap<String, String> channel2channelReturnProductType(String FromCategory, String ToCategory, String MSISDN, String Product, String qty,String PIN) throws InterruptedException {	
	
	//login.LoginAsUser(driver, LoginId, Password);
	login.UserLogin(driver, "ChannelUser", FromCategory);
	CHhomePage.clickC2CTransfer();
	chnlSubLink.clickC2CReturnLink();

	c2cRetPage1.enterMSISDN(MSISDN);
	c2cRetPage1.clickSubmitBtn();
	c2cRetPage2.enterQuantityforC2C1(Product, qty);
	c2cRetPage2.enterRemarks("Remarks entered for C2C to: "+ToCategory);
	c2cRetPage2.enterSMSPin(PIN);
	c2cRetPage2.clickSubmitBtn();
	c2cRetPage2.clickConfirmBtn();

	//c2cReturnMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("userreturn.msg.success",c2cRetPage2.getTransactionID()));
	c2cReturnMap.put("actualMessage",c2cRetPage2.getErrorMessage());

	return c2cReturnMap;

}



public HashMap<String, String> channel2channelReturnAllProducts(String FromCategory, String ToCategory, String MSISDN, String[] Product, String[] qty,String PIN) throws InterruptedException {	
	
	//login.LoginAsUser(driver, LoginId, Password);
	login.UserLogin(driver, "ChannelUser", FromCategory);
	CHhomePage.clickC2CTransfer();
	chnlSubLink.clickC2CReturnLink();

	c2cRetPage1.enterMSISDN(MSISDN);
	c2cRetPage1.clickSubmitBtn();
	c2cRetPage2.enterQuantityforC2CAllProducts(qty,Product);
	c2cRetPage2.enterRemarks("Remarks entered for C2C to: "+ToCategory);
	c2cRetPage2.enterSMSPin(PIN);
	c2cRetPage2.clickSubmitBtn();
	c2cRetPage2.clickConfirmBtn();

	//c2cReturnMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("userreturn.msg.success",c2cRetPage2.getTransactionID()));
	c2cReturnMap.put("actualMessage",c2cRetPage2.getErrorMessage());

	return c2cReturnMap;

}


}
