package com.restapi.channelAdmin.batchUserApprove.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoadBatchListForApprovalResponseVO extends BaseResponse{
	private ArrayList batchList = null;
}
