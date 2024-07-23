package com.restapi.networkadmin.commissionprofile.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class ChangeStatusForCommissionProfileVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChangeStatusForCommissionProfileVO}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setCommProfileSetId(String)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setCommProfileSetName(String)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setDefaultProfile(String)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setLanguage1Message(String)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setLanguage2Message(String)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setLastModifiedOn(long)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setModifiedBy(String)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setModifiedOn(Date)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#setStatus(String)}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getCommProfileSetId()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getCommProfileSetName()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getDefaultProfile()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getLanguage1Message()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getLanguage2Message()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getLastModifiedOn()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getModifiedBy()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getModifiedOn()}
     *   <li>{@link ChangeStatusForCommissionProfileVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChangeStatusForCommissionProfileVO actualChangeStatusForCommissionProfileVO = new ChangeStatusForCommissionProfileVO();
        actualChangeStatusForCommissionProfileVO.setCommProfileSetId("42");
        actualChangeStatusForCommissionProfileVO.setCommProfileSetName("Comm Profile Set Name");
        actualChangeStatusForCommissionProfileVO.setDefaultProfile("Default Profile");
        actualChangeStatusForCommissionProfileVO.setLanguage1Message("en");
        actualChangeStatusForCommissionProfileVO.setLanguage2Message("en");
        actualChangeStatusForCommissionProfileVO.setLastModifiedOn(1L);
        actualChangeStatusForCommissionProfileVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualChangeStatusForCommissionProfileVO.setModifiedOn(modifiedOn);
        actualChangeStatusForCommissionProfileVO.setStatus("Status");
        assertEquals("42", actualChangeStatusForCommissionProfileVO.getCommProfileSetId());
        assertEquals("Comm Profile Set Name", actualChangeStatusForCommissionProfileVO.getCommProfileSetName());
        assertEquals("Default Profile", actualChangeStatusForCommissionProfileVO.getDefaultProfile());
        assertEquals("en", actualChangeStatusForCommissionProfileVO.getLanguage1Message());
        assertEquals("en", actualChangeStatusForCommissionProfileVO.getLanguage2Message());
        assertEquals(1L, actualChangeStatusForCommissionProfileVO.getLastModifiedOn());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualChangeStatusForCommissionProfileVO.getModifiedBy());
        assertSame(modifiedOn, actualChangeStatusForCommissionProfileVO.getModifiedOn());
        assertEquals("Status", actualChangeStatusForCommissionProfileVO.getStatus());
    }
}

