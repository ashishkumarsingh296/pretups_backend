package com.Features;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.CacheController;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.ProfileManagementSubCategories;
import com.pageobjects.networkadminpages.transfercontrolprofile.TransferControlProfileDetailsPage;
import com.pageobjects.networkadminpages.transfercontrolprofile.TransferControlProfilePage;
import com.pageobjects.networkadminpages.transfercontrolprofile.TransferControlProfilePage2;
import com.pageobjects.networkadminpages.transfercontrolprofile.TransferControlProfileSelectCategoryPage;
import com.pageobjects.superadminpages.categorytransfercontrolprofile.CategoryTrfControlProfilePage1;
import com.pageobjects.superadminpages.categorytransfercontrolprofile.CategoryTrfControlProfilePage2;
import com.pageobjects.superadminpages.categorytransfercontrolprofile.CategoryTrfControlProfilePage3;
import com.pageobjects.superadminpages.categorytransfercontrolprofile.CategoryTrfControlProfilePage4;
import com.pageobjects.superadminpages.categorytransfercontrolprofile.CategoryTrfControlProfilePage5;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class TransferControlProfile {

	public String TCPName;
	public String profile_ID;
	WebDriver driver;
	SuperAdminHomePage sAHomePage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randmGeneration;
	CategoryTrfControlProfilePage1 categoryTrfControlProfilePage1;
	CategoryTrfControlProfilePage2 categoryTrfControlProfilePage2;
	CategoryTrfControlProfilePage3 categoryTrfControlProfilePage3;
	CategoryTrfControlProfilePage4 categoryTrfControlProfilePage4;
	CategoryTrfControlProfilePage5 categoryTrfControlProfilePage5;
	Map_TCPValues Map_TCPValues = new Map_TCPValues();
	NetworkAdminHomePage nAHomePage;
	ProfileManagementSubCategories  profileManagementSubCat;
	TransferControlProfileSelectCategoryPage transferControlProfileSelectCategory;
	TransferControlProfileDetailsPage transferControlProfileDetailsPage;
	TransferControlProfilePage transferControlProfilePage;
	TransferControlProfilePage2 transferControlProfilePage2;
	CacheUpdate CacheUpdate;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
	//Map<String, String> dataMap = new HashMap<String, String>();


	public TransferControlProfile(WebDriver driver) {
		this.driver = driver;
		sAHomePage = new SuperAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		categoryTrfControlProfilePage1 = new CategoryTrfControlProfilePage1(driver);
		categoryTrfControlProfilePage2 = new CategoryTrfControlProfilePage2(driver);
		categoryTrfControlProfilePage3 = new CategoryTrfControlProfilePage3(driver);
		categoryTrfControlProfilePage4 = new CategoryTrfControlProfilePage4(driver);
		categoryTrfControlProfilePage5 = new CategoryTrfControlProfilePage5(driver);

		//Channel Level TCP Pages
		nAHomePage = new NetworkAdminHomePage(driver);
		profileManagementSubCat = new ProfileManagementSubCategories(driver);
		transferControlProfileSelectCategory = new TransferControlProfileSelectCategoryPage(driver);
		transferControlProfileDetailsPage = new TransferControlProfileDetailsPage(driver);
		transferControlProfilePage = new TransferControlProfilePage(driver);
		transferControlProfilePage2 = new TransferControlProfilePage2(driver);
		randmGeneration = new RandomGeneration();
		CacheUpdate = new CacheUpdate(driver);
	}


	public Map<String, String> createCategoryLevelTransferControlProfile(int rowNum, String domainName, String categoryName) {
		final String methodname = "createCategoryLevelTransferControlProfile";
		Log.methodEntry(methodname, rowNum, domainName, categoryName);

		//String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> dataMap1 = new HashMap<String, String>();
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CATEGORY_TCP_ROLECODE); //Getting User with Access to Add Category Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		sAHomePage.clickProfileManagement();

		categoryTrfControlProfilePage1.selectDomainName(domainName);
		categoryTrfControlProfilePage1.selectCategoryName(categoryName);
		categoryTrfControlProfilePage1.clickSubmitButton();
		//String TCPName = ("AUT"+randmGeneration.randomNumeric(5));
		String TCPName = UniqueChecker.UC_TCPName();

		//String TCPName = (rowNum+"AUT"+domainName.substring(0, 3)+categoryName.substring(0, 3)).toUpperCase();

		boolean result = categoryTrfControlProfilePage2.isModifyButtonPresent();
		if(result==true){
			Log.info("Category level TCP for Domain: "+domainName+" and Category: "+categoryName+ "already exists.");
			categoryTrfControlProfilePage2.clickModifyButton();

			int productTableSize= driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.minBalance')]]")).size();

			for(int j=0; j<productTableSize;j++){
				categoryTrfControlProfilePage5.enterMinimumResidualBalance(_masterVO.getProperty("MinimumBalance"), j);
				categoryTrfControlProfilePage5.enterMaximumResidualBalance(_masterVO.getProperty("MaximumBalance"), j);
				categoryTrfControlProfilePage5.enterPerC2STransactionAmountMinimum(_masterVO.getProperty("MinimumBalance"), j);
				categoryTrfControlProfilePage5.enterPerC2STransactionAmountMaximum(_masterVO.getProperty("MaximumBalance"), j);
				categoryTrfControlProfilePage5.enterAlertingBalance(_masterVO.getProperty("AlertingCount"), j);
				categoryTrfControlProfilePage5.enterAllowedMaxPercentage(_masterVO.getProperty("AllowedMaxPercentage"), j);
			}

			// Entering Transfer Control Profile----Daily.
			categoryTrfControlProfilePage5.enterDailyTransferInCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterDailyTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterDailyTransferInValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterDailyTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInCount(_masterVO.getProperty("Count"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInValue(_masterVO.getProperty("MaximumBalance"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			}
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));

			// Entering Transfer Control Profile----Weekly.
			categoryTrfControlProfilePage5.enterWeeklyTransferInCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInCount(_masterVO.getProperty("Count"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInValue(_masterVO.getProperty("MaximumBalance"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			}
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));

			// Entering Transfer Control Profile----Monthly.
			categoryTrfControlProfilePage5.enterMonthlyTransferInCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInCount(_masterVO.getProperty("Count"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInValue(_masterVO.getProperty("MaximumBalance"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			}
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));

			categoryTrfControlProfilePage5.clickSubmitButton();	
			categoryTrfControlProfilePage4.clickConfirmButton();

			String actual = categoryTrfControlProfilePage2.getMessage();
			String expected = "Successfully updated transfer control profile";
			Assert.assertEquals(expected, actual);
		}
		else{
			Log.info("Creating Category level TCP for Domain: "+domainName+" and Category: "+categoryName);

			categoryTrfControlProfilePage3.enterProfileName(TCPName);
			categoryTrfControlProfilePage3.enterShortName(TCPName);
			categoryTrfControlProfilePage3.enterDescription("Created through automation.");

			// Entering balance preferences.
			int productTableSize= driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.minBalance')]]")).size();

			for(int j=0; j<productTableSize;j++){
				categoryTrfControlProfilePage3.enterMinimumResidualBalance(_masterVO.getProperty("MinimumBalance"), j);
				categoryTrfControlProfilePage3.enterMaximumResidualBalance(_masterVO.getProperty("MaximumBalance"), j);
				categoryTrfControlProfilePage3.enterPerC2STransactionAmountMinimum(_masterVO.getProperty("MinimumBalance"), j);
				categoryTrfControlProfilePage3.enterPerC2STransactionAmountMaximum(_masterVO.getProperty("MaximumBalance"), j);
				categoryTrfControlProfilePage3.enterAlertingBalance(_masterVO.getProperty("AlertingCount"), j);
				categoryTrfControlProfilePage3.enterAllowedMaxPercentage(_masterVO.getProperty("AllowedMaxPercentage"), j);
			}

			// Entering Transfer Control Profile----Daily.
			categoryTrfControlProfilePage3.enterDailyTransferInCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterDailyTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterDailyTransferInValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterDailyTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterDailyChannelTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterDailyChannelTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterDailyChannelTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterDailyChannelTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage3.enterDailySubscriberTransferInCount(_masterVO.getProperty("Count"));
				categoryTrfControlProfilePage3.enterDailySubscriberTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
				categoryTrfControlProfilePage3.enterDailySubscriberTransferInValue(_masterVO.getProperty("MaximumBalance"));
				categoryTrfControlProfilePage3.enterDailySubscriberTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			}
			categoryTrfControlProfilePage3.enterDailySubscriberTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterDailySubscriberTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterDailySubscriberTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterDailySubscriberTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));

			// Entering Transfer Control Profile----Weekly.
			categoryTrfControlProfilePage3.enterWeeklyTransferInCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterWeeklyTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterWeeklyTransferInValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterWeeklyTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterWeeklyChannelTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterWeeklyChannelTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterWeeklyChannelTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterWeeklyChannelTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage3.enterWeeklySubscriberTransferInCount(_masterVO.getProperty("Count"));
				categoryTrfControlProfilePage3.enterWeeklySubscriberTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
				categoryTrfControlProfilePage3.enterWeeklySubscriberTransferInValue(_masterVO.getProperty("MaximumBalance"));
				categoryTrfControlProfilePage3.enterWeeklySubscriberTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			}
			categoryTrfControlProfilePage3.enterWeeklySubscriberTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterWeeklySubscriberTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterWeeklySubscriberTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterWeeklySubscriberTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));

			// Entering Transfer Control Profile----Monthly.
			categoryTrfControlProfilePage3.enterMonthlyTransferInCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterMonthlyTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterMonthlyTransferInValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterMonthlyTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterMonthlyChannelTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterMonthlyChannelTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterMonthlyChannelTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterMonthlyChannelTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage3.enterMonthlySubscriberTransferInCount(_masterVO.getProperty("Count"));
				categoryTrfControlProfilePage3.enterMonthlySubscriberTransferInAlertingCount(_masterVO.getProperty("AlertingCount"));
				categoryTrfControlProfilePage3.enterMonthlySubscriberTransferInValue(_masterVO.getProperty("MaximumBalance"));
				categoryTrfControlProfilePage3.enterMonthlySubscriberTransferInAlertingValue(_masterVO.getProperty("AlertingCount"));
			}
			categoryTrfControlProfilePage3.enterMonthlySubscriberTransferOutCount(_masterVO.getProperty("Count"));
			categoryTrfControlProfilePage3.enterMonthlySubscriberTransferOutAlertingCount(_masterVO.getProperty("AlertingCount"));
			categoryTrfControlProfilePage3.enterMonthlySubscriberTransferOutValue(_masterVO.getProperty("MaximumBalance"));
			categoryTrfControlProfilePage3.enterMonthlySubscriberTransferOutAlertingValue(_masterVO.getProperty("AlertingCount"));

			categoryTrfControlProfilePage3.clickSubmitButton();	
			categoryTrfControlProfilePage4.clickConfirmButton();

			String actual = categoryTrfControlProfilePage2.getMessage();
			dataMap1.put("ACTUALMESSAGE",actual);

		}

		String CatTCP =categoryTrfControlProfilePage2.getCatTCPName();
		System.out.println("CategoryTransferProfile Name is :" +CatTCP);
		//HashMap<String, String> map = JDBC.dataBaseConnection("Select * from TRANSFER_PROFILE where PROFILE_NAME="+"'"+CatTCP+"'");
		//String profile_ID = map.get("PROFILE_ID");
		String profile_ID = DBHandler.AccessHandler.fetchTCPID(CatTCP);
		/*ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		ExcelUtility.setCellData(0, ExcelI.SA_TCP_NAME, rowNum, CatTCP);
		ExcelUtility.setCellData(0, ExcelI.SA_TCP_PROFILE_ID, rowNum, profile_ID);*/

		dataMap1.put("CatTCP", CatTCP);
		dataMap1.put("profile_ID",profile_ID);
		nAHomePage.clickLogout();
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());
		Log.methodExit(methodname);
		return dataMap1;
	}


	public Map<String, String> createChannelLevelTransferControlProfile(int rowNum,String domainName, String categoryName) {
		final String methodname = "createChannelLevelTransferControlProfile";
		Log.methodEntry(methodname, rowNum, domainName, categoryName);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> dataMap = new HashMap<String, String>();

		//Operator User Access Implementation by Krishan.
		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		//String TCPName = ("AUT"+randmGeneration.randomNumeric(5));

		String TCPName = UniqueChecker.UC_TCPName();

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		boolean saveExist=transferControlProfilePage.isSaveExist();

		if(saveExist==false){
			transferControlProfileDetailsPage.clickAddButton();
		}
		transferControlProfilePage.enterProfileName(TCPName);
		transferControlProfilePage.enterShortName(TCPName);
		transferControlProfilePage.enterDescription("Automated Tranfer Control Profile Creation.");
		transferControlProfilePage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);

		transferControlProfilePage.clickSaveButton();
		transferControlProfilePage2.clickConfirmButton();

		String profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);

		//boolean result1 = transferControlProfileDetailsPage.verifyMessage();

		String actual = transferControlProfileDetailsPage.getMessage();
		dataMap.put("ACTUALMESSAGE",actual);

		dataMap.put("TCP_Name", TCPName);
		dataMap.put("profile_ID",profile_ID);

		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		Log.methodExit(methodname);
		return dataMap;
	}

	/*public void writeTCPtoExcel(int rowNum, String TCPName){

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	ExcelUtility.setCellData(0, ExcelI.NA_TCP_NAME,rowNum, TCPName);
	ExcelUtility.setCellData(0, ExcelI.NA_TCP_PROFILE_ID, rowNum, dataMap.get("profile_ID"));

	}*/


	public String channelLevelTransferControlProfileSuspend(int rowNum,String domainName, String categoryName, String TCPName, String profile_ID) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);
		System.out.println("The selected Profile ID is"+ profile_ID);

		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+profile_ID+"']"));

		radioButton.click();

		transferControlProfileDetailsPage.clickModifyButton();

		transferControlProfilePage.selectStatus(PretupsI.STATUS_SUSPENDED_LOOKUPS);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();

		String actual = transferControlProfileDetailsPage.getMessage();
		System.out.println("The message displayed is :" +actual);
		//dataMap.put("ACTUAL_MESSAGE",actual);
		//String expected = "Successfully updated transfer control profile";
		//Assert.assertEquals(expected, actual);

		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		return actual;


	}


	public String channelLevelTransferControlProfileActive(int rowNum,String domainName, String categoryName, String TCPName , String profile_ID) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);
		System.out.println("The selected Profile ID is"+ profile_ID);

		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+profile_ID+"']"));

		radioButton.click();

		transferControlProfileDetailsPage.clickModifyButton();

		transferControlProfilePage.selectStatus(PretupsI.STATUS_ACTIVE);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();

		String actual = transferControlProfileDetailsPage.getMessage();
		System.out.println("The message displayed is :" +actual);
		//dataMap.put("ACTUAL_MESSAGE",actual);
		//String expected = "Successfully updated transfer control profile";
		//Assert.assertEquals(expected, actual);

		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		return actual;


	}


	public String modifyChannelLevelTransferProfile(int rowNum,String domainName, String categoryName, String TCPName , String profile_ID) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);
		System.out.println("The selected Profile ID is"+ profile_ID);

		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+profile_ID+"']"));

		radioButton.click();

		transferControlProfileDetailsPage.clickModifyButton();
		transferControlProfilePage.enterETopUpBalancePreferenceMinimumResidualBalance("10");		
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	

		String actual = transferControlProfileDetailsPage.getMessage();
		System.out.println("The message is:"+actual);
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		return actual;	


	}




	public String modifyChannelLevelTransferProfileDefault(int rowNum,String domainName, String categoryName, String TCPName , String profile_ID) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);
		System.out.println("The selected Profile ID is"+ profile_ID);

		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+profile_ID+"']"));

		radioButton.click();

		transferControlProfileDetailsPage.clickModifyButton();
		transferControlProfilePage.clickDefaultPofile();		
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	

		String actual = transferControlProfileDetailsPage.getMessage();
		System.out.println("The message is:"+actual);
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		return actual;	


	}



	public String deleteChannelLevelTransferProfile(int rowNum,String domainName, String categoryName, String TCPName , String profile_ID) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);
		System.out.println("The selected Profile ID is"+ profile_ID);

		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+profile_ID+"']"));

		radioButton.click();

		transferControlProfileDetailsPage.clickDeleteButton();
		driver.switchTo().alert().accept();

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);

		String actual = transferControlProfileDetailsPage.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		return actual;	


	}


	public String defaultChannelLevelTransferProfile(int rowNum,String domainName, String categoryName, String TCPName , String profile_ID) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);
		System.out.println("The selected Profile ID is"+ profile_ID);

		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+profile_ID+"']"));

		radioButton.click();

		transferControlProfileDetailsPage.clickModifyButton();
		transferControlProfilePage.clickDefaultPofile();		
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	

		String actual = transferControlProfileDetailsPage.getMessage();

		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		return actual;	


	}


	public String deleteDefaultChannelLevelTransferProfile(int rowNum,String domainName, String categoryName, String TCPName , String profile_ID) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		profile_ID = DBHandler.AccessHandler.fetchTCPID(TCPName);
		System.out.println("The selected Profile ID is"+ profile_ID);

		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+profile_ID+"']"));

		radioButton.click();

		transferControlProfileDetailsPage.clickDeleteButton();
		driver.switchTo().alert().accept();

		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);

		String actual = transferControlProfileDetailsPage.getDeleteErrorMessage();

		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());

		return actual;	


	}


	/*
	 * SIT TestCases Starts	
	 */
	public Map<String, String> CategoryLevelTCP_SITValidations(Map<String,String> datamap, int rowNum, String domainName, String categoryName) {

		Map<String, String> TCP_CatLevel_Map = datamap;


		//Map<String, String> dataMap1 = new HashMap<String, String>();
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CATEGORY_TCP_ROLECODE); //Getting User with Access to Add Category Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		sAHomePage.clickProfileManagement();

		categoryTrfControlProfilePage1.selectDomainName(domainName);
		categoryTrfControlProfilePage1.selectCategoryName(categoryName);
		categoryTrfControlProfilePage1.clickSubmitButton();

		String TCPName = UniqueChecker.UC_TCPName();



		boolean result = categoryTrfControlProfilePage2.isModifyButtonPresent();
		if(result==true){
			Log.info("Category level TCP for Domain: "+domainName+" and Category: "+categoryName+ "already exists.");
			categoryTrfControlProfilePage2.clickModifyButton();

			int productTableSize= driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.minBalance')]]")).size();
			System.out.println("The size is:" +productTableSize);

			String prodCode = driver.findElement(By.xpath("//form/table/tbody/tr[5]/td/table/tbody/tr[4]/td[1]")).getText();
			TCP_CatLevel_Map.put("prodCode", prodCode);

			for(int j=0; j<productTableSize;j++){

				if(j==0){

					categoryTrfControlProfilePage5.enterMinimumResidualBalance(TCP_CatLevel_Map.get("MinResidualBalance1"), j);
					categoryTrfControlProfilePage5.enterMaximumResidualBalance(TCP_CatLevel_Map.get("MaximumResidualBalance1"), j);
					categoryTrfControlProfilePage5.enterPerC2STransactionAmountMinimum(TCP_CatLevel_Map.get("MinimumBalance1"), j);
					categoryTrfControlProfilePage5.enterPerC2STransactionAmountMaximum(TCP_CatLevel_Map.get("MaximumBalance1"), j);
					categoryTrfControlProfilePage5.enterAlertingBalance( TCP_CatLevel_Map.get("AlertingBalance1"), j);
					categoryTrfControlProfilePage5.enterAllowedMaxPercentage( TCP_CatLevel_Map.get("AllowedMaxPercentage1"), j);

				}else{


					categoryTrfControlProfilePage5.enterMinimumResidualBalance(TCP_CatLevel_Map.get("MinResidualBalance"), j);
					categoryTrfControlProfilePage5.enterMaximumResidualBalance(TCP_CatLevel_Map.get("MaximumResidualBalance"), j);
					categoryTrfControlProfilePage5.enterPerC2STransactionAmountMinimum(TCP_CatLevel_Map.get("MinimumBalance"), j);
					categoryTrfControlProfilePage5.enterPerC2STransactionAmountMaximum(TCP_CatLevel_Map.get("MaximumBalance"), j);
					categoryTrfControlProfilePage5.enterAlertingBalance( TCP_CatLevel_Map.get("AlertingBalance"), j);
					categoryTrfControlProfilePage5.enterAllowedMaxPercentage( TCP_CatLevel_Map.get("AllowedMaxPercentage"), j);
				}
			}

			// Entering Transfer Control Profile----Daily.
			categoryTrfControlProfilePage5.enterDailyTransferInCount(TCP_CatLevel_Map.get("DailyInCount"));
			categoryTrfControlProfilePage5.enterDailyTransferInAlertingCount(TCP_CatLevel_Map.get("DailyInAlertingCount"));
			categoryTrfControlProfilePage5.enterDailyTransferInValue(TCP_CatLevel_Map.get("DailyInTransferValue"));
			categoryTrfControlProfilePage5.enterDailyTransferInAlertingValue(TCP_CatLevel_Map.get("DailyInAlertingValue"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutCount(TCP_CatLevel_Map.get("DailyOutCount"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("DailyOutAlertingCount"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutValue(TCP_CatLevel_Map.get("DailyOutTransferValue"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("DailyOutAlertingValue"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInCount(TCP_CatLevel_Map.get("DailySubscriberInCount"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("DailySubscriberInAlertingCount"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInValue(TCP_CatLevel_Map.get("DailySubscriberTransferInValue"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingValue(TCP_CatLevel_Map.get("DailySubscriberTransferInAlertingValue"));
			}
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutCount(TCP_CatLevel_Map.get("DailySubscriberTransferOutCount"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingCount"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutValue(TCP_CatLevel_Map.get("DailySubscriberTransferOutValue"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingValue"));

			// Entering Transfer Control Profile----Weekly.
			categoryTrfControlProfilePage5.enterWeeklyTransferInCount(TCP_CatLevel_Map.get("WeeklyInCount"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingCount(TCP_CatLevel_Map.get("WeeklyInAlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInValue(TCP_CatLevel_Map.get("WeeklyInTransferValue"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingValue(TCP_CatLevel_Map.get("WeeklyInAlertingValue"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutCount( TCP_CatLevel_Map.get("WeeklyOutCount"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("WeeklyOutAlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutValue(TCP_CatLevel_Map.get("WeeklyOutTransferValue"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("WeeklyOutAlertingValue"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInCount(TCP_CatLevel_Map.get("WeeklySubscriberInCount"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingValue( TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue"));
			}
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutCount(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue"));

			// Entering Transfer Control Profile----Monthly.
			categoryTrfControlProfilePage5.enterMonthlyTransferInCount(TCP_CatLevel_Map.get("MonthlyInCount"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingCount(TCP_CatLevel_Map.get("MonthlyInAlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInValue(TCP_CatLevel_Map.get("MonthlyInTransferValue"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingValue(TCP_CatLevel_Map.get("MonthlyInAlertingValue"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutCount(TCP_CatLevel_Map.get("MonthlyOutCount"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("MonthlyOutAlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutValue(TCP_CatLevel_Map.get("MonthlyOutTransferValue"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("MonthlyOutAlertingValue"));

			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInCount(TCP_CatLevel_Map.get("MonthlySubscriberInCount"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("MonthlySubscriberInAlertingCount"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferInValue"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferInAlertingValue"));
			}
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutCount(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutCount"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutValue"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingValue"));

			categoryTrfControlProfilePage5.clickSubmitButton();	


			String actual = categoryTrfControlProfilePage2.getErrorMessage();
			TCP_CatLevel_Map.put("ACTUALMESSAGE",actual);

		}

		else{
			Log.info("Creating Category level TCP for Domain: "+domainName+" and Category: "+categoryName);

			categoryTrfControlProfilePage3.enterProfileName(TCPName);
			categoryTrfControlProfilePage3.enterShortName(TCPName);
			categoryTrfControlProfilePage3.enterDescription("Created through automation.");

			int productTableSize= driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.minBalance')]]")).size();

			for(int j=0; j<productTableSize;j++){


				categoryTrfControlProfilePage5.enterMinimumResidualBalance(TCP_CatLevel_Map.get("MinResidualBalance"), j);
				categoryTrfControlProfilePage5.enterMaximumResidualBalance(TCP_CatLevel_Map.get("MaximumResidualBalance"), j);
				categoryTrfControlProfilePage5.enterPerC2STransactionAmountMinimum(TCP_CatLevel_Map.get("MinimumBalance"), j);
				categoryTrfControlProfilePage5.enterPerC2STransactionAmountMaximum(TCP_CatLevel_Map.get("MaximumBalance"), j);
				categoryTrfControlProfilePage5.enterAlertingBalance( TCP_CatLevel_Map.get("AlertingBalance"), j);
				categoryTrfControlProfilePage5.enterAllowedMaxPercentage( TCP_CatLevel_Map.get("AllowedMaxPercentage"), j);

			}

			// Entering Transfer Control Profile----Daily.
			categoryTrfControlProfilePage5.enterDailyTransferInCount(TCP_CatLevel_Map.get("DailyInCount"));
			categoryTrfControlProfilePage5.enterDailyTransferInAlertingCount(TCP_CatLevel_Map.get("DailyInAlertingCount"));
			categoryTrfControlProfilePage5.enterDailyTransferInValue(TCP_CatLevel_Map.get("DailyInTransferValue"));
			categoryTrfControlProfilePage5.enterDailyTransferInAlertingValue(TCP_CatLevel_Map.get("DailyInAlertingValue"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutCount(TCP_CatLevel_Map.get("DailyOutCount"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("DailyOutAlertingCount"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutValue(TCP_CatLevel_Map.get("DailyOutTransferValue"));
			categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("DailyOutAlertingValue"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInCount(TCP_CatLevel_Map.get("DailySubscriberInCount"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("DailySubscriberInAlertingCount"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInValue(TCP_CatLevel_Map.get("DailySubscriberTransferInValue"));
				categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingValue(TCP_CatLevel_Map.get("DailySubscriberTransferInAlertingValue"));
			}
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutCount(TCP_CatLevel_Map.get("DailySubscriberTransferOutCount"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingCount"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutValue(TCP_CatLevel_Map.get("DailySubscriberTransferOutValue"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingValue"));

			// Entering Transfer Control Profile----Weekly.
			categoryTrfControlProfilePage5.enterWeeklyTransferInCount(TCP_CatLevel_Map.get("WeeklyInCount"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingCount(TCP_CatLevel_Map.get("WeeklyInAlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInValue(TCP_CatLevel_Map.get("WeeklyInTransferValue"));
			categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingValue(TCP_CatLevel_Map.get("WeeklyInAlertingValue"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutCount( TCP_CatLevel_Map.get("WeeklyOutCount"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("WeeklyOutAlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutValue(TCP_CatLevel_Map.get("WeeklyOutTransferValue"));
			categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("WeeklyOutAlertingValue"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInCount(TCP_CatLevel_Map.get("WeeklySubscriberInCount"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue"));
				categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingValue( TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue"));
			}
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutCount(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue"));

			// Entering Transfer Control Profile----Monthly.
			categoryTrfControlProfilePage5.enterMonthlyTransferInCount(TCP_CatLevel_Map.get("MonthlyInCount"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingCount(TCP_CatLevel_Map.get("MonthlyInAlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInValue(TCP_CatLevel_Map.get("MonthlyInTransferValue"));
			categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingValue(TCP_CatLevel_Map.get("MonthlyInAlertingValue"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutCount(TCP_CatLevel_Map.get("MonthlyOutCount"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("MonthlyOutAlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutValue(TCP_CatLevel_Map.get("MonthlyOutTransferValue"));
			categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("MonthlyOutAlertingValue"));
			if (TCP_ClientVer == 1) {
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInCount(TCP_CatLevel_Map.get("MonthlySubscriberInCount"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("MonthlySubscriberInAlertingCount"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferInValue"));
				categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferInAlertingValue"));
			}
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutCount(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutCount"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutValue"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingValue"));

			categoryTrfControlProfilePage5.clickSubmitButton();	


			String actual = categoryTrfControlProfilePage2.getErrorMessage();
			TCP_CatLevel_Map.put("ACTUALMESSAGE",actual);

		}

		return TCP_CatLevel_Map;

	}


	/**
	 * modify maximum balance in TCP
	 * added by lokesh.kontey
	 * modification done with hard coded amount in code
	 */
	public String modifyTCPmaximumBalance(String domainName, String categoryName, String channeltcpID , String maxbalance,String alertbalance,String productCode) {
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();
		transferControlProfileDetailsPage.selectTCP(channeltcpID);
		transferControlProfileDetailsPage.clickModifyButton();
		categoryTrfControlProfilePage5.enterMaximumResidualBalance(maxbalance,alertbalance,productCode);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());
		return actual;
	}

	/**
	 * modify minimum residual balance in TCP
	 * added by lokesh.kontey
	 * modification done with hard coded amount in code
	 */
	public String modifyTCPminimumBalance(String domainName, String categoryName, String channeltcpID , String minbalance,String allowedmaxpercentage,String productCode) {
		String masterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CATEGORY_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		sAHomePage.clickProfileManagement();

		categoryTrfControlProfilePage1.selectDomainName(domainName);
		categoryTrfControlProfilePage1.selectCategoryName(categoryName);
		categoryTrfControlProfilePage1.clickSubmitButton();
		categoryTrfControlProfilePage2.clickModifyButton();
		categoryTrfControlProfilePage5.enterMinimumResidualBalance(minbalance,allowedmaxpercentage,productCode);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	

		new SuperAdminHomePage(driver).clickLogout();

		Map<String, String> userAccessMap1 = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap1.get("LOGIN_ID"), userAccessMap1.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();
		transferControlProfileDetailsPage.selectTCP(channeltcpID);
		transferControlProfileDetailsPage.clickModifyButton();
		categoryTrfControlProfilePage5.enterMinimumResidualBalance(minbalance,allowedmaxpercentage,productCode);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());
		return actual;
	}

	/**
	 * modify minimum amount per c2s tranasaction in TCP
	 * added by lokesh.kontey
	 * modification done with hard coded amount in code
	 */
	public String modifyTCPPerC2SminimumAmt(String domainName, String categoryName, String channeltcpID , String perC2SminimumAmt,String allowedmaxpercentage,String productCode) {
		String masterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CATEGORY_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		sAHomePage.clickProfileManagement();

		categoryTrfControlProfilePage1.selectDomainName(domainName);
		categoryTrfControlProfilePage1.selectCategoryName(categoryName);
		categoryTrfControlProfilePage1.clickSubmitButton();
		categoryTrfControlProfilePage2.clickModifyButton();
		categoryTrfControlProfilePage5.enterPerC2SMinimumAmt(perC2SminimumAmt, allowedmaxpercentage, productCode);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	

		new SuperAdminHomePage(driver).clickLogout();

		Map<String, String> userAccessMap1 = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap1.get("LOGIN_ID"), userAccessMap1.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();
		transferControlProfileDetailsPage.selectTCP(channeltcpID);
		transferControlProfileDetailsPage.clickModifyButton();
		categoryTrfControlProfilePage5.enterPerC2SMinimumAmt(perC2SminimumAmt, allowedmaxpercentage, productCode);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());
		return actual;
	}

	/**
	 * modify maximum amount per c2s transaction in TCP
	 * added by lokesh.kontey
	 * modification done with hard coded amount in code
	 */
	public String modifyTCPPerC2SmaximumAmt(String domainName, String categoryName, String channeltcpID , String perC2SmaxAmt,String alertbalance,String productCode) {
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();
		transferControlProfileDetailsPage.selectTCP(channeltcpID);
		transferControlProfileDetailsPage.clickModifyButton();
		categoryTrfControlProfilePage5.enterPerC2SMaximumAmt(perC2SmaxAmt,alertbalance,productCode);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());
		return actual;
	}

	/**
	 * modify any field in TCP
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public String modifyAnyTCPValue(String[][] func, Map<String,String> dataMapTCP, String tcpType_Channnel_Or_Category) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		int datacount = func.length; 
		boolean category = false;

		Method[] funcToModify = new Method[datacount];
		CategoryTrfControlProfilePage5 catTrf5 = new CategoryTrfControlProfilePage5(driver);
		Class[] parameterTypes = new Class[1];
		parameterTypes[0] = String.class;
		for(int i=0;i<datacount;i++){
			funcToModify[i]= CategoryTrfControlProfilePage5.class.getMethod(func[i][0], parameterTypes);}

		Object[] parameters = new Object[1];
		Map<String, String> userAccessMap;
		if(tcpType_Channnel_Or_Category.equalsIgnoreCase("category")){
			userAccessMap = UserAccess.getUserWithAccess(RolesI.CATEGORY_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			category=true;}
		else if(tcpType_Channnel_Or_Category.equalsIgnoreCase("channel")){
			userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			category=false;
		}else{
			BaseTest.currentNode.log(Status.FAIL, "TCP type passed in method as '"+tcpType_Channnel_Or_Category+"', it should be either 'category' or 'channel' only.");
		}
		networkPage.selectNetwork();

		if(category){
			sAHomePage.clickProfileManagement();
			categoryTrfControlProfilePage1.selectDomainName(dataMapTCP.get("domainName"));
			categoryTrfControlProfilePage1.selectCategoryName(dataMapTCP.get("categoryName"));
			categoryTrfControlProfilePage1.clickSubmitButton();
			categoryTrfControlProfilePage2.clickModifyButton();}	
		else{		
			nAHomePage.clickProfileManagement();
			profileManagementSubCat.clickTransferControlProfile();
			transferControlProfileSelectCategory.selectDomainName(dataMapTCP.get("domainName"));
			transferControlProfileSelectCategory.selectCategoryName(dataMapTCP.get("categoryName"));
			transferControlProfileSelectCategory.clickSubmitButton();
			transferControlProfileDetailsPage.selectTCP(dataMapTCP.get("tcpID"));
			transferControlProfileDetailsPage.clickModifyButton();}

		for(int i=0;i<datacount;i++){
			parameters[0] = func[i][1];
			funcToModify[i].invoke(catTrf5, parameters);
		}
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();	
		String actualmsg = new AddChannelUserDetailsPage(driver).getActualMessage();
		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());
		return actualmsg;  
	}



	//Method to modify values in TCP for product table
	public String modifyProductValuesInTCP(String domainName, String categoryName, String channeltcpID , String[] parameterToModify,String[] value,String productCode, boolean bothLevel) {
		String masterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CATEGORY_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		sAHomePage.clickProfileManagement();

		categoryTrfControlProfilePage1.selectDomainName(domainName);
		categoryTrfControlProfilePage1.selectCategoryName(categoryName);
		categoryTrfControlProfilePage1.clickSubmitButton();
		categoryTrfControlProfilePage2.clickModifyButton();


		categoryTrfControlProfilePage5.enterProductValues(parameterToModify, productCode,value);
		transferControlProfilePage.clickSubmit();
		transferControlProfilePage2.clickConfirmButton();

		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		new SuperAdminHomePage(driver).clickLogout();

		if(bothLevel){
			Map<String, String> userAccessMap1 = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
			login.LoginAsUser(driver, userAccessMap1.get("LOGIN_ID"), userAccessMap1.get("PASSWORD"));
			networkPage.selectNetwork();
			nAHomePage.clickProfileManagement();
			ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			profileManagementSubCat.clickTransferControlProfile();
			transferControlProfileSelectCategory.selectDomainName(domainName);
			transferControlProfileSelectCategory.selectCategoryName(categoryName);
			transferControlProfileSelectCategory.clickSubmitButton();
			transferControlProfileDetailsPage.selectTCP(channeltcpID);
			transferControlProfileDetailsPage.clickModifyButton();
			categoryTrfControlProfilePage5.enterProductValues(parameterToModify, productCode,value);
			transferControlProfilePage.clickSubmit();
			transferControlProfilePage2.clickConfirmButton();
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();}

		CacheUpdate.updateCache(CacheController.CacheI.TRANSFER_PROFILE(), CacheController.CacheI.TRANSFER_PROFILE_PRODUCT());
		return actual;
	}










	//////////////////////////////////



	/*
	 * SIT TestCases Starts	
	 */
	public Map<String, String> CategoryLevelTCP_SITTCPNameExists(Map<String,String> datamap, int rowNum, String domainName, String categoryName,String TCPName) {

		Map<String, String> TCP_CatLevel_Map = datamap;


		//Map<String, String> dataMap1 = new HashMap<String, String>();
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Category Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		categoryTrfControlProfilePage1.selectDomainName(domainName);
		categoryTrfControlProfilePage1.selectCategoryName(categoryName);
		categoryTrfControlProfilePage1.clickSubmitButton();

		//String TCPName = UniqueChecker.UC_TCPName();

		Log.info("Creating Category level TCP for Domain: "+domainName+" and Category: "+categoryName);

		categoryTrfControlProfilePage3.enterProfileName(TCPName);
		categoryTrfControlProfilePage3.enterShortName(TCPName);
		categoryTrfControlProfilePage3.enterDescription("Created through automation.");

		int productTableSize= driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.minBalance')]]")).size();

		for(int j=0; j<productTableSize;j++){


			categoryTrfControlProfilePage5.enterMinimumResidualBalance(TCP_CatLevel_Map.get("MinResidualBalance"), j);
			categoryTrfControlProfilePage5.enterMaximumResidualBalance(TCP_CatLevel_Map.get("MaximumResidualBalance"), j);
			categoryTrfControlProfilePage5.enterPerC2STransactionAmountMinimum(TCP_CatLevel_Map.get("MinimumBalance"), j);
			categoryTrfControlProfilePage5.enterPerC2STransactionAmountMaximum(TCP_CatLevel_Map.get("MaximumBalance"), j);
			categoryTrfControlProfilePage5.enterAlertingBalance( TCP_CatLevel_Map.get("AlertingBalance"), j);
			categoryTrfControlProfilePage5.enterAllowedMaxPercentage( TCP_CatLevel_Map.get("AllowedMaxPercentage"), j);

		}

		// Entering Transfer Control Profile----Daily.
		categoryTrfControlProfilePage5.enterDailyTransferInCount(TCP_CatLevel_Map.get("DailyInCount"));
		categoryTrfControlProfilePage5.enterDailyTransferInAlertingCount(TCP_CatLevel_Map.get("DailyInAlertingCount"));
		categoryTrfControlProfilePage5.enterDailyTransferInValue(TCP_CatLevel_Map.get("DailyInTransferValue"));
		categoryTrfControlProfilePage5.enterDailyTransferInAlertingValue(TCP_CatLevel_Map.get("DailyInAlertingValue"));
		categoryTrfControlProfilePage5.enterDailyChannelTransferOutCount(TCP_CatLevel_Map.get("DailyOutCount"));
		categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("DailyOutAlertingCount"));
		categoryTrfControlProfilePage5.enterDailyChannelTransferOutValue(TCP_CatLevel_Map.get("DailyOutTransferValue"));
		categoryTrfControlProfilePage5.enterDailyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("DailyOutAlertingValue"));
		if (TCP_ClientVer == 1) {
			categoryTrfControlProfilePage5.enterDailySubscriberTransferInCount(TCP_CatLevel_Map.get("DailySubscriberInCount"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("DailySubscriberInAlertingCount"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferInValue(TCP_CatLevel_Map.get("DailySubscriberTransferInValue"));
			categoryTrfControlProfilePage5.enterDailySubscriberTransferInAlertingValue(TCP_CatLevel_Map.get("DailySubscriberTransferInAlertingValue"));
		}
		categoryTrfControlProfilePage5.enterDailySubscriberTransferOutCount(TCP_CatLevel_Map.get("DailySubscriberTransferOutCount"));
		categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingCount"));
		categoryTrfControlProfilePage5.enterDailySubscriberTransferOutValue(TCP_CatLevel_Map.get("DailySubscriberTransferOutValue"));
		categoryTrfControlProfilePage5.enterDailySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingValue"));

		// Entering Transfer Control Profile----Weekly.
		categoryTrfControlProfilePage5.enterWeeklyTransferInCount(TCP_CatLevel_Map.get("WeeklyInCount"));
		categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingCount(TCP_CatLevel_Map.get("WeeklyInAlertingCount"));
		categoryTrfControlProfilePage5.enterWeeklyTransferInValue(TCP_CatLevel_Map.get("WeeklyInTransferValue"));
		categoryTrfControlProfilePage5.enterWeeklyTransferInAlertingValue(TCP_CatLevel_Map.get("WeeklyInAlertingValue"));
		categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutCount( TCP_CatLevel_Map.get("WeeklyOutCount"));
		categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("WeeklyOutAlertingCount"));
		categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutValue(TCP_CatLevel_Map.get("WeeklyOutTransferValue"));
		categoryTrfControlProfilePage5.enterWeeklyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("WeeklyOutAlertingValue"));
		if (TCP_ClientVer == 1) {
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInCount(TCP_CatLevel_Map.get("WeeklySubscriberInCount"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue"));
			categoryTrfControlProfilePage5.enterWeeklySubscriberTransferInAlertingValue( TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue"));
		}
		categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutCount(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount"));
		categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount"));
		categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue"));
		categoryTrfControlProfilePage5.enterWeeklySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue"));

		// Entering Transfer Control Profile----Monthly.
		categoryTrfControlProfilePage5.enterMonthlyTransferInCount(TCP_CatLevel_Map.get("MonthlyInCount"));
		categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingCount(TCP_CatLevel_Map.get("MonthlyInAlertingCount"));
		categoryTrfControlProfilePage5.enterMonthlyTransferInValue(TCP_CatLevel_Map.get("MonthlyInTransferValue"));
		categoryTrfControlProfilePage5.enterMonthlyTransferInAlertingValue(TCP_CatLevel_Map.get("MonthlyInAlertingValue"));
		categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutCount(TCP_CatLevel_Map.get("MonthlyOutCount"));
		categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingCount(TCP_CatLevel_Map.get("MonthlyOutAlertingCount"));
		categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutValue(TCP_CatLevel_Map.get("MonthlyOutTransferValue"));
		categoryTrfControlProfilePage5.enterMonthlyChannelTransferOutAlertingValue(TCP_CatLevel_Map.get("MonthlyOutAlertingValue"));
		if (TCP_ClientVer == 1) {
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInCount(TCP_CatLevel_Map.get("MonthlySubscriberInCount"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingCount(TCP_CatLevel_Map.get("MonthlySubscriberInAlertingCount"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferInValue"));
			categoryTrfControlProfilePage5.enterMonthlySubscriberTransferInAlertingValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferInAlertingValue"));
		}
		categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutCount(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutCount"));
		categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingCount(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingCount"));
		categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutValue"));
		categoryTrfControlProfilePage5.enterMonthlySubscriberTransferOutAlertingValue(TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingValue"));

		categoryTrfControlProfilePage5.clickSubmitButton();	


		String actual = categoryTrfControlProfilePage2.getErrorMessage();
		TCP_CatLevel_Map.put("ACTUALMESSAGE",actual);



		return TCP_CatLevel_Map;

	}

	public void addProfileToDataProvider(String profileType, int rowNum, String tcpName, String profileID) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		if (profileType.equalsIgnoreCase("SATCP")) {
			ExcelUtility.setCellData(0, ExcelI.SA_TCP_NAME, rowNum, tcpName);
			ExcelUtility.setCellData(0, ExcelI.SA_TCP_PROFILE_ID, rowNum, profileID);
		} else if (profileType.equalsIgnoreCase("NATCP")) {
			ExcelUtility.setCellData(0, ExcelI.NA_TCP_NAME, rowNum, tcpName);
			ExcelUtility.setCellData(0, ExcelI.NA_TCP_PROFILE_ID, rowNum, profileID);
		}
	}
	
	
	public Map<String, String> createChannelLevelTransferControlProfile_Neg(int rowNum,String domainName, String categoryName,String TCPName) {
		final String methodname = "createChannelLevelTransferControlProfile_Neg";
		Log.methodEntry(methodname, rowNum, domainName, categoryName);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		Map<String, String> dataMap = new HashMap<String, String>();

		//Operator User Access Implementation by Krishan.
		Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_TCP_ROLECODE); //Getting User with Access to Add Channel Level TCP
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		nAHomePage.clickProfileManagement();

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		//String TCPName = ("AUT"+randmGeneration.randomNumeric(5));

		
		profileManagementSubCat.clickTransferControlProfile();
		transferControlProfileSelectCategory.selectDomainName(domainName);
		transferControlProfileSelectCategory.selectCategoryName(categoryName);
		transferControlProfileSelectCategory.clickSubmitButton();

		boolean saveExist=transferControlProfilePage.isSaveExist();

		if(saveExist==false){
			transferControlProfileDetailsPage.clickAddButton();
		}
		transferControlProfilePage.enterProfileName(TCPName);
		transferControlProfilePage.enterShortName(TCPName);
		transferControlProfilePage.enterDescription("Automated Tranfer Control Profile Creation.");
		transferControlProfilePage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);

		transferControlProfilePage.clickSaveButton();
		transferControlProfilePage2.clickConfirmButton();

		

		String actual = transferControlProfileDetailsPage.getDeleteErrorMessage();
		dataMap.put("ACTUALMESSAGE",actual);

		
		Log.methodExit(methodname);
		return dataMap;
	}





}
