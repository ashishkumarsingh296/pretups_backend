package com.testscripts.smoke;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PCardGroup;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SMOKE_P2P_CARDGROUP)
public class Smoke_P2PCardGroup extends BaseTest {

    String cardGroupName;
    HashMap<String, String> dataMap;

    //Smoke CardGroup TestCase1: Add a Card Group
    @Test(dataProvider = "serviceData")
     @TestManager(TestKey = "PRETUPS-563") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void a_P2PCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException {
        final String methodName = "Test_P2PCardGroupGroupCreation";
        Log.startTestCase(methodName, serviceName, subService);
        
        
        P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PP2PCARDGRP1").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
        dataMap = (HashMap<String, String>) P2PCardGroup.P2PCardGroupCreation(serviceName, subService);
        cardGroupName = dataMap.get("CARDGROUPNAME");
        String actual = dataMap.get("ACTUALMESSAGE");
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SP2PCARDGRP1").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
        String expected = MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");
        Assertion.assertEquals(actual, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    //Smoke CardGroup TestCase2: Modify Card Group
    @Test(dataProvider = "serviceData")
     @TestManager(TestKey = "PRETUPS-568") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void b_ModifyP2PCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException {
        final String methodName = "Test_ModifyP2PCardGroup_EditCardGroup";
        Log.startTestCase(methodName, serviceName, subService);

        P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SP2PCARDGRP1").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
        String actual = P2PCardGroup.P2PCardGroupModification_EditCardGroup(serviceName, subService, cardGroupName);
        String expected = MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");
        Assertion.assertEquals(actual, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "serviceData")
     @TestManager(TestKey = "PRETUPS-580") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void c_ModifyP2PCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException {
        final String methodName = "Test_ModifyP2PCardGroup_DeleteCardGroup";
        Log.startTestCase(methodName, serviceName, subService);

        P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SP2PCARDGRP2").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
        String actual = P2PCardGroup.P2PCardGroupDeletion(serviceName, subService, cardGroupName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SP2PCARDGRP3").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
        String expected = MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successdeletemessage");
        Assertion.assertEquals(actual, expected);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "serviceData")
    public Object[][] TestDataFeed() {
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);

        int rowCount = ExcelUtility.getRowCount();

		/*Object[][] categoryData = new Object[1][2];
		for (int i = 1; i <= rowCount; i++) {

			String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);
			System.out.println("Service type is " +x);

			if (x.equals(_masterVO.getProperty("P2PCreditTransferCode"))){
				System.out.println(x.equals(_masterVO.getProperty("P2PCreditTransferCode")));



				categoryData[0][0] = ExcelUtility.getCellData(i, 1);
				System.out.println(categoryData[0][0]);
				categoryData[0][1] = ExcelUtility.getCellData(i, 2);
				System.out.println(categoryData[0][1]);

				break;

			}*/


        Object[][] categoryData = null;
        if (rowCount > 0) {
            categoryData = new Object[1][2];
            for (int i = 1; i <= rowCount; i++) {
                String x = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
                if (x.equals(_masterVO.getProperty("P2PCreditTransferCode"))) {
                    categoryData[0][0] = ExcelUtility.getCellData(i, 1);
                    categoryData[0][1] = ExcelUtility.getCellData(i, 2);

                    break;
                }

            }

        } else if (rowCount <= 0) {
            categoryData = new Object[][]{
                    {null, null}
            };
        }


        return categoryData;

    }

    /* ---------------------------------------------------------------------------------------- */
}
