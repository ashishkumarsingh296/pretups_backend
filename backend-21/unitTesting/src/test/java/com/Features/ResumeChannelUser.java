package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.channeladminpages.resumechanneluser.ResumeChannelUserPage1;
import com.pageobjects.channeladminpages.resumechanneluser.ResumeChannelUserPage2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

/**
 * @author lokesh.kontey
 *
 */
public class ResumeChannelUser extends BaseTest {
	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	AddChannelUserPage addChrUserPage;
	ChannelUsersSubCategories channelUserSubCategories;
	ResumeChannelUserPage1 resumeCHNL1;
	ResumeChannelUserPage2 resumeCHNL2;
	Map<String, String> userAccessMap;
	SelectNetworkPage selectNetwork;
	WebDriver driver=null;
	
	public ResumeChannelUser(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		login = new Login();
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		userAccessMap = new HashMap<String, String>();
		resumeCHNL1 = new ResumeChannelUserPage1(driver);
		resumeCHNL2 = new ResumeChannelUserPage2(driver);
		addChrUserPage = new AddChannelUserPage(driver);
		selectNetwork = new SelectNetworkPage(driver);
	}

	public String resumeChannelUser_LoginID(String loginID, String msisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.RESUME_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickResumeChannelUser();
		resumeCHNL1.enterLoginID(loginID);
		resumeCHNL1.enterRemarks(Remarks);
		resumeCHNL1.clickSubmitBtn();
		resumeCHNL2.selectCheckBox(msisdn);
		resumeCHNL2.clickSubmitBtn();
		resumeCHNL2.clickConfirmBtn();
		String message=resumeCHNL2.fetchMessage();
		
		return message;
	}
	
	public String resumeChannelUser_MSISDN(String msisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.RESUME_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickResumeChannelUser();
		resumeCHNL1.enterMSISDN(msisdn);
		resumeCHNL1.enterRemarks(Remarks);
		resumeCHNL1.clickSubmitBtn();
		resumeCHNL2.selectCheckBox(msisdn);
		resumeCHNL2.clickSubmitBtn();
		resumeCHNL2.clickConfirmBtn();
		String message=resumeCHNL2.fetchMessage();
		
		return message;
	}
	
	public String resumeChannelUser_GeoDetails(String loginID, String domain, String category, String geoDomain, String Remarks, String msisdn) throws InterruptedException, IOException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.RESUME_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String geoCount = DBHandler.AccessHandler.fetchUserGeographyCount(loginID);
		//String ownerName = login.UserNameSequence(driver, "Channel", domain, "1");
		//String MasterSheetPath=_masterVO.getMasterValue("DataProvider");
		//int rowNum = ExcelUtility.searchStringRowNum(MasterSheetPath, "Channel Users Hierarchy", loginID);
		//String channelName = ExcelUtility.getCellData(0, "USER NAME", rowNum);
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickResumeChannelUser();
		resumeCHNL1.selectDomainCode(domain);
		resumeCHNL1.selectCategoryCode(category);
		if(!geoCount.equals("1")){
		resumeCHNL1.selectGeoDomain(geoDomain);}
		resumeCHNL1.enterRemarks(Remarks);
		resumeCHNL1.clickSubmitBtn();
		resumeCHNL2.selectCheckBox(msisdn);
		resumeCHNL2.clickSubmitBtn();
		resumeCHNL2.clickConfirmBtn();
		String message=resumeCHNL2.fetchMessage();
		
		return message;
		
	}
	
	public void resumeChannelUser_back(String loginID, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.RESUME_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickResumeChannelUser();
		resumeCHNL1.enterLoginID(loginID);
		resumeCHNL1.enterRemarks(Remarks);
		resumeCHNL1.clickSubmitBtn();
		resumeCHNL2.clickBackBtn();
	}
}
