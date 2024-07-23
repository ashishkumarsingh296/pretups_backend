package com.Features;

import com.classes.*;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.*;
import com.pageobjects.channeladminpages.associateProfile.associateLoanProfilePage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.loginpages.ChangePINForNewUser;
import com.pageobjects.loginpages.ChangePasswordForNewUser;
import com.pageobjects.networkadminpages.LoanProfilePage.AddLoanProfilePage;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pretupsControllers.BTSLUtil;
import com.utils.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;


public class AssociateLoanProfile extends BaseTest {
    public WebDriver driver;

    ChannelAdminHomePage homePage;
    Login login;
    RandomGeneration RandomGenerator;
    AddLoanProfilePage AddLoanProfilePage;
    String selectedNetwork;
    Map<String, String> userAccessMap = new HashMap();
    String MasterSheetPath = _masterVO.getProperty("DataProvider");
    SelectNetworkPage networkPage;
    ChannelUsersSubCategories channelUsersSubCategories;
    associateLoanProfilePage associateLoanProfilePage;
    String masterSheetPath;
    public int RowNum;
    String APPLEVEL;
    public static String webAccess;
    RandomGeneration randStr;
    ChangePasswordForNewUser changenewpwd;
    OperatorUsersSubCategories operatorSubLink;
    ApproveChannelUserPage apprvChannelUsrPage;
    ChannelUsersSubCategories channelUserSubCategories;
    AddChannelUserPage addChrUserPage;
    AddChannelUserDetailsPage addChrUserDetailsPage;
    ChangePINForNewUser changeUsrPIN;
    ModifyChannelUserPage1 modifyCHNLpage1;
    ModifyChannelUserPage2 modifyCHNLpage2;
    HashMap<String, String> channelresultMap;

    CommonUtils commonUtils;
    public String LoginID;

    public AssociateLoanProfile(WebDriver driver) {
        this.driver = driver;
        homePage = new ChannelAdminHomePage(driver);
        login = new Login();
        RandomGenerator = new RandomGeneration();
        AddLoanProfilePage = new AddLoanProfilePage(driver);
        selectedNetwork = _masterVO.getMasterValue("Network Code");
        networkPage = new SelectNetworkPage(driver);
        userAccessMap = new HashMap();
        channelUsersSubCategories = new ChannelUsersSubCategories(driver);
        masterSheetPath =_masterVO.getProperty("DataProvider");
        associateLoanProfilePage = new associateLoanProfilePage(driver);
        commonUtils = new CommonUtils();
        randStr = new RandomGeneration();
        changenewpwd = new ChangePasswordForNewUser(driver);
        operatorSubLink = new OperatorUsersSubCategories(driver);
        apprvChannelUsrPage= new ApproveChannelUserPage(driver);
        channelUserSubCategories = new ChannelUsersSubCategories(driver);
        addChrUserPage = new AddChannelUserPage(driver);
        addChrUserDetailsPage = new AddChannelUserDetailsPage(driver);
        apprvChannelUsrPage = new ApproveChannelUserPage(driver);
        changeUsrPIN = new ChangePINForNewUser(driver);
        userAccessMap = new HashMap<String, String>();
        channelresultMap = new HashMap<String, String>();
        modifyCHNLpage1 = new ModifyChannelUserPage1(driver);
        modifyCHNLpage2 = new ModifyChannelUserPage2(driver);


    }

    public String associateLoanProfile(String MSISDN, String LoginID, String Domain, String Parent, String category, String geoType, String profileName) throws InterruptedException {
        final String methodname = "assignLoanProfile";
        Log.methodEntry(methodname, MSISDN, LoginID, Domain, Parent, category, geoType, profileName);
        String actualMessage;
        userAccessMap = UserAccess.getUserWithAccess(RolesI.ASSOCIATE_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

        networkPage.selectNetwork();

        homePage.clickChannelUsers();
        channelUsersSubCategories.clickAssociateProfileSpring();

        associateLoanProfilePage.enterSearchMsisdn(MSISDN);
        associateLoanProfilePage.clickSubmitButton();

        associateLoanProfilePage.selectLoanProfile(profileName);

        associateLoanProfilePage.clickSaveButton();

        associateLoanProfilePage.clickConfirmButton();
        actualMessage=associateLoanProfilePage.getMessage();

        Log.methodExit(methodname);
        return actualMessage;
    }

    public String associateLoanProfileUserCreation(String MSISDN, String LoginID, String Domain, String Parent, String category, String geoType, String profileName) throws InterruptedException {
        final String methodname = "associateLoanProfileUserCreation";
        Log.methodEntry(methodname, MSISDN, LoginID, Domain, Parent, category, geoType, profileName);
        String actualMessage;
        userAccessMap = UserAccess.getUserWithAccess(RolesI.ASSOCIATE_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

        networkPage.selectNetwork();

        homePage.clickChannelUsers();
        channelUsersSubCategories.clickAssociateProfileSpring();

        associateLoanProfilePage.enterSearchMsisdn(MSISDN);
        associateLoanProfilePage.clickSubmitButton();

        associateLoanProfilePage.selectLoanProfile(profileName);

        associateLoanProfilePage.clickSaveButton();

        associateLoanProfilePage.clickConfirmButton();
        actualMessage=associateLoanProfilePage.getMessage();

        Log.methodExit(methodname);
        return actualMessage;
    }



}
