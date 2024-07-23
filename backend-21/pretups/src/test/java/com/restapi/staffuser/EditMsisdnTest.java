package com.restapi.staffuser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EditMsisdnTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link EditMsisdn}
     *   <li>{@link EditMsisdn#setDescription(String)}
     *   <li>{@link EditMsisdn#setIsprimary(String)}
     *   <li>{@link EditMsisdn#setOldPhoneNo(String)}
     *   <li>{@link EditMsisdn#setOldPin(String)}
     *   <li>{@link EditMsisdn#setOpType(String)}
     *   <li>{@link EditMsisdn#setPhoneNo(String)}
     *   <li>{@link EditMsisdn#setPin(String)}
     *   <li>{@link EditMsisdn#getDescription()}
     *   <li>{@link EditMsisdn#getIsprimary()}
     *   <li>{@link EditMsisdn#getOldPhoneNo()}
     *   <li>{@link EditMsisdn#getOldPin()}
     *   <li>{@link EditMsisdn#getOpType()}
     *   <li>{@link EditMsisdn#getPhoneNo()}
     *   <li>{@link EditMsisdn#getPin()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        EditMsisdn actualEditMsisdn = new EditMsisdn();
        actualEditMsisdn.setDescription("The characteristics of someone or something");
        actualEditMsisdn.setIsprimary("Isprimary");
        actualEditMsisdn.setOldPhoneNo("6625550144");
        actualEditMsisdn.setOldPin("Old Pin");
        actualEditMsisdn.setOpType("Op Type");
        actualEditMsisdn.setPhoneNo("6625550144");
        actualEditMsisdn.setPin("Pin");
        assertEquals("The characteristics of someone or something", actualEditMsisdn.getDescription());
        assertEquals("Isprimary", actualEditMsisdn.getIsprimary());
        assertEquals("6625550144", actualEditMsisdn.getOldPhoneNo());
        assertEquals("Old Pin", actualEditMsisdn.getOldPin());
        assertEquals("Op Type", actualEditMsisdn.getOpType());
        assertEquals("6625550144", actualEditMsisdn.getPhoneNo());
        assertEquals("Pin", actualEditMsisdn.getPin());
    }
}

