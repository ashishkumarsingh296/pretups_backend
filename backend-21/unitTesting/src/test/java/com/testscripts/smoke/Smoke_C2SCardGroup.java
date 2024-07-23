package com.testscripts.smoke;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SMOKE_C2S_CARDGROUP)
public class Smoke_C2SCardGroup extends BaseTest{

	private String cardGroupName;

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-391") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_C2SCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException {
		final String methodName = "Test_C2SCardGroupGroupCreation";
		Log.startTestCase(methodName, serviceName, subService);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PC2SCARDGROUP1");
		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), serviceName,subService)).assignCategory(TestCategory.SMOKE);
		HashMap<String, String> dataMap = (HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName= dataMap.get("CARDGROUPNAME");
		String actual= dataMap.get("ACTUALMESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		Assertion.assertEquals(actual, expected);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/*@Test(dataProvider="serviceData", priority=1)
	public void modifyC2SCardGroup_AddNewSlab(String serviceName, String subService) throws InterruptedException{
		System.out.println(cardGroupName);
		test = extent.createTest("C2S Card Group Modification- Addition of a new Slab: " +serviceName+" "+subService);
		Log.startTestCase("C2S Card Group Modification- Addition of a new Slab.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group modification.");
		currentNode.assignCategory("Smoke");

		String actual=c2sCardGroup.c2sCardGroupModification_AddNewSlab(serviceName, subService, cardGroupName);
		String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}*/

	/*@Test(dataProvider="serviceData", priority=3)
	public void modifyC2SCardGroup_SuspendCardGrp(String serviceName, String subService) throws InterruptedException{

		test = extent.createTest("C2S Card Group Modification- Suspend a card group: " +serviceName+" "+subService);
		Log.startTestCase("C2S Card Group Modification- Suspend a card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group modification.");
		currentNode.assignCategory("Smoke");

		dataMap1 = (HashMap<String, String>) c2sCardGroup.c2sCardGroupModification_SuspendCardGroup(serviceName, subService, cardGroupName);

		String actual= dataMap1.get("ACTUAL_MESSAGE");
		String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}*/

	/*@Test(dataProvider="serviceData", priority=4)
	public void modifyC2SCardGroup_DeleteCardGrp(String serviceName, String subService) throws InterruptedException{

		test = extent.createTest("C2S Card Group Modification- Delete a card group: " +serviceName+" "+subService);
		Log.startTestCase("C2S Card Group Modification- Delete a card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group modification.");
		currentNode.assignCategory("Smoke");
		String actual =c2sCardGroup.c2sCardGroupModification_DeleteCardGroup(serviceName, subService, cardGroupName);
		String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successeditmessage");


		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}*/

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-392") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_ModifyC2SCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{
		final String methodName = "Test_ModifyC2SCardGroup_EditCardGroup";
		Log.startTestCase(methodName, serviceName, subService);

		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SC2SCARDGROUP1");
		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),serviceName,subService)).assignCategory(TestCategory.SMOKE);
		String actual= c2sCardGroup.c2sCardGroupModification_EditCardGroup(serviceName, subService, cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		Assertion.assertEquals(actual, expected);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/* ---------------------- D A T A  P R O V I D E R ------------------------- */
	/* ------------------------------------------------------------------------- */

	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		Object[][] categoryData = new Object[1][2];
		for (int i = 1, j = 0; i <= 1; i++, j++) {
			categoryData[j][0] = ExcelUtility.getCellData(i, 1);
			categoryData[j][1] = ExcelUtility.getCellData(i, 2);
		}
		return categoryData;
	}

	/* ------------------------------------------------------------------------- */

}
