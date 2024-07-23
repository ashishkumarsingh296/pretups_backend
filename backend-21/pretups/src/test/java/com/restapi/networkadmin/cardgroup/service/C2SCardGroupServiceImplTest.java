/* jdk21
package com.restapi.networkadmin.cardgroup.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.XssWrapper;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.cardgroup.requestVO.C2SAddCardGroupSaveRequestVO;
import com.restapi.networkadmin.cardgroup.requestVO.CardGroupDetailsRequestVO;
import com.restapi.networkadmin.cardgroup.responseVO.C2SCardGroupSetNameListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadC2SCardGroupVersionListResponseVO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {C2SCardGroupServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SCardGroupServiceImplTest {
    @Autowired
    private C2SCardGroupServiceImpl c2SCardGroupServiceImpl;

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#saveC2SCardGroup(Connection, HttpServletRequest, UserVO, C2SAddCardGroupSaveRequestVO)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testSaveC2SCardGroup() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO.isCardGroupSetNameExist(CardGroupSetDAO.java:437)
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.saveC2SCardGroup(C2SCardGroupServiceImpl.java:348)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SCardGroupServiceImpl c2sCardGroupServiceImpl = new C2SCardGroupServiceImpl();
        */
/*PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doThrow(new SQLException()).when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

     XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        UserVO userVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userVO.getNetworkID()).thenReturn("Network ID");
        C2SAddCardGroupSaveRequestVO requestVO = mock(C2SAddCardGroupSaveRequestVO.class);
        when(requestVO.getCardGroupSetName()).thenReturn("Card Group Set Name");
        when(requestVO.getCardGroupList()).thenReturn(new ArrayList<>());
        c2sCardGroupServiceImpl.saveC2SCardGroup(com.btsl.util.JUnitConfig.getConnection(), request,null ,userVO, requestVO);
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#addTempList(Connection, HttpServletRequest, UserVO, CardGroupDetailsRequestVO)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddTempList() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.addTempList(C2SCardGroupServiceImpl.java:1066)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));

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
        categoryVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        categoryVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setAppointmentDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssType("Ass Type");
        userVO.setAssoMsisdn("Asso Msisdn");
        userVO.setAssociatedGeographicalList(new ArrayList());
        userVO.setAssociatedProductTypeList(new ArrayList());
        userVO.setAssociatedServiceTypeList(new ArrayList());
        userVO.setAssociationCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssociationModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAuthType("Type");
        userVO.setAuthTypeAllowed("Type Allowed");
        userVO.setBatchID("Batch ID");
        userVO.setBatchName("Batch Name");
        userVO.setBrowserType("Browser Type");
        userVO.setC2sMisFromDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setC2sMisToDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setLastLoginOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLastModified(1L);
        userVO.setLastName("Name");
        userVO.setLatitude("Latitude");
        userVO.setLevel1ApprovedBy("Level1 Approved By");
        userVO.setLevel1ApprovedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLevel2ApprovedBy("Level2 Approved By");
        userVO.setLevel2ApprovedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLoggerMessage("Logger Message");
        userVO.setLoginID("Login ID");
        userVO.setLoginTime(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLongitude("Longitude");
        userVO.setMenuItemList(new ArrayList());
        userVO.setMessage("Not all who wander are lost");
        userVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        userVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setModuleCodeString("Code String");
        userVO.setMsisdn("Msisdn");
        userVO.setMsisdnList(new ArrayList());
        userVO.setNetworkID("Network ID");
        userVO.setNetworkList(new ArrayList());
        userVO.setNetworkName("Network Name");
        userVO.setNetworkStatus("Network Status");
        userVO.setOTPValidated(true);
        userVO.setOldLastLoginOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setOtfCount(3);
        userVO.setOtfValue(42L);
        userVO.setOwnerCategoryName("Owner Category Name");
        userVO.setOwnerCompany("Company");
        userVO.setOwnerID("Owner ID");
        userVO.setOwnerMsisdn("Owner Msisdn");
        userVO.setOwnerName("Owner Name");
        userVO.setP2pMisFromDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setP2pMisToDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPageCodeString("Code String");
        userVO.setParentCategoryName("Parent Category Name");
        userVO.setParentID("Parent ID");
        userVO.setParentMsisdn("Parent Msisdn");
        userVO.setParentName("Parent Name");
        userVO.setPassword("iloveyou");
        userVO.setPasswordCountUpdatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPasswordModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setSuspendedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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

        CardGroupDetailsRequestVO requestVO = new CardGroupDetailsRequestVO();
        requestVO.setBonusAccList(new ArrayList());
        requestVO.setBonusTalktimevalidity(1L);
        requestVO.setBonusValidityValue(1L);
        requestVO.setBoth("Both");
        requestVO.setCardGroupCode("Card Group Code");
        requestVO.setCardGroupID("Card Group ID");
        requestVO.setCardGroupList(new ArrayList<>());
        requestVO.setCardGroupSetID("Card Group Set ID");
        requestVO.setCardGroupSetName("Card Group Set Name");
        requestVO.setCardGroupSubServiceID("Card Group Sub Service ID");
        requestVO.setCardGroupSubServiceIdDesc("Card Group Sub Service Id Desc");
        requestVO.setCardGroupType("Card Group Type");
        requestVO.setCardName("Card Name");
        requestVO.setCosRequired("Cos Required");
        requestVO.setEditDetail("Edit Detail");
        requestVO.setEndRange(1L);
        requestVO.setEndRangeAsString("End Range As String");
        requestVO.setGracePeriod(1L);
        requestVO.setInPromo(10.0d);
        requestVO.setInPromoAsString("In Promo As String");
        requestVO.setLastVersion("1.0.2");
        requestVO.setLocationIndex(1);
        requestVO.setMaxReceiverAccessFee(1L);
        requestVO.setMaxReceiverAccessFeeAsString("Max Receiver Access Fee As String");
        requestVO.setMinReceiverAccessFee(1L);
        requestVO.setMinReceiverAccessFeeAsString("Min Receiver Access Fee As String");
        requestVO.setMultipleOf(1L);
        requestVO.setMultipleOfAsString("Multiple Of As String");
        requestVO.setNetworkCode("Network Code");
        requestVO.setOnline("Online");
        requestVO.setReceiverAccessFeeRate(10.0d);
        requestVO.setReceiverAccessFeeRateAsString("Receiver Access Fee Rate As String");
        requestVO.setReceiverAccessFeeType("Receiver Access Fee Type");
        requestVO.setReceiverConvFactor("Receiver Conv Factor");
        requestVO.setReceiverTax1Name("Receiver Tax1 Name");
        requestVO.setReceiverTax1Rate(10.0d);
        requestVO.setReceiverTax1RateAsString("Receiver Tax1 Rate As String");
        requestVO.setReceiverTax1Type("Receiver Tax1 Type");
        requestVO.setReceiverTax2Name("Receiver Tax2 Name");
        requestVO.setReceiverTax2Rate(10.0d);
        requestVO.setReceiverTax2RateAsString("Receiver Tax2 Rate As String");
        requestVO.setReceiverTax2Type("Receiver Tax2 Type");
        requestVO.setReversalModifiedDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        requestVO.setReversalModifiedDateAsString("2020-03-01");
        requestVO.setReversalPermitted("Reversal Permitted");
        requestVO.setRowIndex(1);
        requestVO.setServiceTypeDesc("Service Type Desc");
        requestVO.setServiceTypeId("42");
        requestVO.setSetType("Set Type");
        requestVO.setSetTypeName("Set Type Name");
        requestVO.setStartRange(1L);
        requestVO.setStartRangeAsString("Start Range As String");
        requestVO.setStatus("Status");
        requestVO.setTempAccList(new ArrayList<>());
        requestVO.setValidityPeriod(1);
        requestVO.setValidityPeriodAsString("Validity Period As String");
        requestVO.setValidityPeriodType("Validity Period Type");
        requestVO.setValidityPeriodTypeDesc("Validity Period Type Desc");
        requestVO.setVersion("1.0.2");
        c2SCardGroupServiceImpl.addTempList(com.btsl.util.JUnitConfig.getConnection(), request, userVO, requestVO);
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#loadC2SCardGroupSetNameList(Connection, String, String, String)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadC2SCardGroupSetNameList() throws BTSLBaseException, SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 1
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.loadC2SCardGroupSetNameList(C2SCardGroupServiceImpl.java:1376)
        //   See https://diff.blue/R013 to resolve this issue.

        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(Date.class));
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

        c2SCardGroupServiceImpl.loadC2SCardGroupSetNameList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "Service Type", "Subservice Type");
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#loadC2SCardGroupSetNameList(Connection, String, String, String)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadC2SCardGroupSetNameList2() throws BTSLBaseException, SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 1
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.loadC2SCardGroupSetNameList(C2SCardGroupServiceImpl.java:1376)
        //   See https://diff.blue/R013 to resolve this issue.

        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(Date.class));
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();
        c2SCardGroupServiceImpl.loadC2SCardGroupSetNameList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "Service Type", "Subservice Type");
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#loadC2SCardGroupSetNameList(Connection, String, String, String)}
     *//*

    @Test
    public void testLoadC2SCardGroupSetNameList3() throws BTSLBaseException, SQLException {
        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

        C2SCardGroupSetNameListResponseVO actualLoadC2SCardGroupSetNameListResult = c2SCardGroupServiceImpl
                .loadC2SCardGroupSetNameList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "Service Type", "Subservice Type");
        assertTrue(actualLoadC2SCardGroupSetNameListResult.getCardGroupSetNameList().isEmpty());
        assertNull(actualLoadC2SCardGroupSetNameListResult.getCurrentDefaultCardGroup());
       */
/* verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp, atLeast(1)).getTime();*//*

    }


    @Test
    public void testLoadVersionList() throws Exception {
        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

        assertTrue(c2SCardGroupServiceImpl
                .loadVersionList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "42",
                        java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()))
                .getCardGroupSetVersionList()
                .isEmpty());
    */
/*    verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();*//*

        */
/*verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp, atLeast(1)).getTime();*//*

    }


    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadVersionList2() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: 2002
        //       at com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO.loadCardGroupDetailsListByID(CardGroupDAO.java:928)
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.loadVersionList(C2SCardGroupServiceImpl.java:1452)
        //   See https://diff.blue/R013 to resolve this issue.

        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("42");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

        c2SCardGroupServiceImpl.loadVersionList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "42",
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    }


    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadVersionList3() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.loadVersionList(C2SCardGroupServiceImpl.java:1449)
        //   See https://diff.blue/R013 to resolve this issue.

*/
/*
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
*//*

        //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

        c2SCardGroupServiceImpl.loadVersionList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "42",
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#loadVersionList(Connection, String, String, java.util.Date)}
     *//*

    @Test
    public void testLoadVersionList4() throws Exception {
        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(java.sql.Date.class));
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

        c2SCardGroupServiceImpl.loadVersionList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "42",
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
*/
/*
        verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getDate(Mockito.<String>any());
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
*//*

    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#loadVersionList(Connection, String, String, java.util.Date)}
     *//*

    @Test
    public void testLoadVersionList5() throws Exception {
        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(java.sql.Date.class));
        when(resultSet.getString(Mockito.<String>any())).thenReturn("42");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

     com.btsl.util.JUnitConfig.init();

        LoadC2SCardGroupVersionListResponseVO actualLoadVersionListResult = c2SCardGroupServiceImpl.loadVersionList(com.btsl.util.JUnitConfig.getConnection(),
                "Network ID", "42",
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        assertEquals("42", actualLoadVersionListResult.getCardGroupSetName());
        assertEquals("42", actualLoadVersionListResult.getSetTypeName());
        assertEquals("42", actualLoadVersionListResult.getServiceTypedesc());
        assertEquals("42", actualLoadVersionListResult.getDefaultCardGroupRequired());
        assertEquals("42", actualLoadVersionListResult.getCardGroupSubServiceName());
        assertEquals("42", actualLoadVersionListResult.getCardGroupSetstatus());
        */
/*verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getDate(Mockito.<String>any());
        verify(resultSet).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();*//*

    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#loadVersionList(Connection, String, String, java.util.Date)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadVersionList6() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.loadVersionList(C2SCardGroupServiceImpl.java:1428)
        //   See https://diff.blue/R013 to resolve this issue.
*/
/*
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(java.sql.Date.class));
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).setTimestamp(anyInt(), Mockito.<Timestamp>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

     com.btsl.util.JUnitConfig.init();

        c2SCardGroupServiceImpl.loadVersionList(com.btsl.util.JUnitConfig.getConnection(), "Network ID", "42",
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#viewC2SCardGroupDetails(Connection, String, ArrayList, String, String)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewC2SCardGroupDetails() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 1
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.viewC2SCardGroupDetails(C2SCardGroupServiceImpl.java:1570)
        //   See https://diff.blue/R013 to resolve this issue.

        */
/*Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(Date.class));
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        *//*
//Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     com.btsl.util.JUnitConfig.init();

     c2SCardGroupServiceImpl.viewC2SCardGroupDetails(com.btsl.util.JUnitConfig.getConnection(), "Network ID", new ArrayList(), "42", "42");
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#checkDeleteVersionAllowed(String, String, StringBuffer)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testCheckDeleteVersionAllowed() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.checkDeleteVersionAllowed(C2SCardGroupServiceImpl.java:1634)

        // Arrange
        // TODO: Populate arranged inputs
        String p_cardGroupSetID = "";
        String p_moduleCode = "";
        StringBuffer strBuff = null;

        // Act
        boolean actualCheckDeleteVersionAllowedResult = this.c2SCardGroupServiceImpl
                .checkDeleteVersionAllowed(p_cardGroupSetID, p_moduleCode, strBuff);

        // Assert
        // TODO: Add assertions on result
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#addModifyC2SCardGroupTempList(Connection, HttpServletRequest, UserVO, CardGroupDetailsRequestVO)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddModifyC2SCardGroupTempList() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.cardgroup.service.C2SCardGroupServiceImpl.addModifyC2SCardGroupTempList(C2SCardGroupServiceImpl.java:1774)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        XssWrapper httpServletRequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));

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
        categoryVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        categoryVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setAppointmentDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssType("Ass Type");
        userVO.setAssoMsisdn("Asso Msisdn");
        userVO.setAssociatedGeographicalList(new ArrayList());
        userVO.setAssociatedProductTypeList(new ArrayList());
        userVO.setAssociatedServiceTypeList(new ArrayList());
        userVO.setAssociationCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssociationModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAuthType("Type");
        userVO.setAuthTypeAllowed("Type Allowed");
        userVO.setBatchID("Batch ID");
        userVO.setBatchName("Batch Name");
        userVO.setBrowserType("Browser Type");
        userVO.setC2sMisFromDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setC2sMisToDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setLastLoginOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLastModified(1L);
        userVO.setLastName("Name");
        userVO.setLatitude("Latitude");
        userVO.setLevel1ApprovedBy("Level1 Approved By");
        userVO.setLevel1ApprovedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLevel2ApprovedBy("Level2 Approved By");
        userVO.setLevel2ApprovedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLoggerMessage("Logger Message");
        userVO.setLoginID("Login ID");
        userVO.setLoginTime(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLongitude("Longitude");
        userVO.setMenuItemList(new ArrayList());
        userVO.setMessage("Not all who wander are lost");
        userVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        userVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setModuleCodeString("Code String");
        userVO.setMsisdn("Msisdn");
        userVO.setMsisdnList(new ArrayList());
        userVO.setNetworkID("Network ID");
        userVO.setNetworkList(new ArrayList());
        userVO.setNetworkName("Network Name");
        userVO.setNetworkStatus("Network Status");
        userVO.setOTPValidated(true);
        userVO.setOldLastLoginOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setOtfCount(3);
        userVO.setOtfValue(42L);
        userVO.setOwnerCategoryName("Owner Category Name");
        userVO.setOwnerCompany("Company");
        userVO.setOwnerID("Owner ID");
        userVO.setOwnerMsisdn("Owner Msisdn");
        userVO.setOwnerName("Owner Name");
        userVO.setP2pMisFromDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setP2pMisToDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPageCodeString("Code String");
        userVO.setParentCategoryName("Parent Category Name");
        userVO.setParentID("Parent ID");
        userVO.setParentMsisdn("Parent Msisdn");
        userVO.setParentName("Parent Name");
        userVO.setPassword("iloveyou");
        userVO.setPasswordCountUpdatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPasswordModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setSuspendedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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

        CardGroupDetailsRequestVO requestVO = new CardGroupDetailsRequestVO();
        requestVO.setBonusAccList(new ArrayList());
        requestVO.setBonusTalktimevalidity(1L);
        requestVO.setBonusValidityValue(1L);
        requestVO.setBoth("Both");
        requestVO.setCardGroupCode("Card Group Code");
        requestVO.setCardGroupID("Card Group ID");
        requestVO.setCardGroupList(new ArrayList<>());
        requestVO.setCardGroupSetID("Card Group Set ID");
        requestVO.setCardGroupSetName("Card Group Set Name");
        requestVO.setCardGroupSubServiceID("Card Group Sub Service ID");
        requestVO.setCardGroupSubServiceIdDesc("Card Group Sub Service Id Desc");
        requestVO.setCardGroupType("Card Group Type");
        requestVO.setCardName("Card Name");
        requestVO.setCosRequired("Cos Required");
        requestVO.setEditDetail("Edit Detail");
        requestVO.setEndRange(1L);
        requestVO.setEndRangeAsString("End Range As String");
        requestVO.setGracePeriod(1L);
        requestVO.setInPromo(10.0d);
        requestVO.setInPromoAsString("In Promo As String");
        requestVO.setLastVersion("1.0.2");
        requestVO.setLocationIndex(1);
        requestVO.setMaxReceiverAccessFee(1L);
        requestVO.setMaxReceiverAccessFeeAsString("Max Receiver Access Fee As String");
        requestVO.setMinReceiverAccessFee(1L);
        requestVO.setMinReceiverAccessFeeAsString("Min Receiver Access Fee As String");
        requestVO.setMultipleOf(1L);
        requestVO.setMultipleOfAsString("Multiple Of As String");
        requestVO.setNetworkCode("Network Code");
        requestVO.setOnline("Online");
        requestVO.setReceiverAccessFeeRate(10.0d);
        requestVO.setReceiverAccessFeeRateAsString("Receiver Access Fee Rate As String");
        requestVO.setReceiverAccessFeeType("Receiver Access Fee Type");
        requestVO.setReceiverConvFactor("Receiver Conv Factor");
        requestVO.setReceiverTax1Name("Receiver Tax1 Name");
        requestVO.setReceiverTax1Rate(10.0d);
        requestVO.setReceiverTax1RateAsString("Receiver Tax1 Rate As String");
        requestVO.setReceiverTax1Type("Receiver Tax1 Type");
        requestVO.setReceiverTax2Name("Receiver Tax2 Name");
        requestVO.setReceiverTax2Rate(10.0d);
        requestVO.setReceiverTax2RateAsString("Receiver Tax2 Rate As String");
        requestVO.setReceiverTax2Type("Receiver Tax2 Type");
        requestVO.setReversalModifiedDate(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        requestVO.setReversalModifiedDateAsString("2020-03-01");
        requestVO.setReversalPermitted("Reversal Permitted");
        requestVO.setRowIndex(1);
        requestVO.setServiceTypeDesc("Service Type Desc");
        requestVO.setServiceTypeId("42");
        requestVO.setSetType("Set Type");
        requestVO.setSetTypeName("Set Type Name");
        requestVO.setStartRange(1L);
        requestVO.setStartRangeAsString("Start Range As String");
        requestVO.setStatus("Status");
        requestVO.setTempAccList(new ArrayList<>());
        requestVO.setValidityPeriod(1);
        requestVO.setValidityPeriodAsString("Validity Period As String");
        requestVO.setValidityPeriodType("Validity Period Type");
        requestVO.setValidityPeriodTypeDesc("Validity Period Type Desc");
        requestVO.setVersion("1.0.2");
        c2SCardGroupServiceImpl.addModifyC2SCardGroupTempList(com.btsl.util.JUnitConfig.getConnection(), httpServletRequest, userVO, requestVO);
    }

    */
/**
     * Method under test: {@link C2SCardGroupServiceImpl#deleteTempC2SCardGroupList(Connection, MComConnectionI, String, String, String, String)}
     *//*

    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteTempC2SCardGroupList() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        //Connection con = null;
     com.btsl.util.JUnitConfig.init();

      //  MComConnectionI mcomCon = null;
        String selectCardGroupSetId = "";
        String version = "";
        String oldApplicableFromDate = "";
        String oldApplicableFromHour = "";

        // Act
        Integer actualDeleteTempC2SCardGroupListResult = this.c2SCardGroupServiceImpl.deleteTempC2SCardGroupList(com.btsl.util.JUnitConfig.getConnection(),
                com.btsl.util.JUnitConfig.getMComConnection(), selectCardGroupSetId, version, oldApplicableFromDate, oldApplicableFromHour);

        // Assert
        // TODO: Add assertions on result
    }
}

*/
