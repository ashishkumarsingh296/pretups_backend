package com.testscripts.smoke;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
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
 * This class is created to add Operator Users
 */

@ModuleManager(name = Module.SMOKE_OPERATOR_USER_CREATION)
public class Smoke_OperatorUserCreation extends BaseTest {

    @Test(dataProvider = "Domain&CategoryProvider")
    @TestManager(TestKey = "PRETUPS-278") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_OperatorUserCreation(String ParentUser, String LoginUser) throws InterruptedException {
        final String methodName = "Test_OperatorUserCreation";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("POPTCREATION1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SOPTCREATION1");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SOPTCREATION2");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SOPTCREATION3");
        CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("POPTCREATION2");
        CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("POPTCREATION3");

        OperatorUser OperatorUserLogic = new OperatorUser(driver);
        String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");
        String intOptInitiateMsg;

        // Test Case Number 1: Operator User Initiate.
        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), ParentUser, LoginUser)).assignCategory(TestCategory.SMOKE);
        HashMap<String, String> optresultMap = OperatorUserLogic.operatorUserInitiate(ParentUser, LoginUser);

        // Test Case Number 2: Message Validation
        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), LoginUser)).assignCategory(TestCategory.SMOKE);

        if (APPLEVEL.equals("0"))
            intOptInitiateMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessage", optresultMap.get("UserName"));
        else
            intOptInitiateMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessageforrequest", optresultMap.get("UserName"));

        Assertion.assertEquals(optresultMap.get("initiateMsg"), intOptInitiateMsg);

        // Test Case Number 3: Operator User Approval
        if (APPLEVEL.equals("1")) {
            currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), ParentUser, LoginUser)).assignCategory(TestCategory.SMOKE);
            optresultMap = OperatorUserLogic.approveUser(ParentUser);

            // Test Case Number 4: Operator User Approval message validation
            currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), LoginUser)).assignCategory(TestCategory.SMOKE);
            String intOptApproveMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.approveuccessmessage", optresultMap.get("UserName"));

            Assertion.assertEquals(optresultMap.get("approveMsg"), intOptApproveMsg);
        } else {
            Log.info("Approval is not required.");
        }

        // Test Case Number 5: Operator user Password Change
        currentNode = test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), LoginUser)).assignCategory(TestCategory.SMOKE);
        OperatorUserLogic.changeUserFirstTimePassword();

        // Test Case Number 6: Operator user PIN Change
        if (OperatorUserLogic.pinChangeRequired.equals("true")) {
            currentNode = test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(), LoginUser)).assignCategory(TestCategory.SMOKE);
            OperatorUserLogic.changeUserFirstTimePIN();
        } else {
            Log.info("PIN required : " + OperatorUserLogic.pinChangeRequired);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "Domain&CategoryProvider")
    public Object[][] DomainCategoryProvider() {

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        Object[][] categoryData = new Object[rowCount - 1][2];
        int j = 0;
        for (int i = 2; i <= rowCount; i++) {
            categoryData[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            j++;
        }

        return categoryData;
    }

    /* ------------------------------------------------------------------------------------------------- */
}