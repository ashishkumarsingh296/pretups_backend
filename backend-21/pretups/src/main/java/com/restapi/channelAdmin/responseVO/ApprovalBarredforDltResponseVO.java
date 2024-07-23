package com.restapi.channelAdmin.responseVO;

import com.btsl.common.BaseResponseMultiple;

public class ApprovalBarredforDltResponseVO extends BaseResponseMultiple{
	   
		private boolean changeStatus;

		public boolean isChangeStatus() {
			return changeStatus;
		}

		public void setChangeStatus(boolean changeStatus) {
			this.changeStatus = changeStatus;
		}
	  
	}

