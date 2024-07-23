package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class O2CVoucherInitiateRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CVoucherInitiateRequestVO}
     *   <li>{@link O2CVoucherInitiateRequestVO#setO2CInitiateReqData(O2CVoucherInitiateReqData)}
     *   <li>{@link O2CVoucherInitiateRequestVO#toString()}
     *   <li>{@link O2CVoucherInitiateRequestVO#getO2CInitiateReqData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CVoucherInitiateRequestVO actualO2cVoucherInitiateRequestVO = new O2CVoucherInitiateRequestVO();
        O2CVoucherInitiateReqData data = new O2CVoucherInitiateReqData();
        actualO2cVoucherInitiateRequestVO.setO2CInitiateReqData(data);
        String actualToStringResult = actualO2cVoucherInitiateRequestVO.toString();
        assertSame(data, actualO2cVoucherInitiateRequestVO.getO2CInitiateReqData());
        assertEquals("O2CVoucherInitiateReqData = o2CInitiateReqData = voucherDetails = nullpaymentdetails = nullrefnumber"
                + " = nulllanguage = nullremarks = nulllanguage = null", actualToStringResult);
    }
}

