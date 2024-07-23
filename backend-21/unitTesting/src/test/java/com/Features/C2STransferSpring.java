package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeConfirmNotificationPageSpring;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeNotificationDisplayedPageSpring;
import com.pageobjects.channeluserspages.c2srecharge.C2STransferPageSpring;
import com.pageobjects.channeluserspages.homepages.C2STransferSubCategoriesPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.loginpages.LoginPage;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class C2STransferSpring extends BaseTest{

	public WebDriver driver;

	ChannelUserHomePage CHhomePage;
	Login login1;
	C2STransferPageSpring C2STransferPage;
	C2SRechargeConfirmNotificationPageSpring C2SRechargeConfirmNotificationPage;
	C2SRechargeNotificationDisplayedPageSpring C2SRechargeNotificationDisplayedPage;
	C2STransferSubCategoriesPage C2STransferSubCategoriesPage;
	RandomGeneration randomNum;
	LoginPage loginPage;

	public C2STransferSpring(WebDriver driver) {
		this.driver = driver;

		CHhomePage = new ChannelUserHomePage(driver);
		login1 = new Login();
		C2STransferPage =new C2STransferPageSpring(driver);
		C2SRechargeConfirmNotificationPage =new C2SRechargeConfirmNotificationPageSpring(driver);
		C2SRechargeNotificationDisplayedPage =new C2SRechargeNotificationDisplayedPageSpring(driver);
		C2STransferSubCategoriesPage =new C2STransferSubCategoriesPage(driver);
		randomNum = new RandomGeneration();
		loginPage =new LoginPage(driver);
	}


	public Map<String, String> performC2STransfer(Map<String, String> map,boolean flagAmount,boolean flagMsisdn) throws IOException, InterruptedException

	{
		Map<String, String> resultMap = new HashMap<String, String>();
		CommonUtils commonUtil=new CommonUtils();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		String login_error=login1.UserLogin(driver,"ChannelUser",map.get("category"));
		if(login_error!=null){
			resultMap.put("loginError", loginPage.getErrorMessage());
			return resultMap;
		}
		

		CHhomePage.clickC2STransfer();
		Thread.sleep(1000);
		CHhomePage.clickC2STransfer();
		Thread.sleep(1000);
		C2STransferSubCategoriesPage.clickC2SRecharge();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();

		Map<String,String> serviceMap=new HashMap<String,String>();
		for(int i=0;i<totalRow;i++)
			serviceMap.put(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i),ExcelUtility.getCellData(0,ExcelI.NAME,i));

		String serviceName= serviceMap.get(map.get("service"));
		if(map.get("service")!=null){
			C2STransferPage.selectService(serviceName);
			
			if(map.get("msisdn").equals("") || map.get("msisdn")== null){
				C2STransferPage.clickSubmitButton();
				resultMap.put("fieldError", C2STransferPage.getFieldErrorMSISDN());
				return resultMap;
			}else if(!(commonUtil.isNumeric(map.get("msisdn")))){
				C2STransferPage.enterSubMSISDN(map.get("msisdn"));
				C2STransferPage.clickSubmitButton();
				resultMap.put("fieldError", C2STransferPage.getFieldErrorMSISDN());
				return resultMap;
			}
			
			if(map.get("service").equals("PPB"))
			{
				String SubMSISDN=UniqueChecker.generate_subscriber_MSISDN("Postpaid");
				C2STransferPage.enterSubMSISDN(SubMSISDN);
			}
			else{
				if(flagMsisdn && map.get("msisdn")!=null){
					C2STransferPage.enterSubMSISDN( map.get("msisdn"));
					
				}else{
					String SubMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
					C2STransferPage.enterSubMSISDN(SubMSISDN);
				}
			}

			C2STransferPage.selectSubService(_masterVO.getProperty("subService"));	

			if(map.get("pin")==null || map.get("pin").equals("")){
				C2STransferPage.clickSubmitButton();
				resultMap.put("fieldError", C2STransferPage.getFieldErrorPin());
				return resultMap;

			}else{
				C2STransferPage.enterPin(map.get("pin"));
			}
			
			double b=C2STransferPage.getCurrentBalance();
			int a= 100;
			int check;
			boolean flagNegative=false;
			if((map.get("amount")==null  || map.get("amount") == "") && flagAmount){
				C2STransferPage.clickSubmitButton();
				resultMap.put("fieldError", C2STransferPage.getFieldErrorAmount());
				return resultMap;
			}else if(map.get("amount")==null || map.get("amount") == ""){
				C2STransferPage.enterAmount(String.valueOf(a));
				 check=Integer.parseInt(String.valueOf(a));
				if(check < 0){
					flagNegative=true;
				}
			}
			else{
				C2STransferPage.enterAmount(map.get("amount"));
				 check=Integer.parseInt(map.get("amount"));
				if(check < 0){
					flagNegative=true;
				}
			}

			C2STransferPage.clickSubmitButton();
			Thread.sleep(1000);
			if(flagNegative){
				resultMap.put("fieldError", C2STransferPage.getFieldErrorAmount());
				return resultMap;
			}
			
			if(!(commonUtil.isNumeric(map.get("pin")))){
				resultMap.put("fieldError", C2STransferPage.getFieldErrorPin());
				return resultMap;
			}
			Thread.sleep(1000);
			C2STransferPage.clickOKButton();
			
			/*pin is wrong
			 * if(!map.get("pin").equals("")){
				resultMap.put("formError", C2STransferPage.getFormError());
				return resultMap;
			}*/

			boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
			if (notificationMsgLink==true){
				Thread.sleep(1000);
				resultMap.put("successMessage", C2SRechargeConfirmNotificationPage.getSuccessMessage());
				C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
				
			}else{
				resultMap.put("formError", C2STransferPage.getFormError());	
				return resultMap;
			}
			String transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
			String transferID = C2SRechargeConfirmNotificationPage.getTransferID();
			String senderMSISDN = C2SRechargeConfirmNotificationPage.getSenderMSISDN();
			String receiverMSISDN = C2SRechargeConfirmNotificationPage.getReceiverMSISDN();
			String transferAmount = C2SRechargeConfirmNotificationPage.getTransferAmount();
			String balance = C2SRechargeConfirmNotificationPage.getBalance();
			resultMap.put("transferID", transferID);
			resultMap.put("senderMSISDN", senderMSISDN);
			resultMap.put("receiverMSISDN", receiverMSISDN);
			resultMap.put("transferAmount", transferAmount);
			resultMap.put("balance", balance);
			if(transferStatus.equals("SUCCESS")){
				Log.info("Transaction is successful.");
				return resultMap;
			}
			else{
				currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status : "+transferStatus);
			}
		}else{
			resultMap.put("formError", C2STransferPage.getFormError());	
			return resultMap;
		}
		
	return resultMap;	
		
	}
	
	public Map<String, String> getResultMap(){
		
		Map<String, String> resultMap = new HashMap<String, String>();
		String transferID = C2SRechargeConfirmNotificationPage.getTransferID();
		String senderMSISDN = C2SRechargeConfirmNotificationPage.getSenderMSISDN();
		String receiverMSISDN = C2SRechargeConfirmNotificationPage.getReceiverMSISDN();
		String transferAmount = C2SRechargeConfirmNotificationPage.getTransferAmount();
		String balance = C2SRechargeConfirmNotificationPage.getBalance();
		resultMap.put("transferID", transferID);
		resultMap.put("senderMSISDN", senderMSISDN);
		resultMap.put("receiverMSISDN", receiverMSISDN);
		resultMap.put("transferAmount", transferAmount);
		resultMap.put("balance", balance);
		return resultMap;
	}
}
