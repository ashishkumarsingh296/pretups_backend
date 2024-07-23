package com.Features;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.commons.ExcelI;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.utils.ExtentI;

public class SuperPreferences extends BaseTest{

	Login login;
	SuperAdminHomePage superhomepage;
	SystemPreferencePage sysPrefPage;
	SelectNetworkPage selectnetwork;
	WebDriver driver=null;
	
	public SuperPreferences(WebDriver driver){
		this.driver=driver;
		login = new Login();
		superhomepage = new SuperAdminHomePage(driver);
		sysPrefPage = new SystemPreferencePage(driver);
		selectnetwork = new SelectNetworkPage(driver);
	}
	public void modifyCategoryPreference(String moduleValue,String preferenceCodeName,String valueofPreference){
		String loginID = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.LOGIN_ID, new String[]{ExcelI.CATEGORY_CODE}, new String[]{"SUADM"});
		String password = ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, new String[]{ExcelI.CATEGORY_CODE}, new String[]{"SUADM"});
		
		login.LoginAsUser(driver, loginID,password);
		selectnetwork.selectNetwork();
		superhomepage.clickPreferences();
		sysPrefPage.clickSystemPrefernce();
		sysPrefPage.selectModule(moduleValue);
		sysPrefPage.selectCategoryPreference();
		sysPrefPage.clickSubmitButton();
		sysPrefPage.setValueofSystemPreference(preferenceCodeName, valueofPreference);
		sysPrefPage.clickModifyBtn();
		sysPrefPage.clickConfirmBtn();
	}
	
}
