package com.restapi.cardgroup.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.channel.profile.businesslogic.CalculateVoucherTransferRuleVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;

import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.ArrayList;

import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CalculateVoucherCardGroupTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link CalculateVoucherCardGroup#calculateVoucherTransferRule(CalculateVoucherTransferRuleVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCalculateVoucherTransferRule() {
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
        //       at com.restapi.cardgroup.service.CalculateVoucherCardGroup.calculateVoucherTransferRule(CalculateVoucherCardGroup.java:81)

        // Arrange
        // TODO: Populate arranged inputs
        CalculateVoucherCardGroup calculateVoucherCardGroup = null;
        CalculateVoucherTransferRuleVO requestVO = null;

        // Act
        PretupsResponse<HashMap<String, Object>> actualCalculateVoucherTransferRuleResult = calculateVoucherCardGroup
                .calculateVoucherTransferRule(requestVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CalculateVoucherCardGroup#getServiceTypeId(ArrayList, String)}
     */
    @Test
    public void testGetServiceTypeId() {
        CalculateVoucherCardGroup calculateVoucherCardGroup = new CalculateVoucherCardGroup();
        assertNull(calculateVoucherCardGroup.getServiceTypeId(new ArrayList(), "Service Type Name"));
    }

    /**
     * Method under test: {@link CalculateVoucherCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses() throws BTSLBaseException {
        CalculateVoucherCardGroup calculateVoucherCardGroup = new CalculateVoucherCardGroup();
        assertNull(calculateVoucherCardGroup.arrangeBonuses(new ArrayList(), "P card Group Sub Service ID", true));
    }

    /**
     * Method under test: {@link CalculateVoucherCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses2() throws BTSLBaseException {
        CalculateVoucherCardGroup calculateVoucherCardGroup = new CalculateVoucherCardGroup();
        ArrayList p_bonusBundleList = new ArrayList();
        ArrayList actualArrangeBonusesResult = calculateVoucherCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
        assertSame(p_bonusBundleList, actualArrangeBonusesResult);
        assertTrue(actualArrangeBonusesResult.isEmpty());
    }

    /**
     * Method under test: {@link CalculateVoucherCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses3() throws BTSLBaseException {
        CalculateVoucherCardGroup calculateVoucherCardGroup = new CalculateVoucherCardGroup();

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add("42");
        thrown.expect(BTSLBaseException.class);
        calculateVoucherCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
    }

    /**
     * Method under test: {@link CalculateVoucherCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses4() throws BTSLBaseException {
        CalculateVoucherCardGroup calculateVoucherCardGroup = new CalculateVoucherCardGroup();

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add("42");
        p_bonusBundleList.add("42");
        thrown.expect(BTSLBaseException.class);
        calculateVoucherCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
    }

    /**
     * Method under test: {@link CalculateVoucherCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    public void testArrangeBonuses5() throws BTSLBaseException {
        CalculateVoucherCardGroup calculateVoucherCardGroup = new CalculateVoucherCardGroup();

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add("42");
        thrown.expect(BTSLBaseException.class);
        calculateVoucherCardGroup.arrangeBonuses(p_bonusBundleList, null, true);
    }

    /**
     * Method under test: {@link CalculateVoucherCardGroup#arrangeBonuses(ArrayList, String, boolean)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testArrangeBonuses6() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        CalculateVoucherCardGroup calculateVoucherCardGroup = null;

        ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
        serviceSelectorMappingVO.setAmountStr("10");
        serviceSelectorMappingVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        serviceSelectorMappingVO.setCreatedOn(mock(java.sql.Date.class));
        serviceSelectorMappingVO.setDefaultCode(true);
        serviceSelectorMappingVO.setDescription("The characteristics of someone or something");
        serviceSelectorMappingVO.setDisplayOrder("arrangeBonuses");
        serviceSelectorMappingVO.setDisplayOrderList(-1);
        serviceSelectorMappingVO.setIsDefaultCodeStr("arrangeBonuses");
        serviceSelectorMappingVO.setMappingStatus("arrangeBonuses");
        serviceSelectorMappingVO.setMappingType("arrangeBonuses");
        serviceSelectorMappingVO.setModifiedAllowed(true);
        serviceSelectorMappingVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        serviceSelectorMappingVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        serviceSelectorMappingVO.setNewOrder("arrangeBonuses");
        serviceSelectorMappingVO.setNewOrderList(new ArrayList());
        serviceSelectorMappingVO.setRadioIndex(1);
        serviceSelectorMappingVO.setReceiverBundleID("arrangeBonuses");
        serviceSelectorMappingVO.setReceiverSubscriberType("arrangeBonuses");
        serviceSelectorMappingVO.setSelectorCode("arrangeBonuses");
        serviceSelectorMappingVO.setSelectorCount(new ArrayList());
        serviceSelectorMappingVO.setSelectorName("arrangeBonuses");
        serviceSelectorMappingVO.setSenderBundleID("arrangeBonuses");
        serviceSelectorMappingVO.setSenderSubscriberType("arrangeBonuses");
        serviceSelectorMappingVO.setServiceName("arrangeBonuses");
        serviceSelectorMappingVO.setServiceType("arrangeBonuses");
        serviceSelectorMappingVO.setSno("arrangeBonuses");
        serviceSelectorMappingVO.setStatus("arrangeBonuses");
        serviceSelectorMappingVO.setStatusDesc("arrangeBonuses");
        serviceSelectorMappingVO.setType("arrangeBonuses");

        ArrayList p_bonusBundleList = new ArrayList();
        p_bonusBundleList.add(serviceSelectorMappingVO);
        calculateVoucherCardGroup.arrangeBonuses(p_bonusBundleList, ":", true);
    }
}

