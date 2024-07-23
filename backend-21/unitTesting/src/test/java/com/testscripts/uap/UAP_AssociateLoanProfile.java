package com.testscripts.uap;

import com.Features.AssociateLoanProfile;
import com.Features.ChannelUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.HashMap;


@ModuleManager(name = Module.UAP_ASSOCIATE_LOAN_PROFILE)
public class UAP_AssociateLoanProfile extends BaseTest {

    String assignCategory="UAP";
    HashMap<String, String> channelresultMap;

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC01_AssociateLoanProfile(int rowNum, String MSISDN, String LoginID, String userName, String Domain, String Parent, String Category, String geotype, String profileName)  throws InterruptedException {
        final String methodName = "TC01_AssociateLoanProfile";
        Log.startTestCase(methodName, rowNum, MSISDN, LoginID, userName, Domain, Parent, Category, geotype, profileName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPASSCTLP1").getExtentCase(),Domain,Category));
        currentNode.assignCategory(assignCategory);

        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(Category);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        AssociateLoanProfile associateLoanProfile = new AssociateLoanProfile(driver);
        String actualMessage = associateLoanProfile.associateLoanProfile(MSISDN, LoginID, Domain, Parent, Category, geotype, profileName);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPASSCTLP2").getExtentCase());
        currentNode.assignCategory(assignCategory);
        String expectedMessage = MessagesDAO.prepareMessageByKey("user.associatechanneluser.updatesuccessmessage", userName);
        Assertion.assertEquals(actualMessage, expectedMessage);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC02_AssociateLoanProfileUserCreation(int rowNum, String MSISDN, String LoginID, String userName, String Domain, String Parent, String Category, String geotype, String profileName)  throws InterruptedException {
        final String methodName = "TC02_AssociateLoanProfileUserCreation";
        Log.startTestCase(methodName, rowNum, MSISDN, LoginID, userName, Domain, Parent, Category, geotype, profileName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPASSCTLP3").getExtentCase(),Domain,Category));
        String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(Category);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode, UserAccess.userapplevelpreference());
        currentNode.assignCategory(assignCategory);
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PCHNLCREATION2");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SCHNLCREATION2");
        CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("PCHNLCREATION3");
        CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("SCHNLCREATION3");
        CaseMaster CaseMaster7 = _masterVO.getCaseMasterByID("PCHNLCREATION4");
        CaseMaster CaseMaster8 = _masterVO.getCaseMasterByID("SCHNLCREATION4");

        ChannelUser channelUserLogic= new ChannelUser(driver);
        channelresultMap=channelUserLogic.channelUserInitiate(rowNum, Domain, Parent, Category, geotype);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPASSCTLP2").getExtentCase());
        currentNode.assignCategory(assignCategory);
        String expectedMessage;
        if(APPLEVEL.equals("0"))
        {
            expectedMessage = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", channelresultMap.get("uName"));
        }else{
            expectedMessage = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", channelresultMap.get("uName"));
        }
        Assertion.assertEquals(channelresultMap.get("channelInitiateMsg"), expectedMessage);
        if(APPLEVEL.equals("2"))
        {
            //Approval level 1
            currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), Category));//"To verify that operator user is able to approve level 1 " + Category+" category Channel user.");
            currentNode.assignCategory("UAP");
            channelresultMap=channelUserLogic.approveLevel1_ChannelUser();

            //Approval level 1 message validation
            currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), Category));//"To verify that valid message is displayed after approval level 1 of "+Category+" category channel user.");
            currentNode.assignCategory("UAP");
            String intChnlApprove1Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagerequiredleveltwoapproval", channelresultMap.get("uName"));
            Assertion.assertEquals(channelresultMap.get("channelApprovelevel1Msg"), intChnlApprove1Msg);

            //Approval level 2
            currentNode=test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), Category));//"To verify that Operator user is able to approve level 2 " + Category+" category Channel user.");
            currentNode.assignCategory("UAP");
            channelresultMap=channelUserLogic.approveLevel2_ChannelUser();

            //Approval level 2 message validation
            currentNode=test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(), Category));//"To verify that valid message is displayed after approval level 2 of "+Category+" category channel user.");
            currentNode.assignCategory("UAP");
            String intChnlApprove2Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
            Assertion.assertEquals(channelresultMap.get("channelApprovelevel2Msg"), intChnlApprove2Msg);
        }
        else if(APPLEVEL.equals("1")){
            //Approval level 1
            currentNode=test.createNode(MessageFormat.format(CaseMaster7.getExtentCase(), Category));//"To verify that Operator user is able to approve " + Category+" category Channel user.");
            currentNode.assignCategory("UAP");
            channelresultMap=channelUserLogic.approveLevel1_ChannelUser();

            //Approval level 1 message validation
            currentNode=test.createNode(MessageFormat.format(CaseMaster8.getExtentCase(), Category));//"To verify that valid message is displayed after approval of "+Category+" category channel user.");
            currentNode.assignCategory("UAP");
            String intChnlApproveMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
            Assertion.assertEquals(channelresultMap.get("channelApproveMsg"), intChnlApproveMsg);
        }else{
            Log.info("Approval not required.");
        }
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
    public void TC03_AssociateLoanProfileUserModification(int rowNum, String MSISDN, String LoginID, String userName, String Domain, String Parent, String Category, String geotype, String profileName)  throws InterruptedException {
        final String methodName = "TC03_AssociateLoanProfileUserModification";
        Log.startTestCase(methodName, rowNum, MSISDN, LoginID, userName, Domain, Parent, Category, geotype, profileName);

        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPASSCTLP3").getExtentCase(),Domain,Category));
        String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
        String preferenceCode = "CAT_USERWISE_LOAN_ENABLE";
        String categoryCode = DBHandler.AccessHandler.getCategoryCode(Category);
        String loanPrefValue= DBHandler.AccessHandler.getValuefromControlCodeControlPreference(preferenceCode, categoryCode);
        Log.info("Category Code is : " +categoryCode);
        if(loanPrefValue.equals("true")) {
        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode, UserAccess.userapplevelpreference());
        currentNode.assignCategory(assignCategory);

        ChannelUser channelUserLogic= new ChannelUser(driver);
        channelresultMap=channelUserLogic.channelUserInitiate(rowNum, Domain, Parent, Category, geotype);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("UAPLOANPROFILE2").getExtentCase());
        currentNode.assignCategory(assignCategory);
        String expectedMessage;
        if(APPLEVEL.equals("0"))
        {
            expectedMessage = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", channelresultMap.get("uName"));
        }else{
            expectedMessage = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", channelresultMap.get("uName"));
        }
        Assertion.assertEquals(channelresultMap.get("channelInitiateMsg"), expectedMessage);
        if(APPLEVEL.equals("2"))
        {
            //Approval level 1
            channelresultMap=channelUserLogic.approveLevel1_ChannelUser();

            //Approval level 1 message validation
            String intChnlApprove1Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagerequiredleveltwoapproval", channelresultMap.get("uName"));
            Assertion.assertEquals(channelresultMap.get("channelApprovelevel1Msg"), intChnlApprove1Msg);

            //Approval level 2
            channelresultMap=channelUserLogic.approveLevel2_ChannelUser();

            //Approval level 2 message validation
            String intChnlApprove2Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
            Assertion.assertEquals(channelresultMap.get("channelApprovelevel2Msg"), intChnlApprove2Msg);
        }
        else if(APPLEVEL.equals("1")){
            //Approval level 1
            channelresultMap=channelUserLogic.approveLevel1_ChannelUser();

            //Approval level 1 message validation
            String intChnlApproveMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
            Assertion.assertEquals(channelresultMap.get("channelApproveMsg"), intChnlApproveMsg);
        }else{
            Log.info("Approval not required.");
        }
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UAPASSCTLP4").getExtentCase(),Domain,Category));
        String actualMessage = channelUserLogic.modifyChannelUserLoanProfile(rowNum, profileName);
        String expectedMessage1 = MessagesDAO.prepareMessageByKey("user.addchanneluser.updatesuccessmessage", channelresultMap.get("uName"));
        Assertion.assertEquals(actualMessage, expectedMessage1);
        }
        else
            Assertion.assertSkip("The system preference for Category Loan Enable or disable User Wise is disabled, hence test case is skipped.");

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {

        _masterVO.loadGeoDomains();
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        Object[][] categoryData = new Object[rowCount][9];
        for (int i = 1, j = 0; i <= rowCount; i++, j++) {
            categoryData[j][0] = i;
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
            categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
            categoryData[j][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
            categoryData[j][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
            categoryData[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            categoryData[j][7] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
            categoryData[j][8] = ExcelUtility.getCellData(0, ExcelI.LOAN_PROFILE, i);
        }
        return categoryData;
    }


}
