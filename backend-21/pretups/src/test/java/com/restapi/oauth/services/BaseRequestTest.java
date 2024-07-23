package com.restapi.oauth.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BaseRequestTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link BaseRequest#BaseRequest()}
     *   <li>{@link BaseRequest#setExternalRefId(String)}
     *   <li>{@link BaseRequest#setUserId(String)}
     *   <li>{@link BaseRequest#getExternalRefId()}
     *   <li>{@link BaseRequest#getUserId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BaseRequest actualBaseRequest = new BaseRequest();
        actualBaseRequest.setExternalRefId("42");
        actualBaseRequest.setUserId("42");
        assertEquals("42", actualBaseRequest.getExternalRefId());
        assertEquals("42", actualBaseRequest.getUserId());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link BaseRequest#BaseRequest(String)}
     *   <li>{@link BaseRequest#setExternalRefId(String)}
     *   <li>{@link BaseRequest#setUserId(String)}
     *   <li>{@link BaseRequest#getExternalRefId()}
     *   <li>{@link BaseRequest#getUserId()}
     * </ul>
     */
    @Test
    public void testConstructor2() {
        BaseRequest actualBaseRequest = new BaseRequest("42");
        actualBaseRequest.setExternalRefId("42");
        actualBaseRequest.setUserId("42");
        assertEquals("42", actualBaseRequest.getExternalRefId());
        assertEquals("42", actualBaseRequest.getUserId());
    }
}

