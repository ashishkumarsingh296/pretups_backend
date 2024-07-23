package com.testscripts.smoke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class Smoke_C2STransfer extends BaseTest {

	static boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	public void C2Srecharge(String FromParent, String FromCategory, String PIN,String service)
			throws IOException, InterruptedException {

		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
		test = extent.createTest("[Smoke]C2S Recharge");
		TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To verify that " + FromCategory + " category user is able to perform C2S Recharge");
		currentNode.assignCategory("Smoke");
		C2STransfer C2STransfer = new C2STransfer(driver);
		C2STransfer.performC2STransfer(FromParent, FromCategory,PIN, service);
		Log.endTestCase(this.getClass().getName());
	}
	
	/*
	 * THIS DATAPROVIDER NOT IN USE
	 */
	
	//@DataProvider(name = "categoryData")
	@SuppressWarnings("unused")
	public Object[][] TestDataFeed() throws IOException {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		System.out.println(rowCount);
		int m = 0;

		String[] servi = { "GRC", "RC", "PPB" };
		String c2sService = null;

		for (String l : servi) {
			// System.out.println();
			for (int i = 1; i <= rowCount; i++) {

				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				String services = ExcelUtility.getCellData(i, 5);
				String servArray[];
				boolean found = false;

				if (services != null) {

					servArray = services.split(", ");

					for (String x : servArray) {

						if (x.equals(l)) {
							c2sService = x;
							m++;
							found = true;
							System.out.println(x);
							break;
						}
					}
				}

			}
		}
		System.out.println(m);

		Object[][] categoryData = new Object[m][3];

		String[] servi1 = { "GRC", "RC", "PPB" };
		int j = 0;
		for (String l : servi1) {

			for (int i = 1; i <= rowCount; i++) {
				System.out.println(i);

				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				String services = ExcelUtility.getCellData(i, 5);
				String servArray[];
				boolean found = false;

				if (services != null) {

					servArray = services.split(", ");
					for (String x : servArray) {
						if (x.equals(l)) {
							c2sService = x;

							System.out.println("Rownum:" + (j));

							categoryData[j][0] = ExcelUtility.getCellData(i, 1);
							System.out.println(categoryData[j][0]);
							categoryData[j][1] = ExcelUtility.getCellData(i, 2);
							System.out.println(categoryData[j][1]);
							categoryData[j][2] = c2sService;
							j++;

							break;
						}
					}
				}
			}
			System.out.println(j);
		}
		return categoryData;
	}
	
//############################################################################################3
	
	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 */
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed1() {
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
/*
 * Array list to store Categories for which Customer Recharge is allowed
 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(CustomerRechargeCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
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
		Object[][] Data = new Object[userCounter][4];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][3] = CustomerRechargeCode;
				j++;
			}

		}
					
	return Data;
	}

}
