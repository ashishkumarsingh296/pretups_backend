package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class FetchTransferProfileVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FetchTransferProfileVO}
     *   <li>{@link FetchTransferProfileVO#setCategory(String)}
     *   <li>{@link FetchTransferProfileVO#setCategoryName(String)}
     *   <li>{@link FetchTransferProfileVO#setCreatedBy(String)}
     *   <li>{@link FetchTransferProfileVO#setCreatedOn(Date)}
     *   <li>{@link FetchTransferProfileVO#setDailyC2STransferOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyC2STransferOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailyOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setDailySubscriberOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setDescription(String)}
     *   <li>{@link FetchTransferProfileVO#setIsDefault(String)}
     *   <li>{@link FetchTransferProfileVO#setIsDefaultDesc(String)}
     *   <li>{@link FetchTransferProfileVO#setLastModifiedTime(long)}
     *   <li>{@link FetchTransferProfileVO#setModifiedBy(String)}
     *   <li>{@link FetchTransferProfileVO#setModifiedOn(Date)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyC2STransferOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyC2STransferOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlyOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setMonthlySubscriberOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setNetworkCode(String)}
     *   <li>{@link FetchTransferProfileVO#setParentProfileID(String)}
     *   <li>{@link FetchTransferProfileVO#setProfileId(String)}
     *   <li>{@link FetchTransferProfileVO#setProfileName(String)}
     *   <li>{@link FetchTransferProfileVO#setProfileProductList(ArrayList)}
     *   <li>{@link FetchTransferProfileVO#setProfileStatusName(String)}
     *   <li>{@link FetchTransferProfileVO#setShortName(String)}
     *   <li>{@link FetchTransferProfileVO#setStatus(String)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlDailyOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlMonthlyOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlTransferFlag(boolean)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setUnctrlWeeklyOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setUpdateRecord(boolean)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyC2STransferOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyC2STransferOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklyOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberInAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberInAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberInCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberInValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberOutAltCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberOutAltValue(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberOutCount(long)}
     *   <li>{@link FetchTransferProfileVO#setWeeklySubscriberOutValue(long)}
     *   <li>{@link FetchTransferProfileVO#getCategory()}
     *   <li>{@link FetchTransferProfileVO#getCategoryName()}
     *   <li>{@link FetchTransferProfileVO#getCreatedBy()}
     *   <li>{@link FetchTransferProfileVO#getCreatedOn()}
     *   <li>{@link FetchTransferProfileVO#getDailyC2STransferOutCount()}
     *   <li>{@link FetchTransferProfileVO#getDailyC2STransferOutValue()}
     *   <li>{@link FetchTransferProfileVO#getDailyInAltCount()}
     *   <li>{@link FetchTransferProfileVO#getDailyInAltValue()}
     *   <li>{@link FetchTransferProfileVO#getDailyInCount()}
     *   <li>{@link FetchTransferProfileVO#getDailyInValue()}
     *   <li>{@link FetchTransferProfileVO#getDailyOutAltCount()}
     *   <li>{@link FetchTransferProfileVO#getDailyOutAltValue()}
     *   <li>{@link FetchTransferProfileVO#getDailyOutCount()}
     *   <li>{@link FetchTransferProfileVO#getDailyOutValue()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberInAltCount()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberInAltValue()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberInCount()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberInValue()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberOutAltCount()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberOutAltValue()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberOutCount()}
     *   <li>{@link FetchTransferProfileVO#getDailySubscriberOutValue()}
     *   <li>{@link FetchTransferProfileVO#getDescription()}
     *   <li>{@link FetchTransferProfileVO#getIsDefault()}
     *   <li>{@link FetchTransferProfileVO#getIsDefaultDesc()}
     *   <li>{@link FetchTransferProfileVO#getLastModifiedTime()}
     *   <li>{@link FetchTransferProfileVO#getModifiedBy()}
     *   <li>{@link FetchTransferProfileVO#getModifiedOn()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyC2STransferOutCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyC2STransferOutValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyInAltCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyInAltValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyInCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyInValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyOutAltCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyOutAltValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyOutCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlyOutValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberInAltCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberInAltValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberInCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberInValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberOutAltCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberOutAltValue()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberOutCount()}
     *   <li>{@link FetchTransferProfileVO#getMonthlySubscriberOutValue()}
     *   <li>{@link FetchTransferProfileVO#getNetworkCode()}
     *   <li>{@link FetchTransferProfileVO#getParentProfileID()}
     *   <li>{@link FetchTransferProfileVO#getProfileId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FetchTransferProfileVO actualFetchTransferProfileVO = new FetchTransferProfileVO();
        actualFetchTransferProfileVO.setCategory("Category");
        actualFetchTransferProfileVO.setCategoryName("Category Name");
        actualFetchTransferProfileVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualFetchTransferProfileVO.setCreatedOn(createdOn);
        actualFetchTransferProfileVO.setDailyC2STransferOutCount(3L);
        actualFetchTransferProfileVO.setDailyC2STransferOutValue(42L);
        actualFetchTransferProfileVO.setDailyInAltCount(3L);
        actualFetchTransferProfileVO.setDailyInAltValue(42L);
        actualFetchTransferProfileVO.setDailyInCount(3L);
        actualFetchTransferProfileVO.setDailyInValue(42L);
        actualFetchTransferProfileVO.setDailyOutAltCount(3L);
        actualFetchTransferProfileVO.setDailyOutAltValue(42L);
        actualFetchTransferProfileVO.setDailyOutCount(3L);
        actualFetchTransferProfileVO.setDailyOutValue(42L);
        actualFetchTransferProfileVO.setDailySubscriberInAltCount(3L);
        actualFetchTransferProfileVO.setDailySubscriberInAltValue(42L);
        actualFetchTransferProfileVO.setDailySubscriberInCount(3L);
        actualFetchTransferProfileVO.setDailySubscriberInValue(42L);
        actualFetchTransferProfileVO.setDailySubscriberOutAltCount(3L);
        actualFetchTransferProfileVO.setDailySubscriberOutAltValue(42L);
        actualFetchTransferProfileVO.setDailySubscriberOutCount(3L);
        actualFetchTransferProfileVO.setDailySubscriberOutValue(42L);
        actualFetchTransferProfileVO.setDescription("The characteristics of someone or something");
        actualFetchTransferProfileVO.setIsDefault("Is Default");
        actualFetchTransferProfileVO.setIsDefaultDesc("Is Default Desc");
        actualFetchTransferProfileVO.setLastModifiedTime(1L);
        actualFetchTransferProfileVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualFetchTransferProfileVO.setModifiedOn(modifiedOn);
        actualFetchTransferProfileVO.setMonthlyC2STransferOutCount(3L);
        actualFetchTransferProfileVO.setMonthlyC2STransferOutValue(42L);
        actualFetchTransferProfileVO.setMonthlyInAltCount(3L);
        actualFetchTransferProfileVO.setMonthlyInAltValue(42L);
        actualFetchTransferProfileVO.setMonthlyInCount(3L);
        actualFetchTransferProfileVO.setMonthlyInValue(42L);
        actualFetchTransferProfileVO.setMonthlyOutAltCount(3L);
        actualFetchTransferProfileVO.setMonthlyOutAltValue(42L);
        actualFetchTransferProfileVO.setMonthlyOutCount(3L);
        actualFetchTransferProfileVO.setMonthlyOutValue(42L);
        actualFetchTransferProfileVO.setMonthlySubscriberInAltCount(3L);
        actualFetchTransferProfileVO.setMonthlySubscriberInAltValue(42L);
        actualFetchTransferProfileVO.setMonthlySubscriberInCount(3L);
        actualFetchTransferProfileVO.setMonthlySubscriberInValue(42L);
        actualFetchTransferProfileVO.setMonthlySubscriberOutAltCount(3L);
        actualFetchTransferProfileVO.setMonthlySubscriberOutAltValue(42L);
        actualFetchTransferProfileVO.setMonthlySubscriberOutCount(3L);
        actualFetchTransferProfileVO.setMonthlySubscriberOutValue(42L);
        actualFetchTransferProfileVO.setNetworkCode("Network Code");
        actualFetchTransferProfileVO.setParentProfileID("Parent Profile ID");
        actualFetchTransferProfileVO.setProfileId("42");
        actualFetchTransferProfileVO.setProfileName("foo.txt");
        actualFetchTransferProfileVO.setProfileProductList(new ArrayList());
        actualFetchTransferProfileVO.setProfileStatusName("Profile Status Name");
        actualFetchTransferProfileVO.setShortName("Short Name");
        actualFetchTransferProfileVO.setStatus("Status");
        actualFetchTransferProfileVO.setUnctrlDailyInAltCount(3L);
        actualFetchTransferProfileVO.setUnctrlDailyInAltValue(42L);
        actualFetchTransferProfileVO.setUnctrlDailyInCount(3L);
        actualFetchTransferProfileVO.setUnctrlDailyInValue(42L);
        actualFetchTransferProfileVO.setUnctrlDailyOutAltCount(3L);
        actualFetchTransferProfileVO.setUnctrlDailyOutAltValue(42L);
        actualFetchTransferProfileVO.setUnctrlDailyOutCount(3L);
        actualFetchTransferProfileVO.setUnctrlDailyOutValue(42L);
        actualFetchTransferProfileVO.setUnctrlMonthlyInAltCount(3L);
        actualFetchTransferProfileVO.setUnctrlMonthlyInAltValue(42L);
        actualFetchTransferProfileVO.setUnctrlMonthlyInCount(3L);
        actualFetchTransferProfileVO.setUnctrlMonthlyInValue(42L);
        actualFetchTransferProfileVO.setUnctrlMonthlyOutAltCount(3L);
        actualFetchTransferProfileVO.setUnctrlMonthlyOutAltValue(42L);
        actualFetchTransferProfileVO.setUnctrlMonthlyOutCount(3L);
        actualFetchTransferProfileVO.setUnctrlMonthlyOutValue(42L);
        actualFetchTransferProfileVO.setUnctrlTransferFlag(true);
        actualFetchTransferProfileVO.setUnctrlWeeklyInAltCount(3L);
        actualFetchTransferProfileVO.setUnctrlWeeklyInAltValue(42L);
        actualFetchTransferProfileVO.setUnctrlWeeklyInCount(3L);
        actualFetchTransferProfileVO.setUnctrlWeeklyInValue(42L);
        actualFetchTransferProfileVO.setUnctrlWeeklyOutAltCount(3L);
        actualFetchTransferProfileVO.setUnctrlWeeklyOutAltValue(42L);
        actualFetchTransferProfileVO.setUnctrlWeeklyOutCount(3L);
        actualFetchTransferProfileVO.setUnctrlWeeklyOutValue(42L);
        actualFetchTransferProfileVO.setUpdateRecord(true);
        actualFetchTransferProfileVO.setWeeklyC2STransferOutCount(3L);
        actualFetchTransferProfileVO.setWeeklyC2STransferOutValue(42L);
        actualFetchTransferProfileVO.setWeeklyInAltCount(3L);
        actualFetchTransferProfileVO.setWeeklyInAltValue(42L);
        actualFetchTransferProfileVO.setWeeklyInCount(3L);
        actualFetchTransferProfileVO.setWeeklyInValue(42L);
        actualFetchTransferProfileVO.setWeeklyOutAltCount(3L);
        actualFetchTransferProfileVO.setWeeklyOutAltValue(42L);
        actualFetchTransferProfileVO.setWeeklyOutCount(3L);
        actualFetchTransferProfileVO.setWeeklyOutValue(42L);
        actualFetchTransferProfileVO.setWeeklySubscriberInAltCount(3L);
        actualFetchTransferProfileVO.setWeeklySubscriberInAltValue(42L);
        actualFetchTransferProfileVO.setWeeklySubscriberInCount(3L);
        actualFetchTransferProfileVO.setWeeklySubscriberInValue(42L);
        actualFetchTransferProfileVO.setWeeklySubscriberOutAltCount(3L);
        actualFetchTransferProfileVO.setWeeklySubscriberOutAltValue(42L);
        actualFetchTransferProfileVO.setWeeklySubscriberOutCount(3L);
        actualFetchTransferProfileVO.setWeeklySubscriberOutValue(42L);
        assertEquals("Category", actualFetchTransferProfileVO.getCategory());
        assertEquals("Category Name", actualFetchTransferProfileVO.getCategoryName());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualFetchTransferProfileVO.getCreatedBy());
        assertSame(createdOn, actualFetchTransferProfileVO.getCreatedOn());
        assertEquals(3L, actualFetchTransferProfileVO.getDailyC2STransferOutCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailyC2STransferOutValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailyInAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailyInAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailyInCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailyInValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailyOutAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailyOutAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailyOutCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailyOutValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailySubscriberInAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailySubscriberInAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailySubscriberInCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailySubscriberInValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailySubscriberOutAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailySubscriberOutAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getDailySubscriberOutCount());
        assertEquals(42L, actualFetchTransferProfileVO.getDailySubscriberOutValue());
        assertEquals("The characteristics of someone or something", actualFetchTransferProfileVO.getDescription());
        assertEquals("Is Default", actualFetchTransferProfileVO.getIsDefault());
        assertEquals("Is Default Desc", actualFetchTransferProfileVO.getIsDefaultDesc());
        assertEquals(1L, actualFetchTransferProfileVO.getLastModifiedTime());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualFetchTransferProfileVO.getModifiedBy());
        assertSame(modifiedOn, actualFetchTransferProfileVO.getModifiedOn());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlyC2STransferOutCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlyC2STransferOutValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlyInAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlyInAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlyInCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlyInValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlyOutAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlyOutAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlyOutCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlyOutValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlySubscriberInAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlySubscriberInAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlySubscriberInCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlySubscriberInValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlySubscriberOutAltCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlySubscriberOutAltValue());
        assertEquals(3L, actualFetchTransferProfileVO.getMonthlySubscriberOutCount());
        assertEquals(42L, actualFetchTransferProfileVO.getMonthlySubscriberOutValue());
        assertEquals("Network Code", actualFetchTransferProfileVO.getNetworkCode());
        assertEquals("Parent Profile ID", actualFetchTransferProfileVO.getParentProfileID());
        assertEquals("42", actualFetchTransferProfileVO.getProfileId());
    }
}

