package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlackListSingleSubscriberRequestVO {
	
	private String msisdn;
	private String ownerID;
	private String userName;

	private String cp2pPayer;
	private String cp2pPayee;
	private String c2sPayee;
	
}
