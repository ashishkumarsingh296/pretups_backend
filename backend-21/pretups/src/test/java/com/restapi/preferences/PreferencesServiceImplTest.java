package com.restapi.preferences;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.preferences.requestVO.UpdateSystemPreferencesRequestVO;
import com.restapi.preferences.responseVO.SystemPreferencesResponseVO;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PreferencesServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PreferencesServiceImplTest {
    @Autowired
    private PreferencesServiceImpl preferencesServiceImpl;

    /**
     * Method under test: {@link PreferencesServiceImpl#getSystemPreferences(String, String, Connection, SystemPreferencesResponseVO, HttpServletResponse, UserVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetSystemPreferences() throws BTSLBaseException {
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

        // Arrange
       //Connection con = mock(Connection.class);

        SystemPreferencesResponseVO response = new SystemPreferencesResponseVO();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setPreferenceList(new ArrayList<>());
        response.setStatus(1);
        response.setTransactionId("42");
        //CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        UserVO sessionUserVO = new UserVO();
        sessionUserVO.setActiveUserID("Active User ID");
        sessionUserVO.setActiveUserLoginId("42");
        sessionUserVO.setActiveUserMsisdn("Active User Msisdn");
        sessionUserVO.setActiveUserPin("Active User Pin");
        sessionUserVO.setAddCommProfOTFDetailId("42");
        sessionUserVO.setAddress1("42 Main St");
        sessionUserVO.setAddress2("42 Main St");
        sessionUserVO.setAgentBalanceList(new ArrayList<>());
        sessionUserVO.setAllowedDay(new String[]{"Allowed Days"});
        sessionUserVO.setAllowedDays("Allowed Days");
        sessionUserVO.setAllowedIps("Allowed Ips");
        sessionUserVO.setAllowedUserTypeCreation("Allowed User Type Creations");
        sessionUserVO.setAppintmentDate("2020-03-01");
        sessionUserVO
                .setAppointmentDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAssType("Ass Type");
        sessionUserVO.setAssoMsisdn("Asso Msisdn");
        sessionUserVO.setAssociatedGeographicalList(new ArrayList());
        sessionUserVO.setAssociatedProductTypeList(new ArrayList());
        sessionUserVO.setAssociatedServiceTypeList(new ArrayList());
        sessionUserVO
                .setAssociationCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAssociationModifiedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAuthType("Type");
        sessionUserVO.setAuthTypeAllowed("Type Allowed");
        sessionUserVO.setBatchID("Batch ID");
        sessionUserVO.setBatchName("Batch Name");
        sessionUserVO.setBrowserType("Browser Type");
        sessionUserVO
                .setC2sMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO
                .setC2sMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setCategoryCode("Category Code");
        sessionUserVO.setCategoryCodeDesc("Category Code Desc");

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
        sessionUserVO.setCategoryVO(categoryVO);
        sessionUserVO.setCity("Oxford");
        sessionUserVO.setCompany("Company");
        sessionUserVO.setConfirmPassword("iloveyou");
        sessionUserVO.setContactNo("Contact N0");
        sessionUserVO.setContactPerson("Contact Person");
        sessionUserVO.setCountry("GB");
        sessionUserVO.setCountryCode("GB");
        sessionUserVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        sessionUserVO.setCreatedByUserName("janedoe");
        sessionUserVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setCreatedOnAsString("Jan 1, 2020 8:00am GMT+0100");
        sessionUserVO.setCreated_On("Jan 1, 2020 8:00am GMT+0100");
        sessionUserVO.setCreationType("Creation Type");
        sessionUserVO.setCurrentModule("Current Module");
        sessionUserVO.setCurrentRoleCode("Current Role Code");
        sessionUserVO.setDepartmentCode("Department Code");
        sessionUserVO.setDepartmentDesc("Department Desc");
        sessionUserVO.setDepartmentList(new ArrayList<>());
        sessionUserVO.setDesignation("Designation");
        sessionUserVO.setDivisionCode("Division Code");
        sessionUserVO.setDivisionDesc("Division Desc");
        sessionUserVO.setDivisionList(new ArrayList<>());
        sessionUserVO.setDocumentNo("Document No");
        sessionUserVO.setDocumentType("Document Type");
        sessionUserVO.setDomainCodes(new String[]{"Domain Codes"});
        sessionUserVO.setDomainID("Domain ID");
        sessionUserVO.setDomainList(new ArrayList());
        sessionUserVO.setDomainName("Domain Name");
        sessionUserVO.setDomainStatus("Domain Status");
        sessionUserVO.setDomainTypeCode("Domain Type Code");
        sessionUserVO.setEmail("jane.doe@example.org");
        sessionUserVO.setEmpCode("Emp Code");
        sessionUserVO.setExternalCode("External Code");
        sessionUserVO.setFax("Fax");
        sessionUserVO.setFirstName("Name");
        sessionUserVO.setFromTime("jane.doe@example.org");
        sessionUserVO.setFxedInfoStr("Fxed Info Str");
        sessionUserVO.setGeographicalAreaList(new ArrayList<>());
        sessionUserVO.setGeographicalCode("Geographical Codes");
        sessionUserVO.setGeographicalCodeArray(new String[]{"Geographical Code Arrays"});
        sessionUserVO.setGeographicalCodeStatus("Geographical Code Status");
        sessionUserVO.setGeographicalList(new ArrayList());
        sessionUserVO.setGrphDomainTypeName("Grph Domain Type Names");
        sessionUserVO.setInfo1("Info1");
        sessionUserVO.setInfo10("Info10");
        sessionUserVO.setInfo11("Info11");
        sessionUserVO.setInfo12("Info12");
        sessionUserVO.setInfo13("Info13");
        sessionUserVO.setInfo14("Info14");
        sessionUserVO.setInfo15("Info15");
        sessionUserVO.setInfo2("Info2");
        sessionUserVO.setInfo3("Info3");
        sessionUserVO.setInfo4("Info4");
        sessionUserVO.setInfo5("Info5");
        sessionUserVO.setInfo6("Info6");
        sessionUserVO.setInfo7("Info7");
        sessionUserVO.setInfo8("Info8");
        sessionUserVO.setInfo9("Info9");
        sessionUserVO.setInvalidPasswordCount(3);
        sessionUserVO.setIsSerAssignChnlAdm(true);
        sessionUserVO.setLanguage("en");
        sessionUserVO.setLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLastModified(1L);
        sessionUserVO.setLastName("Name");
        sessionUserVO.setLatitude("Latitude");
        sessionUserVO.setLevel1ApprovedBy("Level1 Approved By");
        sessionUserVO
                .setLevel1ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLevel2ApprovedBy("Level2 Approved By");
        sessionUserVO
                .setLevel2ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLoggerMessage("Logger Message");
        sessionUserVO.setLoginID("Login ID");
        sessionUserVO.setLoginTime(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLongitude("Longitude");
        sessionUserVO.setMenuItemList(new ArrayList());
        sessionUserVO.setMessage("Not all who wander are lost");
        sessionUserVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        sessionUserVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setModuleCodeString("Code String");
        sessionUserVO.setMsisdn("Msisdn");
        sessionUserVO.setMsisdnList(new ArrayList());
        sessionUserVO.setNetworkID("Network ID");
        sessionUserVO.setNetworkList(new ArrayList());
        sessionUserVO.setNetworkName("Network Name");
        sessionUserVO.setNetworkStatus("Network Status");
        sessionUserVO.setOTPValidated(true);
        sessionUserVO
                .setOldLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setOtfCount(3);
        sessionUserVO.setOtfValue(42L);
        sessionUserVO.setOwnerCategoryName("Owner Category Name");
        sessionUserVO.setOwnerCompany("Company");
        sessionUserVO.setOwnerID("Owner ID");
        sessionUserVO.setOwnerMsisdn("Owner Msisdn");
        sessionUserVO.setOwnerName("Owner Name");
        sessionUserVO
                .setP2pMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO
                .setP2pMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setPageCodeString("Code String");
        sessionUserVO.setParentCategoryName("Parent Category Name");
        sessionUserVO.setParentID("Parent ID");
        sessionUserVO.setParentMsisdn("Parent Msisdn");
        sessionUserVO.setParentName("Parent Name");
        sessionUserVO.setPassword("iloveyou");
        sessionUserVO.setPasswordCountUpdatedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO
                .setPasswordModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setPasswordModifyFlag(true);
        sessionUserVO.setPasswordReset("Password Reset");
        sessionUserVO.setPaymentType("Payment Type");
        sessionUserVO.setPaymentTypes("Payment Types");
        sessionUserVO.setPaymentTypes(new String[]{"Payment Types"});
        sessionUserVO.setPaymentTypesList(new ArrayList());
        sessionUserVO.setPinReset("Pin Reset");
        sessionUserVO.setPreviousStatus("Previous Status");
        sessionUserVO.setProductCodes(new String[]{"Product Codess"});
        sessionUserVO.setProductsList(new ArrayList());
        sessionUserVO.setReferenceID("Reference ID");
        sessionUserVO.setRemarks("Remarks");
        sessionUserVO.setRemoteAddress("42 Main St");
        sessionUserVO.setReportHeaderName("Report Header Name");
        sessionUserVO.setRequestType("Request Type");
        sessionUserVO.setRequetedByUserName("janedoe");
        sessionUserVO.setRestrictedMsisdnAllow("Restricted Msisdn Allow");
        sessionUserVO.setRoleFlag(new String[]{"Role Flags"});
        sessionUserVO.setRoleType("Role Types");
        sessionUserVO.setRolesMap(new HashMap());
        sessionUserVO.setRolesMapSelected(new HashMap());
        sessionUserVO.setRsaAllowed(true);
        sessionUserVO.setRsaFlag("Rsa Flag");
        sessionUserVO.setRsaRequired(true);
        sessionUserVO.setRsavalidated(true);
        sessionUserVO.setSegmentList(new ArrayList());
        sessionUserVO.setServiceList(new ArrayList());
        sessionUserVO.setServicesList(new ArrayList());
        sessionUserVO.setServicesTypes(new String[]{"Services Typess"});

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
        sessionUserVO.setSessionInfoVO(sessionInfoVO);
        sessionUserVO.setShortName("Short Name");
        sessionUserVO.setShowPassword("iloveyou");
        sessionUserVO.setSsn("123-45-678");
        sessionUserVO.setStaffUser(true);
        sessionUserVO.setStaffUserDetails(ChannelUserVO.getInstance());
        sessionUserVO.setState("MD");
        sessionUserVO.setStatus("Status");
        sessionUserVO.setStatusDesc("Status Desc");
        sessionUserVO.setStatusList(new ArrayList());
        sessionUserVO.setSuspendedByUserName("janedoe");
        sessionUserVO.setSuspendedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setToTime("To Time");
        sessionUserVO.setUpdateSimRequired(true);
        sessionUserVO.setUserBalanceList(new ArrayList<>());
        sessionUserVO.setUserCode("User Code");
        sessionUserVO.setUserID("User ID");
        sessionUserVO.setUserLanguage("en");
        sessionUserVO.setUserLanguageDesc("en");
        sessionUserVO.setUserLanguageList(new ArrayList());
        sessionUserVO.setUserLoanVOList(new ArrayList<>());
        sessionUserVO.setUserName("janedoe");
        sessionUserVO.setUserNamePrefix("janedoe");
        sessionUserVO.setUserNamePrefixCode("janedoe");
        sessionUserVO.setUserNamePrefixDesc("janedoe");
        sessionUserVO.setUserNamePrefixList(new ArrayList());
        sessionUserVO.setUserPhoneVO(UserPhoneVO.getInstance());
        sessionUserVO.setUserType("User Type");
        sessionUserVO.setUsingNewSTK(true);
        sessionUserVO.setValidRequestURLs("https://example.org/example");
        sessionUserVO.setValidStatus(1);
        sessionUserVO.setVoucherList(new ArrayList());
        sessionUserVO.setWebLoginID(" web Login ID");
        String resultModule = "Module";
        String type = "Type";

        // Act
        preferencesServiceImpl.getSystemPreferences(resultModule, type, JUnitConfig.getConnection(), response, response1, sessionUserVO);
    }

    /**
     * Method under test: {@link PreferencesServiceImpl#updateSystemPreferences(Connection, MComConnectionI, BaseResponse, HttpServletResponse, UserVO, UpdateSystemPreferencesRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUpdateSystemPreferences() throws Exception {
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

        // Arrange
       //Connection con = mock(Connection.class);
        MComConnection mcomCon = new MComConnection();

        BaseResponse response = new BaseResponse();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
       // CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        UserVO sessionUserVO = new UserVO();
        sessionUserVO.setActiveUserID("Active User ID");
        sessionUserVO.setActiveUserLoginId("42");
        sessionUserVO.setActiveUserMsisdn("Active User Msisdn");
        sessionUserVO.setActiveUserPin("Active User Pin");
        sessionUserVO.setAddCommProfOTFDetailId("42");
        sessionUserVO.setAddress1("42 Main St");
        sessionUserVO.setAddress2("42 Main St");
        sessionUserVO.setAgentBalanceList(new ArrayList<>());
        sessionUserVO.setAllowedDay(new String[]{"Allowed Days"});
        sessionUserVO.setAllowedDays("Allowed Days");
        sessionUserVO.setAllowedIps("Allowed Ips");
        sessionUserVO.setAllowedUserTypeCreation("Allowed User Type Creations");
        sessionUserVO.setAppintmentDate("2020-03-01");
        sessionUserVO
                .setAppointmentDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAssType("Ass Type");
        sessionUserVO.setAssoMsisdn("Asso Msisdn");
        sessionUserVO.setAssociatedGeographicalList(new ArrayList());
        sessionUserVO.setAssociatedProductTypeList(new ArrayList());
        sessionUserVO.setAssociatedServiceTypeList(new ArrayList());
        sessionUserVO.setAssociationCreatedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAssociationModifiedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAuthType("Type");
        sessionUserVO.setAuthTypeAllowed("Type Allowed");
        sessionUserVO.setBatchID("Batch ID");
        sessionUserVO.setBatchName("Batch Name");
        sessionUserVO.setBrowserType("Browser Type");
        sessionUserVO
                .setC2sMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO
                .setC2sMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setCategoryCode("Category Code");
        sessionUserVO.setCategoryCodeDesc("Category Code Desc");

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
        sessionUserVO.setCategoryVO(categoryVO);
        sessionUserVO.setCity("Oxford");
        sessionUserVO.setCompany("Company");
        sessionUserVO.setConfirmPassword("iloveyou");
        sessionUserVO.setContactNo("Contact N0");
        sessionUserVO.setContactPerson("Contact Person");
        sessionUserVO.setCountry("GB");
        sessionUserVO.setCountryCode("GB");
        sessionUserVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        sessionUserVO.setCreatedByUserName("janedoe");
        sessionUserVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setCreatedOnAsString("Jan 1, 2020 8:00am GMT+0100");
        sessionUserVO.setCreated_On("Jan 1, 2020 8:00am GMT+0100");
        sessionUserVO.setCreationType("Creation Type");
        sessionUserVO.setCurrentModule("Current Module");
        sessionUserVO.setCurrentRoleCode("Current Role Code");
        sessionUserVO.setDepartmentCode("Department Code");
        sessionUserVO.setDepartmentDesc("Department Desc");
        sessionUserVO.setDepartmentList(new ArrayList<>());
        sessionUserVO.setDesignation("Designation");
        sessionUserVO.setDivisionCode("Division Code");
        sessionUserVO.setDivisionDesc("Division Desc");
        sessionUserVO.setDivisionList(new ArrayList<>());
        sessionUserVO.setDocumentNo("Document No");
        sessionUserVO.setDocumentType("Document Type");
        sessionUserVO.setDomainCodes(new String[]{"Domain Codes"});
        sessionUserVO.setDomainID("Domain ID");
        sessionUserVO.setDomainList(new ArrayList());
        sessionUserVO.setDomainName("Domain Name");
        sessionUserVO.setDomainStatus("Domain Status");
        sessionUserVO.setDomainTypeCode("Domain Type Code");
        sessionUserVO.setEmail("jane.doe@example.org");
        sessionUserVO.setEmpCode("Emp Code");
        sessionUserVO.setExternalCode("External Code");
        sessionUserVO.setFax("Fax");
        sessionUserVO.setFirstName("Name");
        sessionUserVO.setFromTime("jane.doe@example.org");
        sessionUserVO.setFxedInfoStr("Fxed Info Str");
        sessionUserVO.setGeographicalAreaList(new ArrayList<>());
        sessionUserVO.setGeographicalCode("Geographical Codes");
        sessionUserVO.setGeographicalCodeArray(new String[]{"Geographical Code Arrays"});
        sessionUserVO.setGeographicalCodeStatus("Geographical Code Status");
        sessionUserVO.setGeographicalList(new ArrayList());
        sessionUserVO.setGrphDomainTypeName("Grph Domain Type Names");
        sessionUserVO.setInfo1("Info1");
        sessionUserVO.setInfo10("Info10");
        sessionUserVO.setInfo11("Info11");
        sessionUserVO.setInfo12("Info12");
        sessionUserVO.setInfo13("Info13");
        sessionUserVO.setInfo14("Info14");
        sessionUserVO.setInfo15("Info15");
        sessionUserVO.setInfo2("Info2");
        sessionUserVO.setInfo3("Info3");
        sessionUserVO.setInfo4("Info4");
        sessionUserVO.setInfo5("Info5");
        sessionUserVO.setInfo6("Info6");
        sessionUserVO.setInfo7("Info7");
        sessionUserVO.setInfo8("Info8");
        sessionUserVO.setInfo9("Info9");
        sessionUserVO.setInvalidPasswordCount(3);
        sessionUserVO.setIsSerAssignChnlAdm(true);
        sessionUserVO.setLanguage("en");
        sessionUserVO
                .setLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLastModified(1L);
        sessionUserVO.setLastName("Name");
        sessionUserVO.setLatitude("Latitude");
        sessionUserVO.setLevel1ApprovedBy("Level1 Approved By");
        sessionUserVO
                .setLevel1ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLevel2ApprovedBy("Level2 Approved By");
        sessionUserVO
                .setLevel2ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLoggerMessage("Logger Message");
        sessionUserVO.setLoginID("Login ID");
        sessionUserVO.setLoginTime(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setLongitude("Longitude");
        sessionUserVO.setMenuItemList(new ArrayList());
        sessionUserVO.setMessage("Not all who wander are lost");
        sessionUserVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        sessionUserVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setModuleCodeString("Code String");
        sessionUserVO.setMsisdn("Msisdn");
        sessionUserVO.setMsisdnList(new ArrayList());
        sessionUserVO.setNetworkID("Network ID");
        sessionUserVO.setNetworkList(new ArrayList());
        sessionUserVO.setNetworkName("Network Name");
        sessionUserVO.setNetworkStatus("Network Status");
        sessionUserVO.setOTPValidated(true);
        sessionUserVO
                .setOldLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setOtfCount(3);
        sessionUserVO.setOtfValue(42L);
        sessionUserVO.setOwnerCategoryName("Owner Category Name");
        sessionUserVO.setOwnerCompany("Company");
        sessionUserVO.setOwnerID("Owner ID");
        sessionUserVO.setOwnerMsisdn("Owner Msisdn");
        sessionUserVO.setOwnerName("Owner Name");
        sessionUserVO
                .setP2pMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO
                .setP2pMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setPageCodeString("Code String");
        sessionUserVO.setParentCategoryName("Parent Category Name");
        sessionUserVO.setParentID("Parent ID");
        sessionUserVO.setParentMsisdn("Parent Msisdn");
        sessionUserVO.setParentName("Parent Name");
        sessionUserVO.setPassword("iloveyou");
        sessionUserVO.setPasswordCountUpdatedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO
                .setPasswordModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setPasswordModifyFlag(true);
        sessionUserVO.setPasswordReset("Password Reset");
        sessionUserVO.setPaymentType("Payment Type");
        sessionUserVO.setPaymentTypes("Payment Types");
        sessionUserVO.setPaymentTypes(new String[]{"Payment Types"});
        sessionUserVO.setPaymentTypesList(new ArrayList());
        sessionUserVO.setPinReset("Pin Reset");
        sessionUserVO.setPreviousStatus("Previous Status");
        sessionUserVO.setProductCodes(new String[]{"Product Codess"});
        sessionUserVO.setProductsList(new ArrayList());
        sessionUserVO.setReferenceID("Reference ID");
        sessionUserVO.setRemarks("Remarks");
        sessionUserVO.setRemoteAddress("42 Main St");
        sessionUserVO.setReportHeaderName("Report Header Name");
        sessionUserVO.setRequestType("Request Type");
        sessionUserVO.setRequetedByUserName("janedoe");
        sessionUserVO.setRestrictedMsisdnAllow("Restricted Msisdn Allow");
        sessionUserVO.setRoleFlag(new String[]{"Role Flags"});
        sessionUserVO.setRoleType("Role Types");
        sessionUserVO.setRolesMap(new HashMap());
        sessionUserVO.setRolesMapSelected(new HashMap());
        sessionUserVO.setRsaAllowed(true);
        sessionUserVO.setRsaFlag("Rsa Flag");
        sessionUserVO.setRsaRequired(true);
        sessionUserVO.setRsavalidated(true);
        sessionUserVO.setSegmentList(new ArrayList());
        sessionUserVO.setServiceList(new ArrayList());
        sessionUserVO.setServicesList(new ArrayList());
        sessionUserVO.setServicesTypes(new String[]{"Services Typess"});

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
        sessionUserVO.setSessionInfoVO(sessionInfoVO);
        sessionUserVO.setShortName("Short Name");
        sessionUserVO.setShowPassword("iloveyou");
        sessionUserVO.setSsn("123-45-678");
        sessionUserVO.setStaffUser(true);
        sessionUserVO.setStaffUserDetails(ChannelUserVO.getInstance());
        sessionUserVO.setState("MD");
        sessionUserVO.setStatus("Status");
        sessionUserVO.setStatusDesc("Status Desc");
        sessionUserVO.setStatusList(new ArrayList());
        sessionUserVO.setSuspendedByUserName("janedoe");
        sessionUserVO
                .setSuspendedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setToTime("To Time");
        sessionUserVO.setUpdateSimRequired(true);
        sessionUserVO.setUserBalanceList(new ArrayList<>());
        sessionUserVO.setUserCode("User Code");
        sessionUserVO.setUserID("User ID");
        sessionUserVO.setUserLanguage("en");
        sessionUserVO.setUserLanguageDesc("en");
        sessionUserVO.setUserLanguageList(new ArrayList());
        sessionUserVO.setUserLoanVOList(new ArrayList<>());
        sessionUserVO.setUserName("janedoe");
        sessionUserVO.setUserNamePrefix("janedoe");
        sessionUserVO.setUserNamePrefixCode("janedoe");
        sessionUserVO.setUserNamePrefixDesc("janedoe");
        sessionUserVO.setUserNamePrefixList(new ArrayList());
        sessionUserVO.setUserPhoneVO(UserPhoneVO.getInstance());
        sessionUserVO.setUserType("User Type");
        sessionUserVO.setUsingNewSTK(true);
        sessionUserVO.setValidRequestURLs("https://example.org/example");
        sessionUserVO.setValidStatus(1);
        sessionUserVO.setVoucherList(new ArrayList());
        sessionUserVO.setWebLoginID(" web Login ID");

        UpdateSystemPreferencesRequestVO requestVO = new UpdateSystemPreferencesRequestVO();
        requestVO.setPreferenceUpdateList(new ArrayList<>());

        // Act
        preferencesServiceImpl.updateSystemPreferences(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), response, response1, sessionUserVO, requestVO);
    }
}

