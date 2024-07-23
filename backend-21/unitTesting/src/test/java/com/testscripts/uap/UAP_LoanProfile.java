package com.testscripts.uap;

import com.Features.LoanProfile;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.*;
import com.utils.constants.Module;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;


@ModuleManager(name = Module.UAP_LOAN_PROFILE)
public class UAP_LoanProfile extends BaseTest {

    String assignCategory="UAP";
    String loanProfile;

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC01_LoanProfileCreationHourly(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException, IOException {
        final String methodName = "TC01_LoanProfileCreationHourly";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE1").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeHourly");
        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
            LoanProfile addLoanProfile = new LoanProfile(driver);
            Map<String, String> map = addLoanProfile.addLoanProfileHourly(domainName, categoryName, grade, profileType, productCode);
            loanProfile = map.get("PROFILE_NAME");
            Log.info("The Created Loan profile name is : " + loanProfile);

            currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPLOANPROFILE2").getExtentCase());
            currentNode.assignCategory(assignCategory);
            String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
            addLoanProfile.writeLoanProfileToExcel(rowNum, loanProfile);
            Assertion.assertEquals(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC02_LoanProfileCreationDaily(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC02_LoanProfileCreationDaily";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE3").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");
        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileDaily(domainName, categoryName, grade, profileType, productCode);
        loanProfile = map.get("PROFILE_NAME");
        Log.info("The Created Loan profile name is : " + loanProfile);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPLOANPROFILE2").getExtentCase());
        currentNode.assignCategory(assignCategory);
        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
        Assertion.assertEquals(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC03_LoanProfileCreationPCT(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC03_LoanProfileCreationPCT";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE4").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfilePCT(domainName, categoryName, grade, profileType, productCode);
        loanProfile = map.get("PROFILE_NAME");
        Log.info("The Created Loan profile name is : " + loanProfile);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPLOANPROFILE2").getExtentCase());
        currentNode.assignCategory(assignCategory);
        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
        Assertion.assertEquals(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC04_LoanProfileCreationAMT(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC04_LoanProfileCreationAMT";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE5").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeHourly");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileAMT(domainName, categoryName, grade, profileType, productCode);
        loanProfile = map.get("PROFILE_NAME");
        Log.info("The Created Loan profile name is : " + loanProfile);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPLOANPROFILE2").getExtentCase());
        currentNode.assignCategory(assignCategory);
        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
        Assertion.assertEquals(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC05_LoanProfileCreationModify(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC05_LoanProfileCreationModify";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);
        String actualMessage;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE6").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeHourly");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileAMT(domainName, categoryName, grade, profileType, productCode);
        loanProfile = map.get("PROFILE_NAME");
        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
        if(map.get("INITIATE_MESSAGE").equals(expectedMessage)) {
            actualMessage = addLoanProfile.modifyLoanProfile(domainName, categoryName, grade, profileType, productCode, loanProfile);
            currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPLOANPROFILE2").getExtentCase());
            currentNode.assignCategory(assignCategory);
            String expectedMessage1 = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.modsuccess", "");
            Assertion.assertEquals(actualMessage, expectedMessage1);
        }
        else
            Assertion.assertFail("Loan Profile is not created successfully, hence case is failed.");
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC06_LoanProfileCreationDelete(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC06_LoanProfileCreationDelete";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);
        String actualMessage;

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE7").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeHourly");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileAMT(domainName, categoryName, grade, profileType, productCode);
        loanProfile = map.get("PROFILE_NAME");
        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
        if(map.get("INITIATE_MESSAGE").equals(expectedMessage)) {
            actualMessage = addLoanProfile.deleteLoanProfile(domainName, categoryName, grade, profileType, productCode, loanProfile);
            currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPLOANPROFILE2").getExtentCase());
            currentNode.assignCategory(assignCategory);
            String expectedMessage1 = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.deletesuccess", "");
            Assertion.assertEquals(actualMessage, expectedMessage1);
        }
        else
            Assertion.assertFail("Loan Profile is not created successfully, hence case is failed.");
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC07_LoanProfileCreationWithoutDomain(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC07_LoanProfileCreationWithoutDomain";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE8").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
            LoanProfile addLoanProfile = new LoanProfile(driver);
            Map<String, String> map = addLoanProfile.addLoanProfileWithoutDomain(domainName, categoryName, grade);

            String expectedMessage = MessagesDAO.prepareMessageByKey("userdefaultconfiguration.upload.error.domain.empty", "") + ".";
            Assertion.assertEqualsIgnoreCase(expectedMessage, map.get("INITIATE_MESSAGE"));
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC08_LoanProfileCreationWithoutClickingProduct(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC08_LoanProfileCreationWithoutClickingProduct";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE9").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
            LoanProfile addLoanProfile = new LoanProfile(driver);
            Map<String, String> map = addLoanProfile.addLoanProfileWithoutProductCode(domainName, categoryName, grade, profileType, productCode);

            String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.error.slaboneempty", "");
            Assertion.assertEqualsIgnoreCase(expectedMessage, map.get("INITIATE_MESSAGE"));
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC09_LoanProfileCreationBackButton(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC09_LoanProfileCreationBackButton";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE10").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Boolean checkDomain = addLoanProfile.addLoanProfileBackButton(domainName, categoryName, grade, profileType, productCode);

        if(checkDomain) {
            ExtentI.Markup(ExtentColor.GREEN, "Domain DropDown is displayed, hence back button is working.");
            ExtentI.attachCatalinaLogsForSuccess();
        }else{
            currentNode.log(Status.FAIL, "Domain DropDown is not displayed, hence back button is not working.");
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC10_LoanProfileCreationFromRangeMoreThanToRange(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC10_LoanProfileCreationFromRangeMoreThanToRange";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE11").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileFromRangeMoreThanToRange(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.invalidendrange", "1");

        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC11_LoanProfileCreationSlab2MoreThanSlab1(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC11_LoanProfileCreationSlab2MoreThanSlab1";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE12").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileSlab2MoreThanSlab1(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.invalidcommissionslab", "From Range", "2", "To Range", "1");
        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC12_LoanProfileCreationAlphanumericFromRange(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC12_LoanProfileCreationAlphanumericFromRange";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE13").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileAlphanumericFromRange(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.numericone", "From Range", "1");
        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC13_LoanProfileCreationAlphanumericToRange(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC13_LoanProfileCreationAlphanumericToRange";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE14").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileAlphanumericToRange(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.numericone", "To Range", "1");
        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC14_LoanProfileCreationDecimalFromRange(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC14_LoanProfileCreationDecimalFromRange";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE15").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileDecimalFromRange(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.numericone", "From Range", "1");
        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC15_LoanProfileCreationDecimalToRange(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC15_LoanProfileCreationDecimalToRange";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE16").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileDecimalToRange(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.numericone", "To Range", "1");
        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC16_LoanProfileCreationDecimalPremiumPCT(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC16_LoanProfileCreationDecimalPremiumPCT";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE17").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileDecimalPremiumPCT(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC17_LoanProfileCreationDecimalPremiumAMT(int rowNum, String domainName, String categoryName, String grade, String productCode)  throws InterruptedException {
        final String methodName = "TC17_LoanProfileCreationDecimalPremiumAMT";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade, productCode);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPLOANPROFILE18").getExtentCase(),domainName,categoryName));
        currentNode.assignCategory(assignCategory);
        String profileType = _masterVO.getProperty("ProfileTypeDaily");

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(categoryName);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        LoanProfile addLoanProfile = new LoanProfile(driver);
        Map<String, String> map = addLoanProfile.addLoanProfileDecimalPremiumAMT(domainName, categoryName, grade, profileType, productCode);

        String expectedMessage = MessagesDAO.prepareMessageByKey("profile.loanprofile.addprofile.message.addsuccess", "");
        Assertion.assertEqualsIgnoreCase(map.get("INITIATE_MESSAGE"), expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Log.endTestCase(methodName);
    }











    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        Object[][] categoryData = new Object[rowCount][4];
        for (int i = 1, j = 0; i <= rowCount; i++, j++) {
            categoryData[j][0] = i;
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
        }
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int prodRowCount = ExcelUtility.getRowCount();
        Object[] ProductObject = new Object[prodRowCount];
        for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
            ProductObject[i] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, j);
        }

        int countTotal = ProductObject.length * rowCount;
        Object[][] lpData = new Object[countTotal][5];
        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
            lpData[j][0] = categoryData[k][0];
            lpData[j][1] = categoryData[k][1];
            lpData[j][2] = categoryData[k][2];
            lpData[j][3] = categoryData[k][3];
            lpData[j][4] = ProductObject[i];
            if (k < rowCount) {
                k++;
                if (k >= rowCount) {
                    k = 0;
                    i++;
                    if (i >= ProductObject.length)
                        i = 0;
                }
            } else {
                k = 0;
            }
        }
        return lpData;
    }


}
