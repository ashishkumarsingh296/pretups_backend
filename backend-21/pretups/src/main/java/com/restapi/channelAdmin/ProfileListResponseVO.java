package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetLMSVO;


public class ProfileListResponseVO extends BaseResponse {
	
	public ArrayList<ProfileSetLMSVO> profileList;

	public ArrayList<ProfileSetLMSVO> getProfileList() {
		return profileList;
	}

	public void setProfileList(ArrayList<ProfileSetLMSVO> profileList) {
		this.profileList = profileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProfileListResponseVO [ profileList := " );
		builder.append(profileList);
		builder.append("]");
		return builder.toString();
	}
	
}
