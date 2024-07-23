package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class FocInitiateRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FocInitiateRequestVO}
     *   <li>{@link FocInitiateRequestVO#setDatafoc(ArrayList)}
     *   <li>{@link FocInitiateRequestVO#toString()}
     *   <li>{@link FocInitiateRequestVO#getDatafoc()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FocInitiateRequestVO actualFocInitiateRequestVO = new FocInitiateRequestVO();
        ArrayList<FocTransferInitaiateReqData> datafoc = new ArrayList<>();
        actualFocInitiateRequestVO.setDatafoc(datafoc);
        String actualToStringResult = actualFocInitiateRequestVO.toString();
        assertSame(datafoc, actualFocInitiateRequestVO.getDatafoc());
        assertEquals("FocInitiateRequestVO [datafoc=[]]", actualToStringResult);
    }
}

