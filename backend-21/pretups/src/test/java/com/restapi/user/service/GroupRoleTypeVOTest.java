package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class GroupRoleTypeVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GroupRoleTypeVO}
     *   <li>{@link GroupRoleTypeVO#setGroupRoleList(ArrayList)}
     *   <li>{@link GroupRoleTypeVO#setGroupRoleType(String)}
     *   <li>{@link GroupRoleTypeVO#toString()}
     *   <li>{@link GroupRoleTypeVO#getGroupRoleList()}
     *   <li>{@link GroupRoleTypeVO#getGroupRoleType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GroupRoleTypeVO actualGroupRoleTypeVO = new GroupRoleTypeVO();
        ArrayList<GroupRoleVO> groupRoleList = new ArrayList<>();
        actualGroupRoleTypeVO.setGroupRoleList(groupRoleList);
        actualGroupRoleTypeVO.setGroupRoleType("Group Role Type");
        actualGroupRoleTypeVO.toString();
        assertSame(groupRoleList, actualGroupRoleTypeVO.getGroupRoleList());
        assertEquals("Group Role Type", actualGroupRoleTypeVO.getGroupRoleType());
    }
}

