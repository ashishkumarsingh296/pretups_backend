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

public class SelfBalanceSpring {


	WebDriver driver;
	ChannelAdminHomePage HomePage;
	ChannelEnquirySubCategories ChannelEnquirySubCategory;
	Login login;
	SelectNetworkPage networkPage;
	UserBalanceSpring UserBalanceEnquiry;
	SoftAssert SAssert = new SoftAssert();
	Map<String, String> userAccessMap = new HashMap<String, String>();
	ChannelUserHomePage CHhomePage;

	/**
	 * @param driver
	 */
	public SelfBalanceSpring(WebDriver driver) {
		this.driver = driver;
		HomePage = new ChannelAdminHomePage(driver);
		login = new Login();
		ChannelEnquirySubCategory = new ChannelEnquirySubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		UserBalanceEnquiry = new UserBalanceSpring(driver);
		CHhomePage = new ChannelUserHomePage(driver);
	}

	/**
	 * 
	 * @param parentCategory
	 * @param Category
	 * @return
	 * @throws InterruptedException 
	 */
	public String validateSelfBalancesEnquiry(String parentCategory, String Category) throws InterruptedException {

		login.UserLogin(driver, "ChannelUser", parentCategory, Category);
		Map<String, String> ChannelUserMap = UserAccess.getChannelUserDetails(parentCategory, Category);

		networkPage.selectNetwork();

		HomePage.clickChannelEnquiry();
		Thread.sleep(1000);
		HomePage.clickChannelEnquiry();
		Thread.sleep(1000);
		ChannelEnquirySubCategory.clickSelfBalance();


		String Screenshot = GetScreenshot.getFullScreenshot(driver);

		SelfBalance selfBalance = new SelfBalance(driver); 
		Map<String, String> resultMap = selfBalance.prepareUserBalanceValues();
		SAssert.assertEquals(resultMap.get("User Name").trim(), ChannelUserMap.get("USER_NAME").trim());
		SAssert.assertEquals(resultMap.get("MSISDN").trim(), ChannelUserMap.get("MSISDN").trim());
		SAssert.assertEquals(resultMap.get("Category").trim(), Category.trim());
		SAssert.assertEquals(resultMap.get("Network Name").trim(), CONSTANT.NetworkName);
		SAssert.assertAll();
		return Screenshot;
	}
}
