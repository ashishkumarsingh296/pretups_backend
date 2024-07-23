package com.testscripts.sit;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.C2CTransferDetailsSpring;
import com.Features.mapclasses.ChnnlChnnlTrfDetailRptMap;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferDetailsReportSpring;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.PaginationHandlerSpring;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_C2CTransferDetailsReportSpring extends BaseTest{

	static boolean TestCaseCounter = false;
	static String domainCode;
		
		@Test //Spring module or not
		public void a_C2CTrfDetails(){
			final String methodname = "a_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			OperatorToChannelMap o2cmap = new OperatorToChannelMap();
			Login login1 = new Login();
			boolean spring=false;
			String domainName = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
			domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
			currentNode = test.createNode("To verify that C2C transfer details screen visible in spring.");
			currentNode.assignCategory("SIT");	
			
			Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.C2C_TRANSFER_DETAILS,domainCode);
			login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
			new ChannelUserHomePage(driver).clickChannelTrfC2CReport();
	//		c2cTrfdetail.clickC2CTransferDetailslink();
			try{
			spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();
			if(spring)currentNode.log(Status.PASS, "The module is spring.");
			else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
			catch(Exception e){Log.info("Error while looking for spring module.");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();}
		}	
		
		@Test
		public void b_C2CTrfDetails(){
			final String methodname = "b_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when From Date entered is greater than current date. in Mobile No Panel");
			currentNode.assignCategory("SIT");	
			String[] data={"ALL","ALL","7298745632","7214725836",nhomepage.addDaysToCurrentDate(currentDate, 1),"","",""};
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.fromDate.beforecurrentdate");
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode,"MOBILE_NO",data);
			String actual = c2cTrfdetail.fetcherrormessage("fromDateFormobileNumber");
			Validator.messageCompare(actual, expected);
		}
		
		@Test
		public void c_C2CTrfDetails(){
			final String methodname = "c_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when To Date entered is greater than current date. in Mobile No Panel");
			currentNode.assignCategory("SIT");	
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.toDate.beforecurrentdate");
			String[] data={"ALL","ALL","7298745632","7214725836","","",nhomepage.addDaysToCurrentDate(currentDate, 1),""};
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode,"MOBILE_NO", data);
			String actual = c2cTrfdetail.fetcherrormessage("toDateFormobileNumber");
			Validator.messageCompare(actual, expected);

		}

		@Test
		public void d_C2CTrfDetails(){
			final String methodname = "d_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			currentNode = test.createNode("To check that proper error messages appears when From Time and To Time entered is not in proper format. in Mobile No Panel");
			currentNode.assignCategory("SIT");	
			String[] data={"ALL","ALL","7298745632","7214725836","","10:22:00","","10:23:00"};
			String expected1 = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.timeformat");
			String expected2 = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.timeformat");
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode,"MOBILE_NO",data);
				String actual1 = c2cTrfdetail.fetcherrormessage("fromtime");
				String actual2 = c2cTrfdetail.fetcherrormessage("totime");
				Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);
		}	
		
		@Test
		public void e_C2CTrfDetails(){
			final String methodname = "e_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			currentNode = test.createNode("To verify that proper error message appears when Transfer sub Type is not selected. in Mobile No Panel");
			currentNode.assignCategory("SIT");	
			String[] data={"","ALL","7298745632","7214725836","","10:22","","10:23"};
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.txnSubType.required");
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode, "MOBILE_NO",data);
				String actual = c2cTrfdetail.fetcherrormessage("txnSubTypeMOB");
				Validator.messageCompare(actual, expected);
			
		}
		
		@Test
		public void f_C2CTrfDetails(){
			final String methodname = "f_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			currentNode = test.createNode("To verify that proper error message appears when Transfer In/Out is not selected. in Mobile No Panel");
			currentNode.assignCategory("SIT");	
			String[] data={"ALL","","7298745632","7214725836","","10:22","","10:23"};
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.transferInOrOut.required");
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode, "MOBILE_NO",data);
				String actual = c2cTrfdetail.fetcherrormessage("transferInOrOutMOB");
				Validator.messageCompare(actual, expected);
			
		}
		
		@Test
		public void g_C2CTrfDetails(){
			final String methodname = "g_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			currentNode = test.createNode("To verify that proper error message appears when Transfer sub Type is not selected. in User Name Panel");
			currentNode.assignCategory("SIT");	
			String[] data={"","ALL","","","","10:22","","10:23","","","",""};
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.txnSubType.required");
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode, "USER_NAME",data);
				String actual = c2cTrfdetail.fetcherrormessage("txnSubTypeUSR");
				Validator.messageCompare(actual, expected);
			
		}
		
		@Test
		public void h_C2CTrfDetails(){
			final String methodname = "h_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			currentNode = test.createNode("To verify that proper error message appears when Transfer In/Out is not selected. in User Name Panel");
			currentNode.assignCategory("SIT");	
			String[] data={"ALL","","","","","00:22","","10:23","","","",""};
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.transferInOrOut.required");
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode, "USER_NAME",data);
				String actual = c2cTrfdetail.fetcherrormessage("transferInOrOutUSR");
				Validator.messageCompare(actual, expected);
			
		}
		
		@Test
		public void i_C2CTrfDetails(){
			final String methodname = "i_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To verify that proper error message appears when From Date entered is greater than current date in User Name Panel");
			currentNode.assignCategory("SIT");	
			String[] data={"ALL","ALL","","",nhomepage.addDaysToCurrentDate(currentDate, 1),"00:22","","10:23","","","",""};
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.fromDate.beforecurrentdate");
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode, "USER_NAME",data);
				String actual = c2cTrfdetail.fetcherrormessage("fromDateForUserName");
				Validator.messageCompare(actual, expected);
			
		}
		
		@Test
		public void j_C2CTrfDetails(){
			final String methodname = "j_C2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details");
				TestCaseCounter = true;
			}	
			C2CTransferDetailsReportSpring c2cTrfdetail = new C2CTransferDetailsReportSpring(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when To Date entered is greater than current date. in User Name Panel");
			currentNode.assignCategory("SIT");	
			String expected = MessagesDAO.getLabelByKey("pretups.c2c.reports.c2ctransferretwid.error.toDate.beforecurrentdate");
			String[] data={"ALL","ALL","","","","00:22",nhomepage.addDaysToCurrentDate(currentDate, 1),"10:23","","","",""};
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode,"USER_NAME", data);
			String actual = c2cTrfdetail.fetcherrormessage("toDateForUserName");
			Validator.messageCompare(actual, expected);

		}
		
		public void k_C2CTrfDetails(){
			final String methodname = "k_C2CTrfDetails";
			String criteria="ByMobileno";
			ChnnlChnnlTrfDetailRptMap chnnlChnnlTrfDetailRptMap = new ChnnlChnnlTrfDetailRptMap();
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Transfer Details Report");
				TestCaseCounter = true;
			}	
			currentNode = test.createNode("To validate C2C transactions Transfer Details report.");
			currentNode.assignCategory("SIT");	
			String dateformat = "dd/MM/yy";
			String timeFormat = "hh:mm";
			String fromTime = "00:00";
			String toTime ="00:59";
			String actual = null;
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate(dateformat);
			String fromDate = nhomepage.addDaysToCurrentDate(currentDate, -2)+fromTime+":00";
			String toDate = currentDate+toTime+":00";
			String fromMSISDN = chnnlChnnlTrfDetailRptMap.getchnnlChnnlTrfDetailRptMap("fromMSISDN");
			String toMSISDN = chnnlChnnlTrfDetailRptMap.getchnnlChnnlTrfDetailRptMap("toMSISDN");
			
			String[] columnNames = {"transfer_id"};
			String[][] chnnlChnnlTrfDetailRpt = DBHandler.AccessHandler.getchnnlChnnlTrfDetailRpt(fromDate, toDate, dateformat, fromMSISDN, toMSISDN, columnNames);
			
			String[] data={"ALL","ALL",fromMSISDN,toMSISDN,nhomepage.addDaysToCurrentDate(currentDate, -2),"",nhomepage.addDaysToCurrentDate(currentDate,0),""};
			new C2CTransferDetailsSpring(driver).checkC2CtransferReport("channel",domainCode, "MOBILE_NO",data);
			
			ArrayList<String> txnList = new PaginationHandlerSpring().getTxnIDFromEachpage(driver);
			int dbcount = chnnlChnnlTrfDetailRpt.length;
			int gridcount  = txnList.size();
			
			Log.info("<p>Count fetched from DB: "+dbcount+"<br> Count fetched from grid: "+gridcount+"</p>");
			boolean match=false;
			if (dbcount == gridcount) {
				for (int iterator = 0; iterator < dbcount; iterator++) {
						Log.info("Trying to match: "+chnnlChnnlTrfDetailRpt[iterator][0]);
						if (txnList.contains(chnnlChnnlTrfDetailRpt[iterator][0])) {
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
	

