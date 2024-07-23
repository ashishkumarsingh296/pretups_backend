package com.restapi.superadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DivisonListForDepartmentResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DivisonListForDepartmentResponseVO}
     *   <li>{@link DivisonListForDepartmentResponseVO#setDivisionList(ArrayList)}
     *   <li>{@link DivisonListForDepartmentResponseVO#getDivisionList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DivisonListForDepartmentResponseVO actualDivisonListForDepartmentResponseVO = new DivisonListForDepartmentResponseVO();
        ArrayList divisionList = new ArrayList();
        actualDivisonListForDepartmentResponseVO.setDivisionList(divisionList);
        assertSame(divisionList, actualDivisonListForDepartmentResponseVO.getDivisionList());
    }
}

