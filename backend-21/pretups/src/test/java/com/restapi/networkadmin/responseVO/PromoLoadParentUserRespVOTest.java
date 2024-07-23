package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class PromoLoadParentUserRespVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PromoLoadParentUserRespVO}
     *   <li>{@link PromoLoadParentUserRespVO#setUserList(ArrayList)}
     *   <li>{@link PromoLoadParentUserRespVO#getUserList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PromoLoadParentUserRespVO actualPromoLoadParentUserRespVO = new PromoLoadParentUserRespVO();
        ArrayList userList = new ArrayList();
        actualPromoLoadParentUserRespVO.setUserList(userList);
        assertSame(userList, actualPromoLoadParentUserRespVO.getUserList());
    }
}

