package com.testscripts.sit;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.LmsRedemptionReportSpring;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.lmsPages.lmsRedemptionReport.LmsRedemptionReportPageObject;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_LMSRedemptionReportSpring extends BaseTest{

	static boolean TestCaseCounter = false;
	static String domainCode;
		
		@Test //Spring module or not
		public void a_LmsRedemptionReport(){
			final String methodname = "a_LmsRedemptionReport";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]LMS Redemption Report");
				TestCaseCounter = true;
			}	
			LmsRedemptionReportPageObject lmsRedemptionReportPageObject = new LmsRedemptionReportPageObject(driver);
			OperatorToChannelMap map = new OperatorToChannelMap();//according  to requiredment
			Login login1 = new Login();
			boolean spring=false;
			String domainName = map.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
			domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
			currentNode = test.createNode("To verify that Zero Balance Counter Detail screen visible in spring.");
			currentNode.assignCategory("SIT");	
			
			Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.LMSREDRPT,domainCode);
			login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
			new ChannelAdminHomePage(driver).clicklMSReport();
			lmsRedemptionReportPageObject.clickLmsRedReportLink();  
			try{
			spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();  
			if(spring)currentNode.log(Status.PASS, "The module is spring.");
			else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
			catch(Exception e){Log.info("Error while looking for spring module.");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();}
		}	
		
		@Test
		public void b_lmsRedemptionReportRedemptionType(){
			            final String methodname = "b_lmsRedemptionReportRedemptionType";
			            Log.startTestCase(methodname);
			             String criteria="ByMobileno";
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]LMS Redemptuion Report Details");
							TestCaseCounter = true;
						}
						LmsRedemptionReportPageObject lmsRedemptionReportPageObject = new LmsRedemptionReportPageObject(driver);
                        currentNode = test.createNode("To check that proper error messages appears when threshold type is not selected.");
                        currentNode.assignCategory("SIT");
                    	String[] data={"STOLK","01/02/18","28/02/18","ra_dea"};               		
                        String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.zerobalancecounterdetails.thresholdType.is.required");                      
                        new LmsRedemptionReportSpring(driver).checklmsRedemptionReport("channel",domainCode, data);
                        String actual = lmsRedemptionReportPageObject.fetcherrormessage("redemptionType");
                        Validator.messageCompare(actual, expected);

        }

		
		@Test
		public void c_lmsRedemptionReportFromDate(){
			final String methodname = "c_lmsRedemptionReportFromDate";
			Log.startTestCase(methodname);
			String criteria="ByMobileno";
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]LMS Redemption Report Details");
				TestCaseCounter = true;
			}	
			LmsRedemptionReportPageObject lmsRedemptionReportPageObject = new LmsRedemptionReportPageObject(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when From Date is required.");
			currentNode.assignCategory("SIT");				
			String[] data={"STOLK","01/02/18","28/02/18","ra_dea"};			
			String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.zerobalancecounterdetails.fromDate.is.required");
			new LmsRedemptionReportSpring(driver).checklmsRedemptionReport("channel",domainCode, data);
			String actual = lmsRedemptionReportPageObject.fetcherrormessage("fromDate");
			Validator.messageCompare(actual, expected);
		}
		@Test
		public void d_lmsRedemptionReportToDate(){
			final String methodname = "d_lmsRedemptionReportToDate";
			Log.startTestCase(methodname);
			String criteria="ByMobileno";
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]LMS Redemption Report Details");
				TestCaseCounter = true;
			}	
			LmsRedemptionReportPageObject lmsRedemptionReportPageObject = new LmsRedemptionReportPageObject(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when To Date is required.");
			currentNode.assignCategory("SIT");	
			
			String[] data={"STOLK","01/02/18","28/02/18","ra_dea"};
			
			String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.zerobalancecounterdetails.toDate.is.required");
			new LmsRedemptionReportSpring(driver).checklmsRedemptionReport("channel",domainCode,data);
			String actual = lmsRedemptionReportPageObject.fetcherrormessage("toDate");
			Validator.messageCompare(actual, expected);
		}
		
		@Test
		public void e_lmsRedemptionReportLoginId(){
			            final String methodname = "e_lmsRedemptionReportLoginId";
			            Log.startTestCase(methodname);
			             String criteria="ByMobileno";
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]Zero Balance Counter Details");
							TestCaseCounter = true;
						}
						LmsRedemptionReportPageObject lmsRedemptionReportPageObject = new LmsRedemptionReportPageObject(driver);
                        currentNode = test.createNode("To check that proper error messages appears when msisdn is is required");
                        currentNode.assignCategory("SIT");
                    	String[] data={"STOLK","01/02/18","28/02/18","ra_dea"};               		
                        String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.zerobalancecounterdetails.msisdn.is.required");                      
                        new LmsRedemptionReportSpring(driver).checklmsRedemptionReport("channel",domainCode, data);
                        String actual = lmsRedemptionReportPageObject.fetcherrormessage("msisdn");
                        Validator.messageCompare(actual, expected);

        }
		
		
		
		/*@Test
		public void f_ZeroBalanceCounterDetails() throws IOException{
			final String methodname = "f_ZeroBalanceCounterDetails";
			Log.startTestCase(methodname);
			String criteria="ByMobileno";
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]Zero Balance Counter Details");
				TestCaseCounter = true;
			}	
			OperatorToChannelMap map = new OperatorToChannelMap();
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			currentNode = test.createNode("To validate channel reports-user of Zero Balance Counter Details report.");
			currentNode.assignCategory("SIT");	
			String dateformat = "dd/MM/yy";
			String actual = null;
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate(dateformat);
			String fromDate = nhomepage.addDaysToCurrentDate(currentDate, -2);
			String toDate = currentDate;
			String thresholdType="ALL";
			String msisdn="723211412684666";
			String parentCategoryCode="DIST";
			String userName="ALL";
			
			String geography_name = map.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_GEOGRAPHY");
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
			int rowNum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, geography_name);
			String geodomainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, rowNum);
			String[] columnNames = {"transfer_id"};
			
			String[][] zbcdetails = DBHandler.AccessHandler.getZeroBalanceCounterDetails(thresholdType,fromDate, toDate,msisdn, dateformat,geodomainCode, domainCode,parentCategoryCode ,userName, columnNames);
			
			String[] data={thresholdType,fromDate,toDate,msisdn,"ALL","ALL",nhomepage.addDaysToCurrentDate(currentDate, -2),"",currentDate,"","true"};
		
			new ZeroBalanceCounterDetailSpring(driver).checkZeroBalanceCounterDetailReport("channel",domainCode,criteria, data);
			ArrayList<String> txnList = new PaginationHandlerSpring().getTxnIDFromEachpage(driver);
			int dbcount = 1;//zbcdetails.length;
			int gridcount  = txnList.size();
			
			Log.info("<p>Count fetched from DB: "+dbcount+"<br> Count fetched from grid: "+gridcount+"</p>");
			boolean match=false;
			if (dbcount == gridcount) {
				for (int iterator = 0; iterator < dbcount; iterator++) {
						Log.info("Trying to match: "+zbcdetails[iterator][0]);
						if (txnList.contains(zbcdetails[iterator][0])) {
							Log.info("Matched successfully.");
							match = true;}
						else {Log.info("Not Matched.");
							match = false;}
					}
				}
			
			
			if(match){actual="true";}
			else{actual="false";}
			Validator.messageCompare(actual, "true");
		}*/
}
