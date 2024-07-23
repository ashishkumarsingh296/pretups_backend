package com.restapi.channeluser.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ActionOnUserResVoTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ActionOnUserResVo}
     *   <li>{@link ActionOnUserResVo#setChangeStatus(boolean)}
     *   <li>{@link ActionOnUserResVo#isChangeStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ActionOnUserResVo actualActionOnUserResVo = new ActionOnUserResVo();
        actualActionOnUserResVo.setChangeStatus(true);
        assertTrue(actualActionOnUserResVo.isChangeStatus());
    }
}

