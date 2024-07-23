package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.Features.mapclasses.GeograpichalDomainManagementMap;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.geographicaldomain.AddGeographicalDomainConfirmPage;
import com.pageobjects.networkadminpages.geographicaldomain.AddGeographicalDomainPage;
import com.pageobjects.networkadminpages.geographicaldomain.GeograpichalDomainManagement;
import com.pageobjects.networkadminpages.geographicaldomain.ModifyGeograpichalDomainManagement;
import com.pageobjects.networkadminpages.geographicaldomain.SelectParentGeographicalDomainPage;
import com.pageobjects.networkadminpages.geographicaldomain.ViewGeographicalDomainPage;
import com.pageobjects.networkadminpages.homepage.MastersSubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class GeogaphicalDomainManagement {

	WebDriver driver;
	String[] domainData;
	String MasterSheetPath;
	RandomGeneration RandomNum;
	NetworkAdminHomePage homePage;
	Login login;
	GeograpichalDomainManagement geographicalDomainManagement;
	AddGeographicalDomainPage addGeoDomainPage;
	AddGeographicalDomainConfirmPage addGeoDomainConfirmPage;
	ModifyGeograpichalDomainManagement modifyGeograpichalDomainManagement;
	MastersSubCategories mastersSubCategory;
	ViewGeographicalDomainPage viewGeography;
	SelectParentGeographicalDomainPage selectParentDomain;
	GeograpichalDomainManagementMap geograpichalDomainManagementMap;
	String[] geographicalData;
	UniqueChecker uniqueChecker;
	SelectNetworkPage networkPage;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	SelectNetworkPage selectNetworkPage;

	public GeogaphicalDomainManagement(WebDriver driver) {

		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		geographicalDomainManagement = new GeograpichalDomainManagement(driver);
		addGeoDomainPage = new AddGeographicalDomainPage(driver);
		addGeoDomainConfirmPage = new AddGeographicalDomainConfirmPage(driver);
		modifyGeograpichalDomainManagement = new ModifyGeograpichalDomainManagement(driver);
		mastersSubCategory = new MastersSubCategories(driver);
		viewGeography = new ViewGeographicalDomainPage(driver);
		selectParentDomain = new SelectParentGeographicalDomainPage(driver);
		geograpichalDomainManagementMap = new GeograpichalDomainManagementMap();
		uniqueChecker = new UniqueChecker();
		RandomNum = new RandomGeneration();
		networkPage = new SelectNetworkPage(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
	}

	public String[] getGeographyTypes() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		String[] GeographicalDomainTypeName = new String[totalRow - 2];
		int startIndex = 3;
		for (int i = 0; i < totalRow - 2; i++) {
			GeographicalDomainTypeName[i] = ExcelUtility.getCellData(startIndex, 1);
			startIndex++;
		}
		return GeographicalDomainTypeName;
	}

	public String[] addGeographicalDomain(String parentGeography, String domainTypeName) {
		final String methodname = "addGeographicalDomain";
		Log.methodEntry(methodname, parentGeography, domainTypeName);
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GEOGRAPHICAL_DOMAIN_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		selectNetworkPage.selectNetwork();
		networkPage.selectNetwork();
		geographicalData = new String[5];
		homePage.clickMasters();
		mastersSubCategory.clickGeographicalDomainManagement();
		geographicalDomainManagement.selectDomain(domainTypeName);
		geographicalDomainManagement.clicksubmitButton();
		
		if (!domainTypeName.equals(parentGeography)) {
			selectParentDomain.enterIndexParentValue();
			selectParentDomain.clickSubmitButton();
		}
		
		viewGeography.clickAddButton();
		domainData = uniqueChecker.UC_DomainData();
		addGeoDomainPage.enterGrphDomainCode(domainData[0].trim());
		addGeoDomainPage.enterGrphDomainName(domainData[1].trim());
		addGeoDomainPage.enterGrphDomainShortName(domainData[2].trim());
		addGeoDomainPage.enterDescription("domainType Name");
		addGeoDomainPage.selectIsDefault();
		addGeoDomainPage.clickAddButton();
		addGeoDomainConfirmPage.clickConfirmButton();
		String message = addGeoDomainConfirmPage.getActualMsg();
		geographicalData[0] = domainData[0];
		geographicalData[1] = domainData[1];
		geographicalData[2] = domainData[2];
		geographicalData[3] = domainTypeName;
		geographicalData[4] = message;
		
		Log.info("Added a:" + " " + domainTypeName);
		
		Log.methodExit(methodname);
		return geographicalData;
	}
	
	public String[] validateDefaultGeographicalDomain(String Network, String domainTypeName) {
		final String methodname = "validateDefaultGeographicalDomain";
		Log.methodEntry(methodname, Network, domainTypeName);
		
		String geographicalDomainDetails[] = DBHandler.AccessHandler.getDefaultGeographicalDomain(Network, domainTypeName);
		if (geographicalDomainDetails[0] == null)
			Log.failNode("Default Geographical Domain for " + domainTypeName + " is not availble in system");
		else
			Log.info("Default Geographical Domain for " + domainTypeName + " is available in system");
		
		Log.methodExit(methodname);
		return geographicalDomainDetails;
	}

	public void writeGeographicalData(String[] domainData,int rowCount) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
//		int rowCount = ExcelUtility.getRowCount();
		ExcelUtility.setCellData(0, ExcelI.DOMAIN_CODE, rowCount + 1, domainData[0]);
		ExcelUtility.setCellData(0, ExcelI.DOMAIN_NAME, rowCount + 1, domainData[1]);
		ExcelUtility.setCellData(0, ExcelI.DOMAIN_SHORT_NAME, rowCount + 1, domainData[2]);
		ExcelUtility.setCellData(0, ExcelI.DOMAIN_TYPE_NAME, rowCount + 1, domainData[3]);
	}

	public void createGeographicalSheetHeader() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		ExcelUtility.createHeader(ExcelI.DOMAIN_CODE, ExcelI.DOMAIN_NAME, ExcelI.DOMAIN_SHORT_NAME, ExcelI.DOMAIN_TYPE_NAME);
	}
	
	//Validation Method for SIT
	
public String[] addGeographicalDomain_SIT(HashMap<String, String> mapParam) {
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GEOGRAPHICAL_DOMAIN_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		selectNetworkPage.selectNetwork();
		networkPage.selectNetwork();
		geographicalData = new String[5];
		homePage.clickMasters();
		mastersSubCategory.clickGeographicalDomainManagement();
		if(!mapParam.get("domainType").equalsIgnoreCase(""))
		geographicalDomainManagement.selectDomain(mapParam.get("domainType"));
		geographicalDomainManagement.clicksubmitButton();
		
		if (!mapParam.get("domainType").equals(mapParam.get("parentGeography"))) {
			mapParam.put("parent" , selectParentDomain.enterIndexParentValue());
			selectParentDomain.clickSubmitButton();
		}
		
		viewGeography.clickAddButton();
		domainData = uniqueChecker.UC_DomainData();
		addGeoDomainPage.enterGrphDomainCode(mapParam.get("domainCode"));
		addGeoDomainPage.enterGrphDomainName(mapParam.get("domainName"));
		addGeoDomainPage.enterGrphDomainShortName(mapParam.get("domainShortName"));
		addGeoDomainPage.enterDescription(mapParam.get("domainType"));
		addGeoDomainPage.selectIsDefault();
		addGeoDomainPage.clickAddButton();
		addGeoDomainConfirmPage.clickConfirmButton();
		String message = addGeoDomainConfirmPage.getActualMsg();
		geographicalData[0] = domainData[0];
		geographicalData[1] = domainData[1];
		geographicalData[2] = domainData[2];
		geographicalData[3] = mapParam.get("domainType");
		geographicalData[4] = message;
		
		Log.info("Added a:" + " " + mapParam.get("domainType"));

		return geographicalData;
	}

public String[] modifyGeographicalDomain_SIT(HashMap<String, String> mapParam) {
	
	//Operator User Access Implementation by Krishan.
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GEOGRAPHICAL_DOMAIN_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	//User Access module ends.
	selectNetworkPage.selectNetwork();
	networkPage.selectNetwork();
	geographicalData = new String[5];
	homePage.clickMasters();
	mastersSubCategory.clickGeographicalDomainManagement();
	geographicalDomainManagement.selectDomain(mapParam.get("domainType"));
	geographicalDomainManagement.clicksubmitButton();
	
	if (!mapParam.get("domainType").equals(mapParam.get("parentGeography"))) {
		selectParentDomain.enterIndexParentValue();
		selectParentDomain.clickSubmitButton();
	}
	viewGeography.clickOnRadioButton(mapParam.get("domainName"));
	viewGeography.clickModifyButton();
	domainData = uniqueChecker.UC_DomainData();
	boolean isDefault = modifyGeograpichalDomainManagement.checkIsDefaultStatus();
	if (!mapParam.get("status").equalsIgnoreCase(""))
    modifyGeograpichalDomainManagement.selectStatus(mapParam.get("status"));
	try {
		driver.switchTo().alert().accept();
	} catch (Exception e) {
		Log.info("Alert Message not found hence the Profile is not default");
	}
    modifyGeograpichalDomainManagement.clickModifyButton();
	addGeoDomainConfirmPage.clickConfirmModifyButton();
	String message = addGeoDomainConfirmPage.getActualMsg();
	geographicalData[0] = domainData[0];
	geographicalData[1] = domainData[1];
	geographicalData[2] = domainData[2];
	geographicalData[3] = mapParam.get("domainType");
	geographicalData[4] = message;
	
	Log.info("Added a:" + " " + mapParam.get("domainType"));

	return geographicalData;
}

public String[] deleteGeographicalDomain_SIT(HashMap<String, String> mapParam) {
	
	//Operator User Access Implementation by Krishan.
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GEOGRAPHICAL_DOMAIN_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	//User Access module ends.
	selectNetworkPage.selectNetwork();
	networkPage.selectNetwork();
	geographicalData = new String[5];
	homePage.clickMasters();
	mastersSubCategory.clickGeographicalDomainManagement();
	geographicalDomainManagement.selectDomain(mapParam.get("domainType"));
	geographicalDomainManagement.clicksubmitButton();
	String message;
	if (!mapParam.get("domainType").equals(mapParam.get("parentGeography"))) {
		selectParentDomain.enterIndexParentValue();
		selectParentDomain.clickSubmitButton();
	}
	viewGeography.clickOnRadioButton(mapParam.get("domainName"));
	viewGeography.clickDeleteButton();
	domainData = uniqueChecker.UC_DomainData();
	

		driver.switchTo().alert().accept();
/*		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);*/

    	message = viewGeography.getActualMsg();

	geographicalData[0] = domainData[0];
	geographicalData[1] = domainData[1];
	geographicalData[2] = domainData[2];
	geographicalData[3] = mapParam.get("domainType");
	geographicalData[4] = message;
	
	Log.info("Added a:" + " " + mapParam.get("domainType"));

	return geographicalData;
}
}