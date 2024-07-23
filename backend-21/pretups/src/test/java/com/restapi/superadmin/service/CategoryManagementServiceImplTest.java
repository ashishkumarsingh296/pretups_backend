package com.restapi.superadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.XssWrapper;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.web.DomainForm;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.util.JUnitConfig;
import com.restapi.superadmin.AddAgentValidator;
import com.restapi.superadmin.requestVO.AddAgentRequestVO;
import com.restapi.superadmin.requestVO.DeleteCategoryRequestVO;
import com.restapi.superadmin.requestVO.SaveCategoryRequestVO;
import com.restapi.superadmin.responseVO.AddCategoryResponseVO;
import com.restapi.superadmin.responseVO.CategoryAgentViewResponseVO;
import com.restapi.superadmin.responseVO.CategoryListResponseVO;
import com.restapi.superadmin.responseVO.GetAgentScreenDetailsReq;
import com.restapi.superadmin.responseVO.UpdateCategoryOnlyResp;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {CategoryManagementServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CategoryManagementServiceImplTest {
    @MockBean
    private AddAgentValidator addAgentValidator;

    @Autowired
    private CategoryManagementServiceImpl categoryManagementServiceImpl;

    /**
     * Method under test: {@link CategoryManagementServiceImpl#getCategoryList(String, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetCategoryList() throws SQLException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.getCategoryList(CategoryManagementServiceImpl.java:89)

        // Arrange
        // TODO: Populate arranged inputs
        String domainCode = "";
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        CategoryListResponseVO actualCategoryList = this.categoryManagementServiceImpl.getCategoryList(domainCode,
                responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#deleteCategory(DeleteCategoryRequestVO, String, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testDeleteCategory() throws SQLException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.deleteCategory(CategoryManagementServiceImpl.java:147)

        // Arrange
        // TODO: Populate arranged inputs
        DeleteCategoryRequestVO request = JUnitConfig.getDeleteCategoryRequestVO();
        String loginId = "";
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualDeleteCategoryResult = this.categoryManagementServiceImpl.deleteCategory(request, loginId,
                responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#saveCategory(SaveCategoryRequestVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testSaveCategory() throws SQLException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.saveCategory(CategoryManagementServiceImpl.java:296)

        // Arrange
        // TODO: Populate arranged inputs
        SaveCategoryRequestVO request =  com.btsl.util.JUnitConfig.getSaveCategoryRequestVO();
        String loginId = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        AddCategoryResponseVO actualSaveCategoryResult = this.categoryManagementServiceImpl.saveCategory(request, loginId,
                httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#constructCategoryVOFromForm(DomainForm, CategoryVO)}
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.constructCategoryVOFromForm(CategoryManagementServiceImpl.java:522)
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
        categoryManagementServiceImpl.constructCategoryVOFromForm(p_form, p_categoryVO);
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#constructCategoryVOFromForm(DomainForm, CategoryVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testConstructCategoryVOFromForm2() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.constructCategoryVOFromForm(CategoryManagementServiceImpl.java:489)
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
        categoryManagementServiceImpl.constructCategoryVOFromForm(null, p_categoryVO);
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#constructCategoryVOFromForm(DomainForm, CategoryVO)}
     */
    @Test
    public void testConstructCategoryVOFromForm3() throws Exception {
        DomainForm p_form = mock(DomainForm.class);
        when(p_form.getAgentAllowed()).thenReturn("Agent Allowed");
        when(p_form.getAuthType()).thenReturn("Auth Type");
        when(p_form.getCategoryStatus()).thenReturn("Category Status");
        when(p_form.getCategoryType()).thenReturn("Category Type");
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
        when(p_form.getDomainCodeforCategory()).thenReturn("Domain Codefor Category");
        when(p_form.getDomainTypeCode()).thenReturn("Domain Type Code");
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
        CategoryVO actualConstructCategoryVOFromFormResult = categoryManagementServiceImpl
                .constructCategoryVOFromForm(p_form, p_categoryVO);
        assertSame(p_categoryVO, actualConstructCategoryVOFromFormResult);
        assertEquals("Category Name", actualConstructCategoryVOFromFormResult.getCategoryName());
        assertEquals("Category Code", actualConstructCategoryVOFromFormResult.getCategoryCode());
        assertEquals("Auth Type", actualConstructCategoryVOFromFormResult.getAuthenticationType());
        assertEquals("Agent Allowed", actualConstructCategoryVOFromFormResult.getAgentAllowed());
        verify(p_form).getAgentAllowed();
        verify(p_form).getAuthType();
        verify(p_form).getCategoryCode();
        verify(p_form).getCategoryName();
        verify(p_form).getCategoryStatus();
        verify(p_form).getCategoryType();
        verify(p_form).getCp2pPayee();
        verify(p_form).getCp2pPayer();
        verify(p_form).getCp2pWithinList();
        verify(p_form).getDisplayAllowed();
        verify(p_form).getDomainCodeforCategory();
        verify(p_form).getDomainTypeCode();
        verify(p_form).getFixedRoles();
        verify(p_form).getGrphDomainType();
        verify(p_form).getHierarchyAllowed();
        verify(p_form).getListLevelCode();
        verify(p_form).getLowBalanceAlertAllow();
        verify(p_form).getMaxLoginCount();
        verify(p_form, atLeast(1)).getMaxTxnMsisdn();
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
     * Method under test: {@link CategoryManagementServiceImpl#constructCategoryVOFromForm(DomainForm, CategoryVO)}
     */
    @Test
    public void testConstructCategoryVOFromForm4() throws Exception {
        DomainForm p_form = mock(DomainForm.class);
        when(p_form.getAgentAllowed()).thenReturn("Agent Allowed");
        when(p_form.getAuthType()).thenReturn("Auth Type");
        when(p_form.getCategoryStatus()).thenReturn("Category Status");
        when(p_form.getCategoryType()).thenReturn("Category Type");
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
        when(p_form.getDomainCodeforCategory()).thenReturn("Domain Codefor Category");
        when(p_form.getDomainTypeCode()).thenReturn("Domain Type Code");
        when(p_form.getFixedRoles()).thenReturn("Fixed Roles");
        when(p_form.getGrphDomainType()).thenReturn("Grph Domain Type");
        when(p_form.getMaxLoginCount()).thenReturn("3");
        when(p_form.getMultipleGrphDomains()).thenReturn("Multiple Grph Domains");
        when(p_form.getMultipleLoginAllowed()).thenReturn("Multiple Login Allowed");
        when(p_form.getParentCategoryCode()).thenReturn("Parent Category Code");
        when(p_form.getViewOnNetworkBlock()).thenReturn("View On Network Block");
        when(p_form.getModifiedMessageGatewayList()).thenReturn(new ArrayList());
        CategoryVO p_categoryVO = mock(CategoryVO.class);
        doNothing().when(p_categoryVO).setAgentAgentAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentAllowedFlag(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentCategoryCode(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentCategoryName(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentCategoryStatus(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_categoryVO).setAgentCategoryType(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentCheckArray(Mockito.<String[]>any());
        doNothing().when(p_categoryVO).setAgentCp2pPayee(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentCp2pPayer(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentDomainName(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentFixedRoles(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentGatewayName(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentGatewayType(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentGrphDomainType(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentMaxLoginCount(anyLong());
        doNothing().when(p_categoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_categoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_categoryVO).setAgentModifyAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentRoleName(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_categoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        doNothing().when(p_categoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentServiceAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        doNothing().when(p_categoryVO).setAuthenticationType(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCategoryName(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCategorySequenceNumber(anyInt());
        doNothing().when(p_categoryVO).setCategoryStatus(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCategoryType(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCategoryTypeCode(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCp2pPayee(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCp2pPayer(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCp2pWithinList(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCreatedBy(Mockito.<String>any());
        doNothing().when(p_categoryVO).setCreatedOn(Mockito.<Date>any());
        doNothing().when(p_categoryVO).setDisplayAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setDomainAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setDomainCodeforCategory(Mockito.<String>any());
        doNothing().when(p_categoryVO).setDomainName(Mockito.<String>any());
        doNothing().when(p_categoryVO).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(p_categoryVO).setFixedDomains(Mockito.<String>any());
        doNothing().when(p_categoryVO).setFixedRoles(Mockito.<String>any());
        doNothing().when(p_categoryVO).setGeographicalDomainSeqNo(anyInt());
        doNothing().when(p_categoryVO).setGrphDomainSequenceNo(anyInt());
        doNothing().when(p_categoryVO).setGrphDomainType(Mockito.<String>any());
        doNothing().when(p_categoryVO).setGrphDomainTypeName(Mockito.<String>any());
        doNothing().when(p_categoryVO).setHierarchyAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setLastModifiedTime(anyLong());
        doNothing().when(p_categoryVO).setLowBalAlertAllow(Mockito.<String>any());
        doNothing().when(p_categoryVO).setMaxLoginCount(anyLong());
        doNothing().when(p_categoryVO).setMaxTxnMsisdn(Mockito.<String>any());
        doNothing().when(p_categoryVO).setMaxTxnMsisdnInt(anyInt());
        doNothing().when(p_categoryVO).setModifiedBy(Mockito.<String>any());
        doNothing().when(p_categoryVO).setModifiedOn(Mockito.<Date>any());
        doNothing().when(p_categoryVO).setModifyAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setMultipleGrphDomains(Mockito.<String>any());
        doNothing().when(p_categoryVO).setMultipleLoginAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setNumberOfCategoryForDomain(anyInt());
        doNothing().when(p_categoryVO).setOutletsAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setParentCategoryCode(Mockito.<String>any());
        doNothing().when(p_categoryVO).setParentOrOwnerRadioValue(Mockito.<String>any());
        doNothing().when(p_categoryVO).setProductTypeAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setProductTypeAssociationAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setRadioIndex(anyInt());
        doNothing().when(p_categoryVO).setRechargeByParentOnly(Mockito.<String>any());
        doNothing().when(p_categoryVO).setRecordCount(anyInt());
        doNothing().when(p_categoryVO).setRestrictedMsisdns(Mockito.<String>any());
        doNothing().when(p_categoryVO).setScheduledTransferAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setSequenceNumber(anyInt());
        doNothing().when(p_categoryVO).setServiceAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setSmsInterfaceAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setTransferToListOnly(Mockito.<String>any());
        doNothing().when(p_categoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        doNothing().when(p_categoryVO).setUnctrlTransferAllowed(Mockito.<String>any());
        doNothing().when(p_categoryVO).setUserIdPrefix(Mockito.<String>any());
        doNothing().when(p_categoryVO).setViewOnNetworkBlock(Mockito.<String>any());
        doNothing().when(p_categoryVO).setWebInterfaceAllowed(Mockito.<String>any());
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
        assertSame(p_categoryVO, categoryManagementServiceImpl.constructCategoryVOFromForm(p_form, p_categoryVO));
        verify(p_form).getAgentAllowed();
        verify(p_form).getAuthType();
        verify(p_form).getCategoryCode();
        verify(p_form).getCategoryName();
        verify(p_form).getCategoryStatus();
        verify(p_form).getCategoryType();
        verify(p_form).getCp2pPayee();
        verify(p_form).getCp2pPayer();
        verify(p_form).getCp2pWithinList();
        verify(p_form).getDisplayAllowed();
        verify(p_form).getDomainCodeforCategory();
        verify(p_form).getDomainTypeCode();
        verify(p_form).getFixedRoles();
        verify(p_form).getGrphDomainType();
        verify(p_form).getHierarchyAllowed();
        verify(p_form).getListLevelCode();
        verify(p_form).getLowBalanceAlertAllow();
        verify(p_form).getMaxLoginCount();
        verify(p_form, atLeast(1)).getMaxTxnMsisdn();
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
        verify(p_categoryVO).setAgentAgentAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setAgentAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentAllowedFlag(Mockito.<String>any());
        verify(p_categoryVO).setAgentCategoryCode(Mockito.<String>any());
        verify(p_categoryVO).setAgentCategoryName(Mockito.<String>any());
        verify(p_categoryVO).setAgentCategoryStatus(Mockito.<String>any());
        verify(p_categoryVO).setAgentCategoryStatusList(Mockito.<ArrayList<Object>>any());
        verify(p_categoryVO).setAgentCategoryType(Mockito.<String>any());
        verify(p_categoryVO).setAgentCheckArray(Mockito.<String[]>any());
        verify(p_categoryVO).setAgentCp2pPayee(Mockito.<String>any());
        verify(p_categoryVO).setAgentCp2pPayer(Mockito.<String>any());
        verify(p_categoryVO).setAgentCp2pWithinList(Mockito.<String>any());
        verify(p_categoryVO).setAgentDisplayAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentDomainCodeforCategory(Mockito.<String>any());
        verify(p_categoryVO).setAgentDomainName(Mockito.<String>any());
        verify(p_categoryVO).setAgentFixedRoles(Mockito.<String>any());
        verify(p_categoryVO).setAgentGatewayName(Mockito.<String>any());
        verify(p_categoryVO).setAgentGatewayType(Mockito.<String>any());
        verify(p_categoryVO).setAgentGeographicalDomainList(Mockito.<String>any());
        verify(p_categoryVO).setAgentGrphDomainType(Mockito.<String>any());
        verify(p_categoryVO).setAgentHierarchyAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentLowBalAlertAllow(Mockito.<String>any());
        verify(p_categoryVO).setAgentMaxLoginCount(anyLong());
        verify(p_categoryVO).setAgentMaxTxnMsisdn(Mockito.<String>any());
        verify(p_categoryVO).setAgentMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        verify(p_categoryVO).setAgentModifiedMessageGatewayTypeList(Mockito.<ArrayList<Object>>any());
        verify(p_categoryVO).setAgentModifyAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentMultipleGrphDomains(Mockito.<String>any());
        verify(p_categoryVO).setAgentMultipleLoginAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentOutletsAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentParentOrOwnerRadioValue(Mockito.<String>any());
        verify(p_categoryVO).setAgentProductTypeAssociationAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentRechargeByParentOnly(Mockito.<String>any());
        verify(p_categoryVO).setAgentRestrictedMsisdns(Mockito.<String>any());
        verify(p_categoryVO).setAgentRoleName(Mockito.<String>any());
        verify(p_categoryVO).setAgentRoleTypeList(Mockito.<ArrayList<Object>>any());
        verify(p_categoryVO).setAgentRolesMapSelected(Mockito.<HashMap<Object, Object>>any());
        verify(p_categoryVO).setAgentScheduledTransferAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentServiceAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentSmsInterfaceAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentUnctrlTransferAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAgentUserIdPrefix(Mockito.<String>any());
        verify(p_categoryVO).setAgentViewOnNetworkBlock(Mockito.<String>any());
        verify(p_categoryVO).setAgentWebInterfaceAllowed(Mockito.<String>any());
        verify(p_categoryVO).setAllowedGatewayTypes(Mockito.<ArrayList<Object>>any());
        verify(p_categoryVO, atLeast(1)).setAuthenticationType(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setCategoryCode(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setCategoryName(Mockito.<String>any());
        verify(p_categoryVO).setCategorySequenceNumber(anyInt());
        verify(p_categoryVO, atLeast(1)).setCategoryStatus(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setCategoryType(Mockito.<String>any());
        verify(p_categoryVO).setCategoryTypeCode(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setCp2pPayee(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setCp2pPayer(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setCp2pWithinList(Mockito.<String>any());
        verify(p_categoryVO).setCreatedBy(Mockito.<String>any());
        verify(p_categoryVO).setCreatedOn(Mockito.<Date>any());
        verify(p_categoryVO, atLeast(1)).setDisplayAllowed(Mockito.<String>any());
        verify(p_categoryVO).setDomainAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setDomainCodeforCategory(Mockito.<String>any());
        verify(p_categoryVO).setDomainName(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setDomainTypeCode(Mockito.<String>any());
        verify(p_categoryVO).setFixedDomains(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setFixedRoles(Mockito.<String>any());
        verify(p_categoryVO).setGeographicalDomainSeqNo(anyInt());
        verify(p_categoryVO).setGrphDomainSequenceNo(anyInt());
        verify(p_categoryVO, atLeast(1)).setGrphDomainType(Mockito.<String>any());
        verify(p_categoryVO).setGrphDomainTypeName(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setHierarchyAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setLastModifiedTime(anyLong());
        verify(p_categoryVO, atLeast(1)).setLowBalAlertAllow(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setMaxLoginCount(anyLong());
        verify(p_categoryVO, atLeast(1)).setMaxTxnMsisdn(Mockito.<String>any());
        verify(p_categoryVO).setMaxTxnMsisdnInt(anyInt());
        verify(p_categoryVO).setModifiedBy(Mockito.<String>any());
        verify(p_categoryVO).setModifiedOn(Mockito.<Date>any());
        verify(p_categoryVO, atLeast(1)).setModifyAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setMultipleGrphDomains(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setMultipleLoginAllowed(Mockito.<String>any());
        verify(p_categoryVO).setNumberOfCategoryForDomain(anyInt());
        verify(p_categoryVO, atLeast(1)).setOutletsAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setParentCategoryCode(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setParentOrOwnerRadioValue(Mockito.<String>any());
        verify(p_categoryVO).setProductTypeAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setProductTypeAssociationAllowed(Mockito.<String>any());
        verify(p_categoryVO).setRadioIndex(anyInt());
        verify(p_categoryVO, atLeast(1)).setRechargeByParentOnly(Mockito.<String>any());
        verify(p_categoryVO).setRecordCount(anyInt());
        verify(p_categoryVO, atLeast(1)).setRestrictedMsisdns(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setScheduledTransferAllowed(Mockito.<String>any());
        verify(p_categoryVO).setSequenceNumber(anyInt());
        verify(p_categoryVO, atLeast(1)).setServiceAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setSmsInterfaceAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setTransferToListOnly(Mockito.<String>any());
        verify(p_categoryVO).setTxnOutsideHierchy(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setUnctrlTransferAllowed(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setUserIdPrefix(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setViewOnNetworkBlock(Mockito.<String>any());
        verify(p_categoryVO, atLeast(1)).setWebInterfaceAllowed(Mockito.<String>any());
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#updateCategoryAgent(SaveCategoryRequestVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUpdateCategoryAgent() throws SQLException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.updateCategoryAgent(CategoryManagementServiceImpl.java:580)

        // Arrange
        // TODO: Populate arranged inputs
        SaveCategoryRequestVO request = null;
        String loginId = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualUpdateCategoryAgentResult = this.categoryManagementServiceImpl.updateCategoryAgent(request,
                loginId, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#saveUpdate(DomainForm, HttpServletRequest, CategoryVO, Connection, String)}
     */
    @Test
    public void testSaveUpdate() {
        DomainForm theForm = new DomainForm();
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));

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
        assertEquals(-1,
                categoryManagementServiceImpl.saveUpdate(theForm, request, p_categoryVO, mock(Connection.class), "42"));
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#saveUpdate(DomainForm, HttpServletRequest, CategoryVO, Connection, String)}
     */
    @Test
    public void testSaveUpdate2() {
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));

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
        assertEquals(-1,
                categoryManagementServiceImpl.saveUpdate(null, request, p_categoryVO, mock(Connection.class), "42"));
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#addAgent(AddAgentRequestVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddAgent() throws BTSLBaseException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.addAgent(CategoryManagementServiceImpl.java:872)

        // Arrange
        // TODO: Populate arranged inputs
        AddAgentRequestVO request = null;
        String loginId = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        BaseResponse actualAddAgentResult = this.categoryManagementServiceImpl.addAgent(request, loginId,
                httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#constructAgentCategoryVOFromForm(Connection, AddAgentRequestVO, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructAgentCategoryVOFromForm() throws BTSLBaseException, SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalStateException: Duplicate key _gatewayCode=null,_gatewayName=String,_gatewaySubType=null,_gatewayType=String,_handlerClass=null,_host=null,_networkCode=null,_protocol=null,_binaryMsgAllowed=null,_plainMsgAllowed=null,_accessFrom=String,_gatewaySubTypeName=null,_createdBy=null,_createdOn=null,_modifiedBy=null,_modifiedOn=null
        //   _requestGatewayVO=>_gatewayCode=null,_port=null,_servicePort=null,_loginID=null,_encryptionLevel=null,_encryptionKey=null,_contentType=null,_authType=null,_status=null
        //   _responseGatewayVO=>_gatewayCode=null,_port=null,_servicePort=null,_loginID=null,_destNo=null,_status=null
        //       at java.util.stream.Collectors.lambda$throwingMerger$0(Collectors.java:133)
        //       at java.util.HashMap.merge(HashMap.java:1255)
        //       at java.util.stream.Collectors.lambda$toMap$58(Collectors.java:1320)
        //       at java.util.stream.ReduceOps$3ReducingSink.accept(ReduceOps.java:169)
        //       at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1384)
        //       at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
        //       at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        //       at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
        //       at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        //       at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.constructAgentCategoryVOFromForm(CategoryManagementServiceImpl.java:1017)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        AddAgentRequestVO p_form = new AddAgentRequestVO();
        p_form.setAgentAllowed("Agent Allowed");
        p_form.setAgentCategoryCode("Agent Category Code");
        p_form.setAgentCategoryName("Agent Category Name");
        p_form.setAllowLowBalanceAlert("Allow Low Balance Alert");
        p_form.setAllowedSources("Allowed Sources");
        p_form.setCp2pPayee("Cp2p Payee");
        p_form.setCp2pPayer("Cp2p Payer");
        p_form.setCp2pWithinList("Cp2p Within List");
        p_form.setDomainCodeofCategory("Domain Codeof Category");
        p_form.setDomainName("Domain Name");
        p_form.setGeoDomainType("Geo Domain Type");
        p_form.setHierarchyAllowed("Hierarchy Allowed");
        p_form.setMaximumLoginCount("3");
        p_form.setMaximumTransMsisdn("Maximum Trans Msisdn");
        p_form.setMultipleLoginAllowed("Multiple Login Allowed");
        p_form.setOutletAllowed("Outlet Allowed");
        p_form.setParentCategoryCode("Parent Category Code");
        p_form.setParentOrOwnerRadioValue("42");
        p_form.setRechargeThruParentOnly("Recharge Thru Parent Only");
        p_form.setRestrictedMsisdn("Restricted Msisdn");
        p_form.setRoleFlag(new String[]{"Role Flag"});
        p_form.setRoleType("Role Type");
        p_form.setScheduleTransferAllowed("Schedule Transfer Allowed");
        p_form.setServicesAllowed("Services Allowed");
        p_form.setTransferToListOnly("Transfer To List Only");
        p_form.setUncontrolledTransferAllowed("Uncontrolled Transfer Allowed");
        p_form.setUserIDPrefix("User IDPrefix");
        p_form.setViewonNetworkBlock("Viewon Network Block");

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
        categoryManagementServiceImpl.constructAgentCategoryVOFromForm(JUnitConfig.getConnection(), p_form, p_categoryVO);
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#constructAgentCategoryVOFromForm(Connection, AddAgentRequestVO, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructAgentCategoryVOFromForm2() throws BTSLBaseException, SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.sql.processing
        //       at com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO.loadMessageGatewayTypeList(MessageGatewayWebDAO.java:2138)
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.constructAgentCategoryVOFromForm(CategoryManagementServiceImpl.java:1015)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        AddAgentRequestVO p_form = new AddAgentRequestVO();
        p_form.setAgentAllowed("Agent Allowed");
        p_form.setAgentCategoryCode("Agent Category Code");
        p_form.setAgentCategoryName("Agent Category Name");
        p_form.setAllowLowBalanceAlert("Allow Low Balance Alert");
        p_form.setAllowedSources("Allowed Sources");
        p_form.setCp2pPayee("Cp2p Payee");
        p_form.setCp2pPayer("Cp2p Payer");
        p_form.setCp2pWithinList("Cp2p Within List");
        p_form.setDomainCodeofCategory("Domain Codeof Category");
        p_form.setDomainName("Domain Name");
        p_form.setGeoDomainType("Geo Domain Type");
        p_form.setHierarchyAllowed("Hierarchy Allowed");
        p_form.setMaximumLoginCount("3");
        p_form.setMaximumTransMsisdn("Maximum Trans Msisdn");
        p_form.setMultipleLoginAllowed("Multiple Login Allowed");
        p_form.setOutletAllowed("Outlet Allowed");
        p_form.setParentCategoryCode("Parent Category Code");
        p_form.setParentOrOwnerRadioValue("42");
        p_form.setRechargeThruParentOnly("Recharge Thru Parent Only");
        p_form.setRestrictedMsisdn("Restricted Msisdn");
        p_form.setRoleFlag(new String[]{"Role Flag"});
        p_form.setRoleType("Role Type");
        p_form.setScheduleTransferAllowed("Schedule Transfer Allowed");
        p_form.setServicesAllowed("Services Allowed");
        p_form.setTransferToListOnly("Transfer To List Only");
        p_form.setUncontrolledTransferAllowed("Uncontrolled Transfer Allowed");
        p_form.setUserIDPrefix("User IDPrefix");
        p_form.setViewonNetworkBlock("Viewon Network Block");

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
        categoryManagementServiceImpl.constructAgentCategoryVOFromForm(JUnitConfig.getConnection(), p_form, p_categoryVO);
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#constructAgentCategoryVOFromForm(Connection, AddAgentRequestVO, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructAgentCategoryVOFromForm3() throws BTSLBaseException, SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.constructAgentCategoryVOFromForm(CategoryManagementServiceImpl.java:1024)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        AddAgentRequestVO p_form = new AddAgentRequestVO();
        p_form.setAgentAllowed("Agent Allowed");
        p_form.setAgentCategoryCode("Agent Category Code");
        p_form.setAgentCategoryName("Agent Category Name");
        p_form.setAllowLowBalanceAlert("Allow Low Balance Alert");
        p_form.setAllowedSources("Allowed Sources");
        p_form.setCp2pPayee("Cp2p Payee");
        p_form.setCp2pPayer("Cp2p Payer");
        p_form.setCp2pWithinList("Cp2p Within List");
        p_form.setDomainCodeofCategory("Domain Codeof Category");
        p_form.setDomainName("Domain Name");
        p_form.setGeoDomainType("Geo Domain Type");
        p_form.setHierarchyAllowed("Hierarchy Allowed");
        p_form.setMaximumLoginCount("3");
        p_form.setMaximumTransMsisdn("Maximum Trans Msisdn");
        p_form.setMultipleLoginAllowed("Multiple Login Allowed");
        p_form.setOutletAllowed("Outlet Allowed");
        p_form.setParentCategoryCode("Parent Category Code");
        p_form.setParentOrOwnerRadioValue("42");
        p_form.setRechargeThruParentOnly("Recharge Thru Parent Only");
        p_form.setRestrictedMsisdn("Restricted Msisdn");
        p_form.setRoleFlag(new String[]{"Role Flag"});
        p_form.setRoleType("Role Type");
        p_form.setScheduleTransferAllowed("Schedule Transfer Allowed");
        p_form.setServicesAllowed("Services Allowed");
        p_form.setTransferToListOnly("Transfer To List Only");
        p_form.setUncontrolledTransferAllowed("Uncontrolled Transfer Allowed");
        p_form.setUserIDPrefix("User IDPrefix");
        p_form.setViewonNetworkBlock("Viewon Network Block");

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
        categoryManagementServiceImpl.constructAgentCategoryVOFromForm(JUnitConfig.getConnection(), p_form, p_categoryVO);
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#constructAgentCategoryVOFromForm(Connection, AddAgentRequestVO, CategoryVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConstructAgentCategoryVOFromForm4() throws BTSLBaseException, SQLException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.constructAgentCategoryVOFromForm(CategoryManagementServiceImpl.java:1024)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
       // when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        AddAgentRequestVO p_form = new AddAgentRequestVO();
        p_form.setAgentAllowed("Agent Allowed");
        p_form.setAgentCategoryCode("Agent Category Code");
        p_form.setAgentCategoryName("Agent Category Name");
        p_form.setAllowLowBalanceAlert("Allow Low Balance Alert");
        p_form.setAllowedSources("Allowed Sources");
        p_form.setCp2pPayee("Cp2p Payee");
        p_form.setCp2pPayer("Cp2p Payer");
        p_form.setCp2pWithinList("Cp2p Within List");
        p_form.setDomainCodeofCategory("Domain Codeof Category");
        p_form.setDomainName("Domain Name");
        p_form.setGeoDomainType("Geo Domain Type");
        p_form.setHierarchyAllowed("Hierarchy Allowed");
        p_form.setMaximumLoginCount("3");
        p_form.setMaximumTransMsisdn("Maximum Trans Msisdn");
        p_form.setMultipleLoginAllowed("Multiple Login Allowed");
        p_form.setOutletAllowed("Outlet Allowed");
        p_form.setParentCategoryCode("Parent Category Code");
        p_form.setParentOrOwnerRadioValue("42");
        p_form.setRechargeThruParentOnly("Recharge Thru Parent Only");
        p_form.setRestrictedMsisdn("Restricted Msisdn");
        p_form.setRoleFlag(new String[]{"Role Flag"});
        p_form.setRoleType("Role Type");
        p_form.setScheduleTransferAllowed("Schedule Transfer Allowed");
        p_form.setServicesAllowed("Services Allowed");
        p_form.setTransferToListOnly("Transfer To List Only");
        p_form.setUncontrolledTransferAllowed("Uncontrolled Transfer Allowed");
        p_form.setUserIDPrefix("User IDPrefix");
        p_form.setViewonNetworkBlock("Viewon Network Block");

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
        categoryManagementServiceImpl.constructAgentCategoryVOFromForm(JUnitConfig.getConnection(), p_form, p_categoryVO);
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#getAddAgentScreenInputDet(GetAgentScreenDetailsReq, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetAddAgentScreenInputDet() throws SQLException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.getAddAgentScreenInputDet(CategoryManagementServiceImpl.java:1125)

        // Arrange
        // TODO: Populate arranged inputs
        GetAgentScreenDetailsReq getAgentScreenDetailsReq = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        CategoryAgentViewResponseVO actualAddAgentScreenInputDet = this.categoryManagementServiceImpl
                .getAddAgentScreenInputDet(getAgentScreenDetailsReq, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#getCategoryInfo(String, String, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetCategoryInfo() throws SQLException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.getCategoryInfo(CategoryManagementServiceImpl.java:1207)

        // Arrange
        // TODO: Populate arranged inputs
        String domainCode = "";
        String categoryCode = "";
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        CategoryListResponseVO actualCategoryInfo = this.categoryManagementServiceImpl.getCategoryInfo(domainCode,
                categoryCode, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#updateCategoryOnly(SaveCategoryRequestVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUpdateCategoryOnly() throws SQLException {
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
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.updateCategoryOnly(CategoryManagementServiceImpl.java:1276)

        // Arrange
        // TODO: Populate arranged inputs
        SaveCategoryRequestVO request = null;
        String loginId = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        UpdateCategoryOnlyResp actualUpdateCategoryOnlyResult = this.categoryManagementServiceImpl
                .updateCategoryOnly(request, loginId, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryManagementServiceImpl#cleanupCategoryUnassignedDomainData()}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCleanupCategoryUnassignedDomainData() {
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
        //       at com.web.pretups.domain.businesslogic.CategoryWebDAO.cleanupCategoryunAssgndData(CategoryWebDAO.java:5169)
        //       at com.restapi.superadmin.service.CategoryManagementServiceImpl.cleanupCategoryUnassignedDomainData(CategoryManagementServiceImpl.java:1421)

        // Arrange and Act
        // TODO: Populate arranged inputs
        this.categoryManagementServiceImpl.cleanupCategoryUnassignedDomainData();

        // Assert
        // TODO: Add assertions on result
    }
}

