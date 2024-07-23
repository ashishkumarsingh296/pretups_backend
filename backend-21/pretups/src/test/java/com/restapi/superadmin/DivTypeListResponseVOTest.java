package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DivTypeListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DivTypeListResponseVO}
     *   <li>{@link DivTypeListResponseVO#setDivDepTypeList(ArrayList)}
     *   <li>{@link DivTypeListResponseVO#toString()}
     *   <li>{@link DivTypeListResponseVO#getDivDepTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DivTypeListResponseVO actualDivTypeListResponseVO = new DivTypeListResponseVO();
        ArrayList divDepTypeList = new ArrayList();
        actualDivTypeListResponseVO.setDivDepTypeList(divDepTypeList);
        String actualToStringResult = actualDivTypeListResponseVO.toString();
        assertSame(divDepTypeList, actualDivTypeListResponseVO.getDivDepTypeList());
        assertEquals("DivTypeListResponseVO [divDepTypeList=[]]", actualToStringResult);
    }
}

