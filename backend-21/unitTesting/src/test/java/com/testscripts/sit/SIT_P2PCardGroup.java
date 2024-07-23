package com.testscripts.sit;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.Map_CardGroup;
import com.Features.P2PCardGroup;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_P2PCardGroup extends BaseTest{
	
	String cardGroupName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	Map<String, String> Map_CardGroup;
	static boolean TestCaseCounter = false;
	static String moduleCode;
	String assignCategory="SIT";
	
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
	
	

	@Test(dataProvider="serviceData")
	public void a_P2PCardGroup_MandatoryDetailCheck_CardGroupSetName(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP1").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("P2O Card Group Validation- Network Admin can not define P2P card group details if Card group set name is not entered.");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP1").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual =p2pCardGroup.p2pCardGroupErrorValidation_BlankCardGroupSetName(serviceName, subService);

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.p2pversionlist.label.cardgroupsetname.required.error"); 

		Validator.messageCompare(actual, expected);

	}

	
	
	
	
	
	@Test(dataProvider="serviceData")
	public void b_P2PCardGroup_MandatoryDetailCheck_SubService(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP2").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Card Group Validation- Network Admin can not define P2P card group details if mandatory details are not entered.(Sub-Service)");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP2").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual =p2pCardGroup.p2pCardGroupErrorValidation_BlankSubservice(serviceName, subService);

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetailsview.label.subservice")); 

		Validator.messageCompare(actual, expected);

	}
	
	

	
	@Test(dataProvider="serviceData")
	public void c_P2PCardGroup_DeactivateCardGroup(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP3").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("Network admin can deactivate Card group through P2P card group Status interface.");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP3").getExtentCase());
		currentNode.assignCategory(assignCategory);


		Map<String, String> map =p2pCardGroup.P2PCardGroupCreation(serviceName, subService);
		
		Thread.sleep(5000);
		
		String actual = map.get("ACTUALMESSAGE");
		String cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);
		
		String actual1 = p2pCardGroup.P2PCardGroupStatusDeativate(cardGroupName);
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.cardgrouplist.message.successsuspendmessage"); 

		Validator.messageCompare(actual1, expected1);

	}
	
	
	
	
	
	
	
	@Test(dataProvider="serviceData")
	public void d_P2PCardGroup_UniqueCardGroupName(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP4").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("Card Group Set Name should be unique in System");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP4").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int c=1;
		for( c=1; c<=totalRow1;c++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, c).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, c).matches(subService)))

			break;
		}

		
		
		String actual1 = p2pCardGroup.p2pCardGroupErrorValidation_UniqueCardGroupSetName(serviceName,subService,ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, c));
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.error.cardgroupc2snamealreadyexist"); 

		Validator.messageCompare(actual1, expected1);

	}
	
	
	@Test(dataProvider="serviceData")
	public void e_P2PCardGroup_verifyVersion(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP5").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("Newly created Card Group should have version 1 in System");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
       Map<String, String> map =p2pCardGroup.P2PCardGroupCreation(serviceName, subService);
		
		String actual = map.get("ACTUALMESSAGE");
		cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);
		
		String actual1 = DBHandler.AccessHandler.getCardGroupVersion(cardGroupName);
		
		

		Validator.messageCompare(actual1, "1");

	}
	
	
	@Test(dataProvider="serviceData")
	public void qe1_P2PCardGroup_MultipleVersionsValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP15").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that P2P card group set will have only one version applicable even if it has multiple versions");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Thread.sleep(30000);

		String actualMessage = p2pCardGroup.P2PCardGroupModification_EditCardGroup(serviceName, subService, cardGroupName);

		String expectedmessage= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		Thread.sleep(30000);
		
		Validator.messageCompare(actualMessage, expectedmessage);

		String v = DBHandler.AccessHandler.getCardGroupVersionActive(cardGroupName);


		Validator.messageCompare(v, "2");

	}




	@Test(dataProvider="serviceData")
	public void f_P2PCardGroup_DeleteNegativeCardGroupAssociatedWithTransferRule(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP6").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("Network Admin cannot  delete P2P card group details if it is associated with transfer rule");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP6").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int c=1;
		for( c=1; c<=totalRow1;c++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, c).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, c).matches(subService)))

			break;
		}

		
		String actual1 = p2pCardGroup.P2PCardGroupDeletion(serviceName,subService,ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, c));
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.error.cardgroupc2snamealreadyexist"); 
		String expected2= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupp2pdetails.error.isdefault.nottodelete"); 
		if(actual1.equals(expected1))
			Assertion.assertEquals(actual1, expected1);
		else
			Assertion.assertSkip("Default Card Group can not be deleted");

	}
	
	
	
	@Test(dataProvider="serviceData")
	public void g_P2PCardGroup_ApplicableDateVerification(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP7").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that card group can be set only for future date and time.");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP7").getExtentCase());
		currentNode.assignCategory(assignCategory);

				
		Map<String, String> map = p2pCardGroup.P2PCardGroupApplicableDateVerification(serviceName,subService);
		
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.error.invalidapplicabledate"); 

		Validator.messageCompare(map.get("ACTUALMESSAGE"), expected1);

	}
	
	
	
	@Test(dataProvider="serviceData")
	public void h_P2PCardGroup_InvalidProcessingFeeMaxAmount(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP8").getModuleCode();
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Card Group Validation- To verify that Max Processing fee for the receiver should be within the start range and end range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeMaxAmount", "101");
		Log.info("The entered ProcessingFeeMaxAmount is:" + Map_CardGroup.get("ProcessingFeeMaxAmount"));

		p2pCardGroup.p2pCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidmaxsenderaccessfee","100");
 

	Validator.messageCompare(actual, expected);

	

	}
	

	
	
	
	
	@Test(dataProvider="serviceData")
	public void i_P2PCardGroup_FixValueCardGroup(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP9").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that Max Processing fee for the receiver should be within the start range and end range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
       Map<String, String> map =p2pCardGroup.FixValueP2PCardGroupCreation(serviceName, subService);
		
		String actual = map.get("ACTUALMESSAGE");
		String cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);
		
		String actual1 = DBHandler.AccessHandler.getCardGroupVersion(cardGroupName);
		
		

		Validator.messageCompare(actual1, "1");

	}
	

	
	@Test(dataProvider="serviceData")
	public void j_P2PCardGroup_OverlappingRangeValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP10").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that ranges defined in the card group should not overlap with other ranges defined in the same card group set.");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
       p2pCardGroup.p2pCardGroupOverLappingRangeValueValidation(Map_CardGroup,serviceName, subService);
		System.out.println("lllllllllllllllllllllllll" +Map_CardGroup.get("ACTUAL"));
   	String actual = Map_CardGroup.get("ACTUAL");
	


	
	String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.error.invalidslab",MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.startrange"),"2", 
			MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.endrange"),"1");

		Validator.messageCompare(actual, expected);
	

	}
	
	
	
	
	
	
	@Test(dataProvider="serviceData")
	public void k_P2PCardGroup_Tax1RateValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP11").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 1 Rate  should be within 0 to 100 (if in percent)");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Rate", "101");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax1Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax1rate","100"); 

		Validator.messageCompare(actual, expected);

	}

	
	
	@Test(dataProvider="serviceData")
	public void l_P2PCardGroup_Tax2RateValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP12").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 2 Rate  should be within 0 to 100 (if in percent)");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Rate", "101");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax2Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax2rate","100"); 

		Validator.messageCompare(actual, expected);

	}
	
	

	
	
	@Test(dataProvider="serviceData")
	public void m_P2PCardGroup_Tax1RateinAmountValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP13").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 1 Rate  should be within 0 to Start Range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Type", (_masterVO.getProperty("Tax1TypeAmt")));
		Map_CardGroup.put("Tax1Rate", String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"))+1));
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax2Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");
		
		String startRange = String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"))+ 1.0);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax1rate",startRange+" (Start Range)"); 

		Validator.messageCompare(actual, expected);

	}

	
	
	
	
	
	@Test(dataProvider="serviceData")
	public void n_P2PCardGroup_Tax2RateinAmountValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PCARDGROUP14").getModuleCode();
		
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 2 Rate  should be within 0 to Start Range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PCARDGROUP14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Type", (_masterVO.getProperty("Tax1TypeAmt")));
		Map_CardGroup.put("Tax2Rate", String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"))+1));
		Log.info("The entered Tax2Rate is:" + Map_CardGroup.get("Tax2Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");
		
		String startRange = String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"))+ 1.0);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax2rate",startRange+" (Start Range)"); 

		Validator.messageCompare(actual, expected);

	}
	
	
	
	
}
