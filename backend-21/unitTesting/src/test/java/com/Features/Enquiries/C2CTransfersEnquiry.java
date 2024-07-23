package com.Features.Enquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.classes.GetScreenshot;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.channelenquiries.C2CTransfersPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class C2CTransfersEnquiry {
	
	WebDriver driver;
	ChannelAdminHomePage HomePage;
	ChannelEnquirySubCategories ChannelEnquirySubCategory;
	Login login;
	SelectNetworkPage networkPage;
	C2CTransfersPage C2CEnquiry;
	SoftAssert SAssert = new SoftAssert();
	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	public C2CTransfersEnquiry(WebDriver driver) {
		this.driver = driver;
		//Page Initialization
		HomePage = new ChannelAdminHomePage(driver);
		login = new Login();
		ChannelEnquirySubCategory = new ChannelEnquirySubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		C2CEnquiry = new C2CTransfersPage(driver);
	}
	
	public String validateC2CTransfersEnquiry(String TransactionNumber) {

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2C_TRANSFERS_ENQUIRY_ROLECODE); //Getting User with Access to O2C Transfers Enquiry
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		HomePage.clickChannelEnquiry();
		ChannelEnquirySubCategory.clickC2CTransfersEnquiry();
		C2CEnquiry.enterTransferNumber(TransactionNumber);
		C2CEnquiry.clickSubmitButton();

		String Screenshot = GetScreenshot.getFullScreenshot(driver);
		
		return Screenshot;
	}
	
}
