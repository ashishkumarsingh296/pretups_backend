package com.restapi.o2c.service;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CWithdrawlRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CWithdrawlRequestVO}
     *   <li>{@link O2CWithdrawlRequestVO#setO2CInitiateReqData(List)}
     *   <li>{@link O2CWithdrawlRequestVO#getO2CInitiateReqData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CWithdrawlRequestVO actualO2cWithdrawlRequestVO = new O2CWithdrawlRequestVO();
        ArrayList<O2CWithdrawData> o2cInitiateReqData = new ArrayList<>();
        actualO2cWithdrawlRequestVO.setO2CInitiateReqData(o2cInitiateReqData);
        assertSame(o2cInitiateReqData, actualO2cWithdrawlRequestVO.getO2CInitiateReqData());
    }
}

