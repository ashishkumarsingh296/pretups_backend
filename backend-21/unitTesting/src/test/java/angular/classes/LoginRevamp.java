package angular.classes;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.pageobjects.loginpages.LoginPage;

import angular.pageobjects.loginpages.LoginPageRevamp;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/*
 * This class is created to Login to Application from Operator User or Channel User
 */
public class LoginRevamp {

	public String getUsernameofUser(String UserType, String LoginID) {
		String username = null ;
		String loginid ;
		/* Loading Properties File */
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		 if (UserType.equals("ChannelUser")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			for(int i=1; i<=rowCount; i++)
			{
				loginid = ExcelUtility.getCellData(7, ExcelI.LOGIN_ID, i) ;
				if(loginid.equals(LoginID))
				{
					username = ExcelUtility.getCellData(6, ExcelI.LOGIN_ID, i) ;
					break ;
				} }

			}
			Log.info("Username Found as: " + username) ;
		return username ;
	}

	public String getUserLoginID(WebDriver driver, String UserType, String LoginUser) {
		String LOGINID=null; String PASSWORD=null;
		/* Loading Properties File */
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		//ExcelUtility.setExcelFile(MasterSheetPath, "Operator Users Hierarchy");
		if (UserType.equals("Operator")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("Login ID Found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password Found as: " + PASSWORD);}

		else if (UserType.equals("ChannelUser")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j); //introduced on 30/08/2017
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("Login ID Found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password Found as: " + PASSWORD);}
		return LOGINID;
	}

	
	public String UserLogin(WebDriver driver, String UserType, String LoginUser) {
		String LOGINID=null; String PASSWORD=null;
		/* Loading Properties File */
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		//ExcelUtility.setExcelFile(MasterSheetPath, "Operator Users Hierarchy");
		if (UserType.equals("Operator")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("Login ID Found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password Found as: " + PASSWORD);}
		
		else if (UserType.equals("ChannelUser")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j); //introduced on 30/08/2017
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("Login ID Found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password Found as: " + PASSWORD);}
		
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.MASTER_SHEET_NAME);
			String LANGUAGE=_masterVO.getMasterValue(MasterI.LANGUAGE);
			String URL=_masterVO.getMasterValue(MasterI.WEB_URL);
			driver.get(URL);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			LoginPageRevamp loginPage = new LoginPageRevamp(driver);
			try {
				loginPage.selectLanguage(LANGUAGE);
			} catch (Exception e) {
				Log.info("Language selector does not exists");
			}
			loginPage.enterLoginID(LOGINID);
			loginPage.enterPassword(PASSWORD);
			loginPage.clickLoginButton();
			String ErrorMessage = loginPage.getErrorMessage();
			//loginPage.clickReloginButton();
			return ErrorMessage;
	}

	public HashMap<String, String> getUserLoginDetails(String UserType, String LoginUser) {
		HashMap<String, String> loginDetails = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		if (UserType.equals("Operator")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				String LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			loginDetails.put("LoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j));
			loginDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, j));
			loginDetails.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, j));
			loginDetails.put("Password", ExcelUtility.getCellData(0, ExcelI.PASSWORD, j));
		} else if (UserType.equals("ChannelUser")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				String LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j); //introduced on 30/08/2017
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			loginDetails.put("LoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j));
			loginDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, j));
			loginDetails.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, j));
			loginDetails.put("Password", ExcelUtility.getCellData(0, ExcelI.PASSWORD, j));
		}
			return loginDetails;
	}

	
	public String UserLogin(WebDriver driver, String UserType, String Parent, String LoginUser) {
		String LOGINID = null, PASSWORD = null, USERNAME;
		/* Loading Properties File */
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		if (UserType.equals("Operator")) {
			/* Reading Excel File */
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();

			while (j <= rowCount) {
				String ParentName = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, j);
				String ParentCategoryCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, j);
				String LoginUserName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				String LoginUserCategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,j);
				if ((ParentName.equals(Parent) || ParentCategoryCode.equals(Parent))  && (LoginUserName.equals(LoginUser)||LoginUserCategoryCode.equals(LoginUser))) {
					break;
				}
				j++;
			}

			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("LoginID found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password found as: " + PASSWORD);
		}

		else if (UserType.equals("ChannelUser")) {
			/* Reading Excel File */
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String ParentName = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, j);
				String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				if (ParentName.equals(Parent) && CategoryName.equals(LoginUser)) {
					break;
				}
				j++;
			}

			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("LoginID found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password found as: " + PASSWORD);
			USERNAME = ExcelUtility.getCellData(0, ExcelI.USER_NAME, j);
			Log.info("Username found as: " + USERNAME);
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.MASTER_SHEET_NAME);
		String LANGUAGE=_masterVO.getMasterValue(MasterI.LANGUAGE);
		String URL=_masterVO.getMasterValue(MasterI.WEB_URL);
		driver.get(URL);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LoginPageRevamp loginPage = new LoginPageRevamp(driver);
		try {
			loginPage.selectLanguage(LANGUAGE);
		} catch (Exception e) {
			Log.info("Language selector not found");
		}
		loginPage.enterLoginID(LOGINID);
		loginPage.enterPassword(PASSWORD);
		loginPage.clickLoginButton();
			String ErrorMessage = loginPage.getErrorMessage();
			//loginPage.clickReloginButton();
			return ErrorMessage;
	}

	
	public String UserNameSequence(WebDriver driver, String UserType, String Domain, String Sequence) {
		String UserName = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		if (UserType.equals("Channel")) {
			/* Reading Excel File */
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String DomainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, j);
				String SequenceNumber = ExcelUtility.getCellData(0, ExcelI.SEQUENCE_NO, j);
				if (DomainName.equals(Domain) && SequenceNumber.equals(Sequence)) {
					break;
				}
				j++;
			}

			UserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, j);
			Log.info("User Name found as: " + UserName);
		}
		return UserName;
	}

	
	public String ParentName(WebDriver driver, String UserType, String Domain, String Category) {

		String ParentName = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		if (UserType.equals("Channel")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);}
		else if(UserType.equals("ExtgwChannel")){
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET);
		}
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String DomainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, j);
				String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				//String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,j); // commented as found of no use: identified in some cases when weblogin is not provided
				if (DomainName.equals(Domain) && CategoryName.equals(Category) /*&& (!LoginID.equals("") && LoginID!=null)*/) {
					break;
				}
				j++;
			}

			ParentName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, j);
			Log.info("ParentName found as: " + ParentName);
		
		return ParentName;
	}
	
	public String LoginAsUser(WebDriver driver, String LOGINID,String PASSWORD) {
		/* Loading Properties File */

		String LANGUAGE=_masterVO.getMasterValue(MasterI.LANGUAGE);
		String URL=_masterVO.getMasterValue(MasterI.WEB_URL);
		driver.get(URL);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LoginPageRevamp loginPage = new LoginPageRevamp(driver);
		try {
			loginPage.selectLanguage(LANGUAGE);
		} catch (Exception e) {
			Log.info("Language selector not found");
		}
		loginPage.enterLoginID(LOGINID);
		loginPage.enterPassword(PASSWORD);
		loginPage.clickLoginButton();
		String ErrorMessage = loginPage.getErrorMessage();
		//loginPage.clickReloginButton();
		return ErrorMessage;
	}

	public HashMap<String, String> getUserLoginDetails(String UserType, String Parent, String LoginUser) {
	
		/* Loading Properties File */
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		boolean isExist = false;
		HashMap<String, String> loginDetails = new HashMap<String, String>();
		if (UserType.equals("Operator")) {
			/* Reading Excel File */
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();

			while (j <= rowCount) {
				String ParentName = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, j);
				String ParentCategoryCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, j);
				String LoginUserName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				String LoginUserCategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,j);
				if ((ParentName.equals(Parent) || ParentCategoryCode.equals(Parent))  && (LoginUserName.equals(LoginUser)||LoginUserCategoryCode.equals(LoginUser))) {
					break;
				}
				j++;
			}
			
			loginDetails.put("LoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j));
			loginDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, j));
			loginDetails.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, j));
			loginDetails.put("Password", ExcelUtility.getCellData(0, ExcelI.PASSWORD, j));

		}

		else if (UserType.equals("ChannelUser")) {
			/* Reading Excel File */
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String ParentName = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, j);
				String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				if (ParentName.equals(Parent) && CategoryName.equals(LoginUser)) {
					isExist=true;
					break;
				}
				j++;
			}
			if(isExist){
			loginDetails.put("LoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j));
			loginDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, j));
			loginDetails.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, j));
			loginDetails.put("Password", ExcelUtility.getCellData(0, ExcelI.PASSWORD, j));
			}
			else {
				loginDetails = getUserLoginDetails(UserType, LoginUser);
			}

		}
		
		return loginDetails;
	}

	

	public String doUserLogin(WebDriver driver, String UserType, String Parent, String LoginUser) {
		String LOGINID=null; String PASSWORD=null;
		/* Loading Properties File */
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		if (UserType.equals("Operator")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("Login ID Found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password Found as: " + PASSWORD);}
		
		else if (UserType.equals("ChannelUser")) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int j = 0;
			int rowCount = ExcelUtility.getRowCount();
			while (j <= rowCount) {
				String UserCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j); //introduced on 30/08/2017
				if (UserCategory.equals(LoginUser)&&(LOGINID!=null && !LOGINID.equals(""))) {
					break;
				}
				j++;
			}
			LOGINID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
			Log.info("Login ID Found as: " + LOGINID);
			PASSWORD = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
			Log.info("Password Found as: " + PASSWORD);}
		
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.MASTER_SHEET_NAME);
			String LANGUAGE=_masterVO.getMasterValue(MasterI.LANGUAGE);
			String URL=_masterVO.getMasterValue(MasterI.WEB_URL);
			driver.get(URL);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			LoginPageRevamp loginPage = new LoginPageRevamp(driver);
			try {
				loginPage.selectLanguage(LANGUAGE);
			} catch (Exception e) {
				Log.info("Language selector does not exists");
			}
			loginPage.enterLoginID(LOGINID);
			loginPage.enterPassword(PASSWORD);
			loginPage.clickLoginButton();
			if(loginPage.isErrorMessageVisible()){
				String ErrorMessage = loginPage.getErrorMessage();
				//	loginPage.clickLoginButton();
					return ErrorMessage;
			} else {
				return "";
			}
			
	}
}
