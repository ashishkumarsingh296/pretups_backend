package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class SystemRoleTypeVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SystemRoleTypeVO}
     *   <li>{@link SystemRoleTypeVO#setSystemRoleList(ArrayList)}
     *   <li>{@link SystemRoleTypeVO#setSystemRoleType(String)}
     *   <li>{@link SystemRoleTypeVO#toString()}
     *   <li>{@link SystemRoleTypeVO#getSystemRoleList()}
     *   <li>{@link SystemRoleTypeVO#getSystemRoleType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SystemRoleTypeVO actualSystemRoleTypeVO = new SystemRoleTypeVO();
        ArrayList<SystemRoleVO> systemRoleList = new ArrayList<>();
        actualSystemRoleTypeVO.setSystemRoleList(systemRoleList);
        actualSystemRoleTypeVO.setSystemRoleType("System Role Type");
        actualSystemRoleTypeVO.toString();
        assertSame(systemRoleList, actualSystemRoleTypeVO.getSystemRoleList());
        assertEquals("System Role Type", actualSystemRoleTypeVO.getSystemRoleType());
    }
}

