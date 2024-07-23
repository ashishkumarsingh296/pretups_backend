package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class UpdateServiceClassPreferenceReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateServiceClassPreferenceReqVO}
     *   <li>{@link UpdateServiceClassPreferenceReqVO#setPreferenceUpdateList(ArrayList)}
     *   <li>{@link UpdateServiceClassPreferenceReqVO#getPreferenceUpdateList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateServiceClassPreferenceReqVO actualUpdateServiceClassPreferenceReqVO = new UpdateServiceClassPreferenceReqVO();
        ArrayList<UpdateServiceClassPreferenceVO> preferenceUpdateList = new ArrayList<>();
        actualUpdateServiceClassPreferenceReqVO.setPreferenceUpdateList(preferenceUpdateList);
        assertSame(preferenceUpdateList, actualUpdateServiceClassPreferenceReqVO.getPreferenceUpdateList());
    }
}

