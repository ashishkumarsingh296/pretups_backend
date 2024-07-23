package com.restapi.channeluser.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;

public class GetRolesAndServicesResponseVO extends BaseResponseMultiple {
	Map systemRole = new LinkedHashMap<>();
	Map groupRole = new HashMap<>();
	List<String> servicesList = new ArrayList<String>();
	ArrayList profileList = new ArrayList<String>();
	

	public ArrayList getProfileList() {
		return profileList;
	}

	public void setProfileList(ArrayList profileList) {
		this.profileList = profileList;
	}

	List<LocaleMasterVO> languagesList = new ArrayList<LocaleMasterVO>();

	public List<LocaleMasterVO> getLanguagesList() {
		return languagesList;
	}

	public void setLanguagesList(List<LocaleMasterVO> languagesList) {
		this.languagesList = languagesList;
	}

	List<String> voucherList = new ArrayList<String>();
	public List<String> getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(List<String> voucherList) {
		this.voucherList = voucherList;
	}

	public List<String> getServicesList() {
		return servicesList;
	}

	public void setServicesList(List<String> serviceTypeList) {
		this.servicesList = serviceTypeList;
	}

	
	public Map getSystemRole() {
		return systemRole;
	}

	public void setSystemRole(Map systemRole) {
		this.systemRole = systemRole;
	}

	public Map getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(Map groupRole) {
		this.groupRole = groupRole;
	}

	
}
