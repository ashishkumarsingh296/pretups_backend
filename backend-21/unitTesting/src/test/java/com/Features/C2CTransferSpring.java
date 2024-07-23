package com.Features;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.MessagesDAO;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferConfirmPageSpring;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferDetailsPageSpring;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferInitiatePageSpring;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.utils.CommonUtils;
import com.utils.RandomGeneration;

public class C2CTransferSpring {

	WebDriver driver=null;
	
	C2CTransferInitiatePageSpring C2CTransferInitiatePageSpring;
	C2CTransferDetailsPageSpring C2CTransferDetailsPageSpring;
	C2CTransferConfirmPageSpring C2CTransferConfirmPageSpring;
	ChannelUserHomePage CHhomePage;
	Login login;
	RandomGeneration randomNum;
	HashMap<String, String> c2cTransferMap;
	ChannelUserSubLinkPages chnlSubLink;
	
	public C2CTransferSpring(WebDriver driver){
	this.driver=driver;	
	C2CTransferInitiatePageSpring = new C2CTransferInitiatePageSpring(driver);
	C2CTransferDetailsPageSpring = new C2CTransferDetailsPageSpring(driver);
	C2CTransferConfirmPageSpring = new C2CTransferConfirmPageSpring(driver);
	CHhomePage = new ChannelUserHomePage(driver);
	login = new Login();
	randomNum = new RandomGeneration();
	c2cTransferMap=new HashMap<String, String>();
	chnlSubLink= new ChannelUserSubLinkPages(driver);
	}
	
	public HashMap<String, String> channel2channelTransfer(String FromCategory,String ToCategory, String MSISDN, String PIN)  {	
	
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		C2CTransferInitiatePageSpring.enterMobileNo(MSISDN);
		C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
		C2CTransferDetailsPageSpring.enterQuantityforC2C();
		C2CTransferDetailsPageSpring.enterRefNum(randomNum.randomNumeric(6));
		C2CTransferDetailsPageSpring.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CTransferDetailsPageSpring.enterSmsPin(PIN);
		C2CTransferDetailsPageSpring.clickSubmit();
		C2CTransferConfirmPageSpring.clickConfirm();
		c2cTransferMap.put("TransactionID", C2CTransferInitiatePageSpring.getTransactionID());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("pretups.channeltransfer.transfer.msg.success",C2CTransferInitiatePageSpring.getTransactionID()));
		c2cTransferMap.put("actualMessage",C2CTransferInitiatePageSpring.getMessage());
		return c2cTransferMap;
	}
	
	

	
	public void channel2channelTransfer(String productType, String quantity,String FromCategory,String ToCategory, String MSISDN, String PIN)  {	
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		C2CTransferInitiatePageSpring.enterMobileNo(MSISDN);
		C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
		C2CTransferDetailsPageSpring.enterQuantityforC2C(productType, quantity);
		C2CTransferDetailsPageSpring.enterRefNum(randomNum.randomNumeric(6));
		C2CTransferDetailsPageSpring.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CTransferDetailsPageSpring.enterSmsPin(PIN);
		C2CTransferDetailsPageSpring.clickSubmit();
		C2CTransferConfirmPageSpring.clickConfirm();

	}
	
public void channel2channelTransfer(String[] quantity,String[] productType,String FromCategory,String ToCategory, String MSISDN, String PIN)  {	
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();

		C2CTransferInitiatePageSpring.enterMobileNo(MSISDN);
		C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
		C2CTransferDetailsPageSpring.enterQuantityforC2C(quantity,productType);
		C2CTransferDetailsPageSpring.enterRefNum(randomNum.randomNumeric(6));
		C2CTransferDetailsPageSpring.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CTransferDetailsPageSpring.enterSmsPin(PIN);
		C2CTransferDetailsPageSpring.clickSubmit();
		C2CTransferConfirmPageSpring.clickConfirm();

	}

public HashMap<String, String> channel2channelTransferByMapValue( HashMap<String, String> mapParam, String search) {
	login.UserLogin(driver, "ChannelUser", mapParam.get("fromCategory"));
	CommonUtils commonUtil = new CommonUtils();
	HashMap<String, String> resultMap = new HashMap<String, String>();;
	CHhomePage.clickC2CTransfer();
	CHhomePage.clickC2CTransfer();
	chnlSubLink.clickC2CTransferLink();
	if(search == "msisdn"){
		
		if(mapParam.get("toMSISDN") == null || mapParam.get("toMSISDN") == ""){
			C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
			resultMap.put("fieldError", C2CTransferInitiatePageSpring.getMobileNumberFieldError());	
			return resultMap;
		}
		else if(!(commonUtil.isNumeric(mapParam.get("toMSISDN")))){
			C2CTransferInitiatePageSpring.enterMobileNo(mapParam.get("toMSISDN"));
			C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
			resultMap.put("fieldError", C2CTransferInitiatePageSpring.getMobileNumberFieldError());	
			return resultMap;
		}
		
		else if(mapParam.get("fromPIN") == null || mapParam.get("fromPIN") == ""){
			C2CTransferInitiatePageSpring.enterMobileNo(mapParam.get("toMSISDN"));
			C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
			C2CTransferDetailsPageSpring.enterQuantityforC2C();
			C2CTransferDetailsPageSpring.enterRefNum(randomNum.randomNumeric(6));
			C2CTransferDetailsPageSpring.enterRemarks(mapParam.get("remarks"));
			C2CTransferDetailsPageSpring.enterSmsPin(mapParam.get("fromPIN"));
			C2CTransferDetailsPageSpring.clickSubmit();
			resultMap.put("fieldError", C2CTransferDetailsPageSpring.getSMSPINFieldError());	
			return resultMap;
		}
		 
		else if (mapParam.get("quantity") == null || mapParam.get("quantity") == ""){
			String error = null;
			C2CTransferInitiatePageSpring.enterMobileNo(mapParam.get("toMSISDN"));
			C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
			C2CTransferDetailsPageSpring.enterRefNum(randomNum.randomNumeric(6));
			C2CTransferDetailsPageSpring.enterRemarks(mapParam.get("remarks"));
			C2CTransferDetailsPageSpring.enterSmsPin(mapParam.get("fromPIN"));
			C2CTransferDetailsPageSpring.clickSubmit();
			
				resultMap.put("fieldError",C2CTransferDetailsPageSpring.getQuantityFieldError().toString().trim() );
						
				
			return resultMap;
		} 
		else {
			
			C2CTransferInitiatePageSpring.enterMobileNo(mapParam.get("toMSISDN"));
			C2CTransferInitiatePageSpring.clickSubmitForMobileNo();
			C2CTransferDetailsPageSpring.enterQuantityforC2C();
			C2CTransferDetailsPageSpring.enterRefNum(randomNum.randomNumeric(6));
			C2CTransferDetailsPageSpring.enterRemarks("Remarks entered for C2C to: "+mapParam.get("toCategory"));
			C2CTransferDetailsPageSpring.enterSmsPin(mapParam.get("fromPIN"));
			C2CTransferDetailsPageSpring.clickSubmit();
			C2CTransferConfirmPageSpring.clickConfirm();
			c2cTransferMap.put("TransactionID", C2CTransferInitiatePageSpring.getTransactionID());
			c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("pretups.channeltransfer.transfer.msg.success",C2CTransferInitiatePageSpring.getTransactionID()));
			c2cTransferMap.put("actualMessage",C2CTransferInitiatePageSpring.getMessage());
			return c2cTransferMap;
			
		}
		
		
		
	}
	
	else if(search == "loginId"){
		C2CTransferInitiatePageSpring.clickPannelByUserID();
		if(mapParam.get("toCategory") == null || mapParam.get("toCategory") == ""){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
			C2CTransferInitiatePageSpring.clickSubmitForUserSearch();
			resultMap.put("fieldError", C2CTransferInitiatePageSpring.getToCategoryFieldError());	
			return resultMap;
			
		}
		else if(mapParam.get("toUser") == null || mapParam.get("toUser") == ""){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
			//C2CTransferInitiatePageSpring.selectCategoryCode(mapParam.get("toUser"));
			C2CTransferInitiatePageSpring.clickSubmitForUserSearch();
			resultMap.put("fieldError", C2CTransferInitiatePageSpring.getToUserFieldError());	
			return resultMap;
		}
		
		

	}
	

	
	
	
	return  resultMap;
	
}


	
}

