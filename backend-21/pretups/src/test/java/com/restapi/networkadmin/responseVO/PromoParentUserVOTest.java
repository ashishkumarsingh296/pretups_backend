package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PromoParentUserVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PromoParentUserVO}
     *   <li>{@link PromoParentUserVO#setLoginID(String)}
     *   <li>{@link PromoParentUserVO#setOwnerID(String)}
     *   <li>{@link PromoParentUserVO#setUserID(String)}
     *   <li>{@link PromoParentUserVO#setUserName(String)}
     *   <li>{@link PromoParentUserVO#getLoginID()}
     *   <li>{@link PromoParentUserVO#getOwnerID()}
     *   <li>{@link PromoParentUserVO#getUserID()}
     *   <li>{@link PromoParentUserVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PromoParentUserVO actualPromoParentUserVO = new PromoParentUserVO();
        actualPromoParentUserVO.setLoginID("Login ID");
        actualPromoParentUserVO.setOwnerID("Owner ID");
        actualPromoParentUserVO.setUserID("User ID");
        actualPromoParentUserVO.setUserName("janedoe");
        assertEquals("Login ID", actualPromoParentUserVO.getLoginID());
        assertEquals("Owner ID", actualPromoParentUserVO.getOwnerID());
        assertEquals("User ID", actualPromoParentUserVO.getUserID());
        assertEquals("janedoe", actualPromoParentUserVO.getUserName());
    }
}

