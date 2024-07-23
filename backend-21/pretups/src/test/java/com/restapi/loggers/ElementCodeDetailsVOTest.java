package com.restapi.loggers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashMap;

import org.junit.Test;

public class ElementCodeDetailsVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ElementCodeDetailsVO}
     *   <li>{@link ElementCodeDetailsVO#setGroupNameMap(HashMap)}
     *   <li>{@link ElementCodeDetailsVO#setRoleCode(String)}
     *   <li>{@link ElementCodeDetailsVO#toString()}
     *   <li>{@link ElementCodeDetailsVO#getGroupNameMap()}
     *   <li>{@link ElementCodeDetailsVO#getRoleCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ElementCodeDetailsVO actualElementCodeDetailsVO = new ElementCodeDetailsVO();
        HashMap<String, String> groupNameMap = new HashMap<>();
        actualElementCodeDetailsVO.setGroupNameMap(groupNameMap);
        actualElementCodeDetailsVO.setRoleCode("Role Code");
        String actualToStringResult = actualElementCodeDetailsVO.toString();
        assertSame(groupNameMap, actualElementCodeDetailsVO.getGroupNameMap());
        assertEquals("Role Code", actualElementCodeDetailsVO.getRoleCode());
        assertEquals("ElementCodeDetailsVO [roleCode=Role Code, groupNameMap={}]", actualToStringResult);
    }
}

