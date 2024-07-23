package com.testscripts.uap;

import com.Features.CreditLoanAmount;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.*;
import com.utils.constants.Module;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@ModuleManager(name = Module.UAP_CREDIT_LOAN_AMOUNT)
public class UAP_CreditLoanC2C extends BaseTest {

    String assignCategory="UAP";
    HashMap<String, String> c2cMap;
    Login login = new Login();
    Integer inc;
    HashMap<String, String> o2cMap;


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC01_LoanAmountCreditC2CT(String categoryName, String ToCategory, String toMSISDN, String FromPIN, String domainName, String ParentCategory, String geoType, int RowNum) throws InterruptedException, IOException, SQLException, ParseException {
        final String methodName = "TC01_LoanAmountCreditC2CT";
        Log.startTestCase(methodName, domainName, categoryName, ToCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT1").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", categoryName);
        Log.info("LOGINID : " + loginID);

        inc = ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, categoryName);

        Log.info("Incrementer Value : " + inc);

        String MSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, inc);
        Log.info("MSISDN : " + MSISDN);


        Integer loanAmount = Integer.valueOf(_masterVO.getProperty("LOANAMT"));

        String balance = DBHandler.AccessHandler.getUserBalance("ETOPUP", loginID);
        Log.info(balance);
        int usrBalance = (Integer.parseInt(balance)) / 100;
        int threshold = usrBalance - 200;

        CreditLoanAmount creditLoanAmount = new CreditLoanAmount(driver);
        creditLoanAmount.createLoanThresholdData(MSISDN, "ETOPUP", loanAmount, threshold, categoryName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT2").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);
        creditLoanAmount.findPathFromConstant();

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT3").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);
        creditLoanAmount.UserLoanDataUploadScriptExecution();

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT4").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);
        c2cMap = creditLoanAmount.creditLoanAmountC2CT(domainName, categoryName, MSISDN, threshold, loanAmount, loginID, "ETOPUP", inc, ToCategory, FromPIN, toMSISDN);

        if (c2cMap.get("Loan Given").equals("Y")) {
            Log.info("Loan Amount is credited to the user..");
            if ((c2cMap.get("Actual Balance").equals(c2cMap.get("Expected Balance"))))
                Assertion.assertPass("Actual amount is equal to the expected amount, hence loan amount is credited successfully.");
            else
                Assertion.assertFail("Actual amount is not equal to the expected amount, hence loan amount is not credited successfully.");

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT5").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);

        o2cMap = creditLoanAmount.loanAmountSettlementO2C(domainName, categoryName, MSISDN, threshold, loanAmount, loginID, "ETOPUP", inc, ToCategory, FromPIN, toMSISDN);

        if ((o2cMap.get("Loan Given").equals("N")))
           Assertion.assertPass("Actual amount is equal to the expected amount, hence loan amount is settled successfully.");
        else
           Assertion.assertFail("Actual amount is not equal to the expected amount, hence loan amount is not settled successfully.");
        } else {
           Assertion.assertFail("Loan is not credited to the user of category: " + categoryName);
        }
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC02_LoanAmountCreditC2CTWithoutScript(String categoryName, String ToCategory, String toMSISDN, String FromPIN, String domainName, String ParentCategory, String geoType, int RowNum) throws InterruptedException, IOException, SQLException, ParseException {
        final String methodName = "TC02_LoanAmountCreditC2CTWithoutScript";
        Log.startTestCase(methodName, domainName, categoryName, ToCategory);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        String loginID = login.getUserLoginID(driver, "ChannelUser", categoryName);
        Log.info("LOGINID : "+loginID);
        inc = ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, categoryName);

        Log.info("Incrementer Value : " +inc);

        String MSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, inc);
        Log.info("MSISDN : "+MSISDN);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT1").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);

        Integer loanAmount = Integer.valueOf(_masterVO.getProperty("LOANAMT"));

        String balance = DBHandler.AccessHandler.getUserBalance("ETOPUP", loginID);
        Log.info(balance);
        int usrBalance = (Integer.parseInt(balance)) / 100;
        int threshold = usrBalance - 200;

        CreditLoanAmount creditLoanAmount = new CreditLoanAmount(driver);
        creditLoanAmount.createLoanThresholdData(MSISDN, "ETOPUP", loanAmount, threshold, categoryName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT2").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);
        creditLoanAmount.findPathFromConstant();

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPCRDTLNAMT6").getExtentCase(), domainName, categoryName));
        currentNode.assignCategory(assignCategory);
        c2cMap = creditLoanAmount.creditLoanAmountC2CT(domainName, categoryName, MSISDN, threshold, loanAmount, loginID, "ETOPUP", inc, ToCategory, FromPIN, toMSISDN);

        if(c2cMap.get("Loan Given").equals("N")) {
            Assertion.assertPass("Loan is not credited to the user without script execution of category : " +categoryName);
        }else{
            Assertion.assertPass("Loan is credited to the user without script execution of category : " +categoryName);
        }
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }





    /* ------------------------------------  D   A   T   A    P   R   O   V   I   D   E   R ---------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed1() {
        String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        /*
         * Array list to store Categories for which C2C withdraw is allowed
         */
        ArrayList<String> alist1 = new ArrayList<String>();
        ArrayList<String> alist2 = new ArrayList<String>();
        ArrayList<String> categorySize = new ArrayList<String>();
        ArrayList<String> transfer_rule_type = new ArrayList<String>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(C2CTransferCode) || aList.contains(C2CTransferCode + "[P]") ||
                    aList.contains(C2CTransferCode + "[S]") || aList.contains(C2CTransferCode + "[O]") ||
                    aList.contains(C2CTransferCode + "[D]")) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
                alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
                if (ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i).equals(""))
                    transfer_rule_type.add("D");
                else
                    transfer_rule_type.add(ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i));
            }
        }

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

        /*
         * Calculate the Count of Users for each category
         */
        int totalObjectCounter = 0;
        for (int i = 0; i < alist1.size(); i++) {
            int categorySizeCounter = 0;
            for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(i))) {
                    categorySizeCounter++;
                }
            }
            categorySize.add("" + categorySizeCounter);
            totalObjectCounter = totalObjectCounter + categorySizeCounter;
        }

        /*
         * Counter to count number of users exists in channel users hierarchy sheet
         * of Categories for which C2C Withdraw is allowed
         */

        Object[][] Data = new Object[totalObjectCounter][8];

        for (int j = 0, k = 0; j < alist1.size(); j++) {

            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int excelRowSize = ExcelUtility.getRowCount();
            String ChannelUserPIN = null;
            for (int i = 1; i <= excelRowSize; i++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
                    ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                    break;
                }
            }

            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
                    Data[k][0] = alist2.get(j);
                    Data[k][1] = alist1.get(j);
                    Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                    Data[k][3] = ChannelUserPIN;
                    Data[k][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, excelCounter);
                    Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
                    Data[k][6] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, excelCounter);
                    Data[k][7] = excelCounter;
                    k++;
                }
            }

        }

        /*
         * Clean data on the basis of transfer rules
         */
        String trfUserLevelAlllow = DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRF_RULE_USER_LEVEL_ALLOW);
        if (trfUserLevelAlllow.equalsIgnoreCase("FALSE")) {
            int q = 0;
            ArrayList<Integer> removeData = new ArrayList<Integer>();
            for (int i = 0; i < alist1.size(); i++) {
                if (transfer_rule_type.get(i).equals("P")) {
                    Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));

                    for (int p = 0; p < Data.length; p++) {
                        if (Data[p][0].equals(alist2.get(i)) && Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
                            Log.info("Data to be removed:[" + p + "]");
                            q++;
                            removeData.add(p);
                        }
                    }
                }

                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
                int excelRowSize = ExcelUtility.getRowCount();

                if (transfer_rule_type.get(i).equals("O")) {
                    Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));
                    for (int p = 0; p < Data.length; p++) {
                        int k;
                        for (k = 1; k <= excelRowSize; k++) {
                            if (ExcelUtility.getCellData(0, ExcelI.SEQUENCE_NO, k).equals("1") && ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, k).equals(Data[p][4])) {
                                break;
                            }
                        }

                        if ((Data[p][0].equals(alist2.get(i)) || Data[p][0].equals(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, k)))
                                && Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
                            Log.info("Data to be removed:[" + p + "]");
                            q++;
                            removeData.add(p);
                        }
                    }
                }
            }

            int newObj = Data.length - q;
            Object[][] Data1 = new Object[newObj][8];
            for (int l = 0, m = 0; l < Data.length; l++) {
                if (!removeData.contains(l)) {
                    for (int x = 0; x < 8; x++) {
                        Data1[m][x] = Data[l][x];
                    }
                    Log.info(Data1);
                    m++;
                }
            }


            return Data1;
        } else {
            return Data;
        }
    }

    /* ----------------------------------------------------------------------------------------------- */
}
