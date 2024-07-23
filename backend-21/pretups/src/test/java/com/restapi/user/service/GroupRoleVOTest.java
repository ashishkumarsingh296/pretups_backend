package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GroupRoleVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GroupRoleVO}
     *   <li>{@link GroupRoleVO#setGroupName(String)}
     *   <li>{@link GroupRoleVO#setRoleCode(String)}
     *   <li>{@link GroupRoleVO#setRoleName(String)}
     *   <li>{@link GroupRoleVO#setStatus(String)}
     *   <li>{@link GroupRoleVO#toString()}
     *   <li>{@link GroupRoleVO#getGroupName()}
     *   <li>{@link GroupRoleVO#getRoleCode()}
     *   <li>{@link GroupRoleVO#getRoleName()}
     *   <li>{@link GroupRoleVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GroupRoleVO actualGroupRoleVO = new GroupRoleVO();
        actualGroupRoleVO.setGroupName("Group Name");
        actualGroupRoleVO.setRoleCode("Role Code");
        actualGroupRoleVO.setRoleName("Role Name");
        actualGroupRoleVO.setStatus("Status");
        actualGroupRoleVO.toString();
        assertEquals("Group Name", actualGroupRoleVO.getGroupName());
        assertEquals("Role Code", actualGroupRoleVO.getRoleCode());
        assertEquals("Role Name", actualGroupRoleVO.getRoleName());
        assertEquals("Status", actualGroupRoleVO.getStatus());
    }
}

