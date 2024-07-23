package com.utils;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.classes.Login;
import com.commons.MasterI;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;

import jxl.JXLException;

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
	
	public void updateCache() throws IOException, InterruptedException, JXLException{
		
		//Initializing the Required Pages
		Login LogintoSuperAdmin = new Login();
		SelectNetworkPage selectNetwork = new SelectNetworkPage(driver);
		SuperAdminHomePage homePage = new SuperAdminHomePage(driver);
	
		try {
		WebElement Logout = driver.findElement(By.linkText("Logout"));
		Logout.click();
		}
		catch (NoSuchElementException NoElementException) { Log.writeStackTrace(NoElementException); }
		catch (Exception exception) { Log.writeStackTrace(exception); }
		String WEBURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		LogintoSuperAdmin.UserLogin(driver, "Operator", "Super Admin");
		selectNetwork.selectNetwork();
		homePage.clickMasters();
		homePage.clickUpdateCache();
		driver.findElement(By.name("cacheAll")).click();
		driver.findElement(By.name("submitButton")).click();
		try {
			WebElement Logout = driver.findElement(By.linkText("Logout"));
			Logout.click();
			}
			catch (NoSuchElementException NoElementException) { Log.writeStackTrace(NoElementException); }
			catch (Exception exception) { Log.writeStackTrace(exception); }
		driver.get(WEBURL);
	}
}
