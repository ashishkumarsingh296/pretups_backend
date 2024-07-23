package com.testscripts.sit;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_MODIFY_C2S_TRANSFER_RULE)
public class SIT_ModifyC2STransferRule extends BaseTest  {
	String MasterSheetPath;
	Object[][] transferRuleCategories;
	String assignCategory="SIT";
	
	@DataProvider(name = "modifyTransferRuleData")
	public Object[][] modifyData() throws IOException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][7];
		for (int i = 1; i <= rowCount; i++) {

			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				transferRuleCategories[0][0] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
				transferRuleCategories[0][1] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				transferRuleCategories[0][3] = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				transferRuleCategories[0][4] = i;
				
				transferRuleCategories[0][6] = transferRuleCategories[0][2].toString().split(",")[0];
				
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
				int serviceRowCount = ExcelUtility.getRowCount();
				for (int j = 1; j<=serviceRowCount; j++) {
					String serviceType = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, j);
					if (serviceType.equals(transferRuleCategories[0][6].toString())) {
						transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.NAME, j);
						break;
					}
				}
				
				int rowNum=ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET, transferRuleCategories[0][2].toString());
				int k=rowNum;
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
				for(k=rowNum;k<=ExcelUtility.getRowCount();k++){
					if(ExcelUtility.getCellData(0, ExcelI.NAME, k).equals(transferRuleCategories[0][2]) && 
					   !ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, k).equals("") && 
					   ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, k)!=null){
						transferRuleCategories[0][5] = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, k);
						break;
					}
					else{transferRuleCategories[0][5] = null;}
				}
				break;
			}
		}
		return transferRuleCategories;
	}

	@Test(dataProvider = "modifyTransferRuleData")
	@TestManager(TestKey = "PRETUPS-904") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, int rownum, String subService, String serviceType ) {
		final String methodName = "Test_Modify_C2S_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITMODIFYC2STRFRULE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules(fromDomain, fromCategory, services, requestBearer, subService);
		c2STransferRules.activateC2STransferRules(fromDomain, fromCategory, requestBearer, services ,subService);

		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		if (modifyTransferRuleMsg.equals(actualMsg))
			Assertion.assertPass("Message Validation Successful");
		else
			Assertion.assertPass("Message Validation UnSuccessful");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	//without selecting any Transfer Rule.
	@Test(dataProvider = "modifyTransferRuleData")
	@TestManager(TestKey = "PRETUPS-906") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, int rownum, String subService, String serviceType) {
		final String methodName = "Test_Modify_C2S_Transfer_Rule";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITMODIFYC2STRFRULE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules_null(fromDomain, fromCategory, services, requestBearer);


		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.selectrow");
		if (modifyTransferRuleMsg.equals(actualMsg))
			Assertion.assertPass("Message Validation Successful");
		else
			Assertion.assertPass("Message Validation UnSuccessful");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	//No status selected
	@Test(dataProvider = "modifyTransferRuleData")
	@TestManager(TestKey = "PRETUPS-907") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, int rownum, String subService, String serviceType) {
		final String methodName = "Test_Modify_C2S_Transfer_Rule";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITMODIFYC2STRFRULE3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String status = "";
		String type = "Modify";
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,status, type,subService);


		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.required", MessagesDAO.getLabelByKey("trfrule.addc2stransferrules.label.status"),"1");
		if (modifyTransferRuleMsg.equals(actualMsg))
			Assertion.assertPass("Message Validation Successful");
		else
			Assertion.assertPass("Message Validation UnSuccessful");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	//Verifying Status.
	@Test(dataProvider = "modifyTransferRuleData")
	@TestManager(TestKey = "PRETUPS-908") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, int rownum,String subService, String serviceType ) {
		final String methodName = "Test_Modify_C2S_Transfer_Rule";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITMODIFYC2STRFRULE4").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String status = PretupsI.STATUS_SUSPENDED_LOOKUPS;
		String type = "Modify";
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,status, type,subService);


		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");

		if (modifyTransferRuleMsg.equals(actualMsg))
			Assertion.assertPass("Message Validation Successful");
		else
			Assertion.assertPass("Message Validation UnSuccessful");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	//Reset TransferRule.
	@Test(dataProvider = "modifyTransferRuleData")
	@TestManager(TestKey = "PRETUPS-909") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, int rownum, String subService, String serviceType ) {
		final String methodName = "Test_Modify_C2S_Transfer_Rule";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITMODIFYC2STRFRULE5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String beforeStatus = PretupsI.STATUS_SUSPENDED_LOOKUPS;
		String type = "reset";
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String result = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer, beforeStatus, type,subService);

		if (result.equalsIgnoreCase("true"))
			Assertion.assertPass("Message Validation Successful");
		else
			Assertion.assertPass("Message Validation UnSuccessful");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	//Back button in TransferRule.
	@Test(dataProvider = "modifyTransferRuleData")
	@TestManager(TestKey = "PRETUPS-910") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void f_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, int rownum, String subService, String serviceType ) {
		final String methodName = "Test_Modify_C2S_Transfer_Rule";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITMODIFYC2STRFRULE6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String beforeStatus = PretupsI.STATUS_SUSPENDED_LOOKUPS;
		String type = "back";
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String result = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,beforeStatus, type,subService);

		if (result.equals("true"))
			Assertion.assertPass("Message Validation Successful");
		else
			Assertion.assertPass("Message Validation UnSuccessful");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	//Delete TransferRule.
	@Test(dataProvider = "modifyTransferRuleData")
	@TestManager(TestKey = "PRETUPS-911") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void g_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, int rownum, String subService, String serviceType ) {
		final String methodName = "Test_Modify_C2S_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITMODIFYC2STRFRULE7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String status = PretupsI.STATUS_SUSPENDED_LOOKUPS;
		String type = "delete";
		boolean preRequisite = false;
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,status, type,subService);

		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
		if (modifyTransferRuleMsg.equals(actualMsg))
			Assertion.assertPass("Message Validation Successful");
		else
			Assertion.assertPass("Message Validation UnSuccessful");
		c2STransferRules.addC2STransferRule(fromDomain, fromCategory, serviceType, requestBearer, rownum, preRequisite);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
