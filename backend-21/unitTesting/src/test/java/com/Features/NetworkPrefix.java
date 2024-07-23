package com.Features;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.CacheController;
import com.pageobjects.networkadminpages.homepage.MastersSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.networkprefixes.NetworkPrefixesConfirmPage;
import com.pageobjects.networkadminpages.networkprefixes.NetworkPrefixesPage1;
import com.pageobjects.networkadminpages.prefixservicemapping.PrefixServiceMappingPage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class NetworkPrefix {
	WebDriver driver;
	NetworkAdminHomePage homePage;
	Login login;
	NetworkPrefixesPage1 networkPrefixesPage1;
	NetworkPrefixesConfirmPage networkPrefixesConfirmPage;
	MastersSubCategories mastersSubCategory;
	PrefixServiceMappingPage pefixServiceMappingPage;
	SelectNetworkPage selectNetworkPage;
	CacheUpdate cacheUpdate;

	public NetworkPrefix(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		networkPrefixesPage1 = new NetworkPrefixesPage1(driver);
		networkPrefixesConfirmPage = new NetworkPrefixesConfirmPage(driver);
		mastersSubCategory = new MastersSubCategories(driver);
		pefixServiceMappingPage = new PrefixServiceMappingPage(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		cacheUpdate = new CacheUpdate(driver);
	}

	public String[] addSeries(String series, String seriesType) {
		
		String[] result = new String[2];
		String seriesValue = "," + series;
		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkPrefix();
		if (seriesType.equals("Prepaid"))
			networkPrefixesPage1.EnterprepaidSeries(seriesValue);
		else if (seriesType.equals("Postpaid"))
			networkPrefixesPage1.EnterpostpaidSeries(seriesValue);
		else 
			networkPrefixesPage1.EnterOtherSeries(seriesValue);
		networkPrefixesPage1.ClickOnsave();
		networkPrefixesConfirmPage.clickOnConfirmButton();
		result[0] = networkPrefixesPage1.getActualMsg(); 
		result[1] = pefixServiceMappingPage.getPrefixData(seriesType);
		cacheUpdate.updateCache(CacheController.CacheI.NetworkPrefixCache());
		return result;
	}
}
