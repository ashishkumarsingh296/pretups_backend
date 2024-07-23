package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BarUnbarRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BarUnbarRequestVO}
     *   <li>{@link BarUnbarRequestVO#setBar(List)}
     *   <li>{@link BarUnbarRequestVO#setModule(String)}
     *   <li>{@link BarUnbarRequestVO#setMsisdn(String)}
     *   <li>{@link BarUnbarRequestVO#setUserName(String)}
     *   <li>{@link BarUnbarRequestVO#setUserType(String)}
     *   <li>{@link BarUnbarRequestVO#getBar()}
     *   <li>{@link BarUnbarRequestVO#getModule()}
     *   <li>{@link BarUnbarRequestVO#getMsisdn()}
     *   <li>{@link BarUnbarRequestVO#getUserName()}
     *   <li>{@link BarUnbarRequestVO#getUserType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BarUnbarRequestVO actualBarUnbarRequestVO = new BarUnbarRequestVO();
        ArrayList<Bar> bar = new ArrayList<>();
        actualBarUnbarRequestVO.setBar(bar);
        actualBarUnbarRequestVO.setModule("Module");
        actualBarUnbarRequestVO.setMsisdn("Msisdn");
        actualBarUnbarRequestVO.setUserName("janedoe");
        actualBarUnbarRequestVO.setUserType("User Type");
        assertSame(bar, actualBarUnbarRequestVO.getBar());
        assertEquals("Module", actualBarUnbarRequestVO.getModule());
        assertEquals("Msisdn", actualBarUnbarRequestVO.getMsisdn());
        assertEquals("janedoe", actualBarUnbarRequestVO.getUserName());
        assertEquals("User Type", actualBarUnbarRequestVO.getUserType());
    }
}

