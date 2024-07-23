package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelUserTransferOtpRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserTransferOtpRequestVO}
     *   <li>{@link ChannelUserTransferOtpRequestVO#setMode(String)}
     *   <li>{@link ChannelUserTransferOtpRequestVO#setMsisdn(String)}
     *   <li>{@link ChannelUserTransferOtpRequestVO#setReSend(String)}
     *   <li>{@link ChannelUserTransferOtpRequestVO#toString()}
     *   <li>{@link ChannelUserTransferOtpRequestVO#getMode()}
     *   <li>{@link ChannelUserTransferOtpRequestVO#getMsisdn()}
     *   <li>{@link ChannelUserTransferOtpRequestVO#getReSend()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserTransferOtpRequestVO actualChannelUserTransferOtpRequestVO = new ChannelUserTransferOtpRequestVO();
        actualChannelUserTransferOtpRequestVO.setMode("Mode");
        actualChannelUserTransferOtpRequestVO.setMsisdn("Msisdn");
        actualChannelUserTransferOtpRequestVO.setReSend("Re Send");
        String actualToStringResult = actualChannelUserTransferOtpRequestVO.toString();
        assertEquals("Mode", actualChannelUserTransferOtpRequestVO.getMode());
        assertEquals("Msisdn", actualChannelUserTransferOtpRequestVO.getMsisdn());
        assertEquals("Re Send", actualChannelUserTransferOtpRequestVO.getReSend());
        assertEquals("OTPRequestVO [msisdn=Msisdn, mode=Mode, reSend=Re Send]", actualToStringResult);
    }
}

