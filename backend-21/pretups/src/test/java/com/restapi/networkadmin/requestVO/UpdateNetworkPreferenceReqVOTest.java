package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class UpdateNetworkPreferenceReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateNetworkPreferenceReqVO}
     *   <li>{@link UpdateNetworkPreferenceReqVO#setPreferenceUpdateList(ArrayList)}
     *   <li>{@link UpdateNetworkPreferenceReqVO#getPreferenceUpdateList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateNetworkPreferenceReqVO actualUpdateNetworkPreferenceReqVO = new UpdateNetworkPreferenceReqVO();
        ArrayList<UpdateNetworkPreferenceVO> preferenceUpdateList = new ArrayList<>();
        actualUpdateNetworkPreferenceReqVO.setPreferenceUpdateList(preferenceUpdateList);
        assertSame(preferenceUpdateList, actualUpdateNetworkPreferenceReqVO.getPreferenceUpdateList());
    }
}

