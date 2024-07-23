package com.testscripts.uap;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PTransferRules;
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

@ModuleManager(name = Module.UAP_MODIFY_P2P_TRANSFER_RULE)
public class UAP_ModifyP2PTransferRule extends BaseTest {

	public static boolean testCaseCounter = false;
	String MasterSheetPath;
	Object[][] transferRuleCategories;
	String assignCategory="UAP";
	
	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {

		String serviceName = null;
		P2PTransferRules p2PTransferRules = new P2PTransferRules(driver);
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int MatrixRow = 0;
		int P2PTransferRuleCount = 0;
		
		for (int i = 1; i <= rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && FromCategory.equals("Subscriber")) {
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				String[] serviceArray = p2PTransferRules.serviceValue(services);
				int length = serviceArray.length;
				P2PTransferRuleCount=length;
			}
		}
		transferRuleCategories = new Object[P2PTransferRuleCount][5];

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && FromCategory.equals("Subscriber")) {
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				String[] serviceArray = p2PTransferRules.serviceValue(services);
				String requestBearer = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				String[] requestArray = requestBearer.split(",");
				for (int k = 0; k < serviceArray.length; k++) {
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
					int totalRow = ExcelUtility.getRowCount();
					for (int r = 1; r <= totalRow; r++) {
						serviceName = ExcelUtility.getCellData(0, ExcelI.NAME, r);
						if (serviceArray[k].equals(serviceName)) {
							transferRuleCategories[MatrixRow][0] = serviceArray[k];
							transferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, r);
							transferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, r);
							transferRuleCategories[MatrixRow][3] = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, r + 1);
							transferRuleCategories[MatrixRow][4] =requestArray[0];
							MatrixRow++;
							break;
						}
					}
				}
			}
		}

		return transferRuleCategories;

	}

	@Test(dataProvider = "RequiredTransferRuleCategories")
	@TestManager(TestKey = "PRETUPS-340") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_modifyP2PTransferRulesStatusSuspaend(String service, String subService, String cardGroup,
			String updatedCardGroup,String requestBearer) {

		final String methodName = "Test_modifyP2PTransferRulesStatusSuspaend";
        Log.startTestCase(methodName);
        
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PRETUPS-163").getExtentCase(), service,subService,cardGroup,requestBearer));
		currentNode.assignCategory(assignCategory);
        boolean uap = true;
		//String updatedStatus = "Suspended";
		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
		/*String addResult[] = p2pTransferRules.addP2PTransferRules(service, subService, cardGroup, uap);
		String result[] = p2pTransferRules.modifyP2PTransferRules(service, subService, cardGroup, addResult[4],*/
		//p2pTransferRules.addP2PTransferRules(service, subService, cardGroup, uap);
		String result[] = p2pTransferRules.modifyP2PTransferRules(requestBearer,service, subService, cardGroup,
		PretupsI.STATUS_SUSPENDED_LOOKUPS, cardGroup);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UMODIFYP2PTRFRULE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String modifyP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		Assertion.assertEquals(result[0], modifyP2PTransferRuleSuccessMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "RequiredTransferRuleCategories")
	@TestManager(TestKey = "PRETUPS-341") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_modifyP2PTransferRulesResume(String service, String subService, String cardGroup,
			String updatedCardGroup,String requestBearer) {

		final String methodName = "Test_modifyP2PTransferRulesResume";
        Log.startTestCase(methodName);
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UMODIFYP2PTRFRULE3").getExtentCase(), service,subService,cardGroup,requestBearer));
		currentNode.assignCategory(assignCategory);
       boolean uap =true;
		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
		/*String addResult[] = p2pTransferRules.addP2PTransferRules(service, subService, cardGroup, uap);
		String result[] = p2pTransferRules.modifyP2PTransferRules(service, subService, cardGroup, addResult[4],
				addResult[4], updatedCardGroup);*/
		//p2pTransferRules.addP2PTransferRules(service, subService, cardGroup, uap);
		String result[] = p2pTransferRules.modifyP2PTransferRules(requestBearer,service, subService, cardGroup,
		PretupsI.STATUS_ACTIVE_LOOKUPS, updatedCardGroup);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UMODIFYP2PTRFRULE4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String modifyP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		Assertion.assertEquals(result[0], modifyP2PTransferRuleSuccessMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
