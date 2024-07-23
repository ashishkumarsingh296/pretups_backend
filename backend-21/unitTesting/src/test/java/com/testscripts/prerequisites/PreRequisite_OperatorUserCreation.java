package com.testscripts.prerequisites;


import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

/*
 * @author PVG
 * This class is created to add Operator Users
 */
@ModuleManager(name = Module.PREREQUISITE_OPERATOR_USER_CREATION)
public class PreRequisite_OperatorUserCreation extends BaseTest {

    @Test(dataProvider = "Domain&CategoryProvider")
    @TestManager(TestKey = "PRETUPS-413") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_CreateOperatorUsers(int RowNum, String ParentUser, String LoginUser) throws InterruptedException {
        final String methodName = "Test_CreateOperatorUsers";
        Log.startTestCase(methodName, RowNum, ParentUser, LoginUser);

        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("POPTCREATION1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("POPTCREATION2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("POPTCREATION3");

        // Test Case - To Create Operator Users as per the Operator Users Hierarchy Sheet
        currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), ParentUser, LoginUser)).assignCategory(TestCategory.PREREQUISITE);
        OperatorUser OperatorUserLogic = new OperatorUser(driver);
        HashMap<String, String> operatorDetails = OperatorUserLogic.operatorUserInitiate(ParentUser, LoginUser);
        OperatorUserLogic.approveUser(ParentUser);
        OperatorUserLogic.writeOperatorUserData(RowNum);

        currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), LoginUser)).assignCategory(TestCategory.PREREQUISITE);
        OperatorUserLogic.changeUserFirstTimePassword();
        
        String actual;
		try{actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		if(actual==null){actual="";}
		}catch(Exception e){actual="No message found on screen";}
		
        String expected = MessagesDAO.getLabelByKey("login.changeCommonLoginPassword.updatesuccessmessage");

        if (actual.equals(expected))
            OperatorUserLogic.writeOperatorUserData(RowNum);
        else {
            String password = DBHandler.AccessHandler.fetchUserPassword(operatorDetails.get("LOGINID"));
            boolean isPasswordChanged = Assertion.assertEquals(password, _masterVO.getProperty("NewPassword"));

            if (isPasswordChanged) {
                OperatorUserLogic.writeOperatorUserData(RowNum);
                currentNode.log(Status.PASS, MarkupHelper.createLabel("Password changed successfully but no message appeared on application.", ExtentColor.GREEN));
            } else {
                ExtentI.insertValueInDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, RowNum, _masterVO.getProperty("Password"));
            }
        }

        if (OperatorUserLogic.pinChangeRequired.equalsIgnoreCase(PretupsI.TRUE)) {
            currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), LoginUser)).assignCategory(TestCategory.PREREQUISITE);
            HashMap<String, String> operatorMap = OperatorUserLogic.changeUserFirstTimePIN();
            String intChnlChangePINMsg = MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess");
            boolean isPinChanged = Assertion.assertEquals(operatorMap.get("changePINMsg"), intChnlChangePINMsg);

            if (isPinChanged) {
                OperatorUserLogic.writeOperatorUserData(RowNum);
            } else {
                ExtentI.insertValueInDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PIN, RowNum, _masterVO.getProperty("PIN"));
            }
        } else
            Log.info("Pin Change is not required.");

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
        int counter = 0;

        for (int i = 2; i <= rowCount; i++) {
            String ParentCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, i);
            String CategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
            if (!(ParentCode + CategoryCode).equals(PretupsI.SUPERADMIN_CATCODE + PretupsI.NETWORKADMIN_CATCODE)) {
                counter++;
            }
        }
        Object[][] categoryData = new Object[counter][3];
        int j = 0;
        for (int i = 2; i <= rowCount; i++) {
            String ParentCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, i);
            String CategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
            if (!(ParentCode + CategoryCode).equals(PretupsI.SUPERADMIN_CATCODE + PretupsI.NETWORKADMIN_CATCODE)) {
                categoryData[j][0] = i;
                categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
                categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                j++;
            }
        }
        return categoryData;
    }

    /* ------------------------------------------------------------------------------------------------ */
}