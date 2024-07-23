package com.Features;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.loginpages.ChangePINForNewUser;
import com.pageobjects.loginpages.ChangePasswordForNewUser;
import com.pageobjects.superadminpages.addoperatoruser.AddOperatorUserDetailsPage;
import com.pageobjects.superadminpages.addoperatoruser.AddOperatorUserPage;
import com.pageobjects.superadminpages.addoperatoruser.ApproveOperatorUsersPage;
import com.pageobjects.superadminpages.addoperatoruser.ModifyOperatorUserPage1;
import com.pageobjects.superadminpages.addoperatoruser.ModifyOperatorUserPage2;
import com.pageobjects.superadminpages.addoperatoruser.ViewOperatorUserPage1;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class OperatorUser extends BaseTest {
	
	SuperAdminHomePage homePage;
	AddOperatorUserPage addOptrUserPage;
	AddOperatorUserDetailsPage addOptrUserDetailsPage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randStr;
	ApproveOperatorUsersPage approveOperatorUser;
	ChangePasswordForNewUser changenewpwd;
	OperatorUsersSubCategories operatorSubLink;
	ChannelUsersSubCategories channelUserSubCategories;
	ChannelAdminHomePage chnlhomepage;
	ChangePINForNewUser changeUsrPIN;
	AddChannelUserDetailsPage addChrUserDetailsPage;
	ModifyOperatorUserPage1 modifyOptPage1;
	ModifyOperatorUserPage2 modifyOptPage2;
	ViewOperatorUserPage1 viewOptUserPage1;
	
	public String LoginID;
	public String autoPassword = null;
	public int RowNum;
	public String NEWPASSWORD;
	HashMap<String, String> optresultMap;
	static String NewPin;
	public String autoPIN = null;
	WebDriver driver=null;
	public String pinChangeRequired;
	
	int Nation_Voucher;
	
	public OperatorUser(WebDriver driver) {
		this.driver=driver;
		homePage = new SuperAdminHomePage(driver);
		addOptrUserPage = new AddOperatorUserPage(driver);
		addOptrUserDetailsPage = new AddOperatorUserDetailsPage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		approveOperatorUser = new ApproveOperatorUsersPage(driver);
		changenewpwd = new ChangePasswordForNewUser(driver);
		operatorSubLink = new OperatorUsersSubCategories(driver);
		optresultMap = new HashMap<String, String>();
		chnlhomepage= new ChannelAdminHomePage(driver);
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		changeUsrPIN = new ChangePINForNewUser(driver);
		addChrUserDetailsPage= new AddChannelUserDetailsPage(driver);
		modifyOptPage1 = new ModifyOperatorUserPage1(driver);
		modifyOptPage2 = new ModifyOperatorUserPage2(driver);
		viewOptUserPage1 = new ViewOperatorUserPage1(driver);
		Nation_Voucher = Integer.parseInt(_masterVO.getClientDetail("Nation_Voucher"));
	}

	/**
	 * Initiate Operator User
	 * @param ParentUser
	 * @param LoginUser
	 * @return HashMap -> optresultMap
	 * @throws InterruptedException
	 */
	
	public HashMap<String, String> operatorUserInitiate(String ParentUser, String LoginUser) throws InterruptedException {
		final String methodname = "operatorUserInitiate";
		Log.methodEntry(methodname, ParentUser, LoginUser);

		// Login with SuperAdmin, click link for add operator user and submit.
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickAddOperatorUsers();
		addOptrUserPage.selectCategory(LoginUser);
		addOptrUserPage.clickSubmitButton();

		// Filling the form for operator user
		optresultMap.put("firstName", "AUTFN" + randStr.randomNumeric(4));
		optresultMap.put("lastName", "AUTLN" + randStr.randomNumeric(4));
		optresultMap.put("UserName", optresultMap.get("firstName") + " " + optresultMap.get("lastName"));

		addOptrUserDetailsPage.enterFirstName(optresultMap.get("firstName"));
		addOptrUserDetailsPage.enterLastName(optresultMap.get("lastName"));
		addOptrUserDetailsPage.enterUserName(optresultMap.get("UserName"));
		addOptrUserDetailsPage.enterShortName("AUTSN" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.selectUserNamePrefix(1);
		addOptrUserDetailsPage.enterSubscriberCode(""+ randStr.randomNumeric(6));

		// Select Status as 'Y' or 'N' if drop-down is available.
		addOptrUserDetailsPage.selectStatus("Y");
		optresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		optresultMap.put("EXTCODE", UniqueChecker.UC_EXTCODE());
		// Enter unique external code and MSISDN
		addOptrUserDetailsPage.enterExternalCode(optresultMap.get("EXTCODE"));
		addOptrUserDetailsPage.enterMobileNumber(optresultMap.get("MSISDN"));

		addOptrUserDetailsPage.enterContactNo("" + randStr.randomNumeric(6));
		addOptrUserDetailsPage.selectDivision(); // Select Division
		addOptrUserDetailsPage.selectDepartment(); // Select Department
		addOptrUserDetailsPage.enterAddress1("Add1" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterAddress2("Add2" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterCity("City" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterState("State" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterCountry("Country"
				+ randStr.randomNumeric(2));

		addOptrUserDetailsPage.enterEmailID(randStr.randomAlphaNumeric(5)
				.toLowerCase() + "@mail.com");

		// Enter Unique LoginID
		LoginID = UniqueChecker.UC_LOGINID();
		addOptrUserDetailsPage.enterLoginID(LoginID);
		optresultMap.put("LOGINID",LoginID);
		
		// Assigning Geography
		addOptrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String[] column=new String[]{ExcelI.PARENT_NAME,ExcelI.CATEGORY_NAME};
		String[] values=new String[]{ParentUser,LoginUser};
		int combinationAtRow = ExtentI.combinationExistAtRow(column, values, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String geography = ExtentI.fetchValuefromDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.GRPH_DOMAIN_TYPE, combinationAtRow);
		if(geography.equals("NW"))
			addOptrUserDetailsPage.assignGeographies1();
		else
			{String[] ncolumn = new String[]{ExcelI.GRPH_DOMAIN_TYPE};
			String[] nvalues = new String[]{geography};
			int grphRow =ExtentI.combinationExistAtRow(ncolumn, nvalues, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
			String grphTypeName = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET, ExcelI.GRPH_DOMAIN_TYPE_NAME, grphRow);
			int grphRow1 =ExtentI.combinationExistAtRow(new String[]{ExcelI.DOMAIN_TYPE_NAME}, new String[]{grphTypeName}, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
			String grphCode = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, ExcelI.DOMAIN_CODE, grphRow1);
			addOptrUserDetailsPage.assignGeographies2(grphCode.toUpperCase());
			}
		SwitchWindow.backwindow(driver);

		// Assigning Networks
		addOptrUserDetailsPage.assignNetwork();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignNetwork1();
		SwitchWindow.backwindow(driver);

		// Assigning Roles for Operator user
		addOptrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);

		// Assigning domains
		addOptrUserDetailsPage.assignDomains();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignDomains1();
		SwitchWindow.backwindow(driver);

		// Assigning Products
		addOptrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		
		// Assigning services for Channel user
		addOptrUserDetailsPage.assignVouchers();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignVouchers1();
		SwitchWindow.backwindow(driver);

		// Assigning Phone Number
		addOptrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);
		//String PIN = _masterVO.getProperty("PIN");
		optresultMap.put("PIN", _masterVO.getProperty("PIN"));
		addOptrUserDetailsPage.assignPhoneNumber1(optresultMap.get("MSISDN"),optresultMap.get("PIN"));
		SwitchWindow.backwindow(driver);

		// Assigning services for Channel user
		addOptrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);
		
		addChrUserDetailsPage.assignVoucherType();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignVoucherType1();
		SwitchWindow.backwindow(driver);
		
		if (Nation_Voucher == 1) {
			addChrUserDetailsPage.assignVoucherSegment();
			SwitchWindow.switchwindow(driver);
			addChrUserDetailsPage.assignVoucherSegment1();
			SwitchWindow.backwindow(driver);
		}
		
		// Enter Password & Confirm Password
		String PASSWORD = _masterVO.getProperty("Password");
		String CONFIRMPASSWORD = _masterVO.getProperty("ConfirmPassword");
		addOptrUserDetailsPage.enterPassword(PASSWORD);
		addOptrUserDetailsPage.enterConfirmPassword(CONFIRMPASSWORD);

		addOptrUserDetailsPage.clickSaveButton(); // Click Save button on page
		addOptrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
		
		
		optresultMap.put("PASSWORD", PASSWORD);
		optresultMap.put("initiateMsg", addOptrUserDetailsPage.getActualMessage());		

		Log.methodExit(methodname);
		return optresultMap;
	}
	
	
	public HashMap<String, String> operatorUserInitiateVMS(String ParentUser, String LoginUser) throws InterruptedException {
		final String methodname = "operatorUserInitiate";
		Log.methodEntry(methodname, ParentUser, LoginUser);

		// Login with SuperAdmin, click link for add operator user and submit.
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetworkVMS();
		homePage.clickOperatorUsers();
		operatorSubLink.clickAddOperatorUsers();
		addOptrUserPage.selectCategory(LoginUser);
		addOptrUserPage.clickSubmitButton();

		// Filling the form for operator user
		optresultMap.put("firstName", "AUTFN" + randStr.randomNumeric(4));
		optresultMap.put("lastName", "AUTLN" + randStr.randomNumeric(4));
		optresultMap.put("UserName", optresultMap.get("firstName") + " " + optresultMap.get("lastName"));

		addOptrUserDetailsPage.enterFirstName(optresultMap.get("firstName"));
		addOptrUserDetailsPage.enterLastName(optresultMap.get("lastName"));
		addOptrUserDetailsPage.enterUserName(optresultMap.get("UserName"));
		addOptrUserDetailsPage.enterShortName("AUTSN" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.selectUserNamePrefix(1);
		addOptrUserDetailsPage.enterSubscriberCode(""+ randStr.randomNumeric(6));

		// Select Status as 'Y' or 'N' if drop-down is available.
		addOptrUserDetailsPage.selectStatus("Y");
		optresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		optresultMap.put("EXTCODE", UniqueChecker.UC_EXTCODE());
		// Enter unique external code and MSISDN
		addOptrUserDetailsPage.enterExternalCode(optresultMap.get("EXTCODE"));
		addOptrUserDetailsPage.enterMobileNumber(optresultMap.get("MSISDN"));

		addOptrUserDetailsPage.enterContactNo("" + randStr.randomNumeric(6));
		addOptrUserDetailsPage.selectDivision(); // Select Division
		addOptrUserDetailsPage.selectDepartment(); // Select Department
		addOptrUserDetailsPage.enterAddress1("Add1" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterAddress2("Add2" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterCity("City" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterState("State" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterCountry("Country"
				+ randStr.randomNumeric(2));

		addOptrUserDetailsPage.enterEmailID(randStr.randomAlphaNumeric(5)
				.toLowerCase() + "@mail.com");

		// Enter Unique LoginID
		LoginID = UniqueChecker.UC_LOGINID();
		addOptrUserDetailsPage.enterLoginID(LoginID);
		optresultMap.put("LOGINID",LoginID);
		
		// Assigning Geography
		addOptrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String[] column=new String[]{ExcelI.PARENT_NAME,ExcelI.CATEGORY_NAME};
		String[] values=new String[]{ParentUser,LoginUser};
		int combinationAtRow = ExtentI.combinationExistAtRow(column, values, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String geography = ExtentI.fetchValuefromDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.GRPH_DOMAIN_TYPE, combinationAtRow);
		if(geography.equals("NW"))
			addOptrUserDetailsPage.assignGeographies1();
		else
			{String[] ncolumn = new String[]{ExcelI.GRPH_DOMAIN_TYPE};
			String[] nvalues = new String[]{geography};
			int grphRow =ExtentI.combinationExistAtRow(ncolumn, nvalues, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
			String grphTypeName = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET, ExcelI.GRPH_DOMAIN_TYPE_NAME, grphRow);
			int grphRow1 =ExtentI.combinationExistAtRow(new String[]{ExcelI.DOMAIN_TYPE_NAME}, new String[]{grphTypeName}, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
			String grphCode = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, ExcelI.DOMAIN_CODE, grphRow1);
			addOptrUserDetailsPage.assignGeographies2(grphCode.toUpperCase());
			}
		SwitchWindow.backwindow(driver);

		// Assigning Networks
		addOptrUserDetailsPage.assignNetwork();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignNetwork1();
		SwitchWindow.backwindow(driver);

		// Assigning Roles for Operator user
		addOptrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);

		// Assigning domains
		addOptrUserDetailsPage.assignDomains();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignDomains1();
		SwitchWindow.backwindow(driver);

		// Assigning Products
		addOptrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		
		// Assigning services for Channel user
		addOptrUserDetailsPage.assignVouchers();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignVouchers1();
		SwitchWindow.backwindow(driver);

		// Assigning Phone Number
		addOptrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);
		//String PIN = _masterVO.getProperty("PIN");
		optresultMap.put("PIN", _masterVO.getProperty("PIN"));
		addOptrUserDetailsPage.assignPhoneNumber1(optresultMap.get("MSISDN"),optresultMap.get("PIN"));
		SwitchWindow.backwindow(driver);

		// Assigning services for Channel user
		addOptrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);
		
		addChrUserDetailsPage.assignVoucherType();
		SwitchWindow.switchwindow(driver);
		addChrUserDetailsPage.assignVoucherType1();
		SwitchWindow.backwindow(driver);
		
		if (Nation_Voucher == 1) {
			addChrUserDetailsPage.assignVoucherSegment();
			SwitchWindow.switchwindow(driver);
			addChrUserDetailsPage.assignVoucherSegment1();
			SwitchWindow.backwindow(driver);
		}
		
		// Enter Password & Confirm Password
		String PASSWORD = _masterVO.getProperty("Password");
		String CONFIRMPASSWORD = _masterVO.getProperty("ConfirmPassword");
		addOptrUserDetailsPage.enterPassword(PASSWORD);
		addOptrUserDetailsPage.enterConfirmPassword(CONFIRMPASSWORD);

		addOptrUserDetailsPage.clickSaveButton(); // Click Save button on page
		addOptrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
		
		
		optresultMap.put("PASSWORD", PASSWORD);
		optresultMap.put("initiateMsg", addOptrUserDetailsPage.getActualMessage());		

		Log.methodExit(methodname);
		return optresultMap;
	}

	/**
	 * Approve the initiated operator user.
	 * @param ParentUser
	 * @return HashMap optresultMap
	 */
	
	public HashMap<String, String> approveUser(String ParentUser) {
		final String methodname = "approveUser";
		Log.methodEntry(methodname, ParentUser);

		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");

		if (APPLEVEL.equals("1")) {
			login.UserLogin(driver, "Operator", ParentUser);
			networkPage.selectNetwork();
			homePage.clickOperatorUsers();
			operatorSubLink.clickApproveOperatorUsers();
			approveOperatorUser.enterLoginID(LoginID);
			approveOperatorUser.clickaprlSubmitBtn();
			approveOperatorUser.clickOkSubmitBtn();
			approveOperatorUser.approveBtn();
			approveOperatorUser.confirmBtn();
			optresultMap.put("approveMsg", addOptrUserDetailsPage.getActualMessage());
			homePage.clickLogout();
		} else
			Log.info("Approval for Operator user is not required");
		
		Log.methodExit(methodname);
		return optresultMap;
	}

	public HashMap<String, String> approveUserVMS(String ParentUser) {
		final String methodname = "approveUser";
		Log.methodEntry(methodname, ParentUser);

		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");

		if (APPLEVEL.equals("1")) {
			login.UserLogin(driver, "Operator", ParentUser);
			networkPage.selectNetworkVMS();
			homePage.clickOperatorUsers();
			operatorSubLink.clickApproveOperatorUsers();
			approveOperatorUser.enterLoginID(LoginID);
			approveOperatorUser.clickaprlSubmitBtn();
			approveOperatorUser.clickOkSubmitBtn();
			approveOperatorUser.approveBtn();
			approveOperatorUser.confirmBtn();
			optresultMap.put("approveMsg", addOptrUserDetailsPage.getActualMessage());
			homePage.clickLogout();
		} else
			Log.info("Approval for Operator user is not required");
		
		Log.methodExit(methodname);
		return optresultMap;
	}
	
	/**
	 * Change password at first Login attempt
	 * @param ParentUser
	 * @param LoginUser
	 * @return
	 */
	
	public HashMap<String, String> changeUserFirstTimePassword() {
		final String methodname = "changeUserFirstTimePassword";
		Log.methodEntry(methodname);
		
		String PASSWORD = optresultMap.get("PASSWORD");
		
		NEWPASSWORD = _masterVO.getProperty("NewPassword");
		
		if (DBHandler.AccessHandler.getSystemPreference("AUTO_PWD_GENERATE_ALLOW").toUpperCase().equals("FALSE")) {
			Log.info("Password field exist");
			login.LoginAsUser(driver, LoginID, PASSWORD);
			changenewpwd.changePassword(PASSWORD, NEWPASSWORD, NEWPASSWORD);
		} else {
			Log.info("Password field not exist, password is autogenerated");
			autoPassword = DBHandler.AccessHandler.fetchUserPassword(LoginID);
			login.LoginAsUser(driver, LoginID, autoPassword);
			changenewpwd.changePassword(autoPassword, NEWPASSWORD, NEWPASSWORD);
		}
		//networkPage.selectNetwork();
		pinChangeRequired=String.valueOf(addOptrUserDetailsPage.w1);
		optresultMap.put("PASSWORD", NEWPASSWORD);
		
		Log.methodExit(methodname);
		return optresultMap;
	}
	
	public HashMap<String, String> changeUserFirstTimePIN() {
		final String methodname = "changeUserFirstTimePIN";
		Log.methodEntry(methodname);
		
		if(addOptrUserDetailsPage.w1==true) {
			if(DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase().equals("TRUE")&&
					DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("SHA"))
			{DBHandler.AccessHandler.fetchUserPIN(LoginID,"");}
			login.LoginAsUser(driver, LoginID, NEWPASSWORD);
			networkPage.selectNetwork();
			chnlhomepage.clickChannelUsers();
			channelUserSubCategories.clickChangeSelfPIN();
			
			NewPin=_masterVO.getProperty("NewPIN");
			String ConfirmPin=_masterVO.getProperty("ConfirmPIN");
			
			if(DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase().equals("FALSE")) {
				String Pin=optresultMap.get("PIN");
				changeUsrPIN.changePIN(Pin, NewPin, ConfirmPin);
				optresultMap.put("PIN",ConfirmPin);
			} else {
				Log.info("PIN is autogenerated");
				autoPIN = DBHandler.AccessHandler.fetchUserPIN(LoginID,"");	
				changeUsrPIN.changePIN(autoPIN, NewPin, ConfirmPin);
				optresultMap.put("PIN",ConfirmPin);
			}
			optresultMap.put("changePINMsg", addChrUserDetailsPage.getActualMessage());
			//chnlhomepage.clickLogout();
		} else {
			Log.info("PIN is not required");
		}
		
		Log.methodExit(methodname);
		return optresultMap;	
	}

	
	
	
	/**
	 * Write data to DataProvider sheet
	 * @param RowNum
	 */
	
	public void writeOperatorUserData(int RowNum){
		this.RowNum=RowNum;
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		
		ExcelUtility.setCellData(0, ExcelI.LOGIN_ID, RowNum, LoginID);
		ExcelUtility.setCellData(0, "PASSWORD", RowNum, NEWPASSWORD);
		ExcelUtility.setCellData(0, ExcelI.MSISDN, RowNum,optresultMap.get("MSISDN"));
		if(addOptrUserDetailsPage.w1==true)
		{ExcelUtility.setCellData(0,"PIN",RowNum,optresultMap.get("PIN"));}


	}
	
	public void writeOperatorUserDataVMS(int RowNum){
		this.RowNum=RowNum;
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_NETWORK_ADMIN_HIERARCHY_SHEET);
		
		ExcelUtility.setCellData(0, ExcelI.LOGIN_ID, RowNum, LoginID);
		ExcelUtility.setCellData(0, "PASSWORD", RowNum, NEWPASSWORD);
		ExcelUtility.setCellData(0, ExcelI.MSISDN, RowNum,optresultMap.get("MSISDN"));
		if(addOptrUserDetailsPage.w1==true)
		{ExcelUtility.setCellData(0,"PIN",RowNum,optresultMap.get("PIN"));}
		ExcelUtility.setCellData(0, ExcelI.NETWORK_CODE, RowNum,_masterVO.getMasterValue(MasterI.OTHER_NETWORK_CODE_FOR_VMS));


	}
	
	/**
	 * Modify Operator user details
	 */
	
	public String modifyOperatorDetails(String ParentUser, String Category){
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickModifyOperatorUsers();
		modifyOptPage1.selectCategory(Category);
		modifyOptPage1.enterOperatorName(optresultMap.get("UserName"));
		modifyOptPage1.clickSubmitButton();
		modifyOptPage2.modifyAddress1("Add1" + randStr.randomNumeric(4));
		modifyOptPage2.modifyAddress2("Add2" + randStr.randomNumeric(4));
		modifyOptPage2.modifyCity("City" + randStr.randomNumeric(4));
		modifyOptPage2.modifyState("State" + randStr.randomNumeric(4));
		modifyOptPage2.modifyCountry("Country"+ randStr.randomNumeric(2));
		modifyOptPage2.modifyEmailID(randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		modifyOptPage2.modifyLoginID(UniqueChecker.UC_LOGINID());
		modifyOptPage2.clickModifyButton();
		modifyOptPage2.clickConfirmButton();
		
		String fetchedMessage = addOptrUserDetailsPage.getActualMessage();
		
		return fetchedMessage;
	}
	
	/**
	 * View Operator User
	 */
	public void viewOperatorUser(String ParentUser, String Category){
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickViewOperatorUsers();
		viewOptUserPage1.selectCategory(Category);
		viewOptUserPage1.enterOperatorName(optresultMap.get("UserName"));
		viewOptUserPage1.clickSubmitButton();
		viewOptUserPage1.clickBackButton();
	}
	
	/**
	 * View Self Details
	 */
	
	public void viewSelfDetails(String LOGINID, String PASSWORD){
		login.LoginAsUser(driver, LOGINID, PASSWORD);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickViewSelfDetails();
	}
	
	//SIT Flow Starts
	public HashMap<String, String> operatorUserInitiate_SIT(String ParentUser, String LoginUser, HashMap<String, String> optMap)
			throws InterruptedException {
		
		//HashMap<String, String> optresultMap = new HashMap<String, String>();
		
		Log.startTestCase("Operator User Creation");
		//test = extent.createTest("Operator User: " + LoginUser, "To verify that" + ParentUser + "is able to create" + LoginUser);

		// Login with SuperAdmin, click link for add operator user and submit.
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickAddOperatorUsers();
		addOptrUserPage.selectCategory(LoginUser);
		addOptrUserPage.clickSubmitButton();

		addOptrUserDetailsPage.enterFirstName(optMap.get("firstName"));
		addOptrUserDetailsPage.enterLastName(optMap.get("lastName"));
		addOptrUserDetailsPage.enterUserName(optMap.get("UserName"));
		optresultMap.put("UserName", optMap.get("UserName"));
		addOptrUserDetailsPage.enterShortName(optMap.get("shortName"));
		addOptrUserDetailsPage.selectUserNamePrefix(1);
		addOptrUserDetailsPage.enterSubscriberCode(optMap.get("subscriberCode"));

		// Select Status as 'Y' or 'N' if drop-down is available.
		addOptrUserDetailsPage.selectStatus("Y");
		
		// Enter unique external code and MSISDN
		addOptrUserDetailsPage.enterExternalCode(optMap.get("EXTCODE"));
		addOptrUserDetailsPage.enterMobileNumber(optMap.get("MSISDN"));

		addOptrUserDetailsPage.enterContactNo(optMap.get("contactNo"));
		if(optMap.get("DIVISION").equals("Y")){
		addOptrUserDetailsPage.selectDivision(); // Select Division
		addOptrUserDetailsPage.selectDepartment(); // Select Department
		}
		addOptrUserDetailsPage.enterAddress1(optMap.get("address1"));
		addOptrUserDetailsPage.enterAddress2(optMap.get("address2"));
		addOptrUserDetailsPage.enterCity(optMap.get("city"));
		addOptrUserDetailsPage.enterState(optMap.get("state"));
		addOptrUserDetailsPage.enterCountry(optMap.get("country"));

		addOptrUserDetailsPage.enterEmailID(optMap.get("email"));

		// Enter Unique LoginID
		addOptrUserDetailsPage.enterLoginID(optMap.get("LOGINID"));
		optresultMap.put("LOGINID",LoginID);
		
		// Assigning Geography
		if(optMap.get("AssignGeography").equals("Y")){
		addOptrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignGeographies1();
		SwitchWindow.backwindow(driver);
		}
		
		// Assigning Networks
        if(optMap.get("AssignNetwork").equals("Y")){
		addOptrUserDetailsPage.assignNetwork();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignNetwork1();
		SwitchWindow.backwindow(driver);
        }
        
		// Assigning Roles for Operator user
		addOptrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);

		// Assigning domains
		if(optMap.get("AssignDomain").equals("Y")){
		addOptrUserDetailsPage.assignDomains();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignDomains1();
		SwitchWindow.backwindow(driver);
		}
		
		// Assigning Products
		if(optMap.get("AssignProduct").equals("Y")){
		addOptrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		}
		
		// Assigning services for Channel user
				addOptrUserDetailsPage.assignServices();
				SwitchWindow.switchwindow(driver);
				addOptrUserDetailsPage.assignServices1();
				SwitchWindow.backwindow(driver);
		
		// Assigning Phone Number
		if(optMap.get("AssignPhoneNumber").equals("Y")){
		addOptrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);
		//String PIN = _masterVO.getProperty("PIN");
		optresultMap.put("PIN", _masterVO.getProperty("PIN"));
		addOptrUserDetailsPage.assignPhoneNumber1(optMap.get("MSISDN"),optMap.get("PIN"));
		SwitchWindow.backwindow(driver);
		}
		
		// Enter Password & Confirm Password
		String PASSWORD = _masterVO.getProperty("Password");
		addOptrUserDetailsPage.enterPassword(optMap.get("PASSWORD"));
		addOptrUserDetailsPage.enterConfirmPassword(optMap.get("CONFIRMPASSWORD"));

		addOptrUserDetailsPage.clickSaveButton(); // Click Save button on page	
        addOptrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
		
		
		optresultMap.put("PASSWORD", PASSWORD);
		optresultMap.put("initiateMsg", addOptrUserDetailsPage.getActualMessage());		

		return optresultMap;
	}
	
	/**
	 * Modify Operator user details
	 */
	
	public String modifyOperatorDetails_SIT(String ParentUser, String Category, HashMap<String, String> optMap, String type){
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickModifyOperatorUsers();
		if(!Category.equals("")){
		modifyOptPage1.selectCategory(Category);
		modifyOptPage1.enterOperatorName(optMap.get("UserName"));
		}
		modifyOptPage1.clickSubmitButton();
		modifyOptPage2.modifyAddress1(optMap.get("address1"));
		modifyOptPage2.modifyAddress2(optMap.get("address2"));
		modifyOptPage2.modifyCity(optMap.get("city"));
		modifyOptPage2.modifyState(optMap.get("state"));
		modifyOptPage2.modifyCountry(optMap.get("country"));
		modifyOptPage2.modifyEmailID(optMap.get("email"));
		modifyOptPage2.modifyLoginID(UniqueChecker.UC_LOGINID());
        if(type.equalsIgnoreCase("Modify"))
		modifyOptPage2.clickModifyButton();
        else if(type.equalsIgnoreCase("Delete"))
        	modifyOptPage2.clickDeleteButton();
		modifyOptPage2.clickConfirmButton();
		
		String fetchedMessage = addOptrUserDetailsPage.getActualMessage();
		
		return fetchedMessage;
	}
	
	public HashMap<String, String> approveUser_SIT(String ParentUser, HashMap<String, String> optMap, String type) {

		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");

		if (APPLEVEL.equals("1")) {
			login.UserLogin(driver, "Operator", ParentUser);
			networkPage.selectNetwork();
			homePage.clickOperatorUsers();
			operatorSubLink.clickApproveOperatorUsers();
			approveOperatorUser.enterLoginID(optMap.get("LOGINID"));
			approveOperatorUser.clickaprlSubmitBtn();
			approveOperatorUser.clickOkSubmitBtn();
			if(type.equalsIgnoreCase("Approve"))
			approveOperatorUser.approveBtn();
			else if(type.equalsIgnoreCase("Reject"))
				approveOperatorUser.rejectBtn();
			approveOperatorUser.confirmBtn();
			optresultMap.put("approveMsg", addOptrUserDetailsPage.getActualMessage());
			homePage.clickLogout();
		} else {
			Log.info("Approval for Operator user is not required");
		}
		
		//optresultMap.put("approveMsg", addOptrUserDetailsPage.getActualMessage());
		
		//homePage.clickLogout();
		return optresultMap;
	}
	
	public void viewOperatorUser_SIT(String ParentUser, String Category, HashMap<String, String> optMap){
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickViewOperatorUsers();
		if(!Category.equals("")){
		viewOptUserPage1.selectCategory(Category);
		viewOptUserPage1.enterOperatorName(optMap.get("UserName"));
		}
		viewOptUserPage1.clickSubmitButton();
		viewOptUserPage1.clickBackButton();
	}
	
	public HashMap<String, String> customoperatorUserInitiate(String ParentUser, String LoginUser, String sheetName) throws InterruptedException {
		final String methodname = "operatorUserInitiate";
		Log.methodEntry(methodname, ParentUser, LoginUser);

		String voucherType = null;
		if(sheetName.equals(ExcelI.PHY_OPERATOR_USERS_HIERARCHY_SHEET)){
			voucherType = DBHandler.AccessHandler.getVoucherType("P");
		}
		else if(sheetName.equals(ExcelI.ELC_OPERATOR_USERS_HIERARCHY_SHEET)){
			voucherType = DBHandler.AccessHandler.getVoucherType("E");
		}
		
		// Login with SuperAdmin, click link for add operator user and submit.
		login.UserLogin(driver, "Operator", ParentUser);
		networkPage.selectNetwork();
		homePage.clickOperatorUsers();
		operatorSubLink.clickAddOperatorUsers();
		addOptrUserPage.selectCategory(LoginUser);
		addOptrUserPage.clickSubmitButton();

		// Filling the form for operator user
		optresultMap.put("firstName", "AUTFN" + randStr.randomNumeric(4));
		optresultMap.put("lastName", "AUTLN" + randStr.randomNumeric(4));
		optresultMap.put("UserName", optresultMap.get("firstName") + " " + optresultMap.get("lastName"));

		addOptrUserDetailsPage.enterFirstName(optresultMap.get("firstName"));
		addOptrUserDetailsPage.enterLastName(optresultMap.get("lastName"));
		addOptrUserDetailsPage.enterUserName(optresultMap.get("UserName"));
		addOptrUserDetailsPage.enterShortName("AUTSN" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.selectUserNamePrefix(1);
		addOptrUserDetailsPage.enterSubscriberCode(""+ randStr.randomNumeric(6));

		// Select Status as 'Y' or 'N' if drop-down is available.
		addOptrUserDetailsPage.selectStatus("Y");
		optresultMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		optresultMap.put("EXTCODE", UniqueChecker.UC_EXTCODE());
		// Enter unique external code and MSISDN
		addOptrUserDetailsPage.enterExternalCode(optresultMap.get("EXTCODE"));
		addOptrUserDetailsPage.enterMobileNumber(optresultMap.get("MSISDN"));

		addOptrUserDetailsPage.enterContactNo("" + randStr.randomNumeric(6));
		addOptrUserDetailsPage.selectDivision(); // Select Division
		addOptrUserDetailsPage.selectDepartment(); // Select Department
		addOptrUserDetailsPage.enterAddress1("Add1" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterAddress2("Add2" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterCity("City" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterState("State" + randStr.randomNumeric(4));
		addOptrUserDetailsPage.enterCountry("Country"
				+ randStr.randomNumeric(2));

		addOptrUserDetailsPage.enterEmailID(randStr.randomAlphaNumeric(5)
				.toLowerCase() + "@mail.com");

		// Enter Unique LoginID
		LoginID = UniqueChecker.UC_LOGINID();
		addOptrUserDetailsPage.enterLoginID(LoginID);
		optresultMap.put("LOGINID",LoginID);
		
		// Assigning Geography
		addOptrUserDetailsPage.assignGeographies();
		SwitchWindow.switchwindow(driver);
		String[] column=new String[]{ExcelI.PARENT_NAME,ExcelI.CATEGORY_NAME};
		String[] values=new String[]{ParentUser,LoginUser};

		int combinationAtRow = ExtentI.combinationExistAtRow(column, values, sheetName);
		String geography = ExtentI.fetchValuefromDataProviderSheet(sheetName, ExcelI.GRPH_DOMAIN_TYPE, combinationAtRow);
		if(geography.equals("NW"))
			addOptrUserDetailsPage.assignGeographies1();
		else
			{String[] ncolumn = new String[]{ExcelI.GRPH_DOMAIN_TYPE};
			String[] nvalues = new String[]{geography};
			int grphRow =ExtentI.combinationExistAtRow(ncolumn, nvalues, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
			String grphTypeName = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET, ExcelI.GRPH_DOMAIN_TYPE_NAME, grphRow);
			int grphRow1 =ExtentI.combinationExistAtRow(new String[]{ExcelI.DOMAIN_TYPE_NAME}, new String[]{grphTypeName}, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
			String grphCode = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, ExcelI.DOMAIN_CODE, grphRow1);
			addOptrUserDetailsPage.assignGeographies2(grphCode.toUpperCase());
			}
		SwitchWindow.backwindow(driver);

		// Assigning Networks
		addOptrUserDetailsPage.assignNetwork();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignNetwork1();
		SwitchWindow.backwindow(driver);

		// Assigning Roles for Operator user
		addOptrUserDetailsPage.assignRoles();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignRoles1();
		SwitchWindow.backwindow(driver);

		// Assigning domains
		addOptrUserDetailsPage.assignDomains();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignDomains1();
		SwitchWindow.backwindow(driver);

		// Assigning Products
		addOptrUserDetailsPage.assignProducts();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignProducts1();
		SwitchWindow.backwindow(driver);
		
		// Assigning services for Channel user
		addOptrUserDetailsPage.assignVouchers();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.selectTypeVoucher(voucherType);
		SwitchWindow.backwindow(driver);

		// Assigning Phone Number
		addOptrUserDetailsPage.assignPhoneNumber();
		SwitchWindow.switchwindow(driver);
		//String PIN = _masterVO.getProperty("PIN");
		optresultMap.put("PIN", _masterVO.getProperty("PIN"));
		addOptrUserDetailsPage.assignPhoneNumber1(optresultMap.get("MSISDN"),optresultMap.get("PIN"));
		SwitchWindow.backwindow(driver);

		// Assigning services for Channel user
		addOptrUserDetailsPage.assignServices();
		SwitchWindow.switchwindow(driver);
		addOptrUserDetailsPage.assignServices1();
		SwitchWindow.backwindow(driver);
		
		if (Nation_Voucher == 1) {
			addChrUserDetailsPage.assignVoucherSegment();
			SwitchWindow.switchwindow(driver);
			addChrUserDetailsPage.assignVoucherSegment1();
			SwitchWindow.backwindow(driver);
		}
		
		// Enter Password & Confirm Password
		String PASSWORD = _masterVO.getProperty("Password");
		String CONFIRMPASSWORD = _masterVO.getProperty("ConfirmPassword");
		addOptrUserDetailsPage.enterPassword(PASSWORD);
		addOptrUserDetailsPage.enterConfirmPassword(CONFIRMPASSWORD);

		addOptrUserDetailsPage.clickSaveButton(); // Click Save button on page
		addOptrUserDetailsPage.clickConfirmButton(); // Click Confirm button on
		
		
		optresultMap.put("PASSWORD", PASSWORD);
		optresultMap.put("initiateMsg", addOptrUserDetailsPage.getActualMessage());		

		Log.methodExit(methodname);
		return optresultMap;
	}
	
	public void writeOperatorUserDatacustom(int RowNum,String sheetname){
		this.RowNum=RowNum;
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, sheetname);
		
		ExcelUtility.setCellData(0, ExcelI.LOGIN_ID, RowNum, LoginID);
		ExcelUtility.setCellData(0, "PASSWORD", RowNum, NEWPASSWORD);
		ExcelUtility.setCellData(0, ExcelI.MSISDN, RowNum,optresultMap.get("MSISDN"));
		if(addOptrUserDetailsPage.w1==true)
		{ExcelUtility.setCellData(0,"PIN",RowNum,optresultMap.get("PIN"));}

		
	}
}
