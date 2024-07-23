package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubscriberDetailsListRequestVO {
	private List<SubscriberDetailsRequestVO> subscriberDetailsRequestVOList; 
}
