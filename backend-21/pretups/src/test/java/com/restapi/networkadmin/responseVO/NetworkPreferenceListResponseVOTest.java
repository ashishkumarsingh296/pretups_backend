package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class NetworkPreferenceListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link NetworkPreferenceListResponseVO}
     *   <li>{@link NetworkPreferenceListResponseVO#setModule(String)}
     *   <li>{@link NetworkPreferenceListResponseVO#setNetworkDescription(String)}
     *   <li>{@link NetworkPreferenceListResponseVO#setPreferenceList(ArrayList)}
     *   <li>{@link NetworkPreferenceListResponseVO#setPreferenceType(String)}
     *   <li>{@link NetworkPreferenceListResponseVO#getModule()}
     *   <li>{@link NetworkPreferenceListResponseVO#getNetworkDescription()}
     *   <li>{@link NetworkPreferenceListResponseVO#getPreferenceList()}
     *   <li>{@link NetworkPreferenceListResponseVO#getPreferenceType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        NetworkPreferenceListResponseVO actualNetworkPreferenceListResponseVO = new NetworkPreferenceListResponseVO();
        actualNetworkPreferenceListResponseVO.setModule("Module");
        actualNetworkPreferenceListResponseVO.setNetworkDescription("Network Description");
        ArrayList preferenceList = new ArrayList();
        actualNetworkPreferenceListResponseVO.setPreferenceList(preferenceList);
        actualNetworkPreferenceListResponseVO.setPreferenceType("Preference Type");
        assertEquals("Module", actualNetworkPreferenceListResponseVO.getModule());
        assertEquals("Network Description", actualNetworkPreferenceListResponseVO.getNetworkDescription());
        assertSame(preferenceList, actualNetworkPreferenceListResponseVO.getPreferenceList());
        assertEquals("Preference Type", actualNetworkPreferenceListResponseVO.getPreferenceType());
    }
}

