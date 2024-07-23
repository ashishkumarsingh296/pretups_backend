package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.superadminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.networkManagement.addNetworkDetailsPage;
import com.pageobjects.superadminpages.networkManagement.modifyNetworkDetailsPage;
import com.pageobjects.superadminpages.networkManagement.networkDetailsConfirmPage;
import com.pageobjects.superadminpages.networkManagement.networkManagementPage;
import com.utils.Log;
import com.utils.RandomGeneration;

public class NetworkManagement {

	WebDriver driver = null;
	Login login;
	RandomGeneration randomNum;
	SuperAdminHomePage SuperAdminHomePage;
	MastersSubCategories MastersSubCategories;
	networkManagementPage networkManagementPage;
	addNetworkDetailsPage addNetworkDetailsPage;
	networkDetailsConfirmPage networkDetailsConfirmPage;



	Map<String, String> userAccessMap = new HashMap<String, String>();
	SelectNetworkPage networkPage;
	modifyNetworkDetailsPage modifyNetworkDetailsPage;


	public NetworkManagement(WebDriver driver){
		this.driver = driver;	
		login = new Login();
		randomNum = new RandomGeneration();
		SuperAdminHomePage = new SuperAdminHomePage(driver);
		MastersSubCategories = new MastersSubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		networkManagementPage = new networkManagementPage(driver);
		addNetworkDetailsPage = new addNetworkDetailsPage(driver);
		networkDetailsConfirmPage = new networkDetailsConfirmPage(driver);
		modifyNetworkDetailsPage = new modifyNetworkDetailsPage(driver);

	}



	public String [] addNetwork() throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEWNETWORK); //Getting User with Access to Add Network
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[3];

		String NetworkName = UniqueChecker.UC_NetworkName();
		result[0] = NetworkName;
		String NetworkCode = UniqueChecker.UC_NetworkCode();
		result[1] = NetworkCode;
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickNetworkManagement();
		networkManagementPage.clickAdd();
		addNetworkDetailsPage.enternetworkCode(NetworkCode);
		addNetworkDetailsPage.enternetworkName(NetworkName);
		addNetworkDetailsPage.enternetworkShortName(NetworkName);
		addNetworkDetailsPage.enterCompanyName("Comviva");
		addNetworkDetailsPage.enterreportHeaderName("Comviva Network");
		addNetworkDetailsPage.entererpNetworkCode(NetworkCode);
		addNetworkDetailsPage.entercountryPrefixCode("91");
		addNetworkDetailsPage.selectserviceSetID();
		addNetworkDetailsPage.clickSave();
		networkDetailsConfirmPage.clickConfirm();

		result[2] = networkManagementPage.getMsg();

		return result;


	}



	public String modifyNetwork(String NetworkCode) throws InterruptedException{

		Log.info("The NetworkCode for Modification is" +NetworkCode);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEWNETWORK); //Getting User with Access to Add Network
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickNetworkManagement();
		networkManagementPage.selectnetworkRadioButton(NetworkCode);
		networkManagementPage.clickModify();
		modifyNetworkDetailsPage.enterLanguage1Message("Network Modified");
		modifyNetworkDetailsPage.enterLanguage2Message("Msg2:Modified");
		modifyNetworkDetailsPage.clickSave();
		networkDetailsConfirmPage.clickConfirm();

		String actual = networkManagementPage.getMsg();

		return actual;
	}


	
	
}
