package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnBlackListAllSubscriberRequestVO {
	private String ownerID;
	private String cp2pPayer;
	private String cp2pPayee;
	private String c2sPayee;
}
