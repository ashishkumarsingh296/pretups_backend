package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class FocTransferInitaiateReqDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FocTransferInitaiateReqData}
     *   <li>{@link FocTransferInitaiateReqData#setFocProducts(ArrayList)}
     *   <li>{@link FocTransferInitaiateReqData#setLanguage1(String)}
     *   <li>{@link FocTransferInitaiateReqData#setLanguage2(String)}
     *   <li>{@link FocTransferInitaiateReqData#setMsisdn2(String)}
     *   <li>{@link FocTransferInitaiateReqData#setPin(String)}
     *   <li>{@link FocTransferInitaiateReqData#setRefnumber(String)}
     *   <li>{@link FocTransferInitaiateReqData#setRemarks(String)}
     *   <li>{@link FocTransferInitaiateReqData#toString()}
     *   <li>{@link FocTransferInitaiateReqData#getFocProducts()}
     *   <li>{@link FocTransferInitaiateReqData#getLanguage1()}
     *   <li>{@link FocTransferInitaiateReqData#getLanguage2()}
     *   <li>{@link FocTransferInitaiateReqData#getMsisdn2()}
     *   <li>{@link FocTransferInitaiateReqData#getPin()}
     *   <li>{@link FocTransferInitaiateReqData#getRefnumber()}
     *   <li>{@link FocTransferInitaiateReqData#getRemarks()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FocTransferInitaiateReqData actualFocTransferInitaiateReqData = new FocTransferInitaiateReqData();
        ArrayList<FOCProduct> focProducts = new ArrayList<>();
        actualFocTransferInitaiateReqData.setFocProducts(focProducts);
        actualFocTransferInitaiateReqData.setLanguage1("en");
        actualFocTransferInitaiateReqData.setLanguage2("en");
        actualFocTransferInitaiateReqData.setMsisdn2("Msisdn2");
        actualFocTransferInitaiateReqData.setPin("Pin");
        actualFocTransferInitaiateReqData.setRefnumber("42");
        actualFocTransferInitaiateReqData.setRemarks("Remarks");
        String actualToStringResult = actualFocTransferInitaiateReqData.toString();
        assertSame(focProducts, actualFocTransferInitaiateReqData.getFocProducts());
        assertEquals("en", actualFocTransferInitaiateReqData.getLanguage1());
        assertEquals("en", actualFocTransferInitaiateReqData.getLanguage2());
        assertEquals("Msisdn2", actualFocTransferInitaiateReqData.getMsisdn2());
        assertEquals("Pin", actualFocTransferInitaiateReqData.getPin());
        assertEquals("42", actualFocTransferInitaiateReqData.getRefnumber());
        assertEquals("Remarks", actualFocTransferInitaiateReqData.getRemarks());
        assertEquals("FocTransferInitaiateReqData [refnumber=42, remarks=Remarks, language1=en, language2=en, pin=Pin,"
                + " msisdn2=Msisdn2, focProducts=[]]", actualToStringResult);
    }
}

