package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.superadminpages.grademanagement.AddGradeConfirmPage;
import com.pageobjects.superadminpages.grademanagement.AddGradePage;
import com.pageobjects.superadminpages.grademanagement.GradeManagementPage;
import com.pageobjects.superadminpages.grademanagement.ModifyGradeConfirmPage2;
import com.pageobjects.superadminpages.grademanagement.ViewGradeDetailsPage;
import com.pageobjects.superadminpages.grademanagement.gradeConfirmmMsgDetailsPage;
import com.pageobjects.superadminpages.grademanagement.modifyGradePage;
import com.pageobjects.superadminpages.homepage.ChannelDomainSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class GradeManagement {
	WebDriver driver;
	SuperAdminHomePage homePage;
	ChannelDomainSubCategories ChannelDomainSubCat;
	GradeManagementPage GradeMgmtPage;
	ViewGradeDetailsPage GradesviewDetailsPage;
	AddGradePage AddGrades;
	RandomGeneration randomGen;
	AddGradeConfirmPage AddGradesConfPage;
	SelectNetworkPage networkPage;
	Login login;
	gradeConfirmmMsgDetailsPage gradeConfirmmMsgDetailsPage;
	modifyGradePage modifyGradePage;
	ModifyGradeConfirmPage2 ModifyGradeConfirmPage2;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	public GradeManagement(WebDriver driver) {
		this.driver = driver;
		homePage = new SuperAdminHomePage(driver);
		ChannelDomainSubCat = new ChannelDomainSubCategories(driver);
		GradeMgmtPage = new GradeManagementPage(driver);
		GradesviewDetailsPage = new ViewGradeDetailsPage(driver);
		AddGrades = new AddGradePage(driver);
		randomGen = new RandomGeneration();
		AddGradesConfPage = new AddGradeConfirmPage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		gradeConfirmmMsgDetailsPage = new gradeConfirmmMsgDetailsPage(driver);
		modifyGradePage =new modifyGradePage(driver);
		ModifyGradeConfirmPage2 = new ModifyGradeConfirmPage2(driver);
	}
	
	public Map<String, String> addGrade(String domainName, String categoryName) {
		final String methodname = "addGrade";
		Log.methodEntry(methodname, domainName, categoryName);
		
		Map<String, String> dataMap = new HashMap<String, String>();
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		String gradeName = UniqueChecker.UC_GradeName();
		String gradeCode = UniqueChecker.UC_GradeCode();
		networkPage.selectNetwork();
		homePage.clickChannelDomain();
		ChannelDomainSubCat.clickGradeManagement();
		GradeMgmtPage.selectDomain(domainName);
		GradeMgmtPage.selectCategory(categoryName);
		GradeMgmtPage.clickSubmitButton();
		GradesviewDetailsPage.ClickAddButton();
		AddGrades.enterGradeCode(gradeCode);
		AddGrades.enterGradeName(gradeName);
		AddGrades.ClickSaveButton();
		AddGradesConfPage.ClickConfirmButton();
		String actualMessage = gradeConfirmmMsgDetailsPage.getMessage();
		
		dataMap.put("ACTUALMESSAGE", actualMessage);
		dataMap.put("GRADENAME", gradeName);
		dataMap.put("GRADECODE", gradeCode);
		
		Log.methodExit(methodname);
		return dataMap;
	}
	
	public String getDefaultGrade(String categoryName) {
		String DefaultGradeName = DBHandler.AccessHandler.getGradeName(categoryName);
		return DefaultGradeName;
	}
	
	public void writeGradeToSheet(int rowNum, String GradeName) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.GRADE, rowNum, GradeName);
		
	}
	
	
	public String deleteGrade( String domainName, String categoryName, String gradeName){
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickChannelDomain();
		ChannelDomainSubCat.clickGradeManagement();
		GradeMgmtPage.selectDomain(domainName);
		GradeMgmtPage.selectCategory(categoryName);
		GradeMgmtPage.clickSubmitButton();
		GradesviewDetailsPage.selectGrade(gradeName);
		GradesviewDetailsPage.ClickDeleteButton();
		driver.switchTo().alert().accept();
		String actualMessage = gradeConfirmmMsgDetailsPage.getMessage();
		
		
		return actualMessage;
		
		
	}
	
	
	
	
	
	
	
public Map<String, String> modifyGrade( String domainName, String categoryName, String gradeName){
	
	  Map<String, String> dataMap = new HashMap<String, String>();
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickChannelDomain();
		ChannelDomainSubCat.clickGradeManagement();
		GradeMgmtPage.selectDomain(domainName);
		GradeMgmtPage.selectCategory(categoryName);
		GradeMgmtPage.clickSubmitButton();
		GradesviewDetailsPage.selectGrade(gradeName);
		GradesviewDetailsPage.ClickModifyButton();
		String NewGradeName = "AUT" + randomGen.randomNumeric(4);
		modifyGradePage.changeGradeName(NewGradeName);
		modifyGradePage.ClickSaveButton();
		ModifyGradeConfirmPage2.ClickConfirmButton();
		
		String actualMessage = gradeConfirmmMsgDetailsPage.getMessage();
		
		
		dataMap.put("ACTUALMESSAGE", actualMessage);
		dataMap.put("GRADENAME", NewGradeName);
		
		return dataMap;
		
		
	}




/*
 * SIT Test cases
 */
	

//Verify that Super Admin can not  define Grade for the user profiles (Categories defined in the PreTUPS) when mandatory details are not selected



public Map<String, String> AddGradeWithBlankGradeCode(String domainName, String categoryName) {
	
	Map<String, String> dataMap = new HashMap<String, String>();
	
	//Operator User Access Implementation by Krishan.
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	//User Access module ends.
	
	String gradeName = UniqueChecker.UC_GradeName();

	networkPage.selectNetwork();
	homePage.clickChannelDomain();
	ChannelDomainSubCat.clickGradeManagement();
	GradeMgmtPage.selectDomain(domainName);
	GradeMgmtPage.selectCategory(categoryName);
	GradeMgmtPage.clickSubmitButton();
	GradesviewDetailsPage.ClickAddButton();
	AddGrades.enterGradeCode("");
	AddGrades.enterGradeName(gradeName);
	AddGrades.ClickSaveButton();
	
	String actualMessage = gradeConfirmmMsgDetailsPage.getErrorMessage();
	
	dataMap.put("ACTUALMESSAGE", actualMessage);
	dataMap.put("GRADENAME", gradeName);
	
	return dataMap;
}


public Map<String, String> AddGradeWithBlankGradeName(String domainName, String categoryName) {
	
	Map<String, String> dataMap = new HashMap<String, String>();
	
	//Operator User Access Implementation by Krishan.
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	//User Access module ends.
	

	String gradeCode = UniqueChecker.UC_GradeCode();
	networkPage.selectNetwork();
	homePage.clickChannelDomain();
	ChannelDomainSubCat.clickGradeManagement();
	GradeMgmtPage.selectDomain(domainName);
	GradeMgmtPage.selectCategory(categoryName);
	GradeMgmtPage.clickSubmitButton();
	GradesviewDetailsPage.ClickAddButton();
	AddGrades.enterGradeCode(gradeCode);
	AddGrades.enterGradeName("");
	AddGrades.ClickSaveButton();
	
	String actualMessage = gradeConfirmmMsgDetailsPage.getErrorMessage();
	
	dataMap.put("ACTUALMESSAGE", actualMessage);
	
	
	return dataMap;
}

public Map<String, String> AddDefaultGrade(String domainName, String categoryName) {
	
	Map<String, String> dataMap = new HashMap<String, String>();
	
	//Operator User Access Implementation by Krishan.
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	//User Access module ends.
	
	String gradeName = UniqueChecker.UC_GradeName();
	String gradeCode = UniqueChecker.UC_GradeCode();
	networkPage.selectNetwork();
	homePage.clickChannelDomain();
	ChannelDomainSubCat.clickGradeManagement();
	GradeMgmtPage.selectDomain(domainName);
	GradeMgmtPage.selectCategory(categoryName);
	GradeMgmtPage.clickSubmitButton();
	GradesviewDetailsPage.ClickAddButton();
	AddGrades.enterGradeCode(gradeCode);
	AddGrades.enterGradeName(gradeName);
	AddGrades.checkDefault();	
	AddGrades.ClickSaveButton();
	AddGradesConfPage.ClickConfirmButton();
	String actualMessage = gradeConfirmmMsgDetailsPage.getMessage();
	
	dataMap.put("ACTUALMESSAGE", actualMessage);
	dataMap.put("GRADECODE", gradeCode);
	dataMap.put("GRADENAME", gradeName);
	
	
	return dataMap;
}



public Map<String, String> AddGrade_DuplicategradeCodeValidation(String domainName, String categoryName, String gradeCode) {
	
	Map<String, String> dataMap = new HashMap<String, String>();
	
	//Operator User Access Implementation by Krishan.
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	//User Access module ends.
	String gradeName1 = UniqueChecker.UC_GradeName();
	networkPage.selectNetwork();
	homePage.clickChannelDomain();
	ChannelDomainSubCat.clickGradeManagement();
	GradeMgmtPage.selectDomain(domainName);
	GradeMgmtPage.selectCategory(categoryName);
	GradeMgmtPage.clickSubmitButton();
	GradesviewDetailsPage.ClickAddButton();
	AddGrades.enterGradeCode(gradeCode);
	AddGrades.enterGradeName(gradeName1);
	
	AddGrades.ClickSaveButton();
	AddGradesConfPage.ClickConfirmButton();
	String actualMessage = gradeConfirmmMsgDetailsPage.getErrorMessage();
	
	dataMap.put("ACTUALMESSAGE", actualMessage);
	
	
	return dataMap;
}


public Map<String, String> AddGrade_DuplicategradeNameValidation(String domainName, String categoryName, String gradeName) {
	
	Map<String, String> dataMap = new HashMap<String, String>();
	
	//Operator User Access Implementation by Krishan.
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	//User Access module ends.
	
	String gradeCode1 = UniqueChecker.UC_GradeName();
	networkPage.selectNetwork();
	homePage.clickChannelDomain();
	ChannelDomainSubCat.clickGradeManagement();
	GradeMgmtPage.selectDomain(domainName);
	GradeMgmtPage.selectCategory(categoryName);
	GradeMgmtPage.clickSubmitButton();
	GradesviewDetailsPage.ClickAddButton();
	AddGrades.enterGradeCode(gradeCode1);
	AddGrades.enterGradeName(gradeName);
	
	AddGrades.ClickSaveButton();
	AddGradesConfPage.ClickConfirmButton();
	String actualMessage = gradeConfirmmMsgDetailsPage.getErrorMessage();
	
	dataMap.put("ACTUALMESSAGE", actualMessage);
	
	
	return dataMap;
}



public String deleteDefaultGrade( String domainName, String categoryName, String DefaultGradeName){
	
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GRADE_MANAGEMENT_ROLECODE); //Getting User with Access to Add Geographical Domains
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	
	networkPage.selectNetwork();
	homePage.clickChannelDomain();
	ChannelDomainSubCat.clickGradeManagement();
	GradeMgmtPage.selectDomain(domainName);
	GradeMgmtPage.selectCategory(categoryName);
	GradeMgmtPage.clickSubmitButton();
	GradesviewDetailsPage.selectGrade(DefaultGradeName);
	GradesviewDetailsPage.ClickDeleteButton();
	driver.switchTo().alert().accept();
	String actualMessage = GradesviewDetailsPage.getErrorMessage();
	
	
	return actualMessage;
	
	
}


}
