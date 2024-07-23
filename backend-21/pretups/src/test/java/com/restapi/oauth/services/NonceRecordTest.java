package com.restapi.oauth.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class NonceRecordTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link NonceRecord}
     *   <li>{@link NonceRecord#setCreatedOn(Date)}
     *   <li>{@link NonceRecord#setNonceId(String)}
     *   <li>{@link NonceRecord#getCreatedOn()}
     *   <li>{@link NonceRecord#getNonceId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        NonceRecord actualNonceRecord = new NonceRecord();
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualNonceRecord.setCreatedOn(createdOn);
        actualNonceRecord.setNonceId("42");
        assertSame(createdOn, actualNonceRecord.getCreatedOn());
        assertEquals("42", actualNonceRecord.getNonceId());
    }
}

