package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ServiceClassPreferenceListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ServiceClassPreferenceListResponseVO}
     *   <li>{@link ServiceClassPreferenceListResponseVO#setPreferenceList(ArrayList)}
     *   <li>{@link ServiceClassPreferenceListResponseVO#setServiceDescription(String)}
     *   <li>{@link ServiceClassPreferenceListResponseVO#getPreferenceList()}
     *   <li>{@link ServiceClassPreferenceListResponseVO#getServiceDescription()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ServiceClassPreferenceListResponseVO actualServiceClassPreferenceListResponseVO = new ServiceClassPreferenceListResponseVO();
        ArrayList preferenceList = new ArrayList();
        actualServiceClassPreferenceListResponseVO.setPreferenceList(preferenceList);
        actualServiceClassPreferenceListResponseVO.setServiceDescription("Service Description");
        assertSame(preferenceList, actualServiceClassPreferenceListResponseVO.getPreferenceList());
        assertEquals("Service Description", actualServiceClassPreferenceListResponseVO.getServiceDescription());
    }
}

