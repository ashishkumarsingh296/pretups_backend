package com.Features;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.pageobjects.networkadminpages.homepage.MastersSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.networkinterface.ModifyNetworkInterfacesDetailsPage;
import com.pageobjects.networkadminpages.networkinterface.NetworkInterfacesAddDetailsPage;
import com.pageobjects.networkadminpages.networkinterface.NetworkInterfacesConfirmPage;
import com.pageobjects.networkadminpages.networkinterface.NetworkInterfacesListPage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class NetworkInterface {
	WebDriver driver;
	NetworkAdminHomePage homePage;
	Login login;
	NetworkInterfacesListPage networkInterfacesListPage;
	NetworkInterfacesAddDetailsPage networkInterfacesAddDetailsPage;
	NetworkInterfacesConfirmPage networkInterfacesConfirmPage;
	ModifyNetworkInterfacesDetailsPage modifyNetworkInterfacesDetailsPage;
	MastersSubCategories mastersSubCategory;
	Alert alert;
	SelectNetworkPage selectNetworkPage;

	public NetworkInterface(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		networkInterfacesListPage = new NetworkInterfacesListPage(driver);
		networkInterfacesAddDetailsPage = new NetworkInterfacesAddDetailsPage(driver);
		networkInterfacesConfirmPage = new NetworkInterfacesConfirmPage(driver);
		modifyNetworkInterfacesDetailsPage = new ModifyNetworkInterfacesDetailsPage(driver);
		mastersSubCategory = new MastersSubCategories(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
	}

	public String addNetworkInterface(String interfaceCategory, String interfaceName, String queueSize,
			String queueTimeOut, String requestTimeOut, String nextQueueRetryInterval) {

		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkInterfaces();
		networkInterfacesListPage.clickOnAdd();
		networkInterfacesAddDetailsPage.selectInterfaceCategory(interfaceCategory);
		networkInterfacesAddDetailsPage.selectInterfaceName(interfaceName);
		networkInterfacesAddDetailsPage.enterQueueSize(queueSize);
		networkInterfacesAddDetailsPage.enterQueueTimeOut(queueTimeOut);
		networkInterfacesAddDetailsPage.enterRequestTimeOut(requestTimeOut);
		networkInterfacesAddDetailsPage.enterNextCheckQueueReqSec(nextQueueRetryInterval);
		networkInterfacesAddDetailsPage.clickOnSave();
		networkInterfacesConfirmPage.clickOnConfirm();
		String message = networkInterfacesListPage.getActualMsg();
		return message;

	}

	public String modifyNetworkInterface(String interfaceCategory, String interfaceName, String queueSize,
			String queueTimeOut, String requestTimeOut, String nextQueueRetryInterval, String newQueueSize,
			String newQueueTimeout, String newRequestTimeout, String newQueueRetryInterval) {

		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkInterfaces();
		networkInterfacesListPage.clickOnRadioButton(interfaceCategory, interfaceName, queueSize, queueTimeOut,
				requestTimeOut, nextQueueRetryInterval);
		networkInterfacesListPage.clickOnModify();
		modifyNetworkInterfacesDetailsPage.enterQueueSize(newQueueSize);
		modifyNetworkInterfacesDetailsPage.enterQueueTimeOut(newQueueTimeout);
		modifyNetworkInterfacesDetailsPage.enterRequestTimeOut(newRequestTimeout);
		modifyNetworkInterfacesDetailsPage.enterNextCheckQueueReqSec(newQueueRetryInterval);
		modifyNetworkInterfacesDetailsPage.clickOnSave();
		networkInterfacesConfirmPage.clickOnConfirm();
		String message = networkInterfacesListPage.getActualMsg();
		return message;
	}
	
	public String deleteNetworkInterface(String interfaceCategory, String interfaceName, String queueSize,
			String queueTimeOut, String requestTimeOut, String nextQueueRetryInterval) {

		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkInterfaces();
		networkInterfacesListPage.clickOnRadioButton(interfaceCategory, interfaceName, queueSize, queueTimeOut,
				requestTimeOut, nextQueueRetryInterval);
		networkInterfacesListPage.clickOnDelete();
		alert = driver.switchTo().alert();
		alert.accept();
		String message = networkInterfacesListPage.getActualMsg();
		return message;
	}
}
