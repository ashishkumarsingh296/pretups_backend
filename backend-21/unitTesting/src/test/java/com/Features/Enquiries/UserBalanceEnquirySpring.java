package com.Features.Enquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.classes.CONSTANT;
import com.classes.GetScreenshot;
import com.classes.Login;
import com.classes.UserAccess;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.channelenquiry.SelfBalance;
import com.pageobjects.channeluserspages.channelenquiry.UserBalanceSpring;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CommonUtils;

public class UserBalanceEnquirySpring {

	WebDriver driver;
	ChannelAdminHomePage HomePage;
	ChannelEnquirySubCategories ChannelEnquirySubCategory;
	Login login;
	SelectNetworkPage networkPage;
	UserBalanceSpring UserBalanceEnquiry;
	SoftAssert SAssert = new SoftAssert();
	Map<String, String> userAccessMap = new HashMap<String, String>();
	ChannelUserHomePage CHhomePage;
	CommonUtils commonUtils;

	public UserBalanceEnquirySpring(WebDriver driver) {
		this.driver = driver;
		//Page Initialization
		HomePage = new ChannelAdminHomePage(driver);
		login = new Login();
		ChannelEnquirySubCategory = new ChannelEnquirySubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		UserBalanceEnquiry = new UserBalanceSpring(driver);
		CHhomePage = new ChannelUserHomePage(driver);
		commonUtils = new CommonUtils();
	}

	public Map<String, String> validateUserBalancesEnquiry(String parentCategory, String category, HashMap<String, String> mapParam, String search) throws InterruptedException {


		login.UserLogin(driver, "ChannelUser", parentCategory);

		Map<String, String> ChannelUserMap = UserAccess.getChannelUserDetails(parentCategory, category);
		Map<String, String> resultMap = new HashMap<String, String>();
		networkPage.selectNetwork();


		HomePage.clickChannelEnquiry();
		HomePage.clickChannelEnquiry();
		Thread.sleep(1000);
		ChannelEnquirySubCategory.clickUserBalanceSpring();
		if(search == "msisdn"){

			if(mapParam.get("MSISDN") == null || mapParam.get("MSISDN") == ""){
				UserBalanceEnquiry.clickSubmitMsisdnButton();
				resultMap.put("fieldError", UserBalanceEnquiry.getFieldError());	
				return resultMap;
			}
			if(!commonUtils.isNumeric(mapParam.get("MSISDN"))){
				UserBalanceEnquiry.enterMSISDN(mapParam.get("MSISDN"));
				UserBalanceEnquiry.clickSubmitMsisdnButton();
				resultMap.put("fieldError", UserBalanceEnquiry.getFieldError());	
				return resultMap;
			}
			UserBalanceEnquiry.enterMSISDN(mapParam.get("MSISDN"));
			UserBalanceEnquiry.clickSubmitMsisdnButton();
		}

		else if(search == "loginId"){
			UserBalanceEnquiry.clickPanelTwo();
			Thread.sleep(1000);
			if(mapParam.get("LOGINID") == null || mapParam.get("LOGINID") == ""){
				UserBalanceEnquiry.clickSubmitLoginIdButton();
				resultMap.put("fieldError", UserBalanceEnquiry.getLoginIdFieldError());	
				return resultMap;
			}

			if(!(mapParam.get("LOGINID").matches("[a-zA-Z\\d_\\s]*?"))){
				UserBalanceEnquiry.enterLoginID(mapParam.get("LOGINID"));
				UserBalanceEnquiry.clickSubmitLoginIdButton();
				resultMap.put("fieldError", UserBalanceEnquiry.getLoginIdFieldError());	
				return resultMap;
			}	
			UserBalanceEnquiry.enterLoginID(mapParam.get("LOGINID"));
			UserBalanceEnquiry.clickSubmitLoginIdButton();
		}


		else if(search == "user"){
			UserBalanceEnquiry.clickPanelThree();
			Thread.sleep(1000);
			String selectionCategory = mapParam.get("CATEGORY");
			String userName = mapParam.get("USER_NAME");
			if(selectionCategory == "Select"){
				UserBalanceEnquiry.enterUserId(mapParam.get("USER_NAME").trim());
				UserBalanceEnquiry.clickSubmitUser();
				resultMap.put("fieldError", UserBalanceEnquiry.getCategoryFieldError());
				return resultMap;
			}
			if(userName == null || userName == ""){
				UserBalanceEnquiry.enterChannelCategoryCode(mapParam.get("CATEGORY"));
				UserBalanceEnquiry.clickSubmitUser();
				resultMap.put("fieldError", UserBalanceEnquiry.getUserFieldError());
				return resultMap;
			}
			UserBalanceEnquiry.enterChannelCategoryCode(mapParam.get("CATEGORY"));
			UserBalanceEnquiry.enterUserId(mapParam.get("USER_NAME").trim());
			UserBalanceEnquiry.clickSubmitUser();
		}
		String screenshot = GetScreenshot.getFullScreenshot(driver);
		resultMap.put("screenshot", screenshot);
		SelfBalance selfBalance = new SelfBalance(driver);
		SAssert.assertEquals(selfBalance.prepareUserBalanceValues().get("User Name").trim(), ChannelUserMap.get("USER_NAME").trim());
		SAssert.assertEquals(selfBalance.prepareUserBalanceValues().get("MSISDN").trim(), ChannelUserMap.get("MSISDN").trim());
		SAssert.assertEquals(selfBalance.prepareUserBalanceValues().get("Category").trim(), category.trim());
		SAssert.assertEquals(selfBalance.prepareUserBalanceValues().get("Network Name").trim(), CONSTANT.NetworkName);
		SAssert.assertAll();

		return  resultMap;

	}

}
