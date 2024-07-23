package com.restapi.o2c.service;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CVoucherApprovalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CVoucherApprovalRequestVO}
     *   <li>{@link O2CVoucherApprovalRequestVO#setO2CInitiateReqData(List)}
     *   <li>{@link O2CVoucherApprovalRequestVO#getO2CInitiateReqData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CVoucherApprovalRequestVO actualO2cVoucherApprovalRequestVO = new O2CVoucherApprovalRequestVO();
        ArrayList<O2CVoucherApprvData> o2cInitiateReqData = new ArrayList<>();
        actualO2cVoucherApprovalRequestVO.setO2CInitiateReqData(o2cInitiateReqData);
        assertSame(o2cInitiateReqData, actualO2cVoucherApprovalRequestVO.getO2CInitiateReqData());
    }
}

