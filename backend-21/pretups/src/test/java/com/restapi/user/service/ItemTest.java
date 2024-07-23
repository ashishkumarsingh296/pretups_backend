package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ItemTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link Item}
     *   <li>{@link Item#setPermission(int)}
     *   <li>{@link Item#setUserId(String)}
     *   <li>{@link Item#getPermission()}
     *   <li>{@link Item#getUserId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        Item actualItem = new Item();
        actualItem.setPermission(1);
        actualItem.setUserId("42");
        assertEquals(1, actualItem.getPermission());
        assertEquals("42", actualItem.getUserId());
    }
}

