
package com.testscripts.sit;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.Features.Map_CardGroup;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_C2SCardGroup)
public class SIT_C2SCardGroup extends BaseTest {

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


	/*
	 * Negative test cases
	 */


	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-622")
	public void a_C2SCardGroup_ApplicableTimeValidation(String serviceName, String subService) throws InterruptedException{
		
		
		final String methodName = "a_C2SCardGroup_ApplicableTimeValidation";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- Applicable time is required in correct format for card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP1").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual =c2sCardGroup.c2SCardGroupErrorValidation_AppTimeFormat(serviceName, subService);

		String expected=  MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.applicablefromhour")); 

		Assertion.assertEquals(actual, expected);
		
		
		Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	}


	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-623")
	public void b_C2SCardGroup_ApplicableDateValidation(String serviceName, String subService) throws InterruptedException{
		final String methodName = "b_C2SCardGroup_ApplicableDateValidation";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- Applicable date is required to create card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP2").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual =c2sCardGroup.c2SCardGroupErrorValidation_AppDate(serviceName, subService);

		String expected= MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.error.invalidapplicabledate");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}



	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-624")
	public void c_C2SCardGroup_CardGroupSetName(String serviceName, String subService) throws InterruptedException{
	
		
		final String methodName = "c_C2SCardGroup_CardGroupSetName";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- C2S CardGroupSetName is required in correct format for card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP3").getExtentCase());
		currentNode.assignCategory(assignCategory);

       
		String actual =c2sCardGroup.c2SCardGroupErrorValidation_BlankCardGroupSetName(serviceName, subService,"");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.selectc2scardgroupset.label.cardgroupsetname")); 

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}




	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-625")
	public void d_C2SCardGroup_BlankCardGroupCode(String serviceName, String subService) throws InterruptedException{
		final String methodName = "d_C2SCardGroup_BlankCardGroupCode";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- C2S CardGroupCode is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("CardGroupCode", "");
		Log.info("The entered CardGroupCode is:" + Map_CardGroup.get("CardGroupCode"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.cardgroupcode")); 

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);

	}		






	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-626")
	public void e_C2SCardGroup_BlankValidityDays(String serviceName, String subService) throws InterruptedException{
		final String methodName = "e_C2SCardGroup_BlankValidityDays";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- ValidityDays is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ValidityDays", "");
		Log.info("The entered ValidityDays is:" + Map_CardGroup.get("ValidityDays"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.validityvaluerequired", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.validity"),MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.validityrate")); 

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}		




	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-627")
	public void f_C2SCardGroup_BlankGracePeriod(String serviceName, String subService) throws InterruptedException{
		final String methodName = "f_C2SCardGroup_BlankGracePeriod";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- GracePeriod is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("GracePeriod", "");
		Log.info("The entered GracePeriod is:" + Map_CardGroup.get("GracePeriod"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.graceperiod")); 

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}		




	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-628")
	public void g_C2SCardGroup_BlankMultipleOf(String serviceName, String subService) throws InterruptedException{
		final String methodName = "g_C2SCardGroup_BlankMultipleOf";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- MultipleOf is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("MultipleOf", "");
		Log.info("The entered MultipleOf is:" + Map_CardGroup.get("MultipleOf"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.multipleof")); 

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}		





	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-629")
	public void h_C2SCardGroup_BlankTax1Rate(String serviceName, String subService) throws InterruptedException{
		final String methodName = "h_C2SCardGroup_BlankTax1Rate";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- Tax1Rate is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Rate", "");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax1Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax1"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax1rate")); 

		//Validator.messageCompare(actual, expected);
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}


	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-630")
	public void i_C2SCardGroup_BlankTax2Rate(String serviceName, String subService) throws InterruptedException{
		final String methodName = "i_C2SCardGroup_BlankTax2Rate";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- Tax2Rate is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Rate", "");
		Log.info("The entered Tax2Rate is:" + Map_CardGroup.get("Tax2Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required",
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax2"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax2rate"));

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}






	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-631")
	public void j_C2SCardGroup_BlankProcessingFeeRate(String serviceName, String subService) throws InterruptedException{
		final String methodName = "j_C2SCardGroup_BlankProcessingFeeRate";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- ProcessingFeeRate is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeRate", "");
		Log.info("The entered ProcessingFeeRate is:" + Map_CardGroup.get("ProcessingFeeRate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfee"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfeerate")); 

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);

	}



	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-632")
	public void k_C2SCardGroup_BlankProcessingFeeMinAmount(String serviceName, String subService) throws InterruptedException{
		final String methodName = "k_C2SCardGroup_BlankProcessingFeeMinAmount";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- ProcessingFeeMinAmount is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeMinAmount", "");
		Log.info("The entered ProcessingFeeMinAmount is:" + Map_CardGroup.get("ProcessingFeeMinAmount"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfee"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfeeminrate"));

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);


	}



	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-633")
	public void l_C2SCardGroup_BlankProcessingFeeMaxAmount(String serviceName, String subService) throws InterruptedException{
		final String methodName = "l_C2SCardGroup_BlankProcessingFeeMaxAmount";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- ProcessingFeeMaxAmount is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeMaxAmount", "");
		Log.info("The entered ProcessingFeeMaxAmount is:" + Map_CardGroup.get("ProcessingFeeMaxAmount"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfee"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfeemaxrate"));
 

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);
	}



	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-634")
	public void n_C2SCardGroup_BlankReceiverConversionFactor(String serviceName, String subService) throws InterruptedException{
		final String methodName = "n_C2SCardGroup_BlankReceiverConversionFactor";
        Log.startTestCase(methodName);
		Log.startTestCase("C2S Card Group Validation- ReceiverConversionFactor is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ReceiverConversionFactor","");
		Log.info("The entered ReceiverConversionFactor is:" + Map_CardGroup.get("ReceiverConversionFactor"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.getLabelByKey("cardgroup.rp2p.error.invalidreceiverconvfactorrange"); 

		//Validator.messageCompare(actual, expected);

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);

	}


	
	
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-1160")
	public void o_C2SCardGroup_DeactivateCardGroup(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP14").getModuleCode();
		
		final String methodName = "o_C2SCardGroup_DeactivateCardGroup";
        Log.startTestCase(methodName);
		Log.startTestCase("Network admin can deactivate Card group through C2S card group Status interface.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP14").getExtentCase());
		currentNode.assignCategory(assignCategory);


		Map<String, String> map =c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		
		Thread.sleep(5000);
		
		String actual = map.get("ACTUALMESSAGE");
		String cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);
		
		String actual1 = c2sCardGroup.C2SCardGroupStatusDeativate(cardGroupName);
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.c2scardgrouplist.message.successsuspendmessage"); 

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);

	}
	
	
	
	
	@Test(dataProvider="serviceData")
@TestManager(TestKey = "PRETUPS-1161")
	public void p_C2SCardGroup_UniqueCardGroupName(String serviceName, String subService) throws InterruptedException{
		final String methodName = "p_C2SCardGroup_UniqueCardGroupName";
        Log.startTestCase(methodName);
		Log.startTestCase("Card Group Set Name should be unique in System");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP15").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int c=1;
		for( c=1; c<=totalRow1;c++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, c).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, c).matches(subService)))

			break;
		}

		
		
		String actual1 = c2sCardGroup.c2SCardGroupErrorValidation_UniqueCardGroupSetName(serviceName,subService,ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, c));
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.error.cardgroupc2snamealreadyexist"); 

		Assertion.assertEquals(actual1, expected1);
		Assertion.completeAssertions();
        Log.endTestCase(methodName);


	}


	
	
	
	
	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-1162")
	public void q1_C2SCardGroup_verifyVersion(String serviceName, String subService) throws InterruptedException{
		final String methodName = "q_C2SCardGroup_verifyVersion";
        Log.startTestCase(methodName);
		Log.startTestCase("Newly created Card Group should have version 1 in System");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
       Map<String, String> map =c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		Thread.sleep(60000);//Required for next consecutive case should not be removed as applicable time for card group is  2 minutes
		String actual = map.get("ACTUALMESSAGE");
		cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		//Validator.messageCompare(actual, expected);
		Assertion.assertEquals(actual, expected);
		String actual1 = DBHandler.AccessHandler.getCardGroupVersion(cardGroupName);
		
		

		Assertion.assertEquals(actual1, "1");
		Assertion.completeAssertions();
        Log.endTestCase(methodName);


	}

	
	

	@Test(dataProvider="serviceData")
	public void q2_C2SCardGroup_MultipleVersionsValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP26").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that C2S card group set will have only one version applicable even if it has multiple versions");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		//Map_CardGroup dataMap = new Map_CardGroup();
	
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP26").getExtentCase());
		currentNode.assignCategory(assignCategory);
		//Thread.sleep(30000);

		String actualMessage = c2sCardGroup.c2sCardGroupModification_EditCardGroup(serviceName, subService, cardGroupName);

		String expectedmessage= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		Thread.sleep(120000); //Required as applicable time for card group is 2 minutes
		
		Validator.messageCompare(actualMessage, expectedmessage);

		String v = DBHandler.AccessHandler.getCardGroupVersionActive(cardGroupName);


		Validator.messageCompare(v, "2");

	}





	@Test(dataProvider="serviceData")
	public void r_C2SCardGroup_DeleteNegativeCardGroupAssociatedWithTransferRule(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP17").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("Network Admin cannot  delete C2S card group details if it is associated with transfer rule");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP17").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int c=1;
		for( c=1; c<=totalRow1;c++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, c).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, c).matches(subService)))

			break;
		}


		String actual1 = c2sCardGroup.c2SCardGroupDeletion(serviceName,subService,ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, c));

		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetails.error.isdefault.nottodelete"); 

		String expected2= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.deletetransferruleexistsmessage"); 
		
		boolean flag1 = actual1.equals(expected1);
		boolean flag2 = actual1.equals(expected2);
		if(flag1 || flag2) {
			if(flag1) {
				Validator.messageCompare(actual1, expected1);
			}
			else {
				Validator.messageCompare(actual1, expected2);

			}
			
		Assertion.assertPass("Pass");
		}
		else {
			Assertion.assertFail("Fail");
		}

	}



	@Test(dataProvider="serviceData")
	public void s_C2SCardGroup_ApplicableDateVerification(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP18").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that card group can be set only for future date and time.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP18").getExtentCase());
		currentNode.assignCategory(assignCategory);


		Map<String, String> map = c2sCardGroup.C2SCardGroupApplicableDateVerification(serviceName,subService);


		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.error.invalidapplicabledate"); 

		Validator.messageCompare(map.get("ACTUALMESSAGE"), expected1);

	}







	@Test(dataProvider="serviceData")
	public void t_C2SCardGroup_InvalidProcessingFeeMaxAmount(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP19").getModuleCode();
		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that Max Processing fee for the receiver should be within the start range and end range");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP19").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeMaxAmount", "101");
		Log.info("The entered ProcessingFeeMaxAmount is:" + Map_CardGroup.get("ProcessingFeeMaxAmount"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.invalidmaxreceiveraccessfee","100");


		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}




	@Test(dataProvider="serviceData")
	public void u_C2SCardGroup_FixValue(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP20").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("Newly created Card Group should have version 1 in System");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP20").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Map<String, String> map =c2sCardGroup.FixValueC2SCardGroupCreation(serviceName, subService);

		String actual = map.get("ACTUALMESSAGE");
		String cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);


	}



	@Test(dataProvider="serviceData")
	public void v_C2SCardGroup_OverlappingRangeValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP21").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that ranges defined in the card group should not overlap with other ranges defined in the same card group set.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP21").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map<String, String> map =c2sCardGroup.c2sCardGroupOverLappingRangeValueValidation(Map_CardGroup,serviceName, subService);

		String actual = Map_CardGroup.get("ACTUAL");


		//String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.error.invalidslab");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.error.invalidslab",MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.startrange"),"2", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.endrange"),"1");


		Validator.messageCompare(actual, expected);


	}







	@Test(dataProvider="serviceData")
	public void w_C2SCardGroup_Tax1RateValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP22").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 1 Rate  should be within 0 to 100 (if in percent)");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP22").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Rate", "101");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax1Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.invalidreceivertax1rate","100"); 

		Validator.messageCompare(actual, expected);

	}



	@Test(dataProvider="serviceData")
	public void x_C2SCardGroup_Tax2RateValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP23").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 2 Rate  should be within 0 to 100 (if in percent)");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP23").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Rate", "101");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax2Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.invalidreceivertax2rate","100"); 

		Validator.messageCompare(actual, expected);

	}




	@Test(dataProvider="serviceData")
	public void y_C2SCardGroup_Tax1RateinAmountValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP24").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 1 Rate  should be within 0 to Start Range");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP24").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Type", (_masterVO.getProperty("Tax1TypeAmt")));
		Map_CardGroup.put("Tax1Rate", String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"))+1));
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax2Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String startRange = String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"))+ 1.0);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.invalidreceivertax1rate",startRange+" (Start Range)"); 

		Validator.messageCompare(actual, expected);

	}






	@Test(dataProvider="serviceData")
	public void z_C2SCardGroup_Tax2RateinAmountValidation(String serviceName, String subService) throws InterruptedException{
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SCARDGROUP25").getModuleCode();

		if (TestCaseCounter == false) {
			test=extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		Log.startTestCase("To verify that the Tax 2 Rate  should be within 0 to Start Range");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SCARDGROUP25").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Type", (_masterVO.getProperty("Tax1TypeAmt")));
		Map_CardGroup.put("Tax2Rate", String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"))+1));
		Log.info("The entered Tax2Rate is:" + Map_CardGroup.get("Tax2Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String startRange = String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"))+ 1.0);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.invalidreceivertax2rate",startRange+" (Start Range)"); 

		Validator.messageCompare(actual, expected);

	}





/*
	@Test(dataProvider="serviceData")
	public void n_C2SCardGroup_BlankBonusValue(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- BonusValue is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when BonusValue is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("BonusValue", "");
		Log.info("The entered BonusValue is:" + Map_CardGroup.get("BonusValue"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

    	String expected= MessagesDAO.prepareMessageByKey("errors.required", (change the label)
				MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.multipleof")); 
		
		

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider="serviceData")
	public void o_C2SCardGroup_BlankBonusValidity(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- BonusValidity is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when BonusValidity is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("BonusValidity", "");
		Log.info("The entered BonusValidity is:" + Map_CardGroup.get("BonusValidity"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", (change the label)
				MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.multipleof")); 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	@Test(dataProvider="serviceData")
	public void p_C2SCardGroup_BlankBonusConversionFactor(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- BonusConversionFactor is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when BonusConversionFactor is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("BonusConversionFactor", "");
		Log.info("The entered BonusConversionFactor is:" + Map_CardGroup.get("BonusConversionFactor"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", (change the label)
				MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.multipleof")); 
		
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.rp2p.required.error", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.bonus"),MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.bonusvalidityrate"));
		

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	@Test(dataProvider="serviceData")
	public void m_C2SCardGroup_BlankBonusValidityDays(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- BonusValidityDays is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when BonusValidityDays is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("BonusValidityDays","");
		Log.info("The entered BonusValidityDays is:" + Map_CardGroup.get("BonusValidityDays"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.rp2p.required.error", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.bonus"),MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.bonusvalidityrate"));
		
		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


*/

	
	
	

}

