package com.testscripts.sit;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.StaffSelfC2CReportsSpringFeatures;
import com.Features.mapclasses.OperatorToChannelMap;
import com.Features.mapclasses.ZeroBalCounterSummRptMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.c2c.staffselfc2creports.StaffSelfC2CReportsSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.PaginationHandlerSpring;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_StaffSelfC2CReportSpring extends BaseTest {
	static boolean TestCaseCounter = false;
	static String domainCode;

	@Test
	// Spring module or not
	public void a_StaffSelfC2C() throws InterruptedException {
		final String methodname = " a_StaffSelfC2C";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}
		StaffSelfC2CReportsSpring staffselfC2C = new StaffSelfC2CReportsSpring(
				driver);
		OperatorToChannelMap o2cmap = new OperatorToChannelMap();
		Login login1 = new Login();
		boolean spring = false;
		String domainName = o2cmap.getOperatorToChannelMap(
				_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
		domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
		currentNode = test
				.createNode("To verify that Staff Self C2C Reports screen visible in spring.");
		currentNode.assignCategory("SIT");

		Object[][] login = DBHandler.AccessHandler
				.getChnlUserDetailsForRolecode(RolesI.STAFF_SELF_REPRT,
						domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]),
				String.valueOf(login[0][1]));
	
		new ChannelAdminHomePage(driver).clickChannelReportsC2C();
		new ChannelAdminHomePage(driver).clickChannelReportsC2C();
		staffselfC2C.clickStaffSelfC2CReportlink();
		
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
	public void b_StaffSelfC2C() throws InterruptedException {
		final String methodname = "b_StaffSelfC2C";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}
		StaffSelfC2CReportsSpring staffselfC2C = new StaffSelfC2CReportsSpring(
				driver);

		currentNode = test
				.createNode("To check that proper error messages appears when Transfer Sub type is not selected.");
		currentNode.assignCategory("SIT");
		String[] data = { "", "01/01/18", "02/02/18", "", ""};
		String expected1 = MessagesDAO
				.getLabelByKey("pretups.c2c.reports.trftype.reqd");
		new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		String actual1 = staffselfC2C.fetcherrormessage("subtype");

		Validator.messageCompare(actual1, expected1);

	}
	@Test
	public void c_StaffSelfC2C() throws InterruptedException {
		final String methodname = "c_StaffSelfC2C";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}
		StaffSelfC2CReportsSpring staffselfC2C = new StaffSelfC2CReportsSpring(
				driver);


		currentNode = test
				.createNode("To check that proper error messages appears when from date is not selected.");
		currentNode.assignCategory("SIT");
		String[] data = { "ALL", "", "02/02/18", "", ""};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2c.reports.fromdate.reqd");
		new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		String actual1 = staffselfC2C.fetcherrormessage("fromDate");

		Validator.messageCompare(actual1, expected1);

	}
	@Test
	public void d_StaffSelfC2C() throws InterruptedException {
		final String methodname = "d_StaffSelfC2C";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}
		StaffSelfC2CReportsSpring staffselfC2C = new StaffSelfC2CReportsSpring(
				driver);

		currentNode = test
				.createNode("To check that proper error messages appears when to date is not selected.");
		currentNode.assignCategory("SIT");
		String[] data = { "ALL", "02/02/18", "", "", ""};

		String expected1 = MessagesDAO
				.getLabelByKey("pretups.c2c.reports.todate.reqd");
		
		new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		String actual1 = staffselfC2C.fetcherrormessage("toDate");
		
		Validator.messageCompare(actual1 , expected1 );

	}
	
	@Test
	public void e_StaffSelfC2C() throws InterruptedException{
		final String methodname = "e_StaffSelfC2C";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}	
		StaffSelfC2CReportsSpring staffselfC2C = new StaffSelfC2CReportsSpring(
				driver);
		NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
		String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
		currentNode = test.createNode("To check that proper error message appears when From Date entered is greater than current date.");
		currentNode.assignCategory("SIT");	
		String[] data = { "ALL", nhomepage.addDaysToCurrentDate(currentDate, 1), "", "", ""};
		
		String expected = MessagesDAO.getLabelByKey("pretups.staff.btsl.error.msg.fromdatebeforecurrentdate");
		new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		String actual = staffselfC2C.fetcherrormessage("fromdate");
		Validator.messageCompare(actual, expected);
	}
	
	@Test
	public void f_StaffSelfC2C() throws InterruptedException{
		final String methodname = "f_StaffSelfC2C";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}	
		StaffSelfC2CReportsSpring staffselfC2C = new StaffSelfC2CReportsSpring(
				driver);
		NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
		String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
		currentNode = test.createNode("To check that proper error message appears when To Date entered is greater than current date.");
		currentNode.assignCategory("SIT");	
		String[] data = { "ALL","" , nhomepage.addDaysToCurrentDate(currentDate, 1), "", ""};
		
		String expected = MessagesDAO.getLabelByKey("pretups.staff.btsl.error.msg.todatebeforecurrentdate");
		new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		String actual = staffselfC2C.fetcherrormessage("toDate");
		Validator.messageCompare(actual, expected);

	}
	@Test
	public void g_StaffSelfC2C() throws InterruptedException {
		final String methodname = "g_StaffSelfC2C";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}
		

		currentNode = test
				.createNode("To check that submit button is disabled when no value is entered.");
		currentNode.assignCategory("SIT");
		String[] data = { "", "", "", "", "", "" };
		
		
		HashMap<String, String> result =new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		Validator.messageCompare(result.get("submitEnabled"), "false");
	}
	
	@Test
	public void h_StaffSelfC2C() throws InterruptedException {
		final String methodname = "h_StaffSelfC2C";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}
		

		currentNode = test
				.createNode("To check that inet report button is disabled when no value is entered.");
		currentNode.assignCategory("SIT");
		String[] data = { "", "", "", "", "", "" };
		
		
		HashMap<String, String> result =new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		Validator.messageCompare(result.get("inetEnabled"), "false");
	}
	@Test
	public void i_StaffSelfC2C() throws IOException, ParseException, InterruptedException{
		final String methodname = "i_StaffSelfC2C";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Staff Self C2C Reports");
			TestCaseCounter = true;
		}	
		OperatorToChannelMap o2cmap = new OperatorToChannelMap();
		NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
		currentNode = test.createNode("To validate transactions of Staff Self C2C Reports.");
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
		
		String[] columnNames = {"transfer_id"};
		String[][] zerbaltrans = DBHandler.AccessHandler.getStaffSelfC2CRpt( sqlStartDate,sqlEndDate,networkCode, userID, loggedInUserID, parentCat, userDomainCode, geodomainCode, columnNames);
		String[] data = { "ALL", "02/02/18", "02/03/18", "", ""};
		new StaffSelfC2CReportsSpringFeatures(driver).checkStaffSelfC2CReport("channel",
				domainCode, data);
		ArrayList<String> txnList = new PaginationHandlerSpring().getTxnIDFromEachpageforstaffSelfC2CReport(driver);
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
