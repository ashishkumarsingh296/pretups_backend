package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.Features.P2PCardGroup;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils.constants.TestCategory;

public class SIT_PromotionalCardGroup extends BaseTest {


	String cardGroupNameC2S;
	String cardGroupNameP2P;
	String cardGroupSetID;
	HashMap<String, String> dataMap;
	HashMap<String, String> dataMap1;
	Map<String, String> Map_CardGroup;
	static boolean TestCaseCounter = false;
	String assignCategory="SIT";


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
		rowCount = ExcelUtility.getRowCount();
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
	
	@DataProvider(name = "serviceData1")
	public Object[][] TestDataFeed1() {
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

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		rowCount = ExcelUtility.getRowCount();
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


	@Test(dataProvider="serviceData")
	public void a_C2SCardGroup_Promo(int rowNum, String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITPROMOCARDGROUP1").getModuleCode());
			TestCaseCounter = true;
		}
		Log.startTestCase("Promotional Card Group Creation");

		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOCARDGROUP1").getExtentCase());
		currentNode.assignCategory(assignCategory);


		HashMap<String, String> mapInfo = (HashMap<String, String>) c2sCardGroup.c2SPromoCardGroupCreation(serviceName, subService);
		cardGroupNameC2S= mapInfo.get("CARDGROUPNAME");
		String actual=mapInfo.get("ACTUALMESSAGE");
		

		c2sCardGroup.writePromoCardGroupToExcel(cardGroupNameC2S, rowNum);


	}


	
	
	@Test(dataProvider="serviceData1")
	public void b_P2PCardGroup_Promo(int rowNum, String serviceName, String subService) throws InterruptedException{
		if (TestCaseCounter == false) {
			test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITPROMOCARDGROUP2").getModuleCode());
			TestCaseCounter = true;
		}
		Log.startTestCase("P2P Promotional Card Group Creation");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITPROMOCARDGROUP2").getExtentCase());
		currentNode.assignCategory(assignCategory);


		Map<String, String> mapInfo = p2pCardGroup.p2pPromoCardGroupCreation(serviceName, subService);
		cardGroupNameP2P= mapInfo.get("CARDGROUPNAME");
		String actual=mapInfo.get("ACTUALMESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");
		Validator.messageCompare(actual, expected);

		p2pCardGroup.writePromoCardGroupToExcel(cardGroupNameP2P, rowNum);


	}
	
	@Test(dataProvider="serviceData")
	public void c_ViewC2SCardGroup_Promo(int rowNum, String serviceName, String subService) throws InterruptedException{
        if (TestCaseCounter == false) {
               test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITPROMOCARDGROUP3").getModuleCode());
               TestCaseCounter = true;
        }
        Log.startTestCase("View Promotional Card Group");

        C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITPROMOCARDGROUP3").getExtentCase(), serviceName,subService));
        currentNode.assignCategory(assignCategory);

        String cardGroup = c2sCardGroup.fetchCardGroup(rowNum);
        String actual =c2sCardGroup.viewC2SCardGroup_Promo(serviceName, subService,cardGroup );
        String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.view.heading");
        
        

        Assertion.assertEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase("View Promotional Card Group");


 }

 @Test(dataProvider="serviceData")
 @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
 public void d_ModifyC2SCardGroup_EditCardGroup(int rowNum, String serviceName, String subService) throws InterruptedException{
        final String methodName = "Test_ModifyC2SCardGroup_EditCardGroup";
        Log.startTestCase(methodName, serviceName, subService);

        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITPROMOCARDGROUP4");
        C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),serviceName,subService)).assignCategory(TestCategory.SIT);
        String cardGroup = c2sCardGroup.fetchCardGroup(rowNum);
        String actual= c2sCardGroup.c2sCardGroupModification_Promo(serviceName, subService, cardGroup);
        String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupc2sdetailsview.successeditmessage");
        Assertion.assertEquals(actual, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
 }
 

@Test(dataProvider = "serviceData1")
@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
public void e_ModifyP2PCardGroup_EditCardGroup(int rowNum, String serviceName, String subService) throws InterruptedException {
  final String methodName = "Test_ModifyP2PCardGroup_EditCardGroup";
  Log.startTestCase(methodName, serviceName, subService);

  P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

  currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITPROMOCARDGROUP5").getExtentCase(), serviceName,subService));
        currentNode.assignCategory(assignCategory);

  String cardGroup = P2PCardGroup.fetchCardGroup(rowNum);
  String actual = P2PCardGroup.P2PCardGroupModification_EditCardGroup(serviceName, subService, cardGroup);
  String expected = MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");
  Assertion.assertEquals(actual, expected);

  Assertion.completeAssertions();
  Log.endTestCase(methodName);
}

@Test(dataProvider="serviceData1")
 @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
 public void f_viewP2PCardGroup(int rowNum, String serviceName, String subService)throws InterruptedException{

        final String methodName = "Test_viewCardGroup";
        Log.startTestCase(methodName);
        P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITPROMOCARDGROUP6").getExtentCase(), serviceName,subService));
        currentNode.assignCategory(assignCategory);
        String cardGroup = P2PCardGroup.fetchCardGroup(rowNum);
        String actual =P2PCardGroup.viewP2PCardGroup (serviceName, subService,cardGroup);
        String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.view.heading");
        Assertion.assertEquals(actual, expected);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
 }

	
	
	
	
	
	
}	

