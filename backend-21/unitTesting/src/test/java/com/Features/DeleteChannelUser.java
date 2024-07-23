/**
 * 
 */
package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserPage;
import com.pageobjects.channeladminpages.addchanneluser.ApproveChannelUserPage;
import com.pageobjects.channeladminpages.deletechanneluser.ApprovalDeleteChannelUserPage1;
import com.pageobjects.channeladminpages.deletechanneluser.ApprovalDeletePage2;
import com.pageobjects.channeladminpages.deletechanneluser.DeleteChannelUserPage1;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.RandomGeneration;

/**
 * @author lokesh.kontey
 *
 */
public class DeleteChannelUser extends BaseTest{

	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randStr;
	OperatorUsersSubCategories operatorSubLink;
	ApproveChannelUserPage apprvChannelUsrPage;
	ChannelUsersSubCategories channelUserSubCategories;
	DeleteChannelUserPage1 deleteChnluserPage1;
	ApprovalDeleteChannelUserPage1 approvaldeletePage1;
	ApprovalDeletePage2 approvaldeletePage2;
	AddChannelUserPage addChrUserPage;
	
	Map<String, String> userAccessMap;
	HashMap<String, String> deleteMap;
	WebDriver driver = null;
	
	public DeleteChannelUser(WebDriver driver){
		this.driver=driver;
		
		deleteChnluserPage1= new DeleteChannelUserPage1(driver);
		approvaldeletePage1= new ApprovalDeleteChannelUserPage1(driver);
		approvaldeletePage2= new ApprovalDeletePage2(driver);
		
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		operatorSubLink = new OperatorUsersSubCategories(driver);
		apprvChannelUsrPage= new ApproveChannelUserPage(driver);
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		apprvChannelUsrPage = new ApproveChannelUserPage(driver);
		userAccessMap = new HashMap<String, String>();
		addChrUserPage = new AddChannelUserPage(driver);
		deleteMap = new HashMap<String, String>();
	}
	
	public void deletechannelUser_LoginID(String LOGINID, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickDeleteChannelUser();
		deleteChnluserPage1.enterLoginID(LOGINID);
		deleteChnluserPage1.enterRemarks(Remarks);
		deleteChnluserPage1.clickSubmitBtn();
		deleteChnluserPage1.clickDeleteBtn();
		driver.switchTo().alert().accept();
	}
	
	public HashMap<String, String> deletechannelUser_MSISDN(String MSISDN, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickDeleteChannelUser();
		deleteChnluserPage1.enterMSISDN(MSISDN);
		deleteChnluserPage1.enterRemarks(Remarks);
		deleteChnluserPage1.clickSubmitBtn();
		deleteChnluserPage1.clickDeleteBtn();
		driver.switchTo().alert().accept();
		deleteMap.put("DeletionMsg",deleteChnluserPage1.getMessage());
		return deleteMap;
	}
	
	
	//Only for user with owner as Root.
	public void deletechannelUser_GeoDetails(String GEODOMAIN, String DOMAIN, String CATEGORY, String Remarks, String channeluserName) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickDeleteChannelUser();
		deleteChnluserPage1.selectGeoDomain(GEODOMAIN);
		deleteChnluserPage1.selectDomainCode(DOMAIN);
		deleteChnluserPage1.selectCategoryCode(CATEGORY);
		deleteChnluserPage1.enterRemarks(Remarks);
		deleteChnluserPage1.clickSubmitBtn();
		deleteChnluserPage1.selectChannelUserName(channeluserName);
		deleteChnluserPage1.clickSubmitParentBtn();
		deleteChnluserPage1.clickDeleteBtn();
		driver.switchTo().alert().accept();
	}
	
	public void approveDeleteChannelUser_LoginID(String LoginID, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVAL_DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalDeleteChannelUser();
		approvaldeletePage1.enterLoginID(LoginID);
		approvaldeletePage1.enterRemarks(Remarks);
		approvaldeletePage1.clickSubmitBtn();
		approvaldeletePage2.clickApprove();
		approvaldeletePage2.clicksubmitbutton();
		approvaldeletePage2.clickConfirmBtn();
		
	}
	
	public void approveDeleteChannelUser_MSISDN(String MSISDN, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVAL_DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalDeleteChannelUser();
		approvaldeletePage1.enterMSISDN(MSISDN);
		approvaldeletePage1.enterRemarks(Remarks);
		approvaldeletePage1.clickSubmitBtn();
		approvaldeletePage2.clickApprove();
		approvaldeletePage2.clicksubmitbutton();
		approvaldeletePage2.clickConfirmBtn();
		
	}
	
	public void approveDeleteChannelUser_GeoDetails(String DOMAIN, String CATEGORY, String GEODOMAIN, String MSISDN, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVAL_DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalDeleteChannelUser();
		approvaldeletePage1.selectDomainCode(DOMAIN);
		approvaldeletePage1.selectCategoryCode(CATEGORY);
		approvaldeletePage1.selectGeoDomain(GEODOMAIN);
		approvaldeletePage1.enterRemarks(Remarks);
		approvaldeletePage1.clickSubmitBtn();
		approvaldeletePage2.clickDiscardOtherThan(MSISDN);
		approvaldeletePage2.clicksubmitbutton();
		approvaldeletePage2.clickConfirmBtn();
		
	}
	
	public void discardDeleteChannelUser_MSISDN(String MSISDN, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVAL_DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalDeleteChannelUser();
		approvaldeletePage1.enterMSISDN(MSISDN);
		approvaldeletePage1.enterRemarks(Remarks);
		approvaldeletePage1.clickSubmitBtn();
		approvaldeletePage2.clickDiscard();
		approvaldeletePage2.clicksubmitbutton();
		approvaldeletePage2.clickConfirmBtn();
		
	}
	
	public void rejectDeleteChannelUser_MSISDN(String MSISDN, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVAL_DELETE_CHANNEL_USER); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalDeleteChannelUser();
		approvaldeletePage1.enterMSISDN(MSISDN);
		approvaldeletePage1.enterRemarks(Remarks);
		approvaldeletePage1.clickSubmitBtn();
		approvaldeletePage2.clickReject();
		approvaldeletePage2.clicksubmitbutton();
		approvaldeletePage2.clickConfirmBtn();
		
	}
	
}
