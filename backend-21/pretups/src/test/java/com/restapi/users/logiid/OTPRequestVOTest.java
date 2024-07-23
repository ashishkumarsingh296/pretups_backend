package com.restapi.users.logiid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OTPRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link OTPRequestVO}
     *   <li>{@link OTPRequestVO#setLoginId(String)}
     *   <li>{@link OTPRequestVO#setMode(String)}
     *   <li>{@link OTPRequestVO#setReSend(String)}
     *   <li>{@link OTPRequestVO#toString()}
     *   <li>{@link OTPRequestVO#getLoginId()}
     *   <li>{@link OTPRequestVO#getMode()}
     *   <li>{@link OTPRequestVO#getReSend()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        OTPRequestVO actualOtpRequestVO = new OTPRequestVO();
        actualOtpRequestVO.setLoginId("42");
        actualOtpRequestVO.setMode("Mode");
        actualOtpRequestVO.setReSend("Re Send");
        String actualToStringResult = actualOtpRequestVO.toString();
        assertEquals("42", actualOtpRequestVO.getLoginId());
        assertEquals("Mode", actualOtpRequestVO.getMode());
        assertEquals("Re Send", actualOtpRequestVO.getReSend());
        assertEquals("OTPRequestVO [loginId=42, mode=Mode, reSend=Re Send]", actualToStringResult);
    }
}

