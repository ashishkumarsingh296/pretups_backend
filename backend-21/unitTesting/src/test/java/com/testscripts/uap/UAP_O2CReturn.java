package com.testscripts.uap;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CReturn;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
import com.commons.PretupsI;

@ModuleManager(name = Module.UAP_O2C_RETURN)
public class UAP_O2CReturn extends BaseTest{
	
	public boolean TestCaseCounter = false;
	String assignCategory="UAP";
	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-320") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void UAP_O2CReturn_TC1(String parentCategory, String category, String userMSISDN, String productType, String ProductCode) throws ParseException, SQLException {
		final String methodname = "UAP_O2CReturn_TC1";
		Log.startTestCase(methodname, parentCategory, category, userMSISDN, productType, ProductCode);
		
		
		String ReturnAmount = "100";
		O2CReturn O2CReturnFeature = new O2CReturn(driver);
		businessController businessController = new businessController(_masterVO.getProperty("O2CReturnCode"), userMSISDN, null);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CRETURN1").getExtentCase(), category));
		currentNode.assignCategory(assignCategory);
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		O2CReturnFeature.performO2CReturn(parentCategory, category, userMSISDN, productType, ReturnAmount, "PVG Automated Testing");
	
		/*
		 * Test Case to validate Network Stocks after successful O2C Withdraw
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UO2CRETURN2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(ProductCode, ReturnAmount);
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		/*
		 * Test Case to validate Channel User balance after successful O2C Withdraw
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UO2CRETURN3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
	
		Log.endTestCase(methodname);
	}
	
	@Test(dataProvider = "categoryData")
	 //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void UAP_O2CReturn_TC2(String parentCategory, String category, String userMSISDN, String productType, String ProductCode) throws ParseException, SQLException {
		final String methodname = "UAP_O2CReturn_TC2";
		Log.startTestCase(methodname, parentCategory, category, userMSISDN, productType, ProductCode);

		//Change the ID
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CRETURN4").getExtentCase(), category)).assignCategory(TestCategory.SIT);
        
		String ReturnAmount = "100";
		O2CReturn O2CReturnFeature = new O2CReturn(driver);

		String message = O2CReturnFeature.performO2CReturnPinIncorrect(parentCategory, category, userMSISDN, productType, ReturnAmount, "PVG Automated Testing");
		String expectedMessage = "Invalid PIN";
		Assertion.assertEquals(message, expectedMessage);

		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 */
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {
		String O2CReturnCode = _masterVO.getProperty("O2CReturnCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
/*
 * Array list to store Categories for which O2C Return is allowed
 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(O2CReturnCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
			}
		}

/*
 * Counter to count number of users exists in channel users hierarchy sheet 
 * of Categories for which O2C Return is allowed
 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				userCounter++;
			}
		}

/*
 * Store required data of 'O2C Return allowed category' users in Object
 */
		Object[][] Data = new Object[userCounter][3];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][0] = ExcelUtility.getCellData(0,ExcelI.PARENT_CATEGORY_NAME, i);
				j++;
			}
		}
			
/*
 * Store products from Product Sheet to Object.
 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		Object[][] ProductObject = new Object[prodRowCount][2];
		for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
			ProductObject[i][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
			ProductObject[i][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, j);
		}

/*
 * Creating combination of channel users for each product.
 */
		int countTotal = ProductObject.length * userCounter;
		Object[][] o2cData = new Object[countTotal][5];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			o2cData[j][0] = Data[k][0];
			o2cData[j][1] = Data[k][1];
			o2cData[j][2] = Data[k][2];
			o2cData[j][3] = ProductObject[i][0];
			o2cData[j][4] = ProductObject[i][1];
			if (k < userCounter) {
				k++;
				if (k >= userCounter) {
					k = 0;
					i++;
					if (i >= ProductObject.length)
						i = 0;
				}
			} else {
				k = 0;
			}
		}		
			return o2cData;
	}
	
	
}
