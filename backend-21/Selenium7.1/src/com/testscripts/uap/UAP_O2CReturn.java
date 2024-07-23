package com.testscripts.uap;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CReturn;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class UAP_O2CReturn extends BaseTest{
	
	public boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	public void UAP_O2CReturn_TC1(String parentCategory, String category, String userMSISDN, String productType) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Operator to Channel Return");
			TestCaseCounter = true;
		}
		
		String ReturnAmount = "100";
		O2CReturn O2CReturnFeature = new O2CReturn(driver);

		currentNode = test.createNode("To verify that " + category + " category user is able to perform O2C Return");
		currentNode.assignCategory("UAP");
		O2CReturnFeature.performO2CReturn(parentCategory, category, userMSISDN, productType, ReturnAmount, "PVG Automated Testing");
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
