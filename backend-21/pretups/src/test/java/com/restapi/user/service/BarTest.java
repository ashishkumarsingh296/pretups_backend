package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BarTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link Bar}
     *   <li>{@link Bar#setBarringReason(String)}
     *   <li>{@link Bar#setBarringType(String)}
     *   <li>{@link Bar#getBarringReason()}
     *   <li>{@link Bar#getBarringType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        Bar actualBar = new Bar();
        actualBar.setBarringReason("Just cause");
        actualBar.setBarringType("Barring Type");
        assertEquals("Just cause", actualBar.getBarringReason());
        assertEquals("Barring Type", actualBar.getBarringType());
    }
}

