package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.iat.util.IATCommonUtil;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ChannelUserTransferServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelUserTransferServiceImplTest {
    @Autowired
    private ChannelUserTransferServiceImpl channelUserTransferServiceImpl;

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#sendOtp(OperatorUtilI, BaseResponse, HttpServletResponse, ChannelUserTransferOtpRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendOtp() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserTransferServiceImpl.sendOtp(ChannelUserTransferServiceImpl.java:68)
        //   See https://diff.blue/R013 to resolve this issue.

        IATCommonUtil operatorUtili = new IATCommonUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        ChannelUserTransferOtpRequestVO requestVO = new ChannelUserTransferOtpRequestVO();
        requestVO.setMode("Mode");
        requestVO.setMsisdn("Msisdn");
        requestVO.setReSend("Re Send");
        channelUserTransferServiceImpl.sendOtp(operatorUtili, response, responseSwag, requestVO);
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#sendOtp(OperatorUtilI, BaseResponse, HttpServletResponse, ChannelUserTransferOtpRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendOtp2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserTransferServiceImpl.sendOtp(ChannelUserTransferServiceImpl.java:68)
        //   See https://diff.blue/R013 to resolve this issue.

        OperatorUtil operatorUtili = new OperatorUtil();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        ChannelUserTransferOtpRequestVO requestVO = new ChannelUserTransferOtpRequestVO();
        requestVO.setMode("Mode");
        requestVO.setMsisdn("Msisdn");
        requestVO.setReSend("Re Send");
        channelUserTransferServiceImpl.sendOtp(operatorUtili, response, responseSwag, requestVO);
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#sendOtp(OperatorUtilI, BaseResponse, HttpServletResponse, ChannelUserTransferOtpRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendOtp3() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserTransferServiceImpl.sendOtp(ChannelUserTransferServiceImpl.java:68)
        //   See https://diff.blue/R013 to resolve this issue.

        OperatorUtilI operatorUtili = mock(OperatorUtilI.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());

        ChannelUserTransferOtpRequestVO requestVO = new ChannelUserTransferOtpRequestVO();
        requestVO.setMode("Mode");
        requestVO.setMsisdn("Msisdn");
        requestVO.setReSend("Re Send");
        channelUserTransferServiceImpl.sendOtp(operatorUtili, response, responseSwag, requestVO);
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#confirmTransferUser(Connection, MComConnectionI, ChannelUserVO, UserVO, UserVO, ConfimChannelUserTransferRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConfirmTransferUser() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = JUnitConfig.getChannelUserVO();
        UserVO userVO = JUnitConfig.getUserVO();;
        UserVO sessionUserVO = JUnitConfig.getUserVO();
        ConfimChannelUserTransferRequestVO requestVO = null;

        // Act
        BaseResponse actualConfirmTransferUserResult = this.channelUserTransferServiceImpl.confirmTransferUser (com.btsl.util.JUnitConfig.getConnection(),
                JUnitConfig.getMComConnection(), channelUserVO, userVO, sessionUserVO, requestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#createTransferVO(Connection, ChannelUserVO, ChannelUserTransferVO, UserVO, ConfimChannelUserTransferRequestVO, UserVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCreateTransferVO() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //       at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        //       at java.util.ArrayList.get(ArrayList.java:435)
        //       at com.restapi.channeluser.service.ChannelUserTransferServiceImpl.createTransferVO(ChannelUserTransferServiceImpl.java:387)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance();

        ChannelUserTransferVO channelUserTransferVO = new ChannelUserTransferVO();
        channelUserTransferVO.setCategoryName("Name");
        channelUserTransferVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        channelUserTransferVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        channelUserTransferVO.setDomainCode("Domain Code");
        channelUserTransferVO.setDomainCodeDesc("Domain Code Desc");
        channelUserTransferVO.setDomainList(new ArrayList());
        channelUserTransferVO.setDomainName("Name");
        channelUserTransferVO.setFromOwnerID("Owner ID");
        channelUserTransferVO.setFromParentID("Parent ID");
        channelUserTransferVO.setGeographicalCode("User Geographical Code");
        channelUserTransferVO.setInvalidOtpCount(1);
        channelUserTransferVO.setIsOperationNotAllow(true);
        channelUserTransferVO.setLastModifiedTime(1L);
        channelUserTransferVO.setLoginId("42");
        channelUserTransferVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        channelUserTransferVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        channelUserTransferVO.setMsisdn("Msisdn");
        channelUserTransferVO.setMultibox("Multibox");
        channelUserTransferVO.setNetworkCode("Network Code");
        channelUserTransferVO.setOtp(" otp");
        channelUserTransferVO.setParentUserID("Parent User ID");
        channelUserTransferVO.setParentUserName("janedoe");
        channelUserTransferVO.setServiceType("Type");
        channelUserTransferVO.setStatus("Status");
        channelUserTransferVO.setToOwnerID("To Owner ID");
        channelUserTransferVO.setToParentID("To Parent ID");
        channelUserTransferVO.setToParentUserID("To Parent User ID");
        channelUserTransferVO.setToParentUserName("janedoe");
        channelUserTransferVO.setUserCategoryCode("Category Code");
        channelUserTransferVO.setUserCategoryDesc("User Category Desc");
        channelUserTransferVO.setUserHierarchyList(new ArrayList());
        channelUserTransferVO.setUserID("User ID");
        channelUserTransferVO.setUserName("janedoe");
        channelUserTransferVO.setZoneCode("Zone Code");
        channelUserTransferVO.setZoneName("Name");

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

        UserVO sessionVO = new UserVO();
        sessionVO.setActiveUserID("Active User ID");
        sessionVO.setActiveUserLoginId("42");
        sessionVO.setActiveUserMsisdn("Active User Msisdn");
        sessionVO.setActiveUserPin("Active User Pin");
        sessionVO.setAddCommProfOTFDetailId("42");
        sessionVO.setAddress1("42 Main St");
        sessionVO.setAddress2("42 Main St");
        sessionVO.setAgentBalanceList(new ArrayList<>());
        sessionVO.setAllowedDay(new String[]{"Allowed Days"});
        sessionVO.setAllowedDays("Allowed Days");
        sessionVO.setAllowedIps("Allowed Ips");
        sessionVO.setAllowedUserTypeCreation("Allowed User Type Creations");
        sessionVO.setAppintmentDate("2020-03-01");
        sessionVO.setAppointmentDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setAssType("Ass Type");
        sessionVO.setAssoMsisdn("Asso Msisdn");
        sessionVO.setAssociatedGeographicalList(new ArrayList());
        sessionVO.setAssociatedProductTypeList(new ArrayList());
        sessionVO.setAssociatedServiceTypeList(new ArrayList());
        sessionVO
                .setAssociationCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setAssociationModifiedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setAuthType("Type");
        sessionVO.setAuthTypeAllowed("Type Allowed");
        sessionVO.setBatchID("Batch ID");
        sessionVO.setBatchName("Batch Name");
        sessionVO.setBrowserType("Browser Type");
        sessionVO.setC2sMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setC2sMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setCategoryCode("Category Code");
        sessionVO.setCategoryCodeDesc("Category Code Desc");
        sessionVO.setCategoryVO(categoryVO);
        sessionVO.setCity("Oxford");
        sessionVO.setCompany("Company");
        sessionVO.setConfirmPassword("iloveyou");
        sessionVO.setContactNo("Contact N0");
        sessionVO.setContactPerson("Contact Person");
        sessionVO.setCountry("GB");
        sessionVO.setCountryCode("GB");
        sessionVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        sessionVO.setCreatedByUserName("janedoe");
        sessionVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setCreatedOnAsString("Jan 1, 2020 8:00am GMT+0100");
        sessionVO.setCreated_On("Jan 1, 2020 8:00am GMT+0100");
        sessionVO.setCreationType("Creation Type");
        sessionVO.setCurrentModule("Current Module");
        sessionVO.setCurrentRoleCode("Current Role Code");
        sessionVO.setDepartmentCode("Department Code");
        sessionVO.setDepartmentDesc("Department Desc");
        sessionVO.setDepartmentList(new ArrayList<>());
        sessionVO.setDesignation("Designation");
        sessionVO.setDivisionCode("Division Code");
        sessionVO.setDivisionDesc("Division Desc");
        sessionVO.setDivisionList(new ArrayList<>());
        sessionVO.setDocumentNo("Document No");
        sessionVO.setDocumentType("Document Type");
        sessionVO.setDomainCodes(new String[]{"Domain Codes"});
        sessionVO.setDomainID("Domain ID");
        sessionVO.setDomainList(new ArrayList());
        sessionVO.setDomainName("Domain Name");
        sessionVO.setDomainStatus("Domain Status");
        sessionVO.setDomainTypeCode("Domain Type Code");
        sessionVO.setEmail("jane.doe@example.org");
        sessionVO.setEmpCode("Emp Code");
        sessionVO.setExternalCode("External Code");
        sessionVO.setFax("Fax");
        sessionVO.setFirstName("Name");
        sessionVO.setFromTime("jane.doe@example.org");
        sessionVO.setFxedInfoStr("Fxed Info Str");
        sessionVO.setGeographicalAreaList(new ArrayList<>());
        sessionVO.setGeographicalCode("Geographical Codes");
        sessionVO.setGeographicalCodeArray(new String[]{"Geographical Code Arrays"});
        sessionVO.setGeographicalCodeStatus("Geographical Code Status");
        sessionVO.setGeographicalList(new ArrayList());
        sessionVO.setGrphDomainTypeName("Grph Domain Type Names");
        sessionVO.setInfo1("Info1");
        sessionVO.setInfo10("Info10");
        sessionVO.setInfo11("Info11");
        sessionVO.setInfo12("Info12");
        sessionVO.setInfo13("Info13");
        sessionVO.setInfo14("Info14");
        sessionVO.setInfo15("Info15");
        sessionVO.setInfo2("Info2");
        sessionVO.setInfo3("Info3");
        sessionVO.setInfo4("Info4");
        sessionVO.setInfo5("Info5");
        sessionVO.setInfo6("Info6");
        sessionVO.setInfo7("Info7");
        sessionVO.setInfo8("Info8");
        sessionVO.setInfo9("Info9");
        sessionVO.setInvalidPasswordCount(3);
        sessionVO.setIsSerAssignChnlAdm(true);
        sessionVO.setLanguage("en");
        sessionVO.setLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setLastModified(1L);
        sessionVO.setLastName("Name");
        sessionVO.setLatitude("Latitude");
        sessionVO.setLevel1ApprovedBy("Level1 Approved By");
        sessionVO
                .setLevel1ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setLevel2ApprovedBy("Level2 Approved By");
        sessionVO
                .setLevel2ApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setLoggerMessage("Logger Message");
        sessionVO.setLoginID("Login ID");
        sessionVO.setLoginTime(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setLongitude("Longitude");
        sessionVO.setMenuItemList(new ArrayList());
        sessionVO.setMessage("Not all who wander are lost");
        sessionVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        sessionVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setModuleCodeString("Code String");
        sessionVO.setMsisdn("Msisdn");
        sessionVO.setMsisdnList(new ArrayList());
        sessionVO.setNetworkID("Network ID");
        sessionVO.setNetworkList(new ArrayList());
        sessionVO.setNetworkName("Network Name");
        sessionVO.setNetworkStatus("Network Status");
        sessionVO.setOTPValidated(true);
        sessionVO.setOldLastLoginOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setOtfCount(3);
        sessionVO.setOtfValue(42L);
        sessionVO.setOwnerCategoryName("Owner Category Name");
        sessionVO.setOwnerCompany("Company");
        sessionVO.setOwnerID("Owner ID");
        sessionVO.setOwnerMsisdn("Owner Msisdn");
        sessionVO.setOwnerName("Owner Name");
        sessionVO.setP2pMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setP2pMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setPageCodeString("Code String");
        sessionVO.setParentCategoryName("Parent Category Name");
        sessionVO.setParentID("Parent ID");
        sessionVO.setParentMsisdn("Parent Msisdn");
        sessionVO.setParentName("Parent Name");
        sessionVO.setPassword("iloveyou");
        sessionVO.setPasswordCountUpdatedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO
                .setPasswordModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setPasswordModifyFlag(true);
        sessionVO.setPasswordReset("Password Reset");
        sessionVO.setPaymentType("Payment Type");
        sessionVO.setPaymentTypes("Payment Types");
        sessionVO.setPaymentTypes(new String[]{"Payment Types"});
        sessionVO.setPaymentTypesList(new ArrayList());
        sessionVO.setPinReset("Pin Reset");
        sessionVO.setPreviousStatus("Previous Status");
        sessionVO.setProductCodes(new String[]{"Product Codess"});
        sessionVO.setProductsList(new ArrayList());
        sessionVO.setReferenceID("Reference ID");
        sessionVO.setRemarks("Remarks");
        sessionVO.setRemoteAddress("42 Main St");
        sessionVO.setReportHeaderName("Report Header Name");
        sessionVO.setRequestType("Request Type");
        sessionVO.setRequetedByUserName("janedoe");
        sessionVO.setRestrictedMsisdnAllow("Restricted Msisdn Allow");
        sessionVO.setRoleFlag(new String[]{"Role Flags"});
        sessionVO.setRoleType("Role Types");
        sessionVO.setRolesMap(new HashMap());
        sessionVO.setRolesMapSelected(new HashMap());
        sessionVO.setRsaAllowed(true);
        sessionVO.setRsaFlag("Rsa Flag");
        sessionVO.setRsaRequired(true);
        sessionVO.setRsavalidated(true);
        sessionVO.setSegmentList(new ArrayList());
        sessionVO.setServiceList(new ArrayList());
        sessionVO.setServicesList(new ArrayList());
        sessionVO.setServicesTypes(new String[]{"Services Typess"});
        sessionVO.setSessionInfoVO(sessionInfoVO);
        sessionVO.setShortName("Short Name");
        sessionVO.setShowPassword("iloveyou");
        sessionVO.setSsn("123-45-678");
        sessionVO.setStaffUser(true);
        sessionVO.setStaffUserDetails(ChannelUserVO.getInstance());
        sessionVO.setState("MD");
        sessionVO.setStatus("Status");
        sessionVO.setStatusDesc("Status Desc");
        sessionVO.setStatusList(new ArrayList());
        sessionVO.setSuspendedByUserName("janedoe");
        sessionVO.setSuspendedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionVO.setToTime("To Time");
        sessionVO.setUpdateSimRequired(true);
        sessionVO.setUserBalanceList(new ArrayList<>());
        sessionVO.setUserCode("User Code");
        sessionVO.setUserID("User ID");
        sessionVO.setUserLanguage("en");
        sessionVO.setUserLanguageDesc("en");
        sessionVO.setUserLanguageList(new ArrayList());
        sessionVO.setUserLoanVOList(new ArrayList<>());
        sessionVO.setUserName("janedoe");
        sessionVO.setUserNamePrefix("janedoe");
        sessionVO.setUserNamePrefixList(new ArrayList());
        sessionVO.setUserPhoneVO(UserPhoneVO.getInstance());
        sessionVO.setUserType("User Type");
        sessionVO.setUsingNewSTK(true);
        sessionVO.setValidRequestURLs("https://example.org/example");
        sessionVO.setValidStatus(1);
        sessionVO.setVoucherList(new ArrayList());
        sessionVO.setWebLoginID(" web Login ID");

        ConfimChannelUserTransferRequestVO requestVO = new ConfimChannelUserTransferRequestVO();
        requestVO.setMsisdn("Msisdn");
        requestVO.setOtp("Otp");

        CategoryVO categoryVO2 = new CategoryVO();
        categoryVO2.setAgentAgentAllowed("Agent Agent Allowed");
        categoryVO2.setAgentAllowed("Agent Allowed");
        categoryVO2.setAgentAllowedFlag("Agent Allowed Flag");
        categoryVO2.setAgentCategoryCode("Agent Category Code");
        categoryVO2.setAgentCategoryName("Agent Category Name");
        categoryVO2.setAgentCategoryStatus("Agent Category Status");
        categoryVO2.setAgentCategoryStatusList(new ArrayList());
        categoryVO2.setAgentCategoryType("Agent Category Type");
        categoryVO2.setAgentCheckArray(new String[]{"Agent Check Array"});
        categoryVO2.setAgentCp2pPayee("Cp2p Payee");
        categoryVO2.setAgentCp2pPayer("Cp2p Payer");
        categoryVO2.setAgentCp2pWithinList("Cp2p Within List");
        categoryVO2.setAgentDisplayAllowed("Agent Display Allowed");
        categoryVO2.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        categoryVO2.setAgentDomainName("Agent Domain Name");
        categoryVO2.setAgentFixedRoles("Agent Fixed Roles");
        categoryVO2.setAgentGatewayName("Agent Gateway Name");
        categoryVO2.setAgentGatewayType("Agent Gateway Type");
        categoryVO2.setAgentGeographicalDomainList("Agent Geographical Domain List");
        categoryVO2.setAgentGrphDomainType("Agent Grph Domain Type");
        categoryVO2.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        categoryVO2.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        categoryVO2.setAgentMaxLoginCount(3L);
        categoryVO2.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        categoryVO2.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO2.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO2.setAgentModifyAllowed("Agent Modify Allowed");
        categoryVO2.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        categoryVO2.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        categoryVO2.setAgentOutletsAllowed("Agent Outlets Allowed");
        categoryVO2.setAgentParentOrOwnerRadioValue("42");
        categoryVO2.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        categoryVO2.setAgentRechargeByParentOnly("Recharge By Parent Only");
        categoryVO2.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        categoryVO2.setAgentRoleName("Agent Role Name");
        categoryVO2.setAgentRoleTypeList(new ArrayList());
        categoryVO2.setAgentRolesMapSelected(new HashMap());
        categoryVO2.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        categoryVO2.setAgentServiceAllowed("Agent Service Allowed");
        categoryVO2.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        categoryVO2.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        categoryVO2.setAgentUserIdPrefix("Agent User Id Prefix");
        categoryVO2.setAgentViewOnNetworkBlock("Agent View On Network Block");
        categoryVO2.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        categoryVO2.setAllowedGatewayTypes(new ArrayList());
        categoryVO2.setAuthenticationType("Type");
        categoryVO2.setCategoryCode("Category Code");
        categoryVO2.setCategoryName("Category Name");
        categoryVO2.setCategorySequenceNumber(10);
        categoryVO2.setCategoryStatus("Category Status");
        categoryVO2.setCategoryType("Category Type");
        categoryVO2.setCategoryTypeCode("Category Type Code");
        categoryVO2.setCp2pPayee("Payee");
        categoryVO2.setCp2pPayer("Payer");
        categoryVO2.setCp2pWithinList("Within List");
        categoryVO2.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO2.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO2.setDisplayAllowed("Display Allowed");
        categoryVO2.setDomainAllowed("Domain Allowed");
        categoryVO2.setDomainCodeforCategory("Domain Codefor Category");
        categoryVO2.setDomainName("Domain Name");
        categoryVO2.setDomainTypeCode("Domain Type Code");
        categoryVO2.setFixedDomains("Fixed Domains");
        categoryVO2.setFixedRoles("Fixed Roles");
        categoryVO2.setGeographicalDomainSeqNo(1);
        categoryVO2.setGrphDomainSequenceNo(1);
        categoryVO2.setGrphDomainType("Grph Domain Type");
        categoryVO2.setGrphDomainTypeName("Grph Domain Type Name");
        categoryVO2.setHierarchyAllowed("Hierarchy Allowed");
        categoryVO2.setLastModifiedTime(1L);
        categoryVO2.setLowBalAlertAllow("Low Bal Alert Allow");
        categoryVO2.setMaxLoginCount(3L);
        categoryVO2.setMaxTxnMsisdn("Max Txn Msisdn");
        categoryVO2.setMaxTxnMsisdnInt(3);
        categoryVO2.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO2.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO2.setModifyAllowed("Modify Allowed");
        categoryVO2.setMultipleGrphDomains("Multiple Grph Domains");
        categoryVO2.setMultipleLoginAllowed("Multiple Login Allowed");
        categoryVO2.setNumberOfCategoryForDomain(10);
        categoryVO2.setOutletsAllowed("Outlets Allowed");
        categoryVO2.setParentCategoryCode("Parent Category Code");
        categoryVO2.setParentOrOwnerRadioValue("42");
        categoryVO2.setProductTypeAllowed("Product Type Allowed");
        categoryVO2.setProductTypeAssociationAllowed("Product Type Association Allowed");
        categoryVO2.setRadioIndex(1);
        categoryVO2.setRechargeByParentOnly("By Parent Only");
        categoryVO2.setRecordCount(3);
        categoryVO2.setRestrictedMsisdns("Restricted Msisdns");
        categoryVO2.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        categoryVO2.setSequenceNumber(10);
        categoryVO2.setServiceAllowed("Service Allowed");
        categoryVO2.setSmsInterfaceAllowed("Sms Interface Allowed");
        categoryVO2.setTransferToListOnly("Transfer To List Only");
        categoryVO2.setTxnOutsideHierchy("Txn Outside Hierchy");
        categoryVO2.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        categoryVO2.setUserIdPrefix("User Id Prefix");
        categoryVO2.setViewOnNetworkBlock("View On Network Block");
        categoryVO2.setWebInterfaceAllowed("Web Interface Allowed");

        SessionInfoVO sessionInfoVO2 = new SessionInfoVO();
        sessionInfoVO2.setCookieID("Cookie ID");
        sessionInfoVO2.setCurrentModuleCode("Current Module Code");
        sessionInfoVO2.setCurrentPageCode("Current Page Code");
        sessionInfoVO2.setCurrentPageName("Current Page Name");
        sessionInfoVO2.setCurrentRoleCode("Current Role Code");
        sessionInfoVO2.setMessageGatewayVO(new MessageGatewayVO());
        sessionInfoVO2.setRemoteAddr("42 Main St");
        sessionInfoVO2.setRemoteHost("localhost");
        sessionInfoVO2.setRoleHitTimeMap(new HashMap());
        sessionInfoVO2.setSessionID("Session ID");
        sessionInfoVO2.setTotalHit(1L);
        sessionInfoVO2.setUnderProcess(true);
        sessionInfoVO2.setUnderProcessHit(1L);

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
        userVO
                .setAssociationCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setCategoryVO(categoryVO2);
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
        userVO.setPasswordModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        userVO.setSessionInfoVO(sessionInfoVO2);
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
        channelUserTransferServiceImpl.createTransferVO(JUnitConfig.getConnection(), channelUserVO, channelUserTransferVO, sessionVO, requestVO,
                userVO);
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#validateOTP(Connection, BaseResponse, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateOTP() throws SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.channeluser.service.ChannelUserTransferServiceImpl.validateOTP(ChannelUserTransferServiceImpl.java:545)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(mock(Timestamp.class));
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        channelUserTransferServiceImpl.validateOTP(JUnitConfig.getConnection(), response, "OTP", "Msisdn",
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#validateOTP(Connection, BaseResponse, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateOTP2() throws SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.channeluser.service.ChannelUserTransferServiceImpl.validateOTP(ChannelUserTransferServiceImpl.java:545)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.getTimestamp(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        channelUserTransferServiceImpl.validateOTP(JUnitConfig.getConnection(), response, "OTP", "Msisdn",
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#validateOTP(Connection, BaseResponse, String, String, HttpServletResponse)}
     */
    @Test
    public void testValidateOTP3() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(mock(Timestamp.class));
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        channelUserTransferServiceImpl.validateOTP(JUnitConfig.getConnection(), response, "OTP", "Msisdn", responseSwag);
        //verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet).close();
        assertEquals(400, response.getStatus());
        assertEquals(400, ((MockHttpServletResponse) responseSwag.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link ChannelUserTransferServiceImpl#validateOTP(Connection, BaseResponse, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateOTP4() throws SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserTransferServiceImpl.validateOTP(ChannelUserTransferServiceImpl.java:545)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(mock(Timestamp.class));
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
        // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BaseResponse response = new BaseResponse();
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        channelUserTransferServiceImpl.validateOTP(JUnitConfig.getConnection(), response, "OTP", "Msisdn", null);
    }
}

