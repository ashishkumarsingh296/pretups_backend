package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.channeluserspages.changeSelfPin.ChangeSelfPinPage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class ChangeSelfPinSpring {
	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randStr;
	Map<String, String> userAccessMap;
	ChannelUsersSubCategories channelUserSubCategories;
	ChangeSelfPinPage changeSelfPinPage;
	String masterSheetPath;
	CommonUtils commonUtils;
	WebDriver driver=null;
	
	public ChangeSelfPinSpring(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		userAccessMap = new HashMap<String, String>();
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		changeSelfPinPage = new ChangeSelfPinPage(driver);
		masterSheetPath =_masterVO.getProperty("DataProvider"); 
		commonUtils = new CommonUtils();
	}
	
	public Map<String, String> changeSelfPIN(HashMap<String, String> mapParam) throws IOException, InterruptedException{
	
		Log.info("Change Self Pin.");
		String remarks = "Automated Self Pin Change.";
		
		Map<String, String> resultMap = new HashMap<String, String>();
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGESELFPIN_ROLECODE);
		login.UserLogin(driver, "ChannelUser", mapParam.get("category"));
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		homePage.clickChannelUsers();
		Thread.sleep(1000);
		channelUserSubCategories.clickChangeSelfPIN();
		
		if(mapParam.get("msisdn") == null || mapParam.get("msisdn") == ""){
			changeSelfPinPage.clickOnSubmitButton();
			Thread.sleep(1000);
			resultMap.put("alertifyError", changeSelfPinPage.getAlertifyError());	
			return resultMap;
		}
		changeSelfPinPage.clickCheckBox(mapParam.get("msisdn"));
		
		String oldPin = mapParam.get("oldPin");
		String newPin = mapParam.get("newPin");
		String confirmPin = mapParam.get("confirmPin");
		
		if(oldPin == null || oldPin == "" || !commonUtils.isNumeric(oldPin)){
			changeSelfPinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changeSelfPinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changeSelfPinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changeSelfPinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changeSelfPinPage.getFieldError());	
			return resultMap;
		}
		
		if(newPin == null || newPin == "" || !commonUtils.isNumeric(newPin)){
			changeSelfPinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changeSelfPinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changeSelfPinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changeSelfPinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changeSelfPinPage.getFieldError());	
			return resultMap;
		}
		
		if(confirmPin == null || confirmPin == "" || !commonUtils.isNumeric(confirmPin)){
			changeSelfPinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changeSelfPinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changeSelfPinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changeSelfPinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changeSelfPinPage.getFieldError());	
			return resultMap;
		}
		
		if(!confirmPin.equals(newPin)){
			changeSelfPinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changeSelfPinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changeSelfPinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changeSelfPinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changeSelfPinPage.getFieldError());	
			return resultMap;
		}
		
		
		if(oldPin.equals(newPin)){
			changeSelfPinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changeSelfPinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changeSelfPinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changeSelfPinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changeSelfPinPage.getFieldError());	
			return resultMap;
		}
		
		changeSelfPinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
		changeSelfPinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
		changeSelfPinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
		changeSelfPinPage.enterRemarks(mapParam.get("msisdn"), remarks);
		changeSelfPinPage.clickOnSubmitButton();
		Thread.sleep(1000);
		changeSelfPinPage.clickOKButton();
		resultMap.put("formMessage", changeSelfPinPage.getFormMessage());
		
		if(changeSelfPinPage.getFormMessage().equalsIgnoreCase(MessagesDAO.getLabelByKey("pretups.user.changepin.msg.updatesuccess"))){
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		int rowNo = ExcelUtility.searchStringRowNum(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, mapParam.get("loginId"));
		
		ExcelUtility.setCellData(0, ExcelI.PIN, rowNo, mapParam.get("newPin"));
		
		}
		
		return resultMap;
	}	

}
