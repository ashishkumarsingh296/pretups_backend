/**
 * 
 */
package com.testscripts.sit;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.PrivateRecharge;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.AutomationException;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author lokesh.kontey
 *
 */
@ModuleManager(name = Module.SIT_PRIVATE_RECHARGE)
public class SIT_PrivateRecharge extends BaseTest{
	String actual;
	String expected;
	
	@Test
	@TestManager(TestKey = "PRETUPS-815") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_privateRcRegistration() throws Exception{
		final String methodName = "Test_Private_Recharge";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1=_masterVO.getCaseMasterByID("SITPRVTRECHARGE1");
		CaseMaster CaseMaster2=_masterVO.getCaseMasterByID("SITPRVTRECHARGE2");
		CaseMaster CaseMaster3=_masterVO.getCaseMasterByID("SITPRVTRECHARGE3");
		CaseMaster CaseMaster4=_masterVO.getCaseMasterByID("SITPRVTRECHARGE4");

		PrivateRecharge prvtRc = new PrivateRecharge(driver);
		RandomGeneration randstr = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
    	currentNode=test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration (type:Manual)");
		prvtRc.privateRechargeRegistration(true);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeRegistration.register.success");
		Assertion.assertEquals(actual, expected);
		
		currentNode=test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration (type:Auto)");
		prvtRc.privateRechargeRegistration(false);
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeRegistration.register.success");
		Assertion.assertEquals(actual, expected);
		
		currentNode=test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration (type:Auto)");
		String msisdn = DBHandler.AccessHandler.fetchSubscriberMSISDNRandomAlias("PRE");
		try{prvtRc.privateRechargeRegistration(true,msisdn,UniqueChecker.UC_SubsSID());
		actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeRegistration.register.success");
		}catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.error.registeredMsisdn");
			}
			Assertion.assertEquals(actual, expected);
		
		currentNode=test.createNode(CaseMaster4.getExtentCase());
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration");
		String prefix = new UniqueChecker().UC_PrefixData();
		try {
			if(prefix==null)
				throw new AutomationException("All Prefixes Consumed."); 

			String subsmsisdn = prefix + randstr.randomNumeric(gnMsisdn.generateMSISDN());
			prvtRc.privateRechargeRegistration(true,subsmsisdn,UniqueChecker.UC_SubsSID());
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeRegistration.register.success");
		} catch(AutomationException ex) {
			Assertion.assertSkip("All Network Prefixes are Consumed.");
		} catch(Exception e) {
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.error.nonetworkprefix");
			}
		Assertion.assertEquals(actual, expected);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-854") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_privateRcModification(){
		final String methodName = "Test_Private_Recharge";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster5=_masterVO.getCaseMasterByID("SITPRVTRECHARGE5");
		CaseMaster CaseMaster6=_masterVO.getCaseMasterByID("SITPRVTRECHARGE6");
		CaseMaster CaseMaster7=_masterVO.getCaseMasterByID("SITPRVTRECHARGE7");

		PrivateRecharge prvtRc = new PrivateRecharge(driver);

		currentNode=test.createNode(CaseMaster5.getExtentCase());
		currentNode.assignCategory("SIT");
		String msisdn = DBHandler.AccessHandler.fetchSubscriberMSISDNRandomAlias("PRE");
		ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Modifcation (type:Manual)");
		
		if (UserAccess.getRoleStatus(RolesI.PRIVATE_RECH_MOD)) {
			prvtRc.privateRechargemodification(true,msisdn);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeModification.modify.success");
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip("Private Recharge Modification is not available in system as per provided role sheet.");
		}

		currentNode=test.createNode(CaseMaster6.getExtentCase());
		currentNode.assignCategory("SIT");
		if (UserAccess.getRoleStatus(RolesI.PRIVATE_RECH_MOD)) {
			msisdn = DBHandler.AccessHandler.fetchSubscriberMSISDNRandomAlias("PRE");
			ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Modification(type:Auto)");
			prvtRc.privateRechargemodification(false,msisdn);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeModification.modify.success");
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip("Private Recharge Modification is not available in system as per provided role sheet.");
		}
		
		currentNode=test.createNode(CaseMaster7.getExtentCase());
		currentNode.assignCategory("SIT");
		if (UserAccess.getRoleStatus(RolesI.PRIVATE_RECH_MOD)) {
			msisdn=UniqueChecker.UC_MSISDN_ALIAS("PRE");
			ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Modification(type:Auto)");
			try{prvtRc.privateRechargemodification(false,msisdn);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeModification.modify.success");}
			catch(Exception e){
				actual = new AddChannelUserDetailsPage(driver).getActualMessage();
				expected = MessagesDAO.getLabelByKey("privaterecharge.error.msisdn.doesnot.exist");
			}
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip("Private Recharge Modification is not available in system as per provided role sheet.");
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-860") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_privateRcEnquiry(){
		final String methodName = "Test_Private_Recharge";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster8=_masterVO.getCaseMasterByID("SITPRVTRECHARGE8");
		CaseMaster CaseMaster9=_masterVO.getCaseMasterByID("SITPRVTRECHARGE9");

		PrivateRecharge prvtRc = new PrivateRecharge(driver);
		
		currentNode=test.createNode(CaseMaster8.getExtentCase());
		currentNode.assignCategory("SIT");
		String msisdn = DBHandler.AccessHandler.fetchSubscriberMSISDNRandomAlias("PRE");
		if (UserAccess.getRoleStatus(RolesI.PRIVATE_RECH_ENQUIRY)) {
			ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Enquiry");
			prvtRc.privateRechargeEnquiry(msisdn);
		} else {
			Assertion.assertSkip("Private Recharge Enquiry is not available in system as per provided role sheet.");
		}
			
		currentNode=test.createNode(CaseMaster9.getExtentCase());
		currentNode.assignCategory("SIT");
		if (UserAccess.getRoleStatus(RolesI.PRIVATE_RECH_ENQUIRY)) {
			ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Enquiry");
			msisdn=UniqueChecker.UC_MSISDN_ALIAS("PRE");
			try{prvtRc.privateRechargeEnquiry(msisdn);
			actual="Enquiry done successfully";
			expected = MessagesDAO.getLabelByKey("privaterecharge.error.msisdn.doesnot.exist");}
			catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.error.msisdn.doesnot.exist");}
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip("Private Recharge Enquiry is not available in system as per provided role sheet.");
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-862") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_privateRcDeactivation(){
		final String methodName = "Test_Private_Recharge";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster10=_masterVO.getCaseMasterByID("SITPRVTRECHARGE10");
		CaseMaster CaseMaster11=_masterVO.getCaseMasterByID("SITPRVTRECHARGE11");

		PrivateRecharge prvtRc = new PrivateRecharge(driver);
		
		currentNode=test.createNode(CaseMaster10.getExtentCase());
		currentNode.assignCategory("SIT");
		String msisdn = DBHandler.AccessHandler.fetchSubscriberMSISDNRandomAlias("PRE");
		if (UserAccess.getRoleStatus(RolesI.PRIVATE_RECH_DEACTIVATION)) {
			ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Deactivation");
			prvtRc.privateRechargeDeactivation(msisdn);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeConfirmDeactivation.delete.success");
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip("Private Recharge Deactivation is not available in system as per provided role sheet.");
		}
		
		currentNode=test.createNode(CaseMaster11.getExtentCase());
		currentNode.assignCategory("SIT");
		if (UserAccess.getRoleStatus(RolesI.PRIVATE_RECH_DEACTIVATION)) {
			ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Deactivation");
			msisdn=UniqueChecker.UC_MSISDN_ALIAS("PRE");
			try{prvtRc.privateRechargeDeactivation(msisdn);
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.getLabelByKey("privaterecharge.privateRechargeConfirmDeactivation.delete.success");
			}
			catch(Exception e){
			actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			expected = MessagesDAO.prepareMessageByKey("privaterecharge.error.noinfo",msisdn,"null");}
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip("Private Recharge Deactivation is not available in system as per provided role sheet.");
			}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	//Perform private recharge.
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-864") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_privateRecharge(String ParentCategory, String FromCategory, String PIN,String service)
			throws IOException, InterruptedException {

		final String methodName = "Test_Private_Recharge";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster12=_masterVO.getCaseMasterByID("SITPRVTRECHARGE12");

		PrivateRecharge prvtRc = new PrivateRecharge(driver);
		currentNode = test.createNode(MessageFormat.format(CaseMaster12.getExtentCase(), FromCategory));
		currentNode.assignCategory("SIT");
		if(CommonUtils.roleCodeExistInLinkSheet(RolesI.C2SRECHARGE, FromCategory)){
			ExtentI.Markup(ExtentColor.BLUE, "Private Recharge Registration (type:Manual)");
			String SID = prvtRc.privateRechargeRegistration(true);
			ExtentI.Markup(ExtentColor.BLUE, "Now performing Private Recharge after Registration.");
			prvtRc.performC2STransferToSID(ParentCategory, FromCategory, PIN, service,SID);}
		else{currentNode.log(Status.SKIP, "Recharge is not allowed through WEB as RoleCode is not present in LinksSheet");}
		Log.endTestCase(methodName);
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
