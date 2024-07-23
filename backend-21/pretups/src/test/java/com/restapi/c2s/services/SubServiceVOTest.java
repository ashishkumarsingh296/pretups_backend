package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubServiceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SubServiceVO}
     *   <li>{@link SubServiceVO#setSubServiceCode(String)}
     *   <li>{@link SubServiceVO#setSubServiceName(String)}
     *   <li>{@link SubServiceVO#toString()}
     *   <li>{@link SubServiceVO#getSubServiceCode()}
     *   <li>{@link SubServiceVO#getSubServiceName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SubServiceVO actualSubServiceVO = new SubServiceVO();
        actualSubServiceVO.setSubServiceCode("Sub Service Code");
        actualSubServiceVO.setSubServiceName("Sub Service Name");
        actualSubServiceVO.toString();
        assertEquals("Sub Service Code", actualSubServiceVO.getSubServiceCode());
        assertEquals("Sub Service Name", actualSubServiceVO.getSubServiceName());
    }
}

