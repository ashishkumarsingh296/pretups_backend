package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.channeladminpages.suspendchanneluser.ApproveSuspendChannelUserPage1;
import com.pageobjects.channeladminpages.suspendchanneluser.ApproveSuspendChannelUserPage2;
import com.pageobjects.channeladminpages.suspendchanneluser.SuspendChannelUserPage1;
import com.pageobjects.channeladminpages.suspendchanneluser.SuspendChannelUserPage2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class SuspendChannelUser extends BaseTest {
	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	AddChannelUserPage addChrUserPage;
	ChannelUsersSubCategories channelUserSubCategories;
	SuspendChannelUserPage1 suspendCHNL1;
	SuspendChannelUserPage2 suspendCHNL2;
	ApproveSuspendChannelUserPage1 approvesuspendCHNL1;
	ApproveSuspendChannelUserPage2 approvesuspendCHNL2;
	SelectNetworkPage selectNetwork; 
	Map<String, String> userAccessMap;
	
	WebDriver driver=null;
	
	public SuspendChannelUser(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		login = new Login();
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		userAccessMap = new HashMap<String, String>();
		suspendCHNL1 = new SuspendChannelUserPage1(driver);
		suspendCHNL2 = new SuspendChannelUserPage2(driver);
		approvesuspendCHNL1 = new ApproveSuspendChannelUserPage1(driver);
		approvesuspendCHNL2 = new ApproveSuspendChannelUserPage2(driver);
		addChrUserPage = new AddChannelUserPage(driver);
		selectNetwork = new SelectNetworkPage(driver);
	}

	public String suspendChannelUser_LoginID(String loginID, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickSuspendChannelUser();
		suspendCHNL1.enterLoginID(loginID);
		suspendCHNL1.enterRemarks(Remarks);
		suspendCHNL1.clickSubmitBtn();
		suspendCHNL2.clickSubmitBtn();
		driver.switchTo().alert().accept();
		String message = suspendCHNL2.fetchMessage();
		
		return message;
	}
	
	public String suspendChannelUser_MSISDN(String msisdn,String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickSuspendChannelUser();
		suspendCHNL1.enterMSISDN(msisdn);
		suspendCHNL1.enterRemarks(Remarks);
		suspendCHNL1.clickSubmitBtn();
		suspendCHNL2.clickSubmitBtn();
		driver.switchTo().alert().accept();
		String message = suspendCHNL2.fetchMessage();
		
		return message;
	}
	
	public String suspendChannelUser_GeoDetails(String loginID, String domain, String category, String geoDomain, String Remarks) throws InterruptedException, IOException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		String ownerName = login.UserNameSequence(driver, "Channel", domain, "1");
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		int rowNum = ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, loginID);
		String channelName = ExcelUtility.getCellData(0, "USER NAME", rowNum);
		String geoCount = DBHandler.AccessHandler.fetchUserGeographyCount(loginID);
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickSuspendChannelUser();
		suspendCHNL1.selectDomainCode(domain);
		suspendCHNL1.selectCategoryCode(category);
		if(!geoCount.equals("1")){
		suspendCHNL1.selectGeoDomain(geoDomain);}
		suspendCHNL1.enterRemarks(Remarks);
		suspendCHNL1.clickSubmitBtn();
		suspendCHNL2.enterOwnerUser();
		suspendCHNL2.selectOwnerName(ownerName);
		suspendCHNL2.enterChannelUser();
		suspendCHNL2.selectChannelUserName(channelName);
		suspendCHNL2.clickPrntSubmitBtn();
		suspendCHNL2.clickSubmitBtn();
		driver.switchTo().alert().accept();
		String message = suspendCHNL2.fetchMessage();
		
		return message;
	}
	
	public void suspendChannelUser_back(String loginID, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickSuspendChannelUser();
		suspendCHNL1.enterLoginID(loginID);
		suspendCHNL1.enterRemarks(Remarks);
		suspendCHNL1.clickSubmitBtn();
		suspendCHNL2.clickBackBtn();
		suspendCHNL1.enterRemarks(Remarks);
	}
	
	public String approveCSuspendRequest_LoginID(String loginID, String Remarks){
		String message = null;
		boolean suspendApproval = Boolean.parseBoolean(DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.REQ_CUSER_SUS_APP));
		if (suspendApproval) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVESUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApproveSuspendChannelUser();
		approvesuspendCHNL1.enterLoginID(loginID);
		approvesuspendCHNL1.enterRemarks(Remarks);
		approvesuspendCHNL1.clickSubmitBtn();
		approvesuspendCHNL2.selectToApprove();
		approvesuspendCHNL2.clickSubmitBtn();
		approvesuspendCHNL2.clickConfirmBtn();
		message = suspendCHNL2.fetchMessage();
		} else {
			Log.info("Approval for Suspend User not required.");
		}
		return message;
	}
	
	public String approveCSuspendRequest_MSISDN(String msisdn, String Remarks){
		String message = null;
		boolean suspendApproval = Boolean.parseBoolean(DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.REQ_CUSER_SUS_APP));
		if (suspendApproval) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVESUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApproveSuspendChannelUser();
		approvesuspendCHNL1.enterMSISDN(msisdn);
		approvesuspendCHNL1.enterRemarks(Remarks);
		approvesuspendCHNL1.clickSubmitBtn();
		approvesuspendCHNL2.selectToApprove();
		approvesuspendCHNL2.clickSubmitBtn();
		approvesuspendCHNL2.clickConfirmBtn();
		message = suspendCHNL2.fetchMessage();	
		} else {
			Log.info("Approval for Suspend User is not required.");
		}
		return message;
	}
	
	public String rejectCSuspendRequest_MSISDN(String loginID, String Remarks){
		String message = null;
		boolean suspendApproval = Boolean.parseBoolean(DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.REQ_CUSER_SUS_APP));
		if (suspendApproval) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVESUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApproveSuspendChannelUser();
		approvesuspendCHNL1.enterMSISDN(loginID);
		approvesuspendCHNL1.enterRemarks(Remarks);
		approvesuspendCHNL1.clickSubmitBtn();
		approvesuspendCHNL2.selectToReject();
		approvesuspendCHNL2.clickSubmitBtn();
		approvesuspendCHNL2.clickConfirmBtn();
		message = suspendCHNL2.fetchMessage();
		} else {
			Log.info("Approval for Suspend User is not required.");
		}
		return message;
	}
	
	public String rejectCSuspendRequest_LoginID(String loginID, String Remarks){
		String message = null;
		boolean suspendApproval = Boolean.parseBoolean(DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.REQ_CUSER_SUS_APP));
		if (suspendApproval) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVESUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApproveSuspendChannelUser();
		approvesuspendCHNL1.enterLoginID(loginID);
		approvesuspendCHNL1.enterRemarks(Remarks);
		approvesuspendCHNL1.clickSubmitBtn();
		approvesuspendCHNL2.selectToReject();
		approvesuspendCHNL2.clickSubmitBtn();
		approvesuspendCHNL2.clickConfirmBtn();
		message = suspendCHNL2.fetchMessage();
		} else {
			Log.info("Approval for Suspend User is not required.");
		}
		return message;
	}
	
	public String discardCSuspendRequest_LoginID(String loginID, String Remarks){
		String message = null;
		boolean suspendApproval = Boolean.parseBoolean(DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.REQ_CUSER_SUS_APP));
		if (suspendApproval) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVESUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApproveSuspendChannelUser();
		approvesuspendCHNL1.enterLoginID(loginID);
		approvesuspendCHNL1.enterRemarks(Remarks);
		approvesuspendCHNL1.clickSubmitBtn();
		approvesuspendCHNL2.selectToDiscard();
		approvesuspendCHNL2.clickSubmitBtn();
		approvesuspendCHNL2.clickConfirmBtn();
		message = suspendCHNL2.fetchMessage();
		} else {
			Log.info("Approval for Suspend User is not required");
		}
		return message;
	}
	
	public String discardCSuspendRequest_MSISDN(String MSISDN, String Remarks){
		String message = null;
		boolean suspendApproval = Boolean.parseBoolean(DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), CONSTANT.REQ_CUSER_SUS_APP));
		if (suspendApproval) {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.APPROVESUSPEND_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApproveSuspendChannelUser();
		approvesuspendCHNL1.enterMSISDN(MSISDN);
		approvesuspendCHNL1.enterRemarks(Remarks);
		approvesuspendCHNL1.clickSubmitBtn();
		approvesuspendCHNL2.selectToDiscard();
		approvesuspendCHNL2.clickSubmitBtn();
		approvesuspendCHNL2.clickConfirmBtn();
		message = suspendCHNL2.fetchMessage();
		} else {
			Log.info("Approval for Suspend User is not required");
		}
		return message;
	}

	public void messageCompare(String actual, String expected){
		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
			}
	}
	
	public String suspendChannelUser_MSISDN(String LoginID, String Password, String childmsisdn,String Remarks){
		
		login.LoginAsUser(driver, LoginID, Password);
		
		homePage.clickChannelUsers();
		channelUserSubCategories.clickSuspendChannelUser();
		suspendCHNL1.enterMSISDN(childmsisdn);
		suspendCHNL1.enterRemarks(Remarks);
		suspendCHNL1.clickSubmitBtn();
		suspendCHNL2.clickSubmitBtn();
		driver.switchTo().alert().accept();
		String message = suspendCHNL2.fetchMessage();
		
		return message;
	}
	
	
	public void modifyPreference(String preferenceCode,String valuetoset){
		Map<String,String> usermap=UserAccess.getUserWithAccess(RolesI.MODIFY_SYSTEM_PRF);
		login.LoginAsUser(driver, usermap.get("LOGIN_ID"), usermap.get("PASSWORD"));
		String prefernce = DBHandler.AccessHandler.getPreference("",_masterVO.getMasterValue(ExcelI.NETWORK_CODE), preferenceCode);
		String preferenceCode1 = DBHandler.AccessHandler.getNamefromSystemPreference(preferenceCode);
		boolean updateCache = false;
		new SelectNetworkPage(driver).selectNetwork();
		SystemPreferencePage sysPref = new SystemPreferencePage(driver);
		SuperAdminHomePage suHomepage= new SuperAdminHomePage(driver);
		suHomepage.clickPreferences();
		sysPref.clickSystemPrefernce();
		
		
		if(!prefernce.toUpperCase().equals(valuetoset.toUpperCase())){
		sysPref.selectModule("C2S");
		sysPref.selectSystemPreference();
		sysPref.clickSubmitButton();
		sysPref.setValueofSystemPreference(preferenceCode1, valuetoset);
		sysPref.clickModifyBtn();
		sysPref.clickConfirmBtn();
		updateCache=true;
		}
		else{Log.info("Preference for "+preferenceCode1+" is already set as: "+valuetoset);}
		if(updateCache){
		new UpdateCache().updateCache();}
	}
	
}
