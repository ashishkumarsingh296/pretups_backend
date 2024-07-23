package com.testscripts.uap;

import java.io.IOException;
import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.UAP_MODIFY_C2S_TRANSFER_RULE)
public class UAP_ModifyC2STransferRule extends BaseTest {
	String MasterSheetPath;
	Object[][] transferRuleCategories;
	String assignCategory="UAP";
	
	@DataProvider(name = "modifyTransferRuleData")
	public Object[][] modifyData() throws IOException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][6];
		for (int i = 1; i <= rowCount; i++) {

			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				transferRuleCategories[0][0] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
				transferRuleCategories[0][1] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				transferRuleCategories[0][3] = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				
				transferRuleCategories[0][5] = transferRuleCategories[0][2].toString().split(",")[0];
				
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
				int serviceRowCount = ExcelUtility.getRowCount();
				for (int j = 1; j<=serviceRowCount; j++) {
					String serviceType = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, j);
					if (serviceType.equals(transferRuleCategories[0][5].toString())) {
						transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.NAME, j);
						break;
					}
				}
				
				int C2SServiceSheetRowCount = ExcelUtility.getRowCount(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
				
				for (int C2SServiceSheetCounter = 0; C2SServiceSheetCounter <= C2SServiceSheetRowCount; C2SServiceSheetCounter++) {
					if (ExcelUtility.getCellData(0, ExcelI.NAME, C2SServiceSheetCounter).equals(transferRuleCategories[0][2]) && !BTSLUtil.isNullString(ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, C2SServiceSheetCounter))) {
						transferRuleCategories[0][4] = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, C2SServiceSheetCounter);
						break;
					}
				}
				break;
			}
		}
		return transferRuleCategories;
	}

	@Test(dataProvider = "modifyTransferRuleData")
	 @TestManager(TestKey = "PRETUPS-301") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer, String subService, String serviceType ) {
		final String methodName = "Test_modifyC2STransferRules";
        Log.startTestCase(methodName, fromDomain, fromCategory, services, requestBearer, subService);
        
		//test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SMODIFYC2STRFRULE1").getModuleCode());
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SMODIFYC2STRFRULE1").getExtentCase(),fromCategory));
		currentNode.assignCategory(assignCategory);

		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules(fromDomain, fromCategory, services, requestBearer,subService);
		c2STransferRules.activateC2STransferRules(fromDomain, fromCategory, requestBearer, services,subService);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SMODIFYC2STRFRULE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		Assertion.assertEquals(actualMsg, modifyTransferRuleMsg);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		//c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer, PretupsI.STATUS_ACTIVE_LOOKUPS, "Modify");
	}
}
