package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.StaffUserDTO;

public class StaffUserListByParentResponseVO extends BaseResponse {

	private ArrayList<StaffUserDTO> staffuserList;

	public ArrayList<StaffUserDTO> getStaffuserList() {
		return staffuserList;
	}

	public void setStaffuserList(ArrayList<StaffUserDTO> staffuserList) {
		this.staffuserList = staffuserList;
	}



}