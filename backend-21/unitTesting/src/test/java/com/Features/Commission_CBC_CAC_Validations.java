package com.Features;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.CacheController;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.commissionprofile.AddAdditionalCommissionDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.AddCommissionProfileConfirmPage;
import com.pageobjects.networkadminpages.commissionprofile.AddCommissionProfileDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.AddCommissionProfilePage;
import com.pageobjects.networkadminpages.commissionprofile.CommissionProfilePage;
import com.pageobjects.networkadminpages.commissionprofile.CommissionProfileStatusPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyAdditionalCommProfileDetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyCommProfileConfirmPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyCommProfiledetailsPage;
import com.pageobjects.networkadminpages.commissionprofile.ModifyCommissionProfilePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.ProfileManagementSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils._masterVO;

public class Commission_CBC_CAC_Validations extends BaseTest {
	
	public WebDriver driver;

	String MasterSheetPath = _masterVO.getProperty("DataProvider");
	NetworkAdminHomePage homePage;
	Login login;
	RandomGeneration RandomGenerator;
	CommissionProfilePage commissionProfilePage;
	AddCommissionProfilePage addCommissionProfilePage;
	AddCommissionProfileDetailsPage addProfileDetailsPage;
	AddAdditionalCommissionDetailsPage addAdditionalCommissionDetailsPage;
	ProfileManagementSubCategories profileMgmntSubCats;
	AddCommissionProfileConfirmPage commissionConfirmPage;
	CommissionProfileStatusPage commissionProfileStatusPage;
	ModifyCommissionProfilePage modifyCommissionProfilePage;
	ModifyCommProfiledetailsPage modifyCommProfiledetailsPage;
	ModifyAdditionalCommProfileDetailsPage modifyAdditionalCommProfileDetailsPage;
	ModifyCommProfileConfirmPage modifyCommProfileConfirmPage;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	String[] result;
	SelectNetworkPage selectNetworkPage;
	CacheUpdate CacheUpdate;
	String selectedNetwork;
	int subSlabCount = 0;
	String targetbasedCommission="";
	String targetbasedaddtnlcommission="";
	int COMM_PROFILE_CLIENTVER;
	
	public Commission_CBC_CAC_Validations(WebDriver driver) {
		
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		RandomGenerator = new RandomGeneration();
		commissionProfilePage = new CommissionProfilePage(driver);
		addCommissionProfilePage = new AddCommissionProfilePage(driver);
		addProfileDetailsPage = new AddCommissionProfileDetailsPage(driver);
		addAdditionalCommissionDetailsPage = new AddAdditionalCommissionDetailsPage(driver);
		profileMgmntSubCats = new ProfileManagementSubCategories(driver);
		commissionConfirmPage = new AddCommissionProfileConfirmPage(driver);
		commissionProfileStatusPage = new CommissionProfileStatusPage(driver);
		modifyCommissionProfilePage = new ModifyCommissionProfilePage(driver);
		modifyCommProfiledetailsPage = new ModifyCommProfiledetailsPage(driver);
		
		modifyCommProfileConfirmPage = new ModifyCommProfileConfirmPage(driver);
		modifyAdditionalCommProfileDetailsPage = new ModifyAdditionalCommProfileDetailsPage(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		
		CacheUpdate =new CacheUpdate(driver);
		selectedNetwork = _masterVO.getMasterValue("Network Code");
		if(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork,"TARGET_BASED_ADDNL_COMMISSION_SLABS")!=null){
			subSlabCount = Integer.parseInt(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork,"TARGET_BASED_ADDNL_COMMISSION_SLABS"));}
		if(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "TARGET_BASED_BASE_COMMISSION")!=null){
			targetbasedCommission = DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "TARGET_BASED_BASE_COMMISSION");}
		if(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "TARGET_BASED_ADDNL_COMMISSION")!=null){
			targetbasedaddtnlcommission = DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "TARGET_BASED_ADDNL_COMMISSION");}
		
		COMM_PROFILE_CLIENTVER = Integer.parseInt(_masterVO.getClientDetail("MULTIPAGECOMMISSIONSELECTION"));
	}
	
	public long modifyCBCCACCommissionProfile(HashMap<String, String> dataMap) throws InterruptedException, ParseException {
		userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// Enter Domain, category, Geographical Domain and Grade.
		selectNetworkPage.selectNetwork();
		homePage.clickProfileManagement();
		commissionProfilePage.selectDomain(dataMap.get("TO_DOMAIN"));
		commissionProfilePage.selectCategory(dataMap.get("TO_CATEGORY"));
		if(COMM_PROFILE_CLIENTVER > 0) {
			if(COMM_PROFILE_CLIENTVER == 1)
				commissionProfilePage.clickAddButton();
			commissionProfilePage.selectGeographicalDomain(_masterVO.getProperty("GeographicalDomain"));
			commissionProfilePage.selectGrade(dataMap.get("TO_GRADE"));
		}
		commissionProfilePage.clickModifyButton();
		modifyCommissionProfilePage.selectCommissionProfileSet(dataMap.get("TO_COMMISSION_PROFILE"));
		modifyCommissionProfilePage.clickModifyButton();
		String currDate = homePage.getDate();
		
		if (dataMap.get("COMMTYPE").equalsIgnoreCase(PretupsI.COMM_TYPE_BASECOMM))
			modifyCommProfiledetailsPage.ModifyComm();
		else if (dataMap.get("COMMTYPE").equalsIgnoreCase(PretupsI.COMM_TYPE_ADNLCOMM))
			modifyCommProfiledetailsPage.ModifyAdditionalComm();
		
		// Window handler
		SwitchWindow.switchwindow(driver);
		
		if (dataMap.get("APPLICABLE_FROM_DATE") != null) {
			String applicableFromDate = dataMap.get("APPLICABLE_FROM_DATE");
			
			if (dataMap.get("APPLICABLE_FROM_DATE").equalsIgnoreCase("CURRENTDATE"))
				applicableFromDate = currDate;

			WebElement applicableFromDateElement = driver.findElement(By.xpath("//input[@name[contains(.,'[0].otfApplicableFromStr')]]"));
			applicableFromDateElement.clear();
			applicableFromDateElement.sendKeys(applicableFromDate);	
		}
		
		if (dataMap.get("APPLICABLE_TO_DATE") != null) {
			String applicableToDate = dataMap.get("APPLICABLE_TO_DATE");
			
			if (dataMap.get("APPLICABLE_TO_DATE").equalsIgnoreCase("CURRENTDATE"))
				applicableToDate = currDate;
			
			WebElement applicableToDateElement = driver.findElement(By.xpath("//input[@name[contains(.,'[0].otfApplicableToStr')]]"));
			applicableToDateElement.clear();
			applicableToDateElement.sendKeys(applicableToDate);
		}
		
		if (dataMap.get("TIME_SLAB") != null) {
			WebElement timeSlabElement = driver.findElement(By.xpath("//input[@name[contains(.,'[0].otfTimeSlab')]]"));
			timeSlabElement.clear();
			timeSlabElement.sendKeys(dataMap.get("TIME_SLAB"));
		}

		
		if (dataMap.get("CBC_VALUE") != null) {
			WebElement c2cValueElement = driver.findElement(By.xpath("//input[@name[contains(.,'[0].otfDetails[0].otfValue')]]"));
			c2cValueElement.clear();
			c2cValueElement.sendKeys(dataMap.get("CBC_VALUE"));
		}
		
		if (dataMap.get("CBC_VALUE1") != null) {
			WebElement c2cValueElement = driver.findElement(By.xpath("//input[@name[contains(.,'[0].otfDetails[1].otfValue')]]"));
			c2cValueElement.clear();
			c2cValueElement.sendKeys(dataMap.get("CBC_VALUE1"));
		}
		
		if (dataMap.get("CBC_TYPE") != null) {
			Select select = new Select(driver.findElement(By.xpath("//select[@name[contains(.,'[0].otfDetails[0].otfType')]]")));
			select.selectByValue(dataMap.get("CBC_TYPE"));
		}
		
		if (dataMap.get("CBC_RATE") != null) {
			WebElement c2crateElement = driver.findElement(By.xpath("//input[@name[contains(.,'[0].otfDetails[0].otfRate')]]"));
			c2crateElement.clear();
			c2crateElement.sendKeys(dataMap.get("CBC_RATE"));
		}
		
		addProfileDetailsPage.clickAdd();
		dataMap.put("SlabErrorMessage",CONSTANT.COMM_SLAB_ERR);
		SwitchWindow.backwindow(driver);
		
		modifyCommProfiledetailsPage.modifyCommDate(currDate);
		String time = addCommissionProfilePage.modifyApplicableFromHour(homePage.getApplicableFromTime_1min());
		String time2 = time+":00";
		long requiredtime = homePage.getTimeDifferenceInSeconds(time2);

		modifyCommProfiledetailsPage.clickSave();
		modifyCommProfileConfirmPage.confirmButton();

		String statusText = commissionProfilePage.getActualMsg();
		Log.info("Status Text is:" + statusText);

    	dataMap.put("ACTUAL_MESSAGE", statusText);
		

		CacheUpdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE());
		return requiredtime;
	}
}
