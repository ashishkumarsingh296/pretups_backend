package com.restapi.staffuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;

public class FetchStaffUserResponse extends BaseResponse {

	private Map  rolesList;

	/**
	 * @return the rolesList
	 */
	public Map  getRolesList() {
		return rolesList;
	}

	/**
	 * @param rolesList the rolesList to set
	 */
	public void setRolesList(Map  rolesList) {
		this.rolesList = rolesList;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FetchStaffUserResponse [rolesList=").append(rolesList).append("]");
		return builder.toString();
	}


	
}
