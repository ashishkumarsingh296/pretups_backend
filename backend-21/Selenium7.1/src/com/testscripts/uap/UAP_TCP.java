package com.testscripts.uap;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.TransferControlProfile;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class UAP_TCP extends BaseTest{
	
	static boolean TestCaseCounter = false;
	String TCPName;
	String profile_ID;
	
	HashMap<String, String> dataMap;
	
	
	@DataProvider(name="dataProvider")
	public Object[][] TestDataFeed() throws IOException{
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		Object [][] categoryData=new Object[1][3];
			categoryData[0][0]=1;
			categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
			categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		return categoryData;
	}
	
	//Adding Category and Channel Level TCP 

	@Test(dataProvider = "dataProvider")
	public void a_TransferControlProfileCreation(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Transfer Control Profile");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case Number 1 - Category Level TCP Creation
		 */
		currentNode=test.createNode("To verify that Super Admin is able to create Category Level TCP for " + categoryName + " category.");
		currentNode.assignCategory("UAP");	
		TransferControlProfile.createCategoryLevelTransferControlProfile(rowNum, domainName, categoryName);
		
		/*
		 * Test Case Number 2 - Channel Level TCP Creation
		 */
		currentNode=test.createNode("To verify that Network Admin is able to create Channel Level TCP for " + categoryName + " category.");
		currentNode.assignCategory("UAP");
		
		dataMap=(HashMap<String, String>) TransferControlProfile.createChannelLevelTransferControlProfile(rowNum, domainName, categoryName);
		TCPName=dataMap.get("TCP_Name");
		profile_ID = dataMap.get("profile_ID");
		System.out.println("Transfer Control Profile Name is : " + TCPName);
		
		
		
		Log.endTestCase(this.getClass().getName());
	}

	
	/*
	 * Test Case Number 3 - Category Level TCP Suspend
	 */
	
	@Test(dataProvider = "dataProvider")
	public void b_TransferControlProfileSuspend(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Transfer Control Profile");
			TestCaseCounter = true;
		}
	
		currentNode = test.createNode("To verify that Network Admin  is able to suspend the created TCP for " + categoryName + " category.");
		currentNode.assignCategory("UAP");
		String actual =TransferControlProfile.channelLevelTransferControlProfileSuspend(rowNum, domainName, categoryName, TCPName ,profile_ID);
		
		
		String expected= MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
	}
	
	
	
	
	@Test(dataProvider = "dataProvider")
	public void c_TransferControlProfileActive(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Transfer Control Profile");
			TestCaseCounter = true;
		}
	
		currentNode = test.createNode("To verify that Network Admin  is able to activate the suspended TCP for " + categoryName + " category.");
		currentNode.assignCategory("UAP");
		String actual =TransferControlProfile.channelLevelTransferControlProfileActive(rowNum, domainName, categoryName, TCPName, profile_ID);
		
		//String actual = dataMap.get("ACTUALMESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
	}
	
	
	@Test(dataProvider = "dataProvider")
	public void d_ChannelLevelTransferControlProfileModify(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Transfer Control Profile");
			TestCaseCounter = true;
		}
	
		currentNode = test.createNode("To verify that Network Admin  is able to modify TCP for " + categoryName + " category.");
		currentNode.assignCategory("UAP");
		String actual =TransferControlProfile.modifyChannelLevelTransferProfile(rowNum, domainName, categoryName, TCPName, profile_ID);
		
		
		String expected= MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
	}
	
	
	
	
	
	
	
	
	
	@Test(dataProvider = "dataProvider")
	public void e_DeleteChannelLevelTCP(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Transfer Control Profile");
			TestCaseCounter = true;
		}
	
		currentNode = test.createNode("To verify that Network Admin  is able to delete TCP of " + categoryName + " category to Default Profile");
		currentNode.assignCategory("UAP");
		String actual =TransferControlProfile.deleteChannelLevelTransferProfile(rowNum, domainName, categoryName, TCPName, profile_ID);
		
		currentNode = test.createNode("To verify that Proper Message is displayed on successful TCP deletion for service " + categoryName + " category");
		currentNode.assignCategory("UAP");
				
		
		String expected= MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.deletesuccess");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
	}
	
	
	
	
	@Test(dataProvider = "dataProvider")
	public void f_ChannelLevelTransferControlProfileDefault(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Transfer Control Profile");
			TestCaseCounter = true;
		}
		
		currentNode=test.createNode("To verify that Network Admin is able to create Default Channel Level TCP for " + categoryName + " category.");
		currentNode.assignCategory("UAP");
		
		dataMap=(HashMap<String, String>) TransferControlProfile.createChannelLevelTransferControlProfile(rowNum, domainName, categoryName);
		TCPName=dataMap.get("TCP_Name");
		profile_ID = dataMap.get("profile_ID");
		System.out.println("Transfer Control Profile Name is : " + TCPName);
	
		currentNode = test.createNode("To verify that Network Admin  is able to modify TCP for " + categoryName + " category to Default Profile");
		currentNode.assignCategory("UAP");
		String actual =TransferControlProfile.defaultChannelLevelTransferProfile(rowNum, domainName, categoryName, TCPName, profile_ID);
		
		
		String expected= MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
	}
	
	
	@Test(dataProvider = "dataProvider")
	public void g_Negative_DeleteDefaultChannelLevelTCP(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Transfer Control Profile");
			TestCaseCounter = true;
		}
	
		currentNode = test.createNode("To verify that Network Admin  is not able to delete Default TCP for " + categoryName + " category to Default Profile");
		currentNode.assignCategory("UAP");
		String actual =TransferControlProfile.deleteDefaultChannelLevelTransferProfile(rowNum, domainName, categoryName, TCPName, profile_ID);
		
		
		String expected= MessagesDAO.prepareMessageByKey("profile.modtrfprofile.msg.cannotdeleteprofile");

		if (actual.equals(expected.trim()))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
	}

}
