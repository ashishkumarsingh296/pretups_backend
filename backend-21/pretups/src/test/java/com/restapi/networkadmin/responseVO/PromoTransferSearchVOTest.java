package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PromoTransferSearchVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PromoTransferSearchVO}
     *   <li>{@link PromoTransferSearchVO#setCardGroupSetName(String)}
     *   <li>{@link PromoTransferSearchVO#setPromoApplicableFrom(String)}
     *   <li>{@link PromoTransferSearchVO#setPromoApplicableTo(String)}
     *   <li>{@link PromoTransferSearchVO#setServiceClassName(String)}
     *   <li>{@link PromoTransferSearchVO#setServiceProvideGroup(String)}
     *   <li>{@link PromoTransferSearchVO#setServiceTypeDesc(String)}
     *   <li>{@link PromoTransferSearchVO#setStatusDesc(String)}
     *   <li>{@link PromoTransferSearchVO#setSubServiceDesc(String)}
     *   <li>{@link PromoTransferSearchVO#setSubscriberStatus(String)}
     *   <li>{@link PromoTransferSearchVO#setTimeSlabs(String)}
     *   <li>{@link PromoTransferSearchVO#setTypeDisplay(String)}
     *   <li>{@link PromoTransferSearchVO#getCardGroupSetName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PromoTransferSearchVO actualPromoTransferSearchVO = new PromoTransferSearchVO();
        actualPromoTransferSearchVO.setCardGroupSetName("Card Group Set Name");
        actualPromoTransferSearchVO.setPromoApplicableFrom("jane.doe@example.org");
        actualPromoTransferSearchVO.setPromoApplicableTo("alice.liddell@example.org");
        actualPromoTransferSearchVO.setServiceClassName("Service Class Name");
        actualPromoTransferSearchVO.setServiceProvideGroup("Service Provide Group");
        actualPromoTransferSearchVO.setServiceTypeDesc("Service Type Desc");
        actualPromoTransferSearchVO.setStatusDesc("Status Desc");
        actualPromoTransferSearchVO.setSubServiceDesc("Sub Service Desc");
        actualPromoTransferSearchVO.setSubscriberStatus("Subscriber Status");
        actualPromoTransferSearchVO.setTimeSlabs("Time Slabs");
        actualPromoTransferSearchVO.setTypeDisplay("Type Display");
        assertEquals("Card Group Set Name", actualPromoTransferSearchVO.getCardGroupSetName());
    }

    /**
     * Method under test: {@link PromoTransferSearchVO#getInstancePromoTransferSearchVO()}
     */
    @Test
    public void testGetInstancePromoTransferSearchVO() {
        PromoTransferSearchVO actualInstancePromoTransferSearchVO = PromoTransferSearchVO
                .getInstancePromoTransferSearchVO();
        assertEquals(0L, actualInstancePromoTransferSearchVO.getLastModifiedTime());
        assertEquals("null_null_null_null_null_null_null_null_null_null", actualInstancePromoTransferSearchVO.getKey());
        assertNull(actualInstancePromoTransferSearchVO.getEndTime());
        assertNull(actualInstancePromoTransferSearchVO.getDeniedSeries());
        assertNull(actualInstancePromoTransferSearchVO.getAllowedSeries());
        assertNull(actualInstancePromoTransferSearchVO.getAllowedDays());
    }
}

