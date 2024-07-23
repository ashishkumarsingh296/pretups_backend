package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CAC2STransferReversalConfirmVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CAC2STransferReversalConfirmVO}
     *   <li>{@link CAC2STransferReversalConfirmVO#setSenderMsisdn(String)}
     *   <li>{@link CAC2STransferReversalConfirmVO#setTransactionID(String)}
     *   <li>{@link CAC2STransferReversalConfirmVO#setUserMsisdn(String)}
     *   <li>{@link CAC2STransferReversalConfirmVO#toString()}
     *   <li>{@link CAC2STransferReversalConfirmVO#getSenderMsisdn()}
     *   <li>{@link CAC2STransferReversalConfirmVO#getTransactionID()}
     *   <li>{@link CAC2STransferReversalConfirmVO#getUserMsisdn()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CAC2STransferReversalConfirmVO actualCac2sTransferReversalConfirmVO = new CAC2STransferReversalConfirmVO();
        actualCac2sTransferReversalConfirmVO.setSenderMsisdn("Sender Msisdn");
        actualCac2sTransferReversalConfirmVO.setTransactionID("Transaction ID");
        actualCac2sTransferReversalConfirmVO.setUserMsisdn("User Msisdn");
        String actualToStringResult = actualCac2sTransferReversalConfirmVO.toString();
        assertEquals("Sender Msisdn", actualCac2sTransferReversalConfirmVO.getSenderMsisdn());
        assertEquals("Transaction ID", actualCac2sTransferReversalConfirmVO.getTransactionID());
        assertEquals("User Msisdn", actualCac2sTransferReversalConfirmVO.getUserMsisdn());
        assertEquals(
                "CAC2STransferReversalConfirmVO [transactionID=Transaction ID, senderMsisdn=Sender Msisdn, userMsisdn=User"
                        + " Msisdn]",
                actualToStringResult);
    }
}

