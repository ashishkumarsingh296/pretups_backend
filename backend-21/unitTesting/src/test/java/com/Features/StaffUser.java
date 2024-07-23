package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserPage;
import com.pageobjects.channeladminpages.addchanneluser.ApproveChannelUserPage;
import com.pageobjects.channeladminpages.addchanneluser.ModifyChannelUserPage1;
import com.pageobjects.channeladminpages.addchanneluser.ModifyChannelUserPage2;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.loginpages.ChangePINForNewUser;
import com.pageobjects.loginpages.ChangePasswordForNewUser;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class StaffUser extends BaseTest {
	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randStr;
	ChangePasswordForNewUser changenewpwd;
	OperatorUsersSubCategories operatorSubLink;
	ApproveChannelUserPage apprvChannelUsrPage;
	ChannelUsersSubCategories channelUserSubCategories;
	AddChannelUserPage addChrUserPage;
	AddChannelUserDetailsPage addChrUserDetailsPage;
	ChangePINForNewUser changeUsrPIN;
	ModifyChannelUserPage1 modifyCHNLpage1;
	ModifyChannelUserPage2 modifyCHNLpage2;
	
	public String LoginID;
	public static String webAccess;
	public String autoPassword = null;
	public int RowNum;
	public String NEWPASSWORD;
	HashMap<String, String> staffresultMap;
	String UserName;
	String UserName1;
	static String NewPin;
	static int list;
	static boolean staffDetails;
	public String autoPIN = null;
	String APPLEVEL;
	Map<String, String> userAccessMap;
	
	WebDriver driver=null;
	
	public StaffUser(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		changenewpwd = new ChangePasswordForNewUser(driver);
		operatorSubLink = new OperatorUsersSubCategories(driver);
		staffresultMap = new HashMap<String, String>();
		apprvChannelUsrPage= new ApproveChannelUserPage(driver);
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		addChrUserPage = new AddChannelUserPage(driver);
		addChrUserDetailsPage = new AddChannelUserDetailsPage(driver);
		apprvChannelUsrPage = new ApproveChannelUserPage(driver);
		changeUsrPIN = new ChangePINForNewUser(driver);
		userAccessMap = new HashMap<String, String>();
		modifyCHNLpage1 = new ModifyChannelUserPage1(driver);
		modifyCHNLpage2 = new ModifyChannelUserPage2(driver);
	}

	
	
	/**
	 * Initiate Operator User
	 * @param ParentUser
	 * @param LoginUser
	 * 
	 * @return HashMap -> channelresultMap
	 * @throws InterruptedException
	 */
	
	public HashMap<String, String> staffUserInitiate(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException {
		final String methodname = "channelUserInitiate";
		Log.methodEntry(methodname, RowNum, Domain, Parent, Category, geotype);
		
		this.RowNum=RowNum;
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"STAFF_USER_APRL_LEVEL");
		staffDetails=false;
		webAccess = DBHandler.AccessHandler.webInterface(Category).toUpperCase();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_STAFF_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickAddStaffUsers();
		addChrUserPage.selectDomain(Domain);
		addChrUserPage.selectCategory(Category);
		addChrUserPage.parentCategory(Parent);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		addChrUserPage.selectGeographyDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));
		addChrUserPage.clickSubmitBtn();
		
		list = addChrUserPage.textBoxlist();
		if(list==1||list==2||list==3){
			if(list==1){ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			staffresultMap.put("UserName",ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum));}
			if(list==2||list==3){staffresultMap.put("UserName", login.UserNameSequence(driver, "Channel", Domain, "1"));}
		addChrUserPage.enterOwnerUser();
		addChrUserPage.selectOwnerName(staffresultMap.get("UserName"));}

		if(list==2||list==3)
		{	if(list==2){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			staffresultMap.put("ParentUserName", ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum));}
			if(list==3){
			staffresultMap.put("ParentUserName", login.ParentName(driver, "Channel", Domain, Parent));}
		addChrUserPage.enterParentUser();
		addChrUserPage.selectParentName(staffresultMap.get("ParentUserName"));
		}
		
		if(list==3){
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		staffresultMap.put("ChannelUserName", ExcelUtility.getCellData(0, ExcelI.USER_NAME, RowNum));
		addChrUserPage.enterchannelUsername();
		addChrUserPage.selectchannelUsername(staffresultMap.get("ChannelUserName"));}

		
		addChrUserPage.clickPrntSubmitBtn();
		try{
		// Filling the form for channel user
		staffresultMap.put("fName","AUTFN" + randStr.randomNumeric(4));
		staffresultMap.put("lName", "AUTLN" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterFirstName(staffresultMap.get("fName"));
		addChrUserDetailsPage.enterLastName(staffresultMap.get("lName"));
		staffresultMap.put("uName",staffresultMap.get("fName") + " " + staffresultMap.get("lName"));
		addChrUserDetailsPage.enterUserName(staffresultMap.get("uName"));
		staffresultMap.put("shortName","AUTSN" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterShortName(staffresultMap.get("shortName"));
		addChrUserDetailsPage.selectUserNamePrefix(1);
		addChrUserDetailsPage.enterSubscriberCode("" + randStr.randomNumeric(6));

		// Select Status as 'Y' or 'N' if drop-down is available.
		addChrUserDetailsPage.selectStatus("Y");

		// Enter unique external code and MSISDN
		staffresultMap.put("EXTCODE",UniqueChecker.UC_EXTCODE());
		String isMSISDNrequired = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"IS_REQ_MSISDN_FOR_STAFF");
		if(isMSISDNrequired.equalsIgnoreCase("true")){staffresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());}
		
		staffresultMap.put("ContactNo", "" + randStr.randomNumeric(6));
		addChrUserDetailsPage.enterContactNo(staffresultMap.get("ContactNo"));
		
		staffresultMap.put("Address1", "Add1" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterAddress1(staffresultMap.get("Address1"));
		
		staffresultMap.put("Address2", "Add2" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterAddress2(staffresultMap.get("Address2"));
		
		staffresultMap.put("City", "City" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterCity(staffresultMap.get("City"));
		
		staffresultMap.put("State", "State" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterState(staffresultMap.get("State"));
		
		staffresultMap.put("Country", "Country" + randStr.randomNumeric(2));
		addChrUserDetailsPage.enterCountry(staffresultMap.get("Country"));

		staffresultMap.put("Email", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		addChrUserDetailsPage.enterEmailID(staffresultMap.get("Email"));

		
		// Enter Unique LoginID
		if(webAccess.equals("Y")){
		LoginID = UniqueChecker.UC_LOGINID();
		staffresultMap.put("LOGIN_ID", LoginID);
		addChrUserDetailsPage.enterLoginID(LoginID);
		}
		// Assigning Geography
		addChrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String geoGrpahyCode=_masterVO.geoTypeMap.get(geotype)[0];
		staffresultMap.put("geoGrpahyName",_masterVO.geoTypeMap.get(geotype)[1]);
		String geoRow = _masterVO.geoTypeMap.get(geotype)[2]; //added later on 05September
		addChrUserDetailsPage.assignGeographies1(geoRow,geoGrpahyCode);
		SwitchWindow.backwindow(driver);

		// Assigning Roles for Channel user
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);

		// Assigning Phone Number
		addChrUserDetailsPage.assignStaffPhone();
		SwitchWindow.switchwindow(driver);
		staffresultMap.put("PIN", _masterVO.getProperty("PIN"));
		addChrUserDetailsPage.assignPhoneNumber1(staffresultMap.get("MSISDN"), staffresultMap.get("PIN"));
		SwitchWindow.backwindow(driver);

		// Enter Password & Confirm Password
		if(webAccess.equals("Y")){
		staffresultMap.put("PASSWORD", _masterVO.getProperty("Password"));
		staffresultMap.put("CONFIRMPASSWORD", _masterVO.getProperty("ConfirmPassword"));
		addChrUserDetailsPage.enterPassword(staffresultMap.get("PASSWORD"));
		addChrUserDetailsPage.enterConfirmPassword(staffresultMap.get("CONFIRMPASSWORD"));}
		
		addChrUserDetailsPage.clickSaveButton(); // Click Save button on page
		addChrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
													// page
		staffresultMap.put("channelInitiateMsg", addChrUserDetailsPage.getActualMessage());
		staffDetails=true;
		}catch(Exception e){
			currentNode.log(Status.FAIL, e);
		}	
		Log.methodExit(methodname);
		return staffresultMap;
	}
	
	/**
	 * Approve Channel user on basis of approval preference
	 * @return HashMap channelresultMap
	 */
	public HashMap<String, String> approveLevel1_StaffUser(){
		final String methodname = "approveLevel1_ChannelUser";
		Log.methodEntry(methodname);
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.STAFF_USER_APPROVAL1_ROLECODE); //Getting User with Access to Approve Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalOneStaffUsers();
		apprvChannelUsrPage.enterStaffusername(staffresultMap.get("uName"));;
		apprvChannelUsrPage.clickaprlSubmitBtn();
		apprvChannelUsrPage.clickOkSubmitBtn();
		apprvChannelUsrPage.approveBtn();
		apprvChannelUsrPage.confirmBtn();
		if(APPLEVEL.equals("1"))
			staffresultMap.put("channelApproveMsg", addChrUserDetailsPage.getActualMessage());
		else if(APPLEVEL.equals("2"))
			staffresultMap.put("channelApprovelevel1Msg", addChrUserDetailsPage.getActualMessage());
						
		Log.methodExit(methodname);
		return staffresultMap;				
	}
	
	public HashMap<String, String> approveLevel2_StaffUser(){
		final String methodname = "approveLevel2_ChannelUser";
		Log.methodEntry(methodname);
					
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.STAFF_USER_APPROVAL2_ROLECODE); //Getting User with Access to Add Geographical Domains
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
					
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalTwoStaffUsers();
		apprvChannelUsrPage.enterStaffusername(staffresultMap.get("uName"));;
		apprvChannelUsrPage.clickaprlSubmitBtn();
		apprvChannelUsrPage.clickOkSubmitBtn();
		apprvChannelUsrPage.approveBtn();
		apprvChannelUsrPage.confirmBtn();
		staffresultMap.put("channelApprovelevel2Msg", addChrUserDetailsPage.getActualMessage());
		
		Log.methodExit(methodname);
		return staffresultMap;
	} 
				
	
	
	/**
	 * Change password at first Login attempt
	 * @param ParentUser
	 * @param LoginUser
	 */
	
	public HashMap<String, String> changeUserFirstTimePassword() {
		final String methodname = "changeUserFirstTimePassword";
		Log.methodEntry(methodname);
		
		String PASSWORD = _masterVO.getProperty("Password");
		NEWPASSWORD = _masterVO.getProperty("NewPassword");
		String autoPwdGenerate = DBHandler.AccessHandler.getSystemPreference("AUTO_PWD_GENERATE_ALLOW").toUpperCase();
		
		if(autoPwdGenerate.equals("FALSE"))	{
			Log.info("Password field exist");
			login.LoginAsUser(driver, LoginID, PASSWORD);
			changenewpwd.changePassword(PASSWORD, NEWPASSWORD, NEWPASSWORD);
		} else {
			Log.info("Password field not exist, password is autogenerated");
			autoPassword = DBHandler.AccessHandler.fetchUserPassword(LoginID);
			login.LoginAsUser(driver, LoginID, autoPassword);
			changenewpwd.changePassword(autoPassword, NEWPASSWORD, NEWPASSWORD);
		}
		staffresultMap.put("PASSWORD", NEWPASSWORD);
		
		Log.methodExit(methodname);
		return staffresultMap;
	}
	
	
	public HashMap<String, String> changeUserFirstTimePIN() {
		final String methodname = "changeUserFirstTimePIN";
		Log.methodEntry(methodname);
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGEPIN_ROLECODE); //Getting User with Access to Change First Time PIN
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickChangePIN();
		//changeUsrPIN.enterLoginIDandRemarks(LoginID);
		changeUsrPIN.enterMSISDNandRemarks(staffresultMap.get("MSISDN"));
		
		NewPin=_masterVO.getProperty("NewPIN");
		String ConfirmPin=_masterVO.getProperty("ConfirmPIN");
		
		if(DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase().equals("FALSE")) {
			String Pin=_masterVO.getProperty("PIN");
			changeUsrPIN.changePIN(Pin, NewPin, ConfirmPin);
			staffresultMap.put("PIN",NewPin);
		} else {
			Log.info("PIN is autogenerated");
			autoPIN = DBHandler.AccessHandler.fetchUserPIN(LoginID,staffresultMap.get("MSISDN"));	
			changeUsrPIN.changePIN(autoPIN, NewPin, ConfirmPin);
			staffresultMap.put("PIN", NewPin);
		}
		staffresultMap.put("changePINMsg", addChrUserDetailsPage.getActualMessage());
		
		Log.methodExit(methodname);
		return staffresultMap;
	}
	
	/**
	 * Write data to DataProvider sheet
	 * @param RowNum
	 * @throws IOException 
	 */
	
	public void writeChannelUserData(int RowNum) throws IOException{
		this.RowNum=RowNum;
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		int i=0;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		staffresultMap.put("parentLoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i));
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.STAFF_USERS_SHEET);
		
		if(staffDetails){
		if(webAccess.equals("Y")){
		ExcelUtility.setCellData(0,ExcelI.STAFF_LOGINID, RowNum, LoginID);
		ExcelUtility.setCellData(0,ExcelI.STAFF_PASSWORD, RowNum, staffresultMap.get("PASSWORD"));}
		ExcelUtility.setCellData(0,ExcelI.STAFF_USER_NAME,RowNum,staffresultMap.get("uName"));
		ExcelUtility.setCellData(0,ExcelI.STAFF_PIN,RowNum,staffresultMap.get("PIN"));
		ExcelUtility.setCellData(0,ExcelI.STAFF_MSISDN,RowNum,staffresultMap.get("MSISDN"));}
		if(list==1){ExcelUtility.setCellData(0, ExcelI.CHANNEL_USER_NAME, RowNum, staffresultMap.get("UserName"));
					i = ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, staffresultMap.get("UserName"));}
		if(list==2){ExcelUtility.setCellData(0, ExcelI.OWNER_USER_NAME, RowNum, staffresultMap.get("UserName"));
					ExcelUtility.setCellData(0, ExcelI.CHANNEL_USER_NAME, RowNum, staffresultMap.get("ParentUserName"));
					i = ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, staffresultMap.get("ParentUserName"));}
		if(list==3){ExcelUtility.setCellData(0, ExcelI.CHANNEL_USER_NAME, RowNum, staffresultMap.get("ChannelUserName"));
					ExcelUtility.setCellData(0, ExcelI.PARENT_USER_NAME, RowNum, staffresultMap.get("ParentUserName"));
					ExcelUtility.setCellData(0, ExcelI.OWNER_USER_NAME, RowNum, staffresultMap.get("UserName"));
					i = ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, staffresultMap.get("ChannelUserName"));}
		ExcelUtility.setCellData(0, ExcelI.STAFF_EMAIL_ID, RowNum, staffresultMap.get("Email"));
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		staffresultMap.put("parentLoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i));
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.STAFF_USERS_SHEET);
		
		ExcelUtility.setCellData(0, ExcelI.STAFF_PARENT_LOGIN_ID, RowNum, staffresultMap.get("parentLoginID"));
		
	}	
}
