package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CApprovalListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CApprovalListRequestVO}
     *   <li>{@link O2CApprovalListRequestVO#setApprovalLevel(String)}
     *   <li>{@link O2CApprovalListRequestVO#setCategory(String)}
     *   <li>{@link O2CApprovalListRequestVO#setDomain(String)}
     *   <li>{@link O2CApprovalListRequestVO#setGeographicalDomain(String)}
     *   <li>{@link O2CApprovalListRequestVO#setMsisdn(String)}
     *   <li>{@link O2CApprovalListRequestVO#toString()}
     *   <li>{@link O2CApprovalListRequestVO#getApprovalLevel()}
     *   <li>{@link O2CApprovalListRequestVO#getCategory()}
     *   <li>{@link O2CApprovalListRequestVO#getDomain()}
     *   <li>{@link O2CApprovalListRequestVO#getGeographicalDomain()}
     *   <li>{@link O2CApprovalListRequestVO#getMsisdn()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CApprovalListRequestVO actualO2cApprovalListRequestVO = new O2CApprovalListRequestVO();
        actualO2cApprovalListRequestVO.setApprovalLevel("Approval Level");
        actualO2cApprovalListRequestVO.setCategory("Category");
        actualO2cApprovalListRequestVO.setDomain("Domain");
        actualO2cApprovalListRequestVO.setGeographicalDomain("Geographical Domain");
        actualO2cApprovalListRequestVO.setMsisdn("Msisdn");
        actualO2cApprovalListRequestVO.toString();
        assertEquals("Approval Level", actualO2cApprovalListRequestVO.getApprovalLevel());
        assertEquals("Category", actualO2cApprovalListRequestVO.getCategory());
        assertEquals("Domain", actualO2cApprovalListRequestVO.getDomain());
        assertEquals("Geographical Domain", actualO2cApprovalListRequestVO.getGeographicalDomain());
        assertEquals("Msisdn", actualO2cApprovalListRequestVO.getMsisdn());
    }
}

