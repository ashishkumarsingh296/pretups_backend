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
import com.pageobjects.channeluserpages.associateProfile.AssociateProfile1;
import com.pageobjects.channeluserpages.associateProfile.AssociateProfile2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CommonUtils;
import com.utils.Log;
import com.utils._masterVO;

public class AssociateProfile {

	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	Map<String, String> userAccessMap;
	ChannelUsersSubCategories channelUsersSubCategories;
	String masterSheetPath;
	AssociateProfile1 associateProfilePage1;
	AssociateProfile2 associateProfilePage2;
	CommonUtils commonUtils;
	WebDriver driver=null;
	
	public AssociateProfile(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		userAccessMap = new HashMap<String,String>();
		channelUsersSubCategories = new ChannelUsersSubCategories(driver);
		masterSheetPath =_masterVO.getProperty("DataProvider"); 
		associateProfilePage1 = new AssociateProfile1(driver);
		associateProfilePage2 = new AssociateProfile2(driver);
		commonUtils = new CommonUtils();
	}

	public String associateProfile(HashMap<String, String> mapParam, String searchCriteria) throws IOException, InterruptedException{
		
		Log.info("Associate Profile.");
		
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ASSOCIATE_PROFILE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUsersSubCategories.clickAssociateProfile();
		//String remarksPreference = DBHandler.AccessHandler.getSystemPreference("USER_EVENT_REMARKS");
		
		if(searchCriteria=="msisdn"){
			String msisdn = mapParam.get("MSISDN");
			/*if(msisdn == null || msisdn == "" || !commonUtils.isNumeric(msisdn)){
				associateProfilePage1.enterSearchMsisdn(msisdn);
				associateProfilePage1.clickSubmit();
				resultMap.put("fieldError", associateProfilePage1.getMsisdnFieldError());
				return resultMap;
			}*/
			associateProfilePage1.enterSearchMsisdn(msisdn);
			associateProfilePage1.clickSubmit();
		}
		
		associateProfilePage2.selectGrade(mapParam.get("grade"));
		associateProfilePage2.selectCommissionProfile(mapParam.get("commProfile"));
		associateProfilePage2.clickSubmitButton();
		String message = associateProfilePage1.getMessage();
		return message;
	}

}
