package com.testscripts.uap;

import org.testng.annotations.Test;

import com.Features.C2STransferRules;
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

@ModuleManager(name = Module.UAP_ADD_C2S_TRANSFER_RULE)
public class UAP_AddC2STransferRule extends BaseTest{
	String MasterSheetPath;

	@Test
	@TestManager(TestKey = "PRETUPS-296") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void addC2STransferRules() {
		final String methodName = "Test_addC2STransferRules";
        Log.startTestCase(methodName);

		//test = extent.createTest("[UAP]"+_masterVO.getCaseMasterByID("SC2STRFRULE1").getModuleCode());
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SC2STRFRULE1").getExtentCase());
		currentNode.assignCategory("UAP");

		C2STransferRules c2STransferRules = new C2STransferRules(driver);

		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		String fromDomain = null;
		String fromCategory = null;
		String services = null;
		String requestBearer = null;
		int rownum = 0;

		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {

			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				fromDomain = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
				fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				requestBearer = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				rownum = i;
				break;
			}
		}

		Object[][] c2STransferDataObject;
		String actualMsg = null;
		boolean preRequisite=false;
		c2STransferDataObject = c2STransferRules.addC2STransferRule(fromDomain, fromCategory, services, requestBearer,
				rownum,preRequisite);

		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SC2STRFRULE2").getExtentCase());
		currentNode.assignCategory("UAP");

		String addC2STransferRuleMsg = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccess");
		String addC2STransferRuleExistingMsg = MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist",
				"1");
			for(int i=0;i<c2STransferDataObject.length;i++){
				if(!c2STransferDataObject[i][0].equals("Data Issue"))
					actualMsg = (String) c2STransferDataObject[i][0];
			}
			
			if(addC2STransferRuleExistingMsg.equals(actualMsg)){
				Assertion.assertSkip("Case Skipped");
			} else {
				Assertion.assertEquals(actualMsg, addC2STransferRuleMsg);
			}
				
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
}
