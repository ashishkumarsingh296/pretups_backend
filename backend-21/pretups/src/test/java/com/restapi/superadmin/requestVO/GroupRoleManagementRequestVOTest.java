package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class GroupRoleManagementRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GroupRoleManagementRequestVO}
     *   <li>{@link GroupRoleManagementRequestVO#setCategoryCode(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setDefaultGroupRole(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setDomainType(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setFromHour(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setGroupName(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setRoleCode(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setRoleName(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setRolesList(String[])}
     *   <li>{@link GroupRoleManagementRequestVO#setStatus(String)}
     *   <li>{@link GroupRoleManagementRequestVO#setToHour(String)}
     *   <li>{@link GroupRoleManagementRequestVO#getCategoryCode()}
     *   <li>{@link GroupRoleManagementRequestVO#getDefaultGroupRole()}
     *   <li>{@link GroupRoleManagementRequestVO#getDomainType()}
     *   <li>{@link GroupRoleManagementRequestVO#getFromHour()}
     *   <li>{@link GroupRoleManagementRequestVO#getGroupName()}
     *   <li>{@link GroupRoleManagementRequestVO#getRoleCode()}
     *   <li>{@link GroupRoleManagementRequestVO#getRoleName()}
     *   <li>{@link GroupRoleManagementRequestVO#getRolesList()}
     *   <li>{@link GroupRoleManagementRequestVO#getStatus()}
     *   <li>{@link GroupRoleManagementRequestVO#getToHour()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GroupRoleManagementRequestVO actualGroupRoleManagementRequestVO = new GroupRoleManagementRequestVO();
        actualGroupRoleManagementRequestVO.setCategoryCode("Category Code");
        actualGroupRoleManagementRequestVO.setDefaultGroupRole("Default Group Role");
        actualGroupRoleManagementRequestVO.setDomainType("Domain Type");
        actualGroupRoleManagementRequestVO.setFromHour("jane.doe@example.org");
        actualGroupRoleManagementRequestVO.setGroupName("Group Name");
        actualGroupRoleManagementRequestVO.setRoleCode("Role Code");
        actualGroupRoleManagementRequestVO.setRoleName("Role Name");
        String[] rolesList = new String[]{"Roles List"};
        actualGroupRoleManagementRequestVO.setRolesList(rolesList);
        actualGroupRoleManagementRequestVO.setStatus("Status");
        actualGroupRoleManagementRequestVO.setToHour("To Hour");
        assertEquals("Category Code", actualGroupRoleManagementRequestVO.getCategoryCode());
        assertEquals("Default Group Role", actualGroupRoleManagementRequestVO.getDefaultGroupRole());
        assertEquals("Domain Type", actualGroupRoleManagementRequestVO.getDomainType());
        assertEquals("jane.doe@example.org", actualGroupRoleManagementRequestVO.getFromHour());
        assertEquals("Group Name", actualGroupRoleManagementRequestVO.getGroupName());
        assertEquals("Role Code", actualGroupRoleManagementRequestVO.getRoleCode());
        assertEquals("Role Name", actualGroupRoleManagementRequestVO.getRoleName());
        assertSame(rolesList, actualGroupRoleManagementRequestVO.getRolesList());
        assertEquals("Status", actualGroupRoleManagementRequestVO.getStatus());
        assertEquals("To Hour", actualGroupRoleManagementRequestVO.getToHour());
    }
}

