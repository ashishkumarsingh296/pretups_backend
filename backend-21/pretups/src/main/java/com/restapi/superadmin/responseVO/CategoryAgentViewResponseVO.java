package com.restapi.superadmin.responseVO;

import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BaseResponse;

public class CategoryAgentViewResponseVO extends BaseResponse {

	ArrayList agentGeoList = new ArrayList<>();
	ArrayList agentAllowedSource = new ArrayList<>();
	HashMap agentRoleMap = new HashMap();
	

	public ArrayList getAgentAllowedSource() {
		return agentAllowedSource;
	}

	public void setAgentAllowedSource(ArrayList agentAllowedSource) {
		this.agentAllowedSource = agentAllowedSource;
	}

	public ArrayList getAgentGeoList() {
		return agentGeoList;
	}

	public void setAgentGeoList(ArrayList agentGeoList) {
		this.agentGeoList = agentGeoList;
	}

	public HashMap getAgentRoleMap() {
		return agentRoleMap;
	}

	public void setAgentRoleMap(HashMap agentRoleMap) {
		this.agentRoleMap = agentRoleMap;
	}


}
