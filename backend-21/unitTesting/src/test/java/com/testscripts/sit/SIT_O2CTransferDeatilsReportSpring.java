package com.testscripts.sit;

import java.io.IOException;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.O2CTransferDetailsSpring;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportsO2C.O2CtransferdetailsSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.PaginationHandlerSpring;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_O2CTransferDeatilsReportSpring extends BaseTest{

	static boolean TestCaseCounter = false;
	static String domainCode;
		
		@Test //Spring module or not
		public void a_O2CTrfDetails(){
			final String methodname = "a_O2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Details");
				TestCaseCounter = true;
			}	
			O2CtransferdetailsSpring o2cTrfdetail = new O2CtransferdetailsSpring(driver);
			OperatorToChannelMap o2cmap = new OperatorToChannelMap();
			Login login1 = new Login();
			boolean spring=false;
			String domainName = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
			domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
			currentNode = test.createNode("To verify that O2C transfer details screen visible in spring.");
			currentNode.assignCategory("SIT");	
			
			Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.O2C_TRANSFER_DETAILS,domainCode);
			login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
			new ChannelAdminHomePage(driver).clickChannelTrfO2CReport();
			o2cTrfdetail.clickO2CTransferDetailslink();
			try{
			spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();
			if(spring)currentNode.log(Status.PASS, "The module is spring.");
			else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
			catch(Exception e){Log.info("Error while looking for spring module.");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();}
		}	
		
		@Test
		public void b_O2CTrfDetails(){
			final String methodname = "b_O2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Details");
				TestCaseCounter = true;
			}	
			O2CtransferdetailsSpring o2cTrfdetail = new O2CtransferdetailsSpring(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when From Date entered is greater than current date.");
			currentNode.assignCategory("SIT");	
			String[] data={"","","","","ALL","ALL",nhomepage.addDaysToCurrentDate(currentDate, 1),"","","","true"};
			String expected = MessagesDAO.getLabelByKey("pretups.o2cDetails.btsl.error.msg.fromdatebeforecurrentdate");
			new O2CTransferDetailsSpring(driver).checkO2CtransferReport("channel",domainCode, data);
			String actual = o2cTrfdetail.fetcherrormessage("fromdate");
			Validator.messageCompare(actual, expected);
		}
		
		@Test
		public void c_O2CTrfDetails(){
			final String methodname = "c_O2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Details");
				TestCaseCounter = true;
			}	
			O2CtransferdetailsSpring o2cTrfdetail = new O2CtransferdetailsSpring(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when To Date entered is greater than current date.");
			currentNode.assignCategory("SIT");	
			String[] data={"","","","","ALL","ALL","","",nhomepage.addDaysToCurrentDate(currentDate, 1),"","true"};
			String expected = MessagesDAO.getLabelByKey("pretups.o2cDetails.btsl.error.msg.todatebeforecurrentdate");
			new O2CTransferDetailsSpring(driver).checkO2CtransferReport("channel",domainCode, data);
			String actual = o2cTrfdetail.fetcherrormessage("toDate");
			Validator.messageCompare(actual, expected);

		}

		@Test
		public void d_O2CTrfDetails(){
			final String methodname = "d_O2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Details");
				TestCaseCounter = true;
			}	
			O2CtransferdetailsSpring o2cTrfdetail = new O2CtransferdetailsSpring(driver);
			currentNode = test.createNode("To check that proper error messages appears when From Time and To Time entered is not in proper format.");
			currentNode.assignCategory("SIT");	
			String[] data={"","","","","ALL","ALL","","10:22:00","","10:23:00","true"};
			String expected1 = MessagesDAO.getLabelByKey("pretups.o2cDetails.from.time.is.not.in.proper.format");
			String expected2 = MessagesDAO.getLabelByKey("pretups.o2cDetails.to.time.is.not.in.proper.format");
			new O2CTransferDetailsSpring(driver).checkO2CtransferReport("channel",domainCode, data);
				String actual1 = o2cTrfdetail.fetcherrormessage("fromtime");
				String actual2 = o2cTrfdetail.fetcherrormessage("totime");
				Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);
		}	
		
		@Test
		public void e_O2CTrfDetails(){
			final String methodname = "d_O2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Details");
				TestCaseCounter = true;
			}	
			O2CtransferdetailsSpring o2cTrfdetail = new O2CtransferdetailsSpring(driver);
			currentNode = test.createNode("To verify that proper error message appears when Transfer sub Type is not selected.");
			currentNode.assignCategory("SIT");	
			String[] data={"","","","","","ALL","","10:22","","10:23","true"};
			String expected = MessagesDAO.getLabelByKey("pretups.o2cDetails.txnSubType.is.required");
			new O2CTransferDetailsSpring(driver).checkO2CtransferReport("channel",domainCode, data);
				String actual = o2cTrfdetail.fetcherrormessage("subtype");
				Validator.messageCompare(actual, expected);
			
		}
		
		
		@Test
		public void f_O2CTrfDetails() throws IOException{
			final String methodname = "d_O2CTrfDetails";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Details");
				TestCaseCounter = true;
			}	
			OperatorToChannelMap o2cmap = new OperatorToChannelMap();
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			currentNode = test.createNode("To validate transactions of O2C report.");
			currentNode.assignCategory("SIT");	
			String dateformat = "dd/MM/yy";
			String actual = null;
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate(dateformat);
			String fromDate = nhomepage.addDaysToCurrentDate(currentDate, -2);
			String toDate = currentDate;
			String geography_name = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_GEOGRAPHY");
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
			int rowNum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, geography_name);
			String geodomainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, rowNum);
			String[] columnNames = {"transfer_id"};
			String[][] o2cTxns = DBHandler.AccessHandler.getO2CTransferDetails(fromDate, toDate, dateformat, domainCode, geodomainCode, columnNames);
			String[] data={"","","","","ALL","ALL",nhomepage.addDaysToCurrentDate(currentDate, -2),"",currentDate,"","true"};
		
			new O2CTransferDetailsSpring(driver).checkO2CtransferReport("channel",domainCode, data);
			ArrayList<String> txnList = new PaginationHandlerSpring().getTxnIDFromEachpage(driver);
			int dbcount = 0;
			if(o2cTxns != null)
			dbcount = o2cTxns.length;
			int gridcount  = txnList.size();
			
			Log.info("<p>Count fetched from DB: "+dbcount+"<br> Count fetched from grid: "+gridcount+"</p>");
			boolean match=false;
			if(dbcount == 0)
				match = true;
			else if (dbcount == gridcount) {
				for (int iterator = 0; iterator < dbcount; iterator++) {
						Log.info("Trying to match: "+o2cTxns[iterator][0]);
						if (txnList.contains(o2cTxns[iterator][0])) {
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
	

