package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserDeleteRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserDeleteRequestVO}
     *   <li>{@link UserDeleteRequestVO#setExtnwcode(String)}
     *   <li>{@link UserDeleteRequestVO#setRemarks(String)}
     *   <li>{@link UserDeleteRequestVO#toString()}
     *   <li>{@link UserDeleteRequestVO#getExtnwcode()}
     *   <li>{@link UserDeleteRequestVO#getRemarks()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserDeleteRequestVO actualUserDeleteRequestVO = new UserDeleteRequestVO();
        actualUserDeleteRequestVO.setExtnwcode("Extnwcode");
        actualUserDeleteRequestVO.setRemarks("Remarks");
        String actualToStringResult = actualUserDeleteRequestVO.toString();
        assertEquals("Extnwcode", actualUserDeleteRequestVO.getExtnwcode());
        assertEquals("Remarks", actualUserDeleteRequestVO.getRemarks());
        assertEquals("UserDeleteRequestVO [remarks=Remarks, extnwcode=Extnwcode]", actualToStringResult);
    }
}

