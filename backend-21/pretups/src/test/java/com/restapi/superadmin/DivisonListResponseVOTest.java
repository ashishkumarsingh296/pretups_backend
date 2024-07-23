package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DivisonListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DivisonListResponseVO}
     *   <li>{@link DivisonListResponseVO#setDivisionList(ArrayList)}
     *   <li>{@link DivisonListResponseVO#toString()}
     *   <li>{@link DivisonListResponseVO#getDivisionList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DivisonListResponseVO actualDivisonListResponseVO = new DivisonListResponseVO();
        ArrayList<DivisionVO> divisionList = new ArrayList<>();
        actualDivisonListResponseVO.setDivisionList(divisionList);
        String actualToStringResult = actualDivisonListResponseVO.toString();
        assertSame(divisionList, actualDivisonListResponseVO.getDivisionList());
        assertEquals("DivisonListResponseVO [divisionList=[]]", actualToStringResult);
    }
}

