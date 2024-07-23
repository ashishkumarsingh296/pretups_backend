package com.testscripts.uap;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.BarUnbar;
import com.Features.ChannelUser;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class UAP_BarUnbarUser extends BaseTest{
	String msisdn;
	int Rownum =1;
	String LoginID;
	String MSISDN;
	String PASSWORD;
	String EXTCODE;
	String CONFIRMPASSWORD;
	String NEWPASSWORD;
	String UserName;
	String UserName1;
	//static WebDriver driver;
	static String homepage1;	
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> channelresultMap;
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider = "BarUnBarFeed")
	public void channelUserCreation( String Domain, String Parent, String Category, String geotype,String module, String userType, String barringType) throws InterruptedException {
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[UAP]Bar-Unbar Channel User");
			TestCaseCounter = true;
		}
		
		
		
		 ChannelUser channelUserLogic= new ChannelUser(driver);
		/* 
		 * Test Case Number 1: Channel User Initiation
		 */
		 
		
		
		currentNode=test.createNode("To verify that Channel Admin is able to initiate " + Category+" category Channel user via parent Category "+Parent+" .");
		currentNode.assignCategory("Smoke");
		channelresultMap=channelUserLogic.channelUserInitiate(1, Domain, Parent, Category, geotype);
		
		
		
		 // Test Case Number 2: Message Validation
		 
		currentNode=test.createNode("To verify that valid message is displayed after initiating "+Category+" category channel user.");
		currentNode.assignCategory("Smoke");
		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
		String intChnlInitiateMsg;
		if(APPLEVEL.equals("0"))
		{	
			intChnlInitiateMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", channelresultMap.get("uName"));	
		}else{
			intChnlInitiateMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", channelresultMap.get("uName"));
		}
		
			if (channelresultMap.get("channelInitiateMsg").equals(intChnlInitiateMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
			currentNode.log(Status.FAIL, "Expected [" + intChnlInitiateMsg + "] but found [" + channelresultMap.get("channelInitiateMsg") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		
		
		if(APPLEVEL.equals("2"))
		{
		currentNode=test.createNode("To verify that Channel Admin is able to approve level 1 " + Category+" category Channel user.");
		currentNode.assignCategory("Smoke");
		channelresultMap=channelUserLogic.approveLevel1_ChannelUser();
		
		currentNode=test.createNode("To verify that valid message is displayed after approval level 1 of "+Category+" category channel user.");
		currentNode.assignCategory("Smoke");
		String intChnlApprove1Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagerequiredleveltwoapproval", channelresultMap.get("uName"));
				if (channelresultMap.get("channelApprovelevel1Msg").equals(intChnlApprove1Msg))
				currentNode.log(Status.PASS, "Message Validation Successful");
				else {
				currentNode.log(Status.FAIL, "Expected [" + intChnlApprove1Msg + "] but found [" + channelresultMap.get("channelApprovelevel1Msg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
				}
		
		
		currentNode=test.createNode("To verify that Channel Admin is able to approve level 2 " + Category+" category Channel user.");
		currentNode.assignCategory("Smoke");
		channelresultMap=channelUserLogic.approveLevel2_ChannelUser();
		
		currentNode=test.createNode("To verify that valid message is displayed after approval level 2 of "+Category+" category channel user.");
		currentNode.assignCategory("Smoke");
		String intChnlApprove2Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
				if (channelresultMap.get("channelApprovelevel2Msg").equals(intChnlApprove2Msg))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + intChnlApprove2Msg + "] but found [" + channelresultMap.get("channelApprovelevel2Msg") + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}
		}
		else if(APPLEVEL.equals("1")){
		
			currentNode=test.createNode("To verify that Channel Admin is able to approve " + Category+" category Channel user.");
			currentNode.assignCategory("Smoke");
			channelresultMap=channelUserLogic.approveLevel1_ChannelUser();
		
			currentNode=test.createNode("To verify that valid message is displayed after approval of "+Category+" category channel user.");
			currentNode.assignCategory("Smoke");
			String intChnlApproveMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
			//assertEquals(channelresultMap.get("channelApprovelevel1Msg"), intChnlApproveMsg);
				if (channelresultMap.get("channelApproveMsg").equals(intChnlApproveMsg))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + intChnlApproveMsg + "] but found [" + channelresultMap.get("channelApproveMsg") + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}
			
		}else{
			Log.info("Approval not required.");
		}
		
		String msisdn = channelresultMap.get("MSISDN");
		System.out.println("***************************The MSISDN is:" +msisdn+ "************************************");
		
		
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Bar User for module: "+module+" and user type "+userType);
		currentNode.assignCategory("UAP");

		BarUnbar barUser = new BarUnbar(driver);
		barUser.barringUser(module, userType, msisdn);
		String actual= new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected= MessagesDAO.prepareMessageByKey("subscriber.barreduser.add.mobile.success",msisdn);

		Validator.messageCompare(actual, expected);
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Un-Bar User for module: "+module+" and user type "+userType);
		currentNode.assignCategory("UAP");

		BarUnbar unBarUser = new BarUnbar(driver);
		unBarUser.unBarringUser(module, userType, msisdn);
		String actual2= new AddChannelUserDetailsPage(driver).getActualMessage();

		//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String expected2= MessagesDAO.prepareMessageByKey("subscriber.unbaruser.add.success",msisdn);

		Validator.messageCompare(actual2, expected2);
	
		}

/*
	@Test(dataProvider="BarUnBarFeed")
	public void barringUser(String module, String userType, String barringType, String categoryName, String domain, String ParentName, String geography) throws InterruptedException{
		Log.startTestCase(this.getClass().getName());

		//test = extent.createTest("O2C Withdrawal: " +ProductType);
		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]BarUnBar module");
			TestCaseCounter = true;
		}


		//test = extent.createTest("Barring user: " +module+" "+userType);

		currentNode = test.createNode("To verify that Channel Admin is able to perform Bar User for module: "+module+" and user type "+userType);
		currentNode.assignCategory("Smoke");

		BarUnbar barUser = new BarUnbar(driver);
		HashMap<String, String> dataMap=barUser.barringUser(module, userType, barringType, categoryName, domain, ParentName, geography);
		msisdn= dataMap.get("MSISDN");
		System.out.println(msisdn);
		String actual= dataMap.get("MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("subscriber.barreduser.add.mobile.success");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}

	@Test(dataProvider="BarUnBarFeed")
	public void unBarringUser(String module, String userType, String barringType, String categoryName, String domain, String ParentName, String geography){
		Log.startTestCase(this.getClass().getName());

		//test = extent.createTest("O2C Withdrawal: " +ProductType);
		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]BarUnBar module");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to perform Un-Bar User for module: "+module+" and user type "+userType);
		currentNode.assignCategory("Smoke");

		BarUnbar unBarUser = new BarUnbar(driver);
		String actual= unBarUser.unBarringUser(module, userType, barringType, categoryName, msisdn);


		//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String expected= MessagesDAO.prepareMessageByKey("subscriber.unbaruser.add.success");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	}
*/
	@DataProvider(name = "BarUnBarFeed")
	public Object[][] BarUnBarUserFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		Object[][] categoryData = new Object[1][7];
		
		
		categoryData[0][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, 1);
		categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		categoryData[0][3] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, 1);
		categoryData[0][4] = "Channel to Subscriber";
		categoryData[0][5] = "Sender";
		categoryData[0][6] = "Barred By Retailer";
		return categoryData;
	}

}
