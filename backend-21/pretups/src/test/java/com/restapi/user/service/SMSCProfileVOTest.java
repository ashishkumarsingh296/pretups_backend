package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SMSCProfileVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SMSCProfileVO}
     *   <li>{@link SMSCProfileVO#setSmscProfileCode(String)}
     *   <li>{@link SMSCProfileVO#setSmscProfileName(String)}
     *   <li>{@link SMSCProfileVO#toString()}
     *   <li>{@link SMSCProfileVO#getSmscProfileCode()}
     *   <li>{@link SMSCProfileVO#getSmscProfileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SMSCProfileVO actualSmscProfileVO = new SMSCProfileVO();
        actualSmscProfileVO.setSmscProfileCode("Smsc Profile Code");
        actualSmscProfileVO.setSmscProfileName("foo.txt");
        actualSmscProfileVO.toString();
        assertEquals("Smsc Profile Code", actualSmscProfileVO.getSmscProfileCode());
        assertEquals("foo.txt", actualSmscProfileVO.getSmscProfileName());
    }
}

