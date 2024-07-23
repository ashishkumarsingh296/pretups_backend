package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.channeluserpinmgmt.ChannelUSerPINMgmtPage_1;
import com.pageobjects.channeladminpages.channeluserpinmgmt.ChannelUSerPINMgmtPage_2;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

/**
 * @author lokesh.kontey
 *
 */
public class ChannelUserPinManagement extends BaseTest {
	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	OperatorUsersSubCategories operatorSubLink;
	ChannelUsersSubCategories channelUserSubCategories;
	AddChannelUserDetailsPage addChrUserDetailsPage;
	ChannelUSerPINMgmtPage_1 chnlUserPinMgmt1;
	ChannelUSerPINMgmtPage_2 chnlUserPinMgmt2;
	
	public int RowNum;
	HashMap<String, String> pinresultMap;
	Map<String, String> userAccessMap;
	
	WebDriver driver=null;
	
	public ChannelUserPinManagement(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		operatorSubLink = new OperatorUsersSubCategories(driver);
		pinresultMap = new HashMap<String, String>();
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		addChrUserDetailsPage = new AddChannelUserDetailsPage(driver);
		userAccessMap = new HashMap<String, String>();
		chnlUserPinMgmt1 = new ChannelUSerPINMgmtPage_1(driver);
		chnlUserPinMgmt2 = new ChannelUSerPINMgmtPage_2(driver);
	}

	
	
	/**
	 * Channel USer PIN Management
	 * @param ParentUser
	 * @param LoginUser
	 * 
	 * @return HashMap -> channelresultMap
	 * @throws InterruptedException
	 */
	
	public HashMap<String, String> channelUserPinMgmt_sendPIN(HashMap<String, String> pinMgmtMap) throws InterruptedException{

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_USER_PIN_MGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickChnlUserPINMgmt();
		chnlUserPinMgmt1.enterMSISDN(pinMgmtMap.get("mobileNumber"));
		chnlUserPinMgmt1.enterRemarks(pinMgmtMap.get("Remarks"));
		chnlUserPinMgmt1.clickSubmitButton();
		chnlUserPinMgmt2.clickSendPinButton();
		driver.switchTo().alert().accept();
		
		pinresultMap.put("sendPinMsg", addChrUserDetailsPage.getActualMessage());
		
		return pinresultMap;
	}
	
	public HashMap<String, String> channelUserPinMgmt_ResetPIN(HashMap<String, String> pinMgmtMap) throws InterruptedException{

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_USER_PIN_MGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickChnlUserPINMgmt();
		chnlUserPinMgmt1.enterMSISDN(pinMgmtMap.get("mobileNumber"));
		chnlUserPinMgmt1.enterRemarks(pinMgmtMap.get("Remarks"));
		chnlUserPinMgmt1.clickSubmitButton();
		chnlUserPinMgmt2.clickResetPinButton();
		driver.switchTo().alert().accept();
		
		pinresultMap.put("resetPinMsg", addChrUserDetailsPage.getActualMessage());
		return pinresultMap;
	}
	
	public void channelUserPinMgmt_Cancel(HashMap<String, String> pinMgmtMap) throws InterruptedException{

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_USER_PIN_MGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickChnlUserPINMgmt();
		chnlUserPinMgmt1.enterMSISDN(pinMgmtMap.get("mobileNumber"));
		chnlUserPinMgmt1.enterRemarks(pinMgmtMap.get("Remarks"));
		chnlUserPinMgmt1.clickSubmitButton();
		chnlUserPinMgmt2.clickCancelButton();
	}
	
	public void channelUserPinMgmt_Back(HashMap<String, String> pinMgmtMap) throws InterruptedException{

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_USER_PIN_MGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickChnlUserPINMgmt();
		chnlUserPinMgmt1.enterMSISDN(pinMgmtMap.get("mobileNumber"));
		chnlUserPinMgmt1.enterRemarks(pinMgmtMap.get("Remarks"));
		chnlUserPinMgmt1.clickSubmitButton();
		chnlUserPinMgmt2.clickBackButton();
	}
	
	
}