package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ActionOnUserReqVoTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ActionOnUserReqVo}
     *   <li>{@link ActionOnUserReqVo#setAction(String)}
     *   <li>{@link ActionOnUserReqVo#setLoginId(String)}
     *   <li>{@link ActionOnUserReqVo#setRemarks(String)}
     *   <li>{@link ActionOnUserReqVo#setRequestType(String)}
     *   <li>{@link ActionOnUserReqVo#getAction()}
     *   <li>{@link ActionOnUserReqVo#getLoginId()}
     *   <li>{@link ActionOnUserReqVo#getRemarks()}
     *   <li>{@link ActionOnUserReqVo#getRequestType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ActionOnUserReqVo actualActionOnUserReqVo = new ActionOnUserReqVo();
        actualActionOnUserReqVo.setAction("Action");
        actualActionOnUserReqVo.setLoginId("42");
        actualActionOnUserReqVo.setRemarks("Remarks");
        actualActionOnUserReqVo.setRequestType("Request Type");
        assertEquals("Action", actualActionOnUserReqVo.getAction());
        assertEquals("42", actualActionOnUserReqVo.getLoginId());
        assertEquals("Remarks", actualActionOnUserReqVo.getRemarks());
        assertEquals("Request Type", actualActionOnUserReqVo.getRequestType());
    }
}

