package com.restapi.superadmin.responseVO;

import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.domain.businesslogic.DomainVO;

public class DomainManagmentResponseVO extends BaseResponse{

	ArrayList<DomainVO> domainList = new ArrayList<>();
	ArrayList geoList = new ArrayList<>();
	ArrayList allowdSource = new ArrayList<>();
	HashMap roleList = new HashMap();
	
     
	/**
	 * @return the domainList
	 */
	public ArrayList<DomainVO> getDomainList() {
		return domainList;
	}
	/**
	 * @param domainList the domainList to set
	 */
	public void setDomainList(ArrayList<DomainVO> domainList) {
		this.domainList = domainList;
	}
	/**
	 * @return the geoList
	 */
	public ArrayList getGeoList() {
		return geoList;
	}
	/**
	 * @param geoList the geoList to set
	 */
	public void setGeoList(ArrayList geoList) {
		this.geoList = geoList;
	}
	
	/**
	 * @return the allowdSource
	 */
	public ArrayList getAllowdSource() {
		return allowdSource;
	}
	/**
	 * @param allowdSource the allowdSource to set
	 */
	public void setAllowdSource(ArrayList allowdSource) {
		this.allowdSource = allowdSource;
	}
	/**
	 * @return the roleList
	 */
	public HashMap getRoleList() {
		return roleList;
	}
	/**
	 * @param roleList the roleList to set
	 */
	public void setRoleList(HashMap roleList) {
		this.roleList = roleList;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DomainManagmentResponseVO [domainList=");
		builder.append(domainList);
		builder.append(", geoList=");
		builder.append(geoList);
		builder.append(", allowdSource=");
		builder.append(allowdSource);
		builder.append(", roleList=");
		builder.append(roleList);
		builder.append("]");
		return builder.toString();
	}
     
}
