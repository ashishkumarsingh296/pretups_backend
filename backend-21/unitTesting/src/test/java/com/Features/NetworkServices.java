package com.Features;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.pageobjects.networkadminpages.homepage.MastersSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.networkservices.NetworkServicesConfirmPage;
import com.pageobjects.networkadminpages.networkservices.NetworkServicesPage1;
import com.pageobjects.networkadminpages.networkservices.NetworkServicesPage2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class NetworkServices {

	WebDriver driver;
	NetworkAdminHomePage homePage;
	Login login;
	NetworkServicesPage1 networkServicesPage1;
	NetworkServicesPage2 networkServicesPage2;
	NetworkServicesConfirmPage netwrokServicesConfirmPage;
	MastersSubCategories mastersSubCategory;
	SelectNetworkPage selectNetworkPage;

	public NetworkServices(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		networkServicesPage1 = new NetworkServicesPage1(driver);
		networkServicesPage2 = new NetworkServicesPage2(driver);
		mastersSubCategory = new MastersSubCategories(driver);
		netwrokServicesConfirmPage = new NetworkServicesConfirmPage(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
	}

	public String modifyNetworkService(String network, String module, String serviceType, String lang1Desc,
			String lang2Desc) {
		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkServices();
		String message;
		if(!module.isEmpty() && !serviceType.isEmpty()){
		networkServicesPage1.Selectmodule(module);
		networkServicesPage1.SelectserviceType(serviceType);
		networkServicesPage1.ClickOnbtnSubmit();
		networkServicesPage2.clickOnCheckBox(network);
		networkServicesPage2.enterLanguage1Desc(network, lang1Desc);
		networkServicesPage2.enterLanguage2Desc(network, lang2Desc);
		networkServicesPage2.ClickOnbtnUpdt();
		
		if (lang1Desc != "" & lang2Desc != ""){
			netwrokServicesConfirmPage.clickOnConfirm();
		}
	message = networkServicesPage2.getActualMsg();
		}
		else{
			networkServicesPage1.ClickOnbtnSubmit();
			message = networkServicesPage1.getActualMsg();
		}
		return message;
	}

	public String modifyNetworkService_suspend(String network, String module, String serviceType, String lang1Desc,
			String lang2Desc) {
		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkServices();
		String message;
		boolean isSuspend = false;
		if(!module.isEmpty() && !serviceType.isEmpty()){
		networkServicesPage1.Selectmodule(module);
		networkServicesPage1.SelectserviceType(serviceType);
		networkServicesPage1.ClickOnbtnSubmit();
		networkServicesPage2.uncheckCheckBox(network);
		networkServicesPage2.enterLanguage1Desc(network, lang1Desc);
		networkServicesPage2.enterLanguage2Desc(network, lang2Desc);
		networkServicesPage2.ClickOnbtnUpdt();
		
		if (lang1Desc != "" & lang2Desc != ""){
			isSuspend = netwrokServicesConfirmPage.isSuspend(network);
			netwrokServicesConfirmPage.clickOnConfirm();
		}
		if(isSuspend)
		message = networkServicesPage2.getActualMsg();
		else
		 message = "status not updated.";
		}
		else{
			networkServicesPage1.ClickOnbtnSubmit();
			message = networkServicesPage1.getActualMsg();
		}
		return message;
	}
	
	public String modifyNetworkService_active(String network, String module, String serviceType, String lang1Desc,
			String lang2Desc) {
		login.UserLogin(driver, "Operator", "Super Admin", "Network Admin");
		
		selectNetworkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCategory.clickNetworkServices();
		String message;
		boolean isActive = false;
		if(!module.isEmpty() && !serviceType.isEmpty()){
		networkServicesPage1.Selectmodule(module);
		networkServicesPage1.SelectserviceType(serviceType);
		networkServicesPage1.ClickOnbtnSubmit();
		networkServicesPage2.clickOnCheckBox(network);
		networkServicesPage2.enterLanguage1Desc(network, lang1Desc);
		networkServicesPage2.enterLanguage2Desc(network, lang2Desc);
		networkServicesPage2.ClickOnbtnUpdt();
		
		if (lang1Desc != "" & lang2Desc != ""){
			isActive = netwrokServicesConfirmPage.isActive(network);
			netwrokServicesConfirmPage.clickOnConfirm();
		}
		if(isActive)
		message = networkServicesPage2.getActualMsg();
		else
		 message = "status not updated.";
		}
		else{
			networkServicesPage1.ClickOnbtnSubmit();
			message = networkServicesPage1.getActualMsg();
		}
		return message;
	}

}
