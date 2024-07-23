
package com.testscripts.sit;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.Features.Map_CardGroup;
import com.Features.Map_TCPValues;
import com.Features.P2PCardGroup;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;
import com.utils.Validator;

public class SIT_C2SCardGroup extends BaseTest {

	String cardGroupName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	Map<String, String> Map_CardGroup;
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


	/*
	 * Negative test cases
	 */


	@Test(dataProvider="serviceData")
	public void a_C2SCardGroup_ApplicableTimeValidation(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- Applicable time is required in correct format for card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Applicable time is required for creating a C2S card group");
		currentNode.assignCategory("SIT");


		String actual =c2sCardGroup.c2SCardGroupErrorValidation_AppTimeFormat(serviceName, subService);

		String expected=  MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.applicablefromhour")); 

		Validator.messageCompare(actual, expected);

	}


	@Test(dataProvider="serviceData")
	public void b_C2SCardGroup_ApplicableDateValidation(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- Applicable date is required to create card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that Applicable date is required for creating a C2S card group");
		currentNode.assignCategory("SIT");


		String actual =c2sCardGroup.c2SCardGroupErrorValidation_AppDate(serviceName, subService);

		String expected= MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.error.invalidapplicabledate");

		Validator.messageCompare(actual, expected);

	}



	@Test(dataProvider="serviceData")
	public void c_C2SCardGroup_CardGroupSetName(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- C2S CardGroupSetName is required in correct format for card group.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

		currentNode = test.createNode("To verify that C2S CardGroupSetName is required for creating a C2S card group");
		currentNode.assignCategory("SIT");


		String actual =c2sCardGroup.c2SCardGroupErrorValidation_BlankCardGroupSetName(serviceName, subService);

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.selectc2scardgroupset.label.cardgroupsetname")); 

		Validator.messageCompare(actual, expected);

	}




	@Test(dataProvider="serviceData")
	public void d_C2SCardGroup_BlankCardGroupCode(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- C2S CardGroupCode is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that C2S CardGroupCode is required for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("CardGroupCode", "");
		Log.info("The entered CardGroupCode is:" + Map_CardGroup.get("CardGroupCode"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.cardgroupcode")); 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}		






	@Test(dataProvider="serviceData")
	public void e_C2SCardGroup_BlankValidityDays(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- ValidityDays is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when ValidityDays is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ValidityDays", "");
		Log.info("The entered ValidityDays is:" + Map_CardGroup.get("ValidityDays"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.validityvaluerequired", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.validity"),MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.validityrate")); 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}		




	@Test(dataProvider="serviceData")
	public void f_C2SCardGroup_BlankGracePeriod(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- GracePeriod is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when GracePeriod is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("GracePeriod", "");
		Log.info("The entered GracePeriod is:" + Map_CardGroup.get("GracePeriod"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.graceperiod")); 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}		




	@Test(dataProvider="serviceData")
	public void g_C2SCardGroup_BlankMultipleOf(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- MultipleOf is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when MultipleOf is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("MultipleOf", "");
		Log.info("The entered MultipleOf is:" + Map_CardGroup.get("MultipleOf"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
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
	public void h_C2SCardGroup_BlankTax1Rate(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- Tax1Rate is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when Tax1Rate is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Rate", "");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax1Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax1"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax1rate")); 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider="serviceData")
	public void i_C2SCardGroup_BlankTax2Rate(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- Tax2Rate is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when Tax2Rate is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Rate", "");
		Log.info("The entered Tax2Rate is:" + Map_CardGroup.get("Tax2Rate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required",
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax2"),MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receivertax2rate"));

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}






	@Test(dataProvider="serviceData")
	public void j_C2SCardGroup_BlankProcessingFeeRate(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- ProcessingFeeRate is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when ProcessingFeeRate is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeRate", "");
		Log.info("The entered ProcessingFeeRate is:" + Map_CardGroup.get("ProcessingFeeRate"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfee"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfeerate")); 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	@Test(dataProvider="serviceData")
	public void k_C2SCardGroup_BlankProcessingFeeMinAmount(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- ProcessingFeeMinAmount is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when ProcessingFeeMinAmount is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeMinAmount", "");
		Log.info("The entered ProcessingFeeMinAmount is:" + Map_CardGroup.get("ProcessingFeeMinAmount"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfee"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfeeminrate"));

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	@Test(dataProvider="serviceData")
	public void l_C2SCardGroup_BlankProcessingFeeMaxAmount(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- ProcessingFeeMaxAmount is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when ProcessingFeeMaxAmount is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeMaxAmount", "");
		Log.info("The entered ProcessingFeeMaxAmount is:" + Map_CardGroup.get("ProcessingFeeMaxAmount"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addc2scardgroup.error.required", 
				MessagesDAO.getLabelByKey("cardgroup.testcardgroup.label.receiver"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfee"),MessagesDAO.getLabelByKey("cardgroup.cardgroupc2sdetails.label.receiveraccessfeemaxrate"));
 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	@Test(dataProvider="serviceData")
	public void n_C2SCardGroup_BlankReceiverConversionFactor(String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2S Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("C2S Card Group Validation- ReceiverConversionFactor is required in card group creation.");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode("To verify that correct Error message is displayed when ReceiverConversionFactor is entered as Blank for creating a C2S card group");
		currentNode.assignCategory("SIT");
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ReceiverConversionFactor","");
		Log.info("The entered ReceiverConversionFactor is:" + Map_CardGroup.get("ReceiverConversionFactor"));

		c2sCardGroup.c2SCardGroupSlabErrorValidation(Map_CardGroup, serviceName, subService);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.getLabelByKey("cardgroup.rp2p.error.invalidreceiverconvfactorrange"); 

		//Validator.messageCompare(actual, expected);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

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

