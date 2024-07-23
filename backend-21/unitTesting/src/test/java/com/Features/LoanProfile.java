package com.Features;

import com.aventstack.extentreports.Status;
import com.classes.*;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.pageobjects.networkadminpages.LoanProfilePage.AddLoanProfilePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.*;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class LoanProfile extends BaseTest {

    public WebDriver driver;

    NetworkAdminHomePage homePage;
    Login login;
    RandomGeneration RandomGenerator;
    AddLoanProfilePage AddLoanProfilePage;
    String selectedNetwork;
    SelectNetworkPage networkPage;
    Map<String, String> userAccessMap = new HashMap<String, String>();
    Map<String, String> ResultMap;
    String MasterSheetPath = _masterVO.getProperty("DataProvider");

    public LoanProfile(WebDriver driver) {
        this.driver = driver;
        homePage = new NetworkAdminHomePage(driver);
        login = new Login();
        networkPage = new SelectNetworkPage(driver);
        RandomGenerator = new RandomGeneration();
        AddLoanProfilePage = new AddLoanProfilePage(driver);
        selectedNetwork = _masterVO.getMasterValue("Network Code");
        ResultMap = new HashMap();
    }


    public Map<String, String> addLoanProfileHourly(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileHourly";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();
        String actualMessage;
        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");
        String LPPCTRate1 = _masterVO.getProperty("LPPCTRate1");
        String LPAMTRate2 = _masterVO.getProperty("LPAMTRate2");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1(LPPCTRate1);
        AddLoanProfilePage.enterPremiumRate2(LPAMTRate2);

        AddLoanProfilePage.clickSaveButton();
        AddLoanProfilePage.clickConfirmButton();

        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);


        Log.methodExit(methodname);
        return ResultMap;
    }


    public void writeLoanProfileToExcel(int rowNum, String profileName) {
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        ExcelUtility.setCellData(0, ExcelI.LOAN_PROFILE, rowNum, profileName);
    }


    public Map<String, String> addLoanProfileDaily(java.lang.String domainName, java.lang.String categoryName, java.lang.String grade, java.lang.String profileType, java.lang.String productCode) throws InterruptedException {
        final java.lang.String methodname = "addLoanProfileDaily";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();
        java.lang.String actualMessage;
        java.lang.String fromRange1 = _masterVO.getProperty("FromRange1");
        java.lang.String toRange1 = _masterVO.getProperty("ToRange1");
        java.lang.String fromRange2 = _masterVO.getProperty("FromRange2");
        java.lang.String toRange2 = _masterVO.getProperty("ToRange2");
        java.lang.String LPPCT = _masterVO.getProperty("LPPCT");
        java.lang.String LPAMT = _masterVO.getProperty("LPAMT");
        String LPPCTRate1 = _masterVO.getProperty("LPPCTRate1");
        String LPAMTRate2 = _masterVO.getProperty("LPAMTRate2");

        java.lang.String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1(LPPCTRate1);
        AddLoanProfilePage.enterPremiumRate2(LPAMTRate2);

        AddLoanProfilePage.clickSaveButton();
        AddLoanProfilePage.clickConfirmButton();

        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);


        Log.methodExit(methodname);
        return ResultMap;
    }


    public Map<String, String> addLoanProfilePCT(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfilePCT";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String actualMessage;
        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPPCTRate1 = _masterVO.getProperty("LPPCTRate1");
        String LPPCTRate2 = _masterVO.getProperty("LPPCTRate2");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPPCT);
        AddLoanProfilePage.enterPremiumRate1(LPPCTRate1);
        AddLoanProfilePage.enterPremiumRate2(LPPCTRate2);

        AddLoanProfilePage.clickSaveButton();
        AddLoanProfilePage.clickConfirmButton();

        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);


        Log.methodExit(methodname);
        return ResultMap;
    }


    public Map<String, String> addLoanProfileAMT(java.lang.String domainName, java.lang.String categoryName, java.lang.String grade, java.lang.String profileType, java.lang.String productCode) throws InterruptedException {
        final java.lang.String methodname = "addLoanProfileAMT";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String actualMessage;
        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPAMT = _masterVO.getProperty("LPAMT");
        String LPAMTRate1 = _masterVO.getProperty("LPAMTRate1");
        String LPAMTRate2 = _masterVO.getProperty("LPAMTRate2");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPAMT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1(LPAMTRate1);
        AddLoanProfilePage.enterPremiumRate2(LPAMTRate2);

        AddLoanProfilePage.clickSaveButton();
        AddLoanProfilePage.clickConfirmButton();

        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);

        Log.methodExit(methodname);
        return ResultMap;
    }

    public String modifyLoanProfile(String domainName, String categoryName, String grade, String profileType, String productCode, String LPName) throws InterruptedException {
        final String methodname = "modifyLoanProfile";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String actualMessage;
        String LPAMT = _masterVO.getProperty("LPAMT");

        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickModifyButton();

        AddLoanProfilePage.selectProfile(LPName);
        AddLoanProfilePage.clickModifyButton();

        AddLoanProfilePage.selectPremiumType1(LPAMT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.modifyPremiumRate1("5");
        AddLoanProfilePage.modifyPremiumRate2("20");

        AddLoanProfilePage.clickSaveButton();
        AddLoanProfilePage.clickConfirmButton();

        actualMessage = AddLoanProfilePage.getActualMsg();

        Log.methodExit(methodname);
        return actualMessage;
    }


    public String deleteLoanProfile(String domainName, String categoryName, String grade, String profileType, String productCode, String LPName) throws InterruptedException {
        final String methodname = "deleteLoanProfile";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String actualMessage;

        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickModifyButton();

        AddLoanProfilePage.selectProfile(LPName);
        AddLoanProfilePage.clickDeleteButton();

        driver.switchTo().alert().accept();

        actualMessage = AddLoanProfilePage.getActualMsg();

        Log.methodExit(methodname);
        return actualMessage;
    }

    public Map<String, String> addLoanProfileWithoutDomain(String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodname = "addLoanProfileWithoutDomain";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();
        String actualMessage;

        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.clickAddButton();

        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);


        Log.methodExit(methodname);
        return ResultMap;
    }

    public Map<String, String> addLoanProfileWithoutProductCode(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileDaily";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();
        String actualMessage;

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);

        Boolean checkFromRange1 = AddLoanProfilePage.checkFromRange1();
        Boolean checkToRange1 = AddLoanProfilePage.checkToRange1();

        Boolean checkFromRange2 = AddLoanProfilePage.checkFromRange2();
        Boolean checkToRange2 = AddLoanProfilePage.checkToRange2();

        Boolean checkRanges = checkFromRange1 && checkToRange1 && checkFromRange2 && checkToRange2;

        Boolean checkPremiumType1 = AddLoanProfilePage.checkPremiumType1();
        Boolean checkPremiumType2 = AddLoanProfilePage.checkPremiumType2();

        Boolean checkPremiumRate1 = AddLoanProfilePage.checkPremiumRate1();
        Boolean checkPremiumRate2 = AddLoanProfilePage.checkPremiumRate2();

        Boolean checkPremium = checkPremiumType1 && checkPremiumType2 && checkPremiumRate1 && checkPremiumRate2;

        if(checkPremium && checkRanges)
        {
            AddLoanProfilePage.clickSaveButton();
        }
        else {
            currentNode.log(Status.FAIL, "Elements are not disabled without clicking on the product code.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);

        Log.methodExit(methodname);
        return ResultMap;
    }


    public Boolean addLoanProfileBackButton(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileBackButton";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1("2");
        AddLoanProfilePage.enterPremiumRate2("50");

        AddLoanProfilePage.clickBackButton();

        Boolean checkDomain = AddLoanProfilePage.checkDomain();

        Log.methodExit(methodname);
        return checkDomain;
    }


    public Map<String, String> addLoanProfileFromRangeMoreThanToRange(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileFromRangeMoreThanToRange";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String fromRange1 = "100";
        String toRange1 = "50";
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");
        String LPPCTRate1 = _masterVO.getProperty("LPPCTRate1");
        String LPAMTRate2 = _masterVO.getProperty("LPAMTRate2");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1(LPPCTRate1);
        AddLoanProfilePage.enterPremiumRate2(LPAMTRate2);

        AddLoanProfilePage.clickSaveButton();

        String actualMessage = AddLoanProfilePage.getActualMsg();

        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);

        Log.methodExit(methodname);
        return ResultMap;
    }


    public Map<String, String> addLoanProfileSlab2MoreThanSlab1(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileSlab2MoreThanSlab1";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String fromRange1 = "200";
        String toRange1 = "300";
        String fromRange2 = "100";
        String toRange2 = "199";
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1("2");
        AddLoanProfilePage.enterPremiumRate2("50");

        AddLoanProfilePage.clickSaveButton();

        String actualMessage = AddLoanProfilePage.getActualMsg();

        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);

        Log.methodExit(methodname);
        return ResultMap;
    }


    public Map<String, String> addLoanProfileAlphanumericFromRange(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileAlphanumericFromRange";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String fromRange1 = RandomGenerator.randomAlphaNumeric(4);
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1("2");
        AddLoanProfilePage.enterPremiumRate2("50");

        AddLoanProfilePage.clickSaveButton();

        String actualMessage = AddLoanProfilePage.getActualMsg();

        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);

        Log.methodExit(methodname);
        return ResultMap;
    }




    public Map<String, String> addLoanProfileAlphanumericToRange(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileAlphanumericToRange";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = RandomGenerator.randomAlphaNumeric(4);
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1("2");
        AddLoanProfilePage.enterPremiumRate2("50");

        AddLoanProfilePage.clickSaveButton();

        String actualMessage = AddLoanProfilePage.getActualMsg();

        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);

        Log.methodExit(methodname);
        return ResultMap;
    }


    public Map<String, String> addLoanProfileDecimalFromRange(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileDecimalFromRange";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String fromRange1 = "1.5";
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1("2");
        AddLoanProfilePage.enterPremiumRate2("50");

        AddLoanProfilePage.clickSaveButton();

        String actualMessage = AddLoanProfilePage.getActualMsg();

        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);

        Log.methodExit(methodname);
        return ResultMap;
    }



    public Map<String, String> addLoanProfileDecimalToRange(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileDecimalToRange";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();

        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = "100.1";
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");
        String LPAMT = _masterVO.getProperty("LPAMT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1("2");
        AddLoanProfilePage.enterPremiumRate2("50");

        AddLoanProfilePage.clickSaveButton();

        String actualMessage = AddLoanProfilePage.getActualMsg();

        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);

        Log.methodExit(methodname);
        return ResultMap;
    }


    public Map<String, String> addLoanProfileDecimalPremiumPCT(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileDecimalPremiumPCT";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();
        String actualMessage;
        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPPCT = _masterVO.getProperty("LPPCT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPPCT);
        AddLoanProfilePage.selectPremiumType2(LPPCT);
        AddLoanProfilePage.enterPremiumRate1("1.5");
        AddLoanProfilePage.enterPremiumRate2("2.5");

        AddLoanProfilePage.clickSaveButton();
        AddLoanProfilePage.clickConfirmButton();

        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);


        Log.methodExit(methodname);
        return ResultMap;
    }



    public Map<String, String> addLoanProfileDecimalPremiumAMT(String domainName, String categoryName, String grade, String profileType, String productCode) throws InterruptedException {
        final String methodname = "addLoanProfileDecimalPremiumAMT";
        Log.methodEntry(methodname, domainName, categoryName, grade);

        userAccessMap = UserAccess.getUserWithAccess(RolesI.LOAN_PROFILE);
        login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
        networkPage.selectNetwork();
        String actualMessage;
        String fromRange1 = _masterVO.getProperty("FromRange1");
        String toRange1 = _masterVO.getProperty("ToRange1");
        String fromRange2 = _masterVO.getProperty("FromRange2");
        String toRange2 = _masterVO.getProperty("ToRange2");
        String LPAMT = _masterVO.getProperty("LPAMT");

        String profileName = UniqueChecker.UC_LPName();
        homePage.clickProfileManagement();
        homePage.clickLoanProfile();

        AddLoanProfilePage.selectDomain(domainName);
        AddLoanProfilePage.selectCategory(categoryName);
        AddLoanProfilePage.clickAddButton();

        AddLoanProfilePage.enterProfileName(profileName);
        AddLoanProfilePage.selectProfileType(profileType);
        AddLoanProfilePage.clickProduct(productCode);

        AddLoanProfilePage.enterFromRange1(fromRange1);
        AddLoanProfilePage.enterToRange1(toRange1);
        AddLoanProfilePage.enterFromRange2(fromRange2);
        AddLoanProfilePage.enterToRange2(toRange2);
        AddLoanProfilePage.selectPremiumType1(LPAMT);
        AddLoanProfilePage.selectPremiumType2(LPAMT);
        AddLoanProfilePage.enterPremiumRate1("50.5");
        AddLoanProfilePage.enterPremiumRate2("100.5");

        AddLoanProfilePage.clickSaveButton();
        AddLoanProfilePage.clickConfirmButton();

        actualMessage = AddLoanProfilePage.getActualMsg();
        ResultMap.put("INITIATE_MESSAGE", actualMessage);
        ResultMap.put("PROFILE_NAME",profileName);


        Log.methodExit(methodname);
        return ResultMap;
    }






}
