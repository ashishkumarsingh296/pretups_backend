package com.restapi.c2s.services;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.XssWrapper;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListService;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.c2sservices.controller.RescheduleBatchRechargeRequestVO;
import com.restapi.c2sservices.controller.RescheduleBatchRechargeResponseVO;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {C2SBulkRcServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SBulkRcServiceImplTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @MockBean
    private C2SBulkEVDProcessor c2SBulkEVDProcessor;

    @MockBean
    private DownloadUserListService downloadUserListService;

    @Autowired
    private C2SBulkRcServiceImpl c2SBulkRcServiceImpl;

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processRequest(C2SBulkRechargeRequestVO, String, String, String, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessRequest() throws BTSLBaseException {
        JUnitConfig.init();
        C2SBulkRechargeRequestVO requestVO = new C2SBulkRechargeRequestVO();

        C2SBulkRechargeDetails data = new C2SBulkRechargeDetails();
        data.setPin("1357");
        data.setBatchType("String");
        data.setExtcode("String");
        data.setLoginid("String");
        data.setMsisdn("9999999999");

        requestVO.setData(data);

        String serviceKeyword = "";
        String requestIDStr = "";
        String requestFor = "";
        MultiValueMap<String, String> headers = null;
        HttpServletResponse responseSwag = mock(HttpServletResponse.class);


        doNothing().when(responseSwag).setStatus(Mockito.anyInt());
        // Act
        C2SBulkRechargeResponseVO actualProcessRequestResult = this.c2SBulkRcServiceImpl.processRequest(requestVO,
                serviceKeyword, requestIDStr, requestFor, headers, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateSenderForDVD() {
        JUnitConfig.init();

        // Arrange
        // TODO: Populate arranged inputs
        String msisdn = "";
        String pin = "";

        // Act
        DvdBulkResponse actualValidateSenderForDVDResult = this.c2SBulkRcServiceImpl.validateSenderForDVD(msisdn, pin);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#writeFileCSVForDvd(List, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testWriteFileCSVForDvd() throws IOException {
        JUnitConfig.init();
        c2SBulkRcServiceImpl.writeFileCSVForDvd(new ArrayList<>(), "/data1/pretupsapp/tomcat_trunk_dev/logs/foo.txt");
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#getErrorMessage(String, String[])}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetErrorMessage() {
        JUnitConfig.init();

        c2SBulkRcServiceImpl.getErrorMessage("An error occurred", new String[]{"Args"});
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#getErrorMessage(String, String[])}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetErrorMessage2() {
        JUnitConfig.init();
        c2SBulkRcServiceImpl.getErrorMessage("An error occurred", new String[]{});
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processCancelBatch(CancelBatchC2SRequestVO, String, MultiValueMap, HttpServletResponse, HttpServletRequest, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessCancelBatch() {
        JUnitConfig.init();
      CancelBatchC2SRequestVO requestVO = null;
        String requestIDStr = "";
        MultiValueMap<String, String> headers = null;
        HttpServletResponse responseSwag = mock(HttpServletResponse.class);
        HttpServletRequest httpServletRequest = null;
        String serviceKeyword = "";

        doNothing().when(responseSwag).setStatus(Mockito.anyInt());
        // Act
        DvdBulkResponse actualProcessCancelBatchResult = this.c2SBulkRcServiceImpl.processCancelBatch(requestVO,
                requestIDStr, headers, responseSwag, httpServletRequest, serviceKeyword);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processSingleMsisdnCancelBatch(CancelSingleMsisdnBatchC2SRequestVO, String, MultiValueMap, HttpServletResponse, HttpServletRequest, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessSingleMsisdnCancelBatch() throws Exception {
        JUnitConfig.init();
        CancelSingleMsisdnBatchC2SRequestVO requestVO = new CancelSingleMsisdnBatchC2SRequestVO();
        requestVO.setBatchId("42");
        requestVO.setMsisdnList(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkRcServiceImpl.processSingleMsisdnCancelBatch(requestVO, "Request IDStr", headers, response1,
                new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())), "Service Keyword");
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processSingleMsisdnCancelBatch(CancelSingleMsisdnBatchC2SRequestVO, String, MultiValueMap, HttpServletResponse, HttpServletRequest, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessSingleMsisdnCancelBatch2() throws Exception {
        JUnitConfig.init();
        CancelSingleMsisdnBatchC2SRequestVO requestVO = mock(CancelSingleMsisdnBatchC2SRequestVO.class);
        doNothing().when(requestVO).setBatchId(Mockito.<String>any());
        doNothing().when(requestVO).setMsisdnList(Mockito.<ArrayList<String>>any());
        requestVO.setBatchId("42");
        requestVO.setMsisdnList(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        //CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkRcServiceImpl.processSingleMsisdnCancelBatch(requestVO, "Request IDStr", headers, response1,
                new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())), "Service Keyword");
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#getDomainCode(CategoryVO, Connection, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetDomainCode() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        CategoryVO requestVO = new CategoryVO();
        requestVO.setAgentAgentAllowed("Agent Agent Allowed");
        requestVO.setAgentAllowed("Agent Allowed");
        requestVO.setAgentAllowedFlag("Agent Allowed Flag");
        requestVO.setAgentCategoryCode("Agent Category Code");
        requestVO.setAgentCategoryName("Agent Category Name");
        requestVO.setAgentCategoryStatus("Agent Category Status");
        requestVO.setAgentCategoryStatusList(new ArrayList());
        requestVO.setAgentCategoryType("Agent Category Type");
        requestVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        requestVO.setAgentCp2pPayee("Cp2p Payee");
        requestVO.setAgentCp2pPayer("Cp2p Payer");
        requestVO.setAgentCp2pWithinList("Cp2p Within List");
        requestVO.setAgentDisplayAllowed("Agent Display Allowed");
        requestVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        requestVO.setAgentDomainName("Agent Domain Name");
        requestVO.setAgentFixedRoles("Agent Fixed Roles");
        requestVO.setAgentGatewayName("Agent Gateway Name");
        requestVO.setAgentGatewayType("Agent Gateway Type");
        requestVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        requestVO.setAgentGrphDomainType("Agent Grph Domain Type");
        requestVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        requestVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        requestVO.setAgentMaxLoginCount(3L);
        requestVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        requestVO.setAgentMessageGatewayTypeList(new ArrayList());
        requestVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        requestVO.setAgentModifyAllowed("Agent Modify Allowed");
        requestVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        requestVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        requestVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        requestVO.setAgentParentOrOwnerRadioValue("42");
        requestVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        requestVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        requestVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        requestVO.setAgentRoleName("Agent Role Name");
        requestVO.setAgentRoleTypeList(new ArrayList());
        requestVO.setAgentRolesMapSelected(new HashMap());
        requestVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        requestVO.setAgentServiceAllowed("Agent Service Allowed");
        requestVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        requestVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        requestVO.setAgentUserIdPrefix("Agent User Id Prefix");
        requestVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        requestVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        requestVO.setAllowedGatewayTypes(new ArrayList());
        requestVO.setAuthenticationType("Type");
        requestVO.setCategoryCode("Category Code");
        requestVO.setCategoryName("Category Name");
        requestVO.setCategorySequenceNumber(10);
        requestVO.setCategoryStatus("Category Status");
        requestVO.setCategoryType("Category Type");
        requestVO.setCategoryTypeCode("Category Type Code");
        requestVO.setCp2pPayee("Payee");
        requestVO.setCp2pPayer("Payer");
        requestVO.setCp2pWithinList("Within List");
        requestVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        requestVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        requestVO.setDisplayAllowed("Display Allowed");
        requestVO.setDomainAllowed("Domain Allowed");
        requestVO.setDomainCodeforCategory("Domain Codefor Category");
        requestVO.setDomainName("Domain Name");
        requestVO.setDomainTypeCode("Domain Type Code");
        requestVO.setFixedDomains("Fixed Domains");
        requestVO.setFixedRoles("Fixed Roles");
        requestVO.setGeographicalDomainSeqNo(1);
        requestVO.setGrphDomainSequenceNo(1);
        requestVO.setGrphDomainType("Grph Domain Type");
        requestVO.setGrphDomainTypeName("Grph Domain Type Name");
        requestVO.setHierarchyAllowed("Hierarchy Allowed");
        requestVO.setLastModifiedTime(1L);
        requestVO.setLowBalAlertAllow("Low Bal Alert Allow");
        requestVO.setMaxLoginCount(3L);
        requestVO.setMaxTxnMsisdn("Max Txn Msisdn");
        requestVO.setMaxTxnMsisdnInt(3);
        requestVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        requestVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        requestVO.setModifyAllowed("Modify Allowed");
        requestVO.setMultipleGrphDomains("Multiple Grph Domains");
        requestVO.setMultipleLoginAllowed("Multiple Login Allowed");
        requestVO.setNumberOfCategoryForDomain(10);
        requestVO.setOutletsAllowed("Outlets Allowed");
        requestVO.setParentCategoryCode("Parent Category Code");
        requestVO.setParentOrOwnerRadioValue("42");
        requestVO.setProductTypeAllowed("Product Type Allowed");
        requestVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        requestVO.setRadioIndex(1);
        requestVO.setRechargeByParentOnly("By Parent Only");
        requestVO.setRecordCount(3);
        requestVO.setRestrictedMsisdns("Restricted Msisdns");
        requestVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        requestVO.setSequenceNumber(10);
        requestVO.setServiceAllowed("Service Allowed");
        requestVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        requestVO.setTransferToListOnly("Transfer To List Only");
        requestVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        requestVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        requestVO.setUserIdPrefix("User Id Prefix");
        requestVO.setViewOnNetworkBlock("View On Network Block");
        requestVO.setWebInterfaceAllowed("Web Interface Allowed");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkRcServiceImpl.getDomainCode(requestVO, JUnitConfig.getConnection(), "Domain Code", response1);
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#getDomainCode(CategoryVO, Connection, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetDomainCode2() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        CategoryVO requestVO = new CategoryVO();
        requestVO.setAgentAgentAllowed("Agent Agent Allowed");
        requestVO.setAgentAllowed("Agent Allowed");
        requestVO.setAgentAllowedFlag("Agent Allowed Flag");
        requestVO.setAgentCategoryCode("Agent Category Code");
        requestVO.setAgentCategoryName("Agent Category Name");
        requestVO.setAgentCategoryStatus("Agent Category Status");
        requestVO.setAgentCategoryStatusList(new ArrayList());
        requestVO.setAgentCategoryType("Agent Category Type");
        requestVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        requestVO.setAgentCp2pPayee("Cp2p Payee");
        requestVO.setAgentCp2pPayer("Cp2p Payer");
        requestVO.setAgentCp2pWithinList("Cp2p Within List");
        requestVO.setAgentDisplayAllowed("Agent Display Allowed");
        requestVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        requestVO.setAgentDomainName("Agent Domain Name");
        requestVO.setAgentFixedRoles("Agent Fixed Roles");
        requestVO.setAgentGatewayName("Agent Gateway Name");
        requestVO.setAgentGatewayType("Agent Gateway Type");
        requestVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        requestVO.setAgentGrphDomainType("Agent Grph Domain Type");
        requestVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        requestVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        requestVO.setAgentMaxLoginCount(3L);
        requestVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        requestVO.setAgentMessageGatewayTypeList(new ArrayList());
        requestVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        requestVO.setAgentModifyAllowed("Agent Modify Allowed");
        requestVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        requestVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        requestVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        requestVO.setAgentParentOrOwnerRadioValue("42");
        requestVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        requestVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        requestVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        requestVO.setAgentRoleName("Agent Role Name");
        requestVO.setAgentRoleTypeList(new ArrayList());
        requestVO.setAgentRolesMapSelected(new HashMap());
        requestVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        requestVO.setAgentServiceAllowed("Agent Service Allowed");
        requestVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        requestVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        requestVO.setAgentUserIdPrefix("Agent User Id Prefix");
        requestVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        requestVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        requestVO.setAllowedGatewayTypes(new ArrayList());
        requestVO.setAuthenticationType("Type");
        requestVO.setCategoryCode("Category Code");
        requestVO.setCategoryName("Category Name");
        requestVO.setCategorySequenceNumber(10);
        requestVO.setCategoryStatus("Category Status");
        requestVO.setCategoryType("Category Type");
        requestVO.setCategoryTypeCode("Category Type Code");
        requestVO.setCp2pPayee("Payee");
        requestVO.setCp2pPayer("Payer");
        requestVO.setCp2pWithinList("Within List");
        requestVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        requestVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        requestVO.setDisplayAllowed("Display Allowed");
        requestVO.setDomainAllowed("Domain Allowed");
        requestVO.setDomainCodeforCategory("Domain Codefor Category");
        requestVO.setDomainName("Domain Name");
        requestVO.setDomainTypeCode("Domain Type Code");
        requestVO.setFixedDomains("Fixed Domains");
        requestVO.setFixedRoles("Fixed Roles");
        requestVO.setGeographicalDomainSeqNo(1);
        requestVO.setGrphDomainSequenceNo(1);
        requestVO.setGrphDomainType("Grph Domain Type");
        requestVO.setGrphDomainTypeName("Grph Domain Type Name");
        requestVO.setHierarchyAllowed("Hierarchy Allowed");
        requestVO.setLastModifiedTime(1L);
        requestVO.setLowBalAlertAllow("Low Bal Alert Allow");
        requestVO.setMaxLoginCount(3L);
        requestVO.setMaxTxnMsisdn("Max Txn Msisdn");
        requestVO.setMaxTxnMsisdnInt(3);
        requestVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        requestVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        requestVO.setModifyAllowed("Modify Allowed");
        requestVO.setMultipleGrphDomains("Multiple Grph Domains");
        requestVO.setMultipleLoginAllowed("Multiple Login Allowed");
        requestVO.setNumberOfCategoryForDomain(10);
        requestVO.setOutletsAllowed("Outlets Allowed");
        requestVO.setParentCategoryCode("Parent Category Code");
        requestVO.setParentOrOwnerRadioValue("42");
        requestVO.setProductTypeAllowed("Product Type Allowed");
        requestVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        requestVO.setRadioIndex(1);
        requestVO.setRechargeByParentOnly("By Parent Only");
        requestVO.setRecordCount(3);
        requestVO.setRestrictedMsisdns("Restricted Msisdns");
        requestVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        requestVO.setSequenceNumber(10);
        requestVO.setServiceAllowed("Service Allowed");
        requestVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        requestVO.setTransferToListOnly("Transfer To List Only");
        requestVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        requestVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        requestVO.setUserIdPrefix("User Id Prefix");
        requestVO.setViewOnNetworkBlock("View On Network Block");
        requestVO.setWebInterfaceAllowed("Web Interface Allowed");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkRcServiceImpl.getDomainCode(requestVO, JUnitConfig.getConnection(), "Domain Code", response1);
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processRescheduleFile(RescheduleBatchRechargeRequestVO, OAuthUser, HttpServletRequest, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessRescheduleFile() {
        JUnitConfig.init();
        RescheduleBatchRechargeRequestVO rescheduleBatchRechargeRequestVO = null;
        OAuthUser oAuthUserData = null;
        HttpServletRequest request = null;
        HttpServletResponse responseSwag = null;

        // Act
        RescheduleBatchRechargeResponseVO actualProcessRescheduleFileResult = this.c2SBulkRcServiceImpl
                .processRescheduleFile(rescheduleBatchRechargeRequestVO, oAuthUserData, request, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processViewBatchScheduleDetails(Connection, String, HttpServletResponse, String, String, String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessViewBatchScheduleDetails() throws BTSLBaseException {
        JUnitConfig.init();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkRcServiceImpl.processViewBatchScheduleDetails(JUnitConfig.getConnection(), "42", response1, "42",
                "Schedule Status", "Service Type", "2020-03-01");
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processViewBatchScheduleDetails(Connection, String, HttpServletResponse, String, String, String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessViewBatchScheduleDetails2() throws BTSLBaseException {
        JUnitConfig.init();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkRcServiceImpl.processViewBatchScheduleDetails(JUnitConfig.getConnection(), "42",
                response1, "42", "Schedule Status", "Service Type",
                "2020-03-01");
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processViewScheduleDetails(Connection, UserVO, String, String)}
     */
    @Test
    public void testProcessViewScheduleDetails() throws Exception {
        JUnitConfig.init();
        C2SBulkRcServiceImpl c2sBulkRcServiceImpl = new C2SBulkRcServiceImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getUserID()).thenReturn("User ID");
        thrown.expect(BTSLBaseException.class);
        c2sBulkRcServiceImpl.processViewScheduleDetails(JUnitConfig.getConnection(), sessionUserVO, "42", "Msisdn");
        verify(sessionUserVO).getUserID();
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processViewScheduleDetails(Connection, UserVO, String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessViewScheduleDetails2() throws Exception {
        JUnitConfig.init();

        C2SBulkRcServiceImpl c2sBulkRcServiceImpl = new C2SBulkRcServiceImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getUserID()).thenReturn("User ID");
        c2sBulkRcServiceImpl.processViewScheduleDetails(JUnitConfig.getConnection(), sessionUserVO, "42", "Msisdn");
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processScheduleReportRequest(Connection, UserVO, String, String, String, String)}
     */
    @Test
    public void testProcessScheduleReportRequest() throws Exception {
        JUnitConfig.init();

        C2SBulkRcServiceImpl c2sBulkRcServiceImpl = new C2SBulkRcServiceImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getUserID()).thenReturn("User ID");
        thrown.expect(BTSLBaseException.class);
        c2sBulkRcServiceImpl.processScheduleReportRequest(JUnitConfig.getConnection(), sessionUserVO, "42", "Msisdn", "2020-03-01", "Staff Flag");
        verify(sessionUserVO).getUserID();
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processScheduleReportRequest(Connection, UserVO, String, String, String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessScheduleReportRequest2() throws Exception {
        JUnitConfig.init();

        C2SBulkRcServiceImpl c2sBulkRcServiceImpl = new C2SBulkRcServiceImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getUserID()).thenReturn("User ID");
        c2sBulkRcServiceImpl.processScheduleReportRequest(JUnitConfig.getConnection(), sessionUserVO, "42", "Msisdn", "2020-03-01", "Staff Flag");
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#servicesList(String, Connection, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testServicesList() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        /*
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(java.sql.Date.class));
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        */
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkRcServiceImpl.servicesList("42", JUnitConfig.getConnection(), response1);
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#servicesList(String, Connection, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testServicesList2() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        /*
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(mock(java.sql.Date.class));
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        */

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        c2SBulkRcServiceImpl.servicesList("42", JUnitConfig.getConnection(), response1);
    }

    /**
     * Method under test: {@link C2SBulkRcServiceImpl#processRequestBulkEVD(C2SBulkEvdRechargeRequestVO, String, String, String, MultiValueMap, HttpServletResponse)}
     */
    @Test
    public void testProcessRequestBulkEVD() throws BTSLBaseException {
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        C2SBulkEvdRechargeResponseVO c2sBulkEvdRechargeResponseVO = new C2SBulkEvdRechargeResponseVO();
        c2sBulkEvdRechargeResponseVO.setErrorMap(errorMap);
        c2sBulkEvdRechargeResponseVO.setFileAttachment("File Attachment");
        c2sBulkEvdRechargeResponseVO.setFileName("foo.txt");
        c2sBulkEvdRechargeResponseVO.setMessage("Not all who wander are lost");
        c2sBulkEvdRechargeResponseVO.setMessageCode("Message Code");
        c2sBulkEvdRechargeResponseVO.setNumberOfRecords(1L);
        c2sBulkEvdRechargeResponseVO.setScheduleBatchId("42");
        c2sBulkEvdRechargeResponseVO.setStatus("Txnstatus");
        when(c2SBulkEVDProcessor.processRequestBulkEVD(Mockito.<C2SBulkEvdRechargeRequestVO>any(), Mockito.<String>any(),
                Mockito.<String>any(), Mockito.<String>any(), Mockito.<MultiValueMap<String, String>>any(),
                Mockito.<HttpServletResponse>any())).thenReturn(c2sBulkEvdRechargeResponseVO);

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("No Of Days");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("2020-03-01");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");

        C2SBulkEvdRechargeRequestVO requestVO = new C2SBulkEvdRechargeRequestVO();
        requestVO.setData(data);
        requestVO.setData(data2);
        requestVO.setLoginId("42");
        requestVO.setMsisdn("Msisdn");
        requestVO.setPassword("iloveyou");
        requestVO.setPin("Pin");
        requestVO.setReqGatewayCode("Req Gateway Code");
        requestVO.setReqGatewayLoginId("42");
        requestVO.setReqGatewayPassword("iloveyou");
        requestVO.setReqGatewayType("Req Gateway Type");
        requestVO.setServicePort("Service Port");
        requestVO.setSourceType("Source Type");
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        assertSame(c2sBulkEvdRechargeResponseVO, c2SBulkRcServiceImpl.processRequestBulkEVD(requestVO, "Service Keyword",
                "Request IDStr", "Request For", headers, response1));
        verify(c2SBulkEVDProcessor).processRequestBulkEVD(Mockito.<C2SBulkEvdRechargeRequestVO>any(),
                Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
                Mockito.<MultiValueMap<String, String>>any(), Mockito.<HttpServletResponse>any());
    }
}/*{
    @MockBean
    private C2SBulkEVDProcessor c2SBulkEVDProcessor;

    @Autowired
    private C2SBulkRcServiceImpl c2SBulkRcServiceImpl;

    @MockBean
    private DownloadUserListService downloadUserListService;

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#addBatch(Connection, C2SBulkRechargeRequestVO, ChannelUserVO, String, int)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 * <p>
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 *//*
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddBatch() throws BTSLBaseException, SQLException, ParseException {
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

        C2SBulkRechargeRequestVO requestVO = new C2SBulkRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        requestVO.setData(data);

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("No Of Days");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("2020-03-01");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");
        requestVO.setData(data2);
        requestVO.setLoginId("42");
        requestVO.setMsisdn("Msisdn");
        requestVO.setPassword("iloveyou");
        requestVO.setPin("Pin");
        requestVO.setReqGatewayCode("Req Gateway Code");
        requestVO.setReqGatewayLoginId("42");
        requestVO.setReqGatewayPassword("iloveyou");
        requestVO.setReqGatewayType("Req Gateway Type");
        requestVO.setServicePort("Service Port");
        requestVO.setSourceType("Source Type");
        c2SBulkRcServiceImpl.addBatch(JUnitConfig.getConnection(), requestVO, ChannelUserVO.getInstance(), "Servicekeyword", 2);
    }

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 *//*
    @Test
    public void testIsSameDay() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        Date date1 = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        assertTrue(C2SBulkRcServiceImpl.isSameDay(date1,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())));
    }

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 *//*
    @Test
    //@Ignore("TODO: Complete this test")
    public void testIsSameDay2() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        Date date1 = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        C2SBulkRcServiceImpl.isSameDay(date1,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    }

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 *//*
    @Test
    public void testIsSameDay3() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        java.sql.Date date1 = mock(java.sql.Date.class);
        when(date1.getTime()).thenReturn(10L);
        assertTrue(C2SBulkRcServiceImpl.isSameDay(date1,
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())));
        verify(date1).getTime();
    }

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(java.util.Date, java.util.Date)}
 *//*
    @Test
    //@Ignore("TODO: Complete this test")
    public void testIsSameDay4() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Calendar.setTime(Calendar.java:1770)
        //       at java.text.SimpleDateFormat.format(SimpleDateFormat.java:943)
        //       at java.text.SimpleDateFormat.format(SimpleDateFormat.java:936)
        //       at java.text.DateFormat.format(DateFormat.java:345)
        //       at com.restapi.c2s.services.C2SBulkRcServiceImpl.isSameDay(C2SBulkRcServiceImpl.java:845)
        //   See https://diff.blue/R013 to resolve this issue.

        java.sql.Date date1 = mock(java.sql.Date.class);
        when(date1.getTime()).thenReturn(10L);
        C2SBulkRcServiceImpl.isSameDay(date1, null);
    }

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#isSameDay(Date, Date)}
 *//*
    @Test
    //@Ignore("TODO: Complete this test")
    public void testIsSameDay5() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Calendar.setTime(Calendar.java:1770)
        //       at java.text.SimpleDateFormat.format(SimpleDateFormat.java:943)
        //       at java.text.SimpleDateFormat.format(SimpleDateFormat.java:936)
        //       at java.text.DateFormat.format(DateFormat.java:345)
        //       at com.restapi.c2s.services.C2SBulkRcServiceImpl.isSameDay(C2SBulkRcServiceImpl.java:845)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SBulkRcServiceImpl.isSameDay(null,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    }

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#validateSenderForDVD(String, String)}
 *//*
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateSenderForDVD() {
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

        c2SBulkRcServiceImpl.validateSenderForDVD("Msisdn", "Pin");
    }

    *//**
 * Method under test: {@link C2SBulkRcServiceImpl#getMapInFileFormat(LinkedHashMap)}
 *//*
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetMapInFileFormat() {
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

        c2SBulkRcServiceImpl.getMapInFileFormat(new LinkedHashMap<>());
    }
}*/

