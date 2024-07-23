package com.utils;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.MasterI;
import com.commons.RolesI;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.updatecache.UpdateCachePage;

/**
 * @author krishan.chawla
 * This class is created to perform Update Cache.
 * On Calling this class, the current Logged in user gets Logged out. Update Cache is performed & the user is moved back to URL page.
 */
public class CacheUpdate {

	WebDriver driver = null;
	
	public CacheUpdate(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void updateCache() {
	
		Map<String, String> userAccessMap = new HashMap<String, String>();
		
		//Initializing the Required Pages
		Login login = new Login();
		SelectNetworkPage selectNetwork = new SelectNetworkPage(driver);
		SuperAdminHomePage homePage = new SuperAdminHomePage(driver);
		UpdateCachePage UpdateCache = new UpdateCachePage(driver);
	
		try { homePage.clickLogout(); }
		catch (Exception exception) { }
		
		String WEBURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.UPDATE_CACHE_ROLECODE); //Getting User with Access to Update Cache
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetwork.selectNetwork();
		homePage.clickMasters();
		homePage.clickUpdateCache();
		
		if (_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("1"))
			UpdateCache.checkAllInstances();
		
		UpdateCache.checkAllCache();
		UpdateCache.clickSubmitButton();

		try { homePage.clickLogout(); }
		catch (Exception exception) { }
		
		driver.get(WEBURL);
	}
	
	
	public void updateCache(String ... cacheParams) {
	
		Map<String, String> userAccessMap = new HashMap<String, String>();
		
		//Initializing the Required Pages
		Login login = new Login();
		SelectNetworkPage selectNetwork = new SelectNetworkPage(driver);
		SuperAdminHomePage homePage = new SuperAdminHomePage(driver);
		UpdateCachePage UpdateCache = new UpdateCachePage(driver);
	
		try { homePage.clickLogout(); }
		catch (Exception exception) { }
		
		String WEBURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.UPDATE_CACHE_ROLECODE); //Getting User with Access to Update Cache
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetwork.selectNetwork();
		homePage.clickMasters();
		homePage.clickUpdateCache();
		
		if (_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("1"))
			UpdateCache.checkAllInstances();
		
		if (_masterVO.getClientDetail("UPDATECACHETYPE").equalsIgnoreCase("Specific"))
			UpdateCache.selectCacheParams(cacheParams);
		else
			UpdateCache.checkAllCache();
		
		UpdateCache.clickSubmitButton();

		try { homePage.clickLogout(); }
		catch (Exception exception) { }
		
		driver.get(WEBURL);
	}
}
