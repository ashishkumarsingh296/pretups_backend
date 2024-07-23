package com.restapi.superadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.domain.web.DomainForm;
import com.restapi.superadmin.ChannelDomainListResponseVO;
import com.restapi.superadmin.requestVO.DeleteDomainRequestVO;
import com.restapi.superadmin.requestVO.SaveDomainRequestVO;
import com.restapi.superadmin.requestVO.UpdateDomainRequestVO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ChannelDomainServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelDomainServiceImplTest {
    @Autowired
    private ChannelDomainServiceImpl channelDomainServiceImpl;

    /**
     * Method under test: {@link ChannelDomainServiceImpl#getChannelDomainList(HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetChannelDomainList() throws SQLException {
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
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.getChannelDomainList(ChannelDomainServiceImpl.java:77)

        // Arrange
        // TODO: Populate arranged inputs
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        ChannelDomainListResponseVO actualChannelDomainList = this.channelDomainServiceImpl
                .getChannelDomainList(responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#updateChannelDomain(UpdateDomainRequestVO, String, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUpdateChannelDomain() throws SQLException {
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
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.updateChannelDomain(ChannelDomainServiceImpl.java:129)

        // Arrange
        // TODO: Populate arranged inputs
        UpdateDomainRequestVO request = null;
        String loginId = "";
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualUpdateChannelDomainResult = this.channelDomainServiceImpl.updateChannelDomain(request, loginId,
                responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructDomainVOFromForm(UpdateDomainRequestVO)}
     */
    @Test
    public void testConstructDomainVOFromForm() throws Exception {
        UpdateDomainRequestVO request = new UpdateDomainRequestVO();
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainStatus("Domain Status");
        request.setLastModifiedTime(1L);
        request.setNumberOfCategories("42");
        DomainVO actualConstructDomainVOFromFormResult = channelDomainServiceImpl.constructDomainVOFromForm(request);
        assertEquals("42", actualConstructDomainVOFromFormResult.getNumberOfCategories());
        assertEquals(1L, actualConstructDomainVOFromFormResult.getLastModifiedTime());
        assertEquals("Domain Status", actualConstructDomainVOFromFormResult.getDomainStatus());
        assertEquals("Domain Name", actualConstructDomainVOFromFormResult.getDomainName());
        assertEquals("Domain Codefor Domain", actualConstructDomainVOFromFormResult.getDomainCodeforDomain());
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructDomainVOFromForm(UpdateDomainRequestVO)}
     */
    @Test
    public void testConstructDomainVOFromForm2() throws Exception {
        UpdateDomainRequestVO request = mock(UpdateDomainRequestVO.class);
        when(request.getDomainCodeforDomain()).thenReturn("Domain Codefor Domain");
        when(request.getDomainName()).thenReturn("Domain Name");
        when(request.getDomainStatus()).thenReturn("Domain Status");
        when(request.getNumberOfCategories()).thenReturn("42");
        when(request.getLastModifiedTime()).thenReturn(1L);
        doNothing().when(request).setDomainCodeforDomain(Mockito.<String>any());
        doNothing().when(request).setDomainName(Mockito.<String>any());
        doNothing().when(request).setDomainStatus(Mockito.<String>any());
        doNothing().when(request).setLastModifiedTime(anyLong());
        doNothing().when(request).setNumberOfCategories(Mockito.<String>any());
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainStatus("Domain Status");
        request.setLastModifiedTime(1L);
        request.setNumberOfCategories("42");
        DomainVO actualConstructDomainVOFromFormResult = channelDomainServiceImpl.constructDomainVOFromForm(request);
        assertEquals("42", actualConstructDomainVOFromFormResult.getNumberOfCategories());
        assertEquals(1L, actualConstructDomainVOFromFormResult.getLastModifiedTime());
        assertEquals("Domain Status", actualConstructDomainVOFromFormResult.getDomainStatus());
        assertEquals("Domain Name", actualConstructDomainVOFromFormResult.getDomainName());
        assertEquals("Domain Codefor Domain", actualConstructDomainVOFromFormResult.getDomainCodeforDomain());
        verify(request).getDomainCodeforDomain();
        verify(request).getDomainName();
        verify(request).getDomainStatus();
        verify(request).getNumberOfCategories();
        verify(request).getLastModifiedTime();
        verify(request).setDomainCodeforDomain(Mockito.<String>any());
        verify(request).setDomainName(Mockito.<String>any());
        verify(request).setDomainStatus(Mockito.<String>any());
        verify(request).setLastModifiedTime(anyLong());
        verify(request).setNumberOfCategories(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructDomainVOFromForm(DomainForm)}
     */
    @Test
    public void testConstructDomainVOFromForm3() throws Exception {
        DomainVO actualConstructDomainVOFromFormResult = channelDomainServiceImpl
                .constructDomainVOFromForm(new DomainForm());
        assertNull(actualConstructDomainVOFromFormResult.getOwnerCategory());
        assertNull(actualConstructDomainVOFromFormResult.getNumberOfCategories());
        assertEquals(0L, actualConstructDomainVOFromFormResult.getLastModifiedTime());
        assertNull(actualConstructDomainVOFromFormResult.getDomainTypeCode());
        assertNull(actualConstructDomainVOFromFormResult.getDomainStatus());
        assertNull(actualConstructDomainVOFromFormResult.getDomainName());
        assertNull(actualConstructDomainVOFromFormResult.getDomainCodeforDomain());
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructDomainVOFromForm(DomainForm)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructDomainVOFromForm4() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.constructDomainVOFromForm(ChannelDomainServiceImpl.java:613)
        //   See https://diff.blue/R013 to resolve this issue.

        channelDomainServiceImpl.constructDomainVOFromForm((DomainForm) null);
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructDomainVOFromForm(DomainForm)}
     */
    @Test
    public void testConstructDomainVOFromForm5() throws Exception {
        DomainForm request = mock(DomainForm.class);
        when(request.getCategoryCode()).thenReturn("Category Code");
        when(request.getDomainCodeforDomain()).thenReturn("Domain Codefor Domain");
        when(request.getDomainName()).thenReturn("Domain Name");
        when(request.getDomainStatus()).thenReturn("Domain Status");
        when(request.getDomainTypeCode()).thenReturn("Domain Type Code");
        when(request.getNumberOfCategories()).thenReturn("42");
        when(request.getLastModifiedTime()).thenReturn(1L);
        DomainVO actualConstructDomainVOFromFormResult = channelDomainServiceImpl.constructDomainVOFromForm(request);
        assertEquals("Category Code", actualConstructDomainVOFromFormResult.getOwnerCategory());
        assertEquals("42", actualConstructDomainVOFromFormResult.getNumberOfCategories());
        assertEquals(1L, actualConstructDomainVOFromFormResult.getLastModifiedTime());
        assertEquals("Domain Type Code", actualConstructDomainVOFromFormResult.getDomainTypeCode());
        assertEquals("Domain Status", actualConstructDomainVOFromFormResult.getDomainStatus());
        assertEquals("Domain Name", actualConstructDomainVOFromFormResult.getDomainName());
        assertEquals("Domain Codefor Domain", actualConstructDomainVOFromFormResult.getDomainCodeforDomain());
        verify(request).getCategoryCode();
        verify(request).getDomainCodeforDomain();
        verify(request).getDomainName();
        verify(request).getDomainStatus();
        verify(request).getDomainTypeCode();
        verify(request).getNumberOfCategories();
        verify(request).getLastModifiedTime();
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructCategoryVOFromForm(DomainForm, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructCategoryVOFromForm() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: null
        //       at java.lang.Long.parseLong(Long.java:552)
        //       at java.lang.Long.parseLong(Long.java:631)
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.constructCategoryVOFromForm(ChannelDomainServiceImpl.java:662)
        //   See https://diff.blue/R013 to resolve this issue.

        DomainForm p_form = new DomainForm();

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        channelDomainServiceImpl.constructCategoryVOFromForm(p_form, p_categoryVO);
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructCategoryVOFromForm(DomainForm, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructCategoryVOFromForm2() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.constructCategoryVOFromForm(ChannelDomainServiceImpl.java:632)
        //   See https://diff.blue/R013 to resolve this issue.

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        channelDomainServiceImpl.constructCategoryVOFromForm(null, p_categoryVO);
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructCategoryVOFromForm(DomainForm, CategoryVO)}
     */
    @Test
    public void testConstructCategoryVOFromForm3() throws Exception {
        DomainForm p_form = mock(DomainForm.class);
        when(p_form.getAgentAllowed()).thenReturn("Agent Allowed");
        when(p_form.getCategoryStatus()).thenReturn("Category Status");
        when(p_form.getCp2pPayee()).thenReturn("Cp2p Payee");
        when(p_form.getCp2pPayer()).thenReturn("Cp2p Payer");
        when(p_form.getCp2pWithinList()).thenReturn("Cp2p Within List");
        when(p_form.getDisplayAllowed()).thenReturn("Display Allowed");
        when(p_form.getHierarchyAllowed()).thenReturn("Hierarchy Allowed");
        when(p_form.getListLevelCode()).thenReturn("List Level Code");
        when(p_form.getLowBalanceAlertAllow()).thenReturn("Low Balance Alert Allow");
        when(p_form.getMaxTxnMsisdn()).thenReturn("Max Txn Msisdn");
        when(p_form.getModifyAllowed()).thenReturn("Modify Allowed");
        when(p_form.getOutletsAllowed()).thenReturn("Outlets Allowed");
        when(p_form.getProductTypeAssociationAllowed()).thenReturn("Product Type Association Allowed");
        when(p_form.getRechargeByParentOnly()).thenReturn("Recharge By Parent Only");
        when(p_form.getRestrictedMsisdns()).thenReturn("Restricted Msisdns");
        when(p_form.getScheduledTransferAllowed()).thenReturn("Scheduled Transfer Allowed");
        when(p_form.getServiceAllowed()).thenReturn("Service Allowed");
        when(p_form.getTransferToListOnly()).thenReturn("Transfer To List Only");
        when(p_form.getUnctrlTransferAllowed()).thenReturn("Unctrl Transfer Allowed");
        when(p_form.getUserIdPrefix()).thenReturn("User Id Prefix");
        when(p_form.getLastModifiedTime()).thenReturn(1L);
        when(p_form.getCategoryCode()).thenReturn("Category Code");
        when(p_form.getCategoryName()).thenReturn("Category Name");
        when(p_form.getDomainCodeforDomain()).thenReturn("Domain Codefor Domain");
        when(p_form.getFixedRoles()).thenReturn("Fixed Roles");
        when(p_form.getGrphDomainType()).thenReturn("Grph Domain Type");
        when(p_form.getMaxLoginCount()).thenReturn("3");
        when(p_form.getMultipleGrphDomains()).thenReturn("Multiple Grph Domains");
        when(p_form.getMultipleLoginAllowed()).thenReturn("Multiple Login Allowed");
        when(p_form.getParentCategoryCode()).thenReturn("Parent Category Code");
        when(p_form.getViewOnNetworkBlock()).thenReturn("View On Network Block");
        when(p_form.getModifiedMessageGatewayList()).thenReturn(new ArrayList());

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        CategoryVO actualConstructCategoryVOFromFormResult = channelDomainServiceImpl.constructCategoryVOFromForm(p_form,
                p_categoryVO);
        assertSame(p_categoryVO, actualConstructCategoryVOFromFormResult);
        assertEquals("Category Name", actualConstructCategoryVOFromFormResult.getCategoryName());
        assertEquals("Category Code", actualConstructCategoryVOFromFormResult.getCategoryCode());
        assertEquals("Agent Allowed", actualConstructCategoryVOFromFormResult.getAgentAllowed());
        verify(p_form).getAgentAllowed();
        verify(p_form).getCategoryCode();
        verify(p_form).getCategoryName();
        verify(p_form).getCategoryStatus();
        verify(p_form).getCp2pPayee();
        verify(p_form).getCp2pPayer();
        verify(p_form).getCp2pWithinList();
        verify(p_form).getDisplayAllowed();
        verify(p_form).getDomainCodeforDomain();
        verify(p_form).getFixedRoles();
        verify(p_form).getGrphDomainType();
        verify(p_form).getHierarchyAllowed();
        verify(p_form).getListLevelCode();
        verify(p_form).getLowBalanceAlertAllow();
        verify(p_form).getMaxLoginCount();
        verify(p_form).getMaxTxnMsisdn();
        verify(p_form).getModifyAllowed();
        verify(p_form).getMultipleGrphDomains();
        verify(p_form).getMultipleLoginAllowed();
        verify(p_form).getOutletsAllowed();
        verify(p_form, atLeast(1)).getParentCategoryCode();
        verify(p_form).getProductTypeAssociationAllowed();
        verify(p_form).getRechargeByParentOnly();
        verify(p_form).getRestrictedMsisdns();
        verify(p_form).getScheduledTransferAllowed();
        verify(p_form).getServiceAllowed();
        verify(p_form).getTransferToListOnly();
        verify(p_form).getUnctrlTransferAllowed();
        verify(p_form).getUserIdPrefix();
        verify(p_form).getViewOnNetworkBlock();
        verify(p_form).getModifiedMessageGatewayList();
        verify(p_form).getLastModifiedTime();
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#deleteChannelDomain(DeleteDomainRequestVO, String, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteChannelDomain() throws SQLException {
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
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.deleteChannelDomain(ChannelDomainServiceImpl.java:244)

        // Arrange
        // TODO: Populate arranged inputs
        DeleteDomainRequestVO request = null;
        String loginId = "";
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualDeleteChannelDomainResult = this.channelDomainServiceImpl.deleteChannelDomain(request, loginId,
                responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#saveChannelDomain(SaveDomainRequestVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSaveChannelDomain() throws SQLException {
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
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.saveChannelDomain(ChannelDomainServiceImpl.java:345)

        // Arrange
        // TODO: Populate arranged inputs
        SaveDomainRequestVO domainRequest = null;
        String loginId = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualSaveChannelDomainResult = this.channelDomainServiceImpl.saveChannelDomain(domainRequest,
                loginId, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#updateStatusOfDomain(DeleteDomainRequestVO, String, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUpdateStatusOfDomain() throws SQLException {
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
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.updateStatusOfDomain(ChannelDomainServiceImpl.java:714)

        // Arrange
        // TODO: Populate arranged inputs
        DeleteDomainRequestVO request = null;
        String loginId = "";
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualUpdateStatusOfDomainResult = this.channelDomainServiceImpl.updateStatusOfDomain(request,
                loginId, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#refreshAssignRoles(DomainForm, SaveDomainRequestVO)}
     */
    @Test
    public void testRefreshAssignRoles() {
        DomainForm domainForm = new DomainForm();

        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        request.setModifiedMessageGatewayList(new ArrayList());
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        assertSame(domainForm, channelDomainServiceImpl.refreshAssignRoles(domainForm, request));
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#refreshAssignRoles(DomainForm, SaveDomainRequestVO)}
     */
    @Test
    public void testRefreshAssignRoles2() {
        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        request.setModifiedMessageGatewayList(new ArrayList());
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        assertNull(channelDomainServiceImpl.refreshAssignRoles(null, request));
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#refreshAssignRoles(DomainForm, SaveDomainRequestVO)}
     */
    @Test
    public void testRefreshAssignRoles3() {
        DomainForm domainForm = mock(DomainForm.class);
        when(domainForm.getAgentRolesMap()).thenReturn(new HashMap());
        when(domainForm.getRolesMap()).thenReturn(new HashMap());

        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        request.setModifiedMessageGatewayList(new ArrayList());
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        assertSame(domainForm, channelDomainServiceImpl.refreshAssignRoles(domainForm, request));
        verify(domainForm).getAgentRolesMap();
        verify(domainForm, atLeast(1)).getRolesMap();
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#refreshAssignRoles(DomainForm, SaveDomainRequestVO)}
     */
    @Test
    public void testRefreshAssignRoles4() {
        HashMap hashMap = new HashMap();
        hashMap.put("42", "42");
        DomainForm domainForm = mock(DomainForm.class);
        when(domainForm.getAgentAllowedFlag()).thenReturn("Agent Allowed Flag");
        when(domainForm.getAgentRolesMap()).thenReturn(new HashMap());
        when(domainForm.getRolesMap()).thenReturn(hashMap);

        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        request.setModifiedMessageGatewayList(new ArrayList());
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        assertSame(domainForm, channelDomainServiceImpl.refreshAssignRoles(domainForm, request));
        verify(domainForm, atLeast(1)).getAgentAllowedFlag();
        verify(domainForm).getAgentRolesMap();
        verify(domainForm, atLeast(1)).getRolesMap();
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#refreshAssignRoles(DomainForm, SaveDomainRequestVO)}
     */
    @Test
    public void testRefreshAssignRoles5() {
        HashMap hashMap = new HashMap();
        hashMap.put("42", "42");
        DomainForm domainForm = mock(DomainForm.class);
        when(domainForm.getAgentAllowedFlag()).thenReturn("N");
        when(domainForm.getRolesMap()).thenReturn(hashMap);

        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        request.setModifiedMessageGatewayList(new ArrayList());
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        assertSame(domainForm, channelDomainServiceImpl.refreshAssignRoles(domainForm, request));
        verify(domainForm).getAgentAllowedFlag();
        verify(domainForm, atLeast(1)).getRolesMap();
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#refreshAssignRoles(DomainForm, SaveDomainRequestVO)}
     */
    @Test
    public void testRefreshAssignRoles6() {
        HashMap hashMap = new HashMap();
        hashMap.put("42", "42");
        DomainForm domainForm = mock(DomainForm.class);
        when(domainForm.getAgentAllowedFlag()).thenReturn("Y");
        when(domainForm.getAgentRolesMap()).thenReturn(new HashMap());
        when(domainForm.getRolesMap()).thenReturn(hashMap);

        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        request.setModifiedMessageGatewayList(new ArrayList());
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        assertSame(domainForm, channelDomainServiceImpl.refreshAssignRoles(domainForm, request));
        verify(domainForm, atLeast(1)).getAgentAllowedFlag();
        verify(domainForm).getAgentRolesMap();
        verify(domainForm, atLeast(1)).getRolesMap();
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#refreshAssignRoles(DomainForm, SaveDomainRequestVO)}
     */
    @Test
    public void testRefreshAssignRoles7() {
        HashMap hashMap = new HashMap();
        hashMap.put("42", "42");
        DomainForm domainForm = mock(DomainForm.class);
        when(domainForm.getAgentAllowedFlag()).thenReturn(null);
        when(domainForm.getRolesMap()).thenReturn(hashMap);

        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        request.setModifiedMessageGatewayList(new ArrayList());
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        assertSame(domainForm, channelDomainServiceImpl.refreshAssignRoles(domainForm, request));
        verify(domainForm).getAgentAllowedFlag();
        verify(domainForm, atLeast(1)).getRolesMap();
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#saveAgentChannelDomain(SaveDomainRequestVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSaveAgentChannelDomain() throws SQLException {
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
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.saveAgentChannelDomain(ChannelDomainServiceImpl.java:989)

        // Arrange
        // TODO: Populate arranged inputs
        SaveDomainRequestVO request = null;
        String loginId = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualSaveAgentChannelDomainResult = this.channelDomainServiceImpl.saveAgentChannelDomain(request,
                loginId, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructCategoryVOFrom(DomainForm, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructCategoryVOFrom() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: null
        //       at java.lang.Long.parseLong(Long.java:552)
        //       at java.lang.Long.parseLong(Long.java:631)
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.constructCategoryVOFrom(ChannelDomainServiceImpl.java:1204)
        //   See https://diff.blue/R013 to resolve this issue.

        DomainForm p_form = new DomainForm();

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        channelDomainServiceImpl.constructCategoryVOFrom(p_form, p_categoryVO);
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructCategoryVOFrom(DomainForm, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructCategoryVOFrom2() throws Exception {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.superadmin.service.ChannelDomainServiceImpl.constructCategoryVOFrom(ChannelDomainServiceImpl.java:1174)
        //   See https://diff.blue/R013 to resolve this issue.

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        channelDomainServiceImpl.constructCategoryVOFrom(null, p_categoryVO);
    }

    /**
     * Method under test: {@link ChannelDomainServiceImpl#constructCategoryVOFrom(DomainForm, CategoryVO)}
     */
    @Test
    public void testConstructCategoryVOFrom3() throws Exception {
        DomainForm p_form = mock(DomainForm.class);
        when(p_form.getAgentAllowed()).thenReturn("Agent Allowed");
        when(p_form.getCategoryStatus()).thenReturn("Category Status");
        when(p_form.getCp2pPayee()).thenReturn("Cp2p Payee");
        when(p_form.getCp2pPayer()).thenReturn("Cp2p Payer");
        when(p_form.getCp2pWithinList()).thenReturn("Cp2p Within List");
        when(p_form.getDisplayAllowed()).thenReturn("Display Allowed");
        when(p_form.getHierarchyAllowed()).thenReturn("Hierarchy Allowed");
        when(p_form.getListLevelCode()).thenReturn("List Level Code");
        when(p_form.getLowBalanceAlertAllow()).thenReturn("Low Balance Alert Allow");
        when(p_form.getMaxTxnMsisdn()).thenReturn("Max Txn Msisdn");
        when(p_form.getModifyAllowed()).thenReturn("Modify Allowed");
        when(p_form.getOutletsAllowed()).thenReturn("Outlets Allowed");
        when(p_form.getProductTypeAssociationAllowed()).thenReturn("Product Type Association Allowed");
        when(p_form.getRechargeByParentOnly()).thenReturn("Recharge By Parent Only");
        when(p_form.getRestrictedMsisdns()).thenReturn("Restricted Msisdns");
        when(p_form.getScheduledTransferAllowed()).thenReturn("Scheduled Transfer Allowed");
        when(p_form.getServiceAllowed()).thenReturn("Service Allowed");
        when(p_form.getTransferToListOnly()).thenReturn("Transfer To List Only");
        when(p_form.getUnctrlTransferAllowed()).thenReturn("Unctrl Transfer Allowed");
        when(p_form.getUserIdPrefix()).thenReturn("User Id Prefix");
        when(p_form.getLastModifiedTime()).thenReturn(1L);
        when(p_form.getCategoryCode()).thenReturn("Category Code");
        when(p_form.getCategoryName()).thenReturn("Category Name");
        when(p_form.getDomainCodeforDomain()).thenReturn("Domain Codefor Domain");
        when(p_form.getFixedRoles()).thenReturn("Fixed Roles");
        when(p_form.getGrphDomainType()).thenReturn("Grph Domain Type");
        when(p_form.getMaxLoginCount()).thenReturn("3");
        when(p_form.getMultipleGrphDomains()).thenReturn("Multiple Grph Domains");
        when(p_form.getMultipleLoginAllowed()).thenReturn("Multiple Login Allowed");
        when(p_form.getParentCategoryCode()).thenReturn("Parent Category Code");
        when(p_form.getViewOnNetworkBlock()).thenReturn("View On Network Block");
        when(p_form.getModifiedMessageGatewayList()).thenReturn(new ArrayList());

        CategoryVO p_categoryVO = new CategoryVO();
        p_categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        p_categoryVO.setAgentAllowed("Agent Allowed");
        p_categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        p_categoryVO.setAgentCategoryCode("Agent Category Code");
        p_categoryVO.setAgentCategoryName("Agent Category Name");
        p_categoryVO.setAgentCategoryStatus("Agent Category Status");
        p_categoryVO.setAgentCategoryStatusList(new ArrayList());
        p_categoryVO.setAgentCategoryType("Agent Category Type");
        p_categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        p_categoryVO.setAgentCp2pPayee("Cp2p Payee");
        p_categoryVO.setAgentCp2pPayer("Cp2p Payer");
        p_categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        p_categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        p_categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        p_categoryVO.setAgentDomainName("Agent Domain Name");
        p_categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        p_categoryVO.setAgentGatewayName("Agent Gateway Name");
        p_categoryVO.setAgentGatewayType("Agent Gateway Type");
        p_categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        p_categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        p_categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        p_categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        p_categoryVO.setAgentMaxLoginCount(3L);
        p_categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        p_categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        p_categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        p_categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        p_categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        p_categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        p_categoryVO.setAgentParentOrOwnerRadioValue("42");
        p_categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        p_categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        p_categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        p_categoryVO.setAgentRoleName("Agent Role Name");
        p_categoryVO.setAgentRoleTypeList(new ArrayList());
        p_categoryVO.setAgentRolesMapSelected(new HashMap());
        p_categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        p_categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        p_categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        p_categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        p_categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        p_categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        p_categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        p_categoryVO.setAllowedGatewayTypes(new ArrayList());
        p_categoryVO.setAuthenticationType("Type");
        p_categoryVO.setCategoryCode("Category Code");
        p_categoryVO.setCategoryName("Category Name");
        p_categoryVO.setCategorySequenceNumber(10);
        p_categoryVO.setCategoryStatus("Category Status");
        p_categoryVO.setCategoryType("Category Type");
        p_categoryVO.setCategoryTypeCode("Category Type Code");
        p_categoryVO.setCp2pPayee("Payee");
        p_categoryVO.setCp2pPayer("Payer");
        p_categoryVO.setCp2pWithinList("Within List");
        p_categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        p_categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setDisplayAllowed("Display Allowed");
        p_categoryVO.setDomainAllowed("Domain Allowed");
        p_categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        p_categoryVO.setDomainName("Domain Name");
        p_categoryVO.setDomainTypeCode("Domain Type Code");
        p_categoryVO.setFixedDomains("Fixed Domains");
        p_categoryVO.setFixedRoles("Fixed Roles");
        p_categoryVO.setGeographicalDomainSeqNo(1);
        p_categoryVO.setGrphDomainSequenceNo(1);
        p_categoryVO.setGrphDomainType("Grph Domain Type");
        p_categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        p_categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        p_categoryVO.setLastModifiedTime(1L);
        p_categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        p_categoryVO.setMaxLoginCount(3L);
        p_categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        p_categoryVO.setMaxTxnMsisdnInt(3);
        p_categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        p_categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        p_categoryVO.setModifyAllowed("Modify Allowed");
        p_categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        p_categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        p_categoryVO.setNumberOfCategoryForDomain(10);
        p_categoryVO.setOutletsAllowed("Outlets Allowed");
        p_categoryVO.setParentCategoryCode("Parent Category Code");
        p_categoryVO.setParentOrOwnerRadioValue("42");
        p_categoryVO.setProductTypeAllowed("Product Type Allowed");
        p_categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        p_categoryVO.setRadioIndex(1);
        p_categoryVO.setRechargeByParentOnly("By Parent Only");
        p_categoryVO.setRecordCount(3);
        p_categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        p_categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        p_categoryVO.setSequenceNumber(10);
        p_categoryVO.setServiceAllowed("Service Allowed");
        p_categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        p_categoryVO.setTransferToListOnly("Transfer To List Only");
        p_categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        p_categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        p_categoryVO.setUserIdPrefix("User Id Prefix");
        p_categoryVO.setViewOnNetworkBlock("View On Network Block");
        p_categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        CategoryVO actualConstructCategoryVOFromResult = channelDomainServiceImpl.constructCategoryVOFrom(p_form,
                p_categoryVO);
        assertSame(p_categoryVO, actualConstructCategoryVOFromResult);
        assertEquals("Category Name", actualConstructCategoryVOFromResult.getCategoryName());
        assertEquals("Category Code", actualConstructCategoryVOFromResult.getCategoryCode());
        assertEquals("Agent Allowed", actualConstructCategoryVOFromResult.getAgentAllowed());
        verify(p_form).getAgentAllowed();
        verify(p_form).getCategoryCode();
        verify(p_form).getCategoryName();
        verify(p_form).getCategoryStatus();
        verify(p_form).getCp2pPayee();
        verify(p_form).getCp2pPayer();
        verify(p_form).getCp2pWithinList();
        verify(p_form).getDisplayAllowed();
        verify(p_form).getDomainCodeforDomain();
        verify(p_form).getFixedRoles();
        verify(p_form).getGrphDomainType();
        verify(p_form).getHierarchyAllowed();
        verify(p_form).getListLevelCode();
        verify(p_form).getLowBalanceAlertAllow();
        verify(p_form).getMaxLoginCount();
        verify(p_form).getMaxTxnMsisdn();
        verify(p_form).getModifyAllowed();
        verify(p_form).getMultipleGrphDomains();
        verify(p_form).getMultipleLoginAllowed();
        verify(p_form).getOutletsAllowed();
        verify(p_form, atLeast(1)).getParentCategoryCode();
        verify(p_form).getProductTypeAssociationAllowed();
        verify(p_form).getRechargeByParentOnly();
        verify(p_form).getRestrictedMsisdns();
        verify(p_form).getScheduledTransferAllowed();
        verify(p_form).getServiceAllowed();
        verify(p_form).getTransferToListOnly();
        verify(p_form).getUnctrlTransferAllowed();
        verify(p_form).getUserIdPrefix();
        verify(p_form).getViewOnNetworkBlock();
        verify(p_form).getModifiedMessageGatewayList();
        verify(p_form).getLastModifiedTime();
    }
}

