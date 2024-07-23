package com.testscripts.smoke;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CWithdraw;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
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
import com.commons.PretupsI;

@ModuleManager(name = Module.SMOKE_O2C_WITHDRAW)
public class Smoke_O2CWithdraw extends BaseTest {

    @Test(dataProvider = "Data")
    @TestManager(TestKey = "PRETUPS-430") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_O2CWithdrawal(String parentCategory, String Category, String MSISDN, String ProductType, String ProductCode, String productName) throws ParseException, SQLException {
        final String methodName = "Test_O2CWithdrawal";
        Log.startTestCase(methodName, parentCategory, Category, MSISDN, ProductType, ProductCode);

        O2CWithdraw o2cWithdraw = new O2CWithdraw(driver);
        businessController businessController = new businessController(_masterVO.getProperty("O2CWithdrawCode"), MSISDN, null);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SO2CWITHDRAWAL1").getExtentCase(), Category, ProductType)).assignCategory(TestCategory.SMOKE);
        TransactionVO TransactionVO = businessController.preparePreTransactionVO();
        TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
        HashMap<String, String> TrasactionMap = o2cWithdraw.o2cWithdraw2qty(MSISDN, ProductType,productName);
        String expected = MessagesDAO.prepareMessageByKey("userreturn.withdraw.msg.success", TrasactionMap.get("TransactionID"));
        Assertion.assertEquals(TrasactionMap.get("Message"), expected);

        // Test Case to validate Network Stocks after successful O2C Withdraw
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CWITHDRAWAL2").getExtentCase()).assignCategory(TestCategory.SMOKE);
        HashMap<String, String> initiatedQty = new HashMap<String, String>();
        initiatedQty.put(ProductCode, TrasactionMap.get("InitiatedQty"));
        TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
        BusinessValidator.validateStocks(TransactionVO);

        // Test Case to validate Channel User balance after successful O2C Withdraw
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CWITHDRAWAL3").getExtentCase()).assignCategory(TestCategory.SMOKE);
        BusinessValidator.validateUserBalances(TransactionVO);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

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
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
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
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                j++;
            }
        }

        /*
         * Store products from Product Sheet to Object.
         */
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int prodRowCount = ExcelUtility.getRowCount();
        Object[][] ProductObject = new Object[prodRowCount][3];
        for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
            ProductObject[i][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
            ProductObject[i][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, j);
            ProductObject[i][2] = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, j);
        }

        /*
         * Creating combination of channel users for each product.
         */
        int countTotal = ProductObject.length * userCounter;
        Object[][] o2cData = new Object[countTotal][6];
        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
            o2cData[j][0] = Data[k][0];
            o2cData[j][1] = Data[k][1];
            o2cData[j][2] = Data[k][2];
            o2cData[j][3] = ProductObject[i][0];
            o2cData[j][4] = ProductObject[i][1];
            o2cData[j][5] = ProductObject[i][2];
            
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

    /* ------------------------------------------------------------------------------------------------- */
}