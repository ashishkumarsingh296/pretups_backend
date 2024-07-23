package com.restapi.channelAdmin.batchUserApprove.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkUserApproveRejectResponseVO extends BaseResponse{

	private String fileAttachment;
	private String fileType;
	private String fileName;
	private String errorFlag;
	private ArrayList errorList = null;
	private String noOfRecords;
	private int totalRecords;
}
