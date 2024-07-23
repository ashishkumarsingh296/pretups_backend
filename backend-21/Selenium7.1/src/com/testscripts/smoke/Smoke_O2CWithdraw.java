package com.testscripts.smoke;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CWithdraw;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pageobjects.channeladminpages.o2cwithdraw.O2CWithdrawPage1;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class Smoke_O2CWithdraw extends BaseTest{

	static boolean TestCaseCounter = false;

	/*
	 *THIS DATA PROVIDER NOT IN USE
	 */
	
	//@DataProvider(name = "Data")
	@SuppressWarnings("unused")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowcnt1=ExcelUtility.getRowCount();
		String msisdn= null;
		for(int m=1;m<2;m++){
			//if(ExcelUtility.getCellData(0, "CATEGORY_NAME", m).equals("Super Distributor")||ExcelUtility.getCellData(0, "CATEGORY_NAME", m).equals("Distributor")){
				msisdn=ExcelUtility.getCellData(0, ExcelI.MSISDN, m);
				//break;
			//}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][2];
		for (int i = 1, j = 0; i <=rowCount; i++, j++) {
			//categoryData[j][0] = i;
			categoryData[j][0] = msisdn;
			categoryData[j][1] = ExcelUtility.getCellData(i, 1);
			System.out.println(categoryData[j][1]+" "+categoryData[j][0]);
		}
		return categoryData;
	}
	
//#####################################################################################################3#	
	
	@Test(dataProvider="Data")
	public void o2cWithdrawal(String parentCategory, String Category, String MSISDN, String ProductType){
		Log.startTestCase(this.getClass().getName());
		
		//test = extent.createTest("O2C Withdrawal: " +ProductType);
		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]O2C Withdrawal");
			TestCaseCounter = true;
		}
		// Network Stock Initiation
		currentNode = test.createNode("To verify that Operator user is able to perform Operator to channel withdrawal for category "+Category+", product Type :"+ProductType);
		currentNode.assignCategory("Smoke");

		
		O2CWithdraw o2cWithdraw = new O2CWithdraw(driver);
		O2CWithdrawPage1 o2cWithdrawPage1 = new O2CWithdrawPage1(driver);
		o2cWithdraw.o2cWithdraw(MSISDN, ProductType);
		String actual= o2cWithdrawPage1.getMessage();
		int txnIDIndex= actual.split(" ").length;
		String txnID= actual.split(" ")[txnIDIndex-1].replaceAll("[.]$","");
		System.out.println(txnID);
		//String expected= LoadPropertiesFile.MessagesMap.get("userreturn.withdraw.msg.success");
		String expected = MessagesDAO.prepareMessageByKey("userreturn.withdraw.msg.success", txnID);
		Validator.messageCompare(actual, expected);
	
	}
	
//#####################################################################################################33
	
	/**
	 * DataProvider for Operator to Channel Withdraw
	 * @return Object
	 */
	@DataProvider(name = "Data")
	public Object[][] TestDataFeed1() {
		String O2CWithdrawCode = _masterVO.getProperty("O2CWithdrawCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
/*
 * Array list to store Categories for which O2C transfer is allowed
 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(O2CWithdrawCode)) {
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