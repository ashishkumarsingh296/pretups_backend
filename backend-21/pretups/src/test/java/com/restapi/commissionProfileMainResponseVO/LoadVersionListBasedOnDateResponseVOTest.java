package com.restapi.commissionProfileMainResponseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class LoadVersionListBasedOnDateResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LoadVersionListBasedOnDateResponseVO}
     *   <li>{@link LoadVersionListBasedOnDateResponseVO#setVersionList(ArrayList)}
     *   <li>{@link LoadVersionListBasedOnDateResponseVO#getVersionList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LoadVersionListBasedOnDateResponseVO actualLoadVersionListBasedOnDateResponseVO = new LoadVersionListBasedOnDateResponseVO();
        ArrayList versionList = new ArrayList();
        actualLoadVersionListBasedOnDateResponseVO.setVersionList(versionList);
        assertSame(versionList, actualLoadVersionListBasedOnDateResponseVO.getVersionList());
    }
}

