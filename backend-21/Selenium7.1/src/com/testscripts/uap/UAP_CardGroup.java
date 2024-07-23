package com.testscripts.uap;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.Features.P2PCardGroup;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class UAP_CardGroup extends BaseTest {



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
		for (int i = 1; i <= rowCount; i++) {

			String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);
			System.out.println("Service type is " +x);

			if (x.equals(_masterVO.getProperty("CustomerRechargeCode"))){
				System.out.println(x.equals(_masterVO.getProperty("CustomerRechargeCode")));


				//categoryData[j][0] = i;
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
	public void a_c2sCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("C2S Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("C2S Card Group Group Creation.");

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}


		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group creation for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		
		currentNode = test.createNode("To verify that proper message is displayed C2S card group Creation for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

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
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group - View card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to view C2S card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String actual =c2sCardGroup.viewC2SCardGroup (serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.view.heading");
		
		currentNode = test.createNode("To verify that proper message is displayed for view C2S card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

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
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group - Card group Status.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to check the status of C2S card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String actual =c2sCardGroup.c2SCardGroupStatus(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.c2scardgrouplist.message.successsuspendmessage");
		
		currentNode = test.createNode("To verify that proper message is displayed C2S card group Status for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}





	@Test(dataProvider="serviceData")
	public void d_suspendCardGroup(String serviceName, String subService)throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group - Suspend Card group .");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to suspend C2S card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");

		String actual =c2sCardGroup.c2SCardGroupSuspend(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.c2scardgrouplist.message.successsuspendmessage");
		
		currentNode = test.createNode("To verify that proper message is displayed C2S card group Suspend for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	@Test(dataProvider="serviceData")
	public void e_activateCardGroup(String serviceName, String subService)throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group - Card group Status.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to activate the suspended C2S card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");

		String actual =c2sCardGroup.c2SCardGroupActivateCardGroup(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.c2scardgrouplist.message.successsuspendmessage");
		
		
		
		currentNode = test.createNode("To verify that proper message is displayed on activating the suspended C2S card group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	//UAP CardGroup TestCase4: Modify Card Group

	@Test(dataProvider="serviceData")
	public void f_modifyC2SCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Modification- Modify a card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group Modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String actual =c2sCardGroup.c2sCardGroupModification_EditCardGroup(serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		
		c2sCardGroup.c2SCardGroupDeletion(serviceName, subService, cardGroupName);
		
		currentNode = test.createNode("To verify that proper message is displayed C2S card group Modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	


	@Test(dataProvider="serviceData")
	public void g_modifyC2SCardGroup_AddNewSlab(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Modification- Modify a card group by adding new slab.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group Modification for adding new slab for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");
		
		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);

		String actual =c2sCardGroup.c2sCardGroupModification_AddNewSlab(serviceName, subService, cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		
		currentNode = test.createNode("To verify that proper message is displayed C2S card group Modification (AddNewSlab) for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider="serviceData")
	public void h_suspendedCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}

		Log.startTestCase("C2S Card Group Modification- Suspend a card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to suspend C2S card group slab for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");
		
		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation_withSuspendedSlab(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		String actual =dataMap.get("ACTUALMESSAGE");
		System.out.println("Card Group Name: " + cardGroupName);

	String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		
		currentNode = test.createNode("To verify that proper message is displayed for successful suspend C2S card group slab Modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");
		
		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}



	}





	@Test(dataProvider="serviceData")
	public void i_resumeCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}

		Log.startTestCase("C2S Card Group Modification- Resume a card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to resume C2S card group slab for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String actual =c2sCardGroup.c2sCardGroupModification_ResumeCardGroupSlab(serviceName,subService,cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		
		currentNode = test.createNode("To verify that proper message is displayed C2S card group slab resume for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");
		
		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}



	}


	@Test(dataProvider="serviceData")
	public void j_modifyC2SCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Modification- Delete a card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group deletion for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");
		String actual =c2sCardGroup.c2SCardGroupDeletion(serviceName, subService, cardGroupName);

		currentNode = test.createNode("To verify that Proper Message is displayed on successful card group deletion for service "+serviceName+" and sub-service" +subService);
		currentNode.assignCategory("UAP");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successdeletemessage");


		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}




	@Test(dataProvider="serviceData")
	public void k_c2sCardGroupGroupCreationFutureDate(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("C2S Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("C2S Card Group Group Creation.");

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}


		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group creation for Future Date for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		dataMap=(HashMap<String, String>) c2sCardGroup.C2SCardGroupFutureDate(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");
		
		currentNode = test.createNode("To verify that proper message is displayed C2S card group Creation for Future date for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}
	
	
	
	
	@Test(dataProvider="serviceData")
	public void l_modifyC2SCardGroup_EditCardGroupForFutureDate(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Modification- Modify a card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to perform C2S card group Modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");
		
		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		

		String actual =c2sCardGroup.c2sCardGroupModification_EditCardGroupForFutureDate(serviceName, subService,cardGroupName );
		
		
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		currentNode = test.createNode("To verify that proper message is displayed C2S card group Modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");
		
		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	




	@Test(dataProvider="serviceData")
	public void m_SetdefaultC2SCardGroupGroup(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		Log.startTestCase("P2P Card Group Group Creation.");

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]C2S Card Group");
			TestCaseCounter = true;
		}


		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is able to Set  C2S card group to Default  for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("UAP");

		dataMap=(HashMap<String, String>) c2sCardGroup.setDefaultC2SCardGroup(serviceName, subService, cardGroupName);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetails.successdefaultmessage",cardGroupName);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


	}

	
	@Test(dataProvider="serviceData")
	public void n_modifyC2SCardGroup_DeleteDefaultCardGroup(String serviceName, String subService) throws InterruptedException{

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Modification- Delete a Default card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Network Admin is not able to perform C2S card group deletion for Default Card Group for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");
		String actual =c2sCardGroup.c2SCardGroupDeletionForDefaultCardGroup(serviceName, subService, cardGroupName);

		currentNode = test.createNode("To verify that Proper Message is displayed on unsuccessful card group deletion for Default Card Group for service "+serviceName+" and sub-service" +subService);
		currentNode.assignCategory("SIT");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetails.error.isdefault.nottodelete");


		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	

	
	@Test(dataProvider="serviceData")
	public void o_modifyC2SCardGroup_NewVersion(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Modification- New Version is created on Modify card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that new version of Card Group is getting created on C2S card group Modification for service "+serviceName+" and sub-service "+subService);
		currentNode.assignCategory("SIT");
		
		//dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		//cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);

		boolean actual =c2sCardGroup.c2sCardGroupModification_EditCardGroupNewVersion(serviceName, subService,cardGroupName );



		if (actual ==true)
			currentNode.log(Status.PASS, "New Version is verified");
		else {

			currentNode.log(Status.FAIL, "Incorrect Set Version");
		}

	}
	


}
