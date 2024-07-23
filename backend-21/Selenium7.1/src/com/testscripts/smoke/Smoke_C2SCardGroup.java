package com.testscripts.smoke;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class Smoke_C2SCardGroup extends BaseTest{
	String cardGroupName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	static boolean TestCaseCounter = false;

	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][2];
		for (int i = 1, j = 0; i <= 1; i++, j++) {
			//categoryData[j][0] = i;
			categoryData[j][0] = ExcelUtility.getCellData(i, 1);
			System.out.println(categoryData[j][0]);
			categoryData[j][1] = ExcelUtility.getCellData(i, 2);
			System.out.println(categoryData[j][1]);
		}
		return categoryData;
	}

	@Test(dataProvider="serviceData")
	public void a_c2sCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("C2S Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("C2S Card Group Group Creation.");
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]C2S Card Group Creation");
			TestCaseCounter = true;
		}
		

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group creation for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("Smoke");

		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");
		//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


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
	public void b_modifyC2SCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("C2S Card Group Modification- Edit an existing card group: " +serviceName+" "+subService);
		Log.startTestCase("C2S Card Group Modification- Edit an existing card group.");
		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]C2S Card Group Creation");
			TestCaseCounter = true;
		}
		

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("Smoke");
		//currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group modification.");
		//currentNode.assignCategory("Smoke");

		String actual= c2sCardGroup.c2sCardGroupModification_EditCardGroup(serviceName, subService, cardGroupName);
		//String expected= LoadPropertiesFile.MessagesMap.get("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


}
