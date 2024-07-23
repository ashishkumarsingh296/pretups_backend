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
import com.pageobjects.channeluserspages.changePin.ChangePinPage;
import com.pageobjects.channeluserspages.changePin.ChangePinSelectionPage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class ChangePinSpring {
	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	Map<String, String> userAccessMap;
	ChannelUsersSubCategories channelUsersSubCategories;
	String masterSheetPath;
	ChangePinSelectionPage changePinSelectionPage;
	ChangePinPage changePinPage;
	CommonUtils commonUtils;
	WebDriver driver=null;
	
	public ChangePinSpring(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		userAccessMap = new HashMap<String,String>();
		channelUsersSubCategories = new ChannelUsersSubCategories(driver);
		masterSheetPath =_masterVO.getProperty("DataProvider"); 
		changePinSelectionPage = new ChangePinSelectionPage(driver);
		changePinPage = new ChangePinPage(driver);
		commonUtils = new CommonUtils();
	}

	public Map<String, String> changePIN(HashMap<String, String> mapParam, String searchCriteria) throws IOException, InterruptedException{
		
		Log.info("Change Pin.");
		String remarks = "Automated Pin Change.";
		
		Map<String, String> resultMap = new HashMap<String, String>();
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGEPIN_ROLECODE);
		login.UserLogin(driver, "ChannelUser", mapParam.get("category"));
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUsersSubCategories.clickChangePINChannelUser();
		//String remarksPreference = DBHandler.AccessHandler.getSystemPreference("USER_EVENT_REMARKS");
		
		if(searchCriteria=="msisdn"){
			String msisdn = mapParam.get("msisdn");
			if(msisdn == null || msisdn == "" || !commonUtils.isNumeric(msisdn)){
				changePinSelectionPage.enterSearchMsisdn(msisdn);
				changePinSelectionPage.enterEventRemarks(remarks);
				changePinSelectionPage.clickMsisdnSubmit();
				resultMap.put("fieldError", changePinSelectionPage.getMsisdnFieldError());
				return resultMap;
			}
			changePinSelectionPage.enterSearchMsisdn(msisdn);
			changePinSelectionPage.enterEventRemarks(remarks);
			changePinSelectionPage.clickMsisdnSubmit();
		}
		else if(searchCriteria=="loginId"){
			changePinSelectionPage.clickPanelTwo();
			Thread.sleep(1000);
			String loginId = mapParam.get("loginId");
			if(loginId == null || loginId == "" || commonUtils.isNumeric(loginId)){
				changePinSelectionPage.enterSearchLoginId(loginId);
				changePinSelectionPage.enterLoginIdRemarks(remarks);
				changePinSelectionPage.clickLoginIdSubmit();
				resultMap.put("fieldError", changePinSelectionPage.getLoginIdFieldError());
				return resultMap;
			}
			changePinSelectionPage.enterSearchLoginId(loginId);
			changePinSelectionPage.enterEventRemarks(remarks);
			changePinSelectionPage.clickLoginIdSubmit();
		}
		else if(searchCriteria=="user"){
			changePinSelectionPage.clickPanelThree();
			Thread.sleep(1000);
			String category = mapParam.get("childCategory");
			String userName = mapParam.get("user");
			if(category == "Select"){
				changePinSelectionPage.enterSearchUser(userName);
				changePinSelectionPage.enterUserNameRemarks(remarks);
				changePinSelectionPage.clickUserSubmit();
				resultMap.put("fieldError", changePinSelectionPage.getCategoryFieldError());
				return resultMap;
			}
			if(userName == null || userName == ""){
				changePinSelectionPage.selectCategory(category);
				changePinSelectionPage.enterUserNameRemarks(remarks);
				changePinSelectionPage.clickUserSubmit();
				resultMap.put("fieldError", changePinSelectionPage.getUserFieldError());
				return resultMap;
			}
			changePinSelectionPage.selectCategory(category);
			changePinSelectionPage.enterSearchUser(userName);
			changePinSelectionPage.enterEventRemarks(remarks);
			changePinSelectionPage.clickUserSubmit();
		}
		
		changePinPage.clickCheckBox(mapParam.get("msisdn"));
		
		String oldPin = mapParam.get("oldPin");
		String newPin = mapParam.get("newPin");
		String confirmPin = mapParam.get("confirmPin");
		
		if(oldPin == null || oldPin == "" || !commonUtils.isNumeric(oldPin)){
			changePinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changePinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changePinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changePinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changePinPage.getFieldError());	
			return resultMap;
		}
		
		if(newPin == null || newPin == "" || !commonUtils.isNumeric(newPin)){
			changePinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changePinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changePinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changePinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changePinPage.getFieldError());	
			return resultMap;
		}
		
		if(confirmPin == null || confirmPin == "" || !commonUtils.isNumeric(confirmPin)){
			changePinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changePinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changePinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changePinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changePinPage.getFieldError());	
			return resultMap;
		}
		
		if(!confirmPin.equals(newPin)){
			changePinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changePinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changePinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changePinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changePinPage.getFieldError());	
			return resultMap;
		}
		
		
		if(oldPin.equals(newPin)){
			changePinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
			changePinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
			changePinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
			changePinPage.clickOnSubmitButton();
			resultMap.put("fieldError", changePinPage.getFieldError());	
			return resultMap;
		}
		
		changePinPage.enterOldSmsPin(mapParam.get("msisdn"), oldPin);
		changePinPage.enterNewSmsPin(mapParam.get("msisdn"), newPin);
		changePinPage.enterConfirmSmsPin(mapParam.get("msisdn"), confirmPin);
		changePinPage.enterRemarks(mapParam.get("msisdn"), remarks);
		changePinPage.clickOnSubmitButton();
		Thread.sleep(1000);
		changePinPage.clickOKButton();
		resultMap.put("formMessage", changePinPage.getFormMessage());
		
		if(changePinPage.getFormMessage().equalsIgnoreCase(MessagesDAO.getLabelByKey("pretups.user.changepin.msg.updatesuccess"))){
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		int rowNo = ExcelUtility.searchStringRowNum(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, mapParam.get("loginId"));
		
		ExcelUtility.setCellData(0, ExcelI.PIN, rowNo, mapParam.get("newPin"));
		
		}
		
		return resultMap;
	}
}
