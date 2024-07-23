package com.testscripts.uap;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PCardGroup;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class UAP_P2PCardGroup extends BaseTest {



	String cardGroupName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	static boolean TestCaseCounter = false;

	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);

		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][2];
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
		return categoryData;
	}


	//UAP CardGroup TestCase1: Add a Card Group

	@Test(dataProvider="serviceData")
	public void a_P2PCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("P2P Card Group Group Creation.");

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group creation for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

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



	//UAP CardGroup TestCase2: View Card Group
	@Test(dataProvider="serviceData")
	public void b_viewCardGroup(String serviceName, String subService)throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}
		
		Log.startTestCase("P2P Card Group - View card group.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to view P2P card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String actual =P2PCardGroup.viewP2PCardGroup (serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.view.heading");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}





	//UAP CardGroup TestCase3: Card Group Status
	@Test(dataProvider="serviceData")
	public void c_statusCardGroup(String serviceName, String subService)throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Card Group - Card group Status.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to check the status of P2P card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String actual =P2PCardGroup.P2PCardGroupStatus(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgrouplist.message.successsuspendmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}
	
	//UAP CardGroup TestCase4: Modify Card Group

		@Test(dataProvider="serviceData")
		public void d_modifyP2PCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{
			if (TestCaseCounter == false) {
				test=extent.createTest("[UAP]P2P Card Group");
				TestCaseCounter = true;
			}
			Log.startTestCase("P2P Card Group Modification- Modify a card group.");

			P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

			currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group Modification for service "+serviceName+" and sub-service "+subService);
			currentNode.assignCategory("UAP");

			String actual =P2PCardGroup.P2PCardGroupModification_EditCardGroup(serviceName, subService,cardGroupName );
			String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");
			
			

			if (actual.equals(expected))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

		}
	

		




	//UAP CardGroup TestCase5: Modify Card Group_Add New Slab
	
	@Test(dataProvider="serviceData")
	public void e_modifyP2PCardGroup_AddNewSlab(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Card Group Modification- Modify a card group by adding new slab.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group Modification for adding new slab for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");
		
		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);

		String actual =P2PCardGroup.P2PCardGroupModification_AddNewSlab(serviceName, subService, cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	//UAP CardGroup TestCase6: Suspend Card Group Slab
	
	@Test(dataProvider="serviceData")
	public void f_suspendedCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}

		Log.startTestCase("P2P Card Group Modification- Suspend a card group.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to suspend P2P card group slab for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreationWithSuspendedSlab(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		String actual =dataMap.get("ACTUALMESSAGE");
		System.out.println("Card Group Name: " + cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}



	}




	//UAP CardGroup TestCase7: Resume Card Group Slab
	
	@Test(dataProvider="serviceData")
	public void g_resumeCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}

		Log.startTestCase("P2P Card Group Modification- Resume a card group.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to resume P2P card group slab for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String actual =P2PCardGroup.P2PCardGroupModification_ResumeCardGroupSlab(serviceName,subService,cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}



	}










	//UAP CardGroup TestCase8: Delete Card Group 

	@Test(dataProvider="serviceData")
	public void h_modifyP2PCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Card Group Modification- Delete a card group.");

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group deletion for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");
		String actual =P2PCardGroup.P2PCardGroupDeletion(serviceName, subService, cardGroupName);

		currentNode = test.createNode("To verify that Proper Message is displayed on successful card group deletion for service "+serviceName+" and sub-service" +subService);
		currentNode.assignCategory("UAP");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successdeletemessage");


		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	//UAP CardGroup TestCase9: Add a Future Date CardGroup

	@Test(dataProvider="serviceData")
	public void i_P2PCardGroupGroupCreationFutureDate(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("P2P Card Group Group Creation.");

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform P2P card group creation for Future Date for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupFutureDate(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}
	

	
	@Test(dataProvider="serviceData")
	public void j_SetdefaultP2PCardGroupGroup(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("P2P Card Group Group Creation.");

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]P2P Card Group");
			TestCaseCounter = true;
		}


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to Set  P2P card group to Default  for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");
		
		

		dataMap1=(HashMap<String, String>) P2PCardGroup.setDefaultP2PCardGroup(serviceName, subService);
		cardGroupName=dataMap1.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		
		String actual=dataMap1.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.successdefaultmessage",cardGroupName);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	}

	
	
	




}
