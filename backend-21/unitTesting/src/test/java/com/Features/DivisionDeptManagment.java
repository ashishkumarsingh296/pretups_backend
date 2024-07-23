package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.pageobjects.superadminpages.divisionmanagement.AddDepartmentPage;
import com.pageobjects.superadminpages.divisionmanagement.AddDivisionPage;
import com.pageobjects.superadminpages.divisionmanagement.AddDivisionPage2;
import com.pageobjects.superadminpages.divisionmanagement.ViewDivisionDetailsPage;
import com.pageobjects.superadminpages.divisionmanagement.modifyDepartmentPage;
import com.pageobjects.superadminpages.divisionmanagement.modifyDivisionPage;
import com.pageobjects.superadminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class DivisionDeptManagment extends BaseTest {

	SuperAdminHomePage homePage;
	MastersSubCategories mastersSubCat;
	ViewDivisionDetailsPage divisionDetailsPage;
	AddDivisionPage addDivisionPage;
	AddDivisionPage2 addDivisionPage2;
	AddDepartmentPage addDeptartmentPage;
	modifyDivisionPage modifyDivision;
	modifyDepartmentPage modifyDept;
	CacheUpdate CacheUpdate;
	Login login;
	RandomGeneration randStr;
	SelectNetworkPage networkPage;
	HashMap<String, String> divdeptMap = new HashMap<String, String>();
	String MasterSheetPath;
	WebDriver driver;
	static String division;
	static String division2;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	

	public DivisionDeptManagment(WebDriver driver) {
		// TODO Auto-generated constructor stub
		this.driver=driver;
		homePage = new SuperAdminHomePage(driver);
		mastersSubCat = new MastersSubCategories(driver);
		divisionDetailsPage = new ViewDivisionDetailsPage(driver);
		addDivisionPage = new AddDivisionPage(driver);
		addDivisionPage2 = new AddDivisionPage2(driver);
		addDeptartmentPage = new AddDepartmentPage(driver);
		modifyDivision = new modifyDivisionPage(driver);
		modifyDept = new modifyDepartmentPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		networkPage = new SelectNetworkPage(driver);
		divdeptMap=new HashMap<String, String>();
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		CacheUpdate = new CacheUpdate(driver);
		
	}
	
	/**
	 * Division creation
	 * @return HashMap
	 */
	public HashMap<String, String> divisionManagement() {
		final String methodname = "divisionManagement";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("division", "AUTDIV" + randStr.randomNumeric(5));
		division=divdeptMap.get("division");
		System.out.println(division);
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.clickAddButton();
		addDivisionPage.selectDivisionType();
		addDivisionPage.enterDivisionName(divdeptMap.get("division"));
		addDivisionPage.enterDivisionShortCode(divdeptMap.get("division"));
		addDivisionPage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		addDivisionPage.clickSubmitButton();
		addDivisionPage2.clickConfirmButton();
		/*String expected = "Divison added successfully";
		String actual = divisionDetailsPage.getMessage();
		Assert.assertEquals(expected, actual, "Division successfully added.");*/
		divdeptMap.put("divisionaddMsg", divisionDetailsPage.getMessage());
		//CacheUpdate.updateCache();
		
		Log.methodExit(methodname);
		return divdeptMap;
	}
	
	//Multiple Divisions
	public HashMap<String, String> divisionCreationMultiple() {
		final String methodname = "divisionManagement";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("division", "AUTDIV" + randStr.randomNumeric(5));
		division=divdeptMap.get("division");
		System.out.println(division);
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.clickAddButton();
		addDivisionPage.selectDivisionType();
		addDivisionPage.enterDivisionName(divdeptMap.get("division"));
		addDivisionPage.enterDivisionShortCode(divdeptMap.get("division"));
		addDivisionPage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		addDivisionPage.clickSubmitButton();
		addDivisionPage2.clickConfirmButton();
		
		divdeptMap.put("divisionaddMsg1", divisionDetailsPage.getMessage());
		
		divdeptMap.put("division2", "AUTDIV" + randStr.randomNumeric(5));
		division2=divdeptMap.get("division2");
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.clickAddButton();
		addDivisionPage.selectDivisionType();
		addDivisionPage.enterDivisionName(divdeptMap.get("division2"));
		addDivisionPage.enterDivisionShortCode(divdeptMap.get("division2"));
		addDivisionPage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		addDivisionPage.clickSubmitButton();
		addDivisionPage2.clickConfirmButton();
		divdeptMap.put("divisionaddMsg2", divisionDetailsPage.getMessage());
		//CacheUpdate.updateCache();
		
		Log.methodExit(methodname);
		return divdeptMap;
	}

	/**
	 * Department creation
	 * @return HashMap
	 */
	public HashMap<String, String> departmentManagement(){
		final String methodname = "departmentManagement";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("department","AUTDEPT" + randStr.randomNumeric(6));
		divdeptMap.put("departmentShortCode","AUT" + randStr.randomNumeric(4));	
		homePage.clickMasters();
		mastersSubCat.clickDepartmentManagement();
		addDeptartmentPage.selectDivisionType();
		//division=divdeptMap.get("division");
		addDeptartmentPage.selectDivision(division);
		addDeptartmentPage.clickSubmit();
		addDeptartmentPage.clickAddButton();
		addDeptartmentPage.enterDepartmentName(divdeptMap.get("department"));
		addDeptartmentPage.enterDepartmentShortCode(divdeptMap.get("departmentShortCode"));
		addDeptartmentPage.clickSubmitButton();
		addDeptartmentPage.clickConfirmButton();
		divdeptMap.put("departmentaddMsg", divisionDetailsPage.getMessage());
		
		Log.methodExit(methodname);
		return divdeptMap;
	}
	
	
	
	public HashMap<String, String> divisionManagementModify(String div) {
		final String methodname = "divisionManagementModify";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("division",div );
		division=divdeptMap.get("division");
		System.out.println(division);
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.selectDivision(division);
		divisionDetailsPage.clickModifyButton();
		modifyDivision.enterDivisionName(divdeptMap.get("division") + "mod");
		divdeptMap.put("division",div+"mod" );
		modifyDivision.enterDivisionShortCode(divdeptMap.get("division"));
		modifyDivision.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		modifyDivision.clickSubmitButton();
		modifyDivision.clickConfirmButton();
	
		divdeptMap.put("divisionModifyMsg", divisionDetailsPage.getMessage());
		Log.methodExit(methodname);
		return divdeptMap;
	}
	
	
	
	public HashMap<String, String> deptManagementModify(String dept) {
		final String methodname = "deptManagementModify";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("department",dept );
		homePage.clickMasters();
		mastersSubCat.clickDepartmentManagement();
		addDeptartmentPage.selectDivisionType();
		addDeptartmentPage.selectDivision(division);
		addDeptartmentPage.clickSubmit();
		addDeptartmentPage.selectDepartment(dept);
		addDeptartmentPage.clickModifyButton();
		
		modifyDept.enterDeptName(divdeptMap.get("department") + "mod");
		divdeptMap.put("department",dept+"mod" );
		modifyDept.enterDeptShortCode(divdeptMap.get("department"));
		modifyDept.clickSubmitButton();
		modifyDept.clickConfirmButton();
	
		divdeptMap.put("deptModifyMsg", addDeptartmentPage.getMessage());
		Log.methodExit(methodname);
		return divdeptMap;
	}
	
	
	
	
	public HashMap<String, String> deptManagementDelete(String dept,boolean count) {
		final String methodname = "deptManagementDelete";
		Log.methodEntry(methodname);
		if(count == true) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.DIVISION_DEPT_SHEET);
		String DivisionName = ExcelUtility.getCellData(0, ExcelI.DIVISION, 1);
		division = DivisionName;
		}
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("department",dept );
		homePage.clickMasters();
		mastersSubCat.clickDepartmentManagement();
		addDeptartmentPage.selectDivisionType();
		addDeptartmentPage.selectDivision(division);
		addDeptartmentPage.clickSubmit();
		addDeptartmentPage.selectDepartment(dept);
		addDeptartmentPage.clickDeleteButton();
	
			driver.switchTo().alert().accept();
			
		if(addDeptartmentPage.getMessage()!=null)
		divdeptMap.put("deptDeleteMsg", addDeptartmentPage.getMessage());
		else
			divdeptMap.put("deptDeleteMsg", addDeptartmentPage.getErrorMessage());
		Log.methodExit(methodname);
		return divdeptMap;
	}

	
	
	
	public HashMap<String, String> divisionManagementDelete(String div) {
		final String methodname = "divisionManagementDelete";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.selectDivision(div);
		divisionDetailsPage.clickDeleteButton();
		driver.switchTo().alert().accept();
	
		divdeptMap.put("divisionDelMsg", divisionDetailsPage.getMessage());
		Log.methodExit(methodname);
		return divdeptMap;
	}
	

	public HashMap<String, String> divisionManagementDeleteNeg(String div) {
		final String methodname = "divisionManagementDelete";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		/*divdeptMap.put("division",div );
		division=divdeptMap.get("division");
		System.out.println(division);*/
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.selectDivision(div);
		divisionDetailsPage.clickDeleteButton();
		driver.switchTo().alert().accept();
	
		divdeptMap.put("divisionDelMsg", divisionDetailsPage.getErrorMessage());
		Log.methodExit(methodname);
		return divdeptMap;
	}
	
	
	
	
	/**
	 * Write division department data to excel
	 */
	public void writedivisiondepartment(){
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.DIVISION_DEPT_SHEET);
		ExcelUtility.createHeader(ExcelI.DIVISION, ExcelI.DEPARTMENT);
		String Division = divdeptMap.get("division");
		String Department = divdeptMap.get("department");
		ExcelUtility.setCellData(0, ExcelI.DIVISION, 1,  Division);
		ExcelUtility.setCellData(0, ExcelI.DEPARTMENT, 1, Department);
	}


	
	/*
	 * SIT Test Cases
	 */

	public HashMap<String, String> divisionManagement_blankDivisionName(String divName) {

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.clickAddButton();
		addDivisionPage.selectDivisionType();
		addDivisionPage.enterDivisionName("");
		division=("AUTDIV" + randStr.randomNumeric(5));
		addDivisionPage.enterDivisionShortCode(division);
		addDivisionPage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		addDivisionPage.clickSubmitButton();
		divdeptMap.put("divisionaddMsg", divisionDetailsPage.getErrorMessage());
		return divdeptMap;
	}


	public HashMap<String, String> divisionManagement_blankDivisionCode(String divName) {

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.clickAddButton();
		addDivisionPage.selectDivisionType();
		division=("AUTDIV" + randStr.randomNumeric(5));
		addDivisionPage.enterDivisionName(division);
		addDivisionPage.enterDivisionShortCode("");
		addDivisionPage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		addDivisionPage.clickSubmitButton();
		divdeptMap.put("divisionaddMsg", divisionDetailsPage.getErrorMessage());
		return divdeptMap;
	}


	public HashMap<String, String> divisionManagement_StatusNotSelected(String divName) {

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.clickAddButton();
		addDivisionPage.selectDivisionType();
		division=("AUTDIV" + randStr.randomNumeric(5));
		addDivisionPage.enterDivisionName(division);
		addDivisionPage.enterDivisionShortCode(division);
		addDivisionPage.clickSubmitButton();
		
		divdeptMap.put("divisionaddMsg", divisionDetailsPage.getErrorMessage());
		return divdeptMap;
	}
	
	
	
	public HashMap<String, String> departmentManagement_blankdeptName(String divName){

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("department","AUTDEPT" + randStr.randomNumeric(6));
		divdeptMap.put("departmentShortCode","AUT" + randStr.randomNumeric(4));	
		homePage.clickMasters();
		mastersSubCat.clickDepartmentManagement();
		addDeptartmentPage.selectDivisionType();
		addDeptartmentPage.selectDivision_Neg(1);
		addDeptartmentPage.clickSubmit();
		addDeptartmentPage.clickAddButton();
		addDeptartmentPage.enterDepartmentName("");
		addDeptartmentPage.enterDepartmentShortCode(divdeptMap.get("departmentShortCode"));
		addDeptartmentPage.clickSubmitButton();
		divdeptMap.put("departmentaddMsg", divisionDetailsPage.getErrorMessage());
		return divdeptMap;
	}
	
	
	public HashMap<String, String> departmentManagement_blankDeptCode(String divName){

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("department","AUTDEPT" + randStr.randomNumeric(6));
		divdeptMap.put("departmentShortCode","AUT" + randStr.randomNumeric(4));	
		homePage.clickMasters();
		mastersSubCat.clickDepartmentManagement();
		addDeptartmentPage.selectDivisionType();
		division=divdeptMap.get("division");
		addDeptartmentPage.selectDivision_Neg(1);
		addDeptartmentPage.clickSubmit();
		addDeptartmentPage.clickAddButton();
		addDeptartmentPage.enterDepartmentName(divdeptMap.get("department"));
		addDeptartmentPage.enterDepartmentShortCode("");
		addDeptartmentPage.clickSubmitButton();
		
		divdeptMap.put("departmentaddMsg", divisionDetailsPage.getErrorMessage());
		return divdeptMap;
	}
	
	

	
	public HashMap<String, String> divisionManagementUnique_neg(String div, String shortCode, boolean counter) {
		final String methodname = "divisionManagement";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("division", "AUTDIV" + randStr.randomNumeric(5));
		division=divdeptMap.get("division");
		System.out.println(division);
		homePage.clickMasters();
		mastersSubCat.clickDivisionManagement();
		divisionDetailsPage.clickAddButton();
		addDivisionPage.selectDivisionType();
		if(counter == false){
		addDivisionPage.enterDivisionName(divdeptMap.get("division"));
		addDivisionPage.enterDivisionShortCode(shortCode);
		}
		else{
			addDivisionPage.enterDivisionName(div);
			addDivisionPage.enterDivisionShortCode(divdeptMap.get("division"));	
		}
		addDivisionPage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		addDivisionPage.clickSubmitButton();
		addDivisionPage2.clickConfirmButton();
		divdeptMap.put("divisionaddMsg", divisionDetailsPage.getErrorMessage());
		Log.methodExit(methodname);
		return divdeptMap;
	}
	
	
	
	public HashMap<String, String> departmentManagementUnique_neg(String department, String shortCode, boolean counter) {
		final String methodname = "divisionManagement";
		Log.methodEntry(methodname);

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DIVISION_DEPARTMENT_ROLECODE); //Getting User with Access to Division Department Management
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		divdeptMap.put("department","AUTDEPT" + randStr.randomNumeric(6));
		divdeptMap.put("departmentShortCode","AUT" + randStr.randomNumeric(4));	
		homePage.clickMasters();
		mastersSubCat.clickDepartmentManagement();
		addDeptartmentPage.selectDivisionType();
		addDeptartmentPage.selectDivision(division);
		addDeptartmentPage.clickSubmit();
		addDeptartmentPage.clickAddButton();
		if(counter == false){
			addDeptartmentPage.enterDepartmentName(divdeptMap.get("department"));
			addDeptartmentPage.enterDepartmentShortCode(shortCode);
		}
		else{
			addDeptartmentPage.enterDepartmentName(department);
			addDeptartmentPage.enterDepartmentShortCode(divdeptMap.get("departmentShortCode"));
		}
		
		addDeptartmentPage.clickSubmitButton();
		addDeptartmentPage.clickConfirmButton();
		divdeptMap.put("departmentaddMsg", addDeptartmentPage.getErrorMessage());

		
		Log.methodExit(methodname);
		return divdeptMap;
	}
	
	
	
	



}
