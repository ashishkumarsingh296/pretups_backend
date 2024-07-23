package com.testscripts.uap;

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

public class UAP_CommissionProfile extends BaseTest{

	static boolean TestCaseCounter = false;
	String CommProfile;
	String profileName;

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
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to create Commission Profile for " + domainName
				+ " domain and" + categoryName + "category");
		currentNode.assignCategory("UAP");

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String[] result = addCommissionProfile.addCommissionProfile(domainName, categoryName, grade);
		System.out.println(result);
		CommProfile=result[1];
		System.out.println("The Created Commission profile name is : " + CommProfile);

		currentNode = test.createNode("To verify that the proper Message is displayed on successful Commission Profile Creation");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + result[2] + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}


	@Test(dataProvider = "categoryData")
	public void b_viewCommissionProfile(String domainName, String categoryName, String grade)throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}
		Log.startTestCase("Commission Profile - View Commission Profile.");

		CommissionProfile commissionProfile = new CommissionProfile(driver);

		currentNode = test.createNode("To verify that Network Admin is able to view Commission Profile for domain "+domainName+" and category Name "+categoryName);
		currentNode.assignCategory("UAP");

		String actual = commissionProfile.viewCommissionProfile(domainName, categoryName, grade, CommProfile);
		String expected= MessagesDAO.prepareMessageByKey("profile.commissionprofiledetailview.view.heading");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	}




	@Test(dataProvider = "categoryData")
	public void c_modifyCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		Log.startTestCase(this.getClass().getName());
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}


		currentNode = test.createNode("To verify that Network Admin is able to do successful Commission Profile Modification");
		currentNode.assignCategory("UAP");
		String message = commissionProfile.modifyCommissionProfile(domainName, categoryName, grade, CommProfile);
		
		currentNode = test.createNode("To verify that the proper Message is displayed on successful Commission Profile Modification");
		currentNode.assignCategory("UAP");
		
		String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (message.equals(modifyCommProMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + modifyCommProMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	
	@Test(dataProvider = "categoryData")
	public void d_modifyCommissionProfileForFutureDate(String domainName, String categoryName, String grade) throws InterruptedException {

		Log.startTestCase(this.getClass().getName());
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Network Admin is able to do successful Commission Profile Modification for future date");
		currentNode.assignCategory("UAP");
		
		String message = commissionProfile.modifyCommissionProfileForFutureDate(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode("To verify that the proper Message is displayed on successful Commission Profile Modification for future date");
		currentNode.assignCategory("UAP");
		String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (message.equals(modifyCommProMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + modifyCommProMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	

	
	@Test(dataProvider = "categoryData")
	public void e_modifyAdditionalCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		Log.startTestCase(this.getClass().getName());
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}
		
		//test = extent.createTest("[UAP]Commission Profile");
		currentNode = test.createNode("To verify that Network Admin is able to modify Additional Commission Profile");
		currentNode.assignCategory("UAP");
		String message = commissionProfile.modifyAdditionalCommissionProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode("To verify that the proper Message is displayed on successful Additional Commission Profile Modification");
		currentNode.assignCategory("UAP");
		
		
		String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (message.equals(modifyCommProMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + modifyCommProMsg + "] but found [" + message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void f_suspendAdditionalCommProfile(String domainName, String categoryName, String grade)
			throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to suspend Additional Commission Slabs" + CommProfile);
		currentNode.assignCategory("UAP");
		String StatusText = commissionProfile.suspendAdditionalCommProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode("To verify that the proper Message is displayed on successful suspension of Additional Commission Profile ");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (StatusText.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");

		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + StatusText + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	@Test(dataProvider = "categoryData")
	public void g_Modify_RemoveAdditionalCommProfileSlab(String domainName, String categoryName, String grade)
			throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to delete Additional Commission Slabs" + CommProfile);
		currentNode.assignCategory("UAP");
		String StatusText = commissionProfile.deleteAdditionalCommProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode("To verify that the proper Message is displayed on successful removal of Additional Commission Profile Slab");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		if (StatusText.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");

		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + StatusText + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	@Test(dataProvider = "categoryData")
	public void h_deleteCommProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		
		currentNode = test.createNode("To verify that Channel Admin is able to delete Commission Profile" + CommProfile);
		currentNode.assignCategory("UAP");

		String StatusText = commissionProfile.deleteCommProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode("To verify that the proper Message is displayed on successful Commission Profile Deletion");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successdeletemessage");
		if (StatusText.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");

		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + StatusText + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	@Test(dataProvider = "categoryData")
	public void i_commissionProfileCreationWithDuplicateName(String domainName, String categoryName, String grade)	throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is not able to create Commission Profile for " + domainName
				+ " domain and" + categoryName + "category with an existing Profile Name");
		currentNode.assignCategory("UAP");

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String actual = addCommissionProfile.addCommissionProfileWithDuplicateName(domainName, categoryName, grade, CommProfile);
		System.out.println(actual);
		

		currentNode = test.createNode("To verify that the proper Error Message is displayed on Commission Profile Creation with an existing Profile Name");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.commissionprofilenamealreadyexist");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	
		
	@Test(dataProvider = "categoryData")
	public void j_specificGeoDomainCommissionProfileCreation(String domainName, String categoryName, String grade)
			throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to create Commission Profile for " + domainName
				+ " domain and" + categoryName + "category");
		currentNode.assignCategory("UAP");

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String[] result = addCommissionProfile.addCommissionProfileWithSpecificGeography(domainName, categoryName, grade);
		System.out.println(result);
		CommProfile=result[1];

		currentNode = test.createNode("To verify that the proper Message is displayed on successful Commission Profile Creation for specific Geography");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		if (result[2].equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + result[2] + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	
	
	/*
	@Test(dataProvider = "categoryData")
	public void j_blankMandatoryField(String domainName, String categoryName, String grade)
			throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is not able to create Commission Profile if any mandatory field is blank");
		currentNode.assignCategory("UAP");

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String actual = addCommissionProfile.addCommissionProfileWithBlankMandatoryField(domainName, categoryName, grade);
		System.out.println(actual);

		currentNode = test.createNode("To verify that the proper Error Message is displayed when any mandatory field is blank");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.mandatoryApplicableDate","");
		System.out.println(Message);
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	*/
	
	
	 
	
	
	
	@Test(dataProvider = "categoryData")
	public void k_deleteCommProfileAssociatedWithUser(String domainName, String categoryName, String grade) throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		//test = extent.createTest("[UAP]Commission Profile_NegativeTestCase");
		
		
		currentNode = test.createNode("To verify that Channel Admin is able to delete Commission Profile associated with User" );
		currentNode.assignCategory("UAP");

		String StatusText = commissionProfile.deleteCommProfileAssociatedWithUser(domainName, categoryName, grade);
		currentNode = test.createNode("To verify that the proper Error Message is displayed when User tries to delete Commission Profile associated with User ");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.commissionassociatedwithuser");
		if (StatusText.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");

		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + StatusText + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	

	
	
	
	
	/*
	@Test(dataProvider = "categoryData")
	public void l_commissionProfileStatusChange(String domainName, String categoryName, String grade)
			throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Commission Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that Channel Admin is able to change the status of  Commission Profile for " + domainName
				+ " domain and" + categoryName + "category");
		currentNode.assignCategory("UAP");

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		String[] result = commissionProfile.addCommissionProfile(domainName, categoryName, grade);
		System.out.println(result);
		CommProfile=result[1];
		System.out.println("The Created Commission profile name is : " + CommProfile);
		
		String actual = commissionProfile.CommissionProfileStatusChange(domainName, categoryName, grade,CommProfile);

		currentNode = test.createNode("To verify that the proper Message is displayed on successful status change of Commission Profile Creation");
		currentNode.assignCategory("UAP");
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successsuspendmessage");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	
	*/
	
	
	
	
}
