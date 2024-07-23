package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfoDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link InfoData}
     *   <li>{@link InfoData#setLoginId(String)}
     *   <li>{@link InfoData#setMsisdn(String)}
     *   <li>{@link InfoData#toString()}
     *   <li>{@link InfoData#getLoginId()}
     *   <li>{@link InfoData#getMsisdn()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        InfoData actualInfoData = new InfoData();
        actualInfoData.setLoginId("42");
        actualInfoData.setMsisdn("Msisdn");
        String actualToStringResult = actualInfoData.toString();
        assertEquals("42", actualInfoData.getLoginId());
        assertEquals("Msisdn", actualInfoData.getMsisdn());
        assertEquals("InfoData [loginId=42, msisdn=Msisdn]", actualToStringResult);
    }
}

