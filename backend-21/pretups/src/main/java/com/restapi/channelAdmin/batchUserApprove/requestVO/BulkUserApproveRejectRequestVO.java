package com.restapi.channelAdmin.batchUserApprove.requestVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BulkUserApproveRejectRequestVO {
	String batchID;
	String batchAction;
}
