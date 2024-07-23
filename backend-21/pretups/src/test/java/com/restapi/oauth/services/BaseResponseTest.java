package com.restapi.oauth.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BaseResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BaseResponse}
     *   <li>{@link BaseResponse#setExternalRefId(String)}
     *   <li>{@link BaseResponse#setMessage(String)}
     *   <li>{@link BaseResponse#setMessageCode(String)}
     *   <li>{@link BaseResponse#setReferenceId(String)}
     *   <li>{@link BaseResponse#setStatus(String)}
     *   <li>{@link BaseResponse#getExternalRefId()}
     *   <li>{@link BaseResponse#getMessage()}
     *   <li>{@link BaseResponse#getMessageCode()}
     *   <li>{@link BaseResponse#getReferenceId()}
     *   <li>{@link BaseResponse#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BaseResponse actualBaseResponse = new BaseResponse();
        actualBaseResponse.setExternalRefId("42");
        actualBaseResponse.setMessage("Not all who wander are lost");
        actualBaseResponse.setMessageCode("Message Code");
        actualBaseResponse.setReferenceId("42");
        actualBaseResponse.setStatus("Status");
        assertEquals("42", actualBaseResponse.getExternalRefId());
        assertEquals("Not all who wander are lost", actualBaseResponse.getMessage());
        assertEquals("Message Code", actualBaseResponse.getMessageCode());
        assertEquals("42", actualBaseResponse.getReferenceId());
        assertEquals("Status", actualBaseResponse.getStatus());
    }
}

