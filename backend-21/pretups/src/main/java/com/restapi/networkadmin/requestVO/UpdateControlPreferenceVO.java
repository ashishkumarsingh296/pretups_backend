package com.restapi.networkadmin.requestVO;

import java.util.ArrayList;

import com.btsl.pretups.preference.businesslogic.ControlPreferenceVO;

public class UpdateControlPreferenceVO {

	ArrayList<ControlPreferenceVO> ctrlPreferenceList = new ArrayList<>();

	/**
	 * @return the ctrlPreferenceList
	 */
	public ArrayList<ControlPreferenceVO> getCtrlPreferenceList() {
		return ctrlPreferenceList;
	}

	/**
	 * @param ctrlPreferenceList the ctrlPreferenceList to set
	 */
	public void setCtrlPreferenceList(ArrayList<ControlPreferenceVO> ctrlPreferenceList) {
		this.ctrlPreferenceList = ctrlPreferenceList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateControlPreferenceVO [ctrlPreferenceList=");
		builder.append(ctrlPreferenceList);
		builder.append("]");
		return builder.toString();
	}

	
	
	
}
