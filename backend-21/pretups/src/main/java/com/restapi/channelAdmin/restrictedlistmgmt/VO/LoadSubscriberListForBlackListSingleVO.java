package com.restapi.channelAdmin.restrictedlistmgmt.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadSubscriberListForBlackListSingleVO {
	private boolean cp2pPayerStatusFlag = false;
    private boolean cp2pPayeeStatusFlag = false;
    private boolean c2sPayeeStatusFlag = false;
}
