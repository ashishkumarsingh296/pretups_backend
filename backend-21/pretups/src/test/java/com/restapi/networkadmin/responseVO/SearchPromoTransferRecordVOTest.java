package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SearchPromoTransferRecordVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SearchPromoTransferRecordVO}
     *   <li>{@link SearchPromoTransferRecordVO#setApplicableFrom(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setApplicableTo(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setCardGroupSet(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setServiceClass(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setServiceGroupProvider(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setServiceType(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setStatus(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setSubService(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setSubscriberStatus(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setTimeSlabs(String)}
     *   <li>{@link SearchPromoTransferRecordVO#setType(String)}
     *   <li>{@link SearchPromoTransferRecordVO#getApplicableFrom()}
     *   <li>{@link SearchPromoTransferRecordVO#getApplicableTo()}
     *   <li>{@link SearchPromoTransferRecordVO#getCardGroupSet()}
     *   <li>{@link SearchPromoTransferRecordVO#getServiceClass()}
     *   <li>{@link SearchPromoTransferRecordVO#getServiceGroupProvider()}
     *   <li>{@link SearchPromoTransferRecordVO#getServiceType()}
     *   <li>{@link SearchPromoTransferRecordVO#getStatus()}
     *   <li>{@link SearchPromoTransferRecordVO#getSubService()}
     *   <li>{@link SearchPromoTransferRecordVO#getSubscriberStatus()}
     *   <li>{@link SearchPromoTransferRecordVO#getTimeSlabs()}
     *   <li>{@link SearchPromoTransferRecordVO#getType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SearchPromoTransferRecordVO actualSearchPromoTransferRecordVO = new SearchPromoTransferRecordVO();
        actualSearchPromoTransferRecordVO.setApplicableFrom("jane.doe@example.org");
        actualSearchPromoTransferRecordVO.setApplicableTo("alice.liddell@example.org");
        actualSearchPromoTransferRecordVO.setCardGroupSet("Card Group Set");
        actualSearchPromoTransferRecordVO.setServiceClass("Service Class");
        actualSearchPromoTransferRecordVO.setServiceGroupProvider("Service Group Provider");
        actualSearchPromoTransferRecordVO.setServiceType("Service Type");
        actualSearchPromoTransferRecordVO.setStatus("Status");
        actualSearchPromoTransferRecordVO.setSubService("Sub Service");
        actualSearchPromoTransferRecordVO.setSubscriberStatus("Subscriber Status");
        actualSearchPromoTransferRecordVO.setTimeSlabs("Time Slabs");
        actualSearchPromoTransferRecordVO.setType("Type");
        assertEquals("jane.doe@example.org", actualSearchPromoTransferRecordVO.getApplicableFrom());
        assertEquals("alice.liddell@example.org", actualSearchPromoTransferRecordVO.getApplicableTo());
        assertEquals("Card Group Set", actualSearchPromoTransferRecordVO.getCardGroupSet());
        assertEquals("Service Class", actualSearchPromoTransferRecordVO.getServiceClass());
        assertEquals("Service Group Provider", actualSearchPromoTransferRecordVO.getServiceGroupProvider());
        assertEquals("Service Type", actualSearchPromoTransferRecordVO.getServiceType());
        assertEquals("Status", actualSearchPromoTransferRecordVO.getStatus());
        assertEquals("Sub Service", actualSearchPromoTransferRecordVO.getSubService());
        assertEquals("Subscriber Status", actualSearchPromoTransferRecordVO.getSubscriberStatus());
        assertEquals("Time Slabs", actualSearchPromoTransferRecordVO.getTimeSlabs());
        assertEquals("Type", actualSearchPromoTransferRecordVO.getType());
    }
}

