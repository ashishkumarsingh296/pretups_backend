package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawConfirmPageSpring;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawProductPageSpring;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawUserSearchPageSpring;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
public class C2CWithdrawSpring {

	WebDriver driver=null;
	ChannelUserHomePage CHhomePage;
	Login login;
	HashMap<String, String> c2cWithdrawMap;
	ChannelUserSubLinkPages chnlSubLink;
	
	C2CWithdrawConfirmPageSpring c2CWithdrawConfirmPageSpring;
	C2CWithdrawProductPageSpring c2CWithdrawProductPageSpring;
	C2CWithdrawUserSearchPageSpring c2CWithdrawUserSearchPageSpring;
	
	public C2CWithdrawSpring(WebDriver driver){
		this.driver=driver;
		CHhomePage=new ChannelUserHomePage(driver);
		login=new Login();
		c2CWithdrawConfirmPageSpring= new C2CWithdrawConfirmPageSpring(driver);
		c2CWithdrawProductPageSpring=new C2CWithdrawProductPageSpring(driver);
		c2CWithdrawUserSearchPageSpring=new C2CWithdrawUserSearchPageSpring(driver);
		c2cWithdrawMap=new HashMap<String, String>();
		chnlSubLink= new ChannelUserSubLinkPages(driver);
	}
	
	public HashMap<String, String> channel2channelWithdrawSpring(String ToCategory,String FromCategory,String MSISDN, String PIN) throws InterruptedException {	
		//System.out.println("FromDomain:: "+FromDomain+" From cat:: " +FromCategory+" To cat:: "+ToCategory);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", ToCategory);
		CHhomePage.clickWithdrawalLink();//one for struts
		CHhomePage.clickWithdrawalLink();//one for Spring
		Thread.sleep(500);
		chnlSubLink.clickWithdrawLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		c2CWithdrawUserSearchPageSpring.enterMobileNo(MSISDN);
		c2CWithdrawUserSearchPageSpring.clickSubmitMsisdn();
		c2CWithdrawProductPageSpring.enterQuantityforC2C();
		c2CWithdrawProductPageSpring.enterRemarks("Remarks entered for C2C withdraw from: "+FromCategory);
		c2CWithdrawProductPageSpring.enterSmsPin(PIN);
		c2CWithdrawProductPageSpring.clickSubmit();
		c2CWithdrawConfirmPageSpring.clickConfirm();
	
		c2cWithdrawMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("pretups.userreturn.withdraw.msg.success",c2CWithdrawProductPageSpring.getTransactionID()));
		c2cWithdrawMap.put("actualMessage",c2CWithdrawProductPageSpring.getMessage());
		

		return c2cWithdrawMap;

	}
	
	
	@SuppressWarnings("finally")
	public Map<String, String> SIT_channel2channelWithdrawSpring(Map<String, String> paramMap) throws InterruptedException {	
		Log.info(" Entered SIT_channel2channelWithdrawSpring");
		Map<String, String> resultMap = new HashMap<String, String>();
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		login.UserLogin(driver, "ChannelUser", paramMap.get("fromCategory"));//Super Distributor
		CHhomePage.clickWithdrawalLink();//one for struts
		CHhomePage.clickWithdrawalLink();//one for Spring
		Thread.sleep(1000);
		chnlSubLink.clickWithdrawLink();
		
		String msisdn = paramMap.get("toMSISDN");
		if(msisdn == null || msisdn == ""){
			c2CWithdrawUserSearchPageSpring.clickSubmitMsisdn();
			String msg=c2CWithdrawUserSearchPageSpring.getFieldErrorMSISDN();
			resultMap.put("fieldError", msg);
			return resultMap;
		}
		String suspendedTest=paramMap.get("senderSuspended");
		if(suspendedTest!=null && suspendedTest.equalsIgnoreCase("Y")){
			suspendCHNLUser.suspendChannelUser_MSISDN(msisdn, "Automation Remarks");
			suspendCHNLUser.approveCSuspendRequest_MSISDN(msisdn, "Automation remarks");
			try{
				login.UserLogin(driver, "ChannelUser", paramMap.get("fromCategory"));
				CHhomePage.clickWithdrawalLink();//one for struts
				CHhomePage.clickWithdrawalLink();//one for Spring
				chnlSubLink.clickWithdrawLink();
				executeWithdrawViaMSISDN(paramMap);
				c2cWithdrawMap.put("actualMessage",c2CWithdrawProductPageSpring.getMessage());
				String expectedMessage = MessagesDAO.prepareMessageByKey("pretups.message.channeltransfer.usersuspended.msg", paramMap.get("toMSISDN"));
				resultMap.put("expectedMessage", expectedMessage);
			}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//*[@class='errorClass']")).getText();
				String expectedMessage = MessagesDAO.prepareMessageByKey("pretups.message.channeltransfer.usersuspended.msg", paramMap.get("toMSISDN"));
				Log.info(" Message fetched from WEB as : "+actualMessage);
				resultMap.put("actualMessage", actualMessage);
				resultMap.put("expectedMessage", expectedMessage);
				}
			finally{
				resumeCHNLUser.resumeChannelUser_MSISDN(msisdn, "Auto Resume Remarks");
				return resultMap;
				}
			}
		
		String outSuspend=paramMap.get("outSuspended");
		if(outSuspend!=null && outSuspend.equalsIgnoreCase("Y")){
			ChannelUser chnlUsr;
			chnlUsr = new ChannelUser(driver);
			paramMap.put("outSuspend_chk", "Y");	paramMap.put("searchMSISDN",paramMap.get("toMSISDN") ); paramMap.put("loginChange", "N");
			paramMap.put("assgnPhoneNumber", "N");
			ExtentI.Markup(ExtentColor.TEAL, "OutSuspend Channel User");
			chnlUsr.modifyChannelUserDetails(paramMap.get("toCategory"), (HashMap<String, String>) paramMap);
			try{
				executeWithdrawViaMSISDN(paramMap);
				c2cWithdrawMap.put("actualMessage",c2CWithdrawProductPageSpring.getMessage());
				String expectedMessage = MessagesDAO.prepareMessageByKey("pretups.message.channeltransfer.usersuspended.msg", paramMap.get("toMSISDN"));
				resultMap.put("expectedMessage", expectedMessage);
			}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//*[@class='errorClass']")).getText();
				String expectedMessage = MessagesDAO.prepareMessageByKey("pretups.message.channeltransfer.usersuspended.msg", paramMap.get("toMSISDN"));
				Log.info(" Message fetched from WEB as : "+actualMessage);
				resultMap.put("actualMessage", actualMessage);
				resultMap.put("expectedMessage", expectedMessage);
				}
			finally{
				paramMap.put("outSuspend_chk", "N");
				ExtentI.Markup(ExtentColor.TEAL, "Removing OutSuspended status from Channel User");
				chnlUsr.modifyChannelUserDetails(paramMap.get("toCategory"), (HashMap<String, String>) paramMap);
				return resultMap;
				}
			}
		executeWithdrawViaMSISDN(paramMap);
	
		resultMap.put("actualMessage",c2CWithdrawProductPageSpring.getMessage());
		

		return resultMap;

	}

	private void executeWithdrawViaMSISDN(Map<String, String> paramMap) {
		
		c2CWithdrawUserSearchPageSpring.enterMobileNo(paramMap.get("toMSISDN"));//7275207520
		c2CWithdrawUserSearchPageSpring.clickSubmitMsisdn();
		c2CWithdrawProductPageSpring.enterQuantityforC2C();
		c2CWithdrawProductPageSpring.enterRemarks("Remarks entered for C2C withdraw from: "+paramMap.get("toCategory"));//Dealer
		c2CWithdrawProductPageSpring.enterSmsPin(paramMap.get("PIN"));
		c2CWithdrawProductPageSpring.clickSubmit();
		c2CWithdrawConfirmPageSpring.clickConfirm();
	}
	
public Map<String, String> SIT_channel2channelWithdrawSpringForUserSearch(Map<String, String> paramMap) throws InterruptedException{	
		
		Map<String, String> resultMap = new HashMap<String, String>();

		login.UserLogin(driver, "ChannelUser", paramMap.get("fromCategory"));//Super Distributor
		CHhomePage.clickWithdrawalLink();//one for struts
		CHhomePage.clickWithdrawalLink();//one for Spring
		Thread.sleep(1000);
		chnlSubLink.clickWithdrawLink();
		Thread.sleep(1000);
		c2CWithdrawUserSearchPageSpring.clickByUserName();
		Thread.sleep(1000);
		String toCategory = paramMap.get("toCategory");
		if(toCategory == null || toCategory == ""){
			c2CWithdrawUserSearchPageSpring.clickSubmitSearch();
			String msg=c2CWithdrawUserSearchPageSpring.getFieldErrorCategory();
			resultMap.put("fieldError", msg);
			return resultMap;
		}
		String domainCode = DBHandler.AccessHandler.getDomainCode(paramMap.get("domainName"));
		String selectCat=toCategory+" ("+domainCode+")";
		c2CWithdrawUserSearchPageSpring.selectCategoryCode(selectCat);
		String userName = paramMap.get("userName");
		if(userName == null || userName == ""){
			c2CWithdrawUserSearchPageSpring.clickSubmitSearch();
			String msg=c2CWithdrawUserSearchPageSpring.getFieldErrorUserName();
			resultMap.put("fieldError", msg);
			return resultMap;
		}
		
		c2CWithdrawUserSearchPageSpring.enterToUserName(userName);
		c2CWithdrawUserSearchPageSpring.clickSubmitSearch();
		
		c2CWithdrawProductPageSpring.enterRemarks("Remarks entered for C2C withdraw from: "+paramMap.get("toCategory"));//Dealer
		String smsPin = paramMap.get("PIN");
		if(smsPin == null || smsPin == ""){
			c2CWithdrawProductPageSpring.enterQuantityforC2C();
			c2CWithdrawProductPageSpring.clickSubmit();
			String msg=c2CWithdrawProductPageSpring.getFieldErrorSmsPin();
			resultMap.put("fieldError", msg);
			return resultMap;
		}
		
		String quantity = paramMap.get("quantity");
		if(quantity == null || quantity == ""){
			c2CWithdrawProductPageSpring.enterSmsPin(paramMap.get("PIN"));
			c2CWithdrawProductPageSpring.clickSubmit();
			boolean msg=c2CWithdrawProductPageSpring.isAlertifyQuantity();
			if(msg){
			resultMap.put("fieldError", "SUCCESS");
			}
			return resultMap;
		}
		
		
		
		c2CWithdrawProductPageSpring.enterSmsPin(paramMap.get("PIN"));
		c2CWithdrawProductPageSpring.clickSubmit();
		c2CWithdrawConfirmPageSpring.clickConfirm();
	
		resultMap.put("actualMessage",c2CWithdrawProductPageSpring.getMessage());
		

		return resultMap;

	}
	
}
