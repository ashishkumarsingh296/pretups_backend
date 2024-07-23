package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.channeluserpages.associateProfile.AssociateProfileSpringPage1;
import com.pageobjects.channeluserpages.associateProfile.AssociateProfileSpringPage2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CommonUtils;
import com.utils.Log;
import com.utils._masterVO;

public class AssociateProfileSpring {

	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	Map<String, String> userAccessMap;
	ChannelUsersSubCategories channelUsersSubCategories;
	String masterSheetPath;
	AssociateProfileSpringPage1 associateProfileSpringPage1;
	AssociateProfileSpringPage2 associateProfileSpringPage2;
	CommonUtils commonUtils;
	WebDriver driver=null;
	
	public AssociateProfileSpring(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		userAccessMap = new HashMap<String,String>();
		channelUsersSubCategories = new ChannelUsersSubCategories(driver);
		masterSheetPath =_masterVO.getProperty("DataProvider"); 
		associateProfileSpringPage1 = new AssociateProfileSpringPage1(driver);
		associateProfileSpringPage2 = new AssociateProfileSpringPage2(driver);
		commonUtils = new CommonUtils();
	}

	public Map<String, String> associateProfile(HashMap<String, String> mapParam, String searchCriteria) throws IOException, InterruptedException{
		
		Log.info("Associate Profile.");
		
		Map<String, String> resultMap = new HashMap<String, String>();
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ASSOCIATE_PROFILE);
		login.UserLogin(driver, "ChannelUser", mapParam.get("category"));
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUsersSubCategories.clickAssociateProfileSpring();
		//String remarksPreference = DBHandler.AccessHandler.getSystemPreference("USER_EVENT_REMARKS");
		
		if(searchCriteria=="msisdn"){
			String msisdn = mapParam.get("msisdn");
			if(msisdn == null || msisdn == "" || !commonUtils.isNumeric(msisdn)){
				associateProfileSpringPage1.enterSearchMsisdn(msisdn);
				associateProfileSpringPage1.clickMsisdnSubmit();
				resultMap.put("fieldError", associateProfileSpringPage1.getMsisdnFieldError());
				return resultMap;
			}
			associateProfileSpringPage1.enterSearchMsisdn(msisdn);
			associateProfileSpringPage1.clickMsisdnSubmit();
		}
		else if(searchCriteria=="loginId"){
			associateProfileSpringPage1.clickPanelTwo();
			Thread.sleep(1000);
			String loginId = mapParam.get("loginId");
			if(loginId == null || loginId == "" || commonUtils.isNumeric(loginId)){
				associateProfileSpringPage1.enterSearchLoginId(loginId);
				associateProfileSpringPage1.clickLoginIdSubmit();
				resultMap.put("fieldError", associateProfileSpringPage1.getLoginIdFieldError());
				return resultMap;
			}
			associateProfileSpringPage1.enterSearchLoginId(loginId);
			associateProfileSpringPage1.clickLoginIdSubmit();
		}
		else if(searchCriteria=="user"){
			associateProfileSpringPage1.clickPanelThree();
			Thread.sleep(1000);
			String category = mapParam.get("childCategory");
			String userName = mapParam.get("user");
			if(category == "Select"){
				associateProfileSpringPage1.enterSearchUser(userName);
				associateProfileSpringPage1.clickUserSubmit();
				resultMap.put("fieldError", associateProfileSpringPage1.getCategoryFieldError());
				return resultMap;
			}
			if(userName == null || userName == ""){
				associateProfileSpringPage1.selectCategory(category);
				associateProfileSpringPage1.clickUserSubmit();
				resultMap.put("fieldError", associateProfileSpringPage1.getUserFieldError());
				return resultMap;
			}
			associateProfileSpringPage1.selectCategory(category);
			associateProfileSpringPage1.enterSearchUser(userName);
			associateProfileSpringPage1.clickUserSubmit();
		}		
		return resultMap;
	}

}
