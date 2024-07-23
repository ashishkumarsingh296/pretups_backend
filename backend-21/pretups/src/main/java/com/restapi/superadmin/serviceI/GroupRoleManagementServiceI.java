package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.superadmin.responseVO.GroupRoleManagementResponseVO;

public interface GroupRoleManagementServiceI {

	GroupRoleManagementResponseVO viewGroupRoles(Connection con, MComConnectionI mcomCon, Locale locale,
			String domainCode, String categoryCode, HttpServletResponse responseSwag);

	GroupRoleManagementResponseVO loadRolesListByGroupRole(Connection con, MComConnectionI mcomCon, Locale locale,
			String categoryCode, HttpServletResponse responseSwag);

	GroupRoleManagementResponseVO addGroupRole(Connection con, MComConnectionI mcomCon, Locale locale, ChannelUserVO userVO,
			String domainType, String categoryCode, String roleCode, String roleName, String groupName, String fromHour,
			String toHour, String defaultGroupRole, String[] rolesList, HttpServletResponse responseSwag);

	GroupRoleManagementResponseVO loadRolesByGroupRoleCode(Connection con, MComConnectionI mcomCon, Locale locale,
			String roleCode, HttpServletResponse responseSwag);

	GroupRoleManagementResponseVO updateGroupRole(Connection con, MComConnectionI mcomCon, Locale locale, ChannelUserVO userVO,
			String domainType, String categoryCode, String roleCode, String roleName, String groupName, String fromHour,
			String toHour, String defaultGroupRole, String status, String[] rolesList, HttpServletResponse responseSwag);

	GroupRoleManagementResponseVO deleteGroupRole(Connection con, MComConnectionI mcomCon, Locale locale,
												  ChannelUserVO userVO,String domainType, String categoryCode, String roleCode, HttpServletResponse responseSwag);

}
