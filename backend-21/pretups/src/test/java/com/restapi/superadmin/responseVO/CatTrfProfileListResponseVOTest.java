package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CatTrfProfileListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CatTrfProfileListResponseVO}
     *   <li>{@link CatTrfProfileListResponseVO#setCatProfileTrfList(ArrayList)}
     *   <li>{@link CatTrfProfileListResponseVO#toString()}
     *   <li>{@link CatTrfProfileListResponseVO#getCatProfileTrfList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CatTrfProfileListResponseVO actualCatTrfProfileListResponseVO = new CatTrfProfileListResponseVO();
        ArrayList catProfileTrfList = new ArrayList();
        actualCatTrfProfileListResponseVO.setCatProfileTrfList(catProfileTrfList);
        String actualToStringResult = actualCatTrfProfileListResponseVO.toString();
        assertSame(catProfileTrfList, actualCatTrfProfileListResponseVO.getCatProfileTrfList());
        assertEquals("CatTrfProfileListResponseVO [catProfileTrfList=[]]", actualToStringResult);
    }
}

