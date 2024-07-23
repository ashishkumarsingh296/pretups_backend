package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.apicontrollers.APIController;
import com.apicontrollers.O2C_API;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class PreRequisite_O2CTransferIDEA extends BaseTest {
	
	static boolean TestCaseCounter = false;

	@Test(dataProvider="categoryData")
	public void o2cTransferTC(String originID, String pin, String extcode, String product ) throws InterruptedException {
		final String methodname = "o2cTransferTC";
		Log.startTestCase(methodname, originID, pin, extcode, product);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[Pre-Requisite]"+_masterVO.getCaseMasterByID("PO2CTRFIDEA1").getModuleCode());
			TestCaseCounter = true;
		}	
		
		/*
		 * Test case to initiate O2C Transfer
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFIDEA1").getExtentCase(),originID));
		currentNode.assignCategory("Pre-Requisite");
		
		RandomGeneration random = new RandomGeneration();
		
		HashMap<String, String> xmlDataMap = new HashMap<String, String>();
		xmlDataMap.put("EXTNWCODE", _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		xmlDataMap.put("USERORIGINID", originID);
		xmlDataMap.put("PIN", pin);
		xmlDataMap.put("EXTCODE", extcode);
		xmlDataMap.put("EXTTXNDATE", "2016-03-15T19:11:11.11");
		xmlDataMap.put("EXTTXNNUMBER", random.randomNumeric(10));
		xmlDataMap.put("PRODUCTCODE", "101");
		xmlDataMap.put("QTY", _masterVO.getProperty("Quantity"));
		xmlDataMap.put("TRFCATEGORY", "FOC");
		xmlDataMap.put("REFNUMBER", "600009");
		xmlDataMap.put("PAYMENTTYPE", "CHQ");
		xmlDataMap.put("PAYMENTINSTNUMBER", "889810");
		xmlDataMap.put("REMARKS", "O2C Direct Transfer");
		
		String xmlData = O2C_API.getIdeaAPI(xmlDataMap);		
		APIController.execute(xmlData);
		
		Log.endTestCase(methodname);
	}

	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 */
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {
		String FOCTransferCode = _masterVO.getProperty("FOCCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which FOC transfer is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(FOCTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
			}
		}

		/*
		 * Counter to count number of users exists in channel users hierarchy sheet 
		 * of Categories for which O2C transfer is allowed
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
		 * Store required data of 'O2C transfer allowed category' users in Object
		 */
		Object[][] Data = new Object[userCounter][3];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				String PIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				String userDetails[] = DBHandler.AccessHandler.getUserDetails(loginID, "ORIGIN_ID", "EXTERNAL_CODE");
				Data[j][1] = Decrypt.APIEncryption(PIN);
				Data[j][2] = userDetails[1];
				Data[j][0] = userDetails[0];
				j++;
			}
		}
			
		/*
		 * Store products from Product Sheet to Object.
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		Object[] ProductObject = new Object[prodRowCount];
		for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
			ProductObject[i] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
		}

		/*
		 * Creating combination of channel users for each product.
		 */
		int countTotal = ProductObject.length * userCounter;
		Object[][] o2cData = new Object[countTotal][4];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			o2cData[j][0] = Data[k][0];
			o2cData[j][1] = Data[k][1];
			o2cData[j][2] = Data[k][2];
			o2cData[j][3] = ProductObject[i];
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
