package com.restapi.c2s.services;

import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class SubServicesListVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SubServicesListVO}
     *   <li>{@link SubServicesListVO#setSubServicesList(HashMap)}
     *   <li>{@link SubServicesListVO#toString()}
     *   <li>{@link SubServicesListVO#getSubServicesList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SubServicesListVO actualSubServicesListVO = new SubServicesListVO();
        HashMap<String, List<SubServiceVO>> subServicesList = new HashMap<>();
        actualSubServicesListVO.setSubServicesList(subServicesList);
        actualSubServicesListVO.toString();
        assertSame(subServicesList, actualSubServicesListVO.getSubServicesList());
    }
}

