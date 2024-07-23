package com.restapi.users.logiid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidateOTPRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ValidateOTPRequestVO}
     *   <li>{@link ValidateOTPRequestVO#setOtp(String)}
     *   <li>{@link ValidateOTPRequestVO#toString()}
     *   <li>{@link ValidateOTPRequestVO#getOtp()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ValidateOTPRequestVO actualValidateOTPRequestVO = new ValidateOTPRequestVO();
        actualValidateOTPRequestVO.setOtp("Otp");
        String actualToStringResult = actualValidateOTPRequestVO.toString();
        assertEquals("Otp", actualValidateOTPRequestVO.getOtp());
        assertEquals("ValidateOTPRequestVO [otp=Otp]", actualToStringResult);
    }
}

