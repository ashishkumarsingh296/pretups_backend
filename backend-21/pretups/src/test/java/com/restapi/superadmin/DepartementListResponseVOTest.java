package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DepartementListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DepartementListResponseVO}
     *   <li>{@link DepartementListResponseVO#setDepartmentList(ArrayList)}
     *   <li>{@link DepartementListResponseVO#toString()}
     *   <li>{@link DepartementListResponseVO#getDepartmentList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DepartementListResponseVO actualDepartementListResponseVO = new DepartementListResponseVO();
        ArrayList departmentList = new ArrayList();
        actualDepartementListResponseVO.setDepartmentList(departmentList);
        String actualToStringResult = actualDepartementListResponseVO.toString();
        assertSame(departmentList, actualDepartementListResponseVO.getDepartmentList());
        assertEquals("DepartementListResponseVO [departmentList=[]]", actualToStringResult);
    }
}

