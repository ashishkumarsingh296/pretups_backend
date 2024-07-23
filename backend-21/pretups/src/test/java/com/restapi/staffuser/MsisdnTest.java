package com.restapi.staffuser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MsisdnTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link Msisdn}
     *   <li>{@link Msisdn#setConfirmpin(String)}
     *   <li>{@link Msisdn#setDescription(String)}
     *   <li>{@link Msisdn#setIsprimary(String)}
     *   <li>{@link Msisdn#setPhoneNo(String)}
     *   <li>{@link Msisdn#setPin(String)}
     *   <li>{@link Msisdn#getConfirmpin()}
     *   <li>{@link Msisdn#getDescription()}
     *   <li>{@link Msisdn#getIsprimary()}
     *   <li>{@link Msisdn#getPhoneNo()}
     *   <li>{@link Msisdn#getPin()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        Msisdn actualMsisdn = new Msisdn();
        actualMsisdn.setConfirmpin("Confirmpin");
        actualMsisdn.setDescription("The characteristics of someone or something");
        actualMsisdn.setIsprimary("Isprimary");
        actualMsisdn.setPhoneNo("6625550144");
        actualMsisdn.setPin("Pin");
        assertEquals("Confirmpin", actualMsisdn.getConfirmpin());
        assertEquals("The characteristics of someone or something", actualMsisdn.getDescription());
        assertEquals("Isprimary", actualMsisdn.getIsprimary());
        assertEquals("6625550144", actualMsisdn.getPhoneNo());
        assertEquals("Pin", actualMsisdn.getPin());
    }
}

