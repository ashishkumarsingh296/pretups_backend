package com.restapi.o2c.service.bulko2capprovalrequestvo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkO2CApprovalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkO2CApprovalRequestVO}
     *   <li>{@link BulkO2CApprovalRequestVO#setApprovalLevel(String)}
     *   <li>{@link BulkO2CApprovalRequestVO#setApprovalType(String)}
     *   <li>{@link BulkO2CApprovalRequestVO#setCategory(String)}
     *   <li>{@link BulkO2CApprovalRequestVO#setDomain(String)}
     *   <li>{@link BulkO2CApprovalRequestVO#setGeographicalDomain(String)}
     *   <li>{@link BulkO2CApprovalRequestVO#toString()}
     *   <li>{@link BulkO2CApprovalRequestVO#getApprovalLevel()}
     *   <li>{@link BulkO2CApprovalRequestVO#getApprovalType()}
     *   <li>{@link BulkO2CApprovalRequestVO#getCategory()}
     *   <li>{@link BulkO2CApprovalRequestVO#getDomain()}
     *   <li>{@link BulkO2CApprovalRequestVO#getGeographicalDomain()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkO2CApprovalRequestVO actualBulkO2CApprovalRequestVO = new BulkO2CApprovalRequestVO();
        actualBulkO2CApprovalRequestVO.setApprovalLevel("Approval Level");
        actualBulkO2CApprovalRequestVO.setApprovalType("Approval Type");
        actualBulkO2CApprovalRequestVO.setCategory("Category");
        actualBulkO2CApprovalRequestVO.setDomain("Domain");
        actualBulkO2CApprovalRequestVO.setGeographicalDomain("Geographical Domain");
        String actualToStringResult = actualBulkO2CApprovalRequestVO.toString();
        assertEquals("Approval Level", actualBulkO2CApprovalRequestVO.getApprovalLevel());
        assertEquals("Approval Type", actualBulkO2CApprovalRequestVO.getApprovalType());
        assertEquals("Category", actualBulkO2CApprovalRequestVO.getCategory());
        assertEquals("Domain", actualBulkO2CApprovalRequestVO.getDomain());
        assertEquals("Geographical Domain", actualBulkO2CApprovalRequestVO.getGeographicalDomain());
        assertEquals(
                "BulkO2CApprovalRequestVO [approvalLevel=Approval Level, approvalType=Approval Type, category=Category,"
                        + " domain=Domain, geographicalDomain=Geographical Domain]",
                actualToStringResult);
    }
}

