package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

/**
 * @author lokesh.kontey
 * This class creates Network Admin for PreRequisite
 */
@ModuleManager(name = Module.PREREQUISITE_NETWORK_ADMIN_CREATION)
public class PreRequisite_NetworkAdminCreation extends BaseTest {

    private static String NetworkADM_Name = null;
    private static String SuperADM_Name = null;
    private static int NetworkAdminDataSheetRowNum = 0;

    @Test
    @TestManager(TestKey = "PRETUPS-404") // TO BE UNCOMMENTED BY WITH JIRA TEST CASE ID
    public void Test_CreateNetworkAdmin() throws InterruptedException {
        final String methodName = "Test_CreateNetworkAdmin";
        Log.startTestCase(methodName);

        OperatorUser optUsrCreation = new OperatorUser(driver);
        initializeTestData();

        // Test Case Number 1: Network Admin Creation
        currentNode = test.createNode(
                MessageFormat.format(
                        _masterVO.getCaseMasterByID("PNETWORKADMINCREATION1").getExtentCase(), SuperADM_Name, NetworkADM_Name))
                .assignCategory(TestCategory.PREREQUISITE);
        HashMap<String, String> optMap = optUsrCreation.operatorUserInitiate(SuperADM_Name, NetworkADM_Name);
        Assertion.assertNotNull(optMap.get("initiateMsg"));

        // Test Case Number 2: Network Admin Approval
        currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKADMINCREATION2").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
        optUsrCreation.approveUser(SuperADM_Name);

        // Test Case Number 3: Network Admin Password Change
        currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKADMINCREATION3").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
        optUsrCreation.changeUserFirstTimePassword();

        String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
        String expected = MessagesDAO.getLabelByKey("login.changeCommonLoginPassword.updatesuccessmessage");

        boolean assertStatus = Assertion.assertEquals(actual, expected);

        if (assertStatus) {
            optUsrCreation.writeOperatorUserData(NetworkAdminDataSheetRowNum);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  H   E   L   P   E   R       M   E   T   H   O   D   S ------------------ */
    /* ------------------------------------------------------------------------------------------------- */
    private void initializeTestData() {
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        while (NetworkAdminDataSheetRowNum <= rowCount) {
            String ParentCategoryCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, NetworkAdminDataSheetRowNum);
            String CategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, NetworkAdminDataSheetRowNum);
            if (ParentCategoryCode.equals(PretupsI.SUPERADMIN_CATCODE) && CategoryCode.equals(PretupsI.NETWORKADMIN_CATCODE)) {
                NetworkADM_Name = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, NetworkAdminDataSheetRowNum);
                SuperADM_Name = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, NetworkAdminDataSheetRowNum);
                break;
            }

            NetworkAdminDataSheetRowNum++;
        }
    }
}
