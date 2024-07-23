package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CAC2STransferReversalListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CAC2STransferReversalListRequestVO}
     *   <li>{@link CAC2STransferReversalListRequestVO#setSenderMsisdn(String)}
     *   <li>{@link CAC2STransferReversalListRequestVO#setServiceType(String)}
     *   <li>{@link CAC2STransferReversalListRequestVO#setTransactionID(String)}
     *   <li>{@link CAC2STransferReversalListRequestVO#setUserMsisdn(String)}
     *   <li>{@link CAC2STransferReversalListRequestVO#toString()}
     *   <li>{@link CAC2STransferReversalListRequestVO#getSenderMsisdn()}
     *   <li>{@link CAC2STransferReversalListRequestVO#getServiceType()}
     *   <li>{@link CAC2STransferReversalListRequestVO#getTransactionID()}
     *   <li>{@link CAC2STransferReversalListRequestVO#getUserMsisdn()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CAC2STransferReversalListRequestVO actualCac2sTransferReversalListRequestVO = new CAC2STransferReversalListRequestVO();
        actualCac2sTransferReversalListRequestVO.setSenderMsisdn("Sender Msisdn");
        actualCac2sTransferReversalListRequestVO.setServiceType("Service Type");
        actualCac2sTransferReversalListRequestVO.setTransactionID("Transaction ID");
        actualCac2sTransferReversalListRequestVO.setUserMsisdn("User Msisdn");
        String actualToStringResult = actualCac2sTransferReversalListRequestVO.toString();
        assertEquals("Sender Msisdn", actualCac2sTransferReversalListRequestVO.getSenderMsisdn());
        assertEquals("Service Type", actualCac2sTransferReversalListRequestVO.getServiceType());
        assertEquals("Transaction ID", actualCac2sTransferReversalListRequestVO.getTransactionID());
        assertEquals("User Msisdn", actualCac2sTransferReversalListRequestVO.getUserMsisdn());
        assertEquals("CAC2STransferReversalListRequestVO [transactionID=Transaction ID, senderMsisdn=Sender Msisdn,"
                + " serviceType=Service Type, userMsisdn=User Msisdn]", actualToStringResult);
    }
}

