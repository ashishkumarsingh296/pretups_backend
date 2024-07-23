package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SuspendResumeResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SuspendResumeResponse}
     *   <li>{@link SuspendResumeResponse#setMessage(String)}
     *   <li>{@link SuspendResumeResponse#setMessageCode(String)}
     *   <li>{@link SuspendResumeResponse#setService(String)}
     *   <li>{@link SuspendResumeResponse#setStatus(int)}
     *   <li>{@link SuspendResumeResponse#getMessage()}
     *   <li>{@link SuspendResumeResponse#getMessageCode()}
     *   <li>{@link SuspendResumeResponse#getService()}
     *   <li>{@link SuspendResumeResponse#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SuspendResumeResponse actualSuspendResumeResponse = new SuspendResumeResponse();
        actualSuspendResumeResponse.setMessage("Not all who wander are lost");
        actualSuspendResumeResponse.setMessageCode("Message Code");
        actualSuspendResumeResponse.setService("Service");
        actualSuspendResumeResponse.setStatus(1);
        assertEquals("Not all who wander are lost", actualSuspendResumeResponse.getMessage());
        assertEquals("Message Code", actualSuspendResumeResponse.getMessageCode());
        assertEquals("Service", actualSuspendResumeResponse.getService());
        assertEquals(1, actualSuspendResumeResponse.getStatus());
    }
}

