package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponse;

public class ControlPreferenceListsResponseVO extends BaseResponse{
	ArrayList moduleList = new ArrayList<>();
	ArrayList controlList = new ArrayList<>();
	ArrayList preferenceTypeList = new ArrayList<>();
	ArrayList preferenceList = new ArrayList<>();
	
	/**
	 * @return the preferenceList
	 */
	public ArrayList getPreferenceList() {
		return preferenceList;
	}
	/**
	 * @param preferenceList the preferenceList to set
	 */
	public void setPreferenceList(ArrayList preferenceList) {
		this.preferenceList = preferenceList;
	}
	/**
	 * @return the moduleList
	 */
	public ArrayList getModuleList() {
		return moduleList;
	}
	/**
	 * @param moduleList the moduleList to set
	 */
	public void setModuleList(ArrayList moduleList) {
		this.moduleList = moduleList;
	}
	/**
	 * @return the controlList
	 */
	public ArrayList getControlList() {
		return controlList;
	}
	/**
	 * @param controlList the controlList to set
	 */
	public void setControlList(ArrayList controlList) {
		this.controlList = controlList;
	}
	/**
	 * @return the preferenceList
	 */
	public ArrayList getPreferenceTypeList() {
		return preferenceTypeList;
	}
	/**
	 * @param preferenceList the preferenceList to set
	 */
	public void setPreferenceTypeList(ArrayList preferenceTypeList) {
		this.preferenceTypeList = preferenceTypeList;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ControlPreferenceListsResponseVO [moduleList=");
		builder.append(moduleList);
		builder.append(", controlList=");
		builder.append(controlList);
		builder.append(", preferenceTypeList=");
		builder.append(preferenceTypeList);
		builder.append(", preferenceList=");
		builder.append(preferenceList);
		builder.append("]");
		return builder.toString();
	}
	
	
}
