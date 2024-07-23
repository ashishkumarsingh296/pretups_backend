package com.restapi.channelAdmin;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;

import java.io.IOException;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;

import jxl.read.biff.BiffException;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {BulkAutoC2CSOSCreditLimitServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class BulkAutoC2CSOSCreditLimitServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private BulkAutoC2CSOSCreditLimitServiceImpl bulkAutoC2CSOSCreditLimitServiceImpl;

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#downloadTemplate(Connection, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testDownloadTemplate() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        bulkAutoC2CSOSCreditLimitServiceImpl.downloadTemplate(com.btsl.util.JUnitConfig.getConnection(), response1);
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#downloadUserList(Connection, HttpServletResponse, String, String, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadUserList() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.BulkAutoC2CSOSCreditLimitServiceImpl.downloadUserList(BulkAutoC2CSOSCreditLimitServiceImpl.java:213)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        bulkAutoC2CSOSCreditLimitServiceImpl.downloadUserList(con, new CustomResponseWrapper(new Response()), "Login ID",
                "Domain", "Category", "Geo Domain");
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        bulkAutoC2CSOSCreditLimitServiceImpl.validateFileDetailsMap(new HashMap<>());
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap2() throws BTSLBaseException {
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        thrown.expect(BTSLBaseException.class);
        bulkAutoC2CSOSCreditLimitServiceImpl.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFileDetailsMap3() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.channelAdmin.BulkAutoC2CSOSCreditLimitServiceImpl.validateFileName(BulkAutoC2CSOSCreditLimitServiceImpl.java:1184)
        //       at com.restapi.channelAdmin.BulkAutoC2CSOSCreditLimitServiceImpl.validateFileDetailsMap(BulkAutoC2CSOSCreditLimitServiceImpl.java:1196)
        //   See https://diff.blue/R013 to resolve this issue.

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILEATTACHMENT", "FILEATTACHMENT");
        fileDetailsMap.put("FILENAME", "FILENAME");
        bulkAutoC2CSOSCreditLimitServiceImpl.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap4() throws BTSLBaseException {
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "");
        thrown.expect(BTSLBaseException.class);
        bulkAutoC2CSOSCreditLimitServiceImpl.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#chechExcelData(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChechExcelData() throws IOException, BiffException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.io.FileNotFoundException: foo.txt (The system cannot find the file specified)
        //       at java.io.FileInputStream.open0(Native Method)
        //       at java.io.FileInputStream.open(FileInputStream.java:195)
        //       at java.io.FileInputStream.<init>(FileInputStream.java:138)
        //       at jxl.Workbook.getWorkbook(Workbook.java:213)
        //       at jxl.Workbook.getWorkbook(Workbook.java:198)
        //       at com.restapi.channelAdmin.BulkAutoC2CSOSCreditLimitServiceImpl.chechExcelData(BulkAutoC2CSOSCreditLimitServiceImpl.java:1216)
        //   See https://diff.blue/R013 to resolve this issue.

        try {
            bulkAutoC2CSOSCreditLimitServiceImpl.chechExcelData("foo.txt");
        } catch (BTSLBaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#chechExcelData(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChechExcelData2() throws IOException, BiffException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.io.FileNotFoundException: 0123456789ABCDEF (The system cannot find the file specified)
        //       at java.io.FileInputStream.open0(Native Method)
        //       at java.io.FileInputStream.open(FileInputStream.java:195)
        //       at java.io.FileInputStream.<init>(FileInputStream.java:138)
        //       at jxl.Workbook.getWorkbook(Workbook.java:213)
        //       at jxl.Workbook.getWorkbook(Workbook.java:198)
        //       at com.restapi.channelAdmin.BulkAutoC2CSOSCreditLimitServiceImpl.chechExcelData(BulkAutoC2CSOSCreditLimitServiceImpl.java:1216)
        //   See https://diff.blue/R013 to resolve this issue.

       // bulkAutoC2CSOSCreditLimitServiceImpl.chechExcelData("0123456789ABCDEF");
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#chechExcelData(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChechExcelData3() throws IOException, BiffException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.io.File.<init>(File.java:279)
        //       at com.restapi.channelAdmin.BulkAutoC2CSOSCreditLimitServiceImpl.chechExcelData(BulkAutoC2CSOSCreditLimitServiceImpl.java:1216)
        //   See https://diff.blue/R013 to resolve this issue.

       // bulkAutoC2CSOSCreditLimitServiceImpl.chechExcelData(null);
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitServiceImpl#downloadErrorLogFile(ArrayList, UserVO, BulkAutoC2CSOSCreditLimitFileResponseVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadErrorLogFile() {
        // TODO: Complete this test.
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        ArrayList errorList = new ArrayList();

        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        categoryVO.setAgentAllowed("Agent Allowed");
        categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        categoryVO.setAgentCategoryCode("Agent Category Code");
        categoryVO.setAgentCategoryName("Agent Category Name");
        categoryVO.setAgentCategoryStatus("Agent Category Status");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("Agent Category Type");
        categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        categoryVO.setAgentCp2pPayee("Cp2p Payee");
        categoryVO.setAgentCp2pPayer("Cp2p Payer");
        categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        categoryVO.setAgentDomainName("Agent Domain Name");
        categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        categoryVO.setAgentGatewayName("Agent Gateway Name");
        categoryVO.setAgentGatewayType("Agent Gateway Type");
        categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        categoryVO.setAgentRoleName("Agent Role Name");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("Type");
        categoryVO.setCategoryCode("Category Code");
        categoryVO.setCategoryName("Category Name");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("Category Status");
        categoryVO.setCategoryType("Category Type");
        categoryVO.setCategoryTypeCode("Category Type Code");
        categoryVO.setCp2pPayee("Payee");
        categoryVO.setCp2pPayer("Payer");
        categoryVO.setCp2pWithinList("Within List");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("Display Allowed");
        categoryVO.setDomainAllowed("Domain Allowed");
        categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        categoryVO.setDomainName("Domain Name");
        categoryVO.setDomainTypeCode("Domain Type Code");
        categoryVO.setFixedDomains("Fixed Domains");
        categoryVO.setFixedRoles("Fixed Roles");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("Grph Domain Type");
        categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("Modify Allowed");
        categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("Outlets Allowed");
        categoryVO.setParentCategoryCode("Parent Category Code");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("Product Type Allowed");
        categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("By Parent Only");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("Service Allowed");
        categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        categoryVO.setTransferToListOnly("Transfer To List Only");
        categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        categoryVO.setUserIdPrefix("User Id Prefix");
        categoryVO.setViewOnNetworkBlock("View On Network Block");
        categoryVO.setWebInterfaceAllowed("Web Interface Allowed");

        SessionInfoVO sessionInfoVO = new SessionInfoVO();
        sessionInfoVO.setCookieID("Cookie ID");
        sessionInfoVO.setCurrentModuleCode("Current Module Code");
        sessionInfoVO.setCurrentPageCode("Current Page Code");
        sessionInfoVO.setCurrentPageName("Current Page Name");
        sessionInfoVO.setCurrentRoleCode("Current Role Code");
        sessionInfoVO.setMessageGatewayVO(new MessageGatewayVO());
        sessionInfoVO.setRemoteAddr("42 Main St");
        sessionInfoVO.setRemoteHost("localhost");
        sessionInfoVO.setRoleHitTimeMap(new HashMap());
        sessionInfoVO.setSessionID("Session ID");
        sessionInfoVO.setTotalHit(1L);
        sessionInfoVO.setUnderProcess(true);
        sessionInfoVO.setUnderProcessHit(1L);

        UserVO userVO = new UserVO();
        userVO.setActiveUserID("Active User ID");
        userVO.setActiveUserLoginId("42");
        userVO.setActiveUserMsisdn("Active User Msisdn");
        userVO.setActiveUserPin("Active User Pin");
        userVO.setAddCommProfOTFDetailId("42");
        userVO.setAddress1("42 Main St");
        userVO.setAddress2("42 Main St");
        userVO.setAgentBalanceList(new ArrayList<>());
        userVO.setAllowedDay(new String[]{"Allowed Days"});
        userVO.setAllowedDays("Allowed Days");
        userVO.setAllowedIps("Allowed Ips");
        userVO.setAllowedUserTypeCreation("Allowed User Type Creations");
        userVO.setAppintmentDate("2020-03-01");
        userVO.setAppointmentDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssType("Ass Type");
        userVO.setAssoMsisdn("Asso Msisdn");
        userVO.setAssociatedGeographicalList(new ArrayList());
        userVO.setAssociatedProductTypeList(new ArrayList());
        userVO.setAssociatedServiceTypeList(new ArrayList());
        userVO.setAssociationCreatedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssociationModifiedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAuthType("Type");
        userVO.setAuthTypeAllowed("Type Allowed");
        userVO.setBatchID("Batch ID");
        userVO.setBatchName("Batch Name");
        userVO.setBrowserType("Browser Type");
        userVO.setC2sMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setC2sMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setCategoryCode("Category Code");
        userVO.setCategoryCodeDesc("Category Code Desc");
        userVO.setCategoryVO(categoryVO);
        userVO.setCity("Oxford");
        userVO.setCompany("Company");
        userVO.setConfirmPassword("iloveyou");
        userVO.setContactNo("Contact N0");
        userVO.setContactPerson("Contact Person");
        userVO.setCountry("GB");
        userVO.setCountryCode("GB");
        userVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        userVO.setCreatedByUserName("janedoe");
        userVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setCreatedOnAsString("Jan 1, 2020 8:00am GMT+0100");
        userVO.setCreated_On("Jan 1, 2020 8:00am GMT+0100");
        userVO.setCreationType("Creation Type");
        userVO.setCurrentModule("Current Module");
        userVO.setCurrentRoleCode("Current Role Code");
        userVO.setDepartmentCode("Department Code");
        userVO.setDepartmentDesc("Department Desc");
        userVO.setDepartmentList(new ArrayList<>());
        userVO.setDesignation("Designation");
        userVO.setDivisionCode("Division Code");
        userVO.setDivisionDesc("Division Desc");
        userVO.setDivisionList(new ArrayList<>());
        userVO.setDocumentNo("Document No");
        userVO.setDocumentType("Document Type");
        userVO.setDomainCodes(new String[]{"Domain Codes"});
        userVO.setDomainID("Domain ID");
        userVO.setDomainList(new ArrayList());
        userVO.setDomainName("Domain Name");
        userVO.setDomainStatus("Domain Status");
        userVO.setDomainTypeCode("Domain Type Code");
        userVO.setEmail("jane.doe@example.org");
        userVO.setEmpCode("Emp Code");
        userVO.setExternalCode("External Code");
        userVO.setFax("Fax");
        userVO.setFirstName("Name");
        userVO.setFromTime("jane.doe@example.org");
        userVO.setFxedInfoStr("Fxed Info Str");
        userVO.setGeographicalAreaList(new ArrayList<>());
        userVO.setGeographicalCode("Geographical Codes");
        userVO.setGeographicalCodeArray(new String[]{"Geographical Code Arrays"});
        userVO.setGeographicalCodeStatus("Geographical Code Status");
        userVO.setGeographicalList(new ArrayList());
        userVO.setGrphDomainTypeName("Grph Domain Type Names");
        userVO.setInfo1("Info1");
        userVO.setInfo10("Info10");
        userVO.setInfo11("Info11");
        userVO.setInfo12("Info12");
        userVO.setInfo13("Info13");
        userVO.setInfo14("Info14");
        userVO.setInfo15("Info15");
        userVO.setInfo2("Info2");
        userVO.setInfo3("Info3");
        userVO.setInfo4("Info4");
        userVO.setInfo5("Info5");
        userVO.setInfo6("Info6");
        userVO.setInfo7("Info7");
        userVO.setInfo8("Info8");
        userVO.setInfo9("Info9");
        userVO.setInvalidPasswordCount(3);
        userVO.setIsSerAssignChnlAdm(true);
        userVO.setLanguage("en");
        userVO.setLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLastModified(1L);
        userVO.setLastName("Name");
        userVO.setLatitude("Latitude");
        userVO.setLevel1ApprovedBy("Level1 Approved By");
        userVO.setLevel1ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLevel2ApprovedBy("Level2 Approved By");
        userVO.setLevel2ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLoggerMessage("Logger Message");
        userVO.setLoginID("Login ID");
        userVO.setLoginTime(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLongitude("Longitude");
        userVO.setMenuItemList(new ArrayList());
        userVO.setMessage("Not all who wander are lost");
        userVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        userVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setModuleCodeString("Code String");
        userVO.setMsisdn("Msisdn");
        userVO.setMsisdnList(new ArrayList());
        userVO.setNetworkID("Network ID");
        userVO.setNetworkList(new ArrayList());
        userVO.setNetworkName("Network Name");
        userVO.setNetworkStatus("Network Status");
        userVO.setOTPValidated(true);
        userVO.setOldLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setOtfCount(3);
        userVO.setOtfValue(42L);
        userVO.setOwnerCategoryName("Owner Category Name");
        userVO.setOwnerCompany("Company");
        userVO.setOwnerID("Owner ID");
        userVO.setOwnerMsisdn("Owner Msisdn");
        userVO.setOwnerName("Owner Name");
        userVO.setP2pMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setP2pMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPageCodeString("Code String");
        userVO.setParentCategoryName("Parent Category Name");
        userVO.setParentID("Parent ID");
        userVO.setParentMsisdn("Parent Msisdn");
        userVO.setParentName("Parent Name");
        userVO.setPassword("iloveyou");
        userVO.setPasswordCountUpdatedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO
                .setPasswordModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPasswordModifyFlag(true);
        userVO.setPasswordReset("Password Reset");
        userVO.setPaymentType("Payment Type");
        userVO.setPaymentTypes("Payment Types");
        userVO.setPaymentTypes(new String[]{"Payment Types"});
        userVO.setPaymentTypesList(new ArrayList());
        userVO.setPinReset("Pin Reset");
        userVO.setPreviousStatus("Previous Status");
        userVO.setProductCodes(new String[]{"Product Codess"});
        userVO.setProductsList(new ArrayList());
        userVO.setReferenceID("Reference ID");
        userVO.setRemarks("Remarks");
        userVO.setRemoteAddress("42 Main St");
        userVO.setReportHeaderName("Report Header Name");
        userVO.setRequestType("Request Type");
        userVO.setRequetedByUserName("janedoe");
        userVO.setRestrictedMsisdnAllow("Restricted Msisdn Allow");
        userVO.setRoleFlag(new String[]{"Role Flags"});
        userVO.setRoleType("Role Types");
        userVO.setRolesMap(new HashMap());
        userVO.setRolesMapSelected(new HashMap());
        userVO.setRsaAllowed(true);
        userVO.setRsaFlag("Rsa Flag");
        userVO.setRsaRequired(true);
        userVO.setRsavalidated(true);
        userVO.setSegmentList(new ArrayList());
        userVO.setServiceList(new ArrayList());
        userVO.setServicesList(new ArrayList());
        userVO.setServicesTypes(new String[]{"Services Typess"});
        userVO.setSessionInfoVO(sessionInfoVO);
        userVO.setShortName("Short Name");
        userVO.setShowPassword("iloveyou");
        userVO.setSsn("123-45-678");
        userVO.setStaffUser(true);
        userVO.setStaffUserDetails(ChannelUserVO.getInstance());
        userVO.setState("MD");
        userVO.setStatus("Status");
        userVO.setStatusDesc("Status Desc");
        userVO.setStatusList(new ArrayList());
        userVO.setSuspendedByUserName("janedoe");
        userVO.setSuspendedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setToTime("To Time");
        userVO.setUpdateSimRequired(true);
        userVO.setUserBalanceList(new ArrayList<>());
        userVO.setUserCode("User Code");
        userVO.setUserID("User ID");
        userVO.setUserLanguage("en");
        userVO.setUserLanguageDesc("en");
        userVO.setUserLanguageList(new ArrayList());
        userVO.setUserLoanVOList(new ArrayList<>());
        userVO.setUserName("janedoe");
        userVO.setUserNamePrefix("janedoe");
        userVO.setUserNamePrefixList(new ArrayList());
        userVO.setUserPhoneVO(UserPhoneVO.getInstance());
        userVO.setUserType("User Type");
        userVO.setUsingNewSTK(true);
        userVO.setValidRequestURLs("https://example.org/example");
        userVO.setValidStatus(1);
        userVO.setVoucherList(new ArrayList());
        userVO.setWebLoginID(" web Login ID");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BulkAutoC2CSOSCreditLimitFileResponseVO response = new BulkAutoC2CSOSCreditLimitFileResponseVO();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTotalRecords(1);
        response.setTransactionId("42");
        response.setValidRecords(1);
        response.set_errorFlag("An error occurred");
        response.set_errorList(new ArrayList());
        response.set_noOfRecords(" no Of Records");
        bulkAutoC2CSOSCreditLimitServiceImpl.downloadErrorLogFile(errorList, userVO, response,
                new CustomResponseWrapper(new Response()));
    }
}

