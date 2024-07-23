package com.testscripts.uap;

import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class UAP_AddC2STransferRule extends BaseTest{
	String MasterSheetPath;

	@Test
	public void addC2STransferRules() {
		Log.startTestCase(this.getClass().getName());

		test = extent.createTest("[UAP]C2S Transfer Rule");
		currentNode = test.createNode("To verify that Operator is able to create C2S Transfer Rules for Category.");
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
		for (int i = 1; i < rowCount; i++) {

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
		c2STransferDataObject = c2STransferRules.addC2STransferRule(fromDomain, fromCategory, services, requestBearer,
				rownum);

		currentNode = test
				.createNode("To verify that proper Message is displayed on successful C2S Transfer rule creation.");
		currentNode.assignCategory("UAP");

		String addC2STransferRuleMsg = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccess");
		String addC2STransferRuleExistingMsg = MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist",
				"1");
			for(int i=0;i<c2STransferDataObject.length;i++){
				if(!c2STransferDataObject[i][0].equals("Data Issue"))
					actualMsg = (String) c2STransferDataObject[i][0];
			}
			
			if (addC2STransferRuleMsg.equals(actualMsg))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else if(addC2STransferRuleExistingMsg.equals(actualMsg))
				currentNode.log(Status.SKIP, "Message Validation Successful");
			else{
				currentNode.log(Status.FAIL, "Expected [" + addC2STransferRuleMsg + " / "+addC2STransferRuleExistingMsg+"] but found [" + actualMsg + "]");
				currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
			}		
		}
}
