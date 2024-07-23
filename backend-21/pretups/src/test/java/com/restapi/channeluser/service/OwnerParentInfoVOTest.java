package com.restapi.channeluser.service;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class OwnerParentInfoVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link OwnerParentInfoVO}
     *   <li>{@link OwnerParentInfoVO#getOwnwerVO()}
     *   <li>{@link OwnerParentInfoVO#getParentVO()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        OwnerParentInfoVO actualOwnerParentInfoVO = new OwnerParentInfoVO();
        assertNull(actualOwnerParentInfoVO.getOwnwerVO());
        assertNull(actualOwnerParentInfoVO.getParentVO());
    }
}

