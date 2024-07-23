package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlackListAllSubscriberRequestVO {
	private String ownerID;
	private String userName;

	private String cp2pPayer;
	private String cp2pPayee;
	private String c2sPayee;
}
