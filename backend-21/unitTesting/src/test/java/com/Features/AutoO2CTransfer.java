package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval1AutoO2CTransfer;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval1AutoO2CTransferPage2;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval1AutoO2CTransferPage3;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval1AutoO2CTransferPage4;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval2AutoO2CTransfer;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval2AutoO2CTransferPage2;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval2AutoO2CTransferPage3;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval2AutoO2CTransferPage4;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval3AutoO2CTransfer;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval3AutoO2CTransferPage2;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval3AutoO2CTransferPage3;
import com.pageobjects.channeladminpages.autoO2CTransfer.Approval3AutoO2CTransferPage4;
import com.pageobjects.channeladminpages.autoO2CTransfer.InitiateAutoO2CTransferPage;
import com.pageobjects.channeladminpages.autoO2CTransfer.InitiateAutoO2CTransferPage2;
import com.pageobjects.channeladminpages.autoO2CTransfer.InitiateAutoO2CTransferPage3;
import com.pageobjects.channeladminpages.autoO2CTransfer.InitiateAutoO2CTransferPageIDEA;
import com.pageobjects.channeladminpages.autoO2CTransfer.InitiateAutoO2CTransferPageIDEA2;
import com.pageobjects.channeladminpages.autoO2CTransfer.InitiateAutoO2CTransferPageIDEA3;
import com.pageobjects.channeladminpages.autoO2CTransfer.InitiateAutoO2CTransferPageIDEA4;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.OptToChanSubCatPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.PreferenceSubCategories;
import com.pageobjects.networkadminpages.preferences.ControlPreferencePage1;
import com.pageobjects.networkadminpages.preferences.ControlPreferencePage2;
import com.pageobjects.networkadminpages.preferences.ControlPreferencePage3;
import com.pageobjects.networkadminpages.preferences.ControlPreferencePage4;
import com.pageobjects.networkadminpages.preferences.NetworkPreference;
import com.pageobjects.superadminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.preferences.ServicePreferencePage;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.pageobjects.superadminpages.updatecache.UpdateCachePage;
import com.utils.RandomGeneration;

public class AutoO2CTransfer {

	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	SuperAdminHomePage saHomePage;
	InitiateAutoO2CTransferPage initiateAutoO2CPage;
	InitiateAutoO2CTransferPage2 initiateAutoO2CPage2;
	InitiateAutoO2CTransferPage3 initiateAutoO2CPage3;
	Approval1AutoO2CTransfer approval1AutoO2CTransfer;
	Approval1AutoO2CTransferPage2 approval1AutoO2CTransferPage2;
	Approval1AutoO2CTransferPage3 approval1AutoO2CTransferPage3;
	Approval1AutoO2CTransferPage4 approval1AutoO2CTransferPage4;
	Approval2AutoO2CTransfer approval2AutoO2CTransfer;
	Approval2AutoO2CTransferPage2 approval2AutoO2CTransferPage2;
	Approval2AutoO2CTransferPage3 approval2AutoO2CTransferPage3;
	Approval2AutoO2CTransferPage4 approval2AutoO2CTransferPage4;
	Approval3AutoO2CTransfer approval3AutoO2CTransfer;
	Approval3AutoO2CTransferPage2 approval3AutoO2CTransferPage2;
	Approval3AutoO2CTransferPage3 approval3AutoO2CTransferPage3;
	Approval3AutoO2CTransferPage4 approval3AutoO2CTransferPage4;
	InitiateAutoO2CTransferPageIDEA initiateAutoO2CPageIDEA;
	InitiateAutoO2CTransferPageIDEA2 initiateAutoO2CPageIDEA2;
	InitiateAutoO2CTransferPageIDEA3 initiateAutoO2CPageIDEA3;
	InitiateAutoO2CTransferPageIDEA4 initiateAutoO2CPageIDEA4;
	SystemPreferencePage systemPreferencePage;
	ServicePreferencePage servicePreferencePage;
	MastersSubCategories mastersSubCategories;
	NetworkAdminHomePage networkAdminHomePage;
	NetworkPreference networkPreferencePage;
	PreferenceSubCategories preferenceSubCategories;
	ControlPreferencePage1 controlPreferencePage1;
	ControlPreferencePage2 controlPreferencePage2;								
	ControlPreferencePage3 controlPreferencePage3;
	ControlPreferencePage4 controlPreferencePage4;
	UpdateCachePage updateCache;
	SelectNetworkPage selectNetworkPage;
	OptToChanSubCatPage optToChanSubPage;
	RandomGeneration randmGenrtr;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	Map<String, String> ResultMap;
	
	public AutoO2CTransfer(WebDriver driver) {
		
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		initiateAutoO2CPage = new InitiateAutoO2CTransferPage(driver);
		initiateAutoO2CPage2 = new InitiateAutoO2CTransferPage2(driver);
		initiateAutoO2CPage3 = new InitiateAutoO2CTransferPage3(driver);
		approval1AutoO2CTransfer = new Approval1AutoO2CTransfer(driver);
		approval1AutoO2CTransferPage2 = new Approval1AutoO2CTransferPage2(driver);
		approval1AutoO2CTransferPage3 = new Approval1AutoO2CTransferPage3(driver);
		approval1AutoO2CTransferPage4 = new Approval1AutoO2CTransferPage4(driver);
		approval2AutoO2CTransfer = new Approval2AutoO2CTransfer(driver);
		approval2AutoO2CTransferPage2 = new Approval2AutoO2CTransferPage2(driver);
		approval2AutoO2CTransferPage3 = new Approval2AutoO2CTransferPage3(driver);
		approval2AutoO2CTransferPage4 = new Approval2AutoO2CTransferPage4(driver);
		approval3AutoO2CTransfer = new Approval3AutoO2CTransfer(driver);
		approval3AutoO2CTransferPage2 = new Approval3AutoO2CTransferPage2(driver);
		approval3AutoO2CTransferPage3 = new Approval3AutoO2CTransferPage3(driver);
		approval3AutoO2CTransferPage4 = new Approval3AutoO2CTransferPage4(driver);
		initiateAutoO2CPageIDEA = new InitiateAutoO2CTransferPageIDEA(driver);
		initiateAutoO2CPageIDEA2 = new InitiateAutoO2CTransferPageIDEA2(driver);
		initiateAutoO2CPageIDEA3 = new InitiateAutoO2CTransferPageIDEA3(driver);
		initiateAutoO2CPageIDEA4 = new InitiateAutoO2CTransferPageIDEA4(driver);
		systemPreferencePage = new SystemPreferencePage(driver);
		servicePreferencePage = new ServicePreferencePage(driver);
		controlPreferencePage1 = new ControlPreferencePage1(driver);
		controlPreferencePage2 = new ControlPreferencePage2(driver);
		controlPreferencePage3 = new ControlPreferencePage3(driver);
		controlPreferencePage4 = new ControlPreferencePage4(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		preferenceSubCategories = new PreferenceSubCategories(driver);
		mastersSubCategories = new MastersSubCategories(driver);
		networkAdminHomePage = new NetworkAdminHomePage(driver);
		networkPreferencePage = new NetworkPreference(driver);
		updateCache = new UpdateCachePage(driver);
		optToChanSubPage = new OptToChanSubCatPage(driver);
		randmGenrtr = new RandomGeneration();
		ntwrkPage = new SelectNetworkPage(driver);
		ResultMap = new HashMap<String, String>();
		
	}
	
	public HashMap<String, String> initiateAutoO2CTransfer(HashMap<String, String> initiateMap) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.INITIATE_AUTO_O2C_TRANSFER_ROLECODE); 
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickInitiateAutoO2CTransfer();
		
		if(initiateMap.get("GEO_DOMAIN") != ""){
		initiateAutoO2CPage.selectGeographyDomain(initiateMap.get("GEO_DOMAIN"));
		}
		if(initiateMap.get("TO_DOMAIN") != "")
		initiateAutoO2CPage.selectDomain(initiateMap.get("TO_DOMAIN"));
		if((initiateMap.get("TO_CATEGORY") != "") && (initiateMap.get("TO_DOMAIN") != ""))
		initiateAutoO2CPage.selectCategory(initiateMap.get("TO_CATEGORY"));
		initiateAutoO2CPage.clickSubmitButton();
		if(initiateMap.get("ChannelUser") != "")
		{initiateAutoO2CPage2.enterChannelUser(initiateMap.get("ChannelUser"));
		initiateAutoO2CPage2.searchButton();
		initiateAutoO2CPage2.switchscreen();}
		initiateAutoO2CPage2.clickSubmitButton();
		
		initiateAutoO2CPage3.clickConfirmButton();
		String message = initiateAutoO2CPage.getMessage();
		
		initiateMap.put("Message", message);
		return initiateMap;
		
	}
	
	public String getErrorMessage() {
		if (initiateAutoO2CPage.getMessage() != null)
			return initiateAutoO2CPage.getMessage();
		else
			return initiateAutoO2CPage.getErrorMessage();
		}
	
public Map<String, String> performingLevel1Approval(HashMap<String, String> initiateMap,String type) throws InterruptedException {
		
	    userAccessMap= UserAccess.getUserWithAccess(RolesI.AUTO_O2C_APPROVAL1_ROLECODE);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickOperatorToChannel();
		optToChanSubPage.clickAutoO2CApproveLevel1();
		if(initiateMap.get("GEO_DOMAIN") != ""){
			approval1AutoO2CTransfer.selectGeographyDomain(initiateMap.get("GEO_DOMAIN"));
			}
			if(initiateMap.get("TO_DOMAIN") != "")
		    approval1AutoO2CTransfer.selectDomain(initiateMap.get("TO_DOMAIN"));
			if((initiateMap.get("TO_CATEGORY") != "") && (initiateMap.get("TO_DOMAIN") != ""))
			approval1AutoO2CTransfer.selectCategory(initiateMap.get("TO_CATEGORY"));
			approval1AutoO2CTransfer.clickSubmitButton();
			approval1AutoO2CTransferPage2.clickOnRadioButton(initiateMap.get("ChannelUser"));
			approval1AutoO2CTransferPage2.clickSubmitButton();
			if(type == "Rejecting")
				approval1AutoO2CTransferPage3.clickRejectButton();
			else
			approval1AutoO2CTransferPage3.clickApproveButton();
			approval1AutoO2CTransferPage4.clickApproveButton();
		    ResultMap.put("actualMessage", approval1AutoO2CTransfer.getMessage());
		    return ResultMap;
	}

    public Map<String, String> performingLevel2Approval(HashMap<String, String> initiateMap) throws InterruptedException {
	
    userAccessMap= UserAccess.getUserWithAccess(RolesI.AUTO_O2C_APPROVAL2_ROLECODE);
	login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	selectNetworkPage.selectNetwork();
	caHomepage.clickOperatorToChannel();
	optToChanSubPage.clickAutoO2CApproveLevel2();
	if(initiateMap.get("GEO_DOMAIN") != ""){
		approval2AutoO2CTransfer.selectGeographyDomain(initiateMap.get("GEO_DOMAIN"));
		}
		if(initiateMap.get("TO_DOMAIN") != "")
			approval2AutoO2CTransfer.selectDomain(initiateMap.get("TO_DOMAIN"));
		if((initiateMap.get("TO_CATEGORY") != "") && (initiateMap.get("TO_DOMAIN") != ""))
			approval2AutoO2CTransfer.selectCategory(initiateMap.get("TO_CATEGORY"));
		approval2AutoO2CTransfer.clickSubmitButton();
		approval2AutoO2CTransferPage2.clickOnRadioButton(initiateMap.get("ChannelUser"));
		approval2AutoO2CTransferPage2.clickSubmitButton();
		approval2AutoO2CTransferPage3.clickApproveButton();
		approval2AutoO2CTransferPage4.clickApproveButton();
	    ResultMap.put("actualMessage", approval2AutoO2CTransfer.getMessage());
	    return ResultMap;
}
    
    public Map<String, String> performingLevel3Approval(HashMap<String, String> initiateMap) throws InterruptedException {
    	
        userAccessMap= UserAccess.getUserWithAccess(RolesI.AUTO_O2C_APPROVAL3_ROLECODE);
    	login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
    	selectNetworkPage.selectNetwork();
    	caHomepage.clickOperatorToChannel();
    	optToChanSubPage.clickAutoO2CApproveLevel3();
    	if(initiateMap.get("GEO_DOMAIN") != ""){
    		approval3AutoO2CTransfer.selectGeographyDomain(initiateMap.get("GEO_DOMAIN"));
    		}
    		if(initiateMap.get("TO_DOMAIN") != "")
    			approval3AutoO2CTransfer.selectDomain(initiateMap.get("TO_DOMAIN"));
    		if((initiateMap.get("TO_CATEGORY") != "") && (initiateMap.get("TO_DOMAIN") != ""))
    			approval3AutoO2CTransfer.selectCategory(initiateMap.get("TO_CATEGORY"));
    		approval3AutoO2CTransfer.clickSubmitButton();
    		approval3AutoO2CTransferPage2.clickOnRadioButton(initiateMap.get("ChannelUser"));
    		approval3AutoO2CTransferPage2.clickSubmitButton();
    		approval3AutoO2CTransferPage3.clickApproveButton();
    		approval3AutoO2CTransferPage4.clickApproveButton();
    	    ResultMap.put("actualMessage", approval3AutoO2CTransfer.getMessage());
    	    return ResultMap;
    }
    
    public HashMap<String, String> initiateAutoO2CTransferIDEA(HashMap<String, String> initiateMap, String autoO2CAllowed) throws InterruptedException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.AUTO_O2C_CREDIT_LIMIT_ROLECODE); 
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		caHomepage.clickMasters();
		optToChanSubPage.clickAutoO2CCreditLimit();
		initiateAutoO2CPageIDEA.selectType();
		initiateAutoO2CPageIDEA.clickOnSubmit();
		initiateAutoO2CPageIDEA2.enterMSISDN(initiateMap.get("MSISDN"));
		initiateAutoO2CPageIDEA2.clickAddModifyButton();
		initiateAutoO2CPageIDEA3.selectIsAutoO2CAllowed(autoO2CAllowed);
		initiateAutoO2CPageIDEA3.enterMaxAmt(initiateMap.get("maxTxnAmount"));
		initiateAutoO2CPageIDEA3.enterDailyCount(initiateMap.get("dailyCount"));
		initiateAutoO2CPageIDEA3.enterWeeklyCount(initiateMap.get("weeklyCount"));
		initiateAutoO2CPageIDEA3.enterMonthlyCount(initiateMap.get("monthlyCount"));
		initiateAutoO2CPageIDEA3.clickAddModifyButton();
		initiateAutoO2CPageIDEA4.clickConfirmButton();
		
		String message = initiateAutoO2CPage.getMessage();
		
		initiateMap.put("Message", message);
		return initiateMap;
		
	}
    
    public void changePreference(String controlPreference, String networkPreference, String systemPreferenceType,String categoryName,String valueToSet)
    {
    	String preference = "auto o2c transfer allowed";
    	String preferenceCode = "AUTO_O2C_TRANSFER_ALLOWED";
    	if(controlPreference.equalsIgnoreCase("false") && controlPreference!=null)
    	{
    		userAccessMap = UserAccess.getUserWithAccess(RolesI.CONTROL_PREFERENCE); 
			login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			networkAdminHomePage.clickPreferences();
			preferenceSubCategories.clickControlPreferencelink();
			controlPreferencePage1.selectControlType("Category");
			controlPreferencePage1.clickSubmitBtn();
			controlPreferencePage2.selectControlType(preferenceCode);
			controlPreferencePage2.clickSubmitBtn();
			controlPreferencePage3.setValueofControlPreference(categoryName, valueToSet);
			controlPreferencePage3.clickSubmitBtn();
			controlPreferencePage4.clickConfirmBtn();
			userAccessMap = UserAccess.getUserWithAccess(RolesI.UPDATE_CACHE_ROLECODE); 
			login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			selectNetworkPage.selectNetwork();
			saHomePage.clickMasters();
			mastersSubCategories.clickUpdateCache();
			updateCache.checkAllCache();
			updateCache.clickSubmitButton();
    	}
    	else if(controlPreference == null && networkPreference.equalsIgnoreCase("false") && networkPreference!=null)
    	{
    			userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_PREFERENCE); 
    			login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
    			networkAdminHomePage.clickPreferences();
    			networkPreferencePage.setValueofNetworkPreference(preference, "true");
    			networkPreferencePage.clickModifyBtn();
    			networkPreferencePage.clickConfirmBtn();
    			userAccessMap = UserAccess.getUserWithAccess(RolesI.UPDATE_CACHE_ROLECODE); 
    			login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
    			selectNetworkPage.selectNetwork();
    			saHomePage.clickMasters();
    			mastersSubCategories.clickUpdateCache();
    			updateCache.checkAllCache();
    			updateCache.clickSubmitButton();
    		}
    		else if(controlPreference == null && networkPreference == null)
    		{
    			userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE); 
    			login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
    			selectNetworkPage.selectNetwork();
    			saHomePage.clickPreferences();
    			systemPreferencePage.selectModule("C2S");
    			systemPreferencePage.selectPreferenceType(systemPreferenceType);
    			systemPreferencePage.clickSubmitButton();
    			servicePreferencePage.setValueofServicePreference(preference, "true");
    			servicePreferencePage.clickModifyBtn();
    			servicePreferencePage.clickConfirmBtn();
    			saHomePage.clickMasters();
    			mastersSubCategories.clickUpdateCache();
    			updateCache.checkAllCache();
    			updateCache.clickSubmitButton();
    		}
    	}
    }

