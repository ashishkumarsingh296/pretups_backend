package com.restapi.networkadmin.cardgroup.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class CardGroupSetVersionVODetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CardGroupSetVersionVODetails}
     *   <li>{@link CardGroupSetVersionVODetails#setApplicableFrom(Date)}
     *   <li>{@link CardGroupSetVersionVODetails#setApplicableFromAsString(String)}
     *   <li>{@link CardGroupSetVersionVODetails#setCardGroupSetCombinedID(String)}
     *   <li>{@link CardGroupSetVersionVODetails#setCardGroupSetID(String)}
     *   <li>{@link CardGroupSetVersionVODetails#setCreadtedOn(long)}
     *   <li>{@link CardGroupSetVersionVODetails#setCreatedBy(String)}
     *   <li>{@link CardGroupSetVersionVODetails#setCreatedOnAsString(String)}
     *   <li>{@link CardGroupSetVersionVODetails#setModifiedBy(String)}
     *   <li>{@link CardGroupSetVersionVODetails#setModifiedOn(long)}
     *   <li>{@link CardGroupSetVersionVODetails#setModifiedOnAsString(String)}
     *   <li>{@link CardGroupSetVersionVODetails#setOldApplicableFrom(long)}
     *   <li>{@link CardGroupSetVersionVODetails#setVersion(String)}
     *   <li>{@link CardGroupSetVersionVODetails#getApplicableFrom()}
     *   <li>{@link CardGroupSetVersionVODetails#getApplicableFromAsString()}
     *   <li>{@link CardGroupSetVersionVODetails#getCardGroupSetCombinedID()}
     *   <li>{@link CardGroupSetVersionVODetails#getCardGroupSetID()}
     *   <li>{@link CardGroupSetVersionVODetails#getCreadtedOn()}
     *   <li>{@link CardGroupSetVersionVODetails#getCreatedBy()}
     *   <li>{@link CardGroupSetVersionVODetails#getCreatedOnAsString()}
     *   <li>{@link CardGroupSetVersionVODetails#getModifiedBy()}
     *   <li>{@link CardGroupSetVersionVODetails#getModifiedOn()}
     *   <li>{@link CardGroupSetVersionVODetails#getModifiedOnAsString()}
     *   <li>{@link CardGroupSetVersionVODetails#getOldApplicableFrom()}
     *   <li>{@link CardGroupSetVersionVODetails#getVersion()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CardGroupSetVersionVODetails actualCardGroupSetVersionVODetails = new CardGroupSetVersionVODetails();
        Date applicableFrom = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualCardGroupSetVersionVODetails.setApplicableFrom(applicableFrom);
        actualCardGroupSetVersionVODetails.setApplicableFromAsString("jane.doe@example.org");
        actualCardGroupSetVersionVODetails.setCardGroupSetCombinedID("Card Group Set Combined ID");
        actualCardGroupSetVersionVODetails.setCardGroupSetID("Card Group Set ID");
        actualCardGroupSetVersionVODetails.setCreadtedOn(1L);
        actualCardGroupSetVersionVODetails.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        actualCardGroupSetVersionVODetails.setCreatedOnAsString("Jan 1, 2020 8:00am GMT+0100");
        actualCardGroupSetVersionVODetails.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        actualCardGroupSetVersionVODetails.setModifiedOn(1L);
        actualCardGroupSetVersionVODetails.setModifiedOnAsString("Jan 1, 2020 9:00am GMT+0100");
        actualCardGroupSetVersionVODetails.setOldApplicableFrom(1L);
        actualCardGroupSetVersionVODetails.setVersion("1.0.2");
        assertSame(applicableFrom, actualCardGroupSetVersionVODetails.getApplicableFrom());
        assertEquals("jane.doe@example.org", actualCardGroupSetVersionVODetails.getApplicableFromAsString());
        assertEquals("Card Group Set Combined ID", actualCardGroupSetVersionVODetails.getCardGroupSetCombinedID());
        assertEquals("Card Group Set ID", actualCardGroupSetVersionVODetails.getCardGroupSetID());
        assertEquals(1L, actualCardGroupSetVersionVODetails.getCreadtedOn());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualCardGroupSetVersionVODetails.getCreatedBy());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualCardGroupSetVersionVODetails.getCreatedOnAsString());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualCardGroupSetVersionVODetails.getModifiedBy());
        assertEquals(1L, actualCardGroupSetVersionVODetails.getModifiedOn());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualCardGroupSetVersionVODetails.getModifiedOnAsString());
        assertEquals(1L, actualCardGroupSetVersionVODetails.getOldApplicableFrom());
        assertEquals("1.0.2", actualCardGroupSetVersionVODetails.getVersion());
    }
}

