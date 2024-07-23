package com.restapi.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;

public class RolesResponseVO extends BaseResponseMultiple {
	Map systemRole = new LinkedHashMap<>();
	Map groupRole = new HashMap<>();
	List<String> servicesList = new ArrayList<String>();
	List<LocaleMasterVO> languagesList = new ArrayList<LocaleMasterVO>();

	public List<LocaleMasterVO> getLanguagesList() {
		return languagesList;
	}

	public void setLanguagesList(List<LocaleMasterVO> languagesList) {
		this.languagesList = languagesList;
	}

	List<ListValueVO> voucherList = new ArrayList<ListValueVO>();
	public List<ListValueVO> getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(List<ListValueVO> voucherList) {
		this.voucherList =  voucherList;
	}
	
	List<ListValueVO> domainList = new ArrayList<ListValueVO>();
	

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

	public List<ListValueVO> getDomainList() {
		return domainList;
	}

	public void setDomainList(List<ListValueVO> domainList) {
		this.domainList = domainList;
	}

	
}