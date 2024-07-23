package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ServiceListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ServiceListResponseVO}
     *   <li>{@link ServiceListResponseVO#setServicesList(List)}
     *   <li>{@link ServiceListResponseVO#toString()}
     *   <li>{@link ServiceListResponseVO#getServicesList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ServiceListResponseVO actualServiceListResponseVO = new ServiceListResponseVO();
        ArrayList<ServiceListFilter> servicesList = new ArrayList<>();
        actualServiceListResponseVO.setServicesList(servicesList);
        String actualToStringResult = actualServiceListResponseVO.toString();
        assertSame(servicesList, actualServiceListResponseVO.getServicesList());
        assertEquals("ServiceListVO [servicesList=[]]", actualToStringResult);
    }
}

