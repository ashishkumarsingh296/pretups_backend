package com.restapi.networkadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.restapi.networkadmin.requestVO.AddPromoTransferReqVO;
import com.restapi.networkadmin.requestVO.ModifyPromoTransferReqVO;
import com.restapi.networkadmin.requestVO.PromoLoadParentParamReq;
import com.restapi.networkadmin.requestVO.PromoTransferDropdownListReq;
import com.restapi.networkadmin.requestVO.ReceiverSectionInputs;
import com.restapi.networkadmin.requestVO.SearchPromoTransferReqVO;
import com.restapi.networkadmin.responseVO.AddPromoTransferRuleRespVO;
import com.restapi.networkadmin.responseVO.ModifyPromoTransfRuleRespVO;
import com.restapi.networkadmin.responseVO.PromoDepDropdownlistRespVO;
import com.restapi.networkadmin.responseVO.PromoLoadParentUserRespVO;
import com.restapi.networkadmin.responseVO.SearchPromoTransferRespVO;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PromotionalTransfServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PromotionalTransfServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private PromotionalTransfServiceImpl promotionalTransfServiceImpl;

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getPromoDependencyDropDownlist(PromoTransferDropdownListReq, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testGetPromoDependencyDropDownlist() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.PromotionalTransfServiceImpl.getPromoDependencyDropDownlist(PromotionalTransfServiceImpl.java:86)

        // Arrange
        // TODO: Populate arranged inputs
        PromoTransferDropdownListReq promoTransferDropdownListReq = null;
        String userLoginId = "";

        // Act
        PromoDepDropdownlistRespVO actualPromoDependencyDropDownlist = this.promotionalTransfServiceImpl
                .getPromoDependencyDropDownlist(promoTransferDropdownListReq, userLoginId);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#loadParentUserList(PromoLoadParentParamReq, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testLoadParentUserList() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.PromotionalTransfServiceImpl.loadParentUserList(PromotionalTransfServiceImpl.java:259)

        // Arrange
        // TODO: Populate arranged inputs
        PromoLoadParentParamReq promoLoadParentParamReq = null;
        String userLoginId = "";

        // Act
        PromoLoadParentUserRespVO actualLoadParentUserListResult = this.promotionalTransfServiceImpl
                .loadParentUserList(promoLoadParentParamReq, userLoginId);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#searchPromoTransferData(SearchPromoTransferReqVO, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testSearchPromoTransferData() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.PromotionalTransfServiceImpl.searchPromoTransferData(PromotionalTransfServiceImpl.java:323)

        // Arrange
        // TODO: Populate arranged inputs
        SearchPromoTransferReqVO searchPromoTransferReqVO = null;
        String userLoginId = "";

        // Act
        SearchPromoTransferRespVO actualSearchPromoTransferDataResult = this.promotionalTransfServiceImpl
                .searchPromoTransferData(searchPromoTransferReqVO, userLoginId);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#validateFieldData(SearchPromoTransferReqVO)}
     */
    @Test
    public void testValidateFieldData() throws BTSLBaseException {
        SearchPromoTransferReqVO searchPromoTransferReqVO = new SearchPromoTransferReqVO();
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setUserID("User ID");
        thrown.expect(BTSLBaseException.class);
        promotionalTransfServiceImpl.validateFieldData(searchPromoTransferReqVO);
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#validateAddFieldData(AddPromoTransferReqVO)}
     */
    @Test
    public void testValidateAddFieldData() throws BTSLBaseException {
        AddPromoTransferReqVO addPromoTransferReqVO = new AddPromoTransferReqVO();
        addPromoTransferReqVO.setCategoryCode("Category Code");
        addPromoTransferReqVO.setCellGroupID("Cell Group ID");
        addPromoTransferReqVO.setDomainCode("Domain Code");
        addPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        addPromoTransferReqVO.setGeography("Geography");
        addPromoTransferReqVO.setGrade("Grade");
        addPromoTransferReqVO.setList(new ArrayList<>());
        addPromoTransferReqVO.setOptionTab("Option Tab");
        addPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        addPromoTransferReqVO.setUserID("User ID");
        thrown.expect(BTSLBaseException.class);
        promotionalTransfServiceImpl.validateAddFieldData(addPromoTransferReqVO);
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#validateAddFieldData(AddPromoTransferReqVO)}
     */
    @Test
    public void testValidateAddFieldData2() throws BTSLBaseException {
        AddPromoTransferReqVO addPromoTransferReqVO = mock(AddPromoTransferReqVO.class);
        when(addPromoTransferReqVO.getUserID()).thenReturn("User ID");
        when(addPromoTransferReqVO.getOptionTab()).thenReturn("USR");
        doNothing().when(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        doNothing().when(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setUserID(Mockito.<String>any());
        addPromoTransferReqVO.setCategoryCode("Category Code");
        addPromoTransferReqVO.setCellGroupID("Cell Group ID");
        addPromoTransferReqVO.setDomainCode("Domain Code");
        addPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        addPromoTransferReqVO.setGeography("Geography");
        addPromoTransferReqVO.setGrade("Grade");
        addPromoTransferReqVO.setList(new ArrayList<>());
        addPromoTransferReqVO.setOptionTab("Option Tab");
        addPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        addPromoTransferReqVO.setUserID("User ID");
        promotionalTransfServiceImpl.validateAddFieldData(addPromoTransferReqVO);
        verify(addPromoTransferReqVO).getOptionTab();
        verify(addPromoTransferReqVO).getUserID();
        verify(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        verify(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        verify(addPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#validateAddFieldData(AddPromoTransferReqVO)}
     */
    @Test
    public void testValidateAddFieldData3() throws BTSLBaseException {
        AddPromoTransferReqVO addPromoTransferReqVO = mock(AddPromoTransferReqVO.class);
        when(addPromoTransferReqVO.getUserID()).thenReturn("");
        when(addPromoTransferReqVO.getOptionTab()).thenReturn("USR");
        doNothing().when(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        doNothing().when(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setUserID(Mockito.<String>any());
        addPromoTransferReqVO.setCategoryCode("Category Code");
        addPromoTransferReqVO.setCellGroupID("Cell Group ID");
        addPromoTransferReqVO.setDomainCode("Domain Code");
        addPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        addPromoTransferReqVO.setGeography("Geography");
        addPromoTransferReqVO.setGrade("Grade");
        addPromoTransferReqVO.setList(new ArrayList<>());
        addPromoTransferReqVO.setOptionTab("Option Tab");
        addPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        addPromoTransferReqVO.setUserID("User ID");
        thrown.expect(BTSLBaseException.class);
        promotionalTransfServiceImpl.validateAddFieldData(addPromoTransferReqVO);
        verify(addPromoTransferReqVO).getOptionTab();
        verify(addPromoTransferReqVO).getUserID();
        verify(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        verify(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        verify(addPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#validateAddFieldData(AddPromoTransferReqVO)}
     */
    @Test
    public void testValidateAddFieldData4() throws BTSLBaseException {
        AddPromoTransferReqVO addPromoTransferReqVO = mock(AddPromoTransferReqVO.class);
        when(addPromoTransferReqVO.getGrade()).thenReturn("Grade");
        when(addPromoTransferReqVO.getOptionTab()).thenReturn("GRD");
        doNothing().when(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        doNothing().when(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setUserID(Mockito.<String>any());
        addPromoTransferReqVO.setCategoryCode("Category Code");
        addPromoTransferReqVO.setCellGroupID("Cell Group ID");
        addPromoTransferReqVO.setDomainCode("Domain Code");
        addPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        addPromoTransferReqVO.setGeography("Geography");
        addPromoTransferReqVO.setGrade("Grade");
        addPromoTransferReqVO.setList(new ArrayList<>());
        addPromoTransferReqVO.setOptionTab("Option Tab");
        addPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        addPromoTransferReqVO.setUserID("User ID");
        promotionalTransfServiceImpl.validateAddFieldData(addPromoTransferReqVO);
        verify(addPromoTransferReqVO).getGrade();
        verify(addPromoTransferReqVO).getOptionTab();
        verify(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        verify(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        verify(addPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#validateAddFieldData(AddPromoTransferReqVO)}
     */
    @Test
    public void testValidateAddFieldData5() throws BTSLBaseException {
        AddPromoTransferReqVO addPromoTransferReqVO = mock(AddPromoTransferReqVO.class);
        when(addPromoTransferReqVO.getGrade()).thenReturn("");
        when(addPromoTransferReqVO.getOptionTab()).thenReturn("GRD");
        doNothing().when(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        doNothing().when(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        doNothing().when(addPromoTransferReqVO).setUserID(Mockito.<String>any());
        addPromoTransferReqVO.setCategoryCode("Category Code");
        addPromoTransferReqVO.setCellGroupID("Cell Group ID");
        addPromoTransferReqVO.setDomainCode("Domain Code");
        addPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        addPromoTransferReqVO.setGeography("Geography");
        addPromoTransferReqVO.setGrade("Grade");
        addPromoTransferReqVO.setList(new ArrayList<>());
        addPromoTransferReqVO.setOptionTab("Option Tab");
        addPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        addPromoTransferReqVO.setUserID("User ID");
        thrown.expect(BTSLBaseException.class);
        promotionalTransfServiceImpl.validateAddFieldData(addPromoTransferReqVO);
        verify(addPromoTransferReqVO).getGrade();
        verify(addPromoTransferReqVO).getOptionTab();
        verify(addPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(addPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(addPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(addPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        verify(addPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(addPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        verify(addPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getSenderSubscriberType(SearchPromoTransferReqVO)}
     */
    @Test
    public void testGetSenderSubscriberType() throws BTSLBaseException {
        SearchPromoTransferReqVO searchPromoTransferReqVO = new SearchPromoTransferReqVO();
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setUserID("User ID");
        thrown.expect(BTSLBaseException.class);
        promotionalTransfServiceImpl.getSenderSubscriberType(searchPromoTransferReqVO);
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getSenderSubscriberType(SearchPromoTransferReqVO)}
     */
    @Test
    public void testGetSenderSubscriberType2() throws BTSLBaseException {
        SearchPromoTransferReqVO searchPromoTransferReqVO = mock(SearchPromoTransferReqVO.class);
        when(searchPromoTransferReqVO.getUserID()).thenReturn("User ID");
        when(searchPromoTransferReqVO.getOptionTab()).thenReturn("USR");
        doNothing().when(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setUserID("User ID");
        assertEquals("User ID", promotionalTransfServiceImpl.getSenderSubscriberType(searchPromoTransferReqVO));
        verify(searchPromoTransferReqVO, atLeast(1)).getOptionTab();
        verify(searchPromoTransferReqVO).getUserID();
        verify(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getSenderSubscriberType(SearchPromoTransferReqVO)}
     */
    @Test
    public void testGetSenderSubscriberType3() throws BTSLBaseException {
        SearchPromoTransferReqVO searchPromoTransferReqVO = mock(SearchPromoTransferReqVO.class);
        when(searchPromoTransferReqVO.getOptionTab()).thenReturn(null);
        doNothing().when(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setUserID("User ID");
        assertNull(promotionalTransfServiceImpl.getSenderSubscriberType(searchPromoTransferReqVO));
        verify(searchPromoTransferReqVO).getOptionTab();
        verify(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getSenderSubscriberType(SearchPromoTransferReqVO)}
     */
    @Test
    public void testGetSenderSubscriberType4() throws BTSLBaseException {
        SearchPromoTransferReqVO searchPromoTransferReqVO = mock(SearchPromoTransferReqVO.class);
        when(searchPromoTransferReqVO.getGrade()).thenReturn("Grade");
        when(searchPromoTransferReqVO.getOptionTab()).thenReturn("GRD");
        doNothing().when(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setUserID("User ID");
        assertEquals("Grade", promotionalTransfServiceImpl.getSenderSubscriberType(searchPromoTransferReqVO));
        verify(searchPromoTransferReqVO).getGrade();
        verify(searchPromoTransferReqVO, atLeast(1)).getOptionTab();
        verify(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getAddSenderSubscriberType(AddPromoTransferReqVO)}
     */
    @Test
    public void testGetAddSenderSubscriberType() throws BTSLBaseException {
        AddPromoTransferReqVO searchPromoTransferReqVO = new AddPromoTransferReqVO();
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setList(new ArrayList<>());
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        searchPromoTransferReqVO.setUserID("User ID");
        thrown.expect(BTSLBaseException.class);
        promotionalTransfServiceImpl.getAddSenderSubscriberType(searchPromoTransferReqVO);
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getAddSenderSubscriberType(AddPromoTransferReqVO)}
     */
    @Test
    public void testGetAddSenderSubscriberType2() throws BTSLBaseException {
        AddPromoTransferReqVO searchPromoTransferReqVO = mock(AddPromoTransferReqVO.class);
        when(searchPromoTransferReqVO.getUserID()).thenReturn("User ID");
        when(searchPromoTransferReqVO.getOptionTab()).thenReturn("USR");
        doNothing().when(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        doNothing().when(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setList(new ArrayList<>());
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        searchPromoTransferReqVO.setUserID("User ID");
        assertEquals("User ID", promotionalTransfServiceImpl.getAddSenderSubscriberType(searchPromoTransferReqVO));
        verify(searchPromoTransferReqVO, atLeast(1)).getOptionTab();
        verify(searchPromoTransferReqVO).getUserID();
        verify(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        verify(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#getAddSenderSubscriberType(AddPromoTransferReqVO)}
     */
    @Test
    public void testGetAddSenderSubscriberType3() throws BTSLBaseException {
        AddPromoTransferReqVO searchPromoTransferReqVO = mock(AddPromoTransferReqVO.class);
        when(searchPromoTransferReqVO.getOptionTab()).thenReturn(null);
        doNothing().when(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        doNothing().when(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        doNothing().when(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
        searchPromoTransferReqVO.setCategoryCode("Category Code");
        searchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        searchPromoTransferReqVO.setDomainCode("Domain Code");
        searchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        searchPromoTransferReqVO.setGeography("Geography");
        searchPromoTransferReqVO.setGrade("Grade");
        searchPromoTransferReqVO.setList(new ArrayList<>());
        searchPromoTransferReqVO.setOptionTab("Option Tab");
        searchPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        searchPromoTransferReqVO.setUserID("User ID");
        assertNull(promotionalTransfServiceImpl.getAddSenderSubscriberType(searchPromoTransferReqVO));
        verify(searchPromoTransferReqVO).getOptionTab();
        verify(searchPromoTransferReqVO).setCategoryCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setCellGroupID(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setDomainCode(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeoGraphyDomainType(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGeography(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setGrade(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setList(Mockito.<ArrayList<ReceiverSectionInputs>>any());
        verify(searchPromoTransferReqVO).setOptionTab(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setPromotionalLevel(Mockito.<String>any());
        verify(searchPromoTransferReqVO).setUserID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#addPromoTransferData(AddPromoTransferReqVO, String, Locale)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testAddPromoTransferData() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.PromotionalTransfServiceImpl.addPromoTransferData(PromotionalTransfServiceImpl.java:509)

        // Arrange
        // TODO: Populate arranged inputs
        AddPromoTransferReqVO addPromoTransferReqVO = null;
        String userLoginId = "";
        Locale locale = null;

        // Act
        AddPromoTransferRuleRespVO actualAddPromoTransferDataResult = this.promotionalTransfServiceImpl
                .addPromoTransferData(addPromoTransferReqVO, userLoginId, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PromotionalTransfServiceImpl#modifyPromoTransferData(ModifyPromoTransferReqVO, String, Locale)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testModifyPromoTransferData() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.PromotionalTransfServiceImpl.modifyPromoTransferData(PromotionalTransfServiceImpl.java:1248)

        // Arrange
        // TODO: Populate arranged inputs
        ModifyPromoTransferReqVO modifyPromoTransferReqVO = null;
        String userLoginId = "";
        Locale locale = null;

        // Act
        ModifyPromoTransfRuleRespVO actualModifyPromoTransferDataResult = this.promotionalTransfServiceImpl
                .modifyPromoTransferData(modifyPromoTransferReqVO, userLoginId, locale);

        // Assert
        // TODO: Add assertions on result
    }
}

