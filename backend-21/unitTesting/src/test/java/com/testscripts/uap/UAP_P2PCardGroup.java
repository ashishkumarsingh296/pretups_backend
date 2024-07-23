package com.testscripts.uap;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PCardGroup;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.UAP_P2P_CARD_GROUP)
public class UAP_P2PCardGroup extends BaseTest {



	String cardGroupName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	static boolean TestCaseCounter = false;
	String assignCategory="UAP";
	static String moduleCode;

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
	@TestManager(TestKey = "PRETUPS-323") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_P2PCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_P2PCardGroupGroupCreation";
        Log.startTestCase(methodName,serviceName,subService);

		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("UP2PCARDGRP1").getModuleCode();


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PP2PCARDGRP1").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	//UAP CardGroup TestCase2: View Card Group
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-324") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_viewCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_viewCardGroup";
		Log.startTestCase(methodName);
		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP1").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =P2PCardGroup.viewP2PCardGroup (serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.view.heading");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}





	//UAP CardGroup TestCase3: Card Group Status
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-327") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_statusCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_statusCardGroup";
        Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP2").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =P2PCardGroup.P2PCardGroupStatus(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgrouplist.message.successsuspendmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);

	}
	
	//UAP CardGroup TestCase4: Modify Card Group

		@Test(dataProvider="serviceData")
		@TestManager(TestKey = "PRETUPS-328") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void d_modifyP2PCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{
			
			final String methodName = "Test_modifyP2PCardGroup_EditCardGroup";
	        Log.startTestCase(methodName);

			P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SP2PCARDGRP1").getExtentCase(), serviceName,subService));
			currentNode.assignCategory(assignCategory);

			String actual =P2PCardGroup.P2PCardGroupModification_EditCardGroup(serviceName, subService,cardGroupName );
			String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");
			
			
			Assertion.assertEquals(actual, expected);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	

		//UAP CardGroup TestCase5: Modify Card Group_Add New Slab
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-329") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void e_modifyP2PCardGroup_AddNewSlab(String serviceName, String subService) throws InterruptedException{
		final String methodName = "Test_modifyP2PCardGroup_AddNewSlab";
        Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP3").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);

		String actual =P2PCardGroup.P2PCardGroupModification_AddNewSlab(serviceName, subService, cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	//UAP CardGroup TestCase6: Suspend Card Group Slab
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-330") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void f_suspendedCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_suspendedCardGroupSlab";
        Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP4").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreationWithSuspendedSlab(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		String actual =dataMap.get("ACTUALMESSAGE");
		System.out.println("Card Group Name: " + cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}




	//UAP CardGroup TestCase7: Resume Card Group Slab
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-331") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void g_resumeCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_resumeCardGroupSlab";
        Log.startTestCase(methodName);
		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP5").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =P2PCardGroup.P2PCardGroupModification_ResumeCardGroupSlab(serviceName,subService,cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}


	//UAP CardGroup TestCase8: Delete Card Group 

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-333") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void h_modifyP2PCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_modifyP2PCardGroup_DeleteCardGroup";
        Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP6").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		String actual =P2PCardGroup.P2PCardGroupDeletion(serviceName, subService, cardGroupName);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP7").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successdeletemessage");


		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	//UAP CardGroup TestCase9: Add a Future Date CardGroup

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-335") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void i_P2PCardGroupGroupCreationFutureDate(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_P2PCardGroupGroupCreationFutureDate";
        Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP8").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupFutureDate(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	

	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-336") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void j_SetdefaultP2PCardGroupGroup(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_SetdefaultP2PCardGroupGroup";
        Log.startTestCase(methodName);


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UP2PCARDGRP9").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		boolean RoleExist = ExcelUtility.isRoleExists(RolesI.P2P_CARD_GROUP_DEFAULT_ROLECODE);

		if (!RoleExist){
			currentNode.log(Status.SKIP, "Default Card Group Functionality is not available");
		}
		else{
		
		

		dataMap1=(HashMap<String, String>) P2PCardGroup.setDefaultP2PCardGroup(serviceName, subService);
		cardGroupName=dataMap1.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		
		String actual=dataMap1.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupp2pdetails.successdefaultmessage",cardGroupName);
		Assertion.assertEquals(actual, expected);
	}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
}
