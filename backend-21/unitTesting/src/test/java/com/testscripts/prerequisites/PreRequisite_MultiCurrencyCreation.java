package com.testscripts.prerequisites;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.MultiCurrency;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_MULTICURRENCY)
public class PreRequisite_MultiCurrencyCreation extends BaseTest{
	
	

	
	@Test(dataProvider = "availableCurrencyCodes", priority = 0)
	public void Test_CreateMultiCurrency(int rowNum, String currency) {
		final String methdoName = "Test_CreateMultiCurrency";
		int ODRC_MC = Integer.parseInt(_masterVO.getClientDetail("ODRC_MC"));
		
		Log.startTestCase(methdoName, currency);
		

			
			MultiCurrency multiCurrency = new MultiCurrency(driver);
			// Test Case - To verify if Network Admin can Add Currency
			currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR1").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
			// set ODRC_MC to 1 in ClientLib.properties for client specific flow
			
			if(ODRC_MC == 0) {
				Assertion.assertSkip("Not a valid case in this scenario");
			}

			else {
				
				String actual1 = multiCurrency.addCurrency(currency);
				if(actual1.equals(MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.approvalpending"))) {
					Assertion.assertSkip(actual1);
				}
				String expected1 = MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.successaddmessage");
				Assertion.assertEquals(actual1, expected1);
				
				// Test Case - To verify if Network Admin can perform Level 1 Approval
				currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR2").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
				String actual2 = multiCurrency.approveMultiCurrencyLevel1();
				String expected2 = MessagesDAO.prepareMessageByKey("currencyconversion.approvecurrency.successmessage");
				Assertion.assertEquals(actual2, expected2);
				
				// Test Case - To verify if Network Admin can perform Level 2 Approval
				currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR3").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
				String actual3 = multiCurrency.approveMultiCurrencyLevel2();
				String expected3 = MessagesDAO.prepareMessageByKey("currencyconversion.approvecurrency.successmessage");
				Assertion.assertEquals(actual3, expected3);
		
			}
		

		

		Assertion.completeAssertions();
		Log.endTestCase(methdoName);
	}
	
	@Test(dataProvider = "invalidConversionRates" , priority = 1)
	public void Test_MultiCurrencyWithInvalidConversionRates(String conversionRate) {
		final String methodname = "Test_MultiCurrencyWithInvalidConvertionRates";
		int ODRC_MC = Integer.parseInt(_masterVO.getClientDetail("ODRC_MC"));
		Log.startTestCase(methodname);
		
		MultiCurrency multiCurrency = new MultiCurrency(driver);
		
		// Test Case -  To verify if currency addition fails with invalid data
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR4").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
		// set ODRC_MC to 1 in ClientLib.properties for client specific flow
		if(ODRC_MC == 0) {
			Assertion.assertSkip("Not a valid case in this scenario");
		}
		else {
			String actual1 = multiCurrency.addCurrencyWithInvalidConversionRate(conversionRate);
			String expected1 = MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.conversionratezero");
			Assertion.assertEquals(actual1, expected1);
			
		}

		
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	@Test(priority = 2)
	public void Test_MultiCurrencyWithBlankData() {
		final String methodname = "Test_MultiCurrencyWithBlankData";
		int ODRC_MC = Integer.parseInt(_masterVO.getClientDetail("ODRC_MC"));
		Log.startTestCase(methodname);
		
		MultiCurrency multiCurrency = new MultiCurrency(driver);
		
		// Test Case - To verify if currency addition fails when Currency Code is left blank
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR5").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
		// set ODRC_MC to 1 in ClientLib.properties for client specific flow
		if(ODRC_MC == 0) {
			Assertion.assertSkip("Not a valid case in this scenario");
		}
		else {
			String actual1 = multiCurrency.addMulticurrencywithBlankCurrencyCode();
			String expected1 = MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.targetcurrencycodenull");
			Assertion.assertEquals(actual1, expected1);
			
			// Test Case - To verify if currency addition fails when Currency Name is left blank
			currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR6").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
			String actual2 = multiCurrency.addMulticurrencywithBlankCurrencyName();
			String expected2 = MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.targetcurrencynamenull");
			Assertion.assertEquals(actual2, expected2);
		}

	
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	
	
	}
	
	@Test(priority = 3)
	public void Test_MultiCurrencyRejection() {
		final String methodname = "Test_MultiCurrencyRejection";
		int ODRC_MC = Integer.parseInt(_masterVO.getClientDetail("ODRC_MC"));
		Log.startTestCase(methodname);
		
		MultiCurrency multiCurrency = new MultiCurrency(driver);
		
		//Test case - To verify Network Admin is able to reject currency at Level 1 Approval
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR7").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
		// set ODRC_MC to 1 in ClientLib.properties for client specific flow
		if(ODRC_MC == 0) {
			Assertion.assertSkip("Not a valid case in this scenario");
		}
		else {
			String actual1 = multiCurrency.rejectCurrencyLevel1();
			if(actual1.equals(MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.approvalpending"))) {
				Assertion.assertSkip(actual1);
			}
			String expected1 = MessagesDAO.prepareMessageByKey("currencyconversion.approvecurrency.successmessagereject");
			Assertion.assertEquals(actual1, expected1);
			
			//Test case - To verify Network Admin is able to reject currency at Level 2 Approval
			currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR8").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
			String actual2 = multiCurrency.rejectCurrencyLevel2();
			if(actual2.equals(MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.approvalpending"))) {
				Assertion.assertSkip(actual2);
			}
			
			String expected2 = MessagesDAO.prepareMessageByKey("currencyconversion.approvecurrency.successmessagereject");
			Assertion.assertEquals(actual2, expected2);
		}

		
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	@Test(priority = 4)
	public void Test_ValidationAtApprovals() {
		final String methodname = "Test_ValidationAtApprovals";
		int ODRC_MC = Integer.parseInt(_masterVO.getClientDetail("ODRC_MC"));
		Log.startTestCase(methodname);
		
		MultiCurrency multiCurrency = new MultiCurrency(driver);
		
		// Test case- To verify that approval fails at level 1 for invalid conversion rate
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR9").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
		// set ODRC_MC to 1 in ClientLib.properties for client specific flow
		if(ODRC_MC == 0) {
			Assertion.assertSkip("Not a valid case in this scenario");
		}
		else {
			String actual1 = multiCurrency.approvalFailLevel1();
			if(actual1.equals(MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.approvalpending"))) {
				Assertion.assertSkip(actual1);
			}
			String expected1 = MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.conversionratezero");
			Assertion.assertEquals(actual1, expected1);
			
			// Test case- To verify that approval fails at level 2 for invalid conversion rate
			currentNode = test.createNode(_masterVO.getCaseMasterByID("PMULTICURR10").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
			String actual2 = multiCurrency.approvalFailLevel2();
			String expected2 = MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.conversionratezero");
			Assertion.assertEquals(actual2, expected2);
		}

		
		Assertion.completeAssertions();
		Log.methodExit(methodname);
	}
	
	 /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "availableCurrencyCodes")
    public Object[][] currencyCodes() {
    	Object[][] data = DBHandler.AccessHandler.fetchCurrencyCodes();
    	Object[][] currencyCodes = null;
    	
    	if(data.length == 0) {
    		currencyCodes = new Object[][]{ 
    			{0,null}
    		};
    		
    	}
    	else {
    		currencyCodes = new Object[data.length][2];
    		for(int i=0; i<data.length; i++) {
    			currencyCodes[i][0] = i;
    			currencyCodes[i][1] = data[i][0];
    		}
    	}
    	return currencyCodes;

    }
    
    @DataProvider(name = "invalidConversionRates")
    public Object[][] conversionRates(){
    	String negativeConversionRate = "-" + RandomGeneration.randomDecimalNumer(2, 1);
    	String varCharConversionRate = "Automated String";
    	String blankConversionRate = "";
    	Object[][] invalidConversionRates = new Object[3][1];
    	invalidConversionRates[0][0] = negativeConversionRate;
    	invalidConversionRates[1][0] = varCharConversionRate;
    	invalidConversionRates[2][0] = blankConversionRate;
    	
    	return invalidConversionRates;
    }

    /* ------------------------------------------------------------------------------------------------- */
}
