package com.testscripts.sit;

import com.Features.CommissionProfile;
import com.Features.ViewLoanProfile;
import com.classes.*;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.ProfileManagementSubCategories;
import com.pageobjects.networkadminpages.viewLoanProfile;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.testmanagement.core.TestManager;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.SIT_VIEW_LOAN)
public class SIT_view_loanProfile extends BaseTest {

    HashMap<String, String> LoanMap=new HashMap<>();
    String assignCategory="SIT";
    String loanprofile="AUTLP55899";
    String expectedresult=null;

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-077") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test01_viewLoanprofile(int rownum,String domainName, String categoryName) throws InterruptedException {
        final String methodName = "Test_viewLoanProfile";
        Log.startTestCase(methodName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVIEWLOANPROFILE1").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);

        ViewLoanProfile ViewLoanProfile = new ViewLoanProfile(driver);
    //  currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SIT_VIEWLOANPROFILE1").getExtentCase(), categoryName));
     //   currentNode.assignCategory(assignCategory);


        ViewLoanProfile.viewLoan(domainName, categoryName,loanprofile);
        String actualvalue=ViewLoanProfile.profilename();
         expectedresult=loanprofile;

        Assertion.assertEquals(actualvalue, expectedresult);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);




      //  String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
       /* CommissionProfile.writeCommissionProfileToExcel(rowNum, result);


        if (!_masterVO.getClientDetail("CLIENT_NAME").equalsIgnoreCase("VIETNAM") && result[2].equals(Message)) {
            if(_masterVO.getClientDetail("COMM_PROF_STATUS").equals("0"))
                CommissionProfile.CommissionProfileDefault(domainName, categoryName, result[1]);
        }




        */

    }



    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-077") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test02_viewLoanprofile(int rownum,String domainName, String categoryName) throws InterruptedException {
        final String methodName = "Test_viewLoanProfile";
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        NetworkAdminHomePage homePage;
        Login login;
        ProfileManagementSubCategories ProfileManagementSubCategories;
        viewLoanProfile viewLoanProfile;
        Map<String, String> userAccessMap = new HashMap<String, String>();
        SelectNetworkPage selectNetworkPage;
        String[] result;
        homePage = new NetworkAdminHomePage(driver);
        login = new Login();
        ProfileManagementSubCategories = new ProfileManagementSubCategories(driver);
        viewLoanProfile = new viewLoanProfile(driver);
        selectNetworkPage = new SelectNetworkPage(driver);
        Log.startTestCase(methodName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVIEWLOANPROFILE2").getExtentCase(), categoryName));
        currentNode.assignCategory(assignCategory);

        ViewLoanProfile ViewLoanProfile = new ViewLoanProfile(driver);


        userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        // User Access module ends.
        selectNetworkPage.selectNetwork();
        result = new String[3];
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        homePage.clickProfileManagement();
        ProfileManagementSubCategories.clickviewLoanprofile();
        viewLoanProfile.selectDomain(domainName);
        viewLoanProfile.selectCategory(categoryName);
        viewLoanProfile.clickviewButton();
        viewLoanProfile.clickview2Button();
        String actualMessage = viewLoanProfile.getErrorMessage();
        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.deleteprofile.error.selectprofileid");

        Assertion.assertEquals(actualMessage, expectedMessage);


        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-077") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test03_viewLoanprofile(int rownum,String domainName, String categoryName) throws InterruptedException {
        final String methodName = "Test_viewLoanProfile";
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        NetworkAdminHomePage homePage;
        Login login;
        ProfileManagementSubCategories ProfileManagementSubCategories;
        viewLoanProfile viewLoanProfile;
        Map<String, String> userAccessMap = new HashMap<String, String>();
        SelectNetworkPage selectNetworkPage;
        String[] result;
        homePage = new NetworkAdminHomePage(driver);
        login = new Login();
        ProfileManagementSubCategories = new ProfileManagementSubCategories(driver);
        viewLoanProfile = new viewLoanProfile(driver);
        selectNetworkPage = new SelectNetworkPage(driver);
        Log.startTestCase(methodName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVIEWLOANPROFILE3").getExtentCase(), categoryName));
        currentNode.assignCategory(assignCategory);

        ViewLoanProfile ViewLoanProfile = new ViewLoanProfile(driver);


        userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        // User Access module ends.
        selectNetworkPage.selectNetwork();
        result = new String[3];
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        homePage.clickProfileManagement();
        ProfileManagementSubCategories.clickviewLoanprofile();
        viewLoanProfile.selectDomain(domainName);
        viewLoanProfile.selectCategory(categoryName);
        viewLoanProfile.clickviewButton();
        viewLoanProfile.clickbackButton1();

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-077") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test04_viewLoanprofile(int rownum,String domainName, String categoryName) throws InterruptedException {
        final String methodName = "Test_viewLoanProfile";
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        NetworkAdminHomePage homePage;
        Login login;
        ProfileManagementSubCategories ProfileManagementSubCategories;
        viewLoanProfile viewLoanProfile;
        Map<String, String> userAccessMap = new HashMap<String, String>();
        SelectNetworkPage selectNetworkPage;
        String[] result;
        homePage = new NetworkAdminHomePage(driver);
        login = new Login();
        ProfileManagementSubCategories = new ProfileManagementSubCategories(driver);
        viewLoanProfile = new viewLoanProfile(driver);
        selectNetworkPage = new SelectNetworkPage(driver);
        Log.startTestCase(methodName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVIEWLOANPROFILE4").getExtentCase(), categoryName));
        currentNode.assignCategory(assignCategory);

        ViewLoanProfile ViewLoanProfile = new ViewLoanProfile(driver);


        userAccessMap = UserAccess.getUserWithAccess(RolesI.COMMISSION_PROFILE_ROLECODE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        // User Access module ends.
        selectNetworkPage.selectNetwork();
        result = new String[3];
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        homePage.clickProfileManagement();
        ProfileManagementSubCategories.clickviewLoanprofile();
        viewLoanProfile.selectDomain(domainName);
        viewLoanProfile.selectCategory(categoryName);
        viewLoanProfile.clickviewButton();

        driver.findElement(By.xpath("//td[contains(text(),'"+loanprofile+"')]/../td[3]/input")).click();
        viewLoanProfile.clickview2Button();
        viewLoanProfile.clickbackButton2();


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
        Object[][] categoryData = new Object[rowCount][3];
        for (int i = 1, j = 0; i <= rowCount; i++, j++) {
            categoryData[j][0] = i;
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);


        }
        return categoryData;
    }

    /* ------------------------------------------------------------------------------------------------- */
}



