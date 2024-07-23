package com.restapi.superadmin.service;

import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.superadmin.responseVO.GroupRoleManagementResponseVO;

import java.sql.Connection;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {GroupRoleManagementServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class GroupRoleManagementServiceImplTest {
    @Autowired
    private GroupRoleManagementServiceImpl groupRoleManagementServiceImpl;

    /**
     * Method under test: {@link GroupRoleManagementServiceImpl#viewGroupRoles(Connection, MComConnectionI, Locale, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewGroupRoles() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        String domainCode = "";
        String categoryCode = "";
        HttpServletResponse responseSwag = null;

        // Act
        GroupRoleManagementResponseVO actualViewGroupRolesResult = this.groupRoleManagementServiceImpl.viewGroupRoles (com.btsl.util.JUnitConfig.getConnection(),
                JUnitConfig.getMComConnection(), locale, domainCode, categoryCode, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GroupRoleManagementServiceImpl#loadRolesListByGroupRole(Connection, MComConnectionI, Locale, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadRolesListByGroupRole() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        String categoryCode = "";
        HttpServletResponse responseSwag = null;

        // Act
        GroupRoleManagementResponseVO actualLoadRolesListByGroupRoleResult = this.groupRoleManagementServiceImpl
                .loadRolesListByGroupRole (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, categoryCode, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GroupRoleManagementServiceImpl#addGroupRole(Connection, MComConnectionI, Locale, ChannelUserVO, String, String, String, String, String, String, String, String, String[], HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddGroupRole() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        ChannelUserVO userVO = null;
        String domainType = "";
        String categoryCode = "";
        String roleCode = "";
        String roleName = "";
        String groupName = "";
        String fromHour = "";
        String toHour = "";
        String defaultGroupRole = "";
        String[] rolesList = null;
        HttpServletResponse responseSwag = null;

        // Act
        GroupRoleManagementResponseVO actualAddGroupRoleResult = this.groupRoleManagementServiceImpl.addGroupRole (com.btsl.util.JUnitConfig.getConnection(),
                JUnitConfig.getMComConnection(), locale, userVO, domainType, categoryCode, roleCode, roleName, groupName, fromHour, toHour,
                defaultGroupRole, rolesList, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GroupRoleManagementServiceImpl#loadRolesByGroupRoleCode(Connection, MComConnectionI, Locale, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadRolesByGroupRoleCode() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        String roleCode = "";
        HttpServletResponse responseSwag = null;

        // Act
        GroupRoleManagementResponseVO actualLoadRolesByGroupRoleCodeResult = this.groupRoleManagementServiceImpl
                .loadRolesByGroupRoleCode (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, roleCode, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GroupRoleManagementServiceImpl#updateGroupRole(Connection, MComConnectionI, Locale, ChannelUserVO, String, String, String, String, String, String, String, String, String, String[], HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUpdateGroupRole() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        ChannelUserVO userVO = null;
        String domainType = "";
        String categoryCode = "";
        String roleCode = "";
        String roleName = "";
        String groupName = "";
        String fromHour = "";
        String toHour = "";
        String defaultGroupRole = "";
        String status = "";
        String[] rolesList = null;
        HttpServletResponse responseSwag = null;

        // Act
        GroupRoleManagementResponseVO actualUpdateGroupRoleResult = this.groupRoleManagementServiceImpl.updateGroupRole(
               com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, userVO, domainType, categoryCode, roleCode, roleName, groupName, fromHour, toHour,
                defaultGroupRole, status, rolesList, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GroupRoleManagementServiceImpl#deleteGroupRole(Connection, MComConnectionI, Locale, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteGroupRole() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        String domainType = "";
        String categoryCode = "";
        String roleCode = "";
        HttpServletResponse responseSwag = null;

        // Act
        GroupRoleManagementResponseVO actualDeleteGroupRoleResult = this.groupRoleManagementServiceImpl
                .deleteGroupRole (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, domainType, categoryCode, roleCode, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }
}

