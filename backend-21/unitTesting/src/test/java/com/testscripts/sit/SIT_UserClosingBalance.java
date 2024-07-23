package com.testscripts.sit;

import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.UserClosingBalanceSpringFeatures;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelReportsUser.UserClosingBalanceSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_UserClosingBalance extends BaseTest{

	static boolean TestCaseCounter = false;
	static String domainCode;
	
	@Test //Spring module or not
	public void a_UserClosingBalance(){
		final String methodname = "a_UserClosingBalance";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]User Closing Balance");
			TestCaseCounter = true;
		}	
		UserClosingBalanceSpring userClosingBalance = new UserClosingBalanceSpring(driver);
		OperatorToChannelMap o2cmap = new OperatorToChannelMap();
		Login login1 = new Login();
		boolean spring=false;
		String domainName = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
		domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
		currentNode = test.createNode("To verify that User Closing Balance screen visible in spring.");
		currentNode.assignCategory("SIT");	
		
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.USER_CLOSING_BALANCE,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
		new ChannelAdminHomePage(driver).clickChannelReportsUser();
	    userClosingBalance.clickUserClosingBalancelink();
		try{
		spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();
		if(spring)currentNode.log(Status.PASS, "The module is spring.");
		else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
		catch(Exception e){Log.info("Error while looking for spring module.");
		ExtentI.attachScreenShot();
		ExtentI.attachCatalinaLogs();}
	}
	
	
	@Test
	public void b_UserClosingBalance(){
		final String methodname = "b_UserClosingBalance";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]User Closing Balance");
			TestCaseCounter = true;
		}	
		UserClosingBalanceSpring userClosingBalance = new UserClosingBalanceSpring(driver);
		NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
		String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
		currentNode = test.createNode("To check that proper error message appears when From Date entered is greater than current date.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","","",nhomepage.addDaysToCurrentDate(currentDate, 1),"","",""};
		String expected = MessagesDAO.getLabelByKey("pretups.userClosingBalance.btsl.error.msg.fromdatebeforecurrentdate");
		new UserClosingBalanceSpringFeatures(driver).checkUserClosingBalanceReport("channel",domainCode, data);
		String actual = userClosingBalance.fetcherrormessage("fromDate");
		Validator.messageCompare(actual, expected);
	}
	
	
	@Test
	public void c_UserClosingBalance(){
		final String methodname = "c_UserClosingBalance";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]User Closing Balance");
			TestCaseCounter = true;
		}	
		UserClosingBalanceSpring userClosingBalance = new UserClosingBalanceSpring(driver);
		NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
		String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
		currentNode = test.createNode("To check that proper error message appears when To Date entered is greater than current date.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","","","",nhomepage.addDaysToCurrentDate(currentDate, 1),"",""};
		String expected = MessagesDAO.getLabelByKey("pretups.userClosingBalance.btsl.error.msg.todatebeforecurrentdate");
		new UserClosingBalanceSpringFeatures(driver).checkUserClosingBalanceReport("channel",domainCode, data);
		String actual = userClosingBalance.fetcherrormessage("toDate");
		Validator.messageCompare(actual, expected);

	}
	
	@Test
	public void d_UserClosingBalance(){
		final String methodname = "d_UserClosingBalance";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]User Closing Balance");
			TestCaseCounter = true;
		}	
		UserClosingBalanceSpring userClosingBalance = new UserClosingBalanceSpring(driver);
		currentNode = test.createNode("To check that proper error messages appears when From Amount and To Amount entered is not in proper format.");
		currentNode.assignCategory("SIT");	
		String[] data={"","","","","","","abcd","defg"};
		String expected1 = MessagesDAO.getLabelByKey("pretups.UserClosingBalance.invalid.FromAmount");
		String expected2 = MessagesDAO.getLabelByKey("pretups.UserClosingBalance.invalid.ToAmount");
		new UserClosingBalanceSpringFeatures(driver).checkUserClosingBalanceReport("channel",domainCode, data);
			String actual1 = userClosingBalance.fetcherrormessage("fromAmount");
			String actual2 = userClosingBalance.fetcherrormessage("toAmount");
			Validator.messageCompare(actual1+" "+actual2, expected1+" "+expected2);
	}
	
	
	@Test
	public void e_UserClosingBalance(){
		final String methodname = "e_UserClosingBalance";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]User Closing Balance");
			TestCaseCounter = true;
		}	
		UserClosingBalanceSpring userClosingBalance = new UserClosingBalanceSpring(driver);
		currentNode = test.createNode("To verify that proper error message appears when Category is not selected.");
		currentNode.assignCategory("SIT");	
		String[] data={"","Select","","","","","",""};
		String expected = MessagesDAO.getLabelByKey("pretups.userClosingBalance.parentCategoryCode.is.required");
		new UserClosingBalanceSpringFeatures(driver).checkUserClosingBalanceReport("channel",domainCode, data);
			String actual = userClosingBalance.fetcherrormessage("parentCategoryCode");
			Validator.messageCompare(actual, expected);
		
	}
	
	@Test
	public void f_UserClosingBalance(){
		final String methodname = "e_UserClosingBalance";
		OperatorToChannelMap o2cmap = new OperatorToChannelMap();
		NetworkAdminHomePage nhomepage = new NetworkAdminHomePage(driver);
		String currentDate = DBHandler.AccessHandler.getCurrentServerDate("dd/MM/yy");
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]User Closing Balance");
			TestCaseCounter = true;
		}	
		currentNode = test.createNode("To verify that submit button is enabled when the data entered is correct.");
		currentNode.assignCategory("SIT");
		String category = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_CATEGORY");
		String userName = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_USER_NAME");
		String[] data = {"","",category,userName,nhomepage.addDaysToCurrentDate(currentDate, 0),nhomepage.addDaysToCurrentDate(currentDate, 0),"",""};
		HashMap<String, String> result = new UserClosingBalanceSpringFeatures(driver).checkUserClosingBalanceReport("channel",domainCode, data);
		Validator.messageCompare(result.get("submitEnabled"), "true");
	}
	
}
