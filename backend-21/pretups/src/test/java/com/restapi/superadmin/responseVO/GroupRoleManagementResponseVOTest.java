package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.inet.config.structure.core.systempermissions.SystemPermissionEntry;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class GroupRoleManagementResponseVOTest {
    /**
     * Method under test: {@link GroupRoleManagementResponseVO#get_rolesMap()}
     */
    @Test
    public void testGet_rolesMap() {
        assertNull((new GroupRoleManagementResponseVO()).get_rolesMap());
    }

    /**
     * Method under test: {@link GroupRoleManagementResponseVO#get_rolesMap()}
     */
    @Test
    public void testGet_rolesMap2() {
        GroupRoleManagementResponseVO groupRoleManagementResponseVO = new GroupRoleManagementResponseVO();
        groupRoleManagementResponseVO._rolesMap = mock(SystemPermissionEntry.class);
        assertSame(groupRoleManagementResponseVO._rolesMap, groupRoleManagementResponseVO.get_rolesMap());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GroupRoleManagementResponseVO}
     *   <li>{@link GroupRoleManagementResponseVO#setRolesList(ArrayList)}
     *   <li>{@link GroupRoleManagementResponseVO#set_rolesMap(HashMap)}
     *   <li>{@link GroupRoleManagementResponseVO#getRolesList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GroupRoleManagementResponseVO actualGroupRoleManagementResponseVO = new GroupRoleManagementResponseVO();
        ArrayList<UserRolesVO> rolesList = new ArrayList<>();
        actualGroupRoleManagementResponseVO.setRolesList(rolesList);
        actualGroupRoleManagementResponseVO.set_rolesMap(new HashMap());
        assertSame(rolesList, actualGroupRoleManagementResponseVO.getRolesList());
        assertTrue(actualGroupRoleManagementResponseVO._rolesMap.isEmpty());
    }
}

