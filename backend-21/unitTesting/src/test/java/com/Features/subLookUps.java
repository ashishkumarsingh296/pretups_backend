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
import com.pageobjects.superadminpages.subLookUps.AddSubLookUps;
import com.pageobjects.superadminpages.subLookUps.modifyLookUps;
import com.utils.RandomGeneration;


public class subLookUps {
	
	
	WebDriver driver = null;
	Login login;
	RandomGeneration randomNum;
	SuperAdminHomePage SuperAdminHomePage;
	MastersSubCategories MastersSubCategories;
	AddSubLookUps AddSubLookUps;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	SelectNetworkPage networkPage;
	modifyLookUps modifySubLookUp;

	
	public subLookUps(WebDriver driver){
	this.driver = driver;	
	login = new Login();
	randomNum = new RandomGeneration();
	SuperAdminHomePage = new SuperAdminHomePage(driver);
	MastersSubCategories = new MastersSubCategories(driver);
	AddSubLookUps = new AddSubLookUps(driver);
	modifySubLookUp = new modifyLookUps(driver);
	networkPage = new SelectNetworkPage(driver);
	
	}
	
	@SuppressWarnings("null")
	public String [] addSubLookUp(){
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADDSUBLOOKUP); //Getting User with Access to Add SubLookUps
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[3];
		
		String SubLookUpName = UniqueChecker.UC_SubLookUpName();
		result[0] = SubLookUpName;
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickAddSubLookUp();
		//int index = AddSubLookUps.getLookUpCodeIndex();
		
		String LookUpName = AddSubLookUps.selectLookUpName(1);
		result[1]= LookUpName;
		//AddSubLookUps.selectLookUpName1(PretupsI.Bonus_Comm_Type);
		AddSubLookUps.entersubLookupName(SubLookUpName);
		AddSubLookUps.clickSubmit();
		AddSubLookUps.clickConfirm();
		String actual = AddSubLookUps.getMessage();
		result[2] = actual;
		
		return result;
	}
	
	
	@SuppressWarnings("null")
	public String[] modifySubLookUp(String SubLookUpName , String LookUpName){
		String actual=null;
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADDSUBLOOKUP); //Getting User with Access to Add SubLookUps
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String[] resultSet = new String[2];
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickModifySubLookUp();
		modifySubLookUp.selectLookUpNamebyVisibleText(LookUpName);
		modifySubLookUp.selectSubLookupName(SubLookUpName);
		modifySubLookUp.clickSubmit();
		String NewName = SubLookUpName +"Modified";
		System.out.println("================" +NewName+"===============");
		modifySubLookUp.EnterModifiedName(NewName);
		resultSet[0] = NewName;
		modifySubLookUp.clickSubmit1();
		modifySubLookUp.clickConfirm();
		
		actual = AddSubLookUps.getMessage();
		resultSet[1] = actual;
		
		return resultSet;
			
	}
	
	
	@SuppressWarnings("null")
	public String deleteSubLookUp(String NewName, String LookUpName){
		String actual=null;
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADDSUBLOOKUP); //Getting User with Access to Add SubLookUps
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickModifySubLookUp();
		modifySubLookUp.selectLookUpNamebyVisibleText(LookUpName);
		modifySubLookUp.selectSubLookupName(NewName);
		modifySubLookUp.clickSubmit();
		modifySubLookUp.clickDelete();
		driver.switchTo().alert().accept();
		
		
		actual = AddSubLookUps.getMessage();
		
		
		return actual;
			
	}
	

}
