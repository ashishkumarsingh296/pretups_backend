package com.restapi.users.logiid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OTPResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link OTPResponseVO}
     *   <li>{@link OTPResponseVO#setOtp(String)}
     *   <li>{@link OTPResponseVO#getOtp()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        OTPResponseVO actualOtpResponseVO = new OTPResponseVO();
        actualOtpResponseVO.setOtp("Otp");
        assertEquals("Otp", actualOtpResponseVO.getOtp());
    }
}

