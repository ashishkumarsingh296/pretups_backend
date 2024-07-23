package com.testscripts.sit;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.C2STransferFeatureSpring;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportsO2C.C2STransferSpringPageObject;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_C2STransferReportSpring extends BaseTest{

	static boolean TestCaseCounter = false;
	static String domainCode;
		
		@Test //Spring module or not
		public void a_C2STransfer(){
			final String methodname = "a_C2STransfer";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2STransfer");
				TestCaseCounter = true;
			}	
			C2STransferSpringPageObject c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
			OperatorToChannelMap map = new OperatorToChannelMap();
			Login login1 = new Login();
			boolean spring=false;
			String domainName = map.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
			domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
			currentNode = test.createNode("To verify that C2S Transfer screen visible in spring.");
			currentNode.assignCategory("SIT");	
			
			Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.C2S_TRANSFER,domainCode);
			login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
			new ChannelAdminHomePage(driver).clickChannelReportsC2STransfer();
			new ChannelAdminHomePage(driver).clickChannelReportsC2STransfer();
			c2STransferSpringPageObject.clickC2STransferlink();  
			try{
			spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();  
			if(spring)currentNode.log(Status.PASS, "The module is spring.");
			else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
			catch(Exception e){Log.info("Error while looking for spring module.");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();}
		}	
		
		
		@Test
		public void b_C2STransferServiceType(){
			            final String methodname = "b_C2STransferServiceType";
			            Log.startTestCase(methodname);
			             String criteria="ByMobileno";
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]C2S Transfer Details");
							TestCaseCounter = true;
						}
                        C2STransferSpringPageObject c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
                        currentNode = test.createNode("To check that proper error messages appears when Service type is not selected.");
                        currentNode.assignCategory("SIT");
                    	String[] data={"","ALL","04/01/18","09:39","09:39","723211412684666"};               		
                         String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.c2stransfer.serviceType.is.required");                      
                        new C2STransferFeatureSpring(driver).checkC2STransferReport("channel",domainCode,criteria, data);
                        String actual = c2STransferSpringPageObject.fetcherrormessage("serviceType");  
                        Validator.messageCompare(actual, expected);

        } 
		
		@Test
		public void c_C2STransferStatus(){
			            final String methodname = "c_C2STransferTransferStatus";
			            Log.startTestCase(methodname);
			             String criteria="ByMobileno";
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]C2S Transfer Details");
							TestCaseCounter = true;
						}
                        C2STransferSpringPageObject c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
                        currentNode = test.createNode("To check that proper error messages appears when Transfer Status is not selected.");
                        currentNode.assignCategory("SIT");
                    	String[] data={"ALL","","04/01/18","09:39","09:39","723211412684666"};               		
                        String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.panel.c2stransfer.transferStatus");                      
                        new C2STransferFeatureSpring(driver).checkC2STransferReport("channel",domainCode,criteria, data);
                        String actual = c2STransferSpringPageObject.fetcherrormessage("transferStatus");
                        Validator.messageCompare(actual, expected);

        }
		
		@Test
		public void d_C2STransferDate(){
			final String methodname = "d_C2STransferDate";
			Log.startTestCase(methodname);
			String criteria="ByMobileno";
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2S Transfer Details");
				TestCaseCounter = true;
			}	
			 C2STransferSpringPageObject c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
			currentNode = test.createNode("To check that proper error message appears when Date is required.");
			currentNode.assignCategory("SIT");				
			String[] data={"ALL","ALL","","09:39","09:39","723211412684666","Distributor","ALL","ALL",nhomepage.addDaysToCurrentDate(currentDate, 1),"","","","true"};			
			String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.c2stransfer.date.is.required");
			new C2STransferFeatureSpring(driver).checkC2STransferReport("channel",domainCode,criteria, data);
			String actual = c2STransferSpringPageObject.fetcherrormessage("date");  
			Validator.messageCompare(actual, expected);
		}
		
		@Test
		public void e_C2STransferFromTime(){
			            final String methodname = "e_C2STransferStatus";
			            Log.startTestCase(methodname);
			             String criteria="ByMobileno";
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]C2S Transfer Details");
							TestCaseCounter = true;
						}
                        C2STransferSpringPageObject c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
                        currentNode = test.createNode("To check that proper error messages appears when From Time  is required.");
                        currentNode.assignCategory("SIT");
                    	String[] data={"ALL","ALL","04/01/18","","09:39","723211412684666"};               		
                        String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.c2stransfer.fromTime.is.required");                      
                        new C2STransferFeatureSpring(driver).checkC2STransferReport("channel",domainCode,criteria, data);
                        String actual = c2STransferSpringPageObject.fetcherrormessage("fromTime");
                        Validator.messageCompare(actual, expected);

        }
		@Test
		public void f_C2STransferToTime(){
			            final String methodname = "f_C2STransferToTime";
			            Log.startTestCase(methodname);
			             String criteria="ByMobileno";
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]C2S Transfer Details");
							TestCaseCounter = true;
						}
                        C2STransferSpringPageObject c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
                        currentNode = test.createNode("To check that proper error messages appears when To Time  is required.");
                        currentNode.assignCategory("SIT");
                    	String[] data={"ALL","ALL","04/01/18","09:39","","723211412684666"};               		
                        String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.c2stransfer.toTime.is.required");                      
                        new C2STransferFeatureSpring(driver).checkC2STransferReport("channel",domainCode,criteria, data);
                        String actual = c2STransferSpringPageObject.fetcherrormessage("toTime");
                        Validator.messageCompare(actual, expected);

        }
		
		@Test
		public void g_C2STransferMsisdn(){
			            final String methodname = "g_C2STransferMsisdn";
			            Log.startTestCase(methodname);
			             String criteria="ByMobileno";
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]C2S Transfer Details");
							TestCaseCounter = true;
						}
						 C2STransferSpringPageObject c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
                        currentNode = test.createNode("To check that proper error messages appears when msisdn is is required");
                        currentNode.assignCategory("SIT");
                        String[] data={"ALL","ALL","04/01/18","09:39","09:39",""};             		
                        String expected = MessagesDAO.getLabelByKey("pretups.channel.user.reports.c2stransfer.msisdn.is.required");                      
                        new C2STransferFeatureSpring(driver).checkC2STransferReport("channel",domainCode,criteria, data);
                        String actual = c2STransferSpringPageObject.fetcherrormessage("msisdn");
                        Validator.messageCompare(actual, expected);

        }
		
		//end case	
		
		/*@Test
		public void h_C2STransfer() throws IOException{
			final String methodname = "h_C2STransfer";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2STransfer");
				TestCaseCounter = true;
			}	
			OperatorToChannelMap map = new OperatorToChannelMap();
			NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
			currentNode = test.createNode("To validate transactions of C2STransfer report.");
			currentNode.assignCategory("SIT");	
			String dateformat = "dd/MM/yy";
			String actual = null;
			String currentDate = DBHandler.AccessHandler.getCurrentServerDate(dateformat);
			String fromDate = nhomepage.addDaysToCurrentDate(currentDate, -2);
			String toDate = currentDate;
		
			String geography_name = map.getOperatorToChannelMap(_masterVO.getProperty("C2STransferCode")).get("TO_GEOGRAPHY");
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
			int rowNum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, geography_name);
			String geodomainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, rowNum);
			String[] columnNames = {"transfer_id"};
			String[][] zbcdetails = DBHandler.AccessHandler.getC2STransfer(fromDate, toDate, dateformat, domainCode, geodomainCode, columnNames);
			String[] data={"","","","","ALL","ALL",nhomepage.addDaysToCurrentDate(currentDate, -2),"",currentDate,"","true"};
		
			new C2STransferFeatureSpring(driver).checkC2STransferReport("channel",domainCode, data);
			ArrayList<String> txnList = new PaginationHandlerSpring().getTxnIDFromEachpage(driver);
			int dbcount = zbcdetails.length;
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
