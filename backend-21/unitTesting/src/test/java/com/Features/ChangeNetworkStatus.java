package com.Features;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.pageobjects.networkadminpages.homepage.MastersSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.masters.Networkstatuspage1;
import com.pageobjects.networkadminpages.masters.Networkstatuspage2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class ChangeNetworkStatus {
	WebDriver driver;
	NetworkAdminHomePage homePage;
	Login login;
	Networkstatuspage1 networkstatuspage1;
	Networkstatuspage2 networkstatuspage2;
	MastersSubCategories mastersSubCategory;
	SelectNetworkPage selectNetworkPage;
	
	public ChangeNetworkStatus(WebDriver driver){
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		networkstatuspage1 = new Networkstatuspage1(driver);
		networkstatuspage2 = new Networkstatuspage2(driver);
		mastersSubCategory = new MastersSubCategories(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
	}

	public String activateNetwork(String Network) {
		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkStatus();
		networkstatuspage1.clickoncheckbox(Network);
		networkstatuspage1.ClickOnsaveStatus();
		networkstatuspage2.ClickOnconfirm();
		String Message = networkstatuspage2.getActualMsg();
		return Message;
	}

	public String deactivateNetwork(String Network) {
		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkStatus();
		networkstatuspage1.clickoncheckbox(Network);
		networkstatuspage1.ClickOnsaveStatus();
		networkstatuspage2.ClickOnconfirm();
		String Message = networkstatuspage2.getActualMsg();
		return Message;
	}
	
	public String deactivateNetwork(String Network, String Language1Message, String Language2Message) {
		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkStatus();
		networkstatuspage1.clickoncheckbox(Network);
		networkstatuspage1.Enterdatalanguage1Message(Network, Language1Message);
		networkstatuspage1.Enterdatalanguage2Message(Network, Language2Message);
		networkstatuspage1.ClickOnsaveStatus();
		networkstatuspage2.ClickOnconfirm();
		String Message = networkstatuspage2.getActualMsg();
		return Message;
	}
	
/*	public boolean checkIfOperatorCanLogin() {
		login.UserLogin(driver, "Operator", "Network Admin", "Channel Admin");
		boolean OperatorStatus = login.getLoggedInStatus(driver);
		return OperatorStatus;
	}*/
}
