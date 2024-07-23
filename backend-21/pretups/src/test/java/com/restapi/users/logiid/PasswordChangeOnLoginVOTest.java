package com.restapi.users.logiid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordChangeOnLoginVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PasswordChangeOnLoginVO}
     *   <li>{@link PasswordChangeOnLoginVO#setIsPasswordChange(Boolean)}
     *   <li>{@link PasswordChangeOnLoginVO#toString()}
     *   <li>{@link PasswordChangeOnLoginVO#getIsPasswordChange()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PasswordChangeOnLoginVO actualPasswordChangeOnLoginVO = new PasswordChangeOnLoginVO();
        actualPasswordChangeOnLoginVO.setIsPasswordChange(true);
        String actualToStringResult = actualPasswordChangeOnLoginVO.toString();
        assertTrue(actualPasswordChangeOnLoginVO.getIsPasswordChange());
        assertEquals("PasswordChangeOnLoginVO [isPasswordChange=true]", actualToStringResult);
    }
}

