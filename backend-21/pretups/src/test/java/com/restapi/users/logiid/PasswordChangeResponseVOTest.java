package com.restapi.users.logiid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PasswordChangeResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PasswordChangeResponseVO}
     *   <li>{@link PasswordChangeResponseVO#setChangePassword(String)}
     *   <li>{@link PasswordChangeResponseVO#toString()}
     *   <li>{@link PasswordChangeResponseVO#getChangePassword()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PasswordChangeResponseVO actualPasswordChangeResponseVO = new PasswordChangeResponseVO();
        actualPasswordChangeResponseVO.setChangePassword("iloveyou");
        String actualToStringResult = actualPasswordChangeResponseVO.toString();
        assertEquals("iloveyou", actualPasswordChangeResponseVO.getChangePassword());
        assertEquals("PasswordChangeResponseVO [changePassword=iloveyou]", actualToStringResult);
    }
}

