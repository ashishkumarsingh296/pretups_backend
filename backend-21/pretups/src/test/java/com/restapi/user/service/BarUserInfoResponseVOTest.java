package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BarUserInfoResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BarUserInfoResponseVO}
     *   <li>{@link BarUserInfoResponseVO#setCategory(String)}
     *   <li>{@link BarUserInfoResponseVO#setDomain(String)}
     *   <li>{@link BarUserInfoResponseVO#setMsisdn(String)}
     *   <li>{@link BarUserInfoResponseVO#setSenderAllowed(boolean)}
     *   <li>{@link BarUserInfoResponseVO#setUserName(String)}
     *   <li>{@link BarUserInfoResponseVO#setUserType(String)}
     *   <li>{@link BarUserInfoResponseVO#toString()}
     *   <li>{@link BarUserInfoResponseVO#getCategory()}
     *   <li>{@link BarUserInfoResponseVO#getDomain()}
     *   <li>{@link BarUserInfoResponseVO#getMsisdn()}
     *   <li>{@link BarUserInfoResponseVO#getUserName()}
     *   <li>{@link BarUserInfoResponseVO#getUserType()}
     *   <li>{@link BarUserInfoResponseVO#isSenderAllowed()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BarUserInfoResponseVO actualBarUserInfoResponseVO = new BarUserInfoResponseVO();
        actualBarUserInfoResponseVO.setCategory("Category");
        actualBarUserInfoResponseVO.setDomain("Domain");
        actualBarUserInfoResponseVO.setMsisdn("Msisdn");
        actualBarUserInfoResponseVO.setSenderAllowed(true);
        actualBarUserInfoResponseVO.setUserName("janedoe");
        actualBarUserInfoResponseVO.setUserType("User Type");
        String actualToStringResult = actualBarUserInfoResponseVO.toString();
        assertEquals("Category", actualBarUserInfoResponseVO.getCategory());
        assertEquals("Domain", actualBarUserInfoResponseVO.getDomain());
        assertEquals("Msisdn", actualBarUserInfoResponseVO.getMsisdn());
        assertEquals("janedoe", actualBarUserInfoResponseVO.getUserName());
        assertEquals("User Type", actualBarUserInfoResponseVO.getUserType());
        assertTrue(actualBarUserInfoResponseVO.isSenderAllowed());
        assertEquals("BarUserInfoResponseVO [senderAllowed=true, userName=janedoe, msisdn=Msisdn]", actualToStringResult);
    }
}

