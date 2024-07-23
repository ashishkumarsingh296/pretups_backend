package com.testscripts.prerequisites;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.GroupRoleManagement;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_GROUP_ROLE_MANAGEMENT)
public class PreRequisite_GroupRoleManagement extends BaseTest {

    String groupRoleName;

    /* @Test(dataProvider = "operatorUserData", priority = 1)
    public void Test_GroupRoleCreationForOperatorUsers(int rowNum, String domainCode, String categoryName, String ParentCategory, String geoType, String categoryCode) throws InterruptedException {
        final String methodName = "Test_GroupRoleCreationForOperatorUsers";
        Log.startTestCase(methodName);

        GroupRoleManagement GroupRoleManagement = new GroupRoleManagement(driver);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGRPROLE1").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (categoryCode.contains(PretupsI.SUPERADMIN_CATCODE) || categoryCode.contains(PretupsI.MONITORSERVER_CATCODE)) {
            Assertion.assertSkip("The Category is either Super Admin or Monitor Server, hence Test Case Skipped");
        } else {
            int dataLength = CONSTANT.USERACCESSDAO.length;
            int i = 0;
            for (i = 0; i < dataLength; i++) {
                if (CONSTANT.USERACCESSDAO[i][4].equals(categoryCode) && CONSTANT.USERACCESSDAO[i][5].equals(PretupsI.YES)) {
                    System.out.println("print i" + i);
                }
                break;
            }

            if (CONSTANT.USERACCESSDAO[i][5].equals(PretupsI.YES)) {
                String result[] = GroupRoleManagement.addGroupRoleAsperCategory(domainCode, categoryName);
                groupRoleName = result[0];
                System.out.println("The Created Group Role  is:" + groupRoleName);
                GroupRoleManagement.writeGroupRoleToOperatorSheet(rowNum, groupRoleName);

                String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successaddmessage");
                Validator.messageCompare(result[1], ExpectedMessage);
            } else {
                String domainName = DBHandler.AccessHandler.fetchDomainName(domainCode);
                String result[] = GroupRoleManagement.addGroupRole(domainName, categoryName);
                groupRoleName = result[0];
                System.out.println("The Created Group Role  is:" + groupRoleName);
                GroupRoleManagement.writeGroupRoleToOperatorSheet(rowNum, groupRoleName);

                String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successaddmessage");
                Validator.messageCompare(result[1], ExpectedMessage);
            }
        }

        Log.endTestCase(this.getClass().getName());
    }*/

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-505") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID MAPPING
    public void Test_GroupRoleCreation(int rowNum, String domainName, String categoryName, String ParentCategory, String geoType, String categoryCode) throws InterruptedException {
        final String methodName = "Test_GroupRoleCreation";
        Log.startTestCase(methodName);

        GroupRoleManagement GroupRoleManagement = new GroupRoleManagement(driver);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGRPROLE2").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);

        int dataLength = CONSTANT.USERACCESSDAO.length;
        int i = 0;
        boolean counter = false;
        for (i = 0; i < dataLength; i++) {
            if (CONSTANT.USERACCESSDAO[i][4].equals(categoryCode) && CONSTANT.USERACCESSDAO[i][5].equals(PretupsI.YES)) {
                String result[] = GroupRoleManagement.addGroupRoleAsperChannelCategory(domainName, categoryName, categoryCode);
                groupRoleName = result[0];
                GroupRoleManagement.writeGroupRoleToSheet(rowNum, groupRoleName);

                String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successaddmessage");
                Assertion.assertEquals(result[1], ExpectedMessage);
                counter = true;
                break;
            }
        }

        if (!counter) {
            String result[] = GroupRoleManagement.addGroupRole(domainName, categoryName);
            groupRoleName = result[0];
            GroupRoleManagement.writeGroupRoleToSheet(rowNum, groupRoleName);
            String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successaddmessage");
            Assertion.assertEquals(result[1], ExpectedMessage);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        int objectCount = 0;
        for (int i = 1; i <= rowCount; i++) {
            if (DBHandler.AccessHandler.webInterface(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i)).equals("Y"))
                objectCount++;
        }

        Object[][] categoryData = new Object[objectCount][6];
        for (int i = 1, j = 0; i <= rowCount; i++) {
            if (DBHandler.AccessHandler.webInterface(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i)).equals("Y")) {
                categoryData[j][0] = i;
                categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
                categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                categoryData[j][4] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
                categoryData[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                j++;
            }
        }

        return categoryData;
    }

    @DataProvider(name = "operatorUserData")
    public Object[][] TestDataFeed1() {

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        Object[][] categoryData = new Object[rowCount][6];
        for (int i = 1, j = 0; i <= rowCount; i++, j++) {

            categoryData[j][0] = i;
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i);
            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, i);
            categoryData[j][4] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
            categoryData[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);

        }
        
        return categoryData;
    }

    /* ------------------------------------------------------------------------------------------------ */
}
