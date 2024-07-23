package com.testscripts.sit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.Features.Map_CardGroup;
import com.Features.Map_TCPValues;
import com.Features.TransferControlProfile;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_PromotionalCardGroup extends BaseTest {


	String cardGroupName;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	Map<String, String> Map_CardGroup;
	static boolean TestCaseCounter = false;



	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		ArrayList<String> aList = new ArrayList<String>();
		int rowCount = ExcelUtility.getRowCount();

		for (int i = 1; i <= rowCount; i++) {

			String To_Category = ExcelUtility.getCellData(0, "TO_CATEGORY", i);
			if(To_Category.equalsIgnoreCase("Subscriber"))
			{
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);

				// aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
				aList.addAll(new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*"))));
				System.out.println("services: "+aList);
			}


		}


		System.out.println("services list: "+aList);
		Set<String> uniqueList = new HashSet<String>(aList);
		System.out.println("Unique services are: " + uniqueList);

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int counter =0;

		for (int i = 1; i <= rowCount; i++) {

			String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);

			if (aList.contains(x)){

				counter++;


			}
		}

		Object[][] categoryData = new Object[counter][3];			
		for (int i = 1, j =0; i <= rowCount; i++) {

			String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);

			if (aList.contains(x)){

				System.out.println("alist enters loop");
				categoryData[j][0] = i;
				categoryData[j][1] = ExcelUtility.getCellData(0,ExcelI.NAME, i);
				System.out.println(categoryData[j][0]);
				categoryData[j][2] = ExcelUtility.getCellData(0,ExcelI.SELECTOR_NAME, i);
				System.out.println(categoryData[j][1]);
				j++;
			}
		}



		return categoryData;
	}


	/*	

	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][3];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(i, 1);
			categoryData[j][2] = ExcelUtility.getCellData(i, 2);
		}
		return categoryData;
	}


	 */


	@Test(dataProvider="serviceData")
	public void a_C2SCardGroup_Promo(int rowNum, String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Promotional Card Group");
			TestCaseCounter = true;
		}
		Log.startTestCase("Promotional Card Group Creation");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);


		currentNode = test.createNode("To verify that Network admin can create a Promotional Card Group successfully");
		currentNode.assignCategory("SIT");


		HashMap<String, String> mapInfo = (HashMap<String, String>) c2sCardGroup.c2SPromoCardGroupCreation(serviceName, subService);
		cardGroupName= mapInfo.get("CARDGROUPNAME");
		String actual=mapInfo.get("ACTUALMESSAGE");
		

		c2sCardGroup.writePromoCardGroupToExcel(cardGroupName, rowNum);


	}


}	

