package com.restapi.users.logiid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidateOTPResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ValidateOTPResponseVO}
     *   <li>{@link ValidateOTPResponseVO#setLoginId(String)}
     *   <li>{@link ValidateOTPResponseVO#toString()}
     *   <li>{@link ValidateOTPResponseVO#getLoginId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ValidateOTPResponseVO actualValidateOTPResponseVO = new ValidateOTPResponseVO();
        actualValidateOTPResponseVO.setLoginId("42");
        String actualToStringResult = actualValidateOTPResponseVO.toString();
        assertEquals("42", actualValidateOTPResponseVO.getLoginId());
        assertEquals("ValidateOTPResponseVO [loginId=42]", actualToStringResult);
    }
}

