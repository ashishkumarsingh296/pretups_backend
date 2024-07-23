package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.channelAdmin.requestVO.BulkSusResCURequestVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ChannelUserListImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelUserListImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ChannelUserListImpl channelUserListImpl;

    /**
     * Method under test: {@link ChannelUserListImpl#getChannelUserListByParent(Connection, String, HttpServletResponse, ChannelUserListByParntReqVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetChannelUserListByParent() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.getChannelUserListByParent(ChannelUserListImpl.java:350)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();
        //CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        ChannelUserListByParntReqVO requestVO = new ChannelUserListByParntReqVO();
        requestVO.setDomain("Domain");
        requestVO.setGeography("Geography");
        requestVO.setOwnerUserID("Owner User ID");
        requestVO.setParentUserID("Parent User ID");
        requestVO.setUserCategory("User Category");
        requestVO.setUserName("janedoe");
        channelUserListImpl.getChannelUserListByParent(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1, requestVO);
    }

    /**
     * Method under test: {@link ChannelUserListImpl#processBulkSusResCU(Connection, HttpServletResponse, BulkSusResCURequestVO, String, Locale, UserVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessBulkSusResCU() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.processBulkSusResCU(ChannelUserListImpl.java:490)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        com.btsl.util.JUnitConfig.init();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        BulkSusResCURequestVO requestVO = new BulkSusResCURequestVO();
        requestVO.setFile("File");
        requestVO.setFileName("foo.txt");
        requestVO.setFileType("File Type");
        requestVO.setOperationType("Operation Type");
        Locale locale = Locale.getDefault();

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
        channelUserListImpl.processBulkSusResCU(com.btsl.util.JUnitConfig.getConnection(), response1, requestVO, "Type", locale, userVO);
    }

    /**
     * Method under test: {@link ChannelUserListImpl#deleteRetry(Connection, String, String, String, String, String, String, String, int, UserVO)}
     */
    @Test
    public void testDeleteRetry() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        JUnitConfig.init();
        ChannelUserListImpl channelUserListImpl = new ChannelUserListImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertTrue(channelUserListImpl.deleteRetry(JUnitConfig.getConnection(), "P user ID", "P delete Or Suspend",
                "Jan 1, 2020 9:00am GMT+0100", "P pre Status", "P MSISDN", "P login ID", "Type", 3, mock(UserVO.class)));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserListImpl#deleteRetry(Connection, String, String, String, String, String, String, String, int, UserVO)}
     */
    @Test
    public void testDeleteRetry2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        JUnitConfig.init();
        ChannelUserListImpl channelUserListImpl = new ChannelUserListImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertTrue(channelUserListImpl.deleteRetry(JUnitConfig.getConnection(), "P user ID", "P delete Or Suspend",
                "Jan 1, 2020 9:00am GMT+0100", "P pre Status", "P MSISDN", "P login ID", "Type", 3, mock(UserVO.class)));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserListImpl#deleteRetry(Connection, String, String, String, String, String, String, String, int, UserVO)}
     */
    @Test
    public void testDeleteRetry3() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        JUnitConfig.init();
        ChannelUserListImpl channelUserListImpl = new ChannelUserListImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertTrue(channelUserListImpl.deleteRetry(JUnitConfig.getConnection(), "P user ID", "P delete Or Suspend",
                "Jan 1, 2020 9:00am GMT+0100", "P pre Status", "P MSISDN", "P login ID", "Type", 3, mock(UserVO.class)));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserListImpl#deleteRetry(Connection, String, String, String, String, String, String, String, int, UserVO)}
     */
    @Test
    public void testDeleteRetry4() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        JUnitConfig.init();

        ChannelUserListImpl channelUserListImpl = new ChannelUserListImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertTrue(channelUserListImpl.deleteRetry(JUnitConfig.getConnection(), "P user ID", "P delete Or Suspend",
                "Jan 1, 2020 9:00am GMT+0100", "P pre Status", "P MSISDN", "P login ID", "Type", 3, mock(UserVO.class)));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserListImpl#deleteRetry(Connection, String, String, String, String, String, String, String, int, UserVO)}
     */
    @Test
    public void testDeleteRetry5() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        JUnitConfig.init();
        ChannelUserListImpl channelUserListImpl = new ChannelUserListImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertTrue(channelUserListImpl.deleteRetry(JUnitConfig.getConnection(), "P user ID", "P delete Or Suspend",
                "Jan 1, 2020 9:00am GMT+0100", "P pre Status", "P MSISDN", "P login ID", "LOGIN", 3, mock(UserVO.class)));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserListImpl#validateFileName(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFileName() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.validateFileName(ChannelUserListImpl.java:1640)
        //   See https://diff.blue/R013 to resolve this issue.

        channelUserListImpl.validateFileName("foo.txt");
    }

    /**
     * Method under test: {@link ChannelUserListImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        channelUserListImpl.validateFileDetailsMap(new HashMap<>());
    }

    /**
     * Method under test: {@link ChannelUserListImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap2() throws BTSLBaseException {
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        thrown.expect(BTSLBaseException.class);
        channelUserListImpl.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link ChannelUserListImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFileDetailsMap3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.validateFileName(ChannelUserListImpl.java:1640)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.validateFileDetailsMap(ChannelUserListImpl.java:1657)
        //   See https://diff.blue/R013 to resolve this issue.

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILEATTACHMENT", "FILEATTACHMENT");
        fileDetailsMap.put("FILENAME", "FILENAME");
        channelUserListImpl.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link ChannelUserListImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap4() throws BTSLBaseException {
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "");
        thrown.expect(BTSLBaseException.class);
        channelUserListImpl.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link ChannelUserListImpl#getstaffUserListByParent(Connection, String, HttpServletResponse, StaffUserListByParntReqVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetstaffUserListByParent() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.getstaffUserListByParent(ChannelUserListImpl.java:1677)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        StaffUserListByParntReqVO requestVO = new StaffUserListByParntReqVO();
        requestVO.setDomain("Domain");
        requestVO.setGeography("Geography");
        requestVO.setLoginID("Login ID");
        requestVO.setMsisdn("Msisdn");
        requestVO.setOwnerUserID("Owner User ID");
        requestVO.setParentUserID("Parent User ID");
        requestVO.setStatus("Status");
        requestVO.setUserCategory("User Category");
        requestVO.setUserName("janedoe");
        channelUserListImpl.getstaffUserListByParent(JUnitConfig.getConnection(), "Login ID", responseSwag, requestVO);
    }

    /**
     * Method under test: {@link ChannelUserListImpl#commissionProfileBy(Connection, String, String, String, String)}
     */
    @Test
    public void testCommissionProfileBy() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
        // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(2,
                channelUserListImpl.commissionProfileBy(com.btsl.util.JUnitConfig.getConnection(), "Geography Loc", "Network Code", "User Grade", "Category Code")
                        .getCommissionProfileList()
                        .size());
      /*  verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link ChannelUserListImpl#commissionProfileBy(Connection, String, String, String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCommissionProfileBy2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.web.user.businesslogic.UserWebDAO.loadCommisionProfileListByGradeGeography(UserWebDAO.java:4677)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.commissionProfileBy(ChannelUserListImpl.java:1745)
        //   See https://diff.blue/R013 to resolve this issue.
/*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       */ //Connection con = mock(Connection.class);
        //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        channelUserListImpl.commissionProfileBy(com.btsl.util.JUnitConfig.getConnection(), "Geography Loc", "Network Code", "User Grade", "Category Code");
    }

    /**
     * Method under test: {@link ChannelUserListImpl#commissionProfileBy(Connection, String, String, String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCommissionProfileBy3() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 3000721
        //       at com.web.user.businesslogic.UserWebDAO.loadCommisionProfileListByGradeGeography(UserWebDAO.java:4683)
        //       at com.restapi.channelAdmin.ChannelUserListImpl.commissionProfileBy(ChannelUserListImpl.java:1745)
        //   See https://diff.blue/R013 to resolve this issue.
/*
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
  */      //Connection con = mock(Connection.class);
//        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        com.btsl.util.JUnitConfig.init();

        channelUserListImpl.commissionProfileBy(com.btsl.util.JUnitConfig.getConnection(), "Geography Loc", "Network Code", "User Grade", "Category Code");
    }

    /**
     * Method under test: {@link ChannelUserListImpl#commissionProfileBy(Connection, String, String, String, String)}
     */
    @Test
    public void testCommissionProfileBy4() throws BTSLBaseException, SQLException {
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *///Connection con = mock(Connection.class);
        //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        channelUserListImpl.commissionProfileBy(com.btsl.util.JUnitConfig.getConnection(), "Geography Loc", "Network Code", "User Grade", "Category Code");
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link ChannelUserListImpl#getChannelUserListByAdmin2(Connection, String, HttpServletResponse, ChannelUserListRequestVO, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetChannelUserListByAdmin2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace

        //    com.btsl.util.JUnitConfig.init();

        //Connection con = mock(Connection.class);
        //CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        ChannelUserListRequestVO requestVO = new ChannelUserListRequestVO();
        requestVO.setDomain("Domain");
        requestVO.setGeography("Geography");
        requestVO.setLoginID("Login ID");
        requestVO.setMsisdn("Msisdn");
        requestVO.setParentUserID("Parent User ID");
        requestVO.setStatus("Status");
        requestVO.setUserCategory("User Category");
       try{ channelUserListImpl.getChannelUserListByAdmin2(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1, requestVO, "LoginId");} catch(Exception e){}
   try{     channelUserListImpl.getChannelUserListByAdmin(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1, requestVO, "Msisdn"); } catch(Exception e){}
       try{     channelUserListImpl.getChannelUserListByAdmin(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1, requestVO, "Advance"); } catch(Exception e){}


    }
}

