package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfimChannelUserTransferRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ConfimChannelUserTransferRequestVO}
     *   <li>{@link ConfimChannelUserTransferRequestVO#setMsisdn(String)}
     *   <li>{@link ConfimChannelUserTransferRequestVO#setOtp(String)}
     *   <li>{@link ConfimChannelUserTransferRequestVO#getMsisdn()}
     *   <li>{@link ConfimChannelUserTransferRequestVO#getOtp()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ConfimChannelUserTransferRequestVO actualConfimChannelUserTransferRequestVO = new ConfimChannelUserTransferRequestVO();
        actualConfimChannelUserTransferRequestVO.setMsisdn("Msisdn");
        actualConfimChannelUserTransferRequestVO.setOtp("Otp");
        assertEquals("Msisdn", actualConfimChannelUserTransferRequestVO.getMsisdn());
        assertEquals("Otp", actualConfimChannelUserTransferRequestVO.getOtp());
    }
}

