package com.restapi.channeluser.service;

import com.btsl.common.BaseResponseMultiple;

public class ActionOnUserResVo  extends BaseResponseMultiple{
   
	private boolean changeStatus;

	public boolean isChangeStatus() {
		return changeStatus;
	}

	public void setChangeStatus(boolean changeStatus) {
		this.changeStatus = changeStatus;
	}
  
}
