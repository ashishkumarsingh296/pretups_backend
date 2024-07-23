package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ServiceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ServiceVO}
     *   <li>{@link ServiceVO#setServiceCode(String)}
     *   <li>{@link ServiceVO#setServiceName(String)}
     *   <li>{@link ServiceVO#toString()}
     *   <li>{@link ServiceVO#getServiceCode()}
     *   <li>{@link ServiceVO#getServiceName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ServiceVO actualServiceVO = new ServiceVO();
        actualServiceVO.setServiceCode("Service Code");
        actualServiceVO.setServiceName("Service Name");
        actualServiceVO.toString();
        assertEquals("Service Code", actualServiceVO.getServiceCode());
        assertEquals("Service Name", actualServiceVO.getServiceName());
    }
}

