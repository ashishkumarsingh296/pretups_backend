package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class FetchTransferProfileRespVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FetchTransferProfileRespVO}
     *   <li>{@link FetchTransferProfileRespVO#setTransferProfileVO(TransferProfileVO)}
     *   <li>{@link FetchTransferProfileRespVO#getTransferProfileVO()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FetchTransferProfileRespVO actualFetchTransferProfileRespVO = new FetchTransferProfileRespVO();
        TransferProfileVO transferProfileVO = new TransferProfileVO();
        transferProfileVO.setCategory("Category");
        transferProfileVO.setCategoryName("Category Name");
        transferProfileVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        transferProfileVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        transferProfileVO.setDailyC2STransferOutCount(3L);
        transferProfileVO.setDailyC2STransferOutValue(42L);
        transferProfileVO.setDailyInAltCount(3L);
        transferProfileVO.setDailyInAltValue(42L);
        transferProfileVO.setDailyInCount(3L);
        transferProfileVO.setDailyInValue(42L);
        transferProfileVO.setDailyOutAltCount(3L);
        transferProfileVO.setDailyOutAltValue(42L);
        transferProfileVO.setDailyOutCount(3L);
        transferProfileVO.setDailyOutValue(42L);
        transferProfileVO.setDailyRoamAmount(1L);
        transferProfileVO.setDailySubscriberInAltCount(3L);
        transferProfileVO.setDailySubscriberInAltValue(42L);
        transferProfileVO.setDailySubscriberInCount(3L);
        transferProfileVO.setDailySubscriberInValue(42L);
        transferProfileVO.setDailySubscriberOutAltCount(3L);
        transferProfileVO.setDailySubscriberOutAltValue(42L);
        transferProfileVO.setDailySubscriberOutCount(3L);
        transferProfileVO.setDailySubscriberOutValue(42L);
        transferProfileVO.setDescription("The characteristics of someone or something");
        transferProfileVO.setIsDefault("Is Default");
        transferProfileVO.setIsDefaultDesc("Is Default Desc");
        transferProfileVO.setLastModifiedTime(1L);
        transferProfileVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        transferProfileVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        transferProfileVO.setMonthlyC2STransferOutCount(3L);
        transferProfileVO.setMonthlyC2STransferOutValue(42L);
        transferProfileVO.setMonthlyInAltCount(3L);
        transferProfileVO.setMonthlyInAltValue(42L);
        transferProfileVO.setMonthlyInCount(3L);
        transferProfileVO.setMonthlyInValue(42L);
        transferProfileVO.setMonthlyOutAltCount(3L);
        transferProfileVO.setMonthlyOutAltValue(42L);
        transferProfileVO.setMonthlyOutCount(3L);
        transferProfileVO.setMonthlyOutValue(42L);
        transferProfileVO.setMonthlySubscriberInAltCount(3L);
        transferProfileVO.setMonthlySubscriberInAltValue(42L);
        transferProfileVO.setMonthlySubscriberInCount(3L);
        transferProfileVO.setMonthlySubscriberInValue(42L);
        transferProfileVO.setMonthlySubscriberOutAltCount(3L);
        transferProfileVO.setMonthlySubscriberOutAltValue(42L);
        transferProfileVO.setMonthlySubscriberOutCount(3L);
        transferProfileVO.setMonthlySubscriberOutValue(42L);
        transferProfileVO.setNetworkCode("Network Code");
        transferProfileVO.setParentProfileID("Parent Profile ID");
        transferProfileVO.setProfileId("42");
        transferProfileVO.setProfileName("foo.txt");
        transferProfileVO.setProfileProductList(new ArrayList());
        transferProfileVO.setProfileStatusName("Profile Status Name");
        transferProfileVO.setShortName("Short Name");
        transferProfileVO.setStatus("Status");
        transferProfileVO.setUnctrlDailyInAltCount(3L);
        transferProfileVO.setUnctrlDailyInAltValue(42L);
        transferProfileVO.setUnctrlDailyInCount(3L);
        transferProfileVO.setUnctrlDailyInValue(42L);
        transferProfileVO.setUnctrlDailyOutAltCount(3L);
        transferProfileVO.setUnctrlDailyOutAltValue(42L);
        transferProfileVO.setUnctrlDailyOutCount(3L);
        transferProfileVO.setUnctrlDailyOutValue(42L);
        transferProfileVO.setUnctrlMonthlyInAltCount(3L);
        transferProfileVO.setUnctrlMonthlyInAltValue(42L);
        transferProfileVO.setUnctrlMonthlyInCount(3L);
        transferProfileVO.setUnctrlMonthlyInValue(42L);
        transferProfileVO.setUnctrlMonthlyOutAltCount(3L);
        transferProfileVO.setUnctrlMonthlyOutAltValue(42L);
        transferProfileVO.setUnctrlMonthlyOutCount(3L);
        transferProfileVO.setUnctrlMonthlyOutValue(42L);
        transferProfileVO.setUnctrlTransferFlag(true);
        transferProfileVO.setUnctrlWeeklyInAltCount(3L);
        transferProfileVO.setUnctrlWeeklyInAltValue(42L);
        transferProfileVO.setUnctrlWeeklyInCount(3L);
        transferProfileVO.setUnctrlWeeklyInValue(42L);
        transferProfileVO.setUnctrlWeeklyOutAltCount(3L);
        transferProfileVO.setUnctrlWeeklyOutAltValue(42L);
        transferProfileVO.setUnctrlWeeklyOutCount(3L);
        transferProfileVO.setUnctrlWeeklyOutValue(42L);
        transferProfileVO.setUpdateRecord(true);
        transferProfileVO.setWeeklyC2STransferOutCount(3L);
        transferProfileVO.setWeeklyC2STransferOutValue(42L);
        transferProfileVO.setWeeklyInAltCount(3L);
        transferProfileVO.setWeeklyInAltValue(42L);
        transferProfileVO.setWeeklyInCount(3L);
        transferProfileVO.setWeeklyInValue(42L);
        transferProfileVO.setWeeklyOutAltCount(3L);
        transferProfileVO.setWeeklyOutAltValue(42L);
        transferProfileVO.setWeeklyOutCount(3L);
        transferProfileVO.setWeeklyOutValue(42L);
        transferProfileVO.setWeeklySubscriberInAltCount(3L);
        transferProfileVO.setWeeklySubscriberInAltValue(42L);
        transferProfileVO.setWeeklySubscriberInCount(3L);
        transferProfileVO.setWeeklySubscriberInValue(42L);
        transferProfileVO.setWeeklySubscriberOutAltCount(3L);
        transferProfileVO.setWeeklySubscriberOutAltValue(42L);
        transferProfileVO.setWeeklySubscriberOutCount(3L);
        transferProfileVO.setWeeklySubscriberOutValue(42L);
        actualFetchTransferProfileRespVO.setTransferProfileVO(transferProfileVO);
        assertSame(transferProfileVO, actualFetchTransferProfileRespVO.getTransferProfileVO());
    }
}

