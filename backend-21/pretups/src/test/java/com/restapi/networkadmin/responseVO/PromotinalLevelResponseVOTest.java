package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class PromotinalLevelResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PromotinalLevelResponseVO}
     *   <li>{@link PromotinalLevelResponseVO#setPromotionalLevelList(ArrayList)}
     *   <li>{@link PromotinalLevelResponseVO#toString()}
     *   <li>{@link PromotinalLevelResponseVO#getPromotionalLevelList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PromotinalLevelResponseVO actualPromotinalLevelResponseVO = new PromotinalLevelResponseVO();
        ArrayList promotionalLevelList = new ArrayList();
        actualPromotinalLevelResponseVO.setPromotionalLevelList(promotionalLevelList);
        String actualToStringResult = actualPromotinalLevelResponseVO.toString();
        assertSame(promotionalLevelList, actualPromotinalLevelResponseVO.getPromotionalLevelList());
        assertEquals("PromotinalLevelResponseVO [promotionalLevelList=[]]", actualToStringResult);
    }
}

