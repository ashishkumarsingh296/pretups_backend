package com.testscripts.sit;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.AdditionalcommDetailSpring;
import com.Features.mapclasses.AdditionalCommDetailRptMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.adddetails.AdditionalCommDetailSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.PaginationHandlerSpring;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_AdditionalCommDetSpring extends BaseTest {
	static boolean TestCaseCounter = false;
	static String domainCode;

	@Test
	public void a_AdditionalCommDet(){
		final String methodname = "a_AdditionalCommDet";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}
		AdditionalCommDetailRptMap additionalCommDetailRptMap = new AdditionalCommDetailRptMap();
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);
		Login login1 = new Login();
		boolean spring=false;
		String domainName = additionalCommDetailRptMap.getAddCommDetMap("domainName");
		domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
		currentNode = test.createNode("To verify that Additional Commission detail screen visible in spring.");
		currentNode.assignCategory("SIT");	

		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.ADDITIONAL_COMMN_DETAIL,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
		new ChannelAdminHomePage(driver).clickAdditionalCommDetailReport();
		addticommdet.clickaddCommDetailLink();
		try{
			spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();
			if(spring)currentNode.log(Status.PASS, "The module is spring.");
			else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
		catch(Exception e){Log.info("Error while looking for spring module.");
		ExtentI.attachScreenShot();
		ExtentI.attachCatalinaLogs();}
	}

	@Test
	public void b_AdditionalCommDet(){
		final String methodname = "b_AdditionalCommDet";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);
		String criteria="ByMobileno";
		currentNode = test.createNode("To check that proper error messages appears when From Time and To Time entered is not in proper format.");
		currentNode.assignCategory("SIT");	
		String[] data={"01/02/18","10:22:00","10:23:00","2112145"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.fromtime.format");
		String expected2 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.totime.format");
		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode,criteria,data);
		String actual1 = addticommdet.fetcherrormessage("fromTime");
		String actual2 = addticommdet.fetcherrormessage("toTime");
		Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);

	}

	@Test
	public void c_AdditionalCommDet(){
		final String methodname = "c_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="ByMobileno";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);

		currentNode = test.createNode("To check that proper error messages appears when Msisdn is not in proper format.");
		currentNode.assignCategory("SIT");	
		String[] data={"01/02/18","10:22","10:23","aa"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.numeric.msisdn");

		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode,criteria, data);
		String actual1 = addticommdet.fetcherrormessage("msisdn");

		Validator.messageCompare(actual1, expected1);

	}

	@Test
	public void d_AdditionalCommDet(){
		final String methodname = "d_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="ByMobileno";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);

		currentNode = test.createNode("To check that proper error messages appears when current Date is not entered.");
		currentNode.assignCategory("SIT");	
		String[] data={"","10:22","10:23","2112145"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.required.currentDate");

		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode,criteria, data);
		String actual1 = addticommdet.fetcherrormessage("currentDate1");

		Validator.messageCompare(actual1,expected1);

	}

	@Test
	public void e_AdditionalCommDet(){
		final String methodname = "e_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="ByMobileno";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);

		currentNode = test.createNode("To check that proper error messages appears when From Time and To Time is not entered");
		currentNode.assignCategory("SIT");	
		String[] data={"01/02/18","","","2112145"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.required.fromTime");
		String expected2 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.required.toTime");
		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode,criteria, data);
		String actual1 = addticommdet.fetcherrormessage("fromTime");
		String actual2 = addticommdet.fetcherrormessage("toTime");
		Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);

	}

	@Test
	public void f_AdditionalCommDet(){
		final String methodname = "f_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="ByMobileno";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);

		currentNode = test.createNode("To check that proper error messages appears when msisdn is not entered.");
		currentNode.assignCategory("SIT");	
		String[] data={"01/02/18","10:22","10:23",""};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.required.msisdn");

		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode, criteria,data);
		String actual1 = addticommdet.fetcherrormessage("msisdn");

		Validator.messageCompare(actual1,expected1);

	}

	@Test
	public void g_AdditionalCommDet(){
		final String methodname = "g_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="ByCategory";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);

		currentNode = test.createNode("To check that proper error messages appears when From Time and To Time entered is not in proper format for by category panel.");
		currentNode.assignCategory("SIT");	
		String[] data={"01/02/18","10:22:00","10:23:00","Haryana","Distributor","ALL","ALL"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.fromtime.format");
		String expected2 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.totime.format");
		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode,criteria, data);
		String actual1 = addticommdet.fetcherrormessage("fromTimecat");
		String actual2 = addticommdet.fetcherrormessage("toTimecat");
		Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);

	}

	@Test
	public void h_AdditionalCommDet(){
		final String methodname = "h_AdditionalCommDet";
		String criteria="ByCategory";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);

		currentNode = test.createNode("To check that proper error messages appears when cureent date is not entered.");
		currentNode.assignCategory("SIT");	
		String[] data={"","10:22","10:23","Haryana","Distributor","ALL","ALL"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.required.currentDate");

		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode, criteria,data);
		String actual1 = addticommdet.fetcherrormessage("currentDate");

		Validator.messageCompare(actual1,expected1);

	}

	
	@Test
	public void j_AdditionalCommDet(){
		final String methodname = "j_AdditionalCommDet";
		String criteria="ByCategory";
		AdditionalCommDetailRptMap additionalCommDetailRptMap = new AdditionalCommDetailRptMap();
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		AdditionalCommDetailSpring addticommdet = new AdditionalCommDetailSpring(driver);

		currentNode = test.createNode("To check that proper error messages appears when username is not entered.");
		currentNode.assignCategory("SIT");	
		String parentCat = additionalCommDetailRptMap.getAddCommDetMap("categoryName");
		String[] data={"01/02/18","10:22","10:23","Haryana","Distributor",parentCat,""};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommission.error.msg.required.user");
		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel",domainCode, criteria,data);
		String actual1 = addticommdet.fetcherrormessage("user");

		Validator.messageCompare(actual1,expected1);

	}
	
	public void k_AdditionalCommDet(){
		final String methodname = "j_AdditionalCommDet";
		String criteria="ByMobileno";
		AdditionalCommDetailRptMap additionalCommDetailRptMap = new AdditionalCommDetailRptMap();
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Details");
			TestCaseCounter = true;
		}	
		currentNode = test.createNode("To validate transactions of Additional Commission Detail report.");
		currentNode.assignCategory("SIT");	
		String dateformat = "dd/MM/yy";
		String timeFormat = "HH24:MI";
		String actual = null;
		String currentDate = DBHandler.AccessHandler.getCurrentServerDate(dateformat);
		String fromTime = "00:00";
		String toTime = DBHandler.AccessHandler.getCurrentServerTime(timeFormat);
		String fromDate = currentDate+" "+fromTime+":00";
		String toDate = currentDate+" "+toTime+":00";
		String msisdn = additionalCommDetailRptMap.getAddCommDetMap("msisdn");
		String networkCode = _masterVO.getMasterValue("Network Code");
		String userID = DBHandler.AccessHandler.getUserId(additionalCommDetailRptMap.getAddCommDetMap("userName"));
		String loggedInUserID = DBHandler.AccessHandler.getUserId(additionalCommDetailRptMap.getAddCommDetMap("loggedInUserName"));
		String parentCat = DBHandler.AccessHandler.getCategoryCode(additionalCommDetailRptMap.getAddCommDetMap("parentCategory"));
		String geodomainCode = additionalCommDetailRptMap.getAddCommDetMap("zone");
		String userDomainCode = DBHandler.AccessHandler.getDomainCode(additionalCommDetailRptMap.getAddCommDetMap("domainName"));
		String[] columnNames = {"adjustment_id"};
		String [] data = {currentDate, fromTime, toTime, msisdn};
		String[][] addCommTran = DBHandler.AccessHandler.getAddCommDetailRpt(fromDate, toDate, networkCode, userID, loggedInUserID, parentCat, userDomainCode, geodomainCode, columnNames);
		new AdditionalcommDetailSpring(driver).checkAdditionalCommDetailReport("channel", domainCode, criteria, data);
		
		ArrayList<String> txnList = new PaginationHandlerSpring().getTxnIDFromEachpage(driver);
		int dbcount = 0;
		if(addCommTran != null)
		dbcount = addCommTran.length;
		int gridcount  = txnList.size();
		
		Log.info("<p>Count fetched from DB: "+dbcount+"<br> Count fetched from grid: "+gridcount+"</p>");
		boolean match=false;
		if(gridcount == 0)
			match = true;
		else{
		if (dbcount == gridcount) {
			for (int iterator = 0; iterator < dbcount; iterator++) {
					Log.info("Trying to match: "+addCommTran[iterator][0]);
					if (txnList.contains(addCommTran[iterator][0])) {
						Log.info("Matched successfully.");
						match = true;}
					else {Log.info("Not Matched.");
						match = false;}
				}
			}
		}
		
		if(match){actual="true";}
		else{actual="false";}
		Validator.messageCompare(actual, "true");
	}
}
