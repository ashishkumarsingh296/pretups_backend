/**
 * 
 */
package com.testscripts.sit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.PrivateRecharge;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class SIT_PrivateRecharge extends BaseTest{

	static boolean TestCaseCounter = false;
	String actual;
	String expected;
	
	@Test
	public void a_privateRcRegistration(){
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Private Recharge");
			TestCaseCounter = true;
		}
		PrivateRecharge prvtRc = new PrivateRecharge(driver);
    	currentNode=test.createNode("To verify that Private recharge registration for subscriber is successful with generation type as MANUAL.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration (type:Manual)");
		prvtRc.privateRechargeRegistration(true);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeRegistration.register.success");
		Validator.messageCompare(actual, expected);
		
		currentNode=test.createNode("To verify that Private recharge registration for subscriber is successful with generation type as AUTO.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration (type:Auto)");
		prvtRc.privateRechargeRegistration(false);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeRegistration.register.success");
		Validator.messageCompare(actual, expected);
		
		currentNode=test.createNode("To Verify Private Code registration fails through web if  Subscriber MSISDN is already registered.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration (type:Auto)");
		String msisdn = DBHandler.AccessHandler.fetchSubscriberMSISDNRandomAlias();
		try{prvtRc.privateRechargeRegistration(true,msisdn,UniqueChecker.UC_SubsSID());
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeRegistration.register.success");
		}catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.error.registeredMsisdn");	}
		Validator.messageCompare(actual, expected);
		
		
	}
	
	
	@Test
	public void b_privateRcModification(){
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Private Recharge");
			TestCaseCounter = true;
		}
		PrivateRecharge prvtRc = new PrivateRecharge(driver);

		currentNode=test.createNode("To verify that Private recharge modification for subscriber is successful using generation type as MANUAL.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Modifcation (type:Manual)");
		prvtRc.privateRechargemodification(true);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeModification.modify.success");
		Validator.messageCompare(actual, expected);

		currentNode=test.createNode("To verify that Private recharge modification for subscriber is successful using generation type as AUTO.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Modification(type:Auto)");
		prvtRc.privateRechargemodification(false);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeModification.modify.success");
		Validator.messageCompare(actual, expected);	
	}
	
	@Test
	public void c_privateRcEnquiry(){
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Private Recharge");
			TestCaseCounter = true;
		}
		PrivateRecharge prvtRc = new PrivateRecharge(driver);
		
		currentNode=test.createNode("To verify that Private recharge enquiry for subscriber is successful.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Enquiry");
		prvtRc.privateRechargeEnquiry();	
	}
	
	@Test
	public void d_privateRcDeactivation(){
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Private Recharge");
			TestCaseCounter = true;
		}
		PrivateRecharge prvtRc = new PrivateRecharge(driver);
		
		currentNode=test.createNode("To verify that Private recharge deactivation is successful.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Deactivation");
		prvtRc.privateRechargeDeactivation();
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeConfirmDeactivation.delete.success");
		Validator.messageCompare(actual, expected);
	}

	
	//Perform private recharge.
	@Test(dataProvider = "categoryData")
	public void e_privateRecharge(String ParentCategory, String FromCategory, String PIN,String service)
			throws IOException, InterruptedException {

		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
		test = extent.createTest("[SIT]Private Recharge");
		TestCaseCounter = true;
		}
		PrivateRecharge prvtRc = new PrivateRecharge(driver);
		currentNode = test.createNode("To verify that " + FromCategory + " category user is able to perform C2S(Private) Recharge using SID of subscriber.");
		currentNode.assignCategory("SIT");
		
		prvtRc.performC2STransferToSID(ParentCategory, FromCategory, PIN, service);
		Log.endTestCase(this.getClass().getName());
	}
	
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed1() {
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
/*
 * Array list to store Categories for which Customer Recharge is allowed
 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(CustomerRechargeCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}

/*
 * Counter to count number of users exists in channel users hierarchy sheet 
 * of Categories for which O2C transfer is allowed
 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				userCounter++;
			}
		}

/*
 * Store required data of 'O2C transfer allowed category' users in Object
 */
		Object[][] Data = new Object[userCounter][4];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][3] = CustomerRechargeCode;
				j++;
			}
		}
					
	return Data;
	}
	
	
}
