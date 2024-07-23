package com.restapi.channelAdmin.serviceI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.mchange.v2.c3p0.debug.AfterCloseLoggingConnectionWrapper;
import com.monitorjbl.xlsx.impl.StreamingCell;
import com.restapi.channelAdmin.ChannelAdminTransferVO;
import com.restapi.channelAdmin.requestVO.ApprovalBarredForDltRequestVO;
import com.restapi.channelAdmin.requestVO.BarredusersrequestVO;
import com.restapi.channelAdmin.requestVO.BulkModifyUserRequestVO;
import com.restapi.channelAdmin.requestVO.SuspendResumeUserHierarchyRequestVO;
import com.restapi.channelAdmin.responseVO.BulkModifyUserResponseVO;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.restapi.user.service.UserHierachyCARequestVO;
import com.restapi.user.service.UserHierarchyUIResponseData;
import com.web.pretups.channel.user.web.BatchUserForm;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {ChannelAdminUserHierarchyServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelAdminUserHierarchyServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ChannelAdminUserHierarchyServiceImpl channelAdminUserHierarchyServiceImpl;

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#getUserHierarchyListCA(Connection, String, UserHierachyCARequestVO, List, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserHierarchyListCA() throws BTSLBaseException, SQLException {

        com.btsl.util.JUnitConfig.init(this.getClass().toString()); //Auto replace

        //com.btsl.util.JUnitConfig.init();
        UserHierachyCARequestVO requestVO = new UserHierachyCARequestVO();
        requestVO.setAdvancedSearch(true);
        requestVO.setLoginID("Login ID");
        requestVO.setMsisdn("Msisdn");
        requestVO.setOwnerName("Owner Name");
        requestVO.setParentCategory("Parent Category");
        requestVO.setParentUserId("42");
        requestVO.setUserCategory("User Category");
        requestVO.setUserStatus("User Status");
        ArrayList<UserHierarchyUIResponseData> responseVO = new ArrayList<>();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

try {
    channelAdminUserHierarchyServiceImpl.getUserHierarchyListCA(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO, responseVO,
            response1);
}catch(Exception e){
e.printStackTrace();
}
        requestVO.setAdvancedSearch(false);
    try {
        channelAdminUserHierarchyServiceImpl.getUserHierarchyListCA(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO, responseVO,
                response1);
    }catch(Exception e){}

        requestVO.setLoginID(null);

        requestVO.setAdvancedSearch(true);

        try {
            channelAdminUserHierarchyServiceImpl.getUserHierarchyListCA(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO, responseVO,
                    response1);
        }catch(Exception e){
            e.printStackTrace();
        }
        requestVO.setAdvancedSearch(false);
        try {
            channelAdminUserHierarchyServiceImpl.getUserHierarchyListCA(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO, responseVO,
                    response1);
        }catch(Exception e){}


    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#getUserHierarchyListCA(Connection, String, UserHierachyCARequestVO, List, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserHierarchyListCA2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        UserHierachyCARequestVO requestVO = new UserHierachyCARequestVO();
        requestVO.setAdvancedSearch(true);
        requestVO.setLoginID("Login ID");
        requestVO.setMsisdn("Msisdn");
        requestVO.setOwnerName("Owner Name");
        requestVO.setParentCategory("Parent Category");
        requestVO.setParentUserId("42");
        requestVO.setUserCategory("User Category");
        requestVO.setUserStatus("User Status");
        ArrayList<UserHierarchyUIResponseData> responseVO = new ArrayList<>();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        channelAdminUserHierarchyServiceImpl.getUserHierarchyListCA(JUnitConfig.getConnection(), "Login ID", requestVO, responseVO,
                response1);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#suspendResumeUserHierarchyListCA(Connection, String, SuspendResumeUserHierarchyRequestVO, HttpServletResponse)}
     */
    @Test
    public void testSuspendResumeUserHierarchyListCA() throws BTSLBaseException, SQLException {
        //Connection con = mock(Connection.class);

        com.btsl.util.JUnitConfig.init(this.getClass().toString());
        SuspendResumeUserHierarchyRequestVO requestVO = new SuspendResumeUserHierarchyRequestVO();
        requestVO.setLoginIdList(new ArrayList<>());
        requestVO.setRequestType(PretupsI.USER_STATUS_SUSPEND);

        ArrayList<String> listt = new ArrayList<>() ;
        listt.add("String") ;
        requestVO.setLoginIdList(listt);

        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

     try {
         channelAdminUserHierarchyServiceImpl.suspendResumeUserHierarchyListCA(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO,
                 response1);
     }catch(Exception e){

         e.printStackTrace();
     }

        requestVO.setRequestType(PretupsI.USER_STATUS_ACTIVE);
        channelAdminUserHierarchyServiceImpl.suspendResumeUserHierarchyListCA(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO,
                response1);

    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#suspendResumeUserHierarchyListCA(Connection, String, SuspendResumeUserHierarchyRequestVO, HttpServletResponse)}
     */
    @Test
    public void testSuspendResumeUserHierarchyListCA2() throws BTSLBaseException, SQLException {

        JUnitConfig.init();

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        SuspendResumeUserHierarchyRequestVO requestVO = new SuspendResumeUserHierarchyRequestVO();
        requestVO.setLoginIdList(new ArrayList<>());
        requestVO.setRequestType("Request Type");
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        channelAdminUserHierarchyServiceImpl.suspendResumeUserHierarchyListCA(JUnitConfig.getConnection(), "Login ID", requestVO,
                response1);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#suspendResumeUserHierarchyListCA(Connection, String, SuspendResumeUserHierarchyRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSuspendResumeUserHierarchyListCA3() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.suspendResumeUserHierarchyListCA(ChannelAdminUserHierarchyServiceImpl.java:473)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        com.btsl.util.JUnitConfig.init();

        ArrayList<String> loginIdList = new ArrayList<>();
        loginIdList.add("foo");

        SuspendResumeUserHierarchyRequestVO requestVO = new SuspendResumeUserHierarchyRequestVO();
        requestVO.setLoginIdList(loginIdList);
        requestVO.setRequestType("Request Type");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        channelAdminUserHierarchyServiceImpl.suspendResumeUserHierarchyListCA(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO,
                response1);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#confirmChannelUserTransfer(Connection, MComConnectionI, BaseResponse, HttpServletResponse, ArrayList, ChannelUserVO, UserVO, ChannelAdminTransferVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConfirmChannelUserTransfer() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(this.getClass().toString()); //Auto replace


        BaseResponse response = mock(BaseResponse.class);;
        doNothing().when(response).setMessageCode(Mockito.anyString());
        doNothing().when(response).setMessage(Mockito.anyString());


        HttpServletResponse response1 = mock(HttpServletResponse.class);
        doNothing().when(response1).setStatus(Mockito.anyInt());

        ArrayList<ChannelUserTransferVO> userList = new ArrayList<>();

        ChannelUserTransferVO channelUserTransferVO = new ChannelUserTransferVO() ;
        channelUserTransferVO.setUserID("TEST");
        channelUserTransferVO.setUserName("TEST");
        channelUserTransferVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
        userList.add(channelUserTransferVO) ;

        ChannelUserVO channelUserVO = JUnitConfig.getChannelUserVO();



        UserVO sessionUserVO = JUnitConfig.getUserVO();
        ChannelAdminTransferVO requestVO = JUnitConfig.getChannelAdminTransferVO();

        // Act
        BaseResponse actualConfirmChannelUserTransferResult = this.channelAdminUserHierarchyServiceImpl
                .confirmChannelUserTransfer(com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), response, response1, userList, channelUserVO, sessionUserVO,
                        requestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#downloadBulkModifyUsersList(MultiValueMap, String, String, String, BatchUserForm, UserVO, FileDownloadResponseMulti, Connection)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadBulkModifyUsersList() throws BTSLBaseException, IOException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        HttpHeaders headers = new HttpHeaders();
        BatchUserForm form = new BatchUserForm();

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

        FileDownloadResponseMulti response = new FileDownloadResponseMulti();
        response.setErrorMap(errorMap);
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setFileattachment("Fileattachment");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        channelAdminUserHierarchyServiceImpl.downloadBulkModifyUsersList(headers, "Domain Type", "Category Type",
                "Geo Domain Type", form, userVO, response, mock(Connection.class));
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#basicFileValidations(BulkModifyUserRequestVO, BulkModifyUserResponseVO, String, String, String, Locale, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testBasicFileValidations() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.basicFileValidations(ChannelAdminUserHierarchyServiceImpl.java:1135)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelAdminUserHierarchyServiceImpl channelAdminUserHierarchyServiceImpl = new ChannelAdminUserHierarchyServiceImpl();

        BulkModifyUserRequestVO request = new BulkModifyUserRequestVO();
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BulkModifyUserResponseVO response = new BulkModifyUserResponseVO();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus("Status");
        response.setTotalRecords(1);
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        channelAdminUserHierarchyServiceImpl.basicFileValidations(request, response, "Domain Type", "Category Type",
                "Geo Domain Type", locale, new ArrayList<>());
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#basicFileValidations(BulkModifyUserRequestVO, BulkModifyUserResponseVO, String, String, String, Locale, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testBasicFileValidations2() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.basicFileValidations(ChannelAdminUserHierarchyServiceImpl.java:1135)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelAdminUserHierarchyServiceImpl channelAdminUserHierarchyServiceImpl = new ChannelAdminUserHierarchyServiceImpl();
        BulkModifyUserRequestVO request = mock(BulkModifyUserRequestVO.class);
        when(request.getFileAttachment()).thenReturn("File Attachment");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BulkModifyUserResponseVO response = new BulkModifyUserResponseVO();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus("Status");
        response.setTotalRecords(1);
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        channelAdminUserHierarchyServiceImpl.basicFileValidations(request, response, "Domain Type", "Category Type",
                "Geo Domain Type", locale, new ArrayList<>());
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#basicFileValidations(BulkModifyUserRequestVO, BulkModifyUserResponseVO, String, String, String, Locale, ArrayList)}
     */
    @Test
    public void testBasicFileValidations3() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.basicFileValidations(ChannelAdminUserHierarchyServiceImpl.java:1135)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelAdminUserHierarchyServiceImpl channelAdminUserHierarchyServiceImpl = new ChannelAdminUserHierarchyServiceImpl();
        BulkModifyUserRequestVO request = mock(BulkModifyUserRequestVO.class);
        when(request.getFileAttachment()).thenReturn("");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BulkModifyUserResponseVO response = new BulkModifyUserResponseVO();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus("Status");
        response.setTotalRecords(1);
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        ArrayList<MasterErrorList> inputValidations = new ArrayList<>();
        ArrayList<MasterErrorList> actualBasicFileValidationsResult = channelAdminUserHierarchyServiceImpl
                .basicFileValidations(request, response, "Domain Type", "Category Type", "Geo Domain Type", locale,
                        inputValidations);
        assertSame(inputValidations, actualBasicFileValidationsResult);
        assertEquals(1, actualBasicFileValidationsResult.size());
        verify(request, atLeast(1)).getFileAttachment();
        verify(request, atLeast(1)).getFileName();
        verify(request).getFileType();
        verify(request).setFileAttachment(Mockito.<String>any());
        verify(request).setFileName(Mockito.<String>any());
        verify(request).setFileType(Mockito.<String>any());
        assertEquals("File is Empty", response.getMessage());
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#basicFileValidations(BulkModifyUserRequestVO, BulkModifyUserResponseVO, String, String, String, Locale, ArrayList)}
     */
    @Test
    public void testBasicFileValidations4() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.basicFileValidations(ChannelAdminUserHierarchyServiceImpl.java:1135)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelAdminUserHierarchyServiceImpl channelAdminUserHierarchyServiceImpl = new ChannelAdminUserHierarchyServiceImpl();
        BulkModifyUserRequestVO request = mock(BulkModifyUserRequestVO.class);
        when(request.getFileAttachment()).thenReturn("");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BulkModifyUserResponseVO response = new BulkModifyUserResponseVO();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus("Status");
        response.setTotalRecords(1);
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        ArrayList<MasterErrorList> inputValidations = new ArrayList<>();
        ArrayList<MasterErrorList> actualBasicFileValidationsResult = channelAdminUserHierarchyServiceImpl
                .basicFileValidations(request, response, "", "Category Type", "Geo Domain Type", locale, inputValidations);
        assertSame(inputValidations, actualBasicFileValidationsResult);
        assertEquals(2, actualBasicFileValidationsResult.size());
        verify(request, atLeast(1)).getFileAttachment();
        verify(request, atLeast(1)).getFileName();
        verify(request).getFileType();
        verify(request).setFileAttachment(Mockito.<String>any());
        verify(request).setFileName(Mockito.<String>any());
        verify(request).setFileType(Mockito.<String>any());
        assertEquals("File is Empty", response.getMessage());
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#basicFileValidations(BulkModifyUserRequestVO, BulkModifyUserResponseVO, String, String, String, Locale, ArrayList)}
     */
    @Test
    public void testBasicFileValidations5() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.basicFileValidations(ChannelAdminUserHierarchyServiceImpl.java:1135)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelAdminUserHierarchyServiceImpl channelAdminUserHierarchyServiceImpl = new ChannelAdminUserHierarchyServiceImpl();
        BulkModifyUserRequestVO request = mock(BulkModifyUserRequestVO.class);
        when(request.getFileAttachment()).thenReturn("");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BulkModifyUserResponseVO response = new BulkModifyUserResponseVO();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus("Status");
        response.setTotalRecords(1);
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        ArrayList<MasterErrorList> inputValidations = new ArrayList<>();
        ArrayList<MasterErrorList> actualBasicFileValidationsResult = channelAdminUserHierarchyServiceImpl
                .basicFileValidations(request, response, "42", "Category Type", "Geo Domain Type", locale, inputValidations);
        assertSame(inputValidations, actualBasicFileValidationsResult);
        assertEquals(2, actualBasicFileValidationsResult.size());
        verify(request, atLeast(1)).getFileAttachment();
        verify(request, atLeast(1)).getFileName();
        verify(request).getFileType();
        verify(request).setFileAttachment(Mockito.<String>any());
        verify(request).setFileName(Mockito.<String>any());
        verify(request).setFileType(Mockito.<String>any());
        assertEquals("File is Empty", response.getMessage());
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#basicFileValidations(BulkModifyUserRequestVO, BulkModifyUserResponseVO, String, String, String, Locale, ArrayList)}
     */
    @Test
    public void testBasicFileValidations6() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.basicFileValidations(ChannelAdminUserHierarchyServiceImpl.java:1135)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelAdminUserHierarchyServiceImpl channelAdminUserHierarchyServiceImpl = new ChannelAdminUserHierarchyServiceImpl();
        BulkModifyUserRequestVO request = mock(BulkModifyUserRequestVO.class);
        when(request.getFileAttachment()).thenReturn("");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BulkModifyUserResponseVO response = new BulkModifyUserResponseVO();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus("Status");
        response.setTotalRecords(1);
        response.setValidRecords(1);
        ArrayList<MasterErrorList> inputValidations = new ArrayList<>();
        ArrayList<MasterErrorList> actualBasicFileValidationsResult = channelAdminUserHierarchyServiceImpl
                .basicFileValidations(request, response, "", "Category Type", "Geo Domain Type", null, inputValidations);
        assertSame(inputValidations, actualBasicFileValidationsResult);
        assertEquals(2, actualBasicFileValidationsResult.size());
        verify(request, atLeast(1)).getFileAttachment();
        verify(request, atLeast(1)).getFileName();
        verify(request).getFileType();
        verify(request).setFileAttachment(Mockito.<String>any());
        verify(request).setFileName(Mockito.<String>any());
        verify(request).setFileType(Mockito.<String>any());
        assertEquals("File is Empty", response.getMessage());
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#uploadAndValidateModifyBulkUserFile(Connection, MComConnectionI, ChannelUserVO, BulkModifyUserRequestVO, BulkModifyUserResponseVO, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUploadAndValidateModifyBulkUserFile() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        //Connection con = null;
        com.btsl.util.JUnitConfig.init();

        //MComConnectionI mcomCon = null;
        ChannelUserVO userVO =  com.btsl.util.JUnitConfig.getChannelUserVO();

        BulkModifyUserRequestVO request = new BulkModifyUserRequestVO();
        request.setFileType("String");
        request.setFileAttachment("String");
        request.setFileName("String");
        BulkModifyUserResponseVO response = null;
        ArrayList fileErrorList = null;

        // Act
        boolean actualUploadAndValidateModifyBulkUserFileResult = this.channelAdminUserHierarchyServiceImpl
                .uploadAndValidateModifyBulkUserFile(com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), userVO, request, response, fileErrorList);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#processUploadedModifyBulkUserFile(Connection, MComConnectionI, ChannelUserVO, String, String, BulkModifyUserRequestVO, BulkModifyUserResponseVO, HttpServletResponse, ArrayList, int)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessUploadedModifyBulkUserFile() throws BTSLBaseException, IOException, SQLException {
        com.btsl.util.JUnitConfig.init(this.getClass().toString()); //Auto replace


        //MComConnectionI mcomCon = null;
        ChannelUserVO userVO = JUnitConfig.getChannelUserVO();
        when(userVO.getUserID()).thenReturn("String");

        String categoryType = "TEST";
        String geoDomainType = "TEST";

        BulkModifyUserRequestVO request = new BulkModifyUserRequestVO();

        String pattern = "ddMMyyyyhhmmss";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());


        //request.setFileName("TEST" + dateInString);
        request.setFileName("TEST");
        request.setFileAttachment("QSxCLEMsRA0KMSwyLDMsNA0KMSwyLDMsNA0K");
        request.setFileType("csv");

        BulkModifyUserResponseVO response = new BulkModifyUserResponseVO();


        HttpServletResponse responseSwag = mock(HttpServletResponse.class);

        ArrayList fileErrorList = new ArrayList();
        fileErrorList.add("TEST");

        int emptyRowCount = 0;

        // Act
        boolean actualProcessUploadedModifyBulkUserFileResult = this.channelAdminUserHierarchyServiceImpl
                .processUploadedModifyBulkUserFile(com.btsl.util.JUnitConfig.getConnection(), com.btsl.util.JUnitConfig.getMComConnection(), userVO, categoryType, geoDomainType, request, response,
                        responseSwag, fileErrorList, emptyRowCount);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#fetchChannelUsersByStatusForBarredfrdltReq(Connection, BarredusersrequestVO)}
     */
    @Test
    public void testFetchChannelUsersByStatusForBarredfrdltReq() throws BTSLBaseException {
        //Connection con = mock(Connection.class);

        com.btsl.util.JUnitConfig.init(this.getClass().toString());

        BarredusersrequestVO requestVo = new BarredusersrequestVO();
        requestVo.setApprovalLevel("Approval Level");
        requestVo.setCategory("Category");
        requestVo.setDomain("Domain");
        requestVo.setExternalcode("Externalcode");
        requestVo.setGeography("Geography");
        requestVo.setLoggedInUserUserid("Logged In User Userid");
        requestVo.setLoggedUserNeworkCode("Logged User Nework Code");
        requestVo.setLoginID("Login ID");
        requestVo.setMobileNumber("42");
        requestVo.setSearchType("Search Type");
        requestVo.setUserStatus(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST);

        channelAdminUserHierarchyServiceImpl.fetchChannelUsersByStatusForBarredfrdltReq(com.btsl.util.JUnitConfig.getConnection(), requestVo);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#fetchChannelUsersByStatusForBarredfrdltReq(Connection, BarredusersrequestVO)}
     */
    @Test
    public void testFetchChannelUsersByStatusForBarredfrdltReq2() throws BTSLBaseException {

        JUnitConfig.init();

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        BarredusersrequestVO requestVo = new BarredusersrequestVO();
        requestVo.setApprovalLevel("Approval Level");
        requestVo.setCategory("Category");
        requestVo.setDomain("Domain");
        requestVo.setExternalcode("Externalcode");
        requestVo.setGeography("Geography");
        requestVo.setLoggedInUserUserid("Logged In User Userid");
        requestVo.setLoggedUserNeworkCode("Logged User Nework Code");
        requestVo.setLoginID("Login ID");
        requestVo.setMobileNumber("42");
        requestVo.setSearchType("Search Type");
        requestVo.setUserStatus("User Status");
        thrown.expect(BTSLBaseException.class);
        channelAdminUserHierarchyServiceImpl.fetchChannelUsersByStatusForBarredfrdltReq(JUnitConfig.getConnection(), requestVo);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#fetchChannelUsersByStatusForBarredfrdltReq(Connection, BarredusersrequestVO)}
     */
    @Test
    public void testFetchChannelUsersByStatusForBarredfrdltReq3() throws BTSLBaseException {
        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        BarredusersrequestVO requestVo = mock(BarredusersrequestVO.class);
        when(requestVo.getUserStatus()).thenReturn("");
        doNothing().when(requestVo).setApprovalLevel(Mockito.<String>any());
        doNothing().when(requestVo).setCategory(Mockito.<String>any());
        doNothing().when(requestVo).setDomain(Mockito.<String>any());
        doNothing().when(requestVo).setExternalcode(Mockito.<String>any());
        doNothing().when(requestVo).setGeography(Mockito.<String>any());
        doNothing().when(requestVo).setLoggedInUserUserid(Mockito.<String>any());
        doNothing().when(requestVo).setLoggedUserNeworkCode(Mockito.<String>any());
        doNothing().when(requestVo).setLoginID(Mockito.<String>any());
        doNothing().when(requestVo).setMobileNumber(Mockito.<String>any());
        doNothing().when(requestVo).setSearchType(Mockito.<String>any());
        doNothing().when(requestVo).setUserStatus(Mockito.<String>any());
        requestVo.setApprovalLevel("Approval Level");
        requestVo.setCategory("Category");
        requestVo.setDomain("Domain");
        requestVo.setExternalcode("Externalcode");
        requestVo.setGeography("Geography");
        requestVo.setLoggedInUserUserid("Logged In User Userid");
        requestVo.setLoggedUserNeworkCode("Logged User Nework Code");
        requestVo.setLoginID("Login ID");
        requestVo.setMobileNumber("42");
        requestVo.setSearchType("Search Type");
        requestVo.setUserStatus("User Status");
        thrown.expect(BTSLBaseException.class);
        channelAdminUserHierarchyServiceImpl.fetchChannelUsersByStatusForBarredfrdltReq(com.btsl.util.JUnitConfig.getConnection(), requestVo);
        verify(requestVo).getUserStatus();
        verify(requestVo).setApprovalLevel(Mockito.<String>any());
        verify(requestVo).setCategory(Mockito.<String>any());
        verify(requestVo).setDomain(Mockito.<String>any());
        verify(requestVo).setExternalcode(Mockito.<String>any());
        verify(requestVo).setGeography(Mockito.<String>any());
        verify(requestVo).setLoggedInUserUserid(Mockito.<String>any());
        verify(requestVo).setLoggedUserNeworkCode(Mockito.<String>any());
        verify(requestVo).setLoginID(Mockito.<String>any());
        verify(requestVo).setMobileNumber(Mockito.<String>any());
        verify(requestVo).setSearchType(Mockito.<String>any());
        verify(requestVo).setUserStatus(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#approvalOrRejectBarredUser(Connection, ApprovalBarredForDltRequestVO, OAuthUserData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testApprovalOrRejectBarredUser() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.approvalOrRejectBarredUser(ChannelAdminUserHierarchyServiceImpl.java:4708)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        com.btsl.util.JUnitConfig.init();

        ApprovalBarredForDltRequestVO approvalBarredForDltRequestVO = new ApprovalBarredForDltRequestVO();
        approvalBarredForDltRequestVO.setAction("Action");
        approvalBarredForDltRequestVO.setLoginId("42");
        approvalBarredForDltRequestVO.setRemarks("Remarks");
        approvalBarredForDltRequestVO.setRequestType("Request Type");

        OAuthUserData oauthUserData = new OAuthUserData();
        oauthUserData.setExtcode("Extcode");
        oauthUserData.setLoginid("Loginid");
        oauthUserData.setMsisdn("Msisdn");
        oauthUserData.setPassword("iloveyou");
        oauthUserData.setPin("Pin");
        oauthUserData.setUserid("Userid");
        channelAdminUserHierarchyServiceImpl.approvalOrRejectBarredUser(com.btsl.util.JUnitConfig.getConnection(), approvalBarredForDltRequestVO,
                oauthUserData);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#approvalOrRejectBarredUser(Connection, ApprovalBarredForDltRequestVO, OAuthUserData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testApprovalOrRejectBarredUser2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.approvalOrRejectBarredUser(ChannelAdminUserHierarchyServiceImpl.java:4708)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        ApprovalBarredForDltRequestVO approvalBarredForDltRequestVO = new ApprovalBarredForDltRequestVO();
        approvalBarredForDltRequestVO.setAction("Action");
        approvalBarredForDltRequestVO.setLoginId("42");
        approvalBarredForDltRequestVO.setRemarks("Remarks");
        approvalBarredForDltRequestVO.setRequestType("Request Type");

        OAuthUserData oauthUserData = new OAuthUserData();
        oauthUserData.setExtcode("Extcode");
        oauthUserData.setLoginid("Loginid");
        oauthUserData.setMsisdn("Msisdn");
        oauthUserData.setPassword("iloveyou");
        oauthUserData.setPin("Pin");
        oauthUserData.setUserid("Userid");
        channelAdminUserHierarchyServiceImpl.approvalOrRejectBarredUser(JUnitConfig.getConnection(), approvalBarredForDltRequestVO,
                oauthUserData);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#approvalOrRejectBarredUser(Connection, ApprovalBarredForDltRequestVO, OAuthUserData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testApprovalOrRejectBarredUser3() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.ChannelAdminUserHierarchyServiceImpl.approvalOrRejectBarredUser(ChannelAdminUserHierarchyServiceImpl.java:4708)
        //   See https://diff.blue/R013 to resolve this issue.

        AfterCloseLoggingConnectionWrapper con = new AfterCloseLoggingConnectionWrapper();

        ApprovalBarredForDltRequestVO approvalBarredForDltRequestVO = new ApprovalBarredForDltRequestVO();
        approvalBarredForDltRequestVO.setAction("Action");
        approvalBarredForDltRequestVO.setLoginId("42");
        approvalBarredForDltRequestVO.setRemarks("Remarks");
        approvalBarredForDltRequestVO.setRequestType("Request Type");

        OAuthUserData oauthUserData = new OAuthUserData();
        oauthUserData.setExtcode("Extcode");
        oauthUserData.setLoginid("Loginid");
        oauthUserData.setMsisdn("Msisdn");
        oauthUserData.setPassword("iloveyou");
        oauthUserData.setPin("Pin");
        oauthUserData.setUserid("Userid");
        channelAdminUserHierarchyServiceImpl.approvalOrRejectBarredUser(JUnitConfig.getConnection(), approvalBarredForDltRequestVO,
                oauthUserData);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#downloadErrorLogFile(ArrayList, UserVO, ErrorFileResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadErrorLogFile() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
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

        ErrorFileResponse response = new ErrorFileResponse();
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        channelAdminUserHierarchyServiceImpl.downloadErrorLogFile(errorList, userVO, response,
                response1);
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#cellValueNull(Cell)}
     */
    @Test
    public void testCellValueNull() {
        assertEquals("", channelAdminUserHierarchyServiceImpl.cellValueNull(new StreamingCell(1, 1, true)));
    }

    /**
     * Method under test: {@link ChannelAdminUserHierarchyServiceImpl#cellValueNull(Cell)}
     */
    @Test
    public void testCellValueNull2() {
        StreamingCell cell = mock(StreamingCell.class);
        when(cell.getStringCellValue()).thenReturn("42");
        assertEquals("42", channelAdminUserHierarchyServiceImpl.cellValueNull(cell));
        verify(cell).getStringCellValue();
    }
}

