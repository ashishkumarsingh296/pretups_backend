package com.Features;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.CONSTANT;
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
import com.pageobjects.superadminpages.addoperatoruser.AddOperatorUserDetailsPage;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils._masterVO;
import org.apache.xpath.operations.Bool;

/**
 * @author lokesh.kontey
 *
 */
public class ChannelUser extends BaseTest {
	
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
	AddOperatorUserDetailsPage addOptrUserDetailsPage;
	
	public String LoginID;
	public static String webAccess;
	public String autoPassword = null;
	public int RowNum;
	public String NEWPASSWORD;
	HashMap<String, String> channelresultMap;
	String UserName;
	String UserName1;
	static String NewPin;
	public String autoPIN = null;
	String APPLEVEL;
	String loanProfileApplicable = "null";
	Map<String, String> userAccessMap;
	
	WebDriver driver=null;
	
	public ChannelUser(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		changenewpwd = new ChangePasswordForNewUser(driver);
		operatorSubLink = new OperatorUsersSubCategories(driver);
		channelresultMap = new HashMap<String, String>();
		apprvChannelUsrPage= new ApproveChannelUserPage(driver);
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		addChrUserPage = new AddChannelUserPage(driver);
		addChrUserDetailsPage = new AddChannelUserDetailsPage(driver);
		apprvChannelUsrPage = new ApproveChannelUserPage(driver);
		changeUsrPIN = new ChangePINForNewUser(driver);
		userAccessMap = new HashMap<String, String>();
		modifyCHNLpage1 = new ModifyChannelUserPage1(driver);
		modifyCHNLpage2 = new ModifyChannelUserPage2(driver);
		addOptrUserDetailsPage = new AddOperatorUserDetailsPage(driver);
	}

	
	
	/**
	 * Initiate Operator User
	 * @param ParentUser
	 * @param LoginUser
	 * 
	 * @return HashMap -> channelresultMap
	 * @throws InterruptedException
	 */
	
	public HashMap<String, String> channelUserInitiate(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException {
		final String methodname = "channelUserInitiate";
		Log.methodEntry(methodname, RowNum, Domain, Parent, Category, geotype);
		
		this.RowNum=RowNum;
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);

		APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		String grpRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.GROUP_ROLE_ALLOWED);
		String sysRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_ROLE_ALLOWED);
		String roleTypeDisp=DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHANNEL_USER_ROLE_TYPE_DISPLAY);
		if(_masterVO.getClientDetail("LOANPROFILE").equals("0")) {
			 loanProfileApplicable = DBHandler.AccessHandler.getSystemPreference(CONSTANT.USERWISE_LOAN_ENABLE).toUpperCase();
		}
		if (APPLEVEL == null)
			APPLEVEL = DBHandler.AccessHandler.getSystemPreference("USRLEVELAPPROVAL");
		
		webAccess = DBHandler.AccessHandler.webInterface(Category).toUpperCase();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickAddChannelUsers();
		addChrUserPage.selectDomain(Domain);
		addChrUserPage.selectCategory(Category);
		addChrUserPage.parentCategory(Parent);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		addChrUserPage.selectGeographyDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));
		addChrUserPage.clickSubmitBtn();
		addChrUserPage.enterOwnerUser();
		channelresultMap.put("UserName", login.UserNameSequence(driver, "Channel", Domain, "1"));
		addChrUserPage.selectOwnerName(channelresultMap.get("UserName"));

		addChrUserPage.enterParentUser();
		channelresultMap.put("ParentUserName", login.ParentName(driver, "Channel", Domain, Parent));
		addChrUserPage.selectParentName(channelresultMap.get("ParentUserName"));

		addChrUserPage.clickPrntSubmitBtn();
		// Filling the form for channel user
		channelresultMap.put("fName","AUTFN" + randStr.randomNumeric(4));
		channelresultMap.put("lName", "AUTLN" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterFirstName(channelresultMap.get("fName"));
		addChrUserDetailsPage.enterLastName(channelresultMap.get("lName"));
		channelresultMap.put("uName",channelresultMap.get("fName") + " " + channelresultMap.get("lName"));
		addChrUserDetailsPage.enterUserName(channelresultMap.get("uName"));
		channelresultMap.put("shortName","AUTSN" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterShortName(channelresultMap.get("shortName"));
		addChrUserDetailsPage.selectUserNamePrefix(1);
		addChrUserDetailsPage.enterSubscriberCode("" + randStr.randomNumeric(6));

		// Select Status as 'Y' or 'N' if drop-down is available.
		addChrUserDetailsPage.selectStatus("Y");
		addChrUserDetailsPage.selectLanguage(_masterVO.getMasterValue("Language"));

		// Enter unique external code and MSISDN
		channelresultMap.put("EXTCODE",UniqueChecker.UC_EXTCODE());
		channelresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		addChrUserDetailsPage.enterExternalCode(channelresultMap.get("EXTCODE"));
		addChrUserDetailsPage.enterMobileNumber(channelresultMap.get("MSISDN"));

		//select outlets if exists
		addChrUserDetailsPage.selectOutlet();
		addChrUserDetailsPage.selectSubOutlet();
		
		channelresultMap.put("ContactNo", "" + randStr.randomNumeric(6));
		addChrUserDetailsPage.enterContactNo(channelresultMap.get("ContactNo"));
		
		channelresultMap.put("Address1", "Add1" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterAddress1(channelresultMap.get("Address1"));
		
		channelresultMap.put("Address2", "Add2" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterAddress2(channelresultMap.get("Address2"));
		
		channelresultMap.put("City", "City" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterCity(channelresultMap.get("City"));
		
		channelresultMap.put("State", "State" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterState(channelresultMap.get("State"));
		
		channelresultMap.put("Country", "Country" + randStr.randomNumeric(2));
		addChrUserDetailsPage.enterCountry(channelresultMap.get("Country"));

		channelresultMap.put("Email", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		addChrUserDetailsPage.enterEmailID(channelresultMap.get("Email"));
		
		channelresultMap.put("paymentType","ALL");
		addChrUserDetailsPage.selectPaymentType(channelresultMap.get("paymentType"));
	
		
		// Enter Unique LoginID
		if(webAccess.equals("Y")){
		LoginID = UniqueChecker.UC_LOGINID();
		channelresultMap.put("LOGIN_ID", LoginID);
		addChrUserDetailsPage.enterLoginID(LoginID);
		}
		// Assigning Geography
		addChrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String geoGrpahyCode=_masterVO.geoTypeMap.get(geotype)[0];
		channelresultMap.put("geoGrpahyName",_masterVO.geoTypeMap.get(geotype)[1]);
		String geoRow = _masterVO.geoTypeMap.get(geotype)[2]; //added later on 05September
		addChrUserDetailsPage.assignGeographies1(geoRow,geoGrpahyCode);
		SwitchWindow.backwindow(driver);

		// Assigning Networks
		addChrUserDetailsPage.assignNetwork();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignNetwork1();
		SwitchWindow.backwindow(driver);

		// Assigning Roles for Channel user
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		if(!BTSLUtil.isNullString(grpRole)&&!BTSLUtil.isNullString(sysRole)&&!BTSLUtil.isNullString(roleTypeDisp)&&grpRole.equalsIgnoreCase("Y")&&sysRole.equalsIgnoreCase("N")&&roleTypeDisp.equalsIgnoreCase("GROUP"))
		{ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		addChrUserDetailsPage.assignGroupRole(ExcelUtility.getCellData(0, ExcelI.GROUP_ROLE, RowNum));}
		else
			addChrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);

		// Assigning services for Channel user
		addChrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);

		// Assigning domains
		addChrUserDetailsPage.assignDomains();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignDomains1();
		SwitchWindow.backwindow(driver);

		// Assigning Products
		addChrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		
		// Assigning services for Channel user
		addOptrUserDetailsPage.assignVouchers();
				SwitchWindow.switchwindow(driver);
				addOptrUserDetailsPage.assignVouchers1();
				SwitchWindow.backwindow(driver);


		// Assigning Phone Number
		addChrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);
		channelresultMap.put("PIN", _masterVO.getProperty("PIN"));
		addChrUserDetailsPage.assignPhoneNumber1(channelresultMap.get("MSISDN"), channelresultMap.get("PIN"));
		SwitchWindow.backwindow(driver);

		// Enter Password & Confirm Password
		if(webAccess.equals("Y")){
		channelresultMap.put("PASSWORD", _masterVO.getProperty("Password"));
		channelresultMap.put("CONFIRMPASSWORD", _masterVO.getProperty("ConfirmPassword"));
		addChrUserDetailsPage.enterPassword(channelresultMap.get("PASSWORD"));
		addChrUserDetailsPage.enterConfirmPassword(channelresultMap.get("CONFIRMPASSWORD"));}

		if(APPLEVEL.equals("0") || _masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("0")){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
			if(loanProfileApplicable.equals("true")){apprvChannelUsrPage.selectLoanProfile(0,ExcelI.LOAN_PROFILE,RowNum);}
			else{Log.info("Loan Profile dropdown not available, preference value : "+loanProfileApplicable);}
		}
		
		addChrUserDetailsPage.clickSaveButton(); // Click Save button on page
		addChrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
													// page
		channelresultMap.put("channelInitiateMsg", addChrUserDetailsPage.getActualMessage());
		
		Log.methodExit(methodname);
		return channelresultMap;
	}
	
	/**
	 * Approve Channel user on basis of approval preference
	 * @return HashMap channelresultMap
	 */
	public HashMap<String, String> approveLevel1_ChannelUser(){
		final String methodname = "approveLevel1_ChannelUser";
		Log.methodEntry(methodname);
				
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
				
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.LEVEL1_CHANNEL_USER_APPROVAL_ROLECODE); //Getting User with Access to Approve Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalOneChannelUsers();
		//loanProfileApplicable = DBHandler.AccessHandler.getSystemPreference("USERWISE_LOAN_ENABLE").toUpperCase();
		//apprvChannelUsrPage.enterLoginID(LoginID);
		apprvChannelUsrPage.enterMSISDN(channelresultMap.get("MSISDN"));
		apprvChannelUsrPage.clickaprlSubmitBtn();
		apprvChannelUsrPage.clickOkSubmitBtn();
					
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
					
		if (_masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("1")) {
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
			if(loanProfileApplicable.equals("true"))
			{
				apprvChannelUsrPage.selectLoanProfile(0,ExcelI.LOAN_PROFILE,RowNum);
			}
			else
				Log.info("Loan Profile dropdown not available, preference value : "+loanProfileApplicable);

		}
		apprvChannelUsrPage.approveBtn();
		apprvChannelUsrPage.confirmBtn();
		if(APPLEVEL.equals("1"))
			channelresultMap.put("channelApproveMsg", addChrUserDetailsPage.getActualMessage());
		else if(APPLEVEL.equals("2"))
			channelresultMap.put("channelApprovelevel1Msg", addChrUserDetailsPage.getActualMessage());
						
		Log.methodExit(methodname);
		return channelresultMap;				
	}
	
	public HashMap<String, String> approveLevel2_ChannelUser(){
		final String methodname = "approveLevel2_ChannelUser";
		Log.methodEntry(methodname);
					
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.LEVEL2_CHANNEL_USER_APPROVAL_ROLECODE); //Getting User with Access to Add Geographical Domains
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
					
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalTwoChannelUsers();
		//apprvChannelUsrPage.enterLoginID(LoginID);
		apprvChannelUsrPage.enterMSISDN(channelresultMap.get("MSISDN"));
		apprvChannelUsrPage.clickaprlSubmitBtn();
		apprvChannelUsrPage.clickOkSubmitBtn();
		apprvChannelUsrPage.approveBtn();
		apprvChannelUsrPage.confirmBtn();
		channelresultMap.put("channelApprovelevel2Msg", addChrUserDetailsPage.getActualMessage());
		
		Log.methodExit(methodname);
		return channelresultMap;
	} 
	
	public HashMap<String, String> approveLevel1_ChannelUserVoucher(String voucherType){
		final String methodname = "approveLevel1_ChannelUser";
		Log.methodEntry(methodname);
				
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
				
		//Operator User Access Implementation by Krishan.
		if(voucherType.equalsIgnoreCase("electronic")){
		userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.LEVEL1_CHANNEL_USER_APPROVAL_ROLECODE,"electronic"); //Getting User with Access to Approve Channel Users
		}
		else if(voucherType.equalsIgnoreCase("physical")) {
			userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.LEVEL1_CHANNEL_USER_APPROVAL_ROLECODE,"physical"); //Getting User with Access to Approve Channel Users
		}
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalOneChannelUsers();
		//String loanProfileApplicable = DBHandler.AccessHandler.getSystemPreference("USERWISE_LOAN_ENABLE").toUpperCase();
		//apprvChannelUsrPage.enterLoginID(LoginID);
		apprvChannelUsrPage.enterMSISDN(channelresultMap.get("MSISDN"));
		apprvChannelUsrPage.clickaprlSubmitBtn();
		apprvChannelUsrPage.clickOkSubmitBtn();
					
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
					
		if (_masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("1")) {
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
			if(loanProfileApplicable.equals("true")){apprvChannelUsrPage.selectLoanProfile(0,ExcelI.LOAN_PROFILE,RowNum);}
			else{Log.info("Loan Profile dropdown not available, preference value : "+loanProfileApplicable);}
		}
		apprvChannelUsrPage.approveBtn();
		apprvChannelUsrPage.confirmBtn();
		if(APPLEVEL.equals("1"))
			channelresultMap.put("channelApproveMsg", addChrUserDetailsPage.getActualMessage());
		else if(APPLEVEL.equals("2"))
			channelresultMap.put("channelApprovelevel1Msg", addChrUserDetailsPage.getActualMessage());
						
		Log.methodExit(methodname);
		return channelresultMap;				
	}
	
	public HashMap<String, String> approveLevel2_ChannelUserVoucher(String voucherType){
		final String methodname = "approveLevel2_ChannelUser";
		Log.methodEntry(methodname);
					
		//Operator User Access Implementation by Krishan.
		if(voucherType.equalsIgnoreCase("electronic")){
			userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.LEVEL2_CHANNEL_USER_APPROVAL_ROLECODE,"electronic"); //Getting User with Access to Approve Channel Users
			}
			else if(voucherType.equalsIgnoreCase("physical")) {
				userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.LEVEL2_CHANNEL_USER_APPROVAL_ROLECODE,"physical"); //Getting User with Access to Approve Channel Users
			}
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
					
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalTwoChannelUsers();
		//apprvChannelUsrPage.enterLoginID(LoginID);
		apprvChannelUsrPage.enterMSISDN(channelresultMap.get("MSISDN"));
		apprvChannelUsrPage.clickaprlSubmitBtn();
		apprvChannelUsrPage.clickOkSubmitBtn();
		apprvChannelUsrPage.approveBtn();
		apprvChannelUsrPage.confirmBtn();
		channelresultMap.put("channelApprovelevel2Msg", addChrUserDetailsPage.getActualMessage());
		
		Log.methodExit(methodname);
		return channelresultMap;
	} 
				
	
	
	/**
	 * Change password at first Login attempt
	 * @param ParentUser
	 * @param LoginUser
	 */
	
	public HashMap<String, String> changeUserFirstTimePassword() {
		final String methodname = "changeUserFirstTimePassword";
		Log.methodEntry(methodname);
		
		//String PASSWORD = _masterVO.getProperty("Password");
		String PASSWORD = channelresultMap.get("PASSWORD");
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
		channelresultMap.put("PASSWORD", NEWPASSWORD);
		
		Log.methodExit(methodname);
		return channelresultMap;
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
		
		//Only for SHA when PIN is auto-generated as PIN needs to be updated prior to entering MSISDN on GUI
		if(DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase().equals("TRUE")&&
				DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("SHA"))
		{DBHandler.AccessHandler.fetchUserPIN(LoginID,channelresultMap.get("MSISDN"));}
		
		//changeUsrPIN.enterLoginIDandRemarks(LoginID);
		changeUsrPIN.enterMSISDNandRemarks(channelresultMap.get("MSISDN"));
		
		NewPin=_masterVO.getProperty("NewPIN");
		String ConfirmPin=_masterVO.getProperty("ConfirmPIN");
		
		if(DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase().equals("FALSE")) {
			String Pin=_masterVO.getProperty("PIN");
			changeUsrPIN.changePIN(Pin, NewPin, ConfirmPin);
		} else {
			Log.info("PIN is autogenerated");
			autoPIN = DBHandler.AccessHandler.fetchUserPIN(LoginID,channelresultMap.get("MSISDN"));	
			changeUsrPIN.changePIN(autoPIN, NewPin, ConfirmPin);
		}
		
		if (DBHandler.AccessHandler.fetchUserPIN(LoginID,channelresultMap.get("MSISDN")).equalsIgnoreCase(NewPin)) {
			channelresultMap.put("PIN", NewPin);
		}
		
		channelresultMap.put("changePINMsg", addChrUserDetailsPage.getActualMessage());
		
		Log.methodExit(methodname);
		return channelresultMap;
	}
	
	/**
	 * Write data to DataProvider sheet
	 * @param RowNum
	 */
	
	public void writeChannelUserData(int RowNum){
		this.RowNum=RowNum;
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		if(webAccess.equals("Y")){
		ExcelUtility.setCellData(0,ExcelI.LOGIN_ID, RowNum, LoginID);
		ExcelUtility.setCellData(0,ExcelI.PASSWORD, RowNum, channelresultMap.get("PASSWORD"));}
		ExcelUtility.setCellData(0,ExcelI.USER_NAME,RowNum,channelresultMap.get("fName") + " " + channelresultMap.get("lName"));
		ExcelUtility.setCellData(0,ExcelI.PIN,RowNum,channelresultMap.get("PIN"));
		ExcelUtility.setCellData(0,ExcelI.MSISDN,RowNum,channelresultMap.get("MSISDN"));
		ExcelUtility.setCellData(0,ExcelI.EXTERNAL_CODE,RowNum,channelresultMap.get("EXTCODE"));
		ExcelUtility.setCellData(0,ExcelI.GEOGRAPHY,RowNum,channelresultMap.get("geoGrpahyName"));
		
	}
	
	/**
	 * OverLoading channelUserInitiate function 
	 */
	
	public HashMap<String, String> channelUserInitiate(int RowNum, String Domain, String Parent, String Category, String geotype,HashMap<String, String> paraMeterMap) throws InterruptedException{
		this.RowNum=RowNum;
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		String grpRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.GROUP_ROLE_ALLOWED);
		String sysRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_ROLE_ALLOWED);
		String roleTypeDisp=DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHANNEL_USER_ROLE_TYPE_DISPLAY);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		try{webAccess = DBHandler.AccessHandler.webInterface(Category).toUpperCase();}catch(Exception e){Log.writeStackTrace(e);}
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickAddChannelUsers();
		try{addChrUserPage.selectDomain(Domain);
		addChrUserPage.selectCategory(Category);
		addChrUserPage.parentCategory(Parent);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		addChrUserPage.selectGeographyDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));}
		catch(Exception e){ Log.writeStackTrace(e);}
		addChrUserPage.clickSubmitBtn();
		addChrUserPage.enterOwnerUser();

		channelresultMap.put("UserName", login.UserNameSequence(driver, "Channel", Domain, "1"));
		addChrUserPage.selectOwnerName(channelresultMap.get("UserName"));

		addChrUserPage.enterParentUser();
		channelresultMap.put("ParentUserName", login.ParentName(driver, "Channel", Domain, Parent));
		addChrUserPage.selectParentName(channelresultMap.get("ParentUserName"));

		addChrUserPage.clickPrntSubmitBtn();

		// Filling the form for channel user
		if(paraMeterMap.get("fName")!=null){
		addChrUserDetailsPage.enterFirstName(paraMeterMap.get("fName"));}

		if(paraMeterMap.get("lName")!=null){
		addChrUserDetailsPage.enterLastName(paraMeterMap.get("lName"));}
		
		if(paraMeterMap.get("uName")!=null){
		addChrUserDetailsPage.enterUserName(paraMeterMap.get("uName"));}
		
		if(paraMeterMap.get("sName")!=null){
		addChrUserDetailsPage.enterShortName(paraMeterMap.get("sName"));}
		
		addChrUserDetailsPage.selectUserNamePrefix(1);
		
		if(paraMeterMap.get("subscriberCode")!=null){
		addChrUserDetailsPage.enterSubscriberCode(paraMeterMap.get("subscriberCode"));}

		// Select Status as 'Y' or 'N' if drop-down is available.
		addChrUserDetailsPage.selectStatus("Y");

		// Enter unique external code and MSISDN
		//channelresultMap.put("EXTCODE",UniqueChecker.UC_EXTCODE());
		//channelresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		
		if(paraMeterMap.get("EXTCODE")!=null){
		addChrUserDetailsPage.enterExternalCode(paraMeterMap.get("EXTCODE"));}
		
		if(paraMeterMap.get("MSISDN")!=null){
			channelresultMap.put("MSISDN", paraMeterMap.get("MSISDN"));
			addChrUserDetailsPage.enterMobileNumber(paraMeterMap.get("MSISDN"));}

		//select outlets if exists
		if(paraMeterMap.get("selectOutletSubOutlet").equals("Y")){
		addChrUserDetailsPage.selectOutlet();
		addChrUserDetailsPage.selectSubOutlet();}
			
		if(paraMeterMap.get("contactNo")!=null){
		addChrUserDetailsPage.enterContactNo(paraMeterMap.get("contactNo"));}
		
		if(paraMeterMap.get("address1")!=null){
		addChrUserDetailsPage.enterAddress1(paraMeterMap.get("address1"));}
		
		if(paraMeterMap.get("address2")!=null){
		addChrUserDetailsPage.enterAddress2(paraMeterMap.get("address2"));}
		
		if(paraMeterMap.get("city")!=null){
		addChrUserDetailsPage.enterCity(paraMeterMap.get("city"));}
		
		if(paraMeterMap.get("state")!=null){
		addChrUserDetailsPage.enterState(paraMeterMap.get("state"));}
		
		if(paraMeterMap.get("country")!=null){
		addChrUserDetailsPage.enterCountry(paraMeterMap.get("country"));}

	
		if(paraMeterMap.get("emailID")!=null){
		addChrUserDetailsPage.enterEmailID(paraMeterMap.get("emailID"));}
		
 		if(paraMeterMap.get("paymentType")!=null){
	     addChrUserDetailsPage.selectPaymentType(paraMeterMap.get("paymentType"));}
		
		if(paraMeterMap.get("documentType")!=null){
		
		 addChrUserDetailsPage.selectDocumentType(paraMeterMap.get("documentType"));}
	       
		if(paraMeterMap.get("documentNo")!=null){
			
			 addChrUserDetailsPage.enterDocumentNumber(paraMeterMap.get("documentNo"));}
		
		// Enter Unique LoginID
		if(webAccess.equals("Y") && paraMeterMap.get("LoginID")!=null){
			LoginID = paraMeterMap.get("LoginID");
			channelresultMap.put("LOGIN_ID", paraMeterMap.get("LoginID"));
			addChrUserDetailsPage.enterLoginID(paraMeterMap.get("LoginID"));
		}
		// Assigning Geography
		if(paraMeterMap.get("assignGeography").equals("Y"))
		{
		addChrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String geoGrpahyCode=_masterVO.geoTypeMap.get(geotype)[0];
		String geoRow = _masterVO.geoTypeMap.get(geotype)[2]; //added later on 05September
		addChrUserDetailsPage.assignGeographies1(geoRow,geoGrpahyCode);
		SwitchWindow.backwindow(driver);
		}

		// Assigning Roles for Channel user
		if(paraMeterMap.get("assignRoles").equals("Y"))
		{
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		if(!BTSLUtil.isNullString(grpRole)&&!BTSLUtil.isNullString(sysRole)&&BTSLUtil.isNullString(roleTypeDisp)&&grpRole.equalsIgnoreCase("Y")&&sysRole.equalsIgnoreCase("N")&&roleTypeDisp.equalsIgnoreCase("GROUP"))
		{ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		addChrUserDetailsPage.assignGroupRole(ExcelUtility.getCellData(0, ExcelI.GROUP_ROLE, RowNum));}
		else
		addChrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);
		}
		// Assigning services for Channel user
		if(paraMeterMap.get("assignServices").equals("Y"))
		{
		addChrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);
		}

		// Assigning Products
		if(paraMeterMap.get("assignProducts").equals("Y"))
		{
		addChrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		}
	
		// Assigning Phone Number
		
		if(paraMeterMap.get("assgnPhoneNumber").equals("Y"))
		{
		addChrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);

		addChrUserDetailsPage.assignPhoneNumber1(paraMeterMap.get("MSISDN"), paraMeterMap.get("PIN"));
		SwitchWindow.backwindow(driver);
		}
		addChrUserDetailsPage.assignVoucherType();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignVoucherType1();
		SwitchWindow.backwindow(driver);
		
		
		// Enter Password & Confirm Password
		if(webAccess.equals("Y") && paraMeterMap.get("PASSWORD")!=null){
			channelresultMap.put("PASSWORD", paraMeterMap.get("PASSWORD"));
			addChrUserDetailsPage.enterPassword(paraMeterMap.get("PASSWORD"));
		}
		
		if(paraMeterMap.get("CONFIRMPASSWORD")!=null){
			channelresultMap.put("CONFIRMPASSWORD", paraMeterMap.get("CONFIRMPASSWORD"));
			addChrUserDetailsPage.enterConfirmPassword(paraMeterMap.get("CONFIRMPASSWORD"));
		}
		
		if(paraMeterMap.get("AllowedIP") != null) {
			addChrUserDetailsPage.enterAllowedIPs(paraMeterMap.get("AllowedIP"));
		}
		
		
		if(APPLEVEL.equals("0") || _masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("0")){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
		}
	
	
		addChrUserDetailsPage.clickSaveButton(); // Click Save button on page
		channelresultMap.put("channelErrorMsg", addChrUserDetailsPage.getActualMessage());
		addChrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
													// page
		channelresultMap.put("channelInitiateMsg", addChrUserDetailsPage.getActualMessage());
		
		return channelresultMap;
	}
	
	public HashMap<String, String> channelUserInitiateVoucher(int RowNum, String Domain, String Parent, String Category, String geotype,HashMap<String, String> paraMeterMap, String voucherType) throws InterruptedException{
		this.RowNum=RowNum;
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		String grpRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.GROUP_ROLE_ALLOWED);
		String sysRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_ROLE_ALLOWED);
		String roleTypeDisp=DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHANNEL_USER_ROLE_TYPE_DISPLAY);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		try{webAccess = DBHandler.AccessHandler.webInterface(Category).toUpperCase();}catch(Exception e){Log.writeStackTrace(e);}
		//Operator User Access Implementation by Krishan.
		if(voucherType.equalsIgnoreCase("electronic")) {
		userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.ADD_CHANNEL_USER_ROLECODE,"electronic");
		}
		else if (voucherType.equalsIgnoreCase("physical")){
			userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.ADD_CHANNEL_USER_ROLECODE,"physical");
		}
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickAddChannelUsers();
		try{addChrUserPage.selectDomain(Domain);
		addChrUserPage.selectCategory(Category);
		addChrUserPage.parentCategory(Parent);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		addChrUserPage.selectGeographyDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));}
		catch(Exception e){ Log.writeStackTrace(e);}
		addChrUserPage.clickSubmitBtn();
		addChrUserPage.enterOwnerUser();

		channelresultMap.put("UserName", login.UserNameSequence(driver, "Channel", Domain, "1"));
		addChrUserPage.selectOwnerName(channelresultMap.get("UserName"));

		addChrUserPage.enterParentUser();
		channelresultMap.put("ParentUserName", login.ParentName(driver, "Channel", Domain, Parent));
		addChrUserPage.selectParentName(channelresultMap.get("ParentUserName"));

		addChrUserPage.clickPrntSubmitBtn();

		// Filling the form for channel user
		if(paraMeterMap.get("fName")!=null){
		addChrUserDetailsPage.enterFirstName(paraMeterMap.get("fName"));}

		if(paraMeterMap.get("lName")!=null){
		addChrUserDetailsPage.enterLastName(paraMeterMap.get("lName"));}
		
		if(paraMeterMap.get("uName")!=null){
		addChrUserDetailsPage.enterUserName(paraMeterMap.get("uName"));}
		
		if(paraMeterMap.get("sName")!=null){
		addChrUserDetailsPage.enterShortName(paraMeterMap.get("sName"));}
		
		addChrUserDetailsPage.selectUserNamePrefix(1);
		
		if(paraMeterMap.get("subscriberCode")!=null){
		addChrUserDetailsPage.enterSubscriberCode(paraMeterMap.get("subscriberCode"));}

		// Select Status as 'Y' or 'N' if drop-down is available.
		addChrUserDetailsPage.selectStatus("Y");

		// Enter unique external code and MSISDN
		//channelresultMap.put("EXTCODE",UniqueChecker.UC_EXTCODE());
		//channelresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		
		if(paraMeterMap.get("EXTCODE")!=null){
		addChrUserDetailsPage.enterExternalCode(paraMeterMap.get("EXTCODE"));}
		
		if(paraMeterMap.get("MSISDN")!=null){
			channelresultMap.put("MSISDN", paraMeterMap.get("MSISDN"));
			addChrUserDetailsPage.enterMobileNumber(paraMeterMap.get("MSISDN"));}

		//select outlets if exists
		if(paraMeterMap.get("selectOutletSubOutlet").equals("Y")){
		addChrUserDetailsPage.selectOutlet();
		addChrUserDetailsPage.selectSubOutlet();}
			
		if(paraMeterMap.get("contactNo")!=null){
		addChrUserDetailsPage.enterContactNo(paraMeterMap.get("contactNo"));}
		
		if(paraMeterMap.get("address1")!=null){
		addChrUserDetailsPage.enterAddress1(paraMeterMap.get("address1"));}
		
		if(paraMeterMap.get("address2")!=null){
		addChrUserDetailsPage.enterAddress2(paraMeterMap.get("address2"));}
		
		if(paraMeterMap.get("city")!=null){
		addChrUserDetailsPage.enterCity(paraMeterMap.get("city"));}
		
		if(paraMeterMap.get("state")!=null){
		addChrUserDetailsPage.enterState(paraMeterMap.get("state"));}
		
		if(paraMeterMap.get("country")!=null){
		addChrUserDetailsPage.enterCountry(paraMeterMap.get("country"));}

	
		if(paraMeterMap.get("emailID")!=null){
		addChrUserDetailsPage.enterEmailID(paraMeterMap.get("emailID"));}
		
 		if(paraMeterMap.get("paymentType")!=null){
	     addChrUserDetailsPage.selectPaymentType(paraMeterMap.get("paymentType"));}
		
		if(paraMeterMap.get("documentType")!=null){
		
		 addChrUserDetailsPage.selectDocumentType(paraMeterMap.get("documentType"));}
	       
		if(paraMeterMap.get("documentNo")!=null){
			
			 addChrUserDetailsPage.enterDocumentNumber(paraMeterMap.get("documentNo"));}
		
		// Enter Unique LoginID
		if(webAccess.equals("Y") && paraMeterMap.get("LoginID")!=null){
			LoginID = paraMeterMap.get("LoginID");
			channelresultMap.put("LOGIN_ID", paraMeterMap.get("LoginID"));
			addChrUserDetailsPage.enterLoginID(paraMeterMap.get("LoginID"));
		}
		// Assigning Geography
		if(paraMeterMap.get("assignGeography").equals("Y"))
		{
		addChrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String geoGrpahyCode=_masterVO.geoTypeMap.get(geotype)[0];
		String geoRow = _masterVO.geoTypeMap.get(geotype)[2]; //added later on 05September
		addChrUserDetailsPage.assignGeographies1(geoRow,geoGrpahyCode);
		SwitchWindow.backwindow(driver);
		}

		// Assigning Roles for Channel user
		if(paraMeterMap.get("assignRoles").equals("Y"))
		{
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		if(!BTSLUtil.isNullString(grpRole)&&!BTSLUtil.isNullString(sysRole)&&BTSLUtil.isNullString(roleTypeDisp)&&grpRole.equalsIgnoreCase("Y")&&sysRole.equalsIgnoreCase("N")&&roleTypeDisp.equalsIgnoreCase("GROUP"))
		{ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		addChrUserDetailsPage.assignGroupRole(ExcelUtility.getCellData(0, ExcelI.GROUP_ROLE, RowNum));}
		else
		addChrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);
		}
		// Assigning services for Channel user
		if(paraMeterMap.get("assignServices").equals("Y"))
		{
		addChrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);
		}

		// Assigning Products
		if(paraMeterMap.get("assignProducts").equals("Y"))
		{
		addChrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		}
	
		// Assigning Phone Number
		
		if(paraMeterMap.get("assgnPhoneNumber").equals("Y"))
		{
		addChrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);

		addChrUserDetailsPage.assignPhoneNumber1(paraMeterMap.get("MSISDN"), paraMeterMap.get("PIN"));
		SwitchWindow.backwindow(driver);
		}
		addChrUserDetailsPage.assignVoucherType();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignVoucherType1();
		SwitchWindow.backwindow(driver);
		
		
		// Enter Password & Confirm Password
		if(webAccess.equals("Y") && paraMeterMap.get("PASSWORD")!=null){
			channelresultMap.put("PASSWORD", paraMeterMap.get("PASSWORD"));
			addChrUserDetailsPage.enterPassword(paraMeterMap.get("PASSWORD"));
		}
		
		if(paraMeterMap.get("CONFIRMPASSWORD")!=null){
			channelresultMap.put("CONFIRMPASSWORD", paraMeterMap.get("CONFIRMPASSWORD"));
			addChrUserDetailsPage.enterConfirmPassword(paraMeterMap.get("CONFIRMPASSWORD"));
		}
		
		if(paraMeterMap.get("AllowedIP") != null) {
			addChrUserDetailsPage.enterAllowedIPs(paraMeterMap.get("AllowedIP"));
		}
		
		
		if(APPLEVEL.equals("0") || _masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("0")){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
		}
	
	
		addChrUserDetailsPage.clickSaveButton(); // Click Save button on page
		channelresultMap.put("channelErrorMsg", addChrUserDetailsPage.getActualMessage());
		addChrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
													// page
		channelresultMap.put("channelInitiateMsg", addChrUserDetailsPage.getActualMessage());
		
		return channelresultMap;
	}
	
	
	public HashMap<String, String> channelUserInitiateVoucherNoVoucherType(int RowNum, String Domain, String Parent, String Category, String geotype,HashMap<String, String> paraMeterMap, String voucherType) throws InterruptedException{
		this.RowNum=RowNum;
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		String grpRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.GROUP_ROLE_ALLOWED);
		String sysRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_ROLE_ALLOWED);
		String roleTypeDisp=DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHANNEL_USER_ROLE_TYPE_DISPLAY);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		try{webAccess = DBHandler.AccessHandler.webInterface(Category).toUpperCase();}catch(Exception e){Log.writeStackTrace(e);}
		//Operator User Access Implementation by Krishan.
		if(voucherType.equalsIgnoreCase("electronic")) {
		userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.ADD_CHANNEL_USER_ROLECODE,"electronic");
		}
		else if (voucherType.equalsIgnoreCase("physical")){
			userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.ADD_CHANNEL_USER_ROLECODE,"physical");
		}
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickAddChannelUsers();
		try{addChrUserPage.selectDomain(Domain);
		addChrUserPage.selectCategory(Category);
		addChrUserPage.parentCategory(Parent);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		addChrUserPage.selectGeographyDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));}
		catch(Exception e){ Log.writeStackTrace(e);}
		addChrUserPage.clickSubmitBtn();
		addChrUserPage.enterOwnerUser();

		channelresultMap.put("UserName", login.UserNameSequence(driver, "Channel", Domain, "1"));
		addChrUserPage.selectOwnerName(channelresultMap.get("UserName"));

		addChrUserPage.enterParentUser();
		channelresultMap.put("ParentUserName", login.ParentName(driver, "Channel", Domain, Parent));
		addChrUserPage.selectParentName(channelresultMap.get("ParentUserName"));

		addChrUserPage.clickPrntSubmitBtn();

		// Filling the form for channel user
		if(paraMeterMap.get("fName")!=null){
		addChrUserDetailsPage.enterFirstName(paraMeterMap.get("fName"));}

		if(paraMeterMap.get("lName")!=null){
		addChrUserDetailsPage.enterLastName(paraMeterMap.get("lName"));}
		
		if(paraMeterMap.get("uName")!=null){
		addChrUserDetailsPage.enterUserName(paraMeterMap.get("uName"));}
		
		if(paraMeterMap.get("sName")!=null){
		addChrUserDetailsPage.enterShortName(paraMeterMap.get("sName"));}
		
		addChrUserDetailsPage.selectUserNamePrefix(1);
		
		if(paraMeterMap.get("subscriberCode")!=null){
		addChrUserDetailsPage.enterSubscriberCode(paraMeterMap.get("subscriberCode"));}

		// Select Status as 'Y' or 'N' if drop-down is available.
		addChrUserDetailsPage.selectStatus("Y");

		// Enter unique external code and MSISDN
		//channelresultMap.put("EXTCODE",UniqueChecker.UC_EXTCODE());
		//channelresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		
		if(paraMeterMap.get("EXTCODE")!=null){
		addChrUserDetailsPage.enterExternalCode(paraMeterMap.get("EXTCODE"));}
		
		if(paraMeterMap.get("MSISDN")!=null){
			channelresultMap.put("MSISDN", paraMeterMap.get("MSISDN"));
			addChrUserDetailsPage.enterMobileNumber(paraMeterMap.get("MSISDN"));}

		//select outlets if exists
		if(paraMeterMap.get("selectOutletSubOutlet").equals("Y")){
		addChrUserDetailsPage.selectOutlet();
		addChrUserDetailsPage.selectSubOutlet();}
			
		if(paraMeterMap.get("contactNo")!=null){
		addChrUserDetailsPage.enterContactNo(paraMeterMap.get("contactNo"));}
		
		if(paraMeterMap.get("address1")!=null){
		addChrUserDetailsPage.enterAddress1(paraMeterMap.get("address1"));}
		
		if(paraMeterMap.get("address2")!=null){
		addChrUserDetailsPage.enterAddress2(paraMeterMap.get("address2"));}
		
		if(paraMeterMap.get("city")!=null){
		addChrUserDetailsPage.enterCity(paraMeterMap.get("city"));}
		
		if(paraMeterMap.get("state")!=null){
		addChrUserDetailsPage.enterState(paraMeterMap.get("state"));}
		
		if(paraMeterMap.get("country")!=null){
		addChrUserDetailsPage.enterCountry(paraMeterMap.get("country"));}

	
		if(paraMeterMap.get("emailID")!=null){
		addChrUserDetailsPage.enterEmailID(paraMeterMap.get("emailID"));}
		
 		if(paraMeterMap.get("paymentType")!=null){
	     addChrUserDetailsPage.selectPaymentType(paraMeterMap.get("paymentType"));}
		
		if(paraMeterMap.get("documentType")!=null){
		
		 addChrUserDetailsPage.selectDocumentType(paraMeterMap.get("documentType"));}
	       
		if(paraMeterMap.get("documentNo")!=null){
			
			 addChrUserDetailsPage.enterDocumentNumber(paraMeterMap.get("documentNo"));}
		
		// Enter Unique LoginID
		if(webAccess.equals("Y") && paraMeterMap.get("LoginID")!=null){
			LoginID = paraMeterMap.get("LoginID");
			channelresultMap.put("LOGIN_ID", paraMeterMap.get("LoginID"));
			addChrUserDetailsPage.enterLoginID(paraMeterMap.get("LoginID"));
		}
		// Assigning Geography
		if(paraMeterMap.get("assignGeography").equals("Y"))
		{
		addChrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String geoGrpahyCode=_masterVO.geoTypeMap.get(geotype)[0];
		String geoRow = _masterVO.geoTypeMap.get(geotype)[2]; //added later on 05September
		addChrUserDetailsPage.assignGeographies1(geoRow,geoGrpahyCode);
		SwitchWindow.backwindow(driver);
		}

		// Assigning Roles for Channel user
		if(paraMeterMap.get("assignRoles").equals("Y"))
		{
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		if(!BTSLUtil.isNullString(grpRole)&&!BTSLUtil.isNullString(sysRole)&&BTSLUtil.isNullString(roleTypeDisp)&&grpRole.equalsIgnoreCase("Y")&&sysRole.equalsIgnoreCase("N")&&roleTypeDisp.equalsIgnoreCase("GROUP"))
		{ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		addChrUserDetailsPage.assignGroupRole(ExcelUtility.getCellData(0, ExcelI.GROUP_ROLE, RowNum));}
		else
		addChrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);
		}
		// Assigning services for Channel user
		if(paraMeterMap.get("assignServices").equals("Y"))
		{
		addChrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);
		}

		// Assigning Products
		if(paraMeterMap.get("assignProducts").equals("Y"))
		{
		addChrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		}
	
		// Assigning Phone Number
		
		if(paraMeterMap.get("assgnPhoneNumber").equals("Y"))
		{
		addChrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);

		addChrUserDetailsPage.assignPhoneNumber1(paraMeterMap.get("MSISDN"), paraMeterMap.get("PIN"));
		SwitchWindow.backwindow(driver);
		}
				
		// Enter Password & Confirm Password
		if(webAccess.equals("Y") && paraMeterMap.get("PASSWORD")!=null){
			channelresultMap.put("PASSWORD", paraMeterMap.get("PASSWORD"));
			addChrUserDetailsPage.enterPassword(paraMeterMap.get("PASSWORD"));
		}
		
		if(paraMeterMap.get("CONFIRMPASSWORD")!=null){
			channelresultMap.put("CONFIRMPASSWORD", paraMeterMap.get("CONFIRMPASSWORD"));
			addChrUserDetailsPage.enterConfirmPassword(paraMeterMap.get("CONFIRMPASSWORD"));
		}
		
		if(paraMeterMap.get("AllowedIP") != null) {
			addChrUserDetailsPage.enterAllowedIPs(paraMeterMap.get("AllowedIP"));
		}
		
		
		if(APPLEVEL.equals("0") || _masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("0")){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
		}
	
	
		addChrUserDetailsPage.clickSaveButton(); // Click Save button on page
		channelresultMap.put("channelErrorMsg", addChrUserDetailsPage.getActualMessage());
		addChrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
													// page
		channelresultMap.put("channelInitiateMsg", addChrUserDetailsPage.getActualMessage());
		
		return channelresultMap;
	}
	
	public HashMap<String, String> channelUserInitiateVoucherNoOrderRequest(int RowNum, String Domain, String Parent, String Category, String geotype,HashMap<String, String> paraMeterMap, String voucherType) throws InterruptedException{
		this.RowNum=RowNum;
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		String grpRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.GROUP_ROLE_ALLOWED);
		String sysRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_ROLE_ALLOWED);
		String roleTypeDisp=DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHANNEL_USER_ROLE_TYPE_DISPLAY);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		try{webAccess = DBHandler.AccessHandler.webInterface(Category).toUpperCase();}catch(Exception e){Log.writeStackTrace(e);}
		//Operator User Access Implementation by Krishan.
		if(voucherType.equalsIgnoreCase("electronic")) {
		userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.ADD_CHANNEL_USER_ROLECODE,"electronic");
		}
		else if (voucherType.equalsIgnoreCase("physical")){
			userAccessMap = UserAccess.getUserWithAccessVoucher(RolesI.ADD_CHANNEL_USER_ROLECODE,"physical");
		}
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickAddChannelUsers();
		try{addChrUserPage.selectDomain(Domain);
		addChrUserPage.selectCategory(Category);
		addChrUserPage.parentCategory(Parent);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		addChrUserPage.selectGeographyDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));}
		catch(Exception e){ Log.writeStackTrace(e);}
		addChrUserPage.clickSubmitBtn();
		addChrUserPage.enterOwnerUser();

		channelresultMap.put("UserName", login.UserNameSequence(driver, "Channel", Domain, "1"));
		addChrUserPage.selectOwnerName(channelresultMap.get("UserName"));

		addChrUserPage.enterParentUser();
		channelresultMap.put("ParentUserName", login.ParentName(driver, "Channel", Domain, Parent));
		addChrUserPage.selectParentName(channelresultMap.get("ParentUserName"));

		addChrUserPage.clickPrntSubmitBtn();

		// Filling the form for channel user
		if(paraMeterMap.get("fName")!=null){
		addChrUserDetailsPage.enterFirstName(paraMeterMap.get("fName"));}

		if(paraMeterMap.get("lName")!=null){
		addChrUserDetailsPage.enterLastName(paraMeterMap.get("lName"));}
		
		if(paraMeterMap.get("uName")!=null){
		addChrUserDetailsPage.enterUserName(paraMeterMap.get("uName"));}
		
		if(paraMeterMap.get("sName")!=null){
		addChrUserDetailsPage.enterShortName(paraMeterMap.get("sName"));}
		
		addChrUserDetailsPage.selectUserNamePrefix(1);
		
		if(paraMeterMap.get("subscriberCode")!=null){
		addChrUserDetailsPage.enterSubscriberCode(paraMeterMap.get("subscriberCode"));}

		// Select Status as 'Y' or 'N' if drop-down is available.
		addChrUserDetailsPage.selectStatus("Y");

		// Enter unique external code and MSISDN
		//channelresultMap.put("EXTCODE",UniqueChecker.UC_EXTCODE());
		//channelresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		
		if(paraMeterMap.get("EXTCODE")!=null){
		addChrUserDetailsPage.enterExternalCode(paraMeterMap.get("EXTCODE"));}
		
		if(paraMeterMap.get("MSISDN")!=null){
			channelresultMap.put("MSISDN", paraMeterMap.get("MSISDN"));
			addChrUserDetailsPage.enterMobileNumber(paraMeterMap.get("MSISDN"));}

		//select outlets if exists
		if(paraMeterMap.get("selectOutletSubOutlet").equals("Y")){
		addChrUserDetailsPage.selectOutlet();
		addChrUserDetailsPage.selectSubOutlet();}
			
		if(paraMeterMap.get("contactNo")!=null){
		addChrUserDetailsPage.enterContactNo(paraMeterMap.get("contactNo"));}
		
		if(paraMeterMap.get("address1")!=null){
		addChrUserDetailsPage.enterAddress1(paraMeterMap.get("address1"));}
		
		if(paraMeterMap.get("address2")!=null){
		addChrUserDetailsPage.enterAddress2(paraMeterMap.get("address2"));}
		
		if(paraMeterMap.get("city")!=null){
		addChrUserDetailsPage.enterCity(paraMeterMap.get("city"));}
		
		if(paraMeterMap.get("state")!=null){
		addChrUserDetailsPage.enterState(paraMeterMap.get("state"));}
		
		if(paraMeterMap.get("country")!=null){
		addChrUserDetailsPage.enterCountry(paraMeterMap.get("country"));}

	
		if(paraMeterMap.get("emailID")!=null){
		addChrUserDetailsPage.enterEmailID(paraMeterMap.get("emailID"));}
		
 		if(paraMeterMap.get("paymentType")!=null){
	     addChrUserDetailsPage.selectPaymentType(paraMeterMap.get("paymentType"));}
		
		if(paraMeterMap.get("documentType")!=null){
		
		 addChrUserDetailsPage.selectDocumentType(paraMeterMap.get("documentType"));}
	       
		if(paraMeterMap.get("documentNo")!=null){
			
			 addChrUserDetailsPage.enterDocumentNumber(paraMeterMap.get("documentNo"));}
		
		// Enter Unique LoginID
		if(webAccess.equals("Y") && paraMeterMap.get("LoginID")!=null){
			LoginID = paraMeterMap.get("LoginID");
			channelresultMap.put("LOGIN_ID", paraMeterMap.get("LoginID"));
			addChrUserDetailsPage.enterLoginID(paraMeterMap.get("LoginID"));
		}
		// Assigning Geography
		if(paraMeterMap.get("assignGeography").equals("Y"))
		{
		addChrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String geoGrpahyCode=_masterVO.geoTypeMap.get(geotype)[0];
		String geoRow = _masterVO.geoTypeMap.get(geotype)[2]; //added later on 05September
		addChrUserDetailsPage.assignGeographies1(geoRow,geoGrpahyCode);
		SwitchWindow.backwindow(driver);
		}

		// Assigning Roles for Channel user
		if(paraMeterMap.get("assignRoles").equals("Y"))
		{
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		if(!BTSLUtil.isNullString(grpRole)&&!BTSLUtil.isNullString(sysRole)&&BTSLUtil.isNullString(roleTypeDisp)&&grpRole.equalsIgnoreCase("Y")&&sysRole.equalsIgnoreCase("N")&&roleTypeDisp.equalsIgnoreCase("GROUP"))
		{ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		addChrUserDetailsPage.assignGroupRole(ExcelUtility.getCellData(0, ExcelI.GROUP_ROLE, RowNum));}
		else
		addChrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.uncheckOrderRequest();
		SwitchWindow.backwindow(driver);
		}
		// Assigning services for Channel user
		if(paraMeterMap.get("assignServices").equals("Y"))
		{
		addChrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);
		}

		// Assigning Products
		if(paraMeterMap.get("assignProducts").equals("Y"))
		{
		addChrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		}
	
		// Assigning Phone Number
		
		if(paraMeterMap.get("assgnPhoneNumber").equals("Y"))
		{
		addChrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);

		addChrUserDetailsPage.assignPhoneNumber1(paraMeterMap.get("MSISDN"), paraMeterMap.get("PIN"));
		SwitchWindow.backwindow(driver);
		}
				
		// Enter Password & Confirm Password
		if(webAccess.equals("Y") && paraMeterMap.get("PASSWORD")!=null){
			channelresultMap.put("PASSWORD", paraMeterMap.get("PASSWORD"));
			addChrUserDetailsPage.enterPassword(paraMeterMap.get("PASSWORD"));
		}
		
		if(paraMeterMap.get("CONFIRMPASSWORD")!=null){
			channelresultMap.put("CONFIRMPASSWORD", paraMeterMap.get("CONFIRMPASSWORD"));
			addChrUserDetailsPage.enterConfirmPassword(paraMeterMap.get("CONFIRMPASSWORD"));
		}
		
		if(paraMeterMap.get("AllowedIP") != null) {
			addChrUserDetailsPage.enterAllowedIPs(paraMeterMap.get("AllowedIP"));
		}
		
		
		if(APPLEVEL.equals("0") || _masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("0")){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
		}
	
	
		addChrUserDetailsPage.clickSaveButton(); // Click Save button on page
		channelresultMap.put("channelErrorMsg", addChrUserDetailsPage.getActualMessage());
		addChrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
													// page
		channelresultMap.put("channelInitiateMsg", addChrUserDetailsPage.getActualMessage());
		
		return channelresultMap;
	}
	
	/*
	 * Rejecting Channel user at approval level 1
	 */
	public HashMap<String, String> rejection_approveLevel1ChannelUser(){
		
			//Operator User Access Implementation by Krishan.
			userAccessMap = UserAccess.getUserWithAccess(RolesI.LEVEL1_CHANNEL_USER_APPROVAL_ROLECODE); //Getting User with Access to Approve Channel Users
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			//User Access module ends.
			networkPage.selectNetwork();
			homePage.clickChannelUsers();
			channelUserSubCategories.clickApprovalOneChannelUsers();
			//apprvChannelUsrPage.enterLoginID(LoginID);
			apprvChannelUsrPage.enterMSISDN(channelresultMap.get("MSISDN"));
			apprvChannelUsrPage.clickaprlSubmitBtn();
			apprvChannelUsrPage.clickOkSubmitBtn();
			apprvChannelUsrPage.clickRejectBtn();
			apprvChannelUsrPage.confirmBtn();
			channelresultMap.put("channelReject1Msg", addChrUserDetailsPage.getActualMessage());
	
			return channelresultMap;
}
	
	public HashMap<String, String> rejection_approveLevel2ChannelUser(){
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.LEVEL1_CHANNEL_USER_APPROVAL_ROLECODE); //Getting User with Access to Approve Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickApprovalTwoChannelUsers();
		//apprvChannelUsrPage.enterLoginID(LoginID);
		apprvChannelUsrPage.enterMSISDN(channelresultMap.get("MSISDN"));
		apprvChannelUsrPage.clickaprlSubmitBtn();
		apprvChannelUsrPage.clickOkSubmitBtn();
		
		apprvChannelUsrPage.clickRejectBtn();
		apprvChannelUsrPage.confirmBtn();
		channelresultMap.put("channelReject2Msg", addChrUserDetailsPage.getActualMessage());

		return channelresultMap;
}
	
	//Modify channel user details
	public String modifyChannelUserDetails(){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_CHANNEL_USER_ROLECODE); //Getting User with Access to Approve Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		if(_masterVO.getClientDetail("LOANPROFILE").equals("0")) {
			loanProfileApplicable = DBHandler.AccessHandler.getSystemPreference(CONSTANT.USERWISE_LOAN_ENABLE).toUpperCase();
		}
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickModifyChannelUsers();
		modifyCHNLpage1.enterMSISDN(channelresultMap.get("MSISDN"));
		modifyCHNLpage1.clickSubmitButton();
		modifyCHNLpage2.modifyAddress1("Add1" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyAddress2("Add2" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyCity("City" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyState("State" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyCountry("Country"+ randStr.randomNumeric(2));
		modifyCHNLpage2.modifyEmailID(randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		if(webAccess.equals("Y")){
		modifyCHNLpage2.modifyLoginID(UniqueChecker.UC_LOGINID());}
		modifyCHNLpage2.clickSaveButton();
		modifyCHNLpage2.clickConfirmButton();
		
		String fetchedMessage = addChrUserDetailsPage.getActualMessage();
		
		return fetchedMessage;
	}
	
	//Modify Channel user overloaded
	public String modifyChannelUserDetails(String Category, HashMap<String, String> parameterMap) throws InterruptedException{
		final String methodname = "modifyChannelUserDetails";
		Log.methodEntry(methodname, Category, Arrays.asList(parameterMap));
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_CHANNEL_USER_ROLECODE); //Getting User with Access to Approve Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickModifyChannelUsers();
		modifyCHNLpage1.enterMSISDN(parameterMap.get("searchMSISDN"));
		modifyCHNLpage1.clickSubmitButton();
		String webAccess1 = DBHandler.AccessHandler.webInterface(Category).toUpperCase();
		
 		if(parameterMap.get("outSuspend_chk") != null && parameterMap.get("outSuspend_chk").equals("Y")){
			addChrUserDetailsPage.checkOutSuspend();
		}
		else if(parameterMap.get("outSuspend_chk") != null && parameterMap.get("outSuspend_chk").equals("N")){
			addChrUserDetailsPage.uncheckOutSuspend();
		}
		
		if(parameterMap.get("inSuspend_chk") != null)
			addChrUserDetailsPage.inSuspended(parameterMap.get("inSuspend_chk"));
		
		if(webAccess1.equals("Y") && parameterMap.get("loginChange") != null && parameterMap.get("loginChange").equals("Y")){
		modifyCHNLpage2.modifyLoginID(parameterMap.get("LoginID"));}
		
		if(parameterMap.get("assgnPhoneNumber") != null && parameterMap.get("assgnPhoneNumber").equals("Y"))
		{
		addChrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);

		addChrUserDetailsPage.assignPhoneNumber1(parameterMap.get("MSISDN"), parameterMap.get("PIN"));
		SwitchWindow.backwindow(driver);
		}
		
		modifyCHNLpage2.clickSaveButton();
		modifyCHNLpage2.clickConfirmButton();
		
		String fetchedMessage = addChrUserDetailsPage.getActualMessage();
		
		Log.methodExit(methodname);
		return fetchedMessage;
	}
	
	
	
	
	
	/*
	 * Channel User Initiate with Group Role
	 */
	
	public HashMap<String, String> channelUserInitiateWithGroupRole(int RowNum, String Domain, String Parent, String Category, String geotype ,String roleName) throws InterruptedException {
		final String methodname = "channelUserInitiate";
		Log.methodEntry(methodname, RowNum, Domain, Parent, Category, geotype);
		
		this.RowNum=RowNum;
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		
		APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		/*if (APPLEVEL == null)
			APPLEVEL = DBHandler.AccessHandler.getSystemPreference("USRLEVELAPPROVAL");*/
		
		webAccess = DBHandler.AccessHandler.webInterface(Category).toUpperCase();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickAddChannelUsers();
		addChrUserPage.selectDomain(Domain);
		addChrUserPage.selectCategory(Category);
		addChrUserPage.parentCategory(Parent);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		addChrUserPage.selectGeographyDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));
		addChrUserPage.clickSubmitBtn();
		addChrUserPage.enterOwnerUser();
		channelresultMap.put("UserName", login.UserNameSequence(driver, "Channel", Domain, "1"));
		addChrUserPage.selectOwnerName(channelresultMap.get("UserName"));

		addChrUserPage.enterParentUser();
		channelresultMap.put("ParentUserName", login.ParentName(driver, "Channel", Domain, Parent));
		addChrUserPage.selectParentName(channelresultMap.get("ParentUserName"));

		addChrUserPage.clickPrntSubmitBtn();

		// Filling the form for channel user
		channelresultMap.put("fName","AUTFN" + randStr.randomNumeric(4));
		channelresultMap.put("lName", "AUTLN" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterFirstName(channelresultMap.get("fName"));
		addChrUserDetailsPage.enterLastName(channelresultMap.get("lName"));
		channelresultMap.put("uName",channelresultMap.get("fName") + " " + channelresultMap.get("lName"));
		addChrUserDetailsPage.enterUserName(channelresultMap.get("uName"));
		channelresultMap.put("shortName","AUTSN" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterShortName(channelresultMap.get("shortName"));
		addChrUserDetailsPage.selectUserNamePrefix(1);
		addChrUserDetailsPage.enterSubscriberCode("" + randStr.randomNumeric(6));

		// Select Status as 'Y' or 'N' if drop-down is available.
		addChrUserDetailsPage.selectStatus("Y");

		// Enter unique external code and MSISDN
		channelresultMap.put("EXTCODE",UniqueChecker.UC_EXTCODE());
		channelresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		addChrUserDetailsPage.enterExternalCode(channelresultMap.get("EXTCODE"));
		addChrUserDetailsPage.enterMobileNumber(channelresultMap.get("MSISDN"));

		//select outlets if exists
		addChrUserDetailsPage.selectOutlet();
		addChrUserDetailsPage.selectSubOutlet();
		
		channelresultMap.put("ContactNo", "" + randStr.randomNumeric(6));
		addChrUserDetailsPage.enterContactNo(channelresultMap.get("ContactNo"));
		
		channelresultMap.put("Address1", "Add1" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterAddress1(channelresultMap.get("Address1"));
		
		channelresultMap.put("Address2", "Add2" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterAddress2(channelresultMap.get("Address2"));
		
		channelresultMap.put("City", "City" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterCity(channelresultMap.get("City"));
		
		channelresultMap.put("State", "State" + randStr.randomNumeric(4));
		addChrUserDetailsPage.enterState(channelresultMap.get("State"));
		
		channelresultMap.put("Country", "Country" + randStr.randomNumeric(2));
		addChrUserDetailsPage.enterCountry(channelresultMap.get("Country"));

		channelresultMap.put("Email", randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		addChrUserDetailsPage.enterEmailID(channelresultMap.get("Email"));

		
		// Enter Unique LoginID
		if(webAccess.equals("Y")){
		LoginID = UniqueChecker.UC_LOGINID();
		channelresultMap.put("LOGIN_ID", LoginID);
		addChrUserDetailsPage.enterLoginID(LoginID);
		}
		// Assigning Geography
		addChrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String geoGrpahyCode=_masterVO.geoTypeMap.get(geotype)[0];
		channelresultMap.put("geoGrpahyName",_masterVO.geoTypeMap.get(geotype)[1]);
		String geoRow = _masterVO.geoTypeMap.get(geotype)[2]; //added later on 05September
		addChrUserDetailsPage.assignGeographies1(geoRow,geoGrpahyCode);
		SwitchWindow.backwindow(driver);

		// Assigning Networks
		addChrUserDetailsPage.assignNetwork();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignNetwork1();
		SwitchWindow.backwindow(driver);

		// Assigning Roles for Channel user
		addChrUserDetailsPage.selectGroupRoleRadioButton();
		addChrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		
		boolean groupRoleExist = addChrUserDetailsPage.groupRoleExistenceCheck(roleName);
		if(groupRoleExist==true){
		addChrUserDetailsPage.assignGroupRole(roleName);
		SwitchWindow.backwindow(driver);

		// Assigning services for Channel user
		addChrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);

		// Assigning domains
		addChrUserDetailsPage.assignDomains();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignDomains1();
		SwitchWindow.backwindow(driver);

		// Assigning Products
		addChrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);

		// Assigning Phone Number
		addChrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);
		channelresultMap.put("PIN", _masterVO.getProperty("PIN"));
		addChrUserDetailsPage.assignPhoneNumber1(channelresultMap.get("MSISDN"), channelresultMap.get("PIN"));
		SwitchWindow.backwindow(driver);

		// Enter Password & Confirm Password
		if(webAccess.equals("Y")){
		channelresultMap.put("PASSWORD", _masterVO.getProperty("Password"));
		channelresultMap.put("CONFIRMPASSWORD", _masterVO.getProperty("ConfirmPassword"));
		addChrUserDetailsPage.enterPassword(channelresultMap.get("PASSWORD"));
		addChrUserDetailsPage.enterConfirmPassword(channelresultMap.get("CONFIRMPASSWORD"));}

		if(APPLEVEL.equals("0") || _masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("0")){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			
			apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
			apprvChannelUsrPage.selectComm(0,ExcelI.COMMISSION_PROFILE,RowNum);
			apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
			apprvChannelUsrPage.selectTransferRuleType();
		}
		
		addChrUserDetailsPage.clickSaveButton(); // Click Save button on page
		addChrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
													// page
		channelresultMap.put("channelInitiateMsg", addChrUserDetailsPage.getActualMessage());
		
		}
		else {
		
			channelresultMap.put("channelInitiateMsg","As Group Role is suspended, It is not available to associate with Channel User");
			
		}
		
		Log.methodExit(methodname);
		return channelresultMap;
	}

	

	//Modify channel user details for GroupRole
		public String modifyChannelUserAssignedRoleDetails(String MSISDN) throws InterruptedException{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_CHANNEL_USER_ROLECODE); //Getting User with Access to Approve Channel Users
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			
			networkPage.selectNetwork();
			homePage.clickChannelUsers();
			channelUserSubCategories.clickModifyChannelUsers();
			modifyCHNLpage1.enterMSISDN(MSISDN);
			modifyCHNLpage1.clickSubmitButton();
			// Assigning Roles for Channel user
			modifyCHNLpage2.selectSystemRoleRadioButton();
			addChrUserDetailsPage.assignRoles();
			SwitchWindow.switchwindow(driver);
			addChrUserDetailsPage.assignRoles1();
			SwitchWindow.backwindow(driver);
			if(webAccess.equals("Y")){
				String NewLoginID = UniqueChecker.UC_LOGINID();
			modifyCHNLpage2.modifyLoginID(NewLoginID);}
			modifyCHNLpage2.clickSaveButton();
			modifyCHNLpage2.clickConfirmButton();
			
			String fetchedMessage = addChrUserDetailsPage.getActualMessage();
			
			return fetchedMessage;
		}
		
		public String getErrorMessage() {
			return addChrUserDetailsPage.getActualMessage();
		}
	
		public void changeUserFirstTimePassword(String LoginID,String PASSWORD, String NEWPASSWORD) {
			final String methodname = "changeUserFirstTimePassword";
			Log.methodEntry(methodname);
		
			String autoPwdGenerate = DBHandler.AccessHandler.getSystemPreference("AUTO_PWD_GENERATE_ALLOW").toUpperCase();
			
			if(autoPwdGenerate.equals("FALSE"))	{
				Log.info("Password is not autogenerated");
				new Login().LoginAsUser(driver, LoginID, PASSWORD);
				new ChangePasswordForNewUser(driver).changePassword(PASSWORD, NEWPASSWORD, NEWPASSWORD);
			} else {
				Log.info("Password is autogenerated");
				String autoPassword = DBHandler.AccessHandler.fetchUserPassword(LoginID);
				new Login().LoginAsUser(driver, LoginID, autoPassword);
				new ChangePasswordForNewUser(driver).changePassword(autoPassword, NEWPASSWORD, NEWPASSWORD);
			}
			Log.methodExit(methodname);
		}
		
		public void changeUserFirstTimePIN(String LOGINID, String MSISDN,String PIN,String NEWPIN) {
			final String methodname = "changeUserFirstTimePIN";
			Log.methodEntry(methodname);

			userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGEPIN_ROLECODE); //Getting User with Access to Change First Time PIN
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

			networkPage.selectNetwork();
			homePage.clickChannelUsers();
			channelUserSubCategories.clickChangePIN();
			
			//Only for SHA when PIN is autogenerated as PIN needs to be updated prior to entering MSISDN on GUI
			if(DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase().equals("TRUE")&&
					DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("SHA"))
			{DBHandler.AccessHandler.fetchUserPIN(LOGINID,MSISDN);}
			
			changeUsrPIN.enterMSISDNandRemarks(MSISDN);
			
			if(DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase().equals("FALSE")) {
				changeUsrPIN.changePIN(PIN, NEWPIN, NEWPIN);
			} else {
				Log.info("PIN is autogenerated");
				autoPIN = DBHandler.AccessHandler.fetchUserPIN(LOGINID,MSISDN);	
				changeUsrPIN.changePIN(autoPIN, NEWPIN, NEWPIN);
			}
			
			Log.methodExit(methodname);
		}
		
		
		public HashMap<String, String> approveLevel1_ChannelUser_AssignCommisison(String commissionProfile){
			final String methodname = "approveLevel1_ChannelUser_AssignCommisison";
			Log.methodEntry(methodname);
					
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
					
			//Operator User Access Implementation by Krishan.
			userAccessMap = UserAccess.getUserWithAccess(RolesI.LEVEL1_CHANNEL_USER_APPROVAL_ROLECODE); //Getting User with Access to Approve Channel Users
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			//User Access module ends.
			networkPage.selectNetwork();
			homePage.clickChannelUsers();
			channelUserSubCategories.clickApprovalOneChannelUsers();
			//apprvChannelUsrPage.enterLoginID(LoginID);
			apprvChannelUsrPage.enterMSISDN(channelresultMap.get("MSISDN"));
			apprvChannelUsrPage.clickaprlSubmitBtn();
			apprvChannelUsrPage.clickOkSubmitBtn();
						
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
						
			if (_masterVO.getClientDetail("CHANNELUSERINITIATE_VER").equalsIgnoreCase("1")) {
				apprvChannelUsrPage.selectGrade(0,ExcelI.GRADE,RowNum);
				apprvChannelUsrPage.selectSpeicificComm(commissionProfile);
				apprvChannelUsrPage.selectTCP(0,ExcelI.NA_TCP_NAME,RowNum);
				apprvChannelUsrPage.selectTransferRuleType();
			}
			apprvChannelUsrPage.approveBtn();
			apprvChannelUsrPage.confirmBtn();
			if(APPLEVEL.equals("1"))
				channelresultMap.put("channelApproveMsg", addChrUserDetailsPage.getActualMessage());
			else if(APPLEVEL.equals("2"))
				channelresultMap.put("channelApprovelevel1Msg", addChrUserDetailsPage.getActualMessage());
							
			Log.methodExit(methodname);
			return channelresultMap;				
		}
		
		
	public String modifyChannelUserLoanProfile(int rowNum, String LoanProfile){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODIFY_CHANNEL_USER_ROLECODE); //Getting User with Access to Approve Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String loanProfileApplicable = DBHandler.AccessHandler.getSystemPreference("USERWISE_LOAN_ENABLE").toUpperCase();
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickModifyChannelUsers();
		modifyCHNLpage1.enterMSISDN(channelresultMap.get("MSISDN"));
		modifyCHNLpage1.clickSubmitButton();
		modifyCHNLpage2.modifyAddress1("Add1" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyAddress2("Add2" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyCity("City" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyState("State" + randStr.randomNumeric(4));
		modifyCHNLpage2.modifyCountry("Country"+ randStr.randomNumeric(2));
		modifyCHNLpage2.modifyEmailID(randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		if(webAccess.equals("Y")){
			modifyCHNLpage2.modifyLoginID(UniqueChecker.UC_LOGINID());}
		if(loanProfileApplicable.equals("true")){apprvChannelUsrPage.modifyLoanProfile1(LoanProfile);}
		else{Log.info("Loan Profile dropdown not available, preference value : "+loanProfileApplicable);}
		modifyCHNLpage2.clickSaveButton();
		modifyCHNLpage2.clickConfirmButton();

		String fetchedMessage = addChrUserDetailsPage.getActualMessage();

		return fetchedMessage;
	}
}
