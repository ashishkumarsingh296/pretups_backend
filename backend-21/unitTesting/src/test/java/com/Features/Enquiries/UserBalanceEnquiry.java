package com.Features.Enquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.classes.CONSTANT;
import com.classes.GetScreenshot;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.channelenquiries.UserBalances;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class UserBalanceEnquiry {
	
	WebDriver driver;
	ChannelAdminHomePage HomePage;
	ChannelEnquirySubCategories ChannelEnquirySubCategory;
	Login login;
	SelectNetworkPage networkPage;
	UserBalances UserBalanceEnquiry;
	SoftAssert SAssert = new SoftAssert();
	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	public UserBalanceEnquiry(WebDriver driver) {
		this.driver = driver;
		//Page Initialization
		HomePage = new ChannelAdminHomePage(driver);
		login = new Login();
		ChannelEnquirySubCategory = new ChannelEnquirySubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		UserBalanceEnquiry = new UserBalances(driver);
	}
	
	public HashMap<String, String> prepareUserBalancesDAO() {
		
		HashMap<String, String> UserBalancesDAO= new HashMap<String, String>();
		
		String userName_Key = MessagesDAO.getLabelByKey("user.channeluserviewbalances.label.username");
		String MSISDN_Key = MessagesDAO.getLabelByKey("user.channeluserviewbalances.label.msisdn");
		String LoginID_Key = MessagesDAO.getLabelByKey("user.channeluserviewbalances.label.loginid");
		String UserType_Key = MessagesDAO.getLabelByKey("user.channeluserviewbalances.label.usertype");
		String NetworkName_Key = MessagesDAO.getLabelByKey("user.channeluserviewbalances.label.network");
		String Category_Key = MessagesDAO.getLabelByKey("user.channeluserviewbalances.label.categorycode");

		String userName_Locator = "//tr/td[text()[contains(.,'"+ userName_Key +"')]]/following-sibling::td";
		String MSISDN_Locator = "//tr/td[text()[contains(.,'"+ MSISDN_Key +"')]]/following-sibling::td";
		String LoginID_Locator = "//tr/td[text()[contains(.,'"+ LoginID_Key +"')]]/following-sibling::td";
		String UserType_Locator = "//tr/td[text()[contains(.,'"+ UserType_Key +"')]]/following-sibling::td";
		String NetworkName_Locator = "//tr/td[text()[contains(.,'"+ NetworkName_Key +"')]]/following-sibling::td";
		String Category_Locator = "//tr/td[text()[contains(.,'"+ Category_Key +"')]]/following-sibling::td";
		
		UserBalancesDAO.put("User Name", driver.findElement(By.xpath(userName_Locator)).getText());
		UserBalancesDAO.put("MSISDN", driver.findElement(By.xpath(MSISDN_Locator)).getText());
		UserBalancesDAO.put("LoginID", driver.findElement(By.xpath(LoginID_Locator)).getText());
		UserBalancesDAO.put("UserType", driver.findElement(By.xpath(UserType_Locator)).getText());
		UserBalancesDAO.put("Network Name", driver.findElement(By.xpath(NetworkName_Locator)).getText());
		UserBalancesDAO.put("Category", driver.findElement(By.xpath(Category_Locator)).getText());
		
		return UserBalancesDAO;
	}
	
	public String validateUserBalancesEnquiry(String parentCategory, String Category) {
		
		HashMap<String, String> UserBalancesDAO = new HashMap<String, String>();
				
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USER_BALANCES_ENQUIRY_ROLECODE); //Getting User with Access to O2C Transfers Enquiry
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		HashMap<String, String> ChannelUserMap = UserAccess.getChannelUserDetails(parentCategory, Category);
		
		networkPage.selectNetwork();
		HomePage.clickChannelEnquiry();
		ChannelEnquirySubCategory.clickUserBalance();
		UserBalanceEnquiry.enterMSISDN(ChannelUserMap.get("MSISDN"));
		UserBalanceEnquiry.clickSubmitButton();
		
		String Screenshot = GetScreenshot.getFullScreenshot(driver);
		//Enquiry Validator Begins
		UserBalancesDAO = prepareUserBalancesDAO();
		SAssert.assertEquals(UserBalancesDAO.get("User Name"), ChannelUserMap.get("USER_NAME"));
		SAssert.assertEquals(UserBalancesDAO.get("MSISDN"), ChannelUserMap.get("MSISDN"));
		SAssert.assertEquals(UserBalancesDAO.get("Category"), Category);
		SAssert.assertEquals(UserBalancesDAO.get("Network Name"), CONSTANT.NetworkName);
		SAssert.assertAll();
		return Screenshot;
	}
	
}
