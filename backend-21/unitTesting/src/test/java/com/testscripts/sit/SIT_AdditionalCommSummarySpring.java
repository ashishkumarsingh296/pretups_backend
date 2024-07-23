package com.testscripts.sit;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.AdditionalcommDetailSpring;
import com.Features.AdditionalcommSummarySpring;
import com.Features.mapclasses.AdditionalCommDetailRptMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeluserspages.channelReportC2S.AdditionalCommSummaryFirstPageSpring;
import com.pageobjects.channeluserspages.homepages.ChannelReportsC2SSubLinksPage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.PaginationHandlerSpring;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_AdditionalCommSummarySpring extends BaseTest {
	static boolean TestCaseCounter = false;
	static String domainCode;

	@Test
	public void a_AdditionalCommSummary(){
		final String methodname = "a_AdditionalCommSummary";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Summary");
			TestCaseCounter = true;
		}
		AdditionalCommDetailRptMap additionalCommDetailRptMap = new AdditionalCommDetailRptMap();
		ChannelReportsC2SSubLinksPage channelReportsC2SSubLinksPage =  new ChannelReportsC2SSubLinksPage(driver);
		Login login1 = new Login();
		boolean spring=false;
		String domainName = additionalCommDetailRptMap.getAddCommDetMap("domainName");
		domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
		currentNode = test.createNode("To verify that Additional Commission Summary screen visible in spring.");
		currentNode.assignCategory("SIT");	

		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.ADDITIONAL_COMMN_SUMMARY,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
		new ChannelAdminHomePage(driver).clickAdditionalCommDetailReport();
		channelReportsC2SSubLinksPage.clickAddCommSmryRptStruts();
		try{
			spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();
			if(spring)currentNode.log(Status.PASS, "The module is spring.");
			else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
		catch(Exception e){Log.info("Error while looking for spring module.");
		ExtentI.attachScreenShot();
		ExtentI.attachCatalinaLogs();}
	}

	
	@Test
	public void b_AdditionalCommSummary(){
		final String methodname = "b_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="MONTHLY";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Summary");
			TestCaseCounter = true;
		}	
		AdditionalCommSummaryFirstPageSpring additionalCommSummaryFirstPageSpring = new AdditionalCommSummaryFirstPageSpring(driver);
		currentNode = test.createNode("To check that proper error messages appears when From Month and To Month entered is not in proper format.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","","","12/12/12","12/12/13"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.fromMonth.format");
		String expected2 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.toMonth.format");
		new AdditionalcommSummarySpring(driver).checkAdditionalCommDetailSummary("channel",domainCode,criteria,data);
		String actual1 = additionalCommSummaryFirstPageSpring.fetcherrormessage("fromMonth");
		String actual2 = additionalCommSummaryFirstPageSpring.fetcherrormessage("toMonth");
		Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);

	}
	
	
	
	
	@Test
	public void c_AdditionalCommSummary(){
		final String methodname = "c_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="MONTHLY";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Summary");
			TestCaseCounter = true;
		}	
		AdditionalCommSummaryFirstPageSpring additionalCommSummaryFirstPageSpring = new AdditionalCommSummaryFirstPageSpring(driver);
		currentNode = test.createNode("To check that proper error messages appears when From Month and To Month entered is blank.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","","","",""};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.required.fromMonth");
		String expected2 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.required.toMonth");
		new AdditionalcommSummarySpring(driver).checkAdditionalCommDetailSummary("channel",domainCode,criteria,data);
		String actual1 = additionalCommSummaryFirstPageSpring.fetcherrormessage("fromMonth");
		String actual2 = additionalCommSummaryFirstPageSpring.fetcherrormessage("toMonth");
		Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);

	}
	
	
	@Test
	public void d_AdditionalCommSummary(){
		final String methodname = "d_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="DAILY";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Summary");
			TestCaseCounter = true;
		}	
		AdditionalCommSummaryFirstPageSpring additionalCommSummaryFirstPageSpring = new AdditionalCommSummaryFirstPageSpring(driver);
		currentNode = test.createNode("To check that proper error messages appears when From Time and To Time entered is blank.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","","","",""};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.required.fromDate");
		String expected2 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.required.toDate");
		new AdditionalcommSummarySpring(driver).checkAdditionalCommDetailSummary("channel",domainCode,criteria,data);
		String actual1 = additionalCommSummaryFirstPageSpring.fetcherrormessage("fromDate");
		String actual2 = additionalCommSummaryFirstPageSpring.fetcherrormessage("toDate");
		Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);
	}

	@Test
	public void e_AdditionalCommSummary(){
		final String methodname = "e_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="MONTHLY";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Summary");
			TestCaseCounter = true;
		}	
		AdditionalCommSummaryFirstPageSpring additionalCommSummaryFirstPageSpring = new AdditionalCommSummaryFirstPageSpring(driver);
		currentNode = test.createNode("To check that proper error messages appears when Service Type is not selected.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","","","10/18","10/18"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.required.serviceType");
		new AdditionalcommSummarySpring(driver).checkAdditionalCommDetailSummary("channel",domainCode,criteria,data);
		String actual1 = additionalCommSummaryFirstPageSpring.fetcherrormessage("serviceType");
		Validator.messageCompare(actual1, expected1);

	}
	
	
	@Test
	public void f_AdditionalCommSummary(){
		final String methodname = "f_AdditionalCommDet";
		Log.startTestCase(methodname);
		String criteria="DAILY";
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Summary");
			TestCaseCounter = true;
		}	
		AdditionalCommSummaryFirstPageSpring additionalCommSummaryFirstPageSpring = new AdditionalCommSummaryFirstPageSpring(driver);
		currentNode = test.createNode("To check that proper error messages appears when To Date is less than From Date.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","01/02/18","01/01/18","",""};
		String expected1 = MessagesDAO.getLabelByKey("pretups.c2s.reports.web.additionalcommissionSummary.error.msg.todatemustbegreaterthanorequaltotodate");
		new AdditionalcommSummarySpring(driver).checkAdditionalCommDetailSummary("channel",domainCode,criteria,data);
		String actual1 = additionalCommSummaryFirstPageSpring.fetcherrormessage("toDate");
		Validator.messageCompare(actual1, expected1);

	}
	
	
	
		
	public void g_AdditionalCommDet(){
		final String methodname = "g_AdditionalCommDet";
		String criteria="DAILY";
		AdditionalCommDetailRptMap additionalCommDetailRptMap = new AdditionalCommDetailRptMap();
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Additional Commission Summary");
			TestCaseCounter = true;
		}	
		currentNode = test.createNode("To validate transactions of Additional Commission Summary report.");
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
