package com.testscripts.uap;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.UAP_CARD_GROUP)
public class UAP_CardGroup extends BaseTest {



	String cardGroupName;
	String cardGroupSetName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	static boolean TestCaseCounter = false;
	String assignCategory="UAP";
	static String moduleCode;
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
	@TestManager(TestKey = "PRETUPS-366") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_c2sCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("C2S Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_c2sCardGroupGroupCreation";
        Log.startTestCase(methodName);

		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("UC2SCARDGROUP1").getModuleCode();
		


		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PC2SCARDGROUP1").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP1").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		Assertion.assertEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);

	}



	//UAP CardGroup TestCase2: View Card Group
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-367") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_viewCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_viewCardGroup";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP2").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =c2sCardGroup.viewC2SCardGroup (serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.view.heading");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP3").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}


	//UAP CardGroup TestCase3: Card Group Status
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-368") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_statusCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_statusCardGroup";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP4").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =c2sCardGroup.c2SCardGroupStatus(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.c2scardgrouplist.message.successsuspendmessage");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP5").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-369") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void d_suspendCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_suspendCardGroup";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP6").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =c2sCardGroup.c2SCardGroupSuspend(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.c2scardgrouplist.message.successsuspendmessage");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP7").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-370") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void e_activateCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_activateCardGroup";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP8").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =c2sCardGroup.c2SCardGroupActivateCardGroup(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.c2scardgrouplist.message.successsuspendmessage");
		
		
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP9").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}


	//UAP CardGroup TestCase4: Modify Card Group

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-371") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void f_modifyC2SCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{
		final String methodName = "Test_modifyC2SCardGroup_EditCardGroup";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SC2SCARDGROUP1").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =c2sCardGroup.c2sCardGroupModification_EditCardGroup(serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		
		c2sCardGroup.c2SCardGroupDeletion(serviceName, subService, cardGroupName);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP10").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	


	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-372") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void g_modifyC2SCardGroup_AddNewSlab(String serviceName, String subService) throws InterruptedException{
		final String methodName = "Test_modifyC2SCardGroup_AddNewSlab";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP11").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);

		String actual =c2sCardGroup.c2sCardGroupModification_AddNewSlab(serviceName, subService, cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP12").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}


	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-373") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void h_suspendedCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_suspendedCardGroupSlab";
        Log.startTestCase(methodName);
		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP13").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation_withSuspendedSlab(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		String actual =dataMap.get("ACTUALMESSAGE");
		System.out.println("Card Group Name: " + cardGroupName);

	String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP14").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);

	}

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-374") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void i_resumeCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_resumeCardGroupSlab";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP15").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =c2sCardGroup.c2sCardGroupModification_ResumeCardGroupSlab(serviceName,subService,cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP16").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);


	}


	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-375") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void j_modifyC2SCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_modifyC2SCardGroup_DeleteCardGroup";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP17").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		String actual =c2sCardGroup.c2SCardGroupDeletion(serviceName, subService, cardGroupName);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP18").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successdeletemessage");


		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}




	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-376") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void k_c2sCardGroupGroupCreationFutureDate(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("C2S Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_modifyC2SCardGroup_NewVersion";
        Log.startTestCase(methodName);


		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP19").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) c2sCardGroup.C2SCardGroupFutureDate(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP20").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successaddmessage");
		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);

	}
	
	
	
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-377") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void l_modifyC2SCardGroup_EditCardGroupForFutureDate(String serviceName, String subService) throws InterruptedException{
		final String methodName = "Test_modifyC2SCardGroup_NewVersion";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP21").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		

		String actual =c2sCardGroup.c2sCardGroupModification_EditCardGroupForFutureDate(serviceName, subService,cardGroupName );
		
		
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP22").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-378") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void m_SetdefaultC2SCardGroupGroup(String serviceName, String subService) throws InterruptedException, ParseException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_SetdefaultC2SCardGroupGroup";
        Log.startTestCase(methodName);


		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP23").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		
		boolean RoleExist = ExcelUtility.isRoleExists(RolesI.C2S_CARD_GROUP_DEFAULT_ROLECODE);

		if (!RoleExist){
			Assertion.assertSkip("Default Card Group Functionality is not available");
		}
		else{
		

		String actual = null;
		String expected  = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile( MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, i).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i).matches(subService)))

			break;
		}

		System.out.println(i);
		cardGroupName= ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
		System.out.println(cardGroupName);
		
		String cardGroupDefaultStatus= DBHandler.AccessHandler.getDefaultCardGroupStatus(cardGroupName);
		Log.info("The card Group default status is: " +cardGroupDefaultStatus);
		
		if(!cardGroupDefaultStatus.equals("Y")){
		dataMap=(HashMap<String, String>) c2sCardGroup.setDefaultC2SCardGroup(serviceName, subService, cardGroupName);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		Log.info("The above created card group" +cardGroupName+ "  is set as Default");
		System.out.println(cardGroupName);
		actual=dataMap.get("ACTUALMESSAGE");
		expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetails.successdefaultmessage",cardGroupName);
		}

	else
	{
		dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreationForDefault(serviceName, subService);
		cardGroupSetName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupSetName);
		String actual1=dataMap.get("ACTUALMESSAGE");
		Log.info(actual1);
		System.out.println("time diff is" + dataMap.get("Requiredtime"));
		long time = Long.parseLong(dataMap.get("Requiredtime"));
		Thread.sleep(time);
		
		dataMap=(HashMap<String, String>) c2sCardGroup.setDefaultC2SCardGroup(serviceName, subService, cardGroupSetName);
		Log.info("The newly created card group" +cardGroupSetName+ "  is set as Default");
		actual=dataMap.get("ACTUALMESSAGE");
		expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetails.successdefaultmessage",cardGroupSetName);
	}
	

		Assertion.assertEquals(actual, expected);
		}
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-379") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void n_modifyC2SCardGroup_DeleteDefaultCardGroup(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_modifyC2SCardGroup_DeleteDefaultCardGroup";
        Log.startTestCase(methodName);

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP24").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		boolean RoleExist = ExcelUtility.isRoleExists(RolesI.C2S_CARD_GROUP_DEFAULT_ROLECODE);

		if (!RoleExist){
			//currentNode.log(Status.SKIP, "Default Card Group Functionality is not available");
			Assertion.assertSkip("Default Card Group Functionality is not available");
		}
		else{
			String actual =c2sCardGroup.c2SCardGroupDeletionForDefaultCardGroup(serviceName, subService, cardGroupSetName);
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP25").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetails.error.isdefault.nottodelete");
        Assertion.assertEquals(actual, expected);
        
		}
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}
	

	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-380") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void o_modifyC2SCardGroup_NewVersion(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_modifyC2SCardGroup_NewVersion";
        Log.startTestCase(methodName);
        
		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UC2SCARDGROUP26").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		//dataMap=(HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		//cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		Thread.sleep(60000); // To make the existing version of card group applicable 1 min sleep is applied
		boolean actual =c2sCardGroup.c2sCardGroupModification_EditCardGroupNewVersion(serviceName, subService,cardGroupName );
				
		if (actual ==true) {
			currentNode.log(Status.PASS, "New Version is verified");
			}
		else {
			currentNode.log(Status.FAIL, "Incorrect Set Version");
			Assertion.assertFail("Incorrect Set Version");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	


}
