package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class O2CVoucherTransferRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CVoucherTransferRequestVO}
     *   <li>{@link O2CVoucherTransferRequestVO#setO2CTrasfereReqData(O2CVoucherTransferReqData)}
     *   <li>{@link O2CVoucherTransferRequestVO#toString()}
     *   <li>{@link O2CVoucherTransferRequestVO#getO2CTrasfereReqData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CVoucherTransferRequestVO actualO2cVoucherTransferRequestVO = new O2CVoucherTransferRequestVO();
        O2CVoucherTransferReqData o2cTrasfereReqData = new O2CVoucherTransferReqData();
        actualO2cVoucherTransferRequestVO.setO2CTrasfereReqData(o2cTrasfereReqData);
        String actualToStringResult = actualO2cVoucherTransferRequestVO.toString();
        assertSame(o2cTrasfereReqData, actualO2cVoucherTransferRequestVO.getO2CTrasfereReqData());
        assertEquals(
                "O2CVoucherTransferRequestVO [o2CTrasfereReqData=O2CVoucherTransferReqData [msisdn2=null, loginid2=null,"
                        + " extcode2=null, voucherDetails=null, paymentDetails=null, pin=null, refnumber=null, remarks=null,"
                        + " language=null]]",
                actualToStringResult);
    }
}

