package com.Features;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.MasterI;
import com.commons.RolesI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.updatecache.UpdateCachePage;
import com.utils.Log;
import com.utils._masterVO;

public class CacheUpdate {

	WebDriver driver;
	SuperAdminHomePage SuperAdminHomePage;
	NetworkAdminHomePage NetworkAdminHomePage;
	Login login;
	com.pageobjects.superadminpages.homepage.MastersSubCategories SuperAdminMastersCategory;
	com.pageobjects.networkadminpages.homepage.MastersSubCategories NetworkAdminMastersCategories;
	SelectNetworkPage selectNetwork;
	UpdateCachePage updateCache;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	/*
	 * Constructor for CacheUpdate which loads the driver and all requried Pages
	 */
	public CacheUpdate(WebDriver driver) {
		this.driver = driver;
		
		//Page Initialization
		SuperAdminHomePage = new SuperAdminHomePage(driver);
		login = new Login();
		SuperAdminMastersCategory = new com.pageobjects.superadminpages.homepage.MastersSubCategories(driver);
		NetworkAdminMastersCategories = new com.pageobjects.networkadminpages.homepage.MastersSubCategories(driver);
		selectNetwork = new SelectNetworkPage(driver);
		updateCache = new UpdateCachePage(driver);
	}
	
	public String updateCache() {
		
		String WEBURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.UPDATE_CACHE_ROLECODE); //Getting User with Access to Update Cache
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		selectNetwork.selectNetwork();
		SuperAdminHomePage.clickMasters();
		SuperAdminHomePage.clickUpdateCache();
		
		if (_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("1"))
			updateCache.checkAllInstances();
		
		updateCache.checkAllCache();
		updateCache.clickSubmitButton();
		String MessageText = updateCache.getMessage();
		try {
			SuperAdminHomePage.clickLogout();
			}
		catch (Exception exception) 
		{ 
			Log.writeStackTrace(exception); 
		}
		driver.get(WEBURL);
		return MessageText;
	}
	
	public String updateCache(String... cacheParams) {
		final String methodname = "updateCache";
		Log.methodEntry(methodname, Arrays.toString(cacheParams));
		
		String WEBURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.UPDATE_CACHE_ROLECODE); //Getting User with Access to Update Cache
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		selectNetwork.selectNetwork();
		SuperAdminHomePage.clickMasters();
		SuperAdminHomePage.clickUpdateCache();
		
		if (_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("1"))
			updateCache.checkAllInstances();

		if (_masterVO.getClientDetail("UPDATECACHETYPE").equalsIgnoreCase("Specific"))
			updateCache.selectCacheParams(cacheParams);
		
		if (_masterVO.getClientDetail("UPDATECACHETYPE").equalsIgnoreCase("Redis"))
			updateCache.selectCacheParams(cacheParams);
		else
			updateCache.checkAllCache();

		updateCache.clickSubmitButton();
		String MessageText = updateCache.getMessage();
		try { SuperAdminHomePage.clickLogout();	}
		catch (Exception exception) { }
		driver.get(WEBURL);
		
		Log.methodExit(methodname);
		return MessageText;
	}
	
	
	public boolean validateMessage(String Message) {
		Log.info("Trying to Validate Message");
		Log.info("<b>Actual Message:</b> " + Message);
		boolean messageValidationStatus = false;
		if (Message.contains("updated successufully")) {
			messageValidationStatus = true;
		}
		return messageValidationStatus;
	}
	
}
