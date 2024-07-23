package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserWidgetResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserWidgetResponse}
     *   <li>{@link UserWidgetResponse#setMessage(String)}
     *   <li>{@link UserWidgetResponse#setMessageCode(String)}
     *   <li>{@link UserWidgetResponse#setStatus(int)}
     *   <li>{@link UserWidgetResponse#getMessage()}
     *   <li>{@link UserWidgetResponse#getMessageCode()}
     *   <li>{@link UserWidgetResponse#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserWidgetResponse actualUserWidgetResponse = new UserWidgetResponse();
        actualUserWidgetResponse.setMessage("Not all who wander are lost");
        actualUserWidgetResponse.setMessageCode("Message Code");
        actualUserWidgetResponse.setStatus(1);
        assertEquals("Not all who wander are lost", actualUserWidgetResponse.getMessage());
        assertEquals("Message Code", actualUserWidgetResponse.getMessageCode());
        assertEquals(1, actualUserWidgetResponse.getStatus());
    }
}

