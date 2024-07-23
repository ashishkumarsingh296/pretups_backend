package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.grouprolemanagement.AddGroupRolePage;
import com.pageobjects.superadminpages.grouprolemanagement.AddGroupRolePage2;
import com.pageobjects.superadminpages.grouprolemanagement.GroupRoleManagementPage1;
import com.pageobjects.superadminpages.grouprolemanagement.GroupRolesListPage;
import com.pageobjects.superadminpages.grouprolemanagement.ModifyGroupRolePage;
import com.pageobjects.superadminpages.grouprolemanagement.ModifyGroupRolePage2;
import com.pageobjects.superadminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class GroupRoleManagement {
	
	WebDriver driver = null;
	Login login;
	RandomGeneration randomNum;
	SuperAdminHomePage SuperAdminHomePage;
	MastersSubCategories MastersSubCategories;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	SelectNetworkPage networkPage;
	AddGroupRolePage addGroupRole;
	AddGroupRolePage2 addGroupRole2;
	GroupRoleManagementPage1 GroupRoleManagement;
	GroupRolesListPage GroupRolesList;
	ModifyGroupRolePage modifyGroupRole;
	ModifyGroupRolePage2 modifyGroupRoleConfirmPage;
	ChannelUserHomePage channelUser;
	CacheUpdate CacheUpdate;
	
	
	public GroupRoleManagement(WebDriver driver){
		this.driver = driver;	
		login = new Login();
		randomNum = new RandomGeneration();
		SuperAdminHomePage = new SuperAdminHomePage(driver);
		MastersSubCategories = new MastersSubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		addGroupRole = new AddGroupRolePage(driver);
		addGroupRole2 = new AddGroupRolePage2(driver);
		GroupRoleManagement = new GroupRoleManagementPage1(driver);
		GroupRolesList = new GroupRolesListPage(driver);
		modifyGroupRole = new ModifyGroupRolePage(driver);
		modifyGroupRoleConfirmPage = new ModifyGroupRolePage2(driver);
		channelUser = new ChannelUserHomePage(driver);
		CacheUpdate =new CacheUpdate(driver);
		
	}
	
	
	
	public String[] addGroupRole(String domain, String Category) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GROUPROLE); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[2];
		String RoleName = UniqueChecker.UC_GroupRoleName(); 
		result[0] = RoleName;
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickGroupRoleManagement();
		GroupRoleManagement.selectDomain(domain);
		GroupRoleManagement.selectCategory(Category);
		GroupRoleManagement.clickSubmitButton();
		GroupRolesList.clickAddButton();
		addGroupRole.enterRoleCode(RoleName);
		addGroupRole.enterRoleName(RoleName);
		addGroupRole.enterGroupName(RoleName);
		addGroupRole.enterFromHour("1");
		addGroupRole.enterToHour("24");
		addGroupRole.clickCheckAllBox();
		addGroupRole.clickSaveButton();
		addGroupRole2.clickConfirmButton();
		result[1] = GroupRoleManagement.getMessage();
		
		return result;
		
	}
	
	public String getDefaultGroupRoleName(String categoryCode) {
		String DefaultGroupRoleName = DBHandler.AccessHandler.getDefaultGroupRoleName(categoryCode);
		return DefaultGroupRoleName;
	}
	
	
	public void writeGroupRoleToSheet(int rowNum, String GroupRoleName) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.GROUP_ROLE, rowNum, GroupRoleName);
		
	}
	
	public void writeGroupRoleToOperatorSheet(int rowNum, String GradeName) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.GROUP_ROLE, rowNum, GradeName);
		
	}
		
	
	public String[] modifyGroupRole(String domain, String Category, String RoleName) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GROUPROLE); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[2];
		result[0]= RoleName+"Modified";
		 
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickGroupRoleManagement();
		GroupRoleManagement.selectDomain(domain);
		GroupRoleManagement.selectCategory(Category);
		GroupRoleManagement.clickSubmitButton();
		
		boolean groupRoleExist = GroupRolesList.groupRoleExistenceCheck(RoleName);
		if(groupRoleExist==true){
			GroupRolesList.clickGroupRoleRadioButton(RoleName);	
		
		GroupRolesList.clickEditButton();
		modifyGroupRole.entergroupName(result[0]);
		modifyGroupRole.clickSaveButton();
		modifyGroupRoleConfirmPage.clickConfirmButton();
		result[1] = GroupRoleManagement.getMessage();
		
		}
		else
		{
			Log.info("Role" +RoleName+ " does not exist");
		}
		
		return result;
		
	}
	
	
	
	public String[] deleteGroupRole(String domain, String Category, String RoleName) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GROUPROLE); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[2];
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickGroupRoleManagement();
		GroupRoleManagement.selectDomain(domain);
		GroupRoleManagement.selectCategory(Category);
		GroupRoleManagement.clickSubmitButton();
		
		boolean groupRoleExist = GroupRolesList.groupRoleExistenceCheck(RoleName);
		if(groupRoleExist==true){
			GroupRolesList.clickGroupRoleRadioButton(RoleName);	
		
		GroupRolesList.clickDeleteButton();
		driver.switchTo().alert().accept();
		result[1] = GroupRoleManagement.getMessage();
		
		}
		else
		{
			Log.info("Role" +RoleName+ " does not exist");
		}
		
		return result;
		
	}
	
	
	public String[] suspendGroupRole(String domain, String Category, String RoleName) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.GROUPROLE); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[2];
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickGroupRoleManagement();
		GroupRoleManagement.selectDomain(domain);
		GroupRoleManagement.selectCategory(Category);
		GroupRoleManagement.clickSubmitButton();
		
		boolean groupRoleExist = GroupRolesList.groupRoleExistenceCheck(RoleName);
		if(groupRoleExist==true){
			GroupRolesList.clickGroupRoleRadioButton(RoleName);	
		
		GroupRolesList.clickEditButton();
		modifyGroupRole.selectStatus(PretupsI.STATUS_SUSPENDED_LOOKUPS);
		modifyGroupRole.clickSaveButton();
		modifyGroupRoleConfirmPage.clickConfirmButton();
		result[1] = GroupRoleManagement.getMessage();
		
		CacheUpdate.updateCache();
		
		}
		else
		{
			Log.info("Role" +RoleName+ " does not exist");
		}
		
		return result;
		
	}
	

public String [] addGroupRolewithC2S(String domain,String Category) throws InterruptedException{
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GROUPROLE); //Getting User with Access to Add Interface
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	String [] result = new String[2];
	String RoleName = UniqueChecker.UC_GroupRoleName(); 
	result[0] = RoleName;
	networkPage.selectNetwork();
	SuperAdminHomePage.clickMasters();
	MastersSubCategories.clickGroupRoleManagement();
	GroupRoleManagement.selectDomain(domain);
	GroupRoleManagement.selectCategory(Category);
	GroupRoleManagement.clickSubmitButton();
	GroupRolesList.clickAddButton();
	addGroupRole.enterRoleCode(RoleName);
	addGroupRole.enterRoleName(RoleName);
	addGroupRole.enterGroupName(RoleName);
	addGroupRole.enterFromHour("1");
	addGroupRole.enterToHour("24");
	addGroupRole.SelectRole(RolesI.C2SRECHARGE);
	addGroupRole.clickSaveButton();
	addGroupRole2.clickConfirmButton();
	result[1] = GroupRoleManagement.getMessage();
	
	return result;
	
}


public String loginWithUserHavingSpecificGroupRole(String LoginId,String Pwd) throws InterruptedException {
	String actual= null;
	login.LoginAsUser(driver, LoginId , Pwd );
	
	networkPage.selectNetwork();
	
	boolean c2sVisiblity= channelUser.C2STransferLinkVisibility();
	
	boolean c2cVisibility = channelUser.C2CTransferLinkVisibility();
	
	if(c2sVisiblity==true && c2cVisibility==false){
		actual = "Only the Links included in the associated GroupRole are displayed on Channel user homepage";
	}
	else if(c2sVisiblity==true && c2cVisibility==true){
		Log.info("The Displayed Links on Channel User Homepage are irrespective of the associated Group Role");
	}
	
	else
	{
		Log.info("There is some issue in displaying links on Channel User Homepage");
	}
	
	
	return actual;
	
}







public String [] addGroupRoleAsperCategory(String domainCode,String Category) throws InterruptedException{
	
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GROUPROLE); //Getting User with Access to Add Interface
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	String [] result = new String[2];
	String RoleName = UniqueChecker.UC_GroupRoleName(); 
	result[0] = RoleName;
	
	String domainName = DBHandler.AccessHandler.fetchDomainName(domainCode); 
	
	networkPage.selectNetwork();
	SuperAdminHomePage.clickMasters();
	MastersSubCategories.clickGroupRoleManagement();
	GroupRoleManagement.selectDomain(domainName);
	GroupRoleManagement.selectCategory(Category);
	GroupRoleManagement.clickSubmitButton();
	GroupRolesList.clickAddButton();
	addGroupRole.enterRoleCode(RoleName);
	addGroupRole.enterRoleName(RoleName);
	addGroupRole.enterGroupName(RoleName);
	addGroupRole.enterFromHour("1");
	addGroupRole.enterToHour("24");
	
	String MasterSheetPath = _masterVO.getProperty("DataProvider");
	ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	int totalRow1 = ExcelUtility.getRowCount();

	String categoryCode = null;
	for(int c=1; c<=totalRow1; c++) {
		if((ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, c).matches(domainCode)) && (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, c).matches(Category))) {
			categoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, c);
			break;
		}
	} 

	addGroupRole.selectRoles(UserAccess.getApplicableRolesForCategory(categoryCode));
		
	addGroupRole.clickSaveButton();
	addGroupRole2.clickConfirmButton();
	result[1] = GroupRoleManagement.getMessage();
		
	return result;
	
}








public String [] addGroupRoleAsperChannelCategory(String domainName,String Category , String categoryCode) throws InterruptedException{
	
	userAccessMap = UserAccess.getUserWithAccess(RolesI.GROUPROLE); //Getting User with Access to Add Interface
	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	String [] result = new String[2];
	String RoleName = UniqueChecker.UC_GroupRoleName(); 
	result[0] = RoleName;
	
		
	networkPage.selectNetwork();
	SuperAdminHomePage.clickMasters();
	MastersSubCategories.clickGroupRoleManagement();
	GroupRoleManagement.selectDomain(domainName);
	GroupRoleManagement.selectCategory(Category);
	GroupRoleManagement.clickSubmitButton();
	GroupRolesList.clickAddButton();
	addGroupRole.enterRoleCode(RoleName);
	addGroupRole.enterRoleName(RoleName);
	addGroupRole.enterGroupName(RoleName);
	addGroupRole.enterFromHour("1");
	addGroupRole.enterToHour("24");
	addGroupRole.selectRoles(UserAccess.getApplicableRolesForCategory(categoryCode));
	addGroupRole.clickSaveButton();
	addGroupRole2.clickConfirmButton();
	result[1] = GroupRoleManagement.getMessage();
		
	return result;
	
}






	
}
