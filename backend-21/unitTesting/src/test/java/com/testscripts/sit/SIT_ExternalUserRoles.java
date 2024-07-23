package com.testscripts.sit;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.ExternalUserRolesReportSpring;
import com.Features.mapclasses.OperatorToChannelMap;
import com.Features.mapclasses.ZeroBalCounterSummRptMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportssuser.ChannelUserOperatorUserRolesSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.PaginationHandlerSpring;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_ExternalUserRoles extends BaseTest {
	static boolean TestCaseCounter = false;
	static String domainCode;

	@Test
	// Spring module or not
	public void a_ExternalUserRoles() {
		final String methodname = "a_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		ChannelUserOperatorUserRolesSpring extuserrole = new ChannelUserOperatorUserRolesSpring(
				driver);
		OperatorToChannelMap o2cmap = new OperatorToChannelMap();
		Login login1 = new Login();
		boolean spring = false;
		String domainName = o2cmap.getOperatorToChannelMap(
				_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
		domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
		currentNode = test
				.createNode("To verify that External user roles screen visible in spring.");
		currentNode.assignCategory("SIT");

		Object[][] login = DBHandler.AccessHandler
				.getChnlUserDetailsForRolecode(RolesI.EXT_USER_REPRT,
						domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]),
				String.valueOf(login[0][1]));
		new ChannelAdminHomePage(driver).clickChannelReportsUser();
		extuserrole.clickExternalUsersReportlink();
		try {
			spring = driver.findElement(By.xpath("//span[@id='servertime']"))
					.isDisplayed();
			if (spring)
				currentNode.log(Status.PASS, "The module is spring.");
			else if (!spring)
				currentNode.log(Status.FAIL, "Module is not spring.");
		} catch (Exception e) {
			Log.info("Error while looking for spring module.");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();
		}
	}
	
	@Test
	public void b_ExternalUserRoles() {
		final String methodname = "b_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		ChannelUserOperatorUserRolesSpring extuserrole = new ChannelUserOperatorUserRolesSpring(
				driver);

		currentNode = test
				.createNode("To check that proper error messages appears when channel domain is not selected.");
		currentNode.assignCategory("SIT");
		String[] data = { "AUTcg8Oj", "", "", "ALL", "ALL", "Channel Category" };
		String expected1 = MessagesDAO
				.getLabelByKey("pretups.c2s.reports.external.dom.req");
		new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		String actual1 = extuserrole.fetcherrormessage("domainList");

		Validator.messageCompare(actual1, expected1);

	}
	@Test
	public void c_ExternalUserRoles() {
		final String methodname = "c_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		ChannelUserOperatorUserRolesSpring extuserrole = new ChannelUserOperatorUserRolesSpring(
				driver);

		currentNode = test
				.createNode("To check that proper error messages appears when user name is not selected.");
		currentNode.assignCategory("SIT");
		String[] data = { "AUTcg8Oj", "Distributor", "Retailer", "", "ALL", "Channel Category" };
		String expected1 = MessagesDAO
				.getLabelByKey("pretups.c2s.reports.external.user.req");
		new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		String actual1 = extuserrole.fetcherrormessage("userName");

		Validator.messageCompare(actual1, expected1);

	}
	@Test
	public void d_ExternalUserRoles() {
		final String methodname = "d_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		ChannelUserOperatorUserRolesSpring extuserrole = new ChannelUserOperatorUserRolesSpring(
				driver);

		currentNode = test
				.createNode("To check that proper error messages appears when user status is not selected.");
		currentNode.assignCategory("SIT");
		String[] data = { "AUTcg8Oj", "Distributor", "ALL", "ALL", "", "Channel Category" };

		String expected1 = MessagesDAO
				.getLabelByKey("pretups.c2s.reports.external.st.req");
		
		new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		String actual1 = extuserrole.fetcherrormessage("userStatus");
		
		Validator.messageCompare(actual1 , expected1 );

	}
	@Test
	public void e_ExternalUserRoles() {
		final String methodname = "e_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		ChannelUserOperatorUserRolesSpring extuserrole = new ChannelUserOperatorUserRolesSpring(
				driver);

		currentNode = test
				.createNode("To check that proper error messages appears when parent category list is not selected.");
		currentNode.assignCategory("SIT");
		String[] data = { "AUTcg8Oj", "", "", "ALL", "ALL", "Channel Category" };
		String expected1 = MessagesDAO
				.getLabelByKey("pretups.c2s.reports.external.cat.req");
		new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		String actual1 = extuserrole.fetcherrormessage("parentCategoryList");

		Validator.messageCompare(actual1, expected1);

	}

	@Test
	public void f_ExternalUserRoles() {
		final String methodname = "f_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		

		currentNode = test
				.createNode("To check that submit button is disabled when no value is entered.");
		currentNode.assignCategory("SIT");
		String[] data = { "", "", "", "", "", "" };
		
		
		HashMap<String, String> result =new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		Validator.messageCompare(result.get("submitEnabled"), "false");
	}
	@Test
	public void g_ExternalUserRoles() {
		final String methodname = "g_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		

		currentNode = test
				.createNode("To check that submit button is enabled when  values are entered correctly.");
		currentNode.assignCategory("SIT");
		String[] data = { "AUTcg8Oj", "Distributor", "ALL", "ALL", "ALL", "Channel Category" };
		
		
		HashMap<String, String> result =new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		Validator.messageCompare(result.get("submitEnabled"), "false");
	}
	@Test
	public void h_ExternalUserRoles() {
		final String methodname = "h_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		

		currentNode = test
				.createNode("To check that inet report button is disabled when no value is entered.");
		currentNode.assignCategory("SIT");
		String[] data = { "", "", "", "", "", "" };
		
		
		HashMap<String, String> result =new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		Validator.messageCompare(result.get("inetEnabled"), "false");
	}
	@Test
	public void i_ExternalUserRoles() {
		final String methodname = "i_ExternalUserRoles";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}
		

		currentNode = test
				.createNode("To check that inet button is enabled when  values are entered correctly.");
		currentNode.assignCategory("SIT");
		String[] data = { "AUTcg8Oj", "Distributor", "ALL", "ALL", "ALL", "Channel Category" };
		
		
		HashMap<String, String> result =new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		Validator.messageCompare(result.get("inetEnabled"), "false");
	}
	
	@Test
	public void j_ExternalUserRoles() throws IOException, ParseException{
		final String methodname = "j_ExternalUserRoles";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]External User Roles Report");
			TestCaseCounter = true;
		}	
		OperatorToChannelMap o2cmap = new OperatorToChannelMap();
		NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
		currentNode = test.createNode("To validate transactions of External User Roles Report.");
		currentNode.assignCategory("SIT");	
		String dateformat = "dd/MM/yy";
		String actual = null;
		String currentDate = DBHandler.AccessHandler.getCurrentServerDate(dateformat);
		String fromDate = "15/02/18";
		String toDate =  "15/02/18";
		Date date1=new SimpleDateFormat("dd/MM/yy").parse(fromDate); 
		java.sql.Date sqlStartDate = new java.sql.Date(date1.getTime()); 
		Date date2=new SimpleDateFormat("dd/MM/yy").parse(toDate); 
		java.sql.Date sqlEndDate = new java.sql.Date(date1.getTime()); 
		String geography_name = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_GEOGRAPHY");
		String networkCode = _masterVO.getMasterValue("Network Code");
		ZeroBalCounterSummRptMap zerobj=new ZeroBalCounterSummRptMap();
		String userID = DBHandler.AccessHandler.getUserId(zerobj.getzerobalMap("userName"));
		String loggedInUserID = DBHandler.AccessHandler.getUserId(zerobj.getzerobalMap("loggedInUserName"));
		String parentCat = DBHandler.AccessHandler.getCategoryCode(zerobj.getzerobalMap("parentcategorycode"));
		String geodomainCode = zerobj.getzerobalMap("geozone");
		String userDomainCode = DBHandler.AccessHandler.getDomainCode(zerobj.getzerobalMap("domainName"));
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		int rowNum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, geography_name);
		
		String[] columnNames = {"msisdn"};
		String[][] zerbaltrans = DBHandler.AccessHandler.getExternalRolesRpt( networkCode, userID, loggedInUserID, parentCat, userDomainCode, geodomainCode, columnNames);
		String[] data = { "AUTcg8Oj", "Distributor", "ALL", "ALL", "ALL", "Channel Category" };
	
		new ExternalUserRolesReportSpring(driver).checkExternaluserRoleReport("channel",
				domainCode, data);
		ArrayList<String> txnList = new PaginationHandlerSpring().getTxnIDFromEachpageforExternaluserRoles(driver);
		int dbcount = 0;
		if(zerbaltrans != null)
		dbcount = zerbaltrans.length;
		int gridcount  = txnList.size();
		
		Log.info("<p>Count fetched from DB: "+dbcount+"<br> Count fetched from grid: "+gridcount+"</p>");
		boolean match=false;
		if(dbcount == 0)
			match = true;
		else if (dbcount == gridcount) {
			for (int iterator = 0; iterator < dbcount; iterator++) {
					Log.info("Trying to match: "+zerbaltrans[iterator][0]);
					if (txnList.contains(zerbaltrans[iterator][0])) {
						Log.info("Matched successfully.");
						match = true;}
					else {Log.info("Not Matched.");
						match = false;}
				}
			}
		
		
		if(match){actual="true";}
		else{actual="false";}
		Validator.messageCompare(actual, "true");
	}



	
	
	
	
	
	
	
	
	
	
}
