package com.testscripts.uap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferSpring;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class UAP_C2STransferSpring extends BaseTest {

	static boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	public void C2Srecharge(String ParentCategory, String FromCategory, String PIN,String service)
			throws IOException, InterruptedException {

		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
		test = extent.createTest("[UAP]C2S Recharge");
		TestCaseCounter = true;
		}
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		currentNode = test.createNode("To verify that Category(Parent Category): " + FromCategory +"("+ParentCategory+") user is able to perform C2S Recharge");
		currentNode.assignCategory("UAP");
		C2STransferSpring C2STransferSpring = new C2STransferSpring(driver);
		Map<String,String> map1 = new HashMap<>();
		String subMSISDN;
		if(service.equals("PPB"))
		{
			subMSISDN=UniqueChecker.generate_subscriber_MSISDN("Postpaid");
		}
		else{
			subMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		}
		
		map1.put("parentCategory", ParentCategory);
		map1.put("category", FromCategory);
		map1.put("service",service);
		map1.put("pin",PIN );
		map1.put("msisdn",subMSISDN );
		if(webAccessAllowed.equals("Y")){
			C2STransferSpring.performC2STransfer(map1,false,false);}
		else{currentNode.log(Status.SKIP,"As webaccess is not allowed for "+FromCategory+", case is skipped.");}
		Log.endTestCase(this.getClass().getName());
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
	 * of Categories for which C2S transfer is allowed
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
	 * Store required data of 'C2S transfer allowed category' users in Object
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
