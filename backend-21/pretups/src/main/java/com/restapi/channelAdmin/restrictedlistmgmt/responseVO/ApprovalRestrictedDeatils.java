package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import lombok.Getter;
import lombok.Setter;

	@Setter
	@Getter
	public class ApprovalRestrictedDeatils{
		private String subscriberMobileNumber;
		private String subscriberType;
		private String registerOn;
		private String ownerID;
		private String status;
		private String statusDes;
	}

