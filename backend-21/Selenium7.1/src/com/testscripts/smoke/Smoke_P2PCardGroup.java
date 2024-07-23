package com.testscripts.smoke;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;
import com.Features.P2PCardGroup;
import com.aventstack.extentreports.Status;

public class Smoke_P2PCardGroup extends BaseTest{



	String cardGroupName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	static boolean TestCaseCounter = false;

	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);

		int rowCount = ExcelUtility.getRowCount();
		
		/*Object[][] categoryData = new Object[1][2];
		for (int i = 1; i <= rowCount; i++) {

			String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);
			System.out.println("Service type is " +x);

			if (x.equals(_masterVO.getProperty("P2PCreditTransferCode"))){
				System.out.println(x.equals(_masterVO.getProperty("P2PCreditTransferCode")));



				categoryData[0][0] = ExcelUtility.getCellData(i, 1);
				System.out.println(categoryData[0][0]);
				categoryData[0][1] = ExcelUtility.getCellData(i, 2);
				System.out.println(categoryData[0][1]);

				break;

			}*/




		Object[][] categoryData = null;
		if(rowCount>0){
			categoryData = new Object[1][2];
			for (int i = 1; i <= rowCount; i++) {
				String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);
				System.out.println("Service type is " +x);

				if (x.equals(_masterVO.getProperty("P2PCreditTransferCode"))){
					System.out.println(x.equals(_masterVO.getProperty("P2PCreditTransferCode")));



					categoryData[0][0] = ExcelUtility.getCellData(i, 1);
					System.out.println(categoryData[0][0]);
					categoryData[0][1] = ExcelUtility.getCellData(i, 2);
					System.out.println(categoryData[0][1]);

					break;
				}

			}

		}
		else if(rowCount<=0){
			categoryData = new Object[][]{
				{null,null}
			};
		}


		return categoryData;

	}




	//Smoke CardGroup TestCase1: Add a Card Group

	@Test(dataProvider="serviceData", priority=1)
	public void P2PCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("P2P Card Group Group Creation.");

		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]P2P Card Group");
			TestCaseCounter = true;
		}


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group creation for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("Smoke");

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}



	//Smoke CardGroup TestCase2: Modify Card Group

	@Test(dataProvider="serviceData", priority=2)
	public void modifyP2PCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]P2P Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Card Group Modification- Modify a card group.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group Modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("Smoke");

		String actual =P2PCardGroup.P2PCardGroupModification_EditCardGroup(serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider="serviceData", priority=3)
	public void modifyP2PCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]P2P Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Card Group Modification- Delete a card group.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group deletion for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("Smoke");
		String actual =P2PCardGroup.P2PCardGroupDeletion(serviceName, subService, cardGroupName);

		currentNode = test.createNode("To verify that Proper Message is displayed on successful card group deletion for service "+serviceName+" and sub-service" +subService);
		currentNode.assignCategory("Smoke");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successdeletemessage");


		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

}
