package com.restapi.superadmin.responseVO;

import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;

public class GroupRoleManagementResponseVO extends BaseResponse{

	public ArrayList<UserRolesVO> rolesList;
    public HashMap _rolesMap;

	public ArrayList<UserRolesVO> getRolesList() {
		return rolesList;
	}

	public void setRolesList(ArrayList<UserRolesVO> rolesList) {
		this.rolesList = rolesList;
	}

	public HashMap get_rolesMap() {
		return _rolesMap;
	}

	public void set_rolesMap(HashMap _rolesMap) {
		this._rolesMap = _rolesMap;
	}
	
	
	
    
}
