package com.testscripts.uap;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.TransferControlProfile;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.UAP_TCP)
public class UAP_TCP extends BaseTest {

	static boolean TestCaseCounter = false;
	String TCPName;
	String profile_ID;
	String assignCategory = "UAP";
	static String moduleCode;
	HashMap<String, String> dataMap;

	@DataProvider(name = "dataProvider")
	public Object[][] TestDataFeed() throws IOException {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		Object[][] categoryData = new Object[1][3];
		categoryData[0][0] = 1;
		categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		return categoryData;
	}

	// Adding Category and Channel Level TCP

	@Test(dataProvider = "dataProvider")
	@TestManager(TestKey = "PRETUPS-359") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_TransferControlProfileCreation(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_TransferControlProfileCreation";
		Log.startTestCase(methodName);

		moduleCode = "[" + assignCategory + "]" + _masterVO.getCaseMasterByID("UTRFCNTRLPRF1").getModuleCode();
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		/*
		 * Test Case Number 1 - Category Level TCP Creation
		 */
		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("PTRFCNTRLPRF1").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);
		TransferControlProfile.createCategoryLevelTransferControlProfile(rowNum, domainName, categoryName);

		/*
		 * Test Case Number 2 - Channel Level TCP Creation
		 */
		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("PTRFCNTRLPRF2").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);

		dataMap = (HashMap<String, String>) TransferControlProfile.createChannelLevelTransferControlProfile(rowNum,
				domainName, categoryName);
		TCPName = dataMap.get("TCP_Name");
		profile_ID = dataMap.get("profile_ID");
		System.out.println("Transfer Control Profile Name is : " + TCPName);

		/*
		 * Test Case Number 2.1 - Channel Level TCP Creation (Make this twice) to test
		 * scenario.
		 */
		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("PTRFCNTRLPRF2").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);

		dataMap = (HashMap<String, String>) TransferControlProfile.createChannelLevelTransferControlProfile(rowNum,
				domainName, categoryName);
		TCPName = dataMap.get("TCP_Name");
		profile_ID = dataMap.get("profile_ID");
		System.out.println("Transfer Control Profile Name is : " + TCPName);

		Log.endTestCase(methodName);
	}

	/*
	 * Test Case Number 3 - Category Level TCP Suspend
	 */

	@Test(dataProvider = "dataProvider")
	@TestManager(TestKey = "PRETUPS-360") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_TransferControlProfileSuspend(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_TransferControlProfileSuspend";
		Log.startTestCase(methodName);

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF1").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);
		String actual = TransferControlProfile.channelLevelTransferControlProfileSuspend(rowNum, domainName,
				categoryName, TCPName, profile_ID);

		String expected = MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "dataProvider")
	@TestManager(TestKey = "PRETUPS-361") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_TransferControlProfileActive(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_TransferControlProfileActive";
		Log.startTestCase(methodName);

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF2").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);
		String actual = TransferControlProfile.channelLevelTransferControlProfileActive(rowNum, domainName,
				categoryName, TCPName, profile_ID);

		// String actual = dataMap.get("ACTUALMESSAGE");
		String expected = MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "dataProvider")
	@TestManager(TestKey = "PRETUPS-362") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void d_ChannelLevelTransferControlProfileModify(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_ChannelLevelTransferControlProfileModify";
		Log.startTestCase(methodName);

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF3").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);
		String actual = TransferControlProfile.modifyChannelLevelTransferProfile(rowNum, domainName, categoryName,
				TCPName, profile_ID);

		String expected = MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "dataProvider")
	@TestManager(TestKey = "PRETUPS-363") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void e_DeleteChannelLevelTCP(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_DeleteChannelLevelTCP";
		Log.startTestCase(methodName);

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF4").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);
		String actual = TransferControlProfile.deleteChannelLevelTransferProfile(rowNum, domainName, categoryName,
				TCPName, profile_ID);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF5").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);

		String expected = MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.deletesuccess");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "dataProvider")
	@TestManager(TestKey = "PRETUPS-364") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void f_ChannelLevelTransferControlProfileDefault(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_ChannelLevelTransferControlProfileDefault";
		Log.startTestCase(methodName);

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF6").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);

		dataMap = (HashMap<String, String>) TransferControlProfile.createChannelLevelTransferControlProfile(rowNum,
				domainName, categoryName);
		TCPName = dataMap.get("TCP_Name");
		profile_ID = dataMap.get("profile_ID");
		System.out.println("Transfer Control Profile Name is : " + TCPName);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF7").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);
		String actual = TransferControlProfile.defaultChannelLevelTransferProfile(rowNum, domainName, categoryName,
				TCPName, profile_ID);

		String expected = MessagesDAO.prepareMessageByKey("profile.transferprofileaction.msg.successupdate");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "dataProvider")
	@TestManager(TestKey = "PRETUPS-365") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void g_Negative_DeleteDefaultChannelLevelTCP(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_DeleteDefaultChannelLevelTCP";
		Log.startTestCase(methodName);

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("UTRFCNTRLPRF8").getExtentCase(), categoryName));
		currentNode.assignCategory(assignCategory);
		String actual = TransferControlProfile.deleteDefaultChannelLevelTransferProfile(rowNum, domainName,
				categoryName, TCPName, profile_ID);

		String expected = MessagesDAO.prepareMessageByKey("profile.modtrfprofile.msg.cannotdeleteprofile");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
