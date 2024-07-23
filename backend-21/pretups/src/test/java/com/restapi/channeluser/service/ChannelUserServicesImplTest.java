package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaData;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchAdminRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.SAPResponseVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.JUnitConfig;
import com.web.user.web.UserForm;

import java.io.UnsupportedEncodingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ChannelUserServicesImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelUserServicesImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ChannelUserServicesImpl channelUserServicesImpl;

    /**
     * Method under test: {@link ChannelUserServicesImpl#searchArea(String, Connection, AreaSearchRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSearchArea() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.searchArea(ChannelUserServicesImpl.java:659)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        AreaSearchRequestVO requestVO = new AreaSearchRequestVO("Domain Code", "Category Code", "Geo Domain Code", "42",
                "Request Type");

        channelUserServicesImpl.searchArea("42",com.btsl.util.JUnitConfig.getConnection(), requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#searchArea(String, Connection, AreaSearchRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSearchArea2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.searchArea(ChannelUserServicesImpl.java:659)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        channelUserServicesImpl.searchArea("42",com.btsl.util.JUnitConfig.getConnection(), null, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#searchAreaAdmin(String, Connection, AreaSearchAdminRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSearchAreaAdmin() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.searchAreaAdmin(ChannelUserServicesImpl.java:684)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        AreaSearchAdminRequestVO requestVO = new AreaSearchAdminRequestVO("Domain Code", "Category Code",
                "Geo Domain Code", "42", "Request Type");

        channelUserServicesImpl.searchAreaAdmin("42",com.btsl.util.JUnitConfig.getConnection(), requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#searchAreaAdmin(String, Connection, AreaSearchAdminRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSearchAreaAdmin2() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.searchAreaAdmin(ChannelUserServicesImpl.java:684)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
        AreaSearchAdminRequestVO requestVO = new AreaSearchAdminRequestVO("Domain Code", "Category Code",
                "Geo Domain Code", "42", "Request Type");

        channelUserServicesImpl.searchAreaAdmin("42",com.btsl.util.JUnitConfig.getConnection(), requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateSelectedGeographyAndDomain(ArrayList, ArrayList, String, CategoryVO, String)}
     */
    @Test
    public void testValidateSelectedGeographyAndDomain() throws Exception {
        ArrayList domainList = new ArrayList();
        ArrayList<UserGeographiesVO> geographyList = new ArrayList<>();

        CategoryVO selectedCategoryVO = new CategoryVO();
        selectedCategoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        selectedCategoryVO.setAgentAllowed("Agent Allowed");
        selectedCategoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        selectedCategoryVO.setAgentCategoryCode("Agent Category Code");
        selectedCategoryVO.setAgentCategoryName("Agent Category Name");
        selectedCategoryVO.setAgentCategoryStatus("Agent Category Status");
        selectedCategoryVO.setAgentCategoryStatusList(new ArrayList());
        selectedCategoryVO.setAgentCategoryType("Agent Category Type");
        selectedCategoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        selectedCategoryVO.setAgentCp2pPayee("Cp2p Payee");
        selectedCategoryVO.setAgentCp2pPayer("Cp2p Payer");
        selectedCategoryVO.setAgentCp2pWithinList("Cp2p Within List");
        selectedCategoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        selectedCategoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        selectedCategoryVO.setAgentDomainName("Agent Domain Name");
        selectedCategoryVO.setAgentFixedRoles("Agent Fixed Roles");
        selectedCategoryVO.setAgentGatewayName("Agent Gateway Name");
        selectedCategoryVO.setAgentGatewayType("Agent Gateway Type");
        selectedCategoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        selectedCategoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        selectedCategoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        selectedCategoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        selectedCategoryVO.setAgentMaxLoginCount(3L);
        selectedCategoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        selectedCategoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        selectedCategoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        selectedCategoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        selectedCategoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        selectedCategoryVO.setAgentParentOrOwnerRadioValue("42");
        selectedCategoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        selectedCategoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        selectedCategoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        selectedCategoryVO.setAgentRoleName("Agent Role Name");
        selectedCategoryVO.setAgentRoleTypeList(new ArrayList());
        selectedCategoryVO.setAgentRolesMapSelected(new HashMap());
        selectedCategoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        selectedCategoryVO.setAgentServiceAllowed("Agent Service Allowed");
        selectedCategoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        selectedCategoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        selectedCategoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        selectedCategoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        selectedCategoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        selectedCategoryVO.setAllowedGatewayTypes(new ArrayList());
        selectedCategoryVO.setAuthenticationType("Type");
        selectedCategoryVO.setCategoryCode("Category Code");
        selectedCategoryVO.setCategoryName("Category Name");
        selectedCategoryVO.setCategorySequenceNumber(10);
        selectedCategoryVO.setCategoryStatus("Category Status");
        selectedCategoryVO.setCategoryType("Category Type");
        selectedCategoryVO.setCategoryTypeCode("Category Type Code");
        selectedCategoryVO.setCp2pPayee("Payee");
        selectedCategoryVO.setCp2pPayer("Payer");
        selectedCategoryVO.setCp2pWithinList("Within List");
        selectedCategoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        selectedCategoryVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setDisplayAllowed("Display Allowed");
        selectedCategoryVO.setDomainAllowed("Domain Allowed");
        selectedCategoryVO.setDomainCodeforCategory("Domain Codefor Category");
        selectedCategoryVO.setDomainName("Domain Name");
        selectedCategoryVO.setDomainTypeCode("Domain Type Code");
        selectedCategoryVO.setFixedDomains("Fixed Domains");
        selectedCategoryVO.setFixedRoles("Fixed Roles");
        selectedCategoryVO.setGeographicalDomainSeqNo(1);
        selectedCategoryVO.setGrphDomainSequenceNo(1);
        selectedCategoryVO.setGrphDomainType("Grph Domain Type");
        selectedCategoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        selectedCategoryVO.setHierarchyAllowed("Hierarchy Allowed");
        selectedCategoryVO.setLastModifiedTime(1L);
        selectedCategoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        selectedCategoryVO.setMaxLoginCount(3L);
        selectedCategoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        selectedCategoryVO.setMaxTxnMsisdnInt(3);
        selectedCategoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        selectedCategoryVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setModifyAllowed("Modify Allowed");
        selectedCategoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        selectedCategoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        selectedCategoryVO.setNumberOfCategoryForDomain(10);
        selectedCategoryVO.setOutletsAllowed("Outlets Allowed");
        selectedCategoryVO.setParentCategoryCode("Parent Category Code");
        selectedCategoryVO.setParentOrOwnerRadioValue("42");
        selectedCategoryVO.setProductTypeAllowed("Product Type Allowed");
        selectedCategoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        selectedCategoryVO.setRadioIndex(1);
        selectedCategoryVO.setRechargeByParentOnly("By Parent Only");
        selectedCategoryVO.setRecordCount(3);
        selectedCategoryVO.setRestrictedMsisdns("Restricted Msisdns");
        selectedCategoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        selectedCategoryVO.setSequenceNumber(10);
        selectedCategoryVO.setServiceAllowed("Service Allowed");
        selectedCategoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        selectedCategoryVO.setTransferToListOnly("Transfer To List Only");
        selectedCategoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        selectedCategoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        selectedCategoryVO.setUserIdPrefix("User Id Prefix");
        selectedCategoryVO.setViewOnNetworkBlock("View On Network Block");
        selectedCategoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.validateSelectedGeographyAndDomain(domainList, geographyList, "Selected Domain Code",
                selectedCategoryVO, "Selected Geo Domain Code");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateSelectedGeographyAndDomain(ArrayList, ArrayList, String, CategoryVO, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateSelectedGeographyAndDomain2() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.validateSelectedGeographyAndDomain(ChannelUserServicesImpl.java:1117)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList domainList = new ArrayList();
        domainList.add(new ListValueVO());
        ArrayList<UserGeographiesVO> geographyList = new ArrayList<>();
        CategoryVO selectedCategoryVO = mock(CategoryVO.class);
        doNothing().when(selectedCategoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(selectedCategoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setLastModifiedTime(anyLong());
        doNothing().when(selectedCategoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(selectedCategoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(selectedCategoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRadioIndex(anyInt());
        doNothing().when(selectedCategoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRecordCount(anyInt());
        doNothing().when(selectedCategoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setWebInterfaceAllowed(Mockito.<String>any());
        selectedCategoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        selectedCategoryVO.setAgentAllowed("Agent Allowed");
        selectedCategoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        selectedCategoryVO.setAgentCategoryCode("Agent Category Code");
        selectedCategoryVO.setAgentCategoryName("Agent Category Name");
        selectedCategoryVO.setAgentCategoryStatus("Agent Category Status");
        selectedCategoryVO.setAgentCategoryStatusList(new ArrayList());
        selectedCategoryVO.setAgentCategoryType("Agent Category Type");
        selectedCategoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        selectedCategoryVO.setAgentCp2pPayee("Cp2p Payee");
        selectedCategoryVO.setAgentCp2pPayer("Cp2p Payer");
        selectedCategoryVO.setAgentCp2pWithinList("Cp2p Within List");
        selectedCategoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        selectedCategoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        selectedCategoryVO.setAgentDomainName("Agent Domain Name");
        selectedCategoryVO.setAgentFixedRoles("Agent Fixed Roles");
        selectedCategoryVO.setAgentGatewayName("Agent Gateway Name");
        selectedCategoryVO.setAgentGatewayType("Agent Gateway Type");
        selectedCategoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        selectedCategoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        selectedCategoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        selectedCategoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        selectedCategoryVO.setAgentMaxLoginCount(3L);
        selectedCategoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        selectedCategoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        selectedCategoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        selectedCategoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        selectedCategoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        selectedCategoryVO.setAgentParentOrOwnerRadioValue("42");
        selectedCategoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        selectedCategoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        selectedCategoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        selectedCategoryVO.setAgentRoleName("Agent Role Name");
        selectedCategoryVO.setAgentRoleTypeList(new ArrayList());
        selectedCategoryVO.setAgentRolesMapSelected(new HashMap());
        selectedCategoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        selectedCategoryVO.setAgentServiceAllowed("Agent Service Allowed");
        selectedCategoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        selectedCategoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        selectedCategoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        selectedCategoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        selectedCategoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        selectedCategoryVO.setAllowedGatewayTypes(new ArrayList());
        selectedCategoryVO.setAuthenticationType("Type");
        selectedCategoryVO.setCategoryCode("Category Code");
        selectedCategoryVO.setCategoryName("Category Name");
        selectedCategoryVO.setCategorySequenceNumber(10);
        selectedCategoryVO.setCategoryStatus("Category Status");
        selectedCategoryVO.setCategoryType("Category Type");
        selectedCategoryVO.setCategoryTypeCode("Category Type Code");
        selectedCategoryVO.setCp2pPayee("Payee");
        selectedCategoryVO.setCp2pPayer("Payer");
        selectedCategoryVO.setCp2pWithinList("Within List");
        selectedCategoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        selectedCategoryVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setDisplayAllowed("Display Allowed");
        selectedCategoryVO.setDomainAllowed("Domain Allowed");
        selectedCategoryVO.setDomainCodeforCategory("Domain Codefor Category");
        selectedCategoryVO.setDomainName("Domain Name");
        selectedCategoryVO.setDomainTypeCode("Domain Type Code");
        selectedCategoryVO.setFixedDomains("Fixed Domains");
        selectedCategoryVO.setFixedRoles("Fixed Roles");
        selectedCategoryVO.setGeographicalDomainSeqNo(1);
        selectedCategoryVO.setGrphDomainSequenceNo(1);
        selectedCategoryVO.setGrphDomainType("Grph Domain Type");
        selectedCategoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        selectedCategoryVO.setHierarchyAllowed("Hierarchy Allowed");
        selectedCategoryVO.setLastModifiedTime(1L);
        selectedCategoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        selectedCategoryVO.setMaxLoginCount(3L);
        selectedCategoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        selectedCategoryVO.setMaxTxnMsisdnInt(3);
        selectedCategoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        selectedCategoryVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setModifyAllowed("Modify Allowed");
        selectedCategoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        selectedCategoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        selectedCategoryVO.setNumberOfCategoryForDomain(10);
        selectedCategoryVO.setOutletsAllowed("Outlets Allowed");
        selectedCategoryVO.setParentCategoryCode("Parent Category Code");
        selectedCategoryVO.setParentOrOwnerRadioValue("42");
        selectedCategoryVO.setProductTypeAllowed("Product Type Allowed");
        selectedCategoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        selectedCategoryVO.setRadioIndex(1);
        selectedCategoryVO.setRechargeByParentOnly("By Parent Only");
        selectedCategoryVO.setRecordCount(3);
        selectedCategoryVO.setRestrictedMsisdns("Restricted Msisdns");
        selectedCategoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        selectedCategoryVO.setSequenceNumber(10);
        selectedCategoryVO.setServiceAllowed("Service Allowed");
        selectedCategoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        selectedCategoryVO.setTransferToListOnly("Transfer To List Only");
        selectedCategoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        selectedCategoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        selectedCategoryVO.setUserIdPrefix("User Id Prefix");
        selectedCategoryVO.setViewOnNetworkBlock("View On Network Block");
        selectedCategoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        channelUserServicesImpl.validateSelectedGeographyAndDomain(domainList, geographyList, "Selected Domain Code",
                selectedCategoryVO, "Selected Geo Domain Code");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateSelectedGeographyAndDomain(ArrayList, ArrayList, String, CategoryVO, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateSelectedGeographyAndDomain3() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.validateSelectedGeographyAndDomain(ChannelUserServicesImpl.java:1117)
        //   See https://diff.blue/R013 to resolve this issue.

        ArrayList domainList = new ArrayList();
        domainList.add(null);
        ArrayList<UserGeographiesVO> geographyList = new ArrayList<>();
        CategoryVO selectedCategoryVO = mock(CategoryVO.class);
        doNothing().when(selectedCategoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(selectedCategoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setLastModifiedTime(anyLong());
        doNothing().when(selectedCategoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(selectedCategoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(selectedCategoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRadioIndex(anyInt());
        doNothing().when(selectedCategoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRecordCount(anyInt());
        doNothing().when(selectedCategoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setWebInterfaceAllowed(Mockito.<String>any());
        selectedCategoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        selectedCategoryVO.setAgentAllowed("Agent Allowed");
        selectedCategoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        selectedCategoryVO.setAgentCategoryCode("Agent Category Code");
        selectedCategoryVO.setAgentCategoryName("Agent Category Name");
        selectedCategoryVO.setAgentCategoryStatus("Agent Category Status");
        selectedCategoryVO.setAgentCategoryStatusList(new ArrayList());
        selectedCategoryVO.setAgentCategoryType("Agent Category Type");
        selectedCategoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        selectedCategoryVO.setAgentCp2pPayee("Cp2p Payee");
        selectedCategoryVO.setAgentCp2pPayer("Cp2p Payer");
        selectedCategoryVO.setAgentCp2pWithinList("Cp2p Within List");
        selectedCategoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        selectedCategoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        selectedCategoryVO.setAgentDomainName("Agent Domain Name");
        selectedCategoryVO.setAgentFixedRoles("Agent Fixed Roles");
        selectedCategoryVO.setAgentGatewayName("Agent Gateway Name");
        selectedCategoryVO.setAgentGatewayType("Agent Gateway Type");
        selectedCategoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        selectedCategoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        selectedCategoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        selectedCategoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        selectedCategoryVO.setAgentMaxLoginCount(3L);
        selectedCategoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        selectedCategoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        selectedCategoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        selectedCategoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        selectedCategoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        selectedCategoryVO.setAgentParentOrOwnerRadioValue("42");
        selectedCategoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        selectedCategoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        selectedCategoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        selectedCategoryVO.setAgentRoleName("Agent Role Name");
        selectedCategoryVO.setAgentRoleTypeList(new ArrayList());
        selectedCategoryVO.setAgentRolesMapSelected(new HashMap());
        selectedCategoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        selectedCategoryVO.setAgentServiceAllowed("Agent Service Allowed");
        selectedCategoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        selectedCategoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        selectedCategoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        selectedCategoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        selectedCategoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        selectedCategoryVO.setAllowedGatewayTypes(new ArrayList());
        selectedCategoryVO.setAuthenticationType("Type");
        selectedCategoryVO.setCategoryCode("Category Code");
        selectedCategoryVO.setCategoryName("Category Name");
        selectedCategoryVO.setCategorySequenceNumber(10);
        selectedCategoryVO.setCategoryStatus("Category Status");
        selectedCategoryVO.setCategoryType("Category Type");
        selectedCategoryVO.setCategoryTypeCode("Category Type Code");
        selectedCategoryVO.setCp2pPayee("Payee");
        selectedCategoryVO.setCp2pPayer("Payer");
        selectedCategoryVO.setCp2pWithinList("Within List");
        selectedCategoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        selectedCategoryVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setDisplayAllowed("Display Allowed");
        selectedCategoryVO.setDomainAllowed("Domain Allowed");
        selectedCategoryVO.setDomainCodeforCategory("Domain Codefor Category");
        selectedCategoryVO.setDomainName("Domain Name");
        selectedCategoryVO.setDomainTypeCode("Domain Type Code");
        selectedCategoryVO.setFixedDomains("Fixed Domains");
        selectedCategoryVO.setFixedRoles("Fixed Roles");
        selectedCategoryVO.setGeographicalDomainSeqNo(1);
        selectedCategoryVO.setGrphDomainSequenceNo(1);
        selectedCategoryVO.setGrphDomainType("Grph Domain Type");
        selectedCategoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        selectedCategoryVO.setHierarchyAllowed("Hierarchy Allowed");
        selectedCategoryVO.setLastModifiedTime(1L);
        selectedCategoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        selectedCategoryVO.setMaxLoginCount(3L);
        selectedCategoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        selectedCategoryVO.setMaxTxnMsisdnInt(3);
        selectedCategoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        selectedCategoryVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setModifyAllowed("Modify Allowed");
        selectedCategoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        selectedCategoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        selectedCategoryVO.setNumberOfCategoryForDomain(10);
        selectedCategoryVO.setOutletsAllowed("Outlets Allowed");
        selectedCategoryVO.setParentCategoryCode("Parent Category Code");
        selectedCategoryVO.setParentOrOwnerRadioValue("42");
        selectedCategoryVO.setProductTypeAllowed("Product Type Allowed");
        selectedCategoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        selectedCategoryVO.setRadioIndex(1);
        selectedCategoryVO.setRechargeByParentOnly("By Parent Only");
        selectedCategoryVO.setRecordCount(3);
        selectedCategoryVO.setRestrictedMsisdns("Restricted Msisdns");
        selectedCategoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        selectedCategoryVO.setSequenceNumber(10);
        selectedCategoryVO.setServiceAllowed("Service Allowed");
        selectedCategoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        selectedCategoryVO.setTransferToListOnly("Transfer To List Only");
        selectedCategoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        selectedCategoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        selectedCategoryVO.setUserIdPrefix("User Id Prefix");
        selectedCategoryVO.setViewOnNetworkBlock("View On Network Block");
        selectedCategoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        channelUserServicesImpl.validateSelectedGeographyAndDomain(domainList, geographyList, "Selected Domain Code",
                selectedCategoryVO, "Selected Geo Domain Code");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateSelectedGeographyAndDomain(ArrayList, ArrayList, String, CategoryVO, String)}
     */
    @Test
    public void testValidateSelectedGeographyAndDomain4() throws Exception {
        ArrayList domainList = new ArrayList();
        domainList
                .add(new ListValueVO("validateSelectedGeographyAndDomain", "42", "42", "validateSelectedGeographyAndDomain"));
        ArrayList<UserGeographiesVO> geographyList = new ArrayList<>();
        CategoryVO selectedCategoryVO = mock(CategoryVO.class);
        doNothing().when(selectedCategoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(selectedCategoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setLastModifiedTime(anyLong());
        doNothing().when(selectedCategoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(selectedCategoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(selectedCategoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRadioIndex(anyInt());
        doNothing().when(selectedCategoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRecordCount(anyInt());
        doNothing().when(selectedCategoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setWebInterfaceAllowed(Mockito.<String>any());
        selectedCategoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        selectedCategoryVO.setAgentAllowed("Agent Allowed");
        selectedCategoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        selectedCategoryVO.setAgentCategoryCode("Agent Category Code");
        selectedCategoryVO.setAgentCategoryName("Agent Category Name");
        selectedCategoryVO.setAgentCategoryStatus("Agent Category Status");
        selectedCategoryVO.setAgentCategoryStatusList(new ArrayList());
        selectedCategoryVO.setAgentCategoryType("Agent Category Type");
        selectedCategoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        selectedCategoryVO.setAgentCp2pPayee("Cp2p Payee");
        selectedCategoryVO.setAgentCp2pPayer("Cp2p Payer");
        selectedCategoryVO.setAgentCp2pWithinList("Cp2p Within List");
        selectedCategoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        selectedCategoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        selectedCategoryVO.setAgentDomainName("Agent Domain Name");
        selectedCategoryVO.setAgentFixedRoles("Agent Fixed Roles");
        selectedCategoryVO.setAgentGatewayName("Agent Gateway Name");
        selectedCategoryVO.setAgentGatewayType("Agent Gateway Type");
        selectedCategoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        selectedCategoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        selectedCategoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        selectedCategoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        selectedCategoryVO.setAgentMaxLoginCount(3L);
        selectedCategoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        selectedCategoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        selectedCategoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        selectedCategoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        selectedCategoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        selectedCategoryVO.setAgentParentOrOwnerRadioValue("42");
        selectedCategoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        selectedCategoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        selectedCategoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        selectedCategoryVO.setAgentRoleName("Agent Role Name");
        selectedCategoryVO.setAgentRoleTypeList(new ArrayList());
        selectedCategoryVO.setAgentRolesMapSelected(new HashMap());
        selectedCategoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        selectedCategoryVO.setAgentServiceAllowed("Agent Service Allowed");
        selectedCategoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        selectedCategoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        selectedCategoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        selectedCategoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        selectedCategoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        selectedCategoryVO.setAllowedGatewayTypes(new ArrayList());
        selectedCategoryVO.setAuthenticationType("Type");
        selectedCategoryVO.setCategoryCode("Category Code");
        selectedCategoryVO.setCategoryName("Category Name");
        selectedCategoryVO.setCategorySequenceNumber(10);
        selectedCategoryVO.setCategoryStatus("Category Status");
        selectedCategoryVO.setCategoryType("Category Type");
        selectedCategoryVO.setCategoryTypeCode("Category Type Code");
        selectedCategoryVO.setCp2pPayee("Payee");
        selectedCategoryVO.setCp2pPayer("Payer");
        selectedCategoryVO.setCp2pWithinList("Within List");
        selectedCategoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        selectedCategoryVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setDisplayAllowed("Display Allowed");
        selectedCategoryVO.setDomainAllowed("Domain Allowed");
        selectedCategoryVO.setDomainCodeforCategory("Domain Codefor Category");
        selectedCategoryVO.setDomainName("Domain Name");
        selectedCategoryVO.setDomainTypeCode("Domain Type Code");
        selectedCategoryVO.setFixedDomains("Fixed Domains");
        selectedCategoryVO.setFixedRoles("Fixed Roles");
        selectedCategoryVO.setGeographicalDomainSeqNo(1);
        selectedCategoryVO.setGrphDomainSequenceNo(1);
        selectedCategoryVO.setGrphDomainType("Grph Domain Type");
        selectedCategoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        selectedCategoryVO.setHierarchyAllowed("Hierarchy Allowed");
        selectedCategoryVO.setLastModifiedTime(1L);
        selectedCategoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        selectedCategoryVO.setMaxLoginCount(3L);
        selectedCategoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        selectedCategoryVO.setMaxTxnMsisdnInt(3);
        selectedCategoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        selectedCategoryVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setModifyAllowed("Modify Allowed");
        selectedCategoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        selectedCategoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        selectedCategoryVO.setNumberOfCategoryForDomain(10);
        selectedCategoryVO.setOutletsAllowed("Outlets Allowed");
        selectedCategoryVO.setParentCategoryCode("Parent Category Code");
        selectedCategoryVO.setParentOrOwnerRadioValue("42");
        selectedCategoryVO.setProductTypeAllowed("Product Type Allowed");
        selectedCategoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        selectedCategoryVO.setRadioIndex(1);
        selectedCategoryVO.setRechargeByParentOnly("By Parent Only");
        selectedCategoryVO.setRecordCount(3);
        selectedCategoryVO.setRestrictedMsisdns("Restricted Msisdns");
        selectedCategoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        selectedCategoryVO.setSequenceNumber(10);
        selectedCategoryVO.setServiceAllowed("Service Allowed");
        selectedCategoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        selectedCategoryVO.setTransferToListOnly("Transfer To List Only");
        selectedCategoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        selectedCategoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        selectedCategoryVO.setUserIdPrefix("User Id Prefix");
        selectedCategoryVO.setViewOnNetworkBlock("View On Network Block");
        selectedCategoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.validateSelectedGeographyAndDomain(domainList, geographyList, "Selected Domain Code",
                selectedCategoryVO, "Selected Geo Domain Code");
        verify(selectedCategoryVO).setAgentAgentAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentAllowedFlag(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryCode(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryStatus(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentCategoryType(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCheckArray(Mockito.<String[]>any());
        verify(selectedCategoryVO).setAgentCp2pPayee(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCp2pPayer(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentDomainName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentFixedRoles(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGatewayName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGatewayType(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGrphDomainType(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMaxLoginCount(anyLong());
        verify(selectedCategoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentModifyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRoleName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        verify(selectedCategoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentServiceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAuthenticationType(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryCode(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryName(Mockito.<String>any());
        verify(selectedCategoryVO).setCategorySequenceNumber(anyInt());
        verify(selectedCategoryVO).setCategoryStatus(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryType(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryTypeCode(Mockito.<String>any());
        verify(selectedCategoryVO).setCp2pPayee(Mockito.<String>any());
        verify(selectedCategoryVO).setCp2pPayer(Mockito.<String>any());
        verify(selectedCategoryVO).setCp2pWithinList(Mockito.<String>any());
        verify(selectedCategoryVO).setCreatedBy(Mockito.<String>any());
        verify(selectedCategoryVO).setCreatedOn(Mockito.<Date>any());
        verify(selectedCategoryVO).setDisplayAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainCodeforCategory(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainName(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainTypeCode(Mockito.<String>any());
        verify(selectedCategoryVO).setFixedDomains(Mockito.<String>any());
        verify(selectedCategoryVO).setFixedRoles(Mockito.<String>any());
        verify(selectedCategoryVO).setGeographicalDomainSeqNo(anyInt());
        verify(selectedCategoryVO).setGrphDomainSequenceNo(anyInt());
        verify(selectedCategoryVO).setGrphDomainType(Mockito.<String>any());
        verify(selectedCategoryVO).setGrphDomainTypeName(Mockito.<String>any());
        verify(selectedCategoryVO).setHierarchyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setLastModifiedTime(anyLong());
        verify(selectedCategoryVO).setLowBalAlertAllow(Mockito.<String>any());
        verify(selectedCategoryVO).setMaxLoginCount(anyLong());
        verify(selectedCategoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        verify(selectedCategoryVO).setMaxTxnMsisdnInt(anyInt());
        verify(selectedCategoryVO).setModifiedBy(Mockito.<String>any());
        verify(selectedCategoryVO).setModifiedOn(Mockito.<Date>any());
        verify(selectedCategoryVO).setModifyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setMultipleGrphDomains(Mockito.<String>any());
        verify(selectedCategoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setNumberOfCategoryForDomain(anyInt());
        verify(selectedCategoryVO).setOutletsAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setParentCategoryCode(Mockito.<String>any());
        verify(selectedCategoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        verify(selectedCategoryVO).setProductTypeAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setRadioIndex(anyInt());
        verify(selectedCategoryVO).setRechargeByParentOnly(Mockito.<String>any());
        verify(selectedCategoryVO).setRecordCount(anyInt());
        verify(selectedCategoryVO).setRestrictedMsisdns(Mockito.<String>any());
        verify(selectedCategoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setSequenceNumber(anyInt());
        verify(selectedCategoryVO).setServiceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setTransferToListOnly(Mockito.<String>any());
        verify(selectedCategoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        verify(selectedCategoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setUserIdPrefix(Mockito.<String>any());
        verify(selectedCategoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        verify(selectedCategoryVO).setWebInterfaceAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateSelectedGeographyAndDomain(ArrayList, ArrayList, String, CategoryVO, String)}
     */
    @Test
    public void testValidateSelectedGeographyAndDomain5() throws Exception {
        ListValueVO listValueVO = mock(ListValueVO.class);
        when(listValueVO.getValue()).thenReturn("42");

        ArrayList domainList = new ArrayList();
        domainList.add(listValueVO);
        ArrayList<UserGeographiesVO> geographyList = new ArrayList<>();
        CategoryVO selectedCategoryVO = mock(CategoryVO.class);
        doNothing().when(selectedCategoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(selectedCategoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(selectedCategoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(selectedCategoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setLastModifiedTime(anyLong());
        doNothing().when(selectedCategoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxLoginCount(anyLong());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(selectedCategoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(selectedCategoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(selectedCategoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRadioIndex(anyInt());
        doNothing().when(selectedCategoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setRecordCount(anyInt());
        doNothing().when(selectedCategoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSequenceNumber(anyInt());
        doNothing().when(selectedCategoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(selectedCategoryVO).setWebInterfaceAllowed(Mockito.<String>any());
        selectedCategoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        selectedCategoryVO.setAgentAllowed("Agent Allowed");
        selectedCategoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        selectedCategoryVO.setAgentCategoryCode("Agent Category Code");
        selectedCategoryVO.setAgentCategoryName("Agent Category Name");
        selectedCategoryVO.setAgentCategoryStatus("Agent Category Status");
        selectedCategoryVO.setAgentCategoryStatusList(new ArrayList());
        selectedCategoryVO.setAgentCategoryType("Agent Category Type");
        selectedCategoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        selectedCategoryVO.setAgentCp2pPayee("Cp2p Payee");
        selectedCategoryVO.setAgentCp2pPayer("Cp2p Payer");
        selectedCategoryVO.setAgentCp2pWithinList("Cp2p Within List");
        selectedCategoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        selectedCategoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        selectedCategoryVO.setAgentDomainName("Agent Domain Name");
        selectedCategoryVO.setAgentFixedRoles("Agent Fixed Roles");
        selectedCategoryVO.setAgentGatewayName("Agent Gateway Name");
        selectedCategoryVO.setAgentGatewayType("Agent Gateway Type");
        selectedCategoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        selectedCategoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        selectedCategoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        selectedCategoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        selectedCategoryVO.setAgentMaxLoginCount(3L);
        selectedCategoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        selectedCategoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        selectedCategoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        selectedCategoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        selectedCategoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        selectedCategoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        selectedCategoryVO.setAgentParentOrOwnerRadioValue("42");
        selectedCategoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        selectedCategoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        selectedCategoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        selectedCategoryVO.setAgentRoleName("Agent Role Name");
        selectedCategoryVO.setAgentRoleTypeList(new ArrayList());
        selectedCategoryVO.setAgentRolesMapSelected(new HashMap());
        selectedCategoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        selectedCategoryVO.setAgentServiceAllowed("Agent Service Allowed");
        selectedCategoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        selectedCategoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        selectedCategoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        selectedCategoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        selectedCategoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        selectedCategoryVO.setAllowedGatewayTypes(new ArrayList());
        selectedCategoryVO.setAuthenticationType("Type");
        selectedCategoryVO.setCategoryCode("Category Code");
        selectedCategoryVO.setCategoryName("Category Name");
        selectedCategoryVO.setCategorySequenceNumber(10);
        selectedCategoryVO.setCategoryStatus("Category Status");
        selectedCategoryVO.setCategoryType("Category Type");
        selectedCategoryVO.setCategoryTypeCode("Category Type Code");
        selectedCategoryVO.setCp2pPayee("Payee");
        selectedCategoryVO.setCp2pPayer("Payer");
        selectedCategoryVO.setCp2pWithinList("Within List");
        selectedCategoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        selectedCategoryVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setDisplayAllowed("Display Allowed");
        selectedCategoryVO.setDomainAllowed("Domain Allowed");
        selectedCategoryVO.setDomainCodeforCategory("Domain Codefor Category");
        selectedCategoryVO.setDomainName("Domain Name");
        selectedCategoryVO.setDomainTypeCode("Domain Type Code");
        selectedCategoryVO.setFixedDomains("Fixed Domains");
        selectedCategoryVO.setFixedRoles("Fixed Roles");
        selectedCategoryVO.setGeographicalDomainSeqNo(1);
        selectedCategoryVO.setGrphDomainSequenceNo(1);
        selectedCategoryVO.setGrphDomainType("Grph Domain Type");
        selectedCategoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        selectedCategoryVO.setHierarchyAllowed("Hierarchy Allowed");
        selectedCategoryVO.setLastModifiedTime(1L);
        selectedCategoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        selectedCategoryVO.setMaxLoginCount(3L);
        selectedCategoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        selectedCategoryVO.setMaxTxnMsisdnInt(3);
        selectedCategoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        selectedCategoryVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        selectedCategoryVO.setModifyAllowed("Modify Allowed");
        selectedCategoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        selectedCategoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        selectedCategoryVO.setNumberOfCategoryForDomain(10);
        selectedCategoryVO.setOutletsAllowed("Outlets Allowed");
        selectedCategoryVO.setParentCategoryCode("Parent Category Code");
        selectedCategoryVO.setParentOrOwnerRadioValue("42");
        selectedCategoryVO.setProductTypeAllowed("Product Type Allowed");
        selectedCategoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        selectedCategoryVO.setRadioIndex(1);
        selectedCategoryVO.setRechargeByParentOnly("By Parent Only");
        selectedCategoryVO.setRecordCount(3);
        selectedCategoryVO.setRestrictedMsisdns("Restricted Msisdns");
        selectedCategoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        selectedCategoryVO.setSequenceNumber(10);
        selectedCategoryVO.setServiceAllowed("Service Allowed");
        selectedCategoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        selectedCategoryVO.setTransferToListOnly("Transfer To List Only");
        selectedCategoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        selectedCategoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        selectedCategoryVO.setUserIdPrefix("User Id Prefix");
        selectedCategoryVO.setViewOnNetworkBlock("View On Network Block");
        selectedCategoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.validateSelectedGeographyAndDomain(domainList, geographyList, "Selected Domain Code",
                selectedCategoryVO, "Selected Geo Domain Code");
        verify(listValueVO).getValue();
        verify(selectedCategoryVO).setAgentAgentAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentAllowedFlag(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryCode(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryStatus(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentCategoryType(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCheckArray(Mockito.<String[]>any());
        verify(selectedCategoryVO).setAgentCp2pPayee(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCp2pPayer(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentDomainName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentFixedRoles(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGatewayName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGatewayType(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentGrphDomainType(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMaxLoginCount(anyLong());
        verify(selectedCategoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentModifyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRoleName(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        verify(selectedCategoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentServiceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        verify(selectedCategoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        verify(selectedCategoryVO).setAuthenticationType(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryCode(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryName(Mockito.<String>any());
        verify(selectedCategoryVO).setCategorySequenceNumber(anyInt());
        verify(selectedCategoryVO).setCategoryStatus(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryType(Mockito.<String>any());
        verify(selectedCategoryVO).setCategoryTypeCode(Mockito.<String>any());
        verify(selectedCategoryVO).setCp2pPayee(Mockito.<String>any());
        verify(selectedCategoryVO).setCp2pPayer(Mockito.<String>any());
        verify(selectedCategoryVO).setCp2pWithinList(Mockito.<String>any());
        verify(selectedCategoryVO).setCreatedBy(Mockito.<String>any());
        verify(selectedCategoryVO).setCreatedOn(Mockito.<Date>any());
        verify(selectedCategoryVO).setDisplayAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainCodeforCategory(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainName(Mockito.<String>any());
        verify(selectedCategoryVO).setDomainTypeCode(Mockito.<String>any());
        verify(selectedCategoryVO).setFixedDomains(Mockito.<String>any());
        verify(selectedCategoryVO).setFixedRoles(Mockito.<String>any());
        verify(selectedCategoryVO).setGeographicalDomainSeqNo(anyInt());
        verify(selectedCategoryVO).setGrphDomainSequenceNo(anyInt());
        verify(selectedCategoryVO).setGrphDomainType(Mockito.<String>any());
        verify(selectedCategoryVO).setGrphDomainTypeName(Mockito.<String>any());
        verify(selectedCategoryVO).setHierarchyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setLastModifiedTime(anyLong());
        verify(selectedCategoryVO).setLowBalAlertAllow(Mockito.<String>any());
        verify(selectedCategoryVO).setMaxLoginCount(anyLong());
        verify(selectedCategoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        verify(selectedCategoryVO).setMaxTxnMsisdnInt(anyInt());
        verify(selectedCategoryVO).setModifiedBy(Mockito.<String>any());
        verify(selectedCategoryVO).setModifiedOn(Mockito.<Date>any());
        verify(selectedCategoryVO).setModifyAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setMultipleGrphDomains(Mockito.<String>any());
        verify(selectedCategoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setNumberOfCategoryForDomain(anyInt());
        verify(selectedCategoryVO).setOutletsAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setParentCategoryCode(Mockito.<String>any());
        verify(selectedCategoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        verify(selectedCategoryVO).setProductTypeAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setRadioIndex(anyInt());
        verify(selectedCategoryVO).setRechargeByParentOnly(Mockito.<String>any());
        verify(selectedCategoryVO).setRecordCount(anyInt());
        verify(selectedCategoryVO).setRestrictedMsisdns(Mockito.<String>any());
        verify(selectedCategoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setSequenceNumber(anyInt());
        verify(selectedCategoryVO).setServiceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setTransferToListOnly(Mockito.<String>any());
        verify(selectedCategoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        verify(selectedCategoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        verify(selectedCategoryVO).setUserIdPrefix(Mockito.<String>any());
        verify(selectedCategoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        verify(selectedCategoryVO).setWebInterfaceAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#childGeoDataSearch(Connection, ArrayList, UserForm, UserVO, ArrayList, String)}
     */
    @Test
    public void testChildGeoDataSearch() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1158)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        Connection con = mock(Connection.class);
        ArrayList<UserGeographiesVO> graphDomainList = new ArrayList<>();

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
        UserForm theForm = mock(UserForm.class);
        when(theForm.getCategoryVO()).thenReturn(categoryVO);
        UserVO userSessionVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        channelUserServicesImpl.childGeoDataSearch (com.btsl.util.JUnitConfig.getConnection(), graphDomainList, theForm, userSessionVO, new ArrayList<>(),
                "Current Seq Num");
        verify(theForm).getCategoryVO();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#childGeoDataSearch(Connection, ArrayList, UserForm, UserVO, ArrayList, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChildGeoDataSearch2() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1158)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1173)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        Connection con = mock(Connection.class);

        UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
        userGeographiesVO.setCategoryCode("1");
        userGeographiesVO.setGraphDomainCode("1");
        userGeographiesVO.setGraphDomainName("1");
        userGeographiesVO.setGraphDomainSequenceNumber(10);
        userGeographiesVO.setGraphDomainType("1");
        userGeographiesVO.setGraphDomainTypeName("1");
        userGeographiesVO.setIsDefault("1");
        userGeographiesVO.setNetworkName("1");
        userGeographiesVO.setParentGraphDomainCode("1");
        userGeographiesVO.setUserId("42");

        ArrayList<UserGeographiesVO> graphDomainList = new ArrayList<>();
        graphDomainList.add(userGeographiesVO);

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
        UserForm theForm = mock(UserForm.class);
        when(theForm.getCategoryVO()).thenReturn(categoryVO);
        UserVO userSessionVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userSessionVO.getNetworkID()).thenReturn("Network ID");
        channelUserServicesImpl.childGeoDataSearch (com.btsl.util.JUnitConfig.getConnection(), graphDomainList, theForm, userSessionVO, new ArrayList<>(),
                "Current Seq Num");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#childGeoDataSearch(Connection, ArrayList, UserForm, UserVO, ArrayList, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChildGeoDataSearch3() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1158)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1173)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        Connection con = mock(Connection.class);

        UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
        userGeographiesVO.setCategoryCode("1");
        userGeographiesVO.setGraphDomainCode("1");
        userGeographiesVO.setGraphDomainName("1");
        userGeographiesVO.setGraphDomainSequenceNumber(10);
        userGeographiesVO.setGraphDomainType("1");
        userGeographiesVO.setGraphDomainTypeName("1");
        userGeographiesVO.setIsDefault("1");
        userGeographiesVO.setNetworkName("1");
        userGeographiesVO.setParentGraphDomainCode("1");
        userGeographiesVO.setUserId("42");

        ArrayList<UserGeographiesVO> graphDomainList = new ArrayList<>();
        graphDomainList.add(userGeographiesVO);
        CategoryVO categoryVO = mock(CategoryVO.class);
        when(categoryVO.getGrphDomainSequenceNo()).thenReturn(1);
        doNothing().when(categoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(categoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(categoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(categoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(categoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setLastModifiedTime(anyLong());
        doNothing().when(categoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(categoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(categoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setRadioIndex(anyInt());
        doNothing().when(categoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setRecordCount(anyInt());
        doNothing().when(categoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSequenceNumber(anyInt());
        doNothing().when(categoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(categoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setWebInterfaceAllowed(Mockito.<String>any());
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
        UserForm theForm = mock(UserForm.class);
        when(theForm.getCategoryVO()).thenReturn(categoryVO);
        UserVO userSessionVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userSessionVO.getNetworkID()).thenReturn("Network ID");
        channelUserServicesImpl.childGeoDataSearch (com.btsl.util.JUnitConfig.getConnection(), graphDomainList, theForm, userSessionVO, new ArrayList<>(),
                "Current Seq Num");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#childGeoDataSearch(Connection, ArrayList, UserForm, UserVO, ArrayList, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChildGeoDataSearch4() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1158)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1186)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
        userGeographiesVO.setCategoryCode("1");
        userGeographiesVO.setGraphDomainCode("1");
        userGeographiesVO.setGraphDomainName("1");
        userGeographiesVO.setGraphDomainSequenceNumber(10);
        userGeographiesVO.setGraphDomainType("1");
        userGeographiesVO.setGraphDomainTypeName("1");
        userGeographiesVO.setIsDefault("1");
        userGeographiesVO.setNetworkName("1");
        userGeographiesVO.setParentGraphDomainCode("1");
        userGeographiesVO.setUserId("42");

        ArrayList<UserGeographiesVO> graphDomainList = new ArrayList<>();
        graphDomainList.add(userGeographiesVO);
        CategoryVO categoryVO = mock(CategoryVO.class);
        when(categoryVO.getGrphDomainSequenceNo()).thenReturn(1);
        doNothing().when(categoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(categoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(categoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(categoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(categoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setLastModifiedTime(anyLong());
        doNothing().when(categoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(categoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(categoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setRadioIndex(anyInt());
        doNothing().when(categoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setRecordCount(anyInt());
        doNothing().when(categoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSequenceNumber(anyInt());
        doNothing().when(categoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(categoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setWebInterfaceAllowed(Mockito.<String>any());
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
        UserForm theForm = mock(UserForm.class);
        when(theForm.getCategoryVO()).thenReturn(categoryVO);
        UserVO userSessionVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userSessionVO.getNetworkID()).thenReturn("Network ID");

        AreaData areaData = new AreaData();
        areaData.setGeoCode("1");
        areaData.setGeoDomainName("1");
        areaData.setGeoDomainSequenceNo("1");
        areaData.setGeoList(new ArrayList<>());
        areaData.setGeoName("1");
        areaData.setIsDefault("1");

        ArrayList<AreaData> currentLeafList = new ArrayList<>();
        currentLeafList.add(areaData);
        channelUserServicesImpl.childGeoDataSearch (com.btsl.util.JUnitConfig.getConnection(), graphDomainList, theForm, userSessionVO, currentLeafList,
                "Current Seq Num");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#childGeoDataSearch(Connection, ArrayList, UserForm, UserVO, ArrayList, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChildGeoDataSearch5() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1158)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.sql.processing
        //       at com.web.pretups.master.businesslogic.GeographicalDomainWebDAO.loadGeographyList(GeographicalDomainWebDAO.java:1467)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1173)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
        userGeographiesVO.setCategoryCode("1");
        userGeographiesVO.setGraphDomainCode("1");
        userGeographiesVO.setGraphDomainName("1");
        userGeographiesVO.setGraphDomainSequenceNumber(10);
        userGeographiesVO.setGraphDomainType("1");
        userGeographiesVO.setGraphDomainTypeName("1");
        userGeographiesVO.setIsDefault("1");
        userGeographiesVO.setNetworkName("1");
        userGeographiesVO.setParentGraphDomainCode("1");
        userGeographiesVO.setUserId("42");

        ArrayList<UserGeographiesVO> graphDomainList = new ArrayList<>();
        graphDomainList.add(userGeographiesVO);
        CategoryVO categoryVO = mock(CategoryVO.class);
        when(categoryVO.getGrphDomainSequenceNo()).thenReturn(1);
        doNothing().when(categoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(categoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(categoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(categoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(categoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setLastModifiedTime(anyLong());
        doNothing().when(categoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(categoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(categoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setRadioIndex(anyInt());
        doNothing().when(categoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setRecordCount(anyInt());
        doNothing().when(categoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSequenceNumber(anyInt());
        doNothing().when(categoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(categoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setWebInterfaceAllowed(Mockito.<String>any());
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
        UserForm theForm = mock(UserForm.class);
        when(theForm.getCategoryVO()).thenReturn(categoryVO);
        UserVO userSessionVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userSessionVO.getNetworkID()).thenReturn("Network ID");

        AreaData areaData = new AreaData();
        areaData.setGeoCode("1");
        areaData.setGeoDomainName("1");
        areaData.setGeoDomainSequenceNo("1");
        areaData.setGeoList(new ArrayList<>());
        areaData.setGeoName("1");
        areaData.setIsDefault("1");

        ArrayList<AreaData> currentLeafList = new ArrayList<>();
        currentLeafList.add(areaData);
        channelUserServicesImpl.childGeoDataSearch (com.btsl.util.JUnitConfig.getConnection(), graphDomainList, theForm, userSessionVO, currentLeafList,
                "Current Seq Num");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#childGeoDataSearch(Connection, ArrayList, UserForm, UserVO, ArrayList, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChildGeoDataSearch6() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1158)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.childGeoDataSearch(ChannelUserServicesImpl.java:1173)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        UserGeographiesVO userGeographiesVO = mock(UserGeographiesVO.class);
        when(userGeographiesVO.getGraphDomainCode()).thenReturn("Graph Domain Code");
        doNothing().when(userGeographiesVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setGraphDomainCode(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setGraphDomainName(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setGraphDomainSequenceNumber(anyInt());
        doNothing().when(userGeographiesVO).setGraphDomainType(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setGraphDomainTypeName(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setIsDefault(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setNetworkName(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setParentGraphDomainCode(Mockito.<String>any());
        doNothing().when(userGeographiesVO).setUserId(Mockito.<String>any());
        userGeographiesVO.setCategoryCode("1");
        userGeographiesVO.setGraphDomainCode("1");
        userGeographiesVO.setGraphDomainName("1");
        userGeographiesVO.setGraphDomainSequenceNumber(10);
        userGeographiesVO.setGraphDomainType("1");
        userGeographiesVO.setGraphDomainTypeName("1");
        userGeographiesVO.setIsDefault("1");
        userGeographiesVO.setNetworkName("1");
        userGeographiesVO.setParentGraphDomainCode("1");
        userGeographiesVO.setUserId("42");

        ArrayList<UserGeographiesVO> graphDomainList = new ArrayList<>();
        graphDomainList.add(userGeographiesVO);
        CategoryVO categoryVO = mock(CategoryVO.class);
        when(categoryVO.getGrphDomainSequenceNo()).thenReturn(1);
        doNothing().when(categoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(categoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(categoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(categoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(categoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(categoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(categoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(categoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(categoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(categoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(categoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(categoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(categoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setLastModifiedTime(anyLong());
        doNothing().when(categoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxLoginCount(anyLong());
        doNothing().when(categoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(categoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(categoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(categoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(categoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(categoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(categoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(categoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setRadioIndex(anyInt());
        doNothing().when(categoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setRecordCount(anyInt());
        doNothing().when(categoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(categoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSequenceNumber(anyInt());
        doNothing().when(categoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(categoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(categoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(categoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(categoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(categoryVO).setWebInterfaceAllowed(Mockito.<String>any());
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
        UserForm theForm = mock(UserForm.class);
        when(theForm.getCategoryVO()).thenReturn(categoryVO);
        UserVO userSessionVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(userSessionVO.getNetworkID()).thenReturn("Network ID");

        AreaData areaData = new AreaData();
        areaData.setGeoCode("1");
        areaData.setGeoDomainName("1");
        areaData.setGeoDomainSequenceNo("1");
        areaData.setGeoList(new ArrayList<>());
        areaData.setGeoName("1");
        areaData.setIsDefault("1");

        ArrayList<AreaData> currentLeafList = new ArrayList<>();
        currentLeafList.add(areaData);
        channelUserServicesImpl.childGeoDataSearch (com.btsl.util.JUnitConfig.getConnection(), graphDomainList, theForm, userSessionVO, currentLeafList,
                "Current Seq Num");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#setHierarchicalResponse(Connection, ArrayList, UserForm, String)}
     */
    @Test
    public void testSetHierarchicalResponse() throws Exception {
        //Connection con = mock(Connection.class);
        ArrayList geographyList = new ArrayList();
        assertTrue(channelUserServicesImpl.setHierarchicalResponse(JUnitConfig.getConnection(), geographyList, new UserForm(), "42").isEmpty());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#setHierarchicalResponse(Connection, ArrayList, UserForm, String)}
     */
    @Test
    public void testSetHierarchicalResponse2() throws Exception {
        Connection con = mock(Connection.class);
        ArrayList geographyList = new ArrayList();
        assertTrue(channelUserServicesImpl.setHierarchicalResponse (com.btsl.util.JUnitConfig.getConnection(), geographyList, new UserForm(), "42").isEmpty());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserData(String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testFetchUserData() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.fetchUserData(ChannelUserServicesImpl.java:1259)
        //   See https://diff.blue/R013 to resolve this issue.

        channelUserServicesImpl.fetchUserData("Network", "Ext Code", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserData(String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testFetchUserData2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.fetchUserData(ChannelUserServicesImpl.java:1259)
        //   See https://diff.blue/R013 to resolve this issue.

        channelUserServicesImpl.fetchUserData("Network", "Ext Code",
                new CustomResponseWrapper(new HttpServletResponseWrapper(new CustomResponseWrapper(new Response()))));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserData(String, String, HttpServletResponse)}
     */
    @Test
    public void testFetchUserData3() {
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        SAPResponseVO actualFetchUserDataResult = channelUserServicesImpl.fetchUserData("Network", "Ext Code",
                responseSwag);
        assertNull(actualFetchUserDataResult.getAddress());
        assertNull(actualFetchUserDataResult.getTelephone());
        assertEquals(400, actualFetchUserDataResult.getStatus());
        assertNull(actualFetchUserDataResult.getState());
        assertNull(actualFetchUserDataResult.getName());
        assertEquals("241141", actualFetchUserDataResult.getMessageCode());
        assertNull(actualFetchUserDataResult.getMessage());
        assertNull(actualFetchUserDataResult.getEmpCode());
        assertNull(actualFetchUserDataResult.getEmail());
        assertNull(actualFetchUserDataResult.getCountry());
        assertNull(actualFetchUserDataResult.getCity());
        assertEquals(400, ((MockHttpServletResponse) responseSwag.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getLoginID(Connection, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetLoginID() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getLoginID(ChannelUserServicesImpl.java:1320)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        channelUserServicesImpl.getLoginID (com.btsl.util.JUnitConfig.getConnection(), "42");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getLoginID(Connection, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetLoginID2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getLoginID(ChannelUserServicesImpl.java:1328)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new RuntimeException("241291"));
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        channelUserServicesImpl.getLoginID (com.btsl.util.JUnitConfig.getConnection(), "42");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getLoginID(Connection, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetLoginID3() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getLoginID(ChannelUserServicesImpl.java:1320)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        channelUserServicesImpl.getLoginID (com.btsl.util.JUnitConfig.getConnection(), "42");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getLoginID(Connection, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetLoginID4() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getLoginID(ChannelUserServicesImpl.java:1328)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("42");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        channelUserServicesImpl.getLoginID (com.btsl.util.JUnitConfig.getConnection(), "42");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserHierarchy(Connection, UserHierarchyResponseVO, HttpServletResponse, String, String, String, String, String, String, String, ChannelUserVO)}
     */
    @Test
    public void testFetchUserHierarchy() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        doNothing().when(con).close();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        UserHierarchyResponseVO response = new UserHierarchyResponseVO();
        response.setChanerUserVO(ChannelUserVO.getInstance());
        response.setErrorMap(errorMap);
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setFileattachment("Fileattachment");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        response.setUserHierarchyList(new ArrayList<>());
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.fetchUserHierarchy (com.btsl.util.JUnitConfig.getConnection(), response, responseSwag, "User Domain", "Parent Category",
                "User Category", "Geography", "Status", "42", "Msisdn", ChannelUserVO.getInstance());
        verify(con).prepareStatement(Mockito.<String>any());
        verify(con).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserHierarchy(Connection, UserHierarchyResponseVO, HttpServletResponse, String, String, String, String, String, String, String, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testFetchUserHierarchy2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.web.pretups.user.businesslogic.ChannelUserWebDAO.loadCategoryUsers(ChannelUserWebDAO.java:863)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.fetchUserHierarchy(ChannelUserServicesImpl.java:1375)
        //   java.lang.RuntimeException: fetchUserHierarchy
        //       at com.web.pretups.user.businesslogic.ChannelUserWebDAO.loadCategoryUsers(ChannelUserWebDAO.java:844)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.fetchUserHierarchy(ChannelUserServicesImpl.java:1375)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new RuntimeException("fetchUserHierarchy"));
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        doNothing().when(con).close();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        UserHierarchyResponseVO response = new UserHierarchyResponseVO();
        response.setChanerUserVO(ChannelUserVO.getInstance());
        response.setErrorMap(errorMap);
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setFileattachment("Fileattachment");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        response.setUserHierarchyList(new ArrayList<>());
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        channelUserServicesImpl.fetchUserHierarchy (com.btsl.util.JUnitConfig.getConnection(), response, responseSwag, "User Domain", "Parent Category",
                "User Category", "Geography", "Status", "42", "Msisdn", ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserHierarchy(Connection, UserHierarchyResponseVO, HttpServletResponse, String, String, String, String, String, String, String, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testFetchUserHierarchy3() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.sql.processing
        //       at com.web.pretups.user.businesslogic.ChannelUserWebDAO.loadCategoryUsers(ChannelUserWebDAO.java:854)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.fetchUserHierarchy(ChannelUserServicesImpl.java:1375)
        //   java.sql.SQLException
        //       at com.web.pretups.user.businesslogic.ChannelUserWebDAO.loadCategoryUsers(ChannelUserWebDAO.java:844)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.fetchUserHierarchy(ChannelUserServicesImpl.java:1375)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        doNothing().when(con).close();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        UserHierarchyResponseVO response = new UserHierarchyResponseVO();
        response.setChanerUserVO(ChannelUserVO.getInstance());
        response.setErrorMap(errorMap);
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setFileattachment("Fileattachment");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        response.setUserHierarchyList(new ArrayList<>());
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        channelUserServicesImpl.fetchUserHierarchy (com.btsl.util.JUnitConfig.getConnection(), response, responseSwag, "User Domain", "Parent Category",
                "User Category", "Geography", "Status", "42", "Msisdn", ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserHierarchy(Connection, UserHierarchyResponseVO, HttpServletResponse, String, String, String, String, String, String, String, ChannelUserVO)}
     */
    @Test
    public void testFetchUserHierarchy4() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        doNothing().when(con).close();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        UserHierarchyResponseVO response = new UserHierarchyResponseVO();
        response.setChanerUserVO(ChannelUserVO.getInstance());
        response.setErrorMap(errorMap);
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setFileattachment("Fileattachment");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        response.setUserHierarchyList(new ArrayList<>());
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.fetchUserHierarchy (com.btsl.util.JUnitConfig.getConnection(), response, responseSwag, "User Domain", "Parent Category",
                "User Category", "Geography", "Status", "42", "Msisdn", ChannelUserVO.getInstance());
        verify(con).prepareStatement(Mockito.<String>any());
        verify(con).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchUserHierarchy(Connection, UserHierarchyResponseVO, HttpServletResponse, String, String, String, String, String, String, String, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testFetchUserHierarchy5() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.user.businesslogic.ChannelUserDAO.loadUserHierarchyList(ChannelUserDAO.java:4334)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.fetchUserHierarchy(ChannelUserServicesImpl.java:1405)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        doNothing().when(con).close();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        UserHierarchyResponseVO response = new UserHierarchyResponseVO();
        response.setChanerUserVO(ChannelUserVO.getInstance());
        response.setErrorMap(errorMap);
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setFileattachment("Fileattachment");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        response.setUserHierarchyList(new ArrayList<>());
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        channelUserServicesImpl.fetchUserHierarchy (com.btsl.util.JUnitConfig.getConnection(), response, responseSwag, "User Domain", "Parent Category",
                "User Category", "Geography", "Status", "42", "Msisdn", ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#batchUserInitiateProcess(String, BatchUserInitiateRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testBatchUserInitiateProcess() {
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
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.batchUserInitiateProcess(ChannelUserServicesImpl.java:1465)

        // Arrange
        // TODO: Populate arranged inputs
        String loginId = "";
        BatchUserInitiateRequestVO requestVO = null;
        HttpServletResponse responseSwag = null;

        // Act
        BatchUserInitiateResponseVO actualBatchUserInitiateProcessResult = this.channelUserServicesImpl
                .batchUserInitiateProcess(loginId, requestVO, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#createDirectory(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCreateDirectory() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files (file '\directory\foo.txt', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        channelUserServicesImpl.createDirectory("/directory/foo.txt");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileSize(String, byte[])}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFileSize() throws BTSLBaseException, UnsupportedEncodingException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: For input string: "File Size"
        //       at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
        //       at java.lang.Long.parseLong(Long.java:589)
        //       at java.lang.Long.parseLong(Long.java:631)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.validateFileSize(ChannelUserServicesImpl.java:3644)
        //   See https://diff.blue/R013 to resolve this issue.

        channelUserServicesImpl.validateFileSize("File Size", "AXAXAXAX".getBytes("UTF-8"));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileSize(String, byte[])}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFileSize2() throws BTSLBaseException, UnsupportedEncodingException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: For input string: "validateFileSize"
        //       at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
        //       at java.lang.Long.parseLong(Long.java:589)
        //       at java.lang.Long.parseLong(Long.java:631)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.validateFileSize(ChannelUserServicesImpl.java:3644)
        //   See https://diff.blue/R013 to resolve this issue.

        channelUserServicesImpl.validateFileSize("validateFileSize", "AXAXAXAX".getBytes("UTF-8"));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileSize(String, byte[])}
     */
    @Test
    public void testValidateFileSize3() throws BTSLBaseException, UnsupportedEncodingException {
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.validateFileSize("", "AXAXAXAX".getBytes("UTF-8"));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileSize(String, byte[])}
     */
    @Test
    public void testValidateFileSize4() throws BTSLBaseException, UnsupportedEncodingException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R002 Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ChannelUserServicesImpl.areaList
        //     ChannelUserServicesImpl.channelUserDao
        //     ChannelUserServicesImpl.channelUserWebDao
        //     ChannelUserServicesImpl.extUserDao
        //     ChannelUserServicesImpl.geographicalDomainWebDAO
        //     ChannelUserServicesImpl.senderVO
        //     ChannelUserServicesImpl.userDAO
        //     ChannelUserServicesImpl.userwebDAO

        channelUserServicesImpl.validateFileSize("42", "AXAXAXAX".getBytes("UTF-8"));
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileName(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFileName() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.validateFileName(ChannelUserServicesImpl.java:3652)
        //   See https://diff.blue/R013 to resolve this issue.

        channelUserServicesImpl.validateFileName("foo.txt");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileType(String)}
     */
    @Test
    public void testValidateFileType() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.validateFileType("File Type");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileType(String)}
     */
    @Test
    public void testValidateFileType2() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.validateFileType("VOUCHER_UPLOAD_FILE_FORMATS");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#validateFileType(String)}
     */
    @Test
    public void testValidateFileType3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R002 Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ChannelUserServicesImpl.areaList
        //     ChannelUserServicesImpl.channelUserDao
        //     ChannelUserServicesImpl.channelUserWebDao
        //     ChannelUserServicesImpl.extUserDao
        //     ChannelUserServicesImpl.geographicalDomainWebDAO
        //     ChannelUserServicesImpl.senderVO
        //     ChannelUserServicesImpl.userDAO
        //     ChannelUserServicesImpl.userwebDAO

        channelUserServicesImpl.validateFileType("xls");
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchChannelUsersByStatusForSRAndDelReq(Connection, ChannelUserSearchReqVo)}
     */
    @Test
    public void testFetchChannelUsersByStatusForSRAndDelReq() throws BTSLBaseException {
        Connection con = mock(Connection.class);

        ChannelUserSearchReqVo requestVo = new ChannelUserSearchReqVo();
        requestVo.setCategory("Category");
        requestVo.setDomain("Domain");
        requestVo.setGeography("Geography");
        requestVo.setLoggedInUserUserid("Logged In User Userid");
        requestVo.setLoggedUserNeworkCode("Logged User Nework Code");
        requestVo.setLoginID("Login ID");
        requestVo.setMobileNumber("42");
        requestVo.setSearchType("Search Type");
        requestVo.setUserStatus("User Status");
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.fetchChannelUsersByStatusForSRAndDelReq (com.btsl.util.JUnitConfig.getConnection(), requestVo);
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchChannelUsersByStatusForSRAndDelReq(Connection, ChannelUserSearchReqVo)}
     */
    @Test
    public void testFetchChannelUsersByStatusForSRAndDelReq2() throws BTSLBaseException {
        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        ChannelUserSearchReqVo requestVo = new ChannelUserSearchReqVo();
        requestVo.setCategory("Category");
        requestVo.setDomain("Domain");
        requestVo.setGeography("Geography");
        requestVo.setLoggedInUserUserid("Logged In User Userid");
        requestVo.setLoggedUserNeworkCode("Logged User Nework Code");
        requestVo.setLoginID("Login ID");
        requestVo.setMobileNumber("42");
        requestVo.setSearchType("Search Type");
        requestVo.setUserStatus("User Status");
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.fetchChannelUsersByStatusForSRAndDelReq (com.btsl.util.JUnitConfig.getConnection(), requestVo);
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#fetchChannelUsersByStatusForSRAndDelReq(Connection, ChannelUserSearchReqVo)}
     */
    @Test
    public void testFetchChannelUsersByStatusForSRAndDelReq3() throws BTSLBaseException {
        Connection con = mock(Connection.class);
        ChannelUserSearchReqVo requestVo = mock(ChannelUserSearchReqVo.class);
        when(requestVo.getUserStatus()).thenReturn("");
        doNothing().when(requestVo).setCategory(Mockito.<String>any());
        doNothing().when(requestVo).setDomain(Mockito.<String>any());
        doNothing().when(requestVo).setGeography(Mockito.<String>any());
        doNothing().when(requestVo).setLoggedInUserUserid(Mockito.<String>any());
        doNothing().when(requestVo).setLoggedUserNeworkCode(Mockito.<String>any());
        doNothing().when(requestVo).setLoginID(Mockito.<String>any());
        doNothing().when(requestVo).setMobileNumber(Mockito.<String>any());
        doNothing().when(requestVo).setSearchType(Mockito.<String>any());
        doNothing().when(requestVo).setUserStatus(Mockito.<String>any());
        requestVo.setCategory("Category");
        requestVo.setDomain("Domain");
        requestVo.setGeography("Geography");
        requestVo.setLoggedInUserUserid("Logged In User Userid");
        requestVo.setLoggedUserNeworkCode("Logged User Nework Code");
        requestVo.setLoginID("Login ID");
        requestVo.setMobileNumber("42");
        requestVo.setSearchType("Search Type");
        requestVo.setUserStatus("User Status");
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.fetchChannelUsersByStatusForSRAndDelReq (com.btsl.util.JUnitConfig.getConnection(), requestVo);
        verify(requestVo).getUserStatus();
        verify(requestVo).setCategory(Mockito.<String>any());
        verify(requestVo).setDomain(Mockito.<String>any());
        verify(requestVo).setGeography(Mockito.<String>any());
        verify(requestVo).setLoggedInUserUserid(Mockito.<String>any());
        verify(requestVo).setLoggedUserNeworkCode(Mockito.<String>any());
        verify(requestVo).setLoginID(Mockito.<String>any());
        verify(requestVo).setMobileNumber(Mockito.<String>any());
        verify(requestVo).setSearchType(Mockito.<String>any());
        verify(requestVo).setUserStatus(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#approvalOrRejectSuspendUser(Connection, ActionOnUserReqVo, OAuthUserData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testApprovalOrRejectSuspendUser() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.approvalOrRejectSuspendUser(ChannelUserServicesImpl.java:3848)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ActionOnUserReqVo actionReqVo = new ActionOnUserReqVo();
        actionReqVo.setAction("Action");
        actionReqVo.setLoginId("42");
        actionReqVo.setRemarks("Remarks");
        actionReqVo.setRequestType("Request Type");

        OAuthUserData oauthUserData = new OAuthUserData();
        oauthUserData.setExtcode("Extcode");
        oauthUserData.setLoginid("Loginid");
        oauthUserData.setMsisdn("Msisdn");
        oauthUserData.setPassword("iloveyou");
        oauthUserData.setPin("Pin");
        oauthUserData.setUserid("Userid");
        channelUserServicesImpl.approvalOrRejectSuspendUser (com.btsl.util.JUnitConfig.getConnection(), actionReqVo, oauthUserData);
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#approvalOrRejectSuspendUser(Connection, ActionOnUserReqVo, OAuthUserData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testApprovalOrRejectSuspendUser2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.approvalOrRejectSuspendUser(ChannelUserServicesImpl.java:3848)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        ActionOnUserReqVo actionReqVo = mock(ActionOnUserReqVo.class);
        doNothing().when(actionReqVo).setAction(Mockito.<String>any());
        doNothing().when(actionReqVo).setLoginId(Mockito.<String>any());
        doNothing().when(actionReqVo).setRemarks(Mockito.<String>any());
        doNothing().when(actionReqVo).setRequestType(Mockito.<String>any());
        actionReqVo.setAction("Action");
        actionReqVo.setLoginId("42");
        actionReqVo.setRemarks("Remarks");
        actionReqVo.setRequestType("Request Type");

        OAuthUserData oauthUserData = new OAuthUserData();
        oauthUserData.setExtcode("Extcode");
        oauthUserData.setLoginid("Loginid");
        oauthUserData.setMsisdn("Msisdn");
        oauthUserData.setPassword("iloveyou");
        oauthUserData.setPin("Pin");
        oauthUserData.setUserid("Userid");
        channelUserServicesImpl.approvalOrRejectSuspendUser (com.btsl.util.JUnitConfig.getConnection(), actionReqVo, oauthUserData);
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getParentCategoryList(UserVO, String, Connection)}
     */
    @Test
    public void testGetParentCategoryList() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:270)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertNull(channelUserServicesImpl.getParentCategoryList(sessionUserVO, "Category Code", con));
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getParentCategoryList(UserVO, String, Connection)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetParentCategoryList2() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:270)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.sql.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:265)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        channelUserServicesImpl.getParentCategoryList(sessionUserVO, "Category Code", con);
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getParentCategoryList(UserVO, String, Connection)}
     */
    @Test
    public void testGetParentCategoryList3() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:270)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getNetworkID()).thenReturn("Network ID");
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertEquals(1, channelUserServicesImpl.getParentCategoryList(sessionUserVO, "String", con).size());
        verify(sessionUserVO).getNetworkID();
        verify (com.btsl.util.JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getParentCategoryList(UserVO, String, Connection)}
     */
    @Test
    public void testGetParentCategoryList4() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:270)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getDomainID()).thenReturn("Domain ID");
        when(sessionUserVO.getNetworkID()).thenReturn("Network ID");
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(5);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.getParentCategoryList(sessionUserVO, "String", con);
        verify(sessionUserVO, atLeast(1)).getDomainID();
        verify(sessionUserVO).getNetworkID();
        verify (com.btsl.util.JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getParentCategoryList(UserVO, String, Connection)}
     */
    @Test
    public void testGetParentCategoryList5() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:270)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getDomainID()).thenReturn("OPT");
        when(sessionUserVO.getNetworkID()).thenReturn("Network ID");
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(5);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        channelUserServicesImpl.getParentCategoryList(sessionUserVO, "String", con);
        verify(sessionUserVO, atLeast(1)).getDomainID();
        verify(sessionUserVO).getNetworkID();
        verify (com.btsl.util.JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getParentCategoryList(UserVO, String, Connection)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetParentCategoryList6() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:270)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        when(sessionUserVO.getDomainID()).thenReturn("Domain ID");
        when(sessionUserVO.getNetworkID()).thenReturn("Network ID");
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(5);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        channelUserServicesImpl.getParentCategoryList(sessionUserVO, "String", con);
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl#getParentCategoryList(UserVO, String, Connection)}
     */
    @Test
    public void testGetParentCategoryList7() throws Exception {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.btsl.pretups.domain.businesslogic.CategoryDAO.loadOtherCategorList(CategoryDAO.java:270)
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl.getParentCategoryList(ChannelUserServicesImpl.java:4444)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelUserServicesImpl channelUserServicesImpl = new ChannelUserServicesImpl();
        UserVO sessionUserVO = com.btsl.util.JUnitConfig.getUserVO();//mock(UserVO.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        assertNull(channelUserServicesImpl.getParentCategoryList(sessionUserVO, "String", con));
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ChannelUserServicesImpl.UpdateRecordsInDB#run()}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUpdateRecordsInDBRun() {
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
        //       at com.restapi.channeluser.service.ChannelUserServicesImpl$UpdateRecordsInDB.run(ChannelUserServicesImpl.java:4395)

        // Arrange
        // TODO: Populate arranged inputs
        ChannelUserServicesImpl.UpdateRecordsInDB updateRecordsInDB = null;

        // Act
        updateRecordsInDB.run();

        // Assert
        // TODO: Add assertions on result
    }
}

