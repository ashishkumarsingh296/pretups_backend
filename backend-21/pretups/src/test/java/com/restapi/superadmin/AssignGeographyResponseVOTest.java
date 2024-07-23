package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class AssignGeographyResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AssignGeographyResponseVO}
     *   <li>{@link AssignGeographyResponseVO#setGeographyList(ArrayList)}
     *   <li>{@link AssignGeographyResponseVO#toString()}
     *   <li>{@link AssignGeographyResponseVO#getGeographyList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AssignGeographyResponseVO actualAssignGeographyResponseVO = new AssignGeographyResponseVO();
        ArrayList geographyList = new ArrayList();
        actualAssignGeographyResponseVO.setGeographyList(geographyList);
        String actualToStringResult = actualAssignGeographyResponseVO.toString();
        assertSame(geographyList, actualAssignGeographyResponseVO.getGeographyList());
        assertEquals("AssignGeographyResponseVO [geographyList=[]]", actualToStringResult);
    }
}

