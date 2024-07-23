package com.testscripts.smoke;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.CommissionProfile;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class Smoke_CommissionProfile extends BaseTest {

	static boolean TestCaseCounter = false;

	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		//int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][3];
		categoryData[0][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.GRADE, 1);

		return categoryData;
	}

	@DataProvider(name = "commissionData")
	public Object[][] getCommissionData() {
		String additionalCommission = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][4];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			additionalCommission = ExcelUtility.getCellData(0, ExcelI.ADDITIONAL_COMMISSION, i);
			if (additionalCommission.equals("Y")) {
				categoryData[j][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
				categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
				break;
			}
		}
		return categoryData;
	}

	@Test(dataProvider = "categoryData")
	public void a_commissionProfileCreation(String domainName, String categoryName, String grade)
			throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[Smoke]Add Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to create Commission Profile for " + domainName
				+ " domain and" + categoryName + "category");
		currentNode.assignCategory("Smoke");

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String[] result = addCommissionProfile.addCommissionProfile(domainName, categoryName, grade);

		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Commission Profile Creation");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + result[2] + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	/*@Test(dataProvider = "categoryData")
	public void additionalCommProfileCreation(int rowNum, String domainName, String categoryName, String grade)
			throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("Add Additional Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to create Additional Commission Profile"
				+ domainName + " domain and" + categoryName + "category");
		currentNode.assignCategory("Smoke");

		AddAdditionalCommProfile_GatewaycodeALL AddAdditionalCommProfile_GatewaycodeALL = new AddAdditionalCommProfile_GatewaycodeALL();

		String StatusText[] = AddAdditionalCommProfile_GatewaycodeALL.additionalCommProfileCreation(driver, rowNum,
				domainName, categoryName, grade, profileName);

		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Commission Profile Creation");
		currentNode.assignCategory("Smoke");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (StatusText[1].equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + StatusText + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
*/
	@Test(dataProvider = "categoryData")
	public void c_modifyCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		Log.startTestCase(this.getClass().getName());
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		test = extent.createTest("[Smoke]Commission Profile Modification");
		currentNode = test.createNode("To verify that Network Admin is able to modify Commission Profile");
		currentNode.assignCategory("Smoke");
		String[] result = commissionProfile.addCommissionProfile(domainName, categoryName, grade);
		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Commission Profile Modification");
		currentNode.assignCategory("Smoke");
		String message = commissionProfile.modifyCommissionProfile(domainName, categoryName, grade, result[1]);
		String modifyCommProMsg = MessagesDAO
				.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (message.equals(modifyCommProMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + modifyCommProMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}

		Log.endTestCase(this.getClass().getName());
	}

	@Test(dataProvider = "categoryData")
	public void b_modifyAdditionalCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		Log.startTestCase(this.getClass().getName());
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		test = extent.createTest("[Smoke]Additional Commission Profile Modification");
		currentNode = test.createNode("To verify that Network Admin is able to modify Additional Commission Profile");
		currentNode.assignCategory("Smoke");
		String[] result = commissionProfile.addCommissionProfile(domainName, categoryName, grade);
		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Additional Commission Profile Modification");
		currentNode.assignCategory("Smoke");
		String message = commissionProfile.modifyAdditionalCommissionProfile(domainName, categoryName, grade, result[1]);
		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Additional Commission Profile Modification");
		currentNode.assignCategory("Smoke");

		String modifyCommProMsg = MessagesDAO
				.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (message.equals(modifyCommProMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + modifyCommProMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}

		Log.endTestCase(this.getClass().getName());
	}

	@Test(dataProvider = "categoryData")
	public void e_deleteCommProfile(String domainName, String categoryName, String grade) throws InterruptedException {
		
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		String[] result = commissionProfile.addCommissionProfile(domainName, categoryName, grade);
		test = extent.createTest("[Smoke]Commission Profile Deletion");
		currentNode = test
				.createNode("To verify that Channel Admin is able to delete Commission Profile" + result[1]);
		currentNode.assignCategory("Smoke");

		String StatusText = commissionProfile.deleteCommProfile(domainName, categoryName, grade, result[1]);
		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Commission Profile Deletion");
		currentNode.assignCategory("Smoke");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successdeletemessage");
		if (StatusText.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");

		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + StatusText + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	@Test(dataProvider = "categoryData")
	public void d_deleteAdditionalCommProfile(String domainName, String categoryName, String grade)
			throws InterruptedException {
		
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		String[] result = commissionProfile.addCommissionProfile(domainName, categoryName, grade);
		test = extent.createTest("[Smoke]Additional Commission Profile Deletion");
		currentNode = test
				.createNode("To verify that Channel Admin is able to delete Additional Commission Slabs" + result[1]);
		currentNode.assignCategory("Smoke");
		String StatusText = commissionProfile.deleteAdditionalCommProfile(domainName, categoryName, grade, result[1]);
		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Additional Commission Profile Creation");
		currentNode.assignCategory("Smoke");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (StatusText.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");

		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + StatusText + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

}
